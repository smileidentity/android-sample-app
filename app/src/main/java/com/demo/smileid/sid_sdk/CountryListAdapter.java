package com.demo.smileid.sid_sdk;

import androidx.annotation.NonNull;
import com.google.common.collect.Collections2;
import com.hbb20.CCPCountry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CountryListAdapter extends ItemListAdapter {

  private Comparator mComparator = (o1, o2) -> ((CCPCountry) o1).getName().compareToIgnoreCase(((CCPCountry) o2)
      .getName());

  private ArrayList<CCPCountry> mDefaultCountries = new ArrayList(CCPCountry.getLibraryMasterCountriesEnglish());
  private ArrayList<CCPCountry> mCountries = new ArrayList();

  private String mSupportedCountries = "";

  public CountryListAdapter(String supportedCountries) {
    mSupportedCountries = supportedCountries;
    filterList("");
  }

  public void filterList(String constraint) {
    mCountries = new ArrayList<>(Collections2.filter(mDefaultCountries, country -> {
      boolean valid = mSupportedCountries.isEmpty() || mSupportedCountries.contains(country.getNameCode().toUpperCase());

      if (!constraint.isEmpty()) {
        valid &= country.getName().toLowerCase().contains(constraint.toLowerCase());
      }

      return valid;
    }));

    Collections.sort(mCountries, mComparator);
    notifyDataSetChanged();
  }

  @Override
  public void onBindViewHolder(@NonNull ItemViewHolder viewHolder, int i) {
    viewHolder.populate(mCountries.get(i));
    viewHolder.itemView.setOnClickListener(v -> mListener.applyChoice(mCountries.get(i)));
  }

  @Override
  public int getItemCount() {
    return mCountries.size();
  }
}