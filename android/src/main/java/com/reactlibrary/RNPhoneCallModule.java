
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNPhoneCallModule extends ReactContextBaseJavaModule {

    private static final String NATIVE_MODULE_NAME = "RNPhoneCall";
    private static final int REQUEST_READ_CALL_LOG_CODE = 210;
    private static final int REQUEST_CALL_PHONE_CODE = 220;

    private Promise phoneCallPromise;
    private String phoneNumber;
    private final ReactApplicationContext reactContext;

    public RNPhoneCallModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void call(String phone, Promise promise) {
      this.phoneCallPromise = promise;
      this.phoneNumber = phone;
      checkDirectCallPermission();
    }

    @Override
    public String getName() {
      return NATIVE_MODULE_NAME;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
      switch (requestCode) {
        case REQUEST_READ_CALL_LOG_CODE:
          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startReadingCallLog();
          } else {
            phoneCallPromise.reject(NATIVE_MODULE_NAME, "android.permission.READ_CALL_LOG is denied");
          }
          break;
        case REQUEST_CALL_PHONE_CODE:
          if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startMakingCall();
          } else {
            phoneCallPromise.reject(NATIVE_MODULE_NAME, "android.permission.CALL_PHONE is denied");
          }
          break;
      }
    }

    private void checkCallLogPermission() {
      if (ContextCompat.checkSelfPermission(getReactApplicationContext(), permission.READ_CALL_LOG)
              == PackageManager.PERMISSION_GRANTED) {
        startReadingCallLog();
      } else if (getCurrentActivity() != null) {
        ActivityCompat.requestPermissions(getCurrentActivity(),
                new String[]{permission.READ_CALL_LOG}, REQUEST_READ_CALL_LOG_CODE);
      }
    }

    private void checkDirectCallPermission() {
      if (ContextCompat.checkSelfPermission(getReactApplicationContext(), permission.CALL_PHONE)
              == PackageManager.PERMISSION_GRANTED) {
        startMakingCall();
      } else if (getCurrentActivity() != null) {
        ActivityCompat.requestPermissions(getCurrentActivity(),
                new String[]{permission.CALL_PHONE}, REQUEST_CALL_PHONE_CODE);
      }
    }

    private void startMakingCall() {
      Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
      callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), permission.CALL_PHONE)
              != PackageManager.PERMISSION_GRANTED) {
        return;
      }
      getReactApplicationContext().startActivity(callIntent);
      getReactApplicationContext().getContentResolver()
              .registerContentObserver(CallLog.Calls.CONTENT_URI, true, new CallContentObserver());
    }

    private void startReadingCallLog() {
      if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), permission.READ_CALL_LOG)
              != PackageManager.PERMISSION_GRANTED) {
        return;
      }
      String columns[] = new String[]{CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION, CallLog.Calls.TYPE};
      String selection = CallLog.Calls.NUMBER + "='" + phoneNumber + "'";
      Cursor cursor = getReactApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
              columns, selection, null, CallLog.Calls._ID + " DESC");
      if (cursor != null && cursor.moveToFirst()) {
        long date = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
        long duration = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION));
        if (duration == 0) {
          phoneCallPromise.reject(NATIVE_MODULE_NAME, "Call is not answered");
        } else {
          WritableMap writableMap = createPhoneCallMap(duration);
          phoneCallPromise.resolve(writableMap);
        }
      } else {
        phoneCallPromise.reject(NATIVE_MODULE_NAME, "Unexpected call log read failed");
      }
    }

    private WritableMap createPhoneCallMap(long duration) {
      WritableMap params = Arguments.createMap();
      params.putString("call_duration", String.valueOf(duration));
      return params;
    }

    private class CallContentObserver extends ContentObserver {

      CallContentObserver() {
        super(null);
      }

      @Override
      public boolean deliverSelfNotifications() {
        return true;
      }

      @Override
      public void onChange(boolean selfChange) {
        getReactApplicationContext().getContentResolver()
                .unregisterContentObserver(this);
        checkCallLogPermission();
      }
    }
}