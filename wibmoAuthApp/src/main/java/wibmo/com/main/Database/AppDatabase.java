package wibmo.com.main.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = { NotificationAlert.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NotificationAlertDao mNotificationAlertDao();
}
