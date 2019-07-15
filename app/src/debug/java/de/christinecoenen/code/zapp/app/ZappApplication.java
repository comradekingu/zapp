package de.christinecoenen.code.zapp.app;

import de.christinecoenen.code.zapp.ZappApplicationBase;
import timber.log.Timber;


public class ZappApplication extends ZappApplicationBase {

	@Override
	protected void initLogging() {
		Timber.plant(new Timber.DebugTree());
	}

}
