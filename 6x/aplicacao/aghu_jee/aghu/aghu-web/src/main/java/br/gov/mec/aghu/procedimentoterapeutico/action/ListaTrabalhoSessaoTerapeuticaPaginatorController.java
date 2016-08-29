package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioControleFrequenciaSituacao;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentosPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ConsultaDadosAPACVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DadosRelatorioControleFrequenciaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ExtratoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAgendadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProcedimentosAPACVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.action.SecurityController;

public class ListaTrabalhoSessaoTerapeuticaPaginatorController extends ActionReport{
	private static final long serialVersionUID = 5626145373844848030L;
	private final String TAB_1="aba1";
	private final String TAB_2="aba2";
	private final String TAB_3="aba3";
	private final String TAB_4="aba4";
	private final String TAB_5="aba5";
	private static final String TRACO = " - ";
	private static final String PONTO = ".";
	private static final String ESPACO = " ";
	private static final String CONTROLE_DE_FREQUENCIA_NAO_IMPRESSO_PARA = "Controle de Frequência não impresso para ";
	private static final String CLIQUE_PARA_IMPRIMIR = "Clique para Imprimir.";
	private static final String CONTROLE_DE_FREQUENCIA_MANUAL_JA_IMPRESSO_PARA = "Controle de Frequência manual já impresso para ";
	private static final String CONTROLE_DE_FREQUENCIA_RELACIONADO_A_APAC_JA_IMPRESSO_PARA = "Controle de Frequência relacionado à APAC já impresso para ";
	private static final String CLIQUE_PARA_IMPRIMIR_NOVO_CONTROLE_DE_FREQUENCIA = "Clique para Imprimir Novo Controle de Frequência.";
		
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	@Inject
	private SecurityController securityController;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@Inject
	private AbaAgardandoAtendimentoPaginatorController controllerAba3;
	@Inject
	private AbaEmAtendimentoPaginatorController controllerAba4;
	private Integer selectedTab;
	private Tab selecionarAbas;
	@EJB
    private IParametroFacade parametroFacade;
	private MpaProtocoloAssistencial mpaProtocoloAssistencial; 
	private MptLocalAtendimento localAtendimento; 
	private List<MptTipoSessao> listaTipoSessao = new ArrayList<MptTipoSessao>();
	private Short tipoSessaoCombo;
	private DominioTurno turnoCombo;
	private DominioTipoAcomodacao acomodacaoCombo;
	private List<MptSalas> listaSalas = new ArrayList<MptSalas>();
	private Short salaCombo;
	private Date dataInicial;
	private RapServidores servidorLogado;
	private List<ListaPacienteAgendadoVO> listaPacientes;
	private ListaPacienteAgendadoVO parametroSelecionado;
	private List<DadosRelatorioControleFrequenciaVO> listDadosRelatorioControleFrequencia;
	private DadosRelatorioControleFrequenciaVO dadosRelatorioControleFrequenciaVO;
	private Long countPaciente;
	private MptTurnoTipoSessao horario;
	private Integer toggleFiltro = 0;
	@Inject
	private ListaAcolhimentoPaginatorController listaAcolhimentoPaginatorController;
	@Inject
	private SistemaImpressao sistemaImpressao;
	private boolean exibirColunaApac;
	private boolean exibirColunaLM;
	private boolean exibirColunaCF;
	private boolean exibirAbaAcolhimento;
	private List<String> listaSiglas = new ArrayList<String>();
	private ListaPacienteAgendadoVO selecionado;
	private List<AgendamentosPacienteVO> listaAgendamentosPaciente;
	private AgendamentosPacienteVO selecionadoAgendamentoPaciente;
	private List<ExtratoSessaoVO> listaExtratoSessao = new ArrayList<ExtratoSessaoVO>();
	private ExtratoSessaoVO selecionadoSessaoModal;
	private ListaPacienteAgendadoVO dadoRelatorio;	
	private AipPacientes pacienteRelatorio;
	private ConsultaDadosAPACVO consultaDadosAPAC;
	private boolean btAPAC = false;
	private boolean pauseEmAtendimento = false;
	private boolean pauseAguardAtendimento = false;
	private boolean fromBack = false;
	private List<ProcedimentosAPACVO> listaProcedimentosApac = new ArrayList<ProcedimentosAPACVO>();
	private AipPacientes pacienteAgendamentoModal;
	@Inject
	private ListaConcluidoPaginatorController listaConcluidoPaginatorController;
	private String cameFrom;
	
	public MptTurnoTipoSessao getHorario() {
		return horario;
	}

	public void setHorario(MptTurnoTipoSessao horario) {
		this.horario = horario;
	}

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public void iniciar() {	
		if (!fromBack){	
			selecionarAbas = new Tab();
			servidorLogado = servidorLogadoFacade.obterServidorLogado();
			MptFavoritoServidor retorno = procedimentoTerapeuticoFacade.pesquisarFavoritosServidor(servidorLogado);
			if (retorno != null){
				if (retorno.getTipoSessao() != null){
					setTipoSessaoCombo(retorno.getTipoSessao().getSeq());
				}
				if (retorno.getSala() != null){
					setSalaCombo(retorno.getSala().getSeq());
				}
			}
			dataInicial = new Date();
			carregarCombos();
			listaPacientes = new ArrayList<ListaPacienteAgendadoVO>();
			selectedTab = 0;
			selecionarAbas.setId(TAB_1);
			pauseAguardAtendimento = false;
			pauseEmAtendimento = false;
		}
		pesquisarAbas();
		fromBack = false;
	}
	
	public List<MpaProtocoloAssistencial> pesquisarProtocolo(final String pesquisa) {		
		try {
			procedimentoTerapeuticoFacade.validarCampoTipoSessao(tipoSessaoCombo);		
			return this.returnSGWithCount(procedimentoTerapeuticoFacade.buscarProtocolo(pesquisa.trim(), tipoSessaoCombo),pesquisarProtocoloCount(pesquisa.trim()));			
		}  catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public Long pesquisarProtocoloCount(final String strPesquisa) {
		return procedimentoTerapeuticoFacade.buscarProtocoloCount(strPesquisa, tipoSessaoCombo);
	}

	/* Consulta do Suggestion Box de Acomodação (Local Atendimento)*/
	public List<MptLocalAtendimento> pesquisarLocalAtendimento(final String pesquisa) {
		try {
			procedimentoTerapeuticoFacade.validarCampoSala(salaCombo);
			return this.returnSGWithCount(procedimentoTerapeuticoFacade.buscarLocalAtendimento(pesquisa.trim(), salaCombo),pesquisarLocalAtendimentoCount(pesquisa.trim()));			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public Long pesquisarLocalAtendimentoCount(final String strPesquisa) {
		return procedimentoTerapeuticoFacade.buscarLocalAtendimentoCount(strPesquisa, salaCombo);
	}

	public void carregarCombos() {
		carregarComboTipoSessao();
		carregarComboSala();
	}
		
	private void carregarComboTipoSessao(){
		listaTipoSessao = procedimentoTerapeuticoFacade.buscarTipoSessao();
	}

	public void carregarComboSala(){
		if (tipoSessaoCombo != null){
			listaSalas = procedimentoTerapeuticoFacade.buscarSala(tipoSessaoCombo);
			mpaProtocoloAssistencial = null;
			localAtendimento = null;
		}else{
			salaCombo = null;
			listaSalas = null;
			mpaProtocoloAssistencial = null;
			localAtendimento = null;
		}
	}

	public void limparCampoAcomodacao(){
		localAtendimento = null;		
	}
	
	public void pesquisarAbas(){
		listaSiglas = procedimentoTerapeuticoFacade.obterSiglaCaracteristicaPorTpsSeq(tipoSessaoCombo);
		exibirAbaAcolhimento = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas, "TRIA");
		if (selecionarAbas.getId().equals(TAB_1)) {
			pesquisar();		
		} else if (selecionarAbas.getId().equals(TAB_2)) {
			listaAcolhimentoPaginatorController.pesquisar();
		} else if (selecionarAbas.getId().equals(TAB_3)) {
			controllerAba3.pesquisar();			
		} else if (selecionarAbas.getId().equals(TAB_4)) {
			controllerAba4.pesquisar();
		} else if (selecionarAbas.getId().equals(TAB_5)) {
			listaConcluidoPaginatorController.pesquisar();
		}
	}
		
	public void pesquisar() {		
		try {
			List<ListaPacienteAgendadoVO> pacienteAux = null;
			procedimentoTerapeuticoFacade.validarCampos(dataInicial, tipoSessaoCombo);
			listaPacientes = new ArrayList<ListaPacienteAgendadoVO>();
			countPaciente = null;
			pacienteAux = this.procedimentoTerapeuticoFacade.pesquisarListaPacientes(dataInicial, horario, tipoSessaoCombo, salaCombo, localAtendimento, acomodacaoCombo, mpaProtocoloAssistencial);
			incluiMesToolTipBlue(pacienteAux);
			countPaciente = procedimentoTerapeuticoFacade.pesquisarListaPacientesCount(dataInicial, horario, tipoSessaoCombo, salaCombo, localAtendimento, acomodacaoCombo, mpaProtocoloAssistencial);
			toggleFiltro = -1;			
			procedimentoTerapeuticoFacade.validarListagem(pacienteAux);
			carregarExibicaoComponentes();
			selecionado = null;
		} catch (BaseListException e) {
				apresentarExcecaoNegocio(e);			
		} catch (ApplicationBusinessException e) {
			 apresentarExcecaoNegocio(e);
		}
	}
		
	private void incluiMesToolTipBlue(List<ListaPacienteAgendadoVO> listPacientes) {
		for(ListaPacienteAgendadoVO paciente : listPacientes ) {
			StringBuilder concatenaMesToolTipBlue = new StringBuilder(100);
			StringBuilder concatenaMesToolTipRed = new StringBuilder(100);
			StringBuilder concatenaMesToolTipGreen = new StringBuilder(100);
			consultaDadosAPAC= this.procedimentoTerapeuticoFacade.dadosPacienteAPAC(paciente.getData(),(int)paciente.getHorarioSessaoSeq().shortValue());
			if (consultaDadosAPAC != null) {
				concatenaMesToolTipRed.append(CONTROLE_DE_FREQUENCIA_NAO_IMPRESSO_PARA)
				.append(StringUtils.capitalize(obtemMesAno(String.valueOf(consultaDadosAPAC.getCpeMes()),String.valueOf(consultaDadosAPAC.getCpeAno())).toLowerCase()))
				.append(PONTO).append(ESPACO).append(CLIQUE_PARA_IMPRIMIR);
				paciente.setMesToolTipRed(concatenaMesToolTipRed.toString());
				
				concatenaMesToolTipBlue.append(CONTROLE_DE_FREQUENCIA_MANUAL_JA_IMPRESSO_PARA)
				.append(StringUtils.capitalize(obtemMesAno(String.valueOf(consultaDadosAPAC.getCpeMes()),String.valueOf(consultaDadosAPAC.getCpeAno())).toLowerCase()))
				.append(PONTO).append(ESPACO).append(CLIQUE_PARA_IMPRIMIR_NOVO_CONTROLE_DE_FREQUENCIA);
				paciente.setMesToolTipBlue(concatenaMesToolTipBlue.toString());
				
				concatenaMesToolTipGreen.append(CONTROLE_DE_FREQUENCIA_RELACIONADO_A_APAC_JA_IMPRESSO_PARA)
				.append(StringUtils.capitalize(obtemMesAno(String.valueOf(consultaDadosAPAC.getCpeMes()),String.valueOf(consultaDadosAPAC.getCpeAno())).toLowerCase()))
				.append(PONTO).append(ESPACO).append(CLIQUE_PARA_IMPRIMIR_NOVO_CONTROLE_DE_FREQUENCIA);
				paciente.setMesToolTipGreen(concatenaMesToolTipGreen.toString());
			} else {
				concatenaMesToolTipRed.append(CONTROLE_DE_FREQUENCIA_NAO_IMPRESSO_PARA)
				.append(StringUtils.capitalize(obtemMesAno(new Date()).toLowerCase()))
				.append(PONTO).append(ESPACO).append(CLIQUE_PARA_IMPRIMIR);
				paciente.setMesToolTipRed(concatenaMesToolTipRed.toString());
				
				concatenaMesToolTipBlue.append(CONTROLE_DE_FREQUENCIA_MANUAL_JA_IMPRESSO_PARA)
				.append(StringUtils.capitalize(obtemMesAno(new Date()).toLowerCase()))
				.append(PONTO).append(ESPACO).append(CLIQUE_PARA_IMPRIMIR_NOVO_CONTROLE_DE_FREQUENCIA);
				paciente.setMesToolTipBlue(concatenaMesToolTipBlue.toString());
				
				concatenaMesToolTipGreen.append(CONTROLE_DE_FREQUENCIA_RELACIONADO_A_APAC_JA_IMPRESSO_PARA)
				.append(StringUtils.capitalize(obtemMesAno(new Date()).toLowerCase()))
				.append(PONTO).append(ESPACO).append(CLIQUE_PARA_IMPRIMIR_NOVO_CONTROLE_DE_FREQUENCIA);
				paciente.setMesToolTipGreen(concatenaMesToolTipGreen.toString());
			}
			listaPacientes.add(paciente);
		}
	}

	private void carregarExibicaoComponentes() {
		exibirColunaApac = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas,"APAC");
		exibirColunaLM = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas,"LIBM");
		exibirColunaCF = procedimentoTerapeuticoFacade.exibirColuna(listaSiglas,"FREQ");
	}
	
	public void limpar() {
		mpaProtocoloAssistencial = null;
		localAtendimento = null;
		tipoSessaoCombo = null;
		turnoCombo = null;
		acomodacaoCombo = null;
		salaCombo = null;
		dataInicial = null;
		listaPacientes = null;
		controllerAba4.setListaPacientes(null);
		controllerAba3.setListaPacientes(null);
		countPaciente = null;
		toggleFiltro = 0;		
		selecionado = null;
		selecionadoAgendamentoPaciente = null;
		setSelecionadoSessaoModal(null);
		listaSiglas = null;
		exibirColunaApac = false;
		exibirColunaLM = false;
		exibirColunaCF = false;
		exibirAbaAcolhimento = false;
		fromBack = false;
	}
	
	public String formatarIdade(Date dataNascimento) throws ParseException {
		if(StringUtils.isNotBlank(dataNascimento.toString())){			
			return DateUtil.getIdade(dataNascimento).toString();
		}
		return null;
	}

	public String obterHintColuna(String colorColuna) throws ParseException {
		String messagem = "";
		if (colorColuna != null && colorColuna.equals("#FFFFD9")){
			messagem = "Primeira Sessão";
		}else if (colorColuna != null && colorColuna.equals("#93C47D")){
			messagem = "Reserva";
		}if (colorColuna != null && colorColuna.equals("#00FFFF")){
			messagem = "Paciente portador de Germe Multirresistente.";
		}
		return messagem;
	}

	public String obterHintLinha(String colorLinha) throws ParseException {
		String messagem = "";
		if (colorLinha != null && colorLinha.equals("#00FFFF")){
			messagem = "Paciente portador de Germe Multirresistente.";
		}if (colorLinha != null && colorLinha.equals("#93C47D")){
			messagem = "Reserva";
		}
		return messagem;
	}

	public String hintProtocolo(List<MptProtocoloCiclo> listaProtocolo) {
		String resultado = "";
		StringBuilder protocolosBuilder = new StringBuilder();
		resultado = concatenarProtocolo(listaProtocolo, resultado,protocolosBuilder);	
		return resultado;
	}
	
	public void chegouPaciente(ListaPacienteAgendadoVO paciente) {
		listaAcolhimentoPaginatorController.setListaPacienteAgendadoVO(paciente);
		procedimentoTerapeuticoFacade.chegouPaciente(paciente);		
		pesquisar();
	}		
	
    /* Trunca nome da Grid.*/
	public String obterHint(String item, Integer tamanhoMaximo) {
		String retorno = item;
		if (retorno.length() > tamanhoMaximo) {
			retorno = StringUtils.abbreviate(retorno, tamanhoMaximo);
		}
		return retorno;
	}
	
	public String protocoloTruncado(List<MptProtocoloCiclo> itens, Integer tamanhoMaximo) {
		String resultado = "";
		StringBuilder protocolosBuilder = new StringBuilder();
		resultado = concatenarProtocolo(itens, resultado, protocolosBuilder);
		if (resultado.length() > tamanhoMaximo) {
			resultado = StringUtils.abbreviate(resultado, tamanhoMaximo);
		}
		return resultado;
	}
	
	private String concatenarProtocolo(List<MptProtocoloCiclo> listaProtocolo, String resultado, StringBuilder protocolosBuilder) {
		if (listaProtocolo == null || listaProtocolo.isEmpty()){
			return resultado;
		}else {
			boolean primeira = true;
			for (MptProtocoloCiclo mptProtocoloCiclo : listaProtocolo) {
				if (mptProtocoloCiclo.getDescricao() != null) {
					resultado = protocolosBuilder.append(mptProtocoloCiclo.getDescricao()).toString();
					break;
				}else{
					if (mptProtocoloCiclo.getMpaVersaoProtAssistencial() != null && mptProtocoloCiclo.getMpaVersaoProtAssistencial().getMpaProtocoloAssistencial() != null) {
						if (primeira) {
							resultado = protocolosBuilder.append(mptProtocoloCiclo.getMpaVersaoProtAssistencial().getMpaProtocoloAssistencial().getTitulo()).toString();
							primeira = false;
						} else {
							resultado = protocolosBuilder.append(" - " + mptProtocoloCiclo.getMpaVersaoProtAssistencial().getMpaProtocoloAssistencial().getTitulo()).toString();
						}
					}
				}
			}	
		}
		return resultado;
	}
	
	/* Retorna os horários do turno;*/
	public void consultarHorarioTurno() {
			try {
			horario = null;
			if (turnoCombo != null){
				procedimentoTerapeuticoFacade.validarCampoTipoSessao(tipoSessaoCombo);
				MptTurnoTipoSessao retorno = procedimentoTerapeuticoFacade.obterHorariosTurnos(tipoSessaoCombo, turnoCombo);
				horario = retorno;				
			}
		} catch (ApplicationBusinessException e) {
			turnoCombo = null;			
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void handleClose(CloseEvent event) { 
		selecionado = null;
	}	

	public void visualizarAgendaPacienteModal(Integer pacCodigo){
		if(pacCodigo != null){
			pacienteAgendamentoModal = procedimentoTerapeuticoFacade.obterPacientePorPacCodigo(pacCodigo);
			listaAgendamentosPaciente = procedimentoTerapeuticoFacade.obterAgendamentosPaciente(pacCodigo);		
			RequestContext.getCurrentInstance().execute("PF('modalVisualizaAgendaPaciente').show()");
		}
	}
	
	public void visualizarExtratoSessaoModal(Integer pacCodigo) {		
		listaExtratoSessao = null;	
		listaExtratoSessao = this.procedimentoTerapeuticoFacade.pesquisarExtratoSessao(pacCodigo);
	}
	
	public String hintExtratoSessao(String tipoJustificativaDescrica, String descricaoJustificativa,Integer tamanhoMaximo) {
		String retorno = tipoJustificativaDescrica+TRACO+descricaoJustificativa;
		if (retorno.length() > tamanhoMaximo) {
			retorno = StringUtils.abbreviate(retorno, tamanhoMaximo);
		}
		return retorno;
	}
	
	public boolean showHint(String tipoJustificativaDescrica, String descricaoJustificativa,Integer tamanhoMaximo) {
		String retorno = tipoJustificativaDescrica+TRACO+descricaoJustificativa;
		if (retorno.length() > tamanhoMaximo) {
			return true;
		}
		return false;
	}
	
	public void dadosModalRelatorio ( ListaPacienteAgendadoVO item) {
		this.setDadoRelatorio(item);
		this.setBtAPAC(false);
		pacienteRelatorio = procedimentoTerapeuticoFacade.dadosPacienteRelatorio(dadoRelatorio.getProntuario());
		consultaDadosAPAC= this.procedimentoTerapeuticoFacade.dadosPacienteAPAC(dadoRelatorio.getData(),(int)dadoRelatorio.getHorarioSessaoSeq().shortValue());
		if(	consultaDadosAPAC != null){	
			listaProcedimentosApac = this.procedimentoTerapeuticoFacade.pesquisarprocedimentoApac(consultaDadosAPAC.getAtmNumero());
			this.setBtAPAC(true);
		}
		RequestContext.getCurrentInstance().execute("PF('modalControleFrequenciaWG').show()");
	}
	
	private void criaListaDadosRelatorioFrequenciaApac(AipPacientes pacienteRelatorio, ConsultaDadosAPACVO consultaDadosAPAC, List<ProcedimentosAPACVO> listaProcedimentosApac) {
		listDadosRelatorioControleFrequencia = new ArrayList<DadosRelatorioControleFrequenciaVO>();
		dadosRelatorioControleFrequenciaVO = new DadosRelatorioControleFrequenciaVO();
		dadosRelatorioControleFrequenciaVO.setNomePaciente(pacienteRelatorio.getNome());
		dadosRelatorioControleFrequenciaVO.setCpfFormatado(formataCPF());
		dadosRelatorioControleFrequenciaVO.setProntuarioPaciente(String.valueOf(dadoRelatorio.getProntuario()));
		dadosRelatorioControleFrequenciaVO.setMesReferencia(obtemDataPorExtenso(new Date()));
		try {
			dadosRelatorioControleFrequenciaVO.setLocalData(this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE).getVlrTexto()+",         "+obtemMesAnoPorExtenso());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	       
		if (consultaDadosAPAC != null) {
			dadosRelatorioControleFrequenciaVO.setNumeroAPAC(String.valueOf(consultaDadosAPAC.getAtmNumero()));
			dadosRelatorioControleFrequenciaVO.setDataInicioTratamento(formataDataTratamento(consultaDadosAPAC.getDtInicioTratamento()));
			dadosRelatorioControleFrequenciaVO.setDataFimTratamento(formataDataTratamento(consultaDadosAPAC.getDtFimTratamento()));
			dadosRelatorioControleFrequenciaVO.setMesDeclaro(obtemMesAno(String.valueOf(consultaDadosAPAC.getCpeMes()),String.valueOf(consultaDadosAPAC.getCpeAno())));
			dadosRelatorioControleFrequenciaVO.setProcedimentoPrincipal(null);
			dadosRelatorioControleFrequenciaVO.setProcedimentoSec1(null);
			dadosRelatorioControleFrequenciaVO.setProcedimentoSec2(null);
			for (ProcedimentosAPACVO procedimento : listaProcedimentosApac) {
				if (DominioPrioridadeCid.P.equals(procedimento.getIndPrioridade())) {
					dadosRelatorioControleFrequenciaVO.setProcedimentoPrincipal(procedimento.getCodTabela());
				} else if (DominioPrioridadeCid.S.equals(procedimento.getIndPrioridade())) {
					if (dadosRelatorioControleFrequenciaVO.getProcedimentoSec1() == null) {
						dadosRelatorioControleFrequenciaVO.setProcedimentoSec1(procedimento.getCodTabela());
					} else if (dadosRelatorioControleFrequenciaVO.getProcedimentoSec2() == null) {
						dadosRelatorioControleFrequenciaVO.setProcedimentoSec2(procedimento.getCodTabela());
					}
				}
			}
		} else {
			dadosRelatorioControleFrequenciaVO.setNumeroAPAC(null);
			dadosRelatorioControleFrequenciaVO.setDataInicioTratamento(null);
			dadosRelatorioControleFrequenciaVO.setDataFimTratamento(null);
			dadosRelatorioControleFrequenciaVO.setMesDeclaro(obtemMesAno(new Date()));
			dadosRelatorioControleFrequenciaVO.setProcedimentoPrincipal(null);
			dadosRelatorioControleFrequenciaVO.setProcedimentoSec1(null);
			dadosRelatorioControleFrequenciaVO.setProcedimentoSec2(null);
		}
		listDadosRelatorioControleFrequencia.add(dadosRelatorioControleFrequenciaVO);
	}
	
	private void criaListaDadosRelatorioFrequenciaManual(AipPacientes pacienteRelatorio, ConsultaDadosAPACVO consultaDadosAPAC, List<ProcedimentosAPACVO> listaProcedimentosApac) {
		listDadosRelatorioControleFrequencia = new ArrayList<DadosRelatorioControleFrequenciaVO>();
		dadosRelatorioControleFrequenciaVO = new DadosRelatorioControleFrequenciaVO();
		dadosRelatorioControleFrequenciaVO.setNomePaciente(pacienteRelatorio.getNome());
		dadosRelatorioControleFrequenciaVO.setCpfFormatado(formataCPF());
		dadosRelatorioControleFrequenciaVO.setProntuarioPaciente(String.valueOf(dadoRelatorio.getProntuario()));
		dadosRelatorioControleFrequenciaVO.setMesReferencia(obtemDataPorExtenso(new Date()));
		try {
			dadosRelatorioControleFrequenciaVO.setLocalData(this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_END_CIDADE).getVlrTexto()+",         "+obtemMesAnoPorExtenso());
		    	       	   	
		  } catch (ApplicationBusinessException e) {
		             apresentarExcecaoNegocio(e);
		  }
      dadosRelatorioControleFrequenciaVO.setNumeroAPAC(null);
      dadosRelatorioControleFrequenciaVO.setDataInicioTratamento(null);
	  dadosRelatorioControleFrequenciaVO.setDataFimTratamento(null);
	  dadosRelatorioControleFrequenciaVO.setMesDeclaro(obtemMesAno(new Date()));
	  dadosRelatorioControleFrequenciaVO.setProcedimentoPrincipal(null);
	  dadosRelatorioControleFrequenciaVO.setProcedimentoSec1(null);
	  dadosRelatorioControleFrequenciaVO.setProcedimentoSec2(null);
	  listDadosRelatorioControleFrequencia.add(dadosRelatorioControleFrequenciaVO);
	}
	
	private String formataCPF() {
		String cpf = String.valueOf(pacienteRelatorio.getCpf());
		int digitos = cpf.length()-2;
		StringBuilder cpfFormatado = new StringBuilder();
		String cpfP = String.valueOf(pacienteRelatorio.getCpf()).substring( (digitos-digitos), digitos)+"/";
		String digitosCpf = String.valueOf(pacienteRelatorio.getCpf()).substring(digitos);
		cpfFormatado.append(cpfP).append(digitosCpf);
		return cpfFormatado.toString();
	}

	public void injetarValores(TabChangeEvent event) {
		selecionarAbas = new Tab();
		selecionarAbas = event.getTab();		
		pauseAguardAtendimento = false;
		pauseEmAtendimento = false;
		if (selecionarAbas.getId().equals(TAB_1)){
			selectedTab = 0;			
			pesquisar();
		}else if(selecionarAbas.getId().equals(TAB_2)){
			selectedTab = 1;			
			listaAcolhimentoPaginatorController.setControllerFiltro(this);
			listaAcolhimentoPaginatorController.iniciar();
		}else if(selecionarAbas.getId().equals(TAB_3)){
			if(exibirAbaAcolhimento){
				selectedTab = 2;
			}else{
				selectedTab = 1;
			}
			pauseAguardAtendimento = true;									
			controllerAba3.setControleFiltro(this);
			controllerAba3.iniciar();
		}else if(selecionarAbas.getId().equals(TAB_4)){
			if(exibirAbaAcolhimento){
				selectedTab = 3;
			}else{
				selectedTab = 2;
			}
			pauseEmAtendimento = true;			
			controllerAba4.setControleFiltro(this);
			controllerAba4.iniciar();
		}else if(selecionarAbas.getId().equals(TAB_5)){
			if(exibirAbaAcolhimento){
				selectedTab = 4;
			}else{
				selectedTab = 3;
			}
			listaConcluidoPaginatorController.setControllerFiltro(this);
			listaConcluidoPaginatorController.iniciar();
		}		
	}
	
	public String trucarProtocolo(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			return StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}

	public String descricaoFiltrosConsulta(){
		StringBuilder descricao = new StringBuilder();
		if(toggleFiltro == -1){
			if(dataInicial != null){
				descricao.append(" |Data: ").append(DateUtil.dataToString(dataInicial, "dd/MM/yyyy"));
			}
			if (tipoSessaoCombo != null) {
				descricao.append(" |Tipo de Sessão: ").append(obterDescricaoTipoSessao());
			}
			if (salaCombo != null) {
				descricao.append(" |Sala: ").append(obterDescricaoSala());
			}
			if(turnoCombo != null){
				descricao.append(" |Turno: ").append(turnoCombo.getDescricao());
			}
			if(mpaProtocoloAssistencial != null){
				descricao.append(" |Protocolos: ").append(mpaProtocoloAssistencial.getTitulo());
			}
			if(localAtendimento != null){
				descricao.append(" |Acomodação: ").append(localAtendimento.getDescricao());
			}
			if (acomodacaoCombo != null) {
				descricao.append(" |Tipo de Acomodação: ").append(acomodacaoCombo.getDescricao());
			}
			}
		return descricao.toString();
	}

	private String obterDescricaoSala() {
		if(listaSalas != null && !listaSalas.isEmpty()){
			for (MptSalas itemSala : listaSalas) {
				if(itemSala.getSeq() == salaCombo){
					return itemSala.getDescricao();
				}
			}
		}
		return "";
	}
	private String obterDescricaoTipoSessao() {
		if(listaTipoSessao != null && !listaTipoSessao.isEmpty()){
			for (MptTipoSessao itemTipoSessao : listaTipoSessao) {
				if(itemTipoSessao.getSeq() == tipoSessaoCombo){
					return itemTipoSessao.getDescricao();
				}
			}
		}
		return "";
	}
	
	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}
	public void setProcedimentoTerapeuticoFacade(IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}
	public MpaProtocoloAssistencial getMpaProtocoloAssistencial() {
		return mpaProtocoloAssistencial;
	}
	public void setMpaProtocoloAssistencial(MpaProtocoloAssistencial mpaProtocoloAssistencial) {
		this.mpaProtocoloAssistencial = mpaProtocoloAssistencial;
	}
	public MptLocalAtendimento getLocalAtendimento() {
		return localAtendimento;
	}
	public void setLocalAtendimento(MptLocalAtendimento localAtendimento) {
		this.localAtendimento = localAtendimento;
	}
	public Short getTipoSessaoCombo() {
		return tipoSessaoCombo;
	}
	public void setTipoSessaoCombo(Short tipoSessaoCombo) {
		this.tipoSessaoCombo = tipoSessaoCombo;
	}
	public List<MptTipoSessao> getListaTipoSessao() {
		return listaTipoSessao;
	}
	public void setListaTipoSessao(List<MptTipoSessao> listaTipoSessao) {
		this.listaTipoSessao = listaTipoSessao;
	}
	public List<MptSalas> getListaSalas() {
		return listaSalas;
	}
	public void setListaSalas(List<MptSalas> listaSalas) {
		this.listaSalas = listaSalas;
	}
	public DominioTurno getTurnoCombo() {
		return turnoCombo;
	}
	public void setTurnoCombo(DominioTurno turnoCombo) {
		this.turnoCombo = turnoCombo;
	}
	public DominioTipoAcomodacao getAcomodacaoCombo() {
		return acomodacaoCombo;
	}
	public void setAcomodacaoCombo(DominioTipoAcomodacao acomodacaoCombo) {
		this.acomodacaoCombo = acomodacaoCombo;
	}
	public Date getDataInicial() {
		return dataInicial;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Short getSalaCombo() {
		return salaCombo;
	}
	public void setSalaCombo(Short salaCombo) {
		this.salaCombo = salaCombo;
	}
	public RapServidores getServidorLogado() {
		return servidorLogado;
	}
	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
	public ListaPacienteAgendadoVO getParametroSelecionado() {
		return parametroSelecionado;
	}
	public void setParametroSelecionado(ListaPacienteAgendadoVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}
	public List<ListaPacienteAgendadoVO> getListaPacientes() {
		return listaPacientes;
	}
	public void setListaPacientes(List<ListaPacienteAgendadoVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}
	public Long getCountPaciente() {
		return countPaciente;
	}
	public void setCountPaciente(Long countPaciente) {
		this.countPaciente = countPaciente;
	}
	public Integer getToggleFiltro() {
		return toggleFiltro;
	}
	public void setToggleFiltro(Integer toggleFiltro) {
		this.toggleFiltro = toggleFiltro;
	}
	public boolean isExibirColunaApac() {
		return exibirColunaApac;
	}
	public void setExibirColunaApac(boolean exibirColunaApac) {
		this.exibirColunaApac = exibirColunaApac;
	}
	public ListaPacienteAgendadoVO getSelecionado() {
		return selecionado;
	}
	public void setSelecionado(ListaPacienteAgendadoVO selecionado) {
		this.selecionado = selecionado;
	}
	public List<AgendamentosPacienteVO> getListaAgendamentosPaciente() {
		return listaAgendamentosPaciente;
	}
	public void setListaAgendamentosPaciente(List<AgendamentosPacienteVO> listaAgendamentosPaciente) {
		this.listaAgendamentosPaciente = listaAgendamentosPaciente;
	}
	public AgendamentosPacienteVO getSelecionadoAgendamentoPaciente() {
		return selecionadoAgendamentoPaciente;
	}
	public void setSelecionadoAgendamentoPaciente(AgendamentosPacienteVO selecionadoAgendamentoPaciente) {
		this.selecionadoAgendamentoPaciente = selecionadoAgendamentoPaciente;
	}
	public List<ExtratoSessaoVO> getListaExtratoSessao() {
		return listaExtratoSessao;
	}
	public void setListaExtratoSessao(List<ExtratoSessaoVO> listaExtratoSessao) {
		this.listaExtratoSessao = listaExtratoSessao;
	}
	public ListaPacienteAgendadoVO getDadoRelatorio() {
		return dadoRelatorio;
	}
	public void setDadoRelatorio(ListaPacienteAgendadoVO dadoRelatorio) {
		this.dadoRelatorio = dadoRelatorio;
	}
	public AipPacientes getPacienteRelatorio() {
		return pacienteRelatorio;
	}
	public void setPacienteRelatorio(AipPacientes pacienteRelatorio) {
		this.pacienteRelatorio = pacienteRelatorio;
	}
	public ConsultaDadosAPACVO getConsultaDadosAPAC() {
		return consultaDadosAPAC;
	}
	public void setConsultaDadosAPAC(ConsultaDadosAPACVO consultaDadosAPAC) {
		this.consultaDadosAPAC = consultaDadosAPAC;
	}
	public boolean isBtAPAC() {
		return btAPAC;
	}
	public void setBtAPAC(boolean btAPAC) {
		this.btAPAC = btAPAC;
	}
	public List<ProcedimentosAPACVO> getListaProcedimentosApac() {
		return listaProcedimentosApac;
	}
	public void setListaProcedimentosApac(List<ProcedimentosAPACVO> listaProcedimentosApac) {
		this.listaProcedimentosApac = listaProcedimentosApac;
	}
	public Integer getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}
	public Tab getSelecionarAbas() {
		return selecionarAbas;
	}
	public void setSelecionarAbas(Tab selecionarAbas) {
		this.selecionarAbas = selecionarAbas;
	}
	public ExtratoSessaoVO getSelecionadoSessaoModal() {
		return selecionadoSessaoModal;
	}
	public void setSelecionadoSessaoModal(ExtratoSessaoVO selecionadoSessaoModal) {
		this.selecionadoSessaoModal = selecionadoSessaoModal;
	}
	public boolean isExibirColunaCF() {
		return exibirColunaCF;
	}
	public void setExibirColunaCF(boolean exibirColunaCF) {
		this.exibirColunaCF = exibirColunaCF;
	}
	public boolean isExibirColunaLM() {
		return exibirColunaLM;
	}
	public void setExibirColunaLM(boolean exibirColunaLM) {
		this.exibirColunaLM = exibirColunaLM;
	}
	@Override
	protected Collection<DadosRelatorioControleFrequenciaVO> recuperarColecao()throws ApplicationBusinessException {
		return listDadosRelatorioControleFrequencia;
	}
	@Override
	protected String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/procedimentoterapeutico/report/controleFrequenciaQuimio.jasper";
	}
	@Override
    public Map<String, Object> recuperarParametros() {
	    Map<String, Object> params = new HashMap<String, Object>();
	  	try {
        	params.put("nomeEstabelecimento",this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto().toUpperCase());
	  	 	params.put("codigo",this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_CNES_HCPA).getVlrNumerico());
        	params.put("cgc",this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_CGC).getVlrTexto());
          	params.put("imagem", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL2_RELATIVO_JEE7));
        	params.put("nomeEstabelecimentoRodape",this.parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto());
        	params.put("codigoBarras", dadoRelatorio.getProntuario());
        } catch (ApplicationBusinessException e) {
            apresentarExcecaoNegocio(e);
        }
	  	return params;
	}
	private static String obtemDataPorExtenso(Date valorData) {
		 StringBuilder data = new StringBuilder();
	     Calendar calendario = Calendar.getInstance();
	     calendario.setTime(valorData);
	     Locale localizacao = new Locale("pt","BR");
	     String valorCalend = calendario.getDisplayName(Calendar.MONTH, Calendar.LONG, localizacao).toUpperCase()+"/";
	     String valorCalendAno = Integer.toString(calendario.get(Calendar.YEAR));
	     data.append(valorCalend).append(valorCalendAno);
	     return data.toString();
	}
	private static String obtemMesAnoPorExtenso() {
		 StringBuilder data = new StringBuilder();
	     Calendar calendario = Calendar.getInstance();
	     Locale localizacao = new Locale("pt","BR");
	     String calendariMes = " de "+ calendario.getDisplayName(Calendar.MONTH, Calendar.LONG, localizacao).toUpperCase();
	     String calendarioAno = " de "+Integer.toString(calendario.get(Calendar.YEAR));
	     data.append(calendariMes).append(calendarioAno);
	     return data.toString();
	}
	private String obtemMesAno(String mes, String ano) {
		   Integer mesJava = Integer.valueOf(mes) - 1;
		   Calendar calendario = Calendar.getInstance();
		   calendario.set(Calendar.MONTH, mesJava);
		   calendario.set(Calendar.YEAR, Integer.valueOf(ano));
		   Locale localizacao = new Locale("pt","BR");
		   StringBuilder data = new StringBuilder();
		   String mesC = calendario.getDisplayName(Calendar.MONTH, Calendar.LONG, localizacao).toUpperCase()+"/";
		   int anoC =  calendario.get(Calendar.YEAR);
		   data.append(mesC).append(anoC);
		   return data.toString();
	}
	private String obtemMesAno(Date valorData) {
		   Calendar calendario = Calendar.getInstance();
		   calendario.setTime(valorData);
		   Locale localizacao = new Locale("pt","BR");
		   StringBuilder data = new StringBuilder();
		   String calMes = 	   calendario.getDisplayName(Calendar.MONTH, Calendar.LONG, localizacao).toUpperCase()+"/";
		   int calAno = calendario.get(Calendar.YEAR);
		   data.append(calMes).append(calAno);
		   return data.toString();
	}
	private String obtemMesAnoNumerico(Date valorData) {
		   Calendar calendario = Calendar.getInstance();
		   calendario.setTime(valorData);
		   StringBuilder data = new StringBuilder();
		   String calMes = 	   calendario.get(Calendar.MONTH)+"/";
		   int calAno = calendario.get(Calendar.YEAR);
		   data.append(calMes).append(calAno);
		   return data.toString();
	}
	private String formataDataTratamento(Date valorDataInicioFimTrata) {
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		return formato.format(valorDataInicioFimTrata);
	}
	public void directPrint() {
        try {
                DocumentoJasper documento = gerarDocumento();
                this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
                apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
        } catch (SistemaImpressaoException e) {
                apresentarExcecaoNegocio(e);
        } catch (Exception e) {
                 apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
        }
	}
	public String imprimirRelatorio() {
        try {
              directPrint();
        } catch (Exception e) {
              apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
        }
        return null;
	}
	public void geraRelatorioManual() {
		registrarControleFrequencia(obtemMesAnoNumerico(dataInicial),DominioControleFrequenciaSituacao.CIS);
		criaListaDadosRelatorioFrequenciaManual(pacienteRelatorio,consultaDadosAPAC,listaProcedimentosApac);
		imprimirRelatorio();
		pesquisar();
	}
	public void geraRelatorioApac() {
		registrarControleFrequencia(obtemMesAnoNumerico(dataInicial),DominioControleFrequenciaSituacao.CIA);
		criaListaDadosRelatorioFrequenciaApac(pacienteRelatorio,consultaDadosAPAC,listaProcedimentosApac);
		imprimirRelatorio();
		pesquisar();
	}
	private void registrarControleFrequencia(String dataAgendado, DominioControleFrequenciaSituacao dominioControleFrequencia) {
		Long atmNumero = null;
		Byte capSeq = null;
		if (dominioControleFrequencia == DominioControleFrequenciaSituacao.CIA && consultaDadosAPAC != null){
			atmNumero = consultaDadosAPAC.getAtmNumero();
			capSeq = consultaDadosAPAC.getCapSeq();
		}
		procedimentoTerapeuticoFacade.registrarControleFrequenciaRelatorio(dataAgendado,dadoRelatorio.getCodPaciente(), dadoRelatorio.getCodigo(), atmNumero, 
				capSeq, dominioControleFrequencia, dadoRelatorio.getData(), servidorLogado);
	}
	public boolean showHintServidor(String usuarioServidor, Integer tamanhoMaximo) {
		String retorno = usuarioServidor;
		if (retorno.length() > tamanhoMaximo) {
			return true;
		}
		return false;
	}
	public boolean isPauseEmAtendimento() {
		return pauseEmAtendimento;
	}
	public void setPauseEmAtendimento(boolean pauseEmAtendimento) {
		this.pauseEmAtendimento = pauseEmAtendimento;
	}
	public boolean isPauseAguardAtendimento() {
		return pauseAguardAtendimento;
	}
	public void setPauseAguardAtendimento(boolean pauseAguardAtendimento) {
		this.pauseAguardAtendimento = pauseAguardAtendimento;
	}
	public String getCameFrom() {
		return cameFrom;
	}
	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	public boolean isFromBack() {
		return fromBack;
	}
	public void setFromBack(boolean fromBack) {
		this.fromBack = fromBack;
	}
	public boolean isExibirAbaAcolhimento() {
		return exibirAbaAcolhimento;
	}
	public void setExibirAbaAcolhimento(boolean exibirAbaAcolhimento) {
		this.exibirAbaAcolhimento = exibirAbaAcolhimento;
	}
	public boolean apresentarOpcaoRegistroAusencia(){
		Boolean podeRetornar = false;
		if (selecionado != null){
			if (securityController.usuarioTemPermissao("registrarAusencia", "executar")){
				podeRetornar = procedimentoTerapeuticoFacade.verificarPacienteAtrasadoSessao(selecionado.getHorarioSessaoSeq(), selecionado.getCodPaciente());
			}
		}
		return podeRetornar;
	}
	public void registrarAusencia(){
		procedimentoTerapeuticoFacade.registrarAusenciaPaciente(selecionado.getCodigo());
		pesquisar();
	}
	public AipPacientes getPacienteAgendamentoModal() {
		return pacienteAgendamentoModal;
	}
	public void setPacienteAgendamentoModal(AipPacientes pacienteAgendamentoModal) {
		this.pacienteAgendamentoModal = pacienteAgendamentoModal;
	}
} 