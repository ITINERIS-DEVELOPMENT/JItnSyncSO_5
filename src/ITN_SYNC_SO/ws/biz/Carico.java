/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ITN_SYNC_SO.ws.biz;

/**
 *
 * @author lucio
 */
public class Carico extends XmlUtil {

    public String spedizioneId;           // required
    public String data;                   // xs:anySimpleType -> manteniamo String
    public String servizio;               // required
    public String note;                   // optional
    public String utenteInserimento;      // optional
    public String tipoOperazione;         // optional
    public String quantita;               // optional (XSD la mette string)
    public Kit kit;                       // required
    public Lotto lotto;                   // required
    public String interventoId;           // optional

    private Carico() {
    }

    /**
     * @return the spedizioneId
     */
    public String getSpedizioneId() {
        return spedizioneId;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @return the servizio
     */
    public String getServizio() {
        return servizio;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @return the utenteInserimento
     */
    public String getUtenteInserimento() {
        return utenteInserimento;
    }

    /**
     * @return the tipoOperazione
     */
    public String getTipoOperazione() {
        return tipoOperazione;
    }

    /**
     * @return the quantita
     */
    public String getQuantita() {
        return quantita;
    }

    /**
     * @return the kit
     */
    public Kit getKit() {
        return kit;
    }

    /**
     * @return the lotto
     */
    public Lotto getLotto() {
        return lotto;
    }

    /**
     * @return the interventoId
     */
    public String getInterventoId() {
        return interventoId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        /*requireNotBlank(spedizioneId, "spedizioneId");
        requireNotBlank(data, "data");
        requireNotBlank(servizio, "servizio");
        if (kit == null) {
            throw new IllegalArgumentException("kit è obbligatorio");
        }
        if (lotto == null) {
            throw new IllegalArgumentException("lotto è obbligatorio");
        }
        kit.validate();
        lotto.validate();*/
    }

    public void appendXml(StringBuilder sb) {
        sb.append("<carico>");
        appendTag(sb, "spedizioneId", spedizioneId);
        appendTag(sb, "data", data);
        appendTag(sb, "servizio", servizio);
        appendOptionalTag(sb, "note", note);
        appendOptionalTag(sb, "utenteInserimento", utenteInserimento);
        appendOptionalTag(sb, "tipoOperazione", tipoOperazione);
        appendOptionalTag(sb, "quantita", quantita);
        kit.appendXml(sb);
        lotto.appendXml(sb);
        appendOptionalTag(sb, "interventoId", interventoId);
        sb.append("</carico>");
    }

    public static final class Builder {

        private final Carico c = new Carico();

        public Builder spedizioneId(String v) {
            c.spedizioneId = v;
            return this;
        }

        public Builder data(String v) {
            c.data = v;
            return this;
        }

        public Builder servizio(String v) {
            c.servizio = v;
            return this;
        }

        public Builder note(String v) {
            c.note = v;
            return this;
        }

        public Builder utenteInserimento(String v) {
            c.utenteInserimento = v;
            return this;
        }

        public Builder tipoOperazione(String v) {
            c.tipoOperazione = v;
            return this;
        }

        public Builder quantita(String v) {
            c.quantita = v;
            return this;
        }

        public Builder kit(Kit v) {
            c.kit = v;
            return this;
        }

        public Builder lotto(Lotto v) {
            c.lotto = v;
            return this;
        }

        public Builder interventoId(String v) {
            c.interventoId = v;
            return this;
        }

        public Carico build() {
            c.validate();
            return c;
        }
    }
}
