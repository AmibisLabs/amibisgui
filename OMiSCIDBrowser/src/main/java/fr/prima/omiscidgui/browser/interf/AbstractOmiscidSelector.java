/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.omiscidgui.browser.interf;

import fr.prima.netbeans.selector.AbstractSelector;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public abstract class AbstractOmiscidSelector<T extends BrowserSelectionElement> extends AbstractSelector<T> {

    public AbstractOmiscidSelector(Class<T> type) {
        super(type);
    }

    // Just a reminder of the superclass
    @Override
    protected abstract void getTasks(ArrayList result, Collection<T> selection);
    // Just a reminder of the superclass

}
