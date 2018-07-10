package rubix.mobile.rubix_mobile.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import rubix.mobile.rubix_mobile.LoginActivity;
import rubix.mobile.rubix_mobile.R;
import rubix.mobile.rubix_mobile.StringEncryption;

public class LoginMainFragment extends Fragment {

    private Button mBtnLogin;
    private EditText mUsername;
    private EditText mPassword;
    private OnClickLogin mLogin;
    private ConnectivityManager cm;
    private Button mSettingConfig;
    private Dialog mDialogConfig;
    private String PassConfig = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_main, container, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //region Binding Data
        mUsername = (EditText) view.findViewById(R.id.editText_username);
        mPassword = (EditText) view.findViewById(R.id.editText_password);
        mBtnLogin = (Button) view.findViewById(R.id.btn_signin);
        mSettingConfig = (Button) view.findViewById(R.id.btn_set_config);
        //endregion
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {
            Toast.makeText(getActivity(), "Please connect internet", Toast.LENGTH_LONG).show();
        }

        //region Config
        mSettingConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogConfig = new Dialog(getActivity());
                mDialogConfig.setTitle("Setting Config");
                mDialogConfig.setContentView(R.layout.dialog_setting_config);
                mDialogConfig.show();
                PassConfig = getActivity().getResources().getString(R.string.config_pass);

                final EditText mAuthen = (EditText) mDialogConfig.findViewById(R.id.editText_authen);
                final EditText mComCode = (EditText) mDialogConfig.findViewById(R.id.editText_comcode);
                final EditText mPass = (EditText) mDialogConfig.findViewById(R.id.editText_password);
                final Button mBtnEdit = (Button) mDialogConfig.findViewById(R.id.btn_edit);
                final Button mBtnOk = (Button) mDialogConfig.findViewById(R.id.btn_ok);
                final Button mBtnSave = (Button) mDialogConfig.findViewById(R.id.btn_save);
                final Button mBtnCancel = (Button) mDialogConfig.findViewById(R.id.btn_cancel);
                final LinearLayout mBtnLayout = (LinearLayout) mDialogConfig.findViewById(R.id.layout_btn);
                mAuthen.setFocusable(false);
                mComCode.setFocusable(false);
                try {
                    FileInputStream fileIn= getActivity().openFileInput("config.xml");
                    InputStreamReader InputRead= new InputStreamReader(fileIn);
                    char[] inputBuffer= new char[150];
                    String s="";
                    int charRead;
                    while ((charRead=InputRead.read(inputBuffer))>0) {
                        String readstring=String.copyValueOf(inputBuffer,0,charRead);
                        s +=readstring;
                    }
                    InputRead.close();
                    mAuthen.setText(getXmlTag("WebAuthenUrl", s));
                    mComCode.setText(getXmlTag("CompanyCode", s));
                } catch (Exception e) {
                    mDialogConfig.dismiss();
                    ((LoginActivity) getActivity()).creatConfig();
                    mSettingConfig.callOnClick();
                }

                mBtnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBtnEdit.setVisibility(View.GONE);
                        mPass.setVisibility(View.VISIBLE);
                        mBtnOk.setVisibility(View.VISIBLE);
                        mPass.requestFocus();
                    }
                });

                mBtnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String encryp = "";
                        try {
                            encryp = new StringEncryption(mPass.getText().toString()).getString();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        if (encryp.equals(PassConfig)) {
                            mPass.getText().clear();
                            mPass.setVisibility(View.GONE);
                            mBtnOk.setVisibility(View.GONE);
                            mBtnLayout.setVisibility(View.VISIBLE);
                            mAuthen.setFocusableInTouchMode(true);
                            mAuthen.requestFocus();
                            mAuthen.setTextColor(Color.BLACK);
                            mComCode.setTextColor(Color.BLACK);
                            mAuthen.setFocusable(true);
                            mComCode.setFocusableInTouchMode(true);
                            mComCode.setFocusable(true);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Password is wrong!");
                            builder.setMessage("Please try again.");
                            builder.show();
                            mDialogConfig.cancel();
                        }
                    }
                });

                mBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogConfig.cancel();
                    }
                });

                mBtnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String xmlFile = "config.xml";
                        try {
                            FileOutputStream fileos = getActivity().openFileOutput(xmlFile, Context.MODE_PRIVATE);
                            OutputStreamWriter outputWriter = new OutputStreamWriter(fileos);
                            XmlSerializer xmlSerializer = Xml.newSerializer();
                            StringWriter writer = new StringWriter();
                            xmlSerializer.setOutput(writer);
                            xmlSerializer.startDocument("UTF-8", true);
                            xmlSerializer.startTag(null, "WebAuthenUrl");
                            xmlSerializer.text(mAuthen.getText().toString().trim());
                            xmlSerializer.endTag(null, "WebAuthenUrl");
                            xmlSerializer.startTag(null,"CompanyCode");
                            xmlSerializer.text(mComCode.getText().toString().trim());
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
                        mDialogConfig.cancel();
                        new AlertDialog.Builder(getActivity()).setTitle("Setting").setNegativeButton("OK",null).setMessage("Setting complete").show();
                        ((LoginActivity) getActivity()).loadConfig();
                    }
                });
            }
        });
        //endregion

        //region Button Login
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mBtnLogin.setEnabled(false);
                    String b = new StringEncryption(mPassword.getText().toString()).getString();
                    mLogin.Login( mUsername.getText().toString(),b);
                    mBtnLogin.setEnabled(true);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        //endregion
        return view;
    }
    public interface OnClickLogin {
        public void Login(String user,String pass);
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLogin = (OnClickLogin) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnClickLogin");
        }
    }

    public void wrong(){
        mPassword.setText("");
    }

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

}
