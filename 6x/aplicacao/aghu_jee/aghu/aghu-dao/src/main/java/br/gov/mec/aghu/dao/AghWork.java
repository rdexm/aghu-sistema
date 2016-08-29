package br.gov.mec.aghu.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.jdbc.Work;


/**
 * Classe utilizada para executar procedures do AGH, com isso utilizando usuário
 * de banco correto
 * 
 * @author lcmoura
 * 
 */
public abstract class AghWork implements Work {
	
	private String user;

	private Exception exception;

	public Exception getException() {
		return exception;
	}

	private final String objetoOracle = EsquemasOracleEnum.AGH + "."
			+ ObjetosBancoOracleEnum.PUT_USUARIO_AGHU;

	private final String initCallOracle = EsquemasOracleEnum.AGH + "."
			+ ObjetosBancoOracleEnum.INICIAR_CHAMADA_AGHU;

	private final String closeCallOracle = EsquemasOracleEnum.AGH + "."
			+ ObjetosBancoOracleEnum.ENCERRAR_CHAMADA_AGHU;

	public AghWork(final String user) {
		this.user = user;
	}

	public abstract void executeAghProcedure(final Connection connection)
			throws SQLException;

	@Override
	public final void execute(final Connection connection) throws SQLException {
		CallableStatement csInit = null;
		CallableStatement cs = null;
		try {
			csInit = connection.prepareCall("{call " + initCallOracle + "()}");
			csInit.execute();

			StringBuffer sbCall = new StringBuffer("{call ").append(
					objetoOracle).append("(?)}");
			cs = connection.prepareCall(sbCall.toString());
			cs.setString(1, user != null ? user.toUpperCase() : null);
			cs.execute();

			this.executeAghProcedure(connection);

		} catch (Exception e) {
			exception = e;

		} finally {

			CallableStatement csClose = null;
			try {
				csClose = connection.prepareCall("{call " + closeCallOracle
						+ "()}");
				csClose.execute();
			} catch (Exception e) {
				if (exception == null) {
					exception = e;
				}
			} finally {

				if (csClose != null) {
					csClose.close();
				}
				if (cs != null) {
					cs.close();
				}
				if (csInit != null) {
					csInit.close();
				}

			}
		}
	}

}
