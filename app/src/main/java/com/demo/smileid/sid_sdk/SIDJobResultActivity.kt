package com.demo.smileid.sid_sdk

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.demo.smileid.sid_sdk.geoloc.SIDGeoInfos
import com.demo.smileid.sid_sdk.sidNet.InternetStateBroadCastReceiver
import com.demo.smileid.sid_sdk.sidNet.InternetStateBroadCastReceiver.OnConnectionReceivedListener
import com.demo.smileid.sid_sdk.sidNet.SIDNetworkingUtils
import com.smileidentity.libsmileid.core.SIDConfig
import com.smileidentity.libsmileid.core.SIDNetworkRequest
import com.smileidentity.libsmileid.core.SIDNetworkRequest.*
import com.smileidentity.libsmileid.core.SIDResponse
import com.smileidentity.libsmileid.core.SIDTagManager
import com.smileidentity.libsmileid.exception.SIDException
import com.smileidentity.libsmileid.model.SIDMetadata
import com.smileidentity.libsmileid.model.SIDNetData
import com.smileidentity.libsmileid.model.SIDUserIdInfo
import com.smileidentity.libsmileid.net.model.idValidation.IDValidationResponse
import com.smileidentity.libsmileid.utils.AppData

class SIDJobResultActivity : BaseSIDActivity(),
    OnCompleteListener, OnErrorListener,
    OnUpdateListener, OnEnrolledListener, OnAuthenticatedListener,
    OnDocVerificationListener, OnIDValidationListener,
    OnConnectionReceivedListener {
    private lateinit var mTvResult: TextView
    private lateinit var mTvConfidenceValue: TextView
    private lateinit var mPbLoading: ProgressBar
    private var mSelectedCountryName: String? = ""
    private var mSelectedIdCard: String? = ""
    private var mCurrentTag: String? = null
    private lateinit var mSIDNetworkRequest: SIDNetworkRequest
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mInternetStateBR: InternetStateBroadCastReceiver
    private var mEnrolledUser = false
    private var mConfig: SIDConfig? = null
    private var mSIDUserIdInfo: HashMap<String, String>? = null
    private var smartAuthType: HashMap<String, String> = HashMap()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sid_activity_result)
        initVars()
        initViews()
        buildNetObserver()
        buildNetRequest()
        uploadNow(null)
    }

    private fun initVars() {
        mEnrolledUser = intent.getBooleanExtra(USER_SELFIE_PARAM, false)
        mCurrentTag =
            intent.getStringExtra(SIDStringExtras.EXTRA_TAG_FOR_ADD_ID_INFO)
        mKYCProductType =
            intent.getSerializableExtra(KYC_PRODUCT_TYPE_PARAM) as KYC_PRODUCT_TYPE?
        mSIDUserIdInfo =
            intent.getSerializableExtra(USER_ID_INFO_PARAM) as HashMap<String, String>?
        mSIDUserIdInfo?.let {
            mSelectedCountryName = it[SIDUserIdInfo.COUNTRY]
            mSelectedIdCard = it[SIDUserIdInfo.ID_TYPE]
        }
        if (mKYCProductType == KYC_PRODUCT_TYPE.DOCUMENT_VERIFICATION) {
            mSelectedCountryName = intent.getStringExtra(DOC_COUNTRY_PARAM)
            mSelectedIdCard = intent.getStringExtra(DOC_ID_TYPE_PARAM)
        } else if (mKYCProductType == KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH) {
            smartAuthType =
                intent.getSerializableExtra(SMART_AUTH_PARAM) as HashMap<String, String>
        }
        mSharedPreferences =
            getSharedPreferences(packageName, Context.MODE_PRIVATE)
    }

    private fun initViews() {
        mTvResult = findViewById(R.id.tvResult)
        mPbLoading = findViewById(R.id.pbLoading)
        mTvConfidenceValue = findViewById(R.id.tvConfidenceValue)
        val text = when (mKYCProductType) {
            KYC_PRODUCT_TYPE.BASIC_KYC, KYC_PRODUCT_TYPE.ENHANCED_KYC -> "ID Info Uploading..."
            KYC_PRODUCT_TYPE.DOCUMENT_VERIFICATION -> "Document Uploading..."
            else -> "Selfie Uploading..."
        }
        mTvConfidenceValue.setText(text)
    }

    private fun buildNetObserver() {
        mInternetStateBR = InternetStateBroadCastReceiver()
        mInternetStateBR.setOnConnectionReceivedListener(this)
    }

    private fun buildNetRequest() {
        mSIDNetworkRequest = SIDNetworkRequest(this)
        mSIDNetworkRequest.setOnCompleteListener(this)
        mSIDNetworkRequest.set0nErrorListener(this)
        mSIDNetworkRequest.setOnUpdateListener(this)
        mSIDNetworkRequest.setOnEnrolledListener(this)
        mSIDNetworkRequest.setOnAuthenticatedListener(this)
        mSIDNetworkRequest.setOnDocVerificationListener(this)
        mSIDNetworkRequest.setOnIDValidationListener(this)
    }

    fun uploadNow(view: View?) {
        upload(mCurrentTag)
    }

    private fun setUserIdInfo(metadata: SIDMetadata): SIDMetadata {
        val userIdInfo = metadata.sidUserIdInfo
        userIdInfo.country = mSelectedCountryName
        mSIDUserIdInfo?.let {
            userIdInfo.firstName = it[SIDUserIdInfo.FIRST_NAME]
            userIdInfo.lastName = it[SIDUserIdInfo.LAST_NAME]
            userIdInfo.idNumber = it[SIDUserIdInfo.ID_NUMBER]
            userIdInfo.additionalValue(
                SIDUserIdInfo.DOB,
                it[SIDUserIdInfo.DOB]
            )
        }
        mSelectedIdCard?.let {
            userIdInfo.idType = it.replace(" ", "_")
        }
        return metadata
    }

    private fun upload(tag: String?) {
        val metadata = SIDMetadata()
        if (mKYCProductType != KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH) {
            setUserIdInfo(metadata)
        }
        val sidConfig = createConfig(metadata)
        if (SIDNetworkingUtils.haveNetworkConnection(this)) {
            mPbLoading.visibility = View.VISIBLE
            mSIDNetworkRequest.submit(sidConfig)
        } else {
            sidConfig?.let {
                SIDTagManager.getInstance(this)
                    .saveConfig(
                        tag,
                        it.jobType,
                        it.mode,
                        it.geoInformation,
                        it.sidMetadata,
                        this
                    )
            }
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun createConfig(metadata: SIDMetadata): SIDConfig? {
        val data = SIDNetData(this, AppData.getInstance(this).sdkEnvir)
        if (!TextUtils.isEmpty(savedUserId)
            && mKYCProductType == KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH
            && smartAuthType[SIDSelfieActivity.SMART_AUTH_CAPTURE_TYPE] != "ENROLL"
        ) {
            setPartnerParamsForAuth(metadata)
        }
        val infos = SIDGeoInfos.getInstance().geoInformation
        mConfig = object : SIDConfig.Builder(this) {
            init {
                setSmileIdNetData(data)
                setGeoInformation(infos)
                useEnrolledImage(mEnrolledUser)
                if (mKYCProductType == KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH) {
                    setJobType(if (smartAuthType[SIDSelfieActivity.SMART_AUTH_CAPTURE_TYPE] == "ENROLL") 4 else 2)
                } else {
                    setJobType(mKYCProductType.jobType)
                }
                setSIDMetadata(metadata)
                val isAuthMode =
                    ((mKYCProductType == KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH
                            && smartAuthType[SIDSelfieActivity.SMART_AUTH_CAPTURE_TYPE] != "ENROLL")
                            || mKYCProductType == KYC_PRODUCT_TYPE.ENHANCED_KYC)
                setMode(if (isAuthMode) SIDConfig.Mode.AUTHENTICATION else SIDConfig.Mode.ENROLL)
            }
        }.build(mCurrentTag)
        return mConfig
    }

    override fun onComplete() {
        mPbLoading.visibility = View.INVISIBLE
        Toast.makeText(this, "Job Complete", Toast.LENGTH_SHORT).show()
    }

    override fun onError(e: SIDException) {
        Log.d("JOB_SUBMISSION", "SIDEXCEPTION: " + e.message)
        mPbLoading.visibility = View.INVISIBLE
        mTvResult.setTextColor(Color.RED)
        mTvResult.text = e.message
        e.printStackTrace()
        go2Next(false, e.message)
    }

    override fun onUpdate(progress: Int) {
        mTvResult.text = "Progress $progress % "
    }

    override fun onStart() {
        super.onStart()
        mInternetStateBR?.register(this)
    }

    override fun onStop() {
        super.onStop()
        mInternetStateBR?.unregister(this)
    }

    private fun setPartnerParamsForAuth(metadata: SIDMetadata) {
        val params = metadata.partnerParams
        params.userId = savedUserId
    }

    override fun onEnrolled(response: SIDResponse) {
        Log.d("JOB_SUBMISSION", "ONENROLLED: $response")
        saveUserId(response.partnerParams.userId, response.partnerParams.jobId)
        mPbLoading.visibility = View.INVISIBLE
        val message: String
        var approved = false
        when (response.resultCodeState) {
            SIDResponse.SID_RESPONSE_ENROL_REJECTED, SIDResponse.SID_RESPONSE_ID_REJECTED, SIDResponse.SID_RESPONSE_ENROL_WID_REJECTED ->         // Enroll/Register was rejected
                message = getString(R.string.demo_enrolled_failed)
            SIDResponse.SID_RESPONSE_ENROL_WID_PROV_APPROVED, SIDResponse.SID_RESPONSE_ENROL_PROVISIONAL_APPROVAL -> {
                // Enroll/Register was provisionally approved
                approved = true
                message = getString(R.string.demo_provisionally_enrolled)
            }
            SIDResponse.SID_RESPONSE_IMAGE_NOT_USABLE ->         // Enroll/Register uploaded images were unsuable
                message = getString(R.string.demo_enrolled_image_unusable)
            SIDResponse.SID_RESPONSE_ENROL_APPROVED, SIDResponse.SID_RESPONSE_ENROL_WID_APPROVED -> {
                approved = true
                message = getString(R.string.demo_enrolled_successfully)
            }
            else -> message = getString(R.string.demo_enrolled_failed)
        }
        val stringBuilder = StringBuilder()
        if (!TextUtils.isEmpty(response.resultText)) {
            stringBuilder
                .append("Result Text : ")
                .append(response.resultText)
                .append(System.getProperty("line.separator"))
        }
        if (response.confidenceValue > 0) {
            stringBuilder.append(
                getString(
                    R.string.demo_enrolled_confidence_value,
                    response.confidenceValue
                )
            )
        }
        mTvConfidenceValue.text = message
        mTvResult.visibility =
            if (TextUtils.isEmpty(stringBuilder)) View.GONE else View.VISIBLE
        mTvResult.text = stringBuilder
        go2Next(approved, message)
    }

    override fun onAuthenticated(response: SIDResponse) {
        Log.d("JOB_SUBMISSION", "ONAUTHENTICATED: $response")
        val message: String
        var approved = false
        when (response.resultCodeState) {
            SIDResponse.SID_RESPONSE_AUTH_REJECTED, SIDResponse.SID_RESPONSE_ID_REJECTED ->         // Auth was rejected
                message = getString(R.string.demo_auth_failed)
            SIDResponse.SID_RESPONSE_UPDATE_PHOTO_REJECTED ->         // update photo was rejected
                message = getString(R.string.demo_update_image_failed)
            SIDResponse.SID_RESPONSE_AUTH_PROVISIONAL_APPROVAL -> {
                // auth was provisionally approved
                message = getString(R.string.demo_provisionally_authed)
                approved = true
            }
            SIDResponse.SID_RESPONSE_UPDATE_PHOTO_PROV_APPROVAL ->         // update photo was provisionally approved
                message = getString(R.string.demo_update_photo_provisional)
            SIDResponse.SID_RESPONSE_IMAGE_NOT_USABLE ->         // auth uploaded images were unsuable
                message = getString(R.string.demo_auth_image_unusable)
            SIDResponse.SID_RESPONSE_UPDATE_PHOTO_APPROVED ->         // update photo approved
                message = getString(R.string.demo_update_image_success)
            SIDResponse.SID_RESPONSE_AUTH_APPROVED -> {
                // auth approved
                message = getString(R.string.demo_auth_successfully)
                approved = true
            }
            else -> message = getString(R.string.demo_auth_failed)
        }
        val stringBuilder = StringBuilder()
        if (!TextUtils.isEmpty(response.resultText)) {
            stringBuilder
                .append("Result Text : ")
                .append(response.resultText)
                .append(System.getProperty("line.separator"))
        }
        if (response.confidenceValue > 0) {
            stringBuilder.append(
                getString(
                    R.string.demo_enrolled_confidence_value,
                    response.confidenceValue
                )
            )
        }
        mTvConfidenceValue.text = message
        mTvResult.visibility =
            if (TextUtils.isEmpty(stringBuilder)) View.GONE else View.VISIBLE
        mTvResult.text = stringBuilder
        go2Next(approved, message)
    }

    private fun go2Next(result: Boolean, message: String?) {
        val clazz: Class<*> =
            if (result) SIDJobCompletedActivity::class.java else SIDJobFailedActivity::class.java
        val runnable = Runnable {
            finish()
            startActivity(
                object :
                    Intent(this, clazz) {
                    init {
                        putExtra(SIDJobFailedActivity.FAILED_MSG, message)
                        if (result && mKYCProductType == KYC_PRODUCT_TYPE
                                .SMART_SELFIE_AUTH
                            && smartAuthType[SIDSelfieActivity
                                .SMART_AUTH_CAPTURE_TYPE].toString()
                                .equals("ENROLL")
                        ) {
                            putExtra(
                                SIDJobCompletedActivity.SUCCESS_MESSAGE,
                                getString(R.string.enroll_success_messsage)
                            )
                        }
                    }
                })
        }
        Handler().postDelayed(runnable, 2000)
    }

    private fun saveUserId(userId: String, jobId: String) {
        val editor = mSharedPreferences.edit()
        editor.putString(SIDStringExtras.SHARED_PREF_USER_ID, userId)
        editor.putString(SIDStringExtras.SHARED_PREF_JOB_ID, jobId)
        editor.apply()
    }

    // Retrieve user id to be saved
    private val savedUserId: String?
        private get() = mSharedPreferences.getString(
            SIDStringExtras.SHARED_PREF_USER_ID,
            ""
        )

    override fun onInternetStateChanged(recovered: Boolean) {
        /*if (recovered && mSAutoUpload.isChecked()) {
        upload(mCurrentTag);
    }*/
    }

    override fun onDocVerified(response: SIDResponse) {
        Log.d("JOB_SUBMISSION", "ONVERIFIED: $response")
        val stringBuilder = StringBuilder()
        if (!TextUtils.isEmpty(response.resultText)) {
            stringBuilder
                .append("Result Text : ")
                .append(response.resultText)
                .append(System.getProperty("line.separator"))
        }
        if (response.confidenceValue > 0) {
            stringBuilder.append(
                getString(
                    R.string.demo_enrolled_confidence_value,
                    response.confidenceValue
                )
            )
        }
        go2Next(true, "")
    }

    override fun onIdValidated(response: IDValidationResponse) {
        Log.d("JOB_SUBMISSION", "ONAUTHENTICATED: $response")
        val message: String
        var approved = false
        when (response.resultCode) {
            1012 -> {
                // auth approved
                message = getString(R.string.demo_auth_successfully)
                approved = true
            }
            else -> message = getString(R.string.demo_auth_failed)
        }
        val stringBuilder = StringBuilder()
        if (!TextUtils.isEmpty(response.resultText)) {
            stringBuilder
                .append("Result Text : ")
                .append(response.resultText)
                .append(System.getProperty("line.separator"))
        }
        mTvConfidenceValue.text = message
        mTvResult.visibility =
            if (TextUtils.isEmpty(stringBuilder)) View.GONE else View.VISIBLE
        mTvResult.text = stringBuilder
        go2Next(approved, message)
    }

    companion object {
        const val USER_ID_INFO_PARAM = "USER_ID_INFO_PARAM"
        const val USER_SELFIE_PARAM = "USER_SELFIE_PARAM"
        const val DOC_COUNTRY_PARAM = "DOC_COUNTRY_PARAM"
        const val DOC_ID_TYPE_PARAM = "DOC_ID_TYPE_PARAM"
        const val SMART_AUTH_PARAM = "SMART_AUTH_PARAM"
    }
}