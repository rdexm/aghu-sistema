package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtSolicTempDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtSolicTempJnDAO;
import br.gov.mec.aghu.model.PdtSolicTemp;
import br.gov.mec.aghu.model.PdtSolicTempJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class PdtSolicTempRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtSolicTempRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtSolicTempJnDAO pdtSolicTempJnDAO;

	@Inject
	private PdtSolicTempDAO pdtSolicTempDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 8983961909121675821L;

	
	public void persistir(PdtSolicTemp solicitacao) {
		if(solicitacao.getVersion() == null) {
			this.inserir(solicitacao);
		} else {
			this.atualizar(solicitacao);
		}
	}
	
	protected void inserir(PdtSolicTemp solicitacao) {
		this.preInserir(solicitacao);
		getPdtSolicTempDAO().persistir(solicitacao);
		
	}
	
	protected void atualizar(PdtSolicTemp solicitacao) {
		this.preAtualizar(solicitacao);
		getPdtSolicTempDAO().atualizar(solicitacao);
		this.posAtualizar(solicitacao);
	}
	
	/**
	 * @ORADB PDT_SOLIC_TEMPS . PDTT_SPT_BRI
	 */
	protected void preInserir(PdtSolicTemp solicitacao ) {
		solicitacao.setCriadoEm(new Date());
		/*atualiza servidor que incluiu registro */
		solicitacao.setServidor(servidorLogadoFacade.obterServidorLogado());
	}
	

	/**
	 * @ORADB PDT_SOLIC_TEMPS . PDTT_SPT_BRU
	 */
	protected void preAtualizar(PdtSolicTemp solicitacao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		/*atualiza servidor que incluiu registro */
		solicitacao.setServidor(servidorLogado);
	}

	/**
	 * @ORADB PDT_SOLIC_TEMPS . PDTT_SPT_ARU
	 */
	protected void posAtualizar(PdtSolicTemp solicitacao) {
		PdtSolicTemp oldSolic = getPdtSolicTempDAO().obterOriginal(solicitacao);
		if(CoreUtil.modificados(solicitacao.getMotivo(), oldSolic.getMotivo())
				|| CoreUtil.modificados(solicitacao.getMedicamentos(), oldSolic.getMedicamentos())
				|| CoreUtil.modificados(solicitacao.getObservacoes(), oldSolic.getObservacoes())
				|| CoreUtil.modificados(solicitacao.getSolicitadoPor(), oldSolic.getSolicitadoPor())
				|| CoreUtil.modificados(solicitacao.getCriadoEm(), oldSolic.getCriadoEm())
				|| CoreUtil.modificados(solicitacao.getServidor(), oldSolic.getServidor())) {
			this.inserirJournal(oldSolic, DominioOperacoesJournal.UPD);
		}
	}

	public void excluir(PdtSolicTemp solicitacao){
		solicitacao = getPdtSolicTempDAO().obterPorChavePrimaria(solicitacao.getSeq());
		getPdtSolicTempDAO().remover(solicitacao);
		posDeletar(solicitacao);
	}
	
	/**
	 * @ORADB PDT_SOLIC_TEMPS . PDTT_SPT_ARD
	 */
	protected void posDeletar(PdtSolicTemp solicitacao) {
		this.inserirJournal(solicitacao, DominioOperacoesJournal.DEL);
	}

	private void inserirJournal(PdtSolicTemp oldSolic, DominioOperacoesJournal operacaoJournal) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		PdtSolicTempJn jn = BaseJournalFactory.getBaseJournal(
				operacaoJournal, PdtSolicTempJn.class, servidorLogado.getUsuario());
		jn.setDdtSeq(oldSolic.getDdtSeq());
		jn.setMedicamentos(oldSolic.getMedicamentos());
		jn.setMotivo(oldSolic.getMotivo());
		jn.setObservacoes(oldSolic.getObservacoes());
		jn.setSolicitadoPor(oldSolic.getSolicitadoPor());
		jn.setCriadoEm(oldSolic.getCriadoEm());
		jn.setServidor(oldSolic.getServidor());
		
		getPdtSolicTempJnDAO().persistir(jn);
	}

	protected PdtSolicTempDAO getPdtSolicTempDAO() {
		return pdtSolicTempDAO;
	}

	protected PdtSolicTempJnDAO getPdtSolicTempJnDAO() {
		return pdtSolicTempJnDAO;
	}
}
