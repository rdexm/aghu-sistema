package br.gov.mec.aghu.controlepaciente.action;

import java.io.IOException;
import java.text.Collator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.dominio.DominioPeriodoRegistroControlePaciente;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.emergencia.action.ListaPacientesEmergenciaPaginatorController;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioRegistrosControlesPacienteController;


@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
public class RegistrosPacienteController extends ActionController {

	private static final String PRESCRICAOENFERMAGEM_LISTA_PACIENTES_ENFERMAGEM = "/pages/prescricaoenfermagem/listaPacientesEnfermagem.xhtml";
	private static final String PAGE_PESQUISAR_PACIENTE_AGENDADO = "ambulatorio-pesquisarPacientesAgendados";
	private static final String PAGE_ATENDER_PACIENTE_AGENDADO = "ambulatorio-atenderPacientesAgendados";

	private static final Log LOG = LogFactory.getLog(RegistrosPacienteController.class);

	private static final String MANTER_REGISTROS = "controlepaciente-manterRegistros";
	private static final String RELATORIO_CONTROLES_PACIENTE = "paciente-relatorioControlesPaciente";
	private static final String LISTA_PACIENTES_EMERGENCIA = "/pages/emergencia/listaPacientesEmergencia.xhtml";
	private static final String LISTA_PACIENTES_PRESCRICAO_MEDICA = "/pages/prescricaomedica/pesquisarListaPacientesInternados.xhtml";
	private static final String LISTA_PACIENTES_CONTROLE_PACIENTE = "/pages/controlepaciente/listarPacientesInternados.xhtml";
	private static final String LISTA_PACIENTES_CIRURGIAS = "/pages/blococirurgico/listarCirurgias.xhtml";
	private static final String LISTA_PACIENTES_AMBULATORIO = "/pages/ambulatorio/pacientesagendados/pesquisarPacientesAgendados.xhtml"; 
	private static final String TAB_MONITORIZACAO = "0";
	private static final String TAB_BALANCO_HIDRICO = "1";
	private static final String PAGE_ATENDER_PACIENTES_AGENDADOS = "ambulatorio-redirecionarAtenderPacientesAgendados";
	private static final String REDIRECIONA_PAGE_ATENDER_PACIENTES_AGENDADOS = "/pages/ambulatorio/pacientesagendados/atenderPacientesAgendados.xhtml";
	
	private static final long serialVersionUID = 4225540328812287685L;

	/**
	 * Comparator null safe e locale-sensitive String.
	 */
	@SuppressWarnings("unchecked")
	private static final Comparator PT_BR_COMPARATOR = new Comparator() {
		@Override
		public int compare(Object o1, Object o2) {

			final Collator collator = Collator.getInstance(new Locale("pt","BR"));
			collator.setStrength(Collator.PRIMARY);

			return ((Comparable) o1).compareTo(o2);
		}
	};
	
	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	
	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private RelatorioRegistrosControlesPacienteController relatorioRegistrosControlesPacienteController;
	
	@Inject
	private ListaPacientesEmergenciaPaginatorController listaPacientesEmergenciaController;
	

	private AipPacientes paciente;
	private AghAtendimentos atendimento;
	private AinLeitos leito;
	private DominioPeriodoRegistroControlePaciente periodo;
	private Date dataInicial;
	private Date dataFinal;

	private AghAtendimentos atendimentoPesquisa;

	//params
	private Integer codigoPaciente;
	private String voltarPara;
	private Long trgSeq;
	private Short unfSeq;
	
	private boolean somenteLeitura = false;

	//tabela de monitorização
	private List<EcpItemControle> listaItensControleMn = new ArrayList<EcpItemControle>();
	private List<EcpItemControle> listaItensControleBh = new ArrayList<EcpItemControle>();
	private List<RegistroControlePacienteVO> listaRegistrosControleMn = new ArrayList<RegistroControlePacienteVO>();
	private List<RegistroControlePacienteVO> listaRegistrosControleBh = new ArrayList<RegistroControlePacienteVO>();
	private boolean apresentaLista;
	private boolean apresentaListaBh;
	private boolean apresentaNovo;
	private Long seqHorarioControle;
	private Comparator<RegistroControlePacienteVO> currentComparator;
	private String currentSortProperty;
	
	private boolean renderMonitorizacao = true;
	private boolean renderBalancoHidrico = false;
	private boolean exibeBH = true;
	private boolean habilitaImprimir = true;
	
	private Integer atdSeq;
	private Long horarioSeq;
	private static final Integer TAM_MAXIMO_DESCRICAO = 13;
	private static final Integer TAM_MAXIMO_ANOTACAO = 42;
	
	private DominioPeriodoRegistroControlePaciente periodoDefault = DominioPeriodoRegistroControlePaciente.HORAS24;
	
	private String labelZona;
	private VAacSiglaUnfSalaVO zona;
	
	private Byte sala;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * Aba corrente
	 */
	private String selectedTab;
	
	private List<Double> somatoriosMedicoesBh;
	
	private Integer cid;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	protected void inicializarParametros() {
		if (getRequestParameter("pac_codigo") != null) {
			this.codigoPaciente = Integer.valueOf(getRequestParameter("pac_codigo"));
		}
		if (getRequestParameter("atd_seq") != null) {
			this.atdSeq = Integer.valueOf(getRequestParameter("atd_seq"));
		}
		if (getRequestParameter("pacienteSelecionado") != null) {
			   setCodigoPaciente(Integer.valueOf(getRequestParameter("pacienteSelecionado")));
		}
		
		if (getRequestParameter("trgSeq") != null) {
			   setTrgSeq(Long.valueOf(getRequestParameter("trgSeq")));
		}
		
		if (getRequestParameter("voltarPara") != null) {
			setVoltarPara(getRequestParameter("voltarPara"));
		}
		
		if (getRequestParameter("unfSeq") != null) {
			   setUnfSeq(Short.valueOf(getRequestParameter("unfSeq")));
			   if (this.getVoltarPara().equalsIgnoreCase(PAGE_ATENDER_PACIENTE_AGENDADO)){
				   setSala(Byte.valueOf(getRequestParameter("sala")));
				   AghUnidadesFuncionais unidade = aghuFacade.obterUnidadeFuncional(getUnfSeq());
				   zona = new VAacSiglaUnfSalaVO(unidade.getSeq(), unidade.getSigla(), getSala());
				   zona.setDescricao(unidade.getDescricao());   
			   }  
		}
		
		
		if(getRequestParameter("param_cid") != null) {		
			setCid(Integer.valueOf(getRequestParameter("param_cid")));
		}
		
		setaPeriodoDefault();
	}

	private void setaPeriodoDefault() {
		try {
			Integer param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PERIODO_DEFAULT_VISUALIZAR_CONTROLES).getVlrNumerico().intValue();
			switch (param) {
			case 1:
				periodoDefault = DominioPeriodoRegistroControlePaciente.HORA1;
				break;
			case 6:
				periodoDefault = DominioPeriodoRegistroControlePaciente.HORAS6;
				break;
			case 12:
				periodoDefault = DominioPeriodoRegistroControlePaciente.HORAS12;
				break;
			case 24:
				periodoDefault = DominioPeriodoRegistroControlePaciente.HORAS24;
				break;
			case 48:
				periodoDefault = DominioPeriodoRegistroControlePaciente.HORAS48;
				break;
			case 168:
				periodoDefault = DominioPeriodoRegistroControlePaciente.DIAS7;
				break;
			case 360:
				periodoDefault = DominioPeriodoRegistroControlePaciente.DIAS15;
				break;
			default:
				periodoDefault = null;
				break;
			}
		} catch (Exception e) {
			periodoDefault = DominioPeriodoRegistroControlePaciente.HORAS24;
		}
	}
	
	
	public void carregar() {	
		inicializarParametros();
		if (selectedTab == null){
			selectedTab = TAB_MONITORIZACAO;
		}
		
		if (trgSeq != null && this.codigoPaciente != null && this.getVoltarPara() != null) {
			AipPacientes pac = pacienteFacade.obterPacientePorCodigo(this.codigoPaciente);
			pesquisarPacientes(pac.getProntuario().toString());
			setPaciente(pac);
			setPeriodo(periodoDefault);
			ajustarDatasConformePeriodo();
			setSomenteLeitura(true);
			setExibeBH(false);
			setHabilitaImprimir(false);
			pesquisar();
		} else if (this.codigoPaciente != null) {
			AipPacientes pac = pacienteFacade.obterPacientePorCodigo(this.codigoPaciente);
			if (pac == null) {
				LOG.info("RegistrosPacienteController.carregar(): Paciente nulo ou não encontrado.");
			} else {
				if(atdSeq != null){
					atendimentoPesquisa =  this.aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
				} else {
					pesquisarPacientes(pac.getProntuario().toString());
				}
				if (atendimentoPesquisa == null) {
					LOG.warn("RegistrosPacienteController.carregar(): Não existe atendimento vigente para o paciente codigo = [" + codigoPaciente + "].");
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NENHUM_ATENDIMENTO_VIGENTE_PACIENTE", pac.getNome());
					return ;
				} else {
					selecionouPaciente();
					selecionouLeito();
					setPeriodo(periodoDefault);
					ajustarDatasConformePeriodo();
					setSomenteLeitura(this.getVoltarPara() != null);
					pesquisar();
				}
			}
		} else  {
			setPeriodo(periodoDefault);
			ajustarDatasConformePeriodo();
			setSomenteLeitura(false);
		}
	
		if (this.isFromAmbulatorio()) {
			this.configurarParaAmbulatorio();
		}
	}
	
	public Double obterSomatorioVolume(DominioTipoGrupoControle tipoGrupoControle) {
		Double somatorio = new Double(0);
		Double valorDouble;
		if (listaRegistrosControleBh != null && listaItensControleBh != null && apresentaListaBh) {
			for (RegistroControlePacienteVO controlePaciente : listaRegistrosControleBh) {
				int i = 0;
				for (EcpItemControle itemControle : listaItensControleBh) {
					valorDouble = 0.0;
					String valor = controlePaciente.getValor()[i];
					if (itemControle.getGrupo().getTipo().equals(tipoGrupoControle) && isNumero(valor)) {
						valorDouble = Double.valueOf(converteValorStringDouble(valor));
						somatorio += valorDouble;
					}
					i++;
				}
			}
		}
		return somatorio;
	}
	
	private Boolean isNumero(String valorCampoTela) {
        if (StringUtils.isNotBlank(valorCampoTela) && NumberUtils.isNumber(converteValorStringDouble(valorCampoTela))) {
        	return Boolean.TRUE;
        }
    return Boolean.FALSE;
	}
	
	private String converteValorStringDouble(String valorCampoTela) {
		valorCampoTela = valorCampoTela.replace(".", StringUtils.EMPTY);
		valorCampoTela = valorCampoTela.replace(',', '.');
		return valorCampoTela;
	}
	
	public Double obterSomatorioTotalVolumes() {
		return obterSomatorioVolume(DominioTipoGrupoControle.CA) - obterSomatorioVolume(DominioTipoGrupoControle.CE);
	}

	public String apresentaSomatorioBalancoAdministrado() {
		return AghuNumberFormat.formatarNumeroMoeda(obterSomatorioVolume(DominioTipoGrupoControle.CA));
	}

	public String apresentaSomatorioBalancoEliminado() {
		return AghuNumberFormat.formatarNumeroMoeda(obterSomatorioVolume(DominioTipoGrupoControle.CE));
	}

	public String apresentaResultadoBalancoHidrico() {
		return AghuNumberFormat.formatarNumeroMoeda(obterSomatorioTotalVolumes());
	}
	
	/**
	 * Método que configura a tela, quando vindo de ambulatório 
	 * #42797
	 */
	private void configurarParaAmbulatorio() {
		try {
			this.setLabelZona(this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void selecionouLeito() {
		this.setAtendimento(atendimentoPesquisa);
		this.setCodigoPaciente(atendimentoPesquisa.getPaciente().getCodigo());
		this.setPaciente(getAtendimento().getPaciente());
	}

	public void selecionouPaciente() {
		this.setAtendimento(atendimentoPesquisa);
		this.setCodigoPaciente(atendimentoPesquisa.getPaciente().getCodigo());
		this.setLeito(getAtendimento().getLeito());
	}

	public void ajustarDatasConformePeriodo() {
		if (this.periodo == null) {
			dataFinal = null;
			dataInicial = null;
		} else {
			dataFinal = Calendar.getInstance().getTime();
			dataInicial = new Date(dataFinal.getTime() - periodo.getValorEmMilisegundos());
		}
	}

	public void limparDadosPacienteLeito() {
		setAtendimento(null);
		setPaciente(null);
		setLeito(null);
	}

	/**
	 * Retorna os valores da tela ao conteúdo do banco de dados
	 */
	public void limpar() {
		if (!isFromAmbulatorio()){
		limparDadosPacienteLeito();
		setSomenteLeitura(false);
		}
		
		setPeriodo(periodoDefault);
		ajustarDatasConformePeriodo();
		selectedTab = TAB_MONITORIZACAO;
		this.setApresentaLista(false);
		this.setApresentaListaBh(false);
		this.setApresentaNovo(false);
		listaItensControleMn = new ArrayList<EcpItemControle>();
		listaItensControleBh = new ArrayList<EcpItemControle>();
		listaRegistrosControleMn = new ArrayList<RegistroControlePacienteVO>();
		listaRegistrosControleBh = new ArrayList<RegistroControlePacienteVO>();
		this.atendimentoPesquisa = null;
	}
	
	public String imprimir() {
		try {
			if (!pacienteInformado()) {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PACIENTE_DEVE_SER_INFORMADO");
				return null;
			}
			controlePacienteFacade.validarDatasPesquisaRegistrosPaciente(dataInicial, dataFinal);
			
			relatorioRegistrosControlesPacienteController.setAtdSeq(atendimento.getSeq());
			relatorioRegistrosControlesPacienteController.setDataInicialImpressaoInMillis(dataInicial.getTime());
			relatorioRegistrosControlesPacienteController.setDataFinalImpressaoInMillis(dataFinal.getTime());
			relatorioRegistrosControlesPacienteController.setFromVisualizarControles(Boolean.TRUE);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return RELATORIO_CONTROLES_PACIENTE;
	}
	
	private Boolean pacienteInformado() {
		return atendimento != null || paciente != null;
	}
	
	public void definirPeriodoNulo() {
		setPeriodo(null);
	}
	
	public void pesquisar() {
		try {
			if (!pacienteInformado()) {
				if (atendimentoPesquisa != null) {
					this.setAtendimento(atendimentoPesquisa);
					this.setPaciente(atendimentoPesquisa.getPaciente());
					this.setLeito(atendimentoPesquisa.getLeito());
				} else {
					apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PACIENTE_DEVE_SER_INFORMADO");
					return ;
				}
			}
			controlePacienteFacade.validarDatasPesquisaRegistrosPaciente(dataInicial, dataFinal);
			// apresentar o botão novo mesmo que não tenham registros para serem apresentados
			this.setApresentaNovo(true);
			//se a aba ativa for Balanço Hidrico, desabilita-la
			if (this.isRenderBalancoHidrico()) {
				this.setRenderBalancoHidrico(false);
				this.setApresentaListaBh(false);
			}
			// verificar qual aba o usuario quer ver se Monitorizacao ou Balanco Hidrico
			if (TAB_MONITORIZACAO.equals(selectedTab)){
				buscaMonitorizacoes();
			} else if (TAB_BALANCO_HIDRICO.equals(selectedTab)){
				buscaBalancosHidricos(); 
			}
			if ((TAB_MONITORIZACAO.equals(selectedTab) && this.listaRegistrosControleMn != null && this.listaRegistrosControleMn.size() > 10) ||
				TAB_BALANCO_HIDRICO.equals(selectedTab) && this.listaRegistrosControleBh != null && this.listaRegistrosControleBh.size() > 10) {
				super.openDialog("pacienteDlgWG");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void tabChange(TabChangeEvent event){
		// verificar qual aba o usuario quer ver se Monitorizacao ou Balanco Hidrico
		if (TAB_MONITORIZACAO.equals(selectedTab)) {
			buscaMonitorizacoes();
		} else if(TAB_BALANCO_HIDRICO.equals(selectedTab)){
			buscaBalancosHidricos(); 
		}
	}
	
	public void buscaMonitorizacoes(){		
		listaItensControleMn = cadastrosBasicosControlePacienteFacade
				.buscarItensControlePorPacientePeriodo(paciente, dataInicial,
						dataFinal, null, DominioTipoGrupoControle.MN);
		if (listaItensControleMn.isEmpty()) {
			if (!this.listaRegistrosControleMn.isEmpty()) {
				this.listaRegistrosControleMn = new ArrayList<RegistroControlePacienteVO>();
			}
			this.setApresentaLista(false);				
		}
		try {
			this.listaRegistrosControleMn = controlePacienteFacade
					.pesquisarRegistrosPaciente((atendimento == null ? null
							: atendimento.getSeq()), paciente,
							(leito == null ? null : leito.getLeitoID()),
							dataInicial, dataFinal, listaItensControleMn, null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}			
		this.renderMonitorizacao = true;
		this.renderBalancoHidrico = false;
		selectedTab = TAB_MONITORIZACAO;
		if (this.listaRegistrosControleMn.isEmpty()) {
			this.setApresentaLista(false);				
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUM_CONTROLE_ENCONTRADO");
			return ;
		}
		this.setApresentaLista(true);
		this.setApresentaListaBh(false);
	}		
		
	public void buscaBalancosHidricos(){
		listaItensControleBh = cadastrosBasicosControlePacienteFacade
				.buscarItensControlePorPacientePeriodo(paciente, dataInicial,
						dataFinal, null, DominioTipoGrupoControle.CA,
						DominioTipoGrupoControle.CE);
		if (listaItensControleBh.isEmpty()) {
			if (!this.listaRegistrosControleBh.isEmpty()) {
				this.listaRegistrosControleBh = new ArrayList<RegistroControlePacienteVO>();
			}
			this.setApresentaListaBh(false);				
			
		}
		try {
			this.listaRegistrosControleBh = controlePacienteFacade.pesquisarRegistrosPaciente(
					(atendimento == null ? null : atendimento.getSeq()),
					paciente,
					(leito == null ? null : leito.getLeitoID()),
					dataInicial, dataFinal, listaItensControleBh, null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}			
		this.renderBalancoHidrico = true;
		this.renderMonitorizacao = false;
		selectedTab = TAB_BALANCO_HIDRICO;
		if (this.listaRegistrosControleBh.isEmpty()) {
			this.setApresentaListaBh(false);				
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUM_CONTROLE_ENCONTRADO");
			return ;
		}
		this.setApresentaLista(false);
		this.setApresentaListaBh(true);
		this.somarBalancoHidrico();
	}
	
	private void inicializarSomatorioBalancoHidrico() {

		this.somatoriosMedicoesBh = new ArrayList<Double>();
		// inicializa o array
		if (!this.listaRegistrosControleBh.isEmpty()) {
			for (int index = 0; index < this.listaRegistrosControleBh.get(0)
					.getValor().length; index++) {
				this.somatoriosMedicoesBh.add(index, 0.0);
			}
		}
	}
	
	private void somarBalancoHidrico() {
		this.inicializarSomatorioBalancoHidrico();
		double valorAux = 0.0;
		for (RegistroControlePacienteVO controle : this.listaRegistrosControleBh) {
			for (int index = 0; index < controle.getValor().length; index++) {
				valorAux = this.somatoriosMedicoesBh.get(index);
				try {
					valorAux += (controle.getValor()[index] == null) ? 0.0
							: Double.valueOf(controle.getValor()[index]
									.replace(",", "."));
				} catch (NumberFormatException nFormat) {
					// caso o dominio seja Sim/Não
					valorAux += 0;
				}
				this.somatoriosMedicoesBh.set(index, valorAux);
			}
		}
	}
		
	public String obterSomatorioTaxaBalancoHidrico(int index) {
		if (somatoriosMedicoesBh != null
				&& index <= somatoriosMedicoesBh.size()) {
			if (somatoriosMedicoesBh.get(index) == 0) {
				return "";
			}
			Locale locale = new Locale("pt", "BR");
			NumberFormat format = NumberFormat.getInstance(locale);
			format.setMaximumFractionDigits(2);
			return format.format(somatoriosMedicoesBh.get(index)).replace(".","");
		}
		return "0,0";
	}
	
	/**
	 * Método da suggestion box para pesquisa de atendimentos
	 */
	public List<AinLeitos> pesquisarLeitos(String parametro) {
		String paramString = (String) parametro;
		paramString = StringUtils.trimToEmpty(paramString);
		paramString = StringUtils.upperCase(paramString);
		List<AinLeitos> listResult = new ArrayList<AinLeitos>();
		try {
			atendimentoPesquisa = controlePacienteFacade
					.pesquisarAtendimentoVigentePorLeito(paramString);
		} catch (ApplicationBusinessException e) {
			atendimentoPesquisa = null;
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		if (atendimentoPesquisa != null) {
			listResult.add(atendimentoPesquisa.getLeito());
			this.selecionouLeito();
		}
		return listResult;
	}
	
	/**
	 * Método da suggestion box para pesquisa de pacientes
	 */
	public List<AipPacientes> pesquisarPacientes(String parametro) {
		List<AipPacientes> listResult = new ArrayList<AipPacientes>();
		String paramString = (String) parametro;
		paramString = StringUtils.trimToEmpty(paramString);
		if (NumberUtils.isNumber(paramString)) {
			Integer numeroProntuario = Integer.valueOf(paramString);
			try {
				atendimentoPesquisa = controlePacienteFacade
						.pesquisarAtendimentoVigentePorProntuarioPaciente(numeroProntuario);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
			if (atendimentoPesquisa != null) {
				listResult.add(atendimentoPesquisa.getPaciente());
				this.selecionouPaciente();
			} else if (trgSeq != null && paciente != null) {
				listResult.add(paciente);
			}
		}
		return listResult;
	}

	public void excluir() {
		try {
			controlePacienteFacade.excluirRegistroControlePaciente(seqHorarioControle);
			this.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		//forçar a aba de monitorização aparecer
		selectedTab = TAB_MONITORIZACAO;
		this.setRenderMonitorizacao(true);
	}

	public String voltar() {
		limpar();
		String returnValue = "listarPacientesInternados";
		if (this.getVoltarPara() != null) {

			if (this.getVoltarPara().equalsIgnoreCase(
					"listarPacientesEnfermagem")) {
				returnValue = PRESCRICAOENFERMAGEM_LISTA_PACIENTES_ENFERMAGEM;
			} else {
				returnValue = this.getVoltarPara();
			}
		}
		return returnValue;
	}
	
	private void redirecionarPaginaPorAjax(String caminhoPagina){
		try{
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext extContext = ctx.getExternalContext();
			String url = extContext.encodeActionURL(ctx.getApplication().getViewHandler().getActionURL(ctx, caminhoPagina)); 
			extContext.redirect(url);
		}
		catch (IOException e) {
			LOG.error(e.getMessage(), e);		
		}
	}	
	
	public void cancelarEdicao(){
		if(this.getVoltarPara().equalsIgnoreCase("prescricaomedica-pesquisarListaPacientesInternados")) {
			limpar();
			this.redirecionarPaginaPorAjax(LISTA_PACIENTES_PRESCRICAO_MEDICA);
		} else if (this.getVoltarPara().equalsIgnoreCase("prescricaoenfermagem-listaPacientesEnfermagem")){	
				limpar();
				this.redirecionarPaginaPorAjax(PRESCRICAOENFERMAGEM_LISTA_PACIENTES_ENFERMAGEM);
				
				} else if(this.getVoltarPara().equalsIgnoreCase("controlepaciente-listarPacientesInternados")){
						limpar();
						this.redirecionarPaginaPorAjax(LISTA_PACIENTES_CONTROLE_PACIENTE);
						
						}else if(this.getVoltarPara().equalsIgnoreCase("blococirurgico-listaCirurgias")){
							  limpar();
							  this.redirecionarPaginaPorAjax(LISTA_PACIENTES_CIRURGIAS);
							  
							  } else if(this.getVoltarPara().equalsIgnoreCase("ambulatorio-pesquisarPacientesAgendados")){
									limpar();
									this.redirecionarPaginaPorAjax(LISTA_PACIENTES_AMBULATORIO);
							  		
							  		} else if (this.getVoltarPara().equalsIgnoreCase(PAGE_ATENDER_PACIENTES_AGENDADOS)){
							  			limpar();
							  			this.redirecionarPaginaPorAjax(REDIRECIONA_PAGE_ATENDER_PACIENTES_AGENDADOS);

							  			} else {
										this.listaPacientesEmergenciaController.setAbaSelecionada(1);
										this.listaPacientesEmergenciaController.pesquisarPacientesAcolhimento();
										this.redirecionarPaginaPorAjax(LISTA_PACIENTES_EMERGENCIA);
									}
		}

	@SuppressWarnings("unchecked")
	public void ordenar(String propriedade) {
		LOG.debug("++ begin ordenar pela propriedade "+ propriedade);
		Comparator comparator = null;
		// se mesma propriedade, reverte a ordem
		if (this.currentComparator != null
				&& this.currentSortProperty.equals(propriedade)) {
			comparator = new ReverseComparator(this.currentComparator);
		} else {
			// cria novo comparator para a propriedade escolhida
			comparator = new BeanComparator(propriedade, new NullComparator(
					PT_BR_COMPARATOR, false));
		}
		Collections.sort(this.listaRegistrosControleBh, comparator);
		Collections.sort(this.listaRegistrosControleMn, comparator);
		// guarda ordenação corrente
		this.currentComparator = comparator;
		this.currentSortProperty = propriedade;
		LOG.debug("++ end ordenar");
	}
	
	public String editarControle() throws ApplicationBusinessException {
		if (!this.isFromAmbulatorio()){
			if (!controlePacienteFacade.validaUnidadeFuncionalInformatizada(this.atendimento, unfSeq)) {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CONTROLE_PACIENTE_NAO_INFORMATIZADO");
				return null;
			} 
		}
		selectedTab = TAB_MONITORIZACAO;
		return MANTER_REGISTROS;
	}

	public String novoControle() throws ApplicationBusinessException {		
		if (!this.isFromAmbulatorio()){
			if (!controlePacienteFacade.validaUnidadeFuncionalInformatizada(this.atendimento, unfSeq)) {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CONTROLE_PACIENTE_NAO_INFORMATIZADO");
				return null;
			} 
		}
		return MANTER_REGISTROS;
	}
	
	/**
	 * Informa se deve ser apresentada a tooltip nos campos de descrição
	 */
	public Boolean apresentaToolTip(Object objParam ){
		try {
			String strParam = (String) objParam;

			if (StringUtils.isNotBlank(strParam)
					&& !CoreUtil.isNumeroInteger(strParam)
					&& strParam.length() > TAM_MAXIMO_DESCRICAO) {
				return true;
			}
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			return false;
		}
		return false;
	}
	
	/**
	 * Utilizado para os campos de descrição cujo tamanho seja maior que a coluna apresentada no dataTable
	 */
	public String mostrarValorFormatado(Object objParam) {
		try {
			String strParam = (String) objParam;
			if (this.apresentaToolTip(objParam)) {
				return strParam.substring(0, TAM_MAXIMO_DESCRICAO) + "...";
			} else {
				return strParam;
			}
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			return null;
		}
	}
	
	public Integer obterTamanhoColunaMn(String descricaoSiglaUnidadeMedida,	int index) {
		// tamanho mínimo padrão para uma coluna qualquer.
		int tamanhoMinimo = 45;
		int auxTamanho = 0;
		if (StringUtils.isNotBlank(descricaoSiglaUnidadeMedida)) {
			// Calculo simples para identificar um tamanho dependendo da
			// quantidade de caracteres de um texto. Para cada caracter, 7.5px,
			// somados mais 5px no total.
			auxTamanho = (int) (descricaoSiglaUnidadeMedida.length() * 7.5) + 5;
		}
		if (auxTamanho < tamanhoMinimo) {
			auxTamanho = tamanhoMinimo;
		}
		// tamanho padrão para o caso de um número tipo ######,## seja
		// informado.
		int tamanhoMinimoCampoNumericoCompleto = 60;
		int quantidadeNumerosQueExibe = 6;
		if (auxTamanho < tamanhoMinimoCampoNumericoCompleto) {
			for (RegistroControlePacienteVO vo : listaRegistrosControleMn) {
				String auxValor = vo.getValor()[index];
				if (StringUtils.isNotBlank(auxValor)
						&& auxValor.length() > quantidadeNumerosQueExibe) {
					auxTamanho = tamanhoMinimoCampoNumericoCompleto;
					break;
				}
			}
		}
		return auxTamanho;
	}
	
	public Integer obterTamanhoColunaBh(String descricaoSiglaUnidadeMedida, int index) {
		// tamanho mínimo padrão para uma coluna qualquer.
		int tamanhoMinimo = 45;
		int auxTamanho = 0;
		if (StringUtils.isNotBlank(descricaoSiglaUnidadeMedida)) {
			// Calculo simples para identificar um tamanho dependendo da
			// quantidade de caracteres de um texto. Para cada caracter, 7.5px,
			// somados mais 5px no total.
			auxTamanho = (int) (descricaoSiglaUnidadeMedida.length() * 7.5) + 5;
		}
		if (auxTamanho < tamanhoMinimo) {
			auxTamanho = tamanhoMinimo;
		}
		// tamanho padrão para o caso de um número tipo ######,## seja informado.
		int tamanhoMinimoCampoNumericoCompleto = 60;
		int quantidadeNumerosQueExibe = 6;
		if (auxTamanho < tamanhoMinimoCampoNumericoCompleto) {
			for (RegistroControlePacienteVO vo : listaRegistrosControleBh) {
				String auxValor = vo.getValor()[index];
				if (StringUtils.isNotBlank(auxValor)
						&& auxValor.length() > quantidadeNumerosQueExibe) {
					auxTamanho = tamanhoMinimoCampoNumericoCompleto;
					break;
				}
			}
		}
		return auxTamanho;
	}
	
	public String mostrarAnotacaoFormatada(Object objParam) {
		try {
			String strParam = (String) objParam;
			if (StringUtils.isNotBlank(strParam) && strParam.length() > TAM_MAXIMO_ANOTACAO) {
				return strParam.substring(0, TAM_MAXIMO_ANOTACAO) + "...";
			} else {
				return strParam;
			}
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			return null;
		}
	}
	public boolean isFromAmbulatorio() {
		return !StringUtils.isEmpty(StringUtils.trim(getVoltarPara()))
				&& (getVoltarPara().equals(PAGE_PESQUISAR_PACIENTE_AGENDADO) || getVoltarPara().equals(PAGE_ATENDER_PACIENTE_AGENDADO) 
						|| getVoltarPara().equals(PAGE_ATENDER_PACIENTES_AGENDADOS));	
		}
	
	public boolean isFromMenu() {
		return StringUtils.isEmpty(StringUtils.trim(getVoltarPara()));
	}
	public boolean isNotFromMenu() {
		return !isFromMenu();
	}
	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	public String getVoltarPara() {
		return voltarPara;
	}
	public void setPeriodo(DominioPeriodoRegistroControlePaciente periodo) {
		this.periodo = periodo;
	}
	public DominioPeriodoRegistroControlePaciente getPeriodo() {
		return periodo;
	}
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	public Date getDataInicial() {
		return dataInicial;
	}
	public long getDataInicialInMillis() {
		return dataInicial == null ? null : dataInicial.getTime();
	}
	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	public Date getDataFinal() {
		return dataFinal;
	}
	public long getDataFinalInMillis() {
		return dataFinal == null ? null : dataFinal.getTime();
	}
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	public AipPacientes getPaciente() {
		return paciente;
	}
	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}
	public AinLeitos getLeito() {
		return leito;
	}
	public void setSomenteLeitura(boolean somenteLeitura) {
		this.somenteLeitura = somenteLeitura;
	}
	public boolean isSomenteLeitura() {
		return somenteLeitura;
	}	
	public boolean isApresentaLista() {
		return apresentaLista;
	}
	public void setApresentaLista(boolean apresentaLista) {
		this.apresentaLista = apresentaLista;
	}
	public Long getSeqHorarioControle() {
		return seqHorarioControle;
	}
	public void setSeqHorarioControle(Long seqHorarioControle) {
		this.seqHorarioControle = seqHorarioControle;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Long getHorarioSeq() {
		return horarioSeq;
	}
	public void setHorarioSeq(Long horarioSeq) {
		this.horarioSeq = horarioSeq;
	}
	public boolean isApresentaNovo() {
		return apresentaNovo;
	}
	public void setApresentaNovo(boolean apresentaNovo) {
		this.apresentaNovo = apresentaNovo;
	}
	public List<EcpItemControle> getListaItensControleMn() {
		return listaItensControleMn;
	}
	public void setListaItensControleMn(List<EcpItemControle> listaItensControleMn) {
		this.listaItensControleMn = listaItensControleMn;
	}
	public List<EcpItemControle> getListaItensControleBh() {
		return listaItensControleBh;
	}
	public void setListaItensControleBh(List<EcpItemControle> listaItensControleBh) {
		this.listaItensControleBh = listaItensControleBh;
	}
	public List<RegistroControlePacienteVO> getListaRegistrosControleMn() {
		return listaRegistrosControleMn;
	}
	public void setListaRegistrosControleMn(
			List<RegistroControlePacienteVO> listaRegistrosControleMn) {
		this.listaRegistrosControleMn = listaRegistrosControleMn;
	}
	public List<RegistroControlePacienteVO> getListaRegistrosControleBh() {
		return listaRegistrosControleBh;
	}
	public void setListaRegistrosControleBh(
			List<RegistroControlePacienteVO> listaRegistrosControleBh) {
		this.listaRegistrosControleBh = listaRegistrosControleBh;
	}
	public boolean isRenderMonitorizacao() {
		return renderMonitorizacao;
	}
	public void setRenderMonitorizacao(boolean renderMonitorizacao) {
		this.renderMonitorizacao = renderMonitorizacao;
	}
	public boolean isRenderBalancoHidrico() {
		return renderBalancoHidrico;
	}
	public void setRenderBalancoHidrico(boolean renderBalancoHidrico) {
		this.renderBalancoHidrico = renderBalancoHidrico;
	}
	public List<Double> getSomatoriosMedicoesBh() {
		return somatoriosMedicoesBh;
	}
	public void setSomatoriosMedicoesBh(List<Double> somatoriosMedicoesBh) {
		this.somatoriosMedicoesBh = somatoriosMedicoesBh;
	}
	public boolean isApresentaListaBh() {
		return apresentaListaBh;
	}
	public void setApresentaListaBh(boolean apresentaListaBh) {
		this.apresentaListaBh = apresentaListaBh;
	}
	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}	
	public Long getTrgSeq() {
		return trgSeq;
	}
	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}
	public boolean isExibeBH() {
		return exibeBH;
	}
	public void setExibeBH(boolean exibeBH) {
		this.exibeBH = exibeBH;
	}
	public boolean isHabilitaImprimir() {
		return habilitaImprimir;
	}
	public void setHabilitaImprimir(boolean habilitaImprimir) {
		this.habilitaImprimir = habilitaImprimir;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public String getLabelZona() {
		return labelZona;
	}
	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}
	public VAacSiglaUnfSalaVO getZona() {
		return zona;
	}
	public void setZona(VAacSiglaUnfSalaVO zona) {
		this.zona = zona;
	}
	public Byte getSala() {
		return sala;
	}
	public void setSala(Byte sala) {
		this.sala = sala;
	}
}