package es.adolfo.openweather;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import es.adolfo.openweather.model.City;

public class MainActivity extends FragmentActivity  {


    private static final String FIRST_FRAGMENT = "FIRST_FRAGMENT";
    private static final String SECOND_FRAGMENT = "SECOND_FRAGMENT";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        WeatherFragment firstFragment = (WeatherFragment) fragmentManager.findFragmentByTag(FIRST_FRAGMENT);
        if(firstFragment == null) {
            Log.d(TAG,"First fragment not found");
            firstFragment = WeatherFragment.newInstance();
        }
        if(findViewById(R.id.second)==null) {

            fragmentTransaction.replace(R.id.main,firstFragment,FIRST_FRAGMENT);
        }
        else {

            fragmentTransaction.replace(R.id.main, firstFragment,FIRST_FRAGMENT);
            WeatherFragment secondFragment = (WeatherFragment) fragmentManager.findFragmentByTag(SECOND_FRAGMENT);
            if(secondFragment == null) {
                Log.d(TAG,"Second fragment not found");
                secondFragment = WeatherFragment.newInstance();
            }
            fragmentTransaction.replace(R.id.second,secondFragment,SECOND_FRAGMENT);
        }
        fragmentTransaction.commit();
     */
    }


}
