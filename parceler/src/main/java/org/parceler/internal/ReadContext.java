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

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import org.androidtransfuse.adapter.ASTType;

/**
 * @author John Ericksen
 */
public class ReadContext {

    private final ASTType type;
    private final JBlock body;
    private final JFieldVar wrapped;
    private final JExpression getExpression;
    private final ASTType getExpressionType;

    public ReadContext(JBlock body, ASTType type, JFieldVar wrapped, ASTType getExpressionType, JExpression getExpression) {
        this.body = body;
        this.wrapped = wrapped;
        this.getExpression = getExpression;
        this.getExpressionType = getExpressionType;
        this.type = type;
    }

    public JBlock getBody() {
        return body;
    }

    public JFieldVar getWrapped() {
        return wrapped;
    }

    public JExpression getGetExpression() {
        return getExpression;
    }

    public ASTType getGetExpressionType() {
        return getExpressionType;
    }

    public ASTType getType() {
        return type;
    }
}
