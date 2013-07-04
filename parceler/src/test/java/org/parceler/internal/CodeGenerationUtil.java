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