package hu.bme.aut.moviemonitor.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Movie extends RealmObject
{
    private String title;
    private boolean watched;
    private String review;
    private float rating;

    @PrimaryKey
    private String movieID;

    public Movie()
    {
    }

    public Movie(String title)
    {
        this.title = title;
        this.review = "";
        this.rating = 0;
    }

    public String getTitle()
    {
        return title;
    }

    public boolean getWatched()
    {
        return watched;
    }

    public String getReview()
    {
        return review;
    }

    public float getRating()
    {
        return rating;
    }

    public String getMovieID()
    {
        return movieID;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setWatched(boolean watched)
    {
        this.watched = watched;
    }

    public void setReview(String review)
    {
        this.review = review;
    }

    public void setRating(float rating)
    {
        this.rating = rating;
    }
}
