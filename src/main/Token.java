package main;

//import com.sun.org.apache.bcel.internal.generic.NEW;

public enum Token {
    UNKNOWN, YELLOW, GREEN, BLUE, RED, SKULL, SKULL_5, MONEY, STAR, WILDCARD, BURN, NEW_WILD;
    /*WILDCARD_3, WILDCARD_4, WILDCARD_5, WILDCARD_6, WILDCARD_7, WILDCARD_8*/

    public boolean matches(Token second) {
        if (this.isAuxiliary()) { return false; }
        if (this == MONEY || this == STAR || this == UNKNOWN) {
            return this == second;
        }
        if (this == SKULL || this == SKULL_5) {
            return second == SKULL || second == SKULL_5;
        }
        if (this == WILDCARD) {
            return second == WILDCARD || second == YELLOW || second == BLUE || second == RED || second == GREEN;
        }
        return second == this || second == WILDCARD;
    }

    public boolean isMana() {
        return this == BLUE || this == RED || this == GREEN || this == YELLOW;
    }

    // simple token just add one unit of corresponding resourse -
    // unlike wildcards that multiply mana or hotskulls that burn the whole square
    public boolean isSimple() {
        return this.isMana() || this == STAR || this == MONEY || this == SKULL;
    }

    // auxiliary token don't appear in the game - they are used for grid analysis
    public boolean isAuxiliary() {
        return this == UNKNOWN || this == BURN || this == NEW_WILD;
    }
}

