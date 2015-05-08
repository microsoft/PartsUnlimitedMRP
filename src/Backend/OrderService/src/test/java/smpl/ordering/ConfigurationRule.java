package smpl.ordering;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * This JUnit rule is used to enforce the availability of an ApplicationContext, which is essential for
 * creating beans and auto-wiring.
 */
public class ConfigurationRule
        implements TestRule
{
    @Override
    public Statement apply(Statement statement, Description description)
    {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(TestOrderingConfiguration.class);
        return statement;
    }
}
