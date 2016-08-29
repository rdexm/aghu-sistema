package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TaxaOcupacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class RelatorioTaxaOcupacaoController extends ActionController {

	private static final long serialVersionUID = 416596072984732828L;
	
	private static final String JQUERY = "jQuery('.jqplot-axis').css('display', 'none')";
	private static final String JQUERY2 = "jQuery('.jqplot-xaxis').css('left', '-4%')";
	private final String FORMATO_HORA_MINUTO = "HH:mm";
	private final String FORMATO_ANO_MES_DIA = "yyyy-MM-dd";


	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private LineChartModel animatedModel;
	
	private BarChartModel animatedBarModel;
	
	private List<MptTipoSessao> listaMptTipoSessao;
	
	private MptTipoSessao tipoSessao;
	
	private List<MptSalas> listaMptSalas; 
	
	private MptSalas sala;	
	
	private Date periodoInicial;
	
	private Date periodoFinal;
	
	private DominioTurno turno;
	
	private MptTurnoTipoSessao horariosTurno;
	
	private Integer enfase;
	
	private String horaInicio;
	
	private String horaFim;
	
	private Boolean renderChartLine;
	
	private Boolean renderChartBar;
	
	private Boolean requiredSala;
	
	private RapServidores servidorLogado;
	
	private List<TaxaOcupacaoVO> listaTaxaOcupacao;
	
	private String labelPanelGrafico;
	
	private final Integer valorRadioSala = 1;
	
	private final Integer valorRadioPoltrona = 2;
	
	@PostConstruct
	protected void inicializar() {
		
		this.iniciar();		
		this.iniciarLineChart();		
		this.iniciarBarChart();
		
		this.begin(conversation);
	}
  
	/**  
	 * Inicia dados na tela.
	 */
	public void iniciar() {
		this.periodoInicial = null;
		this.periodoFinal = null;		
		this.listarTipoSessao();
		this.listaMptSalas = new ArrayList<MptSalas>();
		this.sala = null;
		this.turno = null;
		this.renderChartLine = Boolean.TRUE;
		this.renderChartBar = Boolean.FALSE;
		this.requiredSala = Boolean.FALSE;
		this.enfase = this.valorRadioSala;
		listaTaxaOcupacao = null;
		labelPanelGrafico = "Gráfico de Taxa de Ocupação";
		
		this.servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		if(this.servidorLogado != null){
			MptFavoritoServidor favorito = procedimentoTerapeuticoFacade.obterFavoritoPorServidor(this.servidorLogado);
			if(favorito != null){
				if(favorito.getSeqTipoSessao() != null){					
					this.tipoSessao = procedimentoTerapeuticoFacade.obterTipoSessaoPorId(favorito.getSeqTipoSessao());
					if(favorito.getSeqSala() != null){
						this.sala = procedimentoTerapeuticoFacade.obterSalaPorId(favorito.getSeqSala());
					}
				}
			} else {
				this.tipoSessao = null;
				this.sala = null;
			}
		}
		
		this.carregarComboSalas();
		
		this.iniciarLineChart();		
		this.iniciarBarChart();
		
	}
	
	/**
	 * Inicia gráfico de linha.
	 */
	private void iniciarLineChart() {
		LineChartModel model = new LineChartModel();
		LineChartSeries lineSeries = new LineChartSeries();
		lineSeries.set(null, null);
		lineSeries.setShowLine(false);
		lineSeries.setFill(false);
		lineSeries.setShowMarker(false);
		model.addSeries(lineSeries);
		Axis xAxis = model.getAxis(AxisType.X);
        xAxis.setMax(0);
		Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setMax(0);
		animatedModel = model;
		//RequestContext.getCurrentInstance().execute(JQUERY);
	}
	
	/**
	 * Inicia gráfico de barras.
	 */
	private void iniciarBarChart() {
		BarChartModel modelBar = new BarChartModel();
		BarChartSeries barSeries = new BarChartSeries();
		barSeries.set("0", 0);	
		barSeries.setDisableStack(true);
		modelBar.addSeries(barSeries);
		modelBar.setBarMargin(0);
		modelBar.setBarPadding(0);
		Axis xAxis = modelBar.getAxis(AxisType.X);
		xAxis.setMin(0);
		xAxis.setMax(0);	        
		Axis yAxis = modelBar.getAxis(AxisType.Y);
		yAxis.setMin(0);
		yAxis.setMax(0);	        
		animatedBarModel = modelBar;
		RequestContext.getCurrentInstance().execute(JQUERY);
		RequestContext.getCurrentInstance().execute(JQUERY2);
	}
	
	/**
	 * Método chamado ao trocar a opção no radioButton.
	 */
	public void iniciarChart() {		
		if (this.enfase == this.valorRadioSala) {
			iniciarLineChart();
			labelPanelGrafico = "Gráfico de Taxa de Ocupação";
			this.renderChartLine = Boolean.TRUE;
			this.renderChartBar = Boolean.FALSE;
			this.requiredSala = Boolean.FALSE;
		} else {
			iniciarBarChart();
			labelPanelGrafico = "Gráfico de Salas ou Acomodações";
	        this.renderChartLine = Boolean.FALSE;
			this.renderChartBar = Boolean.TRUE;
			this.requiredSala = Boolean.TRUE;
		}
		
	}
	
	/**
	 * Ação do botão pesquisar.
	 */
	public void pesquisar() {
		
		if(validarCamposIniciais()){
			if(listaTaxaOcupacao == null || listaTaxaOcupacao.isEmpty()){
				RequestContext.getCurrentInstance().execute(JQUERY);
				RequestContext.getCurrentInstance().execute(JQUERY2);
			}
			return;
		}
		
		if(this.periodoFinal.before(this.periodoInicial)){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_DATA_FINAL_MAIOR_IGUAL_DATA_INICIAL");
			if(listaTaxaOcupacao == null || listaTaxaOcupacao.isEmpty()){
				RequestContext.getCurrentInstance().execute(JQUERY);
				RequestContext.getCurrentInstance().execute(JQUERY2);
			}
			return;
		}
		
		
		validarHora();

		if (turno != null && (this.horaInicio.isEmpty() || this.horaFim.isEmpty())) {
			apresentarMsgNegocio(Severity.ERROR, "REGISTRO_NAO_ENCONTRADO");
			listaTaxaOcupacao = null;
			this.iniciarChart();
			return;
		}		
		
		if (this.enfase == this.valorRadioSala) {
			pesquisarListaTaxaOcupacao();
		} else {
			if (sala == null) {
				apresentarMsgNegocio(Severity.ERROR, "MESSAGE_CAMPO_SALA_OBRIGATORIO");
				if(listaTaxaOcupacao == null || listaTaxaOcupacao.isEmpty()){
					RequestContext.getCurrentInstance().execute(JQUERY);
					RequestContext.getCurrentInstance().execute(JQUERY2);
				}
				return;
			}
			List<Short> sequenciaisSala = procedimentoTerapeuticoFacade.obterLocaisAtendimentoPorSala(this.sala.getSeq());
			listaTaxaOcupacao = procedimentoTerapeuticoFacade.consultarTotalOcupacaoPoltronaPorDiaPeriodoSala(periodoInicial, periodoFinal, sala.getSeq(), horaInicio, horaFim, sequenciaisSala);
			
		}
		
		if (listaTaxaOcupacao == null || listaTaxaOcupacao.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "REGISTRO_NAO_ENCONTRADO");
			this.iniciarChart();
			return;
		}
						
		verificarEnfase();
	}

	private Boolean validarCamposIniciais(){
		
		boolean erro = false;
		
		if(this.periodoInicial == null){
			apresentarMsgNegocio(Severity.ERROR, "TITLE_CAMPO_OBRIGATORIO_PERIODO_INICIAL");
			erro = true;
		}
		
		if(this.periodoFinal == null){
			apresentarMsgNegocio(Severity.ERROR, "TITLE_CAMPO_OBRIGATORIO_PERIODO_FINAL");
			erro = true;
		}
		
		if (tipoSessao == null) {
			apresentarMsgNegocio(Severity.ERROR, "MESSAGE_CAMPO_TIPO_SESSAO_OBRIGATORIO");
			erro = true;
		}
		
		if(erro){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
		
	}
	
	private void verificarEnfase(){
		if (this.enfase == this.valorRadioSala) {			
			preencherLineChart();	        
		} else {
			preencherBarChart(); 			
		}    
	}
	
	/**
	 * Pesquisa taxa de ocupação quando o radio de sala estiver marcado.
	 */
	private void pesquisarListaTaxaOcupacao(){
		if (sala == null) {
			List<Short> listaSequenciaisSala = new ArrayList<Short>();
			if (listaMptSalas != null && !listaMptSalas.isEmpty()) {
				for (MptSalas sala : listaMptSalas) {
					listaSequenciaisSala.add(sala.getSeq());
				}
			}			
			listaTaxaOcupacao = procedimentoTerapeuticoFacade.consultarTotalOcupacaoPorDiaPeriodoTipoSessao(periodoInicial, periodoFinal, horaInicio, horaFim, tipoSessao.getSeq(), listaSequenciaisSala);
		} else {
			List<Short> listaSequenciaisSala = new ArrayList<Short>();
			listaSequenciaisSala.add(this.sala.getSeq());
			listaTaxaOcupacao = procedimentoTerapeuticoFacade.consultarTotalOcupacaoPorDiaPeriodoTipoSessao(periodoInicial, periodoFinal, horaInicio, horaFim, tipoSessao.getSeq(), listaSequenciaisSala);
		}
	}
	
	private void validarHora(){
		
		if (horariosTurno != null && horariosTurno.getHoraInicio() != null && horariosTurno.getHoraFim() != null) {
			this.horaInicio = DateUtil.obterDataFormatada(horariosTurno.getHoraInicio(), FORMATO_HORA_MINUTO);
			this.horaFim = DateUtil.obterDataFormatada(horariosTurno.getHoraFim(), FORMATO_HORA_MINUTO);
		} else {
			this.horaInicio = "";
			this.horaFim = "";
		}
		
	}
	
	private void preencherBarras(String descricao, long resultadoHoras, BarChartSeries barSeries) {
		BigDecimal conversor = BigDecimal.valueOf(1000L).multiply(BigDecimal.valueOf(60L)).multiply(BigDecimal.valueOf(60L));
		BigDecimal hours = BigDecimal.valueOf(resultadoHoras).divide(conversor,2,RoundingMode.HALF_UP);
		barSeries.set(descricao, hours.doubleValue());
	}
	
	private void preencherBarChart() {
		BarChartModel modelBar = new BarChartModel();
		BarChartSeries barSeries = new BarChartSeries();
		
		String descricaoAnterior = listaTaxaOcupacao.get(0).getDescricao();
		long resultadoHoras = 0;
		
		for (TaxaOcupacaoVO vo : listaTaxaOcupacao) {
			if (vo.getDescricao().equals(descricaoAnterior)) {				
				String horaSalaIni = DateUtil.obterDataFormatada(vo.getDataInicio(), FORMATO_HORA_MINUTO);
				String horaSalaFim = DateUtil.obterDataFormatada(vo.getDataFim(), FORMATO_HORA_MINUTO);						
				Calendar calendarIni = this.iniciarCalendarComHoraMinuto(horaSalaIni);						
				Calendar calendarFim = this.iniciarCalendarComHoraMinuto(horaSalaFim);
				
				resultadoHoras = resultadoHoras + (calendarFim.getTimeInMillis() - calendarIni.getTimeInMillis()); 						

			} else {
				preencherBarras(descricaoAnterior, resultadoHoras, barSeries);										
				
				descricaoAnterior = vo.getDescricao();
				
				String horaSalaIni = DateUtil.obterDataFormatada(vo.getDataInicio(), FORMATO_HORA_MINUTO);
				String horaSalaFim = DateUtil.obterDataFormatada(vo.getDataFim(), FORMATO_HORA_MINUTO);						
				Calendar calendarIni = this.iniciarCalendarComHoraMinuto(horaSalaIni);						
				Calendar calendarFim = this.iniciarCalendarComHoraMinuto(horaSalaFim);
				
				resultadoHoras = calendarFim.getTimeInMillis() - calendarIni.getTimeInMillis();
			}
		}

		preencherBarras(descricaoAnterior, resultadoHoras, barSeries);
		modelBar.addSeries(barSeries);
		
		Axis yAxis = modelBar.getAxis(AxisType.Y);
        yAxis.setTickFormat("%.1f");
        yAxis.setMin(0d);
		
		animatedBarModel = modelBar;
		animatedBarModel.setAnimate(true);
		RequestContext.getCurrentInstance().execute(JQUERY2);
	}
	
	private void preencherLinhaDistintas(Date data, Short seqSala, long resultadoHoras, LineChartSeries lineSeries){
		BigDecimal conversor = BigDecimal.valueOf(1000L).multiply(BigDecimal.valueOf(60L)).multiply(BigDecimal.valueOf(60L));
		BigDecimal hours = BigDecimal.valueOf(resultadoHoras).divide(conversor,2,RoundingMode.HALF_UP);
		lineSeries.set(DateUtil.obterDataFormatada(data, FORMATO_ANO_MES_DIA), hours.doubleValue());				
	}
	
	private void preencherLineChart() {
		LineChartModel model = new LineChartModel();			 
		LineChartSeries lineSeries = new LineChartSeries();
		Short salaAnterior = null;
		Date dataAnterior = null;
		String dataAnteriorFormatada = null;
		long resultadoHoras = 0;
		
		Date menorDataGrafico = null;
		Date maiorDataGrafico = null;
		
		if(listaTaxaOcupacao != null) {
			salaAnterior = listaTaxaOcupacao.get(0).getSalSeq();
			dataAnterior = listaTaxaOcupacao.get(0).getDataInicio();
			dataAnteriorFormatada = DateUtil.obterDataFormatada(listaTaxaOcupacao.get(0).getDataInicio(), FORMATO_ANO_MES_DIA);
			menorDataGrafico = listaTaxaOcupacao.get(0).getDataInicio();
			maiorDataGrafico = listaTaxaOcupacao.get(0).getDataInicio();
		}
		
		for (TaxaOcupacaoVO vo : listaTaxaOcupacao) {
			if(vo.getSalSeq() != null) {
				if(vo.getSalSeq().equals(salaAnterior)) {
					if(DateUtil.obterDataFormatada(vo.getDataInicio(), FORMATO_ANO_MES_DIA).equals(dataAnteriorFormatada)) {
						String horaSalaIni = DateUtil.obterDataFormatada(vo.getDataInicio(), FORMATO_HORA_MINUTO);
						String horaSalaFim = DateUtil.obterDataFormatada(vo.getDataFim(), FORMATO_HORA_MINUTO);						
						Calendar calendarIni = this.iniciarCalendarComHoraMinuto(horaSalaIni);						
						Calendar calendarFim = this.iniciarCalendarComHoraMinuto(horaSalaFim);
						
						resultadoHoras = resultadoHoras + (calendarFim.getTimeInMillis() - calendarIni.getTimeInMillis()); 						
					} else {
						preencherLinhaDistintas(dataAnterior, salaAnterior, resultadoHoras, lineSeries);
												
						dataAnterior = vo.getDataInicio();
						dataAnteriorFormatada = DateUtil.obterDataFormatada(vo.getDataInicio(), FORMATO_ANO_MES_DIA);
						
						String horaSalaIni = DateUtil.obterDataFormatada(vo.getDataInicio(), FORMATO_HORA_MINUTO);
						String horaSalaFim = DateUtil.obterDataFormatada(vo.getDataFim(), FORMATO_HORA_MINUTO);						
						Calendar calendarIni = this.iniciarCalendarComHoraMinuto(horaSalaIni);						
						Calendar calendarFim = this.iniciarCalendarComHoraMinuto(horaSalaFim);
						
						resultadoHoras = calendarFim.getTimeInMillis() - calendarIni.getTimeInMillis();
					}
				} else {
					preencherLinhaDistintas(dataAnterior, salaAnterior, resultadoHoras, lineSeries);
					
					TaxaOcupacaoVO sala = procedimentoTerapeuticoFacade.obterNomeSala(salaAnterior);
					if(sala != null){
						lineSeries.setLabel(sala.getNomeSala());
					}
					
					model.addSeries(lineSeries);					
					lineSeries = new LineChartSeries();
					
					salaAnterior = vo.getSalSeq();
					dataAnterior = vo.getDataInicio();
					dataAnteriorFormatada = DateUtil.obterDataFormatada(vo.getDataInicio(), FORMATO_ANO_MES_DIA);
					
					String horaSalaIni = DateUtil.obterDataFormatada(vo.getDataInicio(), FORMATO_HORA_MINUTO);
					String horaSalaFim = DateUtil.obterDataFormatada(vo.getDataFim(), FORMATO_HORA_MINUTO);						
					Calendar calendarIni = this.iniciarCalendarComHoraMinuto(horaSalaIni);						
					Calendar calendarFim = this.iniciarCalendarComHoraMinuto(horaSalaFim);
					
					resultadoHoras = calendarFim.getTimeInMillis() - calendarIni.getTimeInMillis();
				}
			}
			
			if (vo.getDataInicio().before(menorDataGrafico)) {
				menorDataGrafico = vo.getDataInicio();
			}
			
			if (vo.getDataInicio().after(maiorDataGrafico)) {
				maiorDataGrafico = vo.getDataInicio();
			}
		}	
		
		preencherLinhaDistintas(dataAnterior, salaAnterior, resultadoHoras, lineSeries);
		
		TaxaOcupacaoVO sala = procedimentoTerapeuticoFacade.obterNomeSala(salaAnterior);
		if(sala != null){
			lineSeries.setLabel(sala.getNomeSala());
		}
		
		model.addSeries(lineSeries);
		
		if(this.sala == null){
			model.setLegendPosition("e");
		}
		
		Axis yAxis = model.getAxis(AxisType.Y);
        yAxis.setTickFormat("%.1f");
        yAxis.setMin(0d);
		
		animatedModel = model;       
		animatedModel.setAnimate(true);
				
		DateAxis axis = new DateAxis();
		axis.setTickAngle(-50);
		axis.setTickFormat("%d-%m");
		axis.setTickCount(DateUtil.obterQtdDiasEntreDuasDatas(menorDataGrafico, maiorDataGrafico).intValue() + 1);
		animatedModel.getAxes().put(AxisType.X, axis);
		
	}
	
	private Calendar iniciarCalendarComHoraMinuto(String hora) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2015);
		calendar.set(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.HOUR, Integer.valueOf(hora.substring(0, 2)));
		calendar.add(Calendar.MINUTE, Integer.valueOf(hora.substring(3, 5)));
		
		return calendar;
	}
	
	/**
	 * Obtém horários de turno.
	 */
	public void obterHorariosTurno() {
		if (this.turno != null && this.tipoSessao != null) {
			horariosTurno = procedimentoTerapeuticoFacade.obterTurnoTipoSessaoPorTurnoTpsSeq(this.turno, this.tipoSessao.getSeq());
		} else {
			this.horaInicio = "";
			this.horaFim = "";
			horariosTurno = null;
		}
	}

	/**
	 * Preenche combo de tipo sessão
	 * @return lista de tipo sessão.
	 */
	public List<MptTipoSessao> listarTipoSessao() {
		this.listaMptTipoSessao =  procedimentoTerapeuticoFacade.obterListaTipoSessaoPorIndSituacaoAtiva();
		return this.listaMptTipoSessao;
	}
	
	/**
	 * Carrega combo de salas
	 */
	public void carregarComboSalas() {
		if (this.tipoSessao == null) {
			this.listaMptSalas = new ArrayList<MptSalas>();			
		} else {
			this.listaMptSalas = procedimentoTerapeuticoFacade.obterListaSalasPorTipoSessao(this.tipoSessao.getSeq());
			if (this.listaMptSalas.isEmpty()) {
				this.listaMptSalas = new ArrayList<MptSalas>();
				this.sala = null;
			}
		}
	}
	
	/**
	 * Ação do botão Limpar
	 */
	public void limpar() {		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		
		while (componentes.hasNext()) {			
			limparValoresSubmetidos(componentes.next());
		}		
		
		this.iniciar();
	}
	
	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	//
	//GETs e SETs
	//
	
	public Date getPeriodoInicial() {
		return periodoInicial;
	}

	public void setPeriodoInicial(Date periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

	public Date getPeriodoFinal() {
		return periodoFinal;
	}

	public void setPeriodoFinal(Date periodoFinal) {
		this.periodoFinal = periodoFinal;
	}

	public IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return procedimentoTerapeuticoFacade;
	}

	public void setProcedimentoTerapeuticoFacade(
			IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade) {
		this.procedimentoTerapeuticoFacade = procedimentoTerapeuticoFacade;
	}

	public List<MptTipoSessao> getListaMptTipoSessao() {
		return listaMptTipoSessao;
	}

	public void setListaMptTipoSessao(List<MptTipoSessao> listaMptTipoSessao) {
		this.listaMptTipoSessao = listaMptTipoSessao;
	}

	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public LineChartModel getAnimatedModel() {
		return animatedModel;
	}

	public void setAnimatedModel(LineChartModel animatedModel) {
		this.animatedModel = animatedModel;
	}

	public List<MptSalas> getListaMptSalas() {
		return listaMptSalas;
	}

	public void setListaMptSalas(List<MptSalas> listaMptSalas) {
		this.listaMptSalas = listaMptSalas;
	}

	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	public BarChartModel getAnimatedBarModel() {
		return animatedBarModel;
	}

	public void setAnimatedBarModel(BarChartModel animatedBarModel) {
		this.animatedBarModel = animatedBarModel;
	}

	public DominioTurno getTurno() {
		return turno;
	}

	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}
	
	public Integer getEnfase() {
		return enfase;
	}

	public void setEnfase(Integer enfase) {
		this.enfase = enfase;
	}

	public Integer getValorRadioSala() {
		return valorRadioSala;
	}

	public Integer getValorRadioPoltrona() {
		return valorRadioPoltrona;
	}

	public MptTurnoTipoSessao getHorariosTurno() {
		return horariosTurno;
	}

	public void setHorariosTurno(MptTurnoTipoSessao horariosTurno) {
		this.horariosTurno = horariosTurno;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	public String getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(String horaFim) {
		this.horaFim = horaFim;
	}

	public Boolean getRenderChartLine() {
		return renderChartLine;
	}

	public void setRenderChartLine(Boolean renderChartLine) {
		this.renderChartLine = renderChartLine;
	}

	public Boolean getRenderChartBar() {
		return renderChartBar;
	}

	public void setRenderChartBar(Boolean renderChartBar) {
		this.renderChartBar = renderChartBar;
	}

	public Boolean getRequiredSala() {
		return requiredSala;
	}

	public void setRequiredSala(Boolean requiredSala) {
		this.requiredSala = requiredSala;
	}

	public String getLabelPanelGrafico() {
		return labelPanelGrafico;
	}

	public void setLabelPanelGrafico(String labelPanelGrafico) {
		this.labelPanelGrafico = labelPanelGrafico;
	}
}
