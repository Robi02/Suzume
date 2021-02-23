package bgame;

import java.security.SecureRandom;

import lombok.Getter;

@Getter
public abstract class Session {
 
    // 열거형
    public enum SessionState {
        INITIALIZING,   // 초기화 중
        PLAYING,        // 게임중
        PAUSED,         // 일시정지
        CLOSING         // 종료중
    }

    // 세션 생성시점 시간
    protected final long sessionMadeTimeMs;

    // 세션 ID
    protected final String sessionId;

    // 세션 상태
    protected SessionState sessionState;

    // 플레이어들이 입력한 액션 저장을 담당할 큐
    protected final ActionQueue actionQueue;
    
    // 세션에서 사용할 렌덤값
    protected final SecureRandom random;

    /**
     * 내부 생성자.
     */
    protected Session(String sessionId) {
        this.sessionMadeTimeMs = System.currentTimeMillis();
        this.sessionId = sessionId;
        this.sessionState = SessionState.INITIALIZING;
        this.actionQueue = ActionQueue.getInstance();
        this.random = new SecureRandom(sessionId.getBytes());
    }

    /**
     * 세션을 정리하고 닫습니다.
     */
    protected abstract void closeSession();

    /**
     * 세션의 메인 로직을 수행합니다.
     */
    protected void run() {
        this.sessionState = SessionState.PLAYING;

        while (this.sessionState != SessionState.CLOSING) {
            Action action = null;
            while ((action = this.actionQueue.pollAction()) != null) {
                action.act();
            }
        }
    }

    /**
     * 세션에 동기 액션을 수행시킵니다.
     * @param action 수행할 액션
     * @return 액션 수행 후 응답받은 ActionResult값.
     * @apiNote 이 메서드를 수행하면 세션에서 다음 업데이트가 완료
     * 되어 액션이 수행되고 결과가 반활될 때 까지 스레드가 블로킹됩니다.
     */
    public ActionResult doActionSync(Action action) {
        // 블로킹 논블로킹 동기 비동기 여기 생각좀 해보자.
        // 어떤 방식을 사용해야 업데이트 완료 후 응답을 전달해 줄 수 있을까?
        // 여기부터 시작 @@
    }

    /**
     * 세션에 비동기 액션을 추가합니다.
     * @param action 추가할 액션
     */
    public void addActionAsync(Action action) {
        this.actionQueue.pushAction(action);
    }

    /**
     * 세션에 저장된 모든 액션을 지웁니다.
     */
    public void clearAction() {
        this.actionQueue.clearAction();
    }
}
