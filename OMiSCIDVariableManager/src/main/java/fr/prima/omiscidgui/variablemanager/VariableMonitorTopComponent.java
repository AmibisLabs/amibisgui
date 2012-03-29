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

package fr.prima.omiscidgui.variablemanager;

import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscid.user.variable.RemoteVariableChangeListener;
import fr.prima.omiscid.user.variable.VariableAccessType;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Formatter;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
final class VariableMonitorTopComponent extends TopComponent {
    
    /** path to the icon used by the component and its open action */
    //    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";

    private RemoteVariableChangeListener remoteVariableListener;
    private ServiceProxy serviceProxy;
    private String variableName;
    private Object[] formatContext;

    private void doNotExpectInput() {
        Runnable task = new Runnable() {
            public void run() {
                setInputComponentsEnabled(false);
                inputHeaderLabel.setText(NbBundle.getMessage(VariableMonitorTopComponent.class, "LBL_isNotWritable", formatContext));
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
    
    private void setValue() {
        serviceProxy.setVariableValue(variableName, inputTextArea.getText());
    }

    private void newValue(String newValue) {
        boolean simpleMode = simpleTextCheckBox.isSelected();
        if (simpleMode) {
            onlyLastValueTextArea.setText(newValue);
        } else {
            DefaultTableModel tableModel = (DefaultTableModel) logTable.getModel();
            String now = new Formatter().format(NbBundle.getMessage(VariableMonitorTopComponent.class, "COL_Timestamp.format"), Calendar.getInstance()).toString();
            tableModel.addRow(new Object[]{now, newValue});
        }
    }
    
    private void setInputComponentsEnabled(final boolean enabled) {
        Runnable task = new Runnable() {
            public void run() {
                setValueButton.setEnabled(enabled);
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

    private void updateViewMode() {
        boolean simpleMode = simpleTextCheckBox.isSelected();
        if (simpleMode) {
            newValue(serviceProxy.getVariableValue(variableName));
            jSplitPane1.setTopComponent(onlyLastValueTextArea);
            DefaultTableModel tableModel = (DefaultTableModel) logTable.getModel();
            tableModel.setRowCount(0);
        } else {
            jSplitPane1.setTopComponent(jScrollPane1);
        }
    }


    private VariableMonitorTopComponent() {
    }
    private Runnable invokeAtOpen;
    public VariableMonitorTopComponent(final ServiceProxy serviceProxy, final String variableName) {
        this.serviceProxy = serviceProxy;
        this.variableName = variableName;
        this.remoteVariableListener = new RemoteVariableChangeListener() {
            public void variableChanged(ServiceProxy serviceProxy, String variableName, String newValue) {
                newValue(newValue);
            }
        };
        this.formatContext = new Object[]{variableName, serviceProxy.getName(), Utility.intTo8HexString(serviceProxy.getPeerId())};
        initComponents();
        onlyLastValueTextArea = new JTextArea();
        onlyLastValueTextArea.setEditable(false);
        onlyLastValueTextArea.setFont(new Font("Monospaced", Font.PLAIN, onlyLastValueTextArea.getFont().getSize()));
        setInputComponentsEnabled(false);
        setName(NbBundle.getMessage(VariableMonitorTopComponent.class, "CTL_VariableMonitorTopComponent_connecting", formatContext));
        setToolTipText(NbBundle.getMessage(VariableMonitorTopComponent.class, "HINT_VariableMonitorTopComponent", formatContext));
        final DefaultTableModel tableModel = (DefaultTableModel) logTable.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(2);
        final String c1 = NbBundle.getMessage(VariableMonitorTopComponent.class, "COL_Timestamp", formatContext);
        final String c2 = NbBundle.getMessage(VariableMonitorTopComponent.class, "COL_Value", formatContext);
        tableModel.setColumnIdentifiers(new Object[] {c1, c2});
        tableModel.addRow(new Object[]{null, serviceProxy.getVariableValue(variableName)});
        logTable.setDefaultRenderer(Object.class, new MultilineCellRenderer());
        //        setIcon(Utilities.loadImage(ICON_PATH, true));
        invokeAtOpen = new Runnable() {
            public void run() {
                serviceProxy.addRemoteVariableChangeListener(variableName, remoteVariableListener);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setName(NbBundle.getMessage(VariableMonitorTopComponent.class, "CTL_VariableMonitorTopComponent", formatContext));
                        if (serviceProxy.getVariableAccessType(variableName) == VariableAccesType.CONSTANT) {
                            headerLabel.setText(NbBundle.getMessage(VariableMonitorTopComponent.class, "LBL_constantWontMove", formatContext));
                        }
                        if (serviceProxy.getVariableAccessType(variableName) != VariableAccessType.READ_WRITE) {
                            doNotExpectInput();
                            setInputComponentsEnabled(false);
                        } else {
                            setInputComponentsEnabled(true);
                        }
                    }
                });
            }
        };
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        new AdaptableJTable();
        logTable = new javax.swing.JTable();
        variableValueSetterPanel = new javax.swing.JPanel();
        inputHeaderLabel = new javax.swing.JLabel();
        setValueButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        inputTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        headerLabel = new javax.swing.JLabel();
        simpleTextCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setOneTouchExpandable(true);

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
        jScrollPane1.setViewportView(logTable);

        jSplitPane1.setLeftComponent(jScrollPane1);

        org.openide.awt.Mnemonics.setLocalizedText(inputHeaderLabel, org.openide.util.NbBundle.getMessage(VariableMonitorTopComponent.class, "VariableMonitorTopComponent.inputHeaderLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(setValueButton, org.openide.util.NbBundle.getMessage(VariableMonitorTopComponent.class, "VariableMonitorTopComponent.setValueButton.text")); // NOI18N
        setValueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setValueButtonsendMessage(evt);
            }
        });

        inputTextArea.setColumns(20);
        inputTextArea.setRows(2);
        inputTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputTextAreacontrolEnterDetector(evt);
            }
        });
        jScrollPane2.setViewportView(inputTextArea);

        org.jdesktop.layout.GroupLayout variableValueSetterPanelLayout = new org.jdesktop.layout.GroupLayout(variableValueSetterPanel);
        variableValueSetterPanel.setLayout(variableValueSetterPanelLayout);
        variableValueSetterPanelLayout.setHorizontalGroup(
            variableValueSetterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(variableValueSetterPanelLayout.createSequentialGroup()
                .add(setValueButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))
            .add(variableValueSetterPanelLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(inputHeaderLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 472, Short.MAX_VALUE))
        );
        variableValueSetterPanelLayout.setVerticalGroup(
            variableValueSetterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(variableValueSetterPanelLayout.createSequentialGroup()
                .add(inputHeaderLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(variableValueSetterPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(setValueButton)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)))
        );

        jSplitPane1.setRightComponent(variableValueSetterPanel);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);

        org.openide.awt.Mnemonics.setLocalizedText(headerLabel, org.openide.util.NbBundle.getMessage(VariableMonitorTopComponent.class, "VariableMonitorTopComponent.headerLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(simpleTextCheckBox, org.openide.util.NbBundle.getMessage(VariableMonitorTopComponent.class, "VariableMonitorTopComponent.simpleTextCheckBox.text")); // NOI18N
        simpleTextCheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(VariableMonitorTopComponent.class, "VariableMonitorTopComponent.simpleTextCheckBox.toolTipText")); // NOI18N
        simpleTextCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpleTextCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(headerLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 229, Short.MAX_VALUE)
                .add(simpleTextCheckBox))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(headerLabel)
                    .add(simpleTextCheckBox)))
        );

        add(jPanel1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void setValueButtonsendMessage(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setValueButtonsendMessage
        setValue();
}//GEN-LAST:event_setValueButtonsendMessage

    private void inputTextAreacontrolEnterDetector(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputTextAreacontrolEnterDetector
        if (evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_ENTER) {
            setValue();
        }
    }//GEN-LAST:event_inputTextAreacontrolEnterDetector

    private void simpleTextCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpleTextCheckBoxActionPerformed
        updateViewMode();
    }//GEN-LAST:event_simpleTextCheckBoxActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel inputHeaderLabel;
    private javax.swing.JTextArea inputTextArea;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable logTable;
    private javax.swing.JButton setValueButton;
    private javax.swing.JCheckBox simpleTextCheckBox;
    private javax.swing.JPanel variableValueSetterPanel;
    // End of variables declaration//GEN-END:variables
    
    private JTextArea onlyLastValueTextArea;

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
        serviceProxy.removeRemoteVariableChangeListener(variableName, remoteVariableListener);
    }
    private VariableAccessType VariableAccesType;

}
