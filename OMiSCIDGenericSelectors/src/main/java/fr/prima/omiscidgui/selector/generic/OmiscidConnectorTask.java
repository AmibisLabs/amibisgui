/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.omiscidgui.selector.generic;

import fr.prima.omiscid.user.service.ServiceProxy;

/**
 *
 * @author emonet
 */
public class OmiscidConnectorTask {

    private ServiceProxy serviceProxy;
    private String connectorName;

    public OmiscidConnectorTask(ServiceProxy serviceProxy, String connectorName) {
        this.serviceProxy = serviceProxy;
        this.connectorName = connectorName;
    }

    public ServiceProxy getServiceProxy() {
        return serviceProxy;
    }

    public String getConnectorName() {
        return connectorName;
    }

    @Override
    public int hashCode() {
        return connectorName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OmiscidConnectorTask) {
            OmiscidConnectorTask o = (OmiscidConnectorTask) obj;
            return o.connectorName.equals(connectorName) && o.serviceProxy == serviceProxy;
        } else {
            return false;
        }
    }


}
