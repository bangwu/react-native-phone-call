
package com.reactlibrary;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;
public class RNPhoneCallPackage implements ReactPackage {

    private RNPhoneCallModule phoneCallModule;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
      phoneCallModule = new RNPhoneCallModule(reactContext);
      return Arrays.<NativeModule>asList(phoneCallModule);
    }

    // Deprecated from RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
      return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
      return Collections.emptyList();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        phoneCallModule.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}