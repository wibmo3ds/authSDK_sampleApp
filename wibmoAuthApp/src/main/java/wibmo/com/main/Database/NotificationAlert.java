package wibmo.com.main.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "notification_alert")
public class NotificationAlert {

  @PrimaryKey(autoGenerate = true)
  private int id;
  private int poolid;
  private String title;
  private String message;
  private String amount;
  private String theme;
  private int imagetype;
  private String expiry;
  private String imageurl;
  private String type;
  private String deepLink;
  private String notificationId;
  private String txnId;
  private String requestrTxnId;
  private String notifyTime;

  public NotificationAlert(){

  }

  public NotificationAlert(int poolid,
                           String title,
                           String message,
                           String theme,
                           String amount,
                           String expiry,
                           String notificationTime,
                           String imageurl,
                           String type,
                           String deepLink,
                           String notificationId,
                           String txnId) {
    this.poolid = poolid;
    this.title = title;
    this.message = message;
    this.amount = amount;
    this.theme = theme;
    this.imagetype = imagetype;
    this.expiry = expiry;
    this.notifyTime = notificationTime;
    this.imageurl = imageurl;
    this.type = type;
    this.deepLink = deepLink;
    this.notificationId = notificationId;
    this.txnId = txnId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getPoolid() {
    return poolid;
  }

  public void setPoolid(int poolid) {
    this.poolid = poolid;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getTheme() {
    return theme;
  }

  public void setTheme(String theme) {
    this.theme = theme;
  }

  public int getImagetype() {
    return imagetype;
  }

  public void setImagetype(int imagetype) {
    this.imagetype = imagetype;
  }

  public String getExpiry() {
    return expiry;
  }

  public void setExpiry(String expiry) {
    this.expiry = expiry;
  }

  public String getImageurl() {
    return imageurl;
  }

  public void setImageurl(String imageurl) {
    this.imageurl = imageurl;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDeepLink() {
    return deepLink;
  }

  public void setDeepLink(String deepLink) {
    this.deepLink = deepLink;
  }
  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getNotificationId() {
    return notificationId;
  }

  public void setNotificationId(String notificationId) {
    this.notificationId = notificationId;
  }

  public String getTxnId() {
    return txnId;
  }

  public void setTxnId(String txnId) {
    this.txnId = txnId;
  }

  public String getRequestrTxnId() {
    return requestrTxnId;
  }

  public void setRequestrTxnId(String requestrTxnId) {
    this.requestrTxnId = requestrTxnId;
  }

  public String getNotifyTime() {
    return notifyTime;
  }

  public void setNotifyTime(String notifyTime) {
    this.notifyTime = notifyTime;
  }
}