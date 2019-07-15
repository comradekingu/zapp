package de.christinecoenen.code.zapp;

import android.app.Activity;
import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.christinecoenen.code.zapp.app.settings.repository.SettingsRepository;
import de.christinecoenen.code.zapp.utils.system.NotificationHelper;


public abstract class ZappApplicationBase extends Application implements HasActivityInjector, HasSupportFragmentInjector {

	@Inject
	DispatchingAndroidInjector<Activity> activityInjector;

	@Inject
	DispatchingAndroidInjector<Fragment> fragmentInjector;

	@Override
	public void onCreate() {
		super.onCreate();
		DaggerMyApplicationComponent.builder().create(this).inject(this);

		initLogging();

		NotificationHelper.createBackgroundPlaybackChannel(this);

		SettingsRepository settingsRepository = new SettingsRepository(this);
		AppCompatDelegate.setDefaultNightMode(settingsRepository.getUiMode());
	}

	protected abstract void initLogging();

	@Override
	public DispatchingAndroidInjector<Activity> activityInjector() {
		return activityInjector;
	}

	@Override
	public AndroidInjector<Fragment> fragmentInjector() {
		return fragmentInjector;
	}
}
