package rubix.mobile.rubix_mobile;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by kantapon on 24/1/2561.
 */

public class Appconfig implements Serializable {
    String Serial;
    String CompanyCode;
    String CompanyName;
    String DBServerName;
    String DBName;
    String UserName;
    String Password;
    String ServiceURL;
    String CurrentUser;
    String URL_Data;
    Context context;
    boolean connect = false;
    //Setting data for authen
    public void setup(String ar){
        JSONObject tmp = null;
        try {
            tmp = new JSONObject(new JSONArray(ar).get(0).toString());
            Serial = tmp.getString("Serial");
            CompanyCode =tmp.getString("CompanyCode");
            CompanyName =tmp.getString("CompanyName");
            DBServerName =tmp.getString("DBServerName");
            DBName =tmp.getString("DBName");
            UserName =tmp.getString("UserName");
            Password =tmp.getString("Password");
            ServiceURL =tmp.getString("ServiceURL");
            URL_Data = "http://192.168.128.249/RUBIXLITEWeb/";

            connect = true;
        } catch (JSONException e) {

        }
    }
    public boolean checkstate(){
        if(Serial==null || CompanyCode==null || CompanyName==null || DBServerName==null || DBName==null
                || UserName==null || Password==null || ServiceURL==null || !connect )
            return false;
        else return true;
    }
    //set current user was login
    public void setCurrentUser(String user){
        CurrentUser = user;
    }
    //get current user
    public String getUser(){
        return CurrentUser;
    }
    //get URL
    public String getURL(){
        return URL_Data;
    }

    public void clear(){
        Serial ="";
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void clearstate() {
        Serial =null;
        CompanyCode=null;
        CompanyName=null;
        DBServerName=null;
        DBName=null;
        UserName=null;
        Password=null;
        ServiceURL=null;
        CurrentUser=null;
        URL_Data=null;
    }

}