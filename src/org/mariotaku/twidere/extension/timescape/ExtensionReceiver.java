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

import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.Twidere;
import org.mariotaku.twidere.extension.timescape.util.EventStreamAccessor;
import org.mariotaku.twidere.extension.timescape.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.sonyericsson.eventstream.EventStreamConstants;
import com.sonyericsson.eventstream.EventStreamConstants.ConfigState;
import com.sonyericsson.eventstream.EventStreamConstants.EventColumns;

/**
 * Listen for broadcasts from Event Stream.
 */
public class ExtensionReceiver extends BroadcastReceiver implements Constants {

	@Override
	public void onReceive(Context context, Intent intent) {
		final Context app_context = context.getApplicationContext();
		final ExtensionApplication application = app_context instanceof ExtensionApplication ? (ExtensionApplication) app_context
				: null;
		if (application == null) return;
		final String action = intent.getAction();
		final long[] selected_ids = Utils.getSelectedIdsInPreferences(context);
		if (EventStreamConstants.Intents.REGISTER_PLUGINS_REQUEST_INTENT.equals(action)) {
			/**
			 * For initial app installation or re-installation during emulator
			 * start,this intent is being called. A null state should be sent to
			 * the EventStreamAccessor to prevent overwriting the current state
			 * of the application during installation/emulator reboot
			 */
			EventStreamAccessor.insertOrUpdatePlugin(context, selected_ids.length > 0 ? ConfigState.CONFIGURED
					: ConfigState.NOT_CONFIGURED);
			EventStreamAccessor.insertOrUpdateSource(context);
			application.loadStatuses();
		} else if (EventStreamConstants.Intents.REFRESH_REQUEST_INTENT.equals(action)) {
			application.refreshStatuses();
		} else if (EventStreamConstants.Intents.VIEW_EVENT_INTENT.equals(action)) {
			openStatus(context, intent);
		} else if (EventStreamConstants.Intents.STATUS_UPDATE_INTENT.equals(action)) {
			final String status = intent.getStringExtra(EventStreamConstants.Intents.EXTRA_STATUS_UPDATE_MESSAGE);
			application.updateStatus(status);
		} else if (Twidere.BROADCAST_HOME_TIMELINE_REFRESHED.equals(action)) {
			application.loadStatuses();
		}
	}

	
	private void openStatus(Context context, Intent i) {
		final Bundle bundle = i.getExtras();
		if (bundle == null) return;
		final String event_key = bundle.getString(EventColumns.EVENT_KEY);
		if (event_key == null) return;
		final String[] keys = event_key.split(";");
		if (keys == null || keys.length != 2) return;
		final Uri.Builder builder = new Uri.Builder();
		builder.scheme(SCHEME_TWIDERE);
		builder.authority(AUTHORITY_STATUS);
		builder.appendQueryParameter(QUERY_PARAM_ACCOUNT_ID, keys[0]);
		builder.appendQueryParameter(QUERY_PARAM_STATUS_ID, keys[1]);
		final Intent intent = new Intent(Intent.ACTION_VIEW, builder.build());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}
