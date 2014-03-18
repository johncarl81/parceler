package org.parceler;

import com.google.auto.value.AutoValue;

@AutoValue
@Parcel(Parcel.Serialization.METHOD)
public abstract class Example {

    @ParcelFactory
  public static Example create(@ParcelProperty("name") String name, @ParcelProperty("integer") int integer) {
    return new AutoValue_Example(name, integer);
  }

  public abstract String getName();
  public abstract int getInteger();
}