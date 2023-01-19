#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <TFT_eSPI.h>
#include <Icons.h>
#include <string.h>
#include <sstream>

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

volatile long lastDebounceTime = 0;
volatile bool debounce = true;
volatile bool orientation = false;
void setupScreen();
void setupCharateristic();
void drawDirectionImage(const char *direction);
void buttonPressed();

void setup()
{
    Serial.begin(921600);
    Serial.println("Start!");
    setupScreen();

    BLEDevice::init("Navigator");

    pServer = BLEDevice::createServer();
    pService = pServer->createService(SERVICE_UUID);
    pinMode(GPIO_NUM_0, INPUT_PULLUP);
    attachInterrupt(GPIO_NUM_0, buttonPressed, FALLING);

    setupCharateristic();

    pService->start();

    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->addServiceUUID(SERVICE_UUID);
    pAdvertising->setScanResponse(true);
    pAdvertising->setMinPreferred(0x06); // functions that help with iPhone connections issue
    pAdvertising->setMinPreferred(0x12);

    BLEDevice::startAdvertising();
    Serial.println("Characteristic defined!");
}

void loop()
{

     if ((millis() - lastDebounceTime) > 100)
    {
        debounce = true; //debounce
    }


    delay(2000);
    tft.fillScreen(tft.color565(56, 178, 92)); // 0x38b25c

    if (pServer->getConnectedCount() == 0)
    {
        tft.pushImage(77, 10, 85, 85, NO_CONNECTION);
        tft.drawString("No Connection", 30, 95, 4);
        
    }
    else
    {
        tft.drawString("ETA:", 5, 5, 4);
        tft.drawString(pService->getCharacteristic(ETA_UUID)->getValue().c_str(), 70, 5, 4);

        tft.drawString(pService->getCharacteristic(ETA_MINUTES_UUID)->getValue().c_str(), 5, 30, 4);
        tft.drawString("left", 100, 30, 4);

        tft.drawString(pService->getCharacteristic(DISTANCE_UUID)->getValue().c_str(), 5, 55, 4);
        tft.drawString("left", 100, 55, 4);

        drawDirectionImage(pService->getCharacteristic(DIRECTION_PRECISE_UUID)->getValue().c_str());
        tft.drawString(pService->getCharacteristic(DIRECTION_DISTANCE_UUID)->getValue().c_str(), 175, 95, 4);

        tft.drawString(pService->getCharacteristic(DIRECTION_UUID)->getValue().c_str(), 5, 90, 4);

        tft.drawString(pService->getCharacteristic(DESTINATION_UUID)->getValue().c_str(), 5, 115, 2);
    }
}

void setupScreen()
{
    tft.init();
    tft.setRotation(3);
    
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
    directionPreciseCharacteristic->setValue("34");
}

void drawDirectionImage(const char *direction)
{
    std::string temp = direction;

    if (temp.compare("0") == 0)
    {
        tft.pushImage(155, 0, 85, 85, ARRIVE);
    }
    else if (temp.compare("1") == 0)
    {
        tft.pushImage(155, 0, 85, 85, ARRIVE_LEFT);
    }
    else if (temp.compare("2") == 0)
    {
        tft.pushImage(155, 0, 85, 85, ARRIVE_RIGHT);
    }
    else if (temp.compare("3") == 0)
    {
        tft.pushImage(155, 0, 85, 85, CONTINUE_LEFT);
    }
    else if (temp.compare("4") == 0)
    {
        tft.pushImage(155, 0, 85, 85, CONTINUE_RETURN);
    }
    else if (temp.compare("5") == 0)
    {
        tft.pushImage(155, 0, 85, 85, CONTINUE_RIGHT);
    }
    else if (temp.compare("6") == 0)
    {
        tft.pushImage(155, 0, 85, 85, CONTINUE_SLIGHT_LEFT);
    }
    else if (temp.compare("7") == 0)
    {
        tft.pushImage(155, 0, 85, 85, CONTINUE_SLIGHT_RIGHT);
    }
    else if (temp.compare("8") == 0)
    {
        tft.pushImage(155, 0, 85, 85, CONTINUE_STRAIGHT);
    }
    else if (temp.compare("9")==0)
    {
        tft.pushImage(155, 0, 85, 85, DEPART);
    }
    else if (temp.compare("10")==0)
    {
        tft.pushImage(155, 0, 85, 85, FORK);
    }
    else if (temp.compare("11")==0)
    {
        tft.pushImage(155, 0, 85, 85, POINTER);
    }
    else if (temp.compare("12")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_EXIT);
    }
    else if (temp.compare("13")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_EXIT_INVERTED);
    }
    else if (temp.compare("14")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_LEFT);
    }
    else if (temp.compare("15")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_LEFT_INVERTED);
    }
    else if (temp.compare("16")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_RIGHT);
    }
    else if (temp.compare("17")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_RIGHT_INVERTED);
    }
    else if (temp.compare("18")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_SHARP_LEFT);
    }
    else if (temp.compare("19")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_SHARP_LEFT_INVERTED);
    }
    else if (temp.compare("20")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_SHARP_RIGHT);
    }
    else if (temp.compare("21")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_SHARP_RIGHT_INVERTED);
    }
    else if (temp.compare("22")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_SLIGHT_LEFT);
    }
    else if (temp.compare("23")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_SLIGHT_LEFT_INVERTED);
    }
    else if (temp.compare("24")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_SLIGHT_RIGHT);
    }
    else if (temp.compare("25")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_SLIGHT_RIGHT_INVERTED);
    }
    else if (temp.compare("26")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_STRAIGHT);
    }
    else if (temp.compare("27")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_STRAIGHT_INVERTED);
    }
    else if (temp.compare("28")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_TOTAL);
    }
    else if (temp.compare("29")==0)
    {
        tft.pushImage(155, 0, 85, 85, ROTATORY_TOTAL_INVERTED);
    }
    else if (temp.compare("30")==0)
    {
        tft.pushImage(155, 0, 85, 85, SHARP_LEFT);
    }
    else if (temp.compare("31")==0)
    {
        tft.pushImage(155, 0, 85, 85, SHARP_RIGHT);
    }
    else if (temp.compare("32")==0)
    {
        tft.pushImage(155, 0, 85, 85, SLIGHT_LEFT);
    }
    else if (temp.compare("33")==0)
    {
        tft.pushImage(155, 0, 85, 85, SLIGHT_RIGHT);
    }
    else if (temp.compare("34")==0)
    {
        tft.pushImage(155, 0, 85, 85, UNKNOWN);
    }
}

void IRAM_ATTR buttonPressed(){
    
    if(debounce){
        lastDebounceTime = millis();
        if(orientation){
            tft.setRotation(1);
            orientation = false;
        }else{
            tft.setRotation(3);
            orientation = true;
        }
    }
}