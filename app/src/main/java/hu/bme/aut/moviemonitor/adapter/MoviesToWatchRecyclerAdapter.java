package hu.bme.aut.moviemonitor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.RealmList;

import hu.bme.aut.moviemonitor.R;
import hu.bme.aut.moviemonitor.data.Movie;
import hu.bme.aut.moviemonitor.touch.ListTouchHelperAdapter;
import hu.bme.aut.moviemonitor.MainActivity;


public class MoviesToWatchRecyclerAdapter extends RecyclerView.Adapter<MoviesToWatchRecyclerAdapter.ViewHolder> implements ListTouchHelperAdapter
{
    private List<Movie> toWatch;
    private Context context;
    private Realm realmToWatch;

    public MoviesToWatchRecyclerAdapter(Context context, Realm realmToWatch)
    {
        this.context = context;
        this.realmToWatch = realmToWatch;

        RealmResults<Movie> itemResult = realmToWatch.where(Movie.class).equalTo("watched", false).findAll(); // .sort("name", Sort.ASCENDING);

        toWatch = new RealmList<Movie>();

        for (int i = 0; i < itemResult.size(); i = i + 1)
        {
            toWatch.add(itemResult.get(i));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);

        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        holder.tvTitle.setText(toWatch.get(position).getTitle());
        holder.cbWatched.setChecked(toWatch.get(position).getWatched());

        holder.cbWatched.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();

                realmToWatch.beginTransaction();
                toWatch.get(position).setWatched(holder.cbWatched.isChecked());
                realmToWatch.commitTransaction();

                toWatch.remove(position);

                notifyItemRemoved(position);

                /* if (ViewWatchedActivity.getWatchedRecyclerAdapter() != null)
                {
                    ViewWatchedActivity.getWatchedRecyclerAdapter().notifyItemInserted(position);
                } */
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity)context).openViewActivity(holder.getAdapterPosition(),
                        toWatch.get(holder.getAdapterPosition()).getMovieID());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return toWatch.size();
    }

    public void add(Movie movie)
    {
        toWatch.add(0, movie);
    }

    @Override
    public void onItemDismiss(int position)
    {
        realmToWatch.beginTransaction();
        toWatch.get(position).deleteFromRealm();
        realmToWatch.commitTransaction();

        toWatch.remove(position);

        // refreshes the whole list
        // notifyDataSetChanged();
        // refreshes just the relevant part that has been deleted
        notifyItemRemoved(position);
    }

    public void dismissAllItems()
    {
        for (int i = 0; i < toWatch.size();)
        {
            realmToWatch.beginTransaction();
            toWatch.get(0).deleteFromRealm();
            realmToWatch.commitTransaction();

            toWatch.remove(0);

            notifyItemRemoved(0);
        }
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition)
    {
        if (fromPosition < toPosition)
        {
            for (int i = fromPosition; i < toPosition; i = i + 1)
            {
                Collections.swap(toWatch, i, i + 1);
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i = i - 1)
            {
                Collections.swap(toWatch, i, i - 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView tvTitle;
        private CheckBox cbWatched;

        public ViewHolder(View itemView)
        {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            cbWatched = (CheckBox) itemView.findViewById(R.id.cbWatched);
        }
    }
}