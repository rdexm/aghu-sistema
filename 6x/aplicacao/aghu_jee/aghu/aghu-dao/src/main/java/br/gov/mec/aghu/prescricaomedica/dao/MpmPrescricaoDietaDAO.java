package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.RapServidores;

public class MpmPrescricaoDietaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmPrescricaoDieta> {

	private static final long serialVersionUID = 3608905332373149669L;

	@Override
	protected void obterValorSequencialId(MpmPrescricaoDieta elemento) {
		if (elemento.getId().getSeq() != null) {
			return;
		}
		Integer seq = this.obterValorSequencialId();
		// chave composta de atd seq e sequence MPM_PDT_SQ1
		elemento.getId().setSeq(seq.longValue());
	}

	public Integer obterValorSequencialId() {
		return this.getNextVal(SequenceID.MPM_PDT_SQ1);
	}

	/**
	 * Retorna instância desatachada do objeto com propriedades anteriores a
	 * alteração(old).<br>
	 * Não estão sendo carregadas todas as propriedades do objeto, verificar
	 * quando necessário.
	 * 
	 * @param dieta
	 * @return instância desatachada
	 */
	public MpmPrescricaoDieta obterOld(MpmPrescricaoDieta dieta) {
		if (dieta == null || dieta.getId() == null) {
			throw new IllegalArgumentException("Argumento obrigatório.");
		}

		MpmPrescricaoDietaId id = dieta.getId();
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);

		// projection
		ProjectionList projectionList = Projections
				.projectionList()
				//
				.add(
						Projections
								.property(MpmPrescricaoDieta.Fields.IND_PENDENTE
										.toString()),
						MpmPrescricaoDieta.Fields.IND_PENDENTE.toString())//
				.add(
						Projections
								.property(MpmPrescricaoDieta.Fields.DTHR_INICIO
										.toString()),
						MpmPrescricaoDieta.Fields.DTHR_INICIO.toString())//
				.add(
						Projections.property(MpmPrescricaoDieta.Fields.DTHR_FIM
								.toString()),
						MpmPrescricaoDieta.Fields.DTHR_FIM.toString());
		criteria.setProjection(projectionList);

		// restrictions
		criteria.add(Restrictions.idEq(id));

		criteria.setResultTransformer(Transformers
				.aliasToBean(MpmPrescricaoDieta.class));

		MpmPrescricaoDieta result = (MpmPrescricaoDieta) executeCriteriaUniqueResult(criteria);

		return result;
	}

	/**
	 * Retorna dietas válidas relacionada a uma prescricao medica.
	 */
	public List<MpmPrescricaoDieta> buscaDietaPorPrescricaoMedica(MpmPrescricaoMedicaId id, Date dthrFimPrescricao, Boolean listarTodas) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_MEDICA_ID.toString(),id));

		criteria.createAlias(MpmPrescricaoDieta.Fields.SERVIDOR_VALIDACAO.toString(), "SV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SV."+RapServidores.Fields.PESSOA_FISICA.toString(), "pf", JoinType.LEFT_OUTER_JOIN);
		
		if(!listarTodas) {
			criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoDieta.Fields.DTHR_FIM.toString()),
										 Restrictions.eq(MpmPrescricaoDieta.Fields.DTHR_FIM.toString(),dthrFimPrescricao)));
		}
		
		return executeCriteria(criteria);
	}

	/**
	 * Método que pesquisa a lista de dietas da prescrição mais recente
	 * 
	 * @param pmeAtdSeq
	 * @param dtFimLida
	 * @return
	 */
	public List<MpmPrescricaoDieta> pesquisarDietasUltimaPrescricao(
			Date dtFimLida, MpmPrescricaoMedica prescricaoMedicaAnterior) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);

		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), prescricaoMedicaAnterior.getId().getAtdSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.PME_SEQ
				.toString().toString(), prescricaoMedicaAnterior.getId()
				.getSeq()));
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.DTHR_FIM
				.toString(), prescricaoMedicaAnterior.getDthrFim()));
		criteria.addOrder(Order.asc(MpmPrescricaoDieta.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Obter dieta da prescrição pelo seu ID.
	 * 
	 * @param pmeAtdSeq
	 * @param seq
	 * @return
	 */
	public MpmPrescricaoDieta obterPrescricaoDietaPorId(Integer pmeAtdSeq,
			Long seq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), pmeAtdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.SEQ.toString(),
				seq));

		MpmPrescricaoDieta prescricaoDieta = (MpmPrescricaoDieta) executeCriteriaUniqueResult(criteria);
		return prescricaoDieta;
	}

	/**
	 * 
	 * @param atendimento
	 * @param dthrTrabalho
	 * @param dthrInicio
	 * @param dthrFim
	 * @param servidor
	 */
	public List<MpmPrescricaoDieta> listarPrescricaoDietaPendente(
			MpmPrescricaoMedica prescricao, Date dthrTrabalho) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);

		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), prescricao.getAtendimento().getSeq()));

		criteria.add(Restrictions.eq(
				MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_MEDICA.toString(),
				prescricao));

		criteria.add(Restrictions.or(Restrictions.ge(
				MpmPrescricaoDieta.Fields.CRIADO_EM.toString(), dthrTrabalho),
				Restrictions.ge(MpmPrescricaoDieta.Fields.ALTERADO_EM
						.toString(), dthrTrabalho)));

		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.IND_PENDENTE
				.toString(), DominioIndPendenteItemPrescricao.B));

		return executeCriteria(criteria);
	}

	/**
	 * Verificar se existe mais de uma prescrição de dieta que esteja
	 * recomendada alta para o mesmo atendimento
	 * 
	 * @return
	 */
	public boolean existePrescricaoDietaRecomendadaAltaPorAtendimento(
			MpmPrescricaoDieta prescricaoDieta) {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), prescricaoDieta.getPrescricaoMedica().getId()
				.getAtdSeq()));
		criteria.add(Restrictions.ne(
				MpmPrescricaoDieta.Fields.MPM_PRECRICAO_DIETA_ID.toString(),
				prescricaoDieta.getId()));
		criteria.add(Restrictions.eq(
				MpmPrescricaoDieta.Fields.IND_ITEM_RECOMENDADO_ALTA.toString(),
				true));

		return executeCriteriaCount(criteria) > 0;

	}

	public List<MpmPrescricaoDieta> buscarPrescricaoDietasPeloAtendimento(
			MpmPrescricaoDieta prescricaoDieta) {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), prescricaoDieta.getPrescricaoMedica().getId()
				.getAtdSeq()));
		criteria.add(Restrictions.ne(
				MpmPrescricaoDieta.Fields.MPM_PRECRICAO_DIETA_ID.toString(),
				prescricaoDieta.getId()));
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.IND_PENDENTE
				.toString(), DominioIndPendenteItemPrescricao.N));
		criteria.add(Restrictions.neProperty(
				MpmPrescricaoDieta.Fields.DTHR_INICIO.toString(),
				MpmPrescricaoDieta.Fields.DTHR_FIM.toString()));

		return executeCriteria(criteria);

	}

	public List<MpmPrescricaoDieta> buscarPrescricaoDietasSemItemRecomendaAltaPeloAtendimento(Integer seqAtendimento) {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), seqAtendimento));
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.IND_ITEM_RECOMENDADO_ALTA.toString(), true));

		return executeCriteria(criteria);
	}	
	
	/**
	 * Retornar a prescrição de dieta original
	 * 
	 * @param prescricaoDietaOrigem
	 * @return
	 */
	// public MpmPrescricaoDieta obterPrescricaoDietaOrigem(
	// MpmPrescricaoDieta prescricaoDietaOrigem) {
	//
	// // Obs.: Ao utilizar evict o objeto prescricaoProcedimento torna-se
	// // desatachado.
	// this.desatachar(prescricaoDietaOrigem);
	// return obterPorChavePrimaria(prescricaoDietaOrigem.getId());
	//
	// }

	/**
	 * Pesquisa as dietas para processar o cancelamento das mesmas
	 * 
	 * @param atdSeq
	 * @param pmeSeq
	 * @param dthrMovimento
	 * @return
	 */
	public List<MpmPrescricaoDieta> pesquisarPrescricaoDietasParaCancelar(
			Integer atdSeq, Integer pmeSeq, Date dthrMovimento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.PME_SEQ
				.toString(), pmeSeq));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.P);
		restricaoIn.add(DominioIndPendenteItemPrescricao.B);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.Y);
		restricaoIn.add(DominioIndPendenteItemPrescricao.R);

		criteria.add(Restrictions.in(MpmPrescricaoDieta.Fields.IND_PENDENTE
				.toString(), restricaoIn));

		Criterion criterionCriadoEmMaiorIgual = Restrictions.ge(
				MpmPrescricaoDieta.Fields.CRIADO_EM.toString(), dthrMovimento);
		Criterion criterionAlteradoEmMaiorIgual = Restrictions
				.ge(MpmPrescricaoDieta.Fields.ALTERADO_EM.toString(),
						dthrMovimento);

		criteria.add(Restrictions.or(criterionCriadoEmMaiorIgual,
				criterionAlteradoEmMaiorIgual));

		List<MpmPrescricaoDieta> retorno = executeCriteria(criteria);

		return retorno;
	}

	/**
	 * Método que pesquisa todas as dietas de uma prescrição médica
	 * 
	 * @param id
	 * @return
	 */
	public List<MpmPrescricaoDieta> pesquisarTodasDietasPrescricaoMedica(
			MpmPrescricaoMedicaId id) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_MEDICA_ID.toString(),
				id));

		List<MpmPrescricaoDieta> list = super.executeCriteria(criteria);

		return list;
	}

	/**
	 * Retorna a prescricao dieta posterior a fornecida.
	 * 
	 * @param dieta
	 * @return
	 */
	public MpmPrescricaoDieta obterPosterior(MpmPrescricaoDieta dieta) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(
				MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_DIETA.toString(),
				dieta));
		return (MpmPrescricaoDieta) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Método que pesquisa as dietas de uma prescrição médica filtrando pelo
	 * atendimento, data de início e de fim da prescrição.
	 * 
	 * @param pmeAtdSeq
	 * @param dataInicioPrescricao
	 * @param dataFimPrescricao
	 * @return
	 */
	public List<MpmPrescricaoDieta> obterPrescricoesDietaParaSumarioDieta(
			Integer pmeAtdSeq, Date dataInicioPrescricao, Date dataFimPrescricao) {
		List<MpmPrescricaoDieta> lista = null;
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);

		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), pmeAtdSeq));

		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);

		criteria.add(Restrictions.in(MpmPrescricaoDieta.Fields.IND_PENDENTE
				.toString(), restricaoIn));

		criteria.add(Restrictions.lt(MpmPrescricaoDieta.Fields.DTHR_INICIO.toString(), dataFimPrescricao));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoDieta.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoDieta.Fields.DTHR_FIM.toString(), dataInicioPrescricao)));

		criteria.add(Restrictions.isNotNull(MpmPrescricaoDieta.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_DIETA.toString()));
		
		lista = executeCriteria(criteria);

		return lista;
	}

	public List<MpmPrescricaoDieta> obterPrescricoesDietaPai(Long seq,
			Integer seqAtendimento, Date dataHoraInicio, Date dataHoraFim) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);
		
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_DIETA_SEQ
				.toString(), seq));
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_DIETA_ATD_SEQ
				.toString(), seqAtendimento));
		
		List<DominioIndPendenteItemPrescricao> restricaoIn = new ArrayList<DominioIndPendenteItemPrescricao>();
		restricaoIn.add(DominioIndPendenteItemPrescricao.N);
		restricaoIn.add(DominioIndPendenteItemPrescricao.A);
		restricaoIn.add(DominioIndPendenteItemPrescricao.E);
		
		criteria.add(Restrictions.in(MpmPrescricaoDieta.Fields.IND_PENDENTE
				.toString(), restricaoIn));
		
		criteria.add(Restrictions.lt(MpmPrescricaoDieta.Fields.DTHR_INICIO.toString(), dataHoraFim));
		
		criteria.add(Restrictions.or(Restrictions.isNull(MpmPrescricaoDieta.Fields.DTHR_FIM.toString()),
				Restrictions.gt(MpmPrescricaoDieta.Fields.DTHR_FIM.toString(), dataHoraInicio)));
		
		criteria.add(Restrictions.isNotNull(MpmPrescricaoDieta.Fields.SERVIDOR_VALIDACAO.toString()));

		criteria.addOrder(Order.asc(MpmPrescricaoDieta.Fields.MPM_PRESCRICAO_DIETA.toString()));
		
		return executeCriteria(criteria);
	}

	
	/**
	 * Obter dieta da prescrição pelo Atd seq
	 * 
	 * @param pmeAtdSeq
	 * @param seq
	 * @return
	 */
	public List<MpmPrescricaoDieta> obterPrescricaoDietaPorAtendimentoRecomendaAlta(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmPrescricaoDieta.class);
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.ATD_SEQ
				.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpmPrescricaoDieta.Fields.IND_ITEM_RECOMENDADO_ALTA.toString(), Boolean.TRUE));
		return  executeCriteria(criteria);
	}
}
