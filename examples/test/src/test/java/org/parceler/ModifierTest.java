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
package org.parceler;

import android.os.Parcelable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.Assert.assertTrue;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class ModifierTest {

    private PodamFactory factory;

    @Before
    public void init() {
        factory = new PodamFactoryImpl();
    }

    @Test
    public void testTypes(){

        FieldModifiers target = factory.manufacturePojo(FieldModifiers.class);

        android.os.Parcel parcel = android.os.Parcel.obtain();
        Parcelable parcelable = new FieldModifiers$$Parcelable(target);
        parcelable.writeToParcel(parcel, 0);

        FieldModifiers unwrapped = Parcels.unwrap(new FieldModifiers$$Parcelable(parcel));

        assertTrue(target.equals(unwrapped));
    }

    @Test
    public void testMethods(){

        MethodModifiers target = factory.manufacturePojo(MethodModifiers.class);

        android.os.Parcel parcel = android.os.Parcel.obtain();
        Parcelable parcelable = new MethodModifiers$$Parcelable(target);
        parcelable.writeToParcel(parcel, 0);

        MethodModifiers unwrapped = Parcels.unwrap(new MethodModifiers$$Parcelable(parcel));

        assertTrue(target.equals(unwrapped));
    }

    @Test
    public void testPublicConstructor(){

        PublicConstructorModifier target = factory.manufacturePojo(PublicConstructorModifier.class);

        android.os.Parcel parcel = android.os.Parcel.obtain();
        Parcelable parcelable = new PublicConstructorModifier$$Parcelable(target);
        parcelable.writeToParcel(parcel, 0);

        PublicConstructorModifier unwrapped = Parcels.unwrap(new PublicConstructorModifier$$Parcelable(parcel));

        assertTrue(target.equals(unwrapped));
    }

    @Test
    public void testPackagePrivateConstructor(){

        PackagePrivateConstructorModifier target = factory.manufacturePojo(PackagePrivateConstructorModifier.class);

        android.os.Parcel parcel = android.os.Parcel.obtain();
        Parcelable parcelable = new PackagePrivateConstructorModifier$$Parcelable(target);
        parcelable.writeToParcel(parcel, 0);

        PackagePrivateConstructorModifier unwrapped = Parcels.unwrap(new PackagePrivateConstructorModifier$$Parcelable(parcel));

        assertTrue(target.equals(unwrapped));
    }

    @Test
    public void testProtectedConstructor(){

        ProtectedConstructorModifier target = factory.manufacturePojo(ProtectedConstructorModifier.class);

        android.os.Parcel parcel = android.os.Parcel.obtain();
        Parcelable parcelable = new ProtectedConstructorModifier$$Parcelable(target);
        parcelable.writeToParcel(parcel, 0);

        ProtectedConstructorModifier unwrapped = Parcels.unwrap(new ProtectedConstructorModifier$$Parcelable(parcel));

        assertTrue(target.equals(unwrapped));
    }

    @Test
    public void testPrivateConstructor(){

        PrivateConstructorModifier target = factory.manufacturePojo(PrivateConstructorModifier.class);

        android.os.Parcel parcel = android.os.Parcel.obtain();
        Parcelable parcelable = new PrivateConstructorModifier$$Parcelable(target);
        parcelable.writeToParcel(parcel, 0);

        PrivateConstructorModifier unwrapped = Parcels.unwrap(new PrivateConstructorModifier$$Parcelable(parcel));

        assertTrue(target.equals(unwrapped));
    }


}
