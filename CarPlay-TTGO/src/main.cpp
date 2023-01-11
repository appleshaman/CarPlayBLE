#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

#define SERVICE_UUID "4fafc201-1fb5-459e-8fcc-c5c9c331914b"

#define DESTINATION_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define ETA_UUID "ca83fac2-2438-4d14-a8ae-a01831c0cf0d"
#define DIRECTION_UUID "dfc521a5-ce89-43bd-82a0-28a37f3a2b5a"
#define ETA_MINUTES_UUID "563c187d-ff17-4a6a-8061-ca9b7b70b2b0"
#define DISTANCE_UUID "8bf31540-eb0d-476c-b233-f514678d2afb"
#define DIRECTION_PRECISE_UUID "a602346d-c2bb-4782-8ea7-196a11f85113"
BLEServer *pServer;
BLEService *pService;

void setup()
{
    Serial.begin(115200);
    Serial.println("Starting BLE work!");

    BLEDevice::init("Navigator");

    pServer = BLEDevice::createServer();
    pService = pServer->createService(SERVICE_UUID);

    BLECharacteristic *destinationCharacteristic = pService->createCharacteristic(
        DESTINATION_UUID,
        BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_WRITE);
    BLECharacteristic *etaCharacteristic = pService->createCharacteristic(
        ETA_UUID,
        BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_WRITE);
    BLECharacteristic *directionCharacteristic = pService->createCharacteristic(
        DIRECTION_UUID,
        BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_WRITE);
    BLECharacteristic *etaInMinutesCharacteristic = pService->createCharacteristic(
        ETA_MINUTES_UUID,
        BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_WRITE);
    BLECharacteristic *distanceCharacteristic = pService->createCharacteristic(
        DISTANCE_UUID,
        BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_WRITE);
    BLECharacteristic *directionPreciseCharacteristic = pService->createCharacteristic(
        DIRECTION_PRECISE_UUID,
        BLECharacteristic::PROPERTY_READ |
            BLECharacteristic::PROPERTY_WRITE);

    destinationCharacteristic->setValue("destination");
    etaCharacteristic->setValue("eta");
    directionCharacteristic->setValue("direction");
    etaInMinutesCharacteristic->setValue("ETA_InMinutes");
    distanceCharacteristic->setValue("distance");
    directionPreciseCharacteristic->setValue("directionPrecise");

    pService->start();
    // BLEAdvertising *pAdvertising = pServer->getAdvertising();  // this still is working for backward compatibility
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_UUID);
    pAdvertising->setScanResponse(true);
    pAdvertising->setMinPreferred(0x06); // functions that help with iPhone connections issue
    pAdvertising->setMinPreferred(0x12);

    BLEDevice::startAdvertising();
    Serial.println("Characteristic defined! Now you can read it in your phone!");
}

void loop()
{
    // put your main code here, to run repeatedly:
    delay(2000);
    std::string a = pService->getCharacteristic(DESTINATION_UUID)->getValue();
    const char *b = a.c_str();
    Serial.println(b);
    a = pService->getCharacteristic(ETA_UUID)->getValue();
    const char *c = a.c_str();
    Serial.println(c);
    a = pService->getCharacteristic(DIRECTION_UUID)->getValue();
    const char *d = a.c_str();
    Serial.println(d);
    a = pService->getCharacteristic(ETA_MINUTES_UUID)->getValue();
    const char *e = a.c_str();
    Serial.println(e);
    a = pService->getCharacteristic(DISTANCE_UUID)->getValue();
    const char *f = a.c_str();
    Serial.println(f);
    a = pService->getCharacteristic(DIRECTION_PRECISE_UUID)->getValue();
    const char *g = a.c_str();
    Serial.println(g);
    Serial.println("");
}
