package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatTipoClassifSecSaude;

public class FatTipoClassifSecSaudeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatTipoClassifSecSaude> {

	private static final long serialVersionUID = 6688784985424862887L;

	public FatTipoClassifSecSaude buscarPrimeiraTipoClassifSecSaude(String codigoSus, DominioSituacao situacaoRegistro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTipoClassifSecSaude.class);

		criteria.add(Restrictions.ilike(FatTipoClassifSecSaude.Fields.CODIGO_SUS.toString(), codigoSus, MatchMode.EXACT));

		criteria.add(Restrictions.eq(FatTipoClassifSecSaude.Fields.IND_SITUACAO_REGISTRO.toString(), situacaoRegistro));

		List<FatTipoClassifSecSaude> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

}
