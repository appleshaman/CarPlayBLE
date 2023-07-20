#include <string.h>
#ifndef Information_h
#define Information_h

class Information {
private:
    std::string mInformation;
    bool mEdited;

public:
    Information() : mEdited(true) {} 

    Information(const std::string& initialValue) : mInformation(initialValue), mEdited(true) {}

    void setBoolean(bool value) {
        mEdited = value;
    }

    bool getBoolean() const {
        return mEdited;
    }

    void setString(const std::string& value) {
        mInformation = value;
        mEdited = true;
    }

    const char* getString() const {
    return mInformation.c_str();
    }
};
#endif