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

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class NoOpElements implements Elements {
    @Override
    public PackageElement getPackageElement(CharSequence charSequence) {
        return null;
    }

    @Override
    public TypeElement getTypeElement(CharSequence charSequence) {
        return null;
    }

    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror annotationMirror) {
        return null;
    }

    @Override
    public String getDocComment(Element element) {
        return null;
    }

    @Override
    public boolean isDeprecated(Element element) {
        return false;
    }

    @Override
    public Name getBinaryName(TypeElement typeElement) {
        return null;
    }

    @Override
    public PackageElement getPackageOf(Element element) {
        return null;
    }

    @Override
    public List<? extends Element> getAllMembers(TypeElement typeElement) {
        return null;
    }

    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element element) {
        return null;
    }

    @Override
    public boolean hides(Element element, Element element1) {
        return false;
    }

    @Override
    public boolean overrides(ExecutableElement executableElement, ExecutableElement executableElement1, TypeElement typeElement) {
        return false;
    }

    @Override
    public String getConstantExpression(Object o) {
        return null;
    }

    @Override
    public void printElements(Writer writer, Element... elements) {
    }

    @Override
    public Name getName(CharSequence charSequence) {
        return null;
    }
}
