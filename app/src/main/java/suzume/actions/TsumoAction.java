package suzume.actions;

import bgame.ActionResult;
import suzume.Player;
import suzume.RuleException;
import suzume.SuzumeSession;

public class TsumoAction extends AbstractAction {

    private final Player actPlayer;

    /**
     * 생성자.
     * @param session 게임 세션
     */
    public TsumoAction(SuzumeSession session, String actPlayerId) {
        super(session);
        this.actPlayer = session.getPlayerById(actPlayerId);
    }

    /**
     * 쯔모(패 가져오기)를 수행합니다.
     * @param player 쯔모를 시도하는 플레이어
     */
    public ActionResult act() {
        final Player turnHolder = session.getTurnHolder();

        if (turnHolder != actPlayer) {
            throw RuleException.of("당신의 턴이 아닙니다.");
        }
        
        if (turnHolder.getHandTiles().size() != 5) {
            throw RuleException.of("이미 패를 가져왔습니다.");
        }

        turnHolder.addTileToHand(session.pickRandomTileFromStock());

        logger.info("{action:\"TsumoAction\",session:\"" + session.getSessionId() + "\",actPlayer:\"" + actPlayer.getId() + "\"}");
        return null;
    }
}
