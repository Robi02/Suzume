package suzume.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bgame.Action;
import bgame.ActionResult;
import lombok.Getter;
import suzume.SuzumeSession;

@Getter
public abstract class AbstractAction implements Action {

    // 로거
    protected static final Logger logger = LoggerFactory.getLogger(AbstractAction.class);

    // 필드
    protected final SuzumeSession session;

    // 추상 메서드
    public abstract ActionResult act();

    /**
     * 내부 생성자.
     * @param session 게임 세션
     */
    protected AbstractAction(SuzumeSession session) {
        this.session = session;
    }
}
