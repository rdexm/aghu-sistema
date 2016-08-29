package br.gov.mec.aghu.emergencia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.MamMotivoAtendimento;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamMotivoAtendimentosDAO extends BaseDao<MamRegistro> {

	private static final long serialVersionUID = 824892726430400298L;
	
	
	public MamMotivoAtendimento obterRegistroPendenteValidadoPorTriagem(Long trgSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(
				MamMotivoAtendimento.class, "mta");
		criteria.add(Restrictions.eq("mta."	+ MamMotivoAtendimento.Fields.TRG_SEQ.toString(), trgSeq));
		criteria.add(Restrictions.eq("mta."	+ MamMotivoAtendimento.Fields.PENDENTE.toString(), DominioIndPendenteAmbulatorio.V));
		criteria.add(Restrictions.isNull("mta."	+ MamMotivoAtendimento.Fields.DTHR_VALIDA_MVTO.toString()));
		return (MamMotivoAtendimento) this.executeCriteriaUniqueResult(criteria);
	}
	
}
