package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatRegistro;

public class FatRegistrosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatRegistro> {

	private static final long serialVersionUID = 1999470682101015947L;
	
	public List<FatRegistro> buscaListaRegistrosAtivos() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatRegistro.class);
		criteria.add(Restrictions.eq(FatRegistro.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		return executeCriteria(criteria);
	}
	
}