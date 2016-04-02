package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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


    public UploadImage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b= (Button) getActivity().findViewById(R.id.UploadButton);
        b.setOnClickListener(this);

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
                Map config = new HashMap();
                config.put("cloud_name", "dfhdu6ytp");
                config.put("api_key", "948126291598285");
                config.put("api_secret", "a-Hh58yVNi6bXfeBrArVhZ1HD6I");
                Cloudinary cloudinary = new Cloudinary(config);
                try {
                    cloudinary.uploader().upload(photoFile.getAbsolutePath(), ObjectUtils.emptyMap());
                } catch (IOException e) {
                    e.printStackTrace();
                }

//            } else if (resultCode == RESULT_CANCELED) {
//                // User cancelled the image capture
//                getActivity().finish();
//            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "capturedImage";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return image;
    }
}



