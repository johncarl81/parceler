/**
 * Copyright 2013-2015 John Ericksen
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

import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.adapter.ASTType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author John Ericksen
 */
public class ParcelImplementations {

    private final JDefinedClass definedClass;
    private final boolean parcelsIndex;
    private final List<ASTType> extraImplementations = new ArrayList<ASTType>();

    public ParcelImplementations(JDefinedClass definedClass, boolean parcelsIndex) {
        this(definedClass, new ASTType[0], parcelsIndex);
    }

    public ParcelImplementations(JDefinedClass definedClass, ASTType[] extraImplementations, boolean parcelsIndex) {
        this.definedClass = definedClass;
        this.parcelsIndex = parcelsIndex;
        if(extraImplementations != null){
            this.extraImplementations.addAll(Arrays.asList(extraImplementations));
        }
    }

    public JDefinedClass getDefinedClass() {
        return definedClass;
    }

    public List<ASTType> getExtraImplementations() {
        return extraImplementations;
    }

    public boolean isParcelsIndex() {
        return parcelsIndex;
    }
}
