package velociraptor.scheduling.interrupt;

import java.util.concurrent.atomic.AtomicBoolean;

public interface ErrorInterruptValue {

    AtomicBoolean getMainErrorInterruptValue();

}