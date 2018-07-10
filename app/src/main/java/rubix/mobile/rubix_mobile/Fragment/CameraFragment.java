package rubix.mobile.rubix_mobile.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.Size;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import rubix.mobile.rubix_mobile.MainActivity;
import rubix.mobile.rubix_mobile.R;

public class CameraFragment extends Fragment {

    private DecoratedBarcodeView barcodeView;
    private String result = "empty";
    private int RequestCameraPermissionID = 1001;
    private String page;
    private Button mScanType;
    private int TypeScan = 0;
    private TextView state;
    private BarcodeView mBarcodeView;
    Collection<BarcodeFormat> formats1D,formats2D;

    public CameraFragment() {
    }

    public static CameraFragment newInstance(String a){
        CameraFragment ar = new CameraFragment();
        Bundle bd = new Bundle();
        bd.putString("Page",a);
        ar.setArguments(bd);
        return ar;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            page = getArguments().getString("Page");
        }

        MainActivity.FragmentName = "ScanCodeFragment";
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        MainActivity.toggle.setDrawerIndicatorEnabled(false);
        MainActivity.drawer.closeDrawer(GravityCompat.START);
        MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
        }

        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.camerabarcode);
        mBarcodeView = (BarcodeView) view.findViewById(R.id.zxing_barcode_surface);

        state = (TextView) view.findViewById(R.id.statebarcode) ;
        mScanType = (Button) view.findViewById(R.id.btn_scan_type);

        TypeScan = 0;
//
        formats1D = Arrays.asList(BarcodeFormat.CODE_128,
                BarcodeFormat.CODE_39,
                BarcodeFormat.CODE_93,
                BarcodeFormat.CODABAR,
                BarcodeFormat.UPC_A,
                BarcodeFormat.UPC_E,
                BarcodeFormat.EAN_8,
                BarcodeFormat.EAN_13);
        formats2D = Arrays.asList(BarcodeFormat.QR_CODE,BarcodeFormat.AZTEC,BarcodeFormat.DATA_MATRIX,BarcodeFormat.MAXICODE);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats1D, null, null, false));

        barcodeView.pause();
        mBarcodeView.setFramingRectSize(new Size(1400, 600));
        barcodeView.resume();

        barcodeView.setStatusText("");
        barcodeView.decodeContinuous(callback);

        state.setText("1D");

        mScanType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TypeScan ==0) {
                    mScanType.setBackgroundResource(R.drawable.ic_barcode);
                    barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats2D, null, null, false));
                    TypeScan = 1;

                    barcodeView.pause();
                    mBarcodeView.setFramingRectSize(new Size(1200,1200));
                    barcodeView.resume();
                    state.setText("2D");
                } else {
                    mScanType.setBackgroundResource(R.drawable.ic_qr);
                    barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats1D, null, null, false));
                    TypeScan = 0;

                    barcodeView.pause();
                    mBarcodeView.setFramingRectSize(new Size(1400, 600));
                    barcodeView.resume();
                    state.setText("1D");
                }
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onDetect(){
        Fragment tmp;
        if(page.equals("Transit_Barcode")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_transit");
            Bundle bd = new Bundle();
            bd.putString("Barcode",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain,tmp,"tag_transit").addToBackStack("MenuTransit").commit();
        }
        else if(page.equals("Transit_Location")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_transit");
            Bundle bd = new Bundle();
            bd.putString("Location",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain,tmp,"tag_transit").addToBackStack("MenuTransit").commit();
        }
        else if(page.equals("Counting_Barcode")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_counting");
            Bundle bd = new Bundle();
            bd.putString("Barcode",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain,tmp,"tag_counting").addToBackStack("MenuCounting").commit();
        }
        else if(page.equals("Counting_Location")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_counting");
            Bundle bd = new Bundle();
            bd.putString("Location",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain,tmp,"tag_counting").addToBackStack("MenuCounting").commit();
        }
        else if(page.equals("Change_Barcode")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_change");
            Bundle bd = new Bundle();
            bd.putString("Barcode",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain,tmp,"tag_change").addToBackStack("MenuChange").commit();
        }
        else if(page.equals("Change_LocationFrom")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_change");
            Bundle bd = new Bundle();
            bd.putString("LocationFrom",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain,tmp,"tag_change").addToBackStack("MenuChange").commit();
        }
        else if(page.equals("Change_LocationTo")){
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_change");
            Bundle bd = new Bundle();
            bd.putString("LocationTo",result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain,tmp,"tag_change").addToBackStack("MenuChange").commit();
        } else if (page.equals("Receive_Do")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Do");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_receiving").addToBackStack("MenuReceive").commit();
        } else if (page.equals("Receive_Invoice")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Invoice");
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_receiving").addToBackStack("MenuReceive").commit();
        } else if (page.equals("Receive_Ref")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Ref");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_receiving").addToBackStack("MenuReceive").commit();
        } else if (page.equals("Receive_Barcode")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_receiving");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Barcode");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_receiving").addToBackStack("MenuReceive").commit();
        } else if (page.equals("Picking_Invoice")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_picking");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Invoice");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_picking").addToBackStack("MenuPick").commit();
        } else if (page.equals("Picking_Po")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_picking");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Po");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_picking").addToBackStack("MenuPick").commit();
        } else if (page.equals("Picking_Wo")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_picking");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Wo");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_picking").addToBackStack("MenuPick").commit();
        } else if (page.equals("Picking_Do")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_picking");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Do");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_picking").addToBackStack("MenuPick").commit();
        }  else if (page.equals("Picking_So")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_picking");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "So");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_picking").addToBackStack("MenuPick").commit();
        } else if (page.equals("Picking_Barcode")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_picking");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Barcode");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_picking").addToBackStack("MenuPick").commit();
        } else if (page.equals("Picking_ScanSerial")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_detail_pick_serial");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Serial");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_detail_pick_serial").addToBackStack("PickingSerial").commit();
        } else if (page.equals("Receive_ScanSerial")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_detail_receive_serial");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "Serial");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_detail_receive_serial").addToBackStack("ReceiveSerial").commit();
        } else if (page.equals("Receive_AddItem")) {
            tmp = getActivity().getSupportFragmentManager().findFragmentByTag("tag_mapping_item");
            Bundle bd = new Bundle();
            bd.putString("WhereScan", "AddItem");
            bd.putString("result", result);
            tmp.setArguments(bd);
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.FragmentMain, tmp, "tag_mapping_item").addToBackStack("MappingItem").commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainActivity.toggle.setDrawerIndicatorEnabled(true);
        MainActivity.drawer.closeDrawer(GravityCompat.START);
        MainActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult resultReader) {
                if (resultReader.getText() != null) {
//                    beepManager.playBeepSoundAndVibrate();
                    result = resultReader.getText();
                    onDetect();
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onResume() {
        barcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }
}
