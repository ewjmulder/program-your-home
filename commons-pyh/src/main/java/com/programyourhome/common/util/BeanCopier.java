package com.programyourhome.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class BeanCopier {

    /*
     * TODO / IDEA: Create own mapper that has a param for a class that defines what source fields to copy instead of what target fields
     * to set. Also, validation on inheritance not needed per see, is choice of using caller if it makes sence (won't break anything).
     * @Inject
     * private ObjectMapper objectMapper;
     * @Inject
     * private MrBeanModule mrBeanModule;
     * final Class<?> propertyClassWithSetters = this.mrBeanModule.getMaterializer().resolveAbstractType(this.objectMapper.getDeserializationConfig(),
     * this.objectMapper.getTypeFactory().constructSimpleType(propertyClass, new JavaType[0])).getRawClass();
     */

    public <T> T copyToNew(final Object source, final Class<T> targetClass) {
        T newInstance;
        try {
            newInstance = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Class " + targetClass + " could not be instantiazed with newInstance().");
        }
        return this.copyTo(source, newInstance);
    }

    public <T> T copyTo(final Object source, final T target) {
        BeanUtils.copyProperties(source, target);
        return target;
    }

}
