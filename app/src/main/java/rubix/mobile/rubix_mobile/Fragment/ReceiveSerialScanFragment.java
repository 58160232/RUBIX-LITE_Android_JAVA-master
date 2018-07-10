package rubix.mobile.rubix_mobile.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import rubix.mobile.rubix_mobile.ReceiveSerialAdapter;
import rubix.mobile.rubix_mobile.SwipeHelper;

import static rubix.mobile.rubix_mobile.MainActivity.FragmentName;
//import rubix.mobile.rubix_mobile.RecycleView

/**
 * Created by niwat on 1/2/2561.
 */

public class ReceiveSerialScanFragment extends Fragment {

    getDataListener mGetData;
    private TextView mTxtCount;
    private EditText mScanSerial;
    private Button mBtnScanSerial;
    private Button mBtnSerialOK;
    private Button mBtnSerialConfirm;
    private RecyclerView mRecycleViewSerial;
    private ReceiveSerialAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Double totalSerial;
    Appconfig appconfig;
    Bundle data = new Bundle();
    JSONArray dtSerail = new JSONArray();
    private JSONObject temp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (appconfig == null)
            appconfig = (Appconfig) getArguments().getSerializable("Appconfig");
        if (!appconfig.checkstate())
            ((MainActivity) getActivity()).restartApp();

        if (getArguments() != null) {
            totalSerial = getArguments().getDouble("TotalQty");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.receive_serial, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentName = "Receive Serial";
        //region Binding Data
        mScanSerial = (EditText) view.findViewById(R.id.editText_serial);
        mBtnScanSerial = (Button) view.findViewById(R.id.btn_Scan_serial);
        mBtnSerialOK = (Button) view.findViewById(R.id.btn_ok_serial);
        mBtnSerialConfirm = (Button) view.findViewById(R.id.btn_serial_confirm);
        mRecycleViewSerial = (RecyclerView) view.findViewById(R.id.recycleview_serial);
        mTxtCount = (TextView) view.findViewById(R.id.txtNumSerial);
        //endregion
        mTxtCount.setText(String.valueOf(totalSerial));
        //region Button OK
        mBtnSerialOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((MainActivity)getActivity()).checkinternet()) {
                    if (totalSerial != 0) {
                        int checkDuplicateSerial = 0;
                        int clearText = 0;

                        if (MainActivity.dt_serial_receive.length() != 0) {
                            for (int i = 0; i < MainActivity.dt_serial_receive.length(); i++) {
                                try {
                                    if (mScanSerial.getText().toString().equals(MainActivity.dt_serial_receive.getJSONObject(i).getString("Serial"))) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Warning.");
                                        builder.setMessage("Serial is duplicate!");
                                        builder.setNegativeButton("OK", null);
                                        builder.show();
                                        mScanSerial.getText().clear();
                                        clearText = 1;
                                        break;
                                    } else {
                                        checkDuplicateSerial += 1;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (mScanSerial.getText().toString().equals("")) {
                            if (clearText == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Warning.");
                                builder.setMessage("Please insert serial number!");
                                builder.setNegativeButton("OK", null);
                                builder.show();
                                mScanSerial.getText().clear();
                            } else {
                            }
                        } else if (dtSerail.length() != 0 && checkDuplicateSerial == MainActivity.dt_serial_receive.length()) {
                            int count = 0;
                            for (int i = 0; i < dtSerail.length(); i++) {
                                try {
                                    if (mScanSerial.getText().toString().equals(new JSONObject(dtSerail.get(i).toString()).getString("Serial"))) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        builder.setTitle("Warning.");
                                        builder.setMessage("Serial is duplicate!");
                                        builder.setNegativeButton("OK", null);
                                        builder.show();
                                        mScanSerial.getText().clear();
                                        break;
                                    } else {
                                        count += 1;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            String checkExist = "";
                            try {
                                JSONObject js_check = new JSONObject();
                                js_check.accumulate("serial", (mScanSerial.getText().toString()));
                                js_check.accumulate("typeOfSeral", 0);
                                AsyncTaskAdapter mCheckExist = new AsyncTaskAdapter(js_check, appconfig);
                                mCheckExist.execute("api/Common/CheckExistSerial");

                                checkExist = mCheckExist.get(10000, TimeUnit.MILLISECONDS).toString();

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

                            if (checkExist.equals("true")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Warning.");
                                builder.setMessage("Serial is duplicate!");
                                builder.setNegativeButton("OK", null);
                                builder.show();
                                mScanSerial.getText().clear();

                            } else if (count == dtSerail.length()) {
                                JSONObject args = new JSONObject();
                                try {

                                    args.accumulate("Serial", mScanSerial.getText().toString());
                                    args.accumulate("ItemCode", getArguments().getString("ItemCode"));
                                    args.accumulate("Seq", getArguments().getInt("Seq"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                dtSerail.put(args);

                                mRecycleViewSerial.setHasFixedSize(true);

                                mLayoutManager = new LinearLayoutManager(getActivity());
                                mRecycleViewSerial.setLayoutManager(mLayoutManager);

                                mAdapter = new ReceiveSerialAdapter(getActivity(), dtSerail);
                                mRecycleViewSerial.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();


                                totalSerial = totalSerial - 1;
                                mScanSerial.setText("");
                                mTxtCount.setText(String.valueOf(totalSerial));
                            }

                        } else {
                            JSONObject args = new JSONObject();
                            String checkExist = "";
                            try {
                                JSONObject js_check = new JSONObject();
                                js_check.accumulate("serial", (mScanSerial.getText().toString()));
                                js_check.accumulate("typeOfSeral", 0);
                                AsyncTaskAdapter mCheckExist = new AsyncTaskAdapter(js_check, appconfig);
                                mCheckExist.execute("api/Common/CheckExistSerial");
                                checkExist = mCheckExist.get(10000,TimeUnit.MILLISECONDS).toString();

                                if (checkExist.equals("true")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Warning.");
                                    builder.setMessage("Serial is duplicate!");
                                    builder.setNegativeButton("OK", null);
                                    builder.show();
                                    mScanSerial.getText().clear();
                                } else {
                                    args.accumulate("Serial", mScanSerial.getText().toString());
                                    args.accumulate("ItemCode", getArguments().getString("ItemCode"));
                                    args.accumulate("Seq", getArguments().getInt("Seq"));

                                    dtSerail.put(args);

                                    mRecycleViewSerial.setHasFixedSize(true);

                                    mLayoutManager = new LinearLayoutManager(getActivity());
                                    mRecycleViewSerial.setLayoutManager(mLayoutManager);

                                    mAdapter = new ReceiveSerialAdapter(getActivity(), dtSerail);
                                    mRecycleViewSerial.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();


                                    totalSerial = totalSerial - 1;
                                    mScanSerial.setText("");
                                    mTxtCount.setText(String.valueOf(totalSerial));
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
                    if (totalSerial == 0) {
                        mBtnSerialConfirm.setEnabled(true);
                        mBtnSerialConfirm.setTextColor(getActivity().getResources().getColor(R.color.bg_gray));
                        mScanSerial.setText("");
                        mScanSerial.setEnabled(false);
                        mBtnScanSerial.setEnabled(false);
                        mBtnSerialOK.setEnabled(false);
                    }
                }else{
                    Toast.makeText(getActivity(),"Please connect internet",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion
        //region Button Confirm
        mBtnSerialConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                mGetData.setReciveDataSerial(dtSerail);

                fm.popBackStack("ReceiveSticker", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fm.popBackStack("ReceiveSerial", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                Fragment tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
                tmp.setArguments(data);
                fm.beginTransaction()
                        .replace(R.id.FragmentMain, tmp, "tag_receiving")
                        .addToBackStack("MenuReceive").commit();
            }
        });
        //endregion
        //region Button Scan Serial
        mBtnScanSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Receive_ScanSerial"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion
        //region Swipe
        new SwipeHelper(getActivity(), mRecycleViewSerial) {
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
                                mTxtCount.setText(String.valueOf(++totalSerial));
                                if (totalSerial != 0)
                                {
                                    mBtnSerialConfirm.setEnabled(false);
                                    mScanSerial.setText("");
                                    mScanSerial.setEnabled(true);
                                    mBtnScanSerial.setEnabled(true);
                                    mBtnSerialOK.setEnabled(true);
                                }
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "",
                        R.drawable.ic_mode_edit_white_24dp,
                        Color.parseColor("#76FF03"), getActivity(),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                try {
                                    temp = new JSONObject(dtSerail.get(pos).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setCancelable(false);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_edit_serial);
                                dialog.show();
                                final EditText Serial = (EditText) dialog.findViewById(R.id.dialogseral_serial);
                                Button btncancel = (Button) dialog.findViewById(R.id.dialogserial_edit_cancel);
                                Button btnok = (Button) dialog.findViewById(R.id.dialogserial_edit_ok);
                                try {
                                    Serial.setText(temp.getString("Serial"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mAdapter.remove(pos);
                                btnok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Serial.getText().toString().trim().isEmpty()) {
                                            Toast.makeText(getActivity(), "input serial", Toast.LENGTH_SHORT).show();
                                        } else {
                                            int count = 1+ 1;
                                            mScanSerial.setText(Serial.getText().toString());
                                            mBtnSerialOK.callOnClick();
                                            if(1 == count-1)
                                                mAdapter.recover();
                                            totalSerial = totalSerial - 1;
                                            mTxtCount.setText(String.valueOf(totalSerial));
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                btncancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAdapter.recover();
                                        dialog.dismiss();
                                    }
                                });
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                ));
            }
        };
        //endregion
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();

        mRecycleViewSerial.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleViewSerial.setLayoutManager(mLayoutManager);

        mAdapter = new ReceiveSerialAdapter(getActivity(), dtSerail);
        mRecycleViewSerial.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if (getArguments() != null) {
            if (getArguments().getString("WhereScan") != null) {
                if (getArguments().getString("WhereScan").equals("Serial")) {
                    mScanSerial.setText(getArguments().getString("result"));
                    mBtnSerialOK.callOnClick();
                }
            }

            if (getArguments().getString("Check") != null) {
                if (getArguments().getString("Check").equals("AddReceive")) {
                    data.putString("Check", getArguments().getString("Check"));
                    data.putString("ItemCode", getArguments().getString("ItemCode"));
                    data.putString("ItemName", getArguments().getString("ItemName"));
                    data.putString("HasSticker", getArguments().getString("HasSticker"));
                    data.putString("HasSerial", getArguments().getString("HasSerial"));
                    data.putString("Lot", getArguments().getString("Lot"));
                    data.putString("PONo", getArguments().getString("PONo"));
                    data.putDouble("TotalQty", getArguments().getDouble("TotalQty"));
                    data.putInt("Pack", getArguments().getInt("Pack"));
                    data.putDouble("QtyPerPack", getArguments().getDouble("QtyPerPack"));
                    data.putInt("Seq", getArguments().getInt("Seq"));
                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof getDataListener) {
            mGetData = (getDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement getData");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGetData = null;
    }

    public interface getDataListener {
        void setReciveDataSerial(JSONArray data);
    }

}
