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

import org.androidtransfuse.adapter.ASTType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author John Ericksen
 */
public class ParcelableDescriptor {

    private final List<ReferencePair<ConstructorReference>> constructorPairs = new ArrayList<ReferencePair<ConstructorReference>>();
    private final List<ReferencePair<FieldReference>> fieldPairs = new ArrayList<ReferencePair<FieldReference>>();
    private final List<ReferencePair<MethodReference>> methodPairs = new ArrayList<ReferencePair<MethodReference>>();
    private final ASTType parcelConverterType;

    public ParcelableDescriptor() {
        parcelConverterType = null;
    }

    public ParcelableDescriptor(ASTType parcelConverterType) {
        this.parcelConverterType = parcelConverterType;
    }

    public List<ReferencePair<ConstructorReference>> getConstructorPairs() {
        return constructorPairs;
    }

    public List<ReferencePair<FieldReference>> getFieldPairs() {
        return fieldPairs;
    }

    public List<ReferencePair<MethodReference>> getMethodPairs() {
        return methodPairs;
    }

    public ASTType getParcelConverterType() {
        return parcelConverterType;
    }
}
