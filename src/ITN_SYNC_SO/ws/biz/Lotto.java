/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ITN_SYNC_SO.ws.biz;

/**
 *
 * @author lucio
 */
public class Lotto extends XmlUtil{
    // XSD: scadenza unsignedLong, codice unsignedLong -> usiamo long >= 0
        public String scadenza;         // required, unsignedLong
        public String descrizione;    // required
        public Long codice;           // required, unsignedLong

        private Lotto() {
        }

    /**
     * @return the scadenza
     */
    public String getScadenza() {
        return scadenza;
    }

    /**
     * @return the descrizione
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * @return the codice
     */
    public Long getCodice() {
        return codice;
    }

        public static Builder builder() {
            return new Builder();
        }

        public void validate() {
            /*requireUnsigned(scadenza, "lotto.scadenza");
            requireNotBlank(descrizione, "lotto.descrizione");
            requireUnsigned(codice, "lotto.codice");*/
        }

        public void appendXml(StringBuilder sb) {
            sb.append("<lotto>");
            appendTag(sb, "scadenza", String.valueOf(scadenza));
            appendTag(sb, "descrizione", descrizione);
            appendTag(sb, "codice", String.valueOf(codice));
            sb.append("</lotto>");
        }

        public static final class Builder {

            private final Lotto l = new Lotto();

            public Builder scadenza(String v) {
                l.scadenza = v;
                return this;
            }

            public Builder descrizione(String v) {
                l.descrizione = v;
                return this;
            }

            public Builder codice(long v) {
                l.codice = v;
                return this;
            }
            
            public Lotto build() {
                l.validate();
                return l;
            }
        }
}
