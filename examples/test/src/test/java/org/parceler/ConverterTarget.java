package org.parceler;

import android.os.Bundle;
import android.os.IBinder;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

/**
 * @author John Ericksen
 */
@Parcel
public class ConverterTarget {

    private byte b;
    private Byte bobj;
    private double d;
    private Double dobj;
    private float f;
    private Float fobj;
    private int i;
    private Integer iobj;
    private long l;
    private Long lobj;
    private byte[] bya;
    private char[] ca;
    private boolean[] ba;
    private int[] ia;
    private long[] la;
    private float[] fa;
    private double[] da;
    private String[] sa;
    private String s;

    public ConverterTarget(){}

    public ConverterTarget(byte b, Byte bobj, double d, Double dobj, float f, Float fobj, int i, Integer iobj, long l, Long lobj, byte[] bya, char[] ca, boolean[] ba, int[] ia, long[] la, float[] fa, double[] da, String[] sa, String s) {
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
    }

    public byte getB() {
        return b;
    }

    public void setB(byte b) {
        this.b = b;
    }

    public Byte getBobj() {
        return bobj;
    }

    public void setBobj(Byte bobj) {
        this.bobj = bobj;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public Double getDobj() {
        return dobj;
    }

    public void setDobj(Double dobj) {
        this.dobj = dobj;
    }

    public float getF() {
        return f;
    }

    public void setF(float f) {
        this.f = f;
    }

    public Float getFobj() {
        return fobj;
    }

    public void setFobj(Float fobj) {
        this.fobj = fobj;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public Integer getIobj() {
        return iobj;
    }

    public void setIobj(Integer iobj) {
        this.iobj = iobj;
    }

    public long getL() {
        return l;
    }

    public void setL(long l) {
        this.l = l;
    }

    public Long getLobj() {
        return lobj;
    }

    public void setLobj(Long lobj) {
        this.lobj = lobj;
    }

    public byte[] getBya() {
        return bya;
    }

    public void setBya(byte[] bya) {
        this.bya = bya;
    }

    public char[] getCa() {
        return ca;
    }

    public void setCa(char[] ca) {
        this.ca = ca;
    }

    public boolean[] getBa() {
        return ba;
    }

    public void setBa(boolean[] ba) {
        this.ba = ba;
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

    public void setLa(long[] la) {
        this.la = la;
    }

    public float[] getFa() {
        return fa;
    }

    public void setFa(float[] fa) {
        this.fa = fa;
    }

    public double[] getDa() {
        return da;
    }

    public void setDa(double[] da) {
        this.da = da;
    }

    public String[] getSa() {
        return sa;
    }

    public void setSa(String[] sa) {
        this.sa = sa;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }
}
