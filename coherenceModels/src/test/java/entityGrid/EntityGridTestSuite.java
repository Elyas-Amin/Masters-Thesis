package entityGrid;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        EntityGridFrameworkTest.class,
        EntityGridExtractorTest.class,
})

public class EntityGridTestSuite {
}
