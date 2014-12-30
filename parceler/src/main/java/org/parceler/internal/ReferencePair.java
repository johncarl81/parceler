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

import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class ReferencePair<T extends Reference> {

    private final String name;
    private final T reference;
    private final AccessibleReference accessor;
    private final ASTType converter;

    public ReferencePair(String name, T reference, AccessibleReference accessor, ASTType converter) {
        this.name = name;
        this.reference = reference;
        this.accessor = accessor;
        this.converter = converter;
    }

    public String getName() {
        return name;
    }

    public T getReference() {
        return reference;
    }

    public AccessibleReference getAccessor() {
        return accessor;
    }

    public ASTType getConverter() {
        return converter;
    }
}
