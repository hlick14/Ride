package donate.cinek.wit.ie.ridetogether;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
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
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba Pieczonka on 05/11/2015.
 */
public class ThreeFragment extends android.support.v4.app.Fragment {

    private ListView listView2;
    public List<SoloTrip> hujumuniu2 = new ArrayList<>();
    SoloTrip oneTrip;
    public List<ParseObject> tempObjectHolder = new ArrayList<>();
    Bitmap bitmap;
    String tName, tDate, tTime;
    private List<Bitmap> imgList2 = new ArrayList<Bitmap>();
    int t = 0;
    View fragmentView;
    ProgressDialog dialog;
    SoloTripAdapter adapter;

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findUser();

    }
    @Override
    public void onResume() {
        super.onResume();
        setTripAdapter();


    }
    @Override
    public void onActivityCreated (Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle b = getActivity().getIntent().getExtras();
        if(b!=null) {
            Boolean check = (Boolean) b.get("AddedNewTrip");
            if(check!=null)
            {
                t =0;
                if(adapter!=null)
                {
                    adapter.clear();
                }
                findUser();
//                adapter.notifyDataSetChanged();

            }
        }


        fragmentView = inflater.inflate(R.layout.fragment_three, container, false);

        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plan = new Intent(getActivity(), GooglePlacesAutocompleteActivity.class);
                plan.putExtra("TripType","g");
//                Intent plan = new Intent(getActivity(), MapsActivity.class);
                startActivity(plan);
            }
        });



        return fragmentView;
    }
    public void findUser() {
//        if (hujumuniu2.isEmpty()) {
//            dialog = new ProgressDialog(this.getActivity(), 1);
//            dialog.setMessage("Retrieving Your Trips List");
//            dialog.setCancelable(false);
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.show();
//        }




        ParseUser currentUser = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> tripQuery = ParseQuery.getQuery("TripImage");

        tripQuery.whereEqualTo("TripType","g");
        tripQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (parseObjects.isEmpty()) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
                }
                if (e == null) {
//                    imgList2 = new ArrayList<Bitmap>();
                    for (int i = 0; i < parseObjects.size(); i++) {


                        ParseObject objects = parseObjects.get(i);
                        ParseFile file = (ParseFile) objects.get("ImageFile");
                        file.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    imgList2.add(bitmap);
                                    t++;
                                    if (t == (parseObjects.size())) {

                                        getTrips();
                                    } else {
//                                        if (dialog.isShowing()) {
//                                            dialog.dismiss();
//                                        }

                                    }
                                } else {
                                    // something went wrong
                                    Toast.makeText(getActivity(), "Error Loading Trip's images", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


                    }


                } else {
                    //if there was a parse error
                    Toast.makeText(getActivity(), "There was not data about this profile on our servers ", Toast.LENGTH_SHORT).show();


                }
            }

        });
    }

    public void getTrips() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Trip");

        query2.whereEqualTo("TripType","g");
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> StartingCity2, ParseException e) {

                if (e == null) {
                    hujumuniu2.clear();
                    if (StartingCity2.isEmpty()) {
//                        if (dialog.isShowing())
//                            dialog.dismiss();

                        return; //no objects
                    }
                    hujumuniu2 = new ArrayList<SoloTrip>();

                    for (int i = 0; i < StartingCity2.size(); i++) {

                        ParseObject object = StartingCity2.get(i);
                        tempObjectHolder.add(object);

                        String id = object.getObjectId();
                        tName = (String) object.get("TripName");
                        tDate = (String) object.get("TripDate");
                        tTime = (String) object.get("TripTime");
                        String tStartCity = (String) object.get("StartCity");
                        String tEndCity = (String) object.get("EndCity");
                        hujumuniu2.add(new SoloTrip(id, imgList2.get(i), tName, tStartCity, tEndCity, tDate, tTime));


                    }
                    // !!!! Adapter initialized at this point due to asynchronous method type . find in background!!!!!!!!!///////////
                    setTripAdapter();
                } else {
//                    if (dialog.isShowing())
//                        dialog.dismiss();

                }
                if (StartingCity2.isEmpty()) {


                    Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                            R.drawable.notrip);
                    Toast.makeText(getActivity(), "This is getting exectured or not ?", Toast.LENGTH_SHORT).show();
                    hujumuniu2.add(new SoloTrip("0", icon, "No available Trips", "Unavailable", "Unavailable", "Unavailable", "Unavailable"));
                    setTripAdapter();
//                    if (dialog.isShowing())
//                        dialog.dismiss();
                }


            }


        });
//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
    }

    public void setTripAdapter()
    {


         adapter = new SoloTripAdapter(getActivity(),
                R.layout.listview_item_row2, hujumuniu2);


        listView2 = (ListView) getView().findViewById(R.id.listView2);


        listView2.setAdapter(adapter);
//        if (dialog.isShowing()) {
//            dialog.dismiss();
//        }
        final ParseUser currentUser = ParseUser.getCurrentUser();

        /////temp disbaled to test async method
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                oneTrip = (SoloTrip) parent.getAdapter().getItem(position);
                Log.d("ONE TRIP DETAILS", oneTrip.getTitle()+ oneTrip.getId());

                Intent intent = new Intent(getActivity().getApplicationContext(), popup2.class);
                intent.putExtra("ID", oneTrip.getId());
                intent.putExtra("UserId",currentUser.getUsername());
                intent.putExtra("Name", oneTrip.getTitle());
                intent.putExtra("Date", oneTrip.getTripDate());
                intent.putExtra("Time", oneTrip.getTripTime());

                startActivity(intent);
            }
        });
    }
}