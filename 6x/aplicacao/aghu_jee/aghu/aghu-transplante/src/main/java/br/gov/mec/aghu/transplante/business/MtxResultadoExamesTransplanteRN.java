package br.gov.mec.aghu.transplante.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxExameUltResults;
import br.gov.mec.aghu.transplante.dao.MtxExameUltResultsDAO;
import br.gov.mec.aghu.transplante.vo.TiposExamesPacienteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MtxResultadoExamesTransplanteRN extends BaseBusiness {

	private static final long serialVersionUID = 3103608347144796275L;
	/**
	 * #47146
	 * RN de transplante para estória 
	 */
	private static final Log LOG = LogFactory.getLog(MtxResultadoExamesTransplanteRN.class);
	
	@Inject 
	private AelExamesDAO aelExamesDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject	
	private MtxExameUltResultsDAO mtxExameUltResultsDAO;
	
	@Inject	
	private AelResultadoExameDAO aelResultadoExameDAO;

	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	// Executa 47146 - C3 para cada item da lista de C2
	public TiposExamesPacienteVO obterTiposExamesPaciente(AipPacientes paciente, MtxExameUltResults mtxExameUltResults) {
		String resultado = "";
		
		AelResultadoExameId id = aelExamesDAO.montarREETiposExamesPaciente(paciente.getCodigo(), mtxExameUltResults.getCampoLaudo().getSeq(), mtxExameUltResults.getAelExames().getSigla());
		if (id != null) {
			AelResultadoExame aelResultadoExame = aelResultadoExameDAO.obterPorChavePrimaria(id);
			
			resultado = examesFacade.obterResultadoExame(aelResultadoExame);
			if (resultado != null && !resultado.isEmpty() && resultado.length() > 24) {
				resultado = resultado.substring(0, 24);
			}
		}
		return aelExamesDAO.obterTiposExamesPaciente(paciente.getCodigo(), mtxExameUltResults.getCampoLaudo().getSeq(), mtxExameUltResults.getAelExames().getSigla(), resultado);
	}
	
	public Boolean verificarHcvReagente(Integer seqTransplante, AipPacientes paciente, MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException {
		String ps02, ps01;
		String resultado = null;
		
		ps01 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_SIGLA_HCV).getVlrTexto();
		ps02 = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_RESULTADO_EXAME_HCV).getVlrTexto();
		
		AelResultadoExame aelResultadoExame = aelExamesDAO.montarREEPCRReagente(ps01, paciente.getCodigo());
		if (aelResultadoExame != null) {
			aelResultadoExame = aelResultadoExameDAO.obterPorChavePrimaria(aelResultadoExame.getId());
			resultado = examesFacade.obterResultadoExame(aelResultadoExame);
			if (resultado != null && !resultado.isEmpty() && resultado.length() > 24) {
				resultado = resultado.substring(0, 24);
			}
		}
		
		if (resultado != null && resultado.equals(ps02)) {
			return true;
		}
		return false;
	}


	public List<TiposExamesPacienteVO> buscaUltimosResultados(AipPacientes paciente) {
		List<TiposExamesPacienteVO> ultimosResultadosList = new ArrayList<TiposExamesPacienteVO>();
		TiposExamesPacienteVO vo;
		
		// Executa 47146 - C2
		List<MtxExameUltResults> exameUltResultsList = mtxExameUltResultsDAO.obterTiposExames();
		
		// Executa 47146 - C3 para cada item da lista de C2
		for (MtxExameUltResults mtxExameUltResultsItem : exameUltResultsList) {
			vo = this.obterTiposExamesPaciente(paciente, mtxExameUltResultsItem);
			if (vo != null) {
				ultimosResultadosList.add(vo);
			}
		}
		
		return ultimosResultadosList;
	}
		
}
