package de.christinecoenen.code.zapp;

import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Component(modules = {AndroidInjectionModule.class, MyApplicationModule.class})
public interface MyApplicationComponent extends AndroidInjector<ZappApplicationBase> {

	@Component.Builder
	abstract class Builder extends AndroidInjector.Builder<ZappApplicationBase> {
	}

}
