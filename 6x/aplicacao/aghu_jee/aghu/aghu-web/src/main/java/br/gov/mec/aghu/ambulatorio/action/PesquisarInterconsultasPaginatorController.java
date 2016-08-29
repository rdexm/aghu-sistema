package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacaoInterconsultas;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultasPesquisa;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class PesquisarInterconsultasPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 5674793387617959097L;
	private static final String  CADASTRO_PACIENTE = "paciente-cadastroPaciente";
	private static final String  DISPONIBILIDADE_HORARIOS = "ambulatorio-disponibilidadeHorarios";
	private static final String  GRADE_AGENDAMENTO_DISPONIBILIDADE_HORARIOS = "gradeagendamento-disponibilidadeHorarios";
	private static final String PAGE_GESTAO_INTERCONSULTAS = "ambulatorio-gestaoInterconsultas";
	private static final String PAGE_EDICAO_GESTAO_INTERCONSULTAS = "ambulatorio-edicaoGestaoInterconsultas";
	private static final String HIFEN = " - ";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private CadastrarPacienteController cadastrarPacienteController;

	@Inject
	private DisponibilidadeHorariosPaginatorController disponibilidadeHorariosPaginatorController;

	@Inject @Paginator
	private DynamicDataModel<MamInterconsultas> dataModel;

	private MamInterconsultas parametroSelecionado;

	// Filtro principal da pesquisa
	private MamInterconsultas filtro = new MamInterconsultas();

	private AghUnidadesFuncionais unidadeFuncional;
	private AghEspecialidades aghEspecialidades;
	private DominioSimNao consultoriaCombo;
	private boolean consultoria;
	private DominioSimNao excluidosCombo;
	private boolean excluidos;
	private DominioSituacaoInterconsultasPesquisa SituacaoCombo;
	private boolean desabilitarDatas;
	private boolean desabilitarSituacao;
	private String ordenar;
	private String labelSetor;
	private boolean ativo;
	private boolean suggestionBoxSetor;
	private boolean desabilitarBotoes;
	private DominioTurno turno;
	private String titleSetor;
	private AghUnidadesFuncionais unidadesFuncionais;
	private Date dataInicial;
	private Date dataFinal;
	private List<SelectItem> listaOrdennacao = new ArrayList<SelectItem>();
	private List<SelectItem> listaSituacao = new ArrayList<SelectItem>();
	private String cameFrom;
	private String strInterconsultaMarcada;
	private String strRetorno;
	private String digitadoPor;
	private String marcadoPor;
	private String avisadoPor;
	private boolean aparecerModal;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		instanciaObject();
		carregarParametros();
		if (!this.dataModel.getPesquisaAtiva()) {
			consultoriaCombo = DominioSimNao.N; 
			excluidosCombo = DominioSimNao.N;			
			ordenar = DominioOrdenacaoInterconsultas.D.toString();
			filtro.setSituacao(DominioSituacaoInterconsultasPesquisa.P);
		}		
		suggestionBoxSetor = true;
	}

	
	
	/** inicia alguns objetos da tela setando alguns valores. */
	public void iniciar() {
		if (cameFrom != null && cameFrom.contains(GRADE_AGENDAMENTO_DISPONIBILIDADE_HORARIOS)){
			desabilitarBotoes = false;	
			cameFrom = null;
		}else if (cameFrom != null && cameFrom.contains(CADASTRO_PACIENTE)){
			desabilitarBotoes = false;	
			cameFrom = null;
		}else{
			desabilitarBotoes = true;
		}
		
		consultoriaCombo = DominioSimNao.N;
		excluidosCombo =  DominioSimNao.N;
	
		if (!this.dataModel.getPesquisaAtiva()) {
			setOrdenar(DominioOrdenacaoInterconsultas.D.toString());
			filtro.setSituacao(DominioSituacaoInterconsultasPesquisa.P);
			
			instanciaObject();			
		}		
	}
	
	private void instanciaObject() {
		if (filtro.getPaciente() == null){
			filtro.setPaciente(new AipPacientes());				
		}
		if (filtro.getConsultaRetorno() == null){
			filtro.setConsultaRetorno(new AacConsultas());				
		}
		if (filtro.getConsultaMarcada() == null){
			filtro.setConsultaMarcada(new AacConsultas());
		}
		if (filtro.getEspecialidade() == null){
			filtro.setEspecialidade(new AghEspecialidades());
		}
		if (filtro.getConsulta() == null){
			filtro.setConsulta(new AacConsultas());
			filtro.getConsulta().setGradeAgendamenConsulta(
					new AacGradeAgendamenConsultas());
			filtro.getConsulta().getGradeAgendamenConsulta()
					.setUnidadeFuncional(new AghUnidadesFuncionais());
		}
	}

	@Override
	public List<MamInterconsultas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.ambulatorioFacade.listaInterconsultas(firstResult, maxResult, ordenar, asc, this.filtro, dataInicial, dataFinal, consultoria, excluidos);
	}
	@Override
	public Long recuperarCount() {
		return this.ambulatorioFacade.listaInterconsultasCount(this.filtro, dataInicial, dataFinal, consultoria, excluidos);
	}
	/**
	 * Método de pesquisa de interconsulta.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void pesquisar() throws ApplicationBusinessException {
		try {
			parametroSelecionado = null;
			this.ambulatorioFacade.validarDatas(dataInicial, dataFinal);
			if (consultoriaCombo.equals(DominioSimNao.S)){
				consultoria = true;
			}else{
				consultoria = false;
			}
			if (excluidosCombo.equals(DominioSimNao.S)){
				excluidos = true;
			}else{
				excluidos = false;
			}
			filtro.setEspecialidade(aghEspecialidades);
			filtro.getConsulta().getGradeAgendamenConsulta().setUnidadeFuncional(unidadeFuncional);
			this.dataModel.reiniciarPaginator();
			desabilitarBotoes = true;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}		
	}

	/**
	 * Método que limpa os campos na tela.
	 */
	public void limpar() {
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
        while (componentes.hasNext()) {
               limparValoresSubmetidos(componentes.next());
        }
		
		this.filtro = new MamInterconsultas();
		consultoria = false;
		desabilitarBotoes = true;
		consultoriaCombo = DominioSimNao.N;
		excluidos = false;		
		excluidosCombo = DominioSimNao.N;		
		dataInicial = null;
		dataFinal = null;		
		desabilitarSituacao = false;		
		desabilitarDatas = false;				
		suggestionBoxSetor = true;
		ordenar = DominioOrdenacaoInterconsultas.D.toString();
		aghEspecialidades = null;
		unidadeFuncional = null;
		filtro.setSituacao(DominioSituacaoInterconsultasPesquisa.P);
		strInterconsultaMarcada = null;
		strRetorno = null;
		this.dataModel.limparPesquisa();
		instanciaObject();
	}
	
	/**
     * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
     * 
      * @param object {@link Object}
	 * @throws InterruptedException 
     */
     private void limparValoresSubmetidos(Object object)  {
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

	public String editar() {
		return PAGE_EDICAO_GESTAO_INTERCONSULTAS;
	}
	/**
	 * método que esclui a interconsulta. Exclusão lógica.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void excluir() throws ApplicationBusinessException {
		this.aparecerModal = true;
		
		try {
			interConsultaMarcadaRetorno();
			ambulatorioFacade.excluirInterconsultas(parametroSelecionado);	
			this.dataModel.reiniciarPaginator();		
		} catch (ApplicationBusinessException e) {
			this.aparecerModal = false;
			apresentarExcecaoNegocio(e);
		}
		
		if (this.aparecerModal){
			RequestContext.getCurrentInstance().execute("PF('modalInterconsultaRetornoWG').show()"); 
		}
	}

	/**
	 * Método que atualiza avisado.
	 * 
	 * @param fatAvisarInterconsultas
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAviso(MamInterconsultas fatAvisarInterconsultas) throws ApplicationBusinessException {
		this.aparecerModal = true;
		String foiAvisado = avisado(fatAvisarInterconsultas);
		try {						
			ambulatorioFacade.avisarInterconsultas(fatAvisarInterconsultas, foiAvisado);
			interConsultaMarcadaRetorno();			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			this.aparecerModal = false;
			apresentarExcecaoNegocio(e);
		}
		if (this.aparecerModal){
			RequestContext.getCurrentInstance().execute("PF('modalInterconsultaRetornoWG').show()"); 
		}
	}

	/**
	 * Método que verifica se está avisado ou não. Retorna "S" ou "N". 
	 * 
	 * @param fatAvisarInterconsultas
	 * @return String
	 */
	public String avisado(MamInterconsultas fatAvisarInterconsultas) {
		String avisado = "";
		Integer consultaMarcada = null;
		Integer consultaRetorno = null;		
		if (fatAvisarInterconsultas.getConsultaMarcada() != null && fatAvisarInterconsultas.getConsultaMarcada().getNumero() != null){
			consultaMarcada = fatAvisarInterconsultas.getConsultaMarcada().getNumero();
		}
		if (fatAvisarInterconsultas.getConsultaRetorno() != null && fatAvisarInterconsultas.getConsultaRetorno().getNumero() != null){
			consultaRetorno = fatAvisarInterconsultas.getConsultaRetorno().getNumero();
		}		
		if (consultaMarcada != null || consultaRetorno != null) {
			if (consultaMarcada != null && consultaRetorno != null) {
				if (fatAvisarInterconsultas.getSituacao().equals(
						DominioSituacaoInterconsultasPesquisa.A)
						&& fatAvisarInterconsultas.getDthrAvisaRetorno() != null) {
					avisado = DominioSimNao.S.toString();
				} else {
					avisado = DominioSimNao.N.toString();
				}
			} else if (consultaMarcada != null && consultaRetorno == null) {
				if (fatAvisarInterconsultas.getSituacao().equals(
						DominioSituacaoInterconsultasPesquisa.A)) {
					avisado = DominioSimNao.S.toString();
				} else {
					avisado = DominioSimNao.N.toString();
				}
			} else if (consultaMarcada == null && consultaRetorno != null) {
				if (fatAvisarInterconsultas.getDthrAvisaRetorno() != null) {
					avisado = DominioSimNao.S.toString();
				} else {
					avisado = DominioSimNao.N.toString();
				}
			} else {
				avisado = DominioSimNao.N.toString();
			}
		} else {
			avisado = DominioSimNao.N.toString();
		}
		return avisado;
	}
	
	/**
	 * Método que verifica se permite avisar ou não.
	 * 
	 * @param fatPermiteAvisarInterconsultas
	 * @return boolean
	 */
	public boolean permiteAvisar(MamInterconsultas fatPermiteAvisarInterconsultas) {		
		if (fatPermiteAvisarInterconsultas.getConsultaMarcada() != null && fatPermiteAvisarInterconsultas.getConsultaMarcada().getNumero() == null
			&& fatPermiteAvisarInterconsultas.getConsultaRetorno() !=null && fatPermiteAvisarInterconsultas.getConsultaRetorno().getNumero() == null){				
			return false;			
		}else{			
			return true;			
		}		
	}
	
	/**
	 * Método que verifica que se tiver avisado mostra checado grid. 
	 * 
	 * @param fatAvisarInterconsultas
	 * @return boolean
	 */
	public boolean avisadoRetorno(MamInterconsultas fatAvisarInterconsultas) {		
		boolean avisado = false; 
		String retorno = avisado(fatAvisarInterconsultas);		
		if (retorno.equals(DominioSimNao.S.toString())){
			avisado = true;			
		}else{
			avisado = false;
		}			
		return avisado;
	}

	/**
	 * Método que verifica que tiver excluído mostra checado na grid.
	 * 
	 * @param fatAvisarInterconsultas
	 * @return boolean
	 */
	public boolean excluidoRetorno(MamInterconsultas fatAvisarInterconsultas) {		
		boolean excluido = false;		
		if (fatAvisarInterconsultas.getPendente().equals(DominioIndPendenteAmbulatorio.C)){
			excluido = true;			
		}else{
			excluido = false;
		}					
		return excluido;
	}
	
	/**
	 * Método que verifica que se permite avisar mostra o botão avisado na grid.
	 * 
	 * @param fatPermiteAvisarInterconsultas
	 * @return boolean
	 */
	public boolean rederizarAtivar(MamInterconsultas fatPermiteAvisarInterconsultas){		
		boolean permiteAvisar = permiteAvisar(fatPermiteAvisarInterconsultas);
		boolean avisado = avisadoRetorno(fatPermiteAvisarInterconsultas);		
		if (permiteAvisar && avisado){
			return true;
		}else{
			return false;			
		}
	}

	/**
	 * Método que verifica que se não permite avisar não mostra o botão avisado na grid.
	 * 
	 * @param fatPermiteAvisarInterconsultas
	 * @return boolean
	 */
	public boolean rederizarDesativar(MamInterconsultas fatPermiteAvisarInterconsultas){		
		boolean permiteAvisar = permiteAvisar(fatPermiteAvisarInterconsultas);		
		boolean avisado = avisadoRetorno(fatPermiteAvisarInterconsultas);		
		if (permiteAvisar && !avisado){
			return true;
		}else{
			return false;			
		}
	}
	
	/**
	 * Retorna uma lista de Domínio Ordenação.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> obterListaOrdenacao() {		
		listaOrdennacao.clear();
		
		for (DominioOrdenacaoInterconsultas dominioOrdenacao :  DominioOrdenacaoInterconsultas.values()) {
			SelectItem item = new SelectItem();
			item.setLabel(dominioOrdenacao.getDescricao());
			item.setValue(dominioOrdenacao);
			listaOrdennacao.add(item);
		}		 
		SelectItem setor = new SelectItem();
		setor.setLabel(labelSetor);
		setor.setValue("S");
		listaOrdennacao.add(setor);		
		return listaOrdennacao;
	}

	/**
	 * Retorna uma lista de Domínio Situação.
	 * 
	 * @return List<SelectItem>
	 */
	public List<SelectItem> obterListaSituacao() {		
		listaSituacao.clear();
		for (DominioSituacaoInterconsultasPesquisa dominioSituacao :  DominioSituacaoInterconsultasPesquisa.values()) {
			SelectItem item = new SelectItem();
			item.setLabel(dominioSituacao.getDescricao());
			item.setValue(dominioSituacao);
			if (!dominioSituacao.equals(DominioSituacaoInterconsultasPesquisa.O)){
				listaSituacao.add(item);				
			}
		}		
		return listaSituacao;
	}
	
	/**
	 * Método que ao clicar no botão detalhar cadastro, redireciona para a tela cadastro paciente.
	 * 
	 * @return String
	 */
	public String detalharCadastro() {		
		if (parametroSelecionado != null && parametroSelecionado.getPaciente() != null){
			cadastrarPacienteController.setPacCodigo(parametroSelecionado.getPaciente().getCodigo());
		}		
		cadastrarPacienteController.setGoingTo(null);
		cadastrarPacienteController.setIdLeito(null);
		cadastrarPacienteController.setQuartoNumero(null);
		cadastrarPacienteController.setSeqUnidadeFuncional(null);
		cadastrarPacienteController.setSeqAtendimentoUrgencia(null);		
		cadastrarPacienteController.setEdicaoInternacao(false);
		cadastrarPacienteController.prepararEdicaoPaciente(parametroSelecionado.getPaciente(), null);		
		cadastrarPacienteController.setCameFrom(PAGE_GESTAO_INTERCONSULTAS);		
		return CADASTRO_PACIENTE;
	}

	/**
	 * Método que ao clicar no botão Marcar Consulta, redireciona para a tela disponibilidade horarios.
	 * 
	 * @return String
	 */
	public String marcarConsulta() {		
		disponibilidadeHorariosPaginatorController.setEspecialidade(parametroSelecionado.getEspecialidadeAdm());
		disponibilidadeHorariosPaginatorController.setDisponibilidade(false);
		disponibilidadeHorariosPaginatorController.setCameFrom(PAGE_GESTAO_INTERCONSULTAS);
		try {
			AacCondicaoAtendimento condicaoAtendimento = new AacCondicaoAtendimento();
			condicaoAtendimento.setSeq(Short.valueOf(parametroFacade.obterAghParametro(AghuParametrosEnum.P_CAA_INTERCONSULTA).getVlrNumerico().toString()));
			disponibilidadeHorariosPaginatorController.setCondicao(condicaoAtendimento);
			disponibilidadeHorariosPaginatorController.pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return DISPONIBILIDADE_HORARIOS;
	}
	
	/**
	 * Pesquisas SuggestionBox.
	 * 
	 * @param parametro
	 * @return List<AghEspecialidades>
	 */
	
	public List<AghEspecialidades> pesquisarPorSiglaOuNomeEspecialidade(String parametro) {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarPorSiglaOuNomeGestaoInterconsultas(parametro),pesquisarPorSiglaOuNomeEspecialidadeCount(parametro));
	}

	/**
	 * Pesquisas SuggestionBox Count.
	 *  
	 * @param parametro
	 * @return Long
	 */
	public Long pesquisarPorSiglaOuNomeEspecialidadeCount(String parametro) {
		return this.ambulatorioFacade.pesquisarPorSiglaOuNomeGestaoInterconsultasCount(parametro);
	}
	
	/**
	 *  Ao marcar a opção 'Marcada' desabilita os campos de data.
	 */
	public void desabilitarCamposDatas() {
		if (filtro.getSituacao() !=null && filtro.getSituacao().equals(DominioSituacaoInterconsultasPesquisa.M)){
			desabilitarDatas = true;
			dataInicial = null;
			dataFinal = null;
			suggestionBoxSetor = false;
		}else{
			desabilitarDatas = false;
			suggestionBoxSetor = true;
		}
	}

	/**
	 * Ao marcar a opção 'Aviso Consultoria' desabilita o campo situacao e o item 'Marcada' fica selecionado.
	 */
	public void desabilitarCampoSituacao() {
		if (consultoriaCombo.equals(DominioSimNao.S)){
			desabilitarSituacao = true;
			desabilitarDatas = true;
			filtro.setSituacao(DominioSituacaoInterconsultasPesquisa.M);
		}else{
			desabilitarSituacao = false;
			desabilitarDatas = false;
			suggestionBoxSetor = true;
			filtro.setSituacao(DominioSituacaoInterconsultasPesquisa.P);
		}
	}
	
	/**
	 * Carrega o parâmetro para setar o label Setor.
	 */
	private void carregarParametros(){
		try {
			turno  = ambulatorioFacade.defineTurnoAtual();
			this.labelSetor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			this.ativo = Boolean.TRUE;
			if (this.labelSetor==null){
				this.labelSetor="Setor";
			}
			
			this.titleSetor = WebUtil.initLocalizedMessage("SETOR_INTERCONSULTA", null, this.labelSetor);
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método para sugestionBox de Setor
	 * @param pesquisa O que foi digitado no sugestionBox que será utilizado para consulta
	 * @return Lista de setores de acordo com valor digitado
	 */
	public List<AghUnidadesFuncionais> obterSetor(String pesquisa){
		return this.ambulatorioFacade.obterSetorPorSiglaDescricaoECaracteristica(pesquisa);
	}

	/**
	 * Trunca nome da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}	
	
	/**
	 * Método que desabilita os botoes da lateral da grid.
	 */
	public void desabilitarBotoeslaterais(){
		desabilitarBotoes = false;
		//setParametroSelecionado(parametroSelecionado);
	}
	/**
     * Carrega os metodos retorno e interConsultaMarcada
     */
     public void interConsultaMarcadaRetorno() {
           retorno();
           interConsultaMarcada();
     }
     //Preenchendo o Campo de Retorno
     private void retorno() {
    	   this.strRetorno = "";
           StringBuilder retornoBuilder = new StringBuilder();
           
           if(parametroSelecionado != null && parametroSelecionado.getConsultaRetorno() != null) {
                  if(parametroSelecionado.getConsultaRetorno().getDtConsulta() != null){
                         retornoBuilder.append(DateUtil.dataToString(parametroSelecionado.getConsultaRetorno().getDtConsulta(), "dd/MM/yyyy HH:mm"));
                         retornoBuilder.append(", ");
                  }
                  
                  if(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta() != null) {
                         
                	  concatenarDadosGradeAgendamentoConsultaRetorno(retornoBuilder);
                  }
           
                  if(retornoBuilder.length() - 1 == ','){
                         this.strInterconsultaMarcada = retornoBuilder.substring(0, retornoBuilder.length() -1);
                  }else{
                         this.strRetorno = retornoBuilder.toString();
                  }
           }
     }
     /**
      * 
      * @param retornoBuilder
      */
	private void concatenarDadosGradeAgendamentoConsultaRetorno(StringBuilder retornoBuilder) {
		if(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getEspecialidade() != null 
		              && parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade() != null){
		        retornoBuilder.append(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade());
		        retornoBuilder.append(", ");
		 }
		 if(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getEquipe() != null 
		              && parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getEquipe().getNome() != null){
		        retornoBuilder.append(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getEquipe().getNome());
		        retornoBuilder.append(", ");
		 }
		 if(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getUnidadeFuncional() != null 
		              && parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getUnidadeFuncional().getSigla() != null){
		        retornoBuilder.append("Zona ");
		        retornoBuilder.append(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getUnidadeFuncional().getSigla());
		        retornoBuilder.append(", ");
		 }
		 if(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getAacUnidFuncionalSala() != null 
		              && parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala() != null){
		        retornoBuilder.append("Sala ");
		        retornoBuilder.append(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().
		                                                getAacUnidFuncionalSala().getId().getSala().toString());
		        retornoBuilder.append(", ");
		 }
		 if(parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getProfServidor() != null  
		              && parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getProfServidor().getPessoaFisica() != null 
		              && parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getProfServidor().getPessoaFisica().getNome() != null){
		        String nomeAbreviado = blocoCirurgicoPortalPlanejamentoFacade.obterNomeIntermediarioPacienteAbreviado(
		                     parametroSelecionado.getConsultaRetorno().getGradeAgendamenConsulta().getProfServidor().getPessoaFisica().getNome()); 
		        retornoBuilder.append(nomeAbreviado);
		 }
	}
     //Preenchendo o Campo de Interconsulta Marcada
     private void interConsultaMarcada(){
    	   this.strInterconsultaMarcada = "";
           StringBuilder interconsultaMarcadaBuilder = new StringBuilder();
           
           if(parametroSelecionado != null && parametroSelecionado.getConsultaMarcada() != null) {
                  if(parametroSelecionado.getConsultaMarcada().getDtConsulta() != null){
                         interconsultaMarcadaBuilder.append(DateUtil.dataToString(parametroSelecionado.getConsultaMarcada().getDtConsulta(), "dd/MM/yyyy HH:mm"));
                         interconsultaMarcadaBuilder.append(", ");
                  }
                  if(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta() != null) {
                         concatenarDadosGradeAgendamentoConsultaMarcada(interconsultaMarcadaBuilder);
                  }
           
                  if(interconsultaMarcadaBuilder.length() - 1 == ','){
                         this.strRetorno = interconsultaMarcadaBuilder.substring(0, interconsultaMarcadaBuilder.length() -1);
                  }else{
                         this.strInterconsultaMarcada = interconsultaMarcadaBuilder.toString();
                  }
           }
           
     }
     
    /**
     * 
     * @param interconsultaMarcadaBuilder
     */
	private void concatenarDadosGradeAgendamentoConsultaMarcada(
			StringBuilder interconsultaMarcadaBuilder) {
		if(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getEspecialidade() != null 
		              && parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade() != null){
		        interconsultaMarcadaBuilder.append(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade());
		        interconsultaMarcadaBuilder.append(", ");
		 }
		 if(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getEquipe() != null 
		              && parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getEquipe().getNome() != null){
		        interconsultaMarcadaBuilder.append(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getEquipe().getNome());
		        interconsultaMarcadaBuilder.append(", ");
		 }
		 if(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getUnidadeFuncional() != null 
		              && parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getUnidadeFuncional().getSigla() != null){
		        interconsultaMarcadaBuilder.append("Zona ");
		        interconsultaMarcadaBuilder.append(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getUnidadeFuncional().getSigla());
		        interconsultaMarcadaBuilder.append(", ");
		 }
		 if(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getAacUnidFuncionalSala() != null 
		              && parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala() != null){
		        interconsultaMarcadaBuilder.append("Sala ");
		        interconsultaMarcadaBuilder.append(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().
		                                                getAacUnidFuncionalSala().getId().getSala().toString());
		        interconsultaMarcadaBuilder.append(", ");
		 }
		 if(parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getProfServidor() != null  
		              && parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getProfServidor().getPessoaFisica() != null 
		              && parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getProfServidor().getPessoaFisica().getNome() != null){
		        String nomeAbreviado = blocoCirurgicoPortalPlanejamentoFacade.obterNomeIntermediarioPacienteAbreviado(
		                     parametroSelecionado.getConsultaMarcada().getGradeAgendamenConsulta().getProfServidor().getPessoaFisica().getNome()); 
		        interconsultaMarcadaBuilder.append(nomeAbreviado);
		 }
	}
     /**
      * Método que concatena os campos de auditoria
      */
     public void truncarCamposAuditoria(){
 		digitadoPor = "";
 		marcadoPor = "";
 		avisadoPor = "";    
 	
 	//Concatena o campo digitado por.	
    	 if (parametroSelecionado.getServidor() != null) {
	  		if (parametroSelecionado.getServidor().getId() != null
	  				&& parametroSelecionado.getServidor().getPessoaFisica() != null 
	  				&& parametroSelecionado.getServidor().getPessoaFisica().getNome() != null){
	  			
	  			digitadoPor = parametroSelecionado.getServidor().getId().getVinCodigo() + HIFEN + 
	  			parametroSelecionado.getServidor().getId().getMatricula() + HIFEN + 
	  					parametroSelecionado.getServidor().getPessoaFisica().getNome();
	  		}  		 
  	     }	 
    	 
    	 //Concatena o campo marcado por.
    	 if (parametroSelecionado.getServidorMarcada() != null) {
    		 if (parametroSelecionado.getServidorMarcada().getId() != null
    				 && parametroSelecionado.getServidorMarcada().getPessoaFisica() != null 
    				 && parametroSelecionado.getServidorMarcada().getPessoaFisica().getNome() != null){
    			 
    			marcadoPor = parametroSelecionado.getServidorMarcada().getId().getVinCodigo() + HIFEN + 
    					 parametroSelecionado.getServidorMarcada().getId().getMatricula() + HIFEN + 
    					 parametroSelecionado.getServidorMarcada().getPessoaFisica().getNome();
    		 }		 
    	 }	
    	 
    	 //Concatena o campo marcado por.
    	 if (parametroSelecionado.getServidorAvisada() != null) {
    		 if (parametroSelecionado.getServidorAvisada().getId() != null
    				 && parametroSelecionado.getServidorAvisada().getPessoaFisica() != null 
    				 && parametroSelecionado.getServidorAvisada().getPessoaFisica().getNome() != null){
    			 
    			avisadoPor = parametroSelecionado.getServidorAvisada().getId().getVinCodigo() + HIFEN + 
    					 parametroSelecionado.getServidorAvisada().getId().getMatricula() + HIFEN + 
    					 parametroSelecionado.getServidorAvisada().getPessoaFisica().getNome();
    		 }    		 
    	 }	  	     	 
     }
     
     
     public String gravar() {
 		if (parametroSelecionado != null) {
 			this.ambulatorioFacade.salvarInterconsulta(parametroSelecionado);
 			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_GRAVAR_INTERCONSULTA");
 		} else {
 			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_GRAVAR_INTERCONSULTA");
     	}
 		return PAGE_GESTAO_INTERCONSULTAS;
 	}
     
	public DynamicDataModel<MamInterconsultas> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamInterconsultas> dataModel) {
		this.dataModel = dataModel;
	}

	public MamInterconsultas getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MamInterconsultas parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public MamInterconsultas getFiltro() {
		return filtro;
	}

	public void setFiltro(MamInterconsultas filtro) {
		this.filtro = filtro;  
	}

	public boolean isConsultoria() {
		return consultoria;
	}

	public void setConsultoria(boolean consultoria) {
		this.consultoria = consultoria;
	}

	public boolean isExcluidos() {
		return excluidos;
	}

	public void setExcluidos(boolean excluidos) {
		this.excluidos = excluidos;
	}

	public String getOrdenar() {
		return ordenar;
	}

	public void setOrdenar(String ordenar) {
		this.ordenar = ordenar;
	}

	public boolean isDesabilitarDatas() {
		return desabilitarDatas;
	}

	public void setDesabilitarDatas(boolean desabilitarDatas) {
		this.desabilitarDatas = desabilitarDatas;
	}

	public boolean isDesabilitarSituacao() {
		return desabilitarSituacao;
	}

	public void setDesabilitarSituacao(boolean desabilitarSituacao) {
		this.desabilitarSituacao = desabilitarSituacao;
	}

	public String getLabelSetor() {
		return labelSetor;
	}

	public void setLabelSetor(String labelSetor) {
		this.labelSetor = labelSetor;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getTitleSetor() {
		return titleSetor;
	}

	public void setTitleSetor(String titleSetor) {
		this.titleSetor = titleSetor;
	}

	public DominioTurno getTurno() {
		return turno;
	}

	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}

	public AghUnidadesFuncionais getUnidadesFuncionais() {
		return unidadesFuncionais;
	}

	public void setUnidadesFuncionais(AghUnidadesFuncionais unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	public List<SelectItem> getListaOrdennacao() {
		return listaOrdennacao;
	}
	public void setListaOrdennacao(List<SelectItem> listaOrdennacao) {
		this.listaOrdennacao = listaOrdennacao;
	}
	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}
	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}
	public DominioSimNao getConsultoriaCombo() {
		return consultoriaCombo;
	}
	public void setConsultoriaCombo(DominioSimNao consultoriaCombo) {
		this.consultoriaCombo = consultoriaCombo;
	}
	public DominioSimNao getExcluidosCombo() {
		return excluidosCombo;
	}
	public void setExcluidosCombo(DominioSimNao excluidosCombo) {
		this.excluidosCombo = excluidosCombo;
	}
	public boolean isSuggestionBoxSetor() {
		return suggestionBoxSetor;
	}
	public void setSuggestionBoxSetor(boolean suggestionBoxSetor) {
		this.suggestionBoxSetor = suggestionBoxSetor;
	}
	public CadastrarPacienteController getCadastrarPacienteController() {
		return cadastrarPacienteController;
	}
	public void setCadastrarPacienteController(
			CadastrarPacienteController cadastrarPacienteController) {
		this.cadastrarPacienteController = cadastrarPacienteController;
	}
	public DisponibilidadeHorariosPaginatorController getDisponibilidadeHorariosPaginatorController() {
		return disponibilidadeHorariosPaginatorController;
	}
	public void setDisponibilidadeHorariosPaginatorController(
			DisponibilidadeHorariosPaginatorController disponibilidadeHorariosPaginatorController) {
		this.disponibilidadeHorariosPaginatorController = disponibilidadeHorariosPaginatorController;
	}
	public DominioSituacaoInterconsultasPesquisa getSituacaoCombo() {
		return SituacaoCombo;
	}
	public void setSituacaoCombo(DominioSituacaoInterconsultasPesquisa situacaoCombo) {
		SituacaoCombo = situacaoCombo;
	}
	public List<SelectItem> getListaSituacao() {
		return listaSituacao;
	}
	public void setListaSituacao(List<SelectItem> listaSituacao) {
		this.listaSituacao = listaSituacao;
	}
	public boolean isDesabilitarBotoes() {
		return desabilitarBotoes;
	}
	public void setDesabilitarBotoes(boolean desabilitarBotoes) {
		this.desabilitarBotoes = desabilitarBotoes;
	}
	public String getCameFrom() {
		return cameFrom;
	}
	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}
	public String getStrInterconsultaMarcada() {
		return strInterconsultaMarcada;
	}
	public void setStrInterconsultaMarcada(String strInterconsultaMarcada) {
		this.strInterconsultaMarcada = strInterconsultaMarcada;
	}
	public String getStrRetorno() {
		return strRetorno;
	}
	public void setStrRetorno(String strRetorno) {
		this.strRetorno = strRetorno;
	}
	public String getAvisadoPor() {
		return avisadoPor;
	}
	public void setAvisadoPor(String avisadoPor) {
		this.avisadoPor = avisadoPor;
	}
	public String getMarcadoPor() {
		return marcadoPor;
	}
	public void setMarcadoPor(String marcadoPor) {
		this.marcadoPor = marcadoPor;
	}
	public String getDigitadoPor() {
		return digitadoPor;
	}
	public void setDigitadoPor(String digitadoPor) {
		this.digitadoPor = digitadoPor;
	}
	public boolean isAparecerModal() {
		return aparecerModal;
	}
	public void setAparecerModal(boolean aparecerModal) {
		this.aparecerModal = aparecerModal;
	}
}