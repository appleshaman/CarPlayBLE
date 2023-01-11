package com.example.carplay_android.javabeans;

public class JavaBeanFilters {
    static private final String FILTER_DEVICE_USED = "DeviceUsed";
    static private final String FILTER_DEVICE_LIST = "DeviceList";
    static private final String FILTER_BT_STATUS = "BTStatus";
    static private final String FILTER_BLE_STATUS = "BleStatus";
    static private final String FILTER_NOTIFICATION_STATUS = "NotificationStatus";
    static private final String FILTER_DEVICE_STATUS = "DeviceStatus";

    public static String getFILTER_DEVICE_USED() {
        return FILTER_DEVICE_USED;
    }

    public static String getFILTER_BT_STATUS() {
        return FILTER_BT_STATUS;
    }

    public static String getFILTER_BLE_STATUS() {
        return FILTER_BLE_STATUS;
    }

    public static String getFILTER_NOTIFICATION_STATUS() {
        return FILTER_NOTIFICATION_STATUS;
    }

    public static String getFILTER_DEVICE_STATUS() {
        return FILTER_DEVICE_STATUS;
    }

    public static String getFilterDeviceList() {
        return FILTER_DEVICE_LIST;
    }
}
