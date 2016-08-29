package br.gov.mec.aghu.parametrosistema.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghModulosParametros;

public class AghModulosParametrosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghModulosParametros> {

	private static final long serialVersionUID = 1755379207344022836L;

	public List<AghModulosParametros> pesquisarPorPsiSeqModulo(String modulo, Integer paramSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghModulosParametros.class);
		criteria.add(Restrictions.eq(AghModulosParametros.Fields.PSI_SEQ.toString(), paramSeq));
		if (modulo != null) {
			criteria.add(Restrictions.eq(AghModulosParametros.Fields.MODULO.toString(), modulo));
		}
		return executeCriteria(criteria);
	}

	public List<AghModulosParametros> pesquisarDependenciaModuloParametro(Integer paramSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghModulosParametros.class);
		criteria.add(Restrictions.eq(AghModulosParametros.Fields.PSI_SEQ.toString(), paramSeq));
		return executeCriteria(criteria);
	}
}
