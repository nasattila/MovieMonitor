package hu.bme.aut.moviemonitor;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MainApplication extends Application
{
	private Realm realm;

	@Override
    public void onCreate()
	{
        super.onCreate();

        Realm.init(this);
    }

	public void openRealm()
	{
		RealmConfiguration config = new RealmConfiguration
				.Builder()
				.deleteRealmIfMigrationNeeded()
				.build();
		realm = Realm.getInstance(config);
	}

	public void closeRealm()
	{
		realm.close();
	}

	public Realm getRealm()
	{
		return realm;
	}
}