package velociraptor.scheduling.io;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueueByteOutputStream<ArrayType,SingleType> implements ObjectListOutputStream<ArrayType,SingleType> {

    private List<ArrayType> cache;
    private int size = 0;
    private Class<?> itemType;

    public QueueByteOutputStream<ArrayType,SingleType> instanceArrayList() {
        cache = new ArrayList<>();
        return this;
    }

    public QueueByteOutputStream<ArrayType,SingleType> instanceCollection(List<ArrayType> list) {
        cache = list;
        return this;
    }

    @Override
    public void write(ArrayType b) {
        if (b == null)
            return;
        if (itemType == null)
            itemType = b.getClass().getComponentType();
        cache.add(b);
        size += Array.getLength(b);
    }


    public QueueByteFrame find(int form, int len) {
        int to = Math.min(form + len, size);

        QueueByteFrame frame = new QueueByteFrame();

        frame.form = form;
        frame.to = to;

        Iterator<ArrayType> iterator = cache.iterator();
        int i0 = 0, i1 = 0, bl, i = 0;
        for (; iterator.hasNext(); i0 = i1, i++) {
            ArrayType bytes = iterator.next();
            bl = Array.getLength(bytes);
            i1 = i0 + bl;
            if (i0 <= form && form < i1) {
                frame.form_i0 = i0;
                frame.form_i1 = i1;
                frame.formIndex = i;
                break;
            }
        }
        if (i1 < to) {
            for (i++, i0 = i1; iterator.hasNext(); i0 = i1, i++) {
                ArrayType bytes = iterator.next();
                bl = Array.getLength(bytes);
                i1 = i0 + bl;
                if (to <= i1) {
                    frame.to_i0 = i0;
                    frame.to_i1 = i1;
                    frame.toIndex = i;
                    break;
                }
            }
        }
        return frame;
    }

    public int indexOf(int form, SingleType single) {
        if (cache.isEmpty() ) return -1;
        int ci = 0, cl = cache.size();
        int start = form;
        int of = 0;
        ArrayType st = null;
        for (int length; ci < cl; ) {
            ArrayType ay = cache.get(ci);
            length = Array.getLength(ay);
            if (start < length) {
                st = ay;
                break;
            }
            of = of + length;
            start = start - length;
            ci++;
        }
        if (st == null) {
            return -1;
        }
        a:for (int i = start, length; ci < cl; ) {
            st = cache.get(ci);
            for (length = Array.getLength(st); i < length; i++) {
                Object o = Array.get(st, i);
                if (o.equals(single)) {
                    of = of + i;
                    st = null;
                    break a;
                }
            }
            i = 0;
            of = of + length;
            ci++;
        }
        if (st != null) {
            of = -1;
        }
        return of;
    }

    public ArrayType get(QueueByteFrame qf) {
        if (qf.formIndex == -1)
            return null;
        ObjectArrayOutputStream<ArrayType> op = new ObjectArrayOutputStream<>(qf.to - qf.form, itemType);
        if (qf.toIndex == -1) {
            op.write(cache.get(qf.formIndex), qf.form - qf.form_i0, qf.to - qf.form);
        } else {
            int i = qf.formIndex;
            int e = qf.toIndex;
            op.write(cache.get(i++), qf.form - qf.form_i0, qf.form_i1 - qf.form);
            for (ArrayType bs; i < e; i++) {
                bs = cache.get(i);
                op.write(bs, 0, Array.getLength(bs));
            }
            op.write(cache.get(e), 0, qf.to - qf.to_i0);
        }

        return op.getBuf();
    }

    @SuppressWarnings({"SuspiciousSystemArraycopy", "unchecked"})
    public void del(QueueByteFrame bf) {
        if (bf.formIndex == -1)
            return;

        int index = bf.formIndex;
        int limit = bf.toIndex;

        int start = bf.form - bf.form_i0;
        int end = limit == -1 ? bf.to - bf.form : Integer.MIN_VALUE;

        if (end == 0) {
            return;
        }

        ArrayType bs = cache.get(index);
        ArrayType b1 = null, b2 = null, b3 = null;
        if (0 < start) {
            b1 = (ArrayType) Array.newInstance(itemType, start);
            System.arraycopy(bs, 0, b1, 0, start);
        }
        if (0 < end && start + end < Array.getLength(bs)) {
            int len = Array.getLength(bs) - end;
            b2 = (ArrayType) Array.newInstance(itemType, len);
            System.arraycopy(bs, end, b2, 0, len);
        }

        if (limit != -1) {
            bs = cache.get(bf.toIndex);
            start = bf.to - bf.to_i0;
            end = Array.getLength(bs);
            if (start < end) {
                int len = end - start;
                b3 = (ArrayType) Array.newInstance(itemType, len);
                System.arraycopy(bs, start, b3, 0, len);
            }
        }

        limit = limit < 0 ? index : limit;
        for (int i = index; i <= limit; i++) {
            bs = cache.remove(index);
            size = size - Array.getLength(bs);
        }

        if (b1 != null) {
            cache.add(index++, b1);
            size += Array.getLength(b1);
        }
        if (b2 != null) {
            cache.add(index++, b2);
            size += Array.getLength(b2);
        }
        if (b3 != null) {
            cache.add(index, b3);
            size += Array.getLength(b3);
        }
    }

    public int size() {
        return size;
    }

}