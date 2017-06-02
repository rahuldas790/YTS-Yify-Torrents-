package rahulkumardas.ytsyifytorrents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rahulkumardas.ytsyifytorrents.databases.DownloadDatabase;

/**
 * Created by Rahul Kumar Das on 27-05-2017.
 */

public class TorrentDownloads extends Fragment {

    private ListView recyclerView;
    private List<String> torrents;
    private List<String> torrents2;
    private List<String> names;
    private DownloadDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_torrent_downloads, null);
        recyclerView = (ListView) view.findViewById(R.id.recyclerView);
        db = new DownloadDatabase(getActivity());
        torrents = db.allTorrents();
        names = new ArrayList<>();
        torrents2 = new ArrayList<>();
        for (int i = 0; i < torrents.size(); i++) {
            File file = new File(torrents.get(i));
            if (file.exists()) {
                names.add(file.getName());
                torrents2.add(torrents.get(i));
            } else {
                db.removeEntry(torrents.get(i));
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(torrents2.get(i)));
                startActivity(intent);*/
            }
        });

        return view;
    }
}
