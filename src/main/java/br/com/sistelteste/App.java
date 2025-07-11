package br.com.sistelteste;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.core.LauncherFactory;

public class App {
      public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectClass(RelacaoBeneficosReajustadosTest.class))
            .build();

        Launcher launcher = LauncherFactory.create();
        launcher.execute(request);
    }
}
