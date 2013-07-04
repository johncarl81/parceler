package org.parceler.internal;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

class Source extends SimpleJavaFileObject {
    private final String content;

    Source(String name, Kind kind, String content) {
        super(URI.create("memo:///" + name.replace('.', '/') + kind.extension), kind);
        this.content = content;
    }

    @Override
    public CharSequence getCharContent(boolean ignore) {
        return this.content;
    }
}