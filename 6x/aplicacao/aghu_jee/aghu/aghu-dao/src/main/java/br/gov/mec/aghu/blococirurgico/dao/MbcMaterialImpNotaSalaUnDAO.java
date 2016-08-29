package br.gov.mec.aghu.blococirurgico.dao;

import java.util.ArrayList;
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

import br.gov.mec.aghu.blococirurgico.vo.SubRelatorioNotasDeConsumoDaSalaMateriaisVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMaterialImpNotaSalaUn;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.ScoMaterial;

public class MbcMaterialImpNotaSalaUnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMaterialImpNotaSalaUn> {
    @Inject
    private MbcUnidadeNotaSalaDAO aMbcUnidadeNotaSalaDAO;
    @Inject
    private MbcMatOrteseProtCirgDAO aMbcMatOrteseProtCirgDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1668190416946283261L;
	
	/**
	 * Pesquisa materiais de impressão de nota sala através da unidade de nota sala
	 * 
	 * @param descricao
	 * @return
	 */
	public List<MbcMaterialImpNotaSalaUn> pesquisarMaterialImpNotaSalaUnPorUnidadeNotaSala(MbcUnidadeNotaSala unidadeNotaSala) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMaterialImpNotaSalaUn.class);
		criteria.createAlias(MbcMaterialImpNotaSalaUn.Fields.SCO_MATERIAL.toString(), "MAT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.UNIDADE_MEDIDA.toString(), "UNID");
		
		criteria.add(Restrictions.eq(MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS.toString(), unidadeNotaSala));
		
		criteria.addOrder(Order.asc(MbcMaterialImpNotaSalaUn.Fields.ORDEM_IMP.toString()));
		
		return executeCriteria(criteria);
	}

	public List<String> obterListaOrteseProtesePorUnfSeqCrgSeq(Short unfSeq, Integer crgSeq, Integer pciSeq,
			Short espSeq, Integer grupoMatOrtProt) {
		List<String> retorno = new ArrayList<String>();
		
		retorno.addAll(obterListaOrteseProtesePorUnfSeqCrgSeqUnion1(unfSeq, crgSeq, pciSeq, espSeq, grupoMatOrtProt));
		retorno.addAll(getMbcMatOrteseProtCirgDAO().obterListaOrteseProtesePorUnfSeqCrgSeqUnion2(crgSeq));
		
		return retorno;
	}
	
	private List<String> obterListaOrteseProtesePorUnfSeqCrgSeqUnion1(Short unfSeq, Integer crgSeq, Integer pciSeq,
			Short espSeq, Integer grupoMatOrtProt) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMaterialImpNotaSalaUn.class, "MNS");
		criteria.createAlias("MNS.".concat(MbcMaterialImpNotaSalaUn.Fields.SCO_MATERIAL.toString()), "MAT");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MNS." + MbcMaterialImpNotaSalaUn.Fields.NOME_IMP.toString())));
		
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), grupoMatOrtProt));
		criteria.add(Restrictions.eq("MNS." + MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS_UNFSEQ.toString(), unfSeq));
		
		List<Short> subCriteriaResultado = obterResultadoSubCriteriaOrteseProtesePorUnfSeqCrgSeq(unfSeq, pciSeq, espSeq);
		
		criteria.add(Restrictions.in("MNS." + MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS_SEQP.toString(), subCriteriaResultado));
		
		/*criteria.getExecutableCriteria(getSession()).setFirstResult(0);
		criteria.getExecutableCriteria(getSession()).setMaxResults(20 - getMbcMatOrteseProtCirgDAO().countMatOrteseProtesePorCrgSeq(crgSeq));
		*/
		
		return executeCriteria(criteria, 0, 20 - getMbcMatOrteseProtCirgDAO().countMatOrteseProtesePorCrgSeq(crgSeq).intValue(), null, false);
		
	}
	
	private List<Short> obterResultadoSubCriteriaOrteseProtesePorUnfSeqCrgSeq(Short unfSeq, Integer pciSeq, Short espSeq) {
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
	
	public List<SubRelatorioNotasDeConsumoDaSalaMateriaisVO> obterListaMateriaisPorSeqpUnfSeq(Short notaSalaSeqp, Short notaSalaUnfSeq,
			Integer grupoMatOrtProt) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMaterialImpNotaSalaUn.class, "MNS");
		criteria.createAlias("MNS.".concat(MbcMaterialImpNotaSalaUn.Fields.SCO_MATERIAL.toString()), "MAT");
		
		StringBuilder sqlProjection = new StringBuilder(200);
		sqlProjection.append("   RPAD({alias}.NOME_IMP,30,'. ') ||': '||                     ");
		sqlProjection.append("   LPAD(COALESCE({alias}.UMD_CODIGO,'___'),3,' ') ||' ______'  "
			).append( SubRelatorioNotasDeConsumoDaSalaMateriaisVO.Fields.DESCRICAO_MATERIAL.toString());
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlProjection.toString(),
						new String[]{SubRelatorioNotasDeConsumoDaSalaMateriaisVO.Fields.DESCRICAO_MATERIAL.toString()},
						new Type[] { StringType.INSTANCE }))
				.add(Projections.property("MNS." + MbcMaterialImpNotaSalaUn.Fields.ORDEM_IMP.toString()),
						SubRelatorioNotasDeConsumoDaSalaMateriaisVO.Fields.ORDEM.toString()));
		
		criteria.add(Restrictions.sqlRestriction("COALESCE(mat1_.GMT_CODIGO,0)  <> " + grupoMatOrtProt));
		criteria.add(Restrictions.eq("MNS." + MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS_SEQP.toString(), notaSalaSeqp));
		criteria.add(Restrictions.eq("MNS." + MbcMaterialImpNotaSalaUn.Fields.MBC_UNIDADE_NOTA_SALAS_UNFSEQ.toString(), notaSalaUnfSeq));
		
		criteria.addOrder(Order.asc(MbcMaterialImpNotaSalaUn.Fields.ORDEM_IMP.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SubRelatorioNotasDeConsumoDaSalaMateriaisVO.class));
		
		return executeCriteria(criteria);
	}
	
	private MbcMatOrteseProtCirgDAO getMbcMatOrteseProtCirgDAO() {
		return aMbcMatOrteseProtCirgDAO;
	}
	
	private MbcUnidadeNotaSalaDAO getMbcUnidadeNotaSalaDAO() {
		return aMbcUnidadeNotaSalaDAO;
	}
}
