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

import hu.bme.aut.moviemonitor.MainActivity;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.RealmList;

import hu.bme.aut.moviemonitor.R;
import hu.bme.aut.moviemonitor.data.Movie;
import hu.bme.aut.moviemonitor.touch.ListTouchHelperAdapter;
import hu.bme.aut.moviemonitor.ViewWatchedActivity;


public class MoviesWatchedRecyclerAdapter extends RecyclerView.Adapter<MoviesWatchedRecyclerAdapter.ViewHolder> implements ListTouchHelperAdapter
{
    private List<Movie> watched;
    private Context context;
    private Realm realmWatched;

    public MoviesWatchedRecyclerAdapter(Context context, Realm realmWatched)
    {
        this.context = context;
        this.realmWatched = realmWatched;

        RealmResults<Movie> itemResult = realmWatched.where(Movie.class).equalTo("watched", true).findAll(); // .sort("name", Sort.ASCENDING);

        watched = new RealmList<Movie>();

        for (int i = 0; i < itemResult.size(); i = i + 1)
        {
            watched.add(itemResult.get(i));
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
        holder.tvTitle.setText(watched.get(position).getTitle());
        holder.cbWatched.setChecked(watched.get(position).getWatched());

        holder.cbWatched.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int position = holder.getAdapterPosition();
                Movie changed = watched.get(position);

                realmWatched.beginTransaction();
                watched.get(position).setWatched(holder.cbWatched.isChecked());
                realmWatched.commitTransaction();

                watched.remove(position);

                notifyItemRemoved(position);

                if (MainActivity.getToWatchRecyclerAdapter() != null)
                {
                    MainActivity.getToWatchRecyclerAdapter().add(changed);

                    MainActivity.getToWatchRecyclerAdapter().notifyItemInserted(position);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((ViewWatchedActivity)context).openViewActivity(holder.getAdapterPosition(),
                        watched.get(holder.getAdapterPosition()).getMovieID());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return watched.size();
    }

    public void add(Movie movie)
    {
        watched.add(0, movie);
    }

    @Override
    public void onItemDismiss(int position)
    {
        realmWatched.beginTransaction();
        watched.get(position).deleteFromRealm();
        realmWatched.commitTransaction();

        watched.remove(position);

        // refreshes the whole list
        // notifyDataSetChanged();
        // refreshes just the relevant part that has been deleted
        notifyItemRemoved(position);
    }

    public void dismissAllItems()
    {
        for (int i = 0; i < watched.size();)
        {
            realmWatched.beginTransaction();
            watched.get(0).deleteFromRealm();
            realmWatched.commitTransaction();

            watched.remove(0);

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
                Collections.swap(watched, i, i + 1);
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i = i - 1)
            {
                Collections.swap(watched, i, i - 1);
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