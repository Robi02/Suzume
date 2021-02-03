package suzume;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Tile {
    
    // 상수
    public static final int VAL_BAL = 10; // 특수패: 발(發)
    public static final int VAL_JUNG = 11; // 특수패: 중(中)

    // 열거형
    public static enum Color {
        RED, GREEN, MIXED; // 적색패, 녹색패, 혼합패
    }

    // 필드
    private static final List<Tile> tileList; // 게임에서 사용되는 정의된 패를 담은 리스트
    private final int id;
    private final int value;
    private final Color color;

    // 정적 초기화
    static {
        tileList = new ArrayList<>();
        int idCnt = 0;

        // 녹색/혼합 일반패 (27개)
        for (int set = 0; set < 3; ++set) {
            for (int val = 1; val <= 9; ++val) {
                tileList.add(new Tile(++idCnt, val, getTileColorByVal(val)));
            }
        }

        // 적색 일반패 (9개)
        for (int val = 1; val <= 9; ++val) {
            tileList.add(new Tile(++idCnt, val, Color.RED));
        }

        // 녹색 특수패: 발 (4개)
        for (int set = 0; set < 4; ++set) {
            tileList.add(new Tile(++idCnt, VAL_BAL, Color.GREEN));
        }

        // 적색 특수패: 중 (4개)
        for (int set = 0; set < 4; ++set) {
            tileList.add(new Tile(++idCnt, VAL_JUNG, Color.RED));
        }
    }

    // 생성자
    private Tile(int id, int value, Color color) {
        this.id = id;
        this.value = value;
        this.color = color;
    }

    /**
     * val값에 따른 타일 색상을 반환합니다. 초기 타일 생성에 사용되는 메서드입니다.
     * @param val 타일 숫자값
     * @return 해당 숫자의 색상을 반환합니다. <code>Color.RED</code>(적색패)는
     * <code>val</code>이 <code>VAL_JUNG</code>일 때만 반환됩니다.
     */
    private static Color getTileColorByVal(int val) {
        if (val == 1 || val == 5 || val == 9) {
            return Color.MIXED; // 1, 5, 9
        }
        else if (val == VAL_JUNG) {
            return Color.RED; // 중
        }

        return Color.GREEN; // 2, 3, 4, 6, 7, 8, 발
    }

    /**
     * 참새작에서 사용하는 모든 타일을 담고 있는 리스트를 반환합니다.
     * @apiNote 이 리스트는 모든 참새작 게임 세션에서 공용으로 사용하므로,
     * <strong>값의 추가 삭제를 엄금하며 오직 조회(get)에만 사용</strong>해야 합니다.
     * @return 클래스 초기화시 생성된 타일들을 담은 리스트
     */
    public static List<Tile> getDefinedTileList() {
        return tileList;
    }
}