/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler;

import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * Utility class for performing a variety of operations through reflection.  This functionality should be used sparingly
 * as frequent calls can cause performance issues.
 *
 * @author John Ericksen
 */
public final class InjectionUtil {

    public static final String GET_FIELD_METHOD = "getField";
    public static final String SET_FIELD_METHOD = "setField";
    public static final String CALL_METHOD_METHOD = "callMethod";
    public static final String CALL_CONSTRUCTOR_METHOD = "callConstructor";

    private InjectionUtil() {
        //singleton constructor
    }
    /**
     * Returns the value of a field.
     *
     * @param returnType type of the field
     * @param targetClass class represented by the target parameter
     * @param target object containing the field
     * @param field name of the field
     * @param <T> type parameter
     * @return field value
     */
    public static <T> T getField(Class<T> returnType, Class<?> targetClass, Object target, String field) {
        try {
            Field declaredField = targetClass.getDeclaredField(field);

            return AccessController.doPrivileged(
                    new GetFieldPrivilegedAction<T>(declaredField, target));

        } catch (NoSuchFieldException e) {
            throw new ParcelerRuntimeException(
                    "NoSuchFieldException Exception during field injection: " + field + " in " + target.getClass(), e);
        } catch (PrivilegedActionException e) {
            throw new ParcelerRuntimeException("PrivilegedActionException Exception during field injection", e);
        } catch (Exception e) {
            throw new ParcelerRuntimeException("Exception during field injection", e);
        }
    }

    private static final class GetFieldPrivilegedAction<T> extends AccessibleElementPrivilegedAction<T, Field> {

        private final Object target;

        private GetFieldPrivilegedAction(Field classField, Object target) {
            super(classField);
            this.target = target;
        }

        @Override
        public T run(Field classField) throws IllegalAccessException {
            return (T) classField.get(target);
        }
    }

    /**
     * Updates field with the given value.
     *
     * @param targetClass class representing the object containing the field.
     * @param target object containing the field to update
     * @param field name of the field
     * @param value object to update the field to
     */
    public static void setField(Class<?> targetClass, Object target, String field, Object value) {
        try {
            Field classField = targetClass.getDeclaredField(field);

            AccessController.doPrivileged(
                    new SetFieldPrivilegedAction(classField, target, value));

        } catch (NoSuchFieldException e) {
            throw new ParcelerRuntimeException(
                    "NoSuchFieldException Exception during field injection: " + field + " in " + target.getClass(), e);
        } catch (PrivilegedActionException e) {
            throw new ParcelerRuntimeException("PrivilegedActionException Exception during field injection", e);
        } catch (Exception e) {
            throw new ParcelerRuntimeException("Exception during field injection", e);
        }
    }

    private static final class SetFieldPrivilegedAction extends AccessibleElementPrivilegedAction<Void, Field> {

        private final Object target;
        private final Object value;

        private SetFieldPrivilegedAction(Field classField, Object target, Object value) {
            super(classField);
            this.target = target;
            this.value = value;
        }

        @Override
        public Void run(Field classField) throws IllegalAccessException {
            classField.set(target, value);

            return null;
        }
    }

    /**
     * Calls a method with the provided arguments as parameters.
     *
     * @param retClass the method return value
     * @param targetClass the instance class
     * @param target the instance containing the method
     * @param method the method name
     * @param argClasses types of the method arguments
     * @param args method arguments used during invocation
     * @param <T> relating type parameter
     * @return method return value
     */
    public static <T> T callMethod(Class<T> retClass, Class<?> targetClass, Object target, String method, Class[] argClasses, Object[] args) {
        try {
            Method classMethod = targetClass.getDeclaredMethod(method, argClasses);

            return AccessController.doPrivileged(
                    new SetMethodPrivilegedAction<T>(classMethod, target, args));

        } catch (NoSuchMethodException e) {
            throw new ParcelerRuntimeException("Exception during method injection: NoSuchFieldException", e);
        } catch (PrivilegedActionException e) {
            throw new ParcelerRuntimeException("PrivilegedActionException Exception during field injection", e);
        } catch (Exception e) {
            throw new ParcelerRuntimeException("Exception during field injection", e);
        }
    }

    private static final class SetMethodPrivilegedAction<T> extends AccessibleElementPrivilegedAction<T, Method> {

        private final Object target;
        private final Object[] args;

        private SetMethodPrivilegedAction(Method classMethod, Object target, Object[] args) {
            super(classMethod);
            this.target = target;
            this.args = args;
        }

        public T run(Method classMethod) throws InvocationTargetException, IllegalAccessException {
            return (T) classMethod.invoke(target, args);
        }
    }


    /**
     * Instantiates a class by calling the constructor.
     *
     * @param targetClass instance type to construct
     * @param argClasses argument types accepted by the constructor
     * @param args constructor argument values
     * @param <T> relating type parameter
     * @return instance created by constructor
     */
    public static <T> T callConstructor(Class<T> targetClass, Class[] argClasses, Object[] args) {
        T output;

        try {
            Constructor classConstructor = targetClass.getDeclaredConstructor(argClasses);

            output = AccessController.doPrivileged(
                    new SetConstructorPrivilegedAction<T>(classConstructor, args));

        } catch (NoSuchMethodException e) {
            throw new ParcelerRuntimeException("Exception during method injection: NoSuchMethodException", e);
        } catch (PrivilegedActionException e) {
            throw new ParcelerRuntimeException("PrivilegedActionException Exception during field injection", e);
        } catch (Exception e) {
            throw new ParcelerRuntimeException("Exception during field injection", e);
        }
        return output;
    }

    private static final class SetConstructorPrivilegedAction<T> extends AccessibleElementPrivilegedAction<T, Constructor> {
        private final Object[] args;

        private SetConstructorPrivilegedAction(Constructor classConstructor, Object[] args) {
            super(classConstructor);
            this.args = args;
        }

        @Override
        public T run(Constructor classConstructor) throws InvocationTargetException, InstantiationException, IllegalAccessException {
            return (T) classConstructor.newInstance(args);
        }
    }

    private static abstract class AccessibleElementPrivilegedAction<T, E extends AccessibleObject> implements PrivilegedExceptionAction<T> {

        private final E accessible;

        protected AccessibleElementPrivilegedAction(E accessible) {
            this.accessible = accessible;
        }

        @Override
        public T run() throws Exception {
            boolean previous = this.accessible.isAccessible();
            accessible.setAccessible(true);

            T output = run(accessible);

            accessible.setAccessible(previous);

            return output;
        }

        /**
         * Execute a Privileged Action against the given element which has been toggled to be accessible.
         *
         * @param element input AccessibleObject
         * @return T
         * @throws Exception
         */
        public abstract T run(E element) throws Exception;
    }
}
