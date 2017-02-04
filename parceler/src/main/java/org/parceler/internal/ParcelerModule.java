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

import com.google.common.collect.ImmutableSet;
import com.sun.codemodel.JCodeModel;
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
import org.androidtransfuse.util.matcher.InheritsMatcher;
import org.androidtransfuse.util.matcher.Matchers;
import org.androidtransfuse.validation.Validator;
import org.parceler.Generated;
import org.parceler.ParcelAnnotationProcessor;
import org.parceler.internal.generator.*;
import org.parceler.internal.matcher.*;

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

    public static final String STACKTRACE = "parcelerStacktrace";
    public static final String DEBUG = "parcelerDebugLogging";

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
    public Logger getLogger(ProcessingEnvironment processingEnvironment, @Named(DEBUG) boolean debug){
        return new MessagerLogger(getLogPreprend(), processingEnvironment.getMessager(), debug);
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
    @Named(STACKTRACE)
    public boolean getStacktraceParameter(ProcessingEnvironment processingEnvironment){
        return processingEnvironment.getOptions().containsKey(STACKTRACE);
    }

    @Provides
    @Named(DEBUG)
    public boolean getDebugOption(ProcessingEnvironment processingEnvironment){
        return processingEnvironment.getOptions().containsKey(DEBUG);
    }

    @Provides
    public ParcelProcessor getParcelProcessor(Provider<ParcelTransactionWorker> parcelTransactionWorkerProvider,
                                              Provider<ExternalParcelTransactionWorker> externalParcelTransactionWorkerProvider,
                                              Provider<ExternalParcelRepositoryTransactionWorker> externalParcelRepositoryTransactionWorkerProvider,
                                              Provider<PackageHelperGeneratorAdapter> packageHelperGeneratorAdapterProvider,
                                              ScopedTransactionBuilder scopedTransactionBuilder,
                                              Logger logger,
                                              @Named(STACKTRACE) boolean stacktrace) {

        TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>> externalParcelRepositoryProcessor =
                new TransactionProcessorPool<Provider<ASTType>, Provider<ASTType>>();
        TransactionProcessorPool<Provider<ASTType>, Void> externalParcelProcessor =
                new TransactionProcessorPool<Provider<ASTType>, Void>();
        TransactionProcessorPool<Provider<ASTType>, Void> parcelProcessor = new TransactionProcessorPool<Provider<ASTType>, Void>();

        TransactionProcessor processor = new TransactionProcessorComposite(ImmutableSet.of(externalParcelRepositoryProcessor,
                                externalParcelProcessor,
                                parcelProcessor));

        TransactionProcessor processorChain = new TransactionProcessorChain(processor,
                        new TransactionProcessorPredefined(ImmutableSet.of(scopedTransactionBuilder.build(packageHelperGeneratorAdapterProvider))));

        return new ParcelProcessor(processorChain, externalParcelRepositoryProcessor, externalParcelProcessor, parcelProcessor, externalParcelRepositoryTransactionWorkerProvider, externalParcelTransactionWorkerProvider, parcelTransactionWorkerProvider, scopedTransactionBuilder, logger, stacktrace);
    }

    @Provides
    public Generators getGenerators(ASTClassFactory astClassFactory,
                                    ClassGenerationUtil generationUtil,
                                    ExternalParcelRepository externalParcelRepository,
                                    UniqueVariableNamer namer,
                                    JCodeModel codeModel,
                                    SerializableReadWriteGenerator serializableReadWriteGenerator,
                                    NullCheckFactory nullCheckFactory,
                                    LinkParcelReadWriteGenerator parcelReadWriteGenerator,
                                    EnumReadWriteGenerator enumReadWriteGenerator){

        return addGenerators(new Generators(astClassFactory), astClassFactory, generationUtil, externalParcelRepository, namer, codeModel, serializableReadWriteGenerator, nullCheckFactory, parcelReadWriteGenerator, enumReadWriteGenerator);
    }
    
    public static Generators addGenerators(Generators generators,
                                           ASTClassFactory astClassFactory,
                                           ClassGenerationUtil generationUtil,
                                           ExternalParcelRepository externalParcelRepository,
                                           UniqueVariableNamer namer,
                                           JCodeModel codeModel,
                                           SerializableReadWriteGenerator serializableReadWriteGenerator,
                                           NullCheckFactory nullCheckFactory,
                                           LinkParcelReadWriteGenerator parcelReadWriteGenerator,
                                           EnumReadWriteGenerator enumReadWriteGenerator){

        generators.addPair(byte.class, "readByte", "writeByte");
        generators.addPair(Byte.class, nullCheckFactory.get(generators, byte.class));
        generators.addPair(double.class, "readDouble", "writeDouble");
        generators.addPair(Double.class, nullCheckFactory.get(generators, double.class));
        generators.addPair(float.class, "readFloat", "writeFloat");
        generators.addPair(Float.class, nullCheckFactory.get(generators, float.class));
        generators.addPair(int.class, "readInt", "writeInt");
        generators.addPair(Integer.class, nullCheckFactory.get(generators, int.class));
        generators.addPair(long.class, "readLong", "writeLong");
        generators.addPair(Long.class, nullCheckFactory.get(generators, long.class));
        generators.addPair(char.class, new SingleEntryArrayReadWriteGenerator("createCharArray", "writeCharArray", char.class, codeModel));
        generators.addPair(Character.class, nullCheckFactory.get(generators, char.class));
        generators.addPair(boolean.class, new BooleanEntryReadWriteGenerator(codeModel));
        generators.addPair(Boolean.class, nullCheckFactory.get(generators, boolean.class));
        generators.addPair(byte[].class, "createByteArray", "writeByteArray");
        generators.addPair(char[].class, "createCharArray", "writeCharArray");
        generators.addPair(boolean[].class, "createBooleanArray", "writeBooleanArray");
        generators.addPair(String.class, "readString", "writeString");
        generators.addPair("android.os.IBinder", "readStrongBinder", "writeStrongBinder");
        generators.add(Matchers.type(new ASTStringType("android.os.Bundle")).ignoreGenerics().build(), new BundleReadWriteGenerator("readBundle", "writeBundle", "android.os.Bundle"));
        generators.add(new ObservableFieldMatcher(generators), nullCheckFactory.get(new ObservableFieldReadWriteGenerator(generators, generationUtil)));
        generators.addPair("android.util.SparseBooleanArray", "readSparseBooleanArray", "writeSparseBooleanArray");
        generators.add(Matchers.type(new ASTStringType("android.util.SparseArray")).ignoreGenerics().build(), new SparseArrayReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel));
        generators.add(new InheritsParcelableMatcher(), new ParcelableReadWriteGenerator("readParcelable", "writeParcelable", "android.os.Parcelable"));
        generators.add(new EnumMatcher(), enumReadWriteGenerator);
        generators.add(new ParcelMatcher(externalParcelRepository), parcelReadWriteGenerator);
        generators.add(new ASTArrayMatcher(), new ArrayReadWriteGenerator(generationUtil, namer, generators, codeModel));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(List.class), generators, 1), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, ArrayList.class, true));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(ArrayList.class), generators, 1), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, ArrayList.class, true));
        generators.add(new GenericCollectionMatcher(new ASTStringType("android.databinding.ObservableArrayList"), generators, 1), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, new ASTStringType("android.databinding.ObservableArrayList"), false));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(LinkedList.class), generators, 1), new ListReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, LinkedList.class, false));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(Map.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, HashMap.class, true, true));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(HashMap.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, HashMap.class, true, true));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(LinkedHashMap.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, LinkedHashMap.class, true, false));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(SortedMap.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, TreeMap.class, false, false));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(TreeMap.class), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, TreeMap.class, false, false));
        generators.add(new GenericCollectionMatcher(new ASTStringType("android.databinding.ObservableArrayMap"), generators, 2), new MapReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, new ASTStringType("android.databinding.ObservableArrayMap"), false, false));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(Set.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, HashSet.class, true));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(HashSet.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, HashSet.class, true));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(SortedSet.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, TreeSet.class, false));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(TreeSet.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, TreeSet.class, false));
        generators.add(new GenericCollectionMatcher(astClassFactory.getType(LinkedHashSet.class), generators, 1), new SetReadWriteGenerator(generationUtil, namer, generators, astClassFactory, codeModel, LinkedHashSet.class, false));
        generators.add(new InheritsMatcher(astClassFactory.getType(Serializable.class)), serializableReadWriteGenerator);

        return generators;
    }
}