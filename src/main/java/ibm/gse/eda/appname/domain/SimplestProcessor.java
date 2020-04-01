package ibm.gse.eda.appname.domain;

import java.util.concurrent.CountDownLatch;

import ibm.gse.eda.appname.infra.kafka.KafkaConfiguration;

/**
 * SimplestProcessor
 */
public class SimplestProcessor {

    public static void main(String[] args) {
        System.out.println("Start a simple streaming app");
        KafkaConfiguration kc = new KafkaConfiguration();
        // as when running as main no data is injected test here need to inject value
        kc.setInTopicName("streams-plaintext-input");
        kc.setOutTopicName("streams-linesplit-output");
        kc.setGroupId("streams-group");
        StreamSampleOperator operator = new StreamSampleOperator(kc);
        
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                operator.stop();
                latch.countDown();
            }
        });

        try {
            operator.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

}