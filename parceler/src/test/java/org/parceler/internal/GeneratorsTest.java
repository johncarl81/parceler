package org.parceler.internal;

import android.os.Parcel;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.bootstrap.Bootstraps;
import org.androidtransfuse.util.matcher.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.parceler.internal.generator.ReadWriteGenerator;
import org.parceler.internal.generator.ReadWriteGeneratorBase;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author John Ericksen
 */
@Bootstrap
public class GeneratorsTest {
    
    @Inject
    private Generators generators;
    
    @Before
    public void setup(){
        Bootstraps.inject(this);
    }

    @Test
    public void testParcelMethodUsage() throws NoSuchMethodException {
        for (Map.Entry<Matcher<ASTType>, ReadWriteGenerator> entry : ((Generators)generators).getGenerators().entrySet()) {

            if(entry.getValue() instanceof ReadWriteGeneratorBase){
                ReadWriteGeneratorBase readWriteGeneratorBase = (ReadWriteGeneratorBase)entry.getValue();
                Method readMethod = Parcel.class.getMethod(readWriteGeneratorBase.getReadMethod(), readWriteGeneratorBase.getReadMethodParams());
                assertNotNull(readMethod);
                Method writeMethod = Parcel.class.getMethod(readWriteGeneratorBase.getWriteMethod(), readWriteGeneratorBase.getWriteMethodParams());
                assertNotNull(writeMethod);
            }
            else{
                assertTrue("Found ReadWriteGenerator that wasn't covered by test: "+ entry.getValue().getClass(), false);
            }
        }
    }
    
}
