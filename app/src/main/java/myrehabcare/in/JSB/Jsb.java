package myrehabcare.in.JSB;

import android.app.Activity;

public class Jsb extends JsbCode {

    Activity activity;

    public Jsb(Activity activity){
        super(activity);
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
