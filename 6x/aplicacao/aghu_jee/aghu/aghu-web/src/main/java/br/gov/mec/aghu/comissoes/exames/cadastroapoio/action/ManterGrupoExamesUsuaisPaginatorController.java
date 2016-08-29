package br.gov.mec.aghu.comissoes.exames.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterGrupoExamesUsuaisPaginatorController  extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = -6544604733570920751L;

	private static final String MANTER_GRUPO_EXAMES_USUAIS_CRUD = "manterGrupoExamesUsuaisCRUD";
	
	@EJB
	private IExamesFacade examesFacade;
	
	// Filtro da Pesquisa
	private Integer seq;
	private String descricao;
	private DominioSituacao situacao;
	
	@Inject @Paginator
	private DynamicDataModel<AelGrupoExameUsual> dataModel;
	
	private AelGrupoExameUsual selecionado;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void limparPesquisa(){
		dataModel.limparPesquisa();
		this.seq = null;
		this.descricao = null;
		this.situacao = null;
	}
	
	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisaGrupoExamesUsuaisCount(seq, descricao, situacao);
	}

	@Override
	public List<AelGrupoExameUsual> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return examesFacade.pesquisaGrupoExamesUsuais(firstResult, maxResult, orderProperty, asc, seq, descricao, situacao);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public String inserir(){
		return MANTER_GRUPO_EXAMES_USUAIS_CRUD;
	}
	
	public String editar(){
		return MANTER_GRUPO_EXAMES_USUAIS_CRUD;
	}
	
	public void excluir(){
		try {
			this.examesFacade.excluirAelGrupoExameUsual(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"LABEL_CONFIRMACAO_EXCLUSAO_GRUPO_EXAMES_USUAIS");
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

	public DynamicDataModel<AelGrupoExameUsual> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelGrupoExameUsual> dataModel) {
		this.dataModel = dataModel;
	}

	public AelGrupoExameUsual getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelGrupoExameUsual selecionado) {
		this.selecionado = selecionado;
	}
}
