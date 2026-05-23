package com.oop.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.oop.game.GameObject

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  총알  — enemy.png 이미지, 마우스 커서 방향으로 이동.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  texture 의 파일명을 자기 이미지로 바꾸거나,
 *  update() 에 발사 로직·특수 능력 등을 추가하면 된다.
 *
 *  핵심 포인트:
 *   ▸ update() 에 입력 처리가 없다 — AI(자율 행동)는 여기서 작성.
 *   ▸ 마우스 커서 좌표를 참조하여 마우스 커서 방향으로 이동하는 로직 구형.
 *
 *  응용 아이디어:
 *
 * @param worldWidth/Height: 월드 크기를 받아 경계에 닿으면 삭제.
 */
class Bullet(
    // 플레이어의 좌표
    playerX: Float,
    playerY: Float,
    // 마우스의 좌표(현재 카매라 기준이 아니라 월드 전체 기준)
    mouseX: Float,
    mouseY: Float,
    private val worldWidth: Float,
    private val worldHeight: Float
) : GameObject(playerX, playerY, 15f, 15f) {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 총알의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 총알의 텍스쳐
    private val texture = Texture(Gdx.files.internal("bullet.png"))

    companion object {

        /*
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 데미지 관련 프로퍼티
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         */
        // 총알의 데미지
        var damage: Float = 1f

        /*
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         * 이동 관련 프로퍼티
         * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
         */
        // 총알의 이동 속도
        var speed: Float = 1000f
    }

    // 총알의 이동 방향(총알의 방향 단위 벡터)
    val dir: Vector2 = Vector2(mouseX - (playerX + 25f), mouseY - (playerY + 25f)).nor()

    // 총알이 월드 맵 경계에 도달 했는지 판단
    var isOutOfBounds: Boolean = false

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 총알 업데이트
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    override fun update(delta: Float) {

        // 총알의 이동
        x += dir.x * speed * delta
        y += dir.y * speed * delta

        // 총알이 맵 경계에 도달했다는 신호
        if (x !in 0f .. worldWidth || y !in 0f .. worldHeight) {
            isOutOfBounds = true
        }
    }


    /**
     * 매 프레임 호출 — 자신의 이미지를 그린다.
     *
     * batch.draw(texture, x, y, w, h):
     *   왼쪽 아래 (x, y) 지점부터 (w, h) 크기로 텍스처를 늘려서 그린다.
     *   원본 이미지가 15x15 이고 w=15, h=15 이면 1:1 그대로 그려진다.
     */
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        texture.dispose()
    }
}