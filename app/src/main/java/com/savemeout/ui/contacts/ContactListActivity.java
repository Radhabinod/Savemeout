package com.savemeout.ui.contacts;

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
import com.savemeout.ui.base.BaseActivity;
import com.savemeout.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactListActivity extends BaseActivity implements ContactAdapter.ContactAdapterActionListener, ContactListView {

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
    MenuItem item2, itemSend;
    @Inject
    ContactListPresenter<ContactListView> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contac_list);
        ButterKnife.bind(this);
        getActivityComponent().inject(this);
        presenter.attachView(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvContacts.getContext(),
                manager.getOrientation());
        rvContacts.addItemDecoration(dividerItemDecoration);
        rvContacts.setLayoutManager(manager);

        if (hasPermission(permissions)) {
            presenter.loadContact();
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
        if (item.getItemId() == R.id.unSelectAll) {
            adapter.cancelSelection();
            item2.setVisible(false);
            itemSend.setVisible(false);
        } else if (item.getItemId() == R.id.menuSend) {


        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 12) {
            if (hasPermission(permissions)) {
                presenter.loadContact();
            }
        }
    }

    @OnClick(R.id.ivAddContact)
    public void addContact() {
       /* AlertActivity dialogActivity=new AlertActivity(this);
        dialogActivity.show();*/
        showDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
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
                if (presenter.addContact(name, phone, email)) {
                    dialog.dismiss();
                    Toast.makeText(ContactListActivity.this, "Contact added successfully!", Toast.LENGTH_SHORT).show();
                    alContacts.clear();
                    presenter.loadContact();
                }


            }
        });
        ArrayAdapter adapter = new ArrayAdapter(ContactListActivity.this, R.layout.spinner_text, arReletions);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRelation.setAdapter(adapter);
        dialog.show();

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
        if (item2 != null && item2.isVisible()) {
            adapter.cancelSelection();
            item2.setVisible(false);
            itemSend.setVisible(false);
            return;
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void setContact(ArrayList<HashMap<String, String>> contacts) {
        adapter = new ContactAdapter(this, contacts);
        rvContacts.setAdapter(adapter);
    }


}
