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
package org.parceler.internal.generator;

import org.parceler.ParcelerRuntimeException;

import java.util.HashMap;
import java.util.Map;

/**
* @author John Ericksen
*/
public abstract class ReadWriteGeneratorBase implements ReadWriteGenerator{
    private final String readMethod;
    private final String[] readMethodParams;
    private final String writeMethod;
    private final String[] writeMethodParams;

    private static final Map<String,Class> PRIMITIVE_CLASSES = new HashMap<String,Class>(){{
        put("int", Integer.TYPE );
        put("long", Long.TYPE );
        put("double", Double.TYPE );
        put("float", Float.TYPE );
        put("bool", Boolean.TYPE );
        put("char", Character.TYPE );
        put("byte", Byte.TYPE );
        put("void", Void.TYPE );
        put("short", Short.TYPE );
    }};

    public ReadWriteGeneratorBase(String readMethod, Class[] readMethodParams, String writeMethod, Class[] writeMethodParams) {
        this(readMethod, classArrayToStringArray(readMethodParams), writeMethod, classArrayToStringArray(writeMethodParams));
    }

    public ReadWriteGeneratorBase(String readMethod, String[] readMethodParams, String writeMethod, String[] writeMethodParams) {
        this.readMethod = readMethod;
        this.readMethodParams = readMethodParams;
        this.writeMethod = writeMethod;
        this.writeMethodParams = writeMethodParams;
    }

    private static String[] classArrayToStringArray(Class[] input){
        String[] output = new String[input.length];

        for(int i = 0; i < input.length; i++){
            output[i] = input[i].getName();
        }

        return output;
    }

    private static Class[] stringArrayToClassArray(String[] input) {
        Class[] output = new Class[input.length];

        for(int i = 0; i < input.length; i++){
            if(PRIMITIVE_CLASSES.containsKey(input[i])){
                output[i] = PRIMITIVE_CLASSES.get(input[i]);
            }
            else{
                try{
                    output[i] = Class.forName(input[i]);
                } catch (ClassNotFoundException e) {
                    throw new ParcelerRuntimeException("Unable to find class " + input[i], e);
                }
            }
        }

        return output;
    }

    public String getReadMethod() {
        return readMethod;
    }

    public Class[] getReadMethodParams() {
        return stringArrayToClassArray(readMethodParams);
    }

    public String getWriteMethod() {
        return writeMethod;
    }

    public Class[] getWriteMethodParams() {
        return stringArrayToClassArray(writeMethodParams);
    }
}
