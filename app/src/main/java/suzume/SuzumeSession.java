package suzume;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import bgame.Session;
import lombok.Getter;

/**
 * 참새작 (Suzume Jong)
 */
@Getter
public class SuzumeSession extends Session {

    // 상수
    public static final int MAX_PLAYER_CNT = 5;

    // 게임 연관 필드
    private final List<Player> playerList;          // 플레이어 리스트
    private final List<Tile> tileStock;             // 패 더미
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
        this.tileStock = new ArrayList<>();
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
     * 세션을 정리하고 닫습니다.
     */
    @Override
    public void closeSession() {
        this.sessionState = SessionState.CLOSING;
    }

    /**
     * 플레이어들의 행동을 수행합니다.
     * @param playerAction 행동을 시도하는 플레이어, 시점과 수행할 행동
     */
    public void interpreatAction(PlayerAction playerAction) {
        Objects.nonNull(playerAction);

        switch (playerAction.getAction()) {
            case SELECT_DORA_TILE:
                
                break;
            case TRY_TSUMO:

                break;
            case TRY_HUARYO:

                break;
            case DISCARD_TILE_AND_PASS_TURN:

                break;
            case TRY_LOAN:

                break;
            case EXIT_GAME:

                break;
            default:
                throw RuleException.of("미정의된 행동입니다.");
        }
    }

    /**
     * 라운드(국) 초기화.
     */
    public void initRound() {
        // 도라패 정리
        this.doraTile = null;

        // 선 플레이어 결정
        if (this.firstPlayer == null) {
            this.firstPlayer = this.playerList.get(0);
        }
        else {
            int playerIdx = 0;
            for (Player player : this.playerList) {
                if (this.firstPlayer == player) {
                    break;
                }

                ++playerIdx;
            }

            this.firstPlayer = this.playerList.get((playerIdx + 1) % this.playerList.size());
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
     * 도라(보너스패 선정)를 수행합니다.
     * @param player 도라를 시도하는 플레이어
     */
    public void dora(Player player) {
        Objects.requireNonNull(player);

        if (player != this.firstPlayer) {
            throw RuleException.of("선 플래이어가 아닙니다.");
        }

        this.doraTile = pickRandomTileFromStock();
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
     * @return 5점 이상일 시 true, 점수가 부족할 시 false
     */
    public boolean huaryo(Player player) {
        Objects.requireNonNull(player);

        if (this.turnHolder != player) {
            throw RuleException.of("당신의 턴이 아닙니다.");
        }

        if (this.turnHolder.getHandTiles().size() != 6) {
            throw RuleException.of("손패가 6개가 아닙니다.");
        }

        // 화료 점수 계산
        if (calcHuaryoScore(this.turnHolder.getHandTiles()) < 5) {
            return false;
        }
        
        return true;
    }

    /**
     * 선택한 패를 버리고 턴을 넘깁니다.
     * @param player 버리기를 시도하는 플레이어
     * @param tile 버려질 타일
     */
    public void discardTileAndPassTurn(Player player, Tile tile) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(tile);

        List<Tile> handTileList = player.getHandTiles();
        if (handTileList.size() != 6) {
            throw RuleException.of("손패가 6개가 아닙니다.");
        }

        player.getHandTiles().remove(tile);
        player.addTileToDiscard(tile);
    }

    /**
     * 론(상대가 버린 패로 점수 내기)을 시도합니다.
     * @param player 론을 시도하는 플레이어
     * @param loanTgtPlayer 론 당하는 플레이어
     * @param loanTile 론 대상 타일
     */
    public void loan(Player player, Player loanTgtPlayer, Tile loanTile) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(loanTgtPlayer);
        Objects.requireNonNull(loanTile);

        // 상대에게 실제 패가 있는지 검사
        boolean hasTile = false;
        for (Tile tile : loanTgtPlayer.getDiscardTiles()) {
            if (tile == loanTile) {
                hasTile = true;
                break;
            }
        }

        if (hasTile == false) {
            throw RuleException.of("론 대상에게서 패를 찾을 수 없습니다!");
        }

        // 내가 버린 종류인지 검사
        boolean hasDiscard = false;
        for (Tile tile : player.getDiscardTiles()) {
            if (tile.getValue() == loanTile.getValue()) {
                hasDiscard = true;
                break;
            }
        }

        if (hasDiscard) {
            throw RuleException.of("버린적 있는 패는 론 할 수 없습니다.");
        }

        // 화료 점수 계산
        player.getHandTiles().add(loanTile);

        final int score = calcHuaryoScore(this.turnHolder.getHandTiles());
        if (score < 5) {
            // 점수 감점
            // 패 공개
            return;
        }
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
     * 현재 패의 점수를 계산합니다.
     * @return 계산된 점수
     */
    public int calcHuaryoScore(List<Tile> tileList) {
        Objects.requireNonNull(tileList);

        if (tileList.size() != 6) {
            throw RuleException.of("손패가 6개가 아닙니다.");
        }

        boolean leftBody = false; // 좌측 3개 패 완성여부
        boolean rightBody = false; // 우측 3개 패 완성여부
        boolean isChinYao = true; // 칭야오 스위치 (모든 패가 1/9/발/중으로만 이루어짐)
        boolean isTangYao = true; // 탕야오 스위치 (모든 패가 2~8사이로만 이루어짐)
        boolean isChanTa = true; // 챤타 스위치 (두 개의 몸통 모두 1/9/발/중 포함)
        int redTileCnt = 0;
        int greenTileCnt = 0;
        int doraTileCnt = 0;
        int bodyScore = 0;
        int totalScore = 0;

        // 패를 오름차순으로 정렬
        Collections.sort(tileList); 

        // 좌(i=0), 우(i=1)패 3개씩 점수 계산
        for (int i = 0; i < 2; ++i) {
            final int idx = i * 2 + i;
            final Tile tile1 = tileList.get(idx);     // 0, 3
            final Tile tile2 = tileList.get(idx + 1); // 1, 4
            final Tile tile3 = tileList.get(idx + 2); // 2, 5
            final Tile.Color color1 = tile1.getColor();
            final Tile.Color color2 = tile2.getColor();
            final Tile.Color color3 = tile3.getColor();
            final int val1 = tile1.getValue();
            final int val2 = tile2.getValue();
            final int val3 = tile3.getValue();

            // 칭야오 확인 (모든 패가 1/9/발/중으로만 이루어짐)
            if (isChinYao) {
                if (1 < val1 && val1 < 9) isChinYao = false;
                if (1 < val2 && val2 < 9) isChinYao = false;
                if (1 < val3 && val3 < 9) isChinYao = false;
            }

            // 탕야오 확인 (모든 패가 2~8사이로만 이루어짐)
            if (isTangYao) {
                if (val1 < 2 || 8 < val1) isTangYao = false;
                if (val2 < 2 || 8 < val2) isTangYao = false;
                if (val3 < 2 || 8 < val3) isTangYao = false;
            }

            // 챤타 확인 (두 개의 몸통 모두 1/9/발/중 포함)
            if (isChanTa) {
                if ((1 < val1 && val1 < 9) && (1 < val2 && val2 < 9) && (1 < val3 && val3 < 9)) isChanTa = false;
            }

            // 적색패 개수 계산
            if (color1 == Tile.Color.RED) ++redTileCnt;
            if (color2 == Tile.Color.RED) ++redTileCnt;            
            if (color3 == Tile.Color.RED) ++redTileCnt;

            // 녹색패 개수 계산
            if (color1 == Tile.Color.GREEN) ++greenTileCnt;
            if (color2 == Tile.Color.GREEN) ++greenTileCnt;            
            if (color3 == Tile.Color.GREEN) ++greenTileCnt;

            // 도라 개수 계산
            int doraValue = this.doraTile.getValue();
            if (tile1.getValue() == doraValue) ++doraTileCnt;
            if (tile2.getValue() == doraValue) ++doraTileCnt;
            if (tile3.getValue() == doraValue) ++doraTileCnt;

            // 연속패(1,2,3) 검사 (+1)
            boolean isStright = false;
            if ((tile3.getValue() < Tile.VAL_BAL)) {
                if (tile1.getValue() == tile2.getValue() + 1) {
                    if (tile2.getValue() == tile3.getValue() + 1) {
                        if (i == 0) {
                            leftBody = true;
                        }
                        else {
                            rightBody = true;
                        }

                        bodyScore += 1;
                        isStright = true;
                    }
                }
            }

            // 동일패(1,1,1) 검사 (+2)
            if (isStright == false) {
                if (tile1.getValue() == tile2.getValue()) {
                    if (tile2.getValue() == tile3.getValue()) {
                        if (i == 0) {
                            leftBody = true;
                        }
                        else {
                            rightBody = true;
                        }

                        bodyScore += 2;
                    }
                }
            }
        }

        // 좌우 몸체가 하나라도 완성되지 않은 경우 0점
        if (leftBody == false || rightBody == false) {
            return 0;
        }
        
        // 역만: 올 그린
        if (greenTileCnt == 6) {
            return bodyScore + 10;
        }

        // 역만: 칭야오
        if (isChinYao) {
            return bodyScore + 15;
        }

        // 역만: 슈퍼 레드
        if (redTileCnt == 6) {
            return bodyScore + 20;
        }

        totalScore = bodyScore;

        // 보너스: 적색 패 점수 계산
        totalScore += redTileCnt;
        
        // 보너스: 도라 패 점수 계산
        totalScore += doraTileCnt;
        
        // 보너스: 탕야오
        if (isTangYao) totalScore += 1;

        // 보너스: 챤타
        if (isChanTa) totalScore += 2;

        return totalScore;
    }

    /**
     * 다음 플레이어로 턴을 넘깁니다.
     * @apiNote <code>turnHolder</code>가 <code>null</code>일 경우, <code>firstPlayer</code>가 턴 소유자가 됩니다.
     */
    public void passTurnToNextPlayer() {
        if (this.turnHolder == null) {
            this.turnHolder = this.firstPlayer;
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
}