package donate.cinek.wit.ie.ridetogether;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "ZF2HXBa3ZRMkMGo008OETwhfiCf3dKNYOzyQ19iA", "RBQNfQvWrDDFG8pWFIdUahrYGZ2oh6lHZ4v85KC1");
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

    }
}
