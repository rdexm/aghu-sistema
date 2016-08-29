package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoFornecedorEventualRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoFornecedorEventualRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6312014757949136564L;

	public enum ScoFornecedorEventualRNExceptionCode implements BusinessExceptionCode {
		SCE_00291;
	}

	/**
	 * ORADB SCEK_SCE_RN.RN_SCEP_VER_FEV_ATIV
	 * Verifica se fornecedor eventual está ativo.
	 * @param fornecedor
	 * @throws ApplicationBusinessException 
	 */
	public void verificarFornecedorEventualAtivo(SceFornecedorEventual fornecedorEventual) throws ApplicationBusinessException {
		if(fornecedorEventual != null && !fornecedorEventual.getIndSituacao().equals(DominioSituacao.A)){
			throw new ApplicationBusinessException(ScoFornecedorEventualRNExceptionCode.SCE_00291);
		}
	}

}
