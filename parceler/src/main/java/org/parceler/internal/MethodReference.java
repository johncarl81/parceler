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

import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class MethodReference implements AccessibleReference {

    private final String name;
    private final ASTMethod method;
    private final ASTType type;

    public MethodReference(String name, ASTType type, ASTMethod method) {
        this.method = method;
        this.type = type;
        this.name = name;
    }

    public ASTMethod getMethod() {
        return method;
    }

    public ASTType getType() {
        return type;
    }

    public <T, R> R accept(ReferenceVisitor<T, R> visitor, T input){
        return visitor.visit(this, input);
    }

    public String getName() {
        return name;
    }
}
