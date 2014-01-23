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
import org.androidtransfuse.util.matcher.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.parceler.Parcels;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
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
    private Parcel parcel;
    private Target target;

    @Before
    public void setup() {
        Bootstraps.inject(this);

        targetType = astClassFactory.getType(Target.class);
        parcel = Parcel.obtain();
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

    @Test
    public void tester(){
        assertTrue(Matchers.type(astClassFactory.getType(double[].class)).build().matches(astClassFactory.getType(double[].class)));
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

        Parcelable outputParcelable = parcelableClass.getConstructor(Target.class).newInstance(target);

        outputParcelable.writeToParcel(parcel, 0);

        Parcelable inputParcelable = parcelableClass.getConstructor(Parcel.class).newInstance(parcel);

        Target wrapped = Parcels.unwrap(inputParcelable);

        assertEquals(target.getValue(), wrapped.getValue());
    }
}
