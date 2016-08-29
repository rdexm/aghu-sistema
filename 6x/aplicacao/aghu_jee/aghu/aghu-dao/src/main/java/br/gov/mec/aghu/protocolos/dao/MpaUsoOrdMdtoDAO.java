package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpaUsoOrdMdto;

public class MpaUsoOrdMdtoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaUsoOrdMdto>{

	private static final long serialVersionUID = 5126731527092061867L;

	/**
	 * Método que pesquisa UsosOrdMdtos por medicamento
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public List<MpaUsoOrdMdto> pesquisarUsosOrdMdtos(Integer atdSeq, Long seq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpaUsoOrdMdto.class);
		criteria.add(Restrictions.eq(MpaUsoOrdMdto.Fields.PMD_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(MpaUsoOrdMdto.Fields.PMD_SEQ.toString(), seq));
		
		return executeCriteria(criteria);
	}

}
