package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatConvPlanoAcomodacoes;
import br.gov.mec.aghu.model.FatConvTipoDocumentos;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatPeriodosEmissao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Classe responsável por controlar as ações da listagem de Convenio Saude.
 * 
 * @author Ricardo Costa
 * 
 */

public class ConvenioSaudePaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8154405009529112919L;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private ConvenioSaudeController convenioSaudeController;
	
	/**
	 * Atributo utilizado para controlar a exibicao do botao de Incluir.
	 */
	private boolean exibirBotaoIncluir;

	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;
	@Inject @Paginator
	private DynamicDataModel<FatConvenioSaude> dataModel;
	private final String PAGE_CADASTRAR_CONVENIO_SAUDE = "convenioSaudeCRUD";
	
	/**
	 * Representa o item selecionado na tabela para visualizar o detalhes.
	 */
	private FatConvenioSaude convenioSaudeSelecionado;

	
	/**
	 * Representa o item selecionado na tabela para visualizar o detalhes.
	 */
	private FatConvenioSaudePlano convSaudePlano = new FatConvenioSaudePlano();

	/**
	 * Atributo utilizado para renderizar o fieldset de Detalhes.
	 */
	private Boolean renderDetalhes = false;

	/**
	 * Atributo utilizado para renderizar o fieldset de Detalhes.
	 */
	private Boolean renderDetalhesConvPlano = false;

	/**
	 * Lista de planos, convênios e docs.
	 */
	private List<FatConvTipoDocumentos> convenioTipoDocumentos;

	/**
	 * Lista de planos, convênios e acomodações.
	 */
	private List<FatConvPlanoAcomodacoes> convenioPlanoAcomodacoes;

	/**
	 * Períodos de emissão.
	 */
	private List<FatPeriodosEmissao> periodosEmissao;

	/**
	 * Detalhar o convênio.
	 * 
	 * @param item
	 */
	public void detalharConvenio(FatConvenioSaude item) {
		this.renderDetalhes = true;
		this.convenioSaudeSelecionado = this.faturamentoFacade.obterConvenioSaudeComPagador(item.getCodigo());
	}

	@PostConstruct
	public void init() {		
		begin(conversation);
	}
	
	/**
	 * Detalhar o convênio.
	 * 
	 * @param item
	 */
	public void detalharConvenioPlano(FatConvenioSaudePlano item) {
		this.renderDetalhes = true;
		this.convSaudePlano = this.faturamentoFacade.obterFatConvenioSaudePlano(item.getId().getCnvCodigo(), item.getId().getSeq());
		
		this.convenioTipoDocumentos = new ArrayList<FatConvTipoDocumentos>(this.faturamentoFacade.pesquisarConvTipoDocumentoConvenioSaudePlano(this.convSaudePlano));
		this.convenioPlanoAcomodacoes = new ArrayList<FatConvPlanoAcomodacoes>(this.faturamentoFacade.pesquisarConvPlanoAcomodocaoConvenioSaudePlano(this.convSaudePlano));
		this.periodosEmissao = new ArrayList<FatPeriodosEmissao>(this.faturamentoFacade.pesquisarPeriodosEmissaoConvenioSaudePlano(this.convSaudePlano));
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluir = true;
	}

	public void limparPesquisa() {
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		this.exibirBotaoIncluir = false;
		this.dataModel.limparPesquisa();
		this.renderDetalhes = false;
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de Tipo de
	 * Característica de Leito.
	 */
	/*public void excluir() {

		FatConvenioSaude fatConvenioSaude = this.faturamentoApoioFacade
				.obterConvenioSaude(this.codigo);
		try {
			if (fatConvenioSaude != null) {
				this.faturamentoApoioFacade.remover(fatConvenioSaude);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_CONVENIO_SAUDE", fatConvenioSaude.getDescricao());
				this.codigo = null;
				this.situacao = DominioSituacao.A;
				this.descricao = null;
				this.convenioSaudeSelecionado = new FatConvenioSaude();
				this.dataModel.reiniciarPaginator();
			} else {
				this.codigo = null;
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_REMOCAO_CONVENIO_SAUDE");
			}

			this.codigo = null;
		} catch (ApplicationBusinessException e) {
			this.codigo = null;			
			apresentarExcecaoNegocio(e);
		}
	}*/

	public String editar() {
		return PAGE_CADASTRAR_CONVENIO_SAUDE;
	}
	
	public String iniciarInclusao() {
		this.getConvenioSaudeController().iniciarInclusao();
		this.getConvenioSaudeController().inicio();
		return PAGE_CADASTRAR_CONVENIO_SAUDE;
	}
	
	// ### Paginação ###
	@Override
	public Long recuperarCount() {
		return faturamentoApoioFacade.pesquisaCount(this.codigo, this.descricao, this.situacao);		
	}

	@Override
	public List<FatConvenioSaude> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<FatConvenioSaude> result = this.faturamentoApoioFacade.pesquisar(
				firstResult, maxResults, orderProperty, asc, this.codigo,
				this.descricao, this.situacao);

		if (result == null) {
			result = new ArrayList<FatConvenioSaude>();
		}

		return result; 		
	}

	// ### GETs e SETs ###

	public boolean isExibirBotaoIncluir() {
		return exibirBotaoIncluir;
	}

	public void setExibirBotaoIncluir(boolean exibirBotaoIncluir) {
		this.exibirBotaoIncluir = exibirBotaoIncluir;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public FatConvenioSaude getItem() {
		return convenioSaudeSelecionado;
	}

	public void setItem(FatConvenioSaude item) {
		this.convenioSaudeSelecionado = item;
	}

	public Boolean getRenderDetalhes() {
		return renderDetalhes;
	}

	public void setRenderDetalhes(Boolean renderDetalhes) {
		this.renderDetalhes = renderDetalhes;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public FatConvenioSaudePlano getConvSaudePlano() {
		return convSaudePlano;
	}

	public void setConvSaudePlano(FatConvenioSaudePlano convSaudePlano) {
		this.convSaudePlano = convSaudePlano;
	}

	public Boolean getRenderDetalhesConvPlano() {
		return renderDetalhesConvPlano;
	}

	public void setRenderDetalhesConvPlano(Boolean renderDetalhesConvPlano) {
		this.renderDetalhesConvPlano = renderDetalhesConvPlano;
	}

	public List<FatConvTipoDocumentos> getConvenioTipoDocumentos() {
		return convenioTipoDocumentos;
	}

	public void setConvenioTipoDocumentos(
			List<FatConvTipoDocumentos> convenioTipoDocumentos) {
		this.convenioTipoDocumentos = convenioTipoDocumentos;
	}

	public List<FatConvPlanoAcomodacoes> getConvenioPlanoAcomodacoes() {
		return convenioPlanoAcomodacoes;
	}

	public void setConvenioPlanoAcomodacoes(
			List<FatConvPlanoAcomodacoes> convenioPlanoAcomodacoes) {
		this.convenioPlanoAcomodacoes = convenioPlanoAcomodacoes;
	}

	public List<FatPeriodosEmissao> getPeriodosEmissao() {
		return periodosEmissao;
	}

	public void setPeriodosEmissao(List<FatPeriodosEmissao> periodosEmissao) {
		this.periodosEmissao = periodosEmissao;
	}

	public DynamicDataModel<FatConvenioSaude> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatConvenioSaude> dataModel) {
		this.dataModel = dataModel;
	}

	public ConvenioSaudeController getConvenioSaudeController() {
		return convenioSaudeController;
	}

	public void setConvenioSaudeController(ConvenioSaudeController convenioSaudeController) {
		this.convenioSaudeController = convenioSaudeController;
	}
	
	public FatConvenioSaude getConvenioSaudeSelecionado() {
		return convenioSaudeSelecionado;
	}
	public void setConvenioSaudeSelecionado(
			FatConvenioSaude convenioSaudeSelecionado) {
		this.convenioSaudeSelecionado = convenioSaudeSelecionado;
	}
}
