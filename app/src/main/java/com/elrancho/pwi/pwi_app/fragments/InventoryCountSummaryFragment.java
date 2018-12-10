//package com.elrancho.pwi.pwi_app.fragments;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//
//import com.elrancho.pwi.pwi_app.R;
//import com.elrancho.pwi.pwi_app.adapters.InventoyCountSummaryAdapter;
//import com.elrancho.pwi.pwi_app.api.RetrofitInventoryCountSummary;
//import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummary;
//import com.elrancho.pwi.pwi_app.models.responses.InventoryCountSummaryResponse;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class InventoryCountSummaryFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private InventoyCountSummaryAdapter inventoyCountSummaryAdapter;
//    private List<InventoryCountSummary> inventoryCountSummaries;
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//
//        super.onViewCreated(view, savedInstanceState);
//
//        recyclerView=view.findViewById(R.id.rvInventoryCountSummary);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        Call<InventoryCountSummaryResponse> call = RetrofitInventoryCountSummary.getInstance().getInventoryCOuntSummaryApi().
//                getInventoryCountSummary("1008", "1008200", "iweubiw");
//
//        call.enqueue(new Callback<InventoryCountSummaryResponse>() {
//            @Override
//            public void onResponse(Call<InventoryCountSummaryResponse> call, Response<InventoryCountSummaryResponse> response) {
//
//                inventoryCountSummaries=response.body().getInventoryCountSummaries();
//                inventoyCountSummaryAdapter=new InventoyCountSummaryAdapter(getActivity(), inventoryCountSummaries);
//                recyclerView.setAdapter(inventoyCountSummaryAdapter);
//
//
//            }
//
//            @Override
//            public void onFailure(Call<InventoryCountSummaryResponse> call, Throwable t) {
//
//            }
//        });
//
//    }
//}
