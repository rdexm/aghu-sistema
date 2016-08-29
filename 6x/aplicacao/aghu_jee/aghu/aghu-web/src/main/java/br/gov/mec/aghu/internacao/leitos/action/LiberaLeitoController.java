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


public class LiberaLeitoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8267445339401690927L;

	/**
	 * Usuario Logado
	 */
	private Pessoa pessoa;

	/**
	 * Injeção da Facade de leitosInternacaoFacade.
	 */
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	/**
	 * Responsável pela pesquisa de AinTiposMovimentoLeito
	 */
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * LOV Leito
	 */
	private AinLeitos leitos = null;

	/**
	 * LOV Nova Situação
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
	private String justificativas;

	/**
	 * Confirma a validação dos dados
	 */
	private boolean validado = false;
	
	/**
	 */
	private String cameFrom;
	
	private String leitoId;

	private boolean justificativaObrigatoria = false;
	
	@PostConstruct
	protected void init() {
		begin(conversation);
	}
	
	/**
	 * Executa o início do controller
	 */
	public void inicio() {
	 

		validado = false;
		RapServidores servidorLogado = null;
		try {
			servidorLogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.INFO, e.getMessage());
		}
		
		if (servidorLogado != null) {
			this.rapServidores = servidorLogado;
		}
		
		this.dataLancamento = new Date();
		
		//Se o leito já vem preenchido é porque é possivel voltar para a tela de pesquisar leitos.
		if (StringUtils.isNotBlank(codigoLeitos)) {
			leitos = cadastrosBasicosInternacaoFacade.obterLeitoBloqueado(codigoLeitos.toUpperCase()); 
			
			if(StringUtils.isNotBlank(cameFrom)){
				this.pesquisarLeitos();
			}
		}
		
		if (tiposMovimentoLeito != null && tiposMovimentoLeito.getCodigo().intValue() == 0
				&& !isSituacaoLeitoBloqueioLimpeza()){
			justificativaObrigatoria = true;
		}
	
	}

	private boolean isSituacaoLeitoBloqueioLimpeza() {
		if(leitos != null && leitos.getTipoMovimentoLeito() != null
				&& DominioMovimentoLeito.BL.equals(leitos.getTipoMovimentoLeito().getGrupoMvtoLeito())){
			return true;
		}
		return false;
	}

	/**
	 * Acao do botao cancelar
	 * @return
	 */
	public String cancelar(){
		return cameFrom;
	}

	/**
	 * Pesquisa AinLeitos para lista de valores.
	 */
	public void pesquisarLeitos() {
		listaLeitos = cadastrosBasicosInternacaoFacade.pesquisarLeitosBloqueados(codigoLeitos.toUpperCase());
	}
	
	public List<AinLeitos> pesquisarLeitosDesocupados(String param){
		this.listaLeitos = this.cadastrosBasicosInternacaoFacade.pesquisarLeitosBloqueados(param);
		return this.listaLeitos;
	}

	/**
	 * Busca Status pela pk.
	 */
	public void buscarStatus() {
		if (codigoStatus == null) {
			tiposMovimentoLeito = cadastrosBasicosInternacaoFacade.pesquisarTipoSituacaoLeitoDesocupado(codigoStatus.shortValue());
		} else {
			tiposMovimentoLeito = new AinTiposMovimentoLeito();
		}
	}

	public List<AinTiposMovimentoLeito> pesquisarMotivos(String param){
		this.listaTiposMovimentoLeito = this.cadastrosBasicosInternacaoFacade.pesquisarTipoSituacaoLeitoDesocupadosPorDescricao(param);
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

	public String validarDados() {

		// Valida filtros
		try {
			leitosInternacaoFacade.validarDadosLiberacao(leitos, tiposMovimentoLeito,rapServidores, dataLancamento, justificativas);
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.validado = false;
			return null;
		}
		openDialog("modalWG");
		this.validado = true;
		return null;

	}
	
	public void cancelarLiberacao() {
		this.validado = false;
	}
	
//	public boolean isJustificativaObrigatoria(){
//		Boolean retorno = false;
//		if (tiposMovimentoLeito != null && tiposMovimentoLeito.getCodigo().intValue() == 0){
//			retorno = true;
//		}
//		return retorno;  
//	}

	/**
	 * Executa a liberacao do leito.
	 */
	public void liberar() {

		Boolean liberar = true;
		try {

			if(isJustificativaObrigatoria() && (this.justificativas==null || this.justificativas.isEmpty())){
				apresentarMsgNegocio(Severity.INFO, "Um valor é obrigatório para o campo Justificativa.");
				liberar = false;
			}
			
			// Verifica se necessita de limpeza
			
			// Atualiza o leito gerando o novo extrato.
			if(liberar){
				String confirmacao = "Leito " + leitos.getLeitoID().toUpperCase() + " liberado com sucesso!";
				if (leitosInternacaoFacade.verificarLimpeza(leitos)) {
					confirmacao = "Leito " + leitos.getLeitoID().toUpperCase() + " possui bloqueio limpeza automatico.";
				}
				leitosInternacaoFacade.inserirExtrato(leitos, tiposMovimentoLeito,
						rapServidores, rapServidores, justificativas, dataLancamento, null, null, null,
						null, null, null);
				
				limparPesquisa();
				apresentarMsgNegocio(Severity.INFO, confirmacao);
			}

		} catch (final ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do botão limpar pesquisa
	 */
	public void limparPesquisa() {
		this.justificativas = null;
		this.validado = false;
		this.codigoResponsavel = null;
		this.nomeResponsavel = null;
		this.tiposMovimentoLeito = null;
		this.leitos = null;
		this.rapServidores = null;
		this.dataLancamento = new Date();
		setJustificativaObrigatoria(false);
	
		RapServidores servidorLogado = null;
		try {
			servidorLogado = this.registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.INFO, e.getMessage());
		}
		
		if (servidorLogado != null) {
			this.rapServidores = servidorLogado;
		}
	}

	// GET AND SET

	public AinLeitos getLeitos() {
		return leitos;
	}

	public void setLeitos(final AinLeitos leitos) {
		this.leitos = leitos;
	}

	public AinTiposMovimentoLeito getTiposMovimentoLeito() {
		return tiposMovimentoLeito;
	}

	public void setTiposMovimentoLeito(
			final AinTiposMovimentoLeito tiposMovimentoLeito) {
		if(tiposMovimentoLeito!=null){
			this.setCodigoStatus(tiposMovimentoLeito.getCodigo().intValue());
		}
		
		if (tiposMovimentoLeito != null && tiposMovimentoLeito.getCodigo().intValue() == 0
				&& !isSituacaoLeitoBloqueioLimpeza()){
			justificativaObrigatoria = true;
		}else{
			justificativaObrigatoria = false;
		}
		
		this.tiposMovimentoLeito = tiposMovimentoLeito;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(final RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public List<AinLeitos> getListaLeitos() {
		return listaLeitos;
	}

	public void setListaLeitos(final List<AinLeitos> listaLeitos) {
		this.listaLeitos = listaLeitos;
	}

	public List<AinTiposMovimentoLeito> getListaTiposMovimentoLeito() {
		return listaTiposMovimentoLeito;
	}

	public void setListaTiposMovimentoLeito(
			final List<AinTiposMovimentoLeito> listaTiposMovimentoLeito) {
		this.listaTiposMovimentoLeito = listaTiposMovimentoLeito;
	}

	public List<RapServidores> getListaResponsaveis() {
		return listaResponsaveis;
	}

	public void setListaResponsaveis(final List<RapServidores> listaResponsaveis) {
		this.listaResponsaveis = listaResponsaveis;
	}

	public Integer getCodigoStatus() {
		return codigoStatus;
	}

	public void setCodigoStatus(final Integer codigoStatus) {
		this.codigoStatus = codigoStatus;
	}

	public Integer getCodigoResponsavel() {
		return codigoResponsavel;
	}

	public void setCodigoResponsavel(final Integer codigoResponsavel) {
		this.codigoResponsavel = codigoResponsavel;
	}

	public Integer getMatriculaResponsavel() {
		return matriculaResponsavel;
	}

	public void setMatriculaResponsavel(final Integer matriculaResponsavel) {
		this.matriculaResponsavel = matriculaResponsavel;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(final String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public Object getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(final Object responsavel) {
		this.responsavel = responsavel;
	}

	public String getDescricaoStatus() {
		return descricaoStatus;
	}

	public void setDescricaoStatus(String descricaoStatus) {
		this.descricaoStatus = descricaoStatus;
	}

	public Date getDataLancamento() {
		return dataLancamento;
	}

	public void setDataLancamento(Date dataLancamento) {
		this.dataLancamento = dataLancamento;
	}

	public boolean isValidado() {
		return validado;
	}

	public void setValidado(final boolean validado) {
		this.validado = validado;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(final Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(final String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public boolean isJustificativaObrigatoria() {
		return justificativaObrigatoria;
	}

	public void setJustificativaObrigatoria(boolean justificativaObrigatoria) {
		this.justificativaObrigatoria = justificativaObrigatoria;
	}

	public String getJustificativas() {
		return justificativas;
	}

	public void setJustificativas(String justificativas) {
		this.justificativas = justificativas;
	}
	
	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}

	public String getCodigoLeitos() {
		return codigoLeitos;
	}

	public void setCodigoLeitos(String codigoLeitos) {
		this.codigoLeitos = codigoLeitos;
	}
}