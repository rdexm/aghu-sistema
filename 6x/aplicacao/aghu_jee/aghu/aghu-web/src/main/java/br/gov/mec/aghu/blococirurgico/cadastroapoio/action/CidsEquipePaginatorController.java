package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.MbcCidUsualEquipe;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class CidsEquipePaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcCidUsualEquipe> dataModel;

	private static final long serialVersionUID = 1672068915368915130L;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private static final String CIDS_EQUIPE_LIST = "cidsEquipeList";
	
	//suggestion aghEquipe
	private AghEquipes equipe;
	
	//objeto para persistencia
	private MbcCidUsualEquipe mbcCidUsualEquipe = new MbcCidUsualEquipe();

	private Boolean ativo;
	
	public void iniciar() {
		this.ativo = Boolean.TRUE;		
	}
	
	@Override
	public List<MbcCidUsualEquipe> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoFacade.pesquisarCidUsualEquipe(
				firstResult, maxResult, orderProperty, asc, this.equipe.getSeq().shortValue());
	}

	@Override
	public Long recuperarCount() {
		return  this.blocoCirurgicoFacade.pesquisarCidUsualEquipeCount(
				this.equipe.getSeq().shortValue());
	}
	
	public String pesquisar() {
		//this.setOrder("descricao asc");
		this.mbcCidUsualEquipe.setAghEquipes(this.equipe);
		this.setAtivo(true);
		this.dataModel.reiniciarPaginator();
		return CIDS_EQUIPE_LIST;
	}
	
	public String gravar() {
		try {
			this.mbcCidUsualEquipe.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			this.atribuirSituacao();
			this.blocoCirurgicoCadastroApoioFacade.inserirCidUsualEquipe(this.mbcCidUsualEquipe);

			//Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_CIDS_EQUIPE", 
					this.mbcCidUsualEquipe.getAghCid().getDescricao());
			this.limparCampos();
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return this.pesquisar();
	}

	public String atualizar(MbcCidUsualEquipe item, Boolean ativar) {
		try {
			item.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			if(ativar) {
				item.setIndSituacao(DominioSituacao.A);
			}else {
				item.setIndSituacao(DominioSituacao.I);
			}
			
			this.blocoCirurgicoCadastroApoioFacade.atualizarCidUsualEquipe(item);
			//Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CIDS_EQUIPE", 
					item.getAghCid().getDescricao());
			this.limparCampos();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return this.pesquisar();
	}
		
	/**
	 * Suggestion Box de Grupo Resultado Codificado
	 * @param objPesquisa
	 * @return
	 */
	public List<AghEquipes> pesquisarSuggestionEquipe(String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.getPesquisaEquipesAtivas((String) objPesquisa),pesquisarSuggestionEquipeCount(objPesquisa));
	}
	
	public Long pesquisarSuggestionEquipeCount(String objPesquisa) {
		return this.aghuFacade.getListaEquipesAtivasCount((String) objPesquisa);
	}
	
	public String limpar() {
		this.limparCampos();
		this.setAtivo(false);
		this.dataModel.setPesquisaAtiva(false);
		return CIDS_EQUIPE_LIST;
	}
	
	public void limparCampos() {
		this.mbcCidUsualEquipe = new MbcCidUsualEquipe();
	}
	
	public List<AghCid> pesquisarSuggestionCid(String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.obterCidPorNomeCodigoAtivaPaginado((String) objPesquisa),pesquisarSuggestionCidCount(objPesquisa));
	}

	public Long pesquisarSuggestionCidCount(String objPesquisa) {
		return this.aghuFacade.pesquisarPorNomeCodigoAtivaCount((String) objPesquisa);
	}
	
	public Boolean verificarAtivo(MbcCidUsualEquipe item) {
		if(DominioSituacao.A == item.getIndSituacao()) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	public void atribuirSituacao() {
		if(this.ativo) {
			this.mbcCidUsualEquipe.setIndSituacao(DominioSituacao.A);
		}else {
			this.mbcCidUsualEquipe.setIndSituacao(DominioSituacao.I);
		}
	}
	
	/** GET/SET **/
	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public MbcCidUsualEquipe getMbcCidUsualEquipe() {
		return mbcCidUsualEquipe;
	}

	public void setMbcCidUsualEquipe(MbcCidUsualEquipe mbcCidUsualEquipe) {
		this.mbcCidUsualEquipe = mbcCidUsualEquipe;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public DynamicDataModel<MbcCidUsualEquipe> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcCidUsualEquipe> dataModel) {
	 this.dataModel = dataModel;
	}
}