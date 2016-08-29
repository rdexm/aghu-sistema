package br.gov.mec.aghu.blococirurgico.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jdbc.Work;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.model.AgfaAdt;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class AgfaAdtDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AgfaAdt> {

	private static final long serialVersionUID = 4887810614297979877L;
	
	private static final Log LOG = LogFactory.getLog(AgfaAdtDAO.class);

	/**
	 * Alterar informações do paciente no sistema de imagens agfa
	 * @param pacCodigoNew
	 * @param pacCodigoOld
	 * @param cirSeq
	 * @throws ApplicationBusinessException
	 */
	public void atualizarInformacoesPacienteAGFACallableStatement(final Integer pacCodigoOld, final Integer pacCodigoNew, final Integer cirSeq) throws ApplicationBusinessException{
		if (isOracle()) {			
		
			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.RN_CRGP_ATU_AGFA;
			try {
				this.doWork(new Work() {
					public void execute(Connection connection) throws SQLException {
						CallableStatement cs = null;
						try {
							cs = connection.prepareCall("{call " + nomeObjeto + "(?,?,?)}");
	
							CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, pacCodigoOld == null ? null : pacCodigoOld);
							CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, pacCodigoNew == null ? null : pacCodigoNew);
							CoreUtil.configurarParametroCallableStatement(cs, 3, Types.INTEGER, cirSeq == null ? null : cirSeq);
	
							// Registro de parametro OUT
							//cs.registerOutParameter(1, Types.VARCHAR);
							cs.execute();
							//String retorno = cs.getString(1);
			
						} finally {
							if(cs != null){
								cs.close();
							}
						}
					}
				});
			} catch (Exception e) {
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(pacCodigoOld == null ? null : pacCodigoOld, pacCodigoNew == null ? null : pacCodigoNew, cirSeq == null ? null : cirSeq);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}		
		}
	}

}