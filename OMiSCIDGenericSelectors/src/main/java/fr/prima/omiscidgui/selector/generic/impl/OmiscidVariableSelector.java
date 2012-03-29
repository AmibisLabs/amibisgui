/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.omiscidgui.selector.generic.impl;

import fr.prima.omiscidgui.browser.interf.AbstractOmiscidSelector;
import fr.prima.omiscidgui.browser.interf.ServiceElement;
import fr.prima.omiscidgui.selector.generic.OmiscidVariableTask;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author emonet
 */
public class OmiscidVariableSelector extends AbstractOmiscidSelector<ServiceElement> {

    public OmiscidVariableSelector() {
        super(ServiceElement.class);
    }

    @Override
    protected void getTasks(ArrayList result, Collection<ServiceElement> selection) {
        for (ServiceElement e : selection) {
            if (e.isOmiscidVariable()) {
                result.add(new OmiscidVariableTask(e.getService().getServiceProxy(), e.getName()));
            }
        }
    }


}
