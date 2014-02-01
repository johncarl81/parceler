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
import org.androidtransfuse.adapter.ASTFactory;
import org.androidtransfuse.adapter.ASTStringType;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.annotations.*;
import org.androidtransfuse.bootstrap.BootstrapModule;
import org.androidtransfuse.bootstrap.Namespace;
import org.androidtransfuse.gen.ClassGenerationStrategy;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.InjectionBuilderContextFactory;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.androidtransfuse.gen.invocationBuilder.InvocationBuilderStrategy;
import org.androidtransfuse.gen.variableDecorator.VariableExpressionBuilderFactory;
import org.androidtransfuse.transaction.*;
import org.androidtransfuse.util.Logger;
import org.androidtransfuse.util.MessagerLogger;
import org.androidtransfuse.util.matcher.ASTArrayMatcher;import org.androidtransfuse.util.matcher.ImplementsMatcher;import org.androidtransfuse.util.matcher.InheritsMatcher;import org.androidtransfuse.util.matcher.Matchers;
import org.parceler.internal.generator.*;
import org.parceler.internal.matcher.ParcelMatcher;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
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
    public ParcelProcessor getParcelProcessor(Provider<ParcelTransactionWorker> parcelTransactionWorkerProvider,
                                              Provider<ParcelsTransactionWorker> parcelsTransactionWorkerProvider,
                                              Provider<ExternalParcelTransactionWorker> externalParcelTransactionWorkerProvider,
                                              Provider<ExternalParcelRepositoryTransactionWorker> externalParcelRepositoryTransactionWorkerProvider,
                                              ScopedTransactionBuilder scopedTransactionBuilder) {

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
                        new TransactionProcessorParcelJoin<Provider<ASTType>, JDefinedClass>(
                                externalParcelRepositoryProcessor,
                                externalParcelProcessor,
                                parcelProcessor),
                        parcelsProcessor,
                        scopedTransactionBuilder.buildFactory(parcelsTransactionWorkerProvider));

        TransactionProcessor processorChain = new TransactionProcessorChain(externalParcelProcessor, processor);

        return new ParcelProcessor(processorChain, externalParcelRepositoryProcessor, externalParcelProcessor, parcelProcessor, externalParcelRepositoryTransactionWorkerProvider, externalParcelTransactionWorkerProvider, parcelTransactionWorkerProvider, scopedTransactionBuilder);
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
        generators.addPair(char.class, new SingleEntryArrayReadWriteGenerator("createCharArray", "writeCharArray", char.class, codeModel));
        generators.addPair(Character.class, new SingleEntryArrayReadWriteGenerator("createCharArray", "writeCharArray", char.class, codeModel));
        generators.addPair(boolean.class, new SingleEntryArrayReadWriteGenerator("createBooleanArray", "writeBooleanArray", boolean.class, codeModel));
        generators.addPair(Boolean.class, new SingleEntryArrayReadWriteGenerator("createBooleanArray", "writeBooleanArray", boolean.class, codeModel));
        generators.addPair(byte[].class, "createByteArray", "writeByteArray");
        generators.addPair(char[].class, "createCharArray", "writeCharArray");
        generators.addPair(boolean[].class, "createBooleanArray", "writeBooleanArray");
        generators.addPair(String.class, "readString", "writeString");
        generators.addPair("android.os.IBinder", "readStrongBinder", "writeStrongBinder");
        generators.addPair("android.os.Bundle", "readBundle", "writeBundle");
        generators.addPair(Exception.class, "readException", "writeException");
        generators.addPair("android.util.SparseBooleanArray", "readSparseBooleanArray", "writeSparseBooleanArray");
        generators.add(Matchers.type(new ASTStringType("android.util.SparseArray")).ignoreGenerics().build(), new SparseArrayReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(new ImplementsMatcher(new ASTStringType("android.os.Parcelable")), new ParcelableReadWriteGenerator("readParcelable", "writeParcelable", "android.os.Parcelable"));
        generators.add(new ParcelMatcher(externalParcelRepository), new ParcelReadWriteGenerator(generationUtil));
        generators.add(new ASTArrayMatcher(), new ArrayReadWriteGenerator(generationUtil, namer, generators, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(List.class)).ignoreGenerics().build(), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(ArrayList.class)).ignoreGenerics().build(), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(Map.class)).ignoreGenerics().build(), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(HashMap.class)).ignoreGenerics().build(), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(Set.class)).ignoreGenerics().build(), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(Matchers.type(astClassFactory.getType(HashSet.class)).ignoreGenerics().build(), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(new InheritsMatcher(astClassFactory.getType(Serializable.class)), serializableReadWriteGenerator);

        return generators;
    }
}