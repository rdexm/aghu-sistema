package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ModeloCartaRecoletaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 914371562041435104L;

	private static final String MODELO_CARTA_RECOLETA = "modeloCartaRecoletaCRUD";
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;	
	
	// Filtros de pesquisa
	private Short seq;
	private String nome;
	private DominioSituacao situacao;	
	

	@Inject @Paginator
	private DynamicDataModel<AelModeloCartas> dataModel;
	
	private AelModeloCartas selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosApoioExamesFacade.obterModeloCartaCount(obterObjetoFiltrosPesquisa());
	}

	@Override
	public List<AelModeloCartas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		orderProperty = AelModeloCartas.Fields.NOME.toString();
		asc = true;
		return cadastrosApoioExamesFacade.pesquisarModeloCarta(firstResult, maxResult, orderProperty, asc, obterObjetoFiltrosPesquisa());
	}
	
	private AelModeloCartas obterObjetoFiltrosPesquisa() {
		AelModeloCartas modeloCarta = new AelModeloCartas();
		modeloCarta.setSeq(seq);
		modeloCarta.setNome(nome);
		modeloCarta.setIndSituacao(situacao);
		return modeloCarta;
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		seq = null;
		nome = null;
		situacao = null;
	}
	
	public void excluir() {
		try {
			cadastrosApoioExamesFacade.excluirModeloCarta(selecionado.getSeq());	
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_MODELO_CARTA_RECOLETA", selecionado.getNome());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
	}
	
	public String inserir(){
		return MODELO_CARTA_RECOLETA;
	}
	
	public String editar(){
		return MODELO_CARTA_RECOLETA;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<AelModeloCartas> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelModeloCartas> dataModel) {
		this.dataModel = dataModel;
	}

	public AelModeloCartas getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelModeloCartas selecionado) {
		this.selecionado = selecionado;
	}
}
