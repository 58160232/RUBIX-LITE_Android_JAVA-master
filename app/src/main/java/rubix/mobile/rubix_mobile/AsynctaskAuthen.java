package rubix.mobile.rubix_mobile;

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
import java.net.HttpURLConnection;

public class AsynctaskAuthen extends AsyncTask<String, Void, Object> {
    public String Parameter = "";
    public int CheckWork;
    public Appconfig config = new Appconfig();
    Context context;
    public AsynctaskAuthen(Object para) {
        Parameter = para.toString();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    protected String doInBackground(String... urls) {
        String result = "";
        try {
            HttpPost httppost =  new HttpPost(urls[0]);
            HttpURLConnection av =  (HttpURLConnection)httppost.getURI().toURL().openConnection();
            int ch = av.getResponseCode();
            if(httppost.getURI().isAbsolute() && av.getResponseCode()!= 404){
                final HttpClient client = new DefaultHttpClient();
                httppost.setEntity(new StringEntity(Parameter.toString(), "UTF-8"));
                HttpResponse response;
                response = client.execute(httppost);
                int statusCode = response.getStatusLine().getStatusCode();
                CheckWork = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                }
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        if(!result.equals(""))config.setup(result);
        return result;
    }
    protected void onPostExecute(Object jsonString) {
    }
}