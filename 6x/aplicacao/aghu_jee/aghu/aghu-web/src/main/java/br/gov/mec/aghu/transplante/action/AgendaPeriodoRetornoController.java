package br.gov.mec.aghu.transplante.action;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.AgendaTransplanteRetornoVO;
import br.gov.mec.aghu.transplante.vo.TotalizadorAgendaTransplanteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class AgendaPeriodoRetornoController extends ActionController {
	private static final long serialVersionUID = -5194748745493396515L;
	
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String MASCARA_TELEFONE_10 = "(##)####-####";
	private static final String MASCARA_TELEFONE_11 = "(##)#####-####";

	private Integer openTogglePesquisa = 0;
	private Integer openToggleResultado = 0;
	private DominioTipoRetorno tipoRetorno;
	private MtxItemPeriodoRetorno descricaoTipoRetorno;	
	private List<AghEspecialidades> selectedEspecialidades;
	private List<AghEspecialidades> especialidades;
	private AipPacientes paciente;
	private List<AgendaTransplanteRetornoVO> listaConsultas;
	private List<TotalizadorAgendaTransplanteVO> listaTotalizador;
	private String currentView = "agendaWeek";
	private ScheduleModel scheduleModelVisualizacao;
	private ScheduleModel scheduleMensal;
	private ScheduleModel scheduleDiario;
	private Date dataInicialCalendario;
	private Boolean resultadoDesabilitado;
	private MtxTransplantes observacaoTransplante;
	private Integer maximoEspecialidades;
	private AbstractMap<Integer, String> contatos;
	private DefaultScheduleEvent eventoSelecionado;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	public List<MtxItemPeriodoRetorno> obterDescTipoRetorno(String objPesquisa) throws BaseException  {
		if (objPesquisa != null) {
			objPesquisa = objPesquisa.trim();
		}
		
		return transplanteFacade.pesquisarItemPeriodoRetorno(this.tipoRetorno, null, objPesquisa);
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(Integer.valueOf((String) event.getNewValue()), event.getComponent().getId());	
			
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparPesquisa() {
		this.paciente = null;
		this.descricaoTipoRetorno = null;
		this.selectedEspecialidades = null;
		this.tipoRetorno = DominioTipoRetorno.A;
		this.resultadoDesabilitado = true;
		this.openToggleResultado = -1;
	}
	
	public void limparDescricao(){
		this.descricaoTipoRetorno = null;
	}
	
	public void pesquisar() throws ApplicationBusinessException{
		
		if (this.selectedEspecialidades != null && this.selectedEspecialidades.size() > maximoEspecialidades){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_EXCEDEU_MAXIMO_ESPECIALIDADE", maximoEspecialidades);
		}else {
			Integer codPaciente = null;
			if (this.paciente != null && this.paciente.getCodigo() != null){
				codPaciente = this.paciente.getCodigo();
			}
			
			this.listaConsultas = transplanteFacade.obterAgendaTransplanteComPrevisaoRetorno(codPaciente, this.selectedEspecialidades, this.tipoRetorno, this.descricaoTipoRetorno);
			this.listaTotalizador = transplanteFacade.obterTotalConsultasPorDia(this.listaConsultas, this.descricaoTipoRetorno);
			this.gerarHorarioCalendario();		
			this.configurarFonteDadosSchedule();
			
			RequestContext.getCurrentInstance().update("resultTab");
		}
	}
	
    public void onEventSelect(SelectEvent selectEvent) {
    	if (!this.currentView.equals("month")){
    		this.eventoSelecionado = (DefaultScheduleEvent) selectEvent.getObject();
	        
			AgendaTransplanteRetornoVO agenda = (AgendaTransplanteRetornoVO) this.eventoSelecionado.getData();
			
			this.observacaoTransplante = new MtxTransplantes();
			this.observacaoTransplante.setSeq(agenda.getSeqTransplante());
			this.observacaoTransplante.setObservacoes(agenda.getObservacaoTransplante());
			
			RequestContext.getCurrentInstance().execute("PF('observacaoTransplanteWG').show();");
    	}
    }
	
	public void gravarObservacao(){
		
		if (!StringUtils.isBlank(this.observacaoTransplante.getObservacoes())){
			this.observacaoTransplante.setObservacoes(this.observacaoTransplante.getObservacoes().trim());
			transplanteFacade.atualizarObservacaoTransplante(this.observacaoTransplante);
			
			((AgendaTransplanteRetornoVO) this.eventoSelecionado.getData()).setObservacaoTransplante(this.observacaoTransplante.getObservacoes());
			
			RequestContext.getCurrentInstance().execute("fecharModal();");
		} else{
			this.apresentarMsgNegocio("MSG_CAMPO_OBSERVACAO_VAZIO");
		}
	}
	
	private void gerarHorarioCalendario() {
		if (this.listaConsultas != null && this.listaConsultas.size() > 0){
			this.resultadoDesabilitado = false;
			this.openToggleResultado = 0;
			this.criarConfiguracaoScheduleMensal();
			this.criarConfiguracaoScheduleDiario();
		}
		else {
			this.apresentarMsgNegocio("MSG_NENHUMA_CONSULTA_ENCONTRADA");
			this.resultadoDesabilitado = true;
			this.openToggleResultado = -1;
		}
	}

	private void criarConfiguracaoScheduleDiario() {
		this.scheduleDiario = new DefaultScheduleModel();
		this.contatos = new HashMap<Integer, String>();

		for (AgendaTransplanteRetornoVO agendaTransplanteRetornoVO : this.listaConsultas) {
			this.scheduleDiario.addEvent(this.criarEventSchedule(agendaTransplanteRetornoVO));
		}
		
		this.contatos.clear();
	}

	private void criarConfiguracaoScheduleMensal() {
		this.scheduleMensal = new DefaultScheduleModel(); 
		
		for (TotalizadorAgendaTransplanteVO totalizadorAgendaTransplanteVO : this.listaTotalizador) {
			this.scheduleMensal.addEvent(this.criarEventSchedule(totalizadorAgendaTransplanteVO));
		}
	}
	
	private DefaultScheduleEvent criarEventSchedule(TotalizadorAgendaTransplanteVO totalizador) {
		DefaultScheduleEvent event = new DefaultScheduleEvent();
		event.setTitle(this.truncarTexto(totalizador.getTipoTotal(), 20) + ": " + totalizador.getQuantidade());
		event.setDescription(totalizador.getTipoTotal() + ": " + totalizador.getQuantidade());
		event.setAllDay(true);
		event.setStartDate(totalizador.getData());
		event.setEndDate(totalizador.getData());
		event.setEditable(false);
		
		return event;
	}
	
	private DefaultScheduleEvent criarEventSchedule(AgendaTransplanteRetornoVO agenda) {
		DefaultScheduleEvent event = new DefaultScheduleEvent();
		event.setData(agenda);
		event.setTitle(agenda.getHoraFormatadaSchedule() + " - " + this.truncarTexto(agenda.getNomePaciente(), 15));
		event.setDescription(this.obterDescricaoEvento(agenda));
		event.setStartDate(agenda.getDataConsultaSchedule());
		event.setEndDate(agenda.getHoraFim());
		event.setEditable(false);
		event.setStyleClass(agenda.getCorLegenda());

		return event;
	}
	
	private String truncarTexto(String descricao, int tamMax) {
		if(descricao.length() > tamMax){
			return StringUtils.abbreviate(descricao, tamMax); 
		}
		return descricao;
	}

	private String obterDescricaoEvento(AgendaTransplanteRetornoVO agenda) {
		StringBuilder texto = new StringBuilder();
		
		if (!this.contatos.containsKey(agenda.getProntuario())){

			texto.append("Prontuário: ").append(CoreUtil.formataProntuarioRelatorio(agenda.getProntuario()))
				.append("\nPaciente: ").append(agenda.getNomePaciente());
	
			if (!StringUtils.isBlank(agenda.getContato())){
				texto.append("\nContato: ").append(StringUtils.split(agenda.getContato(), "[-]")[0].trim())
			 		.append("\nTelefone: ").append(this.inserirMascaraTelefone(Long.valueOf(StringUtils.split(agenda.getContato(), "[-]")[1].trim())));
			}
			else{
				texto.append("\nContato: ");
				
				if (agenda.getFoneResidencial() != null || agenda.getFoneRecado() != null){
					texto.append("\nTelefone: ");
					
					if (agenda.getFoneResidencial() != null){
						texto.append(this.inserirMascaraTelefone(agenda.getDddFoneResidencial().toString(), agenda.getFoneResidencial().toString()));
						
						if (agenda.getFoneRecado() != null){
							texto.append(" / ").append(this.inserirMascaraTelefone(agenda.getDddFoneRecado().toString(), agenda.getFoneRecado()));	
						}
					} else 	if (agenda.getFoneRecado() != null){
						texto.append(this.inserirMascaraTelefone(agenda.getDddFoneRecado().toString(), agenda.getFoneRecado()));	
					}
	
				}
			}
			
			this.contatos.put(agenda.getProntuario(), texto.toString());
		} else{
			texto.append(this.contatos.get(agenda.getProntuario()));
		}
		
		return texto.toString();
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);

		limparPesquisa();
	}	
	
	public void iniciar() throws ApplicationBusinessException {
		CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
		if (codPac != null && codPac.getCodigo() > 0) { 
			paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
		}
		
		this.carregarLimiteEspecialidades();
		this.especialidades = transplanteFacade.obterEspecialidadesAtivas();
		this.dataInicialCalendario = new Date();
		this.resultadoDesabilitado = true;
		this.openToggleResultado = -1;
		this.observacaoTransplante = new MtxTransplantes();
	}
	
	private void carregarLimiteEspecialidades() throws ApplicationBusinessException {
		AghParametros param =  parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_ESPEC_AGENDA);
		if (param != null){
			this.maximoEspecialidades = param.getVlrNumerico().intValue();
		}
	}

	public void scheduleChangeView(SelectEvent event){
		this.configurarFonteDadosSchedule();
	}

	private void configurarFonteDadosSchedule() {
		if (this.currentView.equals("month")){
			this.setScheduleModelVisualizacao(this.scheduleMensal);
		} else{
			this.setScheduleModelVisualizacao(this.scheduleDiario);
		}
	}
	
	private String inserirMascaraTelefone(String ddd, String fone) {
		String telefone = ddd + fone;
		
		if (telefone.length() == 10) {
			return this.inserirMascara(telefone, MASCARA_TELEFONE_10);
		}
		
		return this.inserirMascara(telefone, MASCARA_TELEFONE_11);
	}
	
	private String inserirMascaraTelefone(Long telefoneSemMascara) {
		
		String telefone = telefoneSemMascara.toString();
		
		if (telefone.length() == 10) {
			return this.inserirMascara(telefone, MASCARA_TELEFONE_10);
		}
		
		return this.inserirMascara(telefone, MASCARA_TELEFONE_11);
	}
	
    private String inserirMascara(String valor, String mascara) {

        String novoValor = "";
        int posicao = 0;

        for (int i = 0; mascara.length() > i; i++) {
            if (mascara.charAt(i) == '#') {
                if (valor.length() > posicao) {
                    novoValor = novoValor.concat(String.valueOf(valor.charAt(posicao)));
                    posicao++;
                } else {
                    break;
                }
            } else {
                if (valor.length() > posicao) {
                    novoValor = novoValor.concat(String.valueOf(mascara.charAt(i)));
                } else {
                    break;
                }
            }
        }
        return novoValor;
    }

	public Integer getOpenTogglePesquisa() {
		return openTogglePesquisa;
	}

	public void setOpenTogglePesquisa(Integer openTogglePesquisa) {
		this.openTogglePesquisa = openTogglePesquisa;
	}

	public DominioTipoRetorno getTipoRetorno() {
		return tipoRetorno;
	}

	public void setTipoRetorno(DominioTipoRetorno tipoRetorno) {
		this.tipoRetorno = tipoRetorno;
	}

	public MtxItemPeriodoRetorno getDescricaoTipoRetorno() {
		return descricaoTipoRetorno;
	}

	public void setDescricaoTipoRetorno(MtxItemPeriodoRetorno descricaoTipoRetorno) {
		this.descricaoTipoRetorno = descricaoTipoRetorno;
	}

	public List<AghEspecialidades> getSelectedEspecialidades() {
		return selectedEspecialidades;
	}

	public void setSelectedEspecialidades(
			List<AghEspecialidades> selectedEspecialidades) {
		this.selectedEspecialidades = selectedEspecialidades;
	}

	public List<AghEspecialidades> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(List<AghEspecialidades> especialidades) {
		this.especialidades = especialidades;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	public List<AgendaTransplanteRetornoVO> getListaConsultas() {
		return listaConsultas;
	}

	public void setListaConsultas(List<AgendaTransplanteRetornoVO> listaConsultas) {
		this.listaConsultas = listaConsultas;
	}

	public String getCurrentView() {
		return currentView;
	}

	public void setCurrentView(String currentView) {
		this.currentView = currentView;
	}

	public ScheduleModel getScheduleModelVisualizacao() {
		return scheduleModelVisualizacao;
	}

	public void setScheduleModelVisualizacao(ScheduleModel scheduleModelVisualizacao) {
		this.scheduleModelVisualizacao = scheduleModelVisualizacao;
	}

	public Date getDataInicialCalendario() {
		return dataInicialCalendario;
	}

	public void setDataInicialCalendario(Date dataInicialCalendario) {
		this.dataInicialCalendario = dataInicialCalendario;
	}

	public Boolean getResultadoDesabilitado() {
		return resultadoDesabilitado;
	}

	public void setResultadoDesabilitado(Boolean resultadoDesabilitado) {
		this.resultadoDesabilitado = resultadoDesabilitado;
	}

	public Integer getOpenToggleResultado() {
		return openToggleResultado;
	}

	public void setOpenToggleResultado(Integer openToggleResultado) {
		this.openToggleResultado = openToggleResultado;
	}

	public MtxTransplantes getObservacaoTransplante() {
		return observacaoTransplante;
	}

	public void setObservacaoTransplante(MtxTransplantes observacaoTransplante) {
		this.observacaoTransplante = observacaoTransplante;
	}

	public Integer getMaximoEspecialidades() {
		return maximoEspecialidades;
	}

	public void setMaximoEspecialidades(Integer maximoEspecialidades) {
		this.maximoEspecialidades = maximoEspecialidades;
	}

	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}
}
