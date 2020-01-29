package wibmo.com.main;

import android.arch.persistence.room.Room;
import android.content.Context;

import java.util.List;

import wibmo.com.main.Database.AppDatabase;
import wibmo.com.main.Database.AppExecutors;
import wibmo.com.main.Database.NotificationAlert;


public class ApplicationSingleton {

    private static ApplicationSingleton mInstance;
    Context application;
    private String TAG = ApplicationSingleton.class.getSimpleName();
    private AppDatabase appDatabase;
    private AppExecutors appExecutors = new AppExecutors();




    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new ApplicationSingleton();

            mInstance.onCreate(context);
        }
    }


    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

    public AppDatabase getAppDatabase() {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(application,
                    AppDatabase.class, "trident-db").fallbackToDestructiveMigration().build();
        }
        return appDatabase;
    }

    public List<NotificationAlert> mNotificationAlerts;


    public void onCreate(Context context) {

        application = context;

    }


    public static synchronized ApplicationSingleton getInstance() {
        return mInstance;
    }

    public List getNotitifcationList() {
        return ApplicationSingleton.getInstance().getAppDatabase()
                .mNotificationAlertDao()
                .getAll().getValue();
    }

    public void setmNotificationAlerts(List<NotificationAlert> mNotificationAlerts) {
        this.mNotificationAlerts = mNotificationAlerts;
    }

    public Context getApplicationContext() {
        return application;
    }
}
