package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
public class OneFragment extends android.support.v4.app.Fragment {
    private ListView listView1;
    public List<SoloTrip> hujumuniu = new ArrayList<>();
    SoloTrip oneTrip;
    public List<ParseObject> tempObjectHolder = new ArrayList<>();
    Bitmap bitmap;
    String tName, tDate, tTime;
    private List<Bitmap> imgList = new ArrayList<Bitmap>();
    int t = 0;
    View fragmentView;
    ProgressDialog dialog;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

            findUser();

        fragmentView = inflater.inflate(R.layout.fragment_one, container, false);
        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plan = new Intent(getActivity(), MapsActivity.class);
                startActivity(plan);
            }
        });



        return fragmentView;
    }
    public void findUser() {
        if (hujumuniu.isEmpty()) {
            dialog = new ProgressDialog(this.getActivity(), 1);
            dialog.setMessage("Retrieving Your Trips List");
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
        }




        ParseUser currentUser = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> tripQuery = ParseQuery.getQuery("TripImage");
        tripQuery.whereEqualTo("CreatedbyUser", currentUser); //TO
        tripQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> parseObjects, ParseException e) {
                if (parseObjects.size() == 0) {

                }
                if (e == null) {
                    for (int i = 0; i < parseObjects.size(); i++) {


                        ParseObject objects = parseObjects.get(i);
                        ParseFile file = (ParseFile) objects.get("ImageFile");
                        file.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    imgList.add(bitmap);
                                    t++;
                                    if (t == (parseObjects.size())) {

                                        getTrips();
                                    } else {

                                    }
                                } else {
                                    // something went wrong
                                    Toast.makeText(getActivity(), "Error Loading User's profile Image", Toast.LENGTH_SHORT);

                                }
                            }
                        });


                    }


                } else {
                    Toast.makeText(getActivity(), "Error Loading User's profile ", Toast.LENGTH_SHORT);


                }
            }

        });
    }

    public void getTrips() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
        query.whereEqualTo("CreatedbyUser", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> StartingCity, ParseException e) {

                if (e == null) {
                    if (StartingCity.size() == 0) {
                        if (dialog.isShowing())
                            dialog.dismiss();

                        return; //no objects
                    }

                    for (int i = 0; i < StartingCity.size(); i++) {

                        ParseObject object = StartingCity.get(i);
                        tempObjectHolder.add(object);

                        String id = object.getObjectId();
                        tName = (String) object.get("TripName");
                        tDate = (String) object.get("TripDate");
                        tTime = (String) object.get("TripTime");
                        String tStartCity = (String) object.get("StartCity");
                        String tEndCity = (String) object.get("EndCity");
                        hujumuniu.add(new SoloTrip(id, imgList.get(i), tName, tStartCity, tEndCity, tDate, tTime));


                    }
                    // !!!! Adapter initialized at this point due to asynchronous method type . find in background!!!!!!!!!///////////
                    setTripAdapter();
                } else {
                    if (dialog.isShowing())
                        dialog.dismiss();

                }
                if (hujumuniu.isEmpty()) {


                    Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                            R.drawable.notrip);
                    hujumuniu.add(new SoloTrip("0", icon, "No available Trips", "Unavailable", "Unavailable", "Unavailable", "Unavailable"));
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
                if (dialog.isShowing())
                    dialog.dismiss();


            }



        });


    }

    public void setTripAdapter()
    {
        SoloTripAdapter adapter = new SoloTripAdapter(getActivity(),
                R.layout.listview_item_row, hujumuniu);


        listView1 = (ListView) getView().findViewById(R.id.listView1);


        listView1.setAdapter(adapter);

        /////temp disbaled to test async method
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                oneTrip = (SoloTrip) parent.getAdapter().getItem(position);

                Intent intent = new Intent(getActivity().getApplicationContext(), popup.class);
                intent.putExtra("ID", oneTrip.getId());
                intent.putExtra("Name", tName);
                intent.putExtra("Date", tDate);
                intent.putExtra("Time", tTime);

                startActivity(intent);
            }
        });
    }
}
