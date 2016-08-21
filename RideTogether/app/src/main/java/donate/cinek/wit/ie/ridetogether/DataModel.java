package donate.cinek.wit.ie.ridetogether;

import android.graphics.Bitmap;

/**
 * Created by Kuba Pieczonka on 02/04/2016.
 */
public class DataModel {

    String name;
    Bitmap image;
    int votes;

    public DataModel(String name, Bitmap image, int votes) {
        this.name = name;
        this.votes = votes;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }

    public int setVotes(int votes) {
        this.votes= votes ;
        return votes;
    }

    public Bitmap getImage() {
        return image;
    }
}
