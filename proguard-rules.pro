#-ignorewarnings
#-dontwarn module-info
-keepattributes **
-keepattributes *Annotation*
-keep class !android.support.v7.internal.view.menu.**, ** {
    !private <fields>;
    protected <fields>;
    public <fields>;
    <methods>;
}
