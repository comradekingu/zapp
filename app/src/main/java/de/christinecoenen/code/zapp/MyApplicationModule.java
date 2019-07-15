package de.christinecoenen.code.zapp;

import android.app.Application;
import android.content.Context;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import de.christinecoenen.code.zapp.app.livestream.ui.detail.ChannelDetailActivity;
import de.christinecoenen.code.zapp.app.livestream.ui.list.ChannelListFragment;
import de.christinecoenen.code.zapp.model.IChannelList;
import de.christinecoenen.code.zapp.model.ISortableChannelList;
import de.christinecoenen.code.zapp.model.json.SortableJsonChannelList;

@Module
abstract class MyApplicationModule {

	@Binds
	abstract Application application(ZappApplicationBase app);

	@Provides
	@Named("application-context")
	static Context context(Application app) {
		return app;
	}

	@Provides
	static IChannelList channelList(@Named("application-context") Context context) {
		return new SortableJsonChannelList(context);
	}

	@Provides
	static ISortableChannelList sortableChannelList(@Named("application-context") Context context) {
		return new SortableJsonChannelList(context);
	}


	@ContributesAndroidInjector
	abstract ChannelDetailActivity contributeActivityInjector();


	@ContributesAndroidInjector
	abstract ChannelListFragment channelListFragment();
}
