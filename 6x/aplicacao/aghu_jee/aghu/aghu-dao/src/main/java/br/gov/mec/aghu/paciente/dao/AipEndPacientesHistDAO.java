package br.gov.mec.aghu.paciente.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AipEndPacientesHist;

public class AipEndPacientesHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipEndPacientesHist> {

	private static final long serialVersionUID = -18058524568136975L;

	public List<AipEndPacientesHist> pesquisarHistoricoEnderecoPaciente(Integer codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipEndPacientesHist.class);
		criteria.add(Restrictions.eq(AipEndPacientesHist.Fields.PAC_CODIGO.toString(), codigo));

		return executeCriteria(criteria);
	}

}
