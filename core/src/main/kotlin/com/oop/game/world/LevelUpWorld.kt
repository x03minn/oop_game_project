package com.oop.game.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.OopGame
import com.badlogic.gdx.graphics.Texture
import com.oop.game.entity.Bullet
import com.oop.game.entity.Player

class LevelUpWorld(
    private val game: OopGame,
) : ScreenAdapter() {
    /*
    * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    * 렌더링 관련 프로퍼티
    * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    */
    // 텍스트 및 이미지 출력 도구
    private val batch = SpriteBatch()


    //배경 이미지
    private val LevelUpScreen = Texture(Gdx.files.internal("LevelUpScreen.png"))
    //공격력 상승 버튼 이미지
    private val button_power = Texture(Gdx.files.internal("button_power.png"))
    //공속 상승 버튼 이미지
    private val button_attackSpeed = Texture(Gdx.files.internal("button_attackSpeed.png"))
    //총알 갯수 상승 버튼 이미지
    private val button_bulletAmount = Texture(Gdx.files.internal("button_bulletAmount.png"))

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 버튼 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 버튼 너비
    private val buttonWidth = 560f

    // 버튼 높이
    private val buttonHeight = 100f

    //버튼 사이 간격
    private val buttonGap = 50f

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 매 프레임 처리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 화면 지우기 → 메뉴 그리기 → 입력 처리 순서로 실행
     */
    override fun render(delta: Float) {

        // 화면 크기
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        // 전체 버튼 X 좌표 (화면 중앙 기준)
        val buttonX = screenWidth * 0.5f - buttonWidth * 0.5f

        // 공격력 상승 버튼 좌표
        val powerX = buttonX
        val powerY = screenHeight * 0.5f + 20f

        // 공속 상승 버튼 좌표
        val attackSpeedX = buttonX
        val attackSpeedY = powerY - buttonHeight - buttonGap

        // 총알 갯수 상승 버튼 좌표
        val bulletAmountX = buttonX
        val bulletAmountY = attackSpeedY - buttonHeight - buttonGap


        // 메뉴 그리기
        drawMenu(screenWidth, screenHeight, powerX, powerY, attackSpeedX, attackSpeedY, bulletAmountX, bulletAmountY)

        // 마우스 입력 처리
        handleInput( powerX, powerY, attackSpeedX, attackSpeedY, bulletAmountX, bulletAmountY)
    }

    //검은색으로 화면을 칠해서 지움
    fun clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 메뉴 그리기
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 버튼 배경 사각형을 먼저 그린 뒤 텍스트를 올린다
     */
    fun drawMenu(
        screenWidth: Float,
        screenHeight: Float,
        powerX: Float,
        powerY: Float,
        attackSpeedX: Float,
        attackSpeedY: Float,
        bulletAmountX: Float,
        bulletAmountY: Float
    ) {
        // 배경 이미지 그리기
        batch.begin()
        batch.draw(LevelUpScreen, 0f, 0f, screenWidth, screenHeight)

        // 공격력 상승 버튼 그리기
        batch.draw(button_power, powerX, powerY, buttonWidth, buttonHeight)

        // 공속 상승 버튼 그리기
        batch.draw(button_attackSpeed, attackSpeedX, attackSpeedY, buttonWidth, buttonHeight)

        // 총알갯수 상승 버튼 그리기
        batch.draw(button_bulletAmount, bulletAmountX, bulletAmountY, buttonWidth, buttonHeight)


        batch.end()
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 마우스 입력 처리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 클릭한 위치가 버튼 안에 있으면 해당 동작 실행
     */
    private fun handleInput(powerX: Float, powerY: Float, attackSpeedX: Float, attackSpeedY: Float, bulletAmountX: Float, bulletAmountY: Float) {

        // 마우스 클릭 여부 확인
        if (Gdx.input.justTouched()) {

            // 마우스 x 좌표
            val mouseX = Gdx.input.x.toFloat()

            // 마우스 y 좌표(LibGDX y축은 위아래가 반대라 변환)
            val mouseY = Gdx.graphics.height - Gdx.input.y.toFloat()

            // 공격력 상승 버튼 클릭 시
            if (isInside(mouseX, mouseY, powerX, powerY)) {

                // 총알 데미지 증가
                Bullet.damage += 1f
                game.returnToPlayWorld()
                return
            }
            // 공속 상승 버튼 클릭 시
            if (isInside(mouseX, mouseY, attackSpeedX, attackSpeedY)) {

                // 총알 스피드 증가
                Bullet.speed += 100f
                game.returnToPlayWorld()
                return
            }
            // 총알 갯수 상승 버튼 클릭 시
            if (isInside(mouseX, mouseY, bulletAmountX, bulletAmountY)) {

                // 총알 갯수 +1
                Player.bulletCount += 1
                game.returnToPlayWorld()
                return
            }
        }
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 마우스 위치가 버튼 안에 있는지 확인하는 메서드
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 마우스 x, y 좌표가 버튼 영역(buttonX ~ buttonX+width, buttonY ~ buttonY+height) 안에 있으면 true
     */
    private fun isInside(mouseX: Float, mouseY: Float, buttonX: Float, buttonY: Float): Boolean {
        return mouseX >= buttonX &&
                mouseX <= buttonX + buttonWidth &&
                mouseY >= buttonY &&
                mouseY <= buttonY + buttonHeight
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 자원 해제
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 화면이 전환되거나 앱이 종료될 때 GPU 메모리에 올라간 자원을 해제한다
     */
    override fun dispose() {
        batch.dispose()
        LevelUpScreen.dispose()
        button_power.dispose()
        button_bulletAmount.dispose()
        button_attackSpeed .dispose()
    }
}