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

import com.sun.codemodel.JCodeModel;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class CodeGenerationUtil {

    private final JCodeModel codeModel;
    private final StringCodeWriter stringCodeWriter;
    private final MemoryClassLoader classLoader;

    @Inject
    public CodeGenerationUtil(JCodeModel codeModel, StringCodeWriter stringCodeWriter, MemoryClassLoader classLoader) {
        this.codeModel = codeModel;
        this.stringCodeWriter = stringCodeWriter;
        this.classLoader = classLoader;
    }

    public ClassLoader build() throws IOException {
        return build(false);
    }

    public ClassLoader build(boolean print) throws IOException {
        codeModel.build(stringCodeWriter);

        classLoader.add(stringCodeWriter.getOutput());

        if (print) {
            for (Map.Entry<String, String> codeEntry : stringCodeWriter.getOutput().entrySet()) {
                System.out.println("Key: " + codeEntry.getKey());
                System.out.println(codeEntry.getValue());
            }
        }

        return classLoader;
    }
}