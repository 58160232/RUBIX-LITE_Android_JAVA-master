package rubix.mobile.rubix_mobile.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rubix.mobile.rubix_mobile.Appconfig;
import rubix.mobile.rubix_mobile.AsyncTaskAdapter;
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;

import static rubix.mobile.rubix_mobile.MainActivity.FragmentName;

/**
 * Created by niwat on 6/2/2561.
 */

public class AddItemFragment extends Fragment {
    private EditText mAddItemBacode;
    private EditText mAddItemItemName;
    private EditText mAddItemCode;
    private CheckBox mAddCheckBoxSerial;
    private CheckBox mAddCheckBoxSticker;
    private CheckBox mAddCheckBoxTransit;
    private Button mAddSearchItem;
    private Button mAddBtnConfirm;
    private Button mScanItemCode;
    private LinearLayout mLinearAddNew;
    private Appconfig appconfig;
    private int MethodCheckDelete;
    private JSONObject data = new JSONObject();
    public AddItemFragment() {
        super();
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
        if(!appconfig.checkstate())
            ((MainActivity)getActivity()).restartApp();
        return inflater.inflate(R.layout.dialog_add_new_item, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentName = "Add New Item";
        //region Binding Data
        mAddItemBacode = (EditText) view.findViewById(R.id.editText_item);
        mAddItemItemName = (EditText) view.findViewById(R.id.editText_itemname);
        mAddItemCode = (EditText) view.findViewById(R.id.editText_itemcode);
        mAddCheckBoxSerial = (CheckBox) view.findViewById(R.id.checkBox_serial);
        mAddCheckBoxSticker = (CheckBox) view.findViewById(R.id.checkBox_sticker);
        mAddCheckBoxTransit = (CheckBox) view.findViewById(R.id.checkBox_transit);
        mAddSearchItem = (Button) view.findViewById(R.id.btn_search_itemcode);
        mAddBtnConfirm = (Button) view.findViewById(R.id.btn_ok_add);
        mScanItemCode = (Button) view.findViewById(R.id.btn_scan_itemcode);
        mLinearAddNew = (LinearLayout) view.findViewById(R.id.add_item_new);
        //endregion
        //region Check Item
        mAddSearchItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAddItemCode.getText().toString().equals("")) {
                    AsyncTaskAdapter mapping = new AsyncTaskAdapter(new Gson().toJson(mAddItemCode.getText().toString()), appconfig);
                    mapping.execute("api/Common/CheckExistItem");
                    try {
                        String checkItemcode = mapping.get(14, TimeUnit.SECONDS).toString();

                        //region Mapping item
                        if (checkItemcode.equals("true")){
                            Handler Scan = new Handler();
                            Scan.postDelayed(new Runnable() {
                                public void run() {
                                    mAddSearchItem.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
                                    mAddSearchItem.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.transit_color));
                                    mAddSearchItem.setEnabled(false);
                                    mAddItemCode.setEnabled(false);
                                }
                            }, 500);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Do you want to mapping item?");
                                    builder.setMessage("Barcode: " + mAddItemBacode.getText().toString() + "\n"
                                            + "mapping with\n" + "ItemCode : " + mAddItemCode.getText().toString());
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            JSONObject js_add_item = new JSONObject();
                                            try {
                                                js_add_item.accumulate("Barcode", mAddItemBacode.getText().toString());
                                                js_add_item.accumulate("Code", mAddItemCode.getText().toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            AsyncTaskAdapter addItem = new AsyncTaskAdapter(js_add_item, appconfig);
                                            addItem.execute("api/MobileReceiving/MappingBarcode");
                                            FragmentName = "Add New Item press ok";

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                                    Bundle config = new Bundle();

                                                    config.putString("Check", "FromMapping");
                                                    config.putString("Status", "Confirm");

                                                    Fragment tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
                                                    tmp.setArguments(config);

                                                    fm.popBackStack("MappingItem",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                                    fm.beginTransaction()
                                                            .replace(R.id.FragmentMain, tmp, "tag_receiving")
                                                            .addToBackStack("MenuReceive")
                                                            .commit();
                                                }
                                            }, 1200);
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            mAddSearchItem.setBackgroundResource(R.drawable.ic_arrow_forward_black_24dp);
                                            mAddSearchItem.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.black));
                                            mAddSearchItem.setEnabled(true);
                                            mAddItemCode.setEnabled(true);
                                            mAddBtnConfirm.setVisibility(View.INVISIBLE);
                                            mAddBtnConfirm.setEnabled(false);
                                            mAddSearchItem.setVisibility(View.VISIBLE);
                                            mLinearAddNew.setVisibility(View.INVISIBLE);
                                            mAddSearchItem.setEnabled(true);
                                            mAddItemCode.setEnabled(true);
                                        }
                                    });
                                    builder.show();

                                }
                            }, 800);

                        }
                        //endregion

                        else {
                            Handler Scan = new Handler();
                            Scan.postDelayed(new Runnable() {
                                public void run() {
                                    mAddSearchItem.setBackgroundResource(R.drawable.ic_cancel_black_24dp);
                                    mAddSearchItem.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.receive_color));
                                }
                            }, 500);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {

                                    AsyncTaskAdapter CheckDeleted = new AsyncTaskAdapter(new Gson().toJson(mAddItemCode.getText().toString()), appconfig);
                                    CheckDeleted.execute("api/Common/CheckItemWasDeleted");

                                    try {
                                        if (CheckDeleted.get(12,TimeUnit.SECONDS).toString().equals("true")) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Item was deleted!");
                                            builder.setMessage("Item was deleted, Do you want to activate item?");
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    FragmentName = "Add New Item press ok";
                                                    MethodCheckDelete = 1;
                                                    mAddSearchItem.setEnabled(false);
                                                    mAddItemCode.setEnabled(false);
                                                    mAddBtnConfirm.setVisibility(View.VISIBLE);
                                                    mAddBtnConfirm.setEnabled(true);
                                                    mAddBtnConfirm.callOnClick();
                                                }
                                            });
                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mAddItemCode.getText().clear();
                                                    mAddSearchItem.setBackgroundResource(R.drawable.ic_arrow_forward_black_24dp);
                                                    mAddSearchItem.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.black));
                                                    mAddSearchItem.setEnabled(true);
                                                    mAddItemCode.setEnabled(true);
                                                    mAddBtnConfirm.setVisibility(View.INVISIBLE);
                                                    mAddBtnConfirm.setEnabled(false);
                                                    mAddSearchItem.setVisibility(View.VISIBLE);
                                                    mLinearAddNew.setVisibility(View.INVISIBLE);
                                                    mAddSearchItem.setEnabled(true);
                                                    mAddItemCode.setEnabled(true);
                                                }
                                            });
                                            builder.show();
                                        } else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Add new item.");
                                            builder.setMessage("Item not found, Do you want to add new item?");
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    FragmentName = "Add New Item press ok";
                                                    MethodCheckDelete = 0;
                                                    mAddSearchItem.setEnabled(false);
                                                    mAddItemCode.setEnabled(false);
                                                    mAddBtnConfirm.setVisibility(View.VISIBLE);
                                                    mAddBtnConfirm.setEnabled(true);
                                                    mAddSearchItem.setVisibility(View.INVISIBLE);
                                                    mLinearAddNew.setVisibility(View.VISIBLE);
                                                }
                                            });
                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mAddItemCode.getText().clear();
                                                    mAddSearchItem.setBackgroundResource(R.drawable.ic_arrow_forward_black_24dp);
                                                    mAddSearchItem.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.black));
                                                    mAddSearchItem.setEnabled(true);
                                                    mAddItemCode.setEnabled(true);
                                                    mAddBtnConfirm.setVisibility(View.INVISIBLE);
                                                    mAddBtnConfirm.setEnabled(false);
                                                    mAddSearchItem.setVisibility(View.VISIBLE);
                                                    mLinearAddNew.setVisibility(View.INVISIBLE);
                                                    mAddSearchItem.setEnabled(true);
                                                    mAddItemCode.setEnabled(true);
                                                }
                                            });
                                            builder.show();
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (TimeoutException e) {
                                        Toast.makeText(getActivity(),"Please check internet",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }
                            }, 1000);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        Toast.makeText(getActivity(),"Please check internet",Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Warning.");
                    builder.setMessage("Please insert ItemCode!");
                    builder.setNegativeButton("OK",null);
                    builder.show();
                }
            }
        });
        //endregion
        //region Button Confirm
        mAddBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject js_add_item = new JSONObject();
                final JSONObject js_map_new_item = new JSONObject();

                final FragmentManager fm = getActivity().getSupportFragmentManager();
                final Fragment tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
                final Bundle config = new Bundle();

                try {
                    //region create new item
                    if (MethodCheckDelete == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Confirm add new item");
                        builder.setMessage("Do you want to add new item for receive?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    js_add_item.accumulate("Barcode", mAddItemBacode.getText().toString());
                                    js_add_item.accumulate("Code", mAddItemCode.getText().toString());
                                    js_add_item.accumulate("Name", mAddItemItemName.getText().toString());
                                    js_add_item.accumulate("NeedSerial", mAddCheckBoxSerial.isChecked());
                                    js_add_item.accumulate("NeedSticker", mAddCheckBoxSticker.isChecked());
                                    js_add_item.accumulate("NeedTransit", mAddCheckBoxTransit.isChecked());

                                    js_map_new_item.accumulate("Barcode", mAddItemBacode.getText().toString());
                                    js_map_new_item.accumulate("Code", mAddItemCode.getText().toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                AsyncTaskAdapter addItem = new AsyncTaskAdapter(js_add_item, appconfig);
                                addItem.execute("api/MobileReceiving/AddItem");

                                AsyncTaskAdapter MapNewItem = new AsyncTaskAdapter(js_map_new_item, appconfig);
                                MapNewItem.execute("api/MobileReceiving/MappingBarcode");

                                config.putString("Check", "FromMapping");
                                config.putString("Status", "Confirm");

                                tmp.setArguments(config);

                                fm.popBackStack("MappingItem",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                fm.beginTransaction()
                                        .replace(R.id.FragmentMain, tmp, "tag_receiving")
                                        .addToBackStack("MenuReceive")
                                        .commit();

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
                    //endregion

                    //region Active Item was deleted
                    else {
                        js_add_item.accumulate("Barcode", mAddItemBacode.getText().toString());
                        js_add_item.accumulate("Code", mAddItemCode.getText().toString());


                        AsyncTaskAdapter addItem = new AsyncTaskAdapter(js_add_item, appconfig);
                        addItem.execute("api/MobileReceiving/AddItem");

                        AsyncTaskAdapter MapNewItem = new AsyncTaskAdapter(js_add_item, appconfig);
                        MapNewItem.execute("api/MobileReceiving/MappingBarcode");

                        config.putString("Check", "FromMapping");
                        config.putString("Status", "Confirm");

                        tmp.setArguments(config);

                        fm.popBackStack("MappingItem",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fm.beginTransaction()
                                .replace(R.id.FragmentMain, tmp, "tag_receiving")
                                .addToBackStack("MenuReceive")
                                .commit();
                    }
                    //endregion

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        //endregion
        //region Cameara Detector ItemCode
        mScanItemCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Receive_AddItem"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion
    }
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();

        try {
            if (getArguments() != null)
            {
                if(getArguments().getString("WhereScan") != null)
                {
                    if (getArguments().getString("WhereScan").equals("AddItem")) {
                        mAddItemCode.setText(getArguments().getString("result"));
                        mAddSearchItem.callOnClick();
                    } else if (getArguments().getString("WhereScan").equals("")) {
                        mAddItemCode.getText().clear();
                    }
                }

                if (getArguments().getString("Barcode") != null)
                    data.accumulate("Barcode", getArguments().getString("Barcode"));
            }
            mAddItemBacode.setText(data.getString("Barcode"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
