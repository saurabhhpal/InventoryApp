package com.example.pals.inventoryapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pals.inventoryapp.data.BookContract;

public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView name_tv = (TextView) view.findViewById(R.id.name);
        TextView price_tv = (TextView) view.findViewById(R.id.price);
        final TextView quantity_tv = (TextView) view.findViewById(R.id.quantity);
        Button sale_bt = (Button) view.findViewById(R.id.sale_bt);

        final int index = cursor.getColumnIndex(BookContract.BookEntry._ID);
        final int currentid = cursor.getInt(index);


        final String name = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_PRODUCT_NAME));
        String price = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_PRICE));
        final String quantity = cursor.getString(cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_QUANTITY));
        name_tv.setText(name);
        price_tv.setText(price);
        quantity_tv.setText(quantity);



        sale_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.valueOf(quantity);
                Uri uri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, currentid);
                if (qty > 0) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(BookContract.BookEntry.COLUMN_QUANTITY , --qty);
                    int rowsAffected = context.getContentResolver().update(uri, contentValues, null, null);

                    if (rowsAffected == 0) {
                        //updated fail
                        Toast.makeText(context, "ITEM UPDATION FAILED", Toast.LENGTH_SHORT).show();
                    } else
                        //updated successful
                        Toast.makeText(context, "UPDATION SUCCESSFUL", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(context, "not greater that 0", Toast.LENGTH_SHORT).show();
                }
            }

    });


}
}
