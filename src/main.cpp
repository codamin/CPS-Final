#include <utils.hpp>
#include <Arduino.h>

Servo motor;

void setup() {
  // Serial.begin(9600);
  motor.attach(MOTOR_PIN);
  motor.write(90);
  Serial.begin(9600);

  // User user2 = addUser("02:00:00:00:00:00", "1234567800000000");
  // char* m2 = string2ptr(String("lock") + String("#") + user2.id);
  // aes128_enc_single((uint8_t *)(user2.password).c_str(), m2);
  // String* plain_text_splitted2 = authorize(user2.id, String(m2));
  // process_cmd(plain_text_splitted2, motor);
}

void loop() {
  recv_cmd(motor);
}