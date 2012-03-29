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

import java.lang.reflect.Method;
import javax.swing.JTable;
import javax.swing.SizeSequence;

/**
 *
 * @author emonet
 */
public class AdaptableJTable extends JTable
{

    public void setRowHeight( int row, int rowHeight )
    {
        try {
            Class<?> jTableClass = this.getClass().getSuperclass();
 
            Method getRowModelMethod = jTableClass.getDeclaredMethod("getRowModel");
 
            getRowModelMethod.setAccessible(true );
 
            SizeSequence rowModel = (SizeSequence) getRowModelMethod.invoke(this);
 
            if (rowHeight < 0) {
                throw new IllegalArgumentException("Negative Row Height Requested");
            }
 
            rowModel.setSize( row, rowHeight );
            resizeAndRepaint();
        } catch( Exception ex ) {
            ex.printStackTrace();
            //super.setRowHeight( row, rowHeight );
        }
    }
}
