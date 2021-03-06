package com.elrancho.pwi.pwi_app_test.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elrancho.pwi.pwi_app_test.R;
import com.elrancho.pwi.pwi_app_test.models.responses.InventoryCountDetails;

import java.util.List;
import java.util.Locale;

public class InventoyCountDetailsAdapter extends RecyclerView.Adapter<InventoyCountDetailsAdapter.InventoryCountDetailsViewHolder> {

    private Context mContext;
    private List<InventoryCountDetails> inventoryCountsList;

    public InventoyCountDetailsAdapter(Context mContext, List<InventoryCountDetails> inventoryCountsList) {
        this.mContext = mContext;
        this.inventoryCountsList = inventoryCountsList;
    }

    @NonNull
    @Override
    public InventoryCountDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inventory_count_details_content, parent, false);
        return new InventoryCountDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryCountDetailsViewHolder holder, final int position) {

        //InventoryCountSummary inventoryCountSummary = inventoryCountSummariesList.get(position);

        holder.tvItemDescription.setText(inventoryCountsList.get(position).getItemDescription());
        holder.tvVendorItem.setText(String.valueOf(inventoryCountsList.get(position).getVendorItem()));
        holder.tvCost.setText("$".concat(String.format(Locale.US,"%,.2f", inventoryCountsList.get(position).getCost())));
        holder.tvQuantity.setText(String.valueOf(inventoryCountsList.get(position).getQuantity()));
        holder.tvUnit.setText(inventoryCountsList.get(position).getUnitOfMeasure());
        holder.tvTotalInventory.setText("$".concat(String.format(Locale.US,"%,.2f", inventoryCountsList.get(position).getTotalAmount())));

        //if false change to No, if true change to Yes
        if (inventoryCountsList.get(position).getItemMaster().toString() == "true")
            holder.tvIsItemMaster.setText("Yes");
        else
            holder.tvIsItemMaster.setText("No");


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, inventoryCountsList.get(position).getWeekEndDate(), Toast.LENGTH_LONG).show();

//                Integer storeId = Integer.parseInt(SharedPrefManagerDepartment.getInstance(mContext).getDepartment().getStoreId());
//                Integer departmentId = Integer.parseInt(SharedPrefManagerDepartment.getInstance(mContext).getDepartment().getDepartmentId());
//                String weekEndDate = inventoryCountsList.get(position).getWeekEndDate();
//                Double totalInventory = inventoryCountsList.get(position).getTotalInventory();
//
//                getInventoryDetails(storeId, departmentId, weekEndDate, totalInventory);
            }

//            public void getInventoryDetails(Integer storeId, Integer departmentId, String weekEndDate, Double totalInventory) {
//                InventoryCountSummary inventoryCountSummary = new InventoryCountSummary(storeId, departmentId, weekEndDate, totalInventory, position);
//                SharedPrefManagerInventorySummary.getInstance(mContext).clear();
//                SharedPrefManagerInventorySummary.getInstance(mContext).saveInventoryCountSummary(inventoryCountSummary);
//
//                Intent intent = new Intent(mContext, InventoryCountDetailsActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                mContext.startActivity(intent);
//            }
        });
    }

    @Override
    public int getItemCount() {
        return inventoryCountsList.size();
    }

    class InventoryCountDetailsViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentLayout;

        TextView tvItemDescription, tvVendorItem, tvCost, tvQuantity, tvUnit, tvTotalInventory, tvIsItemMaster;

        public InventoryCountDetailsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItemDescription = itemView.findViewById(R.id.item_description);
            tvVendorItem = itemView.findViewById(R.id.vendor_item);
            tvCost = itemView.findViewById(R.id.item_cost);
            tvQuantity = itemView.findViewById(R.id.quantity);
            tvUnit = itemView.findViewById(R.id.unit);
            tvTotalInventory = itemView.findViewById(R.id.totalInventory);
            tvIsItemMaster = itemView.findViewById(R.id.is_item_master);
            parentLayout = itemView.findViewById(R.id.content_layout);
        }
    }
}
