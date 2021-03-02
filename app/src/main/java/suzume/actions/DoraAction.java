package suzume.actions;

import java.util.Objects;

import bgame.ActionResult;
import suzume.Player;
import suzume.RuleException;
import suzume.SuzumeSession;

public class DoraAction extends AbstractAction {

    private final Player actPlayer;

    /**
     * 생성자.
     * @param session 게임 세션
     * @param actPlayerId 액션을 수행하는 플레이어 아이디
     */
    public DoraAction(SuzumeSession session, String actPlayerId) {
        super(session);
        Objects.requireNonNull(this.actPlayer = session.getPlayerById(actPlayerId));
    }

    /**
     * 도라(보너스패 선정)를 수행합니다.
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
