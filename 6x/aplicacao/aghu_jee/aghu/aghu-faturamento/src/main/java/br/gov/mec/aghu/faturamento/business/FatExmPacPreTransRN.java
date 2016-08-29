package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatExmPacPreTransDAO;
import br.gov.mec.aghu.model.FatExmPacPreTrans;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FatExmPacPreTransRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(FatExmPacPreTransRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatExmPacPreTransDAO fatExmPacPreTransDAO;

	private static final long serialVersionUID = 17813854715430234L;

	protected void persistir(final FatExmPacPreTrans fatExmPacPreTrans) {
		if(fatExmPacPreTrans.getSeq() == null){
			inserir(fatExmPacPreTrans);
			
		} else {
			atualizar(fatExmPacPreTrans);
		}
	}
	
	/**
	 * ORADB: FATT_EPP_BRU
	 */
	private void atualizar(final FatExmPacPreTrans fatExmPacPreTrans) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fatExmPacPreTrans.setServidor(servidorLogado);
		getFatExmPacPreTransDAO().atualizar(fatExmPacPreTrans);
	}

	/**
	 * ORADB: FATT_EPP_BRI
	 */
	private void inserir(final FatExmPacPreTrans fatExmPacPreTrans) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		fatExmPacPreTrans.setCriadoEm(new Date());
		fatExmPacPreTrans.setServidor(servidorLogado);
		getFatExmPacPreTransDAO().persistir(fatExmPacPreTrans);
	}

	protected FatExmPacPreTransDAO getFatExmPacPreTransDAO() {
		return fatExmPacPreTransDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}