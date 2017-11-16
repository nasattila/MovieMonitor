package hu.bme.aut.moviemonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import io.realm.Realm;

import hu.bme.aut.moviemonitor.data.Movie;


public class EditMovieActivity extends AppCompatActivity
{
    public static final String KEY_ITEM = "KEY_ITEM";

    private Movie viewMovie = null;
    private TextView titleText;
    private EditText reviewText;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie);

        if (getIntent().hasExtra(MainActivity.KEY_ITEM_ID))
        {
            String itemID = getIntent().getStringExtra(MainActivity.KEY_ITEM_ID);
            viewMovie = getRealm().where(Movie.class)
                    .equalTo("movieID", itemID)
                    .findFirst();
        }

        titleText = (TextView) findViewById(R.id.tvTitle);
        reviewText = (EditText) findViewById(R.id.tvReview);
        ratingBar = (RatingBar) findViewById(R.id.rbRating);

        Button buttonSave = (Button) findViewById(R.id.bSave);

        buttonSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                saveItem();
            }
        });

        if (viewMovie != null)
        {
            titleText.setText(viewMovie.getTitle());
            reviewText.setText(viewMovie.getReview());
            ratingBar.setRating(viewMovie.getRating());
        }
    }

    private void saveItem()
    {
        Intent intentResult = new Intent();

        getRealm().beginTransaction();
        viewMovie.setTitle(titleText.getText().toString());
        viewMovie.setReview(reviewText.getText().toString());
        viewMovie.setRating(ratingBar.getRating());

        intentResult.putExtra(KEY_ITEM, viewMovie.getMovieID());
        setResult(RESULT_OK, intentResult);

        finish();
    }

    public Realm getRealm()
    {
        return ((MainApplication)getApplication()).getRealm();
    }
}
