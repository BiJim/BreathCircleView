package com.github.bijim.aboutwear;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.wear.widget.SwipeDismissFrameLayout;
import android.support.wear.widget.SwipeDismissFrameLayout.Callback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * base dialogFragment 使DialogFragment在手表上支持右滑关闭
 * Created by BiJim on 2018/8/9.
 */

public abstract class WearDialogFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SwipeDismissFrameLayout swipeLayout = new SwipeDismissFrameLayout(getActivity());
        swipeLayout.addCallback(mSwipeCallBack);
        View rootView = initRootView(inflater, swipeLayout, savedInstanceState);
        swipeLayout.addView(rootView);
        return swipeLayout;
    }

    protected abstract View initRootView(LayoutInflater inflater, SwipeDismissFrameLayout container, Bundle savedInstanceState);

    private final Callback mSwipeCallBack = new Callback() {
        @Override
        public void onSwipeStarted(SwipeDismissFrameLayout layout) {
            super.onSwipeStarted(layout);
        }

        @Override
        public void onSwipeCanceled(SwipeDismissFrameLayout layout) {
            super.onSwipeCanceled(layout);
        }

        @Override
        public void onDismissed(SwipeDismissFrameLayout layout) {
            super.onDismissed(layout);
            dismiss();
        }
    };
}
