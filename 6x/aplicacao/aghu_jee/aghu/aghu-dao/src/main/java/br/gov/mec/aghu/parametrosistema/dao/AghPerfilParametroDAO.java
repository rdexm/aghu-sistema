package br.gov.mec.aghu.parametrosistema.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghModulosParametros;
import br.gov.mec.aghu.model.AghPerfilParametro;

public class AghPerfilParametroDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghPerfilParametro> {

	private static final long serialVersionUID = -1772314777950547935L;

	public List<AghModulosParametros> pesquisarDependenciaPerfilParametro(Integer paramSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghPerfilParametro.class);
		criteria.add(Restrictions.eq(AghPerfilParametro.Fields.PSI_SEQ.toString(), paramSeq));
		return executeCriteria(criteria);

	}
}
