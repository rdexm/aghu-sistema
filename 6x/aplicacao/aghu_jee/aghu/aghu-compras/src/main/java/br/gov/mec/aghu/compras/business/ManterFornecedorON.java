package br.gov.mec.aghu.compras.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterFornecedorON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ManterFornecedorON.class);

@EJB
private ManterFornecedorRN manterFornecedorRN;

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 8169558502495729149L;

	public enum ManterFornecedorONExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_SCOFORNECEDOR
	}

	public ScoFornecedor clonarFornecedor(final ScoFornecedor fornecedor) throws BaseException {
		try {
			return (ScoFornecedor) BeanUtils.cloneBean(fornecedor);
		} catch (final Exception e) {
			logError("Exceção capturada: ", e);
			throw new BaseException(ManterFornecedorONExceptionCode.ERRO_CLONE_SCOFORNECEDOR);
		}
	}
	
	public boolean existePenalidade(ScoFornecedor fornecedor) {
		return getManterFornecedorRN().obterNumeroPenalidadesExcetoOcorrencias(fornecedor) > 0;
	}
	
	public ManterFornecedorRN getManterFornecedorRN() {
		return manterFornecedorRN;
	}
}
