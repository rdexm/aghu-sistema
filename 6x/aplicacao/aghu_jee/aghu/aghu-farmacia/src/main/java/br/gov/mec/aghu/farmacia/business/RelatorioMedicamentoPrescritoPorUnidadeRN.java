package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class RelatorioMedicamentoPrescritoPorUnidadeRN extends BaseBusiness implements Serializable{

	private static final Log LOG = LogFactory.getLog(RelatorioMedicamentoPrescritoPorUnidadeRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ISiconFacade siconFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 975841984849592974L;

	/**
	 * @ORADB scec_busca_vlr_unit
	 * @param material
	 * @param dataCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
public BigDecimal obterCustoMedioPonderado (ScoMaterial material, Date dataCompetencia) throws ApplicationBusinessException{
		
		AghParametros parametro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);//(nome)buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_HCPA);
		Integer frnNumero = parametro.getVlrNumerico().intValue();
		if(dataCompetencia == null){
			dataCompetencia = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_COMPETENCIA).getVlrData();
		}
		Double custoMedioPonderado = getEstoqueFacade().getCustoMedioPonderado(material.getCodigo(), dataCompetencia, frnNumero);
		
		return new BigDecimal(custoMedioPonderado);
		
	}
	
	public BigDecimal obterCustoMedioPonderado (Integer codigoScoMaterial, Date dataCompetencia) throws ApplicationBusinessException{
		ScoMaterial material = getComprasFacade().obterScoMaterial(codigoScoMaterial);
		return obterCustoMedioPonderado(material, dataCompetencia);
	}
	
	private IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected ISiconFacade getSiconFacade(){
		return siconFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
}
