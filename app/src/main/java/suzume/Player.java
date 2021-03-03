package suzume;

import java.util.Arrays;
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
     * 핸드의 가장 마지막에 패을 추가합니다.
     * @param tile 추가할 패
     */
    public void addTileToHand(Tile tile) {
        if (tile == null) {
            return;
        }

        this.handTiles.add(tile);
    }

    /**
     * 버린 패 더미의 가장 마지막에 패를 추가합니다.
     * @param tile 버릴 패
     */
    public void addTileToDiscard(Tile tile) {
        if (tile == null) {
            return;
        }

        this.discardTiles.add(tile);
    }

    /**
     * 핸드와 버린 패를 초기화합니다.
     */
    public void clearHandAndDiscard() {
        this.handTiles.clear();
        this.discardTiles.clear();
    }

    /**
     * 플레이어를 문자열로 출력합니다.
     * @return <code>{'id','name','score','handTiles','discardTiles'}</code>순서로 문자열로 변환
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("{")
          .append("id:" + id)
          .append(",name:" + name)
          .append(",score:" + score)
          .append(",handTiles:" + Arrays.toString(handTiles.toArray()))
          .append(",discardTiles:" + Arrays.toString(discardTiles.toArray()))
          .append("}");

        return sb.toString();
    }
}
