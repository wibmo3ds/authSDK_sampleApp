package wibmo.com.main;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.wibmo.authcallback.FetchTransactionHistory;
import com.wibmo.authcallback.TransactionHistoryCallBack;
import com.wibmo.core.TransactionHistoryModel;
import com.wibmo.device.TransactionHistoryImpl;


import java.util.ArrayList;

import wibmo.com.main.adapter.AdapterTransactionHistory;
import wibmo.com.wibmoAuthApp.R;
/**
 * Created by abhisheksingh on 11/19/17.
 */


public class TransactionHistory extends AppCompatActivity implements TransactionHistoryCallBack {
    //ProgressBar progressBar;
    public static String TAG = TransactionHistory.class.getSimpleName();
    RecyclerView recyclerViewMain;
    RelativeLayout mainLayout, emptyLayout;

    private ProgressDialog progressDialog;


    FetchTransactionHistory transactionHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        transactionHistory = new TransactionHistoryImpl(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Transaction History");
        toolbar.setTitleTextColor(getResources().getColor(R.color.defaultwhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_24px);
        upArrow.setColorFilter(getResources().getColor(R.color.defaultwhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        //progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.GONE);
        recyclerViewMain = findViewById(R.id.recyclerViewMain);
        emptyLayout = findViewById(R.id.emptyLayout);
        mainLayout = findViewById(R.id.mainLayout);

        recyclerViewMain.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);

        initializeUI();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_wibmo_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    protected void initializeUI() {

        LinearLayoutManager lineaLayoutManager = new LinearLayoutManager(this);
        recyclerViewMain.setLayoutManager(lineaLayoutManager);
        recyclerViewMain.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMain.setHasFixedSize(true);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading transaction history\n\n Please wait ...");
        progressDialog.setCancelable(false);

        progressDialog.show();

        transactionHistory.loadTransactions();


    }


    @Override
    public void onSuccess(ArrayList<TransactionHistoryModel> transactionList) {

        // show list and hide progressBar

        progressDialog.dismiss();

        if (!transactionList.isEmpty()) {
            emptyLayout.setVisibility(View.GONE);
            recyclerViewMain.setVisibility(View.VISIBLE);
            AdapterTransactionHistory adapterTransactionHistory = new AdapterTransactionHistory(transactionList);
            recyclerViewMain.setAdapter(adapterTransactionHistory);
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerViewMain.setVisibility(View.GONE);
        }

    }

    @Override
    public void onFailed(String reason) {

        progressDialog.dismiss();
        // hide progressbar and do valid action on fail.

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        transactionHistory.onDetach();
        transactionHistory = null;
    }
}
