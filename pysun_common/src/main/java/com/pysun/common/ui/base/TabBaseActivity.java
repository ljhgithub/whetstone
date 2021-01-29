package com.pysun.common.ui.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TabBaseActivity extends BaseActivity {


    private Map<String, TabBaseFragment> fragmentMap = new HashMap<>();

    private TabBaseFragment currentFragment;

    public TabBaseFragment addFragment(TabBaseFragment fragment) {
        List<TabBaseFragment> fragments = new ArrayList<>();
        fragments.add(fragment);

        addFragments(fragments);
        return addFragments(fragments).get(0);

    }

    public List<TabBaseFragment> addFragments(List<TabBaseFragment> baseFragments) {

        List<TabBaseFragment> fragments = new ArrayList<>(baseFragments.size());
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        boolean commit = false;
        for (int i = 0; i < baseFragments.size(); i++) {
            // install
            TabBaseFragment fragment = baseFragments.get(i);
            int id = fragment.getContainerId();

            // exists
            TabBaseFragment fragment2 = (TabBaseFragment) fm.findFragmentByTag(fragment.getFragmentTag());

            if (fragment2 == null) {
                fragment2 = fragment;
                ft.add(id, fragment, fragment.getFragmentTag()).hide(fragment);
                commit = true;
            } else {
                ft.hide(fragment2);
            }

            fragmentMap.put(fragment2.getFragmentTag(), fragment2);
            fragments.add(i, fragment2);
            fragment2.setCurrentState(TabBaseFragment.HIDDEN);

        }
        if (commit) {
            try {
                ft.commit();
                fm.executePendingTransactions();

            } catch (Exception e) {

            }
        }
        return fragments;
    }


    protected TabBaseFragment switchContent(TabBaseFragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        Fragment fragment2 = fm.findFragmentByTag(fragment.getFragmentTag());
        if (null == fragment2) {
            fragmentTransaction.add(fragment.getContainerId(), fragment, fragment.getFragmentTag());
        }

        for (TabBaseFragment fg:fragmentMap.values()){
            if (!fg.getFragmentTag().equals(fragment.getFragmentTag())){
                fragmentTransaction.hide(fg);
                fg.setCurrentState(TabBaseFragment.HIDDEN);
            }
        }
        fragmentTransaction.show(fragment);
        try {
            fragmentTransaction.commitNow();

            boolean isReenter=false;
            if (null!=currentFragment){
                currentFragment.onExit(currentFragment.getEnterType());
                currentFragment.setCurrentState(TabBaseFragment.HIDDEN);
                if (currentFragment.getFragmentTag().equals(fragment.getFragmentTag())){
                    isReenter=true;
                }
            }

          if (isReenter){
              fragment.onReenter(fragment.getEnterType());
          }else{
              fragment.onEnter(fragment.getEnterType());
          }
            fragment.setCurrentState(TabBaseFragment.SHOWN);
            currentFragment=fragment;
        } catch (Exception e) {

        }

        return fragment;
    }


}
