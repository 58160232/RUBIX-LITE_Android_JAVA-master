package rubix.mobile.rubix_mobile.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;
import rubix.mobile.rubix_mobile.ReceiveAdapter;

import static rubix.mobile.rubix_mobile.MainActivity.FragmentName;

/**
 * Created by niwat on 29/1/2561.
 */

public class ReceiveDetailsStickerFragment extends Fragment {
    EditText mItemname;
    private EditText mPO_no;
    private EditText mLot;
    private EditText mTotal;
    private EditText mSticker;
    private EditText mQtyPerPack;
    private Button mbtnOK;
    private Button mbtnCANCEL;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ReceiveAdapter mAdapter;
    private TextView mCautionDecimal;
    public static String Po = "!";
    public static String Lot = "!";
    String CheckSerial;
    Appconfig appconfig;
    Bundle totalSerial = new Bundle();
    Double x = 0.0;
    int y = 1;
    Double ComputePerPack = x/y;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (appconfig == null)
            appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
        if(!appconfig.checkstate())
            ((MainActivity)getActivity()).restartApp();

        return inflater.inflate(R.layout.receive_details_sticker, container, false);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentName = "Receive sticker info";
        //region Binding Data
        mItemname = (EditText) view.findViewById(R.id.editText_ItemName);
        mbtnOK = (Button) view.findViewById(R.id.btn_ok_receive_item);
        mbtnCANCEL = (Button) view.findViewById(R.id.btn_cancel_receive_item);
        mPO_no = (EditText) view.findViewById(R.id.editText_Po);
        mLot = (EditText) view.findViewById(R.id.editText_Lot);
        mTotal = (EditText) view.findViewById(R.id.editText_Total);
        mQtyPerPack = (EditText) view.findViewById(R.id.editText_PerPack);
        mSticker = (EditText) view.findViewById(R.id.editText_Sticker);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_list_item_receive);
        mCautionDecimal = (TextView) view.findViewById(R.id.txt_caution);
        JSONArray data = new JSONArray();
        //endregion
        if (!Po.equals("!"))
            mPO_no.setText(Po);
        else if (!ReceiveDetailsNoStickerFragment.Po.equals("!"))
            mPO_no.setText(ReceiveDetailsNoStickerFragment.Po);

        if (!Lot.equals("!"))
            mLot.setText(Lot);
        else if (!ReceiveDetailsNoStickerFragment.Lot.equals("!"))
            mLot.setText(ReceiveDetailsNoStickerFragment.Lot);

        mItemname.setText(getArguments().getString("ItemName"));
        mTotal.setText(String.valueOf(x));
        mSticker.setText(String.valueOf(y));
        mQtyPerPack.setText(String.valueOf(ComputePerPack));
        CheckSerial = getArguments().getString("HasSerial");

        //region Calculator QTY PER PACK
        mTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mTotal.getText().toString().isEmpty())
                {
                    mQtyPerPack.setText("");
                } else if (mSticker.getText().toString().isEmpty()) {
                    mQtyPerPack.setText("");
                } else {
                    x = Double.parseDouble(mTotal.getText().toString()) ;
                    y = Integer.parseInt(mSticker.getText().toString());

                    ComputePerPack = x/y;
                    int Result = (int) (x/y);
                    mQtyPerPack.setText(String.valueOf(Result));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSticker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mTotal.getText().toString().isEmpty())
                {
                    mQtyPerPack.setText("");
                } else if (mSticker.getText().toString().isEmpty()) {
                    mQtyPerPack.setText("");
                } else {
                    x = Double.parseDouble(mTotal.getText().toString()) ;
                    y = Integer.parseInt(mSticker.getText().toString());

                    ComputePerPack = x/y;
                    int Result = (int) (x/y);
                    mQtyPerPack.setText(String.valueOf(Result));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//endregion

        mTotal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    if (Double.parseDouble(mTotal.getText().toString()) == 0) {
                        mTotal.getText().clear();
                        mbtnOK.setEnabled(false);
                    }

                } else {
                    if (mTotal.getText().toString().equals("") ||
                            Double.parseDouble(mTotal.getText().toString()) == 0) {
                        mTotal.setText("0.0");
                    }
                    mbtnOK.setEnabled(true);
                }
            }
        });

        mSticker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (Double.parseDouble(mSticker.getText().toString()) == 1) {
                        mSticker.getText().clear();
                        mbtnOK.setEnabled(false);
                    }

                } else {
                    if (mSticker.getText().toString().equals("") ||
                            Double.parseDouble(mSticker.getText().toString()) == 1 ||
                            Double.parseDouble(mSticker.getText().toString()) == 0 ){
                        mSticker.setText("1");
                    }
                    mbtnOK.setEnabled(true);
                }
            }
        });

        mSticker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!mSticker.getText().toString().equals("")) {
                    mbtnOK.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!mTotal.getText().toString().equals("")) {
                    mbtnOK.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //region Button Cancel
        mbtnCANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        //endregion

        //region Button Ok
        mbtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject args = new JSONObject();

                if (mTotal.getText().toString().equals("") ||
                        mTotal.getText().toString().equals(0) ||
                        mTotal.getText().toString().equals("0")||
                        mTotal.getText().toString().equals(0.0) ||
                        mTotal.getText().toString().equals("0.0")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Please insert total quatity.");
                    builder.setMessage("Total quatity must grater than 0 or not blank.");
                    builder.setPositiveButton("OK",null);
                    builder.show();
                } else {
                    try {
                        args.accumulate("Check","AddReceive");

                        args.accumulate("ItemCode", getArguments().getString("ItemCode"));
                        args.accumulate("ItemName", mItemname.getText().toString());
                        args.accumulate("HasSticker",getArguments().getString("HasSticker"));
                        args.accumulate("HasSerial", getArguments().getString("HasSerial"));

                        if (mLot.getText().toString().equals("") || mLot.getText().toString().equals("-"))
                            args.accumulate("Lot","");
                        else
                            args.accumulate("Lot",mLot.getText().toString());

                        if (mPO_no.getText().toString().equals("-") || mPO_no.getText().toString().equals(""))
                            args.accumulate("PONo","");
                        else
                            args.accumulate("PONo",mPO_no.getText().toString());

                        args.accumulate("TotalQty", Double.parseDouble(mTotal.getText().toString()));
                        args.accumulate("Pack", Integer.parseInt(mSticker.getText().toString()));
                        args.accumulate("QtyPerPack", Double.parseDouble(mQtyPerPack.getText().toString()));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Po = mPO_no.getText().toString();
                    Lot = mLot.getText().toString();

                    if (getArguments().getString("HasSerial").equals("true")) {
                        Bundle config = new Bundle();
                        Fragment s = new ReceiveSerialScanFragment();

                        try {
                            config.putSerializable("Appconfig", appconfig);
                            config.putString("Check", args.getString("Check"));
                            config.putString("ItemCode", args.getString("ItemCode"));
                            config.putString("ItemName", args.getString("ItemName"));
                            config.putString("HasSticker", args.getString("HasSticker"));
                            config.putString("HasSerial", args.getString("HasSerial"));
                            config.putString("Lot", args.getString("Lot"));
                            config.putString("PONo", args.getString("PONo"));
                            config.putDouble("TotalQty", Double.parseDouble(mTotal.getText().toString()));
                            config.putInt("Pack", Integer.parseInt(mSticker.getText().toString()));
                            config.putDouble("QtyPerPack", Double.parseDouble(mQtyPerPack.getText().toString()));
                            config.putInt("Seq", getArguments().getInt("Seq"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        s.setArguments(config);
                        getFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                .replace(R.id.FragmentMain, s, "tag_detail_receive_serial")
                                .addToBackStack("ReceiveSerial").commit();
                    } else {

                        Bundle config = new Bundle();
                        try {
                            config.putString("Check", args.getString("Check"));
                            config.putString("ItemCode", args.getString("ItemCode"));
                            config.putString("ItemName", args.getString("ItemName"));
                            config.putString("HasSticker", args.getString("HasSticker"));
                            config.putString("HasSerial", args.getString("HasSerial"));
                            config.putString("Lot", args.getString("Lot"));
                            config.putString("PONo", args.getString("PONo"));
                            config.putDouble("TotalQty", Double.parseDouble(mTotal.getText().toString()));
                            config.putInt("Pack", Integer.parseInt(mSticker.getText().toString()));
                            config.putDouble("QtyPerPack", Double.parseDouble(mQtyPerPack.getText().toString()));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Fragment tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
                        tmp.setArguments(config);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction()
                                .replace(R.id.FragmentMain, tmp, "tag_receiving")
                                .addToBackStack("MenuReceive")
                                .commit();
                    }
                }
            }
        });
        //endregion
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
