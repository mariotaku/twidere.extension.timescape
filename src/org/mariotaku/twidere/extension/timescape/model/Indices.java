package org.mariotaku.twidere.extension.timescape.model;

import org.mariotaku.twidere.provider.TweetStore.Statuses;

import android.database.Cursor;

public final class Indices {

	public final int id_idx, name_idx, text_plain_idx, timestamp_idx, status_id_idx, profile_image_url_idx,
			account_id_idx, user_id_idx;

	public Indices(Cursor cursor) {
		id_idx = cursor.getColumnIndex(Statuses._ID);
		name_idx = cursor.getColumnIndex(Statuses.NAME);
		text_plain_idx = cursor.getColumnIndex(Statuses.TEXT_PLAIN);
		timestamp_idx = cursor.getColumnIndex(Statuses.STATUS_TIMESTAMP);
		status_id_idx = cursor.getColumnIndex(Statuses.STATUS_ID);
		profile_image_url_idx = cursor.getColumnIndex(Statuses.PROFILE_IMAGE_URL);
		account_id_idx = cursor.getColumnIndex(Statuses.ACCOUNT_ID);
		user_id_idx = cursor.getColumnIndex(Statuses.USER_ID);
	}
}