package android.arch.lifecycle;

public class MutableLiveData<T> {

    private T value;

    public T getValue() {
        return value;
    }

    public MutableLiveData() {
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void postValue(T value) {
        this.value = value;
    }
}
