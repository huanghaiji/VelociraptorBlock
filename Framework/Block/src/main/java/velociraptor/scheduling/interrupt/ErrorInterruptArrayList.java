package velociraptor.scheduling.interrupt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ErrorInterruptArrayList implements ErrorInterrupt {

    private final List<AtomicBoolean> list = new ArrayList<>(2);

    @Override
    public void addErrorInterrupt(ErrorInterruptValue value) {
        if (value != null)
            list.add(value.getMainErrorInterruptValue());
    }

    @Override
    public boolean errorInterrupt() {
        for (AtomicBoolean atomicBoolean : list) {
            if (atomicBoolean.get())
                return true;
        }
        return false;
    }


}
