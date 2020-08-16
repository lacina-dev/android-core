package org.ros.android.android_vitulus_control;


import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import std_srvs.EmptyResponse;

/**
 * A simple {@link ServiceClient} {@link NodeMain}.
 *
 * @author damonkohler@google.com (Damon Kohler)
 */
public class SrvsClientMotorStart extends AbstractNodeMain {
    private ServiceClient<std_srvs.EmptyRequest, EmptyResponse> serviceClient;
    private ConnectedNode connNode;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("android/srvs_cli_motor_start");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        connNode = connectedNode;
        try {
            serviceClient = connectedNode.newServiceClient("start_motor", std_srvs.Empty._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }

    }

    public void sendRequest() {
        final std_srvs.EmptyRequest request = serviceClient.newMessage();
        serviceClient.call(request, new ServiceResponseListener<EmptyResponse>() {
            @Override
            public void onSuccess(EmptyResponse response) {
                connNode.getLog().info(
                        String.format("Start lidar service get response ,  %s, %s ", request.toString(), response.toString()));
            }

            @Override
            public void onFailure(RemoteException e) {
                throw new RosRuntimeException(e);
            }
        });
    }

}