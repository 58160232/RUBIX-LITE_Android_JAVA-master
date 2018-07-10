package rubix.mobile.rubix_mobile;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by niwat on 2/2/2561.
 */

public class ConfirmReceiveAdapter {
    public static JSONArray dt_serial;
    public static JSONArray dt_receive;
    public static JSONObject dt_info_receive;
    public static JSONObject complete_data = new JSONObject();
    private Appconfig appconfig ;

    public static void setDataSerial(JSONArray data_serial) {
        dt_serial = data_serial;
    }

    public void setDataReceive(JSONArray data_receive) {
        dt_receive = data_receive;
    }

    public void setDataInfo(JSONObject data_info) {
        dt_info_receive = data_info;
    }

    public void setAppconfig(Appconfig app) {
        appconfig = app;
    }

    public JSONObject ConfirmSuccess(){
        try {
            complete_data.accumulate("ReceivingNo",dt_info_receive.getString("ReceivingNo"));
            complete_data.accumulate("InvoiceNo", dt_info_receive.getString("InvoiceNo"));
            complete_data.accumulate("DO", dt_info_receive.getString("DO"));
            complete_data.accumulate("Ref", dt_info_receive.getString("Ref"));
            complete_data.accumulate("SupplierCode", dt_info_receive.getString("SupplierCode"));
            complete_data.accumulate("dtReceive", dt_receive);
            complete_data.accumulate("dtSerial", dt_serial);
            complete_data.accumulate("CurrentUser", "test");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        AsyncTaskAdapter confirm_receive = new AsyncTaskAdapter(complete_data.toString(), appconfig);
//        confirm_receive.execute("api/MobileReceiving/ConfirmReceiving");

        return complete_data;
    }
}
