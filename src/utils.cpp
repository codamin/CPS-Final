#include <utils.hpp>

char input;
char message[MSG_MAX_LEN];
int msg_idx = 0;

int users_size = 0; 
User users[MAX_USERS_NUMBER];
User admin_user;

void open(Servo motor) {
  motor.write(180);
}

void lock(Servo motor) {
  for (int pos = 180; pos >= 0; pos -= 1) {
    motor.write(pos);
    delay(5);
  }
}

void set_admin(String id, String password) {
  admin_user = User(id, password);
}

String* authorize(String id, String cypher) {
  char* password = getPassword(id);
  if(!password) {
      Serial.println("user with this id not found");
      return NULL;
  }
  char* cypher_cstr = string2ptr(cypher);
  Serial.println(cypher_cstr);
  Serial.println(strlen(cypher_cstr));
  aes128_dec_multiple((uint8_t *)password, cypher_cstr, strlen(cypher_cstr));
  String plain_text = String(cypher_cstr);
  Serial.println(">>>>>");
  Serial.println(plain_text);

  delete[] cypher_cstr;

  String* plain_text_splitted = new String[MAX_SPLIT_SIZE];
  plain_text_splitted = split(plain_text, '#');
  
  Serial.println("fuck<><><>");
  Serial.println(id);
  Serial.println(plain_text_splitted[0]);
  Serial.println(plain_text_splitted[1]);
  Serial.println(plain_text_splitted[2]);
  Serial.println(plain_text_splitted[3]);
  if(plain_text_splitted[1] != id) {
      Serial.println("Wrong Password: user not authorized");
      return NULL;
  }
  else {
      Serial.println("Correct Password : user authorized");
      return plain_text_splitted;
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
    }
  }
  users_size --;
  Serial.print("removed user with id: ");
  Serial.println(id);
}

char* getPassword(String id) {
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

String* split(String str , char c) {
  int size = 0;
  String* splitted = new String[MAX_SPLIT_SIZE];
  String word = "";
  for(int iter = 0 ; iter < str.length() ; iter++)
  {
      if(str[iter] == c)
      {
          splitted[size] = word;
          word = "";
          size += 1;
      }
      else
      {
          word += (str[iter]);
      }
  }
  splitted[size] = word;
  return splitted;
}

char prev = NULL;
int read_step = 0;

void recv_cmd(Servo motor) {
  if(Serial.available()) {
    input = Serial.read();
    Serial.println(input);
    if(input == '\r') {
      read_step += 1;
    }
    else {
      read_step = 0;
    }
    Serial.println(read_step);

    // prev = input;

    if(read_step == 15) {
        // prev = NULL;
        read_step = 0;
        Serial.println(">>>><<<<<>>><");
        Serial.println(msg_idx);
        message[msg_idx-14]='\0';
        // Serial.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        Serial.println(message);
        return;
        // Serial.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        String* splitted_message = split(message, '#');
        String id = splitted_message[0];
        String cypher = splitted_message[1];

        String* plain_text_splitted = authorize(id, cypher);
        if (plain_text_splitted) {
          process_cmd(plain_text_splitted, motor, id);
        }
        msg_idx = 0;
        for (int i = 0; i < MSG_MAX_LEN; ++i)
          message[i] = 0;
      }
    else {
      message[msg_idx]=input;
      msg_idx++;
    }
  }
}
void process_cmd(String* plain_text_splitted, Servo motor, String id) {
  if(plain_text_splitted[0] == "open") {
    Serial.println("unlocking");
    open(motor);
  }
  if(plain_text_splitted[0] == "lock") {
    Serial.println("locking");
    lock(motor);
  }
  if(plain_text_splitted[0] == "add") {
    if(id != admin_user.id) {
      Serial.println("Error : user is not admin");
      return;
    }
    Serial.println("adding user");
    Serial.println(plain_text_splitted[2]);
    Serial.println(plain_text_splitted[3]);
    addUser(plain_text_splitted[2], plain_text_splitted[3]);
  }
  if(plain_text_splitted[0] == "remove") {
    if(id != admin_user.id) {
      Serial.println("Error : user is not admin");
      return;
    }
    Serial.println("removing user");
    removeUser(plain_text_splitted[1]);
  }
  delete[] plain_text_splitted;
}