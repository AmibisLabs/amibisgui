/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.netbeans.selector;

import org.openide.util.Lookup;

/**
 *
 * This is basically a contextual Lookup.Provider.
 * The content of the created lookup will depend on a context provided by a lookup.
 * 
 */
public interface Selector {

    
    /**
     * Create the contextualized lookup.
     * One can add listeners to the results provided by the context lookup.
     *
     * Reuse Warning: if selectors are heavily registered and removed dynamically, we can expect to have some memory leaks (if they registered as listeners on context.lookupResult) (unverified).
     *
     * @see AbstractSelector
     */
    Lookup getLookup(Lookup context);

}
