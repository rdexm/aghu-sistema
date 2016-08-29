package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudOn;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.model.ScoMaterial;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class MaterialFarmaciaON extends AbstractAGHUCrudOn<ScoMaterial> {

@EJB
private MaterialFarmacialRN materialFarmacialRN;

	private static final Log LOG = LogFactory.getLog(MaterialFarmaciaON.class);

	@Inject
	private ScoMaterialDAO scoMaterialDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3061734649539636252L;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	@Override
	public ScoMaterialDAO getEntidadeDAO() {
		return scoMaterialDAO;
	}

	@Override
	public AbstractAGHUCrudRn<ScoMaterial> getRegraNegocio() {
		return materialFarmacialRN;
	}

	@Override
	public Object getChavePrimariaEntidade(ScoMaterial entidade) {
		
		return (entidade != null ? entidade.getCodigo() : null);
	}
	
	
}
