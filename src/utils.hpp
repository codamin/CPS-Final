#ifndef UTIL
#define UTIL

#include <Arduino.h>
#include <AESLib.h>
#include <Servo.h>

typedef unsigned char BYTE;

#define MAX_SPLIT_SIZE 5
#define MAX_USERS_NUMBER 10
#define MOTOR_PIN 8
#define SERVO_OPEN_POSITION 0
#define SERVO_LOCK_POSITION 79
#define MSG_MAX_LEN 100

class User {
  public:
    User(String id_, String password_) {
      id = id_;
      password = password_;
    }

    User() {};

    String id;
    String password;
};

struct Tokens
{
  bool isOk;
  String token1;
  String token2;
  String token3;
  String token4;
};

Tokens authorize(String id, String cypher);
User addUser(String id, String password);
void removeUser(String id);
char* getPassword(String id);
void processCommand(String cmd);
char* string2ptr(String str);
BYTE* password2key(String password);
String* split(String str , char c);
void recv_cmd(Servo motor);
void lock(Servo motor);
void open(Servo motor);
void process_cmd(Tokens tokens, Servo motor, String id);
void set_admin(String id, String password);

#endif