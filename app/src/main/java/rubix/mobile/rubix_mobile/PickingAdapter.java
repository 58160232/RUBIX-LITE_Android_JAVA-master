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
 * Created by niwat on 9/2/2561.
 */

public class PickingAdapter extends RecyclerView.Adapter<PickingAdapter.ViewHolder> {
    public JSONArray js_picking = new JSONArray();
    private Context mContext;
    private String temp;

    public void add(String t){
        js_picking.put(t);
        notifyDataSetChanged();
    }
    public void remove(int p){
        temp = js_picking.remove(p).toString();
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public EditText mItemname_list;
        public EditText mLot_list;
        public EditText mPo_list;
        public EditText mQty_list;
        public EditText mCustomer_list;
        public EditText mLocation_list;
        public TextView mTxtLocation;
        public TextView mTxtColon2;

        public ViewHolder(View view) {
            super(view);
            mItemname_list = (EditText) view.findViewById(R.id.list_itemname);
            mLot_list = (EditText) view.findViewById(R.id.list_Lot);
            mPo_list = (EditText) view.findViewById(R.id.list_po);
            mQty_list = (EditText) view.findViewById(R.id.list_Qty);
            mCustomer_list = (EditText) view.findViewById(R.id.list_customer);
            mLocation_list = (EditText) view.findViewById(R.id.list_location);
            mTxtLocation = (TextView) view.findViewById(R.id.re_TxtLocation);
            mTxtColon2 =(TextView) view.findViewById(R.id.txtColon2);
        }
    }

    public PickingAdapter(Context context, JSONArray data) {
        mContext = context;
        js_picking = data;
    }
    public void recover(){
        js_picking.put(temp);
        temp = null;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycleview_picking_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            JSONObject tmp = new JSONObject(js_picking.get(position).toString());
            if (tmp.getString("HasTransit").equals("true")) {
                viewHolder.mItemname_list.setText(tmp.getString("ItemName"));
                viewHolder.mLot_list.setText(tmp.getString("Lot"));
                viewHolder.mPo_list.setText(tmp.getString("PO"));
                viewHolder.mQty_list.setText(tmp.getString("Qty"));
                viewHolder.mCustomer_list.setText(tmp.getString("CustomerCode"));
                viewHolder.mLocation_list.setText(tmp.getString("Loc"));
            } else {
                viewHolder.mItemname_list.setText(tmp.getString("ItemName"));
                viewHolder.mLot_list.setText(tmp.getString("Lot"));
                viewHolder.mPo_list.setText(tmp.getString("PO"));
                viewHolder.mQty_list.setText(tmp.getString("Qty"));
                viewHolder.mCustomer_list.setText(tmp.getString("CustomerCode"));
                viewHolder.mLocation_list.setEnabled(false);
                viewHolder.mLocation_list.setVisibility(View.GONE);
                viewHolder.mTxtColon2.setVisibility(View.GONE);
                viewHolder.mTxtLocation.setVisibility(View.GONE);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return js_picking.length();
    }
}
