package org.androidtransfuse.gen;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.util.Generated;
import org.androidtransfuse.util.Logger;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//temporary update to get to Transfuse 0.2.2
public class ClassGenerationUtil {

    private static final Map<String, String> PROHIBITED_PACKAGES = new HashMap<String, String>();

    static{
        PROHIBITED_PACKAGES.put("java.", "java_.");
        PROHIBITED_PACKAGES. put("javax.", "javax_.");
    }

    // ISO 8601 standard date format
    private final DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
    private final JCodeModel codeModel;
    private final UniqueVariableNamer namer;

    @Inject
    public ClassGenerationUtil(JCodeModel codeModel, UniqueVariableNamer namer) {
        this.codeModel = codeModel;
        this.namer = namer;
    }

    public ClassGenerationUtil(JCodeModel codeModel, Logger logger, UniqueVariableNamer namer){
        this(codeModel, namer);
    }

    public String getPackage(String inputPackage){
        String validPackage = inputPackage;
        for (Map.Entry<String, String> prohibitedPackage : PROHIBITED_PACKAGES.entrySet()) {
            if(inputPackage.startsWith(prohibitedPackage.getKey())){
                validPackage = inputPackage.replaceFirst(prohibitedPackage.getKey(), prohibitedPackage.getValue());
            }
        }
        return validPackage;
    }

    public JDefinedClass defineClass(PackageClass className) throws JClassAlreadyExistsException {
        return defineClass(className, false);
    }

    public JDefinedClass defineClass(PackageClass className, boolean enforceUniqueName) throws JClassAlreadyExistsException {

        // Avoid prohibited package names
        String packageName = getPackage(className.getPackage());

        JPackage jPackage = codeModel._package(packageName);
        String name;
        if(enforceUniqueName){
            name = namer.generateClassName(className.getClassName());
        }
        else{
            name = className.getClassName();
        }
        JDefinedClass definedClass = jPackage._class(name);

        annotateGeneratedClass(definedClass);

        return definedClass;
    }

    public JClass ref(PackageClass packageClass){
        return ref(packageClass.getCanonicalName());
    }

    public JClass ref(ASTType astType){
        String typeName = astType.getName();
        if(astType.isArray()){
            return ref(typeName.substring(0, typeName.length() - 2)).array();
        }
        return ref(typeName);
    }

    public JClass ref(String typeName){
        return codeModel.ref(typeName);
    }

    public JClass ref(Class<?> clazz) {
        return codeModel.ref(clazz);
    }

    /**
     * Annotates the input class with the `@Generated` annotation
     *
     * @param definedClass input codemodel class
     */
    private void annotateGeneratedClass(JDefinedClass definedClass) {
        definedClass.annotate(Generated.class)
                .param("value", "org.androidtransfuse.TransfuseAnnotationProcessor")
                .param("date", iso8601.format(new Date()));
    }
}