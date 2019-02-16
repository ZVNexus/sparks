/*
 * Copyright (C) 2018 StatiXOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.statix.sparks.fragments.misc;

import android.app.Dialog;
import android.content.Context;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.settings.R;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.du.ActionUtils;

import com.statix.sparks.preferences.CustomSettingsPreferenceFragment;

import com.statix.support.preferences.IconPackPreference;

import java.util.ArrayList;
import java.util.List;

public class Misc extends CustomSettingsPreferenceFragment implements OnPreferenceChangeListener {
    private static final String TAG = "Misc";
    private ListPreference mRecentsComponentType;
    private static final String RECENTS_COMPONENT_TYPE = "recents_component";

    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.misc);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();

        // recents component type
        mRecentsComponentType = (ListPreference) findPreference(RECENTS_COMPONENT_TYPE);
        int type = Settings.System.getInt(resolver,
		Settings.System.RECENTS_COMPONENT, 0);
        mRecentsComponentType.setValue(String.valueOf(type));
        mRecentsComponentType.setSummary(mRecentsComponentType.getEntry());
        mRecentsComponentType.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.SPARKS;
    }

    @Override
    public void onResume() {
        super.onResume();
        IconPackPreference iconPackPref = (IconPackPreference) findPreference("recents_icon_pack");
        // Re-initialise preference
        iconPackPref.init();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
  
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mRecentsComponentType) {
            int type = Integer.valueOf((String) objValue);
            int index = mRecentsComponentType.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.RECENTS_COMPONENT, type);
            mRecentsComponentType.setSummary(mRecentsComponentType.getEntries()[index]);
            if (type == 1) { // Disable swipe up gesture, if oreo type selected
               Settings.Secure.putInt(getActivity().getContentResolver(),
                    Settings.Secure.SWIPE_UP_TO_SWITCH_APPS_ENABLED, 0);
            }
            ActionUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }
}
