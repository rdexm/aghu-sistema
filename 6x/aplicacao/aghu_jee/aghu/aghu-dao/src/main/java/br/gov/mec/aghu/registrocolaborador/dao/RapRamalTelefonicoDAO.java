package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @modulo registrocolaborador.cadastrosbasicos
 *
 */
public class RapRamalTelefonicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapRamalTelefonico> {

	private static final long serialVersionUID = 3966825642005740305L;

	protected RapRamalTelefonicoDAO() {
	}
	
	public List<RapRamalTelefonico> pesquisarRamalTelefonico(Integer numeroRamal,
			String indUrgencia, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {

		DetachedCriteria criteria = montarConsulta(numeroRamal, indUrgencia);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}
	
	public Long pesquisarRamalTelefonicoCount(Integer numeroRamal,
			String indUrgencia) {

		DetachedCriteria criteria = montarConsulta(numeroRamal, indUrgencia);

		return executeCriteriaCount(criteria);
	}	
	
	/**
	 * Pesquisar ramais telefonicos pelo numero do ramal
	 * @param paramPesquisa
	 * @return
	 */
	public List<RapRamalTelefonico> pesquisarRamalTelefonicoPorNroRamal(Object paramPesquisa){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RapRamalTelefonico.class);
		
		if (StringUtils.isNotBlank((String) paramPesquisa)){
			if(CoreUtil.isNumeroInteger(paramPesquisa)){
				Integer integerPesquisa = Integer.valueOf((String) paramPesquisa);
				criteria.add(Restrictions.eq(RapRamalTelefonico.Fields.NUMERORAMAL.toString(),
						integerPesquisa));				
			}
		}
		
		return executeCriteria(criteria);
	}	
	
	private DetachedCriteria montarConsulta(Integer numeroRamal, String indUrgencia) {

		RapRamalTelefonico example = new RapRamalTelefonico();

		if (indUrgencia != null) {
			example.setIndUrgencia(DominioSimNao.valueOf(StringUtils.trimToNull(indUrgencia)));
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(RapRamalTelefonico.class);
		criteria.add(Example.create(example).enableLike(MatchMode.ANYWHERE).ignoreCase());

		if (numeroRamal != null) {
			criteria.add(Restrictions.eq(RapRamalTelefonico.Fields.NUMERORAMAL.toString(), numeroRamal));
		}
		
		return criteria;
	}
}
