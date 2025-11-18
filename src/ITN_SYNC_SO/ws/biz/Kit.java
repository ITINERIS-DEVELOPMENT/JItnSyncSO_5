/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ITN_SYNC_SO.ws.biz;

/**
 *
 * @author lucio
 */
public class Kit extends XmlUtil {

    public String codice;         // required
    public String descrizione;    // required
    public String id;             // required (XSD: "Id")
    public String prezzo;         // optional (XSD: string)

    private Kit() {
    }

    /**
     * @return the codice
     */
    public String getCodice() {
        return codice;
    }

    /**
     * @return the descrizione
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the prezzo
     */
    public String getPrezzo() {
        return prezzo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void validate() {
        /*requireNotBlank(codice, "kit.codice");
        requireNotBlank(descrizione, "kit.descrizione");
        requireNotBlank(id, "kit.Id");*/
    }

    public void appendXml(StringBuilder sb) {
        sb.append("<kit>");
        appendTag(sb, "codice", codice);
        appendTag(sb, "descrizione", descrizione);
        appendTag(sb, "Id", id);
        appendOptionalTag(sb, "prezzo", prezzo);
        sb.append("</kit>");
    }

    public static final class Builder {

        private final Kit k = new Kit();

        public Builder codice(String v) {
            k.codice = v;
            return this;
        }

        public Builder descrizione(String v) {
            k.descrizione = v;
            return this;
        }

        public Builder id(String v) {
            k.id = v;
            return this;
        }

        public Builder prezzo(String v) {
            k.prezzo = v;
            return this;
        }

        public Kit build() {
            k.validate();
            return k;
        }
    }
}
