/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.prima.omiscidgui.servicetree;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Action which shows OMiSCIDServiceTree component.
 */
public class OMiSCIDServiceTreeAction extends AbstractAction {

    public OMiSCIDServiceTreeAction() {
        super(NbBundle.getMessage(OMiSCIDServiceTreeAction.class, "CTL_OMiSCIDServiceTreeAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(OMiSCIDServiceTreeTopComponent.ICON_PATH, true)));
    }

    public void actionPerformed(ActionEvent evt) {
        TopComponent win = OMiSCIDServiceTreeTopComponent.findInstance();
        win.open();
        win.requestActive();
    }
}
