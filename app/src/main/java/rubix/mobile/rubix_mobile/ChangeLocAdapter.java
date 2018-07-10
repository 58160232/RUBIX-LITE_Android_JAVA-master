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
 * Created by niwat on 30/1/2561.
 */

public class ChangeLocAdapter extends RecyclerView.Adapter<ChangeLocAdapter.ViewHolder> {
    public static JSONArray json_change =  new JSONArray();
    private Context mContext;
    String temp;


    public void add(String s) {
        json_change.put(s);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView ItemName;
        private TextView Qty;
        private TextView Lot;
        private TextView LocationCode;
        private TextView Sticker;
        private String ItemCode;
        private boolean Split;
        private int Seq;
        private TextView Title;

        public ViewHolder(View view) {
            super(view);
            ItemName = (TextView) view.findViewById(R.id.change_item);
            Qty = (TextView) view.findViewById(R.id.change_qty);
            Lot = (TextView) view.findViewById(R.id.change_lot);
            LocationCode = (TextView) view.findViewById(R.id.change_location);
            Sticker = (TextView) view.findViewById(R.id.change_barcode);
            Title  = (TextView) view.findViewById(R.id.textview28);
        }
    }
    public ChangeLocAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listview_change, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        try {
            JSONObject tmp = new JSONObject(json_change.get(position).toString());
            viewHolder.ItemName.setText(tmp.getString("ItemName"));
            viewHolder.Qty.setText(tmp.getString("QtyPerPack"));
            viewHolder.Lot.setText(tmp.getString("Lot"));

            if(viewHolder.Lot.getText().toString().trim().isEmpty() || viewHolder.Lot.getText().toString().trim().equals("null"))
                viewHolder.Lot.setText("-");

            viewHolder.LocationCode.setText(tmp.getString("LocationCode"));

            if (tmp.has("StickerCode")) {
                viewHolder.Sticker.setText(tmp.getString("StickerCode"));
                viewHolder.Title.setText("Sticker");
            } else {
                viewHolder.Sticker.setText(tmp.getString("Barcode"));
            }

            viewHolder.ItemCode = tmp.getString("ItemCode");
            viewHolder.Split = tmp.getBoolean("Split");
            viewHolder.Seq = tmp.getInt("Seq");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return json_change.length();
    }

    public void remove(int position) throws JSONException {
        temp = json_change.remove(position).toString();
        notifyDataSetChanged();
    }

    public void recover() throws JSONException {
        json_change.put(temp);
        notifyDataSetChanged();
        temp = null;
    }

    public String getList_change() {
        JSONObject Jsobject = null;
        JSONObject tmp;
        String temp = "";
        for (int i = 0; i < json_change.length(); i++) {
            try {
                Jsobject = new JSONObject(json_change.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tmp = new JSONObject();
            try {
                tmp.accumulate("ItemCode", Jsobject.getString("ItemCode"));
                tmp.accumulate("ItemName", Jsobject.getString("ItemName"));
                tmp.accumulate("Qty", Jsobject.getString("QtyPerPack"));
                tmp.accumulate("Lot", Jsobject.getString("Lot"));
                tmp.accumulate("LocationCode", Jsobject.getString("LocationCode"));
                if(Jsobject.has("Barcode"))
                    tmp.accumulate("Sticker", Jsobject.getString("Barcode"));
                else
                    tmp.accumulate("Sticker",Jsobject.getString("StickerCode"));
                tmp.accumulate("Split", Jsobject.getString("Split"));
                tmp.accumulate("Seq", i);
                if (i == 0) temp += "[" + tmp.toString();
                else temp += "," + tmp.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        temp = temp + "]";
        return temp;
    }

    public void clear() {
        while (json_change.length() != 0)
            json_change.remove(0);
        notifyDataSetChanged();
    }

    public boolean checkSticker(String Sticker) {
        boolean ch = false;
        for (int i = 0; i < json_change.length(); i++) {
            try {
                if (new JSONObject(json_change.get(i).toString()).getString("StickerCode").contains(Sticker))
                    ch = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ch;
    }
}