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

package fr.prima.omiscidgui.browser.interf;

import fr.prima.omiscid.user.service.ServiceProxy;

public class Service implements BrowserSelectionElement, Comparable<Service> {
    
    private ServiceProxy serviceProxy;
    private fr.prima.omiscid.user.service.Service service;
    
    /** Creates a new instance of Service */
    public Service(fr.prima.omiscid.user.service.Service service, ServiceProxy serviceProxy) {
        this.service = service;
        this.serviceProxy = serviceProxy;
    }

    public fr.prima.omiscid.user.service.Service getService() {
        return service;
    }
    public ServiceProxy getServiceProxy() {
        return serviceProxy;
    }

    @Override
    public String toString() {
        return getName();
    }
    public String getName() {
        return serviceProxy.getName();
    }
    public String getHostName() {
        return serviceProxy.getHostName();
    }
    public String getOwner() {
        try {
            return serviceProxy.getVariableValue("owner");
        } catch (Exception e) {
            return "";
        }
    }
    public int getPeerId() {
        return serviceProxy.getPeerId();
    }

    // allow service tree sorting in the service tree view
    public int compareTo(Service o) {
        return this.toString().compareToIgnoreCase(o.toString());
    }

}
