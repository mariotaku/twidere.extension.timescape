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

package org.mariotaku.twidere.extension.timescape.util;

import java.util.HashMap;
import java.util.Map;

import org.mariotaku.twidere.extension.timescape.Constants;
import org.mariotaku.twidere.extension.timescape.ExtensionSettingsActivity;
import org.mariotaku.twidere.extension.timescape.R;
import org.mariotaku.twidere.extension.timescape.model.Indices;
import org.mariotaku.twidere.provider.TweetStore.Accounts;
import org.mariotaku.twidere.provider.TweetStore.Statuses;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;

import com.sonyericsson.eventstream.EventStreamConstants;
import com.sonyericsson.eventstream.EventStreamConstants.EventColumns;
import com.sonyericsson.eventstream.EventStreamConstants.PluginColumns;
import com.sonyericsson.eventstream.EventStreamConstants.ProviderUris;
import com.sonyericsson.eventstream.EventStreamConstants.SourceColumns;
import com.sonyericsson.eventstream.EventStreamConstants.StatusSupport;

/**
 * This class will handle the provider and provides event data to the
 * EventStream.
 */
public class EventStreamAccessor implements Constants {

	private static Map<Long, String> sAccountProfileImageCache = new HashMap<Long, String>();

	/**
	 * Clear the all events from the EventSteam event table.
	 * 
	 * @param context The context
	 */
	public static void clearData(Context context) {
		final int sourceId = getSourceId(context);
		if (-1 == sourceId) return;
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		cv.putNull(SourceColumns.CURRENT_STATUS);
		cv.putNull(SourceColumns.STATUS_TIMESTAMP);
		cr.update(ProviderUris.SOURCE_PROVIDER_URI, cv, null, null);
		cr.delete(ProviderUris.EVENT_PROVIDER_URI, EventColumns.SOURCE_ID + "=" + sourceId, null);
	}

	public static String getAccountProfileImageLink(Context context, long account_id) {
		String link = null;
		if (!sAccountProfileImageCache.keySet().contains(account_id)) {
			final Uri uri = Accounts.CONTENT_URI;
			final String[] cols = new String[] { Accounts.PROFILE_IMAGE_URL };
			final String where = Accounts.USER_ID + " = " + account_id;
			final Cursor cur = context.getContentResolver().query(uri, cols, where, null, null);
			if (cur != null) {
				if (cur.getCount() == 1) {
					cur.moveToFirst();
					link = cur.getString(cur.getColumnIndex(Accounts.PROFILE_IMAGE_URL));
					sAccountProfileImageCache.put(account_id, link);
				}
				cur.close();
			}
		} else {
			link = sAccountProfileImageCache.get(account_id);
		}
		return link;
	}

	/**
	 * Get source id from the EventSteram source table.
	 * 
	 * @param context The context
	 * @return The source id.
	 */
	public static int getSourceId(Context context) {
		int sourceId = -1;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(ProviderUris.SOURCE_PROVIDER_URI,
					new String[] { SourceColumns.ID }, null, null, null);
			if (null != cursor && cursor.moveToFirst() && 0 < cursor.getCount()) {
				sourceId = cursor.getInt(cursor.getColumnIndexOrThrow(SourceColumns.ID));
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
		return sourceId;
	}

	/**
	 * Inserts this plugin data into the EventStream plugin table. If the plugin
	 * is already stored, plugin data is updated
	 * 
	 * @param context The context
	 * @param configState The configuration state value
	 */
	public static void insertOrUpdatePlugin(Context context, Integer configState) {
		Cursor cursor = null;
		try {
			final ContentResolver cr = context.getContentResolver();
			final String configName = new ComponentName(context.getPackageName(),
					ExtensionSettingsActivity.class.getName()).flattenToShortString();
			final Builder iconUriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
					.authority(context.getPackageName()).appendPath(Integer.toString(R.drawable.icon_plugin));

			final ContentValues cv = new ContentValues();
			cv.put(PluginColumns.API_VERSION, 1);
			cv.put(PluginColumns.CONFIGURATION_ACTIVITY, configName);
			cv.put(PluginColumns.ICON_URI, iconUriBuilder.toString());
			cv.put(PluginColumns.NAME, context.getString(R.string.plugin_name));
			cv.put(PluginColumns.PLUGIN_KEY, PLUGIN_KEY);
			cv.put(PluginColumns.STATUS_SUPPORT, StatusSupport.HAS_SUPPORT_TRUE);
			cv.put(PluginColumns.STATUS_TEXT_MAX_LENGTH, 140);

			cursor = cr.query(ProviderUris.PLUGIN_PROVIDER_URI, null, null, null, null);
			if (null != cursor && cursor.moveToNext() && 0 < cursor.getCount()) {
				if (null != configState) {
					cv.put(PluginColumns.CONFIGURATION_STATE, configState);
				}
				cr.update(ProviderUris.PLUGIN_PROVIDER_URI, cv, null, null);
			} else {
				if (null != configState) {
					cv.put(PluginColumns.CONFIGURATION_STATE, configState);
				} else {
					cv.put(PluginColumns.CONFIGURATION_STATE, EventStreamConstants.ConfigState.NOT_CONFIGURED);
				}
				cr.insert(ProviderUris.PLUGIN_PROVIDER_URI, cv);
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
	}

	/**
	 * Inserts this source into the EventStream source table. If the source is
	 * already stored, source data is updated
	 * 
	 * @param context The context
	 */
	public static void insertOrUpdateSource(Context context) {
		Cursor cursor = null;
		try {
			final Builder iconUriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
					.authority(context.getPackageName()).appendPath(Integer.toString(R.drawable.icon_source));

			final ContentValues cv = new ContentValues();
			cv.put(SourceColumns.ICON_URI, iconUriBuilder.toString());
			cv.put(SourceColumns.NAME, context.getString(R.string.source_name));

			final ContentResolver cr = context.getContentResolver();
			cursor = cr.query(ProviderUris.SOURCE_PROVIDER_URI, null, null, null, null);
			if (null != cursor && cursor.moveToNext() && 0 < cursor.getCount()) {
				cr.update(ProviderUris.SOURCE_PROVIDER_URI, cv, null, null);
			} else {
				cv.put(SourceColumns.ENABLED, 1);
				cr.insert(ProviderUris.SOURCE_PROVIDER_URI, cv);
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
	}

	public static void loadHomeTimelineToEventStream(Context context) {
		if (context == null) return;
		final int sourceId = getSourceId(context);
		if (-1 == sourceId) return;

		final SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		final String selection = Statuses.ACCOUNT_ID + " IN(" + preferences.getString(PREFERENCE_KEY_SELECTED_IDS, "")
				+ ")";
		final Cursor cursor = context.getContentResolver().query(Statuses.CONTENT_URI, Statuses.COLUMNS, selection,
				null, null);
		final Indices indices = new Indices(cursor);
		cursor.moveToFirst();
		final ContentValues[] cvs = new ContentValues[cursor.getCount()];

		while (!cursor.isAfterLast()) {
			final ContentValues cv = new ContentValues();
			cv.put(EventColumns.EVENT_KEY,
					cursor.getLong(indices.account_id_idx) + ";" + cursor.getLong(indices.status_id_idx));
			cv.put(EventColumns.FRIEND_KEY, String.valueOf(cursor.getLong(indices.user_id_idx)));
			cv.put(EventColumns.IMAGE_URI, cursor.getString(indices.profile_image_url_idx));
			cv.put(EventColumns.MESSAGE, cursor.getString(indices.text_plain_idx));
			cv.put(EventColumns.OUTGOING, 0);
			cv.put(EventColumns.PERSONAL, 0);
			cv.put(EventColumns.PUBLISHED_TIME, cursor.getLong(indices.timestamp_idx));
			cv.put(EventColumns.SOURCE_ID, sourceId);
			cv.put(EventColumns.STATUS_ICON_URI,
					getAccountProfileImageLink(context, cursor.getLong(indices.account_id_idx)));
			cv.put(EventColumns.TITLE, cursor.getString(indices.name_idx));

			cvs[cursor.getPosition()] = cv;
			cursor.moveToNext();
		}
		cursor.close();
		context.getContentResolver().bulkInsert(ProviderUris.EVENT_PROVIDER_URI, cvs);
	}

	/**
	 * Updates the status into the EventStream source table.
	 * 
	 * @param context The context
	 * @param status The status to update
	 */
	public static void updateStatus(Context context, String status) {
		Cursor cursor = null;
		try {
			final ContentValues cv = new ContentValues();
			cv.put(SourceColumns.CURRENT_STATUS, status);
			cv.put(SourceColumns.STATUS_TIMESTAMP, System.currentTimeMillis());

			final ContentResolver cr = context.getContentResolver();
			cursor = cr.query(ProviderUris.SOURCE_PROVIDER_URI, null, null, null, null);
			if (null != cursor && cursor.moveToNext() && 0 < cursor.getCount()) {
				cr.update(ProviderUris.SOURCE_PROVIDER_URI, cv, null, null);
			}
		} finally {
			if (null != cursor) {
				cursor.close();
			}
		}
	}
}
