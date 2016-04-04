package donate.cinek.wit.ie.ridetogether;

import android.graphics.drawable.Drawable;

/**
 * Created by Kuba Pieczonka on 02/04/2016.
 */
public class DataModel {

    String name;
//    String version;
//    int id_;
    Drawable image;

    public DataModel(String name,Drawable image) {
        this.name = name;
//        this.version = version;
//        this.id_ = id_;
        this.image=image;
    }

    public String getName() {
        return name;
    }
//
//    public String getVersion() {
//        return version;
//    }

    public Drawable getImage() {
        return image;
    }
//
//    public int getId() {
//        return id_;
//    }
}