package com.example.pals.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pals.inventoryapp.data.BookContract;
import com.example.pals.inventoryapp.data.BookContract.BookEntry;

public class BookEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    Uri currentBookUri;
    EditText mBookName, mSupplierName, mPrice, mQuanity, mSupplierPhoneNumber;
    TextView neg_btn, pos_btn;
    public static final int BOOK_LOADER = 0;
    int quantity = 0;
    boolean mBookhasChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_editor);
        initId();

        Intent intent = getIntent();
        currentBookUri = intent.getData();
        quantity = Integer.valueOf(mQuanity.getText().toString().trim());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (currentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
        } else {

            Log.i("Current Pet URI from CA", currentBookUri.toString());
            setTitle(R.string.editor_activity_title_edit_book);
            getLoaderManager().initLoader(BOOK_LOADER, null, this);

        }

        neg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Log.i("hkjhkjh","print---"+quantity+".."+mQuanity.getId());
                Toast.makeText(BookEditorActivity.this, "negative", Toast.LENGTH_SHORT).show();

                quantity--;
                mQuanity.setText("" + quantity);
            }
        });

        pos_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookEditorActivity.this, "positive ", Toast.LENGTH_SHORT).show();

//                quantity = Integer.valueOf(mQuanity.getText().toString().trim());
                quantity++;
                mQuanity.setText("" + quantity);
            }
        });

    }

    private void initId() {
        mBookName = findViewById(R.id.edit_book_name);
        mSupplierName = findViewById(R.id.edit_suppliername);
        mSupplierPhoneNumber = findViewById(R.id.edit_supplier_phone_number);
        mPrice = findViewById(R.id.edit_price);
        mQuanity = findViewById(R.id.edit_quantity);
        pos_btn = findViewById(R.id.pos_btn);
        neg_btn = findViewById(R.id.neg_btn);
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mBookhasChanged = true;
            return false;

        }
    };

    @Override
    public void onBackPressed() {
        if (!mBookhasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
            Log.i("book Name", bookName);
            String supplier = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_NAME));
            String supplierPhone = cursor.getString(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_SUPPLIER_MNO));
            int price = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_PRICE));
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(BookEntry.COLUMN_QUANTITY));

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

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);

                if (!mBookhasChanged) {
                    NavUtils.navigateUpFromSameTask(BookEditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(BookEditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
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
        //checking price value should be greater that 0
        if (Integer.valueOf(priceString) < 0) {

            Toast.makeText(this, "Price should be greater than 0 ", Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.valueOf(quantityString) < 0) {

            Toast.makeText(this, "Price should be greater than 0 ", Toast.LENGTH_SHORT).show();
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

    private void deleteBook() {
        // only delete if it book has entry in database
        if (currentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }


}
