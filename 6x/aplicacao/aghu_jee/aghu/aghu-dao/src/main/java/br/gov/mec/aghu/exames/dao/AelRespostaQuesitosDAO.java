package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelRespostaQuesitos;

public class AelRespostaQuesitosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelRespostaQuesitos> {

	private static final long serialVersionUID = 942464816067541433L;

	public List<AelRespostaQuesitos> listarRespostasQuisitorPorDroPpjPacCodigo(Integer droPpjPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuesitos.class);

		criteria.add(Restrictions.eq(AelRespostaQuesitos.Fields.DRO_PPJ_PAC_CODIGO.toString(), droPpjPacCodigo));

		return executeCriteria(criteria);
	}

}
