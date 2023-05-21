package com.example.crypto.SqliteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.crypto.Model.CoinModel;

import java.util.ArrayList;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DBName = "crypto_app";
    private static final int DBVersion = 1;
    private static final String Coin_Table_Name = "Coin_Details";
    private static final String Coin_Name = "Coin_Name";
    private static final String Current_Price = "Current_Price";
    private static final String image = "Coin_Img";
    private static final String price_change_percentage_24h = "Price_Change";
    private static final String symbol = "Coin_Symbol";
    private static final String id = "CoinId";
    private static final String market_cap_rank = "Coin_Rank";
    private static final String market_cap = "Market_Cap";
    private static final String fully_diluted_valuation = "Coin_Diluation_Value";
    private static final String circulating_supply = "Coin_Circulating_Supply";
    private static final String total_supply = "Coin_Total_Supply";
    private static final String max_supply = "Coin_Max_Supply";
    private static final String ath = "Coin_Ath";
    private static final String ath_change_percentage = "Coin_Ath_Change_Percantage";
    private static final String ath_date = "Coin_Ath_Date";
    private static final String atl = "Coin_Atl";
    private static final String atl_change_percentage = "Coin_Atl_Change_Percantage";
    private static final String atl_date = "Coin_Atl_Date";
    private static final String ID_COL = "id";
    private static final String Currency_Table_Name = "Currency_List";
    private static final String Currency_Type = "Currency_Type";
    public SqliteHelper(@Nullable Context context) {
        super(context,DBName,null,DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String coin_query = "CREATE TABLE " + Coin_Table_Name + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Coin_Name + " TEXT,"
                + Current_Price + " TEXT,"
                + price_change_percentage_24h + " TEXT,"
                + image + " TEXT,"
                + id + " TEXT,"
                + symbol + " TEXT,"
                + market_cap_rank + " TEXT,"
                + market_cap + " TEXT,"
                + fully_diluted_valuation + " TEXT,"
                + circulating_supply + " TEXT,"
                + max_supply + " TEXT,"
                + total_supply + " TEXT,"
                + ath + " TEXT,"
                + ath_change_percentage + " TEXT,"
                + ath_date + " TEXT,"
                + atl + " TEXT,"
                + atl_change_percentage + " TEXT,"
                + atl_date + " TEXT)";

        String currency_query = "CREATE TABLE "+ Currency_Table_Name + " ("
                + Currency_Type + " TEXT) ";

        db.execSQL(coin_query);
        db.execSQL(currency_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Coin_Table_Name);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Currency_Table_Name);
        onCreate(sqLiteDatabase);
    }

    public void addCurrencyType (String Currency){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete all rows from the table
        db.delete(Currency_Table_Name, null, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Currency_Type,Currency);
        db.insert(Currency_Table_Name,null,contentValues);
        //db.close();
    }

    public void addCoinsData (ArrayList<CoinModel> coinModels){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        db.delete(Coin_Table_Name,null,null);
        try {
            ContentValues values = new ContentValues();
            for (CoinModel coinModel : coinModels) {
                values.put(Coin_Name, coinModel.getCoin_name());
                values.put(Current_Price, coinModel.getCoin_price());
                values.put(image, coinModel.getCoin_img_url());
                values.put(price_change_percentage_24h, coinModel.getCoin_price_change());
                values.put(symbol, coinModel.getCoin_symbol());
                values.put(id, coinModel.getCoin_id());
                values.put(market_cap_rank, coinModel.getCoin_rank());
                values.put(market_cap, coinModel.getCoin_market_cap());
                values.put(fully_diluted_valuation, coinModel.getCoin_fully_diluted_market_cap());
                values.put(circulating_supply, coinModel.getCoin_circulating_supply());
                values.put(total_supply, coinModel.getCoin_total_supply());
                values.put(max_supply, coinModel.getCoin_max_supply());
                values.put(ath, coinModel.getCoin_ath_price());
                values.put(ath_change_percentage, coinModel.getCoin_ath_percentage());
                values.put(ath_date, coinModel.getCoin_ath_date());
                values.put(atl, coinModel.getCoin_atl_price());
                values.put(atl_change_percentage, coinModel.getCoin_atl_percentage());
                values.put(atl_date, coinModel.getCoin_atl_date());
                db.insert(Coin_Table_Name, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public String getCurrencydata(){
        // Get a readable database
        SQLiteDatabase db = this.getReadableDatabase();

       // Define the columns to retrieve
        String[] projection = {
                Currency_Type
        };
        Cursor cursor = db.query(
                Currency_Table_Name,
                projection,
                null,
                null,
                null,
                null,
                null
        );

// Create a StringBuilder to build the result string
       // StringBuilder result = new StringBuilder();
        String currency_value = "";
// Iterate over the results
        while (cursor.moveToNext()) {
            currency_value = cursor.getString(cursor.getColumnIndexOrThrow(Currency_Type));
        }
        cursor.close();

// Return the result string
        return currency_value;

    }
}
