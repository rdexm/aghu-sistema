package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacao;
import br.gov.mec.aghu.model.ScoCaminhoSolicitacaoID;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class CaminhoSolicitacaoController extends ActionController {

	private static final Log LOG = LogFactory.getLog(CaminhoSolicitacaoController.class);

	private static final long serialVersionUID = 4731533190708355034L;

	private static final String CAMINHOS_LIST = "caminhosList";

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private ScoCaminhoSolicitacao caminhoSolicitacao;
	private boolean visualizar;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar(){
	 

	 

		if(caminhoSolicitacao != null && caminhoSolicitacao.getId() != null){
			caminhoSolicitacao = solicitacaoComprasFacade.obterCaminhoSolicitacao(caminhoSolicitacao.getId());

			if(caminhoSolicitacao == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			caminhoSolicitacao = new ScoCaminhoSolicitacao();
			caminhoSolicitacao.setId(new ScoCaminhoSolicitacaoID());
		}
		return null;
	
	}
	
	public String gravar() {
		try {
			if (this.getCaminhoSolicitacao() != null) {
				ScoCaminhoSolicitacaoID caminhoSolicitacaoID = new ScoCaminhoSolicitacaoID();
				caminhoSolicitacaoID.setPpsCodigoInicio(this.caminhoSolicitacao.getPontoParadaOrigem().getCodigo());
				caminhoSolicitacaoID.setPpsCodigo(this.caminhoSolicitacao.getPontoParadaDestino().getCodigo());
				
				this.caminhoSolicitacao.setId(caminhoSolicitacaoID);
				this.solicitacaoComprasFacade.inserirCaminhoSolicitacao(this.getCaminhoSolicitacao());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CAMINHO_SOLIC_INSERT_SUCESSO");
				
				return cancelar();
			}
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		caminhoSolicitacao = null;
		visualizar = false;
		return CAMINHOS_LIST;
	}

	//Método para carregar suggestion Origem e Destino
	public List<ScoPontoParadaSolicitacao> pesquisarOrigDestParada(String pontoParadaSolic) {
		return this.comprasCadastrosBasicosFacade.pesquisarPontoParadaSolicitacaoPorCodigoOuDescricaoAtivos((String)pontoParadaSolic);
	}
	
	public ScoCaminhoSolicitacao getCaminhoSolicitacao() {
		return caminhoSolicitacao;
	}

	public void setCaminhoSolicitacao(ScoCaminhoSolicitacao caminhoSolicitacao) {
		this.caminhoSolicitacao = caminhoSolicitacao;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public boolean isVisualizar() {
		return visualizar;
	}
}
