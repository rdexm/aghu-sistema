package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoMaterialAnalisePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1160156098712770193L;

	private static final String GRUPO_MATERIAL_ANALISE_CRUD = "grupoMaterialAnaliseCRUD";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Campos de filtro para pesquisa
	private Integer codigo;
	private String nome;
	private Integer ordem;
	private DominioSituacao situacao;

	@Inject @Paginator
	private DynamicDataModel<AelGrupoMaterialAnalise> dataModel;
	
	private AelGrupoMaterialAnalise selecionado;
	
	private String cameFrom;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private AelGrupoMaterialAnalise getFiltroPesquisa(){
		final AelGrupoMaterialAnalise grupoMaterialAnalise = new AelGrupoMaterialAnalise();
		grupoMaterialAnalise.setSeq(this.codigo);
		grupoMaterialAnalise.setDescricao(this.nome);
		grupoMaterialAnalise.setOrdProntOnline(this.ordem);
		grupoMaterialAnalise.setIndSituacao(this.situacao);
		return grupoMaterialAnalise;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosApoioExamesFacade.pesquisarGrupoMaterialAnaliseCount(this.getFiltroPesquisa());
	}
	
	@Override
	public List<AelGrupoMaterialAnalise> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.cadastrosApoioExamesFacade.pesquisarGrupoMaterialAnalise(firstResult, maxResult, orderProperty, asc, this.getFiltroPesquisa());
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.codigo = null;
		this.nome = null;
		this.ordem = null;
		this.situacao = null;	
	}

	public void excluir()  {
		try {
			this.cadastrosApoioExamesFacade.removerGrupoMaterialAnalise(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_GRUPO_MATERIAL_ANALISE");
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public String inserir(){
		return GRUPO_MATERIAL_ANALISE_CRUD;
	}

	public String editar(){
		return GRUPO_MATERIAL_ANALISE_CRUD;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public DynamicDataModel<AelGrupoMaterialAnalise> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelGrupoMaterialAnalise> dataModel) {
		this.dataModel = dataModel;
	}

	public AelGrupoMaterialAnalise getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelGrupoMaterialAnalise selecionado) {
		this.selecionado = selecionado;
	}
}