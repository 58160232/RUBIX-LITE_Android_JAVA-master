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

import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.AsyncTaskAdapter;
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;
import rubix.mobile.rubix_mobile.SwipeHelper;
import rubix.mobile.rubix_mobile.TransitAdapter;

public class MenuTransitFragment extends Fragment {

    private EditText txtbarcode;
    private RecyclerView mRecyclerViewTransit;
    private Button btnscan;
    private Button btncamera;
    private Button btnconfirm;
    private Context mContext;
    private TransitAdapter itemadapter;
    private Button btncancel;
    private Button btnok;
    private EditText location;
    private ScaleAnimation animbye;
    private EditText txt_qty;
    private EditText txt_lot;
    private TextView txt_item;
    private JSONObject js;
    private LinearLayout ln;
    private Appconfig appconfig;
    private JSONObject temp;
    private Dialog dialog;
    private RecyclerView.LayoutManager mLayoutManager;

    public MenuTransitFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.FragmentName = "Transit";
        final View view = inflater.inflate(R.layout.fragment_menu_transit, container, false);
        //region binding data
        txtbarcode = (EditText) view.findViewById(R.id.txt_transit_sticker);
        mContext = getContext();
        mRecyclerViewTransit = (RecyclerView) view.findViewById(R.id.Recyclerview_Transit);
        if (appconfig == null) appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();
        if (itemadapter != null) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerViewTransit.setLayoutManager(mLayoutManager);
            mRecyclerViewTransit.setAdapter(itemadapter);
        } else {
            itemadapter = new TransitAdapter(mContext);
        }
        txt_item = (TextView) view.findViewById(R.id.txt_transit_Item);
        ln = (LinearLayout) view.findViewById(R.id.detailtransit);
        anim(ln, 1, 0, 1, 0, 390, false);
        btncamera = (Button) view.findViewById(R.id.btn_transit_camera);
        btnscan = (Button) view.findViewById(R.id.btn_transit_search);
        btnconfirm = (Button) view.findViewById(R.id.btn_transit_confirm);
        btnconfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
        btnconfirm.setEnabled(false);
        btnconfirm.setTextColor(getActivity().getResources().getColor(R.color.almost_white));
        txt_lot = (EditText) view.findViewById(R.id.txt_transit_lot);
        txt_qty = (EditText) view.findViewById(R.id.txt_transit_qty);
        btncancel = (Button) view.findViewById(R.id.btn_transit_cancel);
        btnok = (Button) view.findViewById(R.id.btn_transit_ok);
        //endregion

        //region check typing
        txtbarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!txtbarcode.getText().toString().trim().isEmpty())
                    MainActivity.typingTransit(true);
                else MainActivity.typingTransit(false);
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

        //region Button Check Sticker Barcode
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).checkinternet()) {
                    AsyncTaskAdapter s = new AsyncTaskAdapter(new Gson().toJson(txtbarcode.getText().toString().trim()), appconfig);
                    s.execute("api/MobileTransit/LoadTransitDetail");
                    try {
                        String g = " no internet ";
                        g = s.get(10000, TimeUnit.MILLISECONDS).toString();
                        js = new JSONObject(g.substring(2, g.length() - 1));
                        if (js.has("Message")) {
                            new AlertDialog.Builder(getActivity()).setMessage(js.getString("Message")).setNegativeButton("OK", null).show();
                            txtbarcode.setText("");
                            txtbarcode.requestFocus();
                        } else if (js.getString("IsBarCode").toString().equals("false") && itemadapter.checkSticker(js.getString("Sticker"))) {
                            new AlertDialog.Builder(getActivity()).setMessage("Sticker already scan.").setNegativeButton("OK", null).show();
                            txtbarcode.setText("");
                            txtbarcode.requestFocus();
                        } else if (js.getString("IsBarCode").toString().equals("false")) {
                            itemadapter.add(js.toString());
                            btnconfirm.setEnabled(true);
                            btnconfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
                            MainActivity.checkBackTransit(true);
                            mLayoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerViewTransit.setLayoutManager(mLayoutManager);
                            mRecyclerViewTransit.setAdapter(itemadapter);
                            itemadapter.notifyDataSetChanged();
                            txtbarcode.setText("");
                            txtbarcode.requestFocus();
                        } else if (js.getString("HasSticker").toString().equals("true")) {
                            new AlertDialog.Builder(getActivity()).setNegativeButton("OK", null).setMessage("The item has StickerControl.\nPlease scan sticker code.").show();
                            txtbarcode.setText("");
                        } else {
                            txt_item.setText("Item : " + js.getString("ItemName"));
                            txt_qty.setText("0.0");
                            anim(ln, 0, 1, 0, 1, 300, true);
                            txtbarcode.setEnabled(false);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        Toast.makeText(getActivity(), "Please check internet", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(getActivity(), "Please connect internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        //endregion

        //region Button Confirm
        btnconfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (((MainActivity) getActivity()).checkinternet()) {
                    if (mRecyclerViewTransit.getAdapter() != null && itemadapter.getItemCount() != 0 && itemadapter != null) {
                        dialog = new Dialog(getActivity());
                        dialog.setCancelable(false);
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
                                String s = ss.toString();
                                if (!s.equals(s.toUpperCase())) {
                                    s = s.toUpperCase();
                                    location.setText(s);
                                    location.setSelection(s.length());
                                }
                            }
                        });
                        final Button btncameradialog = (Button) dialog.findViewById(R.id.btn_transit_camera);
                        final Button btncanceldialog = (Button) dialog.findViewById(R.id.btn_dialogt_cancel);
                        final Button btnconfirmdialog = (Button) dialog.findViewById(R.id.btn_dialogt_confirm);
                        btncameradialog.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                getFragmentManager().beginTransaction().replace(R.id.FragmentMain, CameraFragment.newInstance("Transit_Location"), "tag_barcode").addToBackStack("ScanCode Fragment").commit();
                                dialog.dismiss();

                            }
                        });
                        btncanceldialog.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btnconfirmdialog.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                confirmTransit(location.getText().toString());
                            }
                        });
                    } else {
                        new AlertDialog.Builder(getActivity()).setMessage("No data to confirm").setNegativeButton("OK", null).setTitle("Warning!").show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please connect internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        //endregion

        //region Camera Detector
        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.FragmentMain, CameraFragment.newInstance("Transit_Barcode"), "tag_barcode").addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region button ok
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txt_qty.getText().toString().trim().isEmpty() && Double.parseDouble(txt_qty.getText().toString().trim()) > 0) {
                    anim(ln, 0, 1, 0, 1, 100, false);
                    try {
                        if (temp == null) {
                            js.put("QtyPerPack", Double.parseDouble(txt_qty.getText().toString().trim()));
                            js.put("Lot", txt_lot.getText().toString());
                            txt_lot.setText("");
                            txt_qty.setText("");
                            itemadapter.add(js.toString());
                            btnconfirm.setEnabled(true);
                            btnconfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
                            MainActivity.checkBackTransit(true);
                        } else {
                            temp.put("QtyPerPack", Double.parseDouble(txt_qty.getText().toString().trim()));
                            temp.put("Lot", txt_lot.getText().toString());
                            txt_lot.setText("");
                            txt_qty.setText("");
                            itemadapter.add(temp.toString());
                            temp = null;
                        }
                        txtbarcode.setText("");
                        txtbarcode.setEnabled(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getActivity(), "Input qty more than 0", Toast.LENGTH_LONG).show();
                mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerViewTransit.setLayoutManager(mLayoutManager);
                mRecyclerViewTransit.setAdapter(itemadapter);
                itemadapter.notifyDataSetChanged();
            }
        });
        //endregion

        //region Button Cancel
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_lot.setText("");
                txt_qty.setText("");
                txtbarcode.setEnabled(true);
                txtbarcode.setText("");
                anim(ln, 1, 0, 1, 0, 100, false);
                if (temp != null) {
                    itemadapter.add(temp.toString());
                    temp = null;
                }
            }
        });
        //endregion

        //region Swipe
        new SwipeHelper(getActivity(), mRecyclerViewTransit) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new UnderlayButton(
                        "", R.drawable.ic_delete_forever_white_24dp,
                        Color.parseColor("#DD2C00"), getActivity(),
                        new UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) throws JSONException {
                                itemadapter.dt_transit.remove(pos);
                                if (itemadapter.dt_transit.length() == 0) {
                                    MainActivity.checkBackTransit(false);
                                    btnconfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                                    btnconfirm.setEnabled(false);
                                }
                                itemadapter.notifyDataSetChanged();
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "",
                        R.drawable.ic_mode_edit_white_24dp,
                        Color.parseColor("#43d63e"), getActivity(),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                try {
                                    temp = new JSONObject(itemadapter.dt_transit.get(pos).toString());
                                    if (!temp.getString("Sticker").equals(temp.getString("ItemCode"))) {
                                        Toast.makeText(getActivity(), "Sticker can't edit", Toast.LENGTH_LONG).show();
                                        temp = null;
                                    } else {
                                        anim(ln, 0, 1, 0, 1, 300, true);
                                        txtbarcode.setEnabled(false);
                                        txt_lot.setText(temp.getString("Lot"));
                                        txt_qty.setText(temp.getString("QtyPerPack"));
                                        itemadapter.dt_transit.remove(pos);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                itemadapter.notifyDataSetChanged();
                            }
                        }
                ));
            }
        };
        //endregion
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (itemadapter.getItemCount() > 0) {
            btnconfirm.setEnabled(true);
            btnconfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
        }
        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();

        if (getArguments().getString("Barcode") != null) {
            txtbarcode.setText(getArguments().getString("Barcode"));
            btnscan.callOnClick();
            getArguments().remove("Barcode");
        }
        if (getArguments().getString("Location") != null) {
            confirmTransit(getArguments().getString("Location"));
            getArguments().remove("Location");
        }
    }

    private void confirmTransit(String locationQR) {
        String chlocation = "no internet";
        try {
            chlocation = new AsyncTaskAdapter(new Gson().toJson(locationQR), appconfig).execute("api/Common/CheckExistLocation").get(10000, TimeUnit.MILLISECONDS).toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            Toast.makeText(getActivity(), "Please check internet", Toast.LENGTH_SHORT).show();
            return;
        }
        if (locationQR.trim().isEmpty()) {
            Toast.makeText(getActivity(), "Input location", Toast.LENGTH_LONG).show();
        } else if (chlocation.equals("false")) {
            Toast.makeText(getActivity(), "Location " + locationQR + " not exist", Toast.LENGTH_LONG).show();
            location.setText("");
            location.requestFocus();
        } else if (chlocation.equals("no internet")) {
            Toast.makeText(getActivity(), "Please check internet", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject tmp = new JSONObject();
            try {
                tmp.accumulate("LocationCode", locationQR);
                tmp.accumulate("DtItem", new JSONArray(itemadapter.getList_transit()));
                tmp.accumulate("CurrentUser", appconfig.getUser());
                AsyncTaskAdapter ae = new AsyncTaskAdapter(tmp.toString(), appconfig);
                ae.execute("api/MobileTransit/ConfirmTransit");
                String g = ae.get(10000, TimeUnit.MILLISECONDS).toString();
                if (g.equals("true")) {
                    dialog.cancel();
                    MainActivity.typingTransit(false);
                    MainActivity.checkBackTransit(false);
                    getActivity().onBackPressed();
                    new AlertDialog.Builder(getActivity()).setMessage("Transit complete").setTitle("Transit").setPositiveButton("OK", null).show();
                    itemadapter.clear();
                    btnconfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                    btnconfirm.setEnabled(false);
                    MainActivity.checkBackTransit(false);

                } else {
                    new AlertDialog.Builder(getActivity()).setMessage("Transit not complete " +
                            "\nPlease check internet").setTitle("Transit").setPositiveButton("OK", null).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                Toast.makeText(getActivity(), "Please check internet", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    //animation when appear
    private void anim(LinearLayout lnn, int fx, int tx, int fy, int ty, int duration, boolean appear) {
        if (appear) {
            mRecyclerViewTransit.setEnabled(false);
            MainActivity.setDialogTransit(true);
            animbye = new ScaleAnimation(fx, tx, fy, ty);
            animbye.setDuration(duration);
            lnn.startAnimation(animbye);
            lnn.setVisibility(View.VISIBLE);
        } else {
            mRecyclerViewTransit.setEnabled(true);
            MainActivity.setDialogTransit(false);
            lnn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerViewTransit = null;
        temp = null;
    }
}
