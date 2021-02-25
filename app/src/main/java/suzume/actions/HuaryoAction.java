package suzume.actions;

import java.util.List;

import bgame.ActionResult;
import suzume.Player;
import suzume.RuleException;
import suzume.SuzumeSession;
import suzume.Tile;

public class HuaryoAction extends AbstractAction {

    private final Player actPlayer;

    /**
     * 생성자.
     * @param session 게임 세션
     */
    public HuaryoAction(SuzumeSession session, String actPlayerId) {
        super(session);
        this.actPlayer = session.getPlayerById(actPlayerId);
    }

    /**
     * 화료(점수 내기)를 수행합니다.
     * @param player 화료를 시도하는 플레이어
     */
    public ActionResult act() {
        final Player turnHolder = session.getTurnHolder();
        final List<Tile> turnHolderHand = turnHolder.getHandTiles();

        if (turnHolder != actPlayer) {
            throw RuleException.of("당신의 턴이 아닙니다.");
        }

        if (turnHolderHand.size() != 6) {
            throw RuleException.of("손패가 6개가 아닙니다.");
        }

        int score = 0;
        if ((score = session.calcHuaryoScore(turnHolderHand)) < 5) {
            throw RuleException.of("점수가 부족합니다! (" + score + "점)");
        }

        logger.info("{action:\"HuaryoAction\",session:\"" + session.getSessionId() + "\",actPlayer:\"" + actPlayer.getId() + "\"}");
        return null;
    }
}
