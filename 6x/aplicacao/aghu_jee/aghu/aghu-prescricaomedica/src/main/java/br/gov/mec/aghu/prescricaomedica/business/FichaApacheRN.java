package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmFichaApacheDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FichaApacheRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(FichaApacheRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmFichaApacheDAO mpmFichaApacheDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2387817608814661686L;

	private enum FichaApacheRNExceptionCode implements
			BusinessExceptionCode {
		ERRO_PERSISTIR_FICHA_APACHE, RAP_00175;
	}

	/**
	 * ORADB Trigger MPMT_FIA_BRI
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void inserirFichaApache(MpmFichaApache fichaApache)
			throws ApplicationBusinessException {
		fichaApache.setCriadoEm(new Date());

		if (fichaApache.getEscalaGlasgow() != null) {
			Integer seqAtendimento = null;
			if (fichaApache.getAtendimento() != null) {
				seqAtendimento = fichaApache.getAtendimento().getSeq();
			}

			getPrescricaoMedicaFacade().verificarEscalaGlasglow(fichaApache
					.getEscalaGlasgow().getSeq(), seqAtendimento);
		}
		
		// Atualiza a ficha apache
		this.getFichaApacheDAO().persistir(fichaApache);
		this.getFichaApacheDAO().flush();
	}

	/**
	 * Método para atualização de MpmFichaApache com validações e chamadas de
	 * métodos necessários para atualizar o objeto.
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("ucd")
	public void atualizarFichaApache(MpmFichaApache fichaApache,
			MpmFichaApache fichaApacheOld) throws ApplicationBusinessException {

		// Before update
		this.executarTriggerBeforeUpdate(fichaApache, fichaApacheOld);

		// After update
		this.executarTriggerAfterUpdate(fichaApache, fichaApacheOld);
		
		// Atualiza a ficha apache
		this.getFichaApacheDAO().atualizar(fichaApache);
		this.getFichaApacheDAO().flush();
	}
	
	/**
	 * ORADB Trigger MPMT_FIA_BRU 
	 * 
	 * @param fichaApache
	 * @param fichaApacheOld
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void executarTriggerBeforeUpdate(MpmFichaApache fichaApache,
			MpmFichaApache fichaApacheOld) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (fichaApache == null || fichaApacheOld == null) {
			return;
		}

		Integer seqAtendimento = fichaApache.getAtendimento() == null ? null
				: fichaApache.getAtendimento().getSeq();
		getPrescricaoMedicaFacade().verificarAtendimento(fichaApache.getCriadoEm(),
				seqAtendimento, fichaApache.getDthrRealizacao());

		Boolean condicao1 = fichaApache.getAtendimento() != null
				&& fichaApacheOld.getAtendimento() != null
				&& fichaApache.getAtendimento().getSeq()
						.equals(fichaApacheOld.getAtendimento().getSeq());
		Boolean condicao2 = fichaApache.getEscalaGlasgow() != null
				&& fichaApacheOld.getEscalaGlasgow() != null
				&& fichaApache.getEscalaGlasgow().getSeq()
						.equals(fichaApacheOld.getEscalaGlasgow().getSeq());

		if (condicao1 || condicao2) {
			getPrescricaoMedicaFacade().verificarEscalaGlasglow(fichaApache
					.getEscalaGlasgow().getSeq(), fichaApache.getAtendimento()
					.getSeq());
		}
		
		fichaApache.setServidor(servidorLogado);
		
		if (fichaApache.getServidor() == null) {
			throw new ApplicationBusinessException(FichaApacheRNExceptionCode.RAP_00175);
		}
		
		if (getPacienteFacade().modificados(fichaApacheOld.getDoencaFigado(), fichaApache.getDoencaFigado())
				|| getPacienteFacade().modificados(fichaApacheOld.getDoencaCoracao(), fichaApache.getDoencaCoracao())
				|| getPacienteFacade().modificados(fichaApacheOld.getDoencaPulmao(), fichaApache.getDoencaPulmao())
				|| getPacienteFacade().modificados(fichaApacheOld.getDoencaRenal(), fichaApache.getDoencaRenal())
				|| getPacienteFacade().modificados(fichaApacheOld.getDoencaImunologica(), fichaApache.getDoencaImunologica())
				|| getPacienteFacade().modificados(fichaApacheOld.getPosOperatorioUrgencia(), fichaApache.getPosOperatorioUrgencia())		
		) {
			if (fichaApache.getDoencaFigado() || fichaApache.getDoencaCoracao()
					|| fichaApache.getDoencaPulmao()
					|| fichaApache.getDoencaRenal()
					|| fichaApache.getDoencaImunologica()
					|| fichaApache.getPosOperatorioUrgencia()) {
				fichaApache.setClinicoPosOperacaoUrgencia(true);
			} else {
				fichaApache.setClinicoPosOperacaoUrgencia(false);
			}
		}

	}
	
	/**
	 * ORADB Trigger MPMT_FIA_ARU
	 * 
	 * @param fichaApache
	 * @param fichaApacheOld
	 * @throws ApplicationBusinessException
	 */
	private void executarTriggerAfterUpdate(MpmFichaApache fichaApache,
			MpmFichaApache fichaApacheOld) throws ApplicationBusinessException {
		
		if (fichaApache == null || fichaApacheOld == null) {
			return;
		}
		
		// Gera Journal (o objeto fichaApacheOld deve ser clonado pelo método
		// FichaApacheJournalRN.clonarFichaApache antes de começar a ser
		// alterado)
		this.getPacienteFacade().gerarJournalFichaApache(
				DominioOperacoesJournal.UPD, fichaApache, fichaApacheOld);
	}

	/**
	 * ORADB Trigger 
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void removerFichaApache(MpmFichaApache fichaApache) throws ApplicationBusinessException {
		
		// Gera journal
		this.getPacienteFacade().gerarJournalFichaApache(
				DominioOperacoesJournal.DEL, fichaApache, null);
		
		this.getFichaApacheDAO().remover(fichaApache);
		this.getFichaApacheDAO().flush();
	}
	
	/**
	 * Retorna o DAO de ficha apache
	 * 
	 * @return
	 */
	protected MpmFichaApacheDAO getFichaApacheDAO() {
		return mpmFichaApacheDAO;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
