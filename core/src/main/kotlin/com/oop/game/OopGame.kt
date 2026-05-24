package com.oop.game

import com.badlogic.gdx.Game
import com.oop.game.system.DifficultySystem
import com.oop.game.world.DifficultyWorld
import com.oop.game.world.MenuWorld
import com.oop.game.world.PlayWorld
import com.oop.game.world.LevelUpWorld
import com.oop.game.world.GameOverWorld

/**
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *  이 프로젝트의 '게임 본체' — LibGDX 의 Game 을 상속해서 만든다.
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 *
 *  Game 은 LibGDX 가 제공하는 '게임 앱의 최상위 껍데기' 클래스다.
 *    - create()   : 앱 시작 시 한 번 호출 (초기화 자리)
 *    - render()   : 매 프레임 호출 (내부에서 현재 Screen.render 를 대신 돌려줌)
 *    - setScreen(): 현재 Screen 을 바꾸는 메서드
 *
 *  이 중 create() 는 추상 메서드 (ApplicationListener 인터페이스 상속).
 *  즉 Game 을 상속하는 순간 반드시 create() 를 구현해야 한다.
 *
 *  (안드로이드의 Activity.onCreate() 와 완전히 같은 패턴이다.
 *   onCreate() 안에서 setContentView(...) 로 첫 화면을 붙이듯,
 *   여기서는 create() 안에서 setScreen(...) 으로 첫 Screen 을 붙인다.)
 *
 *  왜 이 파일이 core 에 있나?
 *    OopGame 은 특정 OS 에 의존하는 코드가 전혀 없다 (LibGDX Game 상속뿐).
 *    따라서 데스크톱·안드로이드·iOS 어느 플랫폼에서 띄우든 그대로 쓸 수 있다.
 *    플랫폼별 런처(DesktopLauncher 등)만 따로 두면 된다.
 *
 *  자기 게임을 만들 때 고칠 곳:
 *   ▸ screenWidth / screenHeight   : 창(카메라) 크기
 *   ▸ worldWidth  / worldHeight    : 스크롤 가능한 월드 크기
 *   ▸ create() 안에서 setScreen 에 넘기는 Screen 을 자기 Screen 으로 교체
 */
class OopGame : Game() {

    // 화면(창) 크기 — DesktopLauncher 가 창 크기 설정에도 이 값을 읽어간다.
    //   public(기본)으로 둔 이유: 외부(DesktopLauncher)에서 접근해야 하므로.
    val screenWidth = 960
    val screenHeight = 1280

    // 월드 크기 — 화면의 1.5배. 카메라(WASD)로 탐험 가능한 영역.
    //   이 값은 내부 설정이므로 private.
    private val worldWidth = 2880
    private val worldHeight = 3840


    private var currentPlayWorld: PlayWorld? = null

    /**
     * LibGDX 가 게임 시작 시 한 번 호출하는 라이프사이클 메서드.
     *
     * 이 함수는 'Gdx.graphics / Gdx.gl / Gdx.files 같은 전역이 모두 준비된 뒤'
     * 호출되므로, Screen 안에서 SpriteBatch / BitmapFont / Texture 같은 LibGDX 자원을
     * 만들어도 안전하다. (생성자에서 미리 Screen 을 만들면 크래시 난다.)
     *
     * 보통 여기서 할 일:
     *   1. 첫 월드(GameWorld 의 자식) 를 만들고
     *   2. setScreen(...) 으로 등록 → 이후 LibGDX 가 매 프레임 그 월드를 렌더
     *
     *  GameWorld 가 LibGDX 의 Screen 인터페이스를 상속하므로 setScreen 인자로 넘길 수 있다.
     */
    override fun create() {

        // 메인 메뉴를 첫 화면으로 설정(MenuWorld 에서 game.startGame() 호출 가능하도록 OopGame 넘김)
        setScreen(MenuWorld(this))
    }

    //난이도 선택 메뉴
    fun openDifficultyMenu(){
        setScreen(DifficultyWorld(this))
    }

    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 게임 시작 메서드
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * MenuWorld 에서 게임 시작 버튼을 누르면 호출된다
     * PlayWorld 를 생성해 현재 화면으로 전환한다
     */
    fun startGame(difficultySystem: DifficultySystem) {
        com.oop.game.entity.Count.killCount=0
        com.oop.game.entity.Count.killPoint=0

        // PlayWorld 생성(화면 크기와 월드 크기 전달)
        val playWorld = PlayWorld(
            game = this,
            screenWidth = screenWidth.toFloat(),
            screenHeight = screenHeight.toFloat(),
            worldWidth = worldWidth.toFloat(),
            worldHeight = worldHeight.toFloat(),
            difficultySystem = difficultySystem
        )
        currentPlayWorld = playWorld
        // 현재 화면을 PlayWorld 로 전환(부모 Game 이 제공하는 메서드)
        setScreen(playWorld)
    }
    fun openLevelUpMenu() {
        setScreen(LevelUpWorld(this))
    }
    fun returnToPlayWorld() {
        val playWorld = currentPlayWorld?: return
        playWorld.finishLevelUp()
        setScreen(playWorld)
    }
    fun gameOver(survivalTime: Int, killCount: Int){
        setScreen(com.oop.game.world.GameOverWorld(this, survivalTime, killCount))

    }



    /**
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 자원 해제
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 앱이 종료될 때 현재 화면의 자원을 해제한다
     */
    override fun dispose() {

        // 현재 화면이 있으면 자원 해제
        if (screen != null) {
            screen.dispose()
        }

        // 부모 dispose 호출
        super.dispose()
    }
}
