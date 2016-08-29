package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AinDocsInternacao;

public class AinDocsInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinDocsInternacao> {

	private static final long serialVersionUID = 9044508582225640744L;

	/**
	 * 
	 * Numero de documentos pendentes de uma determinada internacao.
	 * 
	 * @param intSeq
	 *            - id da internacao
	 */
	public Long countDocumentosPendentes(Integer intSeq) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinDocsInternacao.class);

		cri.add(Restrictions.eq(AinDocsInternacao.Fields.IND_DOC_ENTREGUE.toString(), DominioSimNao.N));
		cri.add(Restrictions.eq(AinDocsInternacao.Fields.ID_INTERNACAO_SEQUENCE.toString(), intSeq));

		cri.setProjection(Projections.rowCount());
		return (Long) this.executeCriteriaUniqueResult(cri);
	}

	public List<AinDocsInternacao> pesquisarPorInternacaoConvenio(Integer seqInternacao, Short codigoConvenioSaudePaciente,
			Byte seqConvenioSaudePaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinDocsInternacao.class);
		criteria.add(Restrictions.eq(AinDocsInternacao.Fields.ID_INTERNACAO_SEQUENCE.toString(), seqInternacao));
		criteria.add(Restrictions.eq(AinDocsInternacao.Fields.ID_CTD_CSP_CODIGO_CONVENIO.toString(), codigoConvenioSaudePaciente));
		criteria.add(Restrictions.eq(AinDocsInternacao.Fields.ID_CTD_CSP_SEQUENCE.toString(), seqConvenioSaudePaciente));
		return executeCriteria(criteria);
	}
}
