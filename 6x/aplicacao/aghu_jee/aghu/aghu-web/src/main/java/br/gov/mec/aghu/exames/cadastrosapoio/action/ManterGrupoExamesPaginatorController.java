package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelGrupoExameTecnicas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterGrupoExamesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2591480721486704500L;

	private static final String MANTER_GRUPO_EXAMES_CRUD = "manterGrupoExamesCRUD";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Campos de filtro para pesquisa
	private Integer codigo;
	private String descricao;
	private DominioSituacao indSituacao;
	
	private boolean exibirBotaoNovo;
	
	@Inject @Paginator
	private DynamicDataModel<AelGrupoExameTecnicas> dataModel;
	private AelGrupoExameTecnicas selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Recupera uma instancia com os filtros de pesquisa atualizados
	 */
	private AelGrupoExameTecnicas getElementoFiltroPesquisa(){
		
		final AelGrupoExameTecnicas elementoFiltroPesquisa = new AelGrupoExameTecnicas();
		
		elementoFiltroPesquisa.setSeq(this.codigo);
		
		this.descricao = StringUtils.trim(this.descricao);
		elementoFiltroPesquisa.setDescricao(this.descricao);

		elementoFiltroPesquisa.setIndSituacao(this.indSituacao);
		
		return elementoFiltroPesquisa;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
		this.exibirBotaoNovo = true;
	}

	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisarAelGrupoExameTecnicasCount(this.getElementoFiltroPesquisa());
	}
	
	@Override
	public List<AelGrupoExameTecnicas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.examesFacade.pesquisarAelGrupoExameTecnicas(firstResult, maxResult, orderProperty, asc, this.getElementoFiltroPesquisa());
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.codigo = null;
		this.descricao = null;
		this.indSituacao = null;	
		this.exibirBotaoNovo = false;
	}
	
	public String inserir(){
		return MANTER_GRUPO_EXAMES_CRUD;
	}
	
	public String editar(){
		return MANTER_GRUPO_EXAMES_CRUD;
	}

	public void excluir()  {
		try {
		
			this.cadastrosApoioExamesFacade.removerAelGrupoExameTecnicas(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_GRUPO_EXAMES",selecionado.getDescricao());
			
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);	
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/*
	 * Getters e setters
	 */
	
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
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

	public DynamicDataModel<AelGrupoExameTecnicas> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelGrupoExameTecnicas> dataModel) {
		this.dataModel = dataModel;
	}

	public AelGrupoExameTecnicas getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelGrupoExameTecnicas selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}
}