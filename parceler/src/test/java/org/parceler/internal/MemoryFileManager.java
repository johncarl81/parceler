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

import javax.tools.*;
import java.util.HashMap;
import java.util.Map;

class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    public final Map<String, Output> map = new HashMap<String, Output>();

    MemoryFileManager(JavaCompiler compiler) {
        super(compiler.getStandardFileManager(null, null, null));
    }

    @Override
    public Output getJavaFileForOutput
            (Location location, String name, JavaFileObject.Kind kind, FileObject source) {
        Output mc = new Output(name, kind);
        this.map.put(name, mc);
        return mc;
    }
}