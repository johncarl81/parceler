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
package org.parceler.internal;

import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.util.matcher.Matcher;
import org.androidtransfuse.util.matcher.Matchers;
import org.parceler.ParcelerRuntimeException;
import org.parceler.internal.generator.ReadWriteGenerator;
import org.parceler.internal.generator.SimpleReadWriteGenerator;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class Generators {

    private final ASTClassFactory astClassFactory;

    private final Map<Matcher<ASTType>, ReadWriteGenerator> generators = new LinkedHashMap<Matcher<ASTType>, ReadWriteGenerator>();

    public Generators(ASTClassFactory astClassFactory) {
        this.astClassFactory = astClassFactory;
    }

    public boolean matches(ASTType type){
        for (Matcher<ASTType> matcher : generators.keySet()) {
            if(matcher.matches(type)){
                return true;
            }
        }
        return false;
    }

    public ReadWriteGenerator getGenerator(ASTType type) {
        for (Map.Entry<Matcher<ASTType>, ReadWriteGenerator> generatorEntry : generators.entrySet()) {
            if(generatorEntry.getKey().matches(type)){
                return generatorEntry.getValue();
            }
        }
        throw new ParcelerRuntimeException("Unable to find appropriate Parcel method to write " + type.getName());
    }

    public void addPair(Class clazz, String readMethod, String writeMethod) {
        addPair(clazz, readMethod, writeMethod, clazz);
    }

    public void addPair(Class clazz, String readMethod, String writeMethod, Class writeParam) {
        addPair(astClassFactory.getType(clazz), readMethod, writeMethod, writeParam.getName());
    }

    public void addPair(String clazzName, String readMethod, String writeMethod) {
        addPair(clazzName, readMethod, writeMethod, clazzName);
    }

    public void addPair(String clazzName, String readMethod, String writeMethod, String writeParam) {
        addPair(new ASTStringType(clazzName), readMethod, writeMethod, writeParam);
    }

    public void addPair(ASTType type, String readMethod, String writeMethod, String writeParam){
        add(Matchers.type(type).build(), new SimpleReadWriteGenerator(readMethod, new String[0], writeMethod, new String[]{writeParam}));
    }

    public void addPair(Class clazz, ReadWriteGenerator generator){
        addPair(astClassFactory.getType(clazz), generator);
    }

    public void addPair(ASTType type, ReadWriteGenerator generator){
        add(Matchers.type(type).build(), generator);
    }

    public void add(Matcher<ASTType> matcher, ReadWriteGenerator generator) {
        generators.put(matcher, generator);
    }

    protected Map<Matcher<ASTType>, ReadWriteGenerator> getGenerators() {
        return generators;
    }
}
