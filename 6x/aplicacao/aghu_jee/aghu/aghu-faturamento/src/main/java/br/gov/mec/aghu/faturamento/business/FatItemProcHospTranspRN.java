package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatItemProcHospTranspDAO;
import br.gov.mec.aghu.model.FatItemProcHospTransp;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatItemProcHospTranspRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8985628714046073972L;
	
	@Inject
	private FatItemProcHospTranspDAO fatItemProcHospTranspDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	

	private static final Log LOG = LogFactory.getLog(FatItemProcHospTranspRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum FatItemProcHospTranspRNExceptionCode implements BusinessExceptionCode {

		MSG_DUPLICIDADE_PROC_TRANSP;
	}
	
	/**
	 * #41082
	 * Método para gravar o registro cadastrado na base 
	 * 
	 */
	public void persistir(final FatItemProcHospTransp entidade, FatItensProcedHospitalar novo, FatItensProcedHospitalar antigo) throws BaseException {
		this.validarRegistroDuplicado(entidade);
		
		fatItemProcHospTranspDAO.persistir(entidade);
		
		faturamentoFacade.atualizarItemProcedimentoHospitalarTransplante(novo, antigo);
	}
	
	/**
	 * método para atualizar o registro 
	 */
	public void atualizar(final FatItemProcHospTransp entidade) throws BaseException {
		
		fatItemProcHospTranspDAO.atualizar(entidade); 	
	}
	
	/**
	 * #41082 - Validação da Duplicidade de código no cadastro 
	 * @throws BaseException 
	 */
	public void validarRegistroDuplicado(FatItemProcHospTransp item) throws BaseException{
		FatItemProcHospTransp fatTransp = fatItemProcHospTranspDAO.pesquisarPorIdETransplante(item);
		
		if(fatTransp != null){
			throw new ApplicationBusinessException(FatItemProcHospTranspRNExceptionCode.MSG_DUPLICIDADE_PROC_TRANSP);
		}
	}

}