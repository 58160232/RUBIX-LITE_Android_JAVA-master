package rubix.mobile.rubix_mobile.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//import rubix.mobile.rubix_mobile.mAdapter;
import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.AsyncTaskAdapter;
import rubix.mobile.rubix_mobile.ChangeLocAdapter;
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;
import rubix.mobile.rubix_mobile.SwipeHelper;

public class MenuChangeFragment extends Fragment {
    private EditText txtbarcode;
    private RecyclerView mRecyclerViewChangeLoc;

    private Button btnscan;
    private Button btncamera;
    private Button btnconfirm;
    private Context mContext;

    private ChangeLocAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText location;
    private EditText txt_qty;
    private EditText txt_lot;
    private Button btncancel;
    private Button btnok;
    private TextView title;
    private Appconfig appconfig;
    private LinearLayout ln;
    private JSONObject json = null;
    private JSONArray jsar;
    private boolean ch;
    private JSONObject temp;
    private ScaleAnimation animbye;
    private Dialog dialog;
    private Button btnconfirmdialogf;

    public MenuChangeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_menu_change, container, false);
        MainActivity.FragmentName = "Change Location";
        //region Binding data
        mContext = getContext();
        txt_lot = (EditText) view.findViewById(R.id.txt_change_lot);
        txt_qty = (EditText) view.findViewById(R.id.txt_change_qty);
        txtbarcode = (EditText) view.findViewById(R.id.txt_change_sticker);
        mRecyclerViewChangeLoc = (RecyclerView) view.findViewById(R.id.listview_change);
        btncancel = (Button) view.findViewById(R.id.btn_dialog_change_cancel);
        btnok = (Button) view.findViewById(R.id.btn_dialog_change_comfirm);
        title = (TextView) view.findViewById(R.id.txt_change_Item);

        if(appconfig == null ) appconfig = (Appconfig) getArguments().getSerializable("Appconfig");

        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();

        if (mAdapter != null) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerViewChangeLoc.setLayoutManager(mLayoutManager);
            mRecyclerViewChangeLoc.setAdapter(mAdapter);
        } else {
            mAdapter = new ChangeLocAdapter(mContext);
        }

        ln = (LinearLayout) view.findViewById(R.id.detailchange);
        anim(ln, 1, 0, 1, 0, 390, false);
        btncamera = (Button) view.findViewById(R.id.btn_change_camerab);
        btnscan = (Button) view.findViewById(R.id.btn_change_search);
        btnconfirm = (Button) view.findViewById(R.id.btn_change_confirm);
        btnconfirm.setEnabled(false);
        btnconfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
        btnconfirm.setTextColor(getActivity().getResources().getColor(R.color.almost_white));
        //endregion
        //region Check Typing
        txtbarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtbarcode.getText().toString().trim().isEmpty())
                    MainActivity.typingChange(true);
                else MainActivity.typingChange(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion
        //region edittion qty
        txt_qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    if (Double.parseDouble(txt_qty.getText().toString()) == 0) {
                        txt_qty.getText().clear();
                    }
                } else {
                    if (txt_qty.getText().toString().equals("") || Double.parseDouble(txt_qty.getText().toString()) == 0) {
                        txt_qty.setText("0.0");
                    }
                }
            }
        });
        txt_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_qty.getText().toString().equals(".")) {
                    txt_qty.setText("0.");
                    txt_qty.setSelection(txt_qty.getText().length());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion
        //region Check Sticker Barcode
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).checkinternet()) {
                    AsyncTaskAdapter s = new AsyncTaskAdapter(new Gson().toJson(txtbarcode.getText().toString()), appconfig);
                    AsyncTaskAdapter b = new AsyncTaskAdapter(new Gson().toJson(txtbarcode.getText().toString()), appconfig);
                    s.execute("api/Common/CheckExistSticker");
                    b.execute("api/Common/CheckExistBarcode");
                    ch = false;
                    try {
                        String ss = s.get(10000, TimeUnit.MILLISECONDS).toString();
                        String bb = b.get(10000,TimeUnit.MILLISECONDS).toString();
                        if (ss.equals("false") && bb.equals("false")) {
                            new AlertDialog.Builder(getActivity()).setMessage("Data not found").setNegativeButton("OK", null).show();
                            txtbarcode.setText("");
                        } else if (ss.equals("true")) {
                            s = new AsyncTaskAdapter(new Gson().toJson(txtbarcode.getText().toString()), appconfig);
                            s.execute("api/Common/LoadStickerDetail");
                            json = new JSONObject(new JSONArray(s.get(10,TimeUnit.SECONDS).toString()).get(0).toString());
                            if (mAdapter.checkSticker(json.getString("StickerCode"))) {
                                new AlertDialog.Builder(getActivity()).setMessage("Sticker already scan.").setNegativeButton("OK", null).show();
                                txtbarcode.setText("");
                            } else if (json.getString("LocationCode").equals("null")) {
                                new AlertDialog.Builder(getActivity()).setMessage("This sticker not transit yet").setNegativeButton("OK", null).show();
                                txtbarcode.setText("");
                            } else {
                                ch = true;
                                setDetail(0);
                            }
                        } else if (bb.toString().equals("true")) {
                            s = new AsyncTaskAdapter(new Gson().toJson(txtbarcode.getText().toString()), appconfig);
                            s.execute("api/MobileLocation/LocationLoadItemDetail");
                            jsar = new JSONArray(s.get(10000,TimeUnit.MILLISECONDS).toString());
                            if (jsar.length() == 0) {
                                new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("This barcode not exits any location").show();
                                txtbarcode.setText("");
                            }
                            if (jsar.length() > 1) {
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_transit);
                                dialog.show();
                                location = (EditText) dialog.findViewById(R.id.txt_dialogt_location);
                                location.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    }

                                    @Override
                                    public void afterTextChanged(Editable ss) {
                                        if (!ss.toString().equals(ss.toString().toUpperCase())) {
                                            location.setText(ss.toString().toUpperCase());
                                            location.setSelection(ss.length());
                                        }
                                    }
                                });
                                final TextView txt_dialog = (TextView) dialog.findViewById(R.id.txt_dialogt_location);
                                txt_dialog.setHint("Scan location from");
                                final Button btncamera = (Button) dialog.findViewById(R.id.btn_transit_camera);
                                final Button btncancel = (Button) dialog.findViewById(R.id.btn_dialogt_cancel);
                                btnconfirmdialogf = (Button) dialog.findViewById(R.id.btn_dialogt_confirm);
                                //region button cancel
                                btncancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        txt_qty.setText("0.0");
                                        txt_lot.setText("");
                                    }
                                });
                                //endregion
                                //region button confirm
                                btnconfirmdialogf.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            String chlocation = new AsyncTaskAdapter(new Gson().toJson(location.getText().toString()), appconfig).execute("api/Common/CheckExistLocation").get(10000,TimeUnit.MILLISECONDS).toString();
                                            if (chlocation.equals("false")) {
                                                Toast.makeText(getActivity(), "Location " + location.getText().toString() + " not exits", Toast.LENGTH_LONG).show();
                                            } else {
                                                for (int i = 0; i < jsar.length(); i++) {
                                                    JSONObject tmp = new JSONObject(jsar.get(i).toString());
                                                    if (tmp.getString("LocationCode").equals(location.getText().toString())) {
                                                        json = new JSONObject(tmp.toString());
                                                        ch = true;
                                                        dialog.dismiss();
                                                        setDetail(1);
                                                    }
                                                }
                                                if (!ch) {
                                                    Toast.makeText(getActivity(), txtbarcode.getText().toString() + " not exits in " + location.getText().toString(), Toast.LENGTH_LONG).show();
                                                    location.setText("");
                                                }
                                            }
                                            location.setText("");
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

                                    }
                                });
                                //endregion
                                //region button camera
                                btncamera.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getFragmentManager().beginTransaction().replace(R.id.FragmentMain, CameraFragment.newInstance("Change_LocationFrom"), "tag_barcode").addToBackStack("ScanCode Fragment").commit();
                                        dialog.dismiss();
                                    }
                                });
                                //endregion
                            } else if (jsar.length() == 1) {
                                json = new JSONObject(new JSONArray(s.get(10000,TimeUnit.MILLISECONDS).toString()).get(0).toString());
                                ch = true;
                                setDetail(1);
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
                }
                else{
                    Toast.makeText(getActivity(),"Please connect internet",Toast.LENGTH_LONG).show();
                }
            }
        });
        //endregion
        //region Camera Detector
        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.FragmentMain,CameraFragment.newInstance("Change_Barcode"),"tag_barcode").addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion
        //region Button Confirm
        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).checkinternet()) {
                    if (mRecyclerViewChangeLoc.getAdapter() != null && mAdapter.getItemCount() != 0 && mAdapter != null) {
                        //region dialog
                        dialog = new Dialog(getActivity());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_transit);
                        dialog.show();
                        final TextView txt_dialog = (TextView) dialog.findViewById(R.id.txt_dialogt_location);
                        txt_dialog.setHint("Scan location to");
                        location = (EditText) dialog.findViewById(R.id.txt_dialogt_location);
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
                        final Button btncamera = (Button) dialog.findViewById(R.id.btn_transit_camera);
                        final Button btncancel = (Button) dialog.findViewById(R.id.btn_dialogt_cancel);
                        final Button btnconfirm = (Button) dialog.findViewById(R.id.btn_dialogt_confirm);
                        btncancel.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btncamera.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                getFragmentManager().beginTransaction().replace(R.id.FragmentMain, CameraFragment.newInstance("Change_LocationTo"), "tag_barcode").addToBackStack("ScanCode Fragment").commit();
                                dialog.dismiss();
                            }
                        });
                        btnconfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (((MainActivity) getActivity()).checkinternet()) {
                                    confirmChange(location.getText().toString());
                                    dialog.dismiss();
                                }else{
                                    Toast.makeText(getActivity(),"Please connect internet",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        //endregion
                    } else {
                        new AlertDialog.Builder(getActivity()).setMessage("No data confirm").setTitle("Warning").setNegativeButton("OK", null).show();
                    }
                }
                else{
                    Toast.makeText(getActivity(),"Please connect internet",Toast.LENGTH_LONG).show();
                }
            }
        });
        //endregion
        //region Swipe
        new SwipeHelper(getActivity(), mRecyclerViewChangeLoc) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new UnderlayButton(
                        "", R.drawable.ic_delete_forever_white_24dp,
                        Color.parseColor("#DD2C00"), getActivity(),
                        new UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) throws JSONException {
                                mAdapter.json_change.remove(pos);
                                if (mAdapter.json_change.length() == 0) {
                                    MainActivity.checkBackTransit(false);
                                    btnconfirm.setEnabled(false);
                                    btnconfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "",
                        R.drawable.ic_mode_edit_white_24dp,
                        Color.parseColor("#43d63e"),getActivity(),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                try {
                                    json = new JSONObject(mAdapter.json_change.get(pos).toString());
                                    temp = new JSONObject(mAdapter.json_change.get(pos).toString());

                                    if (!temp.has("Barcode")) {
                                        setDetail(0);
                                    } else {
                                        setDetail(2);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    mRecyclerViewChangeLoc.setAdapter(mAdapter);
                                    mAdapter.json_change.remove(pos);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                ));
            }
        };
        //endregion
        return view;
    }

    //show or hide detail
    private void setDetail(int i) {
        try {
            title.setText("Item : " + json.getString("ItemCode"));
            if (i == 1) {
                txt_qty.setText("0.0");
                txt_lot.setText(json.getString("Lot"));
            } else if (i == 0) {
                txt_lot.requestFocus();
                txt_qty.setText(json.getString("QtyPerPack"));
                txt_qty.setEnabled(false);
                txt_lot.setText(json.getString("Lot"));
            } else if (i == 2) {
                txt_qty.setEnabled(true);
                txt_qty.requestFocus();
                txt_qty.setText(json.getString("QtyPerPack"));
                txt_lot.setText(json.getString("Lot"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        anim(ln, 0, 1, 0, 1, 300, true);
        txtbarcode.setEnabled(false);
        //txt_qty.requestFocus();
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anim(ln, 1, 0, 1, 0, 200, false);
                txtbarcode.setEnabled(true);
                txtbarcode.setText("");
                if (temp != null) {
                    mAdapter.add(temp.toString());
                    temp = null;
                }
                mRecyclerViewChangeLoc.setAdapter(mAdapter);
            }
        });
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txt_qty.getText().toString().trim().isEmpty() && Double.parseDouble(txt_qty.getText().toString()) > 0) {
                    anim(ln, 1, 0, 1, 0, 200, false);
                    try {
                        json.put("Lot", txt_lot.getText().toString());
                        json.put("QtyPerPack", Double.parseDouble(txt_qty.getText().toString()));
                        ///json.put("Ref", txtbarcode.getText().toString());
                        json.put("Split", false);
                        mAdapter.add(json.toString());
                        btnconfirm.setEnabled(true);
                        btnconfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
                        MainActivity.checkBackChange(true);
                        mLayoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerViewChangeLoc.setLayoutManager(mLayoutManager);
                        mRecyclerViewChangeLoc.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        temp = null;
                        txtbarcode.setText("");
                        txt_qty.setText("");
                        txt_lot.setText("");
                        txt_qty.setEnabled(true);
                        txtbarcode.setEnabled(true);
                        txtbarcode.requestFocus();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "input qty more than 0", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //confirm change location
    private void confirmChange(String LocationQR){
        String chlocation = "false";
        try {
            chlocation = new AsyncTaskAdapter(new Gson().toJson(LocationQR), appconfig).execute("api/Common/CheckExistLocation").get(10000,TimeUnit.MILLISECONDS).toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(getActivity(),"Please check internet",Toast.LENGTH_SHORT).show();
            return;
        }
        if (chlocation.equals("false")) {
            Toast.makeText(getActivity(), "Location " + LocationQR + " not exits", Toast.LENGTH_LONG).show();
            location.setText("");
            location.requestFocus();
        } else {
            JSONObject tmp = new JSONObject();
            try {
                tmp.accumulate("LocationCode", LocationQR);
                tmp.accumulate("DtItem", new JSONArray(mAdapter.getList_change()));
                tmp.accumulate("CurrentUser", appconfig.getUser());
                tmp.accumulate("DtSerial", new JSONArray());
                AsyncTaskAdapter s = new AsyncTaskAdapter(tmp, appconfig);
                s.execute("api/MobileLocation/ConfirmChangeLocation");
                String b = s.get(10000,TimeUnit.MILLISECONDS).toString();
                String result = new JSONObject(new JSONArray(new JSONObject(s.get(10000,TimeUnit.MILLISECONDS).toString()).getString("Table")).get(0).toString()).getString("Result").toString();
                if (result.equals("true")) {
                    dialog.dismiss();
                    MainActivity.checkBackChange(false);
                    MainActivity.typingChange(false);
                    getActivity().onBackPressed();
                    new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("Change location complete.").show();
                    MainActivity.checkBackChange(false);
                    btnconfirm.setEnabled(false);
                    btnconfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                    mAdapter.clear();
                } else {
                    dialog.dismiss();
                    new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("Change location not complete.").show();
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
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter.json_change.length()>0) {
            btnconfirm.setEnabled(true);
            btnconfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
        }
        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();
        if(getArguments().getString("Barcode")!= null) {
            txtbarcode.setText(getArguments().getString("Barcode"));
            btnscan.callOnClick();
            getArguments().remove("Barcode");
        }
        if(getArguments().getString("LocationFrom")!= null) {
            btnscan.callOnClick();
            location.setText(getArguments().getString("LocationFrom"));
            btnconfirmdialogf.callOnClick();
            getArguments().remove("LocationFrom");
        }
        if(getArguments().getString("LocationTo")!= null) {
            confirmChange(getArguments().getString("LocationTo"));
            getArguments().remove("LocationTo");
        }
    }

    //animetion fade in and fade out
    private void anim(LinearLayout ln, int fx, int tx, int fy, int ty, int duration, boolean appear) {

        if (appear) {
            mRecyclerViewChangeLoc.setEnabled(false);
            if(btnconfirm!=null)btnconfirm.setEnabled(false);
            animbye = new ScaleAnimation(fx, tx, fy, ty);
            animbye.setDuration(duration);
            ln.startAnimation(animbye);
            ln.setVisibility(View.VISIBLE);

            mRecyclerViewChangeLoc.setAdapter(mAdapter);
        } else {
            mRecyclerViewChangeLoc.setEnabled(true);
            if(btnconfirm!=null)btnconfirm.setEnabled(true);
            ln.setVisibility(View.GONE);
            mRecyclerViewChangeLoc.setAdapter(mAdapter);
        }
    }
    //region Attach
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
    //endregion
    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerViewChangeLoc = null;
        jsar = null;
        temp = null;
    }
}
