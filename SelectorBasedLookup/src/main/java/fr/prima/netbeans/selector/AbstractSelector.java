/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.netbeans.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 */
public abstract class AbstractSelector<T> implements Selector {

    // used to prevent garbage collection of the result
    // not using this causes the result and the listener to be collected (as intended by netbeans)
    private Lookup.Result selection;

    private Class type;

    public AbstractSelector(Class<T> type) {
        this.type = type;
    }

    /**
     * This method delegates listing of tasks to getTasks.
     * Override this method to get better control on the udpate process (you can then use an empty getTasks).
     *
     * @param content
     * @param selection
     */
    protected void update(InstanceContent content, Collection<T> selection) {
        ArrayList res = new ArrayList();
        getTasks(res, Collections.unmodifiableCollection(selection));
        content.set(res, null);
    }

    /**
     * Returns the tasks associated to the given selection.
     *
     * @param result
     * @param selection
     */
    protected abstract void getTasks(ArrayList result, Collection<T> selection);

    public Lookup getLookup(Lookup context) {
        final InstanceContent instanceContent = new InstanceContent();
        final AbstractLookup res = new AbstractLookup(instanceContent);
        selection = context.lookupResult(type);
        selection.addLookupListener(new LookupListener() {
            public void resultChanged(LookupEvent ev) {
                update(instanceContent, selection.allInstances());
            }
        });
        return res;
    }

}
