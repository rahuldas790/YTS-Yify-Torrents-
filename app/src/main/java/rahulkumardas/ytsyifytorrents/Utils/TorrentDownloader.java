package rahulkumardas.ytsyifytorrents.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Rahul Kumar Das on 17-05-2017.
 */

public class TorrentDownloader {

    private Client client;
    private Context context;
    private File torrent, downDir;

    public TorrentDownloader(Context context) {
        this.context = context;
    }

    public void setClient(File torrent, File downDir) {
        this.torrent = torrent;
        this.downDir = downDir;
        new SetClient().execute();
    }

    public class SetClient extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            InetAddress address = null;
            try {
                address = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.i("TorrentDownloader", "Error : " + e.getMessage());
                Toast.makeText(context, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            try {
                // First, instantiate the Client object.
                client = new Client(
                        // This is the interface the client will listen on (you might need something
                        // else than localhost here).
                        address,

                        // Load the torrent from the torrent file and use the given
                        // output directory. Partials downloads are automatically recovered.
                        SharedTorrent.fromFile(
                                torrent, downDir));
                setDownUpRate(0.0f, 50);
                startDownload();
                trackDownLoad();
            } catch (IOException e) {
                Log.i("TorrentDownloader", "Error : " + e.getMessage());
                Toast.makeText(context, "IOException : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (NoSuchAlgorithmException e) {
                Log.i("TorrentDownloader", "Error : " + e.getMessage());
                Toast.makeText(context, "NoSuchAlgorithmException : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    private void startDownload() {
        /*
          *At this point, can you either call download() to download the torrent and
          * stop immediately after...
          */
        client.download();
    }

    public void stopDownload() {
        // At any time you can call client.stop() to interrupt the download.
        client.stop();
    }

    private void addWaitForCompletionListener() {
        /*
          *Downloading and seeding is done in background threads.
          * To wait for this process to finish, call:
          */
        client.waitForCompletion();
    }

    private void setDownUpRate(float downRate, float upRate) {
        /*
          *  You can optionally set download/upload rate limits
          *  in kB/second. Setting a limit to 0.0 disables rate
          *  limits.
          */

        client.setMaxDownloadRate(downRate);
        client.setMaxUploadRate(upRate);

    }

    private void trackerCode() {
        // First, instantiate a Tracker object with the port you want it to listen on.
        // The default tracker port recommended by the BitTorrent protocol is 6969.
        Tracker tracker = null;
        try {
            tracker = new Tracker(new InetSocketAddress(6969));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Then, for each torrent you wish to announce on this tracker, simply created
        // a TrackedTorrent object and pass it to the tracker.announce() method:
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".torrent");
            }
        };

        for (File f : torrent.listFiles(filter)) {
            try {
                tracker.announce(TrackedTorrent.load(f));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        // Once done, you just have to start the tracker's main operation loop:
        tracker.start();

        // You can stop the tracker when you're done with:
        tracker.stop();
    }

    private void trackDownLoad() {
        client.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object data) {
                Client client = (Client) observable;
                float progress = client.getTorrent().getCompletion();
                // Do something with progress.
                Log.i("Progress", progress + "");
            }
        });
    }

// Or call client.share(...) with a seed time in seconds:
// client.share(3600);
// Which would seed the torrent for an hour after the download is complete.


}
