package org.parceler.internal;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.collect.ImmutableSet;
import com.sun.codemodel.JDefinedClass;
import org.androidtransfuse.adapter.ASTConstructor;
import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTParameter;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.androidtransfuse.bootstrap.Bootstraps;
import org.junit.Before;
import org.junit.Test;
import org.parceler.Parcels;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author John Ericksen
 */
@Bootstrap
public class ParcelableGeneratorTest {

    private static final String TEST_VALUE = "test";

    @Inject
    private ParcelableGenerator generator;
    @Inject
    private ASTClassFactory astClassFactory;
    @Inject
    private CodeGenerationUtil codeGenerationUtil;

    private ASTType targetType;
    private Parcel mockParcel;
    private Target target;

    @Before
    public void setup() {
        Bootstraps.inject(this);

        targetType = astClassFactory.getType(Target.class);
        mockParcel = mock(Parcel.class);
        target = new Target(TEST_VALUE);
    }

    @Test
    public void testFieldSerialization() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();

        descriptor.getFieldPairs().add(
                new ReferencePair<FieldReference>("value",
                        new FieldReference("value", targetType.getFields().iterator().next()),
                        new FieldReference("value", targetType.getFields().iterator().next())));

        testSerialization(descriptor);
    }

    @Test
    public void testMethodSerialization() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();

        ASTMethod getter = getMethod("getValue", targetType.getMethods());
        ASTMethod setter = getMethod("setValue", targetType.getMethods());
        ASTType stringType = astClassFactory.getType(String.class);

        descriptor.getMethodPairs().add(
                new ReferencePair<MethodReference>("value",
                        new MethodReference("value", stringType, setter),
                        new MethodReference("value", stringType, getter)));

        testSerialization(descriptor);
    }

    @Test
    public void testConstructorFieldSerialization() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();

        ASTType stringType = astClassFactory.getType(String.class);

        ASTConstructor constructor = getConstructor(targetType.getConstructors(), stringType);

        ConstructorReference constructorReference = new ConstructorReference(constructor);

        constructorReference.getWriteReferences().put(constructor.getParameters().get(0),
                new FieldReference("value", targetType.getFields().iterator().next()));

        descriptor.setConstructorPair(constructorReference);

        testSerialization(descriptor);
    }

    @Test
    public void testConstructorMethodSerialization() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ParcelableDescriptor descriptor = new ParcelableDescriptor();

        ASTMethod getter = getMethod("getValue", targetType.getMethods());
        ASTType stringType = astClassFactory.getType(String.class);

        ASTConstructor constructor = getConstructor(targetType.getConstructors(), stringType);

        ConstructorReference constructorReference = new ConstructorReference(constructor);

        constructorReference.getWriteReferences().put(constructor.getParameters().get(0),
                new MethodReference("value", stringType, getter));

        descriptor.setConstructorPair(constructorReference);

        testSerialization(descriptor);
    }

    private ASTConstructor getConstructor(ImmutableSet<ASTConstructor> constructors, ASTType... parameters) {
        for (ASTConstructor constructor : constructors) {
            if(matchParameters(constructor.getParameters(), parameters)){
                return constructor;
            }
        }
        return null;
    }

    private boolean matchParameters(List<ASTParameter> parameters, ASTType[] matchPameters) {
        if(matchPameters == null){
            return parameters.size() == 0;
        }
        if(matchPameters.length != parameters.size()){
            return false;
        }
        for(int i = 0; i < parameters.size(); i++){
            if(!parameters.get(i).getASTType().equals(matchPameters[i])){
                return false;
            }
        }
        return true;
    }

    private ASTMethod getMethod(String name, ImmutableSet<ASTMethod> methods) {
        for (ASTMethod method : methods) {
            if(method.getName().equals(name)){
                return method;
            }
        }
        return null;
    }

    private void testSerialization(ParcelableDescriptor descriptor) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        JDefinedClass targetGenerated = generator.generateParcelable(targetType, descriptor);

        ClassLoader classLoader = codeGenerationUtil.build();
        Class<Parcelable> parcelableClass = (Class<Parcelable>) classLoader.loadClass(targetGenerated.fullName());

        when(mockParcel.readString()).thenReturn(TEST_VALUE);

        Parcelable outputParcelable = parcelableClass.getConstructor(Target.class).newInstance(target);

        outputParcelable.writeToParcel(mockParcel, 0);

        Parcelable inputParcelable = parcelableClass.getConstructor(Parcel.class).newInstance(mockParcel);

        Target wrapped = Parcels.unwrap(inputParcelable);

        assertEquals(target.getValue(), wrapped.getValue());

        verify(mockParcel).writeString(TEST_VALUE);
    }


}
