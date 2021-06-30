#include <utils.hpp>

char input;
String message = "";
int msg_idx = 0;
int read_step = 0;
int users_size = 0; 
User users[MAX_USERS_NUMBER];
User admin_user;

void open(Servo motor) {
  motor.write(180);
}

void lock(Servo motor) {
  for (int pos = 0; pos <= 90; pos += 1) {
    motor.write(pos);
    delay(3);
  }
}

void set_admin(String id, String password) {
  admin_user = User(id, password);
}

Tokens authorize(String id, String cypher) {
  Tokens tokens;

  char* password = getPassword(id);
  if(!password) {
      Serial.println("user with this id not found");
      tokens.isOk = 0;
      return tokens;
  }
  char* cypher_cstr = string2ptr(cypher);
  aes128_dec_multiple((uint8_t *)password, cypher_cstr, strlen(cypher_cstr));

  String token1, token2, token3, token4;

  char* token = strtok(cypher_cstr, "#");

  token1 = String(token);
  token2 = String(strtok(NULL, "#"));
  if((token1 == "add") | (token1 == "remove")) {
    token3 = String(strtok(NULL, "#"));
  }
  if(token1 == "add") {
    token4 = String(strtok(NULL, "#"));
  }

  delete[] cypher_cstr;

  if(token2 != id) {
      Serial.println("Wrong Password: user not authorized");
      tokens.isOk=0;
      return tokens;
  }
  else {
      tokens.isOk=1;
      tokens.token1=token1;
      tokens.token2=token2;
      tokens.token3=token3;
      tokens.token4=token4;
      Serial.println("Correct Password : user authorized");
      return tokens;
  }
}

User addUser(String id, String password) {
  User newUser = User(id, password);
  users[users_size] = newUser;
  users_size ++;
  Serial.print("added user with id: ");
  Serial.println(id);
  return newUser;
}

void removeUser(String id) {
  for(int i=0; i < users_size; i++) {
    if(users[i].id == id) {
      users[i] = User();
      users_size --;
      Serial.print("removed user with id: ");
      Serial.println(id);
    }
  }
}

char* getPassword(String id) {
  if(id == admin_user.id) {
    return admin_user.password.c_str();
  }
  for(int i=0; i < users_size; i++) {
    if(users[i].id == id) {
      return(users[i].password.c_str());
    }
  }
  return NULL;
}

char* string2ptr(String str) {
    char* writable = new char[str.length() + 1];
    str.toCharArray(writable, str.length() + 1);
    writable[str.length()] = '\0';
    return writable;
}

BYTE* password2key(String password) {
    BYTE key[16] ;

    for (int j = 0; j < 16; j++) {
        key[j] =  password[j];
    }

    return key;
}


void recv_cmd(Servo motor) {
  if(Serial.available()) {
    input = Serial.read();
    if(input == '>') {
      read_step += 1;
    }
    else {
      read_step = 0;
    }

    if(read_step == 16) {
        read_step = 0;
        message.remove(msg_idx-16+1);

        String id;
        String cypher;
        char* c_str_msg = string2ptr(message);
        char* token = strtok(c_str_msg, "#");
        id = String(token);
        cypher = String(strtok(NULL, "#"));
      
        Tokens tokens = authorize(id, cypher);
        if (tokens.isOk) {
          process_cmd(tokens, motor, id);
        }
        msg_idx = 0;
        message = "";
      }
    else {
      message += input;
      msg_idx++;
    }
  }
}

void process_cmd(Tokens tokens, Servo motor, String id) {
  if(tokens.token1 == "open") {
    Serial.println("unlocking");
    open(motor);
  }
  if(tokens.token1 == "lock") {
    Serial.println("locking");
    lock(motor);
  }
  if(tokens.token1 == "add") {
    if(id != admin_user.id) {
      Serial.println("Error : user is not admin");
      return;
    }
    Serial.println("adding user");
    addUser(tokens.token3, tokens.token4);
  }
  if(tokens.token1 == "remove") {
    if(id != admin_user.id) {
      Serial.println("Error : user is not admin");
      return;
    }
    Serial.println("removing user");
    removeUser(tokens.token3);
  }
}