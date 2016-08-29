package br.gov.mec.aghu.estoque.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoFornecedorRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoFornecedorRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -978286424152605897L;

	public enum ScoFornecedorRNExceptionCode implements BusinessExceptionCode {
		SCE_00290;
	}

	/**
	 * ORADB SCEK_SCE_RN.RN_SCEP_VER_FRN_ATIV
	 * Verifica se fornecedor está ativo.
	 * @param fornecedor
	 * @throws ApplicationBusinessException 
	 */
	public void verificarFornecedorAtivo(ScoFornecedor fornecedor) throws ApplicationBusinessException {
		if(fornecedor != null && !fornecedor.getSituacao().equals(DominioSituacao.A)){
			throw new ApplicationBusinessException(ScoFornecedorRNExceptionCode.SCE_00290);
		}
	}

}
