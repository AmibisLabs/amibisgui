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

package fr.prima.omiscidgui.variablemanager;

import fr.prima.omiscidgui.browser.interf.AbstractContextAwareAction;
import fr.prima.omiscidgui.selector.generic.OmiscidVariableTask;
import fr.prima.omiscidgui.selector.generic.impl.OmiscidVariableSelector;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

public final class VariableMonitorAction extends AbstractContextAwareAction<OmiscidVariableTask> {

    public VariableMonitorAction() {
        // use global lookup if this actions is uncontextualized (e.g. put in the menu or in a key shortcut)
        this(Utilities.actionsGlobalContext());
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        // creates a contextualized version of this action
        return new VariableMonitorAction(actionContext);
    }

    public VariableMonitorAction(Lookup context) {
        // creates a contextualized version of this action
        super(OmiscidVariableTask.class, context);
        putValue("noIconInMenu", Boolean.TRUE);
        putValue(Action.NAME, NbBundle.getMessage(VariableMonitorAction.class, "CTL_VariableMonitorAction"));
    }

    @Override
    protected void updateAction(Lookup context) {
        setEnabled(context.lookupAll(OmiscidVariableTask.class).size() > 0);
    }

    @Override
    public void actionPerformed(Lookup context, ActionEvent ev) {
        for (OmiscidVariableTask v : context.lookupAll(OmiscidVariableTask.class)) {
            TopComponent win = new VariableMonitorTopComponent(v.getServiceProxy(), v.getVariableName());
            win.open();
            win.requestActive();
        }
    }


}
