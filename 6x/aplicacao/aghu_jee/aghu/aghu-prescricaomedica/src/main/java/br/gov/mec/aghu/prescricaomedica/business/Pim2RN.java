package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPim2DAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class Pim2RN extends BaseBusiness {

	@EJB
	private Pim2JournalRN pim2JournalRN;
	
	private static final Log LOG = LogFactory.getLog(Pim2RN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MpmPim2DAO mpmPim2DAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9136903683713440317L;

	/**
	 * Método para remover o pim2 e executar as triggers necessárias
	 * 
	 * @param pim2
	 * @throws ApplicationBusinessException
	 */
	public void removerPim2(MpmPim2 pim2) throws ApplicationBusinessException {
		
		// Gerar journal
		this.executarTriggerAfterDelete(pim2);
		this.getPim2DAO().remover(pim2);
		this.getPim2DAO().flush();
	}
	
	/**
	 * Método para criar journal de MpmPim2
	 * 
	 * ORADB Trigger MPMT_PM2_ARD
	 * 
	 * @param pim2
	 */
	private void executarTriggerAfterDelete(MpmPim2 pim2) throws ApplicationBusinessException {
		this.getPim2JournalRN().gerarJournalPim2(DominioOperacoesJournal.DEL,
				pim2, null);
	}
	
	/**
	 * ORADB Trigger MPMT_PM2_BRI
	 * 
	 * @param pim2
	 */
	public void inserirPim2(MpmPim2 pim2) {
		pim2.setCriadoEm(new Date());
		
		this.getPim2DAO().persistir(pim2);
		this.getPim2DAO().flush();
	}
	
	/**
	 * Método para criar 
	 * 
	 * ORADB Trigger MPMT_PM2_ARU
	 *  
	 * @param pim2
	 * @param pim2Old
	 * @throws ApplicationBusinessException
	 */
	public void atualizarPim2(MpmPim2 pim2, MpmPim2 pim2Anterior) throws ApplicationBusinessException {
		
		MpmPim2 pim2Old = pim2Anterior; 
		
		if(pim2Old == null) {
			pim2Old = mpmPim2DAO.obterOriginal(pim2.getSeq());
		}
		
		if ("A".equals(pim2Old.getSituacao()) || "E".equals(pim2Old.getSituacao())) {
			this.getPim2JournalRN().gerarJournalPim2(DominioOperacoesJournal.UPD, pim2, pim2Old);
		}
		
		this.getPim2DAO().atualizar(pim2);
		this.getPim2DAO().flush();
	}
	
	/**
	 * Retorna o DAO da journal de atendimento
	 * 
	 * @return
	 */
	protected MpmPim2DAO getPim2DAO() {
		return mpmPim2DAO;
	}

	/**
	 * Retorna o DAO da journal de atendimento
	 * 
	 * @return
	 */
	private Pim2JournalRN getPim2JournalRN() {
		return pim2JournalRN;
	}

}
