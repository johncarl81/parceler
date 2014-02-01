package org.parceler.internal;

import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.gen.ClassGenerationStrategy;
import org.parceler.Generated;
import org.parceler.ParcelAnnotationProcessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author John Ericksen
 */
public class ParcelerClassGenerationStrategy implements ClassGenerationStrategy {

    // ISO 8601 standard date format
    private static final DateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");

    @Override
    public void annotateGenerated(JDefinedClass definedClass) {
        definedClass.annotate(Generated.class)
                .param("value", ParcelAnnotationProcessor.class.getName())
                .param("date", ISO_8601.format(new Date()));
    }
}
