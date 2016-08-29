package br.gov.mec.aghu.certificacaodigital.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghDocumento;

public class AghDocumentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghDocumento> {


	private static final long serialVersionUID = -8045621356011936723L;

	public List<AghDocumento> buscarDocumentosPeloAtendimento(Integer seqAtendimento) {
		
		DetachedCriteria criteria = DetachedCriteria
		.forClass(AghDocumento.class);
		criteria.add(Restrictions.eq(AghDocumento.Fields.ATD_SEQ
				.toString(), seqAtendimento));

		return executeCriteria(criteria);
	}	
	
	/**
	 * #39011 - Seqs dos documentos atendidos
	 * @param seqAtendimento
	 * @return
	 */
	public List<Integer> buscarSeqDocumentosAtendidos(Integer seqAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghDocumento.class);
		criteria.add(Restrictions.eq(AghDocumento.Fields.ATD_SEQ.toString(), seqAtendimento));
		Projection p = Projections.property(AghDocumento.Fields.SEQ.toString());
		criteria.setProjection(p);
		return executeCriteria(criteria);
	}	

}
