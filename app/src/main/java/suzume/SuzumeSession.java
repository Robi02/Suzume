package suzume;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;

/**
 * 참새작 (Suzume Jong)
 */
@Getter
public class SuzumeSession {

    // 상수
    public static final int MAX_PLAYER_CNT = 5;

    // 필드
    private final String sessionId;         // 세션 ID
    private final long sessionStartTimeMs;  // 게임 시작시점 시간
    private final SecureRandom random;      // 렌덤
    private int round;                      // 현재 라운드
    private final List<Player> playerList;  // 플레이어 리스트
    private final List<Tile> tileStock;     // 패 더미
    private Tile doraTile;                  // 도라 패
    private Player firstPlayer;             // 선 플레이어
    private Player turnHolder;              // 현시점 턴을 가진 플레이어

    // 생성자
    private SuzumeSession(String sessionId, List<Player> playerList) {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(playerList);

        int playerCnt = playerList.size();
        if (playerCnt < 2 || playerCnt > MAX_PLAYER_CNT) {
            throw new IllegalArgumentException("Player count must between 2~5! (playerCount: " + playerCnt + ")");
        }

        this.sessionId = sessionId;
        this.sessionStartTimeMs = System.currentTimeMillis();
        this.random = new SecureRandom(sessionId.getBytes());
        this.round = 1;
        this.playerList = playerList;
        this.tileStock = new LinkedList<>();
        this.doraTile = null;
        this.turnHolder = this.playerList.get(0);
    }

    // 메서드
    /**
     * 정적 생성자.
     * @param sessionId 사용할 세션 ID
     * @param playerList 플레이어 리스트
     * @return 생성된 게임 세션
     */
    public static SuzumeSession makeSession(String sessionId, List<Player> playerList) {
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(playerList);
        return new SuzumeSession(sessionId, playerList);
    }

    /**
     * 라운드(국) 초기화.
     */
    public void initRound() {
        // 도라패 정리
        this.doraTile = null;

        // 선 플레이어 결정
        int playerIdx = 0;
        for (Player player : this.playerList) {
            if (this.firstPlayer == player) {
                break;
            }

            ++playerIdx;
        }

        this.firstPlayer = this.playerList.get(playerIdx + 1 % this.playerList.size());

        // 패 더미 섞기
        this.tileStock.clear();
        this.tileStock.addAll(Tile.getDefinedTileList());
        Collections.shuffle(this.tileStock, this.random);

        // 플레이어 기본패 나눠주기
        for (Player player : this.playerList) {
            player.clearHand();

            for (int i = 0; i < 5; ++i) {
                player.addTileToHand(pickRandomTileFromStock());
            }
        }
    }

    /**
     * 쯔모(패 가져오기)를 수행합니다.
     * @param player 쯔모를 시도하는 플레이어
     */
    public void tsumo(Player player) {
        Objects.requireNonNull(player);

        if (this.turnHolder != player) {
            throw RuleException.of("당신의 턴이 아닙니다.");
        }
        
        if (this.turnHolder.getHandTiles().size() != 5) {
            throw RuleException.of("이미 패를 가져왔습니다.");
        }

        this.turnHolder.addTileToHand(pickRandomTileFromStock());
    }

    /**
     * 화료(점수 내기)를 수행합니다.
     * @param player 화료를 시도하는 플레이어
     */
    public void huaryo(Player player) {
        Objects.requireNonNull(player);

        if (this.turnHolder != player) {
            throw RuleException.of("당신의 턴이 아닙니다.");
        }

        if (this.turnHolder.getHandTiles().size() != 6) {
            throw RuleException.of("패가 6개가 아닙니다.");
        }

        // 패를 오름차순 정렬
        Collections.sort(this.turnHolder.getHandTiles(), new Comparator<Tile>(){
            @Override
            public int compare(Tile o1, Tile o2) {
                return o1.getId() - o2.getId();
            }
        });

        
    }

    /**
     * 
     * @param player
     * @param tile
     */
    public void dahae(Player player, Tile tile) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(tile);

        
    }

    /**
     * 더미로부터 무작위 타일을 획득합니다.
     * @return <code>tileStock</code>에서 획득한 무작위 타일
     */
    private Tile pickRandomTileFromStock() {
        if (this.tileStock.size() == 0) {
            return null;
        }

        return this.tileStock.remove(random.nextInt(this.tileStock.size()));
    }
}