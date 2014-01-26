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

import org.androidtransfuse.adapter.ASTParameter;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.bootstrap.Bootstraps;
import org.junit.Before;
import org.junit.Test;
import org.parceler.*;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.Assert.*;

@Bootstrap
public class ParcelableAnalysisTest {

    @Inject
    private ParcelableAnalysis parcelableAnalysis;
    @Inject
    private ASTClassFactory astClassFactory;
    @Inject
    private ErrorCheckingMessager messager;
    ASTType converterAst;

    @Before
    public void setup() {
        Bootstraps.inject(this);
        converterAst = astClassFactory.getType(StringWriterConverter.class);
    }

    @Parcel
    private static class FieldSerialization {
        String value;
    }

    @Test
    public void testFieldSerialization(){

        ASTType fieldType = astClassFactory.getType(FieldSerialization.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(fieldType, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(1, analysis.getFieldPairs().size());
        assertEquals(0, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertTrue(fieldsContain(analysis, "value"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class TransientFieldSerialization {
        @Transient String value;
    }

    @Test
    public void testTransientFieldSerialization(){

        ASTType fieldType = astClassFactory.getType(TransientFieldSerialization.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(fieldType, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(0, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertFalse(fieldsContain(analysis, "value"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class ConstructorSerialization {
        String value;

        @ParcelConstructor
        public ConstructorSerialization(@ParcelProperty("value") String value){
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
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class UnnamedConstructorSerialization {
        String value;

        @ParcelConstructor
        public UnnamedConstructorSerialization(@ASTClassFactory.ASTParameterName("value") String value){
            this.value = value;
        }
    }

    @Test
    public void testUnnamedConstructorSerialization() {

        ASTType fieldType = astClassFactory.getType(UnnamedConstructorSerialization.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(fieldType, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(0, analysis.getMethodPairs().size());
        assertNotNull(analysis.getConstructorPair());
        assertEquals(1, analysis.getConstructorPair().getWriteReferences().size());
        assertFalse(messager.getMessage(), messager.isErrored());
    }


    @Parcel(Parcel.Serialization.METHOD)
    public static class Basic {
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
        assertTrue(methodsContain(analysis, "intValue"));
        assertTrue(methodsContain(analysis, "stringValue"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel(Parcel.Serialization.METHOD)
    public static class MissingSetter {
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
        assertTrue(methodsContain(analysis, "intValue"));
        assertFalse(methodsContain(analysis, "stringValue"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel(Parcel.Serialization.METHOD)
    public static class MissingGetter {
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
        assertTrue(methodsContain(analysis, "intValue"));
        assertFalse(methodsContain(analysis, "stringValue"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    public static class Converter implements ParcelConverter {
        @Override
        public void toParcel(Object input, android.os.Parcel destinationParcel) {
        }

        @Override
        public Object fromParcel(android.os.Parcel parcel) {
            return null;
        }
    }

    @Parcel(converter = Converter.class)
    public static class Target {}

    @Test
    public void testParcelConverter() {

        ASTType targetAst = astClassFactory.getType(Target.class);
        ASTType converterAst = astClassFactory.getType(Converter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(targetAst, converterAst);

        assertEquals(converterAst, analysis.getParcelConverterType());
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel(Parcel.Serialization.METHOD)
    public static class MethodTransient {
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

        ASTType basicAst = astClassFactory.getType(MethodTransient.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(basicAst, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(0, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertFalse(methodsContain(analysis, "stringValue"));
        assertFalse(methodsContain(analysis, "intValue"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class DuplicateProperty {
        @ParcelProperty("value")
        String value;
        @ParcelProperty("value")
        String value2;
    }

    @Test
    public void testDuplicatePropertyError(){
        parcelableAnalysis.analyze(astClassFactory.getType(DuplicateProperty.class), null);
        assertTrue(messager.isErrored());
    }

    @Parcel
    public static class FieldMethodProperty {
        String one;
        String two;

        @ParcelProperty("one")
        public String getSomeValue() {
            return one;
        }

        @ParcelProperty("two")
        public void setSomeValue(String two) {
            this.two = two;
        }
    }

    @Test
    public void testFieldMethodProperty() {

        ASTType fieldMethodType = astClassFactory.getType(FieldMethodProperty.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(fieldMethodType, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(1, analysis.getFieldPairs().size());
        assertEquals(1, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertTrue(fieldsContain(analysis, "one"));
        assertTrue(methodsContain(analysis, "two"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class CollidingConstructorProperty {
        @ParcelProperty("value")
        String value;

        @ParcelConstructor
        public CollidingConstructorProperty(@ParcelProperty("value") String value){
            this.value = value;
        }

        @ParcelProperty("value")
        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testCollidingConstructorProperty() {

        ASTType collidingType = astClassFactory.getType(CollidingConstructorProperty.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(collidingType, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(0, analysis.getMethodPairs().size());
        assertNotNull(analysis.getConstructorPair());
        assertTrue(constructorContains(analysis, "value"));
        assertFalse(fieldsContain(analysis, "value"));
        assertFalse(methodsContain(analysis, "value"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class CollidingMethodProperty {
        @ParcelProperty("value")
        String someValue;

        @ParcelProperty("value")
        public void setSomeValue(String value) {
            this.someValue = value;
        }
    }

    @Test
    public void testCollidingMethodProperty() {

        ASTType collidingType = astClassFactory.getType(CollidingMethodProperty.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(collidingType, null);

        assertNull(analysis.getParcelConverterType());
        assertEquals(0, analysis.getFieldPairs().size());
        assertEquals(1, analysis.getMethodPairs().size());
        assertNull(analysis.getConstructorPair());
        assertFalse(fieldsContain(analysis, "value"));
        assertTrue(methodsContain(analysis, "value"));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class PropertyConverterParcel{
        @ParcelProperty("value")
        @ParcelPropertyConverter(StringWriterConverter.class)
        String value;
    }

    @Test
    public void testParcelPropertyConverter() {

        ASTType targetAst = astClassFactory.getType(PropertyConverterParcel.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(targetAst, null);

        assertEquals(1, analysis.getFieldPairs().size());
        assertEquals(converterAst, analysis.getFieldPairs().get(0).getConverter());
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class MethodPropertyConverter {
        String value;

        @ParcelProperty("value")
        @ParcelPropertyConverter(StringWriterConverter.class)
        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testMethodPropertyConverter() {

        ASTType targetAst = astClassFactory.getType(MethodPropertyConverter.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(targetAst, null);

        assertEquals(1, analysis.getMethodPairs().size());
        assertEquals(converterAst, analysis.getMethodPairs().get(0).getConverter());
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class ConstructorConverterSerialization {
        String value;

        @ParcelConstructor
        public ConstructorConverterSerialization(@ParcelProperty("value") @ParcelPropertyConverter(StringWriterConverter.class) String value){
            this.value = value;
        }
    }

    @Test
    public void testConstructorConverterSerialization() {

        ASTType targetAst = astClassFactory.getType(ConstructorConverterSerialization.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(targetAst, null);

        ASTParameter parameter = analysis.getConstructorPair().getConstructor().getParameters().get(0);
        Map<ASTParameter,ASTType> converters = analysis.getConstructorPair().getConverters();

        assertEquals(1, converters.size());
        assertEquals(converterAst, converters.get(parameter));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class UnnamedConstructorConverterSerialization {
        String value;

        @ParcelConstructor
        public UnnamedConstructorConverterSerialization(@ParcelPropertyConverter(StringWriterConverter.class) @ASTClassFactory.ASTParameterName("value") String value){
            this.value = value;
        }
    }

    @Test
    public void testUnnamedConstructorConverterSerialization() {

        ASTType targetAst = astClassFactory.getType(UnnamedConstructorConverterSerialization.class);
        ParcelableDescriptor analysis = parcelableAnalysis.analyze(targetAst, null);

        ASTParameter parameter = analysis.getConstructorPair().getConstructor().getParameters().get(0);
        Map<ASTParameter,ASTType> converters = analysis.getConstructorPair().getConverters();

        assertEquals(1, converters.size());
        assertEquals(converterAst, converters.get(parameter));
        assertFalse(messager.getMessage(), messager.isErrored());
    }

    @Parcel
    public static class CollidingConstructorParameterConverterSerialization {
        @ParcelPropertyConverter(StringWriterConverter.class)
        String value;

        @ParcelConstructor
        public CollidingConstructorParameterConverterSerialization(@ParcelPropertyConverter(StringWriterConverter.class) String value){
            this.value = value;
        }
    }

    @Test
    public void testCollidingConverterSerialization() {

        ASTType targetAst = astClassFactory.getType(CollidingConstructorParameterConverterSerialization.class);
        parcelableAnalysis.analyze(targetAst, null);
        assertTrue(messager.isErrored());
    }

    @Parcel
    public static class CollidingMethodParameterConverterSerialization {
        @ParcelProperty("value")
        @ParcelPropertyConverter(StringWriterConverter.class)
        String value;

        @ParcelProperty("value")
        @ParcelPropertyConverter(StringWriterConverter.class)
        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test
    public void testCollidingMethodParameterConverterSerialization() {

        ASTType targetAst = astClassFactory.getType(CollidingMethodParameterConverterSerialization.class);
        parcelableAnalysis.analyze(targetAst, null);
       assertTrue(messager.isErrored());
    }

    @Parcel
    public static class CollidingMethodConverterSerialization {
        String value;

        @ParcelProperty("value")
        @ParcelPropertyConverter(StringWriterConverter.class)
        public void setValue(String value) {
            this.value = value;
        }

        @ParcelProperty("value")
        @ParcelPropertyConverter(StringWriterConverter.class)
        public String getValue() {
            return value;
        }
    }

    @Test
    public void testCollidingMethodConverterSerialization() {

        ASTType targetAst = astClassFactory.getType(CollidingMethodConverterSerialization.class);
        parcelableAnalysis.analyze(targetAst, null);
        assertTrue(messager.isErrored());
    }

    private boolean constructorContains(ParcelableDescriptor descriptor, String name) {

        if(descriptor.getConstructorPair() != null) {
            for (AccessibleReference accessibleReference : descriptor.getConstructorPair().getWriteReferences().values()) {
                if(accessibleReference.getName().equals(name)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean methodsContain(ParcelableDescriptor descriptor, String name) {
        for (ReferencePair<MethodReference> getterSetterPair : descriptor.getMethodPairs()) {
            if (getterSetterPair.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean fieldsContain(ParcelableDescriptor descriptor, String name) {
        for (ReferencePair<FieldReference> getterSetterPair : descriptor.getFieldPairs()) {
            if (getterSetterPair.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}