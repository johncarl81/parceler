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

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * @author John Ericksen
 */
public class ErrorCheckingMessager implements Messager {

    private boolean errored = false;

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence charSequence) {
        checkError(kind);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence charSequence, Element element) {
        checkError(kind);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence charSequence, Element element, AnnotationMirror annotationMirror) {
        checkError(kind);
    }

    @Override
    public void printMessage(Diagnostic.Kind kind, CharSequence charSequence, Element element, AnnotationMirror annotationMirror, AnnotationValue annotationValue) {
        checkError(kind);
    }

    private void checkError(Diagnostic.Kind kind){
        if(Diagnostic.Kind.ERROR.equals(kind)){
            errored = true;
        }
    }

    public boolean isErrored(){
        return errored;
    }
}
