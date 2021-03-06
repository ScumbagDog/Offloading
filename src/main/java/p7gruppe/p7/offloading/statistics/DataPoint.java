package p7gruppe.p7.offloading.statistics;

public class DataPoint<T> {

    public final long timestamp;
    public final T value;

    public DataPoint(long timestamp, T value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + timestamp + ", " + value + ")";
    }

    public long getTimestamp() {
        return timestamp;
    }

    public T getValue() {
        return value;
    }
}
