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
        }
    }
    
}
