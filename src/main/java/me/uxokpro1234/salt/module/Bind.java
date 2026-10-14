package me.uxokpro1234.salt.module;

import org.lwjgl.input.Keyboard;

public class Bind {
    private int key = Keyboard.KEY_NONE;

    public Bind(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
    public void setKey(int key) {
        //del for none
        if(key == Keyboard.KEY_DELETE) {
            this.key = Keyboard.KEY_NONE;
        }
        else {
            this.key = key;
        }
    }
    public boolean isNone() {
        return key == Keyboard.KEY_NONE;
    }

    @Override
    public String toString() {
        if (isNone()) return "NONE";
        //yes bro addittt
        if (key == Keyboard.KEY_NONE || key == Keyboard.KEY_DELETE) {
            return "NONE";
        }

        return Keyboard.getKeyName(key);
    }
}