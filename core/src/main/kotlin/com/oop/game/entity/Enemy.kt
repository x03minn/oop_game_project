package com.oop.game.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.oop.game.GameObject
import com.oop.game.system.HeartSystem

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  적  — enemy.png 이미지, 플레이어를 쫓아 이동.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  GameObject 를 상속해 만든 '입력 없이 스스로 움직이는' 객체 예제.
 *
 *  핵심 포인트:
 *   ▸ update() 에 입력 처리가 없다 — AI(자율 행동)는 여기서 작성.
 *   ▸ Player 객체의 좌표를 참조하여 플레이어 방향으로 이동하는 추적 로직 구현.
 *
 *  응용 아이디어:
 *   ▸ 생성자에서 speed 를 받아 FastEnemy, SlowEnemy 로 다양화
 *   ▸ 체력(hp)과 takeDamage() 메서드 추가
 *   ▸ 이동 패턴을 사인파, 원운동 등으로 바꾸기
 *
 * @param worldWidth/Height: 월드 크기를 받아 경계 밖으로 못 나가게 제한하는 용도.
 */
abstract class Enemy(
    x: Float,
    y: Float,
    private val worldWidth: Float,
    private val worldHeight: Float,
    protected val player: Player,
    // 각 적마다 크기가 다르므로 자식 클래스에서 크기를 지정하여 전달
    width: Float,
    height: Float,
    point: Boolean = true
) : GameObject(x, y, width, height), HeartSystem {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 텍스쳐(각 적마다의 텍스쳐가 다르기 때문에 각자 초기화 시킴)
    abstract var texture: Texture

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 하트 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 하트 갯수(각 적마다 하트의 수가 다르기 때문에 각자 초기화 시킴)
    abstract override var heart: Float

    // 적 객체가 살아있는지 판단
    override fun isAlive(): Boolean = heart > 0f

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 데미지 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 데미지(각 적마다 데미지가 다르기 때문에 각자 초기화 시킴)
    abstract var damage: Float

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 이동 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 이동 속도
    abstract var speed: Float

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 킬 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 킬 포인트 여부
    open var getPoint: Boolean = point

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적 업데이트
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    override fun update(delta: Float) {

        // 적 위치에서 플레이어 방향으로의 단위 벡터 계산
        // Vector2(플레이어x + 25f - 적x, 플레이어y + 25f - 적y) → nor()로 정규화하여 방향만 추출
        val dir = Vector2((player.x + 25f) - x, (player.y + 25) - y).nor()

        // 적의 x축 이동
        x += dir.x * speed * delta

        // 적의 y축 이동
        y += dir.y * speed * delta

        // 적 객체를 월드 경계 안쪽으로 가두기
        x = x.coerceIn(0f, worldWidth - width)
        y = y.coerceIn(0f, worldHeight - height)
    }


    /**
     * 자신의 이미지를 그린다.
     *   원본은 n x n 이고 width/height 도 n 이라 1:1 로 그려진다.
     *   더 크게 보이게 하려면 width/height 를 늘리면 자동 확대된다.
     */
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        texture.dispose()
    }
}