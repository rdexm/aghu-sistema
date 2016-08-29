package br.gov.mec.aghu.bancosangue.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;

import br.gov.mec.aghu.bancosangue.vo.SolicitacaoHemoterapicaVO;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.dominio.DominioSolicitacaoHemoterapicaPendente;
import br.gov.mec.aghu.faturamento.vo.SolHemoVO;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AbsSolicitacoesHemoterapicasDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsSolicitacoesHemoterapicas> {

	private static final long serialVersionUID = 626407805629033959L;

	@Override
	protected void obterValorSequencialId(AbsSolicitacoesHemoterapicas elemento) {
		elemento.getId().setSeq(
				this.getNextVal(SequenceID.ABS_SHE_SQ1));
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public SolicitacaoHemoterapicaVO obtemSolicitacaoHemoterapicaVO(
			Integer atdSeq, Integer seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsSolicitacoesHemoterapicas.class);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.ATD_SEQ.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.SEQ
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.SEQ.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.DTHR_SOLICITACAO
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.DTHR_FIM
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.DTHR_FIM.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_PENDENTE
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_PAC_TRANSPLANTADO
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_PAC_TRANSPLANTADO
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_COLTERA_AMOSTRA
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_COLTERA_AMOSTRA
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_TRANSF_ANTERIORES
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_TRANSF_ANTERIORES
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.JUSTIFICATIVA
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.JUSTIFICATIVA
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_SITUACAO
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_SITUACAO
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.OBSERVACAO
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.OBSERVACAO.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_URGENTE
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_URGENTE.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_RESP_COLETA
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_RESP_COLETA
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_SIT_COLETA
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_SIT_COLETA
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.DTHR_SIT_COLETA
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.DTHR_SIT_COLETA
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.DTHR_CANC_COLETA
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.DTHR_CANC_COLETA
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.MOTIVO_CANC_COLETA_SEQ
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.MOTIVO_CANC_COLT_SEQ
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.SERVIDOR_CANC_COLETA_MAT
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.MAT_CANC_COLETA
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.SERVIDOR_CANC_COLETA_VIN
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.VIN_CANC_COLETA
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_SIT_ITEM
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_SIT_ITEM
								.toString())
				.add(Projections
						.property(AbsSolicitacoesHemoterapicas.Fields.IND_ALT_ANDAMENTO
								.toString()),
						SolicitacaoHemoterapicaVO.Fields.IND_ALT_ANDAMENTO
								.toString()));

		criteria.add(Restrictions.eq(
				AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(
				AbsSolicitacoesHemoterapicas.Fields.SEQ.toString(), seq));

		criteria.setResultTransformer(Transformers
				.aliasToBean(SolicitacaoHemoterapicaVO.class));

		return (SolicitacaoHemoterapicaVO) executeCriteriaUniqueResult(criteria);
	}

	public List<AbsSolicitacoesHemoterapicas> buscaSolicitacoesHemoterapicas(
			MpmPrescricaoMedicaId id, Boolean listarTodas) {

		/*
		 * select she.seq, she.ind_pac_transplantado, she.observacao,
		 * she.ind_transf_anteriores, she.ind_urgente from
		 * abs_solicitacoes_hemoterapicas she where she.pme_atd_seq = p_atd_seq
		 * and she.pme_seq = p_pme_seq and dthr_desativacao is null;
		 */
		List<AbsSolicitacoesHemoterapicas> list;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsSolicitacoesHemoterapicas.class);

		// she.pme_atd_seq = p_atd_seq
		if (id.getAtdSeq() != null) {
			criteria.add(Restrictions.eq(
					AbsSolicitacoesHemoterapicas.Fields.PME_ATD_SEQ.toString(),
					id.getAtdSeq()));
		}
		// and she.pme_seq = p_pme_seq
		if (id.getSeq() != null) {
			criteria.add(Restrictions.eq(
					AbsSolicitacoesHemoterapicas.Fields.PME_SEQ.toString(),
					id.getSeq()));
		}

		// and dthr_desativacao is null
		if (!listarTodas) {
			criteria.add(Restrictions
					.isNull(AbsSolicitacoesHemoterapicas.Fields.DTHR_FIM
							.toString()));
		}
		list = super.executeCriteria(criteria);

		return list;
	}

	/**
	 * Método que retorna as solicitacoes hemoterápicas de uma prescrição que
	 * torna-se-á pendente.
	 * 
	 * @param atendimento
	 * @param data
	 * @param dataFim
	 * @return
	 */
	public List<AbsSolicitacoesHemoterapicas> buscarSolicitacoesHemoterapicasPendentes(
			MpmPrescricaoMedica prescricao, Date data) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsSolicitacoesHemoterapicas.class);

		criteria.add(Restrictions.eq(
				AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString(),
				prescricao.getAtendimento().getSeq()));

		criteria.add(Restrictions.eq(
				AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(),
				DominioSolicitacaoHemoterapicaPendente.P));

		criteria.add(Restrictions
				.isNull(AbsSolicitacoesHemoterapicas.Fields.JUSTIFICATIVA
						.toString()));

		criteria.add(Restrictions.ge(
				AbsSolicitacoesHemoterapicas.Fields.DT_HR_CRIADO_EM.toString(),
				data));

		criteria.add(Restrictions.lt(
				AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
						.toString(), prescricao.getDthrFim()));

		criteria.add(Restrictions.or(
				Restrictions
						.isNull(AbsSolicitacoesHemoterapicas.Fields.DTHR_FIM
								.toString()),
				Restrictions.le(
						AbsSolicitacoesHemoterapicas.Fields.DTHR_FIM.toString(),
						prescricao.getDthrInicio())));

		return super.executeCriteria(criteria);
	}

	/**
	 * Pesquisa as hemoterapias para processar o cancelamento das mesmas.
	 * 
	 * @param atdSeq
	 * @param pmeSeq
	 * @param dthrMovimento
	 * @return
	 */
	public List<AbsSolicitacoesHemoterapicas> pesquisarHemoterapiasParaCancelar(
			Integer atdSeq, Integer pmeSeq, Date dthrMovimento) {
		MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
		id.setAtdSeq(atdSeq);
		id.setSeq(pmeSeq);

		DetachedCriteria criteria = obterCriteriaHemoterapiasPorPrescricao(id);

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.B);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.Y);
		restricaoIn.add(DominioIndPendenteItemPrescricao.R);

		criteria.add(Restrictions.in(
				AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(),
				restricaoIn));

		Criterion criterionCriadoEmMaiorIgual = Restrictions.ge(
				AbsSolicitacoesHemoterapicas.Fields.CRIADO_EM.toString(),
				dthrMovimento);
		Criterion criterionAlteradoEmMaiorIgual = Restrictions.ge(
				AbsSolicitacoesHemoterapicas.Fields.ALTERADO_EM.toString(),
				dthrMovimento);

		criteria.add(Restrictions.or(criterionCriadoEmMaiorIgual,
				criterionAlteradoEmMaiorIgual));

		List<AbsSolicitacoesHemoterapicas> retorno = executeCriteria(criteria);

		return retorno;
	}

	/**
	 * Método que pesquisa todas as hemoterapias de uma prescrição médica
	 * 
	 * @param id
	 * @return
	 */
	public List<AbsSolicitacoesHemoterapicas> pesquisarTodasHemoterapiasPrescricaoMedica(MpmPrescricaoMedicaId id) {

		DetachedCriteria criteria = obterCriteriaHemoterapiasPorPrescricao(id);
		criteria.setFetchMode(AbsSolicitacoesHemoterapicas.Fields.ITENS_SOLICITACOES_HEMOTERAPICAS.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AbsSolicitacoesHemoterapicas.Fields.ITENS_SOLICITACOES_HEMOTERAPICAS+"."+AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS, FetchMode.JOIN);
		criteria.add(Restrictions.isNull(AbsSolicitacoesHemoterapicas.Fields.DTHR_FIM.toString()));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		List<AbsSolicitacoesHemoterapicas> list = super.executeCriteria(criteria);

		return list;
	}

	/**
	 * Retorna criteria que pesquisa hemoterapias por prescrição médica
	 * 
	 * @param atdSeq
	 * @param pmeSeq
	 * @return
	 */
	private DetachedCriteria obterCriteriaHemoterapiasPorPrescricao(MpmPrescricaoMedicaId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesHemoterapicas.class);
		criteria.add(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.PRESCRICAO_MEDICA_ID.toString(), id));

		return criteria;
	}

	/**
	 * Pesquisa as hemoterapias que deverão constar no relatório
	 * 
	 * @param prescricaoMedica
	 * @return
	 */
	public List<AbsSolicitacoesHemoterapicas> pesquisarSolicitacoesHemoterapicasRelatorio(
			MpmPrescricaoMedica prescricaoMedica) {
		DetachedCriteria criteria = obterCriteriaHemoterapiasPorPrescricao(prescricaoMedica
				.getId());

		criteria.add(Restrictions.eq(
				AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.N));
		criteria.add(Restrictions.lt(
				AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
						.toString(), prescricaoMedica.getDthrFim()));
		criteria.add(Restrictions.ge(
				AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
						.toString(), prescricaoMedica.getDthrInicio()));

		return executeCriteria(criteria);
	}

	/**
	 * Método que pesquisa as solicitações hemoterápicas filtrando pelo
	 * atendimento, data de início e de fim da prescrição.
	 * 
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<AbsSolicitacoesHemoterapicas> obterSolicitacoesHemoterapicasParaSumarioPrescricao(
			Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao) {
		List<AbsSolicitacoesHemoterapicas> lista = null;
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsSolicitacoesHemoterapicas.class);

		criteria.add(Restrictions.eq(
				AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString(),
				pmeAtdSeq));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(
				AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(),
				restricaoIn));

		criteria.add(Restrictions.ge(
				AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
						.toString(), dataInicioPrescricao));
		criteria.add(Restrictions.lt(
				AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
						.toString(), dataFimPrescricao));

		criteria.add(Restrictions
				.isNotNull(AbsSolicitacoesHemoterapicas.Fields.SERVIDOR_VALIDACAO
						.toString()));

		criteria.addOrder(Order
				.asc(AbsSolicitacoesHemoterapicas.Fields.SOLICITACAO_HEMOTERAPICA
						.toString()));

		lista = executeCriteria(criteria);

		return lista;

	}

	public AbsSolicitacoesHemoterapicas obterSolicitacaoPorSeqESitColeta(Integer seq, DominioSituacaoColeta sitColeta){

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesHemoterapicas.class);
		criteria.add(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.IND_SIT_COLETA.toString(), sitColeta));

		return (AbsSolicitacoesHemoterapicas) executeCriteriaUniqueResult(criteria);
	}

	public List<AbsSolicitacoesHemoterapicas> obterPrescricoesHemoterapicaPai(
			Integer seq, Integer seqAtendimento, Date dataHoraInicio,
			Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsSolicitacoesHemoterapicas.class);

		criteria.add(Restrictions
				.eq(AbsSolicitacoesHemoterapicas.Fields.SOLICITACAO_HEMOTERAPICA_SEQ
						.toString(), seq));
		criteria.add(Restrictions
				.eq(AbsSolicitacoesHemoterapicas.Fields.SOLICITACAO_HEMOTERAPICA_ATD_SEQ
						.toString(), seqAtendimento));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(
				AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(),
				restricaoIn));

		criteria.add(Restrictions.ge(
				AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
						.toString(), dataHoraInicio));
		criteria.add(Restrictions.lt(
				AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
						.toString(), dataHoraFim));

		criteria.add(Restrictions
				.isNotNull(AbsSolicitacoesHemoterapicas.Fields.SERVIDOR_VALIDACAO
						.toString()));

		criteria.addOrder(Order
				.asc(AbsSolicitacoesHemoterapicas.Fields.SOLICITACAO_HEMOTERAPICA
						.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Retorna todas as solicitações hemoterápicas relacionadas a um atendimento.
	 * @param atendimento
	 * @return
	 */
	public List<AbsSolicitacoesHemoterapicas> buscarSolicitacoesHemoterapicasPorAtendimento(
			AghAtendimentos atendimento) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsSolicitacoesHemoterapicas.class);
		
		if (atendimento != null) {
			criteria.add(Restrictions.eq(
					AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString(),
					atendimento.getSeq()));
		}
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retorna todas as solicitações hemoterápicas relacionadas a um atendimento.
	 * @param atendimento
	 * @return
	 */
	public List<AbsSolicitacoesHemoterapicas> buscarSolicitacoesHemoterapicasEmColeta(AghAtendimentos atendimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesHemoterapicas.class);
		List<DominioSituacaoColeta> restricaoIn = new ArrayList<DominioSituacaoColeta>();
		restricaoIn.add(DominioSituacaoColeta.P);
		restricaoIn.add(DominioSituacaoColeta.E);
		
		if (atendimento != null) {
			
			criteria.add(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString(), atendimento.getSeq()));
			criteria.add(Restrictions.in(AbsSolicitacoesHemoterapicas.Fields.IND_SIT_COLETA.toString(), restricaoIn));

		}
		
		return this.executeCriteria(criteria);
	}

	public AbsSolicitacoesHemoterapicas obterPorChavePrimariaComItensSolicitacoes(AbsSolicitacoesHemoterapicasId absSolicitacoesHemoterapicasId) {
		AbsSolicitacoesHemoterapicas solicitacoesHemoterapicas = 
				this.obterPorChavePrimaria(absSolicitacoesHemoterapicasId);
		
		this.initialize(solicitacoesHemoterapicas);
		this.initialize(solicitacoesHemoterapicas.getPrescricaoMedica());
		this.initialize(solicitacoesHemoterapicas.getPrescricaoMedica().getAtendimento());
		this.initialize(solicitacoesHemoterapicas.getItensSolHemoterapicas());
		for (AbsItensSolHemoterapicas itensSolHemoterapicas : solicitacoesHemoterapicas.getItensSolHemoterapicas()) {
			this.initialize(itensSolHemoterapicas.getItemSolicitacaoHemoterapicaJustificativas());
			this.initialize(itensSolHemoterapicas.getProcedHemoterapico());
			this.initialize(itensSolHemoterapicas.getComponenteSanguineo());
			itensSolHemoterapicas.getDescricaoFormatada();
		}
		
		return solicitacoesHemoterapicas;
	}

	
	public List<AbsSolicitacoesHemoterapicas> buscarSolicitacoesPorAtendimentoPeriodo(Integer atdSeq, Date dtHrWork, Date dtHrInicio, 
			Date dtHrFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesHemoterapicas.class);
		criteria.add(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.or(Restrictions.and(Restrictions.sqlRestriction("TO_DATE({alias}.CRIADO_EM, 'DD/MM/YYYY HH24:MI:SS') >= TO_DATE('"+ DateUtil.dataToString(dtHrWork, "dd/MM/yyyy HH:mm:ss") +"', 'DD/MM/YYYY HH24:MI:SS')"), 
				Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(), DominioIndPendenteItemPrescricao.P)), 
				Restrictions.and(Restrictions.sqlRestriction("TO_DATE({alias}.ALTERADO_EM, 'DD/MM/YYYY HH24:MI:SS') >= TO_DATE('"+ DateUtil.dataToString(dtHrWork, "dd/MM/yyyy HH:mm:ss") +"', 'DD/MM/YYYY HH24:MI:SS')"), 
				Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(), DominioIndPendenteItemPrescricao.E))));
		criteria.add(Restrictions.sqlRestriction("TO_DATE({alias}.DTHR_SOLICITACAO, 'DD/MM/YYYY HH24:MI:SS') < TO_DATE('"+ DateUtil.dataToString(dtHrFim, "dd/MM/yyyy HH:mm:ss") +"', 'DD/MM/YYYY HH24:MI:SS')"));
		criteria.add(Restrictions.or(Restrictions.sqlRestriction("TO_DATE({alias}.DTHR_DESATIVACAO, 'DD/MM/YYYY HH24:MI:SS')  <= TO_DATE('"+ DateUtil.dataToString(dtHrFim, "dd/MM/yyyy HH:mm:ss") +"', 'DD/MM/YYYY HH24:MI:SS')"), 
				Restrictions.isNull(AbsSolicitacoesHemoterapicas.Fields.DTHR_FIM.toString())));
		return executeCriteria(criteria);
	}
	
	public List<SolHemoVO> buscarSolicitacoesSeqPorAtendimentoPeriodo(Integer atdSeq, Date dtHrWork, Date dtHrInicio, 
			Date dtHrFim) {
	StringBuilder sql = new StringBuilder(4000);
	
	sql.append(" SELECT  \n")
	.append("  this_.SEQ                            AS seq \n")
	.append(" FROM AGH.ABS_SOLICITACOES_HEMOTERAPICAS this_ \n")
	.append(" WHERE this_.ATD_SEQ          ="+atdSeq.toString()+" \n")
	.append(" AND ((this_.CRIADO_EM       >= TO_TIMESTAMP('"+DateUtil.dataToString(dtHrWork, "dd/MM/yyyy HH:mm:ss")+"', 'DD/MM/YYYY HH24:MI:SS') \n")
	.append(" AND this_.IND_PENDENTE       ='P') \n")
	.append(" OR (this_.ALTERADO_EM       >= TO_TIMESTAMP('"+DateUtil.dataToString(dtHrWork, "dd/MM/yyyy HH:mm:ss")+"', 'DD/MM/YYYY HH24:MI:SS') \n")
	.append(" AND this_.IND_PENDENTE       ='E')) \n")
	.append(" AND this_.DTHR_SOLICITACAO   < TO_TIMESTAMP('"+DateUtil.dataToString(dtHrFim, "dd/MM/yyyy HH:mm:ss")+"', 'DD/MM/YYYY HH24:MI:SS') \n")
	.append(" AND (this_.DTHR_DESATIVACAO <= TO_TIMESTAMP('"+DateUtil.dataToString(dtHrFim, "dd/MM/yyyy HH:mm:ss")+"', 'DD/MM/YYYY HH24:MI:SS') \n")
	.append(" OR this_.DTHR_DESATIVACAO   IS NULL)");
	
	SQLQuery query = createSQLQuery(sql.toString());
	
	//query.setString("situacaoProcedimento", DominioSituacao.A.toString());
	//query.setString("tipoAIH5", DominioSimNao.S.toString());

	
	List<SolHemoVO> listaVO = query.
		addScalar("seq",IntegerType.INSTANCE).
		setResultTransformer(Transformers.aliasToBean(SolHemoVO.class)).list();
	

	return listaVO;
	
	}
	
	public List<AbsSolicitacoesHemoterapicas> buscarSolicitacoesConfirmarHemoterapia(AbsSolicitacoesHemoterapicas solicitacaoDependente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsSolicitacoesHemoterapicas.class);
		criteria.add(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.SOLICITACAO_HEMOTERAPICA.toString(), solicitacaoDependente));
		criteria.add(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(), DominioIndPendenteItemPrescricao.A));
		criteria.add(Restrictions.or(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.IND_SIT_COLETA.toString(), DominioSituacaoColeta.P), 
				Restrictions.and(Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.IND_SIT_COLETA.toString(), DominioSituacaoColeta.E), 
						Restrictions.eq(AbsSolicitacoesHemoterapicas.Fields.IND_RESP_COLETA.toString(), DominioResponsavelColeta.S))));
		return executeCriteria(criteria);
	}
}
