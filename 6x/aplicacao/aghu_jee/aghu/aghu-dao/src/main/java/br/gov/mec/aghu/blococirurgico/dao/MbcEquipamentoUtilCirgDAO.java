package br.gov.mec.aghu.blococirurgico.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioNotasDeConsumoDaSalaEquipamentosVO;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;




public class MbcEquipamentoUtilCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcEquipamentoUtilCirg> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -178187363613347451L;
	
	private static final Log LOG = LogFactory.getLog(MbcEquipamentoUtilCirgDAO.class);

	public enum TipoAcao{
		I,
		A,
		E;
	}
	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = 
			DetachedCriteria.forClass(MbcEquipamentoUtilCirg.class);
		return criteria;
	}
	
	
	public List<MbcEquipamentoUtilCirg> listarEquipamentoUtilCirurgico(
			MbcEquipamentoCirurgico equipamentoCirurgico) {
		DetachedCriteria criteria = this.obterCriteria();
		
		criteria.add(Restrictions.eq(MbcEquipamentoUtilCirg
				.Fields.MBC_EQUIPAMENTO_CIRURGICO.toString(), 
					equipamentoCirurgico));
		
		return this.executeCriteria(criteria);
	}
	
	
	public List<MbcEquipamentoUtilCirg> pesquisarMbcEquipamentoUtilCirgPorCirurgia(Integer crgSeq, Short euuSeq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcEquipamentoUtilCirg.class, "EQC");
		
		criteria.createAlias("EQC.".concat(MbcEquipamentoUtilCirg.Fields.MBC_EQUIPAMENTO_CIRURGICO.toString()), "EUU", DetachedCriteria.INNER_JOIN);
		criteria.createAlias("EQC.".concat(MbcEquipamentoUtilCirg.Fields.MBC_CIRURGIAS.toString()), "CRG", DetachedCriteria.INNER_JOIN);

		criteria.add(Restrictions.eq("CRG.".concat(MbcCirurgias.Fields.SEQ.toString()), crgSeq));
		if(euuSeq != null){
			criteria.add(Restrictions.eq("EUU.".concat(MbcCirurgias.Fields.SEQ.toString()), euuSeq));
		}
		
		return this.executeCriteria(criteria);
	}
	
	public void executarFfcInterfaceEqp(final MbcEquipamentoUtilCirg equipamento, final TipoAcao tipoAcao) throws ApplicationBusinessException{
		if (isOracle()) {
			final String storedProcedureName = ObjetosBancoOracleEnum.FFC_INTERFACE_EQP.toString();
			AghWork work = new AghWork(equipamento.getRapServidores() != null ? equipamento.getRapServidores().getUsuario() : null) {
					@Override
					public void executeAghProcedure(final Connection connection) throws SQLException {
						CallableStatement statement = null;
						try {
							final StringBuilder call = new StringBuilder("{call ").append(storedProcedureName).append("(?, ?, ?, ?)}");
							statement = connection.prepareCall(call.toString());
							CoreUtil.configurarParametroCallableStatement(statement, 1, Types.INTEGER, equipamento.getId().getCrgSeq());
							CoreUtil.configurarParametroCallableStatement(statement, 2, Types.INTEGER, equipamento.getId().getEuuSeq());
							CoreUtil.configurarParametroCallableStatement(statement, 3, Types.INTEGER, equipamento.getQuantidade());
							CoreUtil.configurarParametroCallableStatement(statement, 4, Types.VARCHAR, tipoAcao.toString());
							statement.execute();
						} finally {
							if (statement != null) {
								statement.close();
							}
						}

					}
				};
			try {
				this.doWork(work);
			} catch (final Exception e) {
				throwExceptionStoredProcedure(
						storedProcedureName, 
						e, 
						equipamento.getId().getCrgSeq(), 
						equipamento.getId().getEuuSeq(), 
						equipamento.getQuantidade(), 
						"I");
			}
			
			if (work.getException() != null){
				throwExceptionStoredProcedure(
						storedProcedureName, 
						work.getException(), 
						equipamento.getId().getCrgSeq(), 
						equipamento.getId().getEuuSeq(), 
						equipamento.getQuantidade(), 
						"I");
			}
		}
	}
	
	/**
	 * Trata exceptions de chamadas a stored procedures.
	 * 
	 * @param storedProcedureName
	 *            nome da stored procedure
	 * @param e
	 *            exception
	 * @param parametros
	 *            parâmetros fornecidos na chamada
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("deprecation")
	private void throwExceptionStoredProcedure(
			final String storedProcedureName, final Exception e,
			final Object... parametros) throws ApplicationBusinessException {
		final String string = CoreUtil
				.configurarValoresParametrosCallableStatement(parametros);
		LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(
				storedProcedureName, e, true, string));
		throw new ApplicationBusinessException(
				AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
				storedProcedureName, string, CoreUtil
						.configurarMensagemUsuarioLogCallableStatement(
								storedProcedureName, e, false, string));
	}
	
	public Long countEquipamentoUtilCirgPorCrgSeq(Integer crgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcEquipamentoUtilCirg.class, "EQC");
		criteria.add(Restrictions.eq("EQC." + MbcEquipamentoUtilCirg.Fields.MBC_CIRURGIAS_SEQ.toString(), crgSeq));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> obterEquipamentosPorUnfSeqCrgSeqUnion2(Integer crgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcEquipamentoUtilCirg.class, "EQC");
		criteria.createAlias("EQC.".concat(MbcEquipamentoUtilCirg.Fields.MBC_EQUIPAMENTO_CIRURGICO.toString()), "EUU");
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("   RPAD(SUBSTR(euu1_.DESCRICAO,1,15),15,'.') || ': S'   "
			).append( SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.DESCRICAO_EQUIPAMENTO.toString());
		StringBuilder sqlProjection1 = new StringBuilder(50);
		sqlProjection1.append("  CAST(1 AS SMALLINT)   "
			).append( SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.ORDEM.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.DESCRICAO_EQUIPAMENTO.toString()},
						new Type[] { StringType.INSTANCE }))
				.add(Projections.sqlProjection(sqlProjection1.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.ORDEM.toString()},
						new Type[] { ShortType.INSTANCE })));
		
		criteria.add(Restrictions.eq("EQC." + MbcEquipamentoUtilCirg.Fields.MBC_CIRURGIAS_SEQ.toString(), crgSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> obterEquipamentosUtilizadosPorCrgSeq(Integer crgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcEquipamentoUtilCirg.class, "EPN");
		criteria.createAlias("EPN.".concat(MbcEquipamentoUtilCirg.Fields.MBC_EQUIPAMENTO_CIRURGICO.toString()), "EUU");
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("   RPAD(SUBSTR(euu1_.DESCRICAO,1,15),15,'.') || ': ' || {alias}.quantidade   "
			).append( SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.DESCRICAO_EQUIPAMENTO.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.DESCRICAO_EQUIPAMENTO.toString()},
						new Type[] { StringType.INSTANCE })));
		
		criteria.add(Restrictions.eq("EPN." + MbcEquipamentoUtilCirg.Fields.MBC_CIRURGIAS_SEQ.toString(), crgSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.class));
		
		return executeCriteria(criteria);
	}
}
