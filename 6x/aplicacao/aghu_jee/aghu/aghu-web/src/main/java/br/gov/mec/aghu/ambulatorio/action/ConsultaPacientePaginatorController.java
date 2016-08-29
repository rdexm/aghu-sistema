package br.gov.mec.aghu.ambulatorio.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioRetornoAgenda;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ConsultaPacientePaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -5435350655701725866L;
	
	@EJB
	private IParametroFacade parametroFacade;

	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private RelatorioAgendamentoConsultaController relatorioAgendamentoConsultaController;
	
	// Filtros
	private Integer prontuario;
	private Integer pacCodigo;
	private String pacNome;
	private Integer numeroConsulta;
	private Long codCentral;
	private Integer codConsultaAnterior;
	private AacCondicaoAtendimento condicaoAtendimento;
	private Integer gradeSeq;
	private AghEspecialidades especialidade;
	private Date dtInicio;
	private Date dtFim;
	private Integer pacCodigoFonetica;
	private AipPacientes pac;

	// #47350
	private Boolean habilitaVerAltas;
	private String textoAlta;

	// Seleção
	
	// Label Parametrizado
	private String labelZonaSala;
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;
	
	private AacConsultas aacConsultasSelecionada;
	
	private final String PAGE_LISTA_HISTORICO_CONSULTAS = "listarHistoricoConsultas";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente"; 
	private static final String SOLICITA_INTERCONSULTA = "ambulatorio-solicitaInterconsulta";
	
	public enum ConsultaPacienteExceptionCode implements BusinessExceptionCode { 
		MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE, MENSAGEM_RESTRICAO_CONSULTA_PACIENTE, AIP_PACIENTE_NAO_ENCONTRADO 
	}
	
	private String voltarPara;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		try {
    		labelZonaSala = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto() + 
    				"/" +  parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
    		
    	} catch (ApplicationBusinessException e) {
    		apresentarExcecaoNegocio(e);    		
    	}
		
				
	}
	
    /**
     * Método executado ao iniciar a controller
     */
    public void iniciar() {
	 

	 

		// Busca as informações do paciente caso já tenha sido feita a pesquisa fonética 
		if (pacCodigo != null || prontuario != null || pacCodigoFonetica != null) {
			obterInformacoesPaciente(true);
		}
    
	}
	
	
	
	public void pesquisar() {
		//this.setOrder(AacConsultas.Fields.DATA_CONSULTA.toString() + " , " + 
		//		AacGradeAgendamenConsultas.Fields.SEQ.toString() + " desc");
    	
		boolean possuiInformacoesPac = false;
		
		possuiInformacoesPac = obterInformacoesPaciente(false);
		
		if (possuiInformacoesPac) {
			this.dataModel.reiniciarPaginator();			
			
			// #47350 - verificando se deve apresentar o botão de alta
			habilitaVerAltas = pacienteFacade.verificarExisteAlta(pacCodigo);
		}
	}
	
	public void limparPesquisa() {
		prontuario = null;
		pacCodigo = null;
		pacNome = null;
		numeroConsulta = null;
		codCentral = null;
		codConsultaAnterior = null;
		condicaoAtendimento = null;
		gradeSeq = null;
		especialidade = null;
		dtInicio = null;
		dtFim = null;
		pacCodigoFonetica = null;
		pac = null;		
		dataModel.limparPesquisa();
		habilitaVerAltas = false;
	}
	
	
	@Override
	public List<AacConsultas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short condAtendSeq = null;
		Short espSeq = null;
		
		if (condicaoAtendimento != null) {
			condAtendSeq = condicaoAtendimento.getSeq();
		}
		
		if (especialidade != null) {
			espSeq = especialidade.getSeq();
		}
		
		List<AacConsultas> lista = ambulatorioFacade.listarConsultasPaciente(firstResult, maxResult, orderProperty, asc, pacCodigo, numeroConsulta, 
				codCentral, codConsultaAnterior, condAtendSeq, gradeSeq, espSeq, dtInicio, dtFim);
		
		if (lista == null) {
			return new ArrayList<AacConsultas>();
		}
		
		return lista;
	}

	@Override
	public Long recuperarCount() {
		Short condAtendSeq = null;
		Short espSeq = null;
		
		if (condicaoAtendimento != null) {
			condAtendSeq = condicaoAtendimento.getSeq();
		}
		
		if (especialidade != null) {
			espSeq = especialidade.getSeq();
		}
		
		return ambulatorioFacade.listarConsultasPacienteCount(pacCodigo, numeroConsulta, codCentral, codConsultaAnterior, 
				condAtendSeq, gradeSeq, espSeq, dtInicio, dtFim);
	}
	
	/**
	 * Obtem as informações do paciente (nome, codigo e prontuario) e retorna se foi possivel obter
	 * 
	 * @return true caso consiga obter as informações, false caso contrário
	 */
	private boolean obterInformacoesPaciente(boolean inicio) {
				
		if (pacCodigo == null && prontuario == null && numeroConsulta == null && codCentral == null && pacCodigoFonetica == null) {
			apresentarMsgNegocio(Severity.ERROR,ConsultaPacienteExceptionCode.MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE.toString());
			
		} else if (prontuario != null && pacCodigo != null && !inicio
				&& 	(pac == null || (pac != null && (!pac.getProntuario().equals(prontuario) || !pac.getCodigo().equals(pacCodigo))))) {
			
			apresentarMsgNegocio(Severity.ERROR, ConsultaPacienteExceptionCode.MENSAGEM_RESTRICAO_CONSULTA_PACIENTE.toString());
			
		}
		else if (prontuario != null && !inicio) {
    		pac = pacienteFacade.obterPacientePorProntuario(prontuario);
    		if(pac != null){	
    			pacCodigo = pac.getCodigo();
    			pacNome = pac.getNome();
    		} else {
    			apresentarMsgNegocio(Severity.ERROR, ConsultaPacienteExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());       			
    			pacNome = null;
    			return false;
    		}
    		
    		return true;
    		
    	} else if (pacCodigo != null  && !inicio) {
    		
			pac = pacienteFacade.obterPacientePorCodigo(pacCodigo);
			
			if(pac != null){
				pacCodigo = pac.getCodigo();
				prontuario = pac.getProntuario();
	    		pacNome = pac.getNome();
			} else {
				apresentarMsgNegocio(Severity.ERROR, ConsultaPacienteExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());    			
    			pacNome = null;
    			return false;
			}
    		    		
    		return true;
    		
    	} else if (pacCodigoFonetica != null && inicio) {
			pac = pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			pacCodigo = pac.getCodigo();
			prontuario = pac.getProntuario();
    		pacNome = pac.getNome();
		}
		
		if (numeroConsulta != null || codCentral != null) {
			return true;
		}
		return false;
	}
	
	public String listarHistoricoConsultas(){
		return PAGE_LISTA_HISTORICO_CONSULTAS;
	}
	
	public List<AacCondicaoAtendimento> listarCondicaoAtendimento() {
		return ambulatorioFacade.listarCondicaoAtendimento();
	}
	
	public List<AghEspecialidades> obterEspecialidade(String parametro) {
		return aghuFacade.getListaEspecialidadesTodasSituacoes((String) parametro);
	}
	
	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}
	
	
	/**
	 * Realiza a chamada da controller responsavel por imprimir o relatorio de agendamento da consulta via CUPS.
	 */
	public void imprimirRelatorio(Integer consultaNumero) {
		relatorioAgendamentoConsultaController.imprimirAgendamentoConsulta(consultaNumero);
	}
	
	public String solicitarInterconsulta(){
		return SOLICITA_INTERCONSULTA;
	}
	
	/*GET/SET*/
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public Long getCodCentral() {
		return codCentral;
	}

	public void setCodCentral(Long codCentral) {
		this.codCentral = codCentral;
	}

	public Integer getCodConsultaAnterior() {
		return codConsultaAnterior;
	}

	public void setCodConsultaAnterior(Integer codConsultaAnterior) {
		this.codConsultaAnterior = codConsultaAnterior;
	}

	public AacCondicaoAtendimento getCondicaoAtendimento() {
		return condicaoAtendimento;
	}

	public void setCondicaoAtendimento(AacCondicaoAtendimento condicaoAtendimento) {
		this.condicaoAtendimento = condicaoAtendimento;
	}

	public Integer getGradeSeq() {
		return gradeSeq;
	}

	public void setGradeSeq(Integer gradeSeq) {
		this.gradeSeq = gradeSeq;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getLabelZonaSala() {
		return labelZonaSala;
	}

	public void setLabelZonaSala(String labelZonaSala) {
		this.labelZonaSala = labelZonaSala;
	}
	
	public String getDthrMarcacao(Date dataMarcacao) {
		if(dataMarcacao != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			return sdf.format(dataMarcacao);
		}
		else {
			return "-";
		}
	}
	
	public String obterDiaSemana(Date dtConsulta){
		String retorno = "";
		if(dtConsulta != null) {
		    Calendar cal = Calendar.getInstance();  
		    cal.setTime(dtConsulta);  
		    int day = cal.get(Calendar.DAY_OF_WEEK);  
		    switch (day) {
			case 1:
				retorno = "Domingo";
				break;
			case 2:
				retorno = "Segunda-Feira";
				break;
			case 3:
				retorno = "Terça-Feira";
				break;
			case 4:
				retorno = "Quarta-Feira";
				break;
			case 5:
				retorno = "Quinta-Feira";
				break;
			case 6:
				retorno = "Sexta-Feira";
				break;
			case 7:
				retorno = "Sábado";
				break;
			default:
				break;
			}
		}
		return retorno;
	}

	public String voltar() {
		limparPesquisa();
		return this.voltarPara;
	}
	
	
	/**
     * Obtém o nome do responsável pela marcação da consulta.
     */
	public String obterResponsavelMarcacaoConsulta(Integer consulta) {
		return ambulatorioFacade.obterNomeResponsavelMarcacaoConsulta(consulta);
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}


	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}


	public AacConsultas getAacConsultasSelecionada() {
		return aacConsultasSelecionada;
	}


	public void setAacConsultasSelecionada(AacConsultas aacConsultasSelecionada) {
		this.aacConsultasSelecionada = aacConsultasSelecionada;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}	public Boolean getHabilitaVerAltas() {
		return habilitaVerAltas;
	}

	public void setHabilitaVerAltas(Boolean habilitaVerAltas) {
		this.habilitaVerAltas = habilitaVerAltas;
	}

	public String getTextoAlta() {
		return textoAlta;
	}

	public void setTextoAlta(String textoAlta) {
		this.textoAlta = textoAlta;
	}
	
	/**
	 * Realiza a exibição do pop up de altas
	 * #47350
	 */
	public void exibirAltas(){
		this.montarTextoAltas();
		RequestContext.getCurrentInstance().execute("PF('modalAlta').show()");
	}
	
	/**
	 * Obtém e prepara o texto para exibir a informação de alta do paciente
	 * #47350
	 */
	private void montarTextoAltas() {
		List<MamAltaSumario> listaAlta = this.pacienteFacade.obterDadosAltaPaciente(pacCodigo);

		if (listaAlta != null && !listaAlta.isEmpty()) {
			StringBuilder texto = new StringBuilder();
			SimpleDateFormat formatData = new SimpleDateFormat("dd/MM/yyyy");
			String bloqueio = "";

			for (MamAltaSumario mamAltaSumario : listaAlta) {
				bloqueio = "";
				if (mamAltaSumario.getRetornoAgenda() == DominioRetornoAgenda.B && mamAltaSumario.getServidorValida() != null) {
					bloqueio = " RETORNO BLOQUEADO POR " + mamAltaSumario.getServidorValida().getPessoaFisica().getNome();
				}
				
				texto.append(String.format("** %s - %s - %s%s\r\n", 
								mamAltaSumario.getEspecialidade().getSigla(), 
								mamAltaSumario.getDescEsp(), 
								formatData.format(mamAltaSumario.getDthrValida()), 
								bloqueio));
			}

			this.textoAlta = texto.toString();
		}
	}
}
