package bgame;

import java.security.SecureRandom;
import java.util.Objects;

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
    
    // 세션에서 사용할 렌덤값
    protected final SecureRandom random;

    /**
     * 내부 생성자.
     * @param sessionId 세션 고유 아이디
     */
    protected Session(String sessionId) {
        Objects.requireNonNull(sessionId);
        this.sessionMadeTimeMs = System.currentTimeMillis();
        this.sessionId = sessionId;
        this.sessionState = SessionState.INITIALIZING;
        this.random = new SecureRandom(sessionId.getBytes());
    }

    /**
     * 세션을 정리하고 닫습니다.
     */
    protected void closeSession() {
        this.sessionState = SessionState.CLOSING;
    }
}
