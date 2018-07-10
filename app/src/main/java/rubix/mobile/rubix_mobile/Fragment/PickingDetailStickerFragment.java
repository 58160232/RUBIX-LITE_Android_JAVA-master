package rubix.mobile.rubix_mobile.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.AsyncTaskAdapter;
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;

import static rubix.mobile.rubix_mobile.MainActivity.FragmentName;

/**
 * Created by niwat on 8/2/2561.
 */

public class PickingDetailStickerFragment extends Fragment {

    private EditText mItemName;
    private EditText mPO;
    private EditText mLot;
    private EditText mTotal;
    private EditText mLocation;
    private com.toptoche.searchablespinnerlibrary.SearchableSpinner mSpin_Cus;
    private TextView mTxtLocaton;
    private Button mBtnOk;
    private String Detail;
    private AsyncTaskAdapter mLoadDetials;
    private String CheckSticker;
    private JSONObject data = new JSONObject();
    public static String Po = "!";
    public static String Lot = "!";
    ArrayAdapter<String> adapter;
    ArrayList<String> arr_customer = new ArrayList<String>();
    int positionCus = -1;
    private String CusResult = "";
    Appconfig appconfig;

    public PickingDetailStickerFragment() {

    }

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

        return inflater.inflate(R.layout.picking_by_sticker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentName = "Picking info";

        //region Binding Data
        mItemName = (EditText) view.findViewById(R.id.editText_ItemName);
        mPO = (EditText) view.findViewById(R.id.editText_Po);
        mLot = (EditText) view.findViewById(R.id.editText_Lot);
        mTotal = (EditText) view.findViewById(R.id.editText_Total);
        mLocation = (EditText) view.findViewById(R.id.editText_Location);
        mBtnOk = (Button) view.findViewById(R.id.btn_ok_picking_item);
        mTxtLocaton = (TextView) view.findViewById(R.id.txtLocation);
        mSpin_Cus = (com.toptoche.searchablespinnerlibrary.SearchableSpinner) view.findViewById(R.id.spinner_Customer);
        CheckSticker = getArguments().getString("CheckSticker");
        //endregion

        //region initial customer box
        AsyncTaskAdapter LoadCustomer = new AsyncTaskAdapter(new Gson().toJson(""), appconfig);
        LoadCustomer.execute("api/Common/LoadCustomer");
        try {
            String load_customer = LoadCustomer.get(10000, TimeUnit.MILLISECONDS).toString();
            JSONArray getCustomer = new JSONArray(load_customer);

            arr_customer.add("Please select Customer.");

            for (int i = 0 ; i < getCustomer.length() ; i++)
            {
                arr_customer.add(getCustomer.getJSONObject(i).getString("Display"));
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

        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, arr_customer);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpin_Cus.setTitle("Select Customer.");
        mSpin_Cus.setAdapter(adapter);
        //endregion

        //region remember info
        if (!Po.equals("!"))
            mPO.setText(Po);

        if (!Lot.equals("!"))
            mLot.setText(Lot);
        //endregion

        try {

            //region Check StickerBarcode
            if (CheckSticker.equals("1"))
            {
                mLoadDetials = new AsyncTaskAdapter((new Gson().toJson(getArguments().getString("StickerCode"))), appconfig);
                mLoadDetials.execute("api/Common/LoadStickerDetail");
            } else {
                mLoadDetials = new AsyncTaskAdapter((new Gson().toJson(getArguments().getString("Barcode"))), appconfig);
                mLoadDetials.execute("api/Common/LoadItemDetail");
            }

            Detail = mLoadDetials.get(10000,TimeUnit.MILLISECONDS).toString();
            JSONObject getItemDetails = new JSONObject(Detail.substring(2, Detail.length() - 1));
            data = getItemDetails;

            mItemName.setText(data.getString("ItemName"));
            mTotal.setText(data.getString("QtyPerPack"));

            if (data.getString("HasTransit").toString().equals("true")) {
                mTxtLocaton.setVisibility(View.VISIBLE);
                mLocation.setText(data.getString("LocationCode"));
                mLocation.setVisibility(View.VISIBLE);
            }
            //endregion

            mTotal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                    {
                        if (Double.parseDouble(mTotal.getText().toString()) == 0) {
                            mTotal.getText().clear();
                            mBtnOk.setEnabled(false);
                        }
                    } else {
                        if (mTotal.getText().toString().equals("") || Double.parseDouble(mTotal.getText().toString()) == 0){
                            try {
                                mTotal.setText(data.getString("QtyPerPack"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mBtnOk.setEnabled(true);
                    }
                }
            });



            //region Button OK
            mBtnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CusResult = mSpin_Cus.getSelectedItem().toString();

                    if (mTotal.getText().toString().equals("") || mTotal.getText().toString().equals(0) || mTotal.getText().toString().equals("0")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Please insert total quatity.");
                        builder.setMessage("Total quatity must grater than 0 or not blank.");
                        builder.setPositiveButton("OK",null);
                        builder.show();
                    } else {
                        Po = mPO.getText().toString();
                        Lot = mLot.getText().toString();

                        try {
                            if (CheckSticker.equals("1")) {
                                //region Confirm By Sticker
                                Bundle config = new Bundle();
                                Fragment s = new PickingSerialScanFragment();

//                                args.accumulate("CheckPick", "1");
//                                args.accumulate("CheckSticker", CheckSticker.toString());
//                                args.accumulate("HasTransit", data.getString("HasTransit"));
//                                args.accumulate("HasSerial", data.getString("HasSerial"));
//                                args.accumulate("NeedCheckExistSerial", data.getString("NeedCheckExistSerial"));
//
//                                args.accumulate("ItemCode", data.getString("ItemCode"));
//                                args.accumulate("ItemName", mItemName.getText().toString());
//                                args.accumulate("Qty", Double.parseDouble(mTotal.getText().toString()));
//
//                                if (mLot.getText().toString().equals("") || mLot.getText().toString().equals("-"))
//                                    args.accumulate("Lot","");
//                                else
//                                    args.accumulate("Lot",mLot.getText().toString());
//
//                                if (mPO.getText().toString().equals("-") || mPO.getText().toString().equals(""))
//                                    args.accumulate("PO","");
//                                else
//                                    args.accumulate("PO",mPO.getText().toString());
//
//                                if (mLocation.getText().toString().equals("-") || mLocation.getText().toString().equals(""))
//                                    args.accumulate("Loc","");
//                                else
//                                    args.accumulate("Loc",mLocation.getText().toString());
//
//                                args.accumulate("CustomerCode", CusResult.substring(0, CusResult.indexOf(" ")));
//
//                                args.accumulate("StickerCode", getArguments().getString("StickerCode"));
                                config.putSerializable("Appconfig",  appconfig);
                                config.putString("CheckPick", "1");
                                config.putString("CheckSticker", CheckSticker.toString());

                                config.putString("ItemCode", data.getString("ItemCode"));
                                config.putString("ItemName", mItemName.getText().toString());
                                config.putString("HasTransit", data.getString("HasTransit"));
                                config.putString("HasSerial", data.getString("HasSerial"));
                                config.putDouble("Qty", Double.parseDouble(mTotal.getText().toString()));
                                config.putString("NeedCheckExistSerial",  data.getString("NeedCheckExistSerial"));
                                config.putString("Lot",mLot.getText().toString());
                                config.putString("PO",mPO.getText().toString());
                                config.putString("Loc",mLocation.getText().toString());
                                config.putString("CustomerCode", CusResult.substring(0, CusResult.indexOf(" ")));
                                config.putString("StickerCode", getArguments().getString("StickerCode"));
                                config.putInt("Seq", getArguments().getInt("Seq"));

                                if (data.getString("HasSerial").equals("true")) {
                                    s.setArguments(config);
                                    getFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                            .replace(R.id.FragmentMain, s, "tag_detail_pick_serial")
                                            .addToBackStack("PickingSerial").commit();
                                } else {

                                    Fragment tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_picking");
                                    tmp.setArguments(config);

                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    fm.popBackStack("PickBySticker",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                    fm.beginTransaction()
                                            .replace(R.id.FragmentMain, tmp, "tag_picking")
                                            .addToBackStack("MenuPick")
                                            .commit();
                                }
                                //endregion
                            } else {
                                //region Confirm By Barcode

//                                args.accumulate("CheckPick", "1");
//                                args.accumulate("CheckSticker", CheckSticker.toString());
//                                args.accumulate("HasTransit", data.getString("HasTransit"));
//                                args.accumulate("HasSerial", data.getString("HasSerial"));
//                                args.accumulate("NeedCheckExistSerial", data.getString("NeedCheckExistSerial"));
//
//                                args.accumulate("ItemCode", data.getString("ItemCode"));
//                                args.accumulate("ItemName", data.getString("ItemName"));
//                                args.accumulate("Qty",Double.parseDouble(mTotal.getText().toString()));
//
//                                if (mLot.getText().toString().equals("") || mLot.getText().toString().equals("-"))
//                                    args.accumulate("Lot","");
//                                else
//                                    args.accumulate("Lot",mLot.getText().toString());
//
//                                if (mPO.getText().toString().equals("-") || mPO.getText().toString().equals(""))
//                                    args.accumulate("PO","");
//                                else
//                                    args.accumulate("PO",mPO.getText().toString());
//
//                                if (mLocation.getText().toString().equals("-") || mLocation.getText().toString().equals(""))
//                                    args.accumulate("Loc","");
//                                else
//                                    args.accumulate("Loc",mLocation.getText().toString());
//
//                                args.accumulate("CustomerCode", CusResult.substring(0, CusResult.indexOf(" ")));
//
//                                args.accumulate("Barcode", getArguments().getString("Barcode"));

                                Bundle config = new Bundle();

                                config.putSerializable("Appconfig",  appconfig);
                                config.putString("CheckPick", "1");
                                config.putString("CheckSticker", CheckSticker.toString());
                                config.putString("ItemCode", data.getString("ItemCode"));
                                config.putString("ItemName", mItemName.getText().toString());
                                config.putString("HasTransit", data.getString("HasTransit"));
                                config.putString("HasSerial", data.getString("HasSerial"));
                                config.putDouble("Qty", Double.parseDouble(mTotal.getText().toString()));
                                config.putString("NeedCheckExistSerial",  data.getString("NeedCheckExistSerial"));
                                config.putString("Lot",mLot.getText().toString());
                                config.putString("PO",mPO.getText().toString());
                                config.putString("Loc",mLocation.getText().toString());
                                config.putString("CustomerCode", CusResult.substring(0, CusResult.indexOf(" ")));
                                config.putString("StickerCode", getArguments().getString("StickerCode"));
                                config.putInt("Seq", getArguments().getInt("Seq"));


                                Fragment tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_picking");
                                tmp.setArguments(config);

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                fm.popBackStack("PickBySticker",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                fm.beginTransaction()
                                        .replace(R.id.FragmentMain, tmp, "tag_picking")
                                        .addToBackStack("MenuPick")
                                        .commit();

                                //endregion
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            //endregion

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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onButtonPressed(JSONObject data) {

    }
}
