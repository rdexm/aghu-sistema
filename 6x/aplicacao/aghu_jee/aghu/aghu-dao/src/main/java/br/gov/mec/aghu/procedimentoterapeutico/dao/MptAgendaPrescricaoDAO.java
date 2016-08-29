package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioEstornoConsulta;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class MptAgendaPrescricaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptAgendaPrescricao> {

	private static final long serialVersionUID = -8567496229093433158L;
	
	private static final Log LOG = LogFactory.getLog(MptAgendaPrescricaoDAO.class);

	public List<MptAgendaPrescricao> listarAgendasPrescricaoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendaPrescricao.class);

		criteria.add(Restrictions.eq(MptAgendaPrescricao.Fields.CON_NUMERO.toString(), numeroConsulta));

		return executeCriteria(criteria);
	}
	
	/**Atualiza MptAgendaPrescricao no oracle executando a procedure mbck_crg_rn2.rn_crgp_canc_quimio com parametro cirurgia_seq
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarAgendaPrescricaoPorCirurgiaCallableStatement(final Integer cirurgiaSeq) throws ApplicationBusinessException{
		if (isOracle()) {			
		
			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.MBCK_CRG_RN2_RN_CRGP_CANC_QUIMIO;
			try {
				this.doWork(new Work() {
					public void execute(Connection connection) throws SQLException {
						CallableStatement cs = null;
						try {
							cs = connection.prepareCall("{call " + nomeObjeto + "(?)}");
	
							CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, cirurgiaSeq == null ? null : cirurgiaSeq);
	
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
				String valores = CoreUtil.configurarValoresParametrosCallableStatement(cirurgiaSeq == null ? null : cirurgiaSeq);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}		
		}
	}
	
	//c_agp
	public MptAgendaPrescricao obterDataAgendaPrescricaoAtiva(Integer crgSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendaPrescricao.class);

		criteria.add(Restrictions.eq(MptAgendaPrescricao.Fields.CRQ_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MptAgendaPrescricao.Fields.UNF_SEQ_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq(MptAgendaPrescricao.Fields.IND_SITUACAO.toString(), DominioEstornoConsulta.A));
		criteria.add(Restrictions.eq(MptAgendaPrescricao.Fields.IND_ESTORNO_CONSULTA.toString(), DominioEstornoConsulta.N));

		List<MptAgendaPrescricao> result = executeCriteria(criteria);
		
		if (!result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}

}