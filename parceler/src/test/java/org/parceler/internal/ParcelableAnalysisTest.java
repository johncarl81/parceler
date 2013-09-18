/**
 * Copyright 2013 John Ericksen
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

import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.bootstrap.Bootstraps;
import org.junit.Before;
import org.junit.Test;
import org.parceler.Parcel;
import org.parceler.ParcelConverter;
import org.parceler.Transient;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.*;

@Bootstrap
public class ParcelableAnalysisTest {

    @Inject
    private ParcelableAnalysis parcelableAnalysis;
    @Inject
    private ASTClassFactory astClassFactory;

    @Before
    public void setup() {
        Bootstraps.inject(this);
    }

    @Test
    public void testBasic() {
        @Parcel(Parcel.Serialization.METHOD)
        class Basic {
            String stringValue;
            int intValue;

            public String getStringValue() {
                return stringValue;
            }

            public void setStringValue(String stringValue) {
                this.stringValue = stringValue;
            }

            public int getIntValue() {
                return intValue;
            }

            public void setIntValue(int intValue) {
                this.intValue = intValue;
            }
        }

        ASTType basicAst = astClassFactory.getType(Basic.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(2, analysis.getMethodPairs().size());
        assertTrue(contains(analysis.getMethodPairs(), "intValue"));
        assertTrue(contains(analysis.getMethodPairs(), "stringValue"));
    }

    @Test
    public void testMissingSetter() {
        @Parcel(Parcel.Serialization.METHOD)
        class MissingSetter {
            String stringValue;
            int intValue;

            public String getStringValue() {
                return stringValue;
            }

            public int getIntValue() {
                return intValue;
            }

            public void setIntValue(int intValue) {
                this.intValue = intValue;
            }
        }

        ASTType basicAst = astClassFactory.getType(MissingSetter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(1, analysis.getMethodPairs().size());
        assertTrue(contains(analysis.getMethodPairs(), "intValue"));
        assertFalse(contains(analysis.getMethodPairs(), "stringValue"));
    }

    @Test
    public void testMissingGetter() {
        @Parcel(Parcel.Serialization.METHOD)
        class MissingGetter {
            String stringValue;
            int intValue;

            public void setStringValue(String stringValue) {
                this.stringValue = stringValue;
            }

            public int getIntValue() {
                return intValue;
            }

            public void setIntValue(int intValue) {
                this.intValue = intValue;
            }
        }

        ASTType basicAst = astClassFactory.getType(MissingGetter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(1, analysis.getMethodPairs().size());
        assertTrue(contains(analysis.getMethodPairs(), "intValue"));
        assertFalse(contains(analysis.getMethodPairs(), "stringValue"));
    }

    @Test
    public void testParcelConverter() {

        class Converter implements ParcelConverter {
            @Override
            public void toParcel(Object input, android.os.Parcel destinationParcel) {
            }

            @Override
            public Object fromParcel(android.os.Parcel parcel) {
                return null;
            }
        }

        @Parcel(converter = Converter.class)
        class Target {
        }

        ASTType targetAst = astClassFactory.getType(Target.class);
        ASTType converterAst = astClassFactory.getType(Converter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(targetAst, converterAst);

        assertEquals(converterAst, analysis.getParcelConverterType());
    }

    @Test
    public void testTransient() {
        @Parcel(Parcel.Serialization.METHOD)
        class Basic {
            String stringValue;
            int intValue;

            @Transient
            public String getStringValue() {
                return stringValue;
            }

            public void setStringValue(String stringValue) {
                this.stringValue = stringValue;
            }

            public int getIntValue() {
                return intValue;
            }

            @Transient
            public void setIntValue(int intValue) {
                this.intValue = intValue;
            }
        }

        ASTType basicAst = astClassFactory.getType(Basic.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getMethodPairs().size());
    }

    private boolean contains(List<ReferencePair<MethodReference>> getterSetterPairs, String name) {
        for (ReferencePair<MethodReference> getterSetterPair : getterSetterPairs) {
            if (getterSetterPair.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


}