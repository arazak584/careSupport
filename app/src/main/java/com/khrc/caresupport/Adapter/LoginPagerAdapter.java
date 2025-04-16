package com.khrc.caresupport.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.khrc.caresupport.Fragment.HealthcareFragmentProviderLogin;
import com.khrc.caresupport.Fragment.PregnantFragmentWomanLogin;

public class LoginPagerAdapter extends FragmentStateAdapter {

    public LoginPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new PregnantFragmentWomanLogin();
        } else {
            return new HealthcareFragmentProviderLogin();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
