/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.netbeans.selector;

import java.util.Collection;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 */
public class SelectorBasedLookup extends ProxyLookup {

    private Lookup.Result<Selector> selectorsLookupResult;
    private final LookupListener selectorsInLayerListener;

    public SelectorBasedLookup(final Lookup baseLookup, String layerPathForSelectors) {
        selectorsLookupResult = Lookups.forPath(layerPathForSelectors).lookupResult(Selector.class);
        selectorsInLayerListener = new LookupListener() {
            public void resultChanged(LookupEvent ev) {
                selectorsLookupChanged(baseLookup);
            }
        };
        selectorsLookupResult.addLookupListener(selectorsInLayerListener);
        selectorsLookupChanged(baseLookup);
    }

    private void selectorsLookupChanged(Lookup baseLookup) {
        Collection<? extends Selector> selectors = selectorsLookupResult.allInstances();
        Lookup[] lookups = new Lookup[selectors.size()];
        int i = 0;
        for (Selector s : selectors) {
            lookups[i++] = s.getLookup(baseLookup);
        }
        this.setLookups(lookups);
    }
}
