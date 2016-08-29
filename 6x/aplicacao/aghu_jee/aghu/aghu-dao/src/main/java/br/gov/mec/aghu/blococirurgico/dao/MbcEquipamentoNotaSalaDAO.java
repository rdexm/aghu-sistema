package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioNotasDeConsumoDaSalaEquipamentosVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoNotaSala;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;

public class MbcEquipamentoNotaSalaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcEquipamentoNotaSala> {
    @Inject
    private MbcUnidadeNotaSalaDAO aMbcUnidadeNotaSalaDAO;
    @Inject
    private MbcEquipamentoUtilCirgDAO aMbcEquipamentoUtilCirgDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6574727193856873147L;

	
	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = 
			DetachedCriteria.forClass(MbcEquipamentoNotaSala.class);
		criteria.createAlias(MbcEquipamentoNotaSala.Fields.MBC_EQUIPAMENTO_CIRURGICO.toString(), "EQUP");
		
		return criteria;
	}

	public List<MbcEquipamentoNotaSala> listarEquipamentoNotaSala(MbcEquipamentoCirurgico equipamentoCirurgico) {
		DetachedCriteria criteria = this.obterCriteria();
		criteria.add(Restrictions.eq(MbcEquipamentoNotaSala.Fields.MBC_EQUIPAMENTO_CIRURGICO.toString(), equipamentoCirurgico));
		return this.executeCriteria(criteria);
	}

	public List<MbcEquipamentoNotaSala> listarEquipamentoNotaSalaPorUnfSeqp(final short UnfSeq, final short seqp) {
		DetachedCriteria criteria = this.obterCriteria();
		
		criteria.add(Restrictions.eq(MbcEquipamentoNotaSala.Fields.UNF_SEQ.toString(), UnfSeq));
		criteria.add(Restrictions.eq(MbcEquipamentoNotaSala.Fields.SEQP.toString(), seqp));
		criteria.addOrder(Order.asc(MbcEquipamentoNotaSala.Fields.ORDEM_IMP.toString()));

		return this.executeCriteria(criteria);
	}
	
	public List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> obterEquipamentosPorUnfSeqCrgSeq(Short unfSeq,
			Integer crgSeq, Integer pciSeq, Short espSeq) {
		List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> retorno = new ArrayList<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO>();
		
		retorno.addAll(obterEquipamentosPorUnfSeqCrgSeqUnion1(unfSeq, crgSeq, pciSeq, espSeq));
		retorno.addAll(getMbcEquipamentoUtilCirgDAO().obterEquipamentosPorUnfSeqCrgSeqUnion2(crgSeq));
		
		Collections.sort(retorno, new Comparator<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO>() {
			@Override
			public int compare(SubRelatorioNotasDeConsumoDaSalaEquipamentosVO arg0, SubRelatorioNotasDeConsumoDaSalaEquipamentosVO arg1) {
				return arg0.getOrdem().compareTo(arg1.getOrdem());
			}
		});
		
		return retorno;
	}
	
	private List<SubRelatorioNotasDeConsumoDaSalaEquipamentosVO> obterEquipamentosPorUnfSeqCrgSeqUnion1(Short unfSeq,
			Integer crgSeq, Integer pciSeq, Short espSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcEquipamentoNotaSala.class, "ENS");
		criteria.createAlias("ENS.".concat(MbcEquipamentoNotaSala.Fields.MBC_EQUIPAMENTO_CIRURGICO.toString()), "EUX");
		
		StringBuilder sqlProjection = new StringBuilder(100);
		sqlProjection.append("   RPAD(SUBSTR(eux1_.DESCRICAO,1,20),20,'.') || ':__'   "
			).append( SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.DESCRICAO_EQUIPAMENTO.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.DESCRICAO_EQUIPAMENTO.toString()},
						new Type[] { StringType.INSTANCE }))
				.add(Projections.property("ENS." + MbcEquipamentoNotaSala.Fields.ORDEM_IMP.toString()),
						SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.Fields.ORDEM.toString()));
		
		List<Short> subCriteriaResultado = obterResultadoSubCriteriaEquipamentosPorUnfSeqCrgSeq(unfSeq, pciSeq, espSeq);
		
		criteria.add(Restrictions.eq("ENS." + MbcEquipamentoNotaSala.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.in("ENS." + MbcEquipamentoNotaSala.Fields.SEQP.toString(), subCriteriaResultado));
		
		/*criteria.getExecutableCriteria(getSession()).setFirstResult(0);
		criteria.getExecutableCriteria(getSession()).setMaxResults(24 - getMbcEquipamentoUtilCirgDAO().countEquipamentoUtilCirgPorCrgSeq(crgSeq));*/
				
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaEquipamentosVO.class));
		
		return executeCriteria(criteria, 0, 24 - getMbcEquipamentoUtilCirgDAO().countEquipamentoUtilCirgPorCrgSeq(crgSeq).intValue(), null, false);
	}
	
	private List<Short> obterResultadoSubCriteriaEquipamentosPorUnfSeqCrgSeq(Short unfSeq, Integer pciSeq, Short espSeq) {
		DetachedCriteria subCriteria1 = getMbcUnidadeNotaSalaDAO().obterSubCriteriaNotaSala1(unfSeq, pciSeq, espSeq);
		
		DetachedCriteria subCriteria2 = getMbcUnidadeNotaSalaDAO().obterSubCriteriaNotaSala2(unfSeq, pciSeq, espSeq);
		
		DetachedCriteria subCriteria3 = getMbcUnidadeNotaSalaDAO().obterSubCriteriaNotaSala3(unfSeq, pciSeq, espSeq);
		
		DetachedCriteria criteriaPrincipal = DetachedCriteria.forClass(MbcUnidadeNotaSala.class, "NOA");
		criteriaPrincipal.setProjection(Projections.property("NOA." + MbcUnidadeNotaSala.Fields.SEQP.toString()));
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
		
		return executeCriteria(criteriaPrincipal);
	}
	
	private MbcEquipamentoUtilCirgDAO getMbcEquipamentoUtilCirgDAO() {
		return aMbcEquipamentoUtilCirgDAO;
	}
	
	private MbcUnidadeNotaSalaDAO getMbcUnidadeNotaSalaDAO() {
		return aMbcUnidadeNotaSalaDAO;
	}
}