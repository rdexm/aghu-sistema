package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelExtratoItemCartasDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicCartasDAO;
import br.gov.mec.aghu.model.AelExtratoItemCartas;
import br.gov.mec.aghu.model.AelExtratoItemCartasId;
import br.gov.mec.aghu.model.AelItemSolicCartas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lucas
 *
 */
@Stateless
public class AelItemSolicCartasRN extends BaseBusiness {

	@EJB
	private AelExtratoItemCartasON aelExtratoItemCartasON;
	
	private static final Log LOG = LogFactory.getLog(AelItemSolicCartasRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemSolicCartasDAO aelItemSolicCartasDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelExtratoItemCartasDAO aelExtratoItemCartasDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2327960363981196024L;

	public void persistirAelItemSolicCartas(AelItemSolicCartas aelItemSolicCartas, Boolean flush) throws ApplicationBusinessException {
		
		beforeInsertAelItemSolicCartas(aelItemSolicCartas);
		getAelItemSolicCartasDAO().persistir(aelItemSolicCartas);
		if (flush) {
			getAelItemSolicCartasDAO().flush();
		}
		afterInsertAelItemSolicCartas(aelItemSolicCartas);
	
	}
	
	/**
	 * ORADB TRIGGER AELT_ITE_BRI
	 * @param aelItemSolicCartas
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void beforeInsertAelItemSolicCartas(AelItemSolicCartas aelItemSolicCartas) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelItemSolicCartas.setAlteradoEm(new Date());
		aelItemSolicCartas.setServidor(servidorLogado);
	}
	
	/**
	 * ORADB TRIGGER AELT_ITE_ARI
	 * @param aelItemSolicCartas
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void afterInsertAelItemSolicCartas(AelItemSolicCartas aelItemSolicCartas) throws ApplicationBusinessException {
		atuExtrato(aelItemSolicCartas);
	}
	
	/**
	 * ORADB TRIGGER RN_ITEP_ATU_EXTRATO
	 * @param aelItemSolicCartas
	 *  
	 * @throws ApplicationBusinessException 
	 */
	private void atuExtrato(AelItemSolicCartas aelItemSolicCartas) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<AelExtratoItemCartas> extratos = getAelExtratoItemCartasDAO().buscarAelExtratoItemCartasPorItemCartas(aelItemSolicCartas);
		Short seqp = 0;
		for(AelExtratoItemCartas extrato:extratos){
			if(extrato.getId().getSeqp() > seqp){
				seqp = extrato.getId().getSeqp();
			}
		}
		seqp++;
		
		AelExtratoItemCartas novoExtrato = new AelExtratoItemCartas();
		novoExtrato.setId(new AelExtratoItemCartasId());
		novoExtrato.getId().setIteIseSoeSeq(aelItemSolicCartas.getId().getIseSoeSeq());
		novoExtrato.getId().setIteIseSeqp(aelItemSolicCartas.getId().getIseSeqp());
		novoExtrato.getId().setIteSeqp(aelItemSolicCartas.getId().getSeqp());
		novoExtrato.getId().setSeqp(seqp);
		novoExtrato.setAelItemSolicCartas(aelItemSolicCartas);
		novoExtrato.setDthrEvento(new Date());
		novoExtrato.setServidor(servidorLogado);
		novoExtrato.setSituacao(aelItemSolicCartas.getSituacao());
		novoExtrato.setMotivoRetorno(aelItemSolicCartas.getMotivoRetorno());
		
		getAelExtratoItemCartasON().persistirAelExtratoItemCartas(novoExtrato, false);
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AelExtratoItemCartasDAO getAelExtratoItemCartasDAO() {
		return aelExtratoItemCartasDAO;
	}
	
	protected AelItemSolicCartasDAO getAelItemSolicCartasDAO() {
		return aelItemSolicCartasDAO;
	}
	
	protected AelExtratoItemCartasON getAelExtratoItemCartasON() {
		return aelExtratoItemCartasON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
