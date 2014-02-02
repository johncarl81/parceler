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

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import org.androidtransfuse.gen.InvocationBuilder;

import javax.inject.Inject;
import java.util.Collections;

/**
 * @author John Ericksen
 */
public class WriteReferenceVisitor implements ReferenceVisitor<WriteContext, JExpression> {

    private InvocationBuilder invocationBuilder;

    @Inject
    public WriteReferenceVisitor(InvocationBuilder invocationBuilder) {
        this.invocationBuilder = invocationBuilder;
    }

    @Override
    public JExpression visit(FieldReference fieldReference, WriteContext input) {
        JFieldVar wrapped = input.getWrapped();

        return invocationBuilder.buildFieldGet(
                fieldReference.getType(),
                input.getType(),
                wrapped,
                fieldReference.getField().getName(),
                fieldReference.getField().getAccessModifier());
    }

    @Override
    public JExpression visit(MethodReference methodReference, WriteContext input) {
        JFieldVar wrapped = input.getWrapped();

        return invocationBuilder.buildMethodCall(
                methodReference.getMethod().getAccessModifier(),
                methodReference.getType(),
                methodReference.getMethod().getName(),
                Collections.EMPTY_LIST,
                Collections.EMPTY_LIST,
                input.getType(),
                wrapped);
    }
}
