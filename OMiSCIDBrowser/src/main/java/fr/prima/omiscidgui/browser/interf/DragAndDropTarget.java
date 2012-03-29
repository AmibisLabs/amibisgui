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

import java.util.List;
import javax.swing.Action;

/**
 * 
 * Any implementor of this interface should also be JComponent.
 */
public interface DragAndDropTarget {
    
    public List<Action> getDragAndDropActions();

}
