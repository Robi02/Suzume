package suzume;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerAction {
    
    // 게임 액션
    public enum Action {
        SELECT_DORA_TILE,               // 도라타일 선택
        TRY_TSUMO,                      // 쯔모 시도
        TRY_HUARYO,                     // 화료 시도
        DISCARD_TILE_AND_PASS_TURN,     // 타일 버리고 턴 넘기기
        TRY_LOAN,                       // 론 시도
        EXIT_GAME;                      // 게임 나가기
    }

    // 필드
    private final Player player;  // 시도하는 플레이어
    private final long timeMs;    // 시도하는 시점 (서버)
    private final Action action;  // 시도하는 액션

    // 정적 생성자
    public static PlayerAction of(Player player, Action action) {
        return new PlayerAction(player, System.currentTimeMillis(), action);
    }
}
