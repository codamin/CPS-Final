#include <utils.hpp>
#include <Arduino.h>

Servo motor;

void setup() {
  motor.attach(MOTOR_PIN);
  motor.write(90);
  Serial.begin(9600);

  set_admin("hoenza", "8586858600000000");
  User user1 = addUser("hoenza", "8586858600000000");
  User user2 = addUser("03:00:00:00:00:00", "1234567800000000");
}

void loop() {
  recv_cmd(motor);
}