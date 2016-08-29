package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversoJnDAO;
import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.model.AelAtendimentoDiversoJn;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AelAtendimentoDiversosRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(AelAtendimentoDiversosRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelAtendimentoDiversoJnDAO aelAtendimentoDiversoJnDAO;
	
	@Inject
	private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO; 

	private static final long serialVersionUID = -3523769478773737522L;

	public enum AelAtendimentoDiversosRNExceptionCode implements BusinessExceptionCode {
		VIOLACAO_FK_DEPENDENTE;
	}
	
	public void persistirAelAtendimentoDiversos(final AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException{
		if (aelAtendimentoDiversos.getSeq() == null) {
			this.inserir(aelAtendimentoDiversos);
		} else {
			this.atualizar(aelAtendimentoDiversos);
		}
	}
	
	/**
	 * ORADB TRIGGER AELT_ATV_BRI (INSERT)
	 */
	private void preInserir(final AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(aelAtendimentoDiversos.getDthrInicio() == null){
			aelAtendimentoDiversos.setDthrInicio(new Date());
		}

		// Atualiza o servidor que está criando.
		// Substitui aelk_ael_rn.rn_aelp_atu_servidor (:new.ser_matricula,:new.ser_vin_codigo);
		aelAtendimentoDiversos.setServidor(servidorLogado);
	}

	private void inserir(final AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException{
		this.preInserir(aelAtendimentoDiversos);
		this.getAelAtendimentoDiversosDAO().persistir(aelAtendimentoDiversos);
	}
	
	private void atualizar(final AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException{
		final AelAtendimentoDiversos original = getAelAtendimentoDiversosDAO().obterOriginal(aelAtendimentoDiversos);
		this.getAelAtendimentoDiversosDAO().atualizar(aelAtendimentoDiversos);
		this.posAtualizar(original, aelAtendimentoDiversos);
	}
	
	/**
	 * ORADB: AELT_ATV_ARU
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizar(final AelAtendimentoDiversos original, final AelAtendimentoDiversos alterado) throws ApplicationBusinessException{
		if(!CoreUtil.igual(original.getSeq(), alterado.getSeq()) ||
				!CoreUtil.igual(original.getDthrInicio(), alterado.getDthrInicio()) ||
				!CoreUtil.igual(original.getDthrFim(), alterado.getDthrFim()) ||
				!CoreUtil.igual(original.getIdentificacaoAmostra(), alterado.getIdentificacaoAmostra()) ||
				!CoreUtil.igual(original.getDescricaoMaterial(), alterado.getDescricaoMaterial()) ||
				!CoreUtil.igual(original.getAipPaciente(), alterado.getAipPaciente()) ||
				!CoreUtil.igual(original.getNomePaciente(), alterado.getNomePaciente()) ||
				!CoreUtil.igual(original.getTipoAmostra(), alterado.getTipoAmostra()) ||
				!CoreUtil.igual(original.getDtSoro(), alterado.getDtSoro()) ||
				!CoreUtil.igual(original.getAelProjetoPesquisas(), alterado.getAelProjetoPesquisas()) ||
				!CoreUtil.igual(original.getAelCadCtrlQualidades(), alterado.getAelCadCtrlQualidades()) ||
				!CoreUtil.igual(original.getAelLaboratorioExternos(), alterado.getAelLaboratorioExternos()) ||
				!CoreUtil.igual(original.getAelDadosCadaveres(), alterado.getAelDadosCadaveres()) ||
				!CoreUtil.igual(original.getFccCentroCustos(), alterado.getFccCentroCustos()) ||
				!CoreUtil.igual(original.getAghEspecialidades(), alterado.getAghEspecialidades()) ||
				!CoreUtil.igual(original.getServidor(), alterado.getServidor()) ||
				!CoreUtil.igual(original.getOrigemAmostra(), alterado.getOrigemAmostra()) ||
				!CoreUtil.igual(original.getProntuario(), alterado.getProntuario()) ||
				!CoreUtil.igual(original.getDtNascimento(), alterado.getDtNascimento()) ||
				!CoreUtil.igual(original.getSexo(), alterado.getSexo()) ||
				!CoreUtil.igual(original.getAbsCandidatosDoadores(), alterado.getAbsCandidatosDoadores()) ||
				!CoreUtil.igual(original.getAbsAmostraDoacao(), alterado.getAbsAmostraDoacao()) 
		){
			createJournal(alterado, DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * ORADB: AELT_ATV_ARD
	 */
	public void remover(AelAtendimentoDiversos aelAtendimentoDiversos) throws BaseException{
		try{
			aelAtendimentoDiversos = getAelAtendimentoDiversosDAO().merge(aelAtendimentoDiversos);
			getAelAtendimentoDiversosDAO().remover(aelAtendimentoDiversos);
			createJournal(aelAtendimentoDiversos, DominioOperacoesJournal.DEL);
			getAelAtendimentoDiversosDAO().flush();
		} catch (final PersistenceException ce) {
			if (ce.getCause() instanceof ConstraintViolationException) {
				final ConstraintViolationException cve = (ConstraintViolationException) ce.getCause();
				throw new ApplicationBusinessException(AelAtendimentoDiversosRNExceptionCode.VIOLACAO_FK_DEPENDENTE, cve.getConstraintName());
			}
		}
	}
	
	private void createJournal(final AelAtendimentoDiversos reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelAtendimentoDiversoJn journal = BaseJournalFactory.getBaseJournal(operacao, AelAtendimentoDiversoJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setDthrInicio(reg.getDthrInicio());
		journal.setDthrFim(reg.getDthrFim());
		journal.setIdentificacaoAmostra(reg.getIdentificacaoAmostra());
		journal.setDescricaoMaterial(reg.getDescricaoMaterial());
		journal.setAipPaciente(reg.getAipPaciente());
		journal.setNomePaciente(reg.getNomePaciente());
		journal.setTipoAmostra(reg.getTipoAmostra());
		journal.setDtSoro(reg.getDtSoro());
		journal.setAelProjetoPesquisas(reg.getAelProjetoPesquisas());
		journal.setAelCadCtrlQualidades(reg.getAelCadCtrlQualidades());
		journal.setAelLaboratorioExternos(reg.getAelLaboratorioExternos());
		journal.setAelDadosCadaveres(reg.getAelDadosCadaveres());
		journal.setFccCentroCustos(reg.getFccCentroCustos());
		journal.setAghEspecialidades(reg.getAghEspecialidades());
		journal.setServidor(reg.getServidor());
		journal.setOrigemAmostra(reg.getOrigemAmostra());
		journal.setProntuario(reg.getProntuario());
		journal.setDtNascimento(reg.getDtNascimento());
		journal.setSexo(reg.getSexo());
		journal.setAbsCandidatosDoadores(reg.getAbsCandidatosDoadores());
		
		getAelAtendimentoDiversoJnDAO().persistir(journal);
	}

	private AelAtendimentoDiversoJnDAO getAelAtendimentoDiversoJnDAO(){
		return aelAtendimentoDiversoJnDAO;
	}
	
	private AelAtendimentoDiversosDAO getAelAtendimentoDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}