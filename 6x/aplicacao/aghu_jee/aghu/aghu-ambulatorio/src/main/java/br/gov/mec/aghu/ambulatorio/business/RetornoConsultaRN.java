package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacRetornosDAO;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RetornoConsultaRN extends BaseBusiness {

	private static final long serialVersionUID = 8080361518353282721L;
	
	private static final Log LOG = LogFactory.getLog(RetornoConsultaRN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AacRetornosDAO aacRetornosDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private RetornoConsultaON retornoConsultaON;
	
	public enum RetornoConsultaRNExceptionCode implements BusinessExceptionCode {

		MENSAGEM_DEPENDENCIAS_RETORNO_CONSULTA,MENSAGEM_RETORNO_CONSULTA_EXISTENTE;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	public void removerRetornoConsulta(Integer seq) throws ApplicationBusinessException {
		final AacRetornos retornoConsulta = aacRetornosDAO.obterPorChavePrimaria(seq);
		
		if (retornoConsulta == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		if(verificarVinculacaoConsulta(retornoConsulta)){
			throw new ApplicationBusinessException(RetornoConsultaRNExceptionCode.MENSAGEM_DEPENDENCIAS_RETORNO_CONSULTA);
			
		} else {
			this.aacRetornosDAO.remover(retornoConsulta);
		}
	}

	private boolean verificarVinculacaoConsulta(AacRetornos retornoConsulta) {
		return (this.aacConsultasDAO.existeDependenciaComRetorno(retornoConsulta) >0);
	}


	public void persistirRegistro(AacRetornos retornoConsulta, DominioSimNao dominioSimNao, DominioSituacao situacao, DominioIndAbsenteismo dominioIndAbsenteismo) throws ApplicationBusinessException{
		retornoConsulta.setAbsenteismo(dominioIndAbsenteismo);
		retornoConsulta.setFaturaSus(retornoConsultaON.verificarCampoFaturaSus(retornoConsulta));
		retornoConsulta.setSituacao(situacao);
		
		if (verificarCodigoRetorno(retornoConsulta)) {
			throw new ApplicationBusinessException(RetornoConsultaRNExceptionCode.MENSAGEM_RETORNO_CONSULTA_EXISTENTE);
		}else {
			aacRetornosDAO.persistir(retornoConsulta);
		}
	}

	public void atualizarRegistro(AacRetornos retornoConsulta,  DominioSituacao situacao, DominioIndAbsenteismo dominioIndAbsenteismo)  throws ApplicationBusinessException{
		retornoConsulta.setAbsenteismo(dominioIndAbsenteismo);
		retornoConsulta.setSituacao(situacao);
		aacRetornosDAO.merge(retornoConsulta);
	}

	private boolean verificarCodigoRetorno(AacRetornos situacaoConsulta) {
		List<AacRetornos> pesquisarPorNomeCodigoAtivo = aacRetornosDAO.pesquisarPorNomeCodigoAtivo(String.valueOf(situacaoConsulta.getSeq()));
		return (pesquisarPorNomeCodigoAtivo != null && pesquisarPorNomeCodigoAtivo.size() > 0);
	}
}
