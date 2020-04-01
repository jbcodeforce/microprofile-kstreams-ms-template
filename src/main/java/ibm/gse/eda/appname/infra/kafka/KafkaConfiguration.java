package ibm.gse.eda.appname.infra.kafka;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class KafkaConfiguration {
	private static final Logger logger = Logger.getLogger(KafkaConfiguration.class.getName());
  
    @Inject 
    @ConfigProperty(name = "in-topic-name",defaultValue = "streams-plaintext-input")
    protected String inTopicName;
    
    @Inject 
    @ConfigProperty(name = "out-topic-name",defaultValue = "streams-linesplit-output")
    protected String outTopicName;
    
    @Inject
    @ConfigProperty(name = "kafka-streams-groupid", defaultValue = "streams-group")
	private String groupId;
    
    public KafkaConfiguration() {}
    
    
    /**
     * Take into account the environment variables
     *
     * @return common kafka properties
     */
    private  Properties buildCommonProperties() {
        Properties properties = new Properties();
        Map<String, String> env = System.getenv();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, env.getOrDefault("KAFKA_BROKERS","localhost:9092"));

    	if (env.get("KAFKA_APIKEY") != null) {
          properties.put(StreamsConfig.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
          properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
          properties.put(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"token\" password=\""
                            + env.get("KAFKA_APIKEY") + "\";");
          properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
          properties.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
          properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS");
        }
        if (env.get("TRUSTSTORE_PATH") != null && env.get("TRUSTSTORE_PATH").length()>0){
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, env.get("TRUSTSTORE_PATH"));
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, env.get("TRUSTSTORE_PWD"));
        }
        properties.forEach((k,v)  -> logger.info(k + " : " + v)); 
        return properties;
    }

	

	public Properties getStreamingProperties(String clientId) {
        if (clientId == null || clientId.length() == 0) {
            clientId = groupId;
        }
        System.out.println(clientId);
		Properties properties = buildCommonProperties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, clientId);
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.forEach((k,v)  -> logger.info(k + " : " + v)); 
        return properties;
	}

    public String getInTopicName() {
        if (this.inTopicName == null) {
            // temporary there is a problem in CDI
            this.inTopicName = "streams-plaintext-input";
        }
        return this.inTopicName;
    }

    public void setInTopicName(String inTopicName) {
        this.inTopicName = inTopicName;
    };

    public String getOutTopicName() {
        if (this.outTopicName == null) {
            // temporary there is a problem in CDI
            this.outTopicName = "streams-linesplit-output";
        }
        return this.outTopicName;
    }

    public void setOutTopicName(String aTopicName) {
        this.outTopicName = aTopicName;
    }


	public void setGroupId(String v) {
        this.groupId = v;
    };
    
    public String getGroupId() {
        return this.groupId;
    }
}
