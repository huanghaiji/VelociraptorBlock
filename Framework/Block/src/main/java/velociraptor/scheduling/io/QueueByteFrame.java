package velociraptor.scheduling.io;

public class QueueByteFrame {

    int form;

    int to;

    int form_i0 = -1;

    int form_i1 = -1;

    int to_i0 = -1;

    int to_i1 = -1;

    int formIndex = -1;

    int toIndex = -1;

    public boolean isSameArray() {
        return formIndex * toIndex <= 0;
    }

}