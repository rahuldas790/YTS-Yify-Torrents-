package rahulkumardas.ytsyifytorrents.event_bus;

/**
 * Created by Rahul Kumar Das on 06-05-2017.
 */

/**
 * Created by danylo.volokh on 3/15/16.
 *
 * This message is sent from {@link rahulkumardas.ytsyifytorrents.MovieDetailsActivity}
 * to {@link rahulkumardas.ytsyifytorrents.MainActivity}
 *
 * When image transition is about to start. This message should invoke hiding of original image
 * Which transition we are imitating.
 *
 */

public class ChangeImageThumbnailVisibility {
    private final boolean visible;

    public ChangeImageThumbnailVisibility(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
