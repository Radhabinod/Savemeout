package com.savemeout.contacts;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.savemeout.R;
import com.savemeout.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactList extends AppCompatActivity implements ContactAdapter.ContactAdapterActionListener {

    private String TAG = "ContactActivity";
    String[] permissions = {Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS};
    ArrayList<HashMap<String, String>> alContacts = new ArrayList<HashMap<String, String>>();
    @BindView(R.id.rvContacts)
    RecyclerView rvContacts;
    @BindView(R.id.etSearch)
    EditText etSearch;
    ContactAdapter adapter;
    @BindView(R.id.ivAddContact)
    ImageView ivAddContact;
    String[] arReletions = {"Parent", "Brother", "Sister", "Friend", "Other"};
    Menu menu;
    MenuItem item2,itemSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contac_list);
        ButterKnife.bind(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvContacts.getContext(),
                manager.getOrientation());
        rvContacts.addItemDecoration(dividerItemDecoration);
        rvContacts.setLayoutManager(manager);

        if (hasPermission(permissions)) {
            getContactList();
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions, 12);
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filter(etSearch.getText().toString().toLowerCase(Locale.getDefault()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menus, menu);
        this.menu = menu;
        MenuItem item = menu.findItem(R.id.selectAll);
        item2 = menu.findItem(R.id.unSelectAll);
        itemSend = menu.findItem(R.id.menuSend);
        item.setVisible(false);
        item2.setVisible(false);
        itemSend.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.unSelectAll)
        {
            adapter.cancelSelection();
            item2.setVisible(false);
            itemSend.setVisible(false);
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 12) {
            if (hasPermission(permissions)) {
                getContactList();
            }
        }
    }

    @OnClick(R.id.ivAddContact)
    public void addContact() {
       /* AlertActivity dialogActivity=new AlertActivity(this);
        dialogActivity.show();*/
        showDialog();
    }

    private void getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                HashMap<String, String> hashData = new HashMap<String, String>();
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));


                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        hashData.put(Constants.PHONE_NUMBER, phoneNo);
                        hashData.put(Constants.CONTACT_NAME, name);
                        hashData.put(Constants.CHECK_NO_SELECT,"false");
                        alContacts.add(hashData);
                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        adapter = new ContactAdapter(this, alContacts);
        rvContacts.setAdapter(adapter);

    }

    public boolean hasPermission(String[] permisions) {
        for (String permission : permisions) {
            if ((ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        //dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_contact);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setTitle("Add Contact");
        final EditText etEmail = (EditText) dialog.findViewById(R.id.etEmail);
        final EditText etPhone = (EditText) dialog.findViewById(R.id.etPhone);
        final EditText etName = (EditText) dialog.findViewById(R.id.etName);
        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        Spinner spRelation = (Spinner) dialog.findViewById(R.id.spReletions);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String name = etName.getText().toString();
                if (addContact(name, phone, email)) {
                    dialog.dismiss();
                    Toast.makeText(ContactList.this, "Contact added successfully!", Toast.LENGTH_SHORT).show();
                    alContacts.clear();
                    getContactList();
                }


            }
        });
        ArrayAdapter adapter = new ArrayAdapter(ContactList.this, R.layout.spinner_text, arReletions);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRelation.setAdapter(adapter);
        dialog.show();

    }

    private boolean addContact(String DisplayName, String MobileNumber, String emailID) {
        boolean isAdded = false;

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        /*//------------------------------------------------------ Home Numbers
        if (HomeNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Work Numbers
        if (WorkNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }
*/
        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }


        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            isAdded = true;
        } catch (Exception e) {
            e.printStackTrace();
            isAdded = false;
            Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return isAdded;
    }

    @Override
    public void onLongPress() {
        if (item2 != null) {
            item2.setVisible(true);
            itemSend.setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        if(item2!=null&&item2.isVisible())
        {
            adapter.cancelSelection();
            item2.setVisible(false);
            itemSend.setVisible(false);
            return;
        }else {
            super.onBackPressed();
        }
    }
}
