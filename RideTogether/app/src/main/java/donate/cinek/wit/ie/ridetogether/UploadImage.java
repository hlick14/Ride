package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
    Cloudinary cloudinary;
    File file;
    Map res = new HashMap();
    ArrayList<String> listOfUrls = new ArrayList<String>();
    ArrayList<String> listOfDescriptions = new ArrayList<String>();
    ArrayList<Bitmap> listOfPhotos = new ArrayList<>();
    ArrayList<String> votedBy = new ArrayList<>();
    ArrayList<Integer> votes = new ArrayList<Integer>();

    private static ArrayList<DataModel> data;
    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    File photoFile;
    private static final int PICK_IMAGE = 2;
    private String m_Text = "";
    ImageLoader imageLoader;

    ProgressDialog progdialog;
    int viewWidth;
    protected Bitmap snypImageScaled;
    protected byte[] scaledData;
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
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));




    }
    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }

    }
    @Override
    public void onStop(){
        super.onStop();
//        Toast.makeText(getActivity(), "This is happening on activity exit ", Toast.LENGTH_SHORT).show();
//        name = update.getEditText().getText().toString().trim();
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
//
//        query.getInBackground(sId, new GetCallback<ParseObject>() {
//
//
//            public void done(ParseObject cos, com.parse.ParseException e) {
//                if (e == null) {
//
//                    cos.put("TripName", name);
//
//                    cos.saveInBackground();
//
//                } else {
//                    Log.d("score", "Error: " + e.getMessage());
//                }
//            }
//        });
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
        viewWidth = fragmentView.getWidth();
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        progdialog = new ProgressDialog(getActivity(), 1);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());




        FloatingActionButton fab = (FloatingActionButton) fragmentView.findViewById(R.id.fabBike);
        fab.setOnClickListener(this);
        getUrls();

        return fragmentView;
    }




    @Override
    public void onClick(View v) {

        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity(),R.style.AlertDialogCustom);
        builder2.setTitle("Select source");
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
                        takePhotoIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
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
                    intent2.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
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

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogCustom);
            builder.setTitle("The bike");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    Upload task = new Upload(cloudinary );
                    task.execute();

                    progdialog.setMessage("Retrieving Data");
                    progdialog.setCancelable(false);
                    progdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progdialog.show();



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

            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                snypImageScaled = Bitmap.createScaledBitmap(bitmap, 1200, 980
                        * bitmap.getHeight() / bitmap.getWidth(), false);
                // Convert it to byte
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                snypImageScaled.compress(Bitmap.CompressFormat.PNG, 100, bos);

                scaledData = bos.toByteArray();
                FileOutputStream fos = new FileOutputStream(photoFile);
                fos.write(scaledData);
                fos.flush();
                fos.close();
                snypImageScaled.recycle();


//                photoFile.setImageBitmap(snypImageScaled);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


//
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("The bike");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    Upload task = new Upload(cloudinary );
                    task.execute();

                    progdialog.setMessage("Retrieving Data");
                    progdialog.setCancelable(false);
                    progdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progdialog.show();


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
                BikeIdUpload.put("votes",0);
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

//            data.clear();
//            listOfPhotos.clear();
//            listOfDescriptions.clear();
            Log.v("Ride-Upload", result);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    getUrls();
                    Fetch task3 = new Fetch( cloudinary );
                    task3.execute();
                }
            }, 2500);


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


            listOfPhotos.clear();

            for (int i = 0 ; i <= listOfUrls.size() ; i++)
            {
//                listOfPhotos.add(cloudinary.url().type("fetch").format("jpg").imageTag(listOfUrls.get(i).toString()));
                try {
                    InputStream is = (InputStream) new URL(listOfUrls.get(i)).getContent();

//                    BitmapFactory.decodeStream(is1, null, options);

//                    decodeSampledBitmapFromResourceMemOpt(is,200,200);

                    listOfPhotos.add(i,decodeSampledBitmapFromResourceMemOpt(is,250,300));
//                    Log.d("Upload", " The size of list of Photes is "
//                            + listOfDescriptions.get(i));
//                    Log.d("Upload", " The size of list of URLS is "
//                            + listOfUrls.get(i));


                } catch (Exception e) {

                }

            }



            return response;
        }

        @Override
        protected void onPostExecute(String result) {


            if(adapter!=null)
            {
                adapter.notifyDataSetChanged();
            }
            data = new ArrayList<DataModel>();
            for (int i = 0; i <  listOfUrls.size(); i++) {
                data.add(new DataModel(
                        listOfDescriptions.get(i),
                        listOfPhotos.get(i),
                        votes.get(i)
                ));
            }

            adapter = new CustomAdapter(data);
            recyclerView.setAdapter(adapter);
            if(progdialog.isShowing())
            {
                progdialog.dismiss();
            }

        }
    }
    /////////////////////////////////////////// Image decodeing suggested by Binh Tran available at: http://stackoverflow.com/questions/15254272/bitmapfactory-decodestream-out-of-memory-despite-using-reduced-sample-size

    public Bitmap decodeSampledBitmapFromResourceMemOpt(
            InputStream inputStream, int reqWidth, int reqHeight) {

        byte[] byteArr = new byte[0];
        byte[] buffer = new byte[1024];
        int len;
        int count = 0;

        try {
            while ((len = inputStream.read(buffer)) > -1) {
                if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            int[] pids = { android.os.Process.myPid() };


            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }



    //Image file creation uproach suggested by Mohtashim availble at http://stackoverflow.com/questions/33624522/image-upload-to-cloudinary-from-android-camera


    private File createImageFile() throws IOException {


        String imageFileName = "capturedImage";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".png",
                storageDir
        );


        return image;
    }
    public void getUrls()
    {
        if (listOfUrls!=null)
        {
            listOfUrls.clear();
            listOfDescriptions.clear();
        }
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("BikeOfTheDay");
        query.orderByDescending("votes");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> links, ParseException e) {

                if (links != null) {
                    for (int i = 0; i < links.size();i++)
                    {
                        listOfUrls.add(links.get(i).get("PictureLink").toString());
                        listOfDescriptions.add(links.get(i).get("Description").toString());
                        votes.add((Integer) links.get(i).get("votes"));

                    }


                }
                Fetch task2 = new Fetch( cloudinary );
                task2.execute();
            }
        });
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }


        return inSampleSize;
    }
}

