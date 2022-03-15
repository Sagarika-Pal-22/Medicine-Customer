package myrehabcare.in.JSB;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

public class JsbUi extends JsbValidations {

    Activity activity;

    public JsbUi(Activity activity){
        super(activity);
        this.activity = activity;
    }

    public ProgressDialog getProgressDialog(){
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("");
        progressDialog.setMessage("Please Wait...");
        return progressDialog;
    }

    public ProgressDialog getProgressDialog(String title, String message){
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        return progressDialog;
    }


    public void toastShort(String message){
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    public void toastLong(String message){
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

}
