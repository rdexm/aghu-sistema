package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterRecipienteColetaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4088791371580286216L;

	private static final String MANTER_RECIPIENTE_COLETA_CRUD = "manterRecipienteColetaCRUD";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject @Paginator
	private DynamicDataModel<AelRecipienteColeta> dataModel;
	
	private AelRecipienteColeta selecionado;
	
	// campos filtro
	private Integer codigo;
	private String descricao;
	private DominioSimNao indAnticoag;
	private DominioSituacao indSituacao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.setCodigo(null);
		this.setDescricao(null);
		this.setIndAnticoag(null);
		this.setIndSituacao(null);
		dataModel.limparPesquisa();
	}
	
	@Override
	public List<AelRecipienteColeta> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return examesFacade.pesquisaRecipienteColetaList(firstResult, maxResult, AelRecipienteColeta.Fields.DESCRICAO.toString(), true, 
														  new AelRecipienteColeta(codigo, descricao, indAnticoag, indSituacao, null, null));
	}

	@Override
	public Long recuperarCount() {
		return examesFacade.countRecipienteColeta(new AelRecipienteColeta(codigo, descricao, indAnticoag, indSituacao, null, null));
	}
	
	public void excluir() {
		try {
			this.cadastrosApoioExamesFacade.excluirRecipienteColeta(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_RECIPIENTE_COLETA", selecionado.getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return MANTER_RECIPIENTE_COLETA_CRUD;
	}
	
	public String editar(){
		return MANTER_RECIPIENTE_COLETA_CRUD;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getIndAnticoag() {
		return indAnticoag;
	}

	public void setIndAnticoag(DominioSimNao indAnticoag) {
		this.indAnticoag = indAnticoag;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	public DynamicDataModel<AelRecipienteColeta> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelRecipienteColeta> dataModel) {
	 this.dataModel = dataModel;
	}

	public AelRecipienteColeta getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelRecipienteColeta selecionado) {
		this.selecionado = selecionado;
	}
}