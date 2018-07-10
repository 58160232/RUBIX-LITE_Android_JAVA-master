package rubix.mobile.rubix_mobile.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.AsyncTaskAdapter;
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;


public class MenuCountingFragment extends Fragment {

    //region Variable
    private EditText qty;
    private EditText lot;
    private EditText location;
    private EditText txtbarcode;
    private Button btncamera;
    private Button btncameralo;
    private Button btnscan;
    private Button btncancel;
    private Button btnconfirm;
    private TextView Itemtitle;
    private LinearLayout detail;
    private AsyncTaskAdapter s;
    private JSONObject Counting;
    Appconfig appconfig;
    boolean ch = false;
    //endregion

    public MenuCountingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();
        if (getArguments().getString("Barcode") != null) {
            txtbarcode.setText(getArguments().getString("Barcode"));
            btnscan.callOnClick();
            getArguments().remove("Barcode");
        }
        if(getArguments().getString("Location") !=null){
            btnscan.callOnClick();
            location.setText(getArguments().getString("Location"));
            getArguments().remove("Barcode");
            btnconfirm.callOnClick();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_counting, container, false);
        //region Bind Data
        MainActivity.FragmentName ="Counting";
        btncamera = (Button) view.findViewById(R.id.btn_counting_camerab);
        btnscan = (Button) view.findViewById(R.id.btn_counting_search);
        btncancel = (Button) view.findViewById(R.id.btn_counting_cancel);
        btnconfirm = (Button) view.findViewById(R.id.btn_counting_confirm);
        txtbarcode = (EditText) view.findViewById(R.id.txt_counting_sticker);
        Itemtitle = (TextView) view.findViewById(R.id.txt_counting_detail);
        btncameralo = (Button) view.findViewById(R.id.btn_counting_cameral);
        if(appconfig==null)appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();
        qty = (EditText) view.findViewById(R.id.txt_counting_qty);
        lot = (EditText) view.findViewById(R.id.txt_counting_lot);
        location = (EditText) view.findViewById(R.id.txt_counting_location);
        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable ss) {
                String s = ss.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    location.setText(s);
                    location.setSelection(s.length());
                }
            }
        });
        detail = (LinearLayout) view.findViewById(R.id.Layout_Detail_Counting);
        btncancel.setEnabled(false);
        btncancel.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
        btnconfirm.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
        btnconfirm.setEnabled(false);
        //endregion
        detail.setVisibility(LinearLayout.INVISIBLE);
        //region edittion qty
        qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    if (Double.parseDouble(qty.getText().toString()) == 0) {
                        qty.getText().clear();
                    }
                } else {
                    if (qty.getText().toString().equals("") || Double.parseDouble(qty.getText().toString()) == 0) {
                        qty.setText("0.0");
                    }
                }
            }
        });
        qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (qty.getText().toString().equals(".")) {
                    qty.setText("0.");
                    qty.setSelection(qty.getText().length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion
        //region Button Check Sticker Barcode
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).checkinternet()) {
                    s = new AsyncTaskAdapter(new Gson().toJson(txtbarcode.getText().toString().trim()), appconfig);
                    s.execute("api/Common/CheckExistSticker");
                    try {
                        String g = s.get(10000,TimeUnit.MILLISECONDS).toString();
                        if (g.equals("true")) {
                            s = new AsyncTaskAdapter(new Gson().toJson(txtbarcode.getText().toString()), appconfig);
                            s.execute("api/Common/LoadStickerDetail");
                            g = s.get(10000,TimeUnit.MILLISECONDS).toString();
                            JSONObject json = new JSONObject(new JSONArray(g).get(0).toString());
                            if (json.getString("LocationCode").equals("null")) {
                                new AlertDialog.Builder(getActivity()).setMessage("This sticker not transit yet").setNegativeButton("OK", null).show();
                                txtbarcode.setText("");
                            } else {
                                Counting = new JSONObject();
                                Counting.accumulate("ItemCode", json.getString("ItemCode"));
                                Counting.accumulate("Barcode", json.getString("StickerCode"));
                                Counting.accumulate("CurrentUser", appconfig.getUser());
                                Itemtitle.setText("Item : " + json.getString("ItemName"));
                                location.setText(json.getString("LocationCode"));
                                location.setEnabled(false);
                                btncameralo.setEnabled(false);
                                lot.setText(json.getString("Lot"));
                                ch = true;
                                qty.setText("0.0");
                                detail.setVisibility(LinearLayout.VISIBLE);
                                txtbarcode.setEnabled(false);
                                btncamera.setEnabled(false);
                                btncancel.setEnabled(true);
                                btncancel.setBackgroundColor(getActivity().getResources().getColor(R.color.transit_color));
                                btnconfirm.setBackgroundColor(getActivity().getResources().getColor(R.color.transit_color));
                                btnconfirm.setEnabled(true);
                            }
                        } else {
                            s = new AsyncTaskAdapter(new Gson().toJson(txtbarcode.getText().toString()), appconfig);
                            s.execute("api/Common/LoadItemDetail");
                            g = s.get(10000,TimeUnit.MILLISECONDS).toString();
                            g = g.substring(2, g.length() - 1);
                            JSONObject json = new JSONObject(g);
                            if (json.has("Message")) {
                                new AlertDialog.Builder(getActivity()).setMessage(json.getString("Message")).setNegativeButton("OK", null).show();
                                detail.setVisibility(LinearLayout.INVISIBLE);
                                btncancel.setEnabled(false);
                                btncancel.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
                                btnconfirm.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
                                btnconfirm.setEnabled(false);
                                txtbarcode.setText("");
                            } else if (json.getString("HasSticker").toString().equals("true")) {
                                new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("The item has StickerControl.\nPlease scan sticker code.").show();
                                txtbarcode.setText("");
                            } else {
                                txtbarcode.setEnabled(false);
                                btncamera.setEnabled(false);
                                Counting = new JSONObject();
                                Counting.accumulate("ItemCode", json.getString("ItemCode"));
                                Counting.accumulate("Barcode", null);
                                Counting.accumulate("CurrentUser", appconfig.getUser());
                                Itemtitle.setText("Item : " + json.getString("ItemName"));
                                ch = true;
                                qty.setText("0.0");
                                location.setEnabled(true);
                                btncameralo.setEnabled(true);
                                location.setText("");
                                btncancel.setEnabled(true);
                                btncancel.setBackgroundColor(getActivity().getResources().getColor(R.color.transit_color));
                                btnconfirm.setBackgroundColor(getActivity().getResources().getColor(R.color.transit_color));
                                btnconfirm.setEnabled(true);
                                detail.setVisibility(LinearLayout.VISIBLE);
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        Toast.makeText(getActivity(),"Please check internet",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else{
                    Toast.makeText(getActivity(),"Please connect internet",Toast.LENGTH_LONG).show();
                }
            }
        });
        //endregion   Barcode
        //region Camera Detector
        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.FragmentMain, CameraFragment.newInstance("Counting_Barcode"), "tag_barcode").addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion
        //region Camera Detector Location
        btncameralo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.FragmentMain, CameraFragment.newInstance("Counting_Location"), "tag_barcode").addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion
        //region Button Confirm
        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).checkinternet()) {
                    if (ch) {
                        String chlo = null;
                        try {
                            chlo = new AsyncTaskAdapter(new Gson().toJson(location.getText().toString().trim()), appconfig).execute("api/Common/CheckExistLocation").get(10000,TimeUnit.MILLISECONDS).toString();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            Toast.makeText(getActivity(),"Please check internet",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (qty.getText().toString().isEmpty() || location.getText().toString().isEmpty()) {
                            if (qty.getText().toString().trim().isEmpty() && location.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getActivity(), "Input qty and location please", Toast.LENGTH_LONG).show();
                            } else if (qty.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getActivity(), "Input qty at more than 0", Toast.LENGTH_LONG).show();
                            } else if (location.getText().toString().trim().isEmpty()) {
                                Toast.makeText(getActivity(), "Input location", Toast.LENGTH_LONG).show();
                            }
                        } else if (Double.parseDouble(qty.getText().toString().trim()) <= 0) {
                            Toast.makeText(getActivity(), "Input qty at more than 0", Toast.LENGTH_LONG).show();
                        } else if (chlo.equals("false")) {
                            Toast.makeText(getActivity(), "Location " + location.getText().toString() + " not exist", Toast.LENGTH_LONG).show();
                            location.setText("");
                            location.requestFocus();
                        } else {
                            try {
                                Counting.accumulate("Qty", Double.parseDouble(qty.getText().toString().trim()));
                                Counting.accumulate("Lot", lot.getText().toString().trim());
                                Counting.accumulate("LocationCode", location.getText().toString().trim());
                                s = new AsyncTaskAdapter(Counting, appconfig);
                                s.execute("api/MobileStockCounting/ConfirmStockConting");
                                if (s.get(10000, TimeUnit.MILLISECONDS).toString().equals("true")) {
                                    new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("Counting complete").show();
                                    ch = false;
                                    detail.setVisibility(LinearLayout.INVISIBLE);
                                    btncancel.setEnabled(false);
                                    btncancel.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
                                    btnconfirm.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
                                    btnconfirm.setEnabled(false);
                                    txtbarcode.setText("");
                                    qty.setText("");
                                    lot.setText("");
                                    location.setText("");
                                    txtbarcode.setEnabled(true);
                                    btncamera.setEnabled(true);
                                } else {
                                    new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("Wrong information").show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                Toast.makeText(getActivity(),"Please check internet",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } else {
                        new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("No data confirm").setTitle("Warning!").show();
                    }
                }else{
                    Toast.makeText(getActivity(),"Please connect internet",Toast.LENGTH_LONG).show();
                }
            }
        });
        //endregion
        //region Check Typing
        txtbarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtbarcode.getText().toString().trim().isEmpty())
                    MainActivity.typingCounting(true);
                else MainActivity.typingCounting(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion
        //region button Cancel
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detail.setVisibility(View.INVISIBLE);
                txtbarcode.setText("");
                txtbarcode.setEnabled(true);
                btncamera.setEnabled(true);
                location.setEnabled(true);
                btncameralo.setEnabled(true);
                qty.setText("");
                lot.setText("");
                location.setText("");
                btncancel.setEnabled(false);
                btncancel.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
                btnconfirm.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
                btnconfirm.setEnabled(false);
            }
        });
        //endregion
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
