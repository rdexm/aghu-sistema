package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.MptTipoIntercorrencia;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.VincularIntercorrenciaTipoSessaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class VincularIntercorrenciaTipoSessaoPaginatorController extends
		ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8196087541013900952L;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@Inject @Paginator
	private DynamicDataModel<VincularIntercorrenciaTipoSessaoVO> dataModel;
	
	private List<MptTipoSessao> listaTipoSessao;
	private MptTipoSessao tipoSessaoCombo1;
	private MptTipoSessao tipoSessaoCombo2;
	private String descricaoIntercorrencia;
	private MptTipoIntercorrencia tipoIntercorrencia;
	private VincularIntercorrenciaTipoSessaoVO itemExcluir;
	private VincularIntercorrenciaTipoSessaoVO selecionado;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){	
		carregarCombosTipoSessao();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		tipoSessaoCombo1 = null;
		limparCamposAdicionar();
		descricaoIntercorrencia = null;
		itemExcluir = null;
		selecionado = null;
		listaTipoSessao = null;
		this.dataModel.limparPesquisa();
		carregarCombosTipoSessao();
		pesquisar();
	}
	
	public void adicionar(){
		try {
			if(tipoIntercorrencia != null && tipoSessaoCombo2 != null){
				boolean gravou = procedimentoTerapeuticoFacade.adicionarVinculoIntercorrrenciaTipoSessao(tipoIntercorrencia, tipoSessaoCombo2);
				limparCamposAdicionar();
				if(gravou){				
					pesquisar();
					apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_INCLUSAO_VINCULO_INTERCOR");
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void abrirModalExclusao(){
		openDialog("modalConfirmacaoExclusaoWG");
	}
	
	public void excluir(){
		boolean gravou = procedimentoTerapeuticoFacade.excluirVinculoIntercorrrenciaTipoSessao(itemExcluir.getSeqTipoIntercor());
		if(gravou){
			apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_EXCLUSAO_VINCULO_INTERCOR");				
		}else{
			apresentarMsgNegocio(Severity.INFO, "MSG_ERRO_EXCLUSAO_INTERCOR");
		}
	}
	
	//SuggestionBox Intercorrência
	public List<MptTipoIntercorrencia> obterTipoIntercorrencia(String pesquisa){
		return returnSGWithCount(procedimentoTerapeuticoFacade.carregarTiposIntercorrencia(pesquisa), procedimentoTerapeuticoFacade.carregarTiposIntercorrenciaCount(pesquisa));
	}
	
	//Pesquisa Principal
	@Override
	public List<VincularIntercorrenciaTipoSessaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return procedimentoTerapeuticoFacade.carregarVinculosTipoIntercorreciaTipoSessao(tipoSessaoCombo1, descricaoIntercorrencia, firstResult, maxResults);
	}
	
	@Override
	public Long recuperarCount() {
		return this.procedimentoTerapeuticoFacade.carregarVinculosTipoIntercorreciaTipoSessaoCount(this.tipoSessaoCombo1, this.descricaoIntercorrencia);
	}
	
	//Carrega a lista dos combos de tipo de sessão
	public void carregarCombosTipoSessao(){
		listaTipoSessao = procedimentoTerapeuticoFacade.pesquisarTipoSessoesAtivas();
	}
	
	public void limparCamposAdicionar(){
		this.tipoSessaoCombo2 = null;
		this.tipoIntercorrencia = null;
	}
	
	//Getter e Setters
	public DynamicDataModel<VincularIntercorrenciaTipoSessaoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<VincularIntercorrenciaTipoSessaoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public List<MptTipoSessao> getListaTipoSessao() {
		return listaTipoSessao;
	}

	public void setListaTipoSessao(List<MptTipoSessao> listaTipoSessao) {
		this.listaTipoSessao = listaTipoSessao;
	}

	public MptTipoSessao getTipoSessaoCombo1() {
		return tipoSessaoCombo1;
	}

	public void setTipoSessaoCombo1(MptTipoSessao tipoSessaoCombo1) {
		this.tipoSessaoCombo1 = tipoSessaoCombo1;
	}

	public MptTipoSessao getTipoSessaoCombo2() {
		return tipoSessaoCombo2;
	}

	public void setTipoSessaoCombo2(MptTipoSessao tipoSessaoCombo2) {
		this.tipoSessaoCombo2 = tipoSessaoCombo2;
	}

	public String getDescricaoIntercorrencia() {
		return descricaoIntercorrencia;
	}

	public void setDescricaoIntercorrencia(String descricaoIntercorrencia) {
		this.descricaoIntercorrencia = descricaoIntercorrencia;
	}

	public MptTipoIntercorrencia getTipoIntercorrencia() {
		return tipoIntercorrencia;
	}

	public void setTipoIntercorrencia(MptTipoIntercorrencia tipoIntercorrencia) {
		this.tipoIntercorrencia = tipoIntercorrencia;
	}

	public VincularIntercorrenciaTipoSessaoVO getItemExcluir() {
		return itemExcluir;
	}

	public void setItemExcluir(VincularIntercorrenciaTipoSessaoVO itemExcluir) {
		this.itemExcluir = itemExcluir;
	}

	public VincularIntercorrenciaTipoSessaoVO getSelecionado() {
		return selecionado;
	}
	public void setSelecionado(VincularIntercorrenciaTipoSessaoVO selecionado) {
		this.selecionado = selecionado;
	}

}
