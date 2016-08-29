package br.gov.mec.aghu.paciente.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioStatusRelatorio;
import br.gov.mec.aghu.model.AipControleEscalaCirurgia;

/**
 * 
 * @author lalegre
 *
 */
public class AipControleEscalaCirurgiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipControleEscalaCirurgia> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6754583369287103104L;
	
	/**
	 * Verifica se ja existe escala definitiva para a mesma data e unidade funcional
	 * @param unidade
	 * @param dataCirurgia
	 * @param status
	 * @return
	 */
	public boolean existeEscalaDefinitiva(Short unidade, Date dataCirurgia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipControleEscalaCirurgia.class);
		
		criteria.add(Restrictions.eq(AipControleEscalaCirurgia.Fields.UNF_SEQ.toString(), unidade));
		criteria.add(Restrictions.eq(AipControleEscalaCirurgia.Fields.DATA_EXEC.toString(), dataCirurgia));
		criteria.add(Restrictions.eq(AipControleEscalaCirurgia.Fields.STATUS.toString(), DominioStatusRelatorio.D));
		
		return executeCriteriaCount(criteria) > 0;
		
	}
	
	/**
	 * Recupera a ultima execucao da escala parcial
	 * @param unidade
	 * @param dataCirurgia
	 * @return
	 */
	public Date recuperarMaxDataFim(Short unidade, Date dataCirurgia) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AipControleEscalaCirurgia.class);
		criteria.add(Restrictions.eq(AipControleEscalaCirurgia.Fields.UNF_SEQ.toString(), unidade));
		criteria.add(Restrictions.eq(AipControleEscalaCirurgia.Fields.DATA_EXEC.toString(), dataCirurgia));
		criteria.setProjection(Projections.max(AipControleEscalaCirurgia.Fields.DATA_FIM.toString()));
				
		return (Date) this.executeCriteriaUniqueResult(criteria);
		
	}

}
