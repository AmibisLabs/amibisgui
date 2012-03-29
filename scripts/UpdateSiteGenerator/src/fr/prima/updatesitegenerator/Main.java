/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.prima.updatesitegenerator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author emonet
 */
public class Main {

    static XPathFactory xpf = XPathFactory.newInstance();

    public static class Module {
        Element moduleElement;
        String category;
        long size;
        String fileName;

        public Module(Element moduleElement, String category, long size, String fileName) {
            this.moduleElement = moduleElement;
            this.category = category;
            this.size = size;
            this.fileName = fileName;
        }

        private String getXpathString(String xpath) throws XPathExpressionException {
            return (String) xpf.newXPath().evaluate(xpath, moduleElement, XPathConstants.STRING);
        }

        private String getManifestAttribute(String attributeName) {
            return ((Element) moduleElement.getElementsByTagName("manifest").item(0)).getAttribute(attributeName);
        }

        private void setManifestAttribute(String attributeName, String value) {
            ((Element) moduleElement.getElementsByTagName("manifest").item(0)).setAttribute(attributeName, value);
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws XPathExpressionException, TransformerConfigurationException, FileNotFoundException {
        if (args.length == 0) args = new String[] {"/tmp/nbms", "http://plop/", "/tmp/nbms/updates.xml"};
        if (args.length != 3) exit("Wrong number of arguments");

        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        File nbmFolder = new File(args[0]);
        String distBase = args[1];
        File targetFile = new File(args[2]);

        if (!nbmFolder.isDirectory()) exit("Not a directory: '"+args[0]+"'");
        if (!nbmFolder.canRead()) exit("Not readable: '"+args[0]+"'");
        if (!distBase.endsWith("/")) warn("Not ending with '/': '"+distBase+"'");
        if (targetFile.exists() && !targetFile.canWrite()) exit("Not writable: '"+args[2]+"'");


        Map<String/*Category*/, List<Module>> categories = new HashMap<String, List<Module>>();
        Map<String/*LicenseText*/, String/*PossibleID*/> licenses = new HashMap<String, String>();
        XPathExpression moduleXpath = xpf.newXPath().compile("/module");
        XPathExpression relativeCategoryXpath = xpf.newXPath().compile("./manifest/@OpenIDE-Module-Display-Category");


        for (File file : nbmFolder.listFiles()) {
            try {
                if (!file.getName().endsWith(".nbm")) continue;
                ZipFile zipFile = new ZipFile(file);
                ZipEntry zipEntry = zipFile.getEntry("Info/info.xml");
                InputStream infoXmlStream = zipFile.getInputStream(zipEntry);
                Document infoXmlDocument = parseXMLStream(infoXmlStream);

                Element module = (Element) moduleXpath.evaluate(infoXmlDocument.getDocumentElement(), XPathConstants.NODE);
                String category = (String) relativeCategoryXpath.evaluate(module, XPathConstants.STRING);
                System.err.println(file.getName() + ": " + category);

                if (!categories.containsKey(category)) categories.put(category, new ArrayList<Module>());
                Module mod = new Module(module, category, file.length(), file.getName());
                categories.get(category).add(mod);

                licenses.put(mod.getXpathString("./license/text()"), mod.getXpathString("./license/@name"));

            } catch (ZipException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        {
            Document updatesXml = createDomDocument();
            Node base = updatesXml;
            // <module_updates timestamp="18/15/17/24/09/2009">
            Element e = updatesXml.createElement("module_updates");
            e.setAttribute("timestamp", nowAs("%tS/%tM/%tk/%td/%tm/%tY".replace("%", "%1$")));
            base.appendChild(e);
            base = e;
            for (String category : categories.keySet()) {
                // <module_group name="Infrastructure">
                Element cat = updatesXml.createElement("module_group");
                cat.setAttribute("name", category);
                for (Module module : categories.get(category)) {
                    updatesXml.adoptNode(module.moduleElement);
                    String impl = module.getManifestAttribute("OpenIDE-Module-Implementation-Version");
                    String spec = module.getManifestAttribute("OpenIDE-Module-Specification-Version");
                    boolean isAMavenModule = spec != null && !spec.isEmpty() && impl.startsWith(spec + "-");
                    if (isAMavenModule) {
                        //module.setManifestAttribute("OpenIDE-Module-Specification-Version", impl.replaceAll("-", ".0."));
                        //module.moduleElement.getAttribute("distribution").replaceFirst("^null/", distBase.replaceAll(".", "\\\\$0"))
                    } else {
                    }
                    String licenseId = licenses.get(module.getXpathString("./license/text()"));
                    module.moduleElement.setAttribute("distribution", distBase + module.fileName);
                    module.moduleElement.setAttribute("downloadsize", "" + module.size);
                    module.moduleElement.setAttribute("license", licenseId);
                    cat.appendChild(module.moduleElement);
                }
                base.appendChild(cat);
            }
            for (Map.Entry<String, String> license : licenses.entrySet()) {
                e = updatesXml.createElement("license");
                e.setAttribute("name", license.getValue());
                e.setTextContent(license.getKey());
                base.appendChild(e);
            }

            try {
                DOMSource source = new DOMSource(updatesXml);
                OutputStream out = new FileOutputStream(targetFile);
                StreamResult streamResult = new StreamResult(targetFile);
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(source, streamResult);
                out.close();
            } catch (IOException ex) {
                // from out.close
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String nowAs(String format) {
        return new Formatter().format(format, Calendar.getInstance()).toString();
    }

    public static Document createDomDocument() {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return doc;

    }

    public static Document parseXMLStream(InputStream stream) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbf.newDocumentBuilder();
            // this call is a trick to prevent validation (http://forums.sun.com/thread.jspa?threadID=284209&forumID=34)
            // and thus be independent of the status of netbeans website
            docBuilder.setEntityResolver(new EntityResolver() {

                public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId)
                        throws SAXException, java.io.IOException {
                    if (systemId.startsWith("http://www.netbeans.org/dtds"))
                    {
                        return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
                    } else {
                        return null;
                    }
                }
            });

            doc = docBuilder.parse(stream);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private static void warn(String message) {
        System.err.println("WARNING: "+message);
    }
    private static void exit(String message) {
        System.err.println("ERROR:  "+ message);
        System.exit(1);
    }

}
