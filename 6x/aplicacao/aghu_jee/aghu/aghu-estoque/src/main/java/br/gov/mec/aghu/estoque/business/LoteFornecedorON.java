package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.SceLoteFornecedor;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class LoteFornecedorON extends BaseBusiness{

@EJB
private SceLoteFornecedorRN sceLoteFornecedorRN;

private static final Log LOG = LogFactory.getLog(LoteFornecedorON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	private static final long serialVersionUID = -4257023928561369311L;

	public enum LoteFornecedorONExceptionCode implements BusinessExceptionCode {
		SCE_00697_LOTE, SCE_00898;
	}
		
	/**
	 * Persisti o lote fornecedor no banco
	 * @param loteForn
	 * @param movimento
	 * @param qtdeValidades
	 * @param qtdeLotes
	 * @throws BaseException
	 */
	public void inserir(SceLoteFornecedor loteForn, SceMovimentoMaterial movimento, Integer qtdeValidades, Integer qtdeLotes) throws BaseException{
		this.preInserir(loteForn, movimento, qtdeValidades, qtdeLotes);
		this.getSceLoteFornecedorRN().inserir(loteForn);
	}
	
	/**
	 * Persisti o lote fornecedor no banco
	 * @param loteForn
	 * @param movimento
	 * @param qtdeValidades
	 * @param qtdeLotes
	 * @throws BaseException
	 */
	public void atualizar(SceLoteFornecedor loteForn, SceMovimentoMaterial movimento, Integer qtdeValidades, Integer qtdeLotes, Integer loteQtdeAnterior) throws BaseException{
		this.preAtualizar(loteForn, movimento, qtdeValidades, qtdeLotes, loteQtdeAnterior);
		this.getSceLoteFornecedorRN().atualizar(loteForn);
	}
	
	/**
	 * ORADB EVT_KEY_COMMIT, EVT_PRE_INSERT (INSERT)  
	 * Pré-inserir SceLoteFornecedor
	 * @param loteForn
	 * @param movimento
	 * @param qtdeValidades
	 * @param qtdeLotes
	 * @throws BaseException
	 */
	protected void preInserir(SceLoteFornecedor loteForn, SceMovimentoMaterial movimento, Integer qtdeValidades, Integer qtdeLotes) throws BaseException{
		validaQuantidades(qtdeValidades, qtdeLotes);
		defineFevEQTDESAIDA(loteForn);
	}
	
	/**
	 * ORADB EVT_KEY_COMMIT (UPDATE)  
	 * Pré-UPDATE SceLoteFornecedor
	 * @param loteForn
	 * @param movimento
	 * @param qtdeValidades
	 * @param qtdeLotes
	 * @throws BaseException
	 */
	protected void preAtualizar(SceLoteFornecedor loteForn, SceMovimentoMaterial movimento, Integer qtdeValidades, Integer qtdeLotes, Integer loteQtdeAnterior) throws BaseException{
		validaQuantidades(qtdeValidades, qtdeLotes);
		validaValorAnterior(loteForn, loteQtdeAnterior);
	}

	/**
	 * Valida as quantidades das validades e do movimento
	 * @param qtdeValidades
	 * @param qtdeLotes
	 * @throws BaseException
	 */
	public void validaQuantidades(Integer qtdeValidades, Integer qtdeLotes) throws BaseException {
		if(qtdeLotes > qtdeValidades){
			throw new ApplicationBusinessException(LoteFornecedorONExceptionCode.SCE_00697_LOTE);
		}
	}

	/***
	 * Define fornecedor eventual como null e quantidade saída como 0
	 * @param loteForn
	 */
	private void defineFevEQTDESAIDA(SceLoteFornecedor loteForn){
		loteForn.setFevSeq(null);
		loteForn.setQuantidadeSaida(0);
	}
	
	private void validaValorAnterior(SceLoteFornecedor loteForn, Integer loteQtdeAnterior) throws BaseException {
		if(loteForn.getQuantidade() < loteQtdeAnterior){
			throw new ApplicationBusinessException(LoteFornecedorONExceptionCode.SCE_00898);
		}
	}
	
	
	private SceLoteFornecedorRN getSceLoteFornecedorRN(){
		return sceLoteFornecedorRN;
	}
}
