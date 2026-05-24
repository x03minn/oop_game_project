package com.oop.game.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Color
import com.oop.game.OopGame
import com.oop.game.InputHandler
import com.oop.game.entity.Count

class GameOverWorld(
    private val game: OopGame
) : ScreenAdapter() {
    /*
    * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    * 렌더링 관련 프로퍼티
    * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    */
    // 텍스트 및 이미지 출력 도구
    private val batch = SpriteBatch()

    // 글자 출력을 위한 폰트
    private val font = BitmapFont()

    // 배경 이미지
    private val background = Texture(Gdx.files.internal("gameover.png"))
    // 뒤로가기 버튼 이미지
    private val button_back_menu = Texture(Gdx.files.internal("button_back.png"))
    // 종료 버튼 이미지
    private val button_exit = Texture(Gdx.files.internal("button_exit.png"))

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 버튼 관련 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 버튼 너비
    private val buttonWidth = 560f

    // 버튼 높이
    private val buttonHeight = 100f

    // 버튼 사이 간격
    private val buttonGap = 40f

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 매 프레임 처리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 화면 지우기 → 결과 그리기 → 입력 처리 순서로 실행
     */
    override fun render(delta: Float) {

        // 화면 크기
        val screenWidth = Gdx.graphics.width.toFloat()
        val screenHeight = Gdx.graphics.height.toFloat()

        // 전체 버튼 X 좌표 (화면 중앙 기준)
        val buttonX = screenWidth * 0.5f - buttonWidth * 0.5f

        // 뒤로가기 버튼 좌표
        val backX = buttonX
        val backY = screenHeight * 0.3f

        // 종료 버튼 좌표
        val exitX = buttonX
        val exitY = backY - buttonHeight - buttonGap

        // 화면 지우기
        clearScreen()

        // 결과 그리기
        drawGameOver(screenWidth, screenHeight, backX, backY, exitX, exitY)

        // 마우스 및 키보드 입력 처리
        handleInput(backX, backY, exitX, exitY)
    }

    // 투명도 1로 화면을 칠해서 지움
    fun clearScreen() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 결과 그리기
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 배경 이미지와 점수를 먼저 그린 뒤 버튼과 텍스트를 올린다
     */
    fun drawGameOver(
        screenWidth: Float,
        screenHeight: Float,
        backX: Float,
        backY: Float,
        exitX: Float,
        exitY: Float
    ) {
        batch.begin()

        // 배경 이미지 그리기
        batch.draw(background, 0f, 0f, screenWidth, screenHeight)

        // 생존 시간 출력
        font.data.setScale(1.6f)
        font.color = Color.WHITE
        font.draw(batch, "Survival Time : ${PlayWorld.timer} Sec", screenWidth * 0.5f - 150f, screenHeight * 0.50f)

        // 처치 수 출력
        font.color = Color.GOLD
        font.draw(batch, "Total Kills    : ${Count.killPoint}", screenWidth * 0.5f - 110f, screenHeight * 0.43f)

        // 뒤로가기 버튼 그리기
        batch.draw(button_back_menu, backX, backY, buttonWidth, buttonHeight)

        // 종료 버튼 그리기
        batch.draw(button_exit, exitX, exitY, buttonWidth, buttonHeight)


        batch.end()
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 마우스 및 키보드 입력 처리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 클릭한 위치가 버튼 안에 있거나 ESC 입력 시 해당 동작 실행
     */
    private fun handleInput(backX: Float, backY: Float, exitX: Float, exitY: Float) {

        // 마우스 클릭 여부 확인
        if (Gdx.input.justTouched()) {

            // 마우스 x 좌표
            val mouseX = Gdx.input.x.toFloat()

            // 마우스 y 좌표(LibGDX y축은 위아래가 반대라 변환)
            val mouseY = Gdx.graphics.height - Gdx.input.y.toFloat()

            // 뒤로가기 버튼 클릭 시
            if (isInside(mouseX, mouseY, backX, backY)) {

                // 킬 카운트 관련 초기화
                Count.killPoint = 0
                Count.killCount = 0

                // 시간 관련 초기화
                PlayWorld.timer = 0
                PlayWorld.timeFloat = 0f

                game.create()
                return
            }

            // 종료 버튼 클릭 시
            if (isInside(mouseX, mouseY, exitX, exitY)) {
                Gdx.app.exit()
                return
            }
        }

        // 키보드 ESC 키 누를 시 메인 메뉴 이동
        if (InputHandler.isKeyJustPressed(InputHandler.ESCAPE)) {
            game.create()
            return
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
        font.dispose()
        background.dispose()
        button_back_menu.dispose()
        button_exit.dispose()
    }
}