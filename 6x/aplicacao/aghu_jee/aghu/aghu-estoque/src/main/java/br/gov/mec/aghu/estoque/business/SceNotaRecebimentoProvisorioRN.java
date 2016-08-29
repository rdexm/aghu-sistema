package br.gov.mec.aghu.estoque.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.estoque.dao.SceNotaRecebProvisorioDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class SceNotaRecebimentoProvisorioRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(SceNotaRecebimentoProvisorioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceNotaRecebProvisorioDAO sceNotaRecebProvisorioDAO;

	private static final long serialVersionUID = 8919020356375252010L;

	/**
	 * ORADB SCET_NRP_BRU
	 * regras de pre update em SceNotaRecebProvisorio
	 * @param notaRecebimentoProvisorioOriginal, notaRecebimentoProvisorio
	 * @throws BaseException 
	 */
	private void preAtualizar(SceNotaRecebProvisorio notaRecebimentoProvisorio, SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal) throws BaseException {
		
		if(!notaRecebimentoProvisorio.getIndEstorno().equals(notaRecebimentoProvisorioOriginal.getIndEstorno()) && notaRecebimentoProvisorio.getIndEstorno().equals(Boolean.TRUE)){
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			notaRecebimentoProvisorio.setDtEstorno(new Date());
			notaRecebimentoProvisorio.setServidorEstorno(servidorLogado);
		}
	}
	
	public void atualizar(SceNotaRecebProvisorio notaRecebimentoProvisorio, SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal) throws BaseException{
		this.preAtualizar(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
		this.getSceNotaRecebProvisorioDAO().merge(notaRecebimentoProvisorio);
	}

	protected SceNotaRecebProvisorioDAO getSceNotaRecebProvisorioDAO(){
		return sceNotaRecebProvisorioDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	/**
	 * Indica nota de recebimento provisório é de serviço.
	 * 
	 * @return Flag
	 */
	public boolean isNotaRecebProvisorioServico(
			SceNotaRecebProvisorio notaRecebProv) {		
		return getSceNotaRecebProvisorioDAO().contarItensByTipo(notaRecebProv,
				DominioTipoFaseSolicitacao.S) > 0;
	}
	
}
