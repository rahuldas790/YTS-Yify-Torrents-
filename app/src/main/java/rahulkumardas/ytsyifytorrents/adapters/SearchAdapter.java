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

import rahulkumardas.ytsyifytorrents.models.Movie;
import rahulkumardas.ytsyifytorrents.R;

/**
 * Created by Rahul Kumar Das on 08-05-2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<Movie> list;
    private MoviesAdapter.ItemClickListener mClickListener;

    public SearchAdapter(Context context, List<Movie> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        SearchAdapter.ViewHolder holder = new SearchAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(list.get(position).imageUrl.split(";")[0])
                .placeholder(R.mipmap.my_logo)
                .dontAnimate()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.imageView);

        holder.name.setText(list.get(position).movieName);
        holder.year.setText(list.get(position).year+"");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
    public void setClickListener(MoviesAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

