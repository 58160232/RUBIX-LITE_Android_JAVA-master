package rubix.mobile.rubix_mobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rubix.mobile.rubix_mobile.Fragment.LoginMainFragment;
import rubix.mobile.rubix_mobile.Fragment.LoginQRFragment;

public class LoginActivity extends AppCompatActivity implements LoginMainFragment.OnClickLogin, LoginQRFragment.QRlogin {
    String URL = "";
    String CompanyCode = "";
    FragmentTransaction transaction;
    AsynctaskAuthen Init;
    Fragment temp;
    ConnectivityManager cm;
    int Frag;
    private AlertDialog.Builder alert, internet;
    //region Bottom Navigation
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_login:
                    if (Frag != 0) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentLogin, new LoginMainFragment()).commit();
                        Frag = 0;
                    }
                    return true;
                case R.id.navigation_qr_login:
                    if (Frag != 1) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentLogin, new LoginQRFragment()).commit();
                        Frag = 1;
                    }
                    return true;
            }
            return false;
        }
    };

    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadConfig();
        //default fragment from start app
        if (savedInstanceState == null) {
            Frag = 0;
            temp = new LoginMainFragment();
            transaction = getSupportFragmentManager().beginTransaction().replace(R.id.FragmentLogin, temp);
            transaction.commit();
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        //Check internet
        if (null == activeNetwork) {
            Toast.makeText(getApplicationContext(), "Please connect internet", Toast.LENGTH_LONG).show();
        }
    }

    //region get content from XML TAG
    public static String getXmlTag(String tag, String content) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        String xml = "";
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(content));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && tag.equals(xpp.getName())) {
                eventType = xpp.next();
                xml = xpp.getText();
            }
            eventType = xpp.next();
        }
        return xml;
    }

    //endregion

    //region Login by UserID and Password
    @Override
    public void Login(String user, String pass) {
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        LoginMainFragment clearuser = (LoginMainFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentLogin);
        if (null != activeNetwork) {
            if (!Init.config.checkstate()) loadConfig();
            String checkLogin = "";
            try {
                JSONObject job = new JSONObject();
                job.accumulate("Username", user);
                job.accumulate("Password", pass);
                if (!Init.config.checkstate()) {
                    Toast.makeText(this,"please check setting",Toast.LENGTH_SHORT).show();
                    return;
                }
                Init.config.setContext(this);
                AsyncTaskAdapter sr = new AsyncTaskAdapter(job, Init.config);
                sr.execute("api/UserMaintenance/VerifyUser");
                checkLogin = sr.get(10000, TimeUnit.MILLISECONDS).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(this, "Please check internet", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkLogin.equals("true")) {
                Init.config.setCurrentUser(user);
                Intent i_main = new Intent(this, MainActivity.class);
                Init.config.context = null;
                i_main.putExtra("Appconfig", (Serializable) Init.config);
                startActivity(i_main);
                this.finish();
            } else if (checkLogin.equals("false")) {
                alert = new AlertDialog.Builder(this);
                alert.setMessage("Invalid Username or Password!").setNegativeButton("OK", null).show();
                clearuser.wrong();
                clearuser.onDestroy();
                alert = null;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please connect internet", Toast.LENGTH_LONG).show();
            //clearuser.wrong();
        }
    }

    //endregion

    //region Login by QRCode
    @Override
    public void LoginQR(String QR) {
        final LoginQRFragment clearuser = (LoginQRFragment) getSupportFragmentManager().findFragmentById(R.id.FragmentLogin);

        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if (!Init.config.checkstate()) loadConfig();
            String checkLogin = "";
            AsyncTaskAdapter SR = null;
            try {
                JSONObject job = new JSONObject();
                job.accumulate("UserCode", QR);
                if (!Init.config.checkstate()) {
                    Toast.makeText(this,"please check setting",Toast.LENGTH_SHORT).show();
                    return;
                }
                Init.config.setContext(this);
                SR = new AsyncTaskAdapter(job, Init.config);
                SR.execute("api/UserMaintenance/VerifyUser");
                checkLogin = SR.get(10000, TimeUnit.MILLISECONDS).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(this, "Please check internet", Toast.LENGTH_SHORT).show();
                return;
            }
            if (checkLogin.equals("true")) {
                SR = new AsyncTaskAdapter(new Gson().toJson(QR.toString()), Init.config);
                SR.execute("api/UserMaintenance/LoadUserLogin");
                try {
                    String QR_user = new JSONObject(new JSONArray(SR.get(10000, TimeUnit.MILLISECONDS).toString()).get(0).toString()).getString("UserLogin");
                    Init.config.setCurrentUser(QR_user);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    Toast.makeText(this, "Please check internet", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent i_main = new Intent(this, MainActivity.class);
                Init.config.setContext(null);
                i_main.putExtra("Appconfig", (Serializable) Init.config);
                startActivity(i_main);
                this.finish();
            } else {
                if (alert == null) {
                    alert = new AlertDialog.Builder(this);
                    alert.setMessage("Invalid UserCode!").setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearuser.wrong();
                            alert = null;
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            clearuser.wrong();
                            alert = null;
                        }
                    }).show();
                }
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.FragmentLogin, new LoginMainFragment()).commit();
            Frag = 0;
            Toast.makeText(getApplicationContext(), "Please connect internet", Toast.LENGTH_LONG).show();
        }
    }

    //endregion

    //region loadconfig from config.txt
    public void loadConfig() {
        try {
            FileInputStream fileIn = openFileInput("config.xml");
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[150];
            String s = "";
            int charRead;
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
            }
            InputRead.close();
            URL = getXmlTag("WebAuthenUrl", s);
            CompanyCode = getXmlTag("CompanyCode", s);
            Init = new AsynctaskAuthen(new Gson().toJson(CompanyCode));
            Init.execute(URL + "api/Registration/LoadDatabaseConfigByCompanyCode");
            Init.get(3, TimeUnit.SECONDS);

        } catch (IOException e) {
            creatConfig();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(this, "Please check internet", Toast.LENGTH_SHORT).show();
        }
    }

    public void creatConfig() {
        final String xmlFile = "config.xml";
        try {
            FileOutputStream fileos = openFileOutput(xmlFile, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileos);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "WebAuthenUrl");
            xmlSerializer.text("http://13.230.71.205/RUBIXLITEAuthentication/");
            xmlSerializer.endTag(null, "WebAuthenUrl");
            xmlSerializer.startTag(null, "CompanyCode");
            xmlSerializer.text("RUBIX-LITE");
            xmlSerializer.endTag(null, "CompanyCode");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            outputWriter.write(writer.toString());
            outputWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //endregion
}
