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
import org.androidtransfuse.gen.InvocationBuilder;

import javax.inject.Inject;
import java.util.Collections;

/**
 * @author John Ericksen
 */
public class ReadReferenceVisitor implements ReferenceVisitor<ReadContext, Void> {

    private InvocationBuilder invocationBuilder;

    @Inject
    public ReadReferenceVisitor(InvocationBuilder invocationBuilder) {
        this.invocationBuilder = invocationBuilder;
    }

    @Override
    public Void visit(FieldReference fieldReference, ReadContext input) {
        JBlock body = input.getBody();

        body.add(invocationBuilder.buildFieldSet(
                fieldReference.getField().getAccessModifier(),
                input.getType(),
                input.getWrapped(),
                fieldReference.getType(),
                fieldReference.getField().getName(),
                input.getGetExpressionType(),
                input.getGetExpression()
        ));
        return null;
    }

    @Override
    public Void visit(MethodReference methodReference, ReadContext input) {
        JBlock body = input.getBody();

        body.add(invocationBuilder.buildMethodCall(
                methodReference.getMethod().getAccessModifier(),
                methodReference.getMethod().getReturnType(),
                methodReference.getMethod().getName(),
                Collections.singletonList(input.getGetExpression()),
                Collections.singletonList(input.getGetExpressionType()),
                input.getType(),
                input.getWrapped()));
        return null;
    }
}
