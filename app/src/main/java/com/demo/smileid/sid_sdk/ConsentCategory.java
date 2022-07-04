package com.demo.smileid.sid_sdk;

public class ConsentCategory {

    public int mIcon;
    public String mLabel;
    public String mTooltipLbl;
    public String mTooltipContent;

    public ConsentCategory(int icon, String label, String tooltipLbl, String tooltipContent) {
        mIcon = icon;
        mLabel = label;
        mTooltipLbl = tooltipLbl;
        mTooltipContent = tooltipContent;
    }

    public String toString() {
        return mTooltipLbl;
    }

    public static class Builder {

        private int mIcon = R.drawable.ic_info_btn;
        private String mLabel;
        private String mTooltipLbl;
        private String mTooltipContent;

        public ConsentCategory.Builder setIcon(int icon) {
            mIcon = icon;
            return this;
        }

        public ConsentCategory.Builder setLabel(String label) {
            mLabel = label;
            return this;
        }

        public ConsentCategory.Builder setTooltipLabel(String tooltipLbl) {
            mTooltipLbl = tooltipLbl;
            return this;
        }

        public ConsentCategory.Builder setTooltipContent(String tooltipContent) {
            mTooltipContent = tooltipContent;
            return this;
        }

        public ConsentCategory build() {
            return new ConsentCategory(mIcon, mLabel, mTooltipLbl, mTooltipContent);
        }
    }
}