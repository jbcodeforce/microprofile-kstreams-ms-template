package ibm.gse.eda.appname.domain;

import java.util.Arrays;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibm.gse.eda.appname.infra.kafka.KafkaConfiguration;

/**
 * Stream Sample Operator to illustrate building simple topology and to
 * present how it is started by external app
 */
@ApplicationScoped
public class StreamSampleOperator {
    private static final Logger logger = LoggerFactory.getLogger(StreamSampleOperator.class);
    private final StreamsBuilder builder = new StreamsBuilder();
    @Inject
    private KafkaConfiguration kafkaConfiguration;


    private Properties props;
    private final Topology topology;
    private final KafkaStreams streams;

    public StreamSampleOperator() {
        defineStream();
        topology = builder.build();
        streams = new KafkaStreams(topology, props);
    }

    public StreamSampleOperator(KafkaConfiguration kc) {
        kafkaConfiguration = kc;
        defineStream();
        topology = builder.build();
        streams = new KafkaStreams(topology, props);
    }

    private void defineStream() {
        props = getKafkaConfiguration().getStreamingProperties("SampleStreamer");
        builder.<String, String>stream(getKafkaConfiguration().getInTopicName())
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                .to(getKafkaConfiguration().getOutTopicName());
    }

    public void start() {
        logger.info("Start called");
        try {
            streams.start();
        } catch (final Throwable e) {
            System.exit(1);
        }
    }

    public void stop() {
        logger.info("Stop called");
        streams.close();
    }

    public KafkaConfiguration getKafkaConfiguration() {
        if (this.kafkaConfiguration == null) {
            // temporary there is a problem in CDI
            this.kafkaConfiguration = new KafkaConfiguration();
        }
        return this.kafkaConfiguration;
    }


}