package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpTxtPadraoMacro;
import br.gov.mec.aghu.model.AelTextoPadraoMacro;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelTextoPadraoMacroDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTextoPadraoMacro>  {
	
	private static final long serialVersionUID = -3378722239135311653L;

	public List<AelTextoPadraoMacro> pesquisarTextoPadraoMacroPorAelGrpTxtPadraoMacro(final Short seqAelGrpTxtPadraoMacro) {
		
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelTextoPadraoMacro.class);
		
		if (seqAelGrpTxtPadraoMacro != null) {
			criteria.add(Restrictions.eq(AelTextoPadraoMacro.Fields.AEL_GRP_TXT_PADRAO_MACROS_SEQ.toString(), seqAelGrpTxtPadraoMacro));
		}
		
		criteria.addOrder(Order.asc(AelTextoPadraoMacro.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}	
	
	public List<AelTextoPadraoMacro> pesquisarTextoPadraoMacroscopia(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacros, final String filtro, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria = obterCriteria(aelGrpTxtPadraoMacros,filtro, indSituacao);
		criteria.addOrder(Order.asc(AelTextoPadraoMacro.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarTextoPadraoMacroscopiaCount(final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacros, final String filtro, final DominioSituacao indSituacao) {
		return executeCriteriaCount(obterCriteria(aelGrpTxtPadraoMacros,filtro, indSituacao));
	}

	private DetachedCriteria obterCriteria(
			final AelGrpTxtPadraoMacro aelGrpTxtPadraoMacros,
			final String filtro, final DominioSituacao indSituacao) {
		final DetachedCriteria criteria =  DetachedCriteria.forClass(AelTextoPadraoMacro.class);
		
		if(aelGrpTxtPadraoMacros != null){
			criteria.add(Restrictions.eq(AelTextoPadraoMacro.Fields.AEL_GRP_TXT_PADRAO_MACROS.toString(), aelGrpTxtPadraoMacros));
		}
		
		if(indSituacao != null){
			criteria.add(Restrictions.eq(AelTextoPadraoMacro.Fields.IND_SITUACAO.toString(), indSituacao));
		}

		if(StringUtils.isNotBlank(filtro)){
			if(CoreUtil.isNumeroInteger(filtro)){
				criteria.add(Restrictions.eq(AelTextoPadraoMacro.Fields.SEQP.toString(), Short.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelTextoPadraoMacro.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}	
	
	public Short obterProximaSequence(Short lubSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTextoPadraoMacro.class);
		
		criteria.add(Restrictions.eq(AelTextoPadraoMacro.Fields.LUB_SEQ.toString(), lubSeq));
		criteria.setProjection(Projections.max(AelTextoPadraoMacro.Fields.SEQP.toString()));
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			return 1;
		}
		return ++seq;
	}
	
}
