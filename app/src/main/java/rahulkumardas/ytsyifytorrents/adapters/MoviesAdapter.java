package rahulkumardas.ytsyifytorrents.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import rahulkumardas.ytsyifytorrents.Utils.DotProgressBar;
import rahulkumardas.ytsyifytorrents.YtsApplication;
import rahulkumardas.ytsyifytorrents.models.Movie;
import rahulkumardas.ytsyifytorrents.R;

/**
 * Created by Rahul Kumar Das on 06-05-2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private Context context;
    private List<Movie> list;
    private ItemClickListener mClickListener;
    private int span;

    public MoviesAdapter(Context context, List<Movie> list, int span) {
        this.context = context;
        this.list = list;
        this.span = span;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
            view.setTag(1);
            holder = new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list2, parent, false);
            view.setTag(2);
            holder = new ViewHolder(view);
        }
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


//            if (span == 2) {
//                holder.rootView.setPadding(16, 16, 16, 16);
//                holder.imageView.setPadding(4, 4, 4, 4);
//                holder.imageView.setMaxHeight(220);
//            }
//            if (span == 3) {
//                holder.rootView.setPadding(10, 16, 10, 16);
//                holder.imageView.setPadding(2, 2, 2, 2);
//                holder.imageView.setMaxHeight(150);
//            } else {
//                holder.rootView.setPadding(8, 10, 8, 10);
//                holder.imageView.setPadding(1, 1, 1, 1);
//                holder.imageView.setMaxHeight(100);
//            }

        }
    }

    @Override
    public int getItemCount() {
        if (list.size() > 0 && list.size() % 20 == 0)
            return list.size() + 2;
        else
            return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView name, year;
        DotProgressBar progressBar;
        LinearLayout rootView;

        public ViewHolder(View itemView) {
            super(itemView);

            if ((int) itemView.getTag() == 1) {
                imageView = (ImageView) itemView.findViewById(R.id.image);
                name = (TextView) itemView.findViewById(R.id.name);
                year = (TextView) itemView.findViewById(R.id.year);
                rootView = (LinearLayout) itemView.findViewById(R.id.root);

                itemView.setOnClickListener(this);
            } else {
                progressBar = (DotProgressBar) itemView.findViewById(R.id.progress);
            }
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

    @Override
    public int getItemViewType(int position) {
        if (position < list.size())
            return 1;
        else
            return 2;
    }

}
