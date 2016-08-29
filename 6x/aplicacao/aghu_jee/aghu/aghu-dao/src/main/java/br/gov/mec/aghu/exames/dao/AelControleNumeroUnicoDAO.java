package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AelControleNumeroUnico;

public class AelControleNumeroUnicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelControleNumeroUnico> {
	
	
	private static final long serialVersionUID = 7542911845874505136L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelControleNumeroUnico.class);
    }
	
	/**
	 * .
	 * @param dthrNumeroUnico
	 * @param origem
	 * @return
	 */
	public List<AelControleNumeroUnico> obterPorDataNumeroUnicoEOrigem(Date dthrNumeroUnico, DominioOrigemAtendimento origem) {
		
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelControleNumeroUnico.Fields.DATA_NUMERO_UNICO.toString(), dthrNumeroUnico));
		criteria.add(Restrictions.eq(AelControleNumeroUnico.Fields.ORIGEM.toString(), origem));
		
		return executeCriteria(criteria);
	}

}
