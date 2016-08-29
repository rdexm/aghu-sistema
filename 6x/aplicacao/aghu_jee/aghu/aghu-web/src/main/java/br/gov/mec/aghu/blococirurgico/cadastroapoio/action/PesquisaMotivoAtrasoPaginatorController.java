package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcMotivoAtraso;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaMotivoAtrasoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcMotivoAtraso> dataModel;

	private static final String MOTIVO_ATRASO_CRUD = "cadastroMotivoAtraso";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4038614901761601452L;

	/*
	 * Injeções
	 */


	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	/*
	 * Campos do filtro
	 */
	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;

	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {

		// Garante que os resultados da pesquisa serão mantidos ao retonar na tela
		if (this.dataModel.getPesquisaAtiva()) {
			this.pesquisar();
		}

	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		// Limpa filtro
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		this.exibirBotaoNovo = false;

		// Apaga resultados da exibição
		this.dataModel.setPesquisaAtiva(false);
	}

	/**
	 * Obtem o elemento/filtro da pesquisa paginada
	 * 
	 * @return
	 */
	private MbcMotivoAtraso getElemento() {
		MbcMotivoAtraso elemento = new MbcMotivoAtraso();
		elemento.setSeq(this.codigo);
		elemento.setDescricao(this.descricao);
		elemento.setSituacao(this.situacao);
		return elemento;
	}

	@Override
	public List<MbcMotivoAtraso> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarMotivoAtraso(firstResult, maxResult, orderProperty, asc, this.getElemento());
	}

	@Override
	public Long recuperarCount() {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarMotivoAtrasoCount(this.getElemento());
	}

	/**
	 * Carrega a tela de inclusão
	 * 
	 * @return
	 */
	public String iniciarInclusao() {
		return MOTIVO_ATRASO_CRUD;
	}

	/**
	 * Ativa/Desativa destino do paciente
	 * 
	 * @param destinoPaciente
	 */
	public void ativar(MbcMotivoAtraso motivoAtraso) {

		try {
			// Altera/Alterna situação
			motivoAtraso.setSituacao(DominioSituacao.A.equals(motivoAtraso.getSituacao()) ? DominioSituacao.I : DominioSituacao.A);

			// ATUALIZAR
			this.blocoCirurgicoCadastroApoioFacade.persistirMotivoAtraso(motivoAtraso);

			// Apresenta as mensagens de acordo com a ação
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_MOTIVO_ATRASO", motivoAtraso.getDescricao());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Verifica a situação do item
	 * 
	 * @param destinoPaciente
	 * @return
	 */
	public boolean isAtivo(MbcMotivoAtraso motivoAtraso) {
		return DominioSituacao.A.equals(motivoAtraso.getSituacao());
	}

	/*
	 * Getters e Setters
	 */

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
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
 


	public DynamicDataModel<MbcMotivoAtraso> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcMotivoAtraso> dataModel) {
	 this.dataModel = dataModel;
	}
}