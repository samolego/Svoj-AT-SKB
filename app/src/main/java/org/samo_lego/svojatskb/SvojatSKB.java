package org.samo_lego.svojatskb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class SvojatSKB implements IXposedHookLoadPackage {
    private static final String SKB_PACKAGE_NAME = "com.halcom.mobile.hybrid.skbasi2xxxx";

    private static void log(String message) {
        XposedBridge.log("[Svojat] " + message);
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) {
        if (lpparam.packageName.equals(SKB_PACKAGE_NAME)) {
            log("Hooking into " + SKB_PACKAGE_NAME);

            final ClassLoader classLoader = lpparam.classLoader;

            // Method that shows the root dialog if needed
            try {
                XposedHelpers.findAndHookMethod("com.halcom.mobile.hybrid.activity.MainActivity", classLoader, "G1", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("MainActivity#G1 would show root dialog by default, forcing no dialog.");
                        param.setResult(null);
                    }
                });
                log("Hooked into MainActivity#G1 (=showRootDialogIfNeeded)");
            } catch (Throwable e) {
                log("Error while hooking into MainActivity#G1: " + e.getMessage());
            }


            // Modifies root policy
            try {
                XposedHelpers.findAndHookMethod("com.halcom.mobile.hybrid.otp.GemaltoApplicationProperties", classLoader, "getRootPolicy", new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("GemaltoApplicationProperties#getRootPolicy would return " + param.getResult() + " by default, forcing NONE.");
                        getPolicy(classLoader).ifPresent(param::setResult);
                    }
                });
                log("Hooked into getRootPolicy");
            } catch (Throwable e) {
                log("Error while hooking into GemaltoApplicationProperties#getRootPolicy: " + e.getMessage());
            }


            // Modifies isDeviceRooted to always return false
            try {
                XposedHelpers.findAndHookMethod("com.halcom.mobile.security.otp.GemaltoPinVerifier", classLoader, "isDeviceRooted", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("GemaltoPinVerifier#isDeviceRooted would return " + param.getResult() + " by default, forcing false.");
                        param.setResult(false);
                    }
                });
                log("Hooked into isDeviceRooted");
            } catch (Throwable e) {
                log("Error while hooking into GemaltoPinVerifier#isDeviceRooted: " + e.getMessage());
            }

            // Methods returning if the device is rooted
            try {
                Class<?> mainActivityClass = XposedHelpers.findClass("com.halcom.mobile.hybrid.activity.MainActivity", classLoader);
                XposedHelpers.findAndHookMethod("com.halcom.mobile.hybrid.otp.GemaltoOtpToken", classLoader, "isRooted", mainActivityClass, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("GemaltoOtpToken#isRooted would return " + param.getResult() + " by default, forcing false.");
                        param.setResult(false);
                    }
                });
                log("Hooked into GemaltoOtpToken#isRooted");

                XposedHelpers.findAndHookMethod("com.halcom.mobile.hybrid.otp.approve.HIDDirectOtpToken", classLoader, "isRooted", mainActivityClass, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("HIDDirectOtpToken#isRooted would return " + param.getResult() + " by default, forcing false.");
                        param.setResult(false);
                    }
                });
                log("Hooked into HIDDirectOtpToken#isRooted");
            } catch (Throwable e) {
                log("Error while hooking into GemaltoOtpToken#isRooted or HIDDirectOtpToken#isRooted: " + e.getMessage());
            }


            // Disable the root dialog button exit ('Vaša naprava je "rootana", zato se bo aplikacija zaprla.')
            try {
                XposedHelpers.findAndHookMethod("com.halcom.mobile.hybrid.activity.MainActivity$5", classLoader, "a", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("Hiding root warning dialog.");
                        param.setResult(null);
                    }
                });
                log("Hooked into MainActivity$5#a (= app root dialog button to close app)");
            } catch (Throwable e) {
                log("Error while hooking into MainActivity$5#a: " + e.getMessage());
            }
        }
    }

    private Optional<Object> getPolicy(ClassLoader classLoader) {
        try {
            Class<?> RootPolicyClass = classLoader.loadClass("com.halcom.mobile.security.otp.PinVerifier$RootPolicy");
            Method valueOf = RootPolicyClass.getMethod("valueOf", String.class);
            valueOf.invoke(null, "NONE");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            log("Error while loading RootPolicy class: " + e.getMessage());
        }

        return Optional.empty();
    }
}