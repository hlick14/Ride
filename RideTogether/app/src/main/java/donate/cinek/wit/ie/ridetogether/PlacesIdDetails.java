package donate.cinek.wit.ie.ridetogether;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Kuba Pieczonka on 30/03/2016.
 */
public class PlacesIdDetails {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String TYPE_DETAIL = "/details";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY ="AIzaSyBBPpE3VtlxYiVHj_lkItAwzuEfh7EWWvU";
    public PlacesIdDetails() {
        // TODO Auto-generated constructor stub
    }
    public ArrayList<Double> placeDetail(String input) {
        ArrayList<Double> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAIL + OUT_JSON);
            sb.append("?placeid=" + URLEncoder.encode(input, "utf8"));
            sb.append("&key=" + API_KEY);
            URL url = new URL(sb.toString());

            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());


            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);

            }

        } catch (MalformedURLException e) {

            return resultList;
        } catch (IOException e) {

            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }

        }

        try {


            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONObject result = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");

            Double longitude  = result.getDouble("lng");
            Double latitude =  result.getDouble("lat");
            Log.v("LatLong", longitude + "long" + " " + latitude + "latitute");
            resultList = new ArrayList<Double>(result.length());
            resultList.add(result.getDouble("lng"));
            resultList.add(result.getDouble("lat"));
            System.out.println("les latitude dans le table"+resultList);

        } catch (JSONException e) {

        }

        return resultList;
    }
}
