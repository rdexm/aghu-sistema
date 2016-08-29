package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelLaboratorioExternosDAO;
import br.gov.mec.aghu.exames.dao.AelLaboratorioExternosJnDAO;
import br.gov.mec.aghu.exames.dao.AelRefCodeDAO;
import br.gov.mec.aghu.exames.dao.AghAtendimentosPacExternDAO;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AelLaboratorioExternosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class LaboratorioHemocentroRN extends BaseBusiness {

	@Inject
	private AghAtendimentosPacExternDAO aghAtendimentosPacExternDAO;
	
	private static final Log LOG = LogFactory.getLog(LaboratorioHemocentroRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelLaboratorioExternosJnDAO aelLaboratorioExternosJnDAO;
	
	@Inject
	private AelLaboratorioExternosDAO aelLaboratorioExternosDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelRefCodeDAO aelRefCodeDAO;

	private static final long serialVersionUID = 6240238959183735184L;
	private static final String FORMA_ENVIO_DOMINIO = "FORMA ENVIO";
	
	public enum LaboratorioHemocentroRNExceptionCode implements
			BusinessExceptionCode {

		AEL_01601,
		ERRO_GENERICO_BD_LABORATORIO_HEMOCENTRO, ATENDIMENTO_EXTERNO_EXISTENTE;//O valor do campo Forma Envio e invalido.

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	public void atualizar(AelLaboratorioExternos laboratorioExterno) throws ApplicationBusinessException {
		final AelLaboratorioExternos oldLaboratorioExterno = this.getAelLaboratorioExternosDAO().obterOriginal(laboratorioExterno.getSeq());
		
		this.preAtualizar(laboratorioExterno);
		this.getAelLaboratorioExternosDAO().merge(laboratorioExterno);
		this.getAelLaboratorioExternosDAO().flush();
		this.posAtualizar(laboratorioExterno, oldLaboratorioExterno);
	}
	
	public void inserir(AelLaboratorioExternos laboratorioExterno) throws BaseException {
		this.preInserir(laboratorioExterno);
		this.getAelLaboratorioExternosDAO().persistir(laboratorioExterno);
	}
	
	public void removerLaboratorioExterno(Integer seqExclusao) throws ApplicationBusinessException, BaseException {
		
		final AelLaboratorioExternos laboratorioExterno = this.getAelLaboratorioExternosDAO().obterPorChavePrimaria(seqExclusao);
		
		if (laboratorioExterno == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		if(this.getAghAtendimentosPacExternDAO().obterPorLaboratorioExterno(seqExclusao).size() > 0){
			throw new ApplicationBusinessException(LaboratorioHemocentroRNExceptionCode.ATENDIMENTO_EXTERNO_EXISTENTE);
		}
		
		final AelLaboratorioExternos oldLaboratorioExterno = this.getAelLaboratorioExternosDAO().obterOriginal(laboratorioExterno.getSeq());
		
		final String nome = laboratorioExterno.getNome();
		
		try {
			this.getAelLaboratorioExternosDAO().remover(laboratorioExterno);
			this.getAelLaboratorioExternosDAO().flush();
			this.posRemover(laboratorioExterno, oldLaboratorioExterno);
		} catch (PersistenceException e) {
			LOG.error("Exceção capturada: ", e);
			LaboratorioHemocentroRNExceptionCode.ERRO_GENERICO_BD_LABORATORIO_HEMOCENTRO.throwException(nome);
		}
	}
	/**
	 * @ORADB Trigger AELT_LAE_BRI
	 */
	protected void preInserir(AelLaboratorioExternos laboratorioExterno) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		laboratorioExterno.setCriadoEm(new Date());
		laboratorioExterno.setServidor(servidorLogado);
		if(laboratorioExterno.getFormaEnvio() != null 
				&& StringUtils.isNotBlank(laboratorioExterno.getFormaEnvio().toString())) {
			this.verificaFormaEnvio(laboratorioExterno);
		}
	}
	
	/**
	 * @ORADB Trigger AELT_LAE_BRU
	 */
	protected void preAtualizar(AelLaboratorioExternos laboratorioExterno) throws ApplicationBusinessException {
		final AelLaboratorioExternos oldLaboratorioExterno = this.getAelLaboratorioExternosDAO().obterOriginal(laboratorioExterno.getSeq());
		
		if((laboratorioExterno.getFormaEnvio() != null 
				&& StringUtils.isNotBlank(laboratorioExterno.getFormaEnvio().toString())) 
					&&  (laboratorioExterno.getFormaEnvio() != oldLaboratorioExterno.getFormaEnvio()
					|| oldLaboratorioExterno.getFormaEnvio() == null)) {
			
			this.verificaFormaEnvio(laboratorioExterno);
		}
	}
	
	/**
	 * @ORADB Trigger AELT_LAE_ARU
	 */
	private void posAtualizar(AelLaboratorioExternos laboratorioExterno, AelLaboratorioExternos oldLaboratorioExterno) throws ApplicationBusinessException {
		
		if(verificaAlteracao(laboratorioExterno, oldLaboratorioExterno)) {
			inserirLaboratorioExternoJn(oldLaboratorioExterno, DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * @ORADB Trigger AELT_LAE_ARD
	 * 
	 * @param laboratorioExterno
	 * @param oldLaboratorioExterno
	 * @throws ApplicationBusinessException 
	 */
	private void posRemover(AelLaboratorioExternos laboratorioExterno, AelLaboratorioExternos oldLaboratorioExterno) throws ApplicationBusinessException {
		inserirLaboratorioExternoJn(oldLaboratorioExterno, DominioOperacoesJournal.DEL);
	}
	
	private void verificaFormaEnvio(AelLaboratorioExternos laboratorioExterno) throws ApplicationBusinessException {
		if(this.getAelRefCodeDAO().verificarValorValido(laboratorioExterno.getFormaEnvio().toString(), FORMA_ENVIO_DOMINIO) == 0) {
			LaboratorioHemocentroRNExceptionCode.AEL_01601.throwException();
		}
	}
	
	private void inserirLaboratorioExternoJn(AelLaboratorioExternos oldLaboratorioExterno, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelLaboratorioExternosJn laboratorioExternoJn = BaseJournalFactory.getBaseJournal(operacao , AelLaboratorioExternosJn.class, servidorLogado.getUsuario());
						
		laboratorioExternoJn.setSeq(oldLaboratorioExterno.getSeq());
		laboratorioExternoJn.setNome(oldLaboratorioExterno.getNome());
		laboratorioExternoJn.setEndereco(oldLaboratorioExterno.getEndereco());
		laboratorioExternoJn.setTelefone(oldLaboratorioExterno.getTelefone());
		laboratorioExternoJn.setMail(oldLaboratorioExterno.getMail());
		laboratorioExternoJn.setPessoaContato(oldLaboratorioExterno.getPessoaContato());
		laboratorioExternoJn.setCidade(oldLaboratorioExterno.getCidade());
		laboratorioExternoJn.setCep(oldLaboratorioExterno.getCep());
		laboratorioExternoJn.setCgc(oldLaboratorioExterno.getCgc());
		laboratorioExternoJn.setSerMatricula(oldLaboratorioExterno.getServidor().getId().getMatricula());
		laboratorioExternoJn.setSerVinCodigo(oldLaboratorioExterno.getServidor().getId().getVinCodigo());
		laboratorioExternoJn.setCriadoEm(oldLaboratorioExterno.getCriadoEm());
		if(oldLaboratorioExterno.getFormaEnvio() != null 
				&& StringUtils.isNotBlank(oldLaboratorioExterno.getFormaEnvio().toString())) {
			laboratorioExternoJn.setFormaEnvio(oldLaboratorioExterno.getFormaEnvio().toString());
		}
		laboratorioExternoJn.setFax(oldLaboratorioExterno.getFax());
		laboratorioExternoJn.setCspCnvCodigo(oldLaboratorioExterno.getConvenio().getId().getCnvCodigo());
		laboratorioExternoJn.setCspSeq(oldLaboratorioExterno.getConvenio().getId().getSeq().shortValue());
		
		this.getAelLaboratorioExternosJnDAO().persistir(laboratorioExternoJn);
		this.getAelLaboratorioExternosJnDAO().flush();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private Boolean verificaAlteracao(AelLaboratorioExternos laboratorioExterno, AelLaboratorioExternos oldLaboratorioExterno) {
		
		if(CoreUtil.modificados(laboratorioExterno.getNome(), oldLaboratorioExterno.getNome())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getSigla(), oldLaboratorioExterno.getSigla())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getEndereco(), oldLaboratorioExterno.getEndereco())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getCidade(), oldLaboratorioExterno.getCidade())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getCep(), oldLaboratorioExterno.getCep())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getTelefone(), oldLaboratorioExterno.getTelefone())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getFax(), oldLaboratorioExterno.getFax())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getMail(), oldLaboratorioExterno.getMail())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getCgc(), oldLaboratorioExterno.getCgc())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getPessoaContato(), oldLaboratorioExterno.getPessoaContato())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getConvenio(), oldLaboratorioExterno.getConvenio())) {
			return Boolean.TRUE;
		}
		if(CoreUtil.modificados(laboratorioExterno.getFormaEnvio(), oldLaboratorioExterno.getFormaEnvio())) {
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	// getter do DAO
	protected AelLaboratorioExternosDAO getAelLaboratorioExternosDAO() {
		return aelLaboratorioExternosDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected AelRefCodeDAO getAelRefCodeDAO() {
		return aelRefCodeDAO;
	}
	
	protected AelLaboratorioExternosJnDAO getAelLaboratorioExternosJnDAO() {
		return aelLaboratorioExternosJnDAO;
	}
	
	protected AghAtendimentosPacExternDAO getAghAtendimentosPacExternDAO() {
		return aghAtendimentosPacExternDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
