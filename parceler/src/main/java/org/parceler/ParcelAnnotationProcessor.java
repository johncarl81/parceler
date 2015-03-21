/**
 * Copyright 2013-2015 John Ericksen
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
package org.parceler;

import com.google.auto.service.AutoService;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.androidtransfuse.AnnotationProcessorBase;
import org.androidtransfuse.SupportedAnnotations;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.element.ReloadableASTElementFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.bootstrap.Bootstraps;
import org.androidtransfuse.scope.ScopeKey;
import org.parceler.internal.ParcelProcessor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Set;

/**
 * Annotation processor which generates for classes annotated with @Parcel, Android Parcelable wrapper classes.
 * <p/>
 * In addition this processor will generate the org.androidtransfuse.Parcels class.  This utility defines a mapping
 * of annotated @Parcel class with the Parcelable wrapper and allows for easy wrapping of any processed @Parcel.
 *
 * @author John Ericksen
 */
@SupportedAnnotations({Parcel.class, ParcelClass.class, ParcelClasses.class})
@Bootstrap
@AutoService(Processor.class)
public class ParcelAnnotationProcessor extends AnnotationProcessorBase {

    @Inject
    private ParcelProcessor parcelProcessor;
    @Inject
    private ReloadableASTElementFactory reloadableASTElementFactory;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        Bootstraps.getInjector(ParcelAnnotationProcessor.class)
                .add(Singleton.class, ScopeKey.of(ProcessingEnvironment.class), processingEnv)
                .inject(this);
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {

        parcelProcessor.submit(ParcelClass.class, buildASTCollection(roundEnvironment, ParcelClass.class));
        parcelProcessor.submit(ParcelClasses.class, buildASTCollection(roundEnvironment, ParcelClasses.class));
        parcelProcessor.submit(Parcel.class, buildASTCollection(roundEnvironment, Parcel.class));

        parcelProcessor.execute();

        if (roundEnvironment.processingOver()) {
            // Throws an exception if errors still exist.
            parcelProcessor.checkForErrors();
        }

        return true;
    }

    private Collection<Provider<ASTType>> buildASTCollection(RoundEnvironment round, Class<? extends Annotation> annotation) {
        return reloadableASTElementFactory.buildProviders(
                FluentIterable.from(round.getElementsAnnotatedWith(annotation))
                        .filter(new Predicate<Element>() {
                            public boolean apply(Element element) {
                                //we're only dealing with TypeElements
                                return element instanceof TypeElement;
                            }
                        })
                        .transform(new Function<Element, TypeElement>() {
                            public TypeElement apply(Element element) {
                                return (TypeElement)element;
                            }
                        })
                        .toList());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
