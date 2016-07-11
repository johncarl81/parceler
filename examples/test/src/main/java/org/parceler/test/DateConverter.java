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
package org.parceler.test;

import org.parceler.Parcel;
import org.parceler.ParcelClass;
import org.parceler.ParcelConverter;

import java.util.Date;

@ParcelClass(value = Date.class, annotation = @Parcel(converter = DateConverter.class))
public class DateConverter implements ParcelConverter<Date> {
    @Override
    public void toParcel(Date input, android.os.Parcel parcel) {
        parcel.writeLong(input.getTime());
    }

    @Override
    public Date fromParcel(android.os.Parcel parcel) {
        return new Date(parcel.readLong());
    }
}