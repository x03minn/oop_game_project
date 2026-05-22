package com.oop.game.system

interface HeartSystem {

    // 객체들의 하트 갯수
    var heart: Float

    // 객체들의 하트가 깎는 메서드
    fun onDamage(amount: Float) {

        // 하트 수 -amount
        heart -= amount
    }

    // 객체들의 하트를 증가시키는 메서드
    fun heartIncrease(amount: Float) {

        // 하트 수 +amount
        heart += amount
    }
}
