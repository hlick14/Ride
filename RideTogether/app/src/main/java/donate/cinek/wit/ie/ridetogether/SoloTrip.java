package donate.cinek.wit.ie.ridetogether;

import android.graphics.Bitmap;

/**
 * Created by Cinek on 26/02/2015.
 */
public class SoloTrip {

        public Bitmap icon;
        public String title;
        public String startCity,endCity,tDate,tTime,tId;

        public SoloTrip(){
            super();
        }

        public SoloTrip(String tId,Bitmap icon, String title,String startCity,String endCity,String tDate,String tTime) {
            super();
            this.tId=tId;
            this.icon = icon;
            this.title = title;
            this.startCity=startCity;
            this.endCity=endCity;
            this.tDate=tDate;
            this.tTime=tTime;
        }

        public String getTitle () { return title;}
    public String getId(){return tId;}
    public Bitmap getIcon () { return icon;}
    public String getStartCity () { return startCity;}
    public String getEndCity () { return endCity;}
    public String getTripDate () { return tDate;}
    public String getTripTime () { return tTime;}
    public void set (Bitmap icon) {this.icon = icon;}


    }

