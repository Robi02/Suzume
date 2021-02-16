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
    private final List<Tile> discardTiles;

    // 생성자
    private Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.score = 0;
        this.handTiles = new LinkedList<>();
        this.discardTiles = new LinkedList<>();
    }

    // 정적 생성자
    public static Player of(String id, String name) {
        return new Player(id, name);
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

    public void addTileToDiscard(Tile tile) {
        if (tile == null) {
            return;
        }

        this.discardTiles.add(tile);
    }

    /**
     * 핸드와 버린 타일을 초기화합니다.
     */
    public void clearHandAndDiscard() {
        this.handTiles.clear();
    }
}
