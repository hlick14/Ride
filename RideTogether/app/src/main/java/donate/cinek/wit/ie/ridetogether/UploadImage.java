package donate.cinek.wit.ie.ridetogether;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    String name;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int SELECT_PHOTO = 1;

    Cloudinary cloudinary;
    File file;
    Map res = new HashMap();
    ArrayList<String> listOfUrls = new ArrayList<String>();
    ArrayList<String> listOfDescriptions = new ArrayList<String>();
    ArrayList<Drawable> listOfPhotos = new ArrayList<>();
    private static ArrayList<DataModel> data;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;

    File photoFile;
    private static final int PICK_IMAGE = 2;
    private String m_Text = "";
    int votesNumber = 0;
    TextView votes;
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
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }

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

//        b= (Button) fragmentView.findViewById(R.id.UploadButton);
//        b.setOnClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fabBike);
        fab.setOnClickListener(this);
        getUrls();
        return fragmentView;
    }




    @Override
    public void onClick(View v) {

        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
        builder2.setTitle("     Select source");

// Set up the input
//        final TextView input = new TextView(getActivity());
//// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setText("Would you like to take a picture or upload from storage ?");
//        builder2.setView(input);

// Set up the buttons
        builder2.setPositiveButton("Take a picture", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
        });
        builder2.setNegativeButton("Select from storage", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Intent intent2 = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent2, "Select Picture"), PICK_IMAGE);
                }

            }
        });

        builder2.show();





    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO) {
//            if (resultCode == RESULT_OK) {
                //File to upload to cloudinary
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("The bike");

// Set up the input
            final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    Upload task = new Upload(cloudinary );
                    task.execute();

                    Fetch task3 = new Fetch( cloudinary );
                    task3.execute();


                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();


        } else if (requestCode == PICK_IMAGE) {
            final Uri uri = data.getData();
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;

                AssetFileDescriptor fileDescriptor =null;
                fileDescriptor =
                       getActivity().getContentResolver().openAssetFileDescriptor( uri, "r");
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                Bitmap actuallyUsableBitmap
                        = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);

//Convert bitmap to byte array

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                actuallyUsableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                if(bitmap.getByteCount() < 10000000) {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
//                }
//                else if (bitmap.getByteCount() < 15000000)
//                {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, bos);
//                }
//                else
//                {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                }
                byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                FileOutputStream fos = new FileOutputStream(photoFile);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("The bike");

// Set up the input
            final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    Upload task = new Upload(cloudinary );
                    task.execute();

                    Fetch task3 = new Fetch( cloudinary );
                    task3.execute();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

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
                BikeIdUpload.put("Description",m_Text);
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

            Log.v("UPLOAD", res.toString());
            listOfPhotos.clear();
            adapter.notifyDataSetChanged();



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



            for (int i = 0 ; i <= listOfUrls.size() ; i++)
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


//            String[] nameArray = {"Cupcake", "Donut","lolo"};
//            String[] versionArray = {"1.5", "1.6","3.2"};
//            int [] id_ = {1,2,2};
            data = new ArrayList<DataModel>();
            for (int i = 0; i < listOfPhotos.size(); i++) {
                data.add(new DataModel(
                        listOfDescriptions.get(i),
//                        versionArray[i],
//                        id_[i],
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
    public void getUrls()
    {
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("BikeOfTheDay");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> links, ParseException e) {

                if (links != null) {
                    for (int i = 0; i < links.size();i++)
                    {
                        listOfUrls.add(links.get(i).get("PictureLink").toString());
                        listOfDescriptions.add(links.get(i).get("Description").toString());
                    }


                }
                Fetch task2 = new Fetch( cloudinary );
                task2.execute();
            }
        });
    }
}



