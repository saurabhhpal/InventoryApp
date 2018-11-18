package com.example.pals.inventoryapp;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pals.inventoryapp.data.BookContract.BookEntry;

import com.example.pals.inventoryapp.data.BookContract;
import com.example.pals.inventoryapp.data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private BookDbHelper bookDbHelper;
    public static final int BOOK_LOADER = 0;
    InventoryCursorAdapter miInventoryCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fb = findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookEditorActivity.class);
                startActivity(intent);
            }
        });
        ListView bookListView = findViewById(R.id.list_view_books);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);


        miInventoryCursorAdapter = new InventoryCursorAdapter(this, null);
        bookListView.setAdapter(miInventoryCursorAdapter);

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this , BookEditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI , id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
        bookDbHelper = new BookDbHelper(this);
    }

//    private void displayDatabaseInfo() {
//
//        SQLiteDatabase database = bookDbHelper.getReadableDatabase();
//
//        String projection[] = {
//                BookContract.BookEntry._ID,
//                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
//                BookContract.BookEntry.COLUMN_PRICE
//        };
//
//        Cursor cursor = database.query(
//                BookContract.BookEntry.TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null
//        );
//
//        textView = findViewById(R.id.text_view);
//
//        try {
//
//            textView.setText("Books table contains " + cursor.getCount() + "rows \n");
//            textView.append(BookContract.BookEntry._ID + " - " +
//                    BookContract.BookEntry.COLUMN_PRODUCT_NAME + " - " +
//                    BookContract.BookEntry.COLUMN_PRICE
//            );
//
//            int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
//            int productColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
//            int productPriceCOlumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
//
//            while (cursor.moveToNext()) {
//                int current_Id = cursor.getInt(idColumnIndex);
//                String productName = cursor.getString(productColumnIndex);
//                String productPrice = cursor.getString(productPriceCOlumnIndex);
//
//                textView.append(("\n " + current_Id + " - " +
//                        productName + " - " +
//                        productPrice));
//            }
//        } finally {
//            cursor.close();
//        }
//    }


    private void insertBook() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, "DS000");
        contentValues.put(BookContract.BookEntry.COLUMN_PRICE, 100);
        contentValues.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, "Balaji");
        contentValues.put(BookContract.BookEntry.COLUMN_SUPPLIER_MNO, 99999999999L);

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
        Log.i("INSERTING>>>>>>>> " , newUri.toString() );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRICE,
                BookContract.BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_MNO
        };
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        miInventoryCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        miInventoryCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void deleteAllBooks() {
        Log.e("URI", BookContract.BookEntry.CONTENT_URI.toString());
        int rowsDeleted = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }
}
