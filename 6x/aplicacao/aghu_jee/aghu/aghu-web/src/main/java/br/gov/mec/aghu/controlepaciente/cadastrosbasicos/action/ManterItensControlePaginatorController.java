package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterItensControlePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 9048945810660005816L;

	private static final String MANTER_ITENS_CONTROLE = "manterItensControle";
	private static final String ASSOCIAR_ITENS_PRESCRICAO = "associarItensPrescricao";
	private static final String PESQUISAR_LIMITES_ITEM_CONTROLE = "pesquisarLimitesItemControle";

	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;

	// filtros de pesquisa
	private String sigla;
	private String descricao;
	private DominioSituacao situacao;
	private EcpGrupoControle grupo;

	private EcpItemControle selecionado;
	
	@Inject @Paginator
	private DynamicDataModel<EcpItemControle> dataModel;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public List<EcpItemControle> recuperarListaPaginada(Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosControlePacienteFacade.pesquisarItens(firstResult, maxResult, orderProperty, asc,sigla, descricao, grupo, situacao);
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosControlePacienteFacade.pesquisarItensCount(sigla, descricao, grupo, situacao);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void excluir() {
		try {
			cadastrosBasicosControlePacienteFacade.excluir(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_ITEM_CONTROLE",selecionado.getDescricao());					

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String editar(){
		return MANTER_ITENS_CONTROLE;
	}

	public String inserir(){
		return MANTER_ITENS_CONTROLE;
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		this.sigla = null;
		this.descricao = null;
		this.situacao = null;
		this.grupo = null;
	}

	public String associarItensPrescricao(){
		return ASSOCIAR_ITENS_PRESCRICAO;
	}
	
	public String pesquisarLimitesItemControle(){
		return PESQUISAR_LIMITES_ITEM_CONTROLE;
	}
	
	public List<EcpGrupoControle> getListaGruposControleAtivos() {
		return cadastrosBasicosControlePacienteFacade.listarGruposControleAtivos();
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public EcpGrupoControle getGrupo() {
		return grupo;
	}

	public void setGrupo(EcpGrupoControle grupo) {
		this.grupo = grupo;
	}

	public DynamicDataModel<EcpItemControle> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EcpItemControle> dataModel) {
		this.dataModel = dataModel;
	}

	public EcpItemControle getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(EcpItemControle selecionado) {
		this.selecionado = selecionado;
	}
}