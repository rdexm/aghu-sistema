package br.gov.mec.aghu.perinatologia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoPlacar;

/**
 * @author marcelofilho
 *
 */
public class McoPlacarDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoPlacar>{

	private static final long serialVersionUID = -398552129893160443L;
	
	public McoPlacar persistirPlacar(McoPlacar placar) {
		if(super.contains(placar)) {
			placar = super.atualizar(placar);
		} else {
			super.persistir(placar);
		}
		flush();
		return placar;
	}

	/**
	 * @param codigoPaciente
	 * @return
	 */
	public McoPlacar buscarPlacar(Integer codigoPaciente) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoPlacar.class);
		criteria.add(Restrictions.eq(McoPlacar.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		
		return (McoPlacar) executeCriteriaUniqueResult(criteria);
	}

}
