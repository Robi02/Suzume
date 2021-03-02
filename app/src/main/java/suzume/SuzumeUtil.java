package suzume;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SuzumeUtil {

    /**
     * 플레이어가 버린적이 있는 패인지를 확인합니다.
     * @param player 대상 플레이어
     * @param tile 검사할 패
     * @return true: 버려진적이 있는 패 / false: 버려진적이 없는 패
     */
    public static boolean isDiscardedTileValue(Player player, Tile tile) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(tile);

        final int tileValue = tile.getValue();

        for (Tile t : player.getDiscardTiles()) {
            if (tileValue == t.getValue()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 현재 패의 점수를 계산합니다.
     * @param doraTile 도라 패
     * @param tileList 검사할 손패
     * @return 계산된 점수
     */
    public static int calcHuaryoScore(Tile doraTile, List<Tile> tileList) {
        Objects.requireNonNull(doraTile);
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
            int doraValue = doraTile.getValue();
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
}
