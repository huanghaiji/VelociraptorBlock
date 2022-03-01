package velociraptor.scheduling.io;

import java.lang.reflect.Array;

@SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy", "SuspiciousSystemArraycopy"})
class ObjectArrayOutputStream<ArrayType> {

    private ArrayType buf;
    private int count;

    ObjectArrayOutputStream(int size, Class<?> itemType) {
        if (size < 0)
            throw new IllegalArgumentException("Negative initial size: " + size);
        buf = (ArrayType) Array.newInstance(itemType, size);
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - Array.getLength(buf) > 0)
            grow(minCapacity);
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = Array.getLength(buf);
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        ArrayType des = (ArrayType) Array.newInstance(buf.getClass().getComponentType(), newCapacity);
        System.arraycopy(buf, 0, des, 0, Array.getLength(buf));
        buf = des;
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    public void write(ArrayType chars, int off, int len) {
        final int length = Array.getLength(chars);
        if ((off < 0) || (off > length) || (len < 0) || ((off + len) - length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        System.arraycopy(chars, off, buf, count, len);
        count += len;
    }


    ArrayType getBuf() {
        return buf;
    }

    public synchronized int size() {
        return count;
    }


}
