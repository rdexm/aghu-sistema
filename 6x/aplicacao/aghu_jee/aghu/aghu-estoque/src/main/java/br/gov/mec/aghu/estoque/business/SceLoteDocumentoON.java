package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class SceLoteDocumentoON extends BaseBusiness {

@EJB
private SceLoteDocumentoRN sceLoteDocumentoRN;

private static final Log LOG = LogFactory.getLog(SceLoteDocumentoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = -9046719109546539260L;
	
	public enum SceLoteDocumentoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_INSERT_CTRL_VAL_MATERIAL;
	}
	
	public void inserir(SceLoteDocumento loteDocumento) throws BaseException {
		getSceLoteDocumentoRN().inserir(loteDocumento);
		
	}
	
	protected SceLoteDocumentoRN getSceLoteDocumentoRN() {
		return sceLoteDocumentoRN;
	}
	
	/**
	 * Valida se foi informado uma NR ou ESL
	 * @param loteDocumento
	 *  
	 */
	public void validarDocumentoOrigem(SceLoteDocumento loteDocumento) throws ApplicationBusinessException {
		
		if (loteDocumento.getItemNotaRecebimento() != null) {
			
			loteDocumento.setLotMcmCodigo(loteDocumento.getItemNotaRecebimento().getItemAutorizacaoForn().getMarcaComercial().getCodigo());
			
		} else if (loteDocumento.getEntradaSaidaSemLicitacao() != null) {

			loteDocumento.setLotMcmCodigo(loteDocumento.getEntradaSaidaSemLicitacao().getScoMarcaComercial().getCodigo());
			loteDocumento.setFornecedor(loteDocumento.getEntradaSaidaSemLicitacao().getScoFornecedor());
			
		} else {

			throw new ApplicationBusinessException(SceLoteDocumentoONExceptionCode.MENSAGEM_ERRO_INSERT_CTRL_VAL_MATERIAL);

		}
		
	}

}
