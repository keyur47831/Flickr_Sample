package com.code4sharing.flickrsample.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.code4sharing.flickrsample.R;
import com.code4sharing.flickrsample.model.FlickrDataModel;
import com.code4sharing.flickrsample.presenter.FlickrPresenter;

import java.util.List;

public class HomeActivity extends AppCompatActivity {


    FlickrPresenter mFlickrPresenter;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_home);
        mFlickrPresenter=new FlickrPresenter (mRequestFlickrData);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mFlickrPresenter.loadData();
            return true;
        }

        return super.onOptionsItemSelected (item);
    }
    private FlickrPresenter.onFectFlickrDataRequest mRequestFlickrData = new FlickrPresenter.onFectFlickrDataRequest(){

        @Override
        public void  onRequestSucess(List<FlickrDataModel> NearByLocation) {
            Toast.makeText (getApplicationContext (), "DataSize : "+NearByLocation.size (), Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onRequestError() {
            Toast.makeText (getApplicationContext (), R.string.connection_error, Toast.LENGTH_SHORT).show();

        }
    };
}
