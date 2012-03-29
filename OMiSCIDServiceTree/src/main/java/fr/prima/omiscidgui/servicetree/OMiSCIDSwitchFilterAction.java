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

import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.service.ServiceFilter;
import fr.prima.omiscid.user.service.ServiceFilters;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.NbBundle;

/**
 * Action which shows OMiSCIDBrowser component.
 */
public class OMiSCIDSwitchFilterAction extends AbstractAction {

    public OMiSCIDSwitchFilterAction() {
        super(NbBundle.getMessage(OMiSCIDSwitchFilterAction.class, "CTL_OMiSCIDSwitchFilterAction"));
//        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(OMiSCIDBrowserTopComponent.ICON_PATH, true)));
        
    }

    public boolean a = true;
    public void actionPerformed(ActionEvent evt) {
        final Runnable updateServiceTreeLookup = new Runnable() {
            public void run() {
                OMiSCIDServiceTreeTopComponent.getDefault().updateReturnedLookup();
            }
        };

        final JPanel content = new JPanel();
        content.setLayout(new BorderLayout(10, 10));
        final JTextField textField = new JTextField("ownerIs(\""+System.getProperty("user.name")+"\")");
        final JEditorPane message = new JEditorPane("text/html", NbBundle.getMessage(OMiSCIDSwitchFilterAction.class, "CTL_OMiSCIDSwitchFilterDialogMessage"));
        content.add(message, BorderLayout.BEFORE_FIRST_LINE);
        content.add(textField);
        message.setEditable(false);
        message.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    URLDisplayer.getDefault().showURL(e.getURL());
                }
            }
        });
        DialogDescriptor dd = new DialogDescriptor(content, NbBundle.getMessage(OMiSCIDSwitchFilterAction.class, "CTL_OMiSCIDSwitchFilterDialogTitle"), true, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equalsIgnoreCase("ok")) {
                    ServiceFilter filter = readServiceFilter(textField.getText());
                    OMiSCIDServiceTreeTopComponent.getDefault().model.switchToFilter(filter, updateServiceTreeLookup);
                }
            }
        });
        dd.setModal(false);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(dd);
        dialog.setVisible(true);
    }

    // TODO INTEGRATE IN OMISCID
    /*
     * Grammar:
     *       F -> NAME ( ListP )
     *    NAME -> word
     *   ListP -> P , ListP
     *         -> eps
     *       P -> F
     *         -> STRING
     *         -> INTEGER
     *         -> CONSTANT | READ | READ_WRITE
     *         -> INPUT | OUTPUT | INOUTPUT
     */
    public static ServiceFilter readServiceFilter(String stringFilter) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine scriptEngine = manager.getEngineByName("js");
        //scriptEngine.put("sf", ServiceFilters.class);
        try {
            StringBuilder prog = new StringBuilder();
            prog.append(
                    "__A__ = Packages." + Array.class.getCanonicalName() + ";" +
                    "__F__ = Packages." + ServiceFilter.class.getCanonicalName() + ";" +
                    "__SF__ = Packages." + ServiceFilters.class.getCanonicalName() + ";" +
                    "__F__array = function() {" +
                    "  var res = Packages.java.lang.reflect.Array.newInstance(__F__, arguments.length);" +
                    "  for (var i = 0; i < arguments.length; i++){" +
                    "    __A__.set(res, i, arguments[i]);" +
                    "  }" +
                    "  return res;" +
                    "};" +
                    "and = function() { return __SF__.and(__F__array.apply(null, arguments));};" +
                    "or = function() { return __SF__.or(__F__array.apply(null, arguments));};");
            Set<String> existingFilters = new HashSet<String>();
            for (Method method : ServiceFilters.class.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && method.getReturnType().equals(ServiceFilter.class) && !method.isVarArgs()) {
                    existingFilters.add(method.getName());
                }
            }
            if (existingFilters.contains("and") || existingFilters.contains("or")) {
                throw new RuntimeException("We have a bug on service filters and/or (please report with this message)");
            }
            for (String name : existingFilters) {
                prog.append(name).append(" = __SF__.").append(name).append(";");
            }
            addEnumValues(prog, VariableAccessType.class);
            addEnumValues(prog, ConnectorType.class);
            scriptEngine.eval(prog.toString());
            return (ServiceFilter) scriptEngine.eval(stringFilter);
        } catch (ScriptException ex) {
            Logger.getLogger(OMiSCIDSwitchFilterAction.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    private static void addEnumValues(StringBuilder prog, Class<? extends Enum> e) {
        String n = "__"+e.getCanonicalName().replaceAll("[.]", "__");
        prog.append(n + " = Packages." + e.getCanonicalName() + ";");
        for (Enum v : e.getEnumConstants()) {
            prog.append(v.name() + " = " + n + "." + v.name() + ";");
        }
    }

}
