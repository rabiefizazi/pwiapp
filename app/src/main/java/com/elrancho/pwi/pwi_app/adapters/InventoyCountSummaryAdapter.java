package com.elrancho.pwi.pwi_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.activities.InventoryCountDetailsActivity;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerDepartment;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerInventorySummary;

import java.util.ArrayList;
import java.util.List;

public class InventoyCountSummaryAdapter extends RecyclerView.Adapter<InventoyCountSummaryAdapter.InventoryCountSummaryViewHolder>{

    private Context mContext;
    private List<InventoryCountSummary> inventoryCountSummariesList = new ArrayList<>();

    public InventoyCountSummaryAdapter(Context mContext, List<InventoryCountSummary> inventoryCountSummariesList) {
        this.mContext = mContext;
        this.inventoryCountSummariesList = inventoryCountSummariesList;
    }

    @NonNull
    @Override
    public InventoryCountSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_count_summary_content, parent, false);
        return new InventoryCountSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryCountSummaryViewHolder holder, final int position) {

        //InventoryCountSummary inventoryCountSummary = inventoryCountSummariesList.get(position);

        holder.tvWeekEndDate.setText(inventoryCountSummariesList.get(position).getWeekEndDate());
        holder.tvInventoryCountSummary.setText(Double.toString(inventoryCountSummariesList.get(position).getTotalInventory()));
        holder.parentLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Toast.makeText(mContext, inventoryCountSummariesList.get(position).getWeekEndDate(), Toast.LENGTH_LONG).show();

                Integer storeId = Integer.parseInt(SharedPrefManagerDepartment.getInstance(mContext).getDepartment().getStoreId());
                Integer departmentId = Integer.parseInt(SharedPrefManagerDepartment.getInstance(mContext).getDepartment().getDepartmentId());
                String weekEndDate = inventoryCountSummariesList.get(position).getWeekEndDate();
                Double totalInventory = inventoryCountSummariesList.get(position).getTotalInventory();

                saveSelectedWeekInfo(storeId, departmentId, weekEndDate, totalInventory, position);
            }

            public void saveSelectedWeekInfo(Integer storeId, Integer departmentId, String weekEndDate, Double totalInventory, int position) {
                InventoryCountSummary inventoryCountSummary = new InventoryCountSummary(storeId, departmentId, weekEndDate, totalInventory, position);
                SharedPrefManagerInventorySummary.getInstance(mContext).clear();
                SharedPrefManagerInventorySummary.getInstance(mContext).saveInventoryCountSummary(inventoryCountSummary);

                Intent intent = new Intent(mContext, InventoryCountDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryCountSummariesList.size();
    }

    class InventoryCountSummaryViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout parentLayout;

        TextView tvWeekEndDate, tvInventoryCountSummary;

        public InventoryCountSummaryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWeekEndDate=itemView.findViewById(R.id.tvWeekEndDate);
            tvInventoryCountSummary=itemView.findViewById(R.id.tvInventoryCountSummary);
            parentLayout=itemView.findViewById(R.id.content_layout);
        }
    }
}
