package rubix.mobile.rubix_mobile.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DecorContentParent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.integration.android.*;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import rubix.mobile.rubix_mobile.R;

public class LoginQRFragment extends Fragment {
    public LoginQRFragment() {

    }

    //    private CompoundBarcodeView barcodeView;
    private DecoratedBarcodeView barcodeView;
    EditText txtResult;
    private BeepManager beepManager;
    int countScan = 0;
    final int RequestCameraPermissionID = 1001;
    private QRlogin mLogin;
    private ConnectivityManager cm;
    private NetworkInfo activeNetwork;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
            }
            break;
        }
    }

    public String getBarcode() {
        return txtResult.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_qr, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Request permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
            return;
        }

        barcodeView = (DecoratedBarcodeView) view.findViewById(R.id.barcode_scanner);
        barcodeView.setStatusText("");
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats, null, null, false));
        beepManager = new BeepManager(getActivity());
        beepManager.setBeepEnabled(false);
        barcodeView.decodeContinuous(callback);
        cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        if (null == activeNetwork) {
            Toast.makeText(getActivity(), "Please connect internet", Toast.LENGTH_LONG).show();
        }
    }
    public interface QRlogin {
        public void LoginQR(String QR);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mLogin = (QRlogin) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement QRlogin");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void wrong() {
        countScan = 0;
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {
                    activeNetwork = cm.getActiveNetworkInfo();
                    if (null == activeNetwork) {
                        Toast.makeText(getActivity(), "Please connect internet", Toast.LENGTH_LONG).show();
                    } else {
                        if (result.getText() != null && countScan==0) {
                            countScan++;
                            beepManager.playBeepSoundAndVibrate();
                            String test = result.getText();
                            mLogin.LoginQR(test);
                        }
                    }


        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    public void onResume() {
        if(barcodeView !=null)
            barcodeView.resume();
        super.onResume();
    }

    @Override
    public void onPause() {
        barcodeView.pause();
        super.onPause();
    }
}
