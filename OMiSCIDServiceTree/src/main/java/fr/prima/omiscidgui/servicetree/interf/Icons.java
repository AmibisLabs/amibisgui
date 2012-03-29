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

package fr.prima.omiscidgui.servicetree.interf;

import fr.prima.omiscidgui.servicetree.icons.IconsPath;
import javax.swing.ImageIcon;

public class Icons {

    private static ImageIcon createImageIcon(String resourcePath) {
        return new ImageIcon(IconsPath.class.getResource(resourcePath));
    }
    public static ImageIcon outputIcon = createImageIcon("output.png");
    public static ImageIcon inputIcon = createImageIcon("input.png");
    public static ImageIcon inoutputIcon = createImageIcon("inoutput.png");
    public static ImageIcon constantIcon = createImageIcon("constant.png");
    public static ImageIcon readIcon = createImageIcon("readonly.png");
    public static ImageIcon readwriteIcon = createImageIcon("readwrite.png");
    public static ImageIcon serviceIcon = createImageIcon("service.png");

}
