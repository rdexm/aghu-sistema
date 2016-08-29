package br.gov.mec.aghu.compras.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoModalidadeLicitacaoON extends BaseBusiness{

@EJB
private ScoModalidadeLicitacaoRN scoModalidadeLicitacaoRN;

private static final Log LOG = LogFactory.getLog(ScoModalidadeLicitacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = -844274977024307153L;
	
	public enum ScoModalidadeLicitacaoONExceptionCode implements BusinessExceptionCode { MENSAGEM_PARAM_OBRIG; }
	
	/**
	 * Insere modalidade PAC
	 * @param ScoModalidadePac
	 * @author dilceia.alves
	 * @since 05/03/2013
	 * @throws ApplicationBusinessException 
	 */
	public void inserirModalidadeLicitacao(ScoModalidadeLicitacao scoModalidadePac)
			throws ApplicationBusinessException {

		if (scoModalidadePac == null) {
			throw new ApplicationBusinessException(ScoModalidadeLicitacaoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoModalidadeLicitacaoRN().persistir(scoModalidadePac);
	}

	/**
	 * Altera modalidade PAC pelo código
	 * @param ScoModalidadePac
	 * @author dilceia.alves
	 * @since 05/03/2013
	 * @throws ApplicationBusinessException 
	 */
	public void alterarModalidadeLicitacao(ScoModalidadeLicitacao scoModalidadePac)
			throws ApplicationBusinessException {

		if (scoModalidadePac == null) {
			throw new ApplicationBusinessException(ScoModalidadeLicitacaoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoModalidadeLicitacaoRN().atualizar(scoModalidadePac);
	}
	
	protected ScoModalidadeLicitacaoRN getScoModalidadeLicitacaoRN(){
		return scoModalidadeLicitacaoRN;
	}
}