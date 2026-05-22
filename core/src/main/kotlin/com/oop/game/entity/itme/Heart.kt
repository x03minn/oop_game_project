package com.oop.game.entity.item

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.oop.game.entity.Item

class Heart(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float,
) : Item(x, y) {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 하트의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 하트의 텍스쳐
    override var texture = Texture(Gdx.files.internal("heart.png"))
}