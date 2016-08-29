package br.gov.mec.aghu.exames.contratualizacao.business;

import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelArquivoIntegracao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author ebicca
 *
 */
@Stateless
public class PesquisaSolicitacoesArquivoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaSolicitacoesArquivoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}



	private static final long serialVersionUID = -4463186021817827192L;

	protected enum PesquisaSolicitacoesArquivoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_CAMPOS_NAO_PREENCHIDOS
	}
	
	public void validarFiltrosPesquisa(
			AelArquivoIntegracao aelArquivoIntegracao, String nomePaciente,
			Integer numeroSolicitacao) throws ApplicationBusinessException {
		nomePaciente = StringUtils.trimToNull(nomePaciente);
		if (aelArquivoIntegracao == null && (StringUtils.isEmpty(nomePaciente) || nomePaciente.length() < 3)
				&& numeroSolicitacao == null) {
			throw new ApplicationBusinessException(
					PesquisaSolicitacoesArquivoONExceptionCode.MENSAGEM_ERRO_CAMPOS_NAO_PREENCHIDOS);
		}

	}

}
