package br.gov.mec.aghu.compras.parecer.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoAgrupamentoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoAgrupamentoMaterialON extends BaseBusiness {

@EJB
private ScoAgrupamentoMaterialRN scoAgrupamentoMaterialRN;

private static final Log LOG = LogFactory.getLog(ScoAgrupamentoMaterialON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	private static final long serialVersionUID = -2396749430008531741L;

	public enum ScoAgrupamentoMaterialONExceptionCode implements
			BusinessExceptionCode { MENSAGEM_PARAM_OBRIG; }

	/**
	 * Insere agrupamento material
	 * @param scoAgrupMaterial
	 * @author dilceia.alves
	 * @since 09/04/2013
	 * @throws ApplicationBusinessException 
	 */
	public void inserirAgrupamentoMaterial(ScoAgrupamentoMaterial scoAgrupMaterial)
			throws ApplicationBusinessException {

		if (scoAgrupMaterial == null) {
			throw new ApplicationBusinessException(ScoAgrupamentoMaterialONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoAgrupamentoMaterialRN().persistir(scoAgrupMaterial);
	}

	/**
	 * Altera agrupamento material pelo código
	 * @param scoAgrupMaterial
	 * @author dilceia.alves
	 * @since 09/04/2013
	 * @throws ApplicationBusinessException 
	 */
	public void alterarAgrupamentoMaterial(ScoAgrupamentoMaterial scoAgrupMaterial)
			throws ApplicationBusinessException {
		getScoAgrupamentoMaterialRN().atualizar(scoAgrupMaterial);
	}
	
	protected ScoAgrupamentoMaterialRN getScoAgrupamentoMaterialRN(){
		return scoAgrupamentoMaterialRN;
	}
}