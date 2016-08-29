package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghDocumentosAssinados;

public class AghDocumentosAssinadosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghDocumentosAssinados> {

	private static final long serialVersionUID = -1232687443170781698L;

	public List<AghDocumentosAssinados> listarDocAssPorSeqCirurgiaDocAssinadoIsNotNull(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghDocumentosAssinados.class);
		
		criteria.add(Restrictions.eq(AghDocumentosAssinados.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.isNotNull(AghDocumentosAssinados.Fields.DOC_ASSINADO.toString()));

		return executeCriteria(criteria);
	}

}
