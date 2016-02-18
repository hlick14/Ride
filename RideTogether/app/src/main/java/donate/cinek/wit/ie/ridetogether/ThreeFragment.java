package donate.cinek.wit.ie.ridetogether;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba Pieczonka on 05/11/2015.
 */
public class ThreeFragment extends android.support.v4.app.Fragment {

    public ThreeFragment() {
        // Required empty public constructor
    }
    private ListView listView;

    public List<SoloTrip> GroupHujumuniu = new ArrayList<>();
    SoloTrip oneTrip;

    Bitmap bitmap;

    private List <Bitmap> imgList = new ArrayList<Bitmap>();
    String imgTripId;
    int t = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GroupHujumuniu.clear();






    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        GroupHujumuniu.clear();

        findTrip();

        return inflater.inflate(R.layout.fragment_three, container, false);
    }
    public void findTrip()
    {
        final ParseQuery<ParseObject> tripQuery= ParseQuery.getQuery("ImageUpload");
        tripQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {


                    for (int i = 0; i < parseObjects.size(); i++) {

                        ParseObject objects = parseObjects.get(i);
                        imgTripId = (String) objects.get("TripID");

                        ParseFile file = (ParseFile) objects.get("ImageFile");

                        file.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    imgList.add(bitmap);
                                    t++;
                                    if (t == (parseObjects.size() - 1)) {

                                        getTrips();
                                        t=0;
                                    }

                                } else {
                                    Toast.makeText(getActivity(), "Could not load image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                } else {

                }
            }

        });
    }
    public void getTrips()
    {

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> trips, ParseException e) {
                if (e == null) {
                    if (trips == null || trips.size() == 0) {
                        //Toast.makeText(AllTripDisplayer.this, "Ooops, Something went wrong please try again", Toast.LENGTH_SHORT).show();
                        return; //no objects
                    }

                    for (int i = 0; i < trips.size(); i++) {

                        ParseObject object = trips.get(i);

                        String id = (String) object.getObjectId();
                        String tName = (String) object.get("TripName");
                        String tDate = (String) object.get("TripDate");
                        String tTime = (String) object.get("TripTime");
                        String tStartCity = (String) object.get("StartCity");
                        String tEndCity = (String) object.get("EndCity");

                        GroupHujumuniu.add(new SoloTrip(id,imgList.get(i), tName, tStartCity, tEndCity, tDate, tTime));

                    }
                    // !!!! Adapter initialized at this point due to asynchronous method type . find in background!!!!!!!!!///////////
                    SoloTripAdapter adapter = new SoloTripAdapter(getActivity(),
                            R.layout.listviewgroup, GroupHujumuniu);

                    listView = (ListView)getView().findViewById(R.id.listView2);

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            oneTrip = (SoloTrip) parent.getAdapter().getItem(position);

                            Intent intent = new Intent(getActivity().getApplicationContext(), TripInfo.class);
                            intent.putExtra("icon", oneTrip.getIcon());
                            intent.putExtra("startcity", oneTrip.getStartCity());
                            intent.putExtra("endcity", oneTrip.getEndCity());
                            intent.putExtra("date", oneTrip.getTripDate());
                            intent.putExtra("time", oneTrip.getTripTime());
                            intent.putExtra("name", oneTrip.getTitle());
                            intent.putExtra("check", 1);
                            startActivity(intent);
                        }
                    });


                }

                else {
                    Toast.makeText(getActivity(), "There was a problem fetching trip data from our servers", Toast.LENGTH_SHORT).show();

                }

                if (GroupHujumuniu.isEmpty())
                {
                    // Toast.makeText(AllTripDisplayer.this, "You don't have any trips at the moment", Toast.LENGTH_SHORT).show();
                }

            }


        });
    }
}