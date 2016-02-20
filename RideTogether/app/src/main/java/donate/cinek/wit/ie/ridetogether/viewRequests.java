package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
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
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba Pieczonka on 19/02/2016.
 */
public class viewRequests extends android.support.v4.app.Fragment {

    View fragmentView;
    Button friendSearch;
    private ArrayAdapter<String> namesArrayAdapter;
    private ListView usersListView;
    ProgressDialog progressDialog;
    TextView time, date,FoundRequestedUsers;
    TextInputLayout update;
    private ArrayList<String> listOfUsernames;
    String name;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ListView listView1;
    private String requestedUsername;


    public viewRequests() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public void onResume() {
        super.onResume();


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.view_request_fragment, container, false);


        getListOfUsers();

        return fragmentView;
    }
    private void getListOfUsers() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        listOfUsernames = new ArrayList<String>();


//        String currentUser = ParseUser.getCurrentUser().getUsername();
//        ParseQuery<ParseObject> q2 = ParseQuery.getQuery("FriendRequest");
//        q2.whereEqualTo("RequestedUser", currentUser);
//        q2.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> list, ParseException e) {
//                if (e == null) {
//                    for (int i = 0; i < list.size(); i++) {
//
//                        listOfUsernames.add(list.get(i).get("username").toString());
//                    }
//                    if (progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
//                } else {
//                    Toast.makeText(getActivity(), "Could not load the list of users", Toast.LENGTH_SHORT).show();
//                    if (progressDialog.isShowing()) {
//                        progressDialog.dismiss();
//                    }
//                }
//                if(list.isEmpty())
//                {
//                    listOfUsernames.add("No users found ");
//                }
//            }
//        });



        String currentUser = ParseUser.getCurrentUser().getUsername();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", currentUser);

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {

                        listOfUsernames.add(objects.get(i).get("username").toString());
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    setTripAdapter();
                } else {
                    Toast.makeText(getActivity(), "Could not load the list of users", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }


    public void setTripAdapter()
    {
        viewReuestsAdapter adapter = new viewReuestsAdapter(getActivity(),
                R.layout.listview_request_item_row, listOfUsernames);


        listView1 = (ListView) getView().findViewById(R.id.listOfRequests);


        listView1.setAdapter(adapter);

        /////temp disbaled to test async method
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                requestedUsername = (String) parent.getAdapter().getItem(position);

                Intent intent = new Intent(getActivity().getApplicationContext(), popup.class);


                startActivity(intent);
            }
        });
    }


}

