package com.example.crypto;

import static com.example.crypto.Constant.Rupee_Symbol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.res.Configuration;
import android.icu.number.NumberFormatter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crypto.Adapter.CoinAdapter;
import com.example.crypto.Model.CoinModel;
import com.example.crypto.SqliteHelper.SqliteHelper;
import com.facebook.shimmer.ShimmerFrameLayout;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    CoinAdapter coinAdapter;
    ArrayList<CoinModel> coins_data = new ArrayList<>();
    ArrayList<String> currency_list = new ArrayList<>();
    ImageView search_coins;
    TextView app_name,view_analysis;
    EditText coin_search_text;
    boolean doubleBackToExitPressedOnce = false;
    SwipeRefreshLayout swipeRefreshLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView rv_coins_view;
    int page = 1;
    Spinner currency_spinner;
    String currency_selected = "";
    ProgressBar progressBar;
    SqliteHelper db;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv_coins_view = findViewById(R.id.rv_coins);
        coinAdapter = new CoinAdapter(this, coins_data,rv_coins_view);
        search_coins = findViewById(R.id.search_coins);
        app_name = findViewById(R.id.name);
        coin_search_text = findViewById(R.id.coin_search_text);
        swipeRefreshLayout = findViewById(R.id.refresh_down);
        shimmerFrameLayout = findViewById(R.id.shimmer_layout);
        currency_spinner = findViewById(R.id.currency_spinner);

        currency_list.add(Rupee_Symbol+" INR");
        currency_list.add("$ USD");
        ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,currency_list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currency_spinner.setAdapter(arrayAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_coins_view.setLayoutManager(linearLayoutManager);
        rv_coins_view.setAdapter(coinAdapter);
        rv_coins_view.setHasFixedSize(true);
        db = new SqliteHelper(this);

        getcurrencystored();
        getData(page);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            coins_data.clear();
            getData(page);
            isLoading = false;
            swipeRefreshLayout.setRefreshing(false);
        });
        search_coins.setOnClickListener(view -> {
            if (coin_search_text.getVisibility() == View.VISIBLE){
                View view1 = this.getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                coin_search_text.setText("");
                app_name.setVisibility(View.VISIBLE);
                coin_search_text.setVisibility(View.GONE);
            }
            else {
                coin_search_text.setVisibility(View.VISIBLE);
                app_name.setVisibility(View.GONE);
                coin_search_text.requestFocus();
                View view1 = this.getCurrentFocus();
                if (view1 != null) {
                    coin_search_text.setFocusableInTouchMode(true);
                    InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(view1, InputMethodManager.RESULT_SHOWN);
                }

            }
        });
        currency_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String [] split_currency = currency_list.get(i).split(" ");
                currency_selected =split_currency[1].toLowerCase(Locale.ROOT);
                db.addCurrencyType(currency_selected);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do something
            }
        });
        coin_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        filter(editable.toString());
                    }
                },200);

            }
        });

        boolean isDarkThemeOn = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)  == Configuration.UI_MODE_NIGHT_YES;
        if (isDarkThemeOn){
            search_coins.setImageResource(R.drawable.ic_search_coins_light);
        }
        else{
            search_coins.setImageResource(R.drawable.ic_search_coins);
        }


//        rv_coins_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//                if (!isLoading) {
//                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == coins_data.size() - 1) {
//                        //bottom of list!
//                        getData(page+1);
//                        if (page == 5) {
//                            isLoading = true;
//                        }
//                    }
//                }
//            }
//        });
        // Add an OnScrollListener to the RecyclerView
//        rv_coins_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                // Check if the user is scrolling up or down
//                if (dy > 0) {
//                    // Get the number of visible items, total items, and past visible items
//                    int visibleItemCount = linearLayoutManager.getChildCount();
//                    int totalItemCount = linearLayoutManager.getItemCount();
//                    int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();
//
//                    // Check if the last item is reached and if there is more data to load
//                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount && !isLoading && hasMoreData) {
//                        // Set a flag to indicate that data is loading
//                        isLoading = true;
//
//                        // Request more data from the server asynchronously
//                        loadMoreData(new Callback() {
//                            @Override
//                            public void onSuccess(List<Item> newData) {
//                                // Update the adapter with the new data
//                                adapter.addData(newData);
//
//                                // Set the flag to false and update the hasMoreData variable
//                                isLoading = false;
//                                hasMoreData = newData.size() > 0;
//                            }
//
//                            @Override
//                            public void onFailure(Exception e) {
//                                // Handle the error and set the flag to false
//                                isLoading = false;
//                                e.printStackTrace();
//                            }
//                        });
//                    }
//                }
//            }
//        });

    }

    private void getcurrencystored() {
        currency_selected = db.getCurrencydata();
        if (currency_selected.equals("inr")) {
            currency_spinner.setSelection(0);
        }else{
            currency_spinner.setSelection(1);
        }
    }


    @Override
    public void onBackPressed() {
        if(coin_search_text.getVisibility() == View.VISIBLE){
            coin_search_text.setVisibility(View.GONE);
            app_name.setVisibility(View.VISIBLE);
        }
        else{
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            doubleBackToExitPressedOnce = true;
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false,2000);
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        }
        coin_search_text.setText("");
    }
    void filter(String text){
        ArrayList<CoinModel> temp = new ArrayList<>();
        for(CoinModel d: coins_data){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getCoin_id().contains(text.toLowerCase(Locale.ROOT))||(d.getCoin_symbol().contains(text.toLowerCase(Locale.ROOT)))){
                temp.add(d);
            }
        }
        //update recyclerview
        coinAdapter.updateList(temp);

    }

    private void getData(int page){
        Map<String,Object> params = new HashMap<>();
        params.put("vs_currency","inr");
        params.put("page",page);
        params.put("per_page",100);
        params.put("order","market_cap_desc");
        API apiClient =ApiClient.getclient().create(API.class);
        Call<ResponseBody> data = apiClient.getCoinMarket(params);
        if (page == 1){
            shimmerFrameLayout.startShimmer();
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            rv_coins_view.setVisibility(View.GONE);
        }

        data.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                try{
                    if (responseBody != null){
                        String res = responseBody.string();
                        JSONArray coins_array = new JSONArray(res);
                        if (coins_array.length() >0){
                            for (int i = 0;coins_array.length() >i;i++){
                                CoinModel coinModel = new CoinModel();
                                coinModel.setCoin_name(coins_array.getJSONObject(i).getString("name"));
                                coinModel.setCoin_price(coins_array.getJSONObject(i).getString("current_price"));
                                coinModel.setCoin_img_url(coins_array.getJSONObject(i).getString("image"));
                                coinModel.setCoin_price_change(coins_array.getJSONObject(i).getString("price_change_percentage_24h"));
                                coinModel.setCoin_symbol(coins_array.getJSONObject(i).getString("symbol"));
                                coinModel.setCoin_id(coins_array.getJSONObject(i).getString("id"));
                                coinModel.setCoin_rank(coins_array.getJSONObject(i).getString("market_cap_rank"));
                                coinModel.setCoin_market_cap(coins_array.getJSONObject(i).getString("market_cap"));
                                coinModel.setCoin_fully_diluted_market_cap(coins_array.getJSONObject(i).getString("fully_diluted_valuation"));
                                coinModel.setCoin_circulating_supply(coins_array.getJSONObject(i).getString("circulating_supply"));
                                coinModel.setCoin_total_supply(coins_array.getJSONObject(i).getString("total_supply"));
                                coinModel.setCoin_max_supply(coins_array.getJSONObject(i).getString("max_supply"));
                                coinModel.setCoin_ath_price(coins_array.getJSONObject(i).getString("ath"));
                                coinModel.setCoin_ath_percentage(coins_array.getJSONObject(i).getString("ath_change_percentage"));
                                coinModel.setCoin_ath_date(coins_array.getJSONObject(i).getString("ath_date"));
                                coinModel.setCoin_atl_price(coins_array.getJSONObject(i).getString("atl"));
                                coinModel.setCoin_atl_percentage(coins_array.getJSONObject(i).getString("atl_change_percentage"));
                                coinModel.setCoin_atl_date(coins_array.getJSONObject(i).getString("atl_date"));
                                coins_data.add(coinModel);
                            }
                            coinAdapter.notifyDataSetChanged();
                            db.addCoinsData(coins_data);
                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                rv_coins_view.setVisibility(View.VISIBLE);
                        }
                    }
                }
                catch (IOException | JSONException e){
                    Toast.makeText(MainActivity.this, "No Data Found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {

            }
        });
    }
}