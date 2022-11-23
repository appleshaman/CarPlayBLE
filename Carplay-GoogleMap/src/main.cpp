#include <Arduino.h>
/**
 * Demo：
 *    案例：广播数据，可扫描请求，不可连接
 *  
 * @author 单片机菜鸟
 * @date 2021/01/31
 */

#include <BLEDevice.h>
#include <BLEUtils.h>
#include "BLEAdvertising.h"

BLEAdvertising *pAdvertising;

void setup() {
  Serial.begin(9600);
  Serial.println("Advertising...");

  BLEDevice::init("Advertising");
  pAdvertising = BLEDevice::getAdvertising();
  // 不可连接 可扫描更多
  BLEAdvertisementData oScanResponseData = BLEAdvertisementData();
  oScanResponseData.setName("scanRsp");
  pAdvertising->setScanResponseData(oScanResponseData);
  pAdvertising->setAdvertisementType(ADV_TYPE_SCAN_IND);
  // Start advertising
  pAdvertising->start();
  Serial.println("Advertizing started...");
  delay(10000);
  //pAdvertising->stop();
}

void loop() {
  // put your main code here, to run repeatedly:
}


