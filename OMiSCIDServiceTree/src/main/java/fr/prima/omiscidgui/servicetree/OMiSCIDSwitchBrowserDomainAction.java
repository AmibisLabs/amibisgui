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

package fr.prima.omiscidgui.servicetree;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JTextField;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 * Action which shows OMiSCIDBrowser component.
 */
public class OMiSCIDSwitchBrowserDomainAction extends AbstractAction {
    
    public OMiSCIDSwitchBrowserDomainAction() {
        super(NbBundle.getMessage(OMiSCIDSwitchBrowserDomainAction.class, "CTL_OMiSCIDSwitchBrowserDomainAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(OMiSCIDBrowserTopComponent.ICON_PATH, true)));
        
    }

    public void actionPerformed(ActionEvent evt) {
        final Runnable updateServiceTreeLookup = new Runnable() {
            public void run() {
                OMiSCIDServiceTreeTopComponent.getDefault().updateReturnedLookup();
            }
        };
        final JTextField textField = new JTextField("_bip._tcp");
        textField.setCaretPosition(4);
        DialogDescriptor dd = new DialogDescriptor(textField, NbBundle.getMessage(OMiSCIDSwitchBrowserDomainAction.class, "CTL_OMiSCIDSwitchBrowserDomainDialogTitle"), true, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equalsIgnoreCase("ok")) {
                    OMiSCIDServiceTreeTopComponent.getDefault().model.switchToDomain(textField.getText(), updateServiceTreeLookup);
                }
            }
        });
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.setVisible(true);
    }
    
}
