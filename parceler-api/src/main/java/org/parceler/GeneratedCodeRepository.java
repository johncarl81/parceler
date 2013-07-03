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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author John Ericksen
 */
public abstract class GeneratedCodeRepository<T> {

    private ConcurrentMap<Class, T> generatedMap = new ConcurrentHashMap<Class, T>();

    public GeneratedCodeRepository(String repositoryPackage, String repositoryName) {
        loadRepository(getClass().getClassLoader(), repositoryPackage, repositoryName);
    }

    public T get(Class clazz){
        T result = generatedMap.get(clazz);
        if (result == null) {
            T value = findClass(clazz);
            if(value == null){
                return null;
            }
            result = generatedMap.putIfAbsent(clazz, value);
            if (result == null) {
                result = value;
            }
        }

        return result;
    }

    public abstract T findClass(Class clazz);

    /**
     * Update the repository class from the given classloader.  If the given repository class cannot be instantiated
     * then this method will throw a TransfuseRuntimeException.
     *
     * @throws ParcelerRuntimeException
     * @param classLoader
     */
    public final void loadRepository(ClassLoader classLoader, String repositoryPackage, String repositoryName){
        try{
            Class repositoryClass = classLoader.loadClass(repositoryPackage + "." + repositoryName);
            Repository<T> instance = (Repository<T>) repositoryClass.newInstance();
            generatedMap.putAll(instance.get());

        } catch (ClassNotFoundException e) {
            //nothing
        } catch (InstantiationException e) {
            throw new ParcelerRuntimeException("Unable to instantiate generated Repository", e);
        } catch (IllegalAccessException e) {
            throw new ParcelerRuntimeException("Unable to access generated Repository", e);
        }
    }
}
