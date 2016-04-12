package donate.cinek.wit.ie.ridetogether;

import android.graphics.Bitmap;

/**
 * Created by Kuba Pieczonka on 02/04/2016.
 */
public class DataModel {

    String name;
//    String version;
//    int id_;
    Bitmap image;
    int votes;

    public DataModel(String name,Bitmap image,int votes) {
        this.name = name;
//        this.version = version;
//        this.id_ = id_;
        this.votes = votes;
        this.image=image;
    }

    public String getName() {
        return name;
    }
//
//    public String getVersion() {
//        return version;
//    }
public int getVotes() {
    return votes;
}
    public Bitmap getImage() {
        return image;
    }
//
//    public int getId() {
//        return id_;
//    }
}