package rahulkumardas.ytsyifytorrents.dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.net.URLEncoder;

import rahulkumardas.ytsyifytorrents.R;
import rahulkumardas.ytsyifytorrents.models.Torrent;

/**
 * Created by Rahul Kumar Das on 17-05-2017.
 */

public class QualityFragment extends Fragment implements View.OnClickListener {

    private final String TAG = "QUalityFragment";
    private Torrent torrent;
    private int pos;
    private TextView quality, size, seeds, peers, language;
    private String magnetData = "magnet:?xt=urn:btih:";
    private String trackers = "&tr=udp://open.demonii.com:1337/announce" +
            "&tr=udp://tracker.openbittorrent.com:80" +
            "&tr=udp://tracker.coppersurfer.tk:6969" +
            "&tr=udp://glotorrents.pw:6969/announce" +
            "&tr=udp://tracker.opentrackr.org:1337/announce" +
            "&tr=udp://torrent.gresille.org:80/announce" +
            "&tr=udp://p4p.arenabg.com:1337" +
            "&tr=udp://tracker.leechers-paradise.org:6969";
    private Button download, magent;

    public static QualityFragment newInstance(Torrent torrent) {
        QualityFragment f = new QualityFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("torrent", torrent);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        torrent = getArguments().getParcelable("torrent");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_quality, container, false);
        quality = (TextView) view.findViewById(R.id.quality);
        size = (TextView) view.findViewById(R.id.size);
        seeds = (TextView) view.findViewById(R.id.seeds);
        peers = (TextView) view.findViewById(R.id.peers);
        language = (TextView) view.findViewById(R.id.language);
        download = (Button) view.findViewById(R.id.download);
        download.setOnClickListener(this);
        magent = (Button) view.findViewById(R.id.magnet);
        magent.setOnClickListener(this);


//        Spannable buttonLabel = new SpannableString("      Download");
//        buttonLabel.setSpan(new ImageSpan(getActivity(), R.drawable.ic_download,
//                ImageSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        Spannable buttonLabel2 = new SpannableString("      Magnet");
//        buttonLabel2.setSpan(new ImageSpan(getActivity(), R.drawable.ic_magnet,
//                ImageSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

//        download.setText(buttonLabel);
//        magent.setText(buttonLabel2);

        quality.setText(torrent.quality);
        size.setText(torrent.size);
        seeds.setText(torrent.seeds + "");
        peers.setText(torrent.peers + "");
        language.setText(DownloadDialog.language);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.download:
                DownloadProgressDialog.newInstance(torrent, DownloadDialog.movieName, "torrent").show(getFragmentManager(), "");
                break;
            case R.id.magnet:
                magnetData = magnetData + torrent.hash + "&dn=" + URLEncoder.encode(DownloadDialog.movieName) + trackers;
                Log.i(TAG, magnetData);
                startApps(magnetData);
                break;
        }
    }

    private void startApps(String magnet) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(magnet));
        startActivity(intent);
    }
}
