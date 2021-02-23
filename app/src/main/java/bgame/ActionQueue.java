package bgame;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActionQueue {
    
    private final Queue<Action> actionQueue;

    /**
     * 내부 생성자.
     */
    private ActionQueue() {
        this.actionQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * 정적 생성자.
     * @return 생성된 ActionQueue.
     */
    public static ActionQueue getInstance() {
        return new ActionQueue();
    }

    /**
     * 액션 큐에 저장된 가장 첫 번째 Action을 획득합니다.
     * @return 액션 큐의 첫 번째 Action, 비어 있는 경우 null.
     * @apiNote 이 메서드는 <code>thread-safe를 보장</code>합니다.
     */
    public Action pollAction() {
        return this.actionQueue.poll();
    }

    /**
     * 액션 큐에 새 Action을 가장 뒤에 추가합니다.
     * @param action 플레이어 액션
     * @apiNote 이 메서드는 <code>thread-safe를 보장</code>합니다.
     */
    public void pushAction(Action action) {
        this.actionQueue.add(action);
    }
    
    /**
     * 액션 큐의 모든 Action을 제거합니다.
     */
    public void clearAction() {
        this.actionQueue.clear();
    }
}
