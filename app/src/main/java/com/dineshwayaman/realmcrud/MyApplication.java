package com.dineshwayaman.realmcrud;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.mongodb.sync.SyncConfiguration;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        Realm.init(this);

    }
}
