package com.demo.smileid.sid_sdk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class DropDownAdapter extends ArrayAdapter {

    private DropDownInterface mListener = null;

    public DropDownAdapter(@NonNull Context context, @NonNull ArrayList<DropDownObject> languages) {
        super(context, 0, languages);
    }

    public void setListener(DropDownInterface listener) {
        mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return buildView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return buildView(position, convertView, parent);
    }

    private View buildView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_spinner_item, parent, false);
        }

        mListener.applyChoice((TextView) convertView, (DropDownObject) getItem(position), false);

        return convertView;
    }

    static class DropDownObject {

        private int mFlagResId;
        private String mName;

        public DropDownObject(int flagResId, String name) {
            mFlagResId = flagResId;
            mName = name;
        }

        public String getLabel() {
            return mName;
        }

        public int getFlagResId() {
            return mFlagResId;
        }
    }

    interface DropDownInterface {
        void applyChoice(TextView textView, DropDownObject object, boolean isMain);
    }
}