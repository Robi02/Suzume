package suzume.actions;

import java.util.List;
import java.util.Objects;

import bgame.ActionResult;
import suzume.Player;
import suzume.RuleException;
import suzume.SuzumeSession;
import suzume.Tile;

public class DiscardAndPassTurnAction extends AbstractAction {

    private final Player actPlayer;
    private final Tile discardTile;

    /**
     * 생성자.
     * @param session 게임 세션
     * @param actPlayerId 액션을 수행하는 플레이어 아이디
     * @param discardTileId 버려질 패의 아이디
     */
    public DiscardAndPassTurnAction(SuzumeSession session, String actPlayerId, String discardTileId) {
        super(session);
        Objects.requireNonNull(this.actPlayer = session.getPlayerById(actPlayerId));
        Objects.requireNonNull(this.discardTile = session.getTileById(discardTileId));
    }

    /**
     * 선택한 패를 버리고 턴을 넘깁니다.
     */
    public ActionResult act() {
        final List<Tile> turnHolderHand = this.actPlayer.getHandTiles();

        if (session.getTurnHolder() != this.actPlayer) {
            throw RuleException.of("당신의 턴이 아닙니다.");
        }

        if (turnHolderHand.size() != 6) {
            throw RuleException.of("손패가 6개가 아닙니다.");
        }

        turnHolderHand.remove(discardTile);
        actPlayer.addTileToDiscard(discardTile);
        session.passTurnToNextPlayer();

        logger.info("{action:\"DiscardAndPassTurnAction\",session:\"" + session.getSessionId() +
                    "\",actPlayer:\"" + actPlayer.getId() + "\"discardTile:\"" + discardTile.getId() + "\"}");
        return null;
    }
}
