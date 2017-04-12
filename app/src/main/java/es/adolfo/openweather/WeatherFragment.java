package es.adolfo.openweather;

import android.app.Activity;
import android.content.Context;
import java.util.TimeZone;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import es.adolfo.openweather.model.City;
import es.adolfo.openweather.model.Weather;
import es.adolfo.openweather.task.WeatherTask;
;


public class WeatherFragment extends Fragment implements WeatherTask.AsyncResponse {

    private WeatherTask weatherTask;
    private static String TAG = "WeatherFragment";
    

    private City city;
    private String appid = "f34aa163c9c1a53db7404ed0f25f8b51";
    private String locale;
    private Weather currentWeather;

    private Spinner spinner;
    private Button button;

    private LinearLayout content;

    private TableLayout table;
    private TextView cityWeather;
    private TextView temperature;
    private ImageView icon;


    public WeatherFragment() {

    }

    public static WeatherFragment newInstance() {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!= null) {
            currentWeather = (Weather) savedInstanceState.getSerializable("currentWeather");
            city = (City) savedInstanceState.getSerializable("city");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        table = (TableLayout) view.findViewById(R.id.table);
        cityWeather = (TextView) view.findViewById(R.id.cityWeather);
        temperature = (TextView) view.findViewById(R.id.temperature);
        icon = (ImageView) view.findViewById(R.id.icon);
        content = (LinearLayout) view.findViewById(R.id.content);

        DataBaseModel dataBaseModel = DataBaseModel.getInstance(getActivity());
        dataBaseModel.open();
        ArrayAdapter<City> adapter = new ArrayAdapter<City>(getActivity(),
                 android.R.layout.simple_spinner_item,dataBaseModel.getCityByProv("28"));
        dataBaseModel.close();
        Log.d("onCreateView","city is " + (city == null?"null":city.toString()));
        Log.d("onCreateView","currentWeather is " + (currentWeather == null?"null":currentWeather.getName().toString()));
        if(currentWeather!=null) {
            populateTable(currentWeather);
        }
        else {
            content.setVisibility(LinearLayout.GONE);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locale = Locale.getDefault().getCountry();
                weatherTask = new WeatherTask();
                weatherTask.setDelegate(WeatherFragment.this);
                city =  (City) spinner.getSelectedItem();
                Log.d("City has value",city.toString());
                weatherTask.execute(city.getId(),appid,locale);
            }
        });
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("currentWeather",currentWeather);
        outState.putSerializable("city",city);
    }

    @Override
    public void onPostExecute(Weather weather) {
        content.setVisibility(LinearLayout.VISIBLE);
        currentWeather = weather;
        populateTable(weather);

    }

    @Override
    public void onPreExecute() {
        NetworkInfo info = getActiveNetworkInfo();
        if(info==null || !info.isConnected() ) {
            weatherTask.cancel(true);
        }
    }



    private NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }

    private void populateTable(Weather weather) {

        String cityWeatherValue = String.format(getString(R.string.city_weather_label),city );
        cityWeather.setText(cityWeatherValue);
        icon.setImageResource(getResources().getIdentifier("i" + weather.getConditions().get(0).getIcon()
                ,"drawable",getActivity().getPackageName()));
        String temperatureValue = String.format(getString(R.string.temperature_constant),
                weather.getMain().getTemp());
        temperature.setText(temperatureValue);
        TextView windValue = (TextView) table.findViewById(R.id.windValue);
        TextView skyValue = (TextView) table.findViewById(R.id.skyValue);
        TextView pressureValue = (TextView) table.findViewById(R.id.pressureValue);
        TextView humidityValue = (TextView) table.findViewById(R.id.humidityValue);
        TextView sunriseValue = (TextView) table.findViewById(R.id.sunriseValue);
        TextView sunsetValue = (TextView) table.findViewById(R.id.sunsetValue);
        windValue.setText(weather.getWind().getSpeed().toString());
        skyValue.setText(weather.getConditions().get(0).getDescription());
        pressureValue.setText(weather.getMain().getPressure().toString());
        humidityValue.setText(weather.getMain().getHumidity().toString());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(weather.getSys().getSunrise()*1000);
        sunriseValue.setText(formatter.format(calendar.getTime()));
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(weather.getSys().getSunset()*1000);
        sunsetValue.setText(formatter.format(calendar.getTime()));
    }

}
