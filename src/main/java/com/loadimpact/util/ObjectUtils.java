package com.loadimpact.util;

import com.loadimpact.exception.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility methods for objects and reflections.
 *
 * @author jens
 */
public class ObjectUtils {
    /**
     * Makes a deep-copy clone of an object.
     * @param obj   target object, that must implements {@link java.io.Serializable}
     * @return deep-copy
     */
    public static Serializable copy(Serializable obj) {
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream(4096);
            ObjectOutputStream out = new ObjectOutputStream(buf);
            out.writeObject(obj);
            out.close();

            ByteArrayInputStream buf2 = new ByteArrayInputStream(buf.toByteArray());
            ObjectInputStream in = new ObjectInputStream(buf2);
            Serializable            obj2 = (Serializable) in.readObject();
            in.close();

            return obj2;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds a constructor and hides all checked exceptions.
     * @param cls       target class
     * @param params    zero, one or more parameter types
     * @param <T>       target type
     * @return its constructor object
     */
    public static <T> Constructor<T> getConstructor(Class<T> cls, Class... params) {
        try {
            return cls.getConstructor(params);
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    /**
     * Invokes a constructor and hides all checked exceptions.
     * @param constructor   the constructor
     * @param args          zero, one or more arguments
     * @param <T>           target type
     * @return a new object
     */
    public static <T> T newInstance(Constructor<T> constructor, Object... args) {
        try {
            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }
    
}
