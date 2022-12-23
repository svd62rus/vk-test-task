package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

/**
 * Листенер для логирования информации
 *
 * @author Sushkov Denis
 * @version 1.0
 * @since 2023-12-23
 */
public class BaseTestListener implements ITestListener, IInvokedMethodListener {

    private static final Logger logger = LogManager.getRootLogger();

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        String description = iTestResult.getMethod().getMethodName();
        logger.info("Тест '" + description + "' PASSED!\n\n");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        String description = iTestResult.getMethod().getMethodName();
        logger.error("Тест '" + description + "' FAILED!\n\n");
    }

    @Override
    public void beforeInvocation(final IInvokedMethod method, final ITestResult testResult) {
        if (method.isTestMethod()) {
            String description = method.getTestMethod().getMethodName();
            logger.info("Тест '" + description + "' STARTED!");
        }

    }
}
