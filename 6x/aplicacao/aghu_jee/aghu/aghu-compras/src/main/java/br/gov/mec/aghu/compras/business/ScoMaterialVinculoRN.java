package br.gov.mec.aghu.compras.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoMaterialVinculoDAO;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialVinculo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ScoMaterialVinculoRN extends BaseBusiness {
	
	private static final long serialVersionUID = 5537443901395824615L;
	
	@Inject
	private ScoMaterialVinculoDAO scoMaterialVinculoDAO;

	private static final Log LOG = LogFactory.getLog(ScoMaterialVinculoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private ScoMaterialVinculoDAO getScoMaterialVinculoDAO() {
		return scoMaterialVinculoDAO;
	}
	
	public void remover(ScoMaterialVinculo scoMaterialVinculo) throws ApplicationBusinessException {
		scoMaterialVinculo = getScoMaterialVinculoDAO().obterPorChavePrimaria(scoMaterialVinculo.getCodigo());
		getScoMaterialVinculoDAO().remover(scoMaterialVinculo);
		getScoMaterialVinculoDAO().flush();
	}
	
	public void persistirMaterialVinculado(ScoMaterial material, ScoMaterial materialVinculado){
		ScoMaterialVinculo materialVinculo = new ScoMaterialVinculo(material, materialVinculado);
		this.getScoMaterialVinculoDAO().persistir(materialVinculo);
		this.getScoMaterialVinculoDAO().flush();
	}
}
