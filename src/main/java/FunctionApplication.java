
import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.Instance.LifecycleState;
import com.oracle.bmc.core.requests.GetInstanceRequest;
import com.oracle.bmc.core.requests.InstanceActionRequest;
import com.oracle.bmc.core.responses.InstanceActionResponse;

/**
 * 
 * @author ajbal
 * 
 */

public class FunctionApplication {

	private static final Logger LOGGER = LogManager.getLogger(FunctionApplication.class);

	public static void main(String[] args) throws IOException {
		final String ocid = "<instance-ocid>";
		final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse("<file-location-of-oci-config>");
		final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
		final String START = "Start";
		final String STOP = "Stop";
		
		ComputeClient client = new ComputeClient(provider);
		Instance instance = getInstanceData(client, ocid);

			if (instance.getLifecycleState() == LifecycleState.Stopped) {
				InstanceActionResponse response = instanceAction(client, ocid, START);
				instance = response.getInstance();
				LOGGER.info("+++++++++++++ Instance starting and service will be available in few seconds +++ ");

			} else if (instance.getLifecycleState() == LifecycleState.Running) {
				InstanceActionResponse response = instanceAction(client, ocid, STOP);
				instance = response.getInstance();
				LOGGER.info("+++++++++++++ Instance Stopping +++++++ ");
			}
        
    //checks can be added for other state as well
			
		} 

	}


	

	private static Instance getInstanceData(ComputeClient client, String ocid) {
		GetInstanceRequest getInstanceRequest = GetInstanceRequest.builder().instanceId(ocid).build();
		return client.getInstance(getInstanceRequest).getInstance();
	}

	private static InstanceActionResponse instanceAction(ComputeClient client, String ocid, String action) {
		InstanceActionRequest actionRequest = InstanceActionRequest.builder().instanceId(ocid).action(action).build();
		return client.instanceAction(actionRequest);
	}
}
