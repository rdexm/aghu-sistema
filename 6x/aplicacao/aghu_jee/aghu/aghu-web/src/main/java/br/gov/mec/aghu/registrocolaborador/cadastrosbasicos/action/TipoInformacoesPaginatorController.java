package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class TipoInformacoesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2359785741312139428L;

	private static final String CADASTRAR_TIPO_INFORMACOES = "cadastrarTipoInformacoes";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapTipoInformacoes rapTipoInformacao = new RapTipoInformacoes();
	
	@Inject @Paginator
	private DynamicDataModel<RapTipoInformacoes> dataModel;
	
	private RapTipoInformacoes selecionado;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
		
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosFacade.obterTipoInformacoesCount(rapTipoInformacao.getSeq(), rapTipoInformacao.getDescricao());
	}

	@Override
	public List<RapTipoInformacoes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosFacade.pesquisarTipoInformacoes(firstResult, maxResult, rapTipoInformacao.getSeq(), rapTipoInformacao.getDescricao());
	}	

	public String editar(){
		return CADASTRAR_TIPO_INFORMACOES;
	}

	public String inserir(){
		return CADASTRAR_TIPO_INFORMACOES;
	}
	
	public void excluir() {
		try {
			cadastrosBasicosFacade.excluirTipoInformacoes(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_TIPO_INFORMACAO");						
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}

	public void limparPesquisa() {
		this.rapTipoInformacao = new RapTipoInformacoes();
		dataModel.limparPesquisa();
	}

	public RapTipoInformacoes getRapTipoInformacao() {
		return rapTipoInformacao;
	}

	public void setRapTipoInformacao(RapTipoInformacoes rapTipoInformacao) {
		this.rapTipoInformacao = rapTipoInformacao;
	}

	public DynamicDataModel<RapTipoInformacoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapTipoInformacoes> dataModel) {
		this.dataModel = dataModel;
	}

	public RapTipoInformacoes getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RapTipoInformacoes selecionado) {
		this.selecionado = selecionado;
	}
}