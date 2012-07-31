package org.mariotaku.twidere.extension.timescape.util;

import org.mariotaku.twidere.extension.timescape.Constants;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils implements Constants {

	public static long[] getSelectedIdsInPreferences(Context context) {
		if (context == null) return new long[0];
		final SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		final String ids_string = preferences.getString(PREFERENCE_KEY_SELECTED_IDS, "");
		final String[] ids_string_array = ids_string.split(",");
		try {
			final int count = ids_string_array.length;
			final long[] ids = new long[count];
			for (int i = 0; i < count; i++) {
				ids[i] = Long.parseLong(ids_string_array[i]);
			}
			return ids;
		} catch (final NumberFormatException e) {

		}
		return new long[0];
	}
}
