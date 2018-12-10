package com.elrancho.pwi.pwi_app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummaryResponse;

import java.util.List;

public class InventoyCountSummaryAdapter extends RecyclerView.Adapter<InventoyCountSummaryAdapter.InventoryCountSummaryViewHolder> {

    private Context mContext;
    private List<InventoryCountSummary> inventoryCountSummariesList;

    public InventoyCountSummaryAdapter(Context mContext, List<InventoryCountSummary> inventoryCountSummariesList) {
        this.mContext = mContext;
        this.inventoryCountSummariesList = inventoryCountSummariesList;
    }

    @NonNull
    @Override
    public InventoryCountSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_ics, null);
        return new InventoryCountSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryCountSummaryViewHolder holder, int position) {

        InventoryCountSummary inventoryCountSummary = inventoryCountSummariesList.get(position);

        holder.tvWeekEndDate.setText(inventoryCountSummary.getWeekEndDate());
        holder.tvInventoryCountSummary.setText(Double.toString(inventoryCountSummary.getTotalInventory()));

    }

    @Override
    public int getItemCount() {
        return inventoryCountSummariesList.size();
    }

    class InventoryCountSummaryViewHolder extends RecyclerView.ViewHolder{

        TextView tvWeekEndDate, tvInventoryCountSummary;

        public InventoryCountSummaryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWeekEndDate=itemView.findViewById(R.id.tvDate);
            tvInventoryCountSummary=itemView.findViewById(R.id.tvTotalInventory);
        }
    }
}
