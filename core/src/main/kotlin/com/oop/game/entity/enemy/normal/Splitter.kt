package com.oop.game.entity.enemy.nomal

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.oop.game.entity.Count
import com.oop.game.entity.Enemy
import com.oop.game.entity.Player

class Splitter(
    x: Float,
    y: Float,
    worldWidth: Float,
    worldHeight: Float,
    player: Player,
    point: Boolean
) : Enemy(x, y, worldWidth, worldHeight, player, 80f, 80f) {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 텍스쳐
    override var texture: Texture = Texture(Gdx.files.internal("splitter.png"))

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 하트 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 하트 갯수
    override var heart: Float = 5f

    // 현재 적의 기본 하트 갯수
    var defaultHeart: Int = heart.toInt()

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 데미지 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 데미지
    override var damage: Float = 1f

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 킬 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 킬 포인트 여부
    override var getPoint: Boolean = point

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 분열 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 분열 가능성 판단
    var canSplit: Boolean = false

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적이 데미지를 입는 메커니즘
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적이 사망하면 2마리로 분열(킬 카운트는 첫 번째에만 증가)
     * 사망 시 적이 2마리로 분열하면서 크기가 줄어듦
     * 플레이어에게 일정한 경험치를 제공
     */
    override fun onDamage(amount: Float) {

        // 하트 수 -amount
        super.onDamage(amount)

        // 적 사망 시 발생
        if (!isAlive()) {

            // 중복 점수 막기
            if (getPoint) {

                // 킬 카운트 +1
                Count.killCount++
                Count.killPoint++
            }

            // 플레이어가 얻는 경험치 양
            player.gainExp(10f)

            // 기존 체력을 2로 나눈 몫이 0보다 크면 분열 신호 보내기
            if ((defaultHeart / 2) > 0) {

                // 분열 신호
                canSplit = true
            }
        }
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 이동 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 이동 속도
    override var speed: Float = 170f
}