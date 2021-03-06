package com.elrancho.pwi.pwi_app_test.fragments;//package com.elrancho.pwi.pwi_app_test.fragments;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.elrancho.pwi.pwi_app_test.R;
//import com.elrancho.pwi.pwi_app_test.activities.InventoryCountDetailsActivity;
//import com.elrancho.pwi.pwi_app_test.adapters.InventoyCountSummaryAdapter;
//import com.elrancho.pwi.pwi_app_test.api.InventoryCountSummaryRetrofit;
//import com.elrancho.pwi.pwi_app_test.models.responses.InventoryCountSummary;
//import com.elrancho.pwi.pwi_app_test.models.responses.InventoryCountSummaryResponse;
//import com.elrancho.pwi.pwi_app_test.storage.SharedPrefManager;
//import com.elrancho.pwi.pwi_app_test.storage.SharedPrefManagerDepartment;
//import com.elrancho.pwi.pwi_app_test.storage.SharedPrefManagerInventorySummary;
//import com.google.gson.Gson;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class InventoryCountSummaryFragment extends Fragment implements View.OnClickListener {
//
//    private RecyclerView recyclerView;
//    private InventoyCountSummaryAdapter inventoyCountSummaryAdapter;
//    private List<InventoryCountSummary> inventoryCountSummaries;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View  view= inflater.inflate(R.layout.inventory_count_summary_recyclerview, container, false);
//
//        View inflateView = getLayoutInflater().inflate(R.layout.inventory_count_summary_content, null);
//
//        inflateView.findViewById(R.id.inventory_count_summary_card).setOnClickListener(this);
//
//        return view;
//
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//
//        super.onViewCreated(view, savedInstanceState);
//
//        String token = SharedPrefManager.getInstance(getContext()).getUuser().getToken();
//        String storeId = SharedPrefManagerDepartment.getInstance(getContext()).getDepartment().getStoreId();
//        String departmentId = SharedPrefManagerDepartment.getInstance(getContext()).getDepartment().getDepartmentId();
//
//        recyclerView = view.findViewById(R.id.inventory_count_summary_recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        Call<InventoryCountSummaryResponse> call = InventoryCountSummaryRetrofit
//                .getInstance().getInventoryCountSummaryApi().getInventoryCountSummary(token, storeId, departmentId);
//
//        call.enqueue(new Callback<InventoryCountSummaryResponse>() {
//            @Override
//            public void onResponse(Call<InventoryCountSummaryResponse> call, Response<InventoryCountSummaryResponse> response) {
//
//                inventoryCountSummaries = response.body().getInventoryCountSummaries();
//                inventoyCountSummaryAdapter = new InventoyCountSummaryAdapter(getActivity(), inventoryCountSummaries);
//                recyclerView.setAdapter(inventoyCountSummaryAdapter);
//
//                //Saving the inventoryCountSummaries in the SharedPref as a String using JSON
//                ArrayList<InventoryCountSummaryResponse> icsList = new ArrayList<>();
//                String icsString = new Gson().toJson(icsList);
//                //SharedPrefManagerInventorySummary.getInstance(InventoryCountSummaryActivity.this).saveInventoryCountSummary(icsString);
//            }
//
//            @Override
//            public void onFailure(Call<InventoryCountSummaryResponse> call, Throwable t) {
//
//            }
//        });
//
//    }
//
//
//
//    @Override
//    public void onClick(View view) {
//
//        Integer storeId = Integer.parseInt(SharedPrefManagerDepartment.getInstance(getContext()).getDepartment().getStoreId());
//        Integer departmentId = Integer.parseInt(SharedPrefManagerDepartment.getInstance(getContext()).getDepartment().getDepartmentId());
//        View inflateView = getLayoutInflater().inflate(R.layout.inventory_count_summary_content, null);
//        TextView weekEndDate = inflateView.findViewById(R.id.tvWeekEndDate);
//        TextView totalInventory = inflateView.findViewById(R.id.tvInventoryCountSummary);
//
//        switch (view.getId()) {
//            case R.id.inventory_count_summary_card:
//
//                getInventoryDetails(storeId, departmentId, weekEndDate.getText().toString(), Double.parseDouble(totalInventory.getText().toString()));
//                break;
//        }
//    }
//
//    public void getInventoryDetails(Integer storeId, Integer departmentId, String weekEndDate, Double totalInvnetory) {
//
//
//        InventoryCountSummary inventoryCountSummary = new InventoryCountSummary(storeId, departmentId, weekEndDate, totalInvnetory);
//        SharedPrefManagerInventorySummary.getInstance(getContext()).clear();
//        SharedPrefManagerInventorySummary.getInstance(getContext()).saveInventoryCountSummary(inventoryCountSummary);
//
//        Intent intent = new Intent(getContext(), InventoryCountDetailsActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        getContext().startActivity(intent);
//
//    }
//}
