package org.parceler;

import java.util.List;
import java.util.Map;

/**
 * @author John Ericksen
 */
@Parcel
public class ConverterTarget {

    byte b;
    Byte bobj;
    double d;
    Double dobj;
    float f;
    Float fobj;
    int i;
    Integer iobj;
    long l;
    Long lobj;
    byte[] bya;
    char[] ca;
    boolean[] ba;
    int[] ia;
    long[] la;
    float[] fa;
    double[] da;
    String[] sa;
    String s;
    List<String> list;
    Map<String, String> map;
    List<SubParcel> parcelList;

    public ConverterTarget(){}

    public ConverterTarget(byte b, Byte bobj, double d, Double dobj, float f, Float fobj, int i, Integer iobj, long l, Long lobj, byte[] bya, char[] ca, boolean[] ba, int[] ia, long[] la, float[] fa, double[] da, String[] sa, String s, List<String> list, Map<String, String> map, List<SubParcel> parcelList) {
        this.b = b;
        this.bobj = bobj;
        this.d = d;
        this.dobj = dobj;
        this.f = f;
        this.fobj = fobj;
        this.i = i;
        this.iobj = iobj;
        this.l = l;
        this.lobj = lobj;
        this.bya = bya;
        this.ca = ca;
        this.ba = ba;
        this.ia = ia;
        this.la = la;
        this.fa = fa;
        this.da = da;
        this.sa = sa;
        this.s = s;
        this.list = list;
        this.map = map;
        this.parcelList = parcelList;
    }

    public byte getB() {
        return b;
    }

    public Byte getBobj() {
        return bobj;
    }

    public double getD() {
        return d;
    }

    public Double getDobj() {
        return dobj;
    }

    public float getF() {
        return f;
    }

    public Float getFobj() {
        return fobj;
    }

    public int getI() {
        return i;
    }

    public Integer getIobj() {
        return iobj;
    }

    public long getL() {
        return l;
    }

    public Long getLobj() {
        return lobj;
    }

    public byte[] getBya() {
        return bya;
    }

    public char[] getCa() {
        return ca;
    }

    public boolean[] getBa() {
        return ba;
    }

    public int[] getIa() {
        return ia;
    }

    public void setIa(int[] ia) {
        this.ia = ia;
    }

    public long[] getLa() {
        return la;
    }

    public float[] getFa() {
        return fa;
    }

    public double[] getDa() {
        return da;
    }

    public String[] getSa() {
        return sa;
    }

    public String getS() {
        return s;
    }

    public List getList() {
        return list;
    }

    public Map getMap() {
        return map;
    }

    public List<SubParcel> getParcelList(){
        return parcelList;
    }
}
