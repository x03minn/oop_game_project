package com.oop.game.entity.enemy.elite

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.oop.game.entity.Enemy
import com.oop.game.entity.Player

class Ghost(
    x: Float,
    y: Float,
    worldWidth: Float,
    worldHeight: Float,
    player: Player,
    point: Boolean
) : Enemy(x, y, worldWidth, worldHeight, player, 100f, 100f) {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 텍스쳐
    override var texture: Texture = Texture(Gdx.files.internal("ghost.png"))

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 하트 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 하트 갯수
    override var heart: Float = 12f

    // 현재 적의 기본 하트 갯수
    var defaultHeart: Int = heart.toInt()

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 데미지 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적 객체의 데미지
    override var damage: Float = 2f

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 킬 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 킬 포인트 여부
    override var getPoint: Boolean = point

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 유령화 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 현재 시간(남은 유령화 지속 시간)
    var ghostModeTimer: Float = 0f

    // 유령화 시간
    var ghostDuration: Float = 4f

    // 유령화 판단
    val isGhostMode: Boolean
        get() = ghostModeTimer > 0f

    // 유령화 사용 유무
    var getGhost: Boolean = false

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적이 데미지를 입는 메커니즘
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 체력이 절반 이하로 떨어지면 잠시동안 유령화 상태(데미지를 받지 않음, 잠시동안 속도 2배)
     * 플레이어에게 일정한 경험치를 제공
     */
    override fun onDamage(amount: Float) {

        // 적 객체가 유령화 상태인지 확인
        if (isGhostMode) {
            return
        }

        // 하트 수 -amount
        super.onDamage(amount)

        // 체력 절반 이하 시 유령화(한번 만 사용)
        if (heart <= (defaultHeart / 2f) && !getGhost) {

            // 유령화 발동
            ghostModeTimer = ghostDuration

            // 유령화 사용
            getGhost = true
        }

        //적 사망시 발생
        if (!isAlive()) {

            // 플레이어가 얻는 경험치 양
            player.gainExp(20f)
        }
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 이동 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 적의 이동 속도
    override var speed: Float = 170f

    // 유령화 시 이동 속도
    private val ghostSpeed: Float = 340f


    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적 업데이트
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    override fun update(delta: Float) {

        // 플레이어를 따라가기 유지
        super.update(delta)

        // 유령화 시간 및 속도 관리
        if (ghostModeTimer > 0f) {

            // 유령화 시간 관리((매 프레임 마다 델타 만큼 감소 >> 실직적으로 설정해둔 시간만큼 유령화)
            ghostModeTimer -= delta

            // 유령화 시 속도 2배
            speed = ghostSpeed
        } else {

            // 속도 초기화
            speed = 170f
        }
    }
}