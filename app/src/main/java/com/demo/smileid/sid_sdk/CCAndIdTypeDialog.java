package com.demo.smileid.sid_sdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.smileid.sid_sdk.ItemListAdapter.ItemSelectedInterface;
import com.demo.smileid.sid_sdk.sidNet.IdTypeUtil;
import com.hbb20.CCPCountry;

public class CCAndIdTypeDialog implements ItemSelectedInterface {

    private Dialog mDialog;
    private BottomDialogHelper mCountryDialog, mIdDialog;
    private IdListAdapter mIdListAdapter = null;
    private CountryListAdapter mAdapter = new CountryListAdapter();
    private TextView mTvInputCountry, mTvLblIdType, mTvInputIdType, mTvSubmit;
    private DlgListener mListener;
    private String mSelectedCountryName = "", mSelectedIdType;

    @Override
    public void buildItem(TextView textView, Object object, boolean isCountryFlag) {
        int flagId = -1;
        String name = "";
        Drawable left = null;

        if (object instanceof CCPCountry) {
            CCPCountry country = (CCPCountry) object;
            name = country.getName();
            flagId = country.getFlagID();

            if ((isCountryFlag) && (flagId != (-1))) {
                left = mDialog.getContext().getResources().getDrawable(country.getFlagID());
            }
        } else {
            name = object.toString();
        }

        textView.setText(name);
        textView.setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
    }

    @Override
    public void applyChoice(Object object) {
        if (object instanceof CCPCountry) {
            mCountryDialog.setCancellable(true);
            mCountryDialog.dismissDialog();
            mIdDialog.setCancellable(false);

            mSelectedCountryName = ((CCPCountry) object).getName();
            mIdListAdapter.setIdList(IdTypeUtil.idCards(mSelectedCountryName).getIdCards());
            mIdListAdapter.notifyDataSetChanged();
            mTvInputCountry.setText(mSelectedCountryName);
            mTvLblIdType.setVisibility(View.VISIBLE);
            mTvInputIdType.setVisibility(View.VISIBLE);
        } else {
            mIdDialog.setCancellable(true);
            mIdDialog.dismissDialog();

            mSelectedIdType = object.toString();
            mTvInputIdType.setText(mSelectedIdType);

            mDialog.findViewById(R.id.tvSubmit).setVisibility(View.VISIBLE);
        }
    }

    public interface DlgListener {
        void submit(String countryCode, String idType);
        void cancel();
    }

    private void buildIdTypeDialog(Context context, View root) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(root);
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);

        mDialog.findViewById(R.id.tvSubmit).setOnClickListener(v -> {
            mDialog.cancel();
            if (mListener == null) return;
            mListener.cancel();
        });

        mCountryDialog = new BottomDialogHelper(mDialog.getContext(), R.layout.layout_country_list);
        setCountryDialog();

        mIdDialog = new BottomDialogHelper(mDialog.getContext(), R.layout.layout_id_list);
        setIdDialog();
    }

    public void openCountryPicker(View view) {
        mCountryDialog.showDialog();
    }

    public void openIdTypePicker(View view) {
        mIdDialog.showDialog();
    }

    private void setupViews() {
        mTvInputCountry = mDialog.findViewById(R.id.tvInputCountry);
        mTvInputCountry.setOnClickListener(v -> openCountryPicker(null));

        mTvLblIdType = mDialog.findViewById(R.id.tvLblIdType);
        mTvInputIdType = mDialog.findViewById(R.id.tvInputIdType);
        mTvInputIdType.setOnClickListener(v -> openIdTypePicker(null));

        mTvSubmit = mDialog.findViewById(R.id.tvSubmit);

        mTvSubmit.setOnClickListener(v -> {
            mDialog.cancel();
            if (mListener == null) return;
            mListener.submit(mSelectedCountryName, mSelectedIdType);
        });
    }

    private void setCountryDialog() {
        mCountryDialog.setCancellable(false);
        RecyclerView rv = mCountryDialog.getContentView().findViewById(R.id.rvCountries);
        rv.setLayoutManager(new LinearLayoutManager(mDialog.getContext()));
        mAdapter.setListener(this);
        rv.setAdapter(mAdapter);
        filterCountryList("");

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterCountryList(s.toString());
            }
        };

        EditText edtSearch = mCountryDialog.getContentView().findViewById(R.id.edtCountry);
        edtSearch.addTextChangedListener(textWatcher);
        mCountryDialog.setDismissListener(dialog -> edtSearch.setText(""));
    }

    private void filterCountryList(String constraint) {
        mAdapter.filterList(constraint);
    }

    private void setIdDialog() {
        mIdDialog.setCancellable(false);
        RecyclerView rv = mIdDialog.getContentView().findViewById(R.id.rvIds);
        rv.setLayoutManager(new LinearLayoutManager(mDialog.getContext()));
        mIdListAdapter = new IdListAdapter();
        mIdListAdapter.setListener(this);
        rv.setAdapter(mIdListAdapter);
    }

    private void applyChoice(TextView tvLang, DropDownAdapter.DropDownObject dropDownObject, boolean isCountryFlag) {
        tvLang.setText(dropDownObject.getLabel());
        Drawable left = null;
        Drawable right = null;

        if (isCountryFlag) {
            left = mDialog.getContext().getResources().getDrawable(dropDownObject.getFlagResId());
            right = mDialog.getContext().getResources().getDrawable(R.drawable.ic_down_arrow);
        } else {
            tvLang.setTextSize(14);
            tvLang.setPadding(tvLang.getPaddingLeft(), 4, tvLang.getTotalPaddingRight(), 4);
        }

        tvLang.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
    }

    public CCAndIdTypeDialog(Context context, DlgListener listener) {
        View root = LayoutInflater.from(context).inflate(R.layout.layout_cc_id_type_dlg, null);
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        buildIdTypeDialog(context, root);

        mListener = listener;

        setupViews();
    }

    public void showDialog() {
        mDialog.show();
    }
}
