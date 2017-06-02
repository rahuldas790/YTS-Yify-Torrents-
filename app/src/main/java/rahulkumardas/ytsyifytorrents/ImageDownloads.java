package rahulkumardas.ytsyifytorrents;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rahulkumardas.ytsyifytorrents.databases.DownloadDatabase;

/**
 * Created by Rahul Kumar Das on 27-05-2017.
 */

public class ImageDownloads extends Fragment {

    private RecyclerView recyclerView;
    private List<String> images;
    private List<String> uris;
    private DownloadDatabase db;
    private final int COLUMN_SPAN = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_downloads, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        db = new DownloadDatabase(getActivity());
        uris = db.allImages();
        images = new ArrayList<>();
        for (int i = 0; i < uris.size(); i++) {
            File file = new File(uris.get(i));
            if (file.exists()) {
                images.add(uris.get(i));
            } else {
                db.removeEntry(uris.get(i));
            }
        }
        GridLayoutManager manager = new GridLayoutManager(getActivity(), COLUMN_SPAN);
        recyclerView.setLayoutManager(manager);
        MyAdapter adapter = new MyAdapter();
        recyclerView.setAdapter(new MyAdapter());

        return view;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_image, null);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
            Log.i("path is", images.get(position));
            File file = new File(images.get(position));
//            holder.imageView.setImageURI(Uri.parse(images.get(position)));
            Glide.with(getActivity())
                    .load(file)
                    .placeholder(R.mipmap.my_logo)
                    .into(holder.imageView);

            final int pos = position;
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), FullScreenActivity.class);
                    i.putExtra("uri", images.get(pos));
                    startActivity(i);
                }
            });

        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView2);
            }
        }

    }
}
