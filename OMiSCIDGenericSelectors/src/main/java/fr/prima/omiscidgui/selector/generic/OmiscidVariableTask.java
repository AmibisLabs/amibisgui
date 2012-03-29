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
public class OmiscidVariableTask {
    private ServiceProxy  serviceProxy;
    private String variableName;

    public OmiscidVariableTask(ServiceProxy serviceProxy, String variableName) {
        this.serviceProxy = serviceProxy;
        this.variableName = variableName;
    }

    public ServiceProxy getServiceProxy() {
        return serviceProxy;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public int hashCode() {
        return variableName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OmiscidVariableTask) {
            OmiscidVariableTask o = (OmiscidVariableTask) obj;
            return o.variableName.equals(variableName) && o.serviceProxy == serviceProxy;
        } else {
            return false;
        }
    }

}
