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


public class ServiceElement implements BrowserSelectionElement, Comparable<ServiceElement> {

    public static ServiceElement notAnElement = new ServiceElement(null, null, null) {
        @Override
        public String toString() {
            return "Querying ...";
        }
    };
    private String name;
    private Type type;
    private Service service;

    public Service getService() {
        return service;
    }
    
    /** Creates a new instance of ServiceElement */
    public ServiceElement(Service service, String name, Type type) {
        this.service = service;
        this.name = name;
        this.type = type;
    }

    // prevent them to be resorted in the service tree view
    public int compareTo(ServiceElement o) {
        return 0;
    }
    
    //// public API
    public static enum Type {
        Input, Output, Bidirectional, Constant, ReadOnlyVariable, ReadWriteVariable
    }
    public String toString() {
        return getName();
    }
    public String getName() {
        return name;
    }
    public Type getType() {
        return type;
    }

    public boolean isOfType(Type ... types) {
        for (Type t : types) {
            if (this.type == t) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Equivalent to isOfType(Constant, ReadOnlyVariable, ReadWriteVariable)
     */
    public boolean isOmiscidVariable() {
        return isOfType(Type.Constant, Type.ReadOnlyVariable, Type.ReadWriteVariable);
    }

    /**
     * Equivalent to isOfType(Input, Output, Bidirectional)
     */
    public boolean isOmiscidConnector() {
        return isOfType(Type.Input, Type.Output, Type.Bidirectional);
    }
     
}
