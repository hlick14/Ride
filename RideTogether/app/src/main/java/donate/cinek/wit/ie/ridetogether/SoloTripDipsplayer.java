package donate.cinek.wit.ie.ridetogether;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SoloTripDipsplayer extends AppCompatActivity {

    private ListView listView1;
    public List<SoloTrip> hujumuniu = new ArrayList<>();
    SoloTrip oneTrip;
    Bitmap bitmap;
    String idTripu;
    private List <Bitmap> imgList = new ArrayList<Bitmap>();
    String imgTripId;
    int t = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_trip_disp);
        //super.set();

        ParseUser currentUser = ParseUser.getCurrentUser();

        final ParseQuery<ParseObject> tripQuery= ParseQuery.getQuery("TripImage");
            tripQuery.whereEqualTo("CreatedbyUser", currentUser); //TO
            tripQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if(parseObjects.size()==0)
                {
                    Toast.makeText(SoloTripDipsplayer.this, "You don't have any trips at the moment", Toast.LENGTH_LONG).show();
                }
                    if (e == null) {
                        for (int i = 0 ; i <parseObjects.size(); i++)
                    {

                        ParseObject objects =  parseObjects.get(i);
                        imgTripId =(String) objects.get("TripID");
                       ParseFile file = (ParseFile) objects.get("ImageFile");
                        file.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    imgList.add(bitmap);
                                    t++;
                                    if(t == (parseObjects.size())) {

                                        getTrips();
                                    }
                                    else{

                                    }
                                } else {
                                    // something went wrong

                                }
                            }
                        });




                    }


                } else {

                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solo_trip_dipsplayer, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.optionsO) {
            Intent back = new Intent(SoloTripDipsplayer.this,Options.class);
            startActivity(back);

            return true;
        }
        else if (id== R.id.optionsLogout)
        {
            ParseUser.logOut();
            Intent backToMain = new Intent(SoloTripDipsplayer.this,MainActivity.class);
            startActivity(backToMain);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
    public void getTrips()
    {
        ParseUser currentUser = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("CreatedbyUser", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> StartingCity, ParseException e) {
                if (e == null) {
                    if (StartingCity == null || StartingCity.size() == 0) {
                        Toast.makeText(SoloTripDipsplayer.this, "You currently have no trips. Go back and plan a trip ", Toast.LENGTH_SHORT).show();
                        return; //no objects
                    }


                        for (int i = 0; i < StartingCity.size(); i++) {

                            ParseObject object = StartingCity.get(i);

                            String id = (String) object.getObjectId();
                            String tName = (String) object.get("TripName");
                            String tDate = (String) object.get("TripDate");
                            String tTime = (String) object.get("TripTime");
                            String tStartCity = (String) object.get("StartCity");
                            String tEndCity = (String) object.get("EndCity");


                            hujumuniu.add(new SoloTrip(id, imgList.get(i), tName, tStartCity, tEndCity, tDate, tTime));


                        }

                        // !!!! Adapter initialized at this point due to asynchronous method type . find in background!!!!!!!!!///////////
                    Collections.reverse(hujumuniu);
                        SoloTripAdapter adapter = new SoloTripAdapter(SoloTripDipsplayer.this,
                                R.layout.listview_item_row, hujumuniu);

                        listView1 = (ListView) findViewById(R.id.listView1);


                        listView1.setAdapter(adapter);


                        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                oneTrip = (SoloTrip) parent.getAdapter().getItem(position);

                                Intent intent = new Intent(getApplicationContext(), TripInfo.class);
                                intent.putExtra("ID", oneTrip.getId());
                                intent.putExtra("icon", oneTrip.getIcon());
                                intent.putExtra("startcity", oneTrip.getStartCity());
                                intent.putExtra("endcity", oneTrip.getEndCity());
                                intent.putExtra("date", oneTrip.getTripDate());
                                intent.putExtra("time", oneTrip.getTripTime());
                                intent.putExtra("name", oneTrip.getTitle());
                                intent.putExtra("check", 2);
                                startActivity(intent);
                            }
                        });
                    }


                else {

                }
               if (hujumuniu.isEmpty())
                {
                    Toast.makeText(SoloTripDipsplayer.this, "You don't have any trips at the moment", Toast.LENGTH_SHORT).show();
                }

            }


        });
    }

}
