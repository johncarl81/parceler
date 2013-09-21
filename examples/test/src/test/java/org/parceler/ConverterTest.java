package org.parceler;

import android.os.*;
import android.os.Parcel;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import org.junit.Test;

import java.io.FileDescriptor;

import static junit.framework.Assert.assertEquals;

/**
 * @author John Ericksen
 */
public class ConverterTest {
    
    @Test
    public void testTypes(){

        byte b = 1;
        Byte bobj = 1;
        double d = 42.24;
        Double dobj = 42.42;
        float f = 0.2424f;
        Float fobj = 20.2424f;
        int i = 5;
        Integer iobj = 6;
        long l = 7;
        Long lobj = 8L;
        byte[] bya = {0, 1, 0, 1};
        char[] ca = {'t', 'e', 's', 't'};
        boolean[] ba = {true, false};
        int[] ia = {1, 2, 3, 4};
        long[] la = {6, 7, 8};
        float[] fa = {0.1f, 1.2f};
        double[] da = {1.2, 3.4};
        String[] sa = {"one", "two"};
        String s = "test";
        //IBinder binder = new TestBinder();
        //Bundle bundle = new Bundle();
        //Object[] oa = {"obj1", "obj2"};
        //SparseArray sparseArray = new SparseArray(3);
        //sparseArray.put(3, "Test");
        //sparseArray.put(8, "Eight");
        //SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        //sparseBooleanArray.put(7, true);
        //sparseBooleanArray.put(5, false);
        //Exception exception = new Exception("test");
        
        ConverterTarget target = new ConverterTarget(b, bobj, d, dobj, f, fobj, i, iobj, l, lobj, bya, ca, ba, ia, la, fa, da, sa, s);

        Parcelable converted = Parcels.wrap(target);
        ConverterTarget unwrapped = Parcels.unwrap(converted);

        assertEquals(b, unwrapped.getB());
        assertEquals(bobj, unwrapped.getBobj());
        assertEquals(d, unwrapped.getD());
        assertEquals(dobj, unwrapped.getDobj());
        assertEquals(f, unwrapped.getF());
        assertEquals(fobj, unwrapped.getFobj());
        assertEquals(i, unwrapped.getI());
        assertEquals(iobj, unwrapped.getIobj());
        assertEquals(l, unwrapped.getL());
        assertEquals(lobj, unwrapped.getLobj());
        assertEquals(bya, unwrapped.getBya());
        assertEquals(ca, unwrapped.getCa());
        assertEquals(ba, unwrapped.getBa());
        assertEquals(ia, unwrapped.getIa());
        assertEquals(la, unwrapped.getLa());
        assertEquals(fa, unwrapped.getFa());
        assertEquals(da, unwrapped.getDa());
        assertEquals(sa, unwrapped.getSa());
        assertEquals(s, unwrapped.getS());
    }
}
