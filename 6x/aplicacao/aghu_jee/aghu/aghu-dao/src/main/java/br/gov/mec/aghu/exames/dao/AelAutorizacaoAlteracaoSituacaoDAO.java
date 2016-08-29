package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelAutorizacaoAlteracaoSituacao;
import br.gov.mec.aghu.model.AelMatrizSituacao;

public class AelAutorizacaoAlteracaoSituacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAutorizacaoAlteracaoSituacao> {
	
	
	private static final long serialVersionUID = -2208938656190970974L;

	public List<AelAutorizacaoAlteracaoSituacao> listarPorMatriz(AelMatrizSituacao matriz) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAutorizacaoAlteracaoSituacao.class);
	
		criteria.add(Restrictions.eq(AelAutorizacaoAlteracaoSituacao.Fields.MATRIZ_SEQ.toString(), matriz.getSeq()));
		
		return executeCriteria(criteria);
	}
	
}
