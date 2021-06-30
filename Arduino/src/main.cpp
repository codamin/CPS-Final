#include <utils.hpp>
#include <Arduino.h>

Servo motor;

void setup() {
  motor.attach(MOTOR_PIN);
  motor.write(90);
  Serial.begin(9600);

  set_admin("hoenza", "1010101000000000");
  User user1 = addUser("hoenza", "1010101000000000");
}

void loop() {
  recv_cmd(motor);
}