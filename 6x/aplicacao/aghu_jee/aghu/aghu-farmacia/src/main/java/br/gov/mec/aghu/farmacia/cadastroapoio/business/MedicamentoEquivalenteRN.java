package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.OcorrenciaRN.OcorrenciaRNExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoEquivalenteDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoEquivalenteJnDAO;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class MedicamentoEquivalenteRN extends AbstractAGHUCrudRn<AfaMedicamentoEquivalente> {

	private static final long serialVersionUID = 9046912017538835175L;

	private static final Log LOG = LogFactory.getLog(MedicamentoEquivalenteRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AfaMedicamentoEquivalenteDAO afaMedicamentoEquivalenteDAO;
	
	@Inject
	private AfaMedicamentoEquivalenteJnDAO afaMedicamentoEquivalenteJnDAO;
	
	public enum MedicamentoEquivalenteRnExceptionCode implements BusinessExceptionCode {
		ERRO_MEDICAMENTO_EQUIVALENTE_JA_ASSOCIADO, ERRO_MEDICAMENTO_EQUIVALENTE_PROPRIO_MEDICAMENTO;
	}
	
	@Override
	public boolean preInsercao(AfaMedicamentoEquivalente entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		validaMedicamentoAssociadoIgual(entidade);
		validaSeMedicamentoJaAssociado(entidade);
		entidade.setRapServidores(servidorLogado);
		setDtaCriacao(entidade);
		return super.preInsercao(entidade, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	/**
	 * Verifica se o medicamento equivalente é o próprio medicamento
	 * caso sim, lança exceção
	 * @param entidade
	 * @throws ApplicationBusinessException 
	 */
	private void validaMedicamentoAssociadoIgual(
			AfaMedicamentoEquivalente entidade) throws ApplicationBusinessException {
		if(CoreUtil.igual(entidade.getId().getMedMatCodigoEquivalente(),entidade.getId().getMedMatCodigo())) {
			throw new ApplicationBusinessException(MedicamentoEquivalenteRnExceptionCode.ERRO_MEDICAMENTO_EQUIVALENTE_PROPRIO_MEDICAMENTO);
		}
	}
	/**
	 * Verifica se o Medicamento Equivalente/Substituto associado já existe para aquele medicamento
	 * caso sim lança exceção
	 * @param entidade
	 * @throws ApplicationBusinessException
	 */
	private void validaSeMedicamentoJaAssociado(
			AfaMedicamentoEquivalente entidade) throws ApplicationBusinessException {
		AfaMedicamentoEquivalente ent = getDao().obterPorChavePrimaria(entidade.getId());
		if(ent!=null) {
			getDao().desatachar(ent);
			throw new ApplicationBusinessException(MedicamentoEquivalenteRnExceptionCode.ERRO_MEDICAMENTO_EQUIVALENTE_JA_ASSOCIADO);
		}
	}
	
	public AfaMedicamentoEquivalenteDAO getDao(){
		return afaMedicamentoEquivalenteDAO;
	}
	
	/**
	 * @ORADB AGH.AFAT_MEQ_BRI
	 */
	@SuppressWarnings("ucd")
	@Override
	public boolean briPreInsercaoRow(AfaMedicamentoEquivalente entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		if(validarServidorNulo(entidade.getRapServidores().getServidor())){
			throw new ApplicationBusinessException(OcorrenciaRNExceptionCode.AFA_00169);
		}
		return true;
	}
	
	/**
	 * @ORADB AGH.AFAT_MEQ_BRU
	 */
	@SuppressWarnings("ucd")
	@Override
	public boolean bruPreAtualizacaoRow(AfaMedicamentoEquivalente original,
			AfaMedicamentoEquivalente modificada, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		if(validarServidorNulo(original.getRapServidores().getServidor())){
			throw new ApplicationBusinessException(OcorrenciaRNExceptionCode.AFA_00169);
		}
		return true;
	}
	
	protected void setDtaCriacao(AfaMedicamentoEquivalente entidade) {
		entidade.setCriadoEm(getDataCriacao());
	}

	/**
	 * FUNCTION
	 * @ORADB: AFAT_MEQ_ARU
	 */
	@SuppressWarnings("ucd")
	@Override
	public boolean aruPosAtualizacaoRow(AfaMedicamentoEquivalente original,
			AfaMedicamentoEquivalente modificada, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
	if(!CoreUtil.igual(original.getId(), modificada.getId())
				|| !CoreUtil.igual(original.getCriadoEm(), modificada.getCriadoEm())
				|| !CoreUtil.igual(original.getIndSituacao(), modificada.getIndSituacao())
				|| !CoreUtil.igual(original.getMedicamento(), modificada.getMedicamento())
				|| !CoreUtil.igual(original.getMedicamentoEquivalente(), modificada.getMedicamentoEquivalente())
				|| !CoreUtil.igual(original.getTipo(),  modificada.getTipo())
		){
			AfaMedicamentoEquivalenteJn journal = getJournal(original,
					DominioOperacoesJournal.UPD);
			insertAfaMedicamentoEquivalenteJournal(journal);
		}
		return true;
	}
	
	protected AfaMedicamentoEquivalenteJn getJournal(
			AfaMedicamentoEquivalente old, DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		AfaMedicamentoEquivalenteJn journal = BaseJournalFactory.getBaseJournal(operacao, AfaMedicamentoEquivalenteJn.class, servidorLogado.getUsuario());
		journal.setCriadoEm(old.getCriadoEm());
		journal.setIndSituacao(old.getIndSituacao());
		journal.setMedMatCodigo(old.getId().getMedMatCodigo());
		journal.setMedMatCodigoEquivalente(old.getId().getMedMatCodigoEquivalente());
		journal.setSerMatricula(old.getRapServidores().getId().getMatricula());
		journal.setSerVinCodigo(old.getRapServidores().getId().getVinCodigo());
		journal.setTipo(old.getTipo());
		
		return journal;
	}
	
	protected void insertAfaMedicamentoEquivalenteJournal(AfaMedicamentoEquivalenteJn entidade){
		afaMedicamentoEquivalenteJnDAO.persistir(entidade);
		afaMedicamentoEquivalenteJnDAO.flush();
	}
	
	protected boolean validarServidorNulo(RapServidores servidor) {
		if(servidor == null) {
			return true;
		}
		
		return false;
	}
	
}