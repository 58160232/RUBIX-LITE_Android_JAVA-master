package rubix.mobile.rubix_mobile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by niwat on 2/2/2561.
 */

public class ReceiveSerialAdapter extends RecyclerView.Adapter<ReceiveSerialAdapter.ViewHolder> {
    public static JSONArray dt_serial;
    private Context mContext;
    String temp;
    public void recover() {
        dt_serial.put(temp);
        temp =null;
        notifyDataSetChanged();
    }
    public void add(String js){
        dt_serial.put(js);
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText mSerial;

        public ViewHolder(View view) {
            super(view);
            mSerial = (EditText) view.findViewById(R.id.list_serial);
        }
    }
    public ReceiveSerialAdapter(Context context, JSONArray data) {
        mContext = context;
        dt_serial = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycleview_serial_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            JSONObject tmp = new JSONObject(dt_serial.get(position).toString());

            viewHolder.mSerial.setText(tmp.getString("Serial"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void remove(int position){
        temp = dt_serial.remove(position).toString();
        notifyDataSetChanged();
    }
    public JSONArray getDt_serial(){
        return  dt_serial;
    }
    @Override
    public int getItemCount() {
        return dt_serial.length();
    }

}
