package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapInstituicaoQualificadora;
import br.gov.mec.aghu.registrocolaborador.dao.RapQualificacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class InstituicaoQualificadoraRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(InstituicaoQualificadoraRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private RapQualificacaoDAO rapQualificacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7490423109009814372L;

	public enum InstituicaoQualificadoraRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_INSTITUICAO_QUALIFICADORA_NAO_INFORMADA, MENSAGEM_INSTITUICAO_QUALIFICADORA_UTILIZADA_QUALIFICACAO;
	}

	public void validaExclusaoInstituicaoQualificadora(
			RapInstituicaoQualificadora instituicaoQualificadora) throws ApplicationBusinessException {
		if (instituicaoQualificadora == null
				|| instituicaoQualificadora.getCodigo() == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		Long registros = this.getRapQualificacaoDAO().pesquisarQualificacoesCount(instituicaoQualificadora);
		logDebug("InstituicaoQualificadoraRN.validaExclusaoInstituicaoQualificadora(): qualificacoes para a instituicao ["
				+ instituicaoQualificadora.getCodigo()
				+ "]: ["
				+ registros + "].");

		if (registros > 0) {
			throw new ApplicationBusinessException(
					InstituicaoQualificadoraRNExceptionCode.MENSAGEM_INSTITUICAO_QUALIFICADORA_UTILIZADA_QUALIFICACAO);
		}
	}

	protected RapQualificacaoDAO getRapQualificacaoDAO() {
		return rapQualificacaoDAO;
	}

}
