package suzume;

public class RuleException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private RuleException(String msg, Throwable th) {
        super(msg, th);
    }

    public static RuleException of(String msg) {
        return new RuleException(msg, null);
    }

    public static RuleException of(Throwable th) {
        return new RuleException(null, th);
    }

    public static RuleException of(String msg, Throwable th) {
        return new RuleException(msg, th);
    }
}
