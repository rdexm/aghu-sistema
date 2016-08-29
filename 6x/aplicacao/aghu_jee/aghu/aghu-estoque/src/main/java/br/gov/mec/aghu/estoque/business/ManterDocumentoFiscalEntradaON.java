package br.gov.mec.aghu.estoque.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceDocumentoFiscalEntradaDAO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterDocumentoFiscalEntradaON extends BaseBusiness{

@EJB
private SceDocumentoFiscalEntradaRN sceDocumentoFiscalEntradaRN;


@Inject
private SceDocumentoFiscalEntradaDAO sceDocumentoFiscalEntradaDAO;

private static final Log LOG = LogFactory.getLog(ManterDocumentoFiscalEntradaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6937242194977410408L;
	
	public enum ManterDocumentoFiscalEntradaONExceptionCode implements BusinessExceptionCode {
		SCE_00687, SCE_00052;
	}
	
	private void prePersistirDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		
		//RN1:  Verifica se data de emissão foi maior ou igual à data de entrada 
		if (DateUtil.validaDataMaior(documentoFiscalEntrada.getDtEmissao(), documentoFiscalEntrada.getDtEntrada())) {
			throw new ApplicationBusinessException(ManterDocumentoFiscalEntradaONExceptionCode.SCE_00687);
		}
		
		
		
	}
	
	public void persistirDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException{
		this.prePersistirDocumentoFiscalEntrada(documentoFiscalEntrada);
		if(documentoFiscalEntrada.getSeq() == null){
			this.inserirDocumentoFiscalEntrada(documentoFiscalEntrada);
		}else{
			this.atualizarDocumentoFiscalEntrada(documentoFiscalEntrada);
		}
	}

	private void atualizarDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		getSceDocumentoFiscalEntradaRN().atualizar(documentoFiscalEntrada);
	}

	private void inserirDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		getSceDocumentoFiscalEntradaRN().inserir(documentoFiscalEntrada);
	}
	
	public void removerDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) throws BaseException {
		getSceDocumentoFiscalEntradaRN().remover(documentoFiscalEntrada);
	}
	
	public SceDocumentoFiscalEntradaRN getSceDocumentoFiscalEntradaRN(){
		return sceDocumentoFiscalEntradaRN;
	}
	
	protected SceDocumentoFiscalEntradaDAO getSceDocumentoFiscalEntradaDAO() {
		return sceDocumentoFiscalEntradaDAO;
	}

}
