package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelDescricoesResulPadrao;
import br.gov.mec.aghu.model.AelResultadoPadraoCampoId;

public class AelDescricoesResulPadraoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDescricoesResulPadrao> {
	
	private static final long serialVersionUID = -2096009218305120383L;

	@Override
    protected void obterValorSequencialId(AelDescricoesResulPadrao elemento) {
       
        if (elemento == null || elemento.getResultadoPadraoCampo() == null) {
            throw new IllegalArgumentException("Parametro Invalido!!!");
        }
   
        AelResultadoPadraoCampoId id = new AelResultadoPadraoCampoId();
        id.setRpaSeq(elemento.getResultadoPadraoCampo().getId().getRpaSeq());
        id.setSeqp(elemento.getResultadoPadraoCampo().getId().getSeqp());
       
        elemento.setId(id);
    }


	
	
	public AelDescricoesResulPadrao obterAelDescricoesResulPadraoPorID(Integer rpcRpaSeq, Integer seqP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDescricoesResulPadrao.class);
		criteria.add(Restrictions.eq(AelDescricoesResulPadrao.Fields.RPC_RPA_SEQ.toString(), rpcRpaSeq));
		criteria.add(Restrictions.eq(AelDescricoesResulPadrao.Fields.RPC_SEQP.toString(), seqP));
		
		List<AelDescricoesResulPadrao> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
		
	}
		
	/**
	 * Obtém AelDescricoesResulPadraopor id
	 * @param rpaSeq
	 * @param seqp
	 * @return
	 */
	public AelDescricoesResulPadrao obterAelDescricoesResulPadraoPorId(Integer rpaSeq, Integer seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDescricoesResulPadrao.class);
		criteria.add(Restrictions.eq(AelDescricoesResulPadrao.Fields.RPC_RPA_SEQ.toString(), rpaSeq));
		criteria.add(Restrictions.eq(AelDescricoesResulPadrao.Fields.RPC_SEQP.toString(), seqp));
		return (AelDescricoesResulPadrao) executeCriteriaUniqueResult(criteria);
	}

}
