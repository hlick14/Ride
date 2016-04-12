package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
        builder2.setTitle("     Select source");
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
                        takePhotoIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
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
                    intent2.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
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


        } else if (requestCode == PICK_IMAGE) {
            final Uri uri = data.getData();

            Log.v("Show me ", uri.toString());
            imageLoader.loadImage(uri.toString(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Do whatever you want with Bitmap
                    Bitmap bmp = imageLoader.loadImageSync(uri.toString());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.JPEG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    try {
                        FileOutputStream fos = new FileOutputStream(photoFile);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
//            Bitmap bmp = imageLoader.loadImageSync(uri.toString());
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//            bmp.compress(Bitmap.CompressFormat.JPEG, 0, bos);
//                byte[] bitmapdata = bos.toByteArray();
//            try {
//                FileOutputStream fos = new FileOutputStream(photoFile);
//                fos.write(bitmapdata);
//                fos.flush();
//                fos.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 4;
//                AssetFileDescriptor fileDescriptor =null;
//                fileDescriptor =
//                       getActivity().getContentResolver().openAssetFileDescriptor( uri, "r");
//                Bitmap actuallyUsableBitmap
//                        = BitmapFactory.decodeFileDescriptor(
//                        fileDescriptor.getFileDescriptor(), null, options);
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                actuallyUsableBitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos);
//                byte[] bitmapdata = bos.toByteArray();
//                FileOutputStream fos = new FileOutputStream(photoFile);
//                fos.write(bitmapdata);
//                fos.flush();
//                fos.close();
//                if(actuallyUsableBitmap !=null) {
//                    actuallyUsableBitmap.recycle();
//                }
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
            getUrls();
            Fetch task3 = new Fetch( cloudinary );
            task3.execute();

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
//            if(!listOfPhotos.isEmpty()) {
//                listOfPhotos.clear();
//                listOfDescriptions.clear();
//                getUrls();
//            }
             final int BUFFER_IO_SIZE = 8000;
            BufferedOutputStream out;
            Bitmap bitmap = null;
            for (int i = 0 ; i < listOfUrls.size() ; i++)
            {

//                    BufferedInputStream in;
//                    InputStream is = (InputStream) new URL(listOfUrls.get(i)).getContent();
//                    in = new BufferedInputStream(new URL(listOfUrls.get(i))
//                            .openStream(),
//                            BUFFER_IO_SIZE);
//                    final ByteArrayOutputStream dataStream = new
//                            ByteArrayOutputStream();
//                    out = new BufferedOutputStream(dataStream, BUFFER_IO_SIZE);
//                    copy(in, out);
//                    out.flush();
//
//                    final byte[] data = dataStream.toByteArray();
//                     bitmap = BitmapFactory.decodeByteArray(data, 0,
//                            data.length);
//                    Drawable d = Drawable.createFromStream(is, "src name");
                imageLoader.loadImage(listOfUrls.get(i), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        // Do whatever you want with Bitmap
//                            Bitmap bmp = imageLoader.loadImageSync(uri.toString());
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();

                        loadedImage.compress(Bitmap.CompressFormat.JPEG, 25, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        try {
                            FileOutputStream fos = new FileOutputStream(photoFile);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        listOfPhotos.add(loadedImage);

                    }
                });



//                    Picasso.with(getActivity().getApplicationContext()).load("http://i.imgur.com/DvpvklR.png").into(listOfPhotos.add(i,));


            }
//            out = null;
            return response;
        }
        private void copy(final InputStream bis, final OutputStream baos) throws IOException {
            byte[] buf = new byte[256];
            int l;
            while ((l = bis.read(buf)) >= 0) baos.write(buf, 0, l);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.v("Ride-Upload", result);

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
                        votes.add((Integer) links.get(i).get("votes"));

                    }


                }
                Fetch task2 = new Fetch( cloudinary );
                task2.execute();
            }
        });
    }



}



