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