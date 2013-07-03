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

/**
 * @author John Ericksen
 */
public class GetterSetterMethodPair {

    private final String name;
    private final ASTMethod getter;
    private final ASTMethod setter;

    public GetterSetterMethodPair(String name, ASTMethod getter, ASTMethod setter) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
    }

    public ASTMethod getGetter() {
        return getter;
    }

    public ASTMethod getSetter() {
        return setter;
    }

    public String getName() {
        return name;
    }
}
