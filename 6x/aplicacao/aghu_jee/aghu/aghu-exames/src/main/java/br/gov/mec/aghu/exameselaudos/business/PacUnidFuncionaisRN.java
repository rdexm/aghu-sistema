package br.gov.mec.aghu.exameselaudos.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelPacUnidFuncionaisDAO;
import br.gov.mec.aghu.exames.dao.AelPacUnidFuncionaisJnDAO;
import br.gov.mec.aghu.exames.dao.AelProtocoloInternoUnidsDAO;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelPacUnidFuncionaisJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PacUnidFuncionaisRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PacUnidFuncionaisRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelPacUnidFuncionaisDAO aelPacUnidFuncionaisDAO;
	
	@Inject
	private AelPacUnidFuncionaisJnDAO aelPacUnidFuncionaisJnDAO;

	@Inject
	private AelProtocoloInternoUnidsDAO aelProtocoloInternoUnidsDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6809516944831880222L;

	/**
	 * @ORADB Trigger AELT_PUF_ARI (operacao INS)
	 * @ORADB Trigger AELT_PUF_ARD (operacao DEL)
	 * 
	 * @param puf
	 * @param operacao
	 * @throws ApplicationBusinessException 
	 */
	private void inserirJournal(AelPacUnidFuncionais puf,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelPacUnidFuncionaisJn jn = BaseJournalFactory.getBaseJournal(
				operacao, AelPacUnidFuncionaisJn.class, servidorLogado.getUsuario());

		jn.setPiuPacCodigo(puf.getAelProtocoloInternoUnids().getId()
				.getPacCodigo());
		jn.setPiuUnfSeq(puf.getAelProtocoloInternoUnids().getId()
				.getUnidadeFuncional().getSeq());
		jn.setSeqp(puf.getId().getSeqp());
		jn.setUnfExecutaExames(puf.getUnfExecutaExames());
		jn.setCriadoEm(puf.getCriadoEm());
		jn.setServidor(puf.getServidor());
		jn.setItemSolicitacaoExames(puf.getItemSolicitacaoExames());
		jn.setDtExecucao(puf.getDtExecucao());
		jn.setIdentificadorComplementar(puf.getIdentificadorComplementar());
		jn.setCondicaoPac(puf.getCondicaoPac());
		jn.setNroFilme(puf.getNroFilme());
		jn.setObservacao(puf.getObservacao());
		jn.setServidorAlterado(puf.getServidorAlterado());
		jn.setAlteradoEm(puf.getAlteradoEm());
		jn.setEquipamento(puf.getEquipamento());

		this.getAelPacUnidFuncionaisJnDAO().persistir(jn);
		this.getAelPacUnidFuncionaisJnDAO().flush();

	}

	// ORADB Triggers AELT_PUF_BRI e AELT_PUF_BASE_BRI
	private void preInserir(AelPacUnidFuncionais puf)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		puf.setServidor(servidorLogado); // aelk_ael_rn.rn_aelp_atu_servidor
		puf.setServidorAlterado(servidorLogado); // aelk_ael_rn.rn_aelp_atu_servidor
		Date agora = new Date();
		puf.setCriadoEm(agora);
		puf.setAlteradoEm(agora);
		// VER INTEGRIDADE COM AEL_ITEM_SOLICITACAO_EXAMES
		if(puf.getItemSolicitacaoExames() != null) {
			this.getAelItemSolicitacaoExameDAO().verificaItemSolicitacaoExames(
				puf.getItemSolicitacaoExames().getId().getSoeSeq(), 
				puf.getItemSolicitacaoExames().getId().getSeqp(), 
				servidorLogado != null ? servidorLogado.getUsuario() : null); // fatp_ver_item_solic
		}
	}

	// Executa triggers de insert e faz a inserção
	public void inserir(AelPacUnidFuncionais puf) throws ApplicationBusinessException {
		
		// Evita o erro Call to TraversableResolver.isReachable()
		puf.setAelProtocoloInternoUnids(getAelProtocoloInternoUnidsDAO().obterPorChavePrimaria(puf.getAelProtocoloInternoUnids().getId()));
		
		preInserir(puf);
		this.getAelPacUnidFuncionaisDAO().persistir(puf);
		puf.getId().setUnidadeFuncional(puf.getAelProtocoloInternoUnids().getUnidadeFuncional());
		this.getAelPacUnidFuncionaisDAO().flush();
		inserirJournal(puf, DominioOperacoesJournal.INS);
	}
	

	// Executa triggers de delete e faz a exclusão
	public void excluir(AelPacUnidFuncionais puf) throws ApplicationBusinessException {
		puf = this.getAelPacUnidFuncionaisDAO().obterPorChavePrimaria(puf.getId());
		this.getAelPacUnidFuncionaisDAO().remover(puf);
		this.getAelPacUnidFuncionaisDAO().flush();
		inserirJournal(puf, DominioOperacoesJournal.DEL);
	}
	
	public void atualizar(AelPacUnidFuncionais elemento) throws ApplicationBusinessException {
		this.getAelPacUnidFuncionaisDAO().atualizar(elemento);
	}
	
	
	/** GET **/
	protected AelPacUnidFuncionaisJnDAO getAelPacUnidFuncionaisJnDAO() {
		return aelPacUnidFuncionaisJnDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelPacUnidFuncionaisDAO getAelPacUnidFuncionaisDAO() {
		return aelPacUnidFuncionaisDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public AelProtocoloInternoUnidsDAO getAelProtocoloInternoUnidsDAO() {
		return aelProtocoloInternoUnidsDAO;
	}

}
