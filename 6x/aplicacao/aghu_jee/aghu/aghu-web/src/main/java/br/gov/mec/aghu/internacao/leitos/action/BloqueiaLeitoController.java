package br.gov.mec.aghu.internacao.leitos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.recursoshumanos.Pessoa;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class BloqueiaLeitoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5256302467213909521L;

	/**
	 * Usuario Logado
	 */
	private Pessoa pessoa;

	/**
	 * Responsável pela pesquisa de AinTiposMovimentoLeito
	 */
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	

	/**
	 * Injeção da Facade de leitosInternacaoFacade.
	 */
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	/**
	 * LOV Leito
	 */
	private AinLeitos leitos = null;

	/**
	 * LOV Bloquear para
	 */
	private AinTiposMovimentoLeito tiposMovimentoLeito = null;

	/**
	 * LOV Responsável
	 */
	private RapServidores rapServidores = null;

	/**
	 * Lista de AinLeitos
	 */
	private List<AinLeitos> listaLeitos = new ArrayList<AinLeitos>();

	/**
	 * Lista de AinTiposMovimentoLeito
	 */
	private List<AinTiposMovimentoLeito> listaTiposMovimentoLeito = new ArrayList<AinTiposMovimentoLeito>();

	/**
	 * Lista de VRapPessoaServidor
	 */
	private List<RapServidores> listaResponsaveis = new ArrayList<RapServidores>();

	/**
	 * Codigo AinLeitos
	 */
	private String codigoLeitos;

	/**
	 * Codigo AinTiposMovimentoLeito
	 */
	private Integer codigoStatus;

	/**
	 * Codigo VRapPessoaServidor
	 */
	private Integer codigoResponsavel;

	/**
	 * Matricula VRapPessoaServidor
	 */
	private Integer matriculaResponsavel;

	/**
	 * Nome Responsável
	 */
	private String nomeResponsavel = "";

	/**
	 * Matricula ou Nome do Responsavel
	 */
	private Object responsavel;

	/**
	 * Descricao AinTiposMovimentoLeito
	 */
	private String descricaoStatus;

	/**
	 * Data do Lançamento
	 */
	private Date dataLancamento = new Date();

	/**
	 * Justificativa
	 */
	private String justificativa;

	/**
	 * Confirma a validação dos dados
	 */
	private boolean validado = false;
	
	private String voltarPara;
	
	/**
	 * Navegacao do botao cancelar
	 */
	private String cameFrom;
	
	private String idLeito;	

	private final static DominioMovimentoLeito[] situacoesPesquisaLeito = {DominioMovimentoLeito.L};
	
	
	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}
	
	/**
	 * Executa o início do bloqueio
	 */
	public void inicio() {
	 
		
		this.iniciarResponsavel();
		
		if(this.codigoLeitos != null){
			this.idLeito = this.codigoLeitos;
		}
		if (leitos != null) {
			leitos = cadastrosBasicosInternacaoFacade.obterLeitoDesocupado(codigoLeitos.toUpperCase());
	
		}
		
		if (idLeito!=null){
			leitos = cadastrosBasicosInternacaoFacade.obterLeitoPorId(idLeito);
		}		
	
	}
	private void iniciarResponsavel(){

		RapServidores servidor = null;
		try {
			servidor = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
			//Inicializa Responsável
			if (servidor != null) {
				if ((this.codigoResponsavel == null)||(this.matriculaResponsavel == null)) { 
				  this.codigoResponsavel = servidor.getId()
						.getVinCodigo().intValue();
				  this.matriculaResponsavel = servidor.getId()
						.getMatricula();
				  this.rapServidores = servidor;
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	
	
	public String cancelar(){
		limparPesquisa();
		return voltarPara;
	}
			
	/**
	 * Pesquisa AinLeitos para lista de valores.
	 */
	public void pesquisarLeitos() {
		listaLeitos = cadastrosBasicosInternacaoFacade.pesquisarLeitosDesocupados(codigoLeitos.toUpperCase()); 
	}
	
	public List<AinLeitos> pesquisarLeitosDesocupados(String param){
		this.listaLeitos = this.cadastrosBasicosInternacaoFacade.pesquisarLeitosPorSituacoesDoLeito(param,situacoesPesquisaLeito);
		return this.listaLeitos; 
	}

	/**
	 * Busca Status pela pk.
	 */
	public void buscarStatus() {
		if (codigoStatus != null) {
			tiposMovimentoLeito = cadastrosBasicosInternacaoFacade
					.pesquisarTipoSituacaoLeitoBloqueados(codigoStatus
							.shortValue());
		} else {
			tiposMovimentoLeito = new AinTiposMovimentoLeito();
		}
	}

	public List<AinTiposMovimentoLeito> pesquisarMotivos(String param){
		this.listaTiposMovimentoLeito = this.cadastrosBasicosInternacaoFacade
				.pesquisarTipoSituacaoLeitoBloqueadosPorDescricaoOuCodigo(param);
		return this.listaTiposMovimentoLeito;
	}

	/**
	 * Busca Status pela pk.
	 */
	public void buscarResponsavel() {
		
		rapServidores = null;

		if(codigoResponsavel != null && matriculaResponsavel != null){
	        rapServidores = registroColaboradorFacade.pesquisarResponsavel(
	        		codigoResponsavel.shortValue(), matriculaResponsavel, "");
		}

		if(rapServidores == null || rapServidores.getId() == null){
			rapServidores = new RapServidores();
		}
	}

	/**
	 * Pesquisa Status para lista de valores.
	 */
	public void pesquisarResponsavel() {
		listaResponsaveis = leitosInternacaoFacade.pesquisarResponsaveis(responsavel);
	}
	
	public List<RapServidores> pesquisarResponsaveis(String param){
		this.listaResponsaveis = this.leitosInternacaoFacade.pesquisarResponsaveis(param);
		return this.listaResponsaveis;
	}

	public void validarDados() {

		try {
			leitosInternacaoFacade.validarDadosBloqueio(leitos.getLeitoID(), tiposMovimentoLeito.getCodigo().intValue(),
														rapServidores, dataLancamento, justificativa,
														tiposMovimentoLeito, leitos);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.validado = false;
			return;
		}
		
		this.validado = true;
		if(validado){
			this.validado = this.validarModal();
		}
	}
	
	public Boolean verificarRequerido(){
		Boolean retorno = false;
		if (tiposMovimentoLeito != null && tiposMovimentoLeito.getCodigo().intValue() == 23){
			retorno = true;
		}
		return retorno;
	}
	
	public Boolean validarModal(){
		if(this.verificarRequerido()&&StringUtils.isEmpty(this.getJustificativa())){
			return false;
		} else {
			openDialog("modalWG");
			return true;
		}
	}
	
	public void atualizarValidacao(){
		this.validado = false;
		closeDialog("modalWG");
	}
	
	/**
	 * Executa o bloqueio do leito.
	 */
	public void bloquear() {
		this.validado = false;

		String confirmacao = "Leito " + leitos.getLeitoID().toUpperCase() + " bloqueado com sucesso!";

		// Atualiza o leito gerando o novo extrato.
		try {
			leitosInternacaoFacade.inserirExtrato(leitos, tiposMovimentoLeito,
					rapServidores, rapServidores, justificativa, dataLancamento, null, null, null,
					null, null, null);
			limparPesquisa();
			apresentarMsgNegocio(Severity.INFO, confirmacao);
			closeDialog("modalWG");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	
	/**
	 * Ação do botão limpar pesquisa
	 */
	public void limparPesquisa() {
		this.codigoStatus = null;
		this.codigoResponsavel = null;
		this.nomeResponsavel = null;
		this.tiposMovimentoLeito = null;
		this.leitos = null;
		this.rapServidores = null;
		this.justificativa = null;
		this.dataLancamento = new Date();
		listaLeitos = new ArrayList<AinLeitos>();
	}

	// GET'S AND SET'S

	public AinLeitos getLeitos() {
		return leitos;
	}

	public void setLeitos(AinLeitos leitos) {
		this.leitos = leitos;
	}

	public List<AinLeitos> getListaLeitos() {
		return listaLeitos;
	}

	public void setListaLeitos(List<AinLeitos> listaLeitos) {
		this.listaLeitos = listaLeitos;
	}

	public AinTiposMovimentoLeito getTiposMovimentoLeito() {
		return tiposMovimentoLeito;
	}

	public void setTiposMovimentoLeito(
			AinTiposMovimentoLeito tiposMovimentoLeito) {
		this.tiposMovimentoLeito = tiposMovimentoLeito;
	}

	public List<AinTiposMovimentoLeito> getListaTiposMovimentoLeito() {
		return listaTiposMovimentoLeito;
	}

	public void setListaTiposMovimentoLeito(
			List<AinTiposMovimentoLeito> listaTiposMovimentoLeito) {
		this.listaTiposMovimentoLeito = listaTiposMovimentoLeito;
	}

	public Integer getCodigoStatus() {
		return codigoStatus;
	}

	public void setCodigoStatus(Integer codigoStatus) {
		this.codigoStatus = codigoStatus;
	}

	public String getDescricaoStatus() {
		return descricaoStatus;
	}

	public void setDescricaoStatus(String descricaoStatus) {
		this.descricaoStatus = descricaoStatus;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public List<RapServidores> getListaResponsaveis() {
		return listaResponsaveis;
	}

	public void setListaResponsaveis(List<RapServidores> listaResponsaveis) {
		this.listaResponsaveis = listaResponsaveis;
	}

	public Integer getCodigoResponsavel() {
		return codigoResponsavel;
	}

	public void setCodigoResponsavel(Integer codigoResponsavel) {
		this.codigoResponsavel = codigoResponsavel;
	}

	public Integer getMatriculaResponsavel() {
		return matriculaResponsavel;
	}

	public void setMatriculaResponsavel(Integer matriculaResponsavel) {
		this.matriculaResponsavel = matriculaResponsavel;
	}

	public Object getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(Object responsavel) {
		this.responsavel = responsavel;
	}

	public Date getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(Date dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public boolean isValidado() {
		return validado;
	}

	public void setValidado(boolean validado) {
		this.validado = validado;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getIdLeito() {
		return idLeito;
	}

	public void setIdLeito(String idLeito) {
		this.idLeito = idLeito;
	}

	public String getCodigoLeitos() {
		return codigoLeitos;
	}

	public void setCodigoLeitos(String codigoLeitos) {
		this.codigoLeitos = codigoLeitos;
	}
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

}