/**
 * 
 * Authors:
 *   Core Software:
 *     author     Remi Emonet
 *   OMiSCIDAddinManager:
 *     author     Remi Barraquand
 *     integrator Remi Emonet
 * 
 * Contacts:
 *   try to write to cited persons at inria.fr, use firstName.lastName
 *   or contact OMiSCID team using a web search
 * 
 */

package fr.prima.omiscidgui.browser;

import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.exception.ConnectorLimitReached;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceFactory;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ServiceClient {

    /*
     * Connector names are of the form s????c????, are unique and
     * their first part encode the service id.
     */
    /*
     * For each connector type, given an availability (boolean to true means
     * available), provides a list of connector names.
     */
    private Map<ConnectorType, Map<Boolean, List<String>>> connectorPool = new Hashtable<ConnectorType, Map<Boolean, List<String>>>();
    private Map<String, ConnectorType> connectorTypes = new Hashtable<String, ConnectorType>();
    private Map<String, Service> servicePool = new Hashtable<String, Service>();
    private ServiceFactory serviceFactory;
    private int connectorId; // within the current service
    private int serviceId = 0;

    public ServiceClient(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        for (ConnectorType connectorType : ConnectorType.values()) {
            Map<Boolean, List<String>> connectors = new Hashtable<Boolean, List<String>>();
            connectors.put(false, new LinkedList<String>());
            connectors.put(true, new LinkedList<String>());
            this.connectorPool.put(connectorType, connectors);
        }
    }
    
    public synchronized String getConnector(ConnectorType connectorType) throws IOException {
        String connectorName = null;
        { // look for reusable connectors
            List<String> available = connectorPool.get(connectorType).get(true);
            if (!available.isEmpty()) {
                connectorName = available.remove(0);
            }
        }
        if (connectorName == null) { // no reuse possible, create a new one
            final String serviceName = "s" + serviceId;
            fr.prima.omiscid.user.service.Service service = servicePool.get(serviceName);
            if (service == null) {
                service = serviceFactory.create(serviceName);
                servicePool.put(serviceName, service);
                connectorId = 0;
            }
            connectorName = serviceName + "c" + connectorId;
            connectorId++;
            try {
                service.addConnector(connectorName, "auto", connectorType);
                connectorTypes.put(connectorName, connectorType);
            } catch (ConnectorLimitReached e) {
                // this service is full, when trigger the creation of a new one
                serviceId++;
                return getConnector(connectorType);
            }
        }
        connectorPool.get(connectorType).get(false).add(connectorName);
        return connectorName;
    }

    private String serviceName(String connectorName) {
        return connectorName.replaceFirst("c.*$", "");
    }
    
    public synchronized void freeConnector(String connectorName) {
        if (connectorPool.get(connectorTypes.get(connectorName)).get(false).remove(connectorName)) {
            connectorPool.get(connectorTypes.get(connectorName)).get(true).add(connectorName);
            String serviceName = serviceName(connectorName);
            servicePool.get(serviceName).removeAllConnectorListeners(connectorName);
            servicePool.get(serviceName).closeAllConnections(connectorName);
        } else {
            throw new IllegalArgumentException("Connector '"+connectorName+"' was not allocated (or already freed)");
        }
    }
    
    public synchronized Service getConnectorService(String connectorName) {
        return servicePool.get(serviceName(connectorName));
    }

}
