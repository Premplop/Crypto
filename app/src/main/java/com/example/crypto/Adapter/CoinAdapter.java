package com.example.crypto.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.crypto.Constant;
import com.example.crypto.Interface.OnLoadMoreListener;
import com.example.crypto.Model.CoinModel;
import com.example.crypto.R;
import com.example.crypto.activity.CoinDetails;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.ViewHolder>{
    Context context;
    ArrayList<CoinModel> coins_list;
    DecimalFormat decimalFormat = new DecimalFormat("#,##,##,###.##");
    double changes;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener onLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }
    public CoinAdapter(Context context, ArrayList<CoinModel> coins_list, RecyclerView recyclerView) {
        this.context = context;
        this.coins_list = coins_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_coins,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NumberFormat numberFormat;
        if (Constant.currency_symbol.equals("inr")) {
            numberFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        }else{
            numberFormat = NumberFormat.getCurrencyInstance(new Locale("en","US"));
        }
        numberFormat.setMaximumFractionDigits(20);
        holder.coin_name.setText(coins_list.get(position).getCoin_name());
        holder.coin_price.setText(numberFormat.format(new BigDecimal(coins_list.get(position).getCoin_price()).doubleValue()));
        if (!coins_list.get(position).getCoin_price_change().equals("null"))
            changes = Double.parseDouble(coins_list.get(position).getCoin_price_change());
        else
            changes = 0;
        if (changes <0){
            holder.coin_price_change.setTextColor(context.getResources().getColor(R.color.red));
            holder.coin_price_change.setText(decimalFormat.format(changes)+"%");
            holder.coin_price_change.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_down_arrow,0,0,0);
        } else if (changes == 0) {
            holder.coin_price_change.setText(decimalFormat.format(changes)+"%");
        } else {
            holder.coin_price_change.setTextColor(context.getResources().getColor(R.color.green));
            holder.coin_price_change.setText(decimalFormat.format(changes) + "%");
            holder.coin_price_change.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_up_arrow, 0, 0, 0);
        }
        holder.coin_symbol.setText(coins_list.get(position).getCoin_symbol().toUpperCase(Locale.ROOT));
        Glide.with(context).load(coins_list.get(position).getCoin_img_url()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.coin_img);
        holder.card_coin.setOnClickListener(view -> {
            Intent intent = new Intent(context, CoinDetails.class);
            intent.putExtra("name",coins_list.get(position).getCoin_name());
            intent.putExtra("price",coins_list.get(position).getCoin_price());
            intent.putExtra("img_url",coins_list.get(position).getCoin_img_url());
            intent.putExtra("price_change",coins_list.get(position).getCoin_price_change());
            intent.putExtra("symbol",coins_list.get(position).getCoin_symbol());
            intent.putExtra("id",coins_list.get(position).getCoin_id());
            intent.putExtra("rank",coins_list.get(position).getCoin_rank());
            intent.putExtra("market_cap",coins_list.get(position).getCoin_market_cap());
            intent.putExtra("fully_diluted_cap",coins_list.get(position).getCoin_fully_diluted_market_cap());
            intent.putExtra("cir_supply",coins_list.get(position).getCoin_circulating_supply());
            intent.putExtra("tot_supply",coins_list.get(position).getCoin_total_supply());
            intent.putExtra("max_supply",coins_list.get(position).getCoin_max_supply());
            intent.putExtra("ath_price",coins_list.get(position).getCoin_ath_price());
            intent.putExtra("ath_percentage",coins_list.get(position).getCoin_ath_percentage());
            intent.putExtra("ath_date",coins_list.get(position).getCoin_ath_date());
            intent.putExtra("atl_price",coins_list.get(position).getCoin_atl_price());
            intent.putExtra("atl_percentage",coins_list.get(position).getCoin_atl_percentage());
            intent.putExtra("atl_date",coins_list.get(position).getCoin_atl_date());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return coins_list.size();
    }

    public void updateList(ArrayList<CoinModel> list){
        coins_list = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView coin_img;
        TextView coin_name,coin_price,coin_price_change,coin_symbol;
        CardView card_coin;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            coin_img = itemView.findViewById(R.id.coin_img);
            coin_name = itemView.findViewById(R.id.coin_name);
            coin_price = itemView.findViewById(R.id.coin_price);
            coin_price_change = itemView.findViewById(R.id.coin_price_change);
            coin_symbol = itemView.findViewById(R.id.coin_symbol);
            card_coin = itemView.findViewById(R.id.card_coin);
        }
    }

    private static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        }
    }
}
