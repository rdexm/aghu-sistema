package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelDocResultadoExame;

public class AelDocResultadoExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelDocResultadoExame> {

	private static final long serialVersionUID = -6866142153523452376L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelDocResultadoExame.class);
    }
	
	/**
	 * Verifica a existência de documento anexado sem informações de anulação (IND_ANULACAO_DOC) através do item de solicitação
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public boolean existeDocumentosAnexadosNaoAnulados(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelDocResultadoExame.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelDocResultadoExame.Fields.ISE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(AelDocResultadoExame.Fields.IND_ANULACAO_DOC.toString(), false));
		return (executeCriteriaCount(criteria) > 0);
	}
	

	/**
	 * Verifica a existência de documento anexado sem informações de anulação (IND_ANULACAO_DOC e DATA_HORA_ANULACAO_DOC) através do item de solicitação
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public Boolean existeDocumentoAnexado(Integer iseSoeSeq, Short iseSeqp) {
		return obterDocumentoAnexado(iseSoeSeq, iseSeqp) != null;
	}
	
	/**
	 * Obtém documento anexado sem informações de anulação (IND_ANULACAO_DOC ou DATA_HORA_ANULACAO_DOC) através do item de solicitação
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public AelDocResultadoExame obterDocumentoAnexado(Integer iseSoeSeq, Short iseSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelDocResultadoExame.class);
		
		criteria.add(Restrictions.eq(AelDocResultadoExame.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelDocResultadoExame.Fields.ISE_SEQP.toString(), iseSeqp));
		
		Criterion lhs = Restrictions.eq(AelDocResultadoExame.Fields.IND_ANULACAO_DOC.toString(), Boolean.FALSE);
		Criterion rhs = Restrictions.isNull(AelDocResultadoExame.Fields.DATA_HORA_ANULACAO_DOC.toString());
		Criterion or = Restrictions.or(lhs, rhs);
		criteria.add(or);

		return (AelDocResultadoExame) this.executeCriteriaUniqueResult(criteria);
	}

}
