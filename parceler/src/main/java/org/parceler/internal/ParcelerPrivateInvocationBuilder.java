/**
 * Copyright 2011-2015 John Ericksen
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

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.*;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.invocationBuilder.ModifiedInvocationBuilder;
import org.androidtransfuse.model.TypedExpression;
import org.parceler.InjectionUtil;

import javax.inject.Inject;
import java.util.List;

/**
 * Injection Builder for building privately scoped elements.
 *
 * @author John Ericksen
 */
public class ParcelerPrivateInvocationBuilder implements ModifiedInvocationBuilder {

    private final ClassGenerationUtil generationUtil;

    @Inject
    public ParcelerPrivateInvocationBuilder(ClassGenerationUtil generationUtil) {
        this.generationUtil = generationUtil;
    }

    @Override
    public JExpression buildConstructorCall(ASTConstructor constructor, ASTType type, List<? extends JExpression> parameters) {

        //InjectionUtil.setConstructor(Class<T> targetClass, Class[] argClasses,Object[] args)
        JInvocation constructorInvocation = generationUtil.ref(InjectionUtil.class).staticInvoke(InjectionUtil.CALL_CONSTRUCTOR_METHOD)
                .arg(buildTargetType(type));

        //add classes
        JArray classArray = JExpr.newArray(generationUtil.ref(Class.class));
        for (ASTParameter parameterType : constructor.getParameters()) {
            classArray.add(generationUtil.ref(parameterType.getASTType()).dotclass());
        }
        constructorInvocation.arg(classArray);

        //add args
        constructorInvocation.arg(buildArgsArray(parameters));

        return constructorInvocation;
    }

    @Override
    public JInvocation buildMethodCall(boolean cast, ASTMethod method, List<? extends JExpression> parameters, TypedExpression expression) {

        JClass targetType = generationUtil.ref(expression.getType());
        //InjectionUtil.getInstance().setMethod(Class targetClass, Object target, String method, Class[] argClasses,Object[] args)
        JInvocation methodInvocation = generationUtil.ref(InjectionUtil.class).staticInvoke(InjectionUtil.CALL_METHOD_METHOD)
                .arg(buildTargetType(method.getReturnType()))
                .arg(targetType.dotclass())
                .arg(expression.getExpression())
                .arg(method.getName());

        //add classes
        JArray classArray = JExpr.newArray(generationUtil.ref(Class.class));
        for (ASTParameter parameter : method.getParameters()) {
            classArray.add(generationUtil.ref(parameter.getASTType()).dotclass());
        }
        methodInvocation.arg(classArray);

        //add args
        methodInvocation.arg(buildArgsArray(parameters));

        return methodInvocation;
    }

    @Override
    public JExpression buildFieldGet(boolean cast, ASTField field, TypedExpression targetExpression) {
        //InjectionUtil.getInstance().getField(Class returnType, Class targetClass, Object target, String field)
        return generationUtil.ref(InjectionUtil.class).staticInvoke(InjectionUtil.GET_FIELD_METHOD)
                .arg(buildTargetType(field.getASTType()))
                .arg(generationUtil.ref(targetExpression.getType()).dotclass())
                .arg(targetExpression.getExpression())
                .arg(field.getName());
    }

    @Override
    public JStatement buildFieldSet(boolean cast, ASTField field, TypedExpression expression, TypedExpression containingType) {
        //InjectionUtil.getInstance().setField(Class targetClass, Object target, String field, Object value)
        return generationUtil.ref(InjectionUtil.class).staticInvoke(InjectionUtil.SET_FIELD_METHOD)
                .arg(generationUtil.ref(containingType.getType()).dotclass())
                .arg(containingType.getExpression())
                .arg(field.getName())
                .arg(expression.getExpression());
    }

    private JExpression buildArgsArray(Iterable<? extends JExpression> parameters) {
        JArray argArray = JExpr.newArray(generationUtil.ref(Object.class));
        for (JExpression parameter : parameters) {
            argArray.add(parameter);
        }
        return argArray;
    }

    private JExpression buildTargetType(ASTType type) {
        if(type.getGenericArguments().isEmpty()) {
            return generationUtil.ref(type).dotclass();
        }
        else {
            return JExpr._new(generationUtil.ref(InjectionUtil.GenericType.class).narrow(generationUtil.narrowRef(type)));
        }
    }
}
