package com.oop.game.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.oop.game.OopGame
import com.oop.game.system.DifficultySystem
import com.badlogic.gdx.graphics.Texture

class DifficultyWorld(
    private val game: OopGame
) : ScreenAdapter() {
    /*
    * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    * 렌더링 관련 프로퍼티
    * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    */
    // 텍스트 및 이미지 출력 도구
    private val batch = SpriteBatch()

    // 글자 출력 도구
    private val font = BitmapFont()

    // 도형 출력 도구(버튼 배경 사각형)
    private val shapeRenderer = ShapeRenderer()

    //배경 이미지
    private val background = Texture(Gdx.files.internal("menu.png"))

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

        // 이지모드 버튼 좌표
        val easyX = buttonX
        val easyY = screenHeight * 0.5f + 70f

        // 노멀모드 버튼 좌표
        val normalX = buttonX
        val normalY = easyY - buttonHeight - buttonGap

        //하드모드 버튼 좌표
        val hardX = buttonX
        val hardY = normalY - buttonHeight - buttonGap

        // 화면 지우기
        clearScreen()

        // 메뉴 그리기
        drawMenu(screenWidth, screenHeight, easyX, easyY, normalX, normalY, hardX, hardY)

        // 마우스 입력 처리
        handleInput( easyX, easyY, normalX, normalY, hardX, hardY)
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 화면 지우기
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 이전 프레임의 잔상을 어두운 배경색으로 덮어 지운다
     */
    fun clearScreen() {

        // 배경색 설정(어두운 남색)
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.1f, 1f)

        // 화면 지우기
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
        easyX: Float,
        easyY: Float,
        normalX: Float,
        normalY: Float,
        hardX: Float,
        hardY: Float
    ) {
        // 배경 이미지 그리기
        batch.begin()
        batch.draw(background, 0f, 0f, screenWidth, screenHeight)
        batch.end()

        //버튼 배경 사각형 그리기
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        // 이지모드 버튼 배경
        shapeRenderer.setColor(0.25f, 0.25f, 0.3f, 1.0f)
        shapeRenderer.rect(easyX, easyY, buttonWidth, buttonHeight)

        // 노멀모드 버튼 배경
        shapeRenderer.setColor(0.25f, 0.25f, 0.3f, 1.0f)
        shapeRenderer.rect(normalX, normalY, buttonWidth, buttonHeight)

        // 하드모드 버튼 배경
        shapeRenderer.setColor(0.25f, 0.25f, 0.3f, 1.0f)
        shapeRenderer.rect(hardX, hardY, buttonWidth, buttonHeight)

        shapeRenderer.end()

        // 텍스트 그리기
        batch.begin()
        font.setColor(1.0f, 1.0f, 1.0f, 1.0f)

        // 이지모드 버튼 텍스트
        font.draw(batch, "Easy", easyX + 135f, easyY + 40f)

        // 노멀모드 버튼 텍스트
        font.draw(batch, "Normal", normalX + 125f, normalY + 40f)

        // 하드모드 버튼 텍스트
        font.draw(batch, "Hard", hardX + 135f, hardY + 40f)

        batch.end()
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 마우스 입력 처리
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 클릭한 위치가 버튼 안에 있으면 해당 동작 실행
     */
    private fun handleInput(easyX: Float, easyY: Float, normalX: Float, normalY: Float, hardX: Float, hardY: Float) {

        // 마우스 클릭 여부 확인
        if (Gdx.input.justTouched()) {

            // 마우스 x 좌표
            val mouseX = Gdx.input.x.toFloat()

            // 마우스 y 좌표(LibGDX y축은 위아래가 반대라 변환)
            val mouseY = Gdx.graphics.height - Gdx.input.y.toFloat()

            // 이지모드 버튼 클릭 시
            if (isInside(mouseX, mouseY, easyX, easyY)) {
                game.startGame(DifficultySystem.Easy)
                return
            }
            // 노멀모드 버튼 클릭 시
            if (isInside(mouseX, mouseY, normalX, normalY)) {
                game.startGame(DifficultySystem.Normal)
                return
            }
            // 하드모드 버튼 클릭 시
            if (isInside(mouseX, mouseY, hardX, hardY)) {
                game.startGame(DifficultySystem.Hard)
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
        font.dispose()
        shapeRenderer.dispose()
        background.dispose()
    }
}