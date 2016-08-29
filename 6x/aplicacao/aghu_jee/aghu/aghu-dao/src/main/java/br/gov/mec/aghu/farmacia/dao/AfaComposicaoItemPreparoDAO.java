package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.Query;

import br.gov.mec.aghu.farmacia.vo.ComposicaoItemPreparoVO;
import br.gov.mec.aghu.model.AfaComposicaoItemPreparo;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AfaComposicaoItemPreparoDAO extends BaseDao<AfaComposicaoItemPreparo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -988004100003807827L;
	
//	public List<ComposicaoItemPreparoVO> pesquisarComposicaoItemPreparo(Integer itoPtoSeq, Short itoSeqp){
//		DetachedCriteria criteria = DetachedCriteria.forClass(AfaComposicaoItemPreparo.class, "CIP");
//		
//		ProjectionList pList = Projections.projectionList();
//		
//		pList.add(
//				Projections.property("CIP."
//						+AfaComposicaoItemPreparo.Fields.ITO_PTO_SEQ.toString()), "itoPtoSeq")
//			.add(Projections.property("CIP."
//					+AfaComposicaoItemPreparo.Fields.ITO_SEQP.toString()), "itoSeqp")
//			.add(Projections.property("CIP."
//					+AfaComposicaoItemPreparo.Fields.SEQP.toString()), "seqp")
//			.add(Projections.property("MAT."
//					+ScoMaterial.Fields.CODIGO.toString()), "medicamento")
//			.add(Projections.property("CIP."
//					+AfaComposicaoItemPreparo.Fields.IND_EXTERNO.toString()), "indExterno")
//			.add(Projections.property("PHI."
//					+FatProcedHospInternos.Fields.SEQ.toString()), "phiSeq")
//			.add(Projections.property("CIP."
//					+AfaComposicaoItemPreparo.Fields.QTDE_PREPARADA.toString()), "qtdePreparada")
//			.add(Projections.property("AFD."
//					+AfaFormaDosagem.Fields.FATOR_CONVERSAO_UP.toString()), "qtdePreparada");
//		
//		
//		criteria.createAlias(AfaComposicaoItemPreparo.Fields.AFA_MEDICAMENTO.toString(), "MED");
//		
//		criteria.createAlias(AfaMedicamento.Fields.MATERIAL.toString(), "MAT");
//		
//		criteria.createAlias(ScoMaterial.Fields.PROCED_HOSP_INTERNO.toString(), "PHI");
//		
//		criteria.createAlias(AfaComposicaoItemPreparo.Fields.AFA_FORMA_DOSAGEM.toString(), "AFD");
//		
//		criteria.add(Restrictions.eq("CIP."+AfaComposicaoItemPreparo.Fields.ITO_PTO_SEQ.toString(), itoPtoSeq));
//		criteria.add(Restrictions.eq("CIP."+AfaComposicaoItemPreparo.Fields.ITO_SEQP.toString(), itoSeqp));
//		
//		criteria.addOrder(Order.asc("CIP."+AfaComposicaoItemPreparo.Fields.ITO_PTO_SEQ.toString()));
//		criteria.addOrder(Order.asc("CIP."+AfaComposicaoItemPreparo.Fields.ITO_SEQP.toString()));
//		
//		criteria.setProjection(pList);
//
//		criteria.setResultTransformer(Transformers
//				.aliasToBean(ComposicaoItemPreparoVO.class));
//		
//		return executeCriteria(criteria);
//	}
	
	@SuppressWarnings("unchecked")
	public List<ComposicaoItemPreparoVO> pesquisarComposicaoItemPreparo(Integer itoPtoSeq, Short itoSeqp){
		
		StringBuffer sql = new StringBuffer(" Select ")
		.append("   cip.").append(AfaComposicaoItemPreparo.Fields.ITO_PTO_SEQ.toString()).append(" as itoPtoSeq ")
		.append(" , cip.").append(AfaComposicaoItemPreparo.Fields.ITO_SEQP.toString()).append(" as itoSeqp ")
		.append(" , cip.").append(AfaComposicaoItemPreparo.Fields.SEQP.toString()).append(" as seqp ")
		.append(" , mat.").append(ScoMaterial.Fields.CODIGO.toString()).append(" as medicamento ")
		.append(" , cip.").append(AfaComposicaoItemPreparo.Fields.IND_EXTERNO.toString()).append(" as indExterno ")
		.append(" , phi.").append(FatProcedHospInternosPai.Fields.SEQ.toString()).append(" as phiSeq ")
		.append(" , cip.").append(AfaComposicaoItemPreparo.Fields.QTDE_PREPARADA.toString()).append(" as qtdePreparada ")
		.append(" , afd.").append(AfaFormaDosagem.Fields.FATOR_CONVERSAO_UP.toString()).append(" as fatorConversaoUp ")
		.append(" from ")
		.append( AfaComposicaoItemPreparo.class.getSimpleName() ).append(" as cip ")
		.append(" inner join cip.").append(AfaComposicaoItemPreparo.Fields.AFA_MEDICAMENTO.toString()).append(" as med ")
		.append(" inner join med.").append(AfaMedicamento.Fields.MATERIAL.toString()).append(" as mat ")
		.append(" inner join mat.").append(ScoMaterial.Fields.PROCED_HOSP_INTERNO.toString()).append(" as phi ")
		.append(" inner join cip.").append(AfaComposicaoItemPreparo.Fields.AFA_FORMA_DOSAGEM.toString()).append(" as afd ")
		.append(" where ")
		.append("      cip.").append(AfaComposicaoItemPreparo.Fields.ITO_PTO_SEQ.toString()).append(" = :itoPtoSeq ")
		.append(" and  cip.").append(AfaComposicaoItemPreparo.Fields.ITO_SEQP.toString()).append(" = :itoSeqp ")
		.append(" order by ")
		.append("   cip.").append(AfaComposicaoItemPreparo.Fields.ITO_PTO_SEQ.toString())
		.append(" , cip.").append(AfaComposicaoItemPreparo.Fields.ITO_SEQP.toString())
		.append(" , cip.").append(AfaComposicaoItemPreparo.Fields.SEQP.toString());
		
		Query query = createHibernateQuery(sql.toString());
		query.setParameter("itoPtoSeq", itoPtoSeq);
		query.setParameter("itoSeqp", itoSeqp);
		
		return query.list();
	}

}