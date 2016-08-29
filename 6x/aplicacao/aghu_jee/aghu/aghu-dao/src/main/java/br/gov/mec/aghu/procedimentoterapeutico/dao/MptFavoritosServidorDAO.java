package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MptFavoritosServidor;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MptFavoritosServidorDAO extends BaseDao<MptFavoritosServidor> {

	private static final long serialVersionUID = 7846911888847406268L;
	
	public List<MptFavoritosServidor> obterFavoritosTipoSessao(Integer matriculaServidor, Short vinCodigoServidor) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptFavoritosServidor.class);		
		criteria.add(Restrictions.eq(MptFavoritosServidor.Fields.MATRICULA_SERVIDOR.toString(), matriculaServidor));
		criteria.add(Restrictions.eq(MptFavoritosServidor.Fields.VIN_CODIGO_SERVIDOR.toString(), vinCodigoServidor));
		
		return executeCriteria(criteria);	
	}

}
