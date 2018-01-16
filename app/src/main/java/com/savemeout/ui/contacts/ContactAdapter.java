package com.savemeout.ui.contacts;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.savemeout.R;
import com.savemeout.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by binod on 9/1/18.
 */

public class ContactAdapter extends RecyclerView.Adapter {

    ArrayList<HashMap<String, String>> alData;
    ArrayList<HashMap<String, String>> alResult = new ArrayList<HashMap<String, String>>();
    ContactAdapterActionListener listener;
    boolean isLongPressON = false;
    ArrayList<HashMap<String, String>> alSelected = new ArrayList<HashMap<String, String>>();
    int count = 0;
    Context context;
    int totalCount = 3;

    public ContactAdapter(ContactListActivity context, ArrayList<HashMap<String, String>> alContacts) {
        alData = alContacts;
        alResult.addAll(alData);
        listener = context;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder mHolder = (MyViewHolder) holder;
        final HashMap<String, String> hashData = alData.get(position);
        mHolder.tvContactName.setText(replaceNull(hashData.get(Constants.CONTACT_NAME)));
        mHolder.tvPhoneNo.setText(replaceNull(hashData.get(Constants.PHONE_NUMBER)));
        mHolder.cMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isLongPressON) {
                    isLongPressON = true;
                    notifyDataSetChanged();
                    listener.onLongPress();
                }
                return false;
            }
        });


        mHolder.cMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mHolder.checkBox.performClick();


            }
        });
        mHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLongPressON) {
                    Log.e("pressed", "presses" + mHolder.checkBox.isChecked() + count);
                    if (mHolder.checkBox.isChecked()) {
                        if (count < totalCount) {
                            hashData.put(Constants.CHECK_NO_SELECT, true + "");
                            mHolder.checkBox.setChecked(true);
                            count++;
                        } else {
                            Toast.makeText(context, "can not send more than 3 contacts!", Toast.LENGTH_SHORT).show();
                            mHolder.checkBox.setChecked(false);
                            mHolder.checkBox.setEnabled(false);
                        }
                    } else {

                        if (count > 0) {
                            hashData.put(Constants.CHECK_NO_SELECT, false + "");
                            mHolder.checkBox.setChecked(false);
                            count--;
                        }

                    }
                    alData.set(position, hashData);
                }

            }
        });

        if (isLongPressON) {
            mHolder.checkBox.setVisibility(View.VISIBLE);
            Log.e("checkbox_vwlueee", hashData.get(Constants.CHECK_NO_SELECT) + "");
            if (hashData.get(Constants.CHECK_NO_SELECT).equals("true")) {
                mHolder.checkBox.setChecked(true);
            } else {
                mHolder.checkBox.setChecked(false);
            }
        } else {
            mHolder.checkBox.setVisibility(View.GONE);
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;

    }

    public String replaceNull(String text) {
        return text == null ? "" : text;
    }

    @Override
    public int getItemCount() {
        return alData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvContactName)
        TextView tvContactName;
        @BindView(R.id.tvPhoneNo)
        TextView tvPhoneNo;
        @BindView(R.id.checkBox)
        CheckBox checkBox;
        @BindView(R.id.cMain)
        ConstraintLayout cMain;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    void updateData(ArrayList<HashMap<String, String>> alData) {
        this.alData = alData;
        alResult.addAll(alData);
        notifyDataSetChanged();
    }


    public void filter(String keyword) {
        keyword = keyword.toLowerCase(Locale.getDefault());
        alData.clear();
        if (keyword.length() == 0) {
            alData.addAll(alResult);
        } else {
            for (HashMap<String, String> res : alResult) {
                if (res.get(Constants.PHONE_NUMBER).toLowerCase(Locale.getDefault()).contains(keyword.toLowerCase()) || res.get(Constants.CONTACT_NAME).toLowerCase(Locale.getDefault()).contains(keyword.toLowerCase())) {
                    alData.add(res);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void cancelSelection() {
        isLongPressON = false;
        for (int i = 0; i < alData.size(); i++) {
            HashMap<String, String> hash = alData.get(i);
            hash.put(Constants.CHECK_NO_SELECT, false + "");
            alData.set(i, hash);
        }
        count = 0;
        notifyDataSetChanged();
    }

    interface ContactAdapterActionListener {
        void onLongPress();
    }

}
