package ru.hgusein.cftest.activity;

import ru.hgusein.cftest.R;

import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;


public class SettingsActivity extends PreferenceActivity {

  // private static String TAG = "SettingsActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    FragmentManager fm = getFragmentManager();
    PrefsFragment pf = new PrefsFragment();
    fm.beginTransaction().replace(android.R.id.content, pf).commit();
  }

  public static class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.preference_main);
    }
  }

}
