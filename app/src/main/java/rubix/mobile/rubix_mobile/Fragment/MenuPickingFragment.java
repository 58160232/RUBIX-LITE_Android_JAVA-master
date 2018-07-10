package rubix.mobile.rubix_mobile.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.PickingAdapter;
import rubix.mobile.rubix_mobile.R;
import rubix.mobile.rubix_mobile.SwipeHelper;

import static rubix.mobile.rubix_mobile.MainActivity.FragmentName;

public class MenuPickingFragment extends Fragment {
    Appconfig appconfig;
    private EditText mBarcode;
    private EditText mWo;
    private EditText mDo;
    private EditText mSo;
    private EditText mPo;
    private EditText mInvoice;
    private TextView mTxtWo;
    private TextView mTxtDo;
    private TextView mTxtSo;
    private TextView mTxtPo;
    private TextView mTxtInvoice;
    private Button mBtnScanBarcode;
    private Button mBtnScanWo;
    private Button mBtnScanDo;
    private Button mBtnScanSo;
    private Button mBtnScanPo;
    private Button mBtnScanInvoice;
    private Button mOk;
    private Button mCollapse;
    private Button mSearch;
    private Button mConfirm;
    private View mViewDivide;
    private GridLayout mLayoutCollapse;
    private LinearLayout mLinearScan;
    private RecyclerView mRecycleItem;
    private PickingAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static JSONArray DtItem = new JSONArray();
    public static JSONArray DtSerial = new JSONArray();
    private static int countCollapse;
    private int Seq = 0;
    private JSONObject temp;
    ArrayList<String> arr_customer = new ArrayList<>();
    private com.toptoche.searchablespinnerlibrary.SearchableSpinner spin_cus;
    public MenuPickingFragment() {
        // Required empty public constructor
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
        if (!appconfig.checkstate())
            ((MainActivity) getActivity()).restartApp();
        return inflater.inflate(R.layout.fragment_menu_picking, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentName = "Picking Details";

        //region Binding Data
        mBarcode = (EditText) view.findViewById(R.id.editText_barcode);
        mWo = (EditText) view.findViewById(R.id.editText_Wo);
        mDo = (EditText) view.findViewById(R.id.editText_DoNo_pick);
        mSo = (EditText) view.findViewById(R.id.editText_So);
        mPo = (EditText) view.findViewById(R.id.editText_Po);
        mInvoice = (EditText) view.findViewById(R.id.editText_Invoice_pick);
        mTxtWo = (TextView) view.findViewById(R.id.txtWo);
        mTxtDo = (TextView) view.findViewById(R.id.txtDoPick);
        mTxtSo = (TextView) view.findViewById(R.id.txtSo);
        mTxtPo = (TextView) view.findViewById(R.id.txtPo);
        mTxtInvoice = (TextView) view.findViewById(R.id.txtInvoicePick);
        mOk = (Button) view.findViewById(R.id.btn_ok_pick);
        mConfirm = (Button) view.findViewById(R.id.btn_confirm);
        mCollapse = (Button) view.findViewById(R.id.btn_collapse);
        mSearch = (Button) view.findViewById(R.id.btn_seach_barcode);
        mBtnScanWo = (Button) view.findViewById(R.id.btn_scan_wo);
        mBtnScanDo = (Button) view.findViewById(R.id.btn_scan_do_pick);
        mBtnScanSo = (Button) view.findViewById(R.id.btn_scan_so);
        mBtnScanPo = (Button) view.findViewById(R.id.btn_scan_po);
        mBtnScanBarcode = (Button) view.findViewById(R.id.btn_Scan_barcode);
        mBtnScanInvoice = (Button) view.findViewById(R.id.btn_scan_invoice_pick);
        mViewDivide = (View) view.findViewById(R.id.view_devide_pick);
        mLayoutCollapse = (GridLayout) view.findViewById(R.id.grid_details);
        mLinearScan = (LinearLayout) view.findViewById(R.id.linear_scanner_pick);
        mRecycleItem = (RecyclerView) view.findViewById(R.id.recycle_list_item_pick);
        mRecycleItem.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleItem.setLayoutManager(mLayoutManager);
        mAdapter = new PickingAdapter(getActivity(), DtItem);
        mRecycleItem.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        //endregion

        //region Button Collapse
        mCollapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countCollapse == 0) {
                    mLayoutCollapse.setVisibility(View.GONE);
                    mCollapse.setBackgroundResource(R.drawable.ic_collapse_00009);
                    countCollapse = 1;
                } else {
                    mLayoutCollapse.setVisibility(View.VISIBLE);
                    mCollapse.setBackgroundResource(R.drawable.ic_collapse);
                    countCollapse = 0;
                }
            }
        });
        //endregion

        //region Check Sticker Barcode
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).checkinternet()) {
                    String BarcodeExist;
                    String StickerExist;

                    AsyncTaskAdapter checkExistBarcode = new AsyncTaskAdapter(new Gson().toJson(mBarcode.getText().toString()), appconfig);
                    checkExistBarcode.execute("api/Common/CheckExistBarcode");

                    AsyncTaskAdapter checkExistSticker = new AsyncTaskAdapter(new Gson().toJson(mBarcode.getText().toString()), appconfig);
                    checkExistSticker.execute("api/Common/CheckExistSticker");

                    try {
                        BarcodeExist = checkExistBarcode.get(10000,TimeUnit.MILLISECONDS).toString();
                        StickerExist = checkExistSticker.get(10000,TimeUnit.MILLISECONDS).toString();

                        Bundle pushValue = new Bundle();

                        pushValue.putSerializable("Appconfig", appconfig);

                        if (BarcodeExist.toString().equals("true")) {
                            AsyncTaskAdapter mLoadDetials = new AsyncTaskAdapter(new Gson().toJson(mBarcode.getText().toString()), appconfig);
                            mLoadDetials.execute("api/Common/LoadItemDetail");

                            String Detial = mLoadDetials.get(10000,TimeUnit.MILLISECONDS).toString();

                            try {
                                JSONObject getItemDetails = new JSONObject(Detial.substring(2, Detial.length() - 1));
                                JSONObject data = getItemDetails;

                                if (data.getString("HasSticker").toString().equals("true")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Warning!");
                                    builder.setMessage("This item has Sticker Control, Please scan sticker code to picking item.");
                                    builder.setNegativeButton("OK", null);
                                    builder.show();
                                    mBarcode.getText().clear();
                                } else {
                                    pushValue.putString("Barcode", mBarcode.getText().toString());
                                    pushValue.putString("CheckSticker", "0");
                                    pushValue.putInt("Seq", Seq);

                                    Fragment s = new PickingDetailStickerFragment();
                                    s.setArguments(pushValue);
                                    mBarcode.getText().clear();
                                    mConfirm.setEnabled(true);
                                    mConfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
                                    mConfirm.setTextColor(getActivity().getResources().getColor(R.color.almost_white));

                                    getFragmentManager()
                                            .beginTransaction()
                                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                            .replace(R.id.FragmentMain, s, "tag_pick_by_barcode")
                                            .addToBackStack("PickBySticker").commit();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (StickerExist.toString().equals("true")) {

                            pushValue.putString("StickerCode", mBarcode.getText().toString());
                            pushValue.putString("CheckSticker", "1");
                            pushValue.putInt("Seq", Seq);
                            Fragment s = new PickingDetailStickerFragment();
                            s.setArguments(pushValue);
                            mBarcode.getText().clear();
                            mConfirm.setEnabled(true);
                            mConfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
                            mConfirm.setTextColor(getActivity().getResources().getColor(R.color.almost_white));

                            getFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .replace(R.id.FragmentMain, s, "tag_pick_by_sticker")
                                    .addToBackStack("PickBySticker").commit();

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Warning!");
                            builder.setMessage("Data not found.");
                            builder.setNegativeButton("OK", null);
                            builder.show();
                            mBarcode.getText().clear();
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
                    Toast.makeText(getActivity(), "Please connect internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //endregion

        //region Button OK
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.checkBackPick(1);
                FragmentName = "Picking Details press ok";
                if (mWo.getText().toString().equals("")) {
                    mWo.setText("-");
                    mWo.setEnabled(false);
                    mWo.setTextSize(12);
                    mWo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtWo.setVisibility(View.VISIBLE);
                } else {
                    mWo.setEnabled(false);
                    mWo.setTextSize(12);
                    mWo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtWo.setVisibility(View.VISIBLE);
                }

                if (mDo.getText().toString().equals("")) {
                    mDo.setText("-");
                    mDo.setEnabled(false);
                    mDo.setTextSize(12);
                    mDo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtDo.setVisibility(View.VISIBLE);
                } else {
                    mDo.setEnabled(false);
                    mDo.setTextSize(12);
                    mDo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtDo.setVisibility(View.VISIBLE);
                }

                if (mSo.getText().toString().equals("")) {
                    mSo.setText("-");
                    mSo.setEnabled(false);
                    mSo.setTextSize(12);
                    mSo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtSo.setVisibility(View.VISIBLE);
                } else {
                    mSo.setEnabled(false);
                    mSo.setTextSize(12);
                    mSo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtSo.setVisibility(View.VISIBLE);
                }

                if (mPo.getText().toString().equals("")) {
                    mPo.setText("-");
                    mPo.setEnabled(false);
                    mPo.setTextSize(12);
                    mPo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtPo.setVisibility(View.VISIBLE);
                } else {
                    mPo.setEnabled(false);
                    mPo.setTextSize(12);
                    mPo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    mTxtPo.setVisibility(View.VISIBLE);
                }

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

                mOk.setVisibility(View.GONE);

                mConfirm.setEnabled(false);
                mConfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                mConfirm.setVisibility(View.VISIBLE);
                mConfirm.setTextColor(getActivity().getResources().getColor(R.color.almost_white));

                mCollapse.setVisibility(View.VISIBLE);
                mLinearScan.setVisibility(View.VISIBLE);

                mBtnScanWo.setVisibility(View.GONE);
                mBtnScanDo.setVisibility(View.GONE);
                mBtnScanSo.setVisibility(View.GONE);
                mBtnScanPo.setVisibility(View.GONE);
                mBtnScanInvoice.setVisibility(View.GONE);
            }
        });
        //endregion

        //region Button Confirm
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Picking.");
                builder.setMessage("Are you want to confirm this process?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject info = new JSONObject();
                        try {
                            AsyncTaskAdapter genPK = new AsyncTaskAdapter(new Gson().toJson("PK"), appconfig);
                            genPK.execute("api/Common/GenerateDocumentNo");
                            String pk_no = (String) genPK.get(10000, TimeUnit.MILLISECONDS);

                            info.accumulate("PickingNo", pk_no.substring(1, pk_no.length() - 1));

                            if (mWo.getText().toString().equals("-"))
                                info.accumulate("WO", "");
                            else
                                info.accumulate("WO", mWo.getText().toString());

                            if (mDo.getText().toString().equals("-"))
                                info.accumulate("DO", "");
                            else {
                                info.accumulate("DO", mDo.getText().toString());
                            }

                            if (mSo.getText().toString().equals(""))
                                info.accumulate("SO", "");
                            else
                                info.accumulate("SO", mSo.getText().toString());

                            if (mPo.getText().toString().equals(""))
                                info.accumulate("PO", "");
                            else
                                info.accumulate("PO", mPo.getText().toString());

                            if (mInvoice.getText().toString().equals("-"))
                                info.accumulate("Invoice", "");
                            else
                                info.accumulate("Invoice", mInvoice.getText().toString());

                            info.accumulate("DtItem", DtItem);

                            if (DtSerial.length() == 0)
                                info.accumulate("DtSerial", new JSONArray());
                            else
                                info.accumulate("DtSerial", MainActivity.dt_serial_pick);

                            info.accumulate("CurrentUser", appconfig.getUser());

                            AsyncTaskAdapter confirm_pick = new AsyncTaskAdapter(info.toString(), appconfig);
                            confirm_pick.execute("api/MobilePicking/ConfirmPicking");

                            String checkConfirm = confirm_pick.get(10000,TimeUnit.MILLISECONDS).toString();

                            if (checkConfirm.equals("")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Picking Success.");
                                builder.show();

                                Seq = 0;

                                DtItem = new JSONArray();
                                DtSerial = new JSONArray();
                                MainActivity.dt_serial_pick = new JSONArray();

                                FragmentName = "Complete Process";
                                getActivity().onBackPressed();
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

        //region Camera Dectector Invoice
        mBtnScanInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Picking_Invoice"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Camera Detector Po
        mBtnScanPo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Picking_Po"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Camera Dectector Wo
        mBtnScanWo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Picking_Wo"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Camera Detector Do
        mBtnScanDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Picking_Do"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Camera Detector So
        mBtnScanSo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Picking_So"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Camera Dectector Barcode
        mBtnScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.FragmentMain, CameraFragment.newInstance("Picking_Barcode"), "tag_barcode")
                        .addToBackStack("ScanCode Fragment").commit();
            }
        });
        //endregion

        //region Swipe
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            Drawable backgroundl, backgroundr;
            Drawable xMark, yMark;
            int xMarkMargin, yMarkMargin;
            boolean initiated;

            private void init() {
                backgroundl = new ColorDrawable(Color.RED);
                backgroundr = new ColorDrawable(Color.GREEN);
                xMark = ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_forever_white_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) getActivity().getResources().getDimension(R.dimen.cardview_compat_inset_shadow);
                yMark = ContextCompat.getDrawable(getActivity(), R.drawable.ic_mode_edit_white_24dp);
                yMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                yMarkMargin = (int) getActivity().getResources().getDimension(R.dimen.cardview_compat_inset_shadow);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                final PickingAdapter adapter = (PickingAdapter) mRecycleItem.getAdapter();
                if (swipeDir == ItemTouchHelper.LEFT) {
                    mAdapter.remove(swipedPosition);
                    if (mAdapter.getItemCount() == 0) {
                        mViewDivide.setVisibility(View.INVISIBLE);
                        mConfirm.setEnabled(false);
                        mConfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                    }

                    Snackbar.make(getActivity().findViewById(R.id.FragmentMain), "delete..", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mAdapter.getItemCount() == 0) {
                                mConfirm.setEnabled(true);
                                mConfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
                            }
                            adapter.recover();

                        }
                    }).show();
                } else if (swipeDir == ItemTouchHelper.RIGHT) {
                    try {
                        temp = new JSONObject(mAdapter.js_picking.get(swipedPosition).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setCancelable(false);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_edit_picking);
                    dialog.show();
                    final Button btncancel = (Button) dialog.findViewById(R.id.dialogpicking_cancel);
                    final Button btnok = (Button) dialog.findViewById(R.id.dialogpicking_ok);
                    final EditText item = (EditText) dialog.findViewById(R.id.dialogpicking_item);
                    final EditText qty = (EditText) dialog.findViewById(R.id.dialogpicking_qty);
                    final EditText po = (EditText) dialog.findViewById(R.id.dialogpicking_po);
                    final EditText lot = (EditText) dialog.findViewById(R.id.dialogpicking_lot);
                    spin_cus = (com.toptoche.searchablespinnerlibrary.SearchableSpinner) dialog.findViewById(R.id.dialogpicking_customer);
                    ArrayList<String> arr_customer = new ArrayList<String>();
                    try {
                        item.setText(temp.getString("ItemName"));
                        qty.setText(temp.getString("Qty"));
                        po.setText(temp.getString("PO"));
                        lot.setText(temp.getString("Lot"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapter.remove(swipedPosition);
                    btnok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                temp.put("Qty", qty.getText().toString());
                                temp.put("PO", po.getText().toString());
                                temp.put("Lot", lot.getText().toString());
                                String cut[] = spin_cus.getSelectedItem().toString().split(" ");
                                temp.put("CustomerCode", cut[0]);
                                mAdapter.add(temp.toString());
                                dialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
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

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }
                if (!initiated) {
                    init();
                }
                //draw green background
                backgroundr.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
                backgroundr.draw(c);
                // draw red background
                backgroundl.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                backgroundl.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                xMark.draw(c);
                //draw y mark
                intrinsicWidth = yMark.getIntrinsicWidth();
                int yMarkLeft = itemView.getLeft() + yMarkMargin;
                int yMarkRight = itemView.getLeft() + yMarkMargin + intrinsicWidth;
                yMark.setBounds(yMarkLeft, xMarkTop, yMarkRight, xMarkBottom);
                yMark.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new SwipeHelper(getActivity(), mRecycleItem) {
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
                                    mConfirm.setEnabled(false);
                                    mConfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
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
                                    temp = new JSONObject(mAdapter.js_picking.get(pos).toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final Dialog dialog = new Dialog(getActivity());
                                dialog.setCancelable(false);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_edit_picking);
                                dialog.show();
                                final Button btncancel = (Button) dialog.findViewById(R.id.dialogpicking_cancel);
                                final Button btnok = (Button) dialog.findViewById(R.id.dialogpicking_ok);
                                final EditText item = (EditText) dialog.findViewById(R.id.dialogpicking_item);
                                final EditText qty = (EditText) dialog.findViewById(R.id.dialogpicking_qty);
                                final EditText po = (EditText) dialog.findViewById(R.id.dialogpicking_po);
                                final EditText lot = (EditText) dialog.findViewById(R.id.dialogpicking_lot);
                                spin_cus = (com.toptoche.searchablespinnerlibrary.SearchableSpinner) dialog.findViewById(R.id.dialogpicking_customer);
                                final ArrayList<String> arr_customer = new ArrayList<String>();
                                try {
                                    item.setText(temp.getString("ItemName"));
                                    qty.setText(temp.getString("Qty"));
                                    po.setText(temp.getString("PO"));
                                    lot.setText(temp.getString("Lot"));
                                    loadSupplier();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                spin_cus.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if(arr_customer.size()==0 && (((MainActivity)getActivity()).checkinternet()) ) loadSupplier();
                                        return false;
                                    }
                                });
                                mAdapter.remove(pos);
                                btnok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            temp.put("Qty", qty.getText().toString());
                                            temp.put("PO", po.getText().toString());
                                            temp.put("Lot", lot.getText().toString());
                                            String cut[] = spin_cus.getSelectedItem().toString().split(" ");
                                            temp.put("CustomerCode", cut[0]);
                                            mAdapter.add(temp.toString());
                                            dialog.dismiss();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
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
    //Load Supplier to spin
    private void loadSupplier(){
        try {
            String load_customer = new AsyncTaskAdapter(new Gson().toJson(""), appconfig).execute("api/Common/LoadCustomer").get(10000,TimeUnit.MILLISECONDS).toString();
            JSONArray getCustomer = new JSONArray(load_customer);
            arr_customer.add(temp.getString("CustomerCode"));
            for (int i = 0; i < getCustomer.length(); i++) {
                if (!temp.getString("CustomerCode").equals(getCustomer.getJSONObject(i).getString("Display")))
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
        ArrayAdapter<String> adapterCustomer = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arr_customer);
        adapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        try {
            spin_cus.setTitle(temp.getString("CustomerCode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        spin_cus.setAdapter(adapterCustomer);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!appconfig.checkstate()) ((MainActivity) getActivity()).restartApp();
        //receive data
        if (getArguments() != null) {
            if (getArguments().getString("WhereScan") != null) {
                if (getArguments().getString("WhereScan").equals("Invoice")) {
                    mInvoice.setText(getArguments().getString("result"));
                } else if (getArguments().getString("WhereScan").equals("Wo")) {
                    mWo.setText(getArguments().getString("result"));
                } else if (getArguments().getString("WhereScan").equals("So")) {
                    mSo.setText(getArguments().getString("result"));
                } else if (getArguments().getString("WhereScan").equals("Do")) {
                    mDo.setText(getArguments().getString("result"));
                } else if (getArguments().getString("WhereScan").equals("Po")) {
                    mPo.setText(getArguments().getString("result"));
                } else if (getArguments().getString("WhereScan").equals("Barcode")) {
                    mBarcode.setText(getArguments().getString("result"));
                    mSearch.callOnClick();
                }
            }

            if (getArguments().getString("CheckPick") != null) {
                if (getArguments().getString("CheckPick").toString().equals("1")) {

                    JSONObject js_item = new JSONObject();
                    try {
                        mBarcode.getText().clear();

                        js_item.accumulate("HasTransit", getArguments().getString("HasTransit"));
                        js_item.accumulate("ItemCode", getArguments().getString("ItemCode"));
                        js_item.accumulate("ItemName", getArguments().getString("ItemName"));
                        js_item.accumulate("Qty", getArguments().getDouble("Qty"));
                        js_item.accumulate("Lot", getArguments().getString("Lot"));
                        js_item.accumulate("PO", getArguments().getString("PO"));
                        js_item.accumulate("Loc", getArguments().getString("Loc"));
                        js_item.accumulate("CustomerCode", getArguments().getString("CustomerCode"));
                        if (getArguments().getString("CheckSticker").equals("1"))
                            js_item.accumulate("StickerCode", getArguments().getString("StickerCode"));
                        else
                            js_item.accumulate("Barcode", getArguments().getString("Barcode"));

                        js_item.accumulate("Seq", Seq);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    DtItem.put(js_item);
                    mViewDivide.setVisibility(View.VISIBLE);
                    Seq++;

                }
            }
        }

        //region Check Back again
        if (MainActivity.getCheckBackPick() != 0) {
            FragmentName = "Picking Details press ok";

            mWo.setEnabled(false);
            mWo.setTextSize(12);
            mWo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mTxtWo.setVisibility(View.VISIBLE);

            mDo.setEnabled(false);
            mDo.setTextSize(12);
            mDo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mTxtDo.setVisibility(View.VISIBLE);

            mSo.setEnabled(false);
            mSo.setTextSize(12);
            mSo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mTxtSo.setVisibility(View.VISIBLE);

            mPo.setEnabled(false);
            mPo.setTextSize(12);
            mPo.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mTxtPo.setVisibility(View.VISIBLE);

            mInvoice.setEnabled(false);
            mInvoice.setTextSize(12);
            mInvoice.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            mTxtInvoice.setVisibility(View.VISIBLE);

            if (DtItem.length() != 0) {
                mViewDivide.setVisibility(View.VISIBLE);
                mConfirm.setVisibility(View.VISIBLE);
                mConfirm.setEnabled(true);
                mConfirm.setBackgroundColor(getResources().getColor(R.color.transit_color));
            } else {
                mConfirm.setEnabled(false);
                mConfirm.setBackgroundColor(getResources().getColor(R.color.btnDefault));
                mConfirm.setTextColor(getActivity().getResources().getColor(R.color.almost_white));
                mConfirm.setVisibility(View.VISIBLE);
            }

            mOk.setVisibility(View.GONE);
            mCollapse.setVisibility(View.VISIBLE);
            mLinearScan.setVisibility(View.VISIBLE);

            mBtnScanWo.setVisibility(View.GONE);
            mBtnScanDo.setVisibility(View.GONE);
            mBtnScanSo.setVisibility(View.GONE);
            mBtnScanPo.setVisibility(View.GONE);
            mBtnScanInvoice.setVisibility(View.GONE);

            if (countCollapse == 0) {
                mLayoutCollapse.setVisibility(View.VISIBLE);
                mCollapse.setBackgroundResource(R.drawable.ic_collapse);
            } else {
                mLayoutCollapse.setVisibility(View.GONE);
                mCollapse.setBackgroundResource(R.drawable.ic_collapse_00009);
            }
        }
        //endregion
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
