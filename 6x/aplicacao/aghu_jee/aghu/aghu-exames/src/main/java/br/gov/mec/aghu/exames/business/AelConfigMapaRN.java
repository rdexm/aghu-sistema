package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.exames.dao.AelConfigMapaDAO;
import br.gov.mec.aghu.exames.dao.AelConfigMapaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelConfigMapaJnDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelConfigMapaExames;
import br.gov.mec.aghu.model.AelConfigMapaJn;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
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
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class AelConfigMapaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelConfigMapaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelConfigMapaExamesDAO aelConfigMapaExamesDAO;
	
	@Inject
	private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;
	
	@Inject
	private AelConfigMapaJnDAO aelConfigMapaJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelConfigMapaDAO aelConfigMapaDAO;

	private static final long serialVersionUID = 7105275324002030883L;
	

	public enum AelConfigMapaRNExceptionCode implements BusinessExceptionCode {
		AEL_CONFIG_MAPA_COM_DEPENDENCIA
	}	
	

	/**
	 * ORADB AELT_CGM_BRI (INSERT)
	 * @throws BaseException
	 */
	private void preInserir(AelConfigMapa aelConfigMapa) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelConfigMapa.setCriadoEm(new Date());//RN1
		
		// Substitui aelk_ael_rn.rn_aelp_atu_servidor
		aelConfigMapa.setRapServidores(servidorLogado);//RN2
	}
	
	public void inserir(AelConfigMapa aelConfigMapa) throws BaseException{
		final AelConfigMapaDAO dao = this.getAelConfigMapaDAO();
		
		aelConfigMapa.setIndEmite(DominioSimNao.S);
		preInserir(aelConfigMapa);		
		
		dao.persistir(aelConfigMapa);
		dao.flush();
	}
	
	public void persistir(AelConfigMapa aelConfigMapa) throws BaseException{
		if(aelConfigMapa.getSeq() != null ){
			this.atualizar(aelConfigMapa);
		} else {
			this.inserir(aelConfigMapa);
		}
	}
	
	/**
	 * Atualiza AelNotaAdicional
	 */
	public void atualizar(AelConfigMapa aelConfigMapa) throws BaseException{
		
		// AELT_CGM_BRU
		// this.preAtualizar(aelConfigMapa);	// Na trigger efetua o calculo do version, não sendo necessário migrar.
		final AelConfigMapaDAO dao = this.getAelConfigMapaDAO(); 
		dao.merge(aelConfigMapa);
		this.posAtualizar(aelConfigMapa);
	} 
	
	/**
	 * ORADB: AELT_CGM_ARU
	 * @param aelConfigMapa
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizar(AelConfigMapa aelConfigMapa) throws ApplicationBusinessException {
		final AelConfigMapa original = getAelConfigMapaDAO().obterOriginal(aelConfigMapa);
		
		if(CoreUtil.modificados(aelConfigMapa.getRapServidores(), original.getRapServidores()) ||
				CoreUtil.modificados(aelConfigMapa.getAghUnidadesFuncionais(), original.getAghUnidadesFuncionais()) ||
				CoreUtil.modificados(aelConfigMapa.getNomeMapa(), original.getNomeMapa()) ||
				CoreUtil.modificados(aelConfigMapa.getIndEmite(), original.getIndEmite()) ||
				CoreUtil.modificados(aelConfigMapa.getOrigem(), original.getOrigem()) ||
				CoreUtil.modificados(aelConfigMapa.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(aelConfigMapa.getIndSituacao(), original.getIndSituacao()) ||
				CoreUtil.modificados(aelConfigMapa.getReport(), original.getReport()) 
		){
			createJournal(aelConfigMapa, DominioOperacoesJournal.UPD);
		}
	}

	/**
	 * ORADB AELT_CGM_ARD
	 * @param notaAdicional
	 * @throws BaseException
	 */
	private void posRemover(AelConfigMapa aelConfigMapa) throws BaseException{
		createJournal(aelConfigMapa, DominioOperacoesJournal.DEL);
	}
	
	/**
	 * Atualiza AelConfigMapa
	 */
	public void remover(Short seq) throws BaseException{
		final AelConfigMapaDAO dao = this.getAelConfigMapaDAO(); 
		AelConfigMapa aelConfigMapa = dao.obterPorChavePrimaria(seq);
		
		if (aelConfigMapa == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.preExcluir(aelConfigMapa);
		dao.remover(aelConfigMapa);
		this.posRemover(aelConfigMapa);		
	}

	private void preExcluir(AelConfigMapa aelConfigMapa) throws ApplicationBusinessException {
		final List<AelConfigMapaExames> registroExistente = getAelConfigMapaExamesDAO().pesquisarAelConfigMapaExamesPorAelConfigMapa(aelConfigMapa);
		if(registroExistente != null && !registroExistente.isEmpty()){
			throw new ApplicationBusinessException(AelConfigMapaRNExceptionCode.AEL_CONFIG_MAPA_COM_DEPENDENCIA, aelConfigMapa.getNomeMapa());
		}
	}

	private void createJournal(final AelConfigMapa reg, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final AelConfigMapaJn journal = BaseJournalFactory.getBaseJournal(operacao, AelConfigMapaJn.class, servidorLogado.getUsuario());
		
		journal.setSeq(reg.getSeq());
		journal.setRapServidores(reg.getRapServidores());
		journal.setAghUnidadesFuncionais(reg.getAghUnidadesFuncionais());
		journal.setNomeMapa(reg.getNomeMapa());
		journal.setIndEmite(reg.getIndEmite());
		journal.setOrigem(reg.getOrigem());
		journal.setCriadoEm(reg.getCriadoEm());
		journal.setIndSituacao(reg.getIndSituacao());
		journal.setReport(reg.getReport());
		
		getAelConfigMapaJnDAO().persistir(journal);
	}
	
	/**
	 * ORADB Function AELC_LAUDO_IDD_PAC
	 * 
	 * @param soeSeq
	 * @return
	 */
	public Integer laudoIdadePaciente(Integer soeSeq) {
		
		AelSolicitacaoExames solicitacaoExame = getAelSolicitacaoExameDAO().obterPorChavePrimaria(soeSeq);

		Integer atdSeq = null;
		Integer atvSeq = null;
		Integer idade = null;
		Integer idadeAtv = null;
		
		if (solicitacaoExame == null) {
			idade = null;
		}
		else {
			atdSeq = (solicitacaoExame.getAtendimento() != null ) ? solicitacaoExame.getAtendimento().getSeq() : null;
			atvSeq = (solicitacaoExame.getAtendimentoDiverso() != null)?solicitacaoExame.getAtendimentoDiverso().getSeq() : null;
		}
		
		if (atdSeq != null) {
			AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeq);
			idade = atendimento.getPaciente().getIdade();
		}
		else {
			if (atvSeq == null) {
				idade = null;
			}
			AelAtendimentoDiversos atendimentoDiverso = getAelAtendimentoDiversosDAO().obterPorChavePrimaria(atvSeq);

			idadeAtv = DateUtil.obterQtdAnosEntreDuasDatas(atendimentoDiverso.getDtNascimento(), new Date());
			
			if (atendimentoDiverso.getAbsCandidatosDoadores() == null) {
				idade = null;
			}
			else if (atendimentoDiverso.getAipPaciente() == null) {
				idade = null;
			}
			else if (idadeAtv != null) {
				idade = idadeAtv;
			}
			else {
				idade = null;
			}
		}
		
		
		return idade;
	}
	
	private AelConfigMapaJnDAO getAelConfigMapaJnDAO() {
		return aelConfigMapaJnDAO;
	}

	protected AelConfigMapaDAO getAelConfigMapaDAO() {
		return aelConfigMapaDAO;
	}
	
	protected AelConfigMapaExamesDAO getAelConfigMapaExamesDAO() {
		return aelConfigMapaExamesDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	protected AelAtendimentoDiversosDAO getAelAtendimentoDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}