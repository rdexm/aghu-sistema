package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioIndRespAvaliacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmJustificativaUsoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicamentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.LocalDispensa2VO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoMdtoVO;

public class MpmItemPrescricaoMdtoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmItemPrescricaoMdto> {

	private static final long serialVersionUID = -3711357884056305397L;

	private static final String IME = "IME";
	private static final String IME_DOT = "IME.";
	private static final String JUM = "JUM";
	private static final String JUM_DOT = "JUM.";
	private static final String PMD = "PMD";
	private static final String PMD_DOT = "PMD.";
	private static final String TFQ = "TFQ";
	private static final String MED = "MED";
	private static final String MED_DOT = "MED.";
	private static final String UMM = "UMM";
	private static final String FDS = "FDS";
	private static final String FDS_DOT = "FDS.";
	private static final String UMM2 = "UMM2";
	private static final String TUM = "TUM";
	private static final String TUM_DOT = "TUM.";
	private static final String GUP = "GUP";
	private static final String GUP_DOT = "GUP.";
	private static final String IPR = "IPR";

	@Override
	protected void obterValorSequencialId(MpmItemPrescricaoMdto elemento) {
		if (elemento != null && elemento.getId() != null) {
			MpmItemPrescricaoMdtoId id = elemento.getId();
			if (id.getPmdAtdSeq() != null && id.getPmdSeq() != null
					&& id.getMedMatCodigo() != null) {
				elemento.getId().setSeqp(
						buscaProximoSeqP(id.getPmdAtdSeq(), id.getPmdSeq(), id
								.getMedMatCodigo()));
			}
		}
	}

	private Short buscaProximoSeqP(Integer pmdAtdSeq, Long pmdSeq,
			Integer medMatCodigo) {
		Short seq = 1;

		StringBuffer hql = new StringBuffer(170);

		hql.append(" select max(ipm.id.seqp) ");
		hql.append(" from MpmItemPrescricaoMdto ipm ");
		hql.append(" where ipm.id.pmdAtdSeq = :pmdAtdSeq ");
		hql.append(" 	and ipm.id.pmdSeq = :pmdSeq ");
		hql.append(" 	and ipm.id.medMatCodigo = :medMatCodigo ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("pmdAtdSeq", pmdAtdSeq);
		query.setParameter("pmdSeq", pmdSeq);
		query.setParameter("medMatCodigo", medMatCodigo);

		Short retValue = (Short) query.uniqueResult();

		if (retValue != null) {
			seq = (short) (retValue + 1);
		}

		return seq;
	}

	/**
	 * Retorna a lista de itens de uma prescrição de medicamentos
	 * 
	 * @param pmdAtdSeq
	 * @param pmdSeq
	 * @param medMatCodigo
	 * @param seqp
	 * @return lista de itens de uma prescrição de medicamentos
	 */
	public List<MpmItemPrescricaoMdto> pesquisarItensPrescricaoMdtos(
			Integer pmdAtdSeq, Long pmdSeq, Integer medMatCodigo, Short seqp) {
		DetachedCriteria criteria = montaCriteriaPesquisarItensPrescricaoMdtos(
				pmdAtdSeq, pmdSeq, medMatCodigo, seqp);
		return executeCriteria(criteria);
	}

	public Long pesquisarItensPrescricaoMdtosCount(Integer pmdAtdSeq,
			Long pmdSeq, Integer medMatCodigo, Short seqp) {
		DetachedCriteria criteria = montaCriteriaPesquisarItensPrescricaoMdtos(
				pmdAtdSeq, pmdSeq, medMatCodigo, seqp);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria montaCriteriaPesquisarItensPrescricaoMdtos(
			Integer pmdAtdSeq, Long pmdSeq, Integer medMatCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmItemPrescricaoMdto.class);

		if (pmdAtdSeq != null) {
			criteria.add(Restrictions.eq(
					MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(),
					pmdAtdSeq));
		}

		if (pmdSeq != null) {
			criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.PMD_SEQ
					.toString(), pmdSeq));
		}

		if (medMatCodigo != null) {
			criteria.add(Restrictions.eq(
					MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString(),
					medMatCodigo));
		}

		if (seqp != null) {
			criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.SEQP
					.toString(), seqp));
		}
		
		return criteria;
	}

	public MpmItemPrescricaoMdto obtemItemPrescricaoMedicamentoSemDuracaoTrat(Integer codigoMedicamento,
			Integer atdSeqPrescricaoMedicamento, Long seqPrescricaoMedicamento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class);


		criteria.createAlias(MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), "PM", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(), atdSeqPrescricaoMedicamento));
		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.PMD_SEQ.toString(), seqPrescricaoMedicamento));
		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.MEDICAMENTO_CODIGO.toString(), codigoMedicamento));
		criteria.add(Restrictions.isNull("PM."+MpmPrescricaoMdto.Fields.DURACAO_TRAT_SOL.toString()));
		
		List<MpmItemPrescricaoMdto> lista = executeCriteria(criteria);
		
		if(lista != null && lista.size() > 0) {
			for(MpmItemPrescricaoMdto obj : lista){
				return obj;
			}
		}

		return null;
	}
		
	public ItemPrescricaoMedicamentoVO obterItemPrescricaoMdtoVO(
			Integer pmdAtdSeq, Long pmdSeq, Integer medMatCodigo, Short seqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class);

		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString()),ItemPrescricaoMedicamentoVO.Fields.PMD_ATD_SEQ.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.PMD_SEQ.toString()),ItemPrescricaoMedicamentoVO.Fields.PMD_SEQ.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.SEQP.toString()),ItemPrescricaoMedicamentoVO.Fields.SEQP.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.FDSSEQ.toString()),ItemPrescricaoMedicamentoVO.Fields.FDS_SEQ.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString()),ItemPrescricaoMedicamentoVO.Fields.MAT_CODIGO.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.USO_MDTO_ANTIMAC.toString()),ItemPrescricaoMedicamentoVO.Fields.USO_MDTO_ANTIMICROBIA.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.DURACAO_TRAT_APRV.toString()),ItemPrescricaoMedicamentoVO.Fields.DURACAOTRATAPROVADO.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.QTDEMGKG.toString()),ItemPrescricaoMedicamentoVO.Fields.QTDEMGKG.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.QTDEMG_CORP.toString()),ItemPrescricaoMedicamentoVO.Fields.QTDEMGSUPERFCORPORAL.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.OBSERVACAO.toString()),ItemPrescricaoMedicamentoVO.Fields.OBSERVACAO.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.DOSE.toString()),ItemPrescricaoMedicamentoVO.Fields.DOSE.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString()),ItemPrescricaoMedicamentoVO.Fields.ORIGEMJUSTIFICATIVA.toString())
					);

		
		criteria.add(Restrictions.eq(
				MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(),
				pmdAtdSeq));

		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.PMD_SEQ
				.toString(), pmdSeq));

		criteria.add(Restrictions.eq(
				MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString(),
				medMatCodigo));

		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.SEQP
				.toString(), seqp));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItemPrescricaoMedicamentoVO.class));
		
		return (ItemPrescricaoMedicamentoVO)executeCriteriaUniqueResult(criteria);
	}

	public List<ItemPrescricaoMedicamentoVO> obterListaItemPrescricaoMdtoVO(
			Integer pmdAtdSeq, Long pmdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class);

		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString()),ItemPrescricaoMedicamentoVO.Fields.PMD_ATD_SEQ.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.PMD_SEQ.toString()),ItemPrescricaoMedicamentoVO.Fields.PMD_SEQ.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.SEQP.toString()),ItemPrescricaoMedicamentoVO.Fields.SEQP.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.FDSSEQ.toString()),ItemPrescricaoMedicamentoVO.Fields.FDS_SEQ.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString()),ItemPrescricaoMedicamentoVO.Fields.MAT_CODIGO.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.USO_MDTO_ANTIMAC.toString()),ItemPrescricaoMedicamentoVO.Fields.USO_MDTO_ANTIMICROBIA.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.DURACAO_TRAT_APRV.toString()),ItemPrescricaoMedicamentoVO.Fields.DURACAOTRATAPROVADO.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.QTDEMGKG.toString()),ItemPrescricaoMedicamentoVO.Fields.QTDEMGKG.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.QTDEMG_CORP.toString()),ItemPrescricaoMedicamentoVO.Fields.QTDEMGSUPERFCORPORAL.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.OBSERVACAO.toString()),ItemPrescricaoMedicamentoVO.Fields.OBSERVACAO.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.DOSE.toString()),ItemPrescricaoMedicamentoVO.Fields.DOSE.toString())
						.add(Projections.property(MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString()),ItemPrescricaoMedicamentoVO.Fields.ORIGEMJUSTIFICATIVA.toString())
					);

		
		criteria.add(Restrictions.eq(
				MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(),
				pmdAtdSeq));

		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.PMD_SEQ
				.toString(), pmdSeq));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemPrescricaoMedicamentoVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * Metodo para listar itens prescricao medicamento, filtrando pelo atdSeq da PrescricaoMedicamento, e pelo seq da PrescricaoMedicamento
	 * @param atdSeqPrescricaoMedicamento
	 * @param seqPrescricaoMedicamento
	 * @return List<MpmItemPrescricaoMdto>
	 */
	public List<MpmItemPrescricaoMdto> listarItensPrescricaoMedicamentoPeloSeqEAtdSeq(Integer atdSeqPrescricaoMedicamento, Long seqPrescricaoMedicamento) {
		List<MpmItemPrescricaoMdto> lista = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class);
		
		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(), atdSeqPrescricaoMedicamento));
		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.PMD_SEQ.toString(), seqPrescricaoMedicamento));
		
		lista = executeCriteria(criteria);
		
		return lista;
		
	}	
	
	/** 5388 - Movimentacao Triagem e Dispensacao Medicamentos
	 * Metodo para listar itens prescricao medicamento, filtrando pelo atdSeq da PrescricaoMedicamento, e pelo seq da PrescricaoMedicamento e
	 * por Data de Inicio e Fim e por ind_Pendente (‘N’,’A’,’E’)
	 * @param atdSeqPrescricaoMedicamento
	 * @param seqPrescricaoMedicamento
	 * @return List<MpmItemPrescricaoMdto>
	 */
	public List<MpmItemPrescricaoMdto> pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendente(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmPrescricaoMedica prescricaoMedica, AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = obterCriteriaItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendente(prescricaoMedica, medicamento);
		criteria.createAlias(MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), "prescMdto");
		//criteria.addOrder(Order.desc(MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.desc("prescMdto." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString()));
		
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendenteCount(MpmPrescricaoMedica prescricaoMedica, AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = obterCriteriaItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendente(prescricaoMedica, medicamento);
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendente(MpmPrescricaoMedica prescricaoMedica, AfaMedicamento medicamento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class,"t1");
		
		criteria.createAlias(MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), "mdto", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mdto."+AfaMedicamento.Fields.TPR.toString(), "tpr", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(),prescricaoMedica.getId().getAtdSeq()));
				
		//subquery 1
		//--------------------------------------------------------------------------------------------------------
		DetachedCriteria criteriaPrescricaoMdtoAtdSeq = DetachedCriteria.forClass(MpmPrescricaoMdto.class,"t2");
		
		criteriaPrescricaoMdtoAtdSeq.setProjection(Projections
				.projectionList().add(Projections.property(MpmPrescricaoMdto.Fields.ATD_SEQ.toString())));
		
		criteriaPrescricaoMdtoAtdSeq.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), prescricaoMedica.getId().getAtdSeq()));
		
		criteriaPrescricaoMdtoAtdSeq.add(Restrictions.lt(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), prescricaoMedica.getDthrFim()));

		Criterion c1 = Restrictions.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString());
		
		Criterion c2 = Restrictions.gt(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(),prescricaoMedica.getDthrInicio());
		
		Criterion c3 = Restrictions.and(Restrictions.eqProperty(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), 
					   MpmPrescricaoMdto.Fields.DTHR_FIM.toString()), Restrictions.ge(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), prescricaoMedica.getDthrInicio()));
		
		criteriaPrescricaoMdtoAtdSeq.add( Restrictions.disjunction().add(c1).add(c2).add(c3));
		
		criteriaPrescricaoMdtoAtdSeq.add(Restrictions.in(MpmPrescricaoMdto.Fields.INDPENDENTE.toString(), 
					new DominioIndPendenteItemPrescricao[] {DominioIndPendenteItemPrescricao.N,DominioIndPendenteItemPrescricao.A,DominioIndPendenteItemPrescricao.E}));
		//--------------------------------------------------------------------------------------------------------

		//subquery 2
		//--------------------------------------------------------------------------------------------------------
		DetachedCriteria criteriaPrescricaoMdtoSeq = DetachedCriteria.forClass(MpmPrescricaoMdto.class,"t3");
		
		criteriaPrescricaoMdtoSeq.setProjection(Projections
				.projectionList().add(Projections.property(MpmPrescricaoMdto.Fields.SEQ.toString())));
		
		criteriaPrescricaoMdtoSeq.add(Restrictions.eq(MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), prescricaoMedica.getId().getAtdSeq()));
		
		criteriaPrescricaoMdtoSeq.add(Restrictions.lt(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), prescricaoMedica.getDthrFim()));

		Criterion c4 = Restrictions.isNull(MpmPrescricaoMdto.Fields.DTHR_FIM.toString());
		
		Criterion c5 = Restrictions.gt(MpmPrescricaoMdto.Fields.DTHR_FIM.toString(),prescricaoMedica.getDthrInicio());
		
		Criterion c6 = Restrictions.and(Restrictions.eqProperty(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), 
					   	MpmPrescricaoMdto.Fields.DTHR_FIM.toString()), Restrictions.ge(MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), 
					   	prescricaoMedica.getDthrInicio()));
		
		criteriaPrescricaoMdtoSeq.add( Restrictions.disjunction().add(c4).add(c5).add(c6));
		
		criteriaPrescricaoMdtoSeq.add(Restrictions.in(MpmPrescricaoMdto.Fields.INDPENDENTE.toString(), new DominioIndPendenteItemPrescricao[] 
		                {DominioIndPendenteItemPrescricao.N,DominioIndPendenteItemPrescricao.A,DominioIndPendenteItemPrescricao.E}));

		//--------------------------------------------------------------------------------------------------------
		
		criteria.add(Property.forName(MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString()).in(criteriaPrescricaoMdtoAtdSeq));
		
		criteria.add(Property.forName(MpmItemPrescricaoMdto.Fields.PMD_SEQ.toString()).in(criteriaPrescricaoMdtoSeq));
		
		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), medicamento));
		
		return criteria;
		
	}
	// fim 5388

	/**
	 * 
	 * @param medMatCodigo
	 * @return
	 */
	public List<MpmItemPrescricaoMdto> obterListaItemPrescricaoParaMdto(Integer medMatCodigo) {
		
		List<MpmItemPrescricaoMdto> result = null;
		DetachedCriteria criteria = null;
		
		if (medMatCodigo == null) {
			throw new IllegalArgumentException();
		}
		criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class);
		criteria.add(Restrictions.eq(
				MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString(),
				medMatCodigo));		
		result = executeCriteria(criteria);
		
		return result;
	}
	
	/**
	 * 
	 * @param medicamento
	 * @return
	 * @see #obterListaItemPrescricaoParaMdto(Integer)
	 */
	public List<MpmItemPrescricaoMdto> obterListaItemPrescricaoParaMdto(AfaMedicamento medicamento) {
		
		if (medicamento == null) {
			throw new IllegalArgumentException();
		}
		
		return this.obterListaItemPrescricaoParaMdto(medicamento.getMatCodigo());
	}
	
	public MpmItemPrescricaoMdto obterPorIdComJoin(MpmItemPrescricaoMdtoId chavePrimaria){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class);
		criteria.createAlias(MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), "pm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), "mdto", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("mdto."+AfaMedicamento.Fields.TPR.toString(), "tpr", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(MpmItemPrescricaoMdto.Fields.ID.toString(), chavePrimaria));
		return (MpmItemPrescricaoMdto) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MpmItemPrescricaoMdto> pesquisarItensMedicamentoPrescricaoPorJustificativa(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), MED);
		criteria.createAlias(MED_DOT + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), TUM);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), PMD);
		
		criteria.add(Restrictions.eq(IME_DOT + MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(IME_DOT + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(TUM_DOT + AfaTipoUsoMdto.Fields.IND_AVALIACAO.toString(), Boolean.TRUE));
		
		criteria.add(Restrictions.or(Restrictions.isNull(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
			Restrictions.and(Restrictions.isNull(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()), 
			Restrictions.and(Restrictions.gt(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), new Date()), 
			Restrictions.neProperty(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString())))));
		
		return executeCriteria(criteria);
	}
	
	public List<LocalDispensa2VO> listarPrescricaoItemMedicamentoFarmaciaMov(Integer atdSeq, Date dthrInicio, Date dthrFim, Short unfSeq, Date dthrMovimento){		
		DetachedCriteria subquery1 = DetachedCriteria.forClass(MpmPrescricaoMdto.class, "B");
		subquery1.setProjection(Projections.projectionList()
					.add(Projections.property("B." + MpmPrescricaoMdto.Fields.ATD_SEQ.toString()))
					.add(Projections.property("B." + MpmPrescricaoMdto.Fields.SEQ.toString())));
		subquery1.add(Restrictions.eq("B." + MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), atdSeq));
		subquery1.add(Restrictions.eqProperty("B." + MpmPrescricaoMdto.Fields.SEQ.toString(), PMD_DOT + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString()));
		subquery1.add(Restrictions.eq("B." + MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), Boolean.TRUE));
		
		DetachedCriteria subquery3 = DetachedCriteria.forClass(MpmPrescricaoMdto.class, "C");
		subquery3.setProjection(Projections.projectionList()
					.add(Projections.property("C." + MpmPrescricaoMdto.Fields.ATD_SEQ.toString()))
					.add(Projections.property("C." + MpmPrescricaoMdto.Fields.SEQ.toString())));
		subquery3.add(Restrictions.eq("C." + MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), atdSeq));
		subquery3.add(Restrictions.eqProperty("C." + MpmPrescricaoMdto.Fields.SEQ.toString(), "A." + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString()));
		subquery3.add(Restrictions.eq("C." + MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), Boolean.TRUE));
		
		DetachedCriteria subquery2 = DetachedCriteria.forClass(MpmPrescricaoMdto.class, "A");
		subquery2.setProjection(Projections.projectionList()
					.add(Projections.property("A." + MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString()))
					.add(Projections.property("A." + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString())));
		subquery2.add(Restrictions.eq("A." + MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString(), atdSeq));
		subquery2.add(Restrictions.eqProperty("A." + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString(), PMD_DOT + MpmPrescricaoMdto.Fields.SEQ.toString()));
		subquery2.add(Restrictions.eq("A." + MpmPrescricaoMdto.Fields.PENDENTE.toString(), DominioIndPendenteItemPrescricao.N));
		subquery2.add(Restrictions.or(
				Restrictions.eq("A." + MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), Boolean.TRUE),
				Restrictions.and(
						Restrictions.eq("A." + MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), Boolean.FALSE),
						Restrictions.isNotNull("A." + MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString()),
						Restrictions.isNotNull("A." + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString()),
						Subqueries.propertiesIn(new String[] {
								"A." + MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString(), 
								"A." + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString()}, subquery3)
				)
		));
		subquery2.add(
				Restrictions.or(
					Restrictions.ge("A." + MpmPrescricaoMdto.Fields.CRIADO_EM.toString(), dthrMovimento),
					Restrictions.ge("A." + MpmPrescricaoMdto.Fields.ALTERADO_EM.toString(), dthrMovimento)
				)
		);
		subquery2.add(Restrictions.lt("A." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), dthrFim));
		subquery2.add(
			Restrictions.or(
				Restrictions.isNull("A." + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
				Restrictions.gt("A." + MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), dthrInicio),
				Restrictions.and(
					Restrictions.eqProperty("A." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), "A." + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
					Restrictions.ge("A." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), dthrInicio)
				)
			)
		);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), PMD, JoinType.LEFT_OUTER_JOIN);
		criteria.setProjection(Projections.projectionList()
					.add(Projections.property(PMD_DOT + MpmPrescricaoMdto.Fields.ATD_SEQ.toString()), LocalDispensa2VO.Fields.ATD_SEQ.toString())
					.add(Projections.property(IME_DOT + MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString()), LocalDispensa2VO.Fields.MED_MAT_CODIGO.toString())
					.add(Projections.property(IME_DOT + MpmItemPrescricaoMdto.Fields.DOSE.toString()), LocalDispensa2VO.Fields.DOSE.toString())
					.add(Projections.property(IME_DOT + MpmItemPrescricaoMdto.Fields.FDSSEQ.toString()), LocalDispensa2VO.Fields.FDSSEQ.toString())
		);
		criteria.add(Restrictions.eq(PMD_DOT + MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(PMD_DOT + MpmPrescricaoMdto.Fields.INDPENDENTE.toString(), DominioIndPendenteItemPrescricao.N));
		criteria.add(
			Restrictions.or(
				Restrictions.eq(PMD_DOT + MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), Boolean.TRUE),
				Restrictions.and(
						Restrictions.eq(PMD_DOT + MpmPrescricaoMdto.Fields.INDSOLUCAO.toString(), Boolean.FALSE),
						Restrictions.isNotNull(PMD_DOT + MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString()),
						Restrictions.isNotNull(PMD_DOT + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString()),
						Subqueries.propertiesIn(new String[] {
								PMD_DOT + MpmPrescricaoMdto.Fields.PMD_ATD_SEQ_REPRESC.toString(), 
								PMD_DOT + MpmPrescricaoMdto.Fields.PMD_SEQ_REPRESC.toString()}, subquery1)
				)
			)
		);
		criteria.add(
				Restrictions.or(
						Restrictions.ge(PMD_DOT + MpmPrescricaoMdto.Fields.CRIADO_EM.toString(), dthrMovimento),
						Restrictions.ge(PMD_DOT + MpmPrescricaoMdto.Fields.ALTERADO_EM.toString(), dthrMovimento)
				)
		);
		criteria.add(Restrictions.lt(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), dthrFim));
		criteria.add(
				Restrictions.or(
						Restrictions.isNull(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
						Restrictions.gt(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), dthrInicio),
						Restrictions.and(
								Restrictions.eqProperty(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
								Restrictions.ge(PMD_DOT + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString(), dthrInicio)
						)
				)
		);
		criteria.add(
			Subqueries.propertiesNotIn(new String[] {
				PMD_DOT + MpmPrescricaoMdto.Fields.ATD_SEQ.toString(), 
				PMD_DOT + MpmPrescricaoMdto.Fields.SEQ.toString()}, subquery2)
		);
		criteria.setResultTransformer(Transformers.aliasToBean(LocalDispensa2VO.class));
		return executeCriteria(criteria);
	}		

	/**
	 * Obtém lista de itens de prescrição de medicamento relacionados à justificativa informada.
	 * 
	 * @param jumSeq - Código da Justificativa de Uso de Medicamento
	 * @return Lista de itens de prescrição de medicamento
	 */
	public List<MpmItemPrescricaoMdto> listarItensMedicamentoJustificativa(Integer jumSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);

		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO.toString(), JUM);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), PMD);
		criteria.createAlias(PMD_DOT + MpmPrescricaoMdto.Fields.TIPO_FREQ_APRAZAMENTO.toString(), TFQ);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), MED);
		criteria.createAlias(MED_DOT + AfaMedicamento.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), UMM, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.FORMA_DOSAGEM.toString(), FDS);
		criteria.createAlias(FDS_DOT + AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), UMM2, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MED_DOT + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), TUM);
		criteria.createAlias(TUM_DOT + AfaTipoUsoMdto.Fields.GRUPO_USO.toString(), GUP);

		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString(), jumSeq));
		criteria.add(Restrictions.eq(IME_DOT + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString(), DominioSimNao.S.isSim()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem a quantidade de Solicitações de cada Justificativa por Usuario logado e Avaliador selecionado.
	 */
	public List<Object[]> obterContaJustifPorServidorSigla(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO.toString(), JUM, JoinType.INNER_JOIN);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), MED, JoinType.INNER_JOIN);
		criteria.createAlias(MED_DOT + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), TUM, JoinType.INNER_JOIN);
		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.GRUPO_USO_MEDICAMENTO.toString(), GUP, JoinType.INNER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.groupProperty(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmJustificativaUsoMdto.Fields.SEQ.toString());
		projection.add(Projections.count(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmJustificativaUsoMdto.Fields.SEQ.toString());

		criteria.setProjection(projection);
		criteria.add(Restrictions.eq(GUP_DOT + AfaGrupoUsoMedicamento.Fields.RESPONSAVEL_AVALIACAO.toString(), respAvaliacao));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.CAND_APROV_LOTE.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SITUACAO.toString(), DominioSituacaoSolicitacaoMedicamento.T));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_MATRICULA_CONHECIMENTO.toString(), matricula));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_VIN_CODIGO_CONHECIMENTO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(IME_DOT + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq(TUM_DOT + AfaTipoUsoMdto.Fields.IND_AVALIACAO.toString(), DominioSimNao.S.isSim()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem a quantidade de Solicitações da Justificativa com Parecer por Usuario logado e Avaliador selecionado.
	 */
	public Long obterContaParecerPorServidorSigla(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.ITEM_PRESC_PARECER_MDTO.toString(), IPR, JoinType.INNER_JOIN);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO.toString(), JUM, JoinType.INNER_JOIN);
		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.GRUPO_USO_MEDICAMENTO.toString(), GUP, JoinType.INNER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.count(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmJustificativaUsoMdto.Fields.SEQ.toString());

		criteria.setProjection(projection);
		criteria.add(Restrictions.eq(GUP_DOT + AfaGrupoUsoMedicamento.Fields.RESPONSAVEL_AVALIACAO.toString(), respAvaliacao));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.CAND_APROV_LOTE.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SITUACAO.toString(), DominioSituacaoSolicitacaoMedicamento.T));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_MATRICULA_CONHECIMENTO.toString(), matricula));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_VIN_CODIGO_CONHECIMENTO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(IME_DOT + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString(), DominioSimNao.S.isSim())); 
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtem a quantidade de Solicitações de cada Justificativa por Usuario logado.
	 */
	public List<Object[]> obterContaJustifGeralPorServidorSigla(Integer matricula, Short vinCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO.toString(), JUM, JoinType.INNER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.groupProperty(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmJustificativaUsoMdto.Fields.SEQ.toString());
		projection.add(Projections.count(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmJustificativaUsoMdto.Fields.SEQ.toString());

		criteria.setProjection(projection);
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.CAND_APROV_LOTE.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SITUACAO.toString(), DominioSituacaoSolicitacaoMedicamento.T));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_MATRICULA_CONHECIMENTO.toString(), matricula));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_VIN_CODIGO_CONHECIMENTO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(IME_DOT + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString(), DominioSimNao.S.isSim()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtem a quantidade de Solicitações da Justificativa com Parecer por Usuario logado.
	 */
	public Long obterContaParecerGeralPorServidorSigla(Integer matricula, Short vinCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.ITEM_PRESC_PARECER_MDTO.toString(), IPR, JoinType.INNER_JOIN);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO.toString(), JUM, JoinType.INNER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.count(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmJustificativaUsoMdto.Fields.SEQ.toString());

		criteria.setProjection(projection);
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.CAND_APROV_LOTE.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SITUACAO.toString(), DominioSituacaoSolicitacaoMedicamento.T));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_MATRICULA_CONHECIMENTO.toString(), matricula));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_VIN_CODIGO_CONHECIMENTO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(IME_DOT + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString(), DominioSimNao.S.isSim())); 
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtem lista de Itens de Prescriçao por Usuario logado e Avaliador selecionado. 
	 */
	public List<MpmItemPrescricaoMdtoVO> obterItensPrescricaoPorServidorSigla(Integer matricula, Short vinCodigo, DominioIndRespAvaliacao respAvaliacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO.toString(), JUM, JoinType.INNER_JOIN);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), MED, JoinType.INNER_JOIN);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), PMD, JoinType.INNER_JOIN);
		criteria.createAlias(MED_DOT + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), TUM, JoinType.INNER_JOIN);
		criteria.createAlias(JUM_DOT + MpmJustificativaUsoMdto.Fields.GRUPO_USO_MEDICAMENTO.toString(), GUP, JoinType.INNER_JOIN);

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property(IME_DOT + MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString()), MpmItemPrescricaoMdtoVO.Fields.PMD_ATD_SEQ.toString());
		projection.add(Projections.property(IME_DOT + MpmItemPrescricaoMdto.Fields.PMD_SEQ.toString()), MpmItemPrescricaoMdtoVO.Fields.PMD_SEQ.toString());
		projection.add(Projections.property(IME_DOT + MpmItemPrescricaoMdto.Fields.SEQP.toString()), MpmItemPrescricaoMdtoVO.Fields.SEQP.toString());
		projection.add(Projections.property(IME_DOT + MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString()), MpmItemPrescricaoMdtoVO.Fields.MED_MAT_CODIGO.toString());
		projection.add(Projections.property(IME_DOT + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString()), MpmItemPrescricaoMdtoVO.Fields.IND_ORIGEM_JUSTIF.toString());
		projection.add(Projections.property(PMD_DOT + MpmPrescricaoMdto.Fields.DURACAO_TRAT_SOL.toString()), MpmItemPrescricaoMdtoVO.Fields.DURACAO_TRAT_SOLICITADO.toString());
		projection.add(Projections.property(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()), MpmItemPrescricaoMdtoVO.Fields.JUM_SEQ.toString());

		criteria.setProjection(projection);
		if (respAvaliacao != null) {
			criteria.add(Restrictions.eq(GUP_DOT + AfaGrupoUsoMedicamento.Fields.RESPONSAVEL_AVALIACAO.toString(), respAvaliacao));
		}
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.CAND_APROV_LOTE.toString(), DominioSimNao.S.isSim()));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SITUACAO.toString(), DominioSituacaoSolicitacaoMedicamento.T));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_MATRICULA_CONHECIMENTO.toString(), matricula));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SER_VIN_CODIGO_CONHECIMENTO.toString(), vinCodigo));
		criteria.add(Restrictions.eq(TUM_DOT + AfaTipoUsoMdto.Fields.IND_AVALIACAO.toString(), DominioSimNao.S.isSim()));
		
		criteria.addOrder(Order.asc(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(IME_DOT + MpmItemPrescricaoMdto.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc(IME_DOT + MpmItemPrescricaoMdto.Fields.MED_MAT_CODIGO.toString()));
		criteria.addOrder(Order.desc(IME_DOT + MpmItemPrescricaoMdto.Fields.ORIGEM_JUST.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MpmItemPrescricaoMdtoVO.class));
		
		return executeCriteria(criteria);
	}

	public List<MpmItemPrescricaoMdto> listarPrescMedicamentoDetalhePorPacienteAtendimento(Integer pacCodigo, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, "IME");
		criteria.createAlias("IME." + MpmItemPrescricaoMdto.Fields.MEDICAMENTO.toString(), "MED");
		criteria.createAlias("MED." + AfaMedicamento.Fields.TIPO_USO_MEDICAMENTO.toString(), "TUM");
		criteria.createAlias("IME." + MpmItemPrescricaoMdto.Fields.PRESCRICAO_MEDICAMENTO.toString(), "PMD");
		criteria.createAlias("IME." + MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO.toString(), "JUM", JoinType.LEFT_OUTER_JOIN);		
		
		criteria.add(Restrictions.or(Restrictions.eq("TUM." + AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString(), Boolean.TRUE), 
				Restrictions.eq("TUM." + AfaTipoUsoMdto.Fields.SIGLA.toString(), "T")));
		criteria.add(Restrictions.eq("PMD." + MpmPrescricaoMdto.Fields.INDPENDENTE.toString(), DominioIndPendenteItemPrescricao.N));
		criteria.add(Restrictions.or(Restrictions.isNull("PMD." + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()),
				Restrictions.gt("PMD." + MpmPrescricaoMdto.Fields.DTHR_FIM.toString(), new Date())));
		
		criteria.add(Restrictions.eq("IME." + MpmItemPrescricaoMdto.Fields.PMD_ATD_SEQ.toString(), atdSeq));
		
		criteria.addOrder(Order.desc("PMD." + MpmPrescricaoMdto.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.desc("PMD." + MpmPrescricaoMdto.Fields.DTHR_FIM.toString()));
		
		return executeCriteria(criteria);
	}

	public MpmItemPrescricaoMdto obterPorIdJustificativa(MpmItemPrescricaoMdtoId imeId, Integer jumSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoMdto.class, IME);
		criteria.createAlias(IME_DOT + MpmItemPrescricaoMdto.Fields.JUSTIFICATIVA_USO.toString(), JUM, JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(IME_DOT + MpmItemPrescricaoMdto.Fields.ID.toString(), imeId));
		criteria.add(Restrictions.eq(JUM_DOT + MpmJustificativaUsoMdto.Fields.SEQ.toString(), jumSeq));
		
		return (MpmItemPrescricaoMdto) executeCriteriaUniqueResult(criteria);
	}
}
