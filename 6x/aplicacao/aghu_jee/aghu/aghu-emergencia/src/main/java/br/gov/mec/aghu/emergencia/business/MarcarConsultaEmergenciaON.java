package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.configuracao.vo.EspecialidadeFiltro;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;


@Stateless
public class MarcarConsultaEmergenciaON extends BaseBusiness {

	private static final long serialVersionUID = -8969383342648203269L;

	private static final Log LOG = LogFactory.getLog(MarcarConsultaEmergenciaON.class);
	
	@Inject
	private IConfiguracaoService configuracaoService;
	
	@Inject
	private IAmbulatorioService ambulatorioService;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum MarcarConsultaEmergenciaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL
	}

    public List<Especialidade> pesquisarEspecialidadeListaSeq(List<Short> listaEspId, Object objPesquisa) {
		
		String auxParam = (String) objPesquisa;
		EspecialidadeFiltro especialidadeFiltro = new EspecialidadeFiltro();		
		
		if (CoreUtil.isNumeroShort(auxParam)){
			especialidadeFiltro.setSeq(Short.valueOf(auxParam));
		}
		else {
			especialidadeFiltro.setNomeEspecialidade(auxParam);
		}
		List<Especialidade>  listaRetorno = new ArrayList<Especialidade>();
		
		try {
			listaRetorno = this.configuracaoService.pesquisarEspePorNomeSiglaListaSeq(listaEspId, especialidadeFiltro);
			
		} catch (Exception e) {
			
			LOG.error("SERVICE: " + e.getMessage(), e);
		}
		return listaRetorno;
	}
    
    public Long pesquisarEspecialidadeListaSeqCount(List<Short> listaEspId, Object objPesquisa) {
		
		String auxParam = (String) objPesquisa;
		EspecialidadeFiltro especialidadeFiltro = new EspecialidadeFiltro();		
		
		if (CoreUtil.isNumeroShort(auxParam)){
			especialidadeFiltro.setSeq(Short.valueOf(auxParam));
		}
		else {
			especialidadeFiltro.setNomeEspecialidade(auxParam);
		}
		Long retornoCount = Long.valueOf(0);
		
		try {
			retornoCount = this.configuracaoService.pesquisarEspePorNomeSiglaListaSeqCount(listaEspId, especialidadeFiltro);			
		} catch (Exception e) {
			
			LOG.error("SERVICE: " + e.getMessage(), e);
		}
		return retornoCount;
	}
    
    public Especialidade obterEspecialidadePorSeq(Short espSeq) throws ApplicationBusinessException {
    	Especialidade result = null;
    	
    	try {
			result = this.configuracaoService.obterEspecialidadePorSeq(espSeq);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MarcarConsultaEmergenciaONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return result;
    }
    
    public Short obterUnidadeAssociadaAgendaPorNumeroConsulta(Integer conNumero){
    	return this.ambulatorioService.obterUnidadeAssociadaAgendaPorNumeroConsulta(conNumero);
    }
}
