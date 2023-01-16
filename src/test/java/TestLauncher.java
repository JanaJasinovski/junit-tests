import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import service.UserServiceTest;

import java.io.PrintWriter;

public class TestLauncher {
    public static void main(String[] args) {
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(DiscoverySelectors.selectClass(UserServiceTest.class))
//                .selectors(DiscoverySelectors.selectPackage("..."))
                .filters(
//                        TagFilter.includeTags("login")
                )
                .build();
        launcher.execute(request, summaryGeneratingListener);

        try  (PrintWriter printWriter = new PrintWriter(System.out)){
            summaryGeneratingListener.getSummary().printTo(printWriter);
        }
    }
}
