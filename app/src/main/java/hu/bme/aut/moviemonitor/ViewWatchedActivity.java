package hu.bme.aut.moviemonitor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


import java.util.UUID;

import hu.bme.aut.moviemonitor.adapter.MoviesWatchedRecyclerAdapter;
import hu.bme.aut.moviemonitor.data.Movie;
import hu.bme.aut.moviemonitor.touch.ListItemTouchHelperCallback;

import io.realm.Realm;


public class ViewWatchedActivity extends AppCompatActivity
{
    public static final String KEY_ITEM_ID = "KEY_ITEM_ID";
    public static final int REQUEST_CODE_VIEW = 101;

    private static MoviesWatchedRecyclerAdapter watchedRecyclerAdapter;
    private RecyclerView recyclerList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MainApplication)getApplication()).openRealm();

        setupUI();
    }

    public static MoviesWatchedRecyclerAdapter getWatchedRecyclerAdapter()
    {
        return watchedRecyclerAdapter;
    }

    private void setupUI()
    {
        setUpToolBar();
        setUpAddItemUI();
        setupRecyclerView();
    }

    private void setupRecyclerView()
    {
        recyclerList = (RecyclerView) findViewById(R.id.recyclerList);
        recyclerList.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerList.setLayoutManager(layoutManager);

        watchedRecyclerAdapter = new MoviesWatchedRecyclerAdapter(this, ((MainApplication)getApplication()).getRealm());
        recyclerList.setAdapter(watchedRecyclerAdapter);

        // adding touch support
        ItemTouchHelper.Callback callback = new ListItemTouchHelperCallback(watchedRecyclerAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerList);
    }

    private void setUpAddItemUI()
    {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showAddMovieDialog();
            }
        });
    }

    private void setUpToolBar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void openViewActivity(int index, String itemID)
    {
        Intent startView = new Intent(this, EditMovieActivity.class);

        startView.putExtra(KEY_ITEM_ID, itemID);

        startActivityForResult(startView, REQUEST_CODE_VIEW);
    }

    private Realm getRealm()
    {
        return ((MainApplication)getApplication()).getRealm();
    }

    private void addMovie(String title)
    {
        getRealm().beginTransaction();

        Movie addMovie = getRealm().createObject(Movie.class, UUID.randomUUID().toString());

        addMovie.setTitle(title);
        addMovie.setWatched(true);

        getRealm().commitTransaction();

        watchedRecyclerAdapter.add(addMovie);
        watchedRecyclerAdapter.notifyItemInserted(0);
    }

    private void showAddMovieDialog()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("ADD MOVIE TO MOVIES WATCHED");

        final EditText etTitle = new EditText(this);

        builder.setView(etTitle);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                addMovie(etTitle.getText().toString());

                Snackbar.make(recyclerList, "The movie has been added!", Snackbar.LENGTH_LONG).setAction("added", null).show();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        ((MainApplication)getApplication()).closeRealm();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // inflate the menu: this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_watched, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_add_new_movie)
        {
            findViewById(R.id.fab).performClick();

            return true;
        }
        if (id == R.id.action_delete_all_movies)
        {
            watchedRecyclerAdapter.dismissAllItems();

            Snackbar.make(recyclerList, "All of the movies have been removed!", Snackbar.LENGTH_LONG).setAction("delete_all", null).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}