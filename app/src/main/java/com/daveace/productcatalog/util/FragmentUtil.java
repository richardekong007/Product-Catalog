package com.daveace.productcatalog.util;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.daveace.productcatalog.R;

public class FragmentUtil {

    public static void replaceFragment(FragmentManager fManager, Fragment fragment, Bundle args, boolean stackOnBackStack) {

        FragmentTransaction fTransaction;
        if (fragmentManagerIsNotNull(fManager)) {

            fTransaction = fManager.beginTransaction();
            if (fragmentIsNotNull(fragment) && argumentsIsNotNull(args)) {
                fragment.setArguments(args);
            }
            fTransaction.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName());
            new Handler().post(() -> {
                if (stackOnBackStack) {
                    fTransaction.addToBackStack(null);
                } else {
                    fManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                fTransaction.commitAllowingStateLoss();
            });
        }
    }

    public static void takeOffBackStack(FragmentManager fManager, Fragment fragment) {

        FragmentTransaction fTransaction;
        if (fragmentManagerIsNotNull(fManager) && fragmentIsNotNull(fragment)) {

            fTransaction = fManager.beginTransaction();
            fTransaction.remove(fragment);
            fTransaction.commit();
            fManager.popBackStack();
        }
    }

    private static boolean fragmentManagerIsNotNull(FragmentManager fManager) {
        return fManager != null;
    }

    private static boolean argumentsIsNotNull(Bundle arguments) {
        return arguments != null;
    }

    private static boolean fragmentIsNotNull(Fragment fragment) {
        return fragment != null;
    }
}
