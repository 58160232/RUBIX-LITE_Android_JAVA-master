package rubix.mobile.rubix_mobile.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.AsyncTaskAdapter;
import rubix.mobile.rubix_mobile.ConfirmReceiveAdapter;
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;
import rubix.mobile.rubix_mobile.ReceiveAdapter;
import rubix.mobile.rubix_mobile.SwipeHelper;

import static rubix.mobile.rubix_mobile.MainActivity.FragmentName;

public class MenuReceivingFragment extends Fragment {
    private EditText mInvoice;
    private EditText mDoNo;
    private EditText mRef;
    private com.toptoche.searchablespinnerlibrary.SearchableSpinner mSpin_Sup;
    private TextView mTxtInvoice;
    private TextView mTxtRef;
    private TextView mTxtDoNo;
    private TextView mTxtSupp;
    private Button mBtnScanInvoice;
    private Button mBtnScanRef;
    private Button mBtnScanDoNo;
    private Button mBtnScanBarcode;
    private Button mBtnOK;
    private GridLayout mGridDetails;
    private Button mBtnCollapse;
    private static int countCollapse;
    private LinearLayout mLinearScanner;
    private Button mBtnSeach;
    private EditText mBarcode;
    private RecyclerView mRecyclerView;
    private Button mBtnConfirm;
    private View mViewDevide;
    private ConfirmReceiveAdapter mConfirmProcess = new ConfirmReceiveAdapter();
    private ReceiveAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int seq = 0;
    private String SuppResult = "";
    public static JSONArray json_receive = new JSONArray();
    public static JSONArray json_serial = new JSONArray();
    static ArrayAdapter<String> adapter;
    public static ArrayList<String> arr_supplier;
    static int positionSupp = -1;
    Appconfig appconfig;
    JSONObject temp;
    private EditText Item, Lot, Po, qty;


    public MenuReceivingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        if (appconfig == null)
            appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
        if (!appconfig.checkstate())
            ((MainActivity) getActivity()).restartApp();

        return inflater.inflate(R.layout.fragment_menu_receiving, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle saveInstanceState) {
        super.onViewCreated(view, saveInstanceState);
        FragmentName = "Receive Details";

        //region Binding Data
        mInvoice = (EditText) view.findViewById(R.id.editText_Invoice);
        mDoNo = (EditText) view.findViewById(R.id.editText_DoNo);
        mRef = (EditText) view.findViewById(R.id.editText_Ref);
        mSpin_Sup = (com.toptoche.searchablespinnerlibrary.SearchableSpinner) view.findViewById(R.id.spinner_Supp);
        mTxtInvoice = (TextView) view.findViewById(R.id.txtInvoice);
        mTxtDoNo = (TextView) view.findViewById(R.id.txtDo);
        mTxtRef = (TextView) view.findViewById(R.id.txtRef);
        mTxtSupp = (TextView) view.findViewById(R.id.txtSupp);
        mBtnOK = (Button) view.findViewById(R.id.btn_ok);
        mBtnScanInvoice = (Button) view.findViewById(R.id.btn_scan_invoice);
        mBtnScanDoNo = (Button) view.findViewById(R.id.btn_scan_do);
        mBtnScanRef = (Button) view.findViewById(R.id.btn_scan_ref);
        mBtnScanBarcode = (Button) view.findViewById(R.id.btn_Scan_barcode);
        mGridDetails = (GridLayout) view.findViewById(R.id.grid_details);
        mBtnCollapse = (Button) view.findViewById(R.id.btn_collapse);
        mLinearScanner = (LinearLayout) view.findViewById(R.id.linear_scanner);
        mBtnSeach = (Button) view.findViewById(R.id.btn_seach_barcode);
        mBarcode = (EditText) view.findViewById(R.id.editText_barcode);
        mBtnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        mViewDevide = (View) view.findViewById(R.id.view_devide);
        mBtnConfirm.setTextColor(getActivity().getResources().getColor(R.color.almost_white));
        loadSupplier();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_list_item_receive);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReceiveAdapter(getActivity(), json_receive);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



//        mSpin_Sup.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(arr_supplier.size()==0 && ((MainActivity)getActivity()).checkinternet())
//                loadSupplier();
//                return false;
//            }
//        });

        //endregion

        //region Button OK
        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.checkBackReceive(1);
                FragmentName = "Receive Details press ok";
                if (mInvoice.getText().toString().equals("")) {
                    mInvoice.setText("-");
                    mInvoice.setEnabled(false);
                    mInvoice.setTextSize(12);
                    mInvoice.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtInvoice.setVisibility(View.VISIBLE);

                } else {
                    mInvoice.setEnabled(false);
                    mInvoice.setTextSize(12);
                    mInvoice.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtInvoice.setVisibility(View.VISIBLE);
                }
                if (mRef.getText().toString().equals("")) {
                    mRef.setText("-");
                    mRef.setEnabled(false);
                    mRef.setTextSize(12);
                    mRef.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtRef.setVisibility(View.VISIBLE);
                } else {
                    mRef.setEnabled(false);
                    mRef.setTextSize(12);
                    mRef.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtRef.setVisibility(View.VISIBLE);
                }
                if (mDoNo.getText().toString().equals("")) {
                    mDoNo.setText("-");
                    mDoNo.setEnabled(false);
                    mDoNo.setTextSize(12);
                    mDoNo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtDoNo.setVisibility(View.VISIBLE);
                } else {
                    mDoNo.setEnabled(false);
                    mDoNo.setTextSize(12);
                    mDoNo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtDoNo.setVisibility(View.VISIBLE);
                }

                if (mSpin_Sup.getSelectedItemPosition() == 0) {
                    SuppResult = "-";
                    positionSupp = mSpin_Sup.getSelectedItemPosition();
                    mSpin_Sup.setEnabled(false);
                    mSpin_Sup.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorPrimary)));
                    mTxtSupp.setVisibility(View.VISIBLE);
                } else {
                    SuppResult = mSpin_Sup.getSelectedItem().toString();
                    positionSupp = mSpin_Sup.getSelectedItemPosition();
                    mSpin_Sup.setEnabled(false);
                    mSpin_Sup.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorPrimary)));
                    mTxtSupp.setVisibility(View.VISIBLE);
                }

                mBtnOK.setVisibility(View.GONE);

                mBtnScanInvoice.setVisibility(View.GONE);
                mBtnScanDoNo.setVisibility(View.GONE);
                mBtnScanRef.setVisibility(View.GONE);

                mBtnConfirm.setEnabled(false);
                mBtnConfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                mBtnConfirm.setVisibility(View.VISIBLE);

                mBtnCollapse.setVisibility(View.VISIBLE);
                mLinearScanner.setVisibility(View.VISIBLE);
            }
        });
        //endregion

        //region Button Collapse
        mBtnCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countCollapse == 0) {
                    mGridDetails.setVisibility(View.GONE);
                    mBtnCollapse.setBackgroundResource(R.drawable.ic_collapse_00009);
                    countCollapse = 1;
                } else {
                    mGridDetails.setVisibility(View.VISIBLE);
                    mBtnCollapse.setBackgroundResource(R.drawable.ic_collapse);
                    countCollapse = 0;
                }
            }
        });
        //endregion

        //region Button Search Data
        mBtnSeach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).checkinternet()) {
                    if (!mBarcode.getText().toString().trim().isEmpty()) {
                        AsyncTaskAdapter checkExist = new AsyncTaskAdapter(new Gson().toJson(mBarcode.getText().toString()), appconfig);
                        checkExist.execute("api/Common/CheckExistBarcode");
                        try {
                            if (checkExist.get(10000, TimeUnit.MILLISECONDS) != null) {
                                if (checkExist.CheckWork == 200) {
                                    AsyncTaskAdapter mGetDetails = new AsyncTaskAdapter(new Gson().toJson(mBarcode.getText().toString()), appconfig);
                                    mGetDetails.execute("api/Common/LoadItemDetail");
                                    if (mGetDetails.get(10000,TimeUnit.MILLISECONDS) != null) {
                                        String item = mGetDetails.get(10000,TimeUnit.MILLISECONDS).toString();
                                        JSONObject getItemDetails = new JSONObject(item.substring(2, item.length() - 1));
                                        JSONObject data = getItemDetails;
                                        //region Check Barcode not found / Add new
                                        if (getItemDetails.has("Message")) {
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle("Barcode not found.");
                                            builder.setMessage("Barcode not found, Do you want to create new?");
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    Bundle toAddItem = new Bundle();
                                                    Fragment s = new AddItemFragment();
                                                    toAddItem.putString("Barcode", mBarcode.getText().toString());
                                                    toAddItem.putSerializable("Appconfig", appconfig);
                                                    s.setArguments(toAddItem);
                                                    getFragmentManager()
                                                            .beginTransaction()
                                                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                                            .replace(R.id.FragmentMain, s, "tag_mapping_item")
                                                            .addToBackStack("MappingItem").commit();
                                                }
                                            });
                                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();

                                        }
                                        //endregion

                                        //region Has Barcode in system
                                        else {
                                            if (mGetDetails.CheckWork == 200) {
                                                Bundle args = new Bundle();
                                                args.putSerializable("Appconfig", appconfig);
                                                args.putString("Check", "AddReceive");

                                                if (data.getString("HasSticker").equals("true")) {

                                                    Fragment s = new ReceiveDetailsStickerFragment();
                                                    args.putString("Barcode", mBarcode.getText().toString());
                                                    args.putString("ItemCode", data.getString("ItemCode"));
                                                    args.putString("ItemName", data.getString("ItemName"));
                                                    args.putString("HasSerial", data.getString("HasSerial"));
                                                    args.putString("HasSticker", data.getString("HasSticker"));
                                                    args.putString("NeedCheckExistSerial", data.getString("NeedCheckExistSerial"));
                                                    args.putInt("Seq", seq);
                                                    s.setArguments(args);

                                                    mBarcode.setText("");
                                                    getFragmentManager()
                                                            .beginTransaction()
                                                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                                            .replace(R.id.FragmentMain, s, "tag_detail_receive_sticker")
                                                            .addToBackStack("ReceiveSticker").commit();

                                                } else if (data.getString("HasSticker").equals("false")) {

                                                    Fragment s = new ReceiveDetailsNoStickerFragment();
                                                    args.putString("Barcode", mBarcode.getText().toString());
                                                    args.putString("ItemCode", data.getString("ItemCode"));
                                                    args.putString("ItemName", data.getString("ItemName"));
                                                    args.putString("HasSerial", data.getString("HasSerial"));
                                                    args.putString("HasSticker", data.getString("HasSticker"));
                                                    args.putString("NeedCheckExistSerial", data.getString("NeedCheckExistSerial"));
                                                    args.putInt("Seq", seq);
                                                    s.setArguments(args);

                                                    mBarcode.setText("");
                                                    getFragmentManager()
                                                            .beginTransaction()
                                                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                                            .replace(R.id.FragmentMain, s, "tag_detail_receive_no_sticker")
                                                            .addToBackStack("ReceiveNoSticker").commit();
                                                }
                                                mBtnConfirm.setEnabled(true);
                                                mBtnConfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
                                            }
                                        }
                                        //endregion
                                    }
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
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Warning.");
                        builder.setMessage("Please insert Barcode!");
                        builder.setNegativeButton("OK", null);
                        builder.show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please connect internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        //endregion

        //region Cameara Detector Invoice
        mBtnScanInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Receive_Invoice"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Camera Detector Do
        mBtnScanDoNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Receive_Do"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Camera Detector Ref
        mBtnScanRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Receive_Ref"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Camera Detector Barcode
        mBtnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Receive_Barcode"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Button Confirm
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm receiving.");
                builder.setMessage("Are you want to confirm this process?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (((MainActivity) getActivity()).checkinternet()) {
                        JSONObject info = new JSONObject();
                        try {
                            AsyncTaskAdapter genRC = new AsyncTaskAdapter(new Gson().toJson("RC"), appconfig);
                            genRC.execute("api/Common/GenerateDocumentNo");
                            String rc_no = (String) genRC.get(10000,TimeUnit.MILLISECONDS);
                            info.accumulate("ReceivingNo", rc_no.substring(1, rc_no.length() - 1));
                            if (mInvoice.getText().toString().equals("-"))
                                info.accumulate("InvoiceNo", "");
                            else
                                info.accumulate("InvoiceNo", mInvoice.getText().toString());
                            if (mDoNo.getText().toString().equals("-"))
                                info.accumulate("DO", "");
                            else
                                info.accumulate("DO", mDoNo.getText().toString());
                            if (mRef.getText().toString().equals("-"))
                                info.accumulate("Ref", "");
                            else
                                info.accumulate("Ref", mRef.getText().toString());
                            if (!SuppResult.equals("-"))
                                info.accumulate("SupplierCode", SuppResult.substring(0, SuppResult.indexOf(" ")));
                            else
                                info.accumulate("SupplierCode", SuppResult);
                            info.accumulate("dtReceive", json_receive);
                            if (MainActivity.dt_serial_receive.length() == 0)
                                info.accumulate("dtSerial", new JSONArray());
                            else
                                info.accumulate("dtSerial", MainActivity.dt_serial_receive);

                            info.accumulate("CurrentUser", appconfig.getUser());

                            seq = 0;

                            //                    region Confirm data
                            AsyncTaskAdapter confirm_receive = new AsyncTaskAdapter(info.toString(), appconfig);
                            confirm_receive.execute("api/MobileReceiving/ConfirmReceivingList");

                            String checkConfirm = confirm_receive.get(10000,TimeUnit.MILLISECONDS).toString();
                            JSONObject checkSuccess = new JSONObject(checkConfirm.toString());
                            if (!checkSuccess.has("ErrorException")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Receive Success.");
                                builder.show();

                                json_receive = new JSONArray();
                                json_serial = new JSONArray();
                                positionSupp = -1;
                                SuppResult = "";
                                arr_supplier = new ArrayList<String>();

                                MainActivity.dt_serial_receive = new JSONArray();

                                FragmentName = "Complete Process";
                                getActivity().onBackPressed();
                            }

//      endregion
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
                        }else {
                            Toast.makeText(getActivity(),"Please connect internet",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        //endregion

        //region Swipe
        new SwipeHelper(getActivity(), mRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "",
                        R.drawable.ic_delete_forever_white_24dp,
                        Color.parseColor("#DD2C00"), getActivity(),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) throws JSONException {
                                mAdapter.remove(pos);
                                if (mAdapter.getItemCount() == 0) {
                                    mViewDevide.setVisibility(View.INVISIBLE);
                                    mBtnConfirm.setEnabled(false);
                                    mBtnConfirm.setBackgroundColor(getActivity().getResources().getColor(R.color.btnDefault));
                                    mBtnConfirm.setTextColor(getActivity().getResources().getColor(R.color.almost_white));
                                }
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
                                    temp = new JSONObject(mAdapter.json_receive.getJSONObject(pos).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setCancelable(false);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_edit_receive);
                                dialog.show();

                                Item = (EditText) dialog.findViewById(R.id.dialog_edit_item);
                                Lot = (EditText) dialog.findViewById(R.id.dialog_edit_lot);
                                Po = (EditText) dialog.findViewById(R.id.dialog_edit_po);
                                qty = (EditText) dialog.findViewById(R.id.dialog_edit_qty);
                                Button btncancel = (Button) dialog.findViewById(R.id.dialog_edit_cancel);
                                Button btnok = (Button) dialog.findViewById(R.id.dialog_edit_ok);
                                try {
                                    Item.setText(temp.getString("ItemName"));
                                    Lot.setText(temp.getString("Lot"));
                                    Po.setText(temp.getString("PONo"));
                                    qty.setText(temp.getString("TotalQty"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mAdapter.remove(pos);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                btnok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (qty.getText().toString().trim().isEmpty()) {
                                            Toast.makeText(getActivity(), "input qty more than 0", Toast.LENGTH_SHORT).show();
                                        } else {
                                            if (Double.parseDouble(qty.getText().toString()) < 0)
                                                Toast.makeText(getActivity(), "input qty more than 0", Toast.LENGTH_SHORT).show();
                                            else {
                                                try {
                                                    temp.put("Lot", Lot.getText().toString());
                                                    temp.put("PONo", Po.getText().toString());
                                                    temp.put("TotalQty", Double.parseDouble(qty.getText().toString()));
                                                    mAdapter.add(temp.toString());
                                                    mAdapter.notifyDataSetChanged();
                                                    dialog.dismiss();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                });
                                btncancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            mAdapter.recover();
                                            dialog.dismiss();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                        }
                ));
//                underlayButtons.add(new SwipeHelper.UnderlayButton(
//                        "Unshare",
//                        0,
//                        Color.parseColor("#C7C7CB"),getActivity(),
//                        new SwipeHelper.UnderlayButtonClickListener() {
//                            @Override
//                            public void onClick(int pos) {
//
//                            }
//                        }
//                ));
            }
        };
        //endregion
    }

    //load Supplier to spin
    private void loadSupplier(){
        arr_supplier = new ArrayList<>();
            AsyncTaskAdapter LoadSupplier = new AsyncTaskAdapter(new Gson().toJson(""), appconfig);
            LoadSupplier.execute("api/Common/LoadSupplierCode");
            try {
                String load_supplier = LoadSupplier.get(40000,TimeUnit.MILLISECONDS).toString();
                JSONArray getSupplier = new JSONArray(load_supplier);

                arr_supplier.add("Please select supplier.");

                for (int i = 0; i < getSupplier.length(); i++) {
                    arr_supplier.add(getSupplier.getJSONObject(i).getString("Display"));
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

            if (SuppResult.equals("")) {
                adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, arr_supplier);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpin_Sup.setTitle("Select Supplier.");
                mSpin_Sup.setAdapter(adapter);
            } else {
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpin_Sup.setTitle("Select Supplier.");
                mSpin_Sup.setAdapter(adapter);
                mSpin_Sup.setSelection(positionSupp);
            }
        }
    @Override

    public void onResume() {
        super.onResume();
        if (!appconfig.checkstate())
            ((MainActivity) getActivity()).restartApp();

        if(arr_supplier == null)
            loadSupplier();

        //receive data
        if (getArguments() != null) {
            if (getArguments().getString("WhereScan") != null) {
                if (getArguments().getString("WhereScan").equals("Invoice")) {
                    mInvoice.setText(getArguments().getString("result"));
                } else if (getArguments().getString("WhereScan").equals("Do")) {
                    mDoNo.setText(getArguments().getString("result"));
                } else if (getArguments().getString("WhereScan").equals("Ref")) {
                    mRef.setText(getArguments().getString("result"));
                } else if (getArguments().getString("WhereScan").equals("Barcode")) {
                    mBarcode.setText(getArguments().getString("result"));
                    mBtnSeach.callOnClick();
                }
            }

            if (getArguments().getString("Check") != null) {
                if (getArguments().getString("Check").equals("AddReceive")) {
                    JSONObject js_receive = new JSONObject();
                    try {
                        mBarcode.getText().clear();
                        js_receive.accumulate("ItemCode", getArguments().getString("ItemCode"));
                        js_receive.accumulate("ItemName", getArguments().getString("ItemName"));
                        js_receive.accumulate("HasSticker", getArguments().getString("HasSticker"));
                        js_receive.accumulate("HasSerial", getArguments().getString("HasSerial"));
                        js_receive.accumulate("Lot", getArguments().getString("Lot"));
                        js_receive.accumulate("PONo", getArguments().getString("PONo"));
                        js_receive.accumulate("TotalQty", getArguments().getDouble("TotalQty"));
                        js_receive.accumulate("Pack", getArguments().getInt("Pack"));
                        js_receive.accumulate("QtyPerPack", getArguments().getDouble("QtyPerPack"));
                        js_receive.accumulate("Seq", seq);
                        json_receive.put(js_receive);
                        seq++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (getArguments().getString("Check").equals("FromMapping")) {
                    if (getArguments().getString("Status").equals("Confirm")) {
                        mBtnSeach.callOnClick();
                        mBarcode.getText().clear();
                    }
                } else {
                    mBarcode.getText().clear();
                }
            }
        }

        //region Check Back Again
        int test = MainActivity.getCheckBackReceive();
        if (MainActivity.getCheckBackReceive() != 0) {
            FragmentName = "Receive Details press ok";

            mInvoice.setEnabled(false);
            mInvoice.setTextSize(12);
            mInvoice.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mTxtInvoice.setVisibility(View.VISIBLE);

            mRef.setEnabled(false);
            mRef.setTextSize(12);
            mRef.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mTxtRef.setVisibility(View.VISIBLE);

            mDoNo.setEnabled(false);
            mDoNo.setTextSize(12);
            mDoNo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mTxtDoNo.setVisibility(View.VISIBLE);

            mSpin_Sup.setEnabled(false);
            mSpin_Sup.setBackgroundTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorPrimary)));
            mTxtSupp.setVisibility(View.VISIBLE);

            mBtnOK.setVisibility(View.GONE);
            mBtnScanInvoice.setVisibility(View.GONE);
            mBtnScanDoNo.setVisibility(View.GONE);
            mBtnScanRef.setVisibility(View.GONE);
            mBtnCollapse.setVisibility(View.VISIBLE);
            mLinearScanner.setVisibility(View.VISIBLE);

            if (json_receive.length() != 0) {
                mViewDevide.setVisibility(View.VISIBLE);
                mBtnConfirm.setVisibility(View.VISIBLE);
                mBtnConfirm.setEnabled(true);
                mBtnConfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
            } else {
                mBtnConfirm.setEnabled(false);
                mBtnConfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                mBtnConfirm.setVisibility(View.VISIBLE);
            }

            if (countCollapse == 0) {
                mGridDetails.setVisibility(View.VISIBLE);
                mBtnCollapse.setBackgroundResource(R.drawable.ic_collapse);
            } else {
                mGridDetails.setVisibility(View.GONE);
                mBtnCollapse.setBackgroundResource(R.drawable.ic_collapse_00009);
            }
        }
        //endregion
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
