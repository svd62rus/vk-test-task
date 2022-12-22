package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

public class BaseTestListener implements ITestListener, IInvokedMethodListener {

    private static final Logger logger = LogManager.getRootLogger();

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        String description = iTestResult.getMethod().getMethodName();
        logger.info("Autotest '" + description + "' PASSED!");
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        String description = iTestResult.getMethod().getMethodName();
        logger.error("Autotest '" + description + "' FAILED!");
    }

    @Override
    public void beforeInvocation(final IInvokedMethod method, final ITestResult testResult) {
        if(method.isTestMethod()){
            String description = method.getTestMethod().getMethodName();
            logger.info("Autotest '" + description + "' STARTED!");
        }

    }
}
