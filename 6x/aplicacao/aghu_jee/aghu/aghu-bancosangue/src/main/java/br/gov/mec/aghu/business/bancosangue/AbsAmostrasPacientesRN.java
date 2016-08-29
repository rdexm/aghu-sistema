package br.gov.mec.aghu.business.bancosangue;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsAmostrasPacientesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsAmostrasPacientesJnDAO;
import br.gov.mec.aghu.model.AbsAmostraPacienteJn;
import br.gov.mec.aghu.model.AbsAmostrasPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Implementação da package ABSK_APA_RN.
 * Foi migrado apenas a ABST_APA_BRU e ABST_APA_ARU. O restante será migrado conforme necessidade.
 */

@Stateless
public class AbsAmostrasPacientesRN extends BaseBusiness implements Serializable {
	
	private static final Log LOG = LogFactory.getLog(AbsAmostrasPacientesRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -2528174644679527138L;
	
	@EJB 
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AbsAmostrasPacientesDAO absAmostrasPacientesDAO;
	
	@Inject
	private AbsAmostrasPacientesJnDAO absAmostrasPacientesJnDAO;
	
	public void atualizarAbsAmostrasPacientes(AbsAmostrasPacientes amostraPaciente) {
		this.atualizarServidorAltera(amostraPaciente);
		
		this.absAmostrasPacientesDAO.persistir(amostraPaciente);

		AbsAmostrasPacientes oldAmostraPaciente = this.absAmostrasPacientesDAO.obterPorChavePrimaria(amostraPaciente.getId());
		this.posAtualizarAbsAmostrasPacientes(amostraPaciente, oldAmostraPaciente, DominioOperacoesJournal.UPD);		
	}
	
	/**
	 * @ORADB ABST_APA_BRU
	 * @param amostraPaciente
	 */
	private void atualizarServidorAltera(AbsAmostrasPacientes amostraPaciente) {
		RapServidores servidorLogado = this.servidorLogadoFacade.obterServidorLogado();
		amostraPaciente.setRapServidores(servidorLogado);
		amostraPaciente.setAlteradoEm(new Date());
	}
	
	/**
	 * @ORADB ABST_APA_ARU
	 * @param newAmostraPaciente
	 * @param oldAmostraPaciente
	 * @param operacao
	 */
	private void posAtualizarAbsAmostrasPacientes(AbsAmostrasPacientes newAmostraPaciente,
			AbsAmostrasPacientes oldAmostraPaciente, DominioOperacoesJournal operacao) {
		
		if ((newAmostraPaciente.getId() != null && !newAmostraPaciente.getId().equals(oldAmostraPaciente.getId()))
				|| !newAmostraPaciente.getNroAmostra().equals(oldAmostraPaciente.getNroAmostra())
				|| !newAmostraPaciente.getIndSituacao().equals(oldAmostraPaciente.getIndSituacao())
				|| (newAmostraPaciente.getObservacao() != null && oldAmostraPaciente.getObservacao() == null)
				|| (newAmostraPaciente.getObservacao() == null && oldAmostraPaciente.getObservacao() != null)
				|| (newAmostraPaciente.getObservacao() != null && oldAmostraPaciente.getObservacao() != null && !newAmostraPaciente.getObservacao().equals(oldAmostraPaciente.getObservacao()))
				|| !newAmostraPaciente.getRapServidores().equals(oldAmostraPaciente.getRapServidores())
				|| !newAmostraPaciente.getAlteradoEm().equals(oldAmostraPaciente.getAlteradoEm())
				|| !newAmostraPaciente.getNroSolicitacoesAtendidas().equals(oldAmostraPaciente.getNroSolicitacoesAtendidas())
				|| !newAmostraPaciente.getCriadoEm().equals(oldAmostraPaciente.getCriadoEm())
				|| !newAmostraPaciente.getPaciente().equals(oldAmostraPaciente.getPaciente())) {
			
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			final AbsAmostraPacienteJn journal = BaseJournalFactory.getBaseJournal(operacao, AbsAmostraPacienteJn.class, servidorLogado.getUsuario());
			
			journal.setPacCodigo(oldAmostraPaciente.getId().getPacCodigo());
			journal.setDthrAmostra(oldAmostraPaciente.getId().getDthrAmostra());
			journal.setNroAmostra(oldAmostraPaciente.getNroAmostra());
			journal.setIndSituacao(oldAmostraPaciente.getIndSituacao().toString());
			journal.setObservacao(oldAmostraPaciente.getObservacao());
			journal.setSerMatricula(oldAmostraPaciente.getRapServidores().getId().getMatricula());
			journal.setSerVinCodigo(oldAmostraPaciente.getRapServidores().getId().getVinCodigo());
			journal.setAlteradoEm(oldAmostraPaciente.getAlteradoEm());
			journal.setNroSolicitacoesAtendidas(oldAmostraPaciente.getNroSolicitacoesAtendidas().shortValue());
			journal.setCriadoEm(oldAmostraPaciente.getCriadoEm());
			journal.setPacCodigo(oldAmostraPaciente.getPaciente().getCodigo());
			
			absAmostrasPacientesJnDAO.persistir(journal);
		}
	}
				
}
