package com.oop.game.entity.enemy.nomal

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.oop.game.entity.Count
import com.oop.game.entity.Enemy
import com.oop.game.entity.Player

class Bloater(
    x: Float,
    y: Float,
    worldWidth: Float,
    worldHeight: Float,
    player: Player,
    point: Boolean
) : Enemy(x, y, worldWidth, worldHeight, player, 60f, 60f) {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 텍스쳐
    override var texture: Texture = Texture(Gdx.files.internal("bloater.png"))

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 하트 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 하트 갯수
    override var heart: Float = 3f

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 데미지 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 데미지(충돌 데미지는 없지만 충돌 시 바로 폭발 후 플레이어 즉사)
    override var damage: Float = 0f

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 킬 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 킬 포인트 여부
    override var getPoint: Boolean = point

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 폭발 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 폭발 유무
    var isBoom: Boolean = false

    // 현재 폭발 텍스쳐 번호
    var explosionFrame = 0

    // 현재 텍스쳐 유지 시간(매 프레임 delta 만큼 증가)
    var frameTimer: Float = 0f

    // 프레임당 텍스쳐 유지 시간(이 시간이 지나면 다음 텍스쳐로 교체)
    var frameDuration: Float = 0.08f

    // 폭발 텍스쳐 모음
    val explosionTextures = listOf(
        Texture(Gdx.files.internal("explosion_0.png")),
        Texture(Gdx.files.internal("explosion_1.png")),
        Texture(Gdx.files.internal("explosion_2.png")),
        Texture(Gdx.files.internal("explosion_3.png")),
        Texture(Gdx.files.internal("explosion_4.png")),
        Texture(Gdx.files.internal("explosion_5.png")),
        Texture(Gdx.files.internal("explosion_6.png")),
        Texture(Gdx.files.internal("explosion_7.png")),
    )

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 생존 여부 판단 메서드
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 폭발 중일 때는 아직 살아있는 것으로 처리해
     * removeDead() 가 폭발 애니메이션 도중에 객체를 삭제하지 못하게 막는다
     * 폭발이 끝난 순간에만 죽은 것으로 처리해 자동으로 삭제되게 한다
     */
    override fun isAlive(): Boolean {

        // 폭발 중일 때는 폭발이 끝났는지 여부로 생존 판단
        if (isBoom) return !isExplosionFinished()

        // 평소에는 하트가 0보다 크면 살아있음
        return heart > 0f
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적이 데미지를 입는 메커니즘
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 맞을 수록 적의 크기가 커짐
     * 사망 시 터지면서 일정 범위 내에 있는 플레이어 객체에게 데미지를 줌
     * 플레이어에게 일정한 경험치를 제공
     */
    override fun onDamage(amount: Float) {

        // 하트 수 -amount
        super.onDamage(amount)

        // 맞을 수록 적의 크기가 커짐
        width *= 1.4f
        height *= 1.4f

        // 적 객체 사망 시 발생
        if (!isAlive()) {

            // 폭발 유무
            isBoom = true

            // 폭발 시작
            frameTimer = frameDuration

            // 첫 폭발 텍스쳐로 변경
            texture = explosionTextures[0]

            // 중복 점수 막기
            if (getPoint) {

                // 킬 카운트 +1
                Count.killCount++
                Count.killPoint++
            }

            // 플레이어가 얻는 경험치 양
            player.gainExp(50f)
        }
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 이동 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 이동 속도
    override var speed: Float = 300f

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적 업데이트
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    override fun update(delta: Float) {

        // 폭발 중일 때
        if (isBoom) {

            // 적 주변 범위 안에 있는 플레이어 객체에게 폭발 피해
            if (player.x in (x - 10f)..(x + width + 10f) && player.y in (y - 10f) ..(y + height + 10f)) {

                // 플레이어를 즉사 시킴
                player.heart = 0f
            }

            // 텍스쳐 유지 시간 누적
            frameTimer -= delta

            // 유지 시간이 지나면 다음 텍스쳐로 교체
            if (frameTimer <= 0f) {

                // 텍스쳐 유지 시간 초기화
                frameTimer = frameDuration

                // 다음 폭발 텍스쳐 번호로 이동
                explosionFrame++

                // 마지막 텍스쳐(7번)까지만 교체
                if (explosionFrame < 8) {
                    texture = explosionTextures[explosionFrame]
                }
            }
        }

        // 평소 이동 로직
        super.update(delta)
    }

    /**
     * 폭발 종료 여부 판단 메서드
     * 폭발이 시작됐고 마지막 프레임(8번)까지 넘어갔으면 종료
     */
    fun isExplosionFinished(): Boolean = isBoom && explosionFrame >= 8
}