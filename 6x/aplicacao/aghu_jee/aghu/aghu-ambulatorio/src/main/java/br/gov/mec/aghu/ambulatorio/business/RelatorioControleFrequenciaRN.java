package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioControleFrequenciaVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioControleFrequenciaRN extends BaseBusiness{


	/**
	 * 
	 */
	private static final long serialVersionUID = -6194586713198193379L;

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final Log LOG = LogFactory.getLog(RelatorioControleFrequenciaRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}

	public List<RelatorioControleFrequenciaVO> pesquisarControleFrequencia() {
		//parâmetros: lista de pacientes selecionados, nroProntuario, nroConsulta
		
		
		// TODO Auto-generated method stub
		//chamar todas as consultas e montar somente 1
		return null;
	}
	
	/**
	 *ORADB: CF_TTR_CODIGOFORMULA
	 * #42801
	 * 
	 * @param Integer nroProntuario
	 * @return String codigoProcedimento || prefixoTransplante
	 */
	public String obterCodigoFormulaPaciente(Integer nroProntuario){
		String codigo = null, nomeTransplante = "TRANSPLANTE DE ";
		
		codigo = faturamentoFacade.pesquisarCodigoFormulaPaciente(nroProntuario);
		
		if (codigo != null) {
			return nomeTransplante + codigo;
		} else {
			return null;
		}
	}
	
	/**
	 * ORADB: CF_DATA_REFFORMULA
	 * #42801 
	 * 
	 * @return String mesAnoReferencia
	 */
	public String obterMesAnoAtual(Integer nroConsulta){
		AacConsultas aacConsultas= aacConsultasDAO.obterConsulta(nroConsulta);
		return (DateUtil.obterDataFormatada(aacConsultas.getDtConsulta(), "MMMM'/'YYYY")).toUpperCase();
	}
	/**
	 *ORADB: CF_1FORMULA
	 *#42801
	 * 
	 * @param Integer nroConsulta
	 * @return String dataLocal
	 */
	public String obterDataLocalFormula(Integer nroConsulta){
		AacConsultas aacConsultas= aacConsultasDAO.obterConsulta(nroConsulta);
		return "Porto Alegre, ".concat(DateUtil.obterDataFormatada(aacConsultas.getDtConsulta(), "dd ' de ' MMMM ' de ' YYYY"));
	}
	
}
