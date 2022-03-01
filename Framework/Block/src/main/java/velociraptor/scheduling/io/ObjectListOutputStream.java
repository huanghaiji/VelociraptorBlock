package velociraptor.scheduling.io;

import java.io.IOException;
import java.util.List;

public interface ObjectListOutputStream<ArrayType,SingleType> {

//  constructor or write(ArrayType b) call setItemType(Class cls)

    ObjectListOutputStream<ArrayType,SingleType> instanceArrayList();

    @Deprecated
    ObjectListOutputStream<ArrayType,SingleType> instanceCollection(List<ArrayType> list);

    void write(ArrayType b) throws IOException;

    QueueByteFrame find(int form, int len);

    int indexOf(int form, SingleType single);

    ArrayType get(QueueByteFrame qf);

    void del(QueueByteFrame bf);

    int size();

}