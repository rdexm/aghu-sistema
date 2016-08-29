package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoModalidadeLicitacaoDAO;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoModalidadeLicitacaoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ScoModalidadeLicitacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoModalidadeLicitacaoDAO scoModalidadeLicitacaoDAO;

	private static final long serialVersionUID = -944274977024307152L;
	
	public enum ScoModalidadeLicitacaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG, MENSAGEM_MODALIDADE_PAC_DUPLICADO_M03, MENSAGEM_MODALIDADE_PAC_M04;
	}
	
	public void persistir(ScoModalidadeLicitacao scoModalidadePac)
			throws ApplicationBusinessException {

		if (scoModalidadePac == null) {
			throw new ApplicationBusinessException(ScoModalidadeLicitacaoRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.validaRN1(scoModalidadePac);
		this.validaRN2(scoModalidadePac);

		this.getScoModalidadeLicitacaoDAO().persistir(scoModalidadePac);
	}

	public void atualizar(ScoModalidadeLicitacao scoModalidadePac)
			throws ApplicationBusinessException {

		if (scoModalidadePac == null) {
			throw new ApplicationBusinessException(ScoModalidadeLicitacaoRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.validaRN2(scoModalidadePac);

		this.getScoModalidadeLicitacaoDAO().merge(scoModalidadePac);
	}
	
	/**
	 * @ORADB SCO_MODALIDADE_LICITACOES - SCOF_CAD_MOD_LICIT
	 * @throws ApplicationBusinessException
	 */
	public void validaRN1(ScoModalidadeLicitacao scoModalidadePac) throws ApplicationBusinessException {
		
		if (scoModalidadePac == null) {
			throw new ApplicationBusinessException(ScoModalidadeLicitacaoRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		ScoModalidadeLicitacao paramResult = this.getScoModalidadeLicitacaoDAO()
				.obterPorChavePrimaria(scoModalidadePac.getCodigo());
		if (paramResult != null) {
			throw new ApplicationBusinessException(ScoModalidadeLicitacaoRNExceptionCode.MENSAGEM_MODALIDADE_PAC_DUPLICADO_M03);
		}		
	}
	
	/**
	 * @ORADB SCO_MODALIDADE_LICITACOES - SCOT_MLC_BRI / SCOT_MLC_BRU
	 * @throws ApplicationBusinessException
	 */
	public void validaRN2(ScoModalidadeLicitacao scoModalidadePac) throws ApplicationBusinessException {
		
		if (scoModalidadePac == null) {
			throw new ApplicationBusinessException(ScoModalidadeLicitacaoRNExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		if((scoModalidadePac.getDocLicitAno() && !scoModalidadePac.getDocLicitacao()) || 
			(scoModalidadePac.getEditalAno() && !scoModalidadePac.getEdital())) {
			throw new ApplicationBusinessException(ScoModalidadeLicitacaoRNExceptionCode.MENSAGEM_MODALIDADE_PAC_M04);
		}
	}
	
	private ScoModalidadeLicitacaoDAO getScoModalidadeLicitacaoDAO() {
		return scoModalidadeLicitacaoDAO;
	}
}