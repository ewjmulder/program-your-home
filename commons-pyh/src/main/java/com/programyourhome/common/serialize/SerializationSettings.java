package com.programyourhome.common.serialize;

public interface SerializationSettings {

    public void setPrettyPrint(boolean prettyPrint);

    /**
     * For all provided classes: fix the serialization scope of that class and all of it's subtypes
     * to the public interface provided by the given class. Normally, the provided classes
     * would be API interface definitions. When serializing an implementing class of such an interface,
     * the serialization framework should only look at the interface definition and not at any extra
     * public properties that the implementing class might have.
     *
     * @param classes the classes to fix the serialization scope for
     */
    public void fixSerializationScope(Class<?>... classes);

}
