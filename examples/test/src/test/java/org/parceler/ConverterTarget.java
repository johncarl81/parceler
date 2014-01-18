package org.parceler;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.*;

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
    SubParcel[] subparcela;
    String s;
    List<String> list;
    Map<String, String> map;
    SubParcel parcel;
    List<SubParcel> parcelList;
    ArrayList<List<SubParcel>> multiList;
    Map<SubParcel, SubParcel> parcelMap;
    HashMap<SubParcel, SubParcel> parcelHashMap;
    Map<List<Map<SubParcel, Integer>>, Map<List<String>, Integer>> ridiculousMap;
    Set<String> stringSet;
    HashSet<String> stringHashSet;


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

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public SubParcel getParcel() {
        return parcel;
    }

    public void setParcel(SubParcel parcel) {
        this.parcel = parcel;
    }

    public List<SubParcel> getParcelList() {
        return parcelList;
    }

    public void setParcelList(List<SubParcel> parcelList) {
        this.parcelList = parcelList;
    }

    public ArrayList<List<SubParcel>> getMultiList() {
        return multiList;
    }

    public void setMultiList(ArrayList<List<SubParcel>> multiList) {
        this.multiList = multiList;
    }

    public Map<SubParcel, SubParcel> getParcelMap() {
        return parcelMap;
    }

    public void setParcelMap(Map<SubParcel, SubParcel> parcelMap) {
        this.parcelMap = parcelMap;
    }

    public Map<List<Map<SubParcel, Integer>>, Map<List<String>, Integer>> getRidiculousMap() {
        return ridiculousMap;
    }

    public void setRidiculousMap(Map<List<Map<SubParcel, Integer>>, Map<List<String>, Integer>> ridiculousMap) {
        this.ridiculousMap = ridiculousMap;
    }

    public Set<String> getStringSet() {
        return stringSet;
    }

    public void setStringSet(Set<String> stringSet) {
        this.stringSet = stringSet;
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

    public SubParcel[] getSubparcela() {
        return subparcela;
    }

    public void setSubparcela(SubParcel[] subparcela) {
        this.subparcela = subparcela;
    }

    public HashMap<SubParcel, SubParcel> getParcelHashMap() {
        return parcelHashMap;
    }

    public void setParcelHashMap(HashMap<SubParcel, SubParcel> parcelHashMap) {
        this.parcelHashMap = parcelHashMap;
    }

    public HashSet<String> getStringHashSet() {
        return stringHashSet;
    }

    public void setStringHashSet(HashSet<String> stringHashSet) {
        this.stringHashSet = stringHashSet;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
