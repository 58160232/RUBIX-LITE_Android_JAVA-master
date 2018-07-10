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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.R;
import rubix.mobile.rubix_mobile.ReceiveAdapter;

import static rubix.mobile.rubix_mobile.MainActivity.FragmentName;

/**
 * Created by niwat on 29/1/2561.
 */

public class ReceiveDetailsNoStickerFragment extends Fragment {
    EditText mItemname;
    private EditText mPO_no;
    private EditText mLot;
    private EditText mTotal;
    private Button mbtnOK;
    private Button mbtnCANCEL;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ReceiveAdapter mAdapter;
//    private OnFragmentInteractionListener mListener;
    public static String Po = "!";
    public static String Lot = "!";
    Appconfig appconfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.receive_details_no_sticker, container, false);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
        FragmentName = "Receive no sticker info";
        //region Binding Data
        mItemname = (EditText) view.findViewById(R.id.editText_ItemName);
        mbtnOK = (Button) view.findViewById(R.id.btn_ok_receive_item);
        mbtnCANCEL = (Button) view.findViewById(R.id.btn_cancel_receive_item);
        mPO_no = (EditText) view.findViewById(R.id.editText_Po);
        mLot = (EditText) view.findViewById(R.id.editText_Lot);
        mTotal = (EditText) view.findViewById(R.id.editText_Total);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_list_item_receive);
        mTotal.setText("0.0");

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

        mTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mTotal.getText().toString().isEmpty())
                {
                    mbtnOK.setEnabled(false);
                } else {
                    mbtnOK.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //endregion
        final JSONArray data = new JSONArray();

        if (!Po.equals("!"))
            mPO_no.setText(Po);
        else if (!ReceiveDetailsStickerFragment.Po.equals("!"))
            mPO_no.setText(ReceiveDetailsStickerFragment.Po);

        if (!Lot.equals("!"))
            mLot.setText(Lot);
        else if (!ReceiveDetailsStickerFragment.Lot.equals("!"))
            mLot.setText(ReceiveDetailsStickerFragment.Lot);

        mItemname.setText(getArguments().getString("ItemName"));
        //Button Cancel
        mbtnCANCEL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        //region Button OK
        mbtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject args = new JSONObject();
                if (mTotal.getText().toString().equals("") ||
                        mTotal.getText().toString().equals(0) ||
                        mTotal.getText().toString().equals("0")||
                        Double.parseDouble(mTotal.getText().toString()) == 0)
                {
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

                        args.accumulate("TotalQty",Double.parseDouble(mTotal.getText().toString()));
                        args.accumulate("Pack",0);
                        args.accumulate("QtyPerPack",0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Po = mPO_no.getText().toString();
                    Lot = mLot.getText().toString();

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Bundle config = new Bundle();

                    try {
                        config.putString("Check", args.getString("Check"));
                        config.putString("ItemCode", args.getString("ItemCode"));
                        config.putString("ItemName", args.getString("ItemName"));
                        config.putString("HasSticker", args.getString("HasSticker"));
                        config.putString("HasSerial", args.getString("HasSerial"));
                        config.putString("Lot", args.getString("Lot"));
                        config.putString("PONo", args.getString("PONo"));
                        config.putDouble("TotalQty", Double.parseDouble(args.getString("TotalQty")));
                        config.putInt("Pack", Integer.parseInt(args.getString("Pack")));
                        config.putDouble("QtyPerPack", Double.parseDouble(args.getString("QtyPerPack")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Fragment tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
                    tmp.setArguments(config);

                    fm.popBackStack("ReceiveNoSticker",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    fm.beginTransaction()
                            .replace(R.id.FragmentMain, tmp, "tag_receiving")
                            .addToBackStack("MenuReceive")
                            .commit();

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
