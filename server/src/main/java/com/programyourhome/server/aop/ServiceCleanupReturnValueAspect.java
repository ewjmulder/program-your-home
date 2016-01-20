package com.programyourhome.server.aop;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

@Aspect
@Component
// Set the order of the this annotation interception to 'important', but less important than @Transactional,
// because we need to run inside the transaction scope.
@Order(1)
// Without the raw types (using <?>), the whole code would be a lot less readable and it doesn't add any compile time safety.
@SuppressWarnings("rawtypes")
public class ServiceCleanupReturnValueAspect {

    // FIXME: refactor and cleanup this WORKING class!
    // +document: this works also for Hibernate, because this pointcut is executed inside the Transaction annotation scope
    // This seems like a little luck, since there was no way of knowing what pointcut whould go 'fist', although Spring
    // might have intentionally put Transaction as 'outermost' intercepted scope, so others could 'benefit' from it.
    // Is worth a Google to see if there is any documentation on this. Might turn out to be sheer luck or some naming / ordering thing.
    // No, as long as the OpenEntityManagerInViewInterceptor is in place, it'll work anyway.
    // Probably, that is what we want. Might be interesting to try to disable it and see what the effect is.
    // Other test: use poller to get a prodaggr with part collection getter in interface def. Since there is no
    // Web MVC context with a OpenEntityManagerInViewInterceptor this fails without the cleaner (leaky abstraction),
    // because the scope of the transactional annotation is closed after returning, so calling the getter of the collection
    // fails, even though it's on the interface. But with the cleaner, it all works, since the cleaner interception works still inside
    // the Transactional annotation scope! The cleaned up object contains all data that can be reached with the interface spec, so no
    // leaky abstraction surprises! :-)
    // Still open question: luck or Spring on purpose?
    // Yes and no:
    // Advisors are ordered according to the @Ordered annotation value. For instance the advisor that enables context introspection has
    // order Integer.MIN_VALUE, since it wants/needs to be at the front of the advisor list. The transactional annotation does not have an
    // oder specified, which gets defaulted to Integer.MAX_VALUE. So does our own advice, so they have no order. Therefore,
    // the order used is the order in which they are found. They are searched by first looking for the Spring internal advisors and then the
    // 'custom'/user annotated ones, see org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator.findCandidateAdvisors
    // The first ones are type Advisors (eventual wrapper) defined beforehan by Spring Framework (like transactional one:
    // BeanFactoryTransactionAttributeSourceAdvisor)
    // Second ones are type Aspect's, like this class (ServiceCleanupReturnValueAspect)
    // And indeed: if we change the @Order value for this class to be less than Integer.MAX_VALUE (eg 1), we get precendence over the
    // Transactional and so are out of scope and will always cause a lazy initialization in the cleanup
    // (still better than the 'outside world' by accident), cause now the interface data will be forced before returning to the outside world
    // So we are forced to not have an order, then it works fine. But if there was to be another aspect we need and be ordered with this one,
    // we might get in trouble, since how will those be 'detected' and ordered? Doesn't seem t be a pressing issue though atm.
    // Note: actually, this is somewhat trickier, since another aspect with a higher order that in principle does not
    // interfere with the others functionally, could mess up the ordering of same level order aspects, depending
    // on the implementation details of the sorting algorithm. Not really something you want to depend on! (but see below for good solution!)
    // Heey, we can change it! http://docs.spring.io/spring/docs/current/spring-framework-reference/html/transaction.html (look for 'order')
    // @EnableTransactionManagement(order = ...)
    // Perfect, I love Spring!! :-D
    // TODO: Find some sensible values for the ordering. Probably Transaction very high precedence (-1) and this one also quite high (1).

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private MrBeanModule mrBeanModule;

    @Inject
    private ApplicationContext applicationContext;

    private Map<Class, Class> collectionTypeChooser = new HashMap<>();
    private Map<Class, Class> mapTypeChooser = new HashMap<>();

    public ServiceCleanupReturnValueAspect() {
        this.collectionTypeChooser = new HashMap<>();
        this.mapTypeChooser = new HashMap<>();

        this.collectionTypeChooser.put(Collection.class, ArrayList.class);
        this.collectionTypeChooser.put(List.class, ArrayList.class);
        this.collectionTypeChooser.put(Set.class, HashSet.class);

        this.mapTypeChooser.put(Map.class, HashMap.class);
        this.mapTypeChooser.put(SortedMap.class, TreeMap.class);
    }

    // All public methods on all classes that implement PyhApi.
    @Around("execution(public * com.programyourhome.api.PyhApi+.*(..))")
    public Object cleanupReturnValue(final ProceedingJoinPoint joinPoint) throws Throwable {
        // First, let the service method operate normally.
        final Object originalReturnValue = joinPoint.proceed();
        // Then, we'll change it's return value to be a 'pure' interface implementation and remove any leaky abstraction.
        final Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        return this.cleanupValue(originalReturnValue, method.getReturnType(), method.getGenericReturnType());
    }

    // TODO: very clear comments on how this works.

    // TODO: get all PyhApi implementations, pointcut on all PyhApi annotated classes (hmm, shouldn't that be higher up then as well?

    @SuppressWarnings("unchecked")
    // Important: synchronized, because of encountered LinkageError "attempted duplicate class definition for name" when using MrBean
    // TODO: extract call to MrBean abstract type materializer and only synchronize that one.
    private synchronized Object cleanupValue(final Object value, final Class definedClass, final Type type) {
        try {
            // System.out.println("cleanupValue: defined class: " + definedClass + ", type: " + type + ", value: " + value);
            final Object cleanedValue;
            if (value == null) {
                cleanedValue = null;
            } else {
                if (Collection.class.isAssignableFrom(definedClass)) {
                    final Class concreteCollectionClass = this.collectionTypeChooser.getOrDefault(definedClass, definedClass);
                    // If we encounter a collection type, create a new collection of the original type.
                    cleanedValue = concreteCollectionClass.newInstance();
                    // The type of objects in the collection as defined.
                    final Type typeInCollection = ((ParameterizedType) type).getActualTypeArguments()[0];
                    final Type classTypeInCollection;
                    if (typeInCollection instanceof ParameterizedType) {
                        classTypeInCollection = ((ParameterizedType) typeInCollection).getRawType();
                    } else if (typeInCollection instanceof WildcardType) {
                        // TODO: support other configurations than one upper bound type?
                        classTypeInCollection = ((WildcardType) typeInCollection).getUpperBounds()[0];
                    } else {
                        classTypeInCollection = typeInCollection;
                    }

                    // TODO: if type in collection is not a class: raise exception: not suppported!

                    // And add every value of the original collection in the new collection, but with a cleanup.
                    ((Collection) value).stream().forEach(
                            item -> ((Collection) cleanedValue).add(this.cleanupValue(item, (Class) classTypeInCollection, typeInCollection)));
                } else if (Map.class.isAssignableFrom(definedClass)) {
                    final Class concreteMapClass = this.mapTypeChooser.getOrDefault(definedClass, definedClass);
                    // If we encounter a collection type, create a new map of the original type.
                    cleanedValue = concreteMapClass.newInstance();
                    // The type of objects in the collection as defined.
                    final Type keyTypeInMap = ((ParameterizedType) type).getActualTypeArguments()[0];
                    final Type valueTypeInMap = ((ParameterizedType) type).getActualTypeArguments()[1];

                    // TODO: if type in collection is not a class: raise exception: not suppported!

                    // And add every value of the original map in the new map, but with a cleanup.
                    ((Map) value).entrySet().stream().forEach(
                            entry -> ((Map) cleanedValue).put(this.cleanupValue(((Map.Entry) entry).getKey(), (Class) keyTypeInMap, keyTypeInMap),
                                    this.cleanupValue(((Map.Entry) entry).getValue(), (Class) valueTypeInMap, valueTypeInMap)));
                } else if (definedClass.isInterface()) {
                    // If we encounter an interface type: apply cleaning to the object.
                    // We don't check if the type is our 'own' interface type, because this logic should be universally applicable.
                    // We take the stand here that an interface object returned from a service, should never be more than a POJO
                    // and you should not be able to call any 'functional logic' methods on it.
                    // First, create a 'clean' interface implementation class, using the MrBean Jackson module.
                    final Class interfaceImplementation = this.mrBeanModule.getMaterializer().resolveAbstractType(this.objectMapper.getDeserializationConfig(),
                            this.objectMapper.getTypeFactory().constructSimpleType(definedClass, new JavaType[0])).getRawClass();
                    // Create a new instance of that class (always has public no-args constructor)
                    cleanedValue = interfaceImplementation.newInstance();
                    // System.out.println("Copying from: " + value.getClass() + " to " + cleanedValue.getClass());
                    // Copy the properties from the 'dirty' to the clean value, thereby also cleaning these properties.
                    this.copyProperties(value, cleanedValue);
                } else {
                    // Not a specific type we're interested in, so just pass as-is.
                    // It could be that this type has fields of a type we are interested in, e.g. in case of a 'custom' or unmapped
                    // type of collection. This is (for now) not supported. That does make sense, since we expect 'clean' interface definitions.
                    cleanedValue = value;
                }
            }
            return cleanedValue;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Exception during cleanup of value: " + value, e);
        }
    }

    /**
     * Copy the property values of the given source bean into the given target bean.
     * <p>
     * Note: The source and target classes do not have to match or even be derived from each other, as long as the properties match. Any bean properties that
     * the source bean exposes but the target bean does not will silently be ignored.
     *
     * @param source the source bean
     * @param target the target bean
     * @param editable the class (or interface) to restrict property setting to
     * @param ignoreProperties array of property names to ignore
     * @throws BeansException if the copying failed
     * @see BeanWrapper
     */
    // Copied from BeanUtils Spring class to allow for customization: no extra params and calling clean on the properties before copying them.
    private void copyProperties(final Object source, final Object target)
            throws BeansException {
        final PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(target.getClass());

        for (final PropertyDescriptor targetPd : targetPds) {
            final Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null) {
                final PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    final Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            final Object value = readMethod.invoke(source);

                            // TODO: document this adaption, clean up every copied value.
                            // Please note: we have to inspect the target type for the 'follow up types', because the target is the 'clean' type.
                            final Object cleanedValue = this.cleanupValue(value, writeMethod.getParameterTypes()[0], writeMethod.getGenericParameterTypes()[0]);

                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, cleanedValue);
                        } catch (final Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

}
