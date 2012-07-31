/*
 * Copyright 2010-2012 Sony Ericsson Mobile Communications AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mariotaku.twidere.extension.timescape;

import static org.mariotaku.twidere.extension.timescape.util.EventStreamAccessor.insertOrUpdatePlugin;

import java.util.ArrayList;

import org.mariotaku.twidere.Twidere;
import org.mariotaku.twidere.extension.timescape.util.ListUtils;
import org.mariotaku.twidere.extension.timescape.util.Utils;
import org.mariotaku.twidere.provider.TweetStore.Accounts;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.sonyericsson.eventstream.EventStreamConstants.ConfigState;

/**
 * This activity handles the extension event on user interface.
 */
public class ExtensionSettingsActivity extends FragmentActivity implements Constants, LoaderCallbacks<Cursor>,
		OnClickListener, OnItemClickListener {

	private SimpleCursorAdapter mAdapter;
	private ListView mListView;
	private View mEmptyView;
	private Cursor mCursor;
	private SharedPreferences mPreferences;

	private static final String[] FROM = new String[] { Accounts.USERNAME };
	private static final int[] TO = new int[] { android.R.id.text1 };

	private final ArrayList<Long> mSelectedIds = new ArrayList<Long>();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add_account: {
				try {
					startActivity(new Intent(Twidere.INTENT_ACTION_TWITTER_LOGIN));
				} catch (final ActivityNotFoundException e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(TWIDERE_GOOGLE_PLAY_LINK)));
				}
				break;
			}
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, null, FROM, TO, 0);
		mListView = (ListView) findViewById(android.R.id.list);
		mEmptyView = findViewById(android.R.id.empty);
		mListView.setAdapter(mAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mListView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);
		final long[] activated_ids = savedInstanceState != null ? savedInstanceState
				.getLongArray(Twidere.INTENT_KEY_IDS) : Utils.getSelectedIdsInPreferences(this);
		mSelectedIds.clear();
		if (activated_ids != null) {
			for (final long id : activated_ids) {
				mSelectedIds.add(id);
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		final Uri uri = Accounts.CONTENT_URI;
		final String[] cols = Accounts.COLUMNS;
		final String where = Accounts.IS_ACTIVATED + " = 1";
		return new CursorLoader(this, uri, cols, where, null, null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (mCursor == null || mCursor.isClosed()) return;

		final SparseBooleanArray checkedpositions = mListView.getCheckedItemPositions();
		final boolean checked = checkedpositions.get(position, false);
		mCursor.moveToPosition(position);
		final long user_id = mCursor.getLong(mCursor.getColumnIndex(Accounts.USER_ID));
		if (!checked) {
			if (mSelectedIds.contains(user_id)) {
				mSelectedIds.remove(user_id);
			}
		} else if (!mSelectedIds.contains(user_id)) {
			mSelectedIds.add(user_id);
		}
		final SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(PREFERENCE_KEY_SELECTED_IDS, ListUtils.buildString(mSelectedIds, ',', false));
		editor.commit();

		insertOrUpdatePlugin(this, mSelectedIds.size() > 0 ? ConfigState.CONFIGURED : ConfigState.NOT_CONFIGURED);
		final Application application = getApplication();
		if (application instanceof ExtensionApplication) {
			((ExtensionApplication) application).loadStatuses();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
		mCursor = null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.changeCursor(cursor);
		mCursor = cursor;
		final boolean has_activated_account = cursor != null && cursor.getCount() > 0;
		mListView.setVisibility(has_activated_account ? View.VISIBLE : View.GONE);
		mEmptyView.setVisibility(!has_activated_account ? View.VISIBLE : View.GONE);
		if (mCursor != null) {
			for (final long id : mSelectedIds) {
				mCursor.moveToFirst();
				while (!mCursor.isAfterLast()) {
					final long user_id = mCursor.getLong(mCursor.getColumnIndexOrThrow(Accounts.USER_ID));
					if (id == user_id) {
						mListView.setItemChecked(mCursor.getPosition(), true);
					}
					mCursor.moveToNext();
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		final int ids_size = mSelectedIds.size();
		final long[] ids = new long[ids_size];
		for (int i = 0; i < ids_size; i++) {
			ids[i] = mSelectedIds.get(i);
		}
		outState.putLongArray(Twidere.INTENT_KEY_IDS, ids);
		super.onSaveInstanceState(outState);
	}

}
