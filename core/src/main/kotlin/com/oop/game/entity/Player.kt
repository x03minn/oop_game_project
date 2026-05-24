package com.oop.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.GameObject
import com.oop.game.system.HeartSystem
import com.oop.game.InputHandler
import com.oop.game.system.DifficultySystem

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  플레이어 — player.png 이미지, W A S D 키로 조종.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  texture 의 파일명을 자기 이미지로 바꾸거나,
 *  update() 에 발사 로직·특수 능력 등을 추가하면 된다.
 *
 *  핵심 포인트:
 *   ▸ Texture 는 객체가 살아있는 동안 한 번만 만들고 재사용 (생성 비용이 큼).
 *   ▸ 객체가 사라질 때 dispose() 로 GPU 자원 해제 — 기본 GameObject.dispose()를 override.
 *   ▸ batch.draw(texture, x, y, w, h) 한 줄로 이미지를 그린다.
 *
 */
class Player(
    x: Float,
    y: Float,
    private val difficultySystem: DifficultySystem,
) : GameObject(x, y, 50f, 50f), HeartSystem {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 플레이어의 텍스쳐 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 플레이어의 텍스쳐
    private val texture = Texture(Gdx.files.internal("player.png"))

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 레벨 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 현재 플레이어의 레벨
    var level: Float = 1f

    // 현재 플레이어가 얻은 경험치 양
    var exp: Float = 0f

    // 다음 레벨까지 채워야하는 경험치 양(난도 별로 다름)
    var expMax: Float = difficultySystem.playerExpMax

    // 레벨업 시 경험치 양의 증가량
    var expIncrease: Float = difficultySystem.expIncrease

    // 플레이어가 레벨업 했다는 신호
    var isLevelUpReady: Boolean = false

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 플레이어가 경험치를 얻는 메커니즘
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적 객체를 처치하면 적 마다 고유한 경험치 양을 줌
     * 적을 처치하여 경험치를 얻고 레벨업 진행
     */
    fun gainExp(amount: Float) {

        // 적 마다 고유한 경험치 양을 더하기
        exp += amount

        // 현재 얻어야 하는 경험치 양을 초과하면 레벨업 진행
        if (exp >= expMax) {
            levelUp()
        }
    }

    fun levelUp() {

        // 기존 초과한 경험치는 유지 >> 경험치에 필요한 경험치 차감하기
        exp -= expMax

        // 다음 레벨 까지 얻어야 하는 경험치 양 증가
        expMax *= expIncrease

        // 플레이어의 현재 레벨 증가
        level++

        // 플레이어가 레벨업 했다는 신호
        isLevelUpReady = true
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 하트 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 플레이어 객체의 하트 갯수(난도 별로 다름)
    override var heart: Float = difficultySystem.playerMaxHeart

    // 플레이어 객체가 살아있는지 판단
    override fun isAlive(): Boolean = heart > 0f

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 플레이어가 데미지를 입는 메커니즘
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적 객체와 충돌했을 때 하트 수 -1
     * 하트 수는 0 아래로 내려가지 않음
     * 데미지를 입은 후 일정 시간 동안 무적 상태 유지
     * 무적 상태일 경우 적과의 충돌 데미지 무시
     */
    override fun onDamage(amount: Float) {

        // 플레이어 객체와 적과 충돌해서 무적 상태인지 확인
        if (isInvincible) {
            return
        }

        // 하트 수 -amount
        super.onDamage(amount)

        // 적과 충돌이 일어나 데미지를 받았으므로 무적 시작
        invincibleTimer = invincibleDuration

        // 하트 수가 0 아래로 내려가지 않게 함
        if (heart <= 0f) {
            heart = 0f
        }
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 이동 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 플레이어의 이동 속도 (난도 별로 다름)
    var speed: Float = difficultySystem.playerSpeed

    // 난이도별 플레이어 기본 이동 속도
    val defaultSpeed: Float = difficultySystem.playerSpeed

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 총알 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 현재 시간(다음 발사까지 남은 시간)
    var shootingTimer: Float = 0f

    // 다음 발사까지 걸리는 시간
    var shootingInterval: Float = 0.6f

    // 현재 총알을 발사 했는지 판단
    var isShooting: Boolean = false

    companion object {

        // 현재 총알의 갯수
        var bulletCount: Int = 1

        // 현재 연쇄적으로 나가는 총알 사이의 시간
        var bulletTimer: Float = 0f

        // 연쇄적으로 나가는 총알 사이의 시간
        var bulletInterval: Float = 0.1f
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 무적 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 현재 시간(남은 무적 시간)
    var invincibleTimer: Float = 0f

    // 무적 시간
    var invincibleDuration: Float = 2f

    // 무적 상태인지 판단(getter를 사용하여 남은 무적 시간이 0초 이상이면 true 반환)
    val isInvincible: Boolean
        get() = invincibleTimer > 0f


    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 플레이어 업데이트
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    override fun update(delta: Float) {

        // 플레이어 이동(W A S D로 이동) 관리
        if (InputHandler.isKeyPressed(InputHandler.A)) x -= speed * delta
        if (InputHandler.isKeyPressed(InputHandler.D)) x += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.W)) y += speed * delta
        if (InputHandler.isKeyPressed(InputHandler.S)) y -= speed * delta

        // 플레이어 무적 시간 관리(매 프레임 마다 델타 만큼 감소 >> 실직적으로 설정해둔 시간만큼 무적)
        if (invincibleTimer > 0f) invincibleTimer -= delta

        // 다음 총알 발사 까지 걸리는 시간 관리
        if (shootingTimer > 0f) shootingTimer -= delta

        if (bulletTimer > 0f) bulletTimer -= delta

        // 총알을 발사했는지 판단(마우스 좌클릭을 했는지 + 현재 다음 총알 발사까지 남은 시간이 없는지)
        if (InputHandler.isMouseButtonPressed(InputHandler.LEFT_BUTTON) && shootingTimer <= 0f) {

            // 총알을 발사했다는 신호
            isShooting = true
        }
    }

    /**
     * 매 프레임 호출 — 자신의 이미지를 그린다.
     *
     * batch.draw(texture, x, y, w, h):
     *   왼쪽 아래 (x, y) 지점부터 (w, h) 크기로 텍스처를 늘려서 그린다.
     *   원본 이미지가 50 x 50 이고 w=50, h=50 이면 1:1 그대로 그려진다.
     */
    override fun draw(batch: SpriteBatch) {
        batch.draw(texture, x, y, width, height)
    }

    /** GPU 자원 정리 — 화면이 닫힐 때 GameWorld 가 호출. */
    override fun dispose() {
        texture.dispose()
    }
}

