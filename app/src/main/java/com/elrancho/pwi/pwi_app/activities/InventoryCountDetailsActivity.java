package com.elrancho.pwi.pwi_app.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryCountDetailsActivity extends AppCompatActivity implements EMDKListener, DataListener, StatusListener, ScannerConnectionListener, View.OnClickListener {


    private static int REQUEST_CODE = 1;


    //vars for the InventoryCountDetails Retrofit call
    private RecyclerView recyclerView;
    private InventoyCountDetailsAdapter inventoryCountDetailsAdapter;
    private List<InventoryCountDetails> inventoryCounts;

    private EMDKManager emdkManager = null;
    private BarcodeManager barcodeManager = null;
    private Scanner scanner = null;

    private TextView textViewData = null;
    private TextView textViewStatus = null;

    private List<ScannerInfo> deviceList = null;

    private int scannerIndex = 0; // Keep the selected scanner

    private String statusString = "";
    private boolean isInventoryCountExist = false;

    private ProgressBarVisibility progressBarVisibility;

    private View vInventoryCountDetailsForm;
    private View vProgressBar;

    AlertDialog enterQuantityDialog;
    AlertDialog addNewItemDialog;

    AlertDialog manualItemSearchDialog;

    TextView etWeekEndDate;

    private String token, storeId, departmentId, weekEndDate;

    /************************** Code Enhancement for phase 2 : Begin**************************/
    // Scanning Areas
    private Button btnSalesFloor, btnBackroom, btnCooler, btnFreezer, btnSupplies;

    //the booleans have to be initialized with TRUE so that the button background change work properly
    private boolean isBtnSalesFloorPressed = false, isBtnBackroomPressed = false, isBtnCoolerPressed = false, isBtnFreezerPressed = false, isBtnSuppliesPressed = false;

    private boolean addMoreQty = false, modifyQty = false;
    private double quantityFound = 0;

    private EditText etQuantity;
    private TextView etItemCost;
    private TextView tvQuantity;

    private List<InventoryCountDetails> filteredInventoryCounts;

    // for total inventory by the selected areas
    double totalInventoryBySelectedAreas = 0;
    TextView textViewInventoryTotalByArea;
    int manipulatedAreaId = 0, actualAreaId = 0;
    boolean isAnybuttonselected = false;
    /************************** Code Enhancement for phase 2 : End**************************/

    // Properties that make the activity title
    int storeIdTitle;
    String departmentName;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_count_details_recyclerview);

        /************************** Code Enhancement for phase 2 : Begin**************************/

        //initialize the filtered inventoryCount list
        filteredInventoryCounts = new ArrayList<>();
        // Scanning Areas
        //Salesfloor
        btnSalesFloor = findViewById(R.id.btn_sales_floor);
        btnSalesFloor.setOnClickListener(this);
        Utils.getInstance().setBtnPressed(InventoryCountDetailsActivity.this, btnSalesFloor, false);
        btnSalesFloor.setTextColor(getResources().getColor(R.color.DimGray));
        //backroom
        btnBackroom = findViewById(R.id.btn_backroom);
        btnBackroom.setOnClickListener(this);
        Utils.getInstance().setBtnPressed(InventoryCountDetailsActivity.this, btnBackroom, false);
        btnBackroom.setTextColor(getResources().getColor(R.color.DimGray));
        //Cooler
        btnCooler = findViewById(R.id.btn_cooler);
        btnCooler.setOnClickListener(this);
        Utils.getInstance().setBtnPressed(InventoryCountDetailsActivity.this, btnCooler, false);
        btnCooler.setTextColor(getResources().getColor(R.color.DimGray));
        //Freezer
        btnFreezer = findViewById(R.id.btn_freezer);
        btnFreezer.setOnClickListener(this);
        Utils.getInstance().setBtnPressed(InventoryCountDetailsActivity.this, btnFreezer, false);
        btnFreezer.setTextColor(getResources().getColor(R.color.DimGray));
        //Supplies
        btnSupplies = findViewById(R.id.btn_supplies);
        btnSupplies.setOnClickListener(this);
        Utils.getInstance().setBtnPressed(InventoryCountDetailsActivity.this, btnSupplies, false);
        btnSupplies.setTextColor(getResources().getColor(R.color.DimGray));

        textViewInventoryTotalByArea = findViewById(R.id.tv_inventory_total_by_area);
        if (SharedPrefManagerInventorySummary.getInstance(this).getInventorySummary().getTotalInventory() > 0)
            textViewInventoryTotalByArea.setText(String.valueOf(SharedPrefManagerInventorySummary.getInstance(this).getInventorySummary().getTotalInventory()));
        else
            textViewInventoryTotalByArea.setText("0");
        /************************** Code Enhancement for phase 2 : End**************************/


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE);

        token = SharedPrefManager.getInstance(this).getUuser().getToken();
        storeId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getStoreId();
        departmentId = SharedPrefManagerDepartment.getInstance(this).getDepartment().getDepartmentId();
        weekEndDate = SharedPrefManagerInventorySummary.getInstance(this).getInventorySummary().getWeekEndDate();

        vInventoryCountDetailsForm = findViewById(R.id.content_layout);
        vProgressBar = findViewById(R.id.inventory_count_details_progress);

        storeIdTitle = Integer.parseInt(SharedPrefManager.getInstance(this).getUuser().getStoreId()) % 1000;

        String last3digits = departmentId.substring(4);
        departmentName = Utils.getInstance().convertToDepartmentName(last3digits);


        getSupportActionBar().setTitle(+storeIdTitle + " | " + departmentName + " | " + weekEndDate);

        retrofitCallInventoryCountDetails();

        beginScanning();

    }

    public void retrofitCallInventoryCountDetails() {

        recyclerView = findViewById(R.id.inventory_count_details_recyclerview);

        progressBarVisibility = new ProgressBarVisibility(this, vInventoryCountDetailsForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        Call<InventoryCountDetailsResponse> call = InventoryCountDetailsRetrofit
                .getInstance().getInventoryCountDetailsApi().getInventoryCountDetails(token, storeId, departmentId, weekEndDate);


        call.enqueue(new Callback<InventoryCountDetailsResponse>() {
            @Override
            public void onResponse(Call<InventoryCountDetailsResponse> call, Response<InventoryCountDetailsResponse> response) {
                progressBarVisibility.showProgress(false);

                inventoryCounts = response.body().getInventoryCounts();
                Collections.sort(inventoryCounts, new Comparator<InventoryCountDetails>() {
                    @Override
                    public int compare(InventoryCountDetails o1, InventoryCountDetails o2) {
                        if (o2.getDateUpdated() != null)
                            return o2.getDateUpdated().compareTo(o1.getDateUpdated());
                        return 0;
                    }

                });

                /************************** Code Enhancement for phase 2 : Begin**************************/
                // If one or more areas are already selected use the filterInventoryCounts, else use inventoryCounts
                if (actualAreaId != 0) {
                    Button btnTemp = null;
                    switch (actualAreaId) {
                        case 1:
                            btnTemp = btnSalesFloor;
                            break;
                        case 2:
                            btnTemp = btnBackroom;
                            break;
                        case 3:
                            btnTemp = btnCooler;
                            break;
                        case 4:
                            btnTemp = btnFreezer;
                            break;
                        case 5:
                            btnTemp = btnSupplies;
                            break;
                    }
                    filteredInventoryCounts.clear();
                    filterBasedOnAreaSelected(true, btnTemp, actualAreaId);
                }
                /************************** Code Enhancement for phase 2 : End**************************/
                else {
                    inventoryCountDetailsAdapter = new InventoyCountDetailsAdapter(InventoryCountDetailsActivity.this, inventoryCounts);
                    recyclerView.setAdapter(inventoryCountDetailsAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(InventoryCountDetailsActivity.this));
                    calculateTotalInventoryByArea(inventoryCounts);
                }
            }

            @Override
            public void onFailure(Call<InventoryCountDetailsResponse> call, Throwable t) {
                progressBarVisibility.showProgress(false);

            }
        });
    }

    //Item Retrofit Call
    public void retrofitCallItemDetails(String vendorItem) {

        isInventoryCountExist = false;

        final String scannedVendorItem = vendorItem;

        recyclerView = findViewById(R.id.inventory_count_details_recyclerview);

        progressBarVisibility = new ProgressBarVisibility(this, vInventoryCountDetailsForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        Call<ItemResponse> call = ItemRetrofit.getInstance().getItemApi().getItem(token, storeId, scannedVendorItem);

        call.enqueue(new Callback<ItemResponse>() {
            @Override
            public void onResponse(Call<ItemResponse> call, Response<ItemResponse> response) {
                progressBarVisibility.showProgress(false);
                //******this block of code was added in case the user scan an item multiple times without dismissing the open enterQuantity dialog
                if (enterQuantityDialog != null && enterQuantityDialog.isShowing())
                    enterQuantityDialog.dismiss();

                if (addNewItemDialog != null && addNewItemDialog.isShowing())
                    addNewItemDialog.dismiss();
                //********************************************************************************************************************************

                final long storeId, itemUpc, vendorItem;
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

                    etQuantity = quantityDialog.findViewById(R.id.etQuantity);
                    final TextView etItemDescription = quantityDialog.findViewById(R.id.item_description);
                    final TextView etVendorItem = quantityDialog.findViewById(R.id.vendorItem);
                    etItemCost = quantityDialog.findViewById(R.id.item_cost);
                    final TextView etUnitOfMeasure = quantityDialog.findViewById(R.id.unit_of_measure);

                    etItemDescription.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getDescription());
                    etVendorItem.setText(String.valueOf(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getVendorItem()));
                    etItemCost.setText(String.valueOf(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getCost()));
                    etUnitOfMeasure.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getUnitOfMeasure());

                    /************************** Code Enhancement for phase 2 : Begin**************************/
                    // adding the unit of measure next to the EditText of the "enter quantity"
                    final TextView etUnitOfMeasure2 = quantityDialog.findViewById(R.id.unit_of_measure2);
                    etUnitOfMeasure2.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getUnitOfMeasure());
                    /************************** Code Enhancement for phase 2 : end****************************/

                    //populate etQuantity with the actual quantity if the item already exist in the InventoryCount table
                    etQuantity.setText("");
                    if (inventoryCounts.size() > 0) {
                        Iterable<InventoryCountDetails> inventoryCountDetailsIterator = inventoryCounts;
                        for (InventoryCountDetails icd : inventoryCountDetailsIterator) {
                            if ((icd.getVendorItem() - vendorItem) == 0) {
                                etQuantity.setText(String.valueOf(icd.getQuantity()));
                                /************************** Code Enhancement for phase 2 : begin****************************/
                                if (actualAreaId == icd.getAreaId())
                                /************************** Code Enhancement for phase 2 : begin****************************/
                                    isInventoryCountExist = true;
                                break;
                            }
                        }
                    }

                    /************************** Code Enhancement for phase 2 : begin****************************/
                    tvQuantity = quantityDialog.findViewById(R.id.tv_quantity);
                    //if the item has already been scanned

                    if (isInventoryCountExist) {
                        quantityFound = Double.valueOf(etQuantity.getText().toString());
                        //Assign the quantity to the textView if the item has already been scanned
                        tvQuantity.setText(String.valueOf(quantityFound));
                        etItemDescription.setText(SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getDescription());

                        //Add a dialog box to inform the user that this item has already been scanned, and give him the choice to add more or modify the quantity.

                        String tvItemAlreadyScanned = "the item \n" + SharedPrefManagerItem.getInstance(InventoryCountDetailsActivity.this).getItem().getDescription() +
                                " \nhas been scanned already \nThe quantity found is " + quantityFound + " \nDo you wan to?";

                        AlertDialog.Builder itemAlreadyScannedAlertDialog = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                        itemAlreadyScannedAlertDialog.setMessage(tvItemAlreadyScanned);
                        itemAlreadyScannedAlertDialog.setPositiveButton("Modify Qty", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addMoreQty = false;
                                modifyQty = true;
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
                                        retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etItemDescription, etItemCost, etQuantity, etUnitOfMeasure, actualAreaId);
                                        enterQuantityDialog.dismiss();
                                        return false;
                                    }
                                });
                                //alert the user when he enters quantity that makes the inventory greater that $1000.00
                                alertUserWhenInventoryIsGreaterThan999();

                                enterQuantityDialog = new AlertDialog.Builder(InventoryCountDetailsActivity.this)
                                        .setTitle("Modify Quantity")
                                        .setView(quantityDialog)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etItemDescription, etItemCost, etQuantity, etUnitOfMeasure, actualAreaId);
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).create();
                                enterQuantityDialog.show();
                            }
                        });
                        itemAlreadyScannedAlertDialog.setNegativeButton("Add More Qty", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                etQuantity.setText("0");
                                addMoreQty = true;
                                modifyQty = false;
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
                                        retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etItemDescription, etItemCost, etQuantity, etUnitOfMeasure, actualAreaId);
                                        enterQuantityDialog.dismiss();
                                        return false;
                                    }
                                });

                                //alert the user when he enters quantity that makes the inventory greater that $1000.00
                                alertUserWhenInventoryIsGreaterThan999();
                                enterQuantityDialog = new AlertDialog.Builder(InventoryCountDetailsActivity.this)
                                        .setTitle("Add More Quantity")
                                        .setView(quantityDialog)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etItemDescription, etItemCost, etQuantity, etUnitOfMeasure, actualAreaId);
                                            }
                                        })
                                        .setNegativeButton("Cancel", null).create();
                                enterQuantityDialog.show();
                            }
                        });

                        itemAlreadyScannedAlertDialog.show();


                        //if the item has not been scanned yet
                    } else {
                        //Assign the quantity to the textView if the item has already been scanned
                        tvQuantity.setText("0");
                        etQuantity.setText("0");
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
                                retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etItemDescription, etItemCost, etQuantity, etUnitOfMeasure, actualAreaId);
                                enterQuantityDialog.dismiss();
                                return false;
                            }
                        });
                        //alert the user when he enters quantity that makes the inventory greater that $1000.00
                        alertUserWhenInventoryIsGreaterThan999();
                        enterQuantityDialog = new AlertDialog.Builder(InventoryCountDetailsActivity.this)
                                .setTitle("Enter Quantity")
                                .setView(quantityDialog)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        retrofitCallInventoryCountDetailAddUpdate(etVendorItem, etItemDescription, etItemCost, etQuantity, etUnitOfMeasure, actualAreaId);
                                    }
                                })
                                .setNegativeButton("Cancel", null).create();
                        enterQuantityDialog.show();
                    }
                    /************************** Code Enhancement for phase 2 : end****************************/
                }

                if (response.code() == 404) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                    alertDialogBuilder.setTitle("Item Not found!");
                    alertDialogBuilder.setMessage("item " + scannedVendorItem + " not found in the item master.");
                    alertDialogBuilder.setPositiveButton("OK", null);
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

    public void retrofitCallInventoryCountDetailAddUpdate(TextView etVendorItem, TextView etItemDescription, TextView etItemCost, TextView etQuantity, TextView etUnitOfMeasure, int areaId) {


        // Build the Json Request
        Map<String, String> jsonParams = new ArrayMap<>();
        jsonParams.put("storeId", SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser().getStoreId());
        jsonParams.put("departmentId", SharedPrefManagerDepartment.getInstance(InventoryCountDetailsActivity.this).getDepartment().getDepartmentId());
        jsonParams.put("userId", SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser().getUserid());
        jsonParams.put("vendorItem", etVendorItem.getText().toString());
        jsonParams.put("itemDescription", etItemDescription.getText().toString());
        jsonParams.put("cost", etItemCost.getText().toString());
        jsonParams.put("areaId", String.valueOf(actualAreaId));
        /************************** Code Enhancement for phase 2 : begin****************************/
        //jsonParams.put("quantity", etQuantity.getText().toString());
        if (addMoreQty)
            jsonParams.put("quantity", String.valueOf((Double.valueOf(etQuantity.getText().toString()) + quantityFound)));
        else
            jsonParams.put("quantity", etQuantity.getText().toString());
        /************************** Code Enhancement for phase 2 : end****************************/
        jsonParams.put("itemMaster", "true");
        jsonParams.put("unitOfMeasure", etUnitOfMeasure.getText().toString());

        RequestBody inventoryCount = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), new JSONObject(jsonParams).toString());

        progressBarVisibility = new ProgressBarVisibility(InventoryCountDetailsActivity.this, vInventoryCountDetailsForm, vProgressBar);
        progressBarVisibility.showProgress(true);

        Call<ResponseBody> call;
        /************************** Code Enhancement for phase 2 : begin****************************/
        //Adding the areaId to the condition to determine if an item already scanned for the current week or not: If the item is already scanned
        //and it is within the current selected area then call the inventoryupdate method, if the item is alrady scanned but it is in a different area than the current selected area,
        // then call createinventory method. This code before phase 2 was using department only to check in an inventory exist. Now where adding the area code as well

        if (isInventoryCountExist == false)
            call = InventoryCountDetailsRetrofit.getInstance()
                    .getInventoryCountDetailsApi().createInventoryCountDetail(SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser()
                            .getToken(), inventoryCount);

        else
            call = InventoryCountDetailsRetrofit.getInstance()
                    .getInventoryCountDetailsApi().updateInventoryCountDetail(SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser()
                            .getToken(), inventoryCount);
/************************** Code Enhancement for phase 2 : end****************************/
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onClick(View v) {
/************************** Code Enhancement for phase 2 : Begin**************************/
        switch (v.getId()) {
            case (R.id.btn_sales_floor):

                if (isBtnSalesFloorPressed) {
                    isBtnSalesFloorPressed = false;
                    manipulatedAreaId -= 6;
                } else {
                    isBtnSalesFloorPressed = true;
                    manipulatedAreaId += 6;
                }

                filterBasedOnAreaSelected(isBtnSalesFloorPressed, btnSalesFloor, 1);

                break;
            case (R.id.btn_backroom):
                if (isBtnBackroomPressed) {
                    isBtnBackroomPressed = false;
                    manipulatedAreaId -= 7;
                } else {
                    isBtnBackroomPressed = true;
                    manipulatedAreaId += 7;
                }
                filterBasedOnAreaSelected(isBtnBackroomPressed, btnBackroom, 2);
                break;
            case (R.id.btn_cooler):
                if (isBtnCoolerPressed) {
                    isBtnCoolerPressed = false;
                    manipulatedAreaId -= 8;
                } else {
                    isBtnCoolerPressed = true;
                    manipulatedAreaId += 8;
                }
                filterBasedOnAreaSelected(isBtnCoolerPressed, btnCooler, 3);
                break;
            case (R.id.btn_freezer):
                if (isBtnFreezerPressed) {
                    isBtnFreezerPressed = false;
                    manipulatedAreaId -= 9;
                } else {
                    isBtnFreezerPressed = true;
                    manipulatedAreaId += 9;
                }
                filterBasedOnAreaSelected(isBtnFreezerPressed, btnFreezer, 4);
                break;
            case (R.id.btn_supplies):
                if (isBtnSuppliesPressed) {
                    isBtnSuppliesPressed = false;
                    manipulatedAreaId -= 10;
                } else {
                    isBtnSuppliesPressed = true;
                    manipulatedAreaId += 10;
                }
                filterBasedOnAreaSelected(isBtnSuppliesPressed, btnSupplies, 5);
                break;

/************************** Code Enhancement for phase 2 : End**************************/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings_inventory_details_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.manualItemSearch) {

            LayoutInflater inflater = LayoutInflater.from(InventoryCountDetailsActivity.this);
            final View manualItemSearch = inflater.inflate(R.layout.manual_item_search_activity, null);
            final EditText etVendorItem = manualItemSearch.findViewById(R.id.etVendorItem);

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
                    String weekEndDate = SharedPrefManagerInventorySummary.getInstance(InventoryCountDetailsActivity.this).getInventorySummary().getWeekEndDate();
                    try {
                        if (weekEndDate.equals(Utils.getInstance().getCurrentWeekEndDate())) {
                            actualAreaId = areaId(manipulatedAreaId);
                            if (actualAreaId >= 1 && actualAreaId <= 5)
                                retrofitCallItemDetails(etVendorItem.getText().toString());
                            else if (actualAreaId == 0) {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                                alertDialogBuilder.setTitle("Select An Area");
                                alertDialogBuilder.setMessage("Please select an area to start scanning.");
                                alertDialogBuilder.setPositiveButton("OK", null);
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            } else {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                                alertDialogBuilder.setTitle("Multiple Areas Selected");
                                alertDialogBuilder.setMessage("More than one area is selected. Please select one area only.");
                                alertDialogBuilder.setPositiveButton("OK", null);
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                            }
                        } else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                            alertDialogBuilder.setTitle("Week closed!");
                            alertDialogBuilder.setMessage("The selected week is closed. Scanning is allowed for the current week only.");
                            alertDialogBuilder.setPositiveButton("OK", null);
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    manualItemSearchDialog.dismiss();
                    return false;
                }
            });

            manualItemSearchDialog = new AlertDialog.Builder(InventoryCountDetailsActivity.this)
                    .setTitle("Item search")
                    .setView(manualItemSearch)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String weekEndDate = SharedPrefManagerInventorySummary.getInstance(InventoryCountDetailsActivity.this).getInventorySummary().getWeekEndDate();
                            try {
                                if (weekEndDate.equals(Utils.getInstance().getCurrentWeekEndDate())) {
                                    actualAreaId = areaId(manipulatedAreaId);
                                    if (actualAreaId >= 1 && actualAreaId <= 5)
                                        retrofitCallItemDetails(etVendorItem.getText().toString());
                                    else if (actualAreaId == 0) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                                        alertDialogBuilder.setTitle("Select An Area");
                                        alertDialogBuilder.setMessage("Please select an area to start scanning.");
                                        alertDialogBuilder.setPositiveButton("OK", null);
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                                        alertDialogBuilder.setTitle("Multiple Areas Selected");
                                        alertDialogBuilder.setMessage("More than one area is selected. Please select one area only.");
                                        alertDialogBuilder.setPositiveButton("OK", null);
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                } else {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                                    alertDialogBuilder.setTitle("Week closed!");
                                    alertDialogBuilder.setMessage("The selected week is closed. Scanning is allowed for the current week only.");
                                    alertDialogBuilder.setPositiveButton("OK", null);
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null).create();
            manualItemSearchDialog.show();

            return true;
        }
        //download inventory file
        if (item.getItemId() == R.id.downloadInventoryFile) {
            String storeId = SharedPrefManagerInventorySummary.getInstance(InventoryCountDetailsActivity.this).getInventorySummary().getStoreId().toString();
            String token = SharedPrefManager.getInstance(InventoryCountDetailsActivity.this).getUuser().getToken();
            token = token.replace("Bearer ", "");
            String departmentId = SharedPrefManagerInventorySummary.getInstance(InventoryCountDetailsActivity.this).getInventorySummary().getDepartmentId().toString();
            String weekEndDate = SharedPrefManagerInventorySummary.getInstance(InventoryCountDetailsActivity.this).getInventorySummary().getWeekEndDate();

            String url = "http://ec2-3-90-133-23.compute-1.amazonaws.com:8080/pwi-app-ws-dev/inventorycounts/totalInventory/"
                    + storeId + "/" + departmentId + "/" + weekEndDate + "/" + "inventory.csv?token=" + token; // missing 'http://' will cause crashed
            Uri uri = Uri.parse(url);
            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedOverRoaming(false);//Set whether this download may proceed over a roaming connection.

            //create the downloaded file name
            String downloadedFileName = "inventory_" + storeId + "_" + departmentName + "_" + weekEndDate + "_" + System.currentTimeMillis() + ".csv";

            request.setTitle(downloadedFileName);//Set the title of this download, to be displayed in notifications (if enabled).
            request.setDescription("Downloading File");//Set a description of this download, to be displayed in notifications (if enabled)
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadedFileName);//Set the local destination for the downloaded file to a path within the application's external files directory


            Long downloadReference = downloadManager.enqueue(request);//Enqueue a new download and same the referenceId
            Toast.makeText(InventoryCountDetailsActivity.this, "File downloaded!", Toast.LENGTH_LONG).show();

            // Get the downloaded file URI and attached to the email
            File downloadFolderPath = new File(Environment.getExternalStorageDirectory(), "download/");
            File DownloadedInventoryFilePath = new File(downloadFolderPath, downloadedFileName);
            Uri pngUri = FileProvider.getUriForFile(this, "com.elrancho.pwi.pwi_app.fileprovider", DownloadedInventoryFilePath);

            // Open the email intent
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "inventory_" + storeId + "_" + departmentName + "_" + weekEndDate);
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
            emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, pngUri);
            emailIntent.setType("message/rfc822");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

            return true;
        }
        if (item.getItemId() == R.id.logout) {
            SharedPrefManager.getInstance(this).clear();
            //finish();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class AsyncDataUpdate extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return params[0];
        }

        protected void onPostExecute(String result) {

            //allow scanning only if the selected week is the current week
            String weekEndDate = SharedPrefManagerInventorySummary.getInstance(InventoryCountDetailsActivity.this).getInventorySummary().getWeekEndDate();
            try {
                if (weekEndDate.equals(Utils.getInstance().getCurrentWeekEndDate())) {
                    actualAreaId = areaId(manipulatedAreaId);
                    if (actualAreaId >= 1 && actualAreaId <= 5)
                        retrofitCallItemDetails(result);
                    else if (actualAreaId == 0) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                        alertDialogBuilder.setTitle("Select An Area");
                        alertDialogBuilder.setMessage("Please select an area to start scanning.");
                        alertDialogBuilder.setPositiveButton("OK", null);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                        alertDialogBuilder.setTitle("Multiple Areas Selected");
                        alertDialogBuilder.setMessage("More than one area is selected. Please select one area only.");
                        alertDialogBuilder.setPositiveButton("OK", null);
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InventoryCountDetailsActivity.this);
                    alertDialogBuilder.setTitle("Week closed!");
                    alertDialogBuilder.setMessage("The selected week is closed. Scanning is allowed for the current week only.");
                    alertDialogBuilder.setPositiveButton("OK", null);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            } catch (ParseException e) {
                e.printStackTrace();
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

    /************************** Code Enhancement for phase 2 : Begin**************************/
    public void alertUserWhenInventoryIsGreaterThan999() {
        // Concatenate the Quantity text with zero to avoid the app crashes on "Double Invalid '' " error
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //adding a leading zero to the etQuantity field to avoid app crashes with error 'invalid double""'
                String strQuantity = "0" + (etQuantity.getText().toString());
                double quantity = Double.valueOf(strQuantity);

                //add the quantity found to the new entered quantity
                if (addMoreQty)
                    quantity += Double.valueOf(tvQuantity.getText().toString());

                //shake the etquantity editText if the value entered is greater than 999
                if (quantity * Double.valueOf(etItemCost.getText().toString()) > 1000) {
                    //change the textColor of the quantity box
                    etQuantity.setTextColor(getResources().getColor(R.color.Yellow));
                    Animation shake = AnimationUtils.loadAnimation(InventoryCountDetailsActivity.this, R.anim.shake);
                    etQuantity.startAnimation(shake);
                    Toast toast = Toast.makeText(InventoryCountDetailsActivity.this, "WARNING:\n The inventory for this item is greater than $1000.00", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    View view = toast.getView();
                    TextView toastMessage = view.findViewById(android.R.id.message);
                    toastMessage.setTextColor(getResources().getColor(R.color.Yellow));
                    toast.show();
                } else
                    etQuantity.setTextColor(getResources().getColor(R.color.White));
            }
        });
    }

    //add a filter based on the area selected
    public void filterBasedOnAreaSelected(boolean isButtonPressed, Button btn, double areaId) {
        if (isButtonPressed) {

            //change the background and the text color
            Utils.getInstance().setBtnPressed(InventoryCountDetailsActivity.this, btn, true);
            btn.setTextColor(getResources().getColor(R.color.White));
            //add the selected area filter
            Iterable<InventoryCountDetails> inventoryCountDetailsIterator = inventoryCounts;
            for (InventoryCountDetails icd : inventoryCountDetailsIterator) {
                if (icd.getAreaId() == areaId) {
                    filteredInventoryCounts.add(icd);
                    Collections.sort(filteredInventoryCounts, new Comparator<InventoryCountDetails>() {
                        @Override
                        public int compare(InventoryCountDetails o1, InventoryCountDetails o2) {
                            if (o2.getDateUpdated() != null)
                                return o2.getDateUpdated().compareTo(o1.getDateUpdated());
                            return 0;
                        }

                    });


                }
            }
            inventoryCountDetailsAdapter = new InventoyCountDetailsAdapter(InventoryCountDetailsActivity.this, filteredInventoryCounts);
            recyclerView.setAdapter(inventoryCountDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(InventoryCountDetailsActivity.this));
            //Recalculate the total by the selected areas
            calculateTotalInventoryByArea(filteredInventoryCounts);
        } else {

            Utils.getInstance().setBtnPressed(InventoryCountDetailsActivity.this, btn, false);
            btn.setTextColor(getResources().getColor(R.color.DimGray));

            //remove the inventory when the button is unpressed
            Iterable<InventoryCountDetails> inventoryCountDetailsIterator = inventoryCounts;
            for (InventoryCountDetails icd : inventoryCountDetailsIterator) {
                if (areaId == icd.getAreaId()) {
                    filteredInventoryCounts.remove(icd);
                    Collections.sort(filteredInventoryCounts, new Comparator<InventoryCountDetails>() {
                        @Override
                        public int compare(InventoryCountDetails o1, InventoryCountDetails o2) {
                            if (o2.getDateUpdated() != null)
                                return o2.getDateUpdated().compareTo(o1.getDateUpdated());
                            return 0;
                        }

                    });

                }
            }

            if (filteredInventoryCounts.size() > 0 || isAnyAreaSelected()) {
                inventoryCountDetailsAdapter = new InventoyCountDetailsAdapter(InventoryCountDetailsActivity.this, filteredInventoryCounts);
                //Recalculate the total by the selected areas
                calculateTotalInventoryByArea(filteredInventoryCounts);
            } else {
                inventoryCountDetailsAdapter = new InventoyCountDetailsAdapter(InventoryCountDetailsActivity.this, inventoryCounts);
                //Recalculate the total by the selected areas
                calculateTotalInventoryByArea(inventoryCounts);
            }
            recyclerView.setAdapter(inventoryCountDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(InventoryCountDetailsActivity.this));
        }
    }

    public void calculateTotalInventoryByArea(Iterable<InventoryCountDetails> currentSelectedAreasInventoryCount) {
        totalInventoryBySelectedAreas = 0;
        for (InventoryCountDetails inventoryCountDetails : currentSelectedAreasInventoryCount) {
            totalInventoryBySelectedAreas += inventoryCountDetails.getQuantity() * inventoryCountDetails.getCost();
        }

        //using the String.format to make the Double with 2 decimals only
        textViewInventoryTotalByArea.setText("$".concat(String.format(Locale.US, "%,.2f", totalInventoryBySelectedAreas)));
    }

    public int areaId(int areaId) {
        switch (areaId) {
            case 6:
                areaId = 1;
                break;
            case 7:
                areaId = 2;
                break;
            case 8:
                areaId = 3;
                break;
            case 9:
                areaId = 4;
                break;
            case 10:
                areaId = 5;
                break;
        }
        return areaId;
    }

    /************************** Code Enhancement for phase 2 : End**************************/

    public boolean isAnyAreaSelected() {
        if (isBtnSalesFloorPressed || isBtnBackroomPressed || isBtnCoolerPressed || isBtnFreezerPressed || isBtnSuppliesPressed)
            return true;
        return false;
    }
}
