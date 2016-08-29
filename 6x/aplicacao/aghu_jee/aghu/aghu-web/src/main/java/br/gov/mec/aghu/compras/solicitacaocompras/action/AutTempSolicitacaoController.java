package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutTempSolicita;
import br.gov.mec.aghu.model.ScoAutTempSolicitaId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class AutTempSolicitacaoController extends ActionController{

	
	private static final long serialVersionUID = 1102428187259308449L;


	private static final String FORNECER_DIR_SOLICITAR_LIST = "fornecerDirSolicitarList";

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	protected IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private boolean visualizar;
	
	private ScoAutTempSolicita solAutTemp;
	private ScoAutTempSolicitaId solAutTempId;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar(){
	 

	 

		if(solAutTempId != null && solAutTempId.getDtInicio() != null){
			solAutTemp = solicitacaoComprasFacade.obterScoAutTempSolicitaFull(solAutTempId);

			if(solAutTemp == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
			
		} else {
			solAutTemp = new ScoAutTempSolicita();
			solAutTemp.setId(new ScoAutTempSolicitaId());
		}
		
		return null;
	
	}

	public String salvar() {
		try {
			// verificar se o centro de custo e o mesmo do que a pessoa esta inserindo ou editando
			List<FccCentroCustos> listResult = new ArrayList<FccCentroCustos>();
			listResult = this.centroCustoFacade.pesquisarCentroCustosServidor(null);
			
			if (listResult!=null && listResult.contains(this.solAutTemp.getId().getFccCentroCustos())){
				this.solicitacaoComprasFacade.cadastrarAutTempSolicitacao(this.solAutTemp);
				
				
				if (this.solAutTemp.getVersion()>0 ) {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ALTERACAO_FOR_SOL" );
				} else {
					this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUSAO_FOR_SOL" );
				}
			} else{
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RN_CENTRO_CUSTO_FOR_SOL" );
			}
			
			return voltar();
	
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}	
	}
	
	
	public String voltar() {
		solAutTemp = null;
		solAutTempId = null;
		visualizar = false;
		return FORNECER_DIR_SOLICITAR_LIST;
	}

	
	public List<FccCentroCustos> listarCentroCustosSolic(String objPesquisa) {
		return  this.returnSGWithCount(this.centroCustoFacade.pesquisarCentroCustos(objPesquisa),listarCentroCustosSolicCount(objPesquisa));
	}
	
	public Long listarCentroCustosSolicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}
	
	public List<RapServidores> listarServidores(Object objPesquisa) {
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return this.registroColaboradorFacade.pesquisarServidor(objPesquisa);
		}else {return this.registroColaboradorFacade.pesquisarRapServidores();}
	}
	
	public Long listarServidoresCount(Object objPesquisa) {
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return this.registroColaboradorFacade.pesquisarServidoresCount(objPesquisa);
		}else {return this.registroColaboradorFacade.pesquisarRapServidoresCount();}
	}
	
	
	// Metódo para Suggestion Box de Servidor
	public List<RapServidores> obterServidorVinculado(String objPesquisa) {

		try {
			return  this.registroColaboradorFacade
					.pesquisarServidoresVinculados(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public ScoAutTempSolicita getSolAutTemp() {
		return solAutTemp;
	}

	public void setSolAutTemp(ScoAutTempSolicita solAutTemp) {
		this.solAutTemp = solAutTemp;
	}

	public ScoAutTempSolicitaId getSolAutTempId() {
		return solAutTempId;
	}

	public void setSolAutTempId(ScoAutTempSolicitaId solAutTempId) {
		this.solAutTempId = solAutTempId;
	}
}
