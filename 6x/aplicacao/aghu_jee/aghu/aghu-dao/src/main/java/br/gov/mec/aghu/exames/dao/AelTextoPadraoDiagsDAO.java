package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiags;
import br.gov.mec.aghu.model.AelTextoPadraoDiagsId;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelTextoPadraoDiagsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelTextoPadraoDiags> {

	private static final long serialVersionUID = 9203988261239871941L;

	public List<AelTextoPadraoDiags> pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(final Short seqAelGrpTxtPadraoDiag) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTextoPadraoDiags.class);
		if(seqAelGrpTxtPadraoDiag != null){
			criteria.add(Restrictions.eq(AelTextoPadraoDiags.Fields.AEL_GRP_TXT_PADRAO_DIAGS_SEQ.toString(), seqAelGrpTxtPadraoDiag));
		}
		
		criteria.addOrder(Order.asc(AelTextoPadraoDiags.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AelTextoPadraoDiags> pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiags(final Short seqAelGrpTxtPadraoDiag, final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = obterCriteria(seqAelGrpTxtPadraoDiag,filtro, situacao);
		criteria.addOrder(Order.asc(AelTextoPadraoDiags.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public Long pesquisarTextoPadraoDiaguinosticoPorAelGrpTxtPadraoDiagsCount(final Short seqAelGrpTxtPadraoDiag, final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = obterCriteria(seqAelGrpTxtPadraoDiag,filtro, situacao);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteria(final Short seqAelGrpTxtPadraoDiag,
			final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTextoPadraoDiags.class);
		if(seqAelGrpTxtPadraoDiag != null){
			criteria.add(Restrictions.eq(AelTextoPadraoDiags.Fields.AEL_GRP_TXT_PADRAO_DIAGS_SEQ.toString(), seqAelGrpTxtPadraoDiag));
		}
		
		if(StringUtils.isNotBlank(filtro)){
			if(CoreUtil.isNumeroShort(filtro)){
				criteria.add(Restrictions.eq(AelTextoPadraoDiags.Fields.SEQP.toString(), Short.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelTextoPadraoDiags.Fields.DESCRICAO.toString(), filtro,MatchMode.ANYWHERE));
			}
		}
		
		if(situacao != null){
			criteria.add(Restrictions.eq(AelTextoPadraoDiags.Fields.IND_SITUACAO.toString(), situacao));
		}
		return criteria;
	}

	public AelTextoPadraoDiagsId gerarId(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelTextoPadraoDiags.class);		
		
		if(aelGrpTxtPadraoDiags == null || aelGrpTxtPadraoDiags.getSeq() == null){
			throw new IllegalArgumentException("Grupo Texto Padrão Diagnóstico deve ser informado.");
		}
		criteria.add(Restrictions.eq(AelTextoPadraoDiags.Fields.AEL_GRP_TXT_PADRAO_DIAGS_SEQ.toString(), aelGrpTxtPadraoDiags.getSeq()));
		
		criteria.setProjection(Projections.max(AelTextoPadraoDiags.Fields.SEQP.toString()));
		
		Short seq = (Short) executeCriteriaUniqueResult(criteria);
		
		if (seq == null) {
			seq = 1;
		} else {
			seq ++;
		}
		return new AelTextoPadraoDiagsId(aelGrpTxtPadraoDiags.getSeq(), seq);
	}
}
