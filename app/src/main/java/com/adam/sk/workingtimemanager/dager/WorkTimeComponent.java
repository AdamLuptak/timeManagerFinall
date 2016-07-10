package com.adam.sk.workingtimemanager.dager;

import com.adam.sk.workingtimemanager.HomeFragment;
import com.adam.sk.workingtimemanager.MainActivity;
import com.adam.sk.workingtimemanager.Setup;
import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.service.LocationService;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {WorkTimeModule.class})
public interface WorkTimeComponent {
    void inject(HomeFragment homeFragment);

    void inject(Setup setup);

    void inject(MainActivity mainActivity);

    void inject(LocationService service);

    TimeController provideWorTimeController();
}
