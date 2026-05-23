package com.oop.game.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.oop.game.system.DifficultySystem
import com.oop.game.GameWorld
import com.oop.game.InputHandler
import com.oop.game.entity.Player
import com.oop.game.entity.Enemy
import com.oop.game.entity.Bullet
import com.oop.game.entity.Count
import com.oop.game.entity.Item
import com.oop.game.entity.entityEnum.Items
import com.oop.game.entity.entityEnum.Elites
import com.oop.game.entity.entityEnum.Normals
import com.oop.game.entity.enemy.elite.Brute
import com.oop.game.entity.enemy.elite.Ghost
import com.oop.game.entity.enemy.nomal.Bloater
import com.oop.game.entity.enemy.nomal.Grunt
import com.oop.game.entity.enemy.nomal.Splitter
import com.oop.game.entity.item.Heart
import com.oop.game.entity.item.Booster
import kotlin.math.floor
import kotlin.random.Random

/**
 * ════════════════════════════════════════════════════════════
 *  게임 월드 — Player vs Enemy 벰서 게임 (이미지 사용).
 * ════════════════════════════════════════════════════════════
 *
 *  GameWorld 를 상속해 만든 가장 작은 플레이 가능한 월드.
 *
 *  ── 조작법 ──
 *   ▸ WASD       : 플레이어 이동 (월드가 화면보다 커서 탐험 가능)
 *   ▸ Left Click : 총알 발사 (마우스 커서 방향으로 총알 발사)
 *
 *  ── 사용 이미지 (core/src/main/resources/) ──
 *   ▸ player.png  — 50x50 플레이어 스프라이트
 *   ▸ (ex: enemy).png   — n x n 적 스프라이트
 *   ▸ tile.png    — 64x64 흰색 정사각형 (체스판 배경에 색만 입혀 사용)
 *
 *  ── 게임 상태 ──
 *   IN_PLAY   : 일반 진행 (이동·충돌 체크)
 *   GAME_OVER : 충돌 후 정지, ESC 입력 대기
 *
 *  ── 텍스트 데모 ──
 *   ▸ 좌측 상단 "Heart: n"       — 화면 좌표 (카메라 움직여도 고정)
 *   ▸ 좌측 상단 "Level: n"       — 화면 좌표 (카메라 움직여도 고정)
 *   ▸ 좌측 상단 "Time: n"        — 화면 좌표 (카메라 움직여도 고정)
 *
 *  ── 배경 ──
 *   tile.png(흰 사각형)를 두 가지 색으로 틴트해 체스판처럼 깐다.
 *   카메라 이동을 눈으로 보여주기 위함이다.
 *   GameWorld.drawBackground(batch) 를 override 해서 그린다.
 *
 * @param screenWidth  화면에 보이는 영역 너비
 * @param screenHeight 화면에 보이는 영역 높이
 * @param worldWidth   월드 전체 너비 (화면보다 크면 WASD 로 탐험 가능)
 * @param worldHeight  월드 전체 높이
 */
class PlayWorld (
    screenWidth: Float,
    screenHeight: Float,
    worldWidth: Float,
    worldHeight: Float,
    difficultySystem: DifficultySystem
) : GameWorld(screenWidth, screenHeight, worldWidth, worldHeight) {

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 게임 상태 이넘
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 게임의 상태는 | 1. 시작 메뉴 | 2. 게임 중 | 3. 레벨 업 | 4. 게임 오버 |
    private enum class GameState {
        IN_PLAY,
        //LEVEL_UP,
        GAME_OVER
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 생존 시간 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 현재 생존 시간(Float형)
    private var timeFloat: Float = 0f

    // 현재 생존 시간(Int형)
    private var timer: Int = 0

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 플레이어 프로퍼티
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 플레이어 — 월드 중앙 하단에서 시작.
    // 월드 크기를 함께 넘겨, 경계 밖으로 못 나가게 한다.
    private val player: Player = Player(
        // 가로/세로 50 의 절반을 빼서 정확히 중앙
        x = worldWidth / 2 - 25f,
        y = worldHeight / 2 - 25f,
        worldWidth = worldWidth,
        worldHeight = worldHeight,
        difficultySystem = difficultySystem
    )

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 적 생성 프로퍼티 및 메서드
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 현재 남은 적 스폰 시간
    var enemySpawnTimer: Float = 0f

    // 적의 스폰 속도(난이도 별 수정)
    var enemySpawnSpeed: Float = 3f

    // 일정 시간 후 늘어나는 적의 체력 증가폭(난이도 별 수정)
    var heartIncrement: Float = difficultySystem.heartIncrement

    /** 적 생성 좌표 메서드*/
    // 적의 x 좌표(카매라 x좌표 밖 랜덤한 x좌표)
    // offsetX 보다 작은 값 또는 offsetX + screenWidth 보다 큰 값 중 랜덤하게 선택
    private fun randomSpawnX(): Float {
        return if (Random.nextBoolean()) {
            Random.nextFloat() * offsetX
        } else {
            Random.nextFloat() * (worldWidth - (offsetX + screenWidth)) + (offsetX + screenWidth)
        }

    }

    // 적의 y 좌표(카매라 y좌표 밖 랜덤한 y좌표)
    // offsetY 보다 작은 값 또는 offsetY + screenHeight 보다 큰 값 중 랜덤하게 선택
    private fun randomSpawnY(): Float {
        return if (Random.nextBoolean()) {
            Random.nextFloat() * offsetY
        } else {
            Random.nextFloat() * (worldHeight - (offsetY + screenHeight)) + (offsetY + screenHeight)
        }
    }

    /** 일반 적 생성 메서드 */
    fun spawnEnemies(heartIncrement: Float, delta: Float) {

        // 스폰 타이머가 0보다 작으면 적 생성
        if (enemySpawnTimer <= 0f) {

            // 스폰 타임 증가
            enemySpawnTimer = enemySpawnSpeed

            // 적의 x좌표
            val x = randomSpawnX()

            // 적의 y좌표
            val y = randomSpawnY()

            // enum class Normals에서 랜덤하게 엘리트 고르기
            val randomSpawnEnemies = Normals.entries.random()

            // 랜덤하게 적 생성
            val enemy: Enemy = when (randomSpawnEnemies) {
                Normals.Bloater  -> Bloater(x, y, worldWidth, worldHeight, player, true)
                Normals.Grunt    -> Grunt(x, y, worldWidth, worldHeight, player, true)
                Normals.Splitter -> Splitter(x, y, worldWidth, worldHeight, player, true)
            }

            // 일정 시간이 지난 후 적의 체력이 일괄적으로 +n 씩 올라감
            enemy.heartIncrease((timer / 60) * heartIncrement)

            // 적 생성
            add(enemy)
        }

        // 스폰 타임 감소
        enemySpawnTimer -= delta
    }

    // 엘리트 적 생성 조건(일반 적 처치 수(난이도 별 수정))
    var killThreshold: Int = 10

    /** 엘리트 적 생성 메서드 */
    fun spawnElites(killThreshold: Int) {

        // 킬 카운트
        val count: Int = Count.killCount

        // 적 객체를 10마리 잡으면 엘리트 적 생성
        if (count == killThreshold) {

            // 적의 x좌표
            val x = randomSpawnX()

            // 적의 y좌표
            val y = randomSpawnY()

            // 프레임 마다 한 번만 생성(킬 카운트를 초기화 시킴)
            Count.killCount = 0

            // enum class Elites에서 랜덤하게 엘리트 고르기
            val randomSpawnElite = Elites.entries.random()

            // 랜덤하게 적 생성
            val elite: Enemy = when (randomSpawnElite) {
                Elites.Brute -> Brute(x, y, worldWidth, worldHeight, player, true)
                Elites.Ghost -> Ghost(x, y, worldWidth, worldHeight, player, true)
            }

            // 일정 시간이 지난 후 적의 체력이 일괄적으로 +n 씩 올라감
            elite.heartIncrease((timer / 60) * heartIncrement)

            // 적 생성
            add(elite)
        }
    }


    /** 총알 발사 메서드*/
    fun shooting(isShooting: Boolean) {

        if (isShooting) {

            // 신호 초기화(플레이어가 총알을 발사했다는 신호)
            player.isShooting = false

            // 마우스의 x좌표(전체 월드 기준)
            val worldMouseX = InputHandler.getMouseX() + offsetX

            // 마우스의 y좌표(전체 월드 기준)
            val worldMouseY = InputHandler.getMouseY() + offsetY

            // 마우스 커서 방향으로 날라가는 총알 객체
            val bullet = Bullet(player.x, player.y, worldMouseX, worldMouseY, worldWidth, worldHeight)

            // 총알 객체 생성
            add(bullet)

            // 총알이 맵 밖으로 나가면 삭제
            if (bullet.isOutOfBounds) {

                // 총알 객체 삭제
                remove(bullet)

                // 신호 초기화(경계 밖으로 나갔다는 신호)
                bullet.isOutOfBounds = false
            }
        }
    }

    /*
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     * 아이템 생성 프로퍼티 및 메서드
     * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
     */
    // 현재 남은 적 스폰 시간
    var itemSpawnTimer: Float = 5f

    // 적의 스폰 속도(난이도 별 수정)
    var itemSpawnSpeed: Float = 10f

    /** 아이템 생성 메서드*/
    fun spawnItem(delta: Float) {

        // 스폰 타이머가 0보다 작으면 아이템 생성
        if (itemSpawnTimer <= 0f) {

            // 스폰 타임 증가
            itemSpawnTimer = itemSpawnSpeed

            // 아이템의 x좌표
            val x: Float = Random.nextFloat() * worldWidth

            // 아이템의 y좌표
            val y: Float = Random.nextFloat() * worldHeight

            // enum class Items에서 랜덤하게 아이템 고르기
            val randomSpawnItem = Items.entries.random()

            // 골라진 아이템을 생성하기
            val item: Item = when(randomSpawnItem) {
                Items.Heart   -> {Heart(x, y, worldWidth, worldHeight)}
                Items.Booster -> {Booster(x, y, worldWidth, worldHeight)}
            }

            // 아이템 생성
            add(item)
        }

        // 스폰 타임 감소
        itemSpawnTimer -= delta
    }

    // 현재 남은 부스터 시간
    var boosterTimer: Float = 0f

    // 부스터 시간
    val boosterTime: Float = 4f

    // 현재 게임 상태
    private var state = GameState.IN_PLAY

    // ── 체스판 배경 설정 (drawBackground() 에서 사용) ──
    //   이게 없으면 검은 배경뿐이라 카메라(WASD) 이동이 눈에 안 보인다.
    //   학생은 자기 게임에선 다른 배경을 그리거나,  두면 검은 배경이다.
    //
    //   tile.png 는 흰색 64x64 정사각형 한 장. 같은 텍스처에 batch.color 를
    //   바꿔가며 두 가지 색으로 그리는 트릭(틴트) 으로 체스판을 만든다.
    private val tileTexture1 = difficultySystem.tileTexture1
    private val tileTexture2 = difficultySystem.tileTexture2
    private val tileSize = 512f

    /**
     * 생성자 본문 — 월드에 플레이어와 적을 등록한다.
     *   이렇게 등록해야 update / draw 루프에 포함된다.
     */
    init {
        add(player)
    }

    override fun update(delta: Float) {
        super.update(delta)
        when (state) {
            GameState.IN_PLAY -> updateInPlay(delta)
            GameState.GAME_OVER -> updateGameOver()
        }
    }

    /** IN_PLAY 상태에서 매 프레임 처리 - 플레이어 이동, 객체 갱신, 충돌 체크. */
    private fun updateInPlay(delta: Float) {

        // 타이머(실수)
        timeFloat += delta

        // 타이머 시간 정수화
        timer = timeFloat.toInt()

        // 플레이어 객체를 월드 경계 안쪽으로 가두기
        player.x = player.x.coerceIn(0f, worldWidth - player.width)
        player.y = player.y.coerceIn(0f, worldHeight - player.height)

        // ── 카메라 이동 (항상 플레이어를 중앙에 위치하도록 이동) ──
        //   offsetX/Y 를 바꾸면 카메라가 월드 안에서 움직인다.
        // 플레이어의 좌표에서 창의 절반을 빼줘서 플레이어를 중앙에 오게 만듬
        offsetX = (player.x + 25f) - screenWidth / 2
        offsetY = (player.y + 25f) - screenHeight / 2

        // 카메라가 월드 경계 밖을 보여주지 않도록 clamp.
        //   보여주는 영역이 [offset, offset+screen] 이어야 하므로
        //   offset 은 0 ~ (world - screen) 범위여야 한다.
        offsetX = offsetX.coerceIn(0f, worldWidth - screenWidth)
        offsetY = offsetY.coerceIn(0f, worldHeight - screenHeight)

        // 적 생성
        spawnEnemies(heartIncrement, delta)

        // 엘리트 적 생성
        spawnElites(killThreshold)

        // 총알 발사
        shooting(player.isShooting)

        // 아이템 생성
        spawnItem(delta)

        /** 총알과 적 충돌 처리 */
        for (obj in getObjects()) {

            // 객체 모음집에서 총알 객체를 찾음
            if (obj is Bullet) {

                // 이 총알과 충돌한 적 객체를 찾음
                for (enemy in getObjects().filterIsInstance<Enemy>()) {

                    if (enemy is Bloater && enemy.isBoom || enemy is Ghost && enemy.isGhostMode) continue

                    // 살이있는 적 객체만 맞추고 맞으면 총알 객체 삭제
                    if (obj.collidesWith(enemy) && enemy.isAlive()) {

                        // 그 enemy와 충돌한 bullet으로 스마트 캐스트
                        // 적 객체 데미지 입히기
                        enemy.onDamage(obj.damage)

                        // 충돌 후 총알 객체 삭제
                        remove(obj)

                        break
                    }
                }
            }
        }

        /** 플레이어와 적 충돌 처리*/
        for (enemy in getObjects().filterIsInstance<Enemy>()) {

            // 플레이어와 충돌한 적을 찾음
            if (player.collidesWith(enemy) && enemy.isAlive()) {

                // enemy가 충돌한 enemy로 스마트 캐스트
                // 플레이어 객체 데미지 입히기
                player.onDamage(enemy.damage)

                // Bloater 객체라면 바로 폭발
                if (enemy is Bloater) {

                    // 바로 폭발 상태로 변경
                    enemy.isBoom = true
                }

                // 플레이어 사망 시
                if (!player.isAlive()) {

                    // 게임 오버 상태로 전환
                    state = GameState.GAME_OVER
                }
            }
        }

        /** 죽인 적 객체가 Bloater일 때 처리*/
        for (enemy in getObjects().filterIsInstance<Bloater>()) {

            // 폭발이 끝나면
            if (enemy.isExplosionFinished()) {

                // 적 객체 삭제
                remove(enemy)
            }
        }

        /** 죽인 적 객체가 Splitter일 때 처리*/
        for (enemy in getObjects().filterIsInstance<Splitter>()) {

            // 적이 죽으면 분열
            if (!enemy.isAlive() && enemy.canSplit) {

                // 분열한 적의 하트 수
                val newHeartCount: Int = (enemy.defaultHeart / 2)

                // 남은 새로운 하트로 분열 가능성 판단
                if (newHeartCount > 0) {

                    // 분열 가능성 초기화
                    enemy.canSplit = false

                    // 분열된 적 2마리 생성
                    repeat(2) { i ->

                        // 분열된 적의 생성 위치(첫 번째는 오른쪽 두 번째는 왼쪽)
                        val offset = if (i == 0) -40f else 40f

                        // 분열된 적
                        val newEnemy = Splitter(enemy.x + offset, enemy.y + offset, worldWidth, worldHeight, player, false)

                        // 분열된 적의 크기 및 체력 수정
                        newEnemy.heart = newHeartCount.toFloat()
                        newEnemy.width *= 0.95f
                        newEnemy.height *= 0.95f
                        newEnemy.defaultHeart = newHeartCount
                        newEnemy.getPoint = false

                        add(newEnemy)
                    }
                }
            }
        }

        /** 죽인 적 객체가 Brute일 때 처리*/
        for (enemy in getObjects().filterIsInstance<Brute>()) {

            // 광폭화 유무 판단
            if (enemy.getEnraged) {

                // 광폭화 시 텍스쳐 변경
                enemy.texture = Texture(Gdx.files.internal("brute_enraged.png"))
            }
        }

        /** 죽인 적 객체가 Ghost일 때 처리*/
        for (enemy in getObjects().filterIsInstance<Ghost>()) {

            // 유령화 유무 판단
            if (enemy.isGhostMode) {

                // 유령화 시 텍스쳐 변경
                enemy.texture = Texture(Gdx.files.internal("ghost_mode.png"))
            } else {

                // 유령화 끝나면 텍스쳐 변경
                enemy.texture = Texture(Gdx.files.internal("ghost.png"))
            }
        }

        /** 아이템과 겹칠 때 처리*/
        for (item in getObjects().filterIsInstance<Item>()) {

            // 플레이어와 겹친 아이템을 찾음
            if (player.collidesWith(item)){

                when (item) {
                    is Heart -> {
                        // 아이템 삭제
                        remove(item)

                        // 플레이어 체력 +1
                        player.heart += 1
                    }

                    is Booster -> {

                        // 부스터 시간 추가
                        boosterTimer += boosterTime

                        // 아이템 삭제
                        remove(item)

                        // 남아있는 부스터 시간이 0 이상 이면
                        if (boosterTimer >= 0f) {

                            // 플레이어 속도 2배
                            player.speed *= 2
                        }
                    }
                }
            }
        }

        // 부스터 시간 감소
        if (boosterTimer > 0f) {

            // 매 프레임 delta만큼 시간 감소
            boosterTimer -= delta

            // 부스터 시간이 0 이하가 되면
            if (boosterTimer <= 0f) {

                // 플레이어의 속도를 다시 기본 속도로 변경
                player.speed = player.defaultSpeed
            }
        }
    }

    /** GAME_OVER 상태에서 매 프레임 처리 — ESC 입력만 감시한다. */
    private fun updateGameOver() {
        // ESC 키가 '막 눌린 순간' 앱 종료.
        //   isKeyJustPressed 로 한 이유: 누르고 있는 동안 매 프레임 exit 호출되지 않게.
        if (InputHandler.isKeyJustPressed(InputHandler.ESCAPE)) {
            Gdx.app.exit()
        }
    }

    /**
     * 배경 그리기 — GameWorld.drawBackground(batch) 를 override.
     *
     * 부모가 이미 batch.begin() 을 호출한 상태에서 이 함수를 부르므로,
     * 여기선 batch.draw() 호출만 하면 된다. (begin/end 를 또 부르면 안 된다)
     *
     * 카메라(offset) 에 따라 타일 위치가 바뀌어 이동감을 준다.
     *   타일 인덱스 자체는 월드 좌표 격자에서 변하지 않지만,
     *   각 타일을 그릴 때 offset 만큼 빼서 화면 좌표로 변환한다.
     *
     * 색을 입히는 방법:
     *   batch.color 를 바꾼 뒤 batch.draw 하면 텍스처가 그 색으로 곱해져 그려진다.
     *   tile.png 가 흰색이라 어떤 색이든 그대로 적용된다.
     *   끝에 다시 흰색으로 되돌려두지 않으면 그 다음 그리는 것까지 영향을 받으니 주의.
     */
    override fun drawBackground(batch: SpriteBatch) {
        // 현재 카메라 시작점이 속한 타일 인덱스 (여유분으로 -1)
        val startCol = floor(offsetX / tileSize).toInt() - 1
        val startRow = floor(offsetY / tileSize).toInt() - 1
        // 화면을 채우는 데 필요한 타일 개수 (여유분 +3)
        val cols = (screenWidth / tileSize).toInt() + 3
        val rows = (screenHeight / tileSize).toInt() + 3

        for (row in startRow until startRow + rows) {
            for (col in startCol until startCol + cols) {
                // 행+열이 짝수면 어둡게, 홀수면 밝게 → 체스판 패턴
                val texture = if ((row + col) % 2 == 0) tileTexture1 else tileTexture2

                // 월드 좌표의 타일 위치에서 offset 만큼 빼면 화면 좌표
                val drawX = col * tileSize - offsetX
                val drawY = row * tileSize - offsetY
                batch.draw(texture, drawX, drawY, tileSize, tileSize)
            }
        }
    }

    /**
     * 매 프레임 그리기 — 부모가 배경·객체까지 그려준 뒤, 텍스트 UI 를 얹는다.
     *
     * 이 함수에서는 '그리기' 만 한다. 입력 처리·상태 변경은 update() 의 책임.
     *
     * 주의: super.render(delta) 가 화면 clear + 배경 + 객체까지 그리므로,
     *       텍스트는 반드시 super 호출 **이후** 그려야 가려지지 않는다.
     */
    override fun render(delta: Float) {
        super.render(delta)

        // ── 항상 보이는 UI ──
        drawHud()

        // ── 상태별로 그리는 것이 다름 ──
        when (state) {
            GameState.IN_PLAY -> {
                // 플레이 중에는 추가로 그릴 것 없음
            }
            GameState.GAME_OVER -> drawGameOverOverlay()
        }
    }

    /** 항상 화면에 표시되는 정보 — HP 표시와 월드 중앙 표지. */
    private fun drawHud() {
        // 1) UI 텍스트 (화면 고정) — 좌측 상단 HP 표시.
        //    카메라가 움직여도 항상 이 위치에 있다.
        drawTextOnScreen(
            text = "Heart: ${player.heart}",
            x = 10f,
            y = screenHeight - 10f,   // 화면 y 축은 위로 증가 → 맨 위가 screenHeight
            color = Color.YELLOW,
            scale = 1.2f
        )

        drawTextOnScreen(
            text = "Level: ${player.level}",
            x = 100f,
            y = screenHeight - 10f,
            color = Color.ORANGE,
            scale = 1.2f
        )

        drawTextOnScreen(
            text = "Time: $timer",
            x = 190f,
            y = screenHeight - 10f,
            color = Color.GREEN,
            scale = 1.2f
        )

        drawTextOnScreen(
            text = "Kill: ${Count.killPoint}",
            x = 270f,
            y = screenHeight - 10f,
            color = Color.GREEN,
            scale = 1.2f
        )
    }

    /** 게임 오버 시 화면 중앙에 띄우는 안내 메시지. */
    private fun drawGameOverOverlay() {
        drawTextOnScreen(
            text = "Game Over!",
            x = screenWidth / 2 - 80f,
            y = screenHeight / 2,
            color = Color.WHITE,
            scale = 2f
        )

        drawTextOnScreen(
            text = "Press ESC to exit",
            x = screenWidth / 2 - 70f,
            y = screenHeight / 2 - 40f,
            color = Color.WHITE,
            scale = 1f
        )
    }

    /** 화면이 닫힐 때 — 부모도 dispose 한 뒤 우리만의 자원도 해제. */
    override fun dispose() {
        super.dispose()
        tileTexture1.dispose()
        tileTexture2.dispose()
    }
}