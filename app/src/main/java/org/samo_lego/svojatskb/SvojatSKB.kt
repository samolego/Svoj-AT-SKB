package org.samo_lego.svojatskb

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

const val SKB_PACKAGE_NAME = "com.halcom.mobile.hybrid.skbasi2xxxx";
class SvojatSKB : IXposedHookLoadPackage {

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName.equals(SKB_PACKAGE_NAME)) {
            XposedBridge.log("[Svojat] Hooking into " + lpparam.packageName)
        }
    }
}