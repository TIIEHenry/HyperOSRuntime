package tiiehenry.xp.updatehooker;

import android.app.Application;
import android.content.Context;

public class ModuleApplication extends Application {

    private static Context app;

    public static Context getApp() {
        return app;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        app = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
