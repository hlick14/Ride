package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.cloudinary.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kuba Pieczonka on 02/04/2016.
 */
public class UploadImage extends android.support.v4.app.Fragment implements View.OnClickListener {

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
    private Button accept,deny,b;
    static final int REQUEST_TAKE_PHOTO = 1;
    Cloudinary cloudinary;
    JSONObject Result;
    String file_path;
    File file;
    Map res = new HashMap();
    ArrayList<String> listOfUrls = new ArrayList<String>();
    ArrayList<Drawable> listOfPhotos = new ArrayList<>();
    ListView listViewPics;
    private static ArrayList<DataModel> data;

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    static View.OnClickListener myOnClickListener;
    private static ArrayList<Integer> removedItems;
    public UploadImage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map config = new HashMap();
        config.put("cloud_name", "dfhdu6ytp");
        config.put("api_key", "948126291598285");
        config.put("api_secret", "a-Hh58yVNi6bXfeBrArVhZ1HD6I");
        cloudinary = new Cloudinary(config);




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
        fragmentView = inflater.inflate(R.layout.upload_image, container, false);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        b= (Button) fragmentView.findViewById(R.id.UploadButton);
        b.setOnClickListener(this);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("BikeOfTheDay");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> links, ParseException e) {

                if (links != null) {
                    for (int i = 0; i < links.size();i++)
                    {
                        listOfUrls.add(links.get(i).get("PictureLink").toString());
                    }


                }
                Fetch task2 = new Fetch( cloudinary );
                task2.execute();
            }
        });
        return fragmentView;
    }



    File photoFile;

    @Override
    public void onClick(View v) {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO) {
//            if (resultCode == RESULT_OK) {
                //File to upload to cloudinary

                Upload task = new Upload( cloudinary );
                task.execute();

        }
    }

    private class Upload extends AsyncTask<String, Void, String> {
        private Cloudinary mCloudinary;


        public Upload( Cloudinary cloudinary ) {
            super();
            mCloudinary = cloudinary;
        }
        @Override
        protected String doInBackground(String... urls) {
            String response = "";

            try {
                res = mCloudinary.uploader().upload(photoFile.getAbsolutePath(), ObjectUtils.emptyMap());
                ParseObject BikeIdUpload = new ParseObject("BikeOfTheDay");
                BikeIdUpload.put("PictureLink", res.get("url"));
                try {
                    BikeIdUpload.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), "Uploaded"+ res.toString(), Toast.LENGTH_LONG).show();
            Log.v("UPLOAD", res.toString());
        }
    }
    private class Fetch extends AsyncTask<String, Void, String> {
        private Cloudinary fCloudinary;


        public Fetch( Cloudinary cloudinary ) {
            super();
            fCloudinary = cloudinary;
        }
        @Override
        protected String doInBackground(String... urls) {
            String response = "";



            for (int i = 0 ; i < listOfUrls.size() ; i++)
            {
//                listOfPhotos.add(cloudinary.url().type("fetch").format("jpg").imageTag(listOfUrls.get(i).toString()));
                try {
                    InputStream is = (InputStream) new URL(listOfUrls.get(i)).getContent();
                    Drawable d = Drawable.createFromStream(is, "src name");
                    listOfPhotos.add(i,d);
                } catch (Exception e) {

                }

            }

            Log.v("FETCH RETURN " , listOfPhotos.toString());

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), "Uploaded"+ res.toString(), Toast.LENGTH_LONG).show();


//            viewReuestsAdapter adapter = new viewReuestsAdapter(getActivity(),
//                    R.layout.listview_bike_of_the_day_item_row, listOfPhotos);


//            listViewPics = (ListView) getView().findViewById(R.id.listViewPics);
//
//
//
//            listViewPics.setAdapter(adapter);

            String[] nameArray = {"Cupcake", "Donut"};
            String[] versionArray = {"1.5", "1.6"};
            int [] id_ = {1,2};
            data = new ArrayList<DataModel>();
            for (int i = 0; i < listOfPhotos.size(); i++) {
                data.add(new DataModel(
                        nameArray[i],
                        versionArray[i],
                        id_[i],
                        listOfPhotos.get(i)
                ));
            }
            adapter = new CustomAdapter(data);
            recyclerView.setAdapter(adapter);

        }
    }

    private File createImageFile() throws IOException {


        String imageFileName = "capturedImage";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );


        return image;
    }
}



