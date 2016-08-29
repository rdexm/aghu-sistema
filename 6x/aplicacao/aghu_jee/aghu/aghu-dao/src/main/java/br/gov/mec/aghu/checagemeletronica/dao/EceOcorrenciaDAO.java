package br.gov.mec.aghu.checagemeletronica.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EceOcorrencia;
import br.gov.mec.aghu.model.VMpmOcorrenciaPrcr;

public class EceOcorrenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EceOcorrencia> {

	private static final long serialVersionUID = 4674340738534846937L;

	public List<VMpmOcorrenciaPrcr> buscarMpmOcorrenciaPrcr(final Integer atdSeq, final Date dthrMovimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmOcorrenciaPrcr.class);
		
		criteria.add(Restrictions.eq(VMpmOcorrenciaPrcr.Fields.ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.ge(VMpmOcorrenciaPrcr.Fields.ALTERADO_EM_OCO.toString(), dthrMovimento));
		
		return executeCriteria(criteria);
	}

}
