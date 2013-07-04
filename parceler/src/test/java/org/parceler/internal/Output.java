package org.parceler.internal;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.net.URI;

class Output extends SimpleJavaFileObject {
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    Output(String name, Kind kind) {
        super(URI.create("memo:///" + name.replace('.', '/') + kind.extension), kind);
    }

    byte[] toByteArray() {
        return this.outputStream.toByteArray();
    }

    @Override
    public ByteArrayOutputStream openOutputStream() {
        return this.outputStream;
    }
}