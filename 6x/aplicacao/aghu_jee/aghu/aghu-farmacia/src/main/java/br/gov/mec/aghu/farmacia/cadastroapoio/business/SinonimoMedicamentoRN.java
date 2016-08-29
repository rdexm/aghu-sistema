package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaSinonimoMedicamentoJnDAO;
import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class SinonimoMedicamentoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(SinonimoMedicamentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AfaSinonimoMedicamentoJnDAO afaSinonimoMedicamentoJnDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5685204610475335819L;

	/**
	 * @ORADB Trigger AFAT_SMD_BRI</br>
	 * 
	 * Seta data de criação e servidor
	 * 
	 * @param sinonimoMedicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preInsertSinonimoMedicamento(
			AfaSinonimoMedicamento sinonimoMedicamento)
			throws ApplicationBusinessException {

		// Seta o servidor
		this.setServidor(sinonimoMedicamento);

		// Seta data de criação
		sinonimoMedicamento.setCriadoEm(Calendar.getInstance().getTime());
	}

	/**
	 * @ORADB Trigger AFAT_SMD_BRU</br>
	 * 
	 * Seta o servidor
	 * 
	 * @param sinonimoMedicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void preUpdateSinonimoMedicamento(
			AfaSinonimoMedicamento sinonimoMedicamento)
			throws ApplicationBusinessException {

		// Seta o servidor
		this.setServidor(sinonimoMedicamento);
	}

	/**
	 * @ORADB Trigger AFAT_SMD_ARU
	 * 
	 * Cria a journal se foi alterado
	 * 
	 * @param sinonimoMedicamentoOld
	 * @param sinonimoMedicamento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AfaSinonimoMedicamentoJn posUpdateSinonimoMedicamento(
			AfaSinonimoMedicamento sinonimoMedicamentoOld,
			AfaSinonimoMedicamento sinonimoMedicamento)
			throws ApplicationBusinessException {

		if (!CoreUtil.igual(sinonimoMedicamentoOld.getServidor(),
				sinonimoMedicamento.getServidor())
				|| !CoreUtil.igual(sinonimoMedicamentoOld.getDescricao(),
						sinonimoMedicamento.getDescricao())
				|| !CoreUtil.igual(sinonimoMedicamentoOld.getCriadoEm(),
						sinonimoMedicamento.getCriadoEm())
				|| !CoreUtil.igual(sinonimoMedicamentoOld.getIndSituacao(),
						sinonimoMedicamento.getIndSituacao())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			AfaSinonimoMedicamentoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AfaSinonimoMedicamentoJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			
			jn.setMedMatCodigo(sinonimoMedicamentoOld.getId().getMedMatCodigo());
			jn.setSeqp(sinonimoMedicamentoOld.getId().getSeqp());
			jn.setSerMatricula(sinonimoMedicamentoOld.getServidor().getId()
					.getMatricula());
			jn.setSerVinCodigo(sinonimoMedicamentoOld.getServidor().getId()
					.getVinCodigo());
			jn.setDescricao(sinonimoMedicamentoOld.getDescricao());
			jn.setCriadoEm(sinonimoMedicamentoOld.getCriadoEm());
			jn.setIndSituacao(sinonimoMedicamentoOld.getIndSituacao());

			return insertSinonimoMedicamentoJn(jn);
		}

		return null;
	}

	/**
	 * @ORADB Trigger AFAT_SMD_ARD
	 * 
	 * Cria a journal
	 * 
	 * @param sinonimoMedicamento
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AfaSinonimoMedicamentoJn posDeleteSinonimoMedicamento(
			AfaSinonimoMedicamento sinonimoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AfaSinonimoMedicamentoJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AfaSinonimoMedicamentoJn.class, servidorLogado.getUsuario());
		
		jn.setMedMatCodigo(sinonimoMedicamento.getId().getMedMatCodigo());
		jn.setSeqp(sinonimoMedicamento.getId().getSeqp());
		jn.setSerMatricula(sinonimoMedicamento.getServidor().getId()
				.getMatricula());
		jn.setSerVinCodigo(sinonimoMedicamento.getServidor().getId()
				.getVinCodigo());
		jn.setDescricao(sinonimoMedicamento.getDescricao());
		jn.setCriadoEm(sinonimoMedicamento.getCriadoEm());
		jn.setIndSituacao(sinonimoMedicamento.getIndSituacao());

		return insertSinonimoMedicamentoJn(jn);
	}

	/**
	 * Seta o servidor logado
	 * 
	 * @param sinonimoMedicamento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void setServidor(AfaSinonimoMedicamento sinonimoMedicamento)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
		sinonimoMedicamento.setServidor(servidorLogado);
	}

	/**
	 * Insere um sinonimo de medicamento da sua respectiva "journal"
	 * 
	 * @ORADB Trigger AFAT_SMDJ_BRI
	 * 
	 * @param sinonimoMedicamentoJn
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AfaSinonimoMedicamentoJn insertSinonimoMedicamentoJn(
			AfaSinonimoMedicamentoJn sinonimoMedicamentoJn)
			throws ApplicationBusinessException {

		getAfaSinonimoMedicamentoJnDAO().persistir(sinonimoMedicamentoJn);
		getAfaSinonimoMedicamentoJnDAO().flush();
		return sinonimoMedicamentoJn;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AfaSinonimoMedicamentoJnDAO getAfaSinonimoMedicamentoJnDAO() {
		return afaSinonimoMedicamentoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
