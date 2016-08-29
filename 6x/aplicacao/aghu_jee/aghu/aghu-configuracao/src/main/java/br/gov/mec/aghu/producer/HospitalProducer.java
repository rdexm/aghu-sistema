package br.gov.mec.aghu.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.HUsEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.moduleintegration.HospitalQualifier;
import br.gov.mec.aghu.core.persistence.dao.DataAccessService;

/**
 * Essa classe tem como objetivo retornar se o AGHU está sendo executado no HCPA.
 * Usado para testar no sistema através dos métodos isHCPA()
 * Originalmente usado nas classes BaseDao e BaseBusiness
 * 
 * @author twickert
 *
 */
@ApplicationScoped
public class HospitalProducer {

 	@Inject
	private DataAccessService dataAccessService;
	
	private String nomeHospital = null;
	
	@Produces @HospitalQualifier
	public Boolean registrarModulosAtivos() {

		
		if (nomeHospital == null) {
			nomeHospital = obterAghParametroPorNome(AghuParametrosEnum.P_AGHU_PARAMETRO_HU.toString()).getVlrTexto();
		}
		return HUsEnum.HCPA.toString().equals(nomeHospital);
	}
	
	/**
	 * Obtem um aghParametro por nome.
	 * Não é possível injetar uma DAO ou ON aqui porque daria circular injection
	 * 
	 * @param nome
	 * @return
	 */
	private AghParametros obterAghParametroPorNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);
		criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), nome));
	
		Criteria executableCriteria = dataAccessService.createExecutableCriteria(criteria);
		
		return (AghParametros) executableCriteria.uniqueResult();
	}
	
	
	

}