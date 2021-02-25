package suzume.actions;

import bgame.ActionResult;
import suzume.Player;
import suzume.RuleException;
import suzume.SuzumeSession;

public class DoraAction extends AbstractAction {

    private final Player actPlayer;

    /**
     * 생성자.
     * @param session 게임 세션
     */
    public DoraAction(SuzumeSession session, String actPlayerId) {
        super(session);
        this.actPlayer = session.getPlayerById(actPlayerId);
    }

    /**
     * 도라(보너스패 선정)를 수행합니다.
     * @param player 도라를 시도하는 플레이어
     */
    public ActionResult act() {
        if (actPlayer != session.getRoundStartPlayer()) {
            throw RuleException.of("선 플레이어가 아닙니다.");
        }

        if (session.getDoraTile() != null) {
            throw RuleException.of("이미 도라 타일이 선정되었습니다.");
        }

        session.setDoraTile(session.pickRandomTileFromStock());

        logger.info("{action:\"DoraAction\",session:\"" + session.getSessionId() + "\",actPlayer:\"" + actPlayer.getId() + "\"}");
        return null;
    }
}
