package es.adolfo.openweather.task;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import es.adolfo.openweather.model.Weather;
import es.adolfo.openweather.model.Weather;

/**
 * Created by Adolfo on 01/04/2017.
 */

public class WeatherTask extends AsyncTask<String,Void,Weather> {

    private static final String TAG = "WeatherTask";

    private AsyncResponse delegate = null;

    @Override
    protected void onPreExecute() {
        delegate.onPreExecute();
    }

    @Override
    protected Weather doInBackground(String... params) {
        Weather weather = null;
        HttpURLConnection connection = null ;
        Map<String,String> queryStringParams = new HashMap<String,String>();
        queryStringParams.put("id",params[0]);
        queryStringParams.put("appid",params[1]);
        queryStringParams.put("lang",params[2]);
        queryStringParams.put("units","metric");
        try {
            String url = "http://api.openweathermap.org/data/2.5/weather?"+ buildQueryParameters(queryStringParams);
            Log.d(TAG,"URL :" + url);
            connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            BufferedReader br = new BufferedReader( new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine())!= null){
                sb.append(line);
            }

            Log.d(TAG,"Response :" + sb.toString());
            ObjectMapper mapper = new ObjectMapper();
            //Weather weather = mapper.readValue(new URL("http://mkyong.com/api/staff.json"), Staff.class);

            weather = mapper.readValue(sb.toString(), Weather.class);
            Log.d(TAG,"Object :" + weather);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Throwable t){
            t.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return weather;
    }




    private String buildQueryParameters(Map<String,String> params) throws UnsupportedEncodingException {
        boolean first = true;
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String,String> pair : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(Weather weather) {
        if (delegate != null) {
            delegate.onPostExecute(weather);
        }
    }

    public void setDelegate(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        public void onPostExecute(Weather weather);
        public void onPreExecute();
    }
}
