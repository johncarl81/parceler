/**
 * Copyright 2011-2015 John Ericksen
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

import android.util.SparseArray;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import uk.co.jemos.podam.common.PodamCollection;
import uk.co.jemos.podam.common.PodamStrategyValue;

import java.util.*;

/**
 * @author John Ericksen
 */
@Parcel
public class ConverterTarget {

    @Transient
    private static final String[] FIELDS_EXCLUDED = {"stringHashSetArray", "sparseArray"};

    byte b;
    Byte bobj;
    byte[] bya;
    Byte[] bbja;
    double d;
    Double dobj;
    double[] da;
    Double[] dobja;
    float f;
    Float fobj;
    float[] fa;
    Float[] fobja;
    int i;
    Integer iobj;
    int[] ia;
    Integer[] iobja;
    long l;
    Long lobj;
    long[] la;
    Long[] lobja;
    char c;
    Character cobj;
    char[] ca;
    Character[] cobja;
    boolean bo;
    Boolean boobj;
    boolean[] ba;
    Boolean[] bobja;
    String s;
    String[] sa;
    @PodamCollection(collectionElementStrategy = SubParcelStrategy.class)
    SubParcel[] subparcela;
    List<String> list;
    Map<String, String> map;
    @PodamStrategyValue(SubParcelStrategy.class)
    SubParcel parcel;
    @PodamCollection(collectionElementStrategy = SubParcelStrategy.class)
    List<SubParcel> parcelList;
    ArrayList<List<String>> multiList;
    @PodamCollection(mapKeyStrategy = SubParcelStrategy.class, mapElementStrategy = SubParcelStrategy.class)
    Map<SubParcel, SubParcel> parcelMap;
    @PodamCollection(mapKeyStrategy = SubParcelStrategy.class, mapElementStrategy = SubParcelStrategy.class)
    HashMap<SubParcel, SubParcel> parcelHashMap;
    Map<List<Map<String, Integer>>, Map<List<String>, Integer>> ridiculousMap;
    Set<String> stringSet;
    HashSet<String> stringHashSet;
    @PodamCollection(collectionElementStrategy = StringArrayStrategy.class)
    HashSet<String[]> stringHashSetArray;
    Integer[][] multidimensionalArray;
    SparseArray<String> sparseArray;

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

    public double[] getDa() {
        return da;
    }

    public void setDa(double[] da) {
        this.da = da;
    }

    public Double[] getDobja() {
        return dobja;
    }

    public void setDobja(Double[] dobja) {
        this.dobja = dobja;
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

    public float[] getFa() {
        return fa;
    }

    public void setFa(float[] fa) {
        this.fa = fa;
    }

    public Float[] getFobja() {
        return fobja;
    }

    public void setFobja(Float[] fobja) {
        this.fobja = fobja;
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

    public int[] getIa() {
        return ia;
    }

    public void setIa(int[] ia) {
        this.ia = ia;
    }

    public Integer[] getIobja() {
        return iobja;
    }

    public void setIobja(Integer[] iobja) {
        this.iobja = iobja;
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

    public long[] getLa() {
        return la;
    }

    public void setLa(long[] la) {
        this.la = la;
    }

    public Long[] getLobja() {
        return lobja;
    }

    public void setLobja(Long[] lobja) {
        this.lobja = lobja;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
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

    public ArrayList<List<String>> getMultiList() {
        return multiList;
    }

    public void setMultiList(ArrayList<List<String>> multiList) {
        this.multiList = multiList;
    }

    public Map<SubParcel, SubParcel> getParcelMap() {
        return parcelMap;
    }

    public void setParcelMap(Map<SubParcel, SubParcel> parcelMap) {
        this.parcelMap = parcelMap;
    }

    public HashMap<SubParcel, SubParcel> getParcelHashMap() {
        return parcelHashMap;
    }

    public void setParcelHashMap(HashMap<SubParcel, SubParcel> parcelHashMap) {
        this.parcelHashMap = parcelHashMap;
    }

    public Map<List<Map<String, Integer>>, Map<List<String>, Integer>> getRidiculousMap() {
        return ridiculousMap;
    }

    public void setRidiculousMap(Map<List<Map<String, Integer>>, Map<List<String>, Integer>> ridiculousMap) {
        this.ridiculousMap = ridiculousMap;
    }

    public Set<String> getStringSet() {
        return stringSet;
    }

    public void setStringSet(Set<String> stringSet) {
        this.stringSet = stringSet;
    }

    public HashSet<String> getStringHashSet() {
        return stringHashSet;
    }

    public void setStringHashSet(HashSet<String> stringHashSet) {
        this.stringHashSet = stringHashSet;
    }

    public HashSet<String[]> getStringHashSetArray() {
        return stringHashSetArray;
    }

    public void setStringHashSetArray(HashSet<String[]> stringHashSetArray) {
        this.stringHashSetArray = stringHashSetArray;
    }

    public Integer[][] getMultidimensionalArray() {
        return multidimensionalArray;
    }

    public void setMultidimensionalArray(Integer[][] multidimensionalArray) {
        this.multidimensionalArray = multidimensionalArray;
    }

    public SparseArray<String> getSparseArray() {
        return sparseArray;
    }

    public void setSparseArray(SparseArray<String> sparseArray) {
        this.sparseArray = sparseArray;
    }

    public byte[] getBya() {
        return bya;
    }

    public void setBya(byte[] bya) {
        this.bya = bya;
    }

    public Byte[] getBbja() {
        return bbja;
    }

    public void setBbja(Byte[] bbja) {
        this.bbja = bbja;
    }

    public char getC() {
        return c;
    }

    public void setC(char c) {
        this.c = c;
    }

    public Character getCobj() {
        return cobj;
    }

    public void setCobj(Character cobj) {
        this.cobj = cobj;
    }

    public char[] getCa() {
        return ca;
    }

    public void setCa(char[] ca) {
        this.ca = ca;
    }

    public Character[] getCobja() {
        return cobja;
    }

    public void setCobja(Character[] cobja) {
        this.cobja = cobja;
    }

    public boolean isBo() {
        return bo;
    }

    public void setBo(boolean bo) {
        this.bo = bo;
    }

    public Boolean getBoobj() {
        return boobj;
    }

    public void setBoobj(Boolean boobj) {
        this.boobj = boobj;
    }

    public boolean[] getBa() {
        return ba;
    }

    public void setBa(boolean[] ba) {
        this.ba = ba;
    }

    public Boolean[] getBobja() {
        return bobja;
    }

    public void setBobja(Boolean[] bobja) {
        this.bobja = bobja;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that, FIELDS_EXCLUDED);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, FIELDS_EXCLUDED);
    }
}
