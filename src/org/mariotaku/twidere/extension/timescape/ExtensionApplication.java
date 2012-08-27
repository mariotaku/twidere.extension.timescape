package org.mariotaku.twidere.extension.timescape;

import org.mariotaku.twidere.extension.timescape.util.EventStreamAccessor;
import org.mariotaku.twidere.extension.timescape.util.Utils;
import org.mariotaku.twidere.util.ServiceInterface;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

public class ExtensionApplication extends Application {

	private AsyncTask<Void, Void, Void> mLoadStatusesTask, mRefreshStatusesTask, mUpdateStatusTask;
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
	
	public void refreshStatuses() {
		if (mRefreshStatusesTask != null) {
			mRefreshStatusesTask.cancel(true);
		}
		mRefreshStatusesTask = new RefreshStatusesTask(this);
		mRefreshStatusesTask.execute();
	}
	
	public void updateStatus(String status) {
		if (mUpdateStatusTask != null) {
			mUpdateStatusTask.cancel(true);
		}
		mUpdateStatusTask = new UpdateStatusTask(this, status);
		mUpdateStatusTask.execute();
	}

	static class RefreshStatusesTask extends AsyncTask<Void, Void, Void> {

		final ServiceInterface service;
		final Context context;

		public RefreshStatusesTask(ExtensionApplication application) {
			context = application;
			service = application.getServiceInterface();
		}

		@Override
		protected Void doInBackground(Void... args) {
			service.waitForService();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			final long[] selected_ids = Utils.getSelectedIdsInPreferences(context);
			service.getHomeTimeline(selected_ids, null);
			super.onPostExecute(result);
		}
	}
	
	static class UpdateStatusTask extends AsyncTask<Void, Void, Void> {

		final ServiceInterface service;
		final Context context;
		final String status;

		public UpdateStatusTask(ExtensionApplication application, String status) {
			context = application;
			service = application.getServiceInterface();
			this.status = status;
		}

		@Override
		protected Void doInBackground(Void... args) {
			service.waitForService();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			final long[] selected_ids = Utils.getSelectedIdsInPreferences(context);
			service.updateStatus(selected_ids, status, null, null, -1, false);
			EventStreamAccessor.updateStatus(context, status);
			super.onPostExecute(result);
		}
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
