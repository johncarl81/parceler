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
        return new ReloadableASTTypeProvider(input);
    }

    private final class ReloadableASTTypeProvider implements Provider<ASTType> {

        private Element element;

        private ReloadableASTTypeProvider(Element element) {
            this.element = element;
        }

        @Override
        public ASTType get() {
            TypeElement typeElement = elements.getTypeElement(element.asType().toString());

            return astElementFactory.getType(typeElement);
        }

        @Override
        public String toString() {
            return element.toString();
        }
    }
}
