package br.gov.mec.aghu.faturamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatItemProcHospTranspDAO;
import br.gov.mec.aghu.model.FatItemProcHospTransp;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FatItensProcedHospitalarRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8985628714046073972L;
	
	@Inject
	private FatItemProcHospTranspDAO fatItemProcHospTranspDAO;
	
	@EJB
	private IRegistroColaboradorFacade servidor;

	private static final Log LOG = LogFactory.getLog(FatItensProcedHospitalarRN.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Método para gravar o registro cadastrado na base 
	 * 
	 */
	public void persistir(final FatItemProcHospTransp entidade) throws BaseException {
		
		RapServidores servidorRecuperado = this.obterServidor();
		if(servidorRecuperado != null){			
			fatItemProcHospTranspDAO.persistir(entidade); 
			fatItemProcHospTranspDAO.persistirProcedimentoHospitalarComTransplante(entidade);		
		}
	}
	
	private RapServidores obterServidor() throws ApplicationBusinessException{
		return servidor.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
	}
}