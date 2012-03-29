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

/**
 * Listens to wrapping component events. Calls are made in a background thread.
 */
public interface JComponentWrapperListener {

    public void atComponentUICreation(JComponentWrapper wrapper);
    public void atComponentOpened(JComponentWrapper wrapper);
    public void atComponentClosed(JComponentWrapper wrapper);

    
}
