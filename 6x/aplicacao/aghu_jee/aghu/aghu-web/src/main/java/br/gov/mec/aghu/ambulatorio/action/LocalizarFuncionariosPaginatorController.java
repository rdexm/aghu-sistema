package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

public class LocalizarFuncionariosPaginatorController extends ActionController implements ActionPaginator{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5186248274268102707L;

	@Inject @Paginator
	private DynamicDataModel<RapServidores> dataModel;
	
	private RapServidores filtro = null; 
	
	private FccCentroCustos centroCustoLotacao, centroCustoAtuacao = null;
	
	private RapVinculos vinculo = null;
	
	private RapOcupacaoCargo ocupacaoCargo = null;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private boolean colunaCarteira = Boolean.FALSE;

	
	@PostConstruct
	public void inicializar(){
		begin(conversation);
	}

	public void iniciar() {
		
		if(filtro ==  null){
			filtro = new RapServidores();
		}
		
		if(filtro.getId() == null){
			filtro.setId(new RapServidoresId());
		}
		
		if(filtro.getPessoaFisica() == null){
			filtro.setPessoaFisica(new RapPessoasFisicas());
		}
				
		if(filtro.getRamalTelefonico() == null){
			filtro.setRamalTelefonico(new RapRamalTelefonico());
		}
		
		if(filtro.getCentroCustoLotacao() == null){
			filtro.setCentroCustoLotacao(new FccCentroCustos());
		}
	
		if(filtro.getCentroCustoAtuacao() == null){
			filtro.setCentroCustoAtuacao(new FccCentroCustos());
		}
		
		if(filtro.getVinculo() == null){
			filtro.setVinculo(new RapVinculos());
		}
		
		if(filtro.getOcupacaoCargo() == null){
			filtro.setOcupacaoCargo(new RapOcupacaoCargo());
		}		
	}
	
	public void pesquisar(){
		if(contarFiltrosPreenchidos() == 0){
			apresentarMsgNegocio(Severity.WARN, "MSG_FILTRO_OBRIGATORIO");	
			dataModel.setPesquisaAtiva(Boolean.FALSE);
			return;
		}		
		
		preencherFiltrosPesquisa();
		pesquisarContextoBaseLocal();
		atualizarDataModel();
	}
		

	private int contarFiltrosPreenchidos(){
		return contarFiltrosNativos() + contarFiltrosObjetos();
	}
	
	private int contarFiltrosNativos(){
		int contador = 0;
		
		if(filtro.getId() != null && filtro.getId().getMatricula() != null){ contador +=1;}
		
		if(filtro.getPessoaFisica() != null && StringUtils.isNotBlank(filtro.getPessoaFisica().getNome())){	contador +=1;}
		
		if(filtro.getDtInicioVinculo() != null){ contador += 1;}
		
		if(filtro.getRamalTelefonico() != null && filtro.getRamalTelefonico().getNumeroRamal() != null){ contador += 1;}
		
		return contador;
	}
	
	private int contarFiltrosObjetos(){
		int contador = 0;
		
		if(centroCustoLotacao != null && centroCustoLotacao.getCodigo() != null){ contador += 1;}
		
		if(centroCustoAtuacao != null && centroCustoAtuacao.getCodigo() != null){ contador += 1;}
		
		if(vinculo != null && vinculo.getCodigo() != null){ contador += 1;}
		
		if(ocupacaoCargo != null && ocupacaoCargo.getCodigo() != null){ contador += 1;}
		
		return contador;
	}
	
	private void preencherFiltrosPesquisa() {

		if(centroCustoLotacao != null && centroCustoLotacao.getCodigo() != null){ 
			filtro.setCentroCustoLotacao(getCentroCustoLotacao());
		}
		
		if(centroCustoAtuacao != null && centroCustoAtuacao.getCodigo() != null){ 
			filtro.setCentroCustoAtuacao(getCentroCustoAtuacao());
		}
		
		if(vinculo != null && vinculo.getCodigo() != null){ filtro.setVinculo(vinculo);}
		
		if(ocupacaoCargo != null && ocupacaoCargo.getCodigo() != null){ 
			filtro.setOcupacaoCargo(getOcupacaoCargo());
		}
	}
	
	/**
	 * método que faz aparecer coluna carteira unimed da grid 
	 */
	private void pesquisarContextoBaseLocal() {
		colunaCarteira = registroColaboradorFacade.isHospitalHCPA();
	}
	
	/**
	 * pesquisa principal 
	 */
	@Override
	public List<RapServidoresVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc){		
		
		return registroColaboradorFacade.pesquisarFuncionarios(firstResult, maxResult, orderProperty, asc, filtro);
	}
	
	@Override
	public Long recuperarCount() {
		return registroColaboradorFacade.pesquisarFuncionariosCount(filtro);
	}
	
	public void atualizarDataModel(){
		dataModel.reiniciarPaginator();	
	}
	
	// ação limpar	
	public void limpar(){
		filtro = new RapServidores();
		limparFiltrosSuggestionBox();
		
		dataModel.limparPesquisa();
		iniciar();
		colunaCarteira = Boolean.FALSE;
	}	
	
	private void limparFiltrosSuggestionBox(){
		limparFiltroCCLotacao();
		limparFiltroCCAtuacao();
		limparFiltroVinculo();
		limparFiltroOcupacaoCargo();
	}
	
	public void limparFiltroCCLotacao(){
		centroCustoLotacao = null;
		filtro.setCentroCustoLotacao(new FccCentroCustos());
	}
	
	public void limparFiltroCCAtuacao(){
		centroCustoAtuacao = null;
		filtro.setCentroCustoAtuacao(new FccCentroCustos());
	}
	
	public void limparFiltroVinculo(){
		vinculo = null;
		filtro.setVinculo(new RapVinculos());
	}
	
	public void limparFiltroOcupacaoCargo(){
		ocupacaoCargo = null;
		filtro.setOcupacaoCargo(new RapOcupacaoCargo());
	}
	
	/**
	 * pesquisas para suggestionBox 
	 */
	
	//LOTACAO
	public List<FccCentroCustos> pesquisarCCLotacao(String lotacao){
		return  this.returnSGWithCount(centroCustoFacade.pesquisarCCLotacaoEAtuacaoFuncionario(lotacao),pesquisarCCLotacaoCount(lotacao));
	}

	public Long pesquisarCCLotacaoCount(String lotacao) {
		return centroCustoFacade.pesquisarCCLotacaoEAtuacaoFuncionarioCount(lotacao);
	}
	
	//ATUACAO
	public List<FccCentroCustos> pesquisarCCAtuacao(String atuacao){
		return  this.returnSGWithCount(centroCustoFacade.pesquisarCCLotacaoEAtuacaoFuncionario(atuacao),pesquisarCCAtuacaoCount(atuacao));
	}

	public Long pesquisarCCAtuacaoCount(String atuacao) {
		return centroCustoFacade.pesquisarCCLotacaoEAtuacaoFuncionarioCount(atuacao);
	}
	
	//VINCULO
	public List<RapVinculos> pesquisarVinculo(String vinculo){
		return  this.returnSGWithCount(registroColaboradorFacade.pesquisarVinculoFuncionario(vinculo),pesquisarVinculoCount(vinculo));
	}

	public Long pesquisarVinculoCount(String vinculo) {
		return registroColaboradorFacade.pesquisarVinculoFuncionarioCount(vinculo);
	}

	//OCUPACAO
	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(String ocupacaoCargo){
		return  this.returnSGWithCount(registroColaboradorFacade.pesquisarOcupacaoCargoFuncionario(ocupacaoCargo),pesquisarOcupacaoCargoCount(ocupacaoCargo));
	}

	public Long pesquisarOcupacaoCargoCount(String ocupacaoCargo) {
		return registroColaboradorFacade.pesquisarOcupacaoCargoFuncionarioCount(ocupacaoCargo);
	}	

	
	/**
	 * método de hint 
	 */
	
	public String obterHintDescricao(String descricao) {

		if (StringUtils.isNotBlank(descricao) && descricao.length() > 20) {
			descricao = StringUtils.abbreviate(descricao, 20);
		}
		return descricao;
	}
	
	
	/**
	 * Getters and Setters 
	 */	
	public DynamicDataModel<RapServidores> getDataModel() {
		return dataModel;
	}

	public FccCentroCustos getCentroCustoLotacao() {
		return centroCustoLotacao;
	}

	public void setCentroCustoLotacao(FccCentroCustos centroCustoLotacao) {
		this.centroCustoLotacao = centroCustoLotacao;
	}

	public FccCentroCustos getCentroCustoAtuacao() {
		return centroCustoAtuacao;
	}

	public void setCentroCustoAtuacao(FccCentroCustos centroCustoAtuacao) {
		this.centroCustoAtuacao = centroCustoAtuacao;
	}

	public RapVinculos getVinculo() {
		return vinculo;
	}

	public void setVinculo(RapVinculos vinculo) {
		this.vinculo = vinculo;
	}

	public RapOcupacaoCargo getOcupacaoCargo() {
		return ocupacaoCargo;
	}

	public void setOcupacaoCargo(RapOcupacaoCargo ocupacaoCargo) {
		this.ocupacaoCargo = ocupacaoCargo;
	}

	public ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	public void setCentroCustoFacade(ICentroCustoFacade centroCustoFacade) {
		this.centroCustoFacade = centroCustoFacade;
	}

	public void setDataModel(DynamicDataModel<RapServidores> dataModel) {
		this.dataModel = dataModel;
	}

	public RapServidores getFiltro() {
		return filtro;
	}

	public void setFiltro(RapServidores filtro) {
		this.filtro = filtro;
	}

	public boolean isColunaCarteira() {
		return colunaCarteira;
	}

	public void setColunaCarteira(boolean colunaCarteira) {
		this.colunaCarteira = colunaCarteira;
	}

}
