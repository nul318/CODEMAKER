package com.example.codemaker;

/**
 * Created by Hanul on 2016-02-23.
 */
public class Chat_profile {

    String name= "";
    String chatmessage;
    Boolean isMine = false;

    Chat_profile(String name, String chatmessage, Boolean isMine){
        this.name=name;
        this.chatmessage=chatmessage;
        this.isMine=isMine;
    }
}
