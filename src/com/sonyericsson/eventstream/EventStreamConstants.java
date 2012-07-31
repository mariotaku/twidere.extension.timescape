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

package com.sonyericsson.eventstream;

import android.net.Uri;

/**
 * This class defines constants to access the EventSream
 */
public interface EventStreamConstants {

	/**
	 * Configuration state of login
	 */
	public static interface ConfigState {
		/** Configured */
		public final static int CONFIGURED = 0;

		/** Not configured */
		public final static int NOT_CONFIGURED = 1;

		/** Configuration not needed */
		public final static int CONFIGURATION_NOT_NEEDED = 2;
	};

	/**
	 * Column definitions in the Event Stream event provider
	 */
	public static interface EventColumns {
		/** Event key */
		public final static String EVENT_KEY = "event_key";

		/** Friend ID */
		public final static String FRIEND_KEY = "friend_key";

		/** GEO data, not supported */
		public final static String GEODATA = "geodata";

		/** Image uri */
		public final static String IMAGE_URI = "image_uri";

		/** Message */
		public final static String MESSAGE = "message";

		/** Out going */
		public final static String OUTGOING = "outgoing";

		/** Personal */
		public final static String PERSONAL = "personal";

		/** Published time */
		public final static String PUBLISHED_TIME = "published_time";

		/** Source ID */
		public final static String SOURCE_ID = "source_id";

		/** Status icon uri */
		public final static String STATUS_ICON_URI = "status_icon_uri";

		/** Title */
		public final static String TITLE = "title";
	}

	/**
	 * Intents sent by the EventStream
	 */
	public static interface Intents {
		/** Register plugins request intent */
		public final static String REGISTER_PLUGINS_REQUEST_INTENT = "com.sonyericsson.eventstream.REGISTER_PLUGINS";

		/** Refresh request intent */
		public final static String REFRESH_REQUEST_INTENT = "com.sonyericsson.eventstream.REFRESH_REQUEST";

		/** View event intent */
		public final static String VIEW_EVENT_INTENT = "com.sonyericsson.eventstream.VIEW_EVENT_DETAIL";

		/** Status update intent */
		public final static String STATUS_UPDATE_INTENT = "com.sonyericsson.eventstream.SEND_STATUS_UPDATE";

		/** Extra status update message */
		public final static String EXTRA_STATUS_UPDATE_MESSAGE = "new_status_message";
	}

	/**
	 * Column definitions in the Event Stream plugin provider
	 */
	public static interface PluginColumns {
		/** API version */
		public final static String API_VERSION = "api_version";

		/** Configuration activity */
		public final static String CONFIGURATION_ACTIVITY = "config_activity";

		/** Configuration state */
		public final static String CONFIGURATION_STATE = "config_state";

		/** Configuration text */
		public final static String CONFIGURATION_TEXT = "config_text";

		/** Icon uri */
		public final static String ICON_URI = "icon_uri";

		/** Name */
		public final static String NAME = "name";

		/** Plugin key */
		public final static String PLUGIN_KEY = "plugin_key";

		/** Status support */
		public final static String STATUS_SUPPORT = "status_support";

		/** Status text max length */
		public final static String STATUS_TEXT_MAX_LENGTH = "status_text_max_length"; // Not
		// supported
	}

	/**
	 * ProviderUris definitions the uri to access the EventStream
	 */
	public static interface ProviderUris {
		/** Friend provider uri */
		public final static Uri FRIEND_PROVIDER_URI = Uri.parse("content://com.sonyericsson.eventstream/friends");

		/** Event provider uri */
		public final static Uri EVENT_PROVIDER_URI = Uri.parse("content://com.sonyericsson.eventstream/events");

		/** Source provider uri */
		public final static Uri SOURCE_PROVIDER_URI = Uri.parse("content://com.sonyericsson.eventstream/sources");

		/** Plugin provider uri */
		public final static Uri PLUGIN_PROVIDER_URI = Uri.parse("content://com.sonyericsson.eventstream/plugins");
	}

	/**
	 * Column definitions in the Event Stream source provider
	 */
	public static interface SourceColumns {
		/** ID column */
		public final static String ID = "_id";

		/** Current status */
		public final static String CURRENT_STATUS = "current_status"; // Not
																		// supported

		/** Enabled */
		public final static String ENABLED = "enabled";

		/** Icon uri */
		public final static String ICON_URI = "icon_uri";

		/** Name */
		public final static String NAME = "name";

		/** Status time stamp */
		public final static String STATUS_TIMESTAMP = "status_timestamp"; // Not
																			// supported
	}

	/**
	 * Source state.
	 */
	public static interface SourceState {
		/** Not enabled */
		public final static int NOT_ENABLED = 0;

		/** Enabled */
		public final static int ENABLED = 1;
	}

	/**
	 * Support for status update
	 */
	public static interface StatusSupport {
		/** Has support false */
		public final static int HAS_SUPPORT_FALSE = 0;

		/** Has support true */
		public final static int HAS_SUPPORT_TRUE = 1;
	}
}