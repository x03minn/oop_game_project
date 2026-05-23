package com.oop.game.entity.enemy.elite

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.oop.game.entity.Enemy
import com.oop.game.entity.Player

class Brute(
    x: Float,
    y: Float,
    worldWidth: Float,
    worldHeight: Float,
    player: Player,
    point: Boolean
) : Enemy(x, y, worldWidth, worldHeight, player, 150f, 150f) {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 텍스쳐
    override var texture: Texture = Texture(Gdx.files.internal("brute.png"))

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 하트 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 하트 갯수
    override var heart: Float = 20f

    // 현재 적의 기본 하트 갯수
    var defaultHeart: Int = heart.toInt()

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 데미지 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 데미지
    override var damage: Float = 3f

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 킬 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 킬 포인트 여부
    override var getPoint: Boolean = point

    // 광폭화 사용 유무
    var getEnraged: Boolean = false

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적이 데미지를 입는 메커니즘
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적의 체력이 절반으로 떨어지면 광폭화(데미지 2배)
     * 플레이어에게 일정한 경험치를 제공
     */
    override fun onDamage(amount: Float) {

        // 하트 수 -amount
        super.onDamage(amount)

        // 체력이 절반으로 떨어지면 데미지 2배
        if (heart <= (defaultHeart / 2f) && !getEnraged) {

            // 광폭화 사용(적의 이미 변경 신호)
            getEnraged = true

            // 데미지 2배
            damage *= 2f
        }

        // 적 객체 사망 시 발생
        if (!isAlive()) {

            // 플레이어가 얻는 경험치 양
            player.gainExp(40f)
        }
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 이동 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 이동 속도
    override var speed: Float = 120f
}