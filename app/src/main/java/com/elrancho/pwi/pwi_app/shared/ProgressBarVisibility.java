package com.elrancho.pwi.pwi_app.shared;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

public class ProgressBarVisibility {

    private Context mContext;
    private View vLoginForm;
    private View vProgressBar;

    public ProgressBarVisibility(Context mContext, View vLoginForm, View vProgressBar) {
        this.mContext=mContext;
        this.vLoginForm = vLoginForm;
        this.vProgressBar = vProgressBar;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = mContext.getResources().getInteger(android.R.integer.config_shortAnimTime);

            vLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            vLoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                }
            });

            vProgressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            vProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    vProgressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            vProgressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            vLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
