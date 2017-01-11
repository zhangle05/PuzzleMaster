package master.sudoku.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import master.sudoku.utils.AppUtil;

/**
 * Created by zhangle on 03/01/2017.
 */
public class PuzzleMasterApp extends Application {
    private static PuzzleMasterApp sInstance;


    private int mVersionCode;
    private String mVersionName;
    private String mDeviceId;
    private Application mApp;

    private SharedPreferences mPreferences;

    public static PuzzleMasterApp getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public void setApplication(Application app) {
        this.mApp = app;
    }

    public Application getApplication() {
        return mApp;
    }

    public SharedPreferences getPreferences() {
        if (mPreferences == null) {
            mPreferences = getSharedPreferences(getPreferencesName(), MODE_PRIVATE);
        }
        return mPreferences;
    }

    /**
     * @return app shared preference name.
     */
    protected String getPreferencesName() {
        return mApp.getPackageName();
    }

    /**
     * @return the application's version name set in the manifest.
     */
    public String getVersionName() {
        checkVersionInfo();
        return mVersionName;
    }

    /**
     * @return The unique device id.
     */
    public String getDeviceId() {
        if (mDeviceId == null) {
            mDeviceId = generateDeviceId();
        }
        return mDeviceId;
    }

    /**
     * @return true if the device is tablet, otherwise false.
     */
    public boolean isTablet() {
        final TelephonyManager tm = (TelephonyManager) mApp.getSystemService(Application.TELEPHONY_SERVICE);
        return tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE;
    }

    private void checkVersionInfo() {
        if (mVersionName == null || mVersionCode == 0) {
            try {
                final PackageInfo packageInfo = mApp.getPackageManager()
                        .getPackageInfo(mApp.getApplicationInfo().packageName, 0);
                if (packageInfo != null) {
                    mVersionName = packageInfo.versionName;
                    mVersionCode = packageInfo.versionCode;
                }
            } catch (Exception ignored) {
            }
        }
    }

    private String generateDeviceId() {
        final SharedPreferences sp = getPreferences();
        String result = sp.getString("device_id", null);
        if (!TextUtils.isEmpty(result)) {
            return result;
        }

        if (TextUtils.isEmpty(result)) {
            final TelephonyManager tm = (TelephonyManager) mApp.getSystemService(Application.TELEPHONY_SERVICE);
            result = tm.getDeviceId();
            if (TextUtils.isEmpty(result)) {
                result = tm.getSimSerialNumber();
            }
        }

        result = AppUtil.getMd5(result);
        sp.edit().putString("device_id", result).apply();

        return result;
    }
}
