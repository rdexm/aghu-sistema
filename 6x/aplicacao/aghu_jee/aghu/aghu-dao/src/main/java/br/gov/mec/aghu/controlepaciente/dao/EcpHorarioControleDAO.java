package br.gov.mec.aghu.controlepaciente.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.RapServidores;
/**
 * 
 * @modulo controlepaciente
 *
 */
public class EcpHorarioControleDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EcpHorarioControle> {

	private static final long serialVersionUID = 5926804443314503073L;

	private static final Log LOG = LogFactory.getLog(EcpHorarioControleDAO.class);
	/**
	 * Retorna um horario a partir da unique key paciente+dataHora do controle
	 * 
	 * @param paciente
	 * @param dataHora
	 * @return horarioControle
	 */
	public EcpHorarioControle obterHorariopelaDataHora(AipPacientes paciente,
			Date dataHora) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpHorarioControle.class);
		criteria.add(Restrictions.eq(
				EcpHorarioControle.Fields.PACIENTE.toString(), paciente));
		criteria.add(Restrictions.eq(
				EcpHorarioControle.Fields.DATA_HORA.toString(), dataHora));
		return (EcpHorarioControle) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria buscarUltimaMedicaoUlceraPressaoPorAtendimento(
			Integer atdSeq, Short seqItemControleUP) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpHorarioControle.class);

		criteria.createAlias(
				EcpHorarioControle.Fields.CONTROLE_PACIENTE.toString(),
				EcpHorarioControle.Fields.CONTROLE_PACIENTE.toString());
		criteria.createAlias(
				EcpHorarioControle.Fields.CONTROLE_PACIENTE.toString() + "."
						+ EcpControlePaciente.Fields.ITEM.toString(),
				EcpControlePaciente.Fields.ITEM.toString());
		criteria.createAlias(EcpHorarioControle.Fields.ATENDIMENTO.toString(),
				EcpHorarioControle.Fields.ATENDIMENTO.toString());

		criteria.setProjection(Projections
				.max(EcpHorarioControle.Fields.DATA_HORA.toString()));

		criteria.add(Restrictions.eq(
				EcpHorarioControle.Fields.ATENDIMENTO.toString() + "."
						+ AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EcpControlePaciente.Fields.ITEM.toString()
				+ "." + EcpItemControle.Fields.SEQ.toString(),
				seqItemControleUP));

		return criteria;
	}

	public BigDecimal buscarMedicaoUlceraPressaoPorAtendimento(Integer atdSeq,
			Short seqItemControleUP) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpHorarioControle.class);

		criteria.createAlias(
				EcpHorarioControle.Fields.CONTROLE_PACIENTE.toString(),
				EcpHorarioControle.Fields.CONTROLE_PACIENTE.toString());
		criteria.createAlias(
				EcpHorarioControle.Fields.CONTROLE_PACIENTE.toString() + "."
						+ EcpControlePaciente.Fields.ITEM.toString(),
				EcpControlePaciente.Fields.ITEM.toString());
		criteria.createAlias(EcpHorarioControle.Fields.ATENDIMENTO.toString(),
				EcpHorarioControle.Fields.ATENDIMENTO.toString());

		criteria.setProjection(Projections
				.property(EcpHorarioControle.Fields.CONTROLE_PACIENTE
						.toString()
						+ "."
						+ EcpControlePaciente.Fields.MEDICAO.toString()));

		criteria.add(Restrictions.eq(
				EcpHorarioControle.Fields.ATENDIMENTO.toString() + "."
						+ AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(EcpControlePaciente.Fields.ITEM.toString()
				+ "." + EcpItemControle.Fields.SEQ.toString(),
				seqItemControleUP));

		criteria.add(Subqueries.propertyEq(EcpHorarioControle.Fields.DATA_HORA
				.toString(), this
				.buscarUltimaMedicaoUlceraPressaoPorAtendimento(atdSeq,
						seqItemControleUP)));

		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Retorna horários de controles do paciente no período
	 * 
	 * @param paciente
	 * @param dataHoraInicio
	 * @param dataHoraFim
	 * @return
	 */
	public List<EcpHorarioControle> listarHorarioControlePorPacientePeriodo(
			AipPacientes paciente, Date dataHoraInicio, Date dataHoraFim, boolean ascendente, Long trgSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(EcpHorarioControle.class);
		criteria.add(Restrictions.eq(
				EcpHorarioControle.Fields.PACIENTE.toString(), paciente));
		if(dataHoraInicio != null && dataHoraFim != null) {
			criteria.add(Restrictions.between(
					EcpHorarioControle.Fields.DATA_HORA.toString(), dataHoraInicio,
					dataHoraFim));
		}
		if(trgSeq != null) {
			criteria.add(Restrictions.eq(
					EcpHorarioControle.Fields.TRG_SEQ.toString(), trgSeq));
		}
		
		if(ascendente){
			criteria.addOrder(Order.asc(EcpHorarioControle.Fields.DATA_HORA
					.toString()));			
		}else{
			criteria.addOrder(Order.desc(EcpHorarioControle.Fields.DATA_HORA
					.toString()));			
		}

		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem os horários de controle de paciente através do código da consulta
	 * 
	 * #48659
	 * @param consultaCodigo Código da consulta
	 * @return
	 */
	public List<EcpHorarioControle> listarHorarioControlePorConsulta(Integer consultaCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(EcpHorarioControle.class, "hor");
		
		criteria.createAlias("hor."  + EcpHorarioControle.Fields.PACIENTE.toString(), "pac", JoinType.INNER_JOIN);
		criteria.createAlias("hor."  + EcpHorarioControle.Fields.CONTROLE_PACIENTE.toString(), "cont", JoinType.INNER_JOIN);
		criteria.createAlias("cont." + EcpControlePaciente.Fields.ITEM.toString(), "item", JoinType.INNER_JOIN);
		criteria.createAlias("hor."  + EcpHorarioControle.Fields.ATENDIMENTO.toString(), "atd", JoinType.INNER_JOIN);
		criteria.createAlias("atd."  + AghAtendimentos.Fields.CONSULTA.toString(), "cons", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("cons." + AacConsultas.Fields.NUMERO.toString(), consultaCodigo));
		
		criteria.addOrder(Order.asc("hor." + EcpHorarioControle.Fields.DATA_HORA.toString()));			

		return executeCriteria(criteria);
	}
	
	
	public List<EcpHorarioControle> listarHorarioControlePorSeqAtendimento(Integer seqAtendimento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpHorarioControle.class);
		criteria.add(Restrictions.eq(EcpHorarioControle.Fields.ATENDIMENTO.toString() + ".seq", seqAtendimento));
		
		return executeCriteria(criteria);
	}

	/**
	 * Rotina que grava contigência de internação e coloca na fila para geração de pdf
	 * chama package no banco oracle - André Luiz Machado - 11/01/2012
	 * @param horario
	 *  
	 */
	public void dispararGeracaoContigencia(final Integer seqAtendimento, final String usuarioLogado) throws ApplicationBusinessException {

		if (seqAtendimento != null) {
			if (!isOracle()) {
				return;
			}
			
			final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.MAMK_INT_PARADA_MAMP_INT_PARADA;
			
			AghWork work = new AghWork(usuarioLogado) {
				public void executeAghProcedure(final Connection connection) throws SQLException {
					
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall("{call " + nomeObjeto + "(?,?)}");
						CoreUtil.configurarParametroCallableStatement(cs, 1, Types.INTEGER, seqAtendimento);
						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, null);
						cs.execute();
					} finally {
						if (cs != null) {
							cs.close();
						}
					}
				}
			};
			
			try {
				this.doWork(work);
			} catch (final Exception e) {
				final String valores = CoreUtil.configurarValoresParametrosCallableStatement(seqAtendimento);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
			}
			
			if (work.getException() != null){
				final String valores = CoreUtil.configurarValoresParametrosCallableStatement(seqAtendimento);
				LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), true, valores), work.getException());
				throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
						CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, work.getException(), false, valores));
			}
		}
	}
	
	public List<EcpHorarioControle> pesquisarHorarioControlePorPacienteUsuario(
			 AipPacientes paciente, Date dataHora) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpHorarioControle.class);
		criteria.add(Restrictions.eq(EcpHorarioControle.Fields.PACIENTE.toString(), paciente));
		criteria.add(Restrictions.eq(EcpHorarioControle.Fields.DATA_HORA.toString(), dataHora));
		
		return executeCriteria(criteria);
		
	}
	
	public List<EcpHorarioControle> pesquisarHorarioControlePorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpHorarioControle.class);
		criteria.add(Restrictions.eq(EcpHorarioControle.Fields.PACIENTE_CODIGO.toString(), pacCodigo));
		
		return executeCriteria(criteria);
	}
	
	public String obterAnotacoesOriginal(Long seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpHorarioControle.class);
		criteria.setProjection(Projections.property(EcpHorarioControle.Fields.ANOTACOES.toString()));
		criteria.add(Restrictions.eq(EcpHorarioControle.Fields.SEQ.toString(), seq));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	public Object[] obterMatriculaVinculoServidorOriginal(Long seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpHorarioControle.class);
		criteria.createAlias(EcpHorarioControle.Fields.SERVIDOR.toString(),"servidor");
		criteria.setProjection(Projections
				.projectionList()
				.add(// 0
				Projections.property("servidor." + RapServidores.Fields.MATRICULA.toString()))
				.add(// 1
				Projections.property("servidor." + RapServidores.Fields.CODIGO_VINCULO.toString())));
		
		criteria.add(Restrictions.eq(EcpHorarioControle.Fields.SEQ.toString(), seq));
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}
	
	public List<EcpHorarioControle> listarHorarioControlePorTriagem(Long trgSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(EcpHorarioControle.class, "ECP");	
	
		criteria.add(Restrictions.eq("ECP." + EcpHorarioControle.Fields.TRG_SEQ.toString(), trgSeq));
		
		return (this.executeCriteria(criteria));
	}
	
}
