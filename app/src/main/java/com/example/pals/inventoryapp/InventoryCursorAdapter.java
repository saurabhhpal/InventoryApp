package com.example.pals.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.pals.inventoryapp.data.BookContract;

public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item , parent , false );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name_tv = (TextView) view.findViewById(R.id.name);
        TextView price_tv = (TextView) view.findViewById(R.id.price);
        TextView quantity_tv = (TextView) view.findViewById(R.id.quantity);
        String name = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_PRODUCT_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_PRICE));
        String quantity = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_QUANTITY));
        name_tv.setText(name);
        price_tv.setText(price);
        quantity_tv.setText(quantity);

    }
}
