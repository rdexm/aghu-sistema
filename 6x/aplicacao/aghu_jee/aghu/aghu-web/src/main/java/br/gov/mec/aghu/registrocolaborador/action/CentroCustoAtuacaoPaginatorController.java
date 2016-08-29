package br.gov.mec.aghu.registrocolaborador.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class CentroCustoAtuacaoPaginatorController extends ActionController implements ActionPaginator {

	private static final String CADASTRAR_CENTRO_CUSTO_ATUACAO = "cadastrarCentroCustoAtuacao";

	private static final long serialVersionUID = -5056341835801019317L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject @Paginator
	private DynamicDataModel<RapServidores> dataModel;

	private Integer codigoCCLotacao;
	private Integer codVinculo;
	private Integer matricula;
	private String nomeServidor;
	private Integer codigoCCAtuacao;

	private FccCentroCustos centroCustoLotacao;
	private FccCentroCustos centroCustoAtuacao;
	private RapServidores selecionado;

	/**
	 * Indica se os campos devem ser protegidos após a pesquisa.
	 */
	private boolean protegeCampos;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public String editar(){
		return CADASTRAR_CENTRO_CUSTO_ATUACAO;
	}
	
	
	@Override
	public Long recuperarCount() {
		try {
			codigoCCLotacao = centroCustoLotacao == null ? null : centroCustoLotacao.getCodigo();
			codigoCCAtuacao = centroCustoAtuacao == null ? null : centroCustoAtuacao.getCodigo();
			
			return registroColaboradorFacade.pesquisarServidoresCount(codigoCCLotacao, codVinculo, matricula, nomeServidor, codigoCCAtuacao);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return 0L;
	}

	@Override
	public List<RapServidores> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		try {
			codigoCCLotacao = centroCustoLotacao == null ? null : centroCustoLotacao.getCodigo();
			codigoCCAtuacao = centroCustoAtuacao == null ? null : centroCustoAtuacao.getCodigo();
			
			return registroColaboradorFacade.pesquisarServidores( codigoCCLotacao, codVinculo, matricula, nomeServidor, 
																  codigoCCAtuacao, firstResult, maxResult, null, true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		this.protegeCampos = true;
	}

	public void limpar() {
		dataModel.limparPesquisa();
		
		codigoCCLotacao = null;
		codigoCCAtuacao = null;
		matricula = null;
		codVinculo = null;
		nomeServidor = null;
		centroCustoAtuacao = null;
		centroCustoLotacao = null;
		this.protegeCampos = false;
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String descricaoOrId) {
		return registroColaboradorFacade.pesquisarCentroCustosOrdemDescricao(descricaoOrId);
	}

	// getters & setters
	public Integer getCodigoCCLotacao() {
		return codigoCCLotacao;
	}

	public void setCodigoCCLotacao(Integer codigoCCLotacao) {
		this.codigoCCLotacao = codigoCCLotacao;
	}

	public Integer getCodVinculo() {
		return codVinculo;
	}

	public void setCodVinculo(Integer codVinculo) {
		this.codVinculo = codVinculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public Integer getCodigoCCAtuacao() {
		return codigoCCAtuacao;
	}

	public void setCodigoCCAtuacao(Integer codigoCCAtuacao) {
		this.codigoCCAtuacao = codigoCCAtuacao;
	}

	public void setCentroCustoLotacao(FccCentroCustos centroCustoLotacao) {
		this.centroCustoLotacao = centroCustoLotacao;
	}

	public FccCentroCustos getCentroCustoLotacao() {
		return centroCustoLotacao;
	}

	public void setCentroCustoAtuacao(FccCentroCustos centroCustoAtuacao) {
		this.centroCustoAtuacao = centroCustoAtuacao;
	}

	public FccCentroCustos getCentroCustoAtuacao() {
		return centroCustoAtuacao;
	}

	public boolean isProtegeCampos() {
		return protegeCampos;
	}

	public void setProtegeCampos(boolean protegeCampos) {
		this.protegeCampos = protegeCampos;
	}

	public DynamicDataModel<RapServidores> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapServidores> dataModel) {
	 this.dataModel = dataModel;
	}

	public RapServidores getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RapServidores selecionado) {
		this.selecionado = selecionado;
	}
}
