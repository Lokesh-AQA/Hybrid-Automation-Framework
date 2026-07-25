package executor;

import model.TestStep;

@FunctionalInterface
public interface KeywordAction {

    void execute(TestStep step) throws Exception;

}