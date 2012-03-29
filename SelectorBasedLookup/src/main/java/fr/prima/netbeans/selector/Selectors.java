/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.netbeans.selector;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * Utility class.
 *
 */
public class Selectors {


    public static SelectorBasedLookup forPath(Lookup context, String layerPathForSelectors) {
        return new SelectorBasedLookup(context, layerPathForSelectors);
    }

    public static JPopupMenu actionsToPopup(Action[] actions, Component component, boolean hideDisabledActions, String noActionMessage) {
        if (hideDisabledActions) {
            ArrayList<Action> newActions = new ArrayList<Action>();
            for (Action a : actions) {
                if (a.isEnabled()) {
                    newActions.add(a);
                }
            }
            actions = newActions.toArray(new Action[0]);
        }
        if (actions.length == 0 && noActionMessage != null) {
            Action disabledAction = new AbstractAction(noActionMessage) {
                public void actionPerformed(ActionEvent e) {
                }
            };
            disabledAction.setEnabled(false);
            JPopupMenu res = new JPopupMenu();
            res.add(disabledAction);
            return res;
        }
        return Utilities.actionsToPopup(actions, component);
    }
}
