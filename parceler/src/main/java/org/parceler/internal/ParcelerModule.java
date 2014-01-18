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

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.CodeGenerationScope;
import org.androidtransfuse.adapter.ASTArrayType;
import org.androidtransfuse.adapter.ASTFactory;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.annotations.*;
import org.androidtransfuse.bootstrap.BootstrapModule;
import org.androidtransfuse.bootstrap.Namespace;
import org.androidtransfuse.gen.*;
import org.androidtransfuse.gen.invocationBuilder.InvocationBuilderStrategy;
import org.androidtransfuse.gen.variableDecorator.VariableExpressionBuilderFactory;
import org.androidtransfuse.transaction.*;
import org.androidtransfuse.util.Logger;
import org.androidtransfuse.util.MessagerLogger;
import org.androidtransfuse.util.matcher.Matchers;
import org.parceler.internal.generator.*;
import org.parceler.internal.matcher.ImplementsMatcher;
import org.parceler.internal.matcher.InheritsMatcher;
import org.parceler.internal.matcher.ParcelMatcher;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.lang.model.util.Elements;
import java.io.Serializable;
import java.util.*;

@BootstrapModule
@DefineScope(annotation = CodeGenerationScope.class, scope = ThreadLocalScope.class)
@Bindings({
    @Bind(type = InvocationBuilderStrategy.class, to = ParcelerInvocationBuilderStrategy.class),
    @Bind(type = ClassGenerationStrategy.class, to = ParcelerClassGenerationStrategy.class)
})
@Install({
        ASTFactory.class,
        VariableExpressionBuilderFactory.class,
        InjectionBuilderContextFactory.class})
@Namespace("Parceler")
public class ParcelerModule {

    public static final String PARCELS_TRANSACTION_WORKER = "parcelsTransactionWorker";
    public static final String PARCEL_TRANSACTION_WORKER = "parcelTransactionWorker";
    public static final String EXTERNAL_PARCEL_TRANSACTION_WORKER = "externalParcelTransactionWorker";
    public static final String EXTERNAL_PARCEL_REPOSITORY_TRANSACTION_WORKER = "externalParcelRepositoryTransactionWorker";

    @Provides
    @CodeGenerationScope
    public JCodeModel getJCodeModel(){
        return new JCodeModel();
    }

    @Provides
    @Singleton
    public Elements getElements(ProcessingEnvironment processingEnvironment){
        return processingEnvironment.getElementUtils();
    }

    @Provides
    @Singleton
    public Messager getMessenger(ProcessingEnvironment processingEnvironment){
        return processingEnvironment.getMessager();
    }

    @Provides
    @Singleton
    public Logger getLogger(ProcessingEnvironment processingEnvironment){
        return new MessagerLogger(processingEnvironment.getMessager());
    }

    @Provides
    @Singleton
    public Filer getFiler(ProcessingEnvironment processingEnvironment){
        return processingEnvironment.getFiler();
    }

    @Provides
    @Singleton
    public ProcessingEnvironment getProcessingEnvironment(){
        throw new OutOfScopeException("Expected seeded object, unable to construct directly.");
    }

    @Provides
    @Named(PARCEL_TRANSACTION_WORKER)
    public TransactionWorker<Provider<ASTType>, JDefinedClass> getParcelTransactionWorker(JCodeModel codeModel,
                                                                                          FilerSourceCodeWriter codeWriter,
                                                                                          FilerResourceWriter resourceWriter,
                                                                                          ParcelTransactionWorker worker) {
        return new CodeGenerationScopedTransactionWorker<Provider<ASTType>, JDefinedClass>(codeModel, codeWriter, resourceWriter, worker);
    }

    @Provides
    @Named(PARCELS_TRANSACTION_WORKER)
    public TransactionWorker<Map<Provider<ASTType>, JDefinedClass>, Void> getParcelsTransactionWorker(JCodeModel codeModel,
                                                                                                      FilerSourceCodeWriter codeWriter,
                                                                                                      FilerResourceWriter resourceWriter,
                                                                                                      ParcelsTransactionWorker worker) {
        return new CodeGenerationScopedTransactionWorker<Map<Provider<ASTType>, JDefinedClass>, Void>(codeModel, codeWriter, resourceWriter, worker);
    }

    @Provides
    @Named(EXTERNAL_PARCEL_TRANSACTION_WORKER)
    public TransactionWorker<Provider<ASTType>, Map<Provider<ASTType>, JDefinedClass>> getExternalParcelTransactionWorker(JCodeModel codeModel,
                                                                                                                          FilerSourceCodeWriter codeWriter,
                                                                                                                          FilerResourceWriter resourceWriter,
                                                                                                                          ExternalParcelTransactionWorker worker) {
        return new CodeGenerationScopedTransactionWorker<Provider<ASTType>, Map<Provider<ASTType>, JDefinedClass>>(codeModel, codeWriter, resourceWriter, worker);
    }

    @Provides
    @Named(EXTERNAL_PARCEL_REPOSITORY_TRANSACTION_WORKER)
    public TransactionWorker<Provider<ASTType>, Provider<ASTType>> getExternalParcelRepositoryTransactionWorker(JCodeModel codeModel,
                                                                                                                                              FilerSourceCodeWriter codeWriter,
                                                                                                                                              FilerResourceWriter resourceWriter,
                                                                                                                                              ExternalParcelRepositoryTransactionWorker worker) {
        return new CodeGenerationScopedTransactionWorker<Provider<ASTType>, Provider<ASTType>>(codeModel, codeWriter, resourceWriter, worker);
    }

    @Provides
    public ParcelProcessor getParcelProcessor(ParcelTransactionFactory parcelTransactionFactory,
                                              ParcelsTransactionFactory parcelsTransactionFactory,
                                              ExternalParcelTransactionFactory externalParcelTransactionFactory,
                                              ExternalParcelRepositoryTransactionFactory externalParcelRepositoryTransactionFactory) {

        TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>> externalParcelRepositoryProcessor =
                new TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>>();
        TransactionProcessorPool<Provider<ASTType>, Map<Provider<ASTType>, JDefinedClass>> externalParcelProcessor =
                new TransactionProcessorPool<Provider<ASTType>, Map<Provider<ASTType>, JDefinedClass>>();
        TransactionProcessorPool<Provider<ASTType>, JDefinedClass> parcelProcessor =
                new TransactionProcessorPool<Provider<ASTType>, JDefinedClass>();
        TransactionProcessorPool<Map<Provider<ASTType>, JDefinedClass>, Void> parcelsProcessor =
                new TransactionProcessorPool<Map<Provider<ASTType>, JDefinedClass>, Void>();

        TransactionProcessor processor =
                new TransactionProcessorChannel<Provider<ASTType>, JDefinedClass, Void>(
                        new TransactionProcessorParcelJoin<Provider<ASTType>, JDefinedClass>(externalParcelRepositoryProcessor, externalParcelProcessor, parcelProcessor), parcelsProcessor, parcelsTransactionFactory);

        TransactionProcessor processorChain = new TransactionProcessorChain(externalParcelProcessor, processor);

        return new ParcelProcessor(processorChain, externalParcelRepositoryProcessor, externalParcelProcessor, parcelProcessor, externalParcelRepositoryTransactionFactory, externalParcelTransactionFactory, parcelTransactionFactory);
    }

    @Provides
    public Generators getGenerators(ASTClassFactory astClassFactory,
                                    ClassGenerationUtil generationUtil,
                                    ExternalParcelRepository externalParcelRepository,
                                    UniqueVariableNamer namer,
                                    JCodeModel codeModel,
                                    SerializableReadWriteGenerator serializableReadWriteGenerator){

        return addGenerators(new Generators(astClassFactory), astClassFactory, generationUtil, externalParcelRepository, namer, codeModel, serializableReadWriteGenerator);
    }
    
    public static Generators addGenerators(Generators generators,
                                           ASTClassFactory astClassFactory,
                                           ClassGenerationUtil generationUtil,
                                           ExternalParcelRepository externalParcelRepository,
                                           UniqueVariableNamer namer,
                                           JCodeModel codeModel,
                                           SerializableReadWriteGenerator serializableReadWriteGenerator){

        generators.addPair(byte.class, "readByte", "writeByte");
        generators.addPair(Byte.class, "readByte", "writeByte", byte.class);
        generators.addPair(double.class, "readDouble", "writeDouble");
        generators.addPair(Double.class, "readDouble", "writeDouble", double.class);
        generators.addPair(float.class, "readFloat", "writeFloat");
        generators.addPair(Float.class, "readFloat", "writeFloat", float.class);
        generators.addPair(int.class, "readInt", "writeInt");
        generators.addPair(Integer.class, "readInt", "writeInt", int.class);
        generators.addPair(long.class, "readLong", "writeLong");
        generators.addPair(Long.class, "readLong", "writeLong", long.class);
        generators.addPair(byte[].class, "createByteArray", "writeByteArray");
        generators.addPair(char[].class, "createCharArray", "writeCharArray");
        generators.addPair(boolean[].class, "createBooleanArray", "writeBooleanArray");
        generators.addPair(int[].class, "createIntArray", "writeIntArray");
        generators.addPair(long[].class, "createLongArray", "writeLongArray");
        generators.addPair(float[].class, "createFloatArray", "writeFloatArray");
        generators.addPair(double[].class, "createDoubleArray", "writeDoubleArray");
        generators.addPair(String[].class, "createStringArray", "writeStringArray");
        generators.addPair(String.class, "readString", "writeString");
        generators.addPair("android.os.IBinder", "readStrongBinder", "writeStrongBinder");
        generators.addPair("android.os.Bundle", "readBundle", "writeBundle");
        generators.addPair(Exception.class, "readException", "writeException");
        generators.addPair("android.util.SparseBooleanArray", "readSparseBooleanArray", "writeSparseBooleanArray");
        generators.add(Matchers.type(new ASTStringType("android.util.SparseArray")).ignoreGenerics().build(), new ClassloaderReadWriteGenerator("readSparseArray", "writeSparseArray", "android.util.SparseArray"));
        generators.add(new ImplementsMatcher(new ASTStringType("android.os.Parcelable")), new ParcelableReadWriteGenerator("readParcelable", "writeParcelable", "android.os.Parcelable"));
        generators.add(new ImplementsMatcher(new ASTArrayType(new ASTStringType("android.os.Parcelable"))), new ParcelableReadWriteGenerator("readParcelableArray", "writeParcelableArray", "[Landroid.os.Parcelable;"));
        generators.add(new ParcelMatcher(externalParcelRepository), new ParcelReadWriteGenerator(generationUtil));
        generators.add(Matchers.type(astClassFactory.getType(List.class)).ignoreGenerics().build(), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(ArrayList.class)).ignoreGenerics().build(), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(Map.class)).ignoreGenerics().build(), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(HashMap.class)).ignoreGenerics().build(), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(Set.class)).ignoreGenerics().build(), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(HashSet.class)).ignoreGenerics().build(), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(new InheritsMatcher(astClassFactory.getType(Serializable.class)), serializableReadWriteGenerator);
        generators.add(Matchers.type(astClassFactory.getType(Object[].class)).build(), new ClassloaderReadWriteGenerator("readArray", "writeArray", Object[].class));

        return generators;
    }
}