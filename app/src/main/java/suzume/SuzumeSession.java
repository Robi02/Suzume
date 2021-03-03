package suzume;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import bgame.Action;
import bgame.ActionResult;
import bgame.Session;
import lombok.Getter;
import lombok.Setter;

/**
 * 참새작 (Suzume Jong)
 */
@Getter
@Setter
public class SuzumeSession extends Session {

    // 열거형
    public enum SuzumeState {
        WAITING_DORA,
        WAITING_THUMO,
        WAITING_DISCARD,
    }

    // 상수
    public static final int MAX_PLAYER_CNT = 5;

    // 게임 연관 필드
    private final List<Player> playerList;          // 플레이어 리스트
    private final List<Tile> tileList;              // 패 리스트
    private final List<Tile> tileStock;             // 패 더미
    private boolean loanable;                       // 론 가능 여부
    private int round;                              // 현재 라운드
    private Tile doraTile;                          // 도라 패
    private Player roundStartPlayer;                // 라운드의 선 플레이어
    private Player turnHolder;                      // 현시점 턴을 가진 플레이어

    /**
     * 내부 생성자.
     * @param sessionId 세션 ID로 사용할 고유한 값
     * @param playerList 플레이어 정보가 담긴 리스트
     * @throws IllegalArgumentException 플레이어 수가 2보다 작거나 <code>MAX_PLAYER_CNT</code>보다 큰 경우.
     */
    private SuzumeSession(String sessionId, List<Player> playerList) {
        super(sessionId);
        
        Objects.requireNonNull(sessionId);
        Objects.requireNonNull(playerList);

        int playerCnt = playerList.size();
        if (playerCnt < 2 || playerCnt > MAX_PLAYER_CNT) {
            throw new IllegalArgumentException("Player count must between 2~5! (playerCount: " + playerCnt + ")");
        }

        this.playerList = playerList;
        this.tileList = Collections.unmodifiableList(new ArrayList<>(Tile.getDefinedTileList()));
        this.tileStock = new ArrayList<>();
        this.loanable = false;
        this.round = 1;
        this.doraTile = null;
        this.roundStartPlayer = this.playerList.get(0);
        this.turnHolder = this.playerList.get(0);
    }

    // 메서드
    /**
     * 정적 생성자.
     * @param sessionId 사용할 세션 ID
     * @param playerList 플레이어 리스트
     * @return 생성된 게임 세션
     */
    public static SuzumeSession openSession(String sessionId, List<Player> playerList) {
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
        if (this.roundStartPlayer == null) {
            this.roundStartPlayer = this.playerList.get(0);
        }
        else {
            int playerIdx = 0;
            for (Player player : this.playerList) {
                if (this.roundStartPlayer == player) {
                    break;
                }

                ++playerIdx;
            }

            this.roundStartPlayer = this.playerList.get((playerIdx + 1) % this.playerList.size());
        }

        // 패 더미 섞기
        this.tileStock.clear();
        this.tileStock.addAll(Tile.getDefinedTileList());
        Collections.shuffle(this.tileStock, this.random);

        // 플레이어 기본패 나눠주기
        for (Player player : this.playerList) {
            player.clearHandAndDiscard();

            for (int i = 0; i < 5; ++i) {
                player.addTileToHand(pickRandomTileFromStock());
            }
        }
    }

    /**
     * 플레이어 액션을 수행합니다.
     * @param action 플레이어의 액션
     * @return 액션의 결과.
     * @apiNote 이 메서드는 <code>synchronized</code>로 동작하여 thread-safe를 보장합니다.
     */
    public synchronized ActionResult doAction(Action action) {
        Objects.requireNonNull(action);
        return action.act();
    }

    
    /**
     * 세션 내 해당 id의 플레이어를 반환합니다.
     * @param id 플레이어 아이디
     * @return 해당 id를 가진 플레이어를 반환.
     */
    public Player getPlayerById(String id) {
        Objects.requireNonNull(id);

        for (Player player : this.playerList) {
            if (player.getId().equals(id)) {
                return player;
            }
        }

        return null;
    }

    /**
     * 세션 내 해당 id의 패를 반환합니다.
     * @param id 패 아이디
     * @return 해당 id를 가진 패를 반환.
     */
    public Tile getTileById(String id) {
        Objects.requireNonNull(id);
        
        final int tileId = Integer.valueOf(id);

        for (Tile tile : this.tileList) {
            if (tileId == tile.getId()) {
                return tile;
            }
        }

        return null;
    }

    /**
     * 더미로부터 무작위 타일을 획득합니다.
     * @return <code>tileStock</code>에서 획득한 무작위 타일
     */
    public Tile pickRandomTileFromStock() {
        if (this.tileStock.size() == 0) {
            return null;
        }

        return this.tileStock.remove(random.nextInt(this.tileStock.size()));
    }

    /**
     * 다음 플레이어로 턴을 넘깁니다.
     * @apiNote <code>turnHolder</code>가 <code>null</code>일 경우, <code>firstPlayer</code>가 턴 소유자가 됩니다.
     */
    public void passTurnToNextPlayer() {
        if (this.turnHolder == null) {
            this.turnHolder = this.roundStartPlayer;
        }
        else {
            int playerIdx = 0;
            for (Player player : this.playerList) {
                if (this.turnHolder == player) {
                    break;
                }

                ++playerIdx;
            }

            this.turnHolder = this.playerList.get((playerIdx + 1) % this.playerList.size());
        }
    }

    /**
     * 게임 세션 정보를 문자열로 출력합니다.
     * @return 문자열로 변환된 세션 정보
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{")
          .append("playerList:" + Arrays.toString(playerList.toArray()))
          .append(",tileStock:" + Arrays.toString(tileStock.toArray()))
          .append(",round:" + round)
          .append(",doraTile:" + doraTile)
          .append(",roundStartPlayer:" + roundStartPlayer)
          .append(",turnHolder:" + turnHolder)
          .append("}");

        return sb.toString();
    }
}