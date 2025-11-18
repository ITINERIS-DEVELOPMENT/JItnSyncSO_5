/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ITN_SYNC_SO.ws;

import ITN_SYNC_SO.ws.biz.Carico;
import ITN_SYNC_SO.ws.biz.Kit;
import ITN_SYNC_SO.ws.biz.Lotto;
import ITN_SYNC_SO.ws.biz.Request;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class SoapKitFerriMessageWriter {

    private static final String SOAP_ENV_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String SAP_NS = "urn:sap.com:proxy:PRD:/1SAI/TXS3A4B2F48D553AFEB6A7D:700:2008/06/25";

    /**
     * Genera il messaggio SOAP completo come String.
     * @param request
     * @return 
     * @throws java.lang.Exception
     */
    public String buildSoapMessage(Request request) throws Exception {
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        // <soapenv:Envelope ...>
        Element envelope = doc.createElementNS(SOAP_ENV_NS, "soapenv:Envelope");
        envelope.setAttribute("xmlns:soapenv", SOAP_ENV_NS);
        envelope.setAttribute("xmlns:ns", SAP_NS);
        doc.appendChild(envelope);

        // <soapenv:Header/>
        Element header = doc.createElementNS(SOAP_ENV_NS, "soapenv:Header");
        envelope.appendChild(header);

        // <soapenv:Body>
        Element body = doc.createElementNS(SOAP_ENV_NS, "soapenv:Body");
        envelope.appendChild(body);

        // <ns:KitFerri.Import.Request>
        Element root = doc.createElementNS(SAP_NS, "ns:KitFerri.Import.Request");
        body.appendChild(root);

        // <ns:listaCarichi>
        Element listaCarichiEl = doc.createElementNS(SAP_NS, "ns:listaCarichi");
        root.appendChild(listaCarichiEl);

        if (request.getListaCarichi() != null) {
            for (Carico carico : request.getListaCarichi()) {
                if (carico == null) continue;
                Element caricoEl = doc.createElementNS(SAP_NS, "ns:carico");
                listaCarichiEl.appendChild(caricoEl);

                appendChildWithText(doc, caricoEl, "ns:spedizioneId", carico.getSpedizioneId());
                appendChildWithText(doc, caricoEl, "ns:data", carico.getData());
                appendChildWithText(doc, caricoEl, "ns:servizio", carico.getServizio());
                appendChildWithText(doc, caricoEl, "ns:note", carico.getNote());
                appendChildWithText(doc, caricoEl, "ns:utenteInserimento", carico.getUtenteInserimento());
                appendChildWithText(doc, caricoEl, "ns:tipoOperazione", carico.getTipoOperazione());
                appendChildWithText(doc, caricoEl, "ns:quantita", carico.getQuantita());

                // <ns:kit>
                Kit kit = carico.getKit();
                if (kit != null) {
                    Element kitEl = doc.createElementNS(SAP_NS, "ns:kit");
                    caricoEl.appendChild(kitEl);

                    appendChildWithText(doc, kitEl, "ns:codice", kit.getCodice());
                    appendChildWithText(doc, kitEl, "ns:descrizione", kit.getDescrizione());
                    appendChildWithText(doc, kitEl, "ns:Id", kit.getId());
                    appendChildWithText(doc, kitEl, "ns:prezzo", kit.getPrezzo());
                }

                // <ns:lotto>
                Lotto lotto = carico.getLotto();
                if (lotto != null) {
                    Element lottoEl = doc.createElementNS(SAP_NS, "ns:lotto");
                    caricoEl.appendChild(lottoEl);

                    appendChildWithText(doc, lottoEl, "ns:scadenza", Long.toString(lotto.getScadenza()));
                    appendChildWithText(doc, lottoEl, "ns:descrizione", lotto.getDescrizione());
                    appendChildWithText(doc, lottoEl, "ns:codice", Long.toString(lotto.getCodice()));
                }

                appendChildWithText(doc, caricoEl, "ns:interventoId", carico.getInterventoId());
            }
        }

        // Trasforma il Document in String
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(sw));

        return sw.toString();
    }

    /**
     * Crea un figlio col nome qName e testo value.
     * Se value è null ? non crea il nodo.
     * Se value è stringa vuota ? crea il nodo vuoto (<tag/>).
     */
    private void appendChildWithText(Document doc, Element parent, String qName, String value) {
        if (value == null) {
            return;
        }
        Element child = doc.createElementNS(SAP_NS, qName);
        if (!value.isEmpty()) {
            child.setTextContent(value);
        }
        parent.appendChild(child);
    }
}
