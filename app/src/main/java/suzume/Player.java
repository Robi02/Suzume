package suzume;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

@Getter
public class Player {
    
    private final String id;
    private final String name;
    private int score;
    private final List<Tile> handTiles;

    // 생성자
    private Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
        this.handTiles = new LinkedList<>();
    }

    /**
     * 핸드의 가장 마지막에 타일을 추가합니다.
     * @param tile 추가할 타일
     */
    public void addTileToHand(Tile tile) {
        if (tile == null) {
            return;
        }

        this.handTiles.add(tile);
    }

    /**
     * 핸드를 초기화합니다.
     */
    public void clearHand() {
        this.handTiles.clear();
    }
}