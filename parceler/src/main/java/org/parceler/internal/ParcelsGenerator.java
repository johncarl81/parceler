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

import com.sun.codemodel.*;
import org.androidtransfuse.Parcels;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.androidtransfuse.util.Repository;
import org.androidtransfuse.util.TransfuseRuntimeException;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class ParcelsGenerator {

    public static final PackageClass PARCELS_NAME = new PackageClass(Parcels.PARCELS_PACKAGE, Parcels.PARCELS_NAME);
    public static final PackageClass REPOSITORY_NAME = new PackageClass(Parcels.PARCELS_PACKAGE, Parcels.PARCELS_REPOSITORY_NAME);
    private static final String MAP_NAME = "parcelWrappers";

    private final ClassGenerationUtil generationUtil;
    private final JCodeModel codeModel;
    private final UniqueVariableNamer uniqueVariableNamer;

    @Inject
    public ParcelsGenerator(ClassGenerationUtil generationUtil, JCodeModel codeModel, UniqueVariableNamer uniqueVariableNamer) {
        this.generationUtil = generationUtil;
        this.codeModel = codeModel;
        this.uniqueVariableNamer = uniqueVariableNamer;
    }

    public void generate(Map<Provider<ASTType>, JDefinedClass> generated) {

        try {
            JDefinedClass parcelsDefinedClass = generationUtil.defineClass(REPOSITORY_NAME);

            JClass parcelableFactoryWildcard = generationUtil.ref(Parcels.ParcelableFactory.class).narrow(codeModel.wildcard());
            parcelsDefinedClass._implements(generationUtil.ref(Repository.class).narrow(parcelableFactoryWildcard));

            JClass mapRef = codeModel.ref(Map.class).narrow(Class.class).narrow(parcelableFactoryWildcard);
            JClass hashMapRef = codeModel.ref(HashMap.class).narrow(Class.class).narrow(parcelableFactoryWildcard);

            JFieldVar parcelWrappers = parcelsDefinedClass.field(JMod.PRIVATE | JMod.FINAL, mapRef, MAP_NAME, JExpr._new(hashMapRef));

            JBlock constructorBody = parcelsDefinedClass.constructor(JMod.PUBLIC).body();

            for (Map.Entry<Provider<ASTType>, JDefinedClass> astTypeJDefinedClassEntry : generated.entrySet()) {

                JClass type = generationUtil.ref(astTypeJDefinedClassEntry.getKey().get());

                String innerClassName = uniqueVariableNamer.generateClassName(astTypeJDefinedClassEntry.getValue()) + Parcels.IMPL_EXT;

                JDefinedClass factoryDefinedClass = parcelsDefinedClass._class(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, innerClassName);

                factoryDefinedClass._implements(codeModel.ref(Parcels.ParcelableFactory.class).narrow(type));

                JMethod method = factoryDefinedClass.method(JMod.PUBLIC, astTypeJDefinedClassEntry.getValue(), Parcels.ParcelableFactory.BUILD_PARCELABLE);
                method.annotate(Override.class);
                JVar input = method.param(type, "input");

                method.body()._return(JExpr._new(astTypeJDefinedClassEntry.getValue()).arg(input));

                constructorBody.invoke(parcelWrappers, "put").arg(type.staticRef("class")).arg(JExpr._new(factoryDefinedClass));
            }

            JMethod getMethod = parcelsDefinedClass.method(JMod.PUBLIC, mapRef, "get");
            getMethod.body()._return(parcelWrappers);

        } catch (JClassAlreadyExistsException e) {
            throw new TransfuseRuntimeException("Class already exists", e);
        }


    }
}
