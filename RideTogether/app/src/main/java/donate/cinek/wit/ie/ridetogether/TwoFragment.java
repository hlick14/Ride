package donate.cinek.wit.ie.ridetogether;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kuba Pieczonka on 05/11/2015.
 */
public class TwoFragment extends android.support.v4.app.Fragment implements LocationListener {


    Typeface weatherFont;

    TextView cityField;
    TextView detailsField;
    TextView weatherIcon;
    ProgressDialog dialog;

    Context context;
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    Location location;
    double latitude,longitude;

    Handler handler;
    Bitmap userProfile;
    String tDate,tTime;
    TextView tv;
    CountDownTimer mCountDownTimer;
    private String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayAdapter<Bitmap> imagesArrayAdapter;
    private ArrayList<String> names, usernameForImages;
    private ArrayList<Bitmap> images;
    private ListView usersListView;
    private Button logoutButton;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;

    long mInitialTime = DateUtils.DAY_IN_MILLIS * 2 +
            DateUtils.HOUR_IN_MILLIS * 9 +
            DateUtils.MINUTE_IN_MILLIS * 3 +
            DateUtils.SECOND_IN_MILLIS * 42;

    public TwoFragment() {
        // Initializing handler for Open Weather api
        handler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.getActivity().setContentView(R.layout.fragment_two);
        getUserProfileImage();
        getTrips();

        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "font/weather.ttf");


        {
            context = getActivity().getApplicationContext();
            LocationManager lm = null;
             latitude = 0.0;
             longitude = 0.0;
            boolean gps_enabled = false, network_enabled = false;
            if (lm == null)
                lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            try {

                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps_enabled ) {
               showAlert();

            }


            if (network_enabled) {
                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                Log.d("RideTogether", "Network based location Enabled");
                if (lm != null) {
                    location = lm
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (gps_enabled) {
                if (location == null) {
                    lm.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);
                    Log.d("GPS", "GPS Enabled");
                    if (lm != null) {
                        location = lm
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }
        }


            updateWeatherData(latitude, longitude);


//        }
    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }
    private void updateWeatherData(final double lat,final double lon){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON(getActivity(), lat,lon);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }
    private void renderWeather(JSONObject json){
        try {
            JSONObject main = json.getJSONObject("main");
            cityField.setText(json.getString("name").toUpperCase(Locale.ENGLISH) +
                    ", " +
                    json.getJSONObject("sys").getString("country")+
                    "\n" + "Humidity: " + main.getString("humidity") + "%" +
                    "\n" + "Temperature: "+ (String.format("%.2f", main.getDouble("temp"))+ " ℃"));

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);



//            currentTemperatureField.setText(
//                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
//            updatedField.setText("Last update: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
    public void changeCity(String city){
        updateWeatherData(latitude, longitude);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        setConversationsList();



        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);
        de.hdodenhof.circleimageview.CircleImageView cv = (de.hdodenhof.circleimageview.CircleImageView) rootView.findViewById(R.id.circleUserProfileTab);




        cv.setImageBitmap(userProfile);
        weatherIcon.setTypeface(weatherFont);
        return rootView;
    }
    @Override
    public void onResume() {
       super.onResume();
    }
    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void setConversationsList() {
        currentUserId = ParseUser.getCurrentUser().getUsername();
        names = new ArrayList<String>();
        images = new ArrayList<Bitmap>();
        usernameForImages = new ArrayList<String>();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", currentUserId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < userList.size(); i++) {

                        usernameForImages.add(userList.get(i).getObjectId());

                        names.add(userList.get(i).getUsername());

                        ///Fetching image

//                        final ParseQuery<ParseObject> query3 = ParseQuery.getQuery("ImageUpload");
//                        query3.whereEqualTo("CreatedbyUser", usernameForImages.get(i));
//                        query3.getFirstInBackground(new GetCallback<ParseObject>() {
//                            public void done(ParseObject object, ParseException e) {
//                                if (object != null) {
//
//                                    ParseFile file = (ParseFile) object.get("ImageFile");
//                                    file.getDataInBackground(new GetDataCallback() {
//                                        public void done(byte[] data, ParseException e) {
//                                            if (e == null) {
//                                                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                                images.add(bitmap);
//                                                // Toast.makeText(BaseActivity.this, "" + bitmap.getHeight() , Toast.LENGTH_SHORT).show();
//                                                //use this bitmap as you want
//                                            } else {
//                                                Toast.makeText(ListUsersActivity.this, "Zdjecie jest puste" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
//
//                                            }
//                                        }
//
//
//                                    });
//
//                                } else {
//                                    Toast.makeText(ListUsersActivity.this, "objekt jest pusty" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
//
//                                }
//                            }
//                        });
//
//
                    }

                    usersListView = (ListView) getActivity().findViewById(R.id.usersListView2);
                    namesArrayAdapter =
                            new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                    R.layout.user_list_item, names);
                    usersListView.setAdapter(namesArrayAdapter);
//                    imagesArrayAdapter = new ArrayAdapter<Bitmap>(getApplicationContext(),R.layout.user_list_item,images);
//                    usersListView.setAdapter(imagesArrayAdapter);

                    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
//                            Conversation(names, i);
                        }
                    });

                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Toast.makeText(getActivity().getApplicationContext(),
                "" + usernameForImages,
                Toast.LENGTH_LONG).show();

    }
    public void getUserProfileImage()
    {
        ParseUser currentUser2 = ParseUser.getCurrentUser();
        ///Fetching image
        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("ImageUpload");
        query2.whereEqualTo("CreatedbyUser", currentUser2);
        query2.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object != null) {

                    ParseFile file = (ParseFile) object.get("ImageFile");
                    file.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                userProfile = BitmapFactory.decodeByteArray(data, 0, data.length);
                                de.hdodenhof.circleimageview.CircleImageView cv = (de.hdodenhof.circleimageview.CircleImageView) getActivity().findViewById(R.id.circleUserProfileTab);
                                cv.setImageBitmap(userProfile);




                                // Toast.makeText(BaseActivity.this, "" + bitmap.getHeight() , Toast.LENGTH_SHORT).show();
                                //use this bitmap as you want
                            } else {
                                Toast.makeText(getActivity(), "Error Loading Data From Our Servers - Image Problem", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {
                    Toast.makeText(getActivity(), "Error Loading Data From Our Servers - User Problem", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    public void getTrips() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("CreatedbyUser", currentUser);
        query.orderByAscending("UpdatedAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> StartingCity, ParseException e) {

                if (e == null) {
                    if (StartingCity.isEmpty()) {
//                        if (dialog.isShowing())
//                            dialog.dismiss();
                        TextView tv = (TextView) getActivity().findViewById(R.id.TripTimer);
                        tv.setText("You don't have any trips at the moment");

                        return; //no objects
                    }

                    for (int i = 0; i < StartingCity.size(); i++) {

                        ParseObject object = StartingCity.get(0);


                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
                        formatter.setLenient(false);


                        tDate = (String) object.get("TripDate");
                        tTime = (String) object.get("TripTime");
                        String tTimeFormatedMinutes="";
                        String tTimeFormatedHours="";
                        if (tTime!=null) {

                            tTimeFormatedMinutes=tTime.substring(tTime.length() - 2);
                            if(tTime.length()==3) {
                                tTimeFormatedHours = tTime.substring(0, 1);
                            }
                            else if (tTime.length()==4)
                            {
                                tTimeFormatedHours = tTime.substring(0, 2);
                            }


                            } else {
                                // whatever is appropriate in this case
                                throw new IllegalArgumentException("word has less than characters then required!");
                            }
                        if (tTime!=null) {

                            tTimeFormatedMinutes=tTime.substring(tTime.length() - 2);
                            if(tTime.length()==3) {
                                tTimeFormatedHours = tTime.substring(0, 1);
                            }
                            else if (tTime.length()==4)
                            {
                                tTimeFormatedHours = tTime.substring(0, 2);
                            }


                        } else {
                            // whatever is appropriate in this case
                            throw new IllegalArgumentException("word has less than characters then required!");
                        }
                        int dayOfmonthTemp = tDate.indexOf("-");
                        int monthTemp = tDate.lastIndexOf("-");

                        String dayOfMonthFormated = tDate.substring(0,dayOfmonthTemp);
                        String month = tDate.substring(dayOfmonthTemp+1,monthTemp);
                        String year = tDate.substring(monthTemp+1,tDate.length());

                        Calendar thatDay = Calendar.getInstance();
                        thatDay.set(Calendar.DAY_OF_MONTH,Integer.parseInt(dayOfMonthFormated));
                        thatDay.set(Calendar.MONTH,Integer.parseInt(month)-1); // 0-11 so 1 less
                        thatDay.set(Calendar.YEAR, Integer.parseInt(year));

                        Calendar calendar1 = Calendar.getInstance();
                        Calendar calendar2 = Calendar.getInstance();

                        int cyear = calendar1.get(Calendar.YEAR);
                        int cmonth = calendar1.get(Calendar.MONTH)+1;//months start at 0
                        int cday = calendar1.get(Calendar.DAY_OF_MONTH);
                        int cHour = calendar1.get(Calendar.HOUR_OF_DAY);
                        int cMinutes = calendar1.get(Calendar.MINUTE);
                        calendar1.set(cyear, cmonth, cday,cHour,cMinutes);

                        calendar2.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(dayOfMonthFormated),Integer.parseInt(tTimeFormatedHours),Integer.parseInt(tTimeFormatedMinutes));
                        long milsecs1= calendar1.getTimeInMillis();
                        long milsecs2 = calendar2.getTimeInMillis();
                        long diff = milsecs1 - milsecs2;
                        long dsecs = diff / 1000;
                        long dminutes = diff / (60 * 1000);
                        long dhours = diff / (60 * 60 * 1000);
                        long ddays = diff / (24 * 60 * 60 * 1000);





                         mInitialTime = DateUtils.DAY_IN_MILLIS * ddays +
                                DateUtils.HOUR_IN_MILLIS * dhours +
                                DateUtils.MINUTE_IN_MILLIS * dminutes +
                                DateUtils.SECOND_IN_MILLIS * dsecs;
//                        String oldTime = tDate + "," +tTimeFormatedHours+":"+tTimeFormatedMinutes;
                        tv = (TextView) getActivity().findViewById(R.id.TripTimer);
//                        tv.setText("Date of your next trip is " +tDate +   "Hours" + tTimeFormatedHours + "minutes "+tTimeFormatedMinutes);

                        mCountDownTimer = new CountDownTimer(mInitialTime, 1000) {
                            StringBuilder time = new StringBuilder();
                            @Override
                            public void onFinish() {
                                tv.setText(DateUtils.formatElapsedTime(0));
                                //mTextView.setText("Times Up!");
                            }

                            @Override
                            public void onTick(long millisUntilFinished) {
                                time.setLength(0);
                                // Use days if appropriate
                                if(millisUntilFinished > DateUtils.DAY_IN_MILLIS) {
                                    long count = millisUntilFinished / DateUtils.DAY_IN_MILLIS;
                                    if(count > 1)
                                        time.append(count).append(" days ");
                                    else
                                        time.append(count).append(" day ");

                                    millisUntilFinished %= DateUtils.DAY_IN_MILLIS;
                                }

                                time.append(DateUtils.formatElapsedTime(Math.round(millisUntilFinished / 1000d)));
                                tv.setText("Time until your next trip"+
                                        "\n" +time.toString());
                            }
                        }.start();








                }

                } else {
                    if (dialog.isShowing())
                        dialog.dismiss();

                }
                if (StartingCity.isEmpty()) {



//                    if (dialog.isShowing())
//                        dialog.dismiss();
                }


            }


        });

    }

}
