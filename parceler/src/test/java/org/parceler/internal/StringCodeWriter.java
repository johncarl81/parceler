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

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;
import org.androidtransfuse.adapter.PackageClass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author John Ericksen
 */
public class StringCodeWriter extends CodeWriter {

    private Map<PackageClass, ByteArrayOutputStream> streams = new HashMap<PackageClass, ByteArrayOutputStream>();

    @Override
    public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
        PackageClass packageFileName = new PackageClass(pkg.name(), fileName);
        if (!streams.containsKey(packageFileName)) {
            streams.put(packageFileName, new ByteArrayOutputStream());
        }

        return streams.get(packageFileName);
    }

    @Override
    public void close() throws IOException {
        for (OutputStream outputStream : streams.values()) {
            outputStream.flush();
            outputStream.close();
        }
    }

    public String getValue(PackageClass packageFileName) {
        return new String(streams.get(packageFileName).toByteArray());
    }

    public Map<String, String> getOutput() {
        Map<String, String> outputMap = new HashMap<String, String>();

        for (Map.Entry<PackageClass, ByteArrayOutputStream> byteStreamEntry : streams.entrySet()) {
            outputMap.put(byteStreamEntry.getKey()
                    .getCanonicalName(),
                    new String(byteStreamEntry.getValue().toByteArray()));
        }

        return outputMap;
    }
}