package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MptControleDispensacao;

public class MptControleDispensacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptControleDispensacao> {

	private static final long serialVersionUID = 5692626474714123346L;


	public List<MptControleDispensacao> pesquisarControlesDispensacaoPorParam(
			Integer pteAtdSeq, Integer pteSeq, Short unfSeq, String indImpPrescricao) {
		DetachedCriteria criteria = obterControlesDispensacoesPorParam(
				pteAtdSeq, pteSeq, unfSeq);
		criteria.add(Restrictions.eq(MptControleDispensacao.Fields.IND_IMP_PRESCRICAO.toString(), indImpPrescricao));
				
		return executeCriteria(criteria);
	}


	private DetachedCriteria obterControlesDispensacoesPorParam(
			Integer pteAtdSeq, Integer pteSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptControleDispensacao.class);
		
		criteria.add(Restrictions.eq(MptControleDispensacao.Fields.PTE_ATD_SEQ.toString(), pteAtdSeq));
		criteria.add(Restrictions.eq(MptControleDispensacao.Fields.PTE_SEQ.toString(), pteSeq));
		criteria.add(Restrictions.eq(MptControleDispensacao.Fields.UNF_SEQ_SOLICITANTE.toString(), unfSeq));
		return criteria;
	}
	
	public List<MptControleDispensacao> pesquisarControlesDispensacao(Integer pteAtdSeq, 
			Integer pteSeq, Short unfSeqSolicitante, Short unfSeq){
		DetachedCriteria criteria = obterControlesDispensacoesPorParam(
				pteAtdSeq, pteSeq, unfSeqSolicitante);
		criteria.add(Restrictions.eq(MptControleDispensacao.Fields.UNF_SEQ.toString(), unfSeq));
		
		return executeCriteria(criteria);
	}
}