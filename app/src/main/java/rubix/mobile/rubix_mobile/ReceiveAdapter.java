package rubix.mobile.rubix_mobile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by niwat on 30/1/2561.
 */

public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.ViewHolder> {
    public static JSONArray json_receive =  new JSONArray();
    private Context mContext;
    String temp;
    public void add(String a){
        json_receive.put(a);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public EditText mItemname_list;
        public EditText mLot_list;
        public EditText mPo_list;
        public EditText mTotal_list;
        public EditText mPack_list;
        public EditText mPerPack_list;
        public TextView mTxtPerPack;
        public TextView mTxtPack;
        public TextView mTxtColon1;
        public TextView mTxtColon2;

        public ViewHolder(View view) {
            super(view);
            mItemname_list = (EditText) view.findViewById(R.id.list_itemname);
            mLot_list = (EditText) view.findViewById(R.id.list_lot);
            mPo_list = (EditText) view.findViewById(R.id.list_po);
            mTotal_list = (EditText) view.findViewById(R.id.list_total);
            mPack_list = (EditText) view.findViewById(R.id.list_pack);
            mPerPack_list = (EditText) view.findViewById(R.id.list_perpack);
            mTxtPerPack = (TextView) view.findViewById(R.id.re_PerPack);
            mTxtPack = (TextView) view.findViewById(R.id.re_txtPack);
            mTxtColon1 = (TextView) view.findViewById(R.id.txtColon1);
            mTxtColon2 =(TextView) view.findViewById(R.id.txtColon2);
        }
    }
    public ReceiveAdapter(Context context, JSONArray data) {
        mContext = context;
        json_receive = data;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycleview_receive_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            JSONObject tmp = new JSONObject(json_receive.get(position).toString());
            if (tmp.getString("HasSticker").equals("true")) {
                viewHolder.mItemname_list.setText(tmp.getString("ItemName"));
                viewHolder.mLot_list.setText(tmp.getString("Lot"));
                viewHolder.mPo_list.setText(tmp.getString("PONo"));
                viewHolder.mTotal_list.setText(tmp.getString("TotalQty"));
                viewHolder.mPack_list.setText(tmp.getString("Pack"));
                viewHolder.mPerPack_list.setText(tmp.getString("QtyPerPack"));
            } else {
                viewHolder.mItemname_list.setText(tmp.getString("ItemName"));
                viewHolder.mLot_list.setText(tmp.getString("Lot"));
                viewHolder.mPo_list.setText(tmp.getString("PONo"));
                viewHolder.mTotal_list.setText(tmp.getString("TotalQty"));
                viewHolder.mPack_list.setVisibility(View.GONE);
                viewHolder.mPerPack_list.setVisibility(View.GONE);
                viewHolder.mTxtPerPack.setVisibility(View.GONE);
                viewHolder.mTxtPack.setVisibility(View.GONE);
                viewHolder.mTxtColon1.setVisibility(View.GONE);
                viewHolder.mTxtColon2.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return json_receive.length();
    }
    public void remove(int position) throws JSONException {
            temp = json_receive.remove(position).toString();
            notifyDataSetChanged();
    }
    public void recover() throws JSONException {
        json_receive.put(temp);
        notifyDataSetChanged();
        temp =null;
    }
}