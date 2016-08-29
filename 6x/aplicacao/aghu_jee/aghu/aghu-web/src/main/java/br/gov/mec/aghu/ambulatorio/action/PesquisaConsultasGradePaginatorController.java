package br.gov.mec.aghu.ambulatorio.action;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasGradeVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PesquisaConsultasGradePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 745133657358657448L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroSistemaFacade parametroSistemaFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private PesquisaPacienteController pesquisaPacienteController;
	
	@Inject
	private CadastrarPacienteController cadastrarPacienteController;
	
	private ConsultasGradeVO selecionado;
	
	@Inject
	@Paginator
	private DynamicDataModel<ConsultasGradeVO> dataModel;
	
	private GradeAgendamentoVO gradeAgendamentoVO;
	
	private ConsultasGradeVO filtro;
	
	private String labelZona;
	
	private String voltaPara;
	
	private Boolean isAgendados = false;
	
	private Boolean isLimpou;

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		isLimpou = false;
	}

	/**
	 * Método chamado na iniciação da tela.
	 */
	public void iniciar() {
		
		if ((dataModel == null) || (dataModel != null && !dataModel.getPesquisaAtiva())) {
			AghParametros parametro = new AghParametros();
			parametro.setNome(AghuParametrosEnum.P_AGHU_LABEL_ZONA.toString());
			List<AghParametroVO> listaParametros = parametroSistemaFacade.pesquisaParametroList(0, 1, null, false, parametro);
			if (listaParametros != null && !listaParametros.isEmpty()) {
				labelZona = listaParametros.get(0).getVlrTexto();
			}
		}

		if(isAgendados && !isLimpou){
			pesquisar();
		}else if(!isAgendados){
			filtro = new ConsultasGradeVO();
			filtro.setDataInicial(new Date());
		}
	
	}
	
	@Override
	public Long recuperarCount() {
		filtro.setSeqGrade(gradeAgendamentoVO.getSeq());
		return ambulatorioFacade.contarPesquisarConsultasPorGrade(filtro);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ConsultasGradeVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		filtro.setSeqGrade(gradeAgendamentoVO.getSeq());
		return ambulatorioFacade.pesquisarConsultasPorGrade(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	/**
	 * Ação do botão pesquisar.
	 */
	public void pesquisar() {	

		this.dataModel.reiniciarPaginator();
		isLimpou = false;
	}
	
	/**
	 * Ação do botão atualizar paciente.
	 * @return String de navegação.
	 * @throws ApplicationBusinessException 
	 */
	public String atualizarPaciente(Integer codigoPaciente, String nomePaciente, Integer numeroConsulta) {		
		try {
			
			String retorno = pacienteFacade.verificarAtualizacaoPaciente(nomePaciente, numeroConsulta);
			
			if (retorno == null) {
				apresentarMsgNegocio("SUCESSO_ATUALIZACAO_PACIENTE");
				return null;			
			} else if (retorno.equals("NENHUM_PACIENTE_ENCONTRADO") || retorno.equals("UM_PACIENTE_ENCONTRADO")) {
				AipPacientes paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codigoPaciente);
				pesquisaPacienteController.setCameFrom("ambulatorio-pesquisarConsultasGrade");
				pesquisaPacienteController.setPacienteSelecionado(paciente);
				cadastrarPacienteController.setNumeroConsulta(numeroConsulta);
				return pesquisaPacienteController.detalhar();
			} else if (retorno.equals("VARIOS_PACIENTES_ENCONTRADOS")) {
				AipPacientes paciente = new AipPacientes();
				paciente.setNome(nomePaciente);
				pesquisaPacienteController.setCameFrom("ambulatorio-pesquisarConsultasGrade");
				pesquisaPacienteController.setAipPaciente(paciente);
				pesquisaPacienteController.setNumeroConsulta(numeroConsulta);
				pesquisaPacienteController.pesquisarFonetica();
				return "paciente-pesquisaPaciente";
			}
			
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;		
	}
	
	/**
	 * Ação do botão limpar.
	 */
	public void limpar() {	
		isLimpou = true;
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		
		while (componentes.hasNext()) {			
			this.limparValoresSubmetidos(componentes.next());
		}
		
		this.dataModel.limparPesquisa();
		
		if(isAgendados){
			filtro = new ConsultasGradeVO();
		}
		
		iniciar();		
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
	
	/**
	 * Obtém hint de situação da grid.
	 * @param itemGrid
	 * @return Hint
	 */
	public String obterHintSituacaoGrid(ConsultasGradeVO itemGrid) {
		
		String hint = "";
		
		if (itemGrid.getSeqRetorno() != null) {
			hint = hint.concat("Retorno: ").concat(itemGrid.getSeqRetorno().toString()).concat(" - ").concat(itemGrid.getDescricaoRetorno()).concat("\n");
		}
		if (itemGrid.getSeqMotivo() != null) {
			hint = hint.concat("Motivo: ").concat(itemGrid.getSeqMotivo().toString()).concat(" - ").concat(itemGrid.getDescricaoMotivo());
		}
		
		return hint;		
	}
	
	/**
	 * Obtém hint de consulta da grid.
	 * @param itemGrid
	 * @return Hint
	 */
	public String obterHintConsultaGrid(ConsultasGradeVO itemGrid) {
		
		String hint = "";
		
		if (itemGrid.getDescricaoPagador() != null) {
			hint = hint.concat("Pagador: ").concat(itemGrid.getDescricaoPagador()).concat("\n");
		}
		if (itemGrid.getDescricaoAutorizacao() != null) {
			hint = hint.concat("Autorização: ").concat(itemGrid.getDescricaoAutorizacao());
		}
		
		return hint;		
	}
	
	/**
	 * Obtém hint de alterado em da grid.
	 * @param nome
	 * @return hint
	 */
	public String obterHintAlteradoEmGrid(String nome) {
		
		String hint = "";
		
		if (nome != null) {
			hint = hint.concat("Por: ").concat(nome);
		}
		
		return hint;		
	}
	
	/**
	 * Obtém data formatada com hora.
	 * @param data
	 * @return Data formatada
	 */
	public String obterDataHoraFormatada(Date data) {
		return DateUtil.obterDataFormatada(data, "dd/MM/yyyy HH:mm");
	}
	
	/**
	 * Obtém data formatada
	 * @param data
	 * @return Data formatada
	 */
	public String obterDataFormatada(Date data) {
		return DateUtil.obterDataFormatada(data, "dd/MM/yyyy");
	}
	
	/**
	 * Obtém prontuário formatado.
	 * @param prontuario
	 * @return Prontuário formatado
	 */
	public String obterProntuarioFormatado(Integer prontuario) {
		
		String prontuarioFormatado = "";
		
		if (prontuario != null) {
			prontuarioFormatado = CoreUtil.formataProntuario(prontuario);
		}
		
		return prontuarioFormatado;
	}
	
	/**
	 * Obtém descrição truncada.
	 * @param item
	 * @param tamanhoMaximo
	 * @return Descrição truncada
	 */
	public String obterDescricaoTruncada(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}
	
	/**
	 * Ação do botão voltar
	 * @return String de navegação.
	 */
	public String voltar() {
		this.limpar();
		isLimpou = false;
		return voltaPara;
	}
	
	/**
	 * Redireciona para tela de pesquisar consultas por grade.
	 * @return String de navegação.
	 */
	public String redirecionarPesquisaConsultasGrade() {		
		return "ambulatorio-pesquisarConsultasGrade";		
	}
	
	/**
	 * Redireciona para tela de histórico de consultas.
	 * @return String de navegação.
	 */
	public String redirecionarHistoricoConsultas() {		
		return "ambulatorio-listarHistoricoConsultas";		
	}
	
	/**
	 * Verifica se botão atualizar paciente deve ser renderizado. 
	 * @param itemGrid
	 * @return Boolean
	 */
	public Boolean renderizarAtualizarPaciente(ConsultasGradeVO itemGrid) {
		if ((itemGrid.getStcSituacaoConsulta() != null && itemGrid.getStcSituacaoConsulta().equals("M"))
				&& (itemGrid.getExigeProntuario() != null && itemGrid.getExigeProntuario().equals(Boolean.FALSE))
				&& itemGrid.getProntuario() == null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}	
	
	////
	// GETs e SETs
	////
	
	public GradeAgendamentoVO getGradeAgendamentoVO() {
		return gradeAgendamentoVO;
	}

	public void setGradeAgendamentoVO(GradeAgendamentoVO gradeAgendamentoVO) {
		this.gradeAgendamentoVO = gradeAgendamentoVO;
	}

	public ConsultasGradeVO getFiltro() {
		return filtro;
	}

	public void setFiltro(ConsultasGradeVO filtro) {
		this.filtro = filtro;
	}

	public DynamicDataModel<ConsultasGradeVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ConsultasGradeVO> dataModel) {
		this.dataModel = dataModel;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public String getVoltaPara() {
		return voltaPara;
	}

	public void setVoltaPara(String voltaPara) {
		this.voltaPara = voltaPara;
	}

	public Boolean getIsAgendados() {
		return isAgendados;
	}

	public void setIsAgendados(Boolean isAgendados) {
		this.isAgendados = isAgendados;
	}

	public Boolean getIsLimpou() {
		return isLimpou;
	}

	public void setIsLimpou(Boolean isLimpou) {
		this.isLimpou = isLimpou;
	}

	public ConsultasGradeVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ConsultasGradeVO selecionado) {
		this.selecionado = selecionado;
	}

}
