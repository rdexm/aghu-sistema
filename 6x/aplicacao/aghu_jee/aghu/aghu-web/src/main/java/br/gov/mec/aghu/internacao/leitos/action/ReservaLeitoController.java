package br.gov.mec.aghu.internacao.leitos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.recursoshumanos.Pessoa;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ReservaLeitoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 111311143301598852L;

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
	 * Parametro para inicialização de tipo de reserva
	 */
	@EJB
	private IParametroFacade parametroFacade;
	

	@EJB
	private IPacienteFacade pacienteFacade;


	/**
	 * LOV Leitos
	 */
	private AinLeitos leitos = null;

	/**
	 * LOV Reservar para
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
	private Integer codigoTipoReserva;

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
	 * Descrição do Movimento Leito
	 */
	private String descricaoMvtoLeito;

	/**
	 * Descricao AinTiposMovimentoLeito
	 */
	private String descricaoTipoReserva;

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
	 * 
	 */
	
	private boolean validado = false;
	
	/**
	 * Código de origem do evento
	 */
	private Integer codigoOrigem;
	
	
	/**
	 * LOV Origem
	 */
	private AghOrigemEventos origemEventos = null;
	
	/**
	 * Lista de Origens de Evento para uma Reserva
	 */
	private List<AghOrigemEventos> listaOrigemEvento = new ArrayList<AghOrigemEventos>();
	
	/**
	 * Parametro de consulta de Origem de Evento 
	 */
	private Object origemEvento;
	
	/**
	 * Prontuario informada para associar ao leito reservado.
	 */
	private Integer codigoProntuario;
	
	/**
	 * Informações do Paciente, obtidas através do prontuário informado
	 */
	private AipPacientes paciente;
	
	
	private String voltarPara;

	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	/**
	 * Executa o início da reserva
	 */
	public void inicio() {
	 
		//Inicializa Responsável
		this.iniciarResponsavel();
		
		//Inicializa Tipo de Reserva
		this.iniciarTipoReserva();
		
		if (codigoLeitos != null) {			
			this.buscarLeitos();
		}

	
	}
	
	/**
	 * Inicializa responsável
	 */
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
	
	/**
	 * Inicializa tipo de reserva
	 */
	private void iniciarTipoReserva(){
		try{
			this.codigoTipoReserva = null;
			
			AghParametros paramCodMvtoLeitoReservado = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_RESERVADO);
			
			if(paramCodMvtoLeitoReservado != null){
				this.codigoTipoReserva = paramCodMvtoLeitoReservado.getVlrNumerico().intValue();
				buscarTipoReserva();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Pesquisa AinLeitos pela pk
	 */
	public void buscarLeitos() {
		
		leitos = null;
		
		if (StringUtils.isNotBlank(codigoLeitos)) {
			this.codigoLeitos = this.codigoLeitos.toUpperCase();
			leitos = cadastrosBasicosInternacaoFacade.obterLeitoDesocupado(codigoLeitos);
		} 
		
		if (leitos == null || leitos.getLeitoID() == null) {
            leitos = new AinLeitos();
            setDescricaoMvtoLeito("");
		}else{
			setDescricaoMvtoLeito(leitos.getTipoMovimentoLeito().getGrupoMvtoLeito().getDescricao());
		}
	}
	
	public List<AinLeitos> pesquisarLeitosDesocupados(String param){
		this.listaLeitos = this.cadastrosBasicosInternacaoFacade.pesquisarLeitosDesocupados(param);
		return this.listaLeitos; 
	}

	/**
	 * Busca Tipos de Reserva pela pk.
	 */
	public void buscarTipoReserva() {
		if (codigoTipoReserva != null) {
			tiposMovimentoLeito = cadastrosBasicosInternacaoFacade
					.pesquisarTipoSituacaoLeitoReservados(codigoTipoReserva
							.shortValue());
		} else {
			tiposMovimentoLeito = null;
		}
	}

	public List<AinTiposMovimentoLeito> pesquisarTipoReserva(String param){
		this.listaTiposMovimentoLeito = this.cadastrosBasicosInternacaoFacade.pesquisarTipoSituacaoLeitoReservadosPorDescricao(param);
		return this.listaTiposMovimentoLeito;
	}

	/**
     * Busca Responsável pelo vínculo e matrícula.
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

	public List<RapServidores> pesquisarResponsaveis(String param){
		this.listaResponsaveis = this.leitosInternacaoFacade.pesquisarResponsaveis(param);
		return this.listaResponsaveis;
	}
	
	/**
	 * Busca Tipos de Reserva pela pk.
	 */
	public void buscarOrigemEvento() {
		
		if (codigoOrigem != null) {
			origemEventos = cadastrosBasicosInternacaoFacade.obterOrigemInternacao(codigoOrigem.shortValue());
		} else {
			origemEventos = null;
		}
	}
	
	public List<AghOrigemEventos> pesquisarOrigemEvento(String param){
		this.listaOrigemEvento = this.cadastrosBasicosInternacaoFacade.pesquisarOrigemEventoPorCodigoEDescricao(param);
		return this.listaOrigemEvento;
	}
	
	public void obterPacientePorProntuario(){
		
		paciente = null;
		
		if(codigoProntuario != null) {
			try {
				paciente = pacienteFacade.obterPacientePorCodigoOuProntuario(codigoProntuario, null, null);
				
				if(paciente == null || paciente.getCodigo() == null){
					apresentarMsgNegocio(Severity.ERROR, "AIN_00628");
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	
	public String validarDados() {
		try {
			leitosInternacaoFacade.validarDadosReserva(leitos, tiposMovimentoLeito, rapServidores, rapServidores, rapServidores
											  , dataLancamento,justificativa, tiposMovimentoLeito, leitos, codigoProntuario 
											  , paciente, codigoOrigem, origemEventos);
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			this.validado = false;
			return null;
		}
		openDialog("modalWG");
		this.validado = true;
		return null;
	}
	
	
	
	
	/**
	 * Executa a reserva do leito.
	 */
	public void reservar() {

		// Atualiza o leito gerando o novo extrato.
		try {
			
			if(origemEventos != null){
				if(origemEventos.getSeq() == null || StringUtils.isBlank(origemEventos.getDescricao())){
					origemEventos = null;
				}
			}
			leitosInternacaoFacade.inserirExtrato(leitos, tiposMovimentoLeito
										 ,rapServidores, rapServidores
										 ,justificativa, dataLancamento
										 ,null, paciente, null
										 ,null, null, origemEventos);
			inicio();
			reiniciarReserva();
			String confirmacao = "Leito reservado com sucesso!";
			apresentarMsgNegocio(Severity.INFO,confirmacao);
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	/**
	 * Reinicia reserva
	 */
	public void reiniciarReserva() {
		this.limparPesquisa();
		this.iniciarResponsavel();
		this.iniciarTipoReserva();
	}
	
	/**
	 * Ação do botão limpar pesquisa
	 */
	public void limparPesquisa() {
		this.codigoLeitos = null;
		this.codigoTipoReserva = null;
		this.codigoResponsavel = null;
		this.matriculaResponsavel=null;
		this.nomeResponsavel = null;
		this.tiposMovimentoLeito = null;
		this.descricaoMvtoLeito = null;
		this.leitos = null;
		this.rapServidores = null;
		this.justificativa = null;
		this.dataLancamento = new Date();
		this.listaLeitos = new ArrayList<AinLeitos>();
		this.codigoOrigem = null;
		this.origemEventos = null;
		this.codigoProntuario = null;
		this.paciente = null;
	}
	
	public String cancelar(){
		limparPesquisa();
		return voltarPara;
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

	public String getCodigoLeitos() {
		return codigoLeitos;
	}

	public void setCodigoLeitos(String codigoLeitos) {
		this.codigoLeitos = codigoLeitos;
	}

	public String getDescricaoMvtoLeito() {
		return descricaoMvtoLeito;
	}

	public void setDescricaoMvtoLeito(String descricaoMvtoLeito) {
		this.descricaoMvtoLeito = descricaoMvtoLeito;
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

	public Integer getCodigoTipoReserva() {
		return codigoTipoReserva;
	}

	public void setCodigoTipoReserva(Integer codigoTipoReserva) {
		this.codigoTipoReserva = codigoTipoReserva;
	}

	public String getDescricaoTipoReserva() {
		return descricaoTipoReserva;
	}

	public void setDescricaoTipoReserva(String descricaoTipoReserva) {
		this.descricaoTipoReserva = descricaoTipoReserva;
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

	public Integer getCodigoOrigem() {
		return codigoOrigem;
	}

	public void setCodigoOrigem(Integer codigoOrigem) {
		this.codigoOrigem = codigoOrigem;
	}

	public AghOrigemEventos getOrigemEventos() {
		return origemEventos;
	}

	public void setOrigemEventos(AghOrigemEventos origemEventos) {
		this.origemEventos = origemEventos;
	}

	public List<AghOrigemEventos> getListaOrigemEvento() {
		return listaOrigemEvento;
	}

	public void setListaOrigemEvento(List<AghOrigemEventos> listaOrigemEvento) {
		this.listaOrigemEvento = listaOrigemEvento;
	}

	public Object getOrigemEvento() {
		return origemEvento;
	}

	public void setOrigemEvento(Object origemEvento) {
		this.origemEvento = origemEvento;
	}

	public Integer getCodigoProntuario() {
		return codigoProntuario;
	}

	public void setCodigoProntuario(Integer codigoProntuario) {
		this.codigoProntuario = codigoProntuario;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

}