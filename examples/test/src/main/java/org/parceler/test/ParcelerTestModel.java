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
import org.parceler.ParcelConstructor;

import java.util.Date;

@Parcel
public class ParcelerTestModel {

    final Date firstDate;
    final Date secondDate;
    final String someString;

    @ParcelConstructor
    public ParcelerTestModel(Date firstDate, Date secondDate, String someString) {
        this.firstDate = firstDate;
        this.secondDate = secondDate;
        this.someString = someString;
    }
}