/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.omiscidgui.browser.interf;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author emonet
 */
public abstract class AbstractContextAwareAction<SingleTaskType> extends AbstractAction implements ContextAwareAction {

    // keep the result (we listen to) to prevent garbage collection
    Lookup.Result<SingleTaskType> taskLookup;
    Lookup context;

    public AbstractContextAwareAction(Class<SingleTaskType> type, final Lookup context) {
        this.context = context;
        taskLookup = context.lookupResult(type);
        taskLookup.addLookupListener(new LookupListener() {
            public void resultChanged(LookupEvent ev) {
                updateAction(context);
            }
        });
        updateAction(context);
    }
    @Override
    public void actionPerformed(ActionEvent ev) {
        actionPerformed(context, ev);
    }

    protected abstract void updateAction(Lookup context);
    public abstract void actionPerformed(Lookup context, ActionEvent ev);

}
