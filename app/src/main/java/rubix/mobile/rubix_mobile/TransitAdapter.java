package rubix.mobile.rubix_mobile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kantapon on 5/3/2561.
 */

public class TransitAdapter extends RecyclerView.Adapter<TransitAdapter.ViewHolder> {
    public JSONArray dt_transit = new JSONArray();
    Context mContext;
    String temp;

    public TransitAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.listview_transit, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    public void add(String s) {
        dt_transit.put(s);
        notifyDataSetChanged();
    }

    public boolean checkSticker(String Sticker) {
        boolean ch = false;
        for (int i = 0; i < dt_transit.length(); i++) {
            try {
                if (new JSONObject(dt_transit.get(i).toString()).getString("Sticker").contains(Sticker))
                    ch = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ch;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject tmp = new JSONObject(dt_transit.get(position).toString());
            holder.item.setText(tmp.getString("ItemCode"));
            holder.qty.setText(tmp.getString("QtyPerPack"));
            holder.sticker.setText(tmp.getString("Sticker"));
            if (tmp.getString("Lot").equals("null") || tmp.getString("Lot").isEmpty())
                holder.lot.setText("-");
            else holder.lot.setText(tmp.getString("Lot"));
            if (tmp.getString("LocationCode").equals("null")) holder.defaul.setText("-");
            else holder.defaul.setText(tmp.getString("LocationCode"));
            if (tmp.getString("InvoiceNo").equals("null")) holder.invoice.setText("-");
            else holder.invoice.setText(tmp.getString("InvoiceNo"));
            if (tmp.getString("Ref").equals("null")) holder.ref.setText("-");
            else holder.ref.setText(tmp.getString("Ref"));
            if (tmp.getString("PONo").equals("null")) holder.po.setText("-");
            else holder.po.setText(tmp.getString("PONo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getList_transit() {
        JSONObject Jsobject = null;
        JSONObject tmp;
        String temp = "";
        for (int i = 0; i < dt_transit.length(); i++) {
            try {
                Jsobject = new JSONObject(dt_transit.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            tmp = new JSONObject();
            try {
                tmp.accumulate("ItemName", Jsobject.getString("ItemName"));
                tmp.accumulate("ItemCode", Jsobject.getString("ItemCode"));
                tmp.accumulate("QtyPerPack", Jsobject.getString("QtyPerPack"));
                tmp.accumulate("Lot", Jsobject.getString("Lot"));
                tmp.accumulate("LocationCode", Jsobject.getString("LocationCode"));
                tmp.accumulate("InvoiceNo", Jsobject.getString("InvoiceNo"));
                tmp.accumulate("Ref", Jsobject.getString("Ref"));
                tmp.accumulate("PONo", Jsobject.getString("PONo"));
                tmp.accumulate("Sticker", Jsobject.getString("Sticker"));
                tmp.accumulate("IsBarCode", Jsobject.getString("IsBarCode"));
                if (i == 0) temp += "[" + tmp.toString();
                else temp += "," + tmp.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        temp = temp + "]";
        return temp;
    }

    @Override
    public int getItemCount() {
        return dt_transit.length();
    }

    public void clear() {
        for (int i = 0; i < dt_transit.length(); i++)
            dt_transit.remove(0);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item;
        TextView defaul;
        TextView qty;
        TextView invoice;
        TextView ref;
        TextView po;
        TextView lot;
        TextView sticker;

        public ViewHolder(View view) {
            super(view);
            item = view.findViewById(R.id.tran_item);
            defaul = view.findViewById(R.id.tran_default);
            qty = view.findViewById(R.id.tran_qty);
            invoice = view.findViewById(R.id.tran_invoice);
            ref = view.findViewById(R.id.tran_reference);
            po = view.findViewById(R.id.tran_po);
            lot = view.findViewById(R.id.tran_lot);
            sticker = view.findViewById(R.id.tran_sticker);
        }
    }
}
