/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ITN_SYNC_SO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author d.saponaro
 */
public class Synch {

    private int    command        = 0;
    private int    idSpedizione   = 0;
    private String data           = "";
    private String codiceKit      = "";
    private int    codiceKitNum   = 0;
    private String destCode       = "";
    private String descKit        = "";
    private String idKit          = "";
    private int    idKitNum       = 0;
    private String dataScad       = "";
    private String useFlg         = "";
    private String useSalaCode    = "";
    private String useSalaDesc    = "";
    private String useCodeBlocco  = "";
    private String useDescBlocco  = "";
    private String useDescReparto = "";
    private String useDataInt     = "";
    private String useIdInt       = "";
    private String useTiInt       = "";
    private String useSiglaOper   = "";
    private String useOra         = "";
    private String lotto          = "";
    private String numeroDDT      = "";
    private Date   dateTime       = null;

    /**
     * @return the idSpedizione
     */
    public int getIdSpedizione() {
        return idSpedizione;
    }

    /**
     * @param idSpedizione the idSpedizione to set
     */
    public void setIdSpedizione(int idSpedizione) {
        this.idSpedizione = idSpedizione;
    }

    public void setIdSpedizione(String idSpedizione) {
        this.idSpedizione = getInt(idSpedizione);
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * @return the codiceKit
     */
    public String getCodiceKit() {
        return codiceKit;
    }

    /**
     * @param codiceKit the codiceKit to set
     */
    public void setCodiceKit(String codiceKit) {
        this.codiceKit = codiceKit;
    }

    /**
     * @return the destCode
     */
    public String getDestCode() {
        return destCode;
    }

    /**
     * @param destCode the destCode to set
     */
    public void setDestCode(String destCode) {
        this.destCode = destCode;
    }

    /**
     * @return the descKit
     */
    public String getDescKit() {
        return descKit;
    }

    /**
     * @param descKit the descKit to set
     */
    public void setDescKit(String descKit) {
        this.descKit = descKit;
    }

    /**
     * @return the idKit
     */
    public String getIdKit() {
        return idKit;
    }

    /**
     * @param idKit the idKit to set
     */
    public void setIdKit(String idKit) {
        this.idKit = idKit;
    }

    /**
     * @return the dataScad
     */
    public String getDataScad() {
        return dataScad;
    }

    /**
     * @param dataScad the dataScad to set
     */
    public void setDataScad(String dataScad) {
        this.dataScad = dataScad;
    }

    public long getDataScadLong(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        try {
            return sdf.parse(data).getTime();
        } catch (ParseException ex) {
            return 0;
        }
    }
    
    /**
     * @return the command
     */
    public int getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(int command) {
        this.command = command;
    }

    public void setCommand(String command) {
        this.command = getInt(command);
    }

    /**
     * @return the useFlg
     */
    public String getUseFlg() {
        return useFlg;
    }

    /**
     * @param useFlg the useFlg to set
     */
    public void setUseFlg(String useFlg) {
        this.useFlg = useFlg;
    }

    /**
     * @return the useSalaCode
     */
    public String getUseSalaCode() {
        return(getString(useSalaCode));
    }

    /**
     * @param useSalaCode the useSalaCode to set
     */
    public void setUseSalaCode(String useSala) {
        this.useSalaCode = useSala;
    }

    /**
     * @return the useIdInt
     */
    public String getUseIdInt() {
        return(useIdInt == null ? "0" : useIdInt);
    }

    /**
     * @param useIdInt the useIdInt to set
     */
    public void setUseIdInt(String useIdInt) {
        this.useIdInt = useIdInt;
    }

    /**
     * @return the useTiInt
     */
    public String getUseTiInt() {
        return(getString(useTiInt));
    }

    /**
     * @param useTiInt the useTiInt to set
     */
    public void setUseTiInt(String useTiInt) {
        this.useTiInt = useTiInt;
    }

    /**
     * @param useTiInt the useTiInt to add
     */
    public void addUseTiInt(String useIcdCode, String useIcdDesc) {
        StringBuffer buff = new StringBuffer();
        buff.append("<ICD><CODE>");
        buff.append(useIcdCode);
        buff.append("</CODE><DESC>");
        buff.append(useIcdDesc);
        buff.append("</DESC></ICD>");
        useTiInt = useTiInt.concat(buff.toString());
    }

    /**
     * @return the useOra
     */
    public String getUseOra() {
        return(getString(useOra));
    }

    /**
     * @param useOra the useOra to set
     */
    public void setUseOra(String useOra) {
        this.useOra = useOra;
    }

    /**
     * @return the dateTime
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * @param dateTime the dateTime to set
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * @return the lotto
     */
    public String getLotto() {
        return(getString(lotto));
    }

    public long getLottoNumerico() {
        String input = getString(lotto);
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)$").matcher(input);
        if (m.find()) {
            return Long.parseLong(m.group(1));
        }
        return 0;
    }

    
    /**
     * @param lotto the lotto to set
     */
    public void setLotto(String lotto) {
        this.lotto = lotto;
    }

    /**
     * @return the useSalaDesc
     */
    public String getUseSalaDesc() {
        return(getString(useSalaDesc));
    }

    /**
     * @param useSalaDesc the useSalaDesc to set
     */
    public void setUseSalaDesc(String useSalaDesc) {
        this.useSalaDesc = useSalaDesc;
    }

    /**
     * @return the useDescReparto
     */
    public String getUseDescReparto() {
        return(getString(useDescReparto));
    }

    /**
     * @param useDescReparto the useDescReparto to set
     */
    public void setUseDescReparto(String useDescReparto) {
        this.useDescReparto = useDescReparto;
    }

    /**
     * @return the useDescBlocco
     */
    public String getUseDescBlocco() {
        return(getString(useDescBlocco));
    }

    /**
     * @param useDescBlocco the useDescBlocco to set
     */
    public void setUseDescBlocco(String useDescBlocco) {
        this.useDescBlocco = useDescBlocco;
    }

    /**
     * @return the useDataInt
     */
    public String getUseDataInt() {
        return(getString(useDataInt, 10));
    }

    /**
     * @param useDataInt the useDataInt to set
     */
    public void setUseDataInt(String useDataInt) {
        this.useDataInt = useDataInt;
    }

    /**
     * @return the useCodeBlocco
     */
    public String getUseCodeBlocco() {
        return(getString(useCodeBlocco));
    }

    /**
     * @param useCodeBlocco the useCodeBlocco to set
     */
    public void setUseCodeBlocco(String useCodeBlocco) {
        this.useCodeBlocco = useCodeBlocco;
    }

    public String getDatiGeneraliIntervento() {
        StringBuffer buff = new StringBuffer();
        buff.append("<REP>" + getUseDescReparto() + "</REP>");
        buff.append("<BLG>" + getUseDescBlocco() + "</BLG>");
        buff.append("<SALA>" + getUseSalaDesc() + "</SALA>");
        buff.append("<DATA>" + getUseDataInt() + "</DATA>");
        return(buff.toString());
    }

    private String getString(String pValue) {
        return(pValue != null ? pValue : "");
    }

    private String getString(String pValue, int lenght) {
        String ret = getString(pValue);
        return(ret.length() > 10 ? ret.substring(0,10) : ret);
    }

    private int getInt(String pValue) {
        try {
            return(Integer.parseInt((pValue != null ? pValue.trim() : "0")));
        }
        catch(Exception e) {
            return(0);
        }
    }

    /**
     * @return the codiceKitNum
     */
    public int getCodiceKitNum() {
        return codiceKitNum;
    }

    /**
     * @param codiceKitNum the codiceKitNum to set
     */
    public void setCodiceKitNum(int codiceKitNum) {
        this.codiceKitNum = codiceKitNum;
    }
    public void setCodiceKitNum(String codiceKitNum) {
        this.codiceKitNum = (codiceKitNum.length() > 0 ? Integer.parseInt(codiceKitNum) : 0);
    }

    /**
     * @return the idKitNum
     */
    public int getIdKitNum() {
        return idKitNum;
    }

    /**
     * @param idKitNum the idKitNum to set
     */
    public void setIdKitNum(int idKitNum) {
        this.idKitNum = idKitNum;
    }
    public void setIdKitNum(String idKitNum) {
        this.idKitNum = (idKitNum.length() > 0 ? Integer.parseInt(idKitNum) : 0);
    }

    /**
     * @return the useSiglaOper
     */
    public String getUseSiglaOper() {
        return useSiglaOper;
    }

    /**
     * @param useSiglaOper the useSiglaOper to set
     */
    public void setUseSiglaOper(String useSiglaOper) {
        this.useSiglaOper = useSiglaOper;
    }

    /**
     * @return the numeroDDT
     */
    public String getNumeroDDT() {
        return numeroDDT;
    }

    /**
     * @param numeroDDT the numeroDDT to set
     */
    public void setNumeroDDT(String numeroDDT) {
        this.numeroDDT = numeroDDT;
    }

}
