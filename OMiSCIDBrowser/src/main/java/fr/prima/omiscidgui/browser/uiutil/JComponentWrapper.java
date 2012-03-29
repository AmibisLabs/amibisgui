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

package fr.prima.omiscidgui.browser.uiutil;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.openide.windows.TopComponent;

/**
 *
 * Wraps a classical Swing JComponent (JPanel, ...) into a netbeans
 * manipulable TopComponent opened by default in the editor area.
 * 
 * One listener can be set and will be called on frame opening and
 * when frame is closed. Calls will be made in background threads
 * and thus won't block the UI. Modifications to the UI should be
 * made according to swing threading model (SwingUtilities#invokeLater).
 * 
 */
public class JComponentWrapper extends TopComponent {

    private Runnable invokeAtOpen = new Runnable() {
        public void run() {
            listener.atComponentOpened(JComponentWrapper.this);
        }
    };
    private Runnable invokeAtClose = new Runnable() {
        public void run() {
            listener.atComponentClosed(JComponentWrapper.this);
        }
    };
    private Runnable openAndRequestActive = new Runnable() {
        public void run() {
        }
    };
    private JComponentWrapperListener listener;

    public JComponentWrapper(JComponent content, final JComponentWrapperListener listener) {
        this.listener = listener;
        this.setLayout(new BorderLayout());
        this.add(content, BorderLayout.CENTER);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setName("");
                if (listener != null) {
                    listener.atComponentUICreation(JComponentWrapper.this);
                }
                open();
                requestActive();
            }
        });
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        if (listener != null) {
            new Thread(invokeAtOpen).start();
        }
    }

    @Override
    public void componentClosed() {
        if (listener != null) {
            new Thread(invokeAtClose).start();
        }
    }

}
