/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.omiscidgui.servicetree;

import fr.prima.netbeans.selector.SelectorBasedLookup;
import fr.prima.netbeans.selector.Selectors;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscidgui.browser.interf.DragAndDrop;
import fr.prima.omiscidgui.browser.interf.ServiceElement;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.Evaluator;
import org.jdesktop.animation.timing.interpolation.KeyFrames;
import org.jdesktop.animation.timing.interpolation.KeyTimes;
import org.jdesktop.animation.timing.interpolation.KeyValues;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.netbeans.swing.etable.ETableColumnModel;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Provider;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
//import org.openide.util.Utilities;

import static fr.prima.omiscidgui.servicetree.interf.Icons.*;

/**
 * Top component which displays something.
 */
public final class OMiSCIDServiceTreeTopComponent extends TopComponent {

    private static OMiSCIDServiceTreeTopComponent instance;
    private String actionsFolderName = "Omiscid/Actions";
    private String selectorsFolderName = "Omiscid/Selectors";
    
    /** path to the icon used by the component and its open action */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";

    private static final String PREFERRED_ID = "OMiSCIDServiceTreeTopComponent";

    /*package*/ BrowserTreeTableModel model = new BrowserTreeTableModel();
    private OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(model, model);

    private JPopupMenu popupMenu = null;

    private class ListSelectionLookup extends AbstractLookup implements ListSelectionListener {

        InstanceContent content;

        public ListSelectionLookup() {
            this(new InstanceContent());
        }

        public ListSelectionLookup(InstanceContent c) {
            super(c);
            this.content = c;
        }

        public void valueChanged(ListSelectionEvent e) {
            ArrayList all = new ArrayList();
            ListSelectionModel selectionModel = serviceTree.getSelectionModel();
            for (int i = selectionModel.getMinSelectionIndex(); i <= selectionModel.getMaxSelectionIndex(); i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    int modelI = serviceTree.convertRowIndexToModel(i);
                    all.add(serviceTree.getOutlineModel().getValueAt(modelI, 0));
                }
            }
            content.set(all, null);
        }
    }

    final ListSelectionLookup lsl;
    Lookup lookupReturnedForThisComponent;
    /*package*/ void updateReturnedLookup() {
        synchronized (lsl) {
            lookupReturnedForThisComponent = new ProxyLookup(
                    Lookups.singleton(model.getServiceClient()),
                    Lookups.singleton(model.getServiceRepository()),
                    lsl,
                    new SelectorBasedLookup(lsl, selectorsFolderName));
        }
    }

    private OMiSCIDServiceTreeTopComponent() {
        initComponents();
        serviceTree.setGridColor(serviceTree.getBackground());

        lsl = new ListSelectionLookup(new InstanceContent());
        serviceTree.getSelectionModel().addListSelectionListener(lsl);

        updateReturnedLookup();
        associateLookup(Lookups.proxy(new Provider() {
            public Lookup getLookup() {
                synchronized (lsl) {
                    return lookupReturnedForThisComponent;
                }
            }
        }));

        serviceTree.setDragEnabled(true);
        serviceTree.setRootVisible(false);
        /*{
            ETableColumnModel etcm = (ETableColumnModel)serviceTree.getColumnModel();
            ETableColumn etc = (ETableColumn) etcm.getColumn(0);
            etcm.setColumnSorted(etc, false, 0);
        }*/
        serviceTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SLASH) {
                    serviceTree.displaySearchField();
                }
            }
        });
        serviceTree.addMouseListener(new MouseInputAdapter() {
            private void showPopup(final MouseEvent e) {
                if (serviceTree.getSelectedRowCount() <= 1) {
                    TreePath path = serviceTree.getClosestPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        try {
                            Robot robot = new Robot();
                            robot.mousePress(InputEvent.BUTTON1_MASK);
                            robot.delay(3);
                            robot.mouseRelease(InputEvent.BUTTON1_MASK);
                        } catch (AWTException ex) {
                            Exceptions.printStackTrace(ex);
                        }
//                        int row = getRowForPath(serviceTree, path);
//                        serviceTree.setRowSelectionInterval(row, row);
                    } else {
                        serviceTree.clearSelection();
                    }
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        createPopupMenu(e.getX(), e.getY());
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                });
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
                if (e.getButton() == MouseEvent.BUTTON1 && null == serviceTree.getClosestPathForLocation(e.getX(), e.getY())) {
                    serviceTree.clearSelection();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
        });
        RenderDataProvider cellRenderer = new RenderDataProvider() {
            public String getDisplayName(Object o) {
                return o.toString();
            }

            public boolean isHtmlDisplayName(Object arg0) {
                return false;
            }

            public Color getBackground(Object arg0) {
                return null;
            }

            public Color getForeground(Object arg0) {
                return null;
            }

            public String getTooltipText(Object arg0) {
                return null;
            }

            public Icon getIcon(Object value) {
                if (value instanceof ServiceElement && ((ServiceElement)value).getType() != null) {
                    switch (((ServiceElement)value).getType()) {
                    case Output:
                        return outputIcon;
                    case Input:
                        return inputIcon;
                    case Bidirectional:
                        return inoutputIcon;
                    case Constant:
                        return constantIcon;
                    case ReadOnlyVariable:
                        return readIcon;
                    case ReadWriteVariable:
                        return readwriteIcon;
                    default:
                        return null;
                    }
                } else if (value instanceof Service) {
                    return serviceIcon;
                } else {
                    return null;
                }
            }

        };
        serviceTree.setRenderDataProvider(cellRenderer);
        setName(NbBundle.getMessage(OMiSCIDServiceTreeTopComponent.class, "CTL_OMiSCIDServiceTreeTopComponent"));
        setToolTipText(NbBundle.getMessage(OMiSCIDServiceTreeTopComponent.class, "HINT_OMiSCIDServiceTreeTopComponent"));
        //        setIcon(Utilities.loadImage(ICON_PATH, true));
        model.addTreeModelListener(new TreeModelListener() {
            Animator animator = null;
            private void blink(final Color color) {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run() {
                        if (animator != null && animator.isRunning()) {
                            final Runnable task = this;
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException ex) {}
                                    SwingUtilities.invokeLater(task);
                                }
                            }).start();
                            return;
                        }
                        Evaluator<Color> evaluator = new Evaluator<Color>() {
                            public Color evaluate(Color c1, Color c2, float a) {
                                return new Color(
                                        (int) (c1.getRed() * (1 - a) + a * c2.getRed()),
                                        (int) (c1.getGreen() * (1 - a) + a * c2.getGreen()),
                                        (int) (c1.getBlue() * (1 - a) + a * c2.getBlue()),
                                        (int) (c1.getAlpha() * (1 - a) + a * c2.getAlpha()));
                            }
                        };
                        KeyFrames keyFrames = new KeyFrames(
                                KeyValues.create(evaluator, new Color[]{Color.WHITE, color, Color.WHITE}),
                                new KeyTimes(new float[]{0.f, .3f, 1.f}));
                        animator = new Animator(
                                700, 1,
                                Animator.RepeatBehavior.LOOP,
                                new PropertySetter(eventNotification, "background", keyFrames)
                                );
                        animator.start();
                    }
                });
            }
            public void treeNodesChanged(TreeModelEvent e) {
            }
            public void treeNodesInserted(TreeModelEvent e) {
                blink(Color.BLUE);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        serviceTree.expandPath(new TreePath(model.getRoot()));
                    }
                });
                //serviceTree.repaint();
            }
            public void treeNodesRemoved(TreeModelEvent e) {
                blink(Color.RED);
                //serviceTree.repaint();
            }
            public void treeStructureChanged(TreeModelEvent e) {
            }
        });
        DragAndDrop.registerDragAndDropSource(serviceTree);
        serviceTree.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    serviceTree.getTransferHandler().exportAsDrag(serviceTree, e, TransferHandler.COPY);
                }
            }

        });
    }

    protected void createPopupMenu(int x, int y) {
        List<? extends Action> actions = Utilities.actionsForPath(actionsFolderName);
        //popupMenu = Utilities.actionsToPopup(actions.toArray(new Action[0]), this);
        popupMenu = Selectors.actionsToPopup(actions.toArray(new Action[0]), this, true, NbBundle.getMessage(OMiSCIDServiceTreeTopComponent.class, "LBL_EmptyContextualMenu"));
    }

    private TreePath getPathForRow(Outline serviceTree, int row) {
        Rectangle r = serviceTree.getCellRect(row, 0, false);
        int x = (int) r.getCenterX();
        int y = (int) r.getCenterY();
        return serviceTree.getClosestPathForLocation(x, y);
    }

    private void initComponents() {

        eventNotification = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        serviceTree = new org.netbeans.swing.outline.Outline(outlineModel);

        setLayout(new java.awt.BorderLayout());

        eventNotification.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        eventNotification.setOpaque(true);
        eventNotification.setPreferredSize(new java.awt.Dimension(10, 2));
        add(eventNotification, java.awt.BorderLayout.WEST);

        jScrollPane1.setViewportView(serviceTree);
        
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }


    private javax.swing.JLabel eventNotification;
    private javax.swing.JScrollPane jScrollPane1;
    private org.netbeans.swing.outline.Outline serviceTree;

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files only,
     * i.e. deserialization routines; otherwise you could get a non-deserialized instance.
     * To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized OMiSCIDServiceTreeTopComponent getDefault() {
        if (instance == null) {
            instance = new OMiSCIDServiceTreeTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the OMiSCIDServiceTreeTopComponent instance. Never call {@link #getDefault} directly!
     */
    public static synchronized OMiSCIDServiceTreeTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(OMiSCIDServiceTreeTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof OMiSCIDServiceTreeTopComponent) {
            return (OMiSCIDServiceTreeTopComponent) win;
        }
        Logger.getLogger(OMiSCIDServiceTreeTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID +
                "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /** replaces this in object stream */
    @Override
    public Object writeReplace() {
        ResolvableHelper r = new ResolvableHelper();
        ETableColumnModel saveModel = (ETableColumnModel) serviceTree.getColumnModel();
        saveModel.writeSettings(r.p, PREFERRED_ID);
        // OMiSCIDServiceTreeTopComponent
        //P: {OMiSCIDBrowserTopComponentHiddenETableColumn-2-Ascending=false, OMiSCIDBrowserTopComponentHiddenETableColumn-2-ModelIndex=3, OMiSCIDBrowserTopComponentETableColumn-1-PreferredWidth=43, OMiSCIDBrowserTopComponentHiddenETableColumn-2-PreferredWidth=68, OMiSCIDBrowserTopComponentETableColumn-0-HeaderValue=Nodes, OMiSCIDBrowserTopComponentHiddenETableColumn-0-SortRank=0, OMiSCIDBrowserTopComponentETableColumn-0-ModelIndex=0, OMiSCIDBrowserTopComponentHiddenETableColumn-1-PreferredWidth=60, OMiSCIDBrowserTopComponentETableColumn-1-SortRank=0, OMiSCIDBrowserTopComponentETableColumn-1-ModelIndex=2, OMiSCIDBrowserTopComponentHiddenETableColumn-0-Width=20, OMiSCIDBrowserTopComponentETableColumn-1-Width=87, OMiSCIDBrowserTopComponentHiddenColumnsNumber=3, OMiSCIDBrowserTopComponentETableColumn-0-Width=236, OMiSCIDBrowserTopComponentColumnsNumber=2, OMiSCIDBrowserTopComponentETableColumn-0-Ascending=false, OMiSCIDBrowserTopComponentHiddenETableColumn-1-SortRank=0, OMiSCIDBrowserTopComponentHiddenETableColumn-0-HeaderValue=, OMiSCIDBrowserTopComponentHiddenETableColumn-0-Ascending=true, OMiSCIDBrowserTopComponentHiddenETableColumn-0-ModelIndex=1, OMiSCIDBrowserTopComponentETableColumn-1-Ascending=true, OMiSCIDBrowserTopComponentHiddenETableColumn-2-Width=79, OMiSCIDBrowserTopComponentETableColumn-0-PreferredWidth=192, OMiSCIDBrowserTopComponentHiddenETableColumn-1-HeaderValue=owner, OMiSCIDBrowserTopComponentHiddenETableColumn-1-Width=83, OMiSCIDBrowserTopComponentETableColumn-1-HeaderValue=id/type, OMiSCIDBrowserTopComponentHiddenETableColumn-1-Ascending=true, OMiSCIDBrowserTopComponentHiddenETableColumn-1-ModelIndex=4, OMiSCIDBrowserTopComponentETableColumn-0-SortRank=0, OMiSCIDBrowserTopComponentHiddenETableColumn-2-HeaderValue=host, OMiSCIDBrowserTopComponentHiddenETableColumn-2-SortRank=0, OMiSCIDBrowserTopComponentHiddenETableColumn-0-PreferredWidth=20}
        return r;
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    final static class ResolvableHelper implements Serializable {

        private static final long serialVersionUID = 1L;

        private final Properties p = new Properties();

        public Object readResolve() {
            final Outline loadServiceTree = OMiSCIDServiceTreeTopComponent.getDefault().serviceTree;
            ETableColumnModel model = (ETableColumnModel) loadServiceTree.getColumnModel();
            try {
                model.readSettings(p, PREFERRED_ID, loadServiceTree);
            } catch (Exception e) {
                // ignore initialization problem (for first start)
                // should do a better detection to be able to report real problems
            }
            return OMiSCIDServiceTreeTopComponent.getDefault();
        }
    }
}
