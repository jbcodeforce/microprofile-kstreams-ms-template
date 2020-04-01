package ibm.gse.eda.appname.infra.kafka;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import ibm.gse.eda.appname.domain.StreamSampleOperator;

/**
 * Start the different kafka streams operators when the application context is created. This is needed
 * as this application is also exposing some REST api.
 * 
 * @author jeromeboyer
 *
 */
@WebListener
@ApplicationScoped
public class AppContextListener implements ServletContextListener{

	@Inject
	StreamSampleOperator streamSampleOperator;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		streamSampleOperator.start();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		streamSampleOperator.stop();
	}

}
