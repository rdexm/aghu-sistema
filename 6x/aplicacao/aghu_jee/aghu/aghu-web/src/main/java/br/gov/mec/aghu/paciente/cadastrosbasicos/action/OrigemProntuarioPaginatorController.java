package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class OrigemProntuarioPaginatorController extends ActionController implements ActionPaginator{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AghSamis> dataModel;

	private static final long serialVersionUID = -9201832580726053830L;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private Integer codigo;
	private DominioSituacao situacaoOrigemProntuario;
	private String descricaoPesquisa;
	private Boolean exibirBotaoIncluirOrigemProntuario;
	
	private static final String ORIGEM_PRONTUARIO_CRUD = "origemProntuarioCRUD";
	
	@Inject
	private OrigemProntuarioController origemProntuarioController;
	
	private AghSamis selecionado;
	
	public AghSamis getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AghSamis selecionado) {
		this.selecionado = selecionado;
	}

	public Boolean getExibirBotaoIncluirOrigemProntuario() {
		return exibirBotaoIncluirOrigemProntuario;
	}

	public void setExibirBotaoIncluirOrigemProntuario(
			Boolean exibirBotaoIncluirOrigemProntuario) {
		this.exibirBotaoIncluirOrigemProntuario = exibirBotaoIncluirOrigemProntuario;
	}

	public String getDescricaoPesquisa() {
		return descricaoPesquisa;
	}

	public void setDescricaoPesquisa(String descricaoPesquisa) {
		this.descricaoPesquisa = descricaoPesquisa;
	}

	public DominioSituacao getSituacaoOrigemProntuario() {
		return situacaoOrigemProntuario;
	}

	public void setSituacaoOrigemProntuario(DominioSituacao situacaoOrigemProntuario) {
		this.situacaoOrigemProntuario = situacaoOrigemProntuario;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(
			Integer codigoPesquisaOrigemProntuario) {
		this.codigo = codigoPesquisaOrigemProntuario;
	}
	
	
	
	
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AghSamis> recuperarListaPaginada(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc) {
		List<AghSamis> result = this.cadastrosBasicosPacienteFacade.pesquisaOrigemProntuario(firstResult, maxResults, AghSamis.Fields.DESCRICAO.toString(),
				true, this.codigo, this.descricaoPesquisa,
				this.situacaoOrigemProntuario);
		if (result == null) {
			result = new ArrayList<AghSamis>();
		}
		return result;
	}

	@Override
	public Long recuperarCount() {
		Long count = 0L;
		count = cadastrosBasicosPacienteFacade.pesquisaOrigemProntuarioCount(this.codigo,
				this.descricaoPesquisa, this.situacaoOrigemProntuario);
		return count;
	}
	
	
	public String iniciarInclusao() {
		return "origemProntuarioCRUD";
	}
	
	
	
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirOrigemProntuario = true;
	}
	
	public void limparPesquisa() {
		this.codigo = null;
		this.descricaoPesquisa = null;
		this.situacaoOrigemProntuario = null;
		this.exibirBotaoIncluirOrigemProntuario = false;
		//this.dataModel.setPesquisaAtiva(false);
		this.dataModel.limparPesquisa();
	}
	
	public String prepararEdicao(){
		origemProntuarioController.iniciarEdicao(selecionado);
		return ORIGEM_PRONTUARIO_CRUD;
	}
	
	public void excluir() {
		dataModel.reiniciarPaginator();

		// Obtem origem do prontuario e remove a mesma
		AghSamis aghSamisRemover = this.cadastrosBasicosPacienteFacade
				.obterOrigemProntuario(this.selecionado.getCodigo());
		try {
			this.cadastrosBasicosPacienteFacade.excluirOrigemProntuario(aghSamisRemover, registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()), this.selecionado.getCodigo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		// Exibr mensagem de exclusão com sucesso e fecha janela de
		// confirmação
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_ORIGEM_PRONTUARIO");
		limparPesquisa();
		pesquisar();
	}
 


	public DynamicDataModel<AghSamis> getDataModel() {
	  return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghSamis> dataModel) {
	  this.dataModel = dataModel;
	}
}