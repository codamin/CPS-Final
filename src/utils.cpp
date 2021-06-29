#include <utils.hpp>

char input;
String message = "";
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

Tokens authorize(String id, String cypher) {
  Tokens tokens;

  Serial.print("id in authorize= ");
  Serial.println(id);

  Serial.print("cypher in authorize= ");
  Serial.println(cypher);

  char* password = getPassword(id);

  Serial.print("password= ");
  Serial.println(password);

  if(!password) {
      Serial.println("user with this id not found");
      tokens.isOk = 0;
      return tokens;
  }
  char* cypher_cstr = string2ptr(cypher);
  Serial.print("cypher_cstr=");
  Serial.println(cypher_cstr);
  Serial.print("length of cypher_cstr=");
  Serial.println(strlen(cypher_cstr));
  aes128_dec_multiple((uint8_t *)password, cypher_cstr, strlen(cypher_cstr));
  // String plain_text = String(cypher_cstr);
  Serial.print("plain_text=");
  Serial.println(cypher_cstr);

  // static String* plain_text_splitted = split(plain_text, '#');
  
  // Serial.println(id);
  // Serial.println(plain_text_splitted[0]);
  // Serial.println(plain_text_splitted[1]);
  // Serial.println(plain_text_splitted[2]);
  // Serial.println(plain_text_splitted[3]);


  String token1, token2, token3, token4;

  char* token = strtok(cypher_cstr, "#");

  token1 = String(token);
  token2 = String(strtok(NULL, "#"));
  Serial.print("token1= ");
  Serial.println(token1);
  Serial.print("token2= ");
  Serial.println(token2);

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
  // String* splitted = new String[MAX_SPLIT_SIZE];
  static String splitted[MAX_SPLIT_SIZE];
  String word = "";
  for(int iter = 0 ; iter < str.length() ; iter++)
  {
      Serial.print(str[iter]);
      Serial.print(" ");

      if(str[iter] == c)
      {
          Serial.println("found #");
          Serial.print("word=");
          Serial.println(word);
          splitted[size] = word;
          word = "";
          size += 1;
      }
      else
      {
          word += (str[iter]);
      }
  }
  Serial.print("size=");
  Serial.print(size+1);
  splitted[size] = word;
  return splitted;
}

char prev = NULL;
int read_step = 0;

void recv_cmd(Servo motor) {
  if(Serial.available()) {
    input = Serial.read();
    // Serial.println(input);
    if(input == '>') {
      read_step += 1;
    }
    else {
      read_step = 0;
    }
    // Serial.println(read_step);

    if(read_step == 16) {
      // Serial.println("tamoom shod");
        read_step = 0;
        // Serial.println(msg_idx);
        // message[msg_idx-16+1]='\0';
        message.remove(msg_idx-16+1);
        Serial.print("message= ");
        Serial.println(message);

        // String* splitted_message = split(message.c_str(), '#');
        // String id = splitted_message[0];
        // String cypher = splitted_message[1];
        String id;
        String cypher;
        char* c_str_msg = string2ptr(message);
        char* token = strtok(c_str_msg, "#");
        id = String(token);
        cypher = String(strtok(NULL, "#"));

        Serial.print("id in recv_cmd= ");
        Serial.println(id);

        Serial.print("cypher in recv_cmd= ");
        Serial.println(cypher);

        Tokens tokens = authorize(id, cypher);
        if (tokens.isOk) {
          process_cmd(tokens, motor, id);
        }
        msg_idx = 0;
        message = "";
        // for (int i = 0; i < strlen(message); ++i)
        //   message[i] = 0;
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
    Serial.println(tokens.token3);
    Serial.println(tokens.token4);
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