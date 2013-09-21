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
import org.parceler.*;

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

    @Parcel
    private static class FieldSerialization {
        String value;

        private String getValue() {
            return value;
        }

        private void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testFieldSerialization(){

        ASTType fieldType = astClassFactory.getType(FieldSerialization.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(fieldType, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(1, analysis.getFieldPairs().size());
        assertEquals(0, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertTrue(fieldsContain(analysis.getFieldPairs(), "value"));
    }

    @Parcel
    private static class ConstructorSerialization {
        String value;

        @ParcelConstructor
        public ConstructorSerialization(@ParcelProperty("value") String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testConstructor(){

        ASTType fieldType = astClassFactory.getType(ConstructorSerialization.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(fieldType, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(0, analysis.getMethodPairs().size());
        assertNotNull(analysis.getConstructorPair());
        assertEquals(1, analysis.getConstructorPair().getWriteReferences().size());
    }

    @Parcel(Parcel.Serialization.METHOD)
    private static class Basic {
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

    @Test
    public void testBasic() {

        ASTType basicAst = astClassFactory.getType(Basic.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(2, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertTrue(methodsContain(analysis.getMethodPairs(), "intValue"));
        assertTrue(methodsContain(analysis.getMethodPairs(), "stringValue"));
    }

    @Parcel(Parcel.Serialization.METHOD)
    private static class MissingSetter {
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

    @Test
    public void testMissingSetter() {

        ASTType basicAst = astClassFactory.getType(MissingSetter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(1, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertTrue(methodsContain(analysis.getMethodPairs(), "intValue"));
        assertFalse(methodsContain(analysis.getMethodPairs(), "stringValue"));
    }

    @Parcel(Parcel.Serialization.METHOD)
    private static class MissingGetter {
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

    @Test
    public void testMissingGetter() {

        ASTType basicAst = astClassFactory.getType(MissingGetter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(1, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertTrue(methodsContain(analysis.getMethodPairs(), "intValue"));
        assertFalse(methodsContain(analysis.getMethodPairs(), "stringValue"));
    }

    private static class Converter implements ParcelConverter {
        @Override
        public void toParcel(Object input, android.os.Parcel destinationParcel) {
        }

        @Override
        public Object fromParcel(android.os.Parcel parcel) {
            return null;
        }
    }

    @Parcel(converter = Converter.class)
    private static class Target {
    }

    @Test
    public void testParcelConverter() {

        ASTType targetAst = astClassFactory.getType(Target.class);
        ASTType converterAst = astClassFactory.getType(Converter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(targetAst, converterAst);

        assertEquals(converterAst, analysis.getParcelConverterType());
    }

    @Parcel(Parcel.Serialization.METHOD)
    private static class BasicTransient {
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

    @Test
    public void testTransient() {

        ASTType basicAst = astClassFactory.getType(BasicTransient.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(0, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertFalse(methodsContain(analysis.getMethodPairs(), "stringValue"));
        assertFalse(methodsContain(analysis.getMethodPairs(), "intValue"));
    }

    private boolean methodsContain(List<ReferencePair<MethodReference>> getterSetterPairs, String name) {
        for (ReferencePair<MethodReference> getterSetterPair : getterSetterPairs) {
            if (getterSetterPair.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean fieldsContain(List<ReferencePair<FieldReference>> fieldPairs, String name) {
        for (ReferencePair<FieldReference> getterSetterPair : fieldPairs) {
            if (getterSetterPair.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }


}