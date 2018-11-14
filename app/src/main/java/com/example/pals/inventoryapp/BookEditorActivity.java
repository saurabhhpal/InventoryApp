package com.example.pals.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pals.inventoryapp.data.BookContract;
import com.example.pals.inventoryapp.data.BookContract.BookEntry;

import org.w3c.dom.Text;

public class BookEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Uri currentBookUri;
    EditText mBookName, mSupplierName, mPrice, mQuanity, mSupplierPhoneNumber;
    public static final int BOOK_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_editor);

        Intent intent = getIntent();
        currentBookUri = intent.getData();
        if (currentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
        } else {

            Log.i("Current Pet URI from CA", currentBookUri.toString());

            setTitle(R.string.editor_activity_title_edit_book);
            getLoaderManager().initLoader(BOOK_LOADER, null, this);

            mBookName = (EditText) findViewById(R.id.edit_book_name);
            mSupplierName = (EditText)findViewById(R.id.edit_suppliername);
            mSupplierPhoneNumber = (EditText)findViewById(R.id.edit_supplier_phone_number);
            mPrice = (EditText)findViewById(R.id.edit_price);
            mQuanity = (EditText) findViewById(R.id.edit_quantity);

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRICE,
                BookContract.BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_MNO
        };
        return new CursorLoader(this, BookEntry.CONTENT_URI, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            String bookName = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRODUCT_NAME));
            Log.i("book Name" , bookName);
            String supplier = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_NAME));
            String supplierPhone = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_MNO));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY));

            mBookName.setText(bookName);
            mSupplierName.setText(supplier);
            mSupplierPhoneNumber.setText(supplierPhone);
            mPrice.setText(String.valueOf(price));
            mQuanity.setText(String.valueOf(quantity));


        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookName.setText("");
        mSupplierName.setText("");
        mSupplierPhoneNumber.setText("");
        mPrice.setText("0");
        mQuanity.setText("0");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;

            case android.R.id.home:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveBook() {
        String bookNameString = mBookName.getText().toString().trim();
        String bookSupplierNameString = mSupplierName.getText().toString().trim();
        String bookSupplierPhString = mSupplierPhoneNumber.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String quantityString = mQuanity.getText().toString().trim();

        if (currentBookUri == null &&
                TextUtils.isEmpty(bookNameString) && TextUtils.isEmpty(bookSupplierNameString) && TextUtils.isEmpty(bookSupplierPhString)
                && TextUtils.isEmpty(priceString)) {
            return;
        }

        int quantity = 0, price = 0;

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, bookNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, bookSupplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_MNO, bookSupplierPhString);
        if (!TextUtils.isEmpty(priceString) || !TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
            price = Integer.parseInt(priceString);
        }
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);


        if (currentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error while saving pet", Toast.LENGTH_SHORT);
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Book Saved",
                        Toast.LENGTH_SHORT).show();
            }

            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            int rowsAffected = getContentResolver().update(currentBookUri, values, null, null);
            Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


}
