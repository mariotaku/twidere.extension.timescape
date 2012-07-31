package org.mariotaku.twidere.extension.timescape;

import org.mariotaku.twidere.extension.timescape.util.EventStreamAccessor;
import org.mariotaku.twidere.util.ServiceInterface;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

public class ExtensionApplication extends Application {

	private LoadStatusesTask mLoadStatusesTask;
	private ServiceInterface mService;

	public ServiceInterface getServiceInterface() {
		if (mService != null) return mService;
		return mService = ServiceInterface.getInstance(this);
	}

	public void loadStatuses() {
		if (mLoadStatusesTask != null) {
			mLoadStatusesTask.cancel(true);
		}
		mLoadStatusesTask = new LoadStatusesTask(this);
		mLoadStatusesTask.execute();
	}

	static class LoadStatusesTask extends AsyncTask<Void, Void, Void> {

		final Context context;

		public LoadStatusesTask(Context context) {
			this.context = context;
		}

		@Override
		protected Void doInBackground(Void... args) {
			EventStreamAccessor.clearData(context);
			EventStreamAccessor.loadHomeTimelineToEventStream(context);
			return null;
		}

	}

}
