#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <TFT_eSPI.h>
#include <Icons.h>
#include <string.h>

#define SERVICE_UUID "4fafc201-1fb5-459e-8fcc-c5c9c331914b"

#define DESTINATION_UUID "beb5483e-36e1-4688-b7f5-ea07361b26a8"
#define ETA_UUID "ca83fac2-2438-4d14-a8ae-a01831c0cf0d"
#define DIRECTION_UUID "dfc521a5-ce89-43bd-82a0-28a37f3a2b5a"
#define DIRECTION_DISTANCE_UUID "0343ff39-994e-481b-9136-036dabc02a0b"
#define ETA_MINUTES_UUID "563c187d-ff17-4a6a-8061-ca9b7b70b2b0"
#define DISTANCE_UUID "8bf31540-eb0d-476c-b233-f514678d2afb"
#define DIRECTION_PRECISE_UUID "a602346d-c2bb-4782-8ea7-196a11f85113"
BLEServer *pServer;
BLEService *pService;

TFT_eSPI tft = TFT_eSPI();

void setupScreen();
void setupCharateristic();
void drawDirectionImage(const char *direction);

void setup()
{
    Serial.begin(921600);
    Serial.println("Start!");
    setupScreen();

    BLEDevice::init("Navigator");

    pServer = BLEDevice::createServer();
    pService = pServer->createService(SERVICE_UUID);

    setupCharateristic();

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

    // pService->getCharacteristic(DESTINATION_UUID)->getValue();

    // pService->getCharacteristic(DIRECTION_UUID)->getValue();// direction may contains two information

    if (pServer->getConnectedCount() == 1)
    {

        tft.pushImage(77, 10, 85, 85, NO_CONNECTION);
        tft.drawString("No Connection", 30, 95, 4);
        Serial.println(pServer->getConnectedCount());
    }
    else
    {
        tft.drawString("ETA:", 5, 5, 4);
        tft.drawString(pService->getCharacteristic(ETA_UUID)->getValue().c_str(), 65, 5, 4);

        tft.drawString(pService->getCharacteristic(ETA_MINUTES_UUID)->getValue().c_str(), 5, 30, 4);
        tft.drawString("mins left", 40, 30, 4);

        tft.drawString(pService->getCharacteristic(DISTANCE_UUID)->getValue().c_str(), 5, 55, 4);
        tft.drawString("left", 100, 55, 4);

        drawDirectionImage(pService->getCharacteristic(DIRECTION_PRECISE_UUID)->getValue().c_str());
        tft.drawString(pService->getCharacteristic(DIRECTION_DISTANCE_UUID)->getValue().c_str(), 175, 95, 4);
        //  tft.drawString(b, 10, 40, 4);
        //  tft.drawString(c, 10, 10, 4);
        //  tft.drawString(d, 30, 60, 4);
        //  tft.drawString(e, 10, 20, 4);
        //  tft.drawString(f, 50, 10, 4);
        //  tft.drawString(g, 10, 0, 4);
    }
}

void setupScreen()
{
    tft.init();
    tft.setRotation(1);
    tft.fillScreen(tft.color565(56, 178, 92)); // 0x38b25c
    tft.setTextColor(TFT_WHITE);
    tft.setSwapBytes(true);
}

void setupCharateristic()
{
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
    BLECharacteristic *directionDistanceCharacteristic = pService->createCharacteristic(
        DIRECTION_DISTANCE_UUID,
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
    etaCharacteristic->setValue("00:00");
    directionCharacteristic->setValue("direction");
    directionDistanceCharacteristic->setValue("N/A");
    etaInMinutesCharacteristic->setValue("00 mins");
    distanceCharacteristic->setValue("100 km");
    directionPreciseCharacteristic->setValue("DEPART");
}

void drawDirectionImage(const char *direction)
{
    std::map<const char *, int> directionSet = {
        {"ARRIVE", 0},
        {"ARRIVE_LEFT", 1},
        {"ARRIVE_RIGHT", 2},
        {"CONTINUE_LEFT", 3},
        {"CONTINUE_RETURN", 4},
        {"CONTINUE_RIGHT", 5},
        {"CONTINUE_SLIGHT_LEFT", 6},
        {"CONTINUE_SLIGHT_RIGHT", 7},
        {"CONTINUE_STRAIGHT", 8},
        {"DEPART", 9},
        {"FORK", 10},
        {"POINTER", 11},
        {"ROTATORY_EXIT", 12},
        {"ROTATORY_EXIT_INVERTED", 13},
        {"ROTATORY_LEFT", 14},
        {"ROTATORY_LEFT_INVERTED", 15},
        {"ROTATORY_RIGHT", 16},
        {"ROTATORY_RIGHT_INVERTED", 17},
        {"ROTATORY_SHARP_LEFT", 18},
        {"ROTATORY_SHARP_LEFT_INVERTED", 19},
        {"ROTATORY_SHARP_RIGHT", 20},
        {"ROTATORY_SHARP_RIGHT_INVERTED", 21},
        {"ROTATORY_SLIGHT_LEFT", 22},
        {"ROTATORY_SLIGHT_LEFT_INVERTED", 23},
        {"ROTATORY_SLIGHT_RIGHT", 24},
        {"ROTATORY_SLIGHT_RIGHT_INVERTED", 25},
        {"ROTATORY_STRAIGHT", 26},
        {"ROTATORY_STRAIGHT_INVERTED", 27},
        {"ROTATORY_TOTAL", 28},
        {"ROTATORY_TOTAL_INVERTED", 29},
        {"SHARP_LEFT", 30},
        {"SHARP_RIGHT", 31},
        {"SLIGHT_LEFT", 32},
        {"SLIGHT_RIGHT", 33},
        {"UNKNOWN", 34}, // special case
    };
    const char* temp = "UNKNOWN";
    int caseValue = directionSet[temp];
    Serial.println(direction);
    Serial.println(caseValue);
    switch (caseValue)
    {
    case 0:
        tft.pushImage(155, 0, 85, 85, ARRIVE);
        break;
    case 1:
        tft.pushImage(155, 0, 85, 85, ARRIVE_LEFT);
        break;
    case 2:
        tft.pushImage(155, 0, 85, 85, ARRIVE_RIGHT);
        break;
    case 3:
        tft.pushImage(155, 0, 85, 85, CONTINUE_LEFT);
        break;
    case 4:
        tft.pushImage(155, 0, 85, 85, CONTINUE_RETURN);
        break;
    case 5:
        tft.pushImage(155, 0, 85, 85, CONTINUE_RIGHT);
        break;
    case 6:
        tft.pushImage(155, 0, 85, 85, CONTINUE_SLIGHT_LEFT);
        break;
    case 7:
        tft.pushImage(155, 0, 85, 85, CONTINUE_SLIGHT_RIGHT);
        break;
    case 8:
        tft.pushImage(155, 0, 85, 85, CONTINUE_STRAIGHT);
        break;
    case 9:
        tft.pushImage(155, 0, 85, 85, DEPART);
        break;
    case 10:
        tft.pushImage(155, 0, 85, 85, FORK);
        break;
    case 11:
        tft.pushImage(155, 0, 85, 85, POINTER);
        break;
    case 12:
        tft.pushImage(155, 0, 85, 85, ROTATORY_EXIT);
        break;
    case 13:
        tft.pushImage(155, 0, 85, 85, ROTATORY_EXIT_INVERTED);
        break;
    case 14:
        tft.pushImage(155, 0, 85, 85, ROTATORY_LEFT);
        break;
    case 15:
        tft.pushImage(155, 0, 85, 85, ROTATORY_LEFT_INVERTED);
        break;
    case 16:
        tft.pushImage(155, 0, 85, 85, ROTATORY_RIGHT);
        break;
    case 17:
        tft.pushImage(155, 0, 85, 85, ROTATORY_RIGHT_INVERTED);
        break;
    case 18:
        tft.pushImage(155, 0, 85, 85, ROTATORY_SHARP_LEFT);
        break;
    case 19:
        tft.pushImage(155, 0, 85, 85, ROTATORY_SHARP_LEFT_INVERTED);
        break;
    case 20:
        tft.pushImage(155, 0, 85, 85, ROTATORY_SHARP_RIGHT);
        break;
    case 21:
        tft.pushImage(155, 0, 85, 85, ROTATORY_SHARP_RIGHT_INVERTED);
        break;
    case 22:
        tft.pushImage(155, 0, 85, 85, ROTATORY_SLIGHT_LEFT);
        break;
    case 23:
        tft.pushImage(155, 0, 85, 85, ROTATORY_SLIGHT_LEFT_INVERTED);
        break;
    case 24:
        tft.pushImage(155, 0, 85, 85, ROTATORY_SLIGHT_RIGHT);
        break;
    case 25:
        tft.pushImage(155, 0, 85, 85, ROTATORY_SLIGHT_RIGHT_INVERTED);
        break;
    case 26:
        tft.pushImage(155, 0, 85, 85, ROTATORY_STRAIGHT);
        break;
    case 27:
        tft.pushImage(155, 0, 85, 85, ROTATORY_STRAIGHT_INVERTED);
        break;
    case 28:
        tft.pushImage(155, 0, 85, 85, ROTATORY_TOTAL);
        break;
    case 29:
        tft.pushImage(155, 0, 85, 85, ROTATORY_TOTAL_INVERTED);
        break;
    case 30:
        tft.pushImage(155, 0, 85, 85, SHARP_LEFT);
        break;
    case 31:
        tft.pushImage(155, 0, 85, 85, SHARP_RIGHT);
        break;
    case 32:
        tft.pushImage(155, 0, 85, 85, SLIGHT_LEFT);
        break;
    case 33:
        tft.pushImage(155, 0, 85, 85, SLIGHT_RIGHT);
        break;
    case 34:
        tft.pushImage(155, 0, 85, 85, UNKNOWN);
        Serial.println("YES");
        break;
    }
}
