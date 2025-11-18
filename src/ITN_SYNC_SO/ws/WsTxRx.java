/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ITN_SYNC_SO.ws;

import ITN_SYNC_SO.Synch;
import ITN_SYNC_SO.db.CommandInterface;
import ITN_SYNC_SO.db.DbProperties;
import ITN_SYNC_SO.db.rx.UpdTxInterface;
import ITN_SYNC_SO.ws.biz.*;
import org.apache.log4j.Logger;

/**
 *
 * @author lucio
 */
public class WsTxRx implements UpdTxInterface, CommandInterface {

    private final Logger logger = Logger.getLogger(WsTxRx.class);

    private final DbProperties dbProperties;

    public WsTxRx(DbProperties dbProperties) {
        this.dbProperties = dbProperties;
    }

    @Override
    public boolean excecuteUpdate(Synch synch) {
        boolean ret = false;

        try {
            Request req = loadData(synch);

            String parametri = new SoapKitFerriMessageWriter().buildSoapMessage(req);

            WebServiceHandler wsh = new WebServiceHandler(dbProperties);
            String resp = wsh.execRequestAndGetBody(parametri);

            logger.debug("Web service response: " + resp);

            if (resp != null) {
                ret = resp.contains("<responseStatus>0</responseStatus>");
            }

            if (!ret) {
                logger.error("Web service response: " + resp);
            }
        } catch (Exception ex) {
            logger.error("Web service response error", ex);
        }

        return ret;
    }

    @Override
    public boolean getNewConnection() {
        return true;
    }

    @Override
    public void shutdown() {
        //not used
    }

    private Request loadData(Synch synch) {
        String tipoOperazione = "";
        switch (synch.getCommand()) {
            case DF_OPR_NEW:
                tipoOperazione = "Inserimento";
                break;
            case DF_OPR_DEL:
                tipoOperazione = "Cancellazione";
                break;
            case DF_OPR_UPD:
                tipoOperazione = "Modifica";
                break;
            case DF_OPR_FROM_SALA:
                tipoOperazione = "FromSala";
                break;
            case DF_OPR_REFRESH_DEST:
                tipoOperazione = "RefreshDest";
                break;
        }

        logger.debug(String.format("idspedizione=%s, data=%s, tipo operazione=%s, codice kit=%s, desc kit=%s, id kit=%s, scadenza=%s, scadenza long=%s, lotto long=%s", 
                synch.getIdSpedizione(), synch.getData(), tipoOperazione, synch.getCodiceKit(), synch.getDescKit(), 
                synch.getIdKit(), synch.getData(), Long.toString(synch.getDataScadLong()), Long.toString(synch.getLottoNumerico())));
        
        return new Request()
                .addCarico(
                        Carico.builder()
                                .spedizioneId(Integer.toString(synch.getIdSpedizione()))
                                .data(synch.getData()) // xs:anySimpleType -> decidi tu il formato
                                .servizio("")
                                .note("")
                                .utenteInserimento("")
                                .tipoOperazione(tipoOperazione)
                                .quantita("1")
                                .kit(Kit.builder()
                                        .codice(synch.getCodiceKit())
                                        .descrizione(synch.getDescKit())
                                        .id(synch.getIdKit())
                                        .prezzo("")
                                        .build())
                                .lotto(Lotto.builder()
                                        .scadenza(synch.getDataScadLong()) // unsignedLong
                                        .descrizione(synch.getLotto())
                                        .codice(synch.getLottoNumerico()) // unsignedLong
                                        .build())
                                .interventoId("")
                                .build()
                );

        /*String xml = req.toXml();
        System.out.println(xml);

        // Se vuoi bytes UTF-8:
        byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);*/
    }

    public static void main(String[] args) {

        String input = "Abc 12345";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)$").matcher(input);
        if (m.find()) {
            System.out.println((m.group(1)));
        } else {
            System.out.println("0");
        }
    }
}
