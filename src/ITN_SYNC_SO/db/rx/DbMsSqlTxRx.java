/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ITN_SYNC_SO.db.rx;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.apache.log4j.Logger;
import ITN_SYNC_SO.db.DbProperties;
import ITN_SYNC_SO.Synch;
import ITN_SYNC_SO.SynchTkit;
import ITN_SYNC_SO.db.CommandInterface;
import ITN_SYNC_SO.db.DbSqlDelete;
import ITN_SYNC_SO.db.DbSqlInsert;
import ITN_SYNC_SO.db.DbSqlInterface;
import ITN_SYNC_SO.db.DbSqlLogger;
import ITN_SYNC_SO.db.DbSqlUpdate;
import ITN_SYNC_SO.util.MyDate;
import ITN_SYNC_SO.util.MyUtil;
import java.io.FileWriter;
import java.io.RandomAccessFile;

/**
 *
 * @author d.saponaro
 */
public class DbMsSqlTxRx extends DbMsSqlInterface implements CommandInterface, UpdTxInterface {
    private final int tipo_get_item_scambio = 0;
    private final int tipo_get_item_tipokit = 1;
    private final int tipo_get_item_scambio_short = 2;

    private String driver = "";
    private String jdbc   = "";
    private String url    = "";

    private String tableScambio = "";
    private String tableAnaKit  = "";

    private String funct_timestamp = "";

    private int tipo_get_item = tipo_get_item_scambio;

    private Connection connection = null;

    private DbMsSqlViste vista = null;
    
    private Vector vtSynch = new Vector();

    private Logger log = Logger.getLogger(DbMsSqlTxRx.class);
    
    public DbMsSqlTxRx(DbProperties dbProperties){
        super(dbProperties);
        setDriver();
        setUrl();
    }

    private boolean setDriver(){
        boolean ret = false;

        try {

            if(dbProperties.isMSSQL()) {
                driver   = "net.sourceforge.jtds.jdbc.Driver";
                jdbc      = "jdbc:jtds:sqlserver://";
                funct_timestamp = "GETDATE()";
                //jdbc      = "jdbc:jtds:sqlserver://10.0.0.35:1199/SQLEXPRESS;databaseName=JAVATEST";

                DriverManager.registerDriver(new net.sourceforge.jtds.jdbc.Driver());
                ret = true;
            }
            else if(dbProperties.isORACLE()) {
                driver   = "oracle.jdbc.driver.OracleDriver";
                jdbc      = "jdbc:oracle:thin:@//";
                funct_timestamp = "CURRENT_TIMESTAMP";

                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
                ret = true;
            }

        }
        catch (Exception e) {
            log.error("*** Connection Driver Not Found Error : " + e.getMessage());
        }

        if(dbProperties.isORMAWIN() || dbProperties.isORMAWIN_V2())
            tableScambio = tableScambioOW2K;
        else if(dbProperties.isNEGRAR())
            tableScambio = tableScambioDIGI;
        else if(dbProperties.isDIGISTAT()) {
            tableScambio = tableScambioDIGI;
            tableAnaKit  = tableTipoKit;
        }
        else if(dbProperties.isSIX()) {
            tableScambio = tableScambioSIX;
            tableAnaKit  = tableTipoKitSIX;
        }

        return(ret);
    }

    private void setUrl(){
        StringBuffer sb = new StringBuffer();
        sb.append(jdbc);
        sb.append(dbProperties.getMssqlIpAddress());
        sb.append(":");
        sb.append(dbProperties.getMssqlPort());
        if(dbProperties.isORACLE())
            sb.append("/");
        else
            sb.append(";databaseName=");
        sb.append(dbProperties.getMssqlDbName());
        url = sb.toString();
    }

    public boolean excecuteUpdate(Synch synch){
        DbSqlInterface sql = null;
        boolean ret = true;

        if(synch.getCommand() == DF_OPR_NEW) {
            if(dbProperties.isDIGISTAT() || dbProperties.isNEGRAR())
                sql = getSqlInsert(synch);
            else if(!executeKitSeek(synch))
                sql = getSqlInsert(synch);
            else if(dbProperties.isSIX())
                sql = getSqlInsert(synch, false);
        }
        else
            sql = getSqlUpdate(synch);

        if(sql != null) {
            if(dbProperties.isSIX())
                sql.addFunction(DF_NAME_TIMESTAMP_ITN, funct_timestamp);
            else if(dbProperties.isUseTimestamp_params() || dbProperties.isORMAWIN() || dbProperties.isORMAWIN_V2())
                sql.addFunction(DF_NAME_LAST_UPD_ITINERIS, funct_timestamp);

            ret = execSqlCommand(sql.getSql(), true);

            if(ret && dbProperties.isSqlDebugLog()) DbSqlLogger.sqlDebugLog(sql.getSql());

            if( !ret && synch.getCommand() != DF_OPR_DEL )
                log.error("SqlCommand Level 1, sql=" + sql.getSql());

            if( ret && synch.getCommand() == DF_OPR_NEW ) {
                if (! (ret = executeKitSeek(synch)) )
                    log.error("SqlCommand Level 2, sql=" + sql.getSql());
            }
            else if(!ret && synch.getCommand() == DF_OPR_DEL)
                ret = !executeKitSeek(synch);   // se non esiste non va ritrasmesso
        }

        return(ret);
    }

    private boolean executeKitSeek(Synch synch) {
        tipo_get_item = tipo_get_item_scambio_short;

        boolean ret = false;
        String sql = getSqlSeek(synch.getIdKit());
        if(execSqlQuery(sql))
            ret = vtSynch.size() > 0;
        return(ret);
    }

    public boolean excecuteUpdate(SynchTkit synch){
        DbSqlInterface sql;

        if(synch.getCommand() == DF_OPR_DEL)
            sql = getSqlDelete(synch);

        else {// if(synch.getCommand() == DF_OPR_NEW)
            if(executeSeek(synch))
                sql = getSqlUpdate(synch);
            else
                sql = getSqlInsert(synch);
        }

        return execSqlCommand(sql.getSql(), true);
    }

    public boolean executeSeek(SynchTkit synch) {
        tipo_get_item = tipo_get_item_tipokit;

        boolean ret = false;
        String sql = getSqlSeekTK(synch);
        if(execSqlQuery(sql))
            ret = vtSynch.size() > 0;
        return(ret);
    }

    public Vector excecuteList(String lastDateTime){
        tipo_get_item = tipo_get_item_scambio;

        String sql = getSqlList(lastDateTime);
        if(dbProperties.isSqlLogger()) DbSqlLogger.sqlLog(sql);
        if(execSqlQuery(sql))
            return vtSynch;
        else
            return null;
    }

    public boolean execListInterventi(Synch synch) {
        tipo_get_item = tipo_get_item_scambio;

        boolean ret;
        vista = new DbMsSqlViste(dbProperties, df_proc_view_interventi, synch);
        String sql = vista.getSqlListInterventi(Integer.parseInt(synch.getUseIdInt()));
        ret = execSqlQuery(sql);
        vista = null;
        return(ret);
    }

    private Object getItem(ResultSet rs) throws SQLException{
        if(tipo_get_item == tipo_get_item_scambio || tipo_get_item == tipo_get_item_scambio_short)
            return(getItem_Scambio(rs));
        else
            return(getItem_TipoKit(rs));
    }

    private Synch getItem_Scambio(ResultSet rs) throws SQLException{
        Synch synch = new Synch();

        synch.setCodiceKit(rs.getString(DF_NAME_COD_KIT));
        synch.setIdSpedizione(Integer.parseInt(rs.getString(DF_NAME_ID_SPEDIZIONE)));
        synch.setIdKit(rs.getString(DF_NAME_ID_KIT));

        if(!dbProperties.isSIX()) {
            synch.setUseFlg(rs.getString(DF_NAME_FL_USATO));
            synch.setUseIdInt(rs.getString(DF_NAME_ID_INTERVENTO));
        }

        if(tipo_get_item == tipo_get_item_scambio) {

            if(dbProperties.isORMAWIN()) {
                synch.setDateTime(rs.getTimestamp(DF_NAME_LAST_UPD_ORMAWIN));
                // viewSala
                synch.setUseSalaCode(rs.getString(DF_FLD_VSALA_CODE));
                synch.setUseSalaDesc(rs.getString(DF_FLD_VSALA_DESC));
                /*
                synch.setUseCodeBlocco(rs.getString(DF_FLD_VSALA_CODE_BLOC));
                synch.setUseDescBlocco(rs.getString(DF_FLD_VSALA_DESC_BLOC));
                synch.setUseDescReparto(rs.getString(DF_FLD_VSALA_DESC_REP));
                synch.setUseDataInt(rs.getString(DF_FLD_VSALA_DATA_INT));
                */

                // viewSetFerri
                synch.setUseOra(MyDate.toString(rs.getTimestamp(DF_FLD_VKIT_DATETIME), "yyyy-MM-dd HH:mm:ss"));

            }
            
            else if(dbProperties.isORMAWIN_V2()) {
                synch.setDateTime(rs.getTimestamp(DF_NAME_LAST_UPD_ORMAWIN));
                // viewSala
                synch.setUseSalaCode(rs.getString(DF_FLD_SALA_CODE_V2));
                synch.setUseSalaDesc(rs.getString(DF_FLD_SALA_DESC_V2));
                /*
                synch.setUseCodeBlocco(rs.getString(DF_FLD_VSALA_CODE_BLOC));
                synch.setUseDescBlocco(rs.getString(DF_FLD_VSALA_DESC_BLOC));
                synch.setUseDescReparto(rs.getString(DF_FLD_VSALA_DESC_REP));
                synch.setUseDataInt(rs.getString(DF_FLD_VSALA_DATA_INT));
                */

                // viewSetFerri
                //synch.setUseOra(MyDate.toString(rs.getTimestamp(DF_FLD_VKIT_DATETIME), "yyyy-MM-dd HH:mm:ss"));
            }
            
            else { // if(dbProperties.isDIGISTAT())
                if(dbProperties.isUseTimestamp_params())
                    synch.setDateTime(rs.getTimestamp(DF_NAME_LAST_UPD_EXTERN));
                synch.setUseSalaCode(rs.getString(DF_NAME_SALA_CODE));
                synch.setUseSalaDesc(rs.getString(DF_NAME_SALA_DESC));
                synch.setUseDataInt(rs.getString(DF_NAME_SALA_DATAORA));
                synch.setUseSiglaOper(rs.getString(DF_NAME_SALA_SIGLAOP));
                synch.setUseOra(MyDate.toString(rs.getTimestamp(DF_NAME_SALA_DATAORA), "yyyy-MM-dd HH:mm:ss"));
            }

            if(dbProperties.isSqlLogger()) DbSqlLogger.sqlLog(synch);
        }

        return synch;
    }

    private SynchTkit getItem_TipoKit(ResultSet rs) throws SQLException{
        SynchTkit synch = new SynchTkit();

        // utilizzata solo per test esistenza
        if(dbProperties.isDIGISTAT()) {
            synch.setCodiceKitNum(rs.getString(DF_NAME_COD_KIT_NUM));
            synch.setCodiceKit(rs.getString(DF_NAME_COD_KIT));
        }
        else  // if(dbProperties.isSIX())
            synch.setCodiceKit(rs.getString(DF_NAME_COD_KIT_ITN));

        return synch;
    }

    private DbSqlInterface getSqlInsert(Synch synch){
        return(getSqlInsert(synch, true));
    }

    private DbSqlInterface getSqlInsert(Synch synch, boolean flgInsert){
        DbSqlInterface sql;

        if(flgInsert)
            sql = new DbSqlInsert(tableScambio);
        else
            sql = new DbSqlUpdate(tableScambio);
        
        String dataScadField = (dbProperties.isSIX() ? DF_NAME_DATA_SCAD_SIX : DF_NAME_DATA_SCAD);

        sql.addInt(DF_NAME_ID_SPEDIZIONE, synch.getIdSpedizione());
        if(dbProperties.isORACLE()) {
            sql.addDateOracle(DF_NAME_DATA, synch.getData());
            sql.addDateOracle(dataScadField, synch.getDataScad());
        }
        else {
            sql.addString(DF_NAME_DATA, synch.getData());
            sql.addString(dataScadField, synch.getDataScad());
        }
        sql.addString(DF_NAME_COD_KIT, synch.getCodiceKit());
        sql.addString(DF_NAME_DESCR_KIT, synch.getDescKit());

        if(dbProperties.isDIGISTAT() || dbProperties.isNEGRAR()) {
            sql.addInt(DF_NAME_COD_KIT_NUM, synch.getCodiceKitNum());
            sql.addInt(DF_NAME_ID_KIT_NUM, synch.getIdKitNum());
            sql.addString(DF_NAME_FL_USATO, "0");
        }
        else if(dbProperties.isORMAWIN() || dbProperties.isORMAWIN_V2()) {
            sql.addString(DF_NAME_LOTTO, synch.getLotto());
            sql.addString(DF_NAME_FL_USATO, "0");
        }
        else if(dbProperties.isSIX()) {
            sql.addString(DF_NAME_NUMERO_DDT, synch.getNumeroDDT());
            sql.addInt(DF_NAME_FLAG_ANNULLO, 0);
        }

        if(flgInsert)
            sql.addString(DF_NAME_ID_KIT, synch.getIdKit());
        else
            sql.setWhereCondition(DF_NAME_ID_KIT + "='" + synch.getIdKit() + "'");

        return sql;
    }

    private DbSqlInterface getSqlUpdate(Synch synch){
        DbSqlUpdate sql = new DbSqlUpdate(tableScambio);

        if(dbProperties.isSIX())
            sql.addInt(DF_NAME_FLAG_ANNULLO, 1);
        else
            sql.addString(DF_NAME_STATO, "A");

        sql.setWhereCondition(DF_NAME_ID_SPEDIZIONE + "=" + synch.getIdSpedizione()
                              + " AND " + DF_NAME_ID_KIT + "='" + synch.getIdKit() + "'");
        return sql;
    }

    private DbSqlInterface getSqlInsert(SynchTkit synch){
        DbSqlInsert sql = new DbSqlInsert(tableAnaKit);

        if(dbProperties.isDIGISTAT()) {
            sql.addString(DF_NAME_COD_KIT, synch.getCodiceKit());
            sql.addInt(DF_NAME_COD_KIT_NUM, synch.getCodiceKitNum());
        }
        else { // if(dbProperties.isSIX())
            sql.addString(DF_NAME_COD_KIT_ITN, synch.getCodiceKit());
            sql.addFunction(DF_NAME_TIMESTAMP_ITN, funct_timestamp);
        }
        sql.addString(DF_NAME_DESCR_KIT, synch.getDescKit());
        if(dbProperties.isORACLE())
            sql.addDateOracle(DF_NAME_DATA_REV, synch.getDataRev());
        else
            sql.addString(DF_NAME_DATA_REV, synch.getDataRev());
        sql.addInt(DF_NAME_TIPOLOGIA, synch.getTipo());

        return sql;
    }

    private DbSqlInterface getSqlUpdate(SynchTkit synch){
        DbSqlUpdate sql = new DbSqlUpdate(tableAnaKit);

        sql.addString(DF_NAME_DESCR_KIT, synch.getDescKit());

        if(dbProperties.isORACLE())
            sql.addDateOracle(DF_NAME_DATA_REV, synch.getDataRev());
        else
            sql.addString(DF_NAME_DATA_REV, synch.getDataRev());
        sql.addInt(DF_NAME_TIPOLOGIA, synch.getTipo());

        if(dbProperties.isDIGISTAT())
            sql.setWhereCondition(DF_NAME_COD_KIT_NUM + "=" + synch.getCodiceKitNum());
        else { // if(dbProperties.isSIX())
            sql.addFunction(DF_NAME_TIMESTAMP_ITN, funct_timestamp);
            sql.setWhereCondition(DF_NAME_COD_KIT_ITN + "='" + synch.getCodiceKit() + "'");
        }
        
        return sql;
    }

    private DbSqlInterface getSqlDelete(SynchTkit synch){
        DbSqlDelete sql = new DbSqlDelete(tableAnaKit);
        if(dbProperties.isDIGISTAT())
            sql.setWhereCondition(DF_NAME_COD_KIT_NUM + "=" + synch.getCodiceKitNum());
        else // if(dbProperties.isSIX())
            sql.setWhereCondition(DF_NAME_COD_KIT_ITN + "='" + synch.getCodiceKit() + "'");
        return sql;
    }

    private String getSqlList(String lastDateTime){
        String sql = "";

        if(dbProperties.isORACLE())
            //lastDateTime = "to_date(" + lastDateTime + ",'YYYYMMDD HH24MISS')";
            lastDateTime = "to_date(" + lastDateTime + ",'DD-MM-YYYY HH24:MI:SS')";

        if(dbProperties.isORMAWIN()) {
            sql = "SELECT " +  tableScambio + ".*," + viewSala + ".*," + viewSetFerri + "." + DF_FLD_VKIT_DATETIME +
                  " FROM " + tableScambio +
                  " LEFT JOIN " + viewSala + " ON " + DF_FLD_SCAMB_IDINT + "=" + DF_FLD_VSALA_IDINT +
                  " LEFT JOIN " + viewSetFerri + " ON " + DF_FLD_SCAMB_IDKIT + "=" + DF_FLD_VKIT_IDKIT +
                  " WHERE " + DF_NAME_LAST_UPD_ORMAWIN + " >=" + lastDateTime +
                  " ORDER BY " + DF_NAME_LAST_UPD_ORMAWIN;
        }        
        else if(dbProperties.isORMAWIN_V2()) {
            sql = "SELECT " +  tableScambio + ".*" +
                  " FROM " + tableScambio +
                  " WHERE " + DF_NAME_LAST_UPD_ORMAWIN + " >=" + lastDateTime +
                  " ORDER BY " + DF_NAME_LAST_UPD_ORMAWIN;
        }        
        else { // if(dbProperties.isDIGISTAT())
            sql = "SELECT *" +
                  " FROM " + tableScambio;

            if(dbProperties.isUseTimestamp_params()) {
                sql += " WHERE " + DF_NAME_LAST_UPD_EXTERN + " >=" + lastDateTime +
                       " ORDER BY " + DF_NAME_LAST_UPD_EXTERN;
            }
        }

        return sql;
    }

    private String getSqlSeekTK(SynchTkit synch){
        String sql = "";
            sql = "SELECT *" +
                  " FROM " + tableAnaKit;

            if(dbProperties.isDIGISTAT())
                sql += " WHERE " + DF_NAME_COD_KIT_NUM + " = " + Integer.toString(synch.getCodiceKitNum());
            else // if(dbProperties.isSIX())
                sql += " WHERE " + DF_NAME_COD_KIT_ITN + " = '" + synch.getCodiceKit() + "'";

        return sql;
    }

    private String getSqlSeek(String id_kit){
        String sql = "";
            sql = "SELECT *" +
                  " FROM " + tableScambio +
                  " WHERE " + DF_NAME_ID_KIT + "='" + id_kit + "'";
        return sql;
    }

    public boolean getNewConnection() {
        boolean ret = false;

        try	{
            if(connection != null)
                connection.close();

            //Class.forName(driver);
            //connection = DriverManager.getConnection("jdbc:jtds:sqlserver://10.0.0.35:1199/SQLEXPRESS;databaseName=JAVATEST", "root", "paprika");
            connection = DriverManager.getConnection(url, dbProperties.getMssqlUsername(), dbProperties.getMssqlPassword());
            log.info("Connected to mssql server, url=" + url);
            ret = true;

        } catch(java.sql.SQLException e) {
            log.info("Cannot connect to mssql server, url=" + url);
            log.error("*** Connection Error : " + e.getMessage() + " - " +
                                                  e.getSQLState() + " - " + e.getErrorCode());

        } /* catch(java.lang.ClassNotFoundException e) {
            log.info("Cannot connect to mssql server, url=" + url);
            log.error("*** Connection Driver Not Found Error : " + e.getMessage());
        } */
        
        return(ret);
    }

    public void shutdown() {
        if(connection != null) {
            try {
                connection.close();
                connection = null;
                log.info("Connection to mssql server closed");
            } catch(java.sql.SQLException e) {
                log.error("*** Close Error : " + e.getMessage() + " - " +
                                                 e.getSQLState() + " - " + e.getErrorCode());
                //e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        if(connection == null)
            getNewConnection();

        return(connection);
    }

    public boolean execSqlCommand(String sqlStatement, boolean checkRowAffected) {
        boolean ret = false;
        Statement st = null;
        int nRowsAffected;
        
        try {
            log.info("ExecSqlCommand SqlStatement=" + sqlStatement);

            st = getConnection().createStatement();
            nRowsAffected = st.executeUpdate(sqlStatement);
            
            ret = (nRowsAffected >= (checkRowAffected ? 1 : 0));

        } catch(java.sql.SQLException e) {
            //gestione dell'errore
            log.error("*** ExecSqlCommand Error : " + e.getMessage() + " - " + e.getSQLState() + " - " + e.getErrorCode());
            log.error("*** ExecSqlCommand SqlStatement=" + sqlStatement);
            log.error("*** End Of ExecSqlCommand Error!");
        } finally {
            try {
                st.close();
            } catch(java.sql.SQLException e) {}

        }

        return(ret);
    }

    public boolean execSqlQuery(String sqlQuery) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            log.info("ExecSqlQuery SqlQuery = " + sqlQuery);
            ps = getConnection().prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            elabResultSet(rs);

            return true;

        } catch(java.sql.SQLException e) {
            //gestione dell'errore
            log.error("*** ExecSqlQuery Error : " + e.getMessage() + " - " +
                                                    e.getSQLState() + " - " + e.getErrorCode());
            log.error("*** ExecSqlQuery SqlStatement=" + sqlQuery);
            log.error("*** End Of ExecSqlQuery Error!");
            log.error("*** Error:", e);
        } catch(java.lang.Exception e) {
            //gestione dell'errore
            log.error("*** ExecSqlQuery Error : " + e.getMessage());
            log.error("*** ExecSqlQuery SqlStatement=" + sqlQuery);
            log.error("*** End Of ExecSqlQuery Error!");
            log.error("*** Error:", e);
        } finally {
            try {
                rs.close();
                ps.close();
            } catch(java.sql.SQLException e) {}
        }

        return false;
    }

    private void elabResultSet(ResultSet rs) throws SQLException{
        vtSynch.clear();
        while(rs.next()) {
            if(vista == null)
                vtSynch.addElement(getItem(rs));
            else
                vista.getItem(rs);
        }
    }

    protected void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }
}
