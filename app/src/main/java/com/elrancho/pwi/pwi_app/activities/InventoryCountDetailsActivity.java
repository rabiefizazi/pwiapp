package com.elrancho.pwi.pwi_app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.adapters.InventoyCountDetailsAdapter;
import com.elrancho.pwi.pwi_app.api.InventoryCountDetailsRetrofit;
import com.elrancho.pwi.pwi_app.api.ItemRetrofit;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountDetails;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountDetailsResponse;
import com.elrancho.pwi.pwi_app.models.responses.Item;
import com.elrancho.pwi.pwi_app.models.responses.ItemResponse;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManager;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerDepartment;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerInventorySummary;
import com.elrancho.pwi.pwi_app.storage.SharedPrefManagerItem;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.BarcodeManager.ScannerConnectionListener;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCountDetailsActivity extends Activity implements EMDKListener, DataListener, StatusListener, ScannerConnectionListener {

    //vars for the InventoryCountDetails Retrofit call
    private RecyclerView recyclerView;
    private InventoyCountDetailsAdapter inventoyCountDetailsAdapter;
    private List<InventoryCountDetails> inventoryCounts;

    //vars for item retrofit call
    private List<Item> items;

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;

    private TextView textViewData = null;
    private TextView textViewStatus = null;

    private TextView t2;
    private List<ScannerInfo> deviceList = null;

    private int scannerIndex = 0; // Keep the selected scanner

    private int dataLength = 0;
    private String statusString = "";

    //Item information
    String itemDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_count_details_recyclerview);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Sienna)));

        retrofitCallInventoryCountDetails();

        beginScanning();
    }

    public void retrofitCallInventoryCountDetails() {

        String token = SharedPrefManager.getInstance(this).getUuser().getToken();
        String storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();
        String departmentId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getDepartmentId();
        String weekEndDate = SharedPrefManagerInventorySummary.getInstance(this).getInventorySummary().getWeekEndDate();

        recyclerView = findViewById(R.id.inventory_details_count_recyclerview);

        Call<InventoryCountDetailsResponse> call = InventoryCountDetailsRetrofit
                .getInstance().getInventoryCountDetailsApi().getInventoryCountDetails(token, storeId, departmentId, weekEndDate);


        call.enqueue(new Callback<InventoryCountDetailsResponse>() {
            @Override
            public void onResponse(Call<InventoryCountDetailsResponse> call, Response<InventoryCountDetailsResponse> response) {

                inventoryCounts = response.body().getInventoryCounts();
                inventoyCountDetailsAdapter = new InventoyCountDetailsAdapter(InventoryCountDetailsActivity.this, inventoryCounts);
                recyclerView.setAdapter(inventoyCountDetailsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(InventoryCountDetailsActivity.this));
            }

            @Override
            public void onFailure(Call<InventoryCountDetailsResponse> call, Throwable t) {

            }
        });
    }

    //Item Retrofit Call
    public void retrofitCallItemDetails(String vendorItem) {

        String token = SharedPrefManager.getInstance(this).getUuser().getToken();
        String storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();

        recyclerView = findViewById(R.id.inventory_details_count_recyclerview);

        t2 = findViewById(R.id.textViewData2);

        Call<ItemResponse> call = ItemRetrofit.getInstance().getItemApi().getItem(token, storeId, vendorItem);


        call.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {

                Integer storeId = response.body().getItems().get(0).getStoreId();
                String itemDescription = response.body().getItems().get(0).getDescription();
                Integer itemUpc = response.body().getItems().get(0).getItemUPC();
                Integer vendorItem = response.body().getItems().get(0).getVendorItem();
                Double itemCost = response.body().getItems().get(0).getCost();
                String unitOfMeasure = response.body().getItems().get(0).getUnitOfMeasure();
                boolean itemMaster = response.body().getItems().get(0).getItemMaster();

                //t2.setText(items.get(0).getDescription());

                Item item = new Item(itemUpc, vendorItem, storeId, itemDescription, "", itemCost, unitOfMeasure, itemMaster, "");

                SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).clear();
                SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).saveItem(item);

                //populating the fields in the quantity dialog box
                if (vendorItem != null) {
                    if (dataLength++ > 100) { //Clear the cache after 100 scans
                        textViewData.setText("");
                        dataLength = 0;
                    }

                    textViewData.append(vendorItem + "\n");


                    ((View) findViewById(R.id.scrollView1)).post(new Runnable() {
                        public void run() {
                            ((ScrollView) findViewById(R.id.scrollView1)).fullScroll(View.FOCUS_DOWN);
                        }
                    });

                    final EditText textViewData1 = (EditText) findViewById(R.id.textViewData1);

                    LayoutInflater inflater = LayoutInflater.from(InventoryCountDetailsActivity.this);
                    final View quantityDialog = inflater.inflate(R.layout.activity_enter_quantity, null);

                    final EditText etQuantity = (EditText) quantityDialog.findViewById(R.id.etQuantity);

                    final TextView etItemDescription = quantityDialog.findViewById(R.id.item_description);
                    final TextView etVendorItem = quantityDialog.findViewById(R.id.vendorItem);
                    final TextView etItemCost = quantityDialog.findViewById(R.id.item_cost);
                    final TextView etUnitOfMeasure = quantityDialog.findViewById(R.id.unit_of_measure);

                    if (SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getDescription() != null) {
                        etItemDescription.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getDescription());
                        etVendorItem.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getVendorItem().toString());
                        etItemCost.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getCost().toString());
                        etUnitOfMeasure.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getUnitOfMeasure());
                    } else
                        etItemDescription.setText("failed");
                    //******************Testing code end****************//

                    etQuantity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            etQuantity.post(new Runnable() {
                                @Override
                                public void run() {
                                    InputMethodManager inputMethodManager = (InputMethodManager) InventoryCountDetailsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.showSoftInput(etQuantity, InputMethodManager.SHOW_IMPLICIT);
                                }
                            });
                        }
                    });

                    etQuantity.requestFocus();

                    AlertDialog dialog = new AlertDialog.Builder(InventoryCountDetailsActivity.this)
                            .setTitle("Enter Quantity")
                            .setView(quantityDialog)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    textViewData1.setText(etQuantity.getText());
                                }
                            })
                            .setNegativeButton("Cancel", null).create();

                    dialog.show();

                }

            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {

            }
        });
    }

    public void beginScanning() {
        textViewData = findViewById(R.id.textViewData);
        textViewStatus = findViewById(R.id.textViewStatus);


        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            textViewStatus.setText("Status: " + "EMDKManager object request failed!");
            return;
        }

        textViewData.setSelected(true);
        textViewData.setMovementMethod(new ScrollingMovementMethod());
    }


    @Override
    public void onConnectionChange(ScannerInfo scannerInfo, BarcodeManager.ConnectionState connectionState) {

        String status;
        String scannerName = "";

        String statusExtScanner = connectionState.toString();
        String scannerNameExtScanner = scannerInfo.getFriendlyName();

        if (deviceList.size() != 0) {
            scannerName = deviceList.get(scannerIndex).getFriendlyName();
        }

        if (scannerName.equalsIgnoreCase(scannerNameExtScanner)) {

            switch (connectionState) {
                case CONNECTED:
                    deInitScanner();
                    initScanner();
                    setTrigger();
                    setDecoders();
                    break;
                case DISCONNECTED:
                    deInitScanner();
                    break;
            }

            status = scannerNameExtScanner + ":" + statusExtScanner;
            new AsyncStatusUpdate().execute(status);
        } else {
            status = statusString + " " + scannerNameExtScanner + ":" + statusExtScanner;
            new AsyncStatusUpdate().execute(status);
        }
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {

        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
            for (ScanDataCollection.ScanData data : scanData) {

                String dataString = data.getData();

                new AsyncDataUpdate().execute(dataString);
            }
        }
    }

    @Override
    public void onStatus(StatusData statusData) {

        StatusData.ScannerStates state = statusData.getState();
        switch (state) {
            case IDLE:
                statusString = statusData.getFriendlyName() + " is enabled and idle...";
                new AsyncStatusUpdate().execute(statusString);
                try {
                    // An attempt to use the scanner continuously and rapidly (with a delay < 100 ms between scans)
                    // may cause the scanner to pause momentarily before resuming the scanning.
                    // Hence add some delay (>= 100ms) before submitting the next read.
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    scanner.read();
                } catch (ScannerException e) {
                    statusString = e.getMessage();
                    new AsyncStatusUpdate().execute(statusString);
                }
                break;
            case WAITING:
                statusString = "Scanner is waiting for trigger press...";
                new AsyncStatusUpdate().execute(statusString);
                break;
            case SCANNING:
                statusString = "Scanning...";
                new AsyncStatusUpdate().execute(statusString);
                break;
            case DISABLED:
                statusString = statusData.getFriendlyName() + " is disabled.";
                new AsyncStatusUpdate().execute(statusString);
                break;
            case ERROR:
                statusString = "An error has occurred.";
                new AsyncStatusUpdate().execute(statusString);
                break;
            default:
                break;
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {

        textViewStatus.setText("Status: " + "EMDK open success!");

        this.emdkManager = emdkManager;

        // Acquire the barcode manager resources
        barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

        // Add connection listener
        if (barcodeManager != null) {
            deviceList = barcodeManager.getSupportedDevicesInfo();
            barcodeManager.addConnectionListener(this);
        }

        startScan();
    }

    @Override
    public void onClosed() {

        if (emdkManager != null) {

            // Remove connection listener
            if (barcodeManager != null) {
                barcodeManager.removeConnectionListener(this);
                barcodeManager = null;
            }

            // Release all the resources
            emdkManager.release();
            emdkManager = null;
        }
        textViewStatus.setText("Status: " + "EMDK closed unexpectedly! Please close and restart the application.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The application is in background

        // De-initialize scanner
        deInitScanner();

        // Remove connection listener
        if (barcodeManager != null) {
            barcodeManager.removeConnectionListener(this);
            barcodeManager = null;
            deviceList = null;
        }

        // Release the barcode manager resources
        if (emdkManager != null) {
            emdkManager.release(EMDKManager.FEATURE_TYPE.BARCODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The application is in foreground

        // Acquire the barcode manager resources
        if (emdkManager != null) {
            barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

            // Add connection listener
            if (barcodeManager != null) {
                deviceList = barcodeManager.getSupportedDevicesInfo();
                barcodeManager.addConnectionListener(this);
            }

            // Initialize scanner
            initScanner();
            setTrigger();
            setDecoders();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // De-initialize scanner
        deInitScanner();

        // Remove connection listener
        if (barcodeManager != null) {
            barcodeManager.removeConnectionListener(this);
            barcodeManager = null;
        }

        // Release all the resources
        if (emdkManager != null) {
            emdkManager.release();
            emdkManager = null;

        }
    }

    private void setTrigger() {

        if (scanner == null) {
            initScanner();
        }

        if (scanner != null) {
            scanner.triggerType = Scanner.TriggerType.HARD;
        }
    }

    private void setDecoders() {

        if (scanner == null) {
            initScanner();
        }

        if ((scanner != null) && (scanner.isEnabled())) {
            try {

                ScannerConfig config = scanner.getConfig();

                // Set EAN8
                config.decoderParams.ean8.enabled = true;
                // Set EAN13
                config.decoderParams.ean13.enabled = true;
                // Set Code39
                config.decoderParams.code39.enabled = true;
                //Set Code128
                config.decoderParams.code128.enabled = true;
                //Set MSI
                config.decoderParams.msi.enabled = true;
                scanner.setConfig(config);

            } catch (ScannerException e) {

                textViewStatus.setText("Status: " + e.getMessage());
            }
        }
    }

    private void startScan() {

        if (scanner != null) {

            try {

                // Cancel the pending read.
                scanner.cancelRead();

            } catch (ScannerException e) {

                textViewStatus.setText("Status: " + e.getMessage());
            }
        }
        if (scanner == null) {
            initScanner();
        }

        if (scanner != null) {
            try {

                if (scanner.isEnabled()) {
                    ScannerConfig config = scanner.getConfig();
                    //Set MSI
                    config.decoderParams.msi.enabled = true;
                    scanner.setConfig(config);
                    // Submit a new read.
                    scanner.read();

                } else {
                    textViewStatus.setText("Status: Scanner is not enabled");
                }

            } catch (ScannerException e) {

                textViewStatus.setText("Status: " + e.getMessage());
            }
        }

    }

    private void initScanner() {

        if (scanner == null) {

            //deviceList = barcodeManager.getSupportedDevicesInfo();

            if ((deviceList != null) && (deviceList.size() != 0)) {
                scanner = barcodeManager.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT);
            } else {
                textViewStatus.setText("Status: " + "Failed to get the specified scanner device! Please close and restart the application.");
                return;
            }

            if (scanner != null) {

                scanner.addDataListener(this);
                scanner.addStatusListener(this);

                try {
                    scanner.enable();
                } catch (ScannerException e) {

                    textViewStatus.setText("Status: " + e.getMessage());
                }
            } else {
                textViewStatus.setText("Status: " + "Failed to initialize the scanner device.");
            }
        }
    }

    private void deInitScanner() {

        if (scanner != null) {

            try {

                scanner.cancelRead();
                scanner.disable();

            } catch (Exception e) {

                textViewStatus.setText("Status: " + e.getMessage());
            }

            try {
                scanner.removeDataListener(this);
                scanner.removeStatusListener(this);

            } catch (Exception e) {

                textViewStatus.setText("Status: " + e.getMessage());
            }

            try {
                scanner.release();
            } catch (Exception e) {

                textViewStatus.setText("Status: " + e.getMessage());
            }

            scanner = null;
        }
    }


    private class AsyncDataUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        protected void onPostExecute(String result) {

            retrofitCallItemDetails(result);
        }
    }

    private class AsyncStatusUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {

            textViewStatus.setText("Status: " + result);
        }
    }
}
