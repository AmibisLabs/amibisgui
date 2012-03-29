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

package fr.prima.omiscidgui.connectormanager;

import fr.prima.omiscidgui.browser.ServiceClient;
import fr.prima.omiscidgui.browser.interf.AbstractContextAwareAction;
import fr.prima.omiscidgui.browser.uiutil.JComponentWrapper;
import fr.prima.omiscidgui.selector.generic.OmiscidConnectorTask;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


/**
 * Action which shows SimpleConnectorManager component.
 */
public class SimpleConnectorManagerAction extends AbstractContextAwareAction<OmiscidConnectorTask> {

    public SimpleConnectorManagerAction() {
        // use global lookup if this actions is uncontextualized (e.g. put in the menu or in a key shortcut)
        this(Utilities.actionsGlobalContext());
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        // creates a contextualized version of this action
        return new SimpleConnectorManagerAction(context);
    }

    public SimpleConnectorManagerAction(Lookup context) {
        super(OmiscidConnectorTask.class, context);
        // creates a contextualized version of this action
        putValue("noIconInMenu", Boolean.TRUE);
        putValue(Action.NAME, NbBundle.getMessage(SimpleConnectorManagerAction.class, "CTL_SimpleConnectorManagerAction"));
    }

    @Override
    protected void updateAction(Lookup context) {
        setEnabled(context.lookupAll(OmiscidConnectorTask.class).size() > 0);
    }

    @Override
    public void actionPerformed(Lookup context, ActionEvent ev) {
        // retrieve the ServiceClient utility object used to connect to services from the gui
        ServiceClient serviceClient = context.lookup(ServiceClient.class);
        for (OmiscidConnectorTask c : context.lookupAll(OmiscidConnectorTask.class)) {
            SimpleConnectorManagerTopComponent p = new SimpleConnectorManagerTopComponent(serviceClient, c.getServiceProxy(), c.getConnectorName());
            new JComponentWrapper(p, p);
        }
    }

}

