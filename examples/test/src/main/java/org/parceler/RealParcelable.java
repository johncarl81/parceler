/**
 * This product currently only contains code developed by authors
 * of specific components, as identified by the source code files.
 *
 * Since product implements StAX API, it has dependencies to StAX API
 * classes.
 *
 * For additional credits (generally to people who reported problems)
 * see CREDITS file.
 */
package org.parceler;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author John Ericksen
 */
public class RealParcelable implements Parcelable {

    private String value;

    public RealParcelable(String value) {
        this.value = value;
    }

    public RealParcelable(Parcel parcel){
        this(parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
    }

    public static final Creator<RealParcelable> CREATOR = new Creator<RealParcelable>() {
        @Override
        public RealParcelable createFromParcel(Parcel in) {
            return new RealParcelable(in);
        }

        @Override
        public RealParcelable[] newArray(int size) {
            return new RealParcelable[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealParcelable)) return false;

        RealParcelable that = (RealParcelable) o;

        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
