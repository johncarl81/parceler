package org.demo;

import org.parceler.Parcel;

/**
 * Intentionally in a different package to make sure we don't accidentally match it with org.parceler Proguard matchers.
 */
@Parcel
public class ExampleParcel {
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
