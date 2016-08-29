package br.gov.mec.aghu.paciente.business.validacaoprontuario;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;

/**
 * Classe responsável por mostrar a validação do prontuário
 * 
 * @author rafael
 *
 */
public class ValidaProntuario {

	private static final String PARAMETRO_DATABASE_URL = "database.url";
	private static final String PARAMETRO_DATABASE_USER = "database.user";
	private static final String PARAMETRO_DATABASE_PASS = "database.pass";
	private static final String PARAMETRO_DATABASE_DRIVER = "database.driver";
	private static final String PARAMETRO_SCRIPT_FILE = "script.file";

	private static String driverName = System.getProperty(
			PARAMETRO_DATABASE_DRIVER, "org.postgresql.Driver");
	private static String connectionURL = System.getProperty(
			PARAMETRO_DATABASE_URL,
			"jdbc:postgresql://mecsrv168:5432/dbaghu_tst");
	private static String userName = System.getProperty(
			PARAMETRO_DATABASE_USER, "sysdbaghu");
	private static String password = System.getProperty(
			PARAMETRO_DATABASE_PASS, "sysdbaghu");
	private static String scriptFile = System.getProperty(
			PARAMETRO_SCRIPT_FILE, "./update_prontuarios.sql");
	
	@Inject
	private ValidaProntuarioFactory validaProntuarioFactory;

	private static final Log LOG = LogFactory.getLog(ValidaProntuario.class);

	public static void main(String[] args) {
		try {
			new ValidaProntuario().validarProntuarios();
		} catch (IOException e) {
			LOG.error("Erro ao criar arquivo de update.", e);
		}
	}

	/**
	 * Método que imprime os prontuários inválidos conforme o cálculo do dígito
	 * verificador
	 * 
	 * @throws IOException
	 * 
	 * 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void validarProntuarios() throws IOException {
		String colProntuario = "prontuario";
		String colCodigo = "codigo";
		Connection jdbcConnection = null;
		File fScript = null;
		OutputStream osScript = null;
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			LOG.error(e.getMessage(), e);
		}

		try {
			jdbcConnection = DriverManager.getConnection(connectionURL,
					userName, password);
			Statement stm = jdbcConnection.createStatement();
			ResultSet rs = stm
					.executeQuery("select vlr_texto from agh.AGH_PARAMETROS where nome='"
							+ AghuParametrosEnum.P_AGHU_VALIDADOR_PRONTUARIO
							+ "'");
			String nomeValidador = null;
			if (rs.next()) {
				nomeValidador = rs.getString(1);
			}
			rs.close();
			LOG.info("Cálculo para o : " + nomeValidador);
			String sql = "select " + colCodigo + ", " + colProntuario
					+ " from agh.aip_pacientes";
			rs = stm.executeQuery(sql);

			LOG.info("Pront. \t\tPront. correto \t\tCód. Paciente");
			LOG.info("-----------------------------------------------------");
			// Imprime os prontuários inválidos e como seria o número correto de
			// cada prontuario
			InterfaceValidaProntuario validaProntuario = null;
			try {
				validaProntuario = validaProntuarioFactory
						.getValidaProntuario(true);
			} catch (ValidaProntuarioException e) {
				LOG.error(e.getMessage(), e);
			}

			if (!StringUtils.isBlank(scriptFile)) {
				fScript = new File(scriptFile);

				try {
					osScript = new FileOutputStream(fScript);
				} catch (FileNotFoundException e) {
					LOG.error("Erro: " + e.getMessage());
				}

				String strCodeControl = "UPDATE agh.aip_code_controls SET cc_next_value = (SELECT max(prontuario)+1 from agh.aip_pacientes)"
						+ " WHERE cc_domain = 'AIP_PAC_SQ2';\n";
				osScript.write(strCodeControl.getBytes());
			}

			while (rs.next()) {
				String prontuario = rs.getString(colProntuario);
				String codPaciente = rs.getString(colCodigo);
				if (StringUtils.isBlank(nomeValidador)) {
					break;
				} else if (StringUtils.isNotEmpty(prontuario)) {
					if (prontuario.length() < 2) {
						LOG.info(prontuario + "\t\t\tN/D" + "\t\t\t"
								+ codPaciente);
						continue;
					} else {
						Integer digito = validaProntuario.executaModulo(Integer
								.parseInt(prontuario.substring(0,
										prontuario.length() - 1)));
						if (Integer.parseInt(String.valueOf(prontuario
								.charAt(prontuario.length() - 1))) != digito) {
							LOG.info(prontuario
									+ "\t\t"
									+ prontuario.substring(0,
											prontuario.length() - 1) + digito
									+ "\t\t\t" + codPaciente);

							if (!StringUtils.isBlank(scriptFile)) {
								osScript.write(("BEGIN; UPDATE agh.aip_pacientes SET prontuario = "
										+ prontuario.substring(0,
												prontuario.length() - 1)
										+ digito
										+ " WHERE codigo = "
										+ codPaciente + "; COMMIT;\n")
										.getBytes());
							}
						}
					}
				}
			}
			LOG.info("Validação concluida.");
		} catch (SQLException se) {
			LOG.error(se.getMessage(), se);
		} finally {
			if (osScript != null) {
				osScript.flush();
				osScript.close();
			}
			try {
				jdbcConnection.close();
			} catch (SQLException onConClose) {
				LOG.error("Houve erro no fechamento da conexão");
				LOG.error(onConClose.getMessage(), onConClose);
			}
		}
	}
}
