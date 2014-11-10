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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
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
import org.androidtransfuse.config.OutOfScopeException;
import org.androidtransfuse.config.ThreadLocalScope;
import org.androidtransfuse.gen.ClassGenerationStrategy;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.InjectionBuilderContextFactory;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.androidtransfuse.gen.invocationBuilder.InvocationBuilderStrategy;
import org.androidtransfuse.gen.variableDecorator.VariableExpressionBuilderFactory;
import org.androidtransfuse.transaction.*;
import org.androidtransfuse.util.Logger;
import org.androidtransfuse.util.MessagerLogger;
import org.androidtransfuse.util.matcher.ASTArrayMatcher;
import org.androidtransfuse.util.matcher.ImplementsMatcher;
import org.androidtransfuse.util.matcher.InheritsMatcher;
import org.androidtransfuse.util.matcher.Matchers;
import org.androidtransfuse.validation.Validator;
import org.parceler.Generated;
import org.parceler.ParcelAnnotationProcessor;
import org.parceler.internal.generator.*;
import org.parceler.internal.matcher.GenericCollectionMatcher;
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
    @Bind(type = InvocationBuilderStrategy.class, to = ParcelerInvocationBuilderStrategy.class)
})
@Install({
        ASTFactory.class,
        VariableExpressionBuilderFactory.class,
        InjectionBuilderContextFactory.class})
@Namespace("Parceler")
public class ParcelerModule {

    @Provides
    public ClassGenerationStrategy getClassGenerationStrategy(){
        return new ClassGenerationStrategy(Generated.class, ParcelAnnotationProcessor.class.getName());
    }

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
        return new MessagerLogger(getLogPreprend(), processingEnvironment.getMessager());
    }

    @Provides
    @Named(Validator.LOG_PREPEND)
    public String getLogPreprend(){
        return "Parceler: ";
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
                                              Provider<ParcelsGenerator> parcelsTransactionWorkerProvider,
                                              Provider<ExternalParcelTransactionWorker> externalParcelTransactionWorkerProvider,
                                              Provider<ExternalParcelRepositoryTransactionWorker> externalParcelRepositoryTransactionWorkerProvider,
                                              Provider<PackageHelperGeneratorAdapter> packageHelperGeneratorAdapterProvider,
                                              ScopedTransactionBuilder scopedTransactionBuilder) {

        TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>> externalParcelRepositoryProcessor =
                new TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>>();
        TransactionProcessorPool<Provider<ASTType>, Map<Provider<ASTType>, ParcelImplementations>> externalParcelProcessor =
                new ResultTransformerProcessor(
                    new TransactionProcessorPool<Provider<ASTType>, Map<Provider<ASTType>, ParcelImplementations>>(),
                        new Function<Map<Provider<ASTType>, ParcelImplementations>, Map<Provider<ASTType>, ParcelImplementations>>(){
                            public Map<Provider<ASTType>, ParcelImplementations> apply(Map<Provider<ASTType>, ParcelImplementations> input) {
                                return Maps.filterValues(input, new Predicate<ParcelImplementations>() {
                                    public boolean apply(ParcelImplementations parcelImplementations) {
                                        //removes all decriptors where parcelsIndex = false, in turn removing them from processing
                                        //via ParcelsProcessor
                                        return parcelImplementations.isParcelsIndex();
                                    }
                                });
                            }
                        });
        TransactionProcessorPool<Provider<ASTType>, ParcelImplementations> parcelProcessor =
                new ResultTransformerProcessor(new TransactionProcessorPool<Provider<ASTType>, ParcelImplementations>(),
                        new Function<ParcelImplementations, ParcelImplementations>() {
                            public ParcelImplementations apply(ParcelImplementations parcelImplementations) {
                                //removes all decriptors where parcelsIndex = false, in turn removing them from processing
                                //via ParcelsProcessor
                                if(parcelImplementations.isParcelsIndex()){
                                    return parcelImplementations;
                                }
                                return null;
                            }
                        });
        TransactionProcessorPool<Map<Provider<ASTType>, ParcelImplementations>, JDefinedClass> parcelsProcessor =
                new TransactionProcessorPool<Map<Provider<ASTType>, ParcelImplementations>, JDefinedClass>();

        TransactionProcessor processor =
                new TransactionProcessorChannel<Provider<ASTType>, ParcelImplementations, JDefinedClass>(
                        new TransactionProcessorParcelJoin<Provider<ASTType>, ParcelImplementations>(
                                externalParcelRepositoryProcessor,
                                externalParcelProcessor,
                                parcelProcessor),
                        parcelsProcessor,
                        scopedTransactionBuilder.buildFactory(parcelsTransactionWorkerProvider));

        TransactionProcessor processorChain = new TransactionProcessorChain(processor,
                        new TransactionProcessorPredefined(ImmutableSet.of(scopedTransactionBuilder.build(packageHelperGeneratorAdapterProvider))));

        return new ParcelProcessor(processorChain, externalParcelRepositoryProcessor, externalParcelProcessor, parcelProcessor, externalParcelRepositoryTransactionWorkerProvider, externalParcelTransactionWorkerProvider, parcelTransactionWorkerProvider, scopedTransactionBuilder);
    }

    @Provides
    public Generators getGenerators(ASTClassFactory astClassFactory,
                                    ClassGenerationUtil generationUtil,
                                    ExternalParcelRepository externalParcelRepository,
                                    UniqueVariableNamer namer,
                                    JCodeModel codeModel,
                                    SerializableReadWriteGenerator serializableReadWriteGenerator,
                                    NullCheckFactory nullCheckFactory,
                                    ParcelableAnalysis analysis,
                                    Provider<ParcelableGenerator> generator){

        return addGenerators(new Generators(astClassFactory), astClassFactory, generationUtil, externalParcelRepository, namer, codeModel, serializableReadWriteGenerator, nullCheckFactory, analysis, generator);
    }
    
    public static Generators addGenerators(Generators generators,
                                           ASTClassFactory astClassFactory,
                                           ClassGenerationUtil generationUtil,
                                           ExternalParcelRepository externalParcelRepository,
                                           UniqueVariableNamer namer,
                                           JCodeModel codeModel,
                                           SerializableReadWriteGenerator serializableReadWriteGenerator,
                                           NullCheckFactory nullCheckFactory,
                                           ParcelableAnalysis analysis,
                                           Provider<ParcelableGenerator> generator){

        generators.addPair(byte.class, "readByte", "writeByte");
        generators.addPair(Byte.class, nullCheckFactory.get(Byte.class, generators, byte.class));
        generators.addPair(double.class, "readDouble", "writeDouble");
        generators.addPair(Double.class, nullCheckFactory.get(Double.class, generators, double.class));
        generators.addPair(float.class, "readFloat", "writeFloat");
        generators.addPair(Float.class, nullCheckFactory.get(Float.class, generators, float.class));
        generators.addPair(int.class, "readInt", "writeInt");
        generators.addPair(Integer.class, nullCheckFactory.get(Integer.class, generators, int.class));
        generators.addPair(long.class, "readLong", "writeLong");
        generators.addPair(Long.class, nullCheckFactory.get(Long.class, generators, long.class));
        generators.addPair(char.class, new SingleEntryArrayReadWriteGenerator("createCharArray", "writeCharArray", char.class, codeModel));
        generators.addPair(Character.class, nullCheckFactory.get(Character.class, generators, char.class));
        generators.addPair(boolean.class, new BooleanEntryReadWriteGenerator(codeModel));
        generators.addPair(Boolean.class, nullCheckFactory.get(Boolean.class, generators, boolean.class));
        generators.addPair(byte[].class, "createByteArray", "writeByteArray");
        generators.addPair(char[].class, "createCharArray", "writeCharArray");
        generators.addPair(boolean[].class, "createBooleanArray", "writeBooleanArray");
        generators.addPair(String.class, "readString", "writeString");
        generators.addPair("android.os.IBinder", "readStrongBinder", "writeStrongBinder");
        generators.add(Matchers.type(new ASTStringType("android.os.Bundle")).ignoreGenerics().build(), new BundleReadWriteGenerator("readBundle", "writeBundle", "android.os.Bundle"));
        generators.addPair("android.util.SparseBooleanArray", "readSparseBooleanArray", "writeSparseBooleanArray");
        generators.add(Matchers.type(new ASTStringType("android.util.SparseArray")).ignoreGenerics().build(), new SparseArrayReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(new ImplementsMatcher(new ASTStringType("android.os.Parcelable")), new ParcelableReadWriteGenerator("readParcelable", "writeParcelable", "android.os.Parcelable"));
        generators.add(new ParcelMatcher(externalParcelRepository), new ParcelReadWriteGenerator(generationUtil, analysis, generator, namer));
        generators.add(new ASTArrayMatcher(), new ArrayReadWriteGenerator(generationUtil, namer, generators, codeModel));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(List.class), generators, 1), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, ArrayList.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(ArrayList.class), generators, 1), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, ArrayList.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(LinkedList.class), generators, 1), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, LinkedList.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(Map.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, HashMap.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(HashMap.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, HashMap.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(LinkedHashMap.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, LinkedHashMap.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(SortedMap.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, TreeMap.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(TreeMap.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, TreeMap.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(Set.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, HashSet.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(HashSet.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, HashSet.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(SortedSet.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, TreeSet.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(TreeSet.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, TreeSet.class));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(LinkedHashSet.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, LinkedHashSet.class));
        generators.add(new InheritsMatcher(astClassFactory.getType(Serializable.class)), serializableReadWriteGenerator);

        return generators;
    }
}