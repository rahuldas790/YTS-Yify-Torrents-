package rahulkumardas.ytsyifytorrents.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import rahulkumardas.ytsyifytorrents.R;
import rahulkumardas.ytsyifytorrents.Utils.DotProgressBar;
import rahulkumardas.ytsyifytorrents.models.Movie;

/**
 * Created by Rahul Kumar Das on 06-05-2017.
 */

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private Context context;
    private List<Movie> list;
    private ItemClickListener mClickListener;

    public BookmarkAdapter(Context context, List<Movie> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;

        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        view.setTag(1);
        holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < list.size()) {
            Glide.with(context)
                    .load(list.get(position).imageUrl.split(";")[1])
                    .placeholder(R.mipmap.my_logo)
                    .dontAnimate()
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(holder.imageView);

            holder.name.setText(list.get(position).movieName);
            holder.year.setText(list.get(position).year + "");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView name, year;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            year = (TextView) itemView.findViewById(R.id.year);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
