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

package fr.prima.omiscidgui.browser.interf;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.openide.util.Lookup;

public class DragAndDrop {
    /**
     * 
     * @param target
     * @throws java.lang.IllegalArgumentException if the DragAndDropTarget is not a JComponent.
     */
    public static void registerDragAndDropTarget(DragAndDropTarget target)
            throws IllegalArgumentException {
        if (!(target instanceof JComponent)) {
            throw new IllegalArgumentException("Any DragAndDropTarget implementor should extends JComponent");
        }
        ((JComponent)target).setTransferHandler(transfertHandler);
    }

    public static void registerDragAndDropSource(JComponent c) {
        c.setTransferHandler(transfertHandler);
    }

    private static TransferHandler transfertHandler = new BrowserContextTransfertHandler();

    private static class BrowserContextTransfertHandler extends TransferHandler {
        //The data type exported from JColorChooser.
        DataFlavor dataFlavor;
        private boolean changesForegroundColor = true;

        BrowserContextTransfertHandler() {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType;
            mimeType += ";class=" + DragAndDrop.class.getName();
            try {
                dataFlavor = new DataFlavor(mimeType);
            } catch (ClassNotFoundException e) {}
        }


        private boolean accept(DataFlavor[] flavors) {
            if (dataFlavor == null) {
                return false;
            }
            for (int i = 0; i < flavors.length; i++) {
                if (dataFlavor.equals(flavors[i])) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canImport(JComponent c, DataFlavor[] flavors) {
            //TODO DND return accept(flavors) && !getEnabledActions(c).isEmpty();
            return accept(flavors);
        }

        @Override
        public boolean importData(JComponent c, Transferable t) {
            if (accept(t.getTransferDataFlavors())) {
                /*
                 * TODO DND
                List<OmiscidBrowserAction> enabledActions = getEnabledActions(c);
                if (enabledActions.size() == 1) {
                    if (enabledActions.get(0) instanceof CallableSystemAction) {
                        ((CallableSystemAction) enabledActions.get(0)).performAction();
                    } else {
                        enabledActions.get(0).actionPerformed(null);
                    }
                } else if (enabledActions.size() > 1) {
                    JPopupMenu menu = new JPopupMenu();
                    for (OmiscidBrowserAction action : enabledActions) {
                        menu.add(action);
                    }
                    Point mouse = MouseInfo.getPointerInfo().getLocation();
                    Point base = c.getLocationOnScreen();
                    menu.show(c, mouse.x - base.x, mouse.y - base.y);
                    return true;
                }
                 */
            }
            return false;
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            return new Transferable() {
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{dataFlavor};
                }

                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return dataFlavor.equals(flavor);
                }

                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    if (!isDataFlavorSupported(flavor)) {
                        throw new UnsupportedFlavorException(flavor);
                    }
                    return null;
                }
            };
        }
/*
 * TODO DND
        private List<OmiscidBrowserAction> getEnabledActions(JComponent c) {
            if (!(c instanceof DragAndDropTarget)) {
                return Collections.emptyList();
            }
            Lookup lookup = OMiSCIDBrowser.getDefault().getLookup();
            HashMap<Class<?>, List<?>> lookupCache = new HashMap<Class<?>, List<?>>();
            List<OmiscidBrowserAction> enabledActions = new ArrayList<OmiscidBrowserAction>();
            List<OmiscidBrowserAction> proposedActions = ((DragAndDropTarget) c).getDragAndDropActions();
            for (OmiscidBrowserAction action : proposedActions) {
                for (Class<?> clazz : action.getAcceptedClasses()) {
                    List<?> lookupForType = lookupCache.get(clazz);
                    if (lookupForType == null) {
                        lookupForType = lookupForType(lookup, clazz);
                        lookupCache.put(clazz, lookupForType);
                    }
                    if (lookupForType.size() != 0) {
                        enabledActions.add(action);
                        break;
                    }
                }
            }
            return enabledActions;
        }
 */
        
        private List<?> lookupForType(Lookup lookup, Class<?> clazz) {
            List<?> lookupForType;
            @SuppressWarnings(value = "unchecked")
            Lookup.Template<?> template = new Lookup.Template(clazz);
            lookupForType = (List<?>) lookup.lookup(template).allInstances();
            return lookupForType;
        }
    }
    
}
