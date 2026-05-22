package com.oop.game.entity.item

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.oop.game.entity.Item

class Booster(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float,
) : Item(x, y) {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 부스터의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 부스터의 텍스쳐
    override var texture = Texture(Gdx.files.internal("booster.png"))
}