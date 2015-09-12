package com.bemoreio.podchief.ui.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Cody on 3/24/15.
 */
public abstract class BaseFragment extends Fragment
{
//    @Inject
//    ScopedBus scopedBus;

//    private SmeProgressDialog progressDialog;

    protected View toolbarView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    protected boolean shouldInject()
    {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState)
    {
        View view = inflater.inflate(getFragmentLayoutId(), container, false);
        injectViews(view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

//        if (scopedBus != null) {
//            scopedBus.register(oAuthTokenExpiredHandler);
//        }
    }

    @Override
    public void onStop() {
        super.onStop();

//        if (scopedBus != null) {
//            scopedBus.unregister(oAuthTokenExpiredHandler);
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

//        if (scopedBus != null) {
//            scopedBus.resumed();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();

//        if (scopedBus != null) {
//            scopedBus.paused();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * Return the layout id to be inflated in {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}. This method
     * avoids duplicating the inflate and inject code in every fragment. You only have to return the layout to
     * inflate in this method when extends BaseFragment.
     */
    protected abstract int getFragmentLayoutId();

    private void injectViews(final View view) {
        ButterKnife.bind(this, view);
    }

    protected ActionBar getActionBar()
    {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    protected void changeFragment(BaseFragment fragment)
    {
//        MainActivity mainActivity = (MainActivity)getActivity();
//        mainActivity.replaceFragment(fragment, false);
    }

    protected void replaceFragment(int containerViewId, Fragment fragment) {
        replaceFragment(containerViewId, fragment, false);
    }

    protected void replaceFragment(int containerViewId, Fragment fragment, boolean addToBackstack)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (addToBackstack)
            ft.addToBackStack(null);

        ft.remove(this);
        ft.add(containerViewId, fragment).commit();
        getFragmentManager().executePendingTransactions();
    }

//    protected void showFragmentWithSlidingTransition(Fragment fragment, boolean addToBackStack)
//    {
//        showFragmentWithTransition(fragment, R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right, addToBackStack);
//    }

//    protected void showFragmentWithTransition(Fragment fragment, int enter, int exit, int popEnter, int popExit, boolean addToBackStack)
//    {
//        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//
//        fragmentTransaction.setCustomAnimations(enter, exit, popEnter, popExit);
//
//        if (addToBackStack) {
//            fragmentTransaction.addToBackStack(null);
//        }
//
//        fragmentTransaction
//                .replace(R.id.content, fragment)
//                .commit();
//    }

    public void launchExternalWebsite(int urlStringId)
    {
        String urlString = getString(urlStringId);
        launchExternalWebsite(urlString);
    }

    public void launchExternalWebsite(String urlString)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        startActivity(browserIntent);
    }
}
