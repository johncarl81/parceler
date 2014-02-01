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
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.element.ASTElementFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Collection;

import static com.google.common.collect.Collections2.transform;

/**
 * @author John Ericksen
 */
public class ReloadableASTElementFactory implements Function<Element, Provider<ASTType>> {

    private final ASTElementFactory astElementFactory;
    private final Elements elements;

    @Inject
    public ReloadableASTElementFactory(ASTElementFactory astElementFactory, Elements elements) {
        this.astElementFactory = astElementFactory;
        this.elements = elements;
    }

    public Collection<Provider<ASTType>> buildProviders(Collection<? extends Element> elementCollection) {
        return transform(elementCollection, this);
    }

    @Override
    public Provider<ASTType> apply(Element input) {
        return new ReloadableASTTypeProvider(input.asType().toString());
    }

    private final class ReloadableASTTypeProvider implements Provider<ASTType> {

        private String elementName;

        private ReloadableASTTypeProvider(String elementName) {
            this.elementName = elementName;
        }

        @Override
        public ASTType get() {
            TypeElement typeElement = elements.getTypeElement(elementName);

            return astElementFactory.getType(typeElement);
        }

        @Override
        public String toString() {
            return elementName;
        }
    }
}
