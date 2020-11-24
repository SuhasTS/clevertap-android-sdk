package com.clevertap.android.sdk.login;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import com.clevertap.android.sdk.BaseCTApiListener;
import com.clevertap.android.sdk.CleverTapInstanceConfig;

@RestrictTo(Scope.LIBRARY)
public class ProfileHandlerFactory {

    private ProfileHandlerFactory() {

    }

    public static IProfileHandler getProfileHandler(@NonNull BaseCTApiListener ctApiListener) {
        LoginInfoProvider cacheHandler = new LoginInfoProvider(ctApiListener);

        if (cacheHandler.isLegacyProfileLoggedIn()) {
            // case 1: Migration( cached guid but no newly saved profile pref)
            return new LegacyProfileHandlerImpl();
        } else {
            // case 2: Not logged in but default config
            // case 3: Not logged in but non-default config
            return new ConfigurableProfileHandlerImpl(ctApiListener);
        }
    }
}