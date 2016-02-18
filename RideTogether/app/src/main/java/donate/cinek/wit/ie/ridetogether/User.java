package donate.cinek.wit.ie.ridetogether;

import android.graphics.Bitmap;

/**
 * Created by Cinek on 11/03/2015.
 */
public class User {

    public Bitmap icon;

    public String name,username,Motorbike,UserSince;

    public User(){
        super();
    }

    public User(Bitmap icon, String name,String username,String Motorbike,String UserSince) {
        super();
        this.icon = icon;
        this.name = name;
        this.username = username;
        this.Motorbike = Motorbike;
        this.UserSince = UserSince;
    }

    public String getName () { return name;}
    public Bitmap getIcon () { return icon;}
    public String getUsername () { return username;}
    public String getMotorbike () { return Motorbike;}
    public String getUserSince () { return UserSince;}


}
