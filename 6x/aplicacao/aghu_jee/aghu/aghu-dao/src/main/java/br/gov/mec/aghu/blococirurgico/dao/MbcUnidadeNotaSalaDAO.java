package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.MbcUnidadeNotaSalaId;

public class MbcUnidadeNotaSalaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcUnidadeNotaSala> {

	
	private static final long serialVersionUID = 1803028752949109305L;
	



	@Override
	protected void obterValorSequencialId(MbcUnidadeNotaSala elemento) {
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		MbcUnidadeNotaSalaId id = new MbcUnidadeNotaSalaId();

		Integer seqp = 0;
		final Short maxSeqp = this.obterItemNotaSalaSeqpMax(elemento.getId().getUnfSeq());
		if (maxSeqp != null) {
			seqp = maxSeqp.intValue();
		}

		seqp = seqp + 1;
		id.setSeqp(seqp.shortValue());
		id.setUnfSeq(elemento.getId().getUnfSeq());
		elemento.setId(id);
	}
	
	private Short obterItemNotaSalaSeqpMax(final Short unfSeq) {
		final DetachedCriteria criteria  = DetachedCriteria.forClass(MbcUnidadeNotaSala.class);
		criteria.add(Restrictions.eq(MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), unfSeq));

		criteria.setProjection(Projections.max(AelItemSolicitacaoExames.Fields.SEQP.toString()));
		final Object objMax = this.executeCriteriaUniqueResult(criteria);

		return (Short) objMax;
	}
	
	
	public List<MbcUnidadeNotaSala> obterNotaSalaPorUnidade(final Short unfSeq) {
		DetachedCriteria criteria = criarCriteriaNotaSalaPorUnidade(unfSeq);
		criteria.addOrder(Order.asc(MbcUnidadeNotaSala.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public Long obterNotaSalaPorUnidadeCount(final Short unfSeq) {
		DetachedCriteria criteria = criarCriteriaNotaSalaPorUnidade(unfSeq);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteriaNotaSalaPorUnidade(final Short unfSeq) {
		DetachedCriteria criteria  = DetachedCriteria.forClass(MbcUnidadeNotaSala.class);
		criteria.createAlias(MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "PCI", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), unfSeq));
		return criteria;
	}
	
	
	public List<MbcUnidadeNotaSala> obterNotasSalaPorUnidadeSemEspecialidadeProcedimento(final MbcUnidadeNotaSala notaSala) {
		final DetachedCriteria criteria  = DetachedCriteria.forClass(MbcUnidadeNotaSala.class);
		criteria.add(Restrictions.eq(MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), notaSala.getId().getUnfSeq()));
		criteria.add(Restrictions.eq(MbcUnidadeNotaSala.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.isNull(MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES.toString()));
		criteria.add(Restrictions.isNull(MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString()));

		if(notaSala.getId().getSeqp()!=null){
			criteria.add(Restrictions.ne(MbcUnidadeNotaSala.Fields.SEQP.toString(), notaSala.getId().getUnfSeq()));
		}

		return executeCriteria(criteria);
	}
	
	public Integer obterNotaSalaPorUnfSeqPciSeq(Short unfSeq, Integer pciSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "UNS");
		criteria.setProjection(Projections.property("UNS." + MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString()));
		criteria.add(Restrictions.eq("UNS." + MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("UNS." + MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString(), pciSeq));
		
        List<Object> listaResult = executeCriteria(criteria, 0, 1, null, false);
		
		return (Integer) (listaResult != null && listaResult.size() > 0 ? listaResult.get(0) : 0);
	}
	
	public Short obterNotaSalaPorUnfSeqEspSeq(Short unfSeq, Short espSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "UNS");
		criteria.setProjection(Projections.property("UNS." + MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES_SEQ.toString()));
		criteria.add(Restrictions.eq("UNS." + MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), unfSeq));
		criteria.add(Restrictions.eq("UNS." + MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES_SEQ.toString(), espSeq));
		
		List<Object> listaResult = executeCriteria(criteria, 0, 1, null, false);	
		return (Short) (listaResult != null && listaResult.size() > 0 ? listaResult.get(0) : 0);
	}
	
	public MbcUnidadeNotaSalaId obterUnidadeNotaSalaIdPorPciSeqEspSeq(Short unfSeq, Integer pciSeq, Short espSeq) {
		DetachedCriteria subCriteria1 = obterSubCriteriaNotaSala1(unfSeq, pciSeq, espSeq);
		
		DetachedCriteria subCriteria2 = obterSubCriteriaNotaSala2(unfSeq, pciSeq, espSeq);
		
		DetachedCriteria subCriteria3 = obterSubCriteriaNotaSala3(unfSeq, pciSeq, espSeq);
		
		DetachedCriteria criteriaPrincipal = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOA");
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("NOA." + MbcUnidadeNotaSala.Fields.SEQP.toString()))
		.add(Projections.property("NOA." + MbcUnidadeNotaSala.Fields.UNFSEQ.toString()));
		criteriaPrincipal.setProjection(projection);
		
		criteriaPrincipal.add(Restrictions.eq("NOA." + MbcUnidadeNotaSala.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteriaPrincipal.add(Restrictions.eq("NOA." + MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), unfSeq));
		criteriaPrincipal.add(Restrictions.or(
				Restrictions.eq("NOA." + MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString(), pciSeq),
				Restrictions.or(
						Restrictions.and(
								Restrictions.eq("NOA." + MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES_SEQ.toString(), espSeq),
								Subqueries.notExists(subCriteria1)), Restrictions.and(
										Restrictions.isNull("NOA." + MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES_SEQ.toString()),
										Restrictions.and(
												Restrictions.isNull("NOA." + MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString()),
												Restrictions.and(
														Subqueries.notExists(subCriteria2), Subqueries.notExists(subCriteria3)))))));
		
		/*criteriaPrincipal.getExecutableCriteria(getSession()).setFirstResult(0);
		criteriaPrincipal.getExecutableCriteria(getSession()).setMaxResults(1);*/
		List<Object[]> listaResult = executeCriteria(criteriaPrincipal, 0, 1, null, false);	
		
		if (listaResult != null && listaResult.size() > 0) {
			Object[] resultado = listaResult.get(0);
			Short seqPRet = (Short) resultado[0];
			Short unfSeqRet = (Short) resultado[1];
			
			return new MbcUnidadeNotaSalaId(unfSeqRet,seqPRet);
		}
		
		return null;
		
		
		
		
		
		 
	}
	
	public DetachedCriteria obterSubCriteriaNotaSala1(Short unfSeq, Integer pciSeq, Short espSeq) {
		DetachedCriteria subCriteria1 = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOZ");
		subCriteria1.setProjection(Projections.property("NOZ." + MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subCriteria1.add(Restrictions.eq("NOZ." + MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString(), pciSeq));
		subCriteria1.add(Restrictions.eq("NOZ." + MbcUnidadeNotaSala.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (unfSeq != null) {
			subCriteria1.add(Restrictions.eq("NOZ." + MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), unfSeq));
		}
		
		return subCriteria1;
	}
	
	public DetachedCriteria obterSubCriteriaNotaSala2(Short unfSeq, Integer pciSeq, Short espSeq) {
		DetachedCriteria subCriteria2 = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOX");
		subCriteria2.setProjection(Projections.property("NOX." + MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subCriteria2.add(Restrictions.eq("NOX." + MbcUnidadeNotaSala.Fields.AGH_ESPECIALIDADES_SEQ.toString(), espSeq));
		subCriteria2.add(Restrictions.eq("NOX." + MbcUnidadeNotaSala.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (unfSeq != null) {
			subCriteria2.add(Restrictions.eq("NOX." + MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), unfSeq));
		}
		
		return subCriteria2;
	}
	
	public DetachedCriteria obterSubCriteriaNotaSala3(Short unfSeq, Integer pciSeq, Short espSeq) {
		DetachedCriteria subCriteria3 = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOY");
		subCriteria3.setProjection(Projections.property("NOY." + MbcUnidadeNotaSala.Fields.SEQP.toString()));
		subCriteria3.add(Restrictions.eq("NOY." + MbcUnidadeNotaSala.Fields.MBC_PROCEDIMENTO_CIRURGICOS_SEQ.toString(), pciSeq));
		subCriteria3.add(Restrictions.eq("NOY." + MbcUnidadeNotaSala.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (unfSeq != null) {
			subCriteria3.add(Restrictions.eq("NOY." + MbcUnidadeNotaSala.Fields.UNFSEQ.toString(), unfSeq));
		}
		
		return subCriteria3;
	}
}
