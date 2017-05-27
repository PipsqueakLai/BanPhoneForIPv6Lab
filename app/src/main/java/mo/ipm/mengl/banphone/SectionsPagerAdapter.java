package mo.ipm.mengl.banphone;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by laicm on 25/5/2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StartUpFragment();
            case 1:
                return new SettingFragment();
            case 2:
                return new DeviceScannerFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "啟動";
            case 1:
                return "設定";
            case 2:
                return "標記裝置";
        }
        return null;

    }
}

