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

package fr.prima.omiscidgui.servicetree;

import fr.prima.omiscid.user.service.ServiceFilter;
import fr.prima.omiscidgui.browser.interf.Service;
import fr.prima.omiscidgui.browser.interf.ServiceElement.Type;
import fr.prima.omiscidgui.browser.interf.ServiceElement;
import fr.prima.omiscid.user.service.ServiceProxy;
import fr.prima.omiscid.user.service.ServiceRepository;
import fr.prima.omiscid.user.service.ServiceRepositoryListener;
import fr.prima.omiscid.user.service.impl.Digger;
import fr.prima.omiscid.user.service.impl.ServiceFactoryImpl;
import fr.prima.omiscid.user.util.Utility;
import fr.prima.omiscidgui.browser.ServiceClient;
import java.awt.Component;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.netbeans.swing.outline.RowModel;

/**
 *
 * @author emonet
 */
public class BrowserTreeTableModel
//        extends AbstractTreeTableModel implements TreeTableModel {
    implements TreeModel, RowModel {

    private fr.prima.omiscid.user.service.Service service;
    private ServiceRepository serviceRepository;
    private ServiceClient serviceClient;
    
    // these fields should be renamed
    private Vector<Service> browserServices = new Vector<Service>();
    private Hashtable<ServiceProxy, Service> proxyToBrowserService = new Hashtable<ServiceProxy, Service>();

    public ServiceRepository getServiceRepository() {
        return serviceRepository;
    }
    public fr.prima.omiscid.user.service.Service getService() {
        return service;
    }
    
    //private DefaultMutableTreeNode rootNode;
    
    public BrowserTreeTableModel() {
        factory = new ServiceFactoryImpl();
        service = factory.create("OMiSCIDBrowser");
        serviceRepository = factory.createServiceRepository(service);
        serviceClient = new ServiceClient(factory);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serviceRepositoryListener = new ServiceRepositoryListener() {
                    public void serviceAdded(ServiceProxy serviceProxy) {
                        BrowserTreeTableModel.this.serviceAdded(serviceProxy);
                    }
                    public void serviceRemoved(ServiceProxy serviceProxy) {
                        BrowserTreeTableModel.this.serviceRemoved(serviceProxy);
                    }
                };
                serviceRepository.addListener(serviceRepositoryListener);
            }
        });
    }
    private ServiceFactoryImpl factory;
    private ServiceRepositoryListener serviceRepositoryListener;

    public synchronized void switchToDomain(final String domain, final Runnable andThen) {
        serviceRepository.removeListener(serviceRepositoryListener, true);
        serviceRepository.stop();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serviceRepository = Digger.createServiceRepository(factory, service, domain);
                serviceRepository.addListener(serviceRepositoryListener);
                if (andThen != null) {
                    andThen.run();
                }
            }
        });
    }

    public synchronized void switchToFilter(final ServiceFilter serviceFilter, final Runnable andThen) {
        serviceRepository.removeListener(serviceRepositoryListener, true);
        serviceRepository.stop();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serviceRepository = factory.createServiceRepository(service);
                serviceRepository.addListener(serviceRepositoryListener, serviceFilter);
                if (andThen != null) {
                    andThen.run();
                }
            }
        });
    }

    public ServiceClient getServiceClient() {
        return serviceClient;
    }
    
    private synchronized void serviceAdded(ServiceProxy serviceProxy) {
        final Service s = new Service(this.service, serviceProxy);
        browserServices.add(s);
        proxyToBrowserService.put(serviceProxy, s);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fireTreeNodesInserted(getRoot(), new Object[]{getRoot()}, new int[]{browserServices.indexOf(s)}, new Object[]{s});
            }

        });
    }
    
    private synchronized void serviceRemoved(ServiceProxy serviceProxy) {
        final Service removedService = proxyToBrowserService.remove(serviceProxy);
        final int index = browserServices.indexOf(removedService);
        if (index != -1) {
            browserServices.remove(removedService);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireTreeNodesRemoved(getRoot(), new Object[]{getRoot()}, new int[]{index}, new Object[]{removedService});
                }
            });
        }
    }
    
    private synchronized void updateServiceElements(final Service service, ServiceProxy serviceProxy, Vector<ServiceElement> elements) {
        final int index = browserServices.indexOf(service);
        if (index != -1) {
            cache.put(serviceProxy, elements);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    fireTreeStructureChanged(service, new Object[]{getRoot(), service}, new int[]{}, new Object[]{});
                    // The following code is not what is expected by the default handler of jtable,
                    // and this handler will reload everything is the provided path is only root
                    //fireTreeStructureChanged(getRoot(), new Object[]{getRoot()}, new int[]{index}, new Object[]{service});
                }
            });            
        }
    }


    //// Utility methods
    
    private WeakHashMap<ServiceProxy, Vector<ServiceElement>> cache = new WeakHashMap<ServiceProxy, Vector<ServiceElement>>();
    private synchronized List<?> getChilds(Object parent) {
        if (parent == theRoot) return browserServices;
        if (parent instanceof Service) {
            final Service parentService = (Service) parent;
            final ServiceProxy serviceProxy = ((Service) parent).getServiceProxy();
            Vector<ServiceElement> res = cache.get(serviceProxy);
            if (res != null) return res;
            res = new Vector<ServiceElement>();
            res.add(ServiceElement.notAnElement);
            cache.put(serviceProxy, res);
            new Thread(new Runnable() {
                public void run() {
                    Vector<String> constVariable = new Vector<String>();
                    Vector<String> readVariable = new Vector<String>();
                    Vector<String> writeVariable = new Vector<String>();
                    for (String variableName : serviceProxy.getVariables()) {
                        switch (serviceProxy.getVariableAccessType(variableName)) {
                        case READ:
                            readVariable.add(variableName);
                            break;
                        case READ_WRITE:
                            writeVariable.add(variableName);
                            break;
                        case CONSTANT:
                            constVariable.add(variableName);
                            break;
                        }
                    }
                    Vector<ServiceElement> elements = new Vector<ServiceElement>();
                    addAll(elements, parentService, ServiceElement.Type.Output, sort(serviceProxy.getOutputConnectors()));
                    addAll(elements, parentService, ServiceElement.Type.Input, sort(serviceProxy.getInputConnectors()));
                    addAll(elements, parentService, ServiceElement.Type.Bidirectional, sort(serviceProxy.getInputOutputConnectors()));
                    addAll(elements, parentService, ServiceElement.Type.ReadWriteVariable, sort(writeVariable));
                    addAll(elements, parentService, ServiceElement.Type.ReadOnlyVariable, sort(readVariable));
                    addAll(elements, parentService, ServiceElement.Type.Constant, sort(constVariable));
                    updateServiceElements(parentService, serviceProxy, elements);
                }
            }).start();
            return res;
        }
        return Collections.EMPTY_LIST;
    }
    private List<String> sort(Collection<String> collection) {
        Vector<String> res = new Vector<String>(collection);
        Collections.sort(res);
        return res;
    }
    private void addAll(Vector<ServiceElement> res, Service service, Type type, List<String> names) {
        for (String name : names) {
            res.add(new ServiceElement(service, name, type));
        }
    }
    
    TreeCellRenderer interceptorRender(final TreeCellRenderer delegate) {
        return new TreeCellRenderer() {
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof Service) {
                    value = ((Service) value).getName();
                } else if (value instanceof BrowserTreeTableModel) {
                    value = "All Services";
                }
                System.out.println(this + " " + delegate);
                return delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            }
        };
    }

    //// Methods specific to TreeModel
    
    private void fireTreeNodesRemoved(Object root, Object[] path, int[] childIndices, Object[] children) {
        Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();
        synchronized (treeModelListeners) {
            listeners.addAll(treeModelListeners);
        }
        for (TreeModelListener l : listeners) {
            l.treeNodesRemoved(new TreeModelEvent(this, path, childIndices, children));
        }
    }

    private void fireTreeNodesInserted(Object root, Object[] path, int[] childIndices, Object[] children) {
        Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();
        synchronized (treeModelListeners) {
            listeners.addAll(treeModelListeners);
        }
        for (TreeModelListener l : listeners) {
            l.treeNodesInserted(new TreeModelEvent(this, path, childIndices, children));
        }
    }

    private void fireTreeStructureChanged(Object root, Object[] path, int[] childIndices, Object[] children) {
        Vector<TreeModelListener> listeners = new Vector<TreeModelListener>();
        synchronized (treeModelListeners) {
            listeners.addAll(treeModelListeners);
        }
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(new TreeModelEvent(this, path, childIndices, children));
        }
    }

    public static class Root {
        @Override
        public String toString() {
            return "Service List";
        }
    }
    private Root theRoot = new Root();
    public Object getRoot() {
        return theRoot;
    }

    public Object getChild(Object parent, int index) {
        try {
            return getChilds(parent).get(index);
        } catch (Exception e) {
            return null;
        }
    }

    public int getChildCount(Object parent) {
        return getChilds(parent).size();
    }

    public boolean isLeaf(Object node) {
        //System.out.println(node.getClass());
        //System.out.println(getChilds(node).isEmpty());
        return !(node instanceof Root) && !(node instanceof Service);
                //|| getChilds(node).isEmpty();//getChilds(node).isEmpty();
    }

    public int getIndexOfChild(Object parent, Object child) {
        return getChilds(parent).indexOf(child);
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private final Vector<TreeModelListener> treeModelListeners = new Vector<TreeModelListener>();
    public void addTreeModelListener(TreeModelListener l) {
        synchronized (treeModelListeners) {
            treeModelListeners.add(l);
        }
    }

    public void removeTreeModelListener(TreeModelListener l) {
        synchronized (treeModelListeners) {
            treeModelListeners.remove(l);
        }
    }



    //// Methods specific to RowModel

    public int getColumnCount() {
        return 3;
    }

    public Object getValueFor(Object object, int i) {
        if (object == getRoot()) {
            return "";
        }
        /*
        if (i == 0) {
            return object;
        }*/
        if (object instanceof Service) {
            ServiceProxy serviceProxy = ((Service) object).getServiceProxy();
            try {
                switch (i) {
                case 0: return Utility.intTo8HexString(serviceProxy.getPeerId());
                case 1: return serviceProxy.getHostName();
                case 2: return serviceProxy.getVariableValue("owner");
                default: return "";
                }
            } catch (Exception e) {
                return "";
            }
        }
        if (object instanceof ServiceElement) {
            /*
            ServiceElement serviceElement = (ServiceElement) object;
            try {
                switch (i) {
                case 1: return serviceElement.getType().toString();
                default: return "";
                }
            } catch (Exception e) {
                return "";
            }
             */
        }
        return "";
    }

    public Class getColumnClass(int column) {
        return column == 0 ? Object.class : String.class;
    }

    public boolean isCellEditable(Object arg0, int arg1) {
        return false;
    }

    public void setValueFor(Object arg0, int arg1, Object arg2) {
        throw new RuntimeException("unsuported edition of Browser table tree model");
    }

    public String getColumnName(int column) {
        switch (column) {
        //case 0: return "";
        case 0: return "id/type";
        case 1: return "host";
        case 2: return "owner";
        default: return "";
        }
    }

    
}
