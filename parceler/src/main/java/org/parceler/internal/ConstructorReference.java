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

import org.androidtransfuse.adapter.ASTConstructor;
import org.androidtransfuse.adapter.ASTParameter;
import org.androidtransfuse.adapter.ASTType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class ConstructorReference {

    private final ASTConstructor constructor;
    private Map<ASTParameter, AccessibleReference> writeReferences = new HashMap<ASTParameter, AccessibleReference>();
    private Map<ASTParameter, ASTType> converters = new HashMap<ASTParameter, ASTType>();

    public ConstructorReference(ASTConstructor constructor) {
        this.constructor = constructor;
    }

    public ASTConstructor getConstructor() {
        return constructor;
    }

    public void putReference(ASTParameter parameter, AccessibleReference reference){
        writeReferences.put(parameter, reference);
    }

    public void putConverter(ASTParameter parameter, ASTType converter){
        converters.put(parameter, converter);
    }

    public AccessibleReference getWriteReference(ASTParameter parameter) {
        return writeReferences.get(parameter);
    }

    public Map<ASTParameter, AccessibleReference> getWriteReferences() {
        return writeReferences;
    }

    public Map<ASTParameter, ASTType> getConverters(){
        return converters;
    }
}
