package br.gov.mec.aghu.paciente.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.paciente.vo.VAipSolicitantesVO;

public class AipSolicitantesProntuarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipSolicitantesProntuario> {

	private static final long serialVersionUID = 9009242227737850003L;

	public List<AipSolicitantesProntuario> pesquisarSolicitantesProntuarioPorUnidadeFuncional(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipSolicitantesProntuario.class);
		criteria.add(Restrictions.eq(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS_SEQ.toString(), unfSeq));
		
		return executeCriteria(criteria);
	}

	/**
	 * @dbtables AipSolicitantesProntuario select
	 * 
	 * @param seq
	 * @param indSituacao
	 * @param unidadeFuncional
	 * @param origemEvento
	 * @param finalidadeMovimentacao
	 * @param descricao
	 * @param exigeResponsavel
	 * @param mensagemSamis
	 * @param separacaoPrevia
	 * @param retorno
	 * @param volumesManuseados
	 * @return
	 */
	public Long obterSolicitanteProntuarioCount(Integer seq,
			DominioSituacao indSituacao,
			AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento,
			AipFinalidadesMovimentacao finalidadeMovimentacao,
			String descricao, Boolean exigeResponsavel,
			DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia,
			DominioSimNao retorno, DominioTodosUltimo volumesManuseados) {

		return executeCriteriaCount(criarCriteriaAipSolicitanteProntuario(seq,
				indSituacao, unidadeFuncional, origemEvento,
				finalidadeMovimentacao, descricao, exigeResponsavel,
				mensagemSamis, separacaoPrevia, retorno, volumesManuseados));
	}
	
	/**
	 * @dbtables  AipSolicitantesProntuario select
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param seq
	 * @param indSituacao
	 * @param unidadeFuncional
	 * @param origemEvento
	 * @param finalidadeMovimentacao
	 * @param descricao
	 * @param exigeResponsavel
	 * @param mensagemSamis
	 * @param separacaoPrevia
	 * @param retorno
	 * @param volumesManuseados
	 * @return
	 */
	public List<AipSolicitantesProntuario> pesquisarSolicitanteProntuario(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer seq, DominioSituacao indSituacao,
			AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento,
			AipFinalidadesMovimentacao finalidadeMovimentacao,
			String descricao, Boolean exigeResponsavel,
			DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia,
			DominioSimNao retorno, DominioTodosUltimo volumesManuseados) {

		DetachedCriteria criteria = criarCriteriaAipSolicitanteProntuario(seq,
				indSituacao, unidadeFuncional, origemEvento,
				finalidadeMovimentacao, descricao, exigeResponsavel,
				mensagemSamis, separacaoPrevia, retorno, volumesManuseados);

		return executeCriteria(criteria, firstResult, maxResults, orderProperty,
				asc);
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	private DetachedCriteria criarCriteriaAipSolicitanteProntuario(Integer seq,
			DominioSituacao indSituacao,
			AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento,
			AipFinalidadesMovimentacao finalidadeMovimentacao,
			String descricao, Boolean exigeResponsavel,
			DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia,
			DominioSimNao retorno, DominioTodosUltimo volumesManuseados) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipSolicitantesProntuario.class);
		
		criteria.setFetchMode(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS+"."+AghUnidadesFuncionais.Fields.ALA, FetchMode.JOIN);
		criteria.setFetchMode(AipSolicitantesProntuario.Fields.ORIGEM_EVENTOS.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AipSolicitantesProntuario.Fields.FINALIDADES_MOVIMENTACAO.toString(), FetchMode.JOIN);
		
		

		if (seq != null) {
			criteria.add(Restrictions.eq(AipSolicitantesProntuario.Fields.SEQ
					.toString(), seq.shortValue()));
		}

		if (indSituacao != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.IND_SITUACAO.toString(),
					indSituacao));
		}

		if (unidadeFuncional != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS
							.toString(), unidadeFuncional));
		}

		if (origemEvento != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.ORIGEM_EVENTOS.toString(),
					origemEvento));
		}

		if (finalidadeMovimentacao != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.FINALIDADES_MOVIMENTACAO
							.toString(), finalidadeMovimentacao));
		}

		if (descricao != null && !"".equals(descricao)) {
			criteria.add(Restrictions.ilike(
					AipSolicitantesProntuario.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		if (exigeResponsavel != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.EXIGE_RESPONSAVEL
							.toString(), exigeResponsavel));
		}

		if (mensagemSamis != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.MENSAGEM_SAMIS.toString(),
					mensagemSamis));
		}

		if (separacaoPrevia != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.SEPARACAO_PREVIA
							.toString(), separacaoPrevia));
		}

		if (retorno != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.RETORNO.toString(),
					retorno));
		}

		if (volumesManuseados != null) {
			criteria.add(Restrictions.eq(
					AipSolicitantesProntuario.Fields.VOLUMES_MANUSEADOS
							.toString(), volumesManuseados));
		}

		return criteria;
	}

	public List<Object[]> listarViewSolicitantesProntuario(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				AipSolicitantesProntuario.class, "sop");

		criteria.createAlias("origemEventos", "origemEventos",
				Criteria.LEFT_JOIN);
		criteria.createAlias("unidadesFuncionais", "unidadesFuncionais",
				Criteria.LEFT_JOIN);

		if (seq != null) {
			criteria.add(Restrictions.eq(AipSolicitantesProntuario.Fields.SEQ
					.toString(), seq));
		}

		criteria
				.setProjection(Projections
						.projectionList()
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.SEQ
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.ORIGEM_EVENTOS
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.DESCRICAO
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS_ANDAR
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS_DESCRICAO
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS_IND_ALA
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.ORIGEM_EVENTOS_DESCRICAO
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.SEPARACAO_PREVIA
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.VOLUMES_MANUSEADOS
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.IND_SITUACAO
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.FINALIDADES_MOVIMENTACAO
												.toString()))
						.add(
								Projections
										.property(AipSolicitantesProntuario.Fields.MENSAGEM_SAMIS
												.toString())));

		return executeCriteria(criteria);
	}

	public AipMovimentacaoProntuarios obterMovimentacaoPorSolicitacaoPaciente(Integer codigoSolicitacaoProntuario,
			Integer codigoPaciente) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		cri.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.SLP_CODIGO.toString(), codigoSolicitacaoProntuario));
		cri.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), codigoPaciente));

		return (AipMovimentacaoProntuarios) executeCriteriaUniqueResult(cri);
	}

	public List<Object[]> obterMovimentacoes(Date dtInicial, Date dtFinal, DominioSituacaoMovimentoProntuario csSituacao,
			Boolean csExibirArea, VAipSolicitantesVO vAipSolicitantes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class);
		criteria.createAlias("paciente", "paciente");

		criteria.add(Restrictions.ge(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString(), dtInicial));

		Date dtFinalAux = new Date();
		if (dtFinal != null) {
			dtFinalAux = dtFinal;
		}
		dtFinalAux = DateUtils.truncate(dtFinalAux, Calendar.DAY_OF_MONTH);
		dtFinalAux = DateUtils.addDays(dtFinalAux, 1);
		criteria.add(Restrictions.lt(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString(), dtFinalAux));

		boolean between = true;

		Short lo = 0; // A partir
		Short hi = 0; // Até

		if (csExibirArea != null && csExibirArea) {
			if (vAipSolicitantes != null) {
				lo = hi = vAipSolicitantes.getCodigo().shortValue();
			} else {
				between = false;
			}
		} else {
			lo = 0;
			hi = 999;
		}

		if (between) {
			criteria.add(Restrictions.between(AipMovimentacaoProntuarios.Fields.SLC_SEQ.toString(), lo, hi));
		}

		if (csSituacao != null) {
			criteria.add(Restrictions.eq(AipMovimentacaoProntuarios.Fields.SITUACAO.toString(), csSituacao));
		}

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.SLC_SEQ.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.SLC_SEQ.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.PAC_PRONTUARIO.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.PAC_NOME.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.PAC_LEITO_ID.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.DATA_RETIRADA.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.DATA_DEVOLUCAO.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.DT_CADASTRO_ORIGEM_PRONTUARIO.toString()))
				.add(Projections.property(AipMovimentacaoProntuarios.Fields.VOLUMES.toString())));

		criteria.addOrder(Order.asc(AipMovimentacaoProntuarios.Fields.SLC_SEQ.toString()));
		criteria.addOrder(Order.asc(AipMovimentacaoProntuarios.Fields.PAC_PRONTUARIO.toString()));

		return executeCriteria(criteria);
	}
	
	public Long countSolicitantesPorUnidadeFuncional(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipSolicitantesProntuario.class);

		criteria.add(Restrictions.eq(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS_SEQ.toString(), unfSeq));

		return executeCriteriaCount(criteria);
	}
	
	public List<AipSolicitantesProntuario> pesquisarUnidadeSolicitantePorCodigoESigla(Short unfSeq, String unfSigla){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipSolicitantesProntuario.class);
		criteria.createAlias(AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS.toString(), "UN");
		if (unfSeq != null) {
			criteria.add(Restrictions.eq("UN." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq.shortValue()));
		}
		if (StringUtils.isNotBlank(unfSigla)) {
			criteria.add(Restrictions.ilike("UN." + AghUnidadesFuncionais.Fields.SIGLA.toString(), unfSigla, MatchMode.ANYWHERE));
		}
		return executeCriteria(criteria);
	}	
}

