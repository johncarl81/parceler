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

import com.sun.codemodel.JBlock;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.model.TypedExpression;

/**
 * @author John Ericksen
 */
public class ReadContext {

    private final JBlock body;
    private final TypedExpression wrapped;
    private final TypedExpression getExpression;
    private final ASTType container;

    public ReadContext(ASTType container, JBlock body, TypedExpression wrapped, TypedExpression getExpression) {
        this.body = body;
        this.wrapped = wrapped;
        this.getExpression = getExpression;
        this.container = container;
    }

    public JBlock getBody() {
        return body;
    }

    public TypedExpression getWrapped() {
        return wrapped;
    }

    public TypedExpression getGetExpression() {
        return getExpression;
    }

    public ASTType getContainer() {
        return container;
    }
}
