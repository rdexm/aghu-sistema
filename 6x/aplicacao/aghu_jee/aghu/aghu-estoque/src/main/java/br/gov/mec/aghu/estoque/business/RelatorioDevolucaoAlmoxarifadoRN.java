package br.gov.mec.aghu.estoque.business;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.vo.RelatorioDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioDevolucaoAlmoxarifadoRN extends BaseBusiness {

@EJB
private RelatorioDevolucaoAlmoxarifadoON relatorioDevolucaoAlmoxarifadoON;
@EJB
private SceMovimentoMaterialRN sceMovimentoMaterialRN;

private static final Log LOG = LogFactory.getLog(RelatorioDevolucaoAlmoxarifadoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7587625304925435415L;
	
	protected RelatorioDevolucaoAlmoxarifadoON getRelatorioDevolucaoAlmoxarifadoON() {
		return relatorioDevolucaoAlmoxarifadoON;
	}
	
	protected SceMovimentoMaterialRN getSceMovimentoMaterialRN() {
		return sceMovimentoMaterialRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	/**
	 * @param numeroDevAlmox
	 * @return
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public List<RelatorioDevolucaoAlmoxarifadoVO> gerarDadosRelatorioDevolucaoAlmoxarifado(Integer numeroDevAlmox) throws ApplicationBusinessException {
		List<RelatorioDevolucaoAlmoxarifadoVO> result = getRelatorioDevolucaoAlmoxarifadoON().gerarDadosRelatorioDevolucaoAlmoxarifado(numeroDevAlmox);
		
		if (!result.isEmpty()) {
			AghParametros dtCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
			
			Double valorTotal = new Double(0);
			
			for (RelatorioDevolucaoAlmoxarifadoVO vo : result) {
				//ORADB - c_custo_medio
				vo.setValor(getSceMovimentoMaterialRN().obterValorMaterialNoMovimento(
						vo.getSeq(), dtCompetencia.getVlrData(), vo.getMatCodigo(),
						vo.getTmvSeq(), vo.getTmvComplemento(), vo.getNumeroFornecedor()));
				valorTotal += vo.getValor();
			}
			
			result.get(0).setValorTotal(valorTotal);
		}
		
		return result;
	}
	
	public String gerarCsvRelatorioDevolucaoAlmoxarifado(Integer numeroDevolAlmox, List<RelatorioDevolucaoAlmoxarifadoVO> dados, Map<String, Object> parametros) throws IOException {
		return getRelatorioDevolucaoAlmoxarifadoON().gerarCsvRelatorioDevolucaoAlmoxarifado(numeroDevolAlmox, dados, parametros);
	}
	
	public String efetuarDownloadCsvRelatorioDevolucaoAlmoxarifado(Integer numeroDA) {
		return getRelatorioDevolucaoAlmoxarifadoON().nameHeaderEfetuarDownloadCsvRelatorioDevolucaoAlmoxarifado(numeroDA);
	}

}
