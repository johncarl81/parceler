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
package org.parceler.internal;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;

/**
 * Empty filer for testing placeholder purposes.
 *
 * @author John Ericksen
 */
public class NoOpFiler implements Filer {
    @Override
    public JavaFileObject createSourceFile(CharSequence charSequence, Element... elements) throws IOException {
        return null;
    }

    @Override
    public JavaFileObject createClassFile(CharSequence charSequence, Element... elements) throws IOException {
        return null;
    }

    @Override
    public FileObject createResource(JavaFileManager.Location location, CharSequence charSequence, CharSequence charSequence1, Element... elements) throws IOException {
        return null;
    }

    @Override
    public FileObject getResource(JavaFileManager.Location location, CharSequence charSequence, CharSequence charSequence1) throws IOException {
        return null;
    }
}
