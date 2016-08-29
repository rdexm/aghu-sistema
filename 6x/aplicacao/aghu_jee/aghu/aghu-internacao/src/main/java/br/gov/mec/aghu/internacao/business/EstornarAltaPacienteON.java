package br.gov.mec.aghu.internacao.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EstornarAltaPacienteON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(EstornarAltaPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4295361825683431386L;

	public enum EstornarAltaPacienteExceptionCode implements BusinessExceptionCode {
		ESTORNO_PARA_PACIENTE_JA_REALIZADO;
	}

	public List<AinInternacao> pesquisaInternacoesParaEstornarAltaPaciente(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Integer prontuario) {

		return getAinInternacaoDAO().pesquisaInternacoesParaEstornarAltaPaciente(firstResult, maxResult, orderProperty, asc,
				prontuario);
	}

	public Long pesquisaInternacoesParaEstornarAltaPacienteCount(Integer prontuario) {
		return getAinInternacaoDAO().pesquisaInternacoesParaEstornarAltaPacienteCount(prontuario);
	}

	public void verificaPacienteInternado(Integer intSeq) throws ApplicationBusinessException {
		Boolean retorno = getAinInternacaoDAO().verificaPacienteInternado(intSeq);
		if (retorno == null || retorno.equals(true)) {
			throw new ApplicationBusinessException(EstornarAltaPacienteExceptionCode.ESTORNO_PARA_PACIENTE_JA_REALIZADO);
		}
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}
}
