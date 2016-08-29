package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpaUsoOrdProcedimento;

public class MpaUsoOrdProcedimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaUsoOrdProcedimento>{

	private static final long serialVersionUID = 988949175746229475L;

	/**
	 * Método que pesquisa usoOrdProcedimentos pelo medicamento da prescrição
	 * @param seq
	 * @param atdSeq
	 * @return
	 */
	public List<MpaUsoOrdProcedimento> pesquisarUsoOrdProcedimentosPorPrescricaoMedicamento(Long seq, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpaUsoOrdProcedimento.class);
		criteria.add(Restrictions.eq(MpaUsoOrdProcedimento.Fields.PPR_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpaUsoOrdProcedimento.Fields.PPR_SEQ.toString(), seq));
			
		return executeCriteria(criteria);
	}
	
}