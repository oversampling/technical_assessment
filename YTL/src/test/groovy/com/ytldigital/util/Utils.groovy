package com.ytldigital.util

class Utils {
    static int randomInt(int bound) {
        return randomInt(0, bound)
    }

    static int randomInt(int origin, int bound) {
        return new Random().nextInt(origin, bound)
    }
}
