package suzume.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import bgame.ActionResult;
import suzume.Player;
import suzume.RuleException;
import suzume.SuzumeSession;
import suzume.SuzumeUtil;
import suzume.Tile;
import suzume.SuzumeSession.SuzumeState;

public class LoanAction extends AbstractAction {

    private final Player actPlayer;
    private final Player targetPlayer;

    /**
     * 생성자.
     * @param session 게임 세션
     * @param actPlayerId 액션을 수행하는 플레이어 아이디
     * @param targetPlayerId 론을 당하는 플레이어 아이디
     * @param loanTile 론 대상 패
     */
    public LoanAction(SuzumeSession session, String actPlayerId, String targetPlayerId) {
        super(session);
        Objects.requireNonNull(this.actPlayer = session.getPlayerById(actPlayerId));
        Objects.requireNonNull(this.targetPlayer = session.getPlayerById(targetPlayerId));
    }

    /**
     * 론을 수행합니다.
     */
    public ActionResult act() {
        // 게임 상태가 쯔모대기 혹은 론 대기중일때만 론 가능
        if (session.getSuzumeState() != SuzumeState.WAITING_THUMO ||
            session.getSuzumeState() != SuzumeState.WAITING_LOAN ) {
            throw RuleException.of("지금은 론을 할 수 없습니다.");
        }

        final List<Tile> targetDiscardList = targetPlayer.getDiscardTiles();
        final Tile loanTile = targetDiscardList.get(targetDiscardList.size() - 1);

        if (SuzumeUtil.isDiscardedTileValue(actPlayer, loanTile)) {
            throw RuleException.of("버린적이 있는 패를 론에 사용 할 수 없습니다.");
        }

        final List<Tile> copiedHandTile = new ArrayList<>(actPlayer.getHandTiles());
        copiedHandTile.add(loanTile); // 핸드 사본에 론 패 추가

        final int score = SuzumeUtil.calcHuaryoScore(session.getDoraTile(), copiedHandTile);
        if (score < 5) {
            throw RuleException.of("론을 해도 점수가 부족합니다.");
        }

        session.finishRound();
        
        logger.info("{action:\"LoanAction\",session:\"" + session.getSessionId() +
                    "\",actPlayer:\"" + actPlayer.getId() + "\", targetPlayer:\"" +
                    targetPlayer.getId() + ",loanTileValue:\"" + loanTile.getValue() +
                    "\", score:" + score + "}");

        return null;
    }
}
