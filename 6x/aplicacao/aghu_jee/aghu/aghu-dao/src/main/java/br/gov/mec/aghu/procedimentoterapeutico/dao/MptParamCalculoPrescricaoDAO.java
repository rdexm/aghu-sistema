package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MptParamCalculoPrescricao;

public class MptParamCalculoPrescricaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptParamCalculoPrescricao> {

	private static final long serialVersionUID = -418547336843973404L;

	public List<MptParamCalculoPrescricao> pesquisarMptParamCalculoPrescricoes(Integer pesoPacCodigo, Integer alturaPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptParamCalculoPrescricao.class);

		if (pesoPacCodigo != null) {
			criteria.add(Restrictions.eq(MptParamCalculoPrescricao.Fields.PEP_PAC_CODIGO.toString(), pesoPacCodigo));
		}

		if (alturaPacCodigo != null) {
			criteria.add(Restrictions.eq(MptParamCalculoPrescricao.Fields.ATP_PAC_CODIGO.toString(), alturaPacCodigo));
		}

		return executeCriteria(criteria);
	}

}
