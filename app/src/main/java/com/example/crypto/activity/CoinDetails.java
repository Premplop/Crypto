package com.example.crypto.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.crypto.API;
import com.example.crypto.ApiClient;
import com.example.crypto.Constant;
import com.example.crypto.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoinDetails extends AppCompatActivity {

    TextView rank_up,rank_down,coin_symbol,coin_name,coin_price,coin_price_change,chart_heading,coin_mark_cap,coin_dmark_up,circ_supply,coin_tot_supply,coin_max_supply,coin_ath_price,coin_ath_price_change,coin_ath_date,
            coin_atl_date,coin_atl_price,coin_atl_price_change,res1,res2,res3,res4,res5,res6,res7,res8,res9,sup1,sup2,sup3,sup4,sup5,sup6,sup7,sup8,sup9;
    TextView gann_res1,gann_res2,gann_res3,gann_res4,gann_res5,gann_res6,gann_res7,gann_res8,gann_res9,gann_res10,gann_res11,gann_sup1,gann_sup2,gann_sup3,gann_sup4,gann_sup5,gann_sup6,gann_sup7,gann_sup8,gann_sup9,gann_sup10,gann_sup11;
    ImageView coin_img;
    DecimalFormat decimalFormat = new DecimalFormat("###.##");
    Locale us = new Locale("en");
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", us);
    CandleStickChart candleStickChart;
    ArrayList<CandleEntry> candleEntries;
    ArrayList<Double> high_prices = new ArrayList<>();
    ArrayList<Double> low_prices = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;
    ShimmerFrameLayout shimmer_average_move,shimmer_fibo_cardview,shimmer_layout_cardview_gann;
    TableLayout fibo_table_layout,gann_table_layout;
    TextView coin_name_analysis;
    TextView ava_move;
    RadioButton fibo_ratio;
    RadioButton gann_ratio;
    String max_supply="";
    Spinner trading_spinner;
    String name = "";
    String id = "";
    double today_open_price = 0, today_close_price = 0;
    double avrage_move = 0;
    double velocity_median = 0;
    int trading_type;
    ProgressBar progressBar;
    CardView view_analysis;
    LinearLayout ll_fibo,ll_gann_angle;
    NumberFormat numberFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_details);

        rank_up = findViewById(R.id.rank_top_show);
        rank_down = findViewById(R.id.rank_down_show);
        coin_symbol = findViewById(R.id.coin_symbol);
        coin_name = findViewById(R.id.coin_name);
        coin_price = findViewById(R.id.coin_price);
        coin_price_change = findViewById(R.id.coin_price_change);
        chart_heading = findViewById(R.id.convert_chart_name);
        coin_mark_cap = findViewById(R.id.market_cap);
        coin_dmark_up = findViewById(R.id.diluted_market_cap);
        circ_supply = findViewById(R.id.circ_supply);
        coin_tot_supply = findViewById(R.id.tot_supply);
        coin_max_supply = findViewById(R.id.max_supply);
        coin_ath_price = findViewById(R.id.ath_price);
        coin_ath_price_change = findViewById(R.id.ath_change_percentage);
        coin_ath_date = findViewById(R.id.ath_date);
        coin_atl_price = findViewById(R.id.atl_price);
        coin_atl_price_change = findViewById(R.id.atl_change_percentage);
        coin_atl_date = findViewById(R.id.atl_date);
        coin_img = findViewById(R.id.coin_img);
        candleStickChart = findViewById(R.id.chart_view);
        progressBar = findViewById(R.id.load_chart);
        view_analysis = findViewById(R.id.view_analysis);


        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        String img_url =intent.getStringExtra("img_url");
        String price_change = intent.getStringExtra("price_change");
        String symbol = intent.getStringExtra("symbol");
        id = intent.getStringExtra("id");
        String rank = intent.getStringExtra("rank");
        String market_cap = intent.getStringExtra("market_cap");
        String fully_diluted_cap = intent.getStringExtra("fully_diluted_cap");
        String cir_supply = intent.getStringExtra("cir_supply");
        String tot_supply = intent.getStringExtra("tot_supply");

        max_supply = intent.getStringExtra("max_supply");
        String ath_price = intent.getStringExtra("ath_price");
        String ath_percentage = intent.getStringExtra("ath_percentage");
        String ath_date =intent.getStringExtra("ath_date");
        String atl_price = intent.getStringExtra("atl_price");
        String atl_percentage = intent.getStringExtra("atl_percentage");
        String atl_date= intent.getStringExtra("atl_date");

        if (Constant.currency_symbol.equals("inr")){
            numberFormat = NumberFormat.getCurrencyInstance(new Locale("en","IN"));
        }else{
            numberFormat = NumberFormat.getCurrencyInstance(new Locale("en","US"));
        }
        numberFormat.setMaximumFractionDigits(20);
        rank_up.setText("Rank: #"+rank);
        rank_down.setText("#"+rank);
        Glide.with(this).load(img_url).into(coin_img);
        coin_symbol.setText(symbol.toUpperCase(Locale.ROOT));
        coin_name.setText(name);
        coin_price.setText(numberFormat.format(new BigDecimal(price).doubleValue()));
        double changes = Double.parseDouble(price_change);
        if (changes <0){
            coin_price_change.setTextColor(this.getResources().getColor(R.color.red));
            coin_price_change.setText(decimalFormat.format(changes)+"%");
            coin_price_change.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_down_arrow,0,0,0);
        }
        else{
            coin_price_change.setTextColor(this.getResources().getColor(R.color.green));
            coin_price_change.setText(decimalFormat.format(changes)+"%");
            coin_price_change.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_up_arrow,0,0,0);
        }
        chart_heading.setText(name + " to INR Chart");
//        coin_mark_cap.setText(numberFormat.format(new BigDecimal(market_cap).doubleValue()));
//        coin_dmark_up.setText(numberFormat.format(new BigDecimal(fully_diluted_cap).doubleValue()));
//        circ_supply.setText(numberFormat.format(new BigDecimal(cir_supply).doubleValue()));
//        coin_tot_supply.setText(numberFormat.format(new BigDecimal(tot_supply).doubleValue()));
        setTextView(coin_mark_cap,market_cap);
        setTextView(coin_dmark_up,fully_diluted_cap);
        setTextView(coin_max_supply,fully_diluted_cap);
        setTextView(circ_supply,cir_supply);
        setTextView(coin_tot_supply,tot_supply);
        coin_ath_price.setText(numberFormat.format(new BigDecimal(ath_price).doubleValue()));
        double ath_changes = Double.parseDouble(ath_percentage);
        if (ath_changes <0){
            coin_ath_price_change.setTextColor(this.getResources().getColor(R.color.red));
            coin_ath_price_change.setText(decimalFormat.format(ath_changes)+"%");
            //coin_ath_price_change.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_down_arrow,0,0,0);
        }
        else {
            coin_ath_price_change.setTextColor(this.getResources().getColor(R.color.green));
            coin_ath_price_change.setText(decimalFormat.format(ath_changes) + "%");
           // coin_ath_price_change.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_up_arrow,0,0,0);
        }

        coin_atl_price.setText(numberFormat.format(new BigDecimal(atl_price).doubleValue()));
        double atl_changes = Double.parseDouble(atl_percentage);
        if (atl_changes <0){
            coin_atl_price_change.setTextColor(this.getResources().getColor(R.color.red));
            coin_atl_price_change.setText(decimalFormat.format(atl_changes)+"%");
           // coin_atl_price_change.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_down_arrow,0,0,0);
        }
        else {
            coin_atl_price_change.setTextColor(this.getResources().getColor(R.color.green));
            coin_atl_price_change.setText(decimalFormat.format(atl_changes) + "%");
            //coin_atl_price_change.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_up_arrow,0,0,0);
        }

        try {
            Date date = format.parse(atl_date);
            Date date1 = format.parse(ath_date);
            String stringDate_low = new SimpleDateFormat("yyyy-MM-dd", us).format(date);
            String stringDate_high = new SimpleDateFormat("yyyy-MM-dd", us).format(date1);
            coin_atl_date.setText(stringDate_low);
            coin_ath_date.setText(stringDate_high);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        getChartData(id);
        //setLineChart();
        candleStickChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                View view = getLayoutInflater().inflate(R.layout.marker_view_graph,null);
                double data = Double.parseDouble(decimalFormat.format(e.getY()));
                //Toast.makeText(CoinDetails.this, ""+x, Toast.LENGTH_SHORT).show();
                MarkerView markerView = new MarkerView(CoinDetails.this,R.layout.marker_view_graph);
                TextView price = markerView.findViewById(R.id.show_value);
                price.setText("Price : "+numberFormat.format(data));
                markerView.setOffset(-view.getWidth()/2f,-view.getHeight());
                candleStickChart.setMarker(markerView);
                candleStickChart.highlightValue(h);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        view_analysis.setOnClickListener(view -> {
            showBottomSheet();
        });
    }

    private void showBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogStyle);
        View view = View.inflate(this, R.layout.analysis_bottom_layout, null);
        bottomSheetDialog.setContentView(view);
        coin_name_analysis = view.findViewById(R.id.coin_name_analysis);
        ava_move = view.findViewById(R.id.ava_movement);
        fibo_ratio = view.findViewById(R.id.fib_analysis);
        gann_ratio = view.findViewById(R.id.gann_analysis);
        res1 = view.findViewById(R.id.res1);
        res2 = view.findViewById(R.id.res2);
        res3 = view.findViewById(R.id.res3);
        res4 = view.findViewById(R.id.res4);
        res5 = view.findViewById(R.id.res5);
        res6 = view.findViewById(R.id.res6);
        res7 = view.findViewById(R.id.res7);
        res8 = view.findViewById(R.id.res8);
        res9 = view.findViewById(R.id.res9);
        sup1 = view.findViewById(R.id.sup1);
        sup2 = view.findViewById(R.id.sup2);
        sup3 = view.findViewById(R.id.sup3);
        sup4 = view.findViewById(R.id.sup4);
        sup5 = view.findViewById(R.id.sup5);
        sup6 = view.findViewById(R.id.sup6);
        sup7 = view.findViewById(R.id.sup7);
        sup8 = view.findViewById(R.id.sup8);
        sup9 = view.findViewById(R.id.sup9);
        //gann
        gann_res1 = view.findViewById(R.id.gann_res1);
        gann_res2 = view.findViewById(R.id.gann_res2);
        gann_res3 = view.findViewById(R.id.gann_res3);
        gann_res4 = view.findViewById(R.id.gann_res4);
        gann_res5 = view.findViewById(R.id.gann_res5);
        gann_res6 = view.findViewById(R.id.gann_res6);
        gann_res7 = view.findViewById(R.id.gann_res7);
        gann_res8 = view.findViewById(R.id.gann_res8);
        gann_res9 = view.findViewById(R.id.gann_res9);
        gann_res10 = view.findViewById(R.id.gann_res10);
        gann_res11 = view.findViewById(R.id.gann_res11);
        gann_sup1 = view.findViewById(R.id.gann_sup1);
        gann_sup2 = view.findViewById(R.id.gann_sup2);
        gann_sup3 = view.findViewById(R.id.gann_sup3);
        gann_sup4 = view.findViewById(R.id.gann_sup4);
        gann_sup5 = view.findViewById(R.id.gann_sup5);
        gann_sup6 = view.findViewById(R.id.gann_sup6);
        gann_sup7 = view.findViewById(R.id.gann_sup7);
        gann_sup8 = view.findViewById(R.id.gann_sup8);
        gann_sup9 = view.findViewById(R.id.gann_sup9);
        gann_sup10 = view.findViewById(R.id.gann_sup10);
        gann_sup11 = view.findViewById(R.id.gann_sup11);
        shimmer_average_move = view.findViewById(R.id.shimmer_layout_ava_price);
        shimmer_fibo_cardview = view.findViewById(R.id.shimmer_layout_fibo_cardview);
        shimmer_layout_cardview_gann = view.findViewById(R.id.shimmer_layout_cardview_gann);
        fibo_table_layout = view.findViewById(R.id.fibo_table);
        gann_table_layout = view.findViewById(R.id.gann_table);
        ll_fibo = view.findViewById(R.id.ll_fibo);
        ll_gann_angle = view.findViewById(R.id.ll_Gann);
        trading_spinner = view.findViewById(R.id.trading_type);
        ArrayList<String> trading_list = new ArrayList<>();
        trading_list.add("Intra-day");
        trading_list.add("Weekly");
        trading_list.add("Monthly");
        ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,trading_list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trading_spinner.setAdapter(arrayAdapter);
        trading_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                trading_type = i;
                getCommonMethod(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        gann_ratio.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b){
                setGannData(trading_type);
                //getLowHighDataGann();
            }
        });
        fibo_ratio.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                //getLowHighDataFibo();
                setFiboData(trading_type);
            }
        });
        coin_name_analysis.setText(name+" Analysis");
        //getLowHighDataFibo();
        getCommonMethod(0);
        bottomSheetDialog.show();
    }

    private void getCommonMethod(int type) {
        ArrayList<Double> velocity_slope_list = new ArrayList<>();
        //to find velocity slope
        for (int i = 0;i < high_prices.size();i++){
            //TODO: add if else like weekly , monthly
            if (type == 0){
                double velocity_slope = (high_prices.get(i)- low_prices.get(i))/high_prices.get(i);
                velocity_slope_list.add(velocity_slope);
            }else if (type == 1){
                double velocity_slope = ((high_prices.get(i)- low_prices.get(i))/high_prices.get(i))*Math.sqrt(7);
                velocity_slope_list.add(velocity_slope);
            }else {
                double velocity_slope = ((high_prices.get(i)- low_prices.get(i))/high_prices.get(i))*Math.sqrt(30);
                velocity_slope_list.add(velocity_slope);
            }
            //weekly - ((high_prices.get(i)- low_prices.get(i))/high_prices.get(i))*Math.sqrt(7)
            //monthly - ((high_prices.get(i)- low_prices.get(i))/high_prices.get(i))*Math.sqrt(30)
        }

        if (velocity_slope_list.size()%2 == 1) {
            velocity_median = (velocity_slope_list.get(velocity_slope_list.size()/2) + velocity_slope_list.get(velocity_slope_list.size()/2 - 1))/2;
        } else {
            velocity_median = velocity_slope_list.get(velocity_slope_list.size()/2);
        }
        //this is for intraday if for week and month take close price of today
        if (type == 0) {
            avrage_move = today_open_price * velocity_median;
        }else{
            avrage_move = today_close_price * velocity_median;
        }
        if (gann_ratio.isChecked()){
            setGannData(type);
        }else {
            setFiboData(type);
        }
    }

    private void setFiboData(int type) {
        ll_fibo.setVisibility(View.VISIBLE);
        ll_gann_angle.setVisibility(View.GONE);
        //shimmer_average_move.setVisibility(View.VISIBLE);
        shimmer_fibo_cardview.setVisibility(View.VISIBLE);
        //shimmer_average_move.startShimmer();
        shimmer_fibo_cardview.startShimmer();
        //ava_move.setVisibility(View.GONE);
        fibo_table_layout.setVisibility(View.GONE);
        ArrayList<Double> fib_ratio_list = new ArrayList<>();
        ArrayList<String> res_level_list = new ArrayList<>();
        ArrayList<String> sup_level_list = new ArrayList<>();
        fib_ratio_list.add(0.236);
        fib_ratio_list.add(0.382);
        fib_ratio_list.add(0.5);
        fib_ratio_list.add(0.618);
        fib_ratio_list.add(0.786);
        fib_ratio_list.add(0.888);
        fib_ratio_list.add(1.0);
        fib_ratio_list.add(1.236);
        fib_ratio_list.add(1.618);
        for (double values : fib_ratio_list){
            double fib_level = values * avrage_move;
            //this is for intraday if for week and month take close price of today
            double res_level=0;
            double sup_level=0;
            if (type == 0) {
                res_level = today_open_price + fib_level;
                sup_level = today_open_price - fib_level;
            }else{
                res_level = today_close_price + fib_level;
                sup_level = today_close_price - fib_level;
            }
            res_level_list.add(new DecimalFormat("##.####").format(res_level));
            sup_level_list.add(new DecimalFormat("##.####").format(sup_level));
        }
       // shimmer_average_move.stopShimmer();
        shimmer_fibo_cardview.stopShimmer();
        //shimmer_average_move.setVisibility(View.GONE);
        shimmer_fibo_cardview.setVisibility(View.GONE);
        fibo_table_layout.setVisibility(View.VISIBLE);
        res1.setText(res_level_list.get(0));
        res2.setText(res_level_list.get(1));
        res3.setText(res_level_list.get(2));
        res4.setText(res_level_list.get(3));
        res5.setText(res_level_list.get(4));
        res5.setTextColor(Color.RED);
        res6.setTextColor(Color.RED);
        res6.setText(res_level_list.get(5));
        res7.setText(res_level_list.get(6));
        res8.setText(res_level_list.get(7));
        res9.setText(res_level_list.get(8));

        sup1.setText(sup_level_list.get(0));
        sup2.setText(sup_level_list.get(1));
        sup3.setText(sup_level_list.get(2));
        sup4.setText(sup_level_list.get(3));
        sup5.setText(sup_level_list.get(4));
        sup5.setTextColor(Color.RED);
        sup6.setTextColor(Color.RED);
        sup6.setText(sup_level_list.get(5));
        sup7.setText(sup_level_list.get(6));
        sup8.setText(sup_level_list.get(7));
        sup9.setText(sup_level_list.get(8));

        ava_move.setText("Price Movement : " + numberFormat.format(new BigDecimal(new DecimalFormat("##.##").format(avrage_move))));
    }

    private void setGannData(int type) {
        ll_fibo.setVisibility(View.GONE);
        ll_gann_angle.setVisibility(View.VISIBLE);

        //shimmer_average_move.setVisibility(View.VISIBLE);
        shimmer_layout_cardview_gann.setVisibility(View.VISIBLE);
        //shimmer_average_move.startShimmer();
        shimmer_layout_cardview_gann.startShimmer();
        //ava_move.setVisibility(View.GONE);
        gann_table_layout.setVisibility(View.GONE);
        ArrayList<Double> gann_ratio_list = new ArrayList<>();
        ArrayList<String> res_level_list = new ArrayList<>();
        ArrayList<String> sup_level_list = new ArrayList<>();
        gann_ratio_list.add(0.48);
        gann_ratio_list.add(0.46);
        gann_ratio_list.add(0.42);
        gann_ratio_list.add(0.4);
        gann_ratio_list.add(0.35);
        gann_ratio_list.add(0.25);
        gann_ratio_list.add(1.15);
        gann_ratio_list.add(0.1);
        gann_ratio_list.add(0.08);
        gann_ratio_list.add(0.04);
        gann_ratio_list.add(0.02);
        for (double values : gann_ratio_list){
            double gann_level = values * avrage_move;
            //this is for intraday if for week and month take close price of today
            double res_level=0;
            double sup_level=0;
            if (type == 0) {
                res_level = today_open_price + gann_level;
                sup_level = today_open_price - gann_level;
            }else{
                res_level = today_close_price + gann_level;
                sup_level = today_close_price - gann_level;
            }
            res_level_list.add(new DecimalFormat("##.####").format(res_level));
            sup_level_list.add(new DecimalFormat("##.####").format(sup_level));
        }
        // shimmer_average_move.stopShimmer();
        shimmer_layout_cardview_gann.stopShimmer();
        //shimmer_average_move.setVisibility(View.GONE);
        shimmer_layout_cardview_gann.setVisibility(View.GONE);
        gann_table_layout.setVisibility(View.VISIBLE);
        gann_res1.setText(res_level_list.get(0));
        gann_res2.setText(res_level_list.get(1));
        gann_res3.setText(res_level_list.get(2));
        gann_res4.setText(res_level_list.get(3));
        gann_res5.setText(res_level_list.get(4));
        gann_res6.setTextColor(Color.RED);
        gann_res6.setText(res_level_list.get(5));
        gann_res7.setText(res_level_list.get(6));
        gann_res8.setText(res_level_list.get(7));
        gann_res9.setText(res_level_list.get(8));
        gann_res9.setTextColor(getResources().getColor(R.color.gold));
        gann_res10.setText(res_level_list.get(9));
        gann_res11.setText(res_level_list.get(10));

        gann_sup1.setText(sup_level_list.get(0));
        gann_sup2.setText(sup_level_list.get(1));
        gann_sup3.setText(sup_level_list.get(2));
        gann_sup4.setText(sup_level_list.get(3));
        gann_sup5.setText(sup_level_list.get(4));
        gann_sup6.setTextColor(Color.RED);
        gann_sup6.setText(sup_level_list.get(5));
        gann_sup7.setText(sup_level_list.get(6));
        gann_sup8.setText(sup_level_list.get(7));
        gann_sup9.setText(sup_level_list.get(8));
        gann_sup9.setTextColor(getResources().getColor(R.color.gold));
        gann_sup10.setText(sup_level_list.get(9));
        gann_sup11.setText(sup_level_list.get(10));

    }

    private void getLowHighDataGann() {

    }

    private void getLowHighDataFibo() {
        Map<String,Object> query_map = new HashMap<>();
        query_map.put("vs_currency","inr");
        query_map.put("days","30");
        API api  = ApiClient.getclient().create(API.class);
        ll_fibo.setVisibility(View.VISIBLE);
        ll_gann_angle.setVisibility(View.GONE);
        shimmer_average_move.setVisibility(View.VISIBLE);
        shimmer_fibo_cardview.setVisibility(View.VISIBLE);
        shimmer_average_move.startShimmer();
        shimmer_fibo_cardview.startShimmer();
        ava_move.setVisibility(View.GONE);
        fibo_table_layout.setVisibility(View.GONE);

        Call<ResponseBody> call = api.getCandelStickData(id,query_map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                if (responseBody != null){
                    try {
                        String res = responseBody.string();
                        JSONArray jsonArray = new JSONArray(res);
                        low_prices.clear();
                        high_prices.clear();
                        for (int i = 0; i < jsonArray.length();i++){
                            JSONArray data_array = jsonArray.getJSONArray(i);
                            if (i != jsonArray.length()){
                                double high =  data_array.getDouble(2);
                                double low = data_array.getDouble(3);
                                low_prices.add(low);
                                high_prices.add(high);
                            }
                        }
                        double average_movement = Double.parseDouble(new DecimalFormat("##.##").format(Collections.max(high_prices) - Collections.min(low_prices)));
                        double high_price = Collections.max(high_prices);
                        double low_price = Collections.min(low_prices);
                        ArrayList<Double> fib_ratio_list = new ArrayList<>();
                        ArrayList<String> res_level_list = new ArrayList<>();
                        ArrayList<String> sup_level_list = new ArrayList<>();
                        fib_ratio_list.add(0.236);
                        fib_ratio_list.add(0.382);
                        fib_ratio_list.add(0.5);
                        fib_ratio_list.add(0.618);
                        fib_ratio_list.add(0.786);
                        fib_ratio_list.add(0.888);
                        fib_ratio_list.add(1.0);
                        fib_ratio_list.add(1.236);
                        fib_ratio_list.add(1.618);
                        for (double values : fib_ratio_list){
                            double fib_level = values * average_movement;
                            double res_level = low_price + fib_level;
                            double sup_level = high_price - fib_level;
                            res_level_list.add(new DecimalFormat("##.####").format(res_level));
                            sup_level_list.add(new DecimalFormat("##.####").format(sup_level));
                        }
                        shimmer_average_move.stopShimmer();
                        shimmer_fibo_cardview.stopShimmer();
                        shimmer_average_move.setVisibility(View.GONE);
                        shimmer_fibo_cardview.setVisibility(View.GONE);
                        ava_move.setVisibility(View.VISIBLE);
                        fibo_table_layout.setVisibility(View.VISIBLE);
                        res1.setText(res_level_list.get(0));
                        res2.setText(res_level_list.get(1));
                        res3.setText(res_level_list.get(2));
                        res4.setText(res_level_list.get(3));
                        res5.setText(res_level_list.get(4));
                        res5.setTextColor(Color.RED);
                        res6.setTextColor(Color.RED);
                        res6.setText(res_level_list.get(5));
                        res7.setText(res_level_list.get(6));
                        res8.setText(res_level_list.get(7));
                        res9.setText(res_level_list.get(8));

                        sup1.setText(sup_level_list.get(0));
                        sup2.setText(sup_level_list.get(1));
                        sup3.setText(sup_level_list.get(2));
                        sup4.setText(sup_level_list.get(3));
                        sup5.setText(sup_level_list.get(4));
                        sup5.setTextColor(Color.RED);
                        sup6.setTextColor(Color.RED);
                        sup6.setText(sup_level_list.get(5));
                        sup7.setText(sup_level_list.get(6));
                        sup8.setText(sup_level_list.get(7));
                        sup9.setText(sup_level_list.get(8));

                        ava_move.setText("Monthly Price Movement : " + numberFormat.format(new BigDecimal(new DecimalFormat("##.##").format(average_movement))));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(CoinDetails.this, "Sever Side Problem", Toast.LENGTH_SHORT).show();
            }
        });
    }
//TODO: use this data for the analysis also dont take new data from server and restrict if there no four years available.
    private void getChartData(String id) {
        Map<String,Object> query_map = new HashMap<>();
        query_map.put("vs_currency", Constant.currency_symbol);
        query_map.put("days","max");
        progressBar.setVisibility(View.VISIBLE);
        candleStickChart.setVisibility(View.GONE);
        API api  = ApiClient.getclient().create(API.class);
        Call<ResponseBody> call = api.getCandelStickData(id,query_map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ResponseBody responseBody = response.body();
                if (responseBody != null){
                        try {
                            String res = responseBody.string();
                            JSONArray jsonArray = new JSONArray(res);
                            Calendar calendar = Calendar.getInstance();
                            int before_year = calendar.get(Calendar.YEAR)-4;
                            Date last_date = new Date(jsonArray.getJSONArray(0).getLong(0));
                            SimpleDateFormat simple_format = new SimpleDateFormat("yyyy",Locale.getDefault());
                            int last_year = Integer.parseInt(simple_format.format(last_date));

                            if (before_year >= last_year){
                                view_analysis.setVisibility(View.VISIBLE);
                            }else{
                                view_analysis.setVisibility(View.GONE);
                            }
                            high_prices.clear();
                            low_prices.clear();
                            candleEntries = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length();i++){
                                JSONArray data_array = jsonArray.getJSONArray(i);
                                Date date = new Date(jsonArray.getJSONArray(i).getLong(0));
                                dates.add(""+date);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy",Locale.getDefault());
                                int coin_year = Integer.parseInt(format.format(date));
                                if (coin_year >= before_year) {
                                    high_prices.add(data_array.getDouble(2));
                                    low_prices.add(data_array.getDouble(3));
                                }
                                    //TODO: timestamp from api get and convert to Date Format and Validate it has 4 four year data avail else restrict analysis button
//                                long timestamp = Long.parseLong("1508371200000"); //Example -> in ms
//                                Date d = new Date(timestamp );
//                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
//                                String con_date = format.format(d);
                                if (i == jsonArray.length()-1){
                                    today_open_price = data_array.getDouble(1);
                                    today_close_price = data_array.getDouble(4);
                                }
                                    float open = (float) data_array.getDouble(1);
                                    float high = (float) data_array.getDouble(2);
                                    float low = (float) data_array.getDouble(3);
                                    float close = (float) data_array.getDouble(4);
                                    candleEntries.add(new CandleEntry(i, high, low, open, close));
                            }
                            setLineChart();
                            progressBar.setVisibility(View.GONE);
                            candleStickChart.setVisibility(View.VISIBLE);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            candleStickChart.setVisibility(View.GONE);
                        }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(CoinDetails.this, "Sever Side Problem", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setTextView(TextView view, String value) {
        if (value.equals("null")){
            view.setText("-");
        }else {
            view.setText(numberFormat.format(new BigDecimal(value).doubleValue()));
        }
    }

    private void setLineChart() {

        candleStickChart.setHighlightPerDragEnabled(true);

        candleStickChart.setDrawBorders(false);

        candleStickChart.getDescription().setEnabled(false);
        candleStickChart.setBorderColor(getResources().getColor(R.color.light_grey));

        YAxis yAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = candleStickChart.getXAxis();

        xAxis.setDrawGridLines(false);// disable x axis grid lines
        xAxis.setDrawLabels(false);
        rightAxis.setTextColor(Color.BLACK);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = candleStickChart.getLegend();
        l.setEnabled(false);

        CandleDataSet set1 = new CandleDataSet(candleEntries, "DataSet 1");
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(getResources().getColor(R.color.light_grey));
        set1.setShadowWidth(0.8f);
        set1.setDecreasingColor(getResources().getColor(R.color.red));
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(getResources().getColor(R.color.green));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(false);

        CandleData data = new CandleData(set1);

        candleStickChart.setData(data);
        candleStickChart.invalidate();
    }
}