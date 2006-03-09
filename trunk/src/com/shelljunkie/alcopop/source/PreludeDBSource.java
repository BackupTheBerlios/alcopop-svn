/**
 Copyright (c) 2005,2006 Juergen Becker
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright 
 notice, this list of conditions and the following disclaimer in
 the documentation and/or other materials provided with the distribution.

 3. The names of the authors may not be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
 INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.shelljunkie.alcopop.source;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.shelljunkie.alcopop.alert.Alert;
import com.shelljunkie.alcopop.alert.IAlert;
import com.shelljunkie.alcopop.logging.ILogger;
import com.shelljunkie.alcopop.logging.LoggerManager;
import com.shelljunkie.alcopop.pipeline.IPipelineElementConfiguration;
import com.shelljunkie.alcopop.pipeline.ISource;
import com.shelljunkie.alcopop.pipeline.PipelineElementConfigurationException;

/**
 * Class for reading Prelude v0.8 IDMEF alerts form a mysql db.
 * 
 * @author Juergen Becker
 */
public class PreludeDBSource implements ISource {
	private static final String PRELUDE_08_MYSQL_SQLQUERY = "SELECT a.alert_ident AS alertid, a.ntpstamp AS detecttime, cl.name AS attacktype, "
		+ "s.address AS sourceip, t.address AS targetip, serv.protocol AS protocol, serv.port AS targetport, analyzer.analyzerid AS analyzerid "
		+ "FROM Prelude_DetectTime AS a, Prelude_Classification AS cl, Prelude_Address AS s, Prelude_Address AS t, Prelude_Service AS serv, "
		+ "Prelude_Analyzer AS analyzer WHERE a.alert_ident=cl.alert_ident AND a.alert_ident = s.alert_ident AND a.alert_ident = t.alert_ident "
		+ "AND a.alert_ident = serv.alert_ident AND a.alert_ident = analyzer.parent_ident AND s.parent_type = 'S' AND t.parent_type='T' AND serv.parent_type='T' ";
	private static final String PRELUDE_08_MYSQL_SQLQUERY_EXT = "ORDER BY detecttime LIMIT ";
	private static final int NO_OF_RESULTS_PER_QUERY = 5000;

	private static final String CFG_PROPERTY_DBHOST = "DB Host";
	private static final String CFG_PROPERTY_DBPORT = "DB Port";
	private static final String CFG_PROPERTY_DBNAME = "DB Name";
	private static final String CFG_PROPERTY_DBUSER = "DB User";
	private static final String CFG_PROPERTY_DBPASSWD = "DB Password";
	private static final String CFG_PROPERTY_NO_OF_RESULTS = "No of results";
	private static final String CFG_PROPERTY_OFFSET = "Offset";

	private Connection dbConnection;
	private ResultSet result;
	private int results;
	private int maxNoOfResults;
	private int offset;
	private Statement stm;
	private ILogger logger;
	private boolean running;

	public boolean init( IPipelineElementConfiguration configuration ) {
		logger = LoggerManager.getInstance().getLogger( getClass() );
		try {
			Class.forName( "com.mysql.jdbc.Driver" ).newInstance();
		} catch ( Exception e ) {
			logger.error( "MySQL JDBC Driver not found. Check your classpath!", e );
			return false;
		}

		maxNoOfResults = configuration.getIntConfigurationProperty( CFG_PROPERTY_NO_OF_RESULTS, Integer.MAX_VALUE );
		results = 0;
		offset = configuration.getIntConfigurationProperty( CFG_PROPERTY_OFFSET, 0 );

		String dbhost = null;
		String dbport = null;
		String dbname = null;
		String dbuser = null;
		String dbpasswd;
		try {
			dbhost = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_DBHOST );
			dbport = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_DBPORT );
			dbname = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_DBNAME );
			dbuser = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_DBUSER );
			dbpasswd = configuration.getConfigurationPropertyChecked( CFG_PROPERTY_DBPASSWD );
		} catch ( PipelineElementConfigurationException excep ) {
			logger.error( excep.getMessage() );
			return false;
		}

		try {
			String conStr = "jdbc:mysql://" + dbhost + ":" + dbport + "/" + dbname + "?user=" + dbuser + "&password=" + dbpasswd;
			dbConnection = DriverManager.getConnection( conStr );
			stm = dbConnection.createStatement();
			running = true;
			return true;
		} catch ( SQLException excep ) {
			logger.error( "Error opening db connection: ", excep );
		}
		return false;
	}

	public Object produce() {
		try {
			if ( result == null ) {
				result = getResult();
				if ( result == null ) {
					return null;
				}
			}
			IAlert alert = null;
			if ( result.next() ) {
				alert = createAlertFromRow( result );
				++results;
				return alert;
			}
			result = null;
		} catch ( Exception e ) {
			logger.error( "creating of alert failed: ", e );
			stop();
		}
		if ( results >= maxNoOfResults ) {
			stop();
		}
		return null;
	}

	protected ResultSet getResult() throws SQLException {
		System.out.println( "next result. offset: " + ( offset + results ) );
		Statement stm = getStatement();
		if ( stm != null ) {
			return stm.executeQuery( PRELUDE_08_MYSQL_SQLQUERY + PRELUDE_08_MYSQL_SQLQUERY_EXT + String.valueOf( NO_OF_RESULTS_PER_QUERY ) + " OFFSET "
				+ String.valueOf( offset + results ) );
		}
		return null;
	}

	public void stop() {
		running = false;
		try {
			if ( dbConnection != null ) {
				dbConnection.close();
				dbConnection = null;
				stm = null;
			}
		} catch ( SQLException excep ) {
			// ok
		}
		logger.info( "stoped" );
	}

	public boolean isRunning() {
		return running;
	}

	protected Statement getStatement() {
		return stm;
	}

	protected IAlert createAlertFromRow( ResultSet result ) {
		if ( result == null ) {
			return null;
		}
		Alert alert = new Alert();
		try {
			alert.setID( result.getLong( "alertid" ) );
			alert.setAnalyzerID( result.getLong( "analyzerid" ) );
			alert.setStartTimeFromNTPHexString( result.getString( "detecttime" ) );
			alert.setName( result.getString( "attacktype" ) );
			alert.setSourceIP( result.getString( "sourceip" ) );
			alert.setDestinationIP( result.getString( "targetip" ) );
			alert.setProtocol( result.getString( "protocol" ) );
			alert.setDestinationPort( result.getInt( "targetport" ) );
		} catch ( Exception e ) {
			logger.error( "Error creating Alert from db row data: ", e );
			return null;
		}

		return alert;
	}

}
