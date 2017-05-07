package rahulkumardas.ytsyifytorrents.event_bus;

import com.squareup.otto.Bus;

/**
 * Created by Rahul Kumar Das on 06-05-2017.
 * Double checked singleton (basically we need it only in UI thread) for Otto event bus.
 */

public class EventBusCreator {

    private static Bus bus;

    public static Bus defaultEventBus() {

        if (bus == null) {
            synchronized (EventBusCreator.class) {
                if (bus == null) {
                    bus = new Bus();
                }
            }
        }
        return bus;
    }
}
