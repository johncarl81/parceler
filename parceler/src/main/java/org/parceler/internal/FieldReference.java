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

import org.androidtransfuse.adapter.ASTField;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class FieldReference implements AccessibleReference {

    private final ASTType owner;
    private final String name;
    private final ASTField field;

    public FieldReference(ASTType owner, String name, ASTField field) {
        this.owner = owner;
        this.field = field;
        this.name = name;
    }

    public ASTField getField() {
        return field;
    }

    public <T, R> R accept(ReferenceVisitor<T, R> visitor, T input){
        return visitor.visit(this, input);
    }

    public ASTType getType() {
        return field.getASTType();
    }

    public String getName() {
        return name;
    }

    public ASTType getOwner() {
        return owner;
    }
}
