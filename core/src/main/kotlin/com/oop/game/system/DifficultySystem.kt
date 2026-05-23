package com.oop.game.system

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

/** 난이도 별 조정할 수치*/
// 레벨업에 필요한 경험치량, 플레이어 최대 목숨, 플레이어 속도 조정
enum class DifficultySystem (
    val playerExpMax: Float,
    val playerMaxHeart: Float,
    val playerSpeed: Float,
    val tileTexture1: Texture,
    val tileTexture2: Texture,
    val heartIncrement: Float,
    val expIncrease: Float
){
    Easy(
        playerExpMax = 80F,
        playerMaxHeart = 7F,
        playerSpeed = 250F,
        tileTexture1 = Texture(Gdx.files.internal("tile_1_easy.png")),
        tileTexture2 = Texture(Gdx.files.internal("tile_2_easy.png")),
        heartIncrement = 2F,
        expIncrease = 1.2f
    ),
    Normal(
        playerExpMax = 100F,
        playerMaxHeart = 5F,
        playerSpeed = 200F,
        tileTexture1 = Texture(Gdx.files.internal("tile_1_normal.png")),
        tileTexture2 = Texture(Gdx.files.internal("tile_2_normal.png")),
        heartIncrement = 3F,
        expIncrease = 1.5f
    ),
    Hard(
        playerExpMax = 120F,
        playerMaxHeart = 3F,
        playerSpeed = 150F,
        tileTexture1 = Texture(Gdx.files.internal("tile_1_hard.png")),
        tileTexture2 = Texture(Gdx.files.internal("tile_2_hard.png")),
        heartIncrement = 4F,
        expIncrease = 2f
    )
}