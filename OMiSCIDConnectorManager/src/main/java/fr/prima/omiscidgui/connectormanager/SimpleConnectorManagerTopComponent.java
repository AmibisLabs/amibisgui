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

package fr.prima.omiscidgui.connectormanager;

import fr.prima.omiscid.user.connector.ConnectorListener;
import fr.prima.omiscid.user.connector.ConnectorType;
import fr.prima.omiscid.user.connector.Message;
import fr.prima.omiscid.user.service.Service;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscidgui.browser.ServiceClient;
import fr.prima.omiscidgui.browser.uiutil.JComponentWrapper;
import fr.prima.omiscidgui.browser.uiutil.JComponentWrapperListener;
import java.awt.Color;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.openide.ErrorManager;
import org.openide.util.NbBundle;

/**
 * Top component which displays something.
 */
final class SimpleConnectorManagerTopComponent extends JPanel implements JComponentWrapperListener {
    
    /** path to the icon used by the component and its open action */
    //    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    
    private ServiceClient serviceClient;
    private String localConnectorName;
    private Object[] formatContext;
    private ServiceProxy serviceProxy;
    private String connectorName;
    private JComponentWrapper wrapper;
        
    private void freeContext() {
        if (serviceClient != null) {
            serviceClient.freeConnector(localConnectorName);
            serviceClient = null;
        }
    }

    private synchronized void sendMessage() {
        if (serviceClient != null) {
            String message = inputTextArea.getText();
            serviceClient.getConnectorService(localConnectorName).sendToAllClients(localConnectorName, Utility.stringToByteArray(message));
            inputTextArea.setText("");
            DefaultTableModel tableModel = (DefaultTableModel) logTable.getModel();
            tableModel.addRow(new Object[]{message, null});
        }
    }
    private synchronized void messageReceived(Message message) {
        String stringMessage = message.getBufferAsStringUnchecked();
        DefaultTableModel tableModel = (DefaultTableModel) logTable.getModel();
        if (tableModel.getRowCount() == 0 || tableModel.getValueAt(tableModel.getRowCount()-1, 1) != null) {
            tableModel.addRow(new Object[]{null, stringMessage});
        } else {
            tableModel.setValueAt(stringMessage, tableModel.getRowCount()-1, 1);
        }
    }
    private synchronized void disconnected() {
        freeContext();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setInputComponentsEnabled(false);
                inputHeaderLabel.setForeground(Color.RED);
                inputHeaderLabel.setText(NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "LBL_disconnected", formatContext));
                wrapper.setName(NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "CTL_SimpleConnectorManagerTopComponent_disconnected", formatContext));
            }
        });
    }
    private void setInputComponentsEnabled(final boolean enabled) {
        Runnable task = new Runnable() {
            public void run() {
                sendButton.setEnabled(enabled);
                inputTextArea.setEditable(enabled);
                inputTextArea.setEnabled(enabled);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
    private void doNotExpectOutput() {
        Runnable task = new Runnable() {
            public void run() {
                logTable.getColumnModel().getColumn(1).setMinWidth(0);
                logTable.getColumnModel().getColumn(1).setMaxWidth(0);
                logTable.getColumnModel().getColumn(1).setPreferredWidth(0);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
    private void doNotExpectInput() {
        Runnable task = new Runnable() {
            public void run() {
                setInputComponentsEnabled(false);
                inputHeaderLabel.setText(NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "LBL_isAnOutput", formatContext));
                logTable.getColumnModel().getColumn(0).setMinWidth(0);
                logTable.getColumnModel().getColumn(0).setMaxWidth(0);
                logTable.getColumnModel().getColumn(0).setPreferredWidth(0);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    private SimpleConnectorManagerTopComponent() {
    }
    private Runnable invokeAtOpen;
    /*package*/ SimpleConnectorManagerTopComponent(final ServiceClient serviceClient, final ServiceProxy serviceProxy, final String connectorName) {
        this.serviceClient = serviceClient;
        this.serviceProxy = serviceProxy;
        this.connectorName = connectorName;
        this.formatContext = new Object[]{connectorName, serviceProxy.getName(), Utility.intTo8HexString(serviceProxy.getPeerId())};
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        inputHeaderLabel = new javax.swing.JLabel();
        sendButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        inputTextArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        logTable = new AdaptableJTable();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setOneTouchExpandable(true);

        org.openide.awt.Mnemonics.setLocalizedText(inputHeaderLabel, org.openide.util.NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "SimpleConnectorManagerTopComponent.inputHeaderLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(sendButton, org.openide.util.NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "SimpleConnectorManagerTopComponent.sendButton.text")); // NOI18N
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendMessage(evt);
            }
        });

        inputTextArea.setColumns(20);
        inputTextArea.setRows(2);
        inputTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                controlEnterDetector(evt);
            }
        });
        jScrollPane1.setViewportView(inputTextArea);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(sendButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE))
            .add(jPanel1Layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(inputHeaderLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(inputHeaderLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sendButton)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)))
        );

        jSplitPane1.setBottomComponent(jPanel1);

        logTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(logTable);

        jSplitPane1.setLeftComponent(jScrollPane2);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

private void sendMessage(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMessage
    sendMessage();
}//GEN-LAST:event_sendMessage
    
private void controlEnterDetector(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_controlEnterDetector
    if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_ENTER) {
        sendMessage();
    }
}//GEN-LAST:event_controlEnterDetector


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel inputHeaderLabel;
    private javax.swing.JTextArea inputTextArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable logTable;
    private javax.swing.JButton sendButton;
    // End of variables declaration//GEN-END:variables
    public void atComponentOpened(final JComponentWrapper wrapper) {
        this.wrapper = wrapper;
        try {
            localConnectorName = serviceClient.getConnector(ConnectorType.INOUTPUT);
            //System.out.println("Got "+localConnectorName);
            Service service = serviceClient.getConnectorService(localConnectorName);
            service.addConnectorListener(localConnectorName, new ConnectorListener() {

                public void messageReceived(Service service, String localConnectorName, Message message) {
                    SimpleConnectorManagerTopComponent.this.messageReceived(message);
                }

                public void disconnected(Service service, String localConnectorName, int peerId) {
                    SimpleConnectorManagerTopComponent.this.disconnected();
                }

                public void connected(Service service, String localConnectorName, int peerId) {
                }
            });
            service.connectTo(localConnectorName, serviceProxy, connectorName);
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    wrapper.setName(NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "CTL_SimpleConnectorManagerTopComponent", formatContext));
                    if (serviceProxy.getOutputConnectors().contains(connectorName)) {
                        // This is a connector to which we can't send anything
                        doNotExpectInput();
                    } else {
                        setInputComponentsEnabled(true);
                    }
                    if (serviceProxy.getInputConnectors().contains(connectorName)) {
                        // This is a connector from which we wont receive anything
                        doNotExpectOutput();
                    }
                }
            });
        } catch (Exception e) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
        }
    }

    public void atComponentClosed(JComponentWrapper wrapper) {
        freeContext();
    }

    public void atComponentUICreation(JComponentWrapper wrapper) {
        initComponents();
        setInputComponentsEnabled(false);
        wrapper.setName(NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "CTL_SimpleConnectorManagerTopComponent_connecting", formatContext));
        setToolTipText(NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "HINT_SimpleConnectorManagerTopComponent", formatContext));
        final DefaultTableModel tableModel = (DefaultTableModel) logTable.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(2);
        final String c1 = NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "COL_Sent");
        final String c2 = NbBundle.getMessage(SimpleConnectorManagerTopComponent.class, "COL_Received");
        tableModel.setColumnIdentifiers(new Object[]{c1, c2});
        logTable.setDefaultRenderer(Object.class, new MultilineCellRenderer());
    //          setIcon(Utilities.loadImage(ICON_PATH, true));

    }

    /*
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    @Override
    public void componentOpened() {
        new Thread(invokeAtOpen).start();
        invokeAtOpen = null;
    }
    
    @Override
    public void componentClosed() {
        freeContext();
    }
     */
    
}
