package com.elrancho.pwi.pwi_app.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.ArrayMap;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.elrancho.pwi.pwi_app.R;
import com.elrancho.pwi.pwi_app.adapters.InventoyCountDetailsAdapter;
import com.elrancho.pwi.pwi_app.api.InventoryCountDetailsRetrofit;
import com.elrancho.pwi.pwi_app.api.ItemRetrofit;
import com.elrancho.pwi.pwi_app.api.UserRetrofit;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountDetails;
import com.elrancho.pwi.pwi_app.models.responses.InventoryCountDetailsResponse;
import com.elrancho.pwi.pwi_app.models.responses.Item;
import com.elrancho.pwi.pwi_app.models.responses.ItemResponse;
import com.elrancho.pwi.pwi_app.shared.ProgressBarVisibility;
import com.elrancho.pwi.pwi_app.shared.Utils;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCountDetailsActivity extends AppCompatActivity implements EMDKListener, DataListener, StatusListener, ScannerConnectionListener {

    //vars for the InventoryCountDetails Retrofit call
    private RecyclerView recyclerView;
    private InventoyCountDetailsAdapter inventoyCountDetailsAdapter;
    private List<InventoryCountDetails> inventoryCounts;

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;

    private TextView textViewData = null;
    private TextView textViewStatus = null;

    private TextView t2;
    private List<ScannerInfo> deviceList = null;

    private int scannerIndex = 0; // Keep the selected scanner

    private String statusString = "";
    private boolean isInventoryCountExist = false;

    private ProgressBarVisibility progressBarVisibility;

    private View vInventoryCountDetailsForm;
    private View vProgressBar;

    AlertDialog enterQuantityDialog;
    AlertDialog addNewItemDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_count_details_recyclerview);

        vInventoryCountDetailsForm = findViewById(R.id.content_layout);
        vProgressBar = findViewById(R.id.inventory_count_details_progress);

        retrofitCallInventoryCountDetails();

        beginScanning();

    }

    public void retrofitCallInventoryCountDetails() {

        String token = SharedPrefManager.getInstance(this).getUuser().getToken();
        String storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();
        String departmentId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getDepartmentId();
        String weekEndDate = SharedPrefManagerInventorySummary.getInstance(this).getInventorySummary().getWeekEndDate();

        recyclerView = findViewById(R.id.inventory_count_details_recyclerview);

        Call<InventoryCountDetailsResponse> call = InventoryCountDetailsRetrofit
                .getInstance().getInventoryCountDetailsApi().getInventoryCountDetails(token, storeId, departmentId, weekEndDate);

        progressBarVisibility = new ProgressBarVisibility(this, vInventoryCountDetailsForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        call.enqueue(new Callback<InventoryCountDetailsResponse>() {
            @Override
            public void onResponse(Call<InventoryCountDetailsResponse> call, Response<InventoryCountDetailsResponse> response) {
                progressBarVisibility.showProgress(false);

                inventoryCounts = response.body().getInventoryCounts();
                Collections.sort(inventoryCounts, new Comparator<InventoryCountDetails>() {
                    @Override
                    public int compare(InventoryCountDetails o1, InventoryCountDetails o2) {
                        if (!o2.getDateUpdated().equals(null))
                            return o2.getDateUpdated().compareTo(o1.getDateUpdated());
                        return 0;
                    }

                });
                inventoyCountDetailsAdapter = new InventoyCountDetailsAdapter(InventoryCountDetailsActivity.this, inventoryCounts);
                recyclerView.setAdapter(inventoyCountDetailsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(InventoryCountDetailsActivity.this));
            }

            @Override
            public void onFailure(Call<InventoryCountDetailsResponse> call, Throwable t) {
                progressBarVisibility.showProgress(false);

            }
        });
    }

    //Item Retrofit Call
    public void retrofitCallItemDetails(String vendorItem) {

        final String scannedVendorItem = vendorItem;
        String token = SharedPrefManager.getInstance(this).getUuser().getToken();
        String storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();

        recyclerView = findViewById(R.id.inventory_count_details_recyclerview);

        t2 = findViewById(R.id.textViewData2);

        Call<ItemResponse> call = ItemRetrofit.getInstance().getItemApi().getItem(token, storeId, scannedVendorItem);

        progressBarVisibility = new ProgressBarVisibility(this, vInventoryCountDetailsForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        call.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                progressBarVisibility.showProgress(false);
                //******this block of code was added in case the user scan an item multiple times without dismissing the open enterQuantity dialog
                if (enterQuantityDialog != null && enterQuantityDialog.isShowing())
                    enterQuantityDialog.dismiss();

                if (addNewItemDialog != null && addNewItemDialog.isShowing())
                    addNewItemDialog.dismiss();
                //******

                final Integer storeId, itemUpc, vendorItem;
                String itemDescription, unitOfMeasure;
                Double itemCost;
                boolean itemMaster;

                if (response.code() == 200) {
                    ItemResponse itemResponse = response.body();

                    storeId = itemResponse.getItems().get(0).getStoreId();
                    itemDescription = itemResponse.getItems().get(0).getDescription();
                    itemUpc = itemResponse.getItems().get(0).getItemUPC();
                    vendorItem = itemResponse.getItems().get(0).getVendorItem();
                    itemCost = itemResponse.getItems().get(0).getCost();
                    unitOfMeasure = itemResponse.getItems().get(0).getUnitOfMeasure();
                    itemMaster = itemResponse.getItems().get(0).getItemMaster();

                    Item item = new Item(itemUpc, vendorItem, storeId, itemDescription, "", itemCost, unitOfMeasure, itemMaster, "");

                    SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).clear();
                    SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).saveItem(item);

                    //populating the fields in the quantity dialog box
                    findViewById(R.id.scrollView1).post(new Runnable() {
                        public void run() {
                            ((ScrollView) findViewById(R.id.scrollView1)).fullScroll(View.FOCUS_DOWN);
                        }
                    });

                    LayoutInflater inflater = LayoutInflater.from(InventoryCountDetailsActivity.this);
                    final View quantityDialog = inflater.inflate(R.layout.enter_quantity_activity, null);

                    final EditText etQuantity = quantityDialog.findViewById(R.id.etQuantity);
                    final TextView etItemDescription = quantityDialog.findViewById(R.id.item_description);
                    final TextView etVendorItem = quantityDialog.findViewById(R.id.vendorItem);
                    final TextView etItemCost = quantityDialog.findViewById(R.id.item_cost);
                    final TextView etUnitOfMeasure = quantityDialog.findViewById(R.id.unit_of_measure);
                    etItemDescription.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getDescription());
                    etVendorItem.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getVendorItem().toString());
                    etItemCost.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getCost().toString());
                    etUnitOfMeasure.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getUnitOfMeasure());

                    //populate etQuantity with the actual quantity if the item already exist in the InventoryCount table
                    etQuantity.setText("0");
                    if (inventoryCounts.size() > 0) {
                        Iterable<InventoryCountDetails> inventoryCountDetailsIterator = inventoryCounts;
                        for (InventoryCountDetails icd : inventoryCountDetailsIterator) {
                            if ((icd.getVendorItem() - vendorItem) == 0) {
                                etQuantity.setText(icd.getQuantity().toString());
                                isInventoryCountExist = true;
                                break;
                            }
                        }
                    }

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
                    etQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etItemDescription, etItemCost, etQuantity);
                            enterQuantityDialog.dismiss();
                            return false;
                        }
                    });

                    enterQuantityDialog = new AlertDialog.Builder(InventoryCountDetailsActivity.this)
                            .setTitle("Enter Quantity")
                            .setView(quantityDialog)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etItemDescription, etItemCost, etQuantity);
                                }
                            })
                            .setNegativeButton("Cancel", null).create();
                    enterQuantityDialog.show();

                }

                if (response.code() == 404) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                    alertDialogBuilder.setTitle("Item Not found!");
                    alertDialogBuilder.setMessage("item " + scannedVendorItem + " not found in the item master. Would you like to added?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LayoutInflater inflater = LayoutInflater.from(InventoryCountDetailsActivity.this);
                            final View newItemDialog = inflater.inflate(R.layout.add_new_item, null);

                            final EditText etVendorItem = newItemDialog.findViewById(R.id.vendorItem);
                            final EditText etItemDescription = newItemDialog.findViewById(R.id.item_description);

                            final EditText etItemCost = newItemDialog.findViewById(R.id.item_cost);
                            final EditText etUnitOfMeasure = newItemDialog.findViewById(R.id.unit_of_measure);
                            final EditText etQuantity = newItemDialog.findViewById(R.id.etQuantity);

                            etVendorItem.setText(scannedVendorItem);


                            addNewItemDialog = new AlertDialog.Builder(InventoryCountDetailsActivity.this)
                                    .setTitle("Enter item information")
                                    .setView(newItemDialog)
                                    .setPositiveButton("OK", null)
                                    .setNegativeButton("Cancel", null).create();

                            addNewItemDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialog) {
                                    Button okButton = addNewItemDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    etVendorItem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                        @Override
                                        public void onFocusChange(View v, boolean hasFocus) {
                                            etVendorItem.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    InputMethodManager inputMethodManager = (InputMethodManager) InventoryCountDetailsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                    inputMethodManager.showSoftInput(etVendorItem, InputMethodManager.SHOW_IMPLICIT);
                                                }
                                            });
                                        }
                                    });
                                    etVendorItem.requestFocus();
                                    etVendorItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                        @Override
                                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                            etItemDescription.requestFocus();
                                            return false;
                                        }
                                    });
                                    etItemDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                        @Override
                                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                            etItemCost.requestFocus();
                                            return false;
                                        }
                                    });
                                    etItemCost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                        @Override
                                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                            etUnitOfMeasure.requestFocus();
                                            return false;
                                        }
                                    });
                                    etUnitOfMeasure.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                        @Override
                                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                            etQuantity.requestFocus();
                                            return false;
                                        }
                                    });
                                    etQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                        @Override
                                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                            createItem(etVendorItem, etItemDescription, etItemCost, etUnitOfMeasure, etQuantity);
                                            addNewItemDialog.dismiss();
                                            return false;
                                        }
                                    });
                                    okButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            createItem(etVendorItem, etItemDescription, etItemCost, etUnitOfMeasure, etQuantity);
                                            addNewItemDialog.dismiss();

                                        }
                                    });
                                }
                            });
                            addNewItemDialog.show();


                        }
                    });
                    alertDialogBuilder.setNegativeButton("No", null);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ItemResponse> call, Throwable t) {
                progressBarVisibility.showProgress(false);
            }
        });
    }

    public void retrofitCallInventoryCountDetailAddUpdate(TextView etVendorItem, TextView etItemDescription, TextView etItemCost, TextView etQuantity) {


        // Build the Json Request
        Map<String, String> jsonParams = new ArrayMap<>();
        jsonParams.put("storeId", SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser().getStoreId());
        jsonParams.put("departmentId", SharedPrefManagerDepartment.getInstance(InventoryCountDetailsActivity.this).getDepartment().getDepartmentId());
        jsonParams.put("userId", SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser().getUserid());
        jsonParams.put("vendorItem", etVendorItem.getText().toString());
        jsonParams.put("itemDescription", etItemDescription.getText().toString());
        jsonParams.put("cost", etItemCost.getText().toString());
        jsonParams.put("quantity", etQuantity.getText().toString());
        jsonParams.put("itemMaster", "true");

        RequestBody inventoryCount = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new JSONObject(jsonParams).toString());

        Call<ResponseBody> call;
        if (isInventoryCountExist == false)
            call = InventoryCountDetailsRetrofit.getInstance()
                    .getInventoryCountDetailsApi().createInventoryCountDetail(SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser()
                            .getToken(), inventoryCount);
        else
            call = InventoryCountDetailsRetrofit.getInstance()
                    .getInventoryCountDetailsApi().updateInventoryCountDetail(SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser()
                            .getToken(), inventoryCount);

        progressBarVisibility = new ProgressBarVisibility(InventoryCountDetailsActivity.this, vInventoryCountDetailsForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBarVisibility.showProgress(false);

                if (response.code() == 200) {

                    ResponseBody dr = response.body();
                    Toast.makeText(InventoryCountDetailsActivity.this, "saved", Toast.LENGTH_SHORT).show();

                    //Reload the Inventory count data
                    retrofitCallInventoryCountDetails();
                } else {
                    Toast.makeText(InventoryCountDetailsActivity.this, "The Service is down. Please try again later", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBarVisibility.showProgress(false);

                Toast.makeText(InventoryCountDetailsActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
        //************************************************************************************************************************
//        textViewData1.setText(etQuantity.getText());


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

                    //Set MSI
                    setDecoders();
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
                    //Set MSI
                    setDecoders();
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

            //allow scanning only if the selected week is the current week
            String weekEndDate = SharedPrefManagerInventorySummary.getInstance(InventoryCountDetailsActivity.this).getInventorySummary().getWeekEndDate();
            if (weekEndDate.equals(Utils.getInstance().getCurrentWeekEndDate()))
                retrofitCallItemDetails(result);
            else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                alertDialogBuilder.setTitle("Week closed!");
                alertDialogBuilder.setMessage("The selected week is closed. Scanning is allowed for the current week only.");
                alertDialogBuilder.setPositiveButton("OK", null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
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

    private void createItem(final EditText etVendorItem, final EditText etDescription, final EditText etItemCost, final EditText etUnitOfMeasure, final EditText etQuantity) {

        String vendorItem = etVendorItem.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String itemCost = etItemCost.getText().toString().trim();
        String unitOfMeasure = etUnitOfMeasure.getText().toString().trim();

        if (vendorItem.isEmpty()) {
            etVendorItem.setError("VendorItem is required");
            etVendorItem.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            etDescription.setError("Item description is required");
            etDescription.requestFocus();
            return;
        }
        if (itemCost.isEmpty()) {
            etItemCost.setError("Item cost is required");
            etItemCost.requestFocus();
            return;
        }
        if (unitOfMeasure.isEmpty()) {
            etUnitOfMeasure.setError("Unit of measure is required");
            etUnitOfMeasure.requestFocus();
            return;
        }

        // call the store api and populate the store spinner
        Map<String, String> jsonParams = new ArrayMap<>();
        jsonParams.put("vendorItem", vendorItem);
        jsonParams.put("storeId", SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser().getStoreId());
        jsonParams.put("description", description);
        jsonParams.put("cost", itemCost);
        jsonParams.put("unitOfMeasure", unitOfMeasure);
        jsonParams.put("itemMaster", "false");

        RequestBody newItem = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new JSONObject(jsonParams).toString());


        Call<ResponseBody> call = ItemRetrofit.getInstance().getItemApi().createItem(SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser().getToken(), newItem);

        progressBarVisibility = new ProgressBarVisibility(InventoryCountDetailsActivity.this, vInventoryCountDetailsForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                progressBarVisibility.showProgress(false);
                retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etDescription, etItemCost, etQuantity);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                progressBarVisibility.showProgress(false);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                alertDialogBuilder.setTitle("The Service is down");
                alertDialogBuilder.setMessage("The Service is down. Please try again later");
                alertDialogBuilder.setPositiveButton("OK", null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }
}
