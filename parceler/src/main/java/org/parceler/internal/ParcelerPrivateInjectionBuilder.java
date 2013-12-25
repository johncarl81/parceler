package org.parceler.internal;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.invocationBuilder.ModifierInjectionBuilder;
import org.parceler.InjectionUtil;

import javax.inject.Inject;
import java.util.List;

/**
 * Injection Builder for building privately scoped elements.
 *
 * @author John Ericksen
 */
public class ParcelerPrivateInjectionBuilder implements ModifierInjectionBuilder {

    private final ClassGenerationUtil generationUtil;

    @Inject
    public ParcelerPrivateInjectionBuilder(ClassGenerationUtil generationUtil) {
        this.generationUtil = generationUtil;
    }

    @Override
    public JExpression buildConstructorCall(ASTType type, List<ASTType> parameterTypes, Iterable<? extends JExpression> parameters) {

        //InjectionUtil.setConstructor(Class<T> targetClass, Class[] argClasses,Object[] args)
        JInvocation constructorInvocation = generationUtil.ref(InjectionUtil.class).staticInvoke(InjectionUtil.CALL_CONSTRUCTOR_METHOD)
                .arg(generationUtil.ref(type).dotclass());

        //add classes
        JArray classArray = JExpr.newArray(generationUtil.ref(Class.class));
        for (ASTType parameterType : parameterTypes) {
            classArray.add(generationUtil.ref(parameterType).dotclass());
        }
        constructorInvocation.arg(classArray);

        //add args
        constructorInvocation.arg(buildArgsArray(parameters));

        return constructorInvocation;
    }

    @Override
    public JInvocation buildMethodCall(ASTType returnType, String methodName, Iterable<? extends JExpression> parameters, List<ASTType> injectionNodeType, ASTType targetExpressionType, JExpression targetExpression) {

        JClass targetType = generationUtil.ref(targetExpressionType.getName());
        //InjectionUtil.getInstance().setMethod(Class targetClass, Object target, String method, Class[] argClasses,Object[] args)
        JInvocation methodInvocation = generationUtil.ref(InjectionUtil.class).staticInvoke(InjectionUtil.CALL_METHOD_METHOD)
                .arg(generationUtil.ref(returnType.getName()).dotclass())
                .arg(targetType.dotclass())
                .arg(targetExpression)
                .arg(methodName);

        //add classes
        JArray classArray = JExpr.newArray(generationUtil.ref(Class.class));
        for (ASTType injectionNode : injectionNodeType) {
            classArray.add(generationUtil.ref(injectionNode).dotclass());
        }
        methodInvocation.arg(classArray);

        //add args
        methodInvocation.arg(buildArgsArray(parameters));

        return methodInvocation;
    }

    @Override
    public JExpression buildFieldGet(ASTType returnType, ASTType variableType, JExpression variable, String name) {
        return generationUtil.ref(InjectionUtil.class).staticInvoke(InjectionUtil.GET_FIELD_METHOD)
                .arg(generationUtil.ref(returnType).dotclass())
                .arg(generationUtil.ref(variableType).dotclass())
                .arg(variable)
                .arg(name);
    }

    @Override
    public JStatement buildFieldSet(ASTType expressionType, JExpression expression, ASTType containingType, ASTType fieldType, String fieldName, JExpression variable) {
        JClass variableType = generationUtil.ref(containingType);

        return generationUtil.ref(InjectionUtil.class).staticInvoke(InjectionUtil.SET_FIELD_METHOD)
                .arg(variableType.dotclass())
                .arg(variable)
                .arg(fieldName)
                .arg(expression);
    }

    private JExpression buildArgsArray(Iterable<? extends JExpression> parameters) {
        JArray argArray = JExpr.newArray(generationUtil.ref(Object.class));
        for (JExpression parameter : parameters) {
            argArray.add(parameter);
        }
        return argArray;
    }
}