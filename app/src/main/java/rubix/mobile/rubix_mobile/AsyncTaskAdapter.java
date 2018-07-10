package rubix.mobile.rubix_mobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by kantapon on 11/1/2561.
 */

public class AsyncTaskAdapter extends AsyncTask<String, Void, Object> {
    public String Parameter = "";
    public int CheckWork;
    private Context context;
    public Appconfig config = new Appconfig();
    private ProgressDialog progress;


    public AsyncTaskAdapter(Object para, Appconfig c) {
        Parameter = para.toString();
        config = c;
        context = config.context;
    }

    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(this.context);
        progress.setTitle("Please Wait!!");
        progress.setMessage("Wait for loading...");
        progress.setCancelable(false);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        super.onPreExecute();
    }

    protected String doInBackground(String... urls) {
        String result = "false";
        try {
            HttpPost httppost = new HttpPost(config.ServiceURL + urls[0]);
            final HttpClient client = new DefaultHttpClient();
            httppost.setEntity(new StringEntity(Parameter.toString(), "UTF-8"));
            HttpResponse response;
            httppost.addHeader("DATABASE_SERVER_NAME", config.DBServerName);
            httppost.addHeader("DATABASE_NAME", config.DBName);
            httppost.addHeader("DATABASE_USER_NAME", config.UserName);
            httppost.addHeader("DATABASE_PASSWORD", config.Password);
            response = client.execute(httppost);
            int statusCode = response.getStatusLine().getStatusCode();
            CheckWork = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                result = "";
                InputStream inputStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader
                        (new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    result += line;
                }
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return result;

    }

    protected void onPostExecute(Object jsonString) {
//        super.onPostExecute(jsonString);
        progress.dismiss();
    }

}