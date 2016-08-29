package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class LiberarConsultasPaginatorController extends ActionController implements ActionPaginator {


	private static final Log LOG = LogFactory.getLog(LiberarConsultasPaginatorController.class);

	private static final long serialVersionUID = 1482877764725868674L;
	
	
	public enum LiberarConsultasPaginatorControllerExceptionCode implements BusinessExceptionCode {
		AIP_PACIENTE_NAO_ENCONTRADO
	}

	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	//@EJB
	//private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AacConsultas> dataModel;

	private Integer prontuarioPaciente;

	private Integer codigoPaciente;

	private String nomePaciente;

	private Integer grade;

	private AghEspecialidades especialidade;

	private Date dataConsulta;

	private Integer numeroConsulta;

	private String situacaoConsulta;
	
	private Integer nroConsulta;
	
	private AipPacientes paciente;
	
	private String labelZonaSala;
	
	private boolean abriuPesquisaPaciente = false;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFoneticaVO;
		
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		init();
	}
	
	public void init() {
		try {
			this.situacaoConsulta = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SIT_CONS_MARCADA).getVlrTexto();
			
			this.labelZonaSala = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto()
					+ "/" + this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
			
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_CARREGAR_SIT_CONS_MARCADA");
		}
	}
	
	public String pesquisaFonetica() {
		abriuPesquisaPaciente = true;
		return PESQUISA_FONETICA;
	}
	
	public void iniciar() {
	 

	 

		if (abriuPesquisaPaciente) {
			CodPacienteFoneticaVO pacienteSelecionado = this.codPacienteFoneticaVO.get();
			if (pacienteSelecionado != null && pacienteSelecionado.getCodigo() > 0) {
				AipPacientes paciente = this.pacienteFacade.obterPacientePorCodigo(pacienteSelecionado.getCodigo());

				this.codigoPaciente = paciente.getCodigo();
				this.prontuarioPaciente = paciente.getProntuario();
				this.nomePaciente = paciente.getNome();
			}			
		}
	
	}
	
	
	@Override
	public Long recuperarCount() {
		verificarPacienteConsultaSelecionado();
		return this.ambulatorioFacade.listarConsultasParaLiberarCount(this.prontuarioPaciente, this.codigoPaciente, this.grade,
				this.especialidade, this.dataConsulta, this.situacaoConsulta, this.nroConsulta);
	}

	@Override
	public List<AacConsultas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		verificarPacienteConsultaSelecionado();
		return this.ambulatorioFacade.listarConsultasParaLiberar(firstResult, maxResult, orderProperty, asc, this.prontuarioPaciente,
				this.codigoPaciente, this.grade, this.especialidade, this.dataConsulta, this.situacaoConsulta, this.nroConsulta);
	}

	public Long pesquisarEspecialidadesAgendasCount(String filtro) {
		return this.aghuFacade.pesquisarEspecialidadesAgendasCount((String) filtro);
	}

	public List<AghEspecialidades> pesquisarEspecialidadesAgendas(String filtro) {
		return  this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadesAgendas((String) filtro),pesquisarEspecialidadesAgendasCount(filtro));
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		limparPaciente();
		
		this.grade = null;
		this.especialidade = null;
		this.dataConsulta = null;
		this.numeroConsulta = null;
		this.nroConsulta=null;
		this.dataModel.limparPesquisa();
		abriuPesquisaPaciente = false;
	}
	
	public void limparPaciente() {
		this.paciente = null;
		this.codigoPaciente = null;
		this.nomePaciente = null;
		this.prontuarioPaciente = null;
	}
	
	// #52563
	public void verificarReconsultas() {
		List<AacConsultas> reconsultas = this.ambulatorioFacade.verificarConsultaPossuiReconsultasVinculadas(this.numeroConsulta);
		if(reconsultas!=null && !reconsultas.isEmpty()) {
			openDialog("modalPossuiReconsultasWG");
		}
		else {
			openDialog("modalConfirmacaoLiberacaoWG");
		}
	}

	public void liberarConsulta(Boolean possuiReconsulta) {
		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e1) {
				LOG.error("Exceção capturada:", e1);
			}
			
			//RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			this.ambulatorioFacade.liberarConsulta(this.numeroConsulta, nomeMicrocomputador, possuiReconsulta);

			apresentarMsgNegocio(Severity.INFO, "CONSULTA_ESTORNADA_SUCESSO");

			// Recarrega as informações na tela
			this.dataModel.reiniciarPaginator();
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
		catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_ESTORNAR_CONSULTA");
		}
	}

	public void pesquisar() {
		if (this.prontuarioPaciente == null && this.codigoPaciente == null && this.grade == null && this.especialidade == null
				&& this.dataConsulta == null && nroConsulta == null) {
			apresentarMsgNegocio(Severity.ERROR, "INFORME_CRITERIO_PESQUISA");
			this.dataModel.limparPesquisa();
		} else if ((this.grade != null && (this.prontuarioPaciente == null && this.codigoPaciente == null
				&& this.especialidade == null && this.dataConsulta == null && nroConsulta == null))
				|| (this.especialidade != null && (this.prontuarioPaciente == null && this.codigoPaciente == null
						&& this.grade == null && this.dataConsulta == null && nroConsulta == null))
				|| (this.dataConsulta != null && (this.prontuarioPaciente == null && this.codigoPaciente == null
						&& this.especialidade == null && this.grade == null && nroConsulta == null))) {
			apresentarMsgNegocio(Severity.ERROR, "PESQUISA_GRADE_ESPECIALIDADE_DATA_CONSULTA_INFORME_OUTRO_CRITERIO");
			this.dataModel.limparPesquisa();
		} else {
			this.dataModel.reiniciarPaginator();
		}
		
		verificarPacienteConsultaSelecionado();
	}
	
	/*
	 * Metodo criado devido ao enfileiramento de requisições no
	 * momento da pesquisa e com prontuario ou codigoPac informados.
	 */
	private void verificarPacienteConsultaSelecionado() {
		if (this.paciente != null && 
				(this.codigoPaciente == null || this.prontuarioPaciente == null)) {
			this.codigoPaciente = this.paciente.getCodigo();
			this.prontuarioPaciente = this.paciente.getProntuario();
		}
	}
	
	public void selecionarPacienteConsultaEdicao() {
		if (this.paciente != null || this.codigoPaciente != null
				|| this.prontuarioPaciente != null) {
			if (this.codigoPaciente != null) {
				this.paciente = pacienteFacade
						.obterPacientePorCodigo(this.codigoPaciente);
			} else if (this.prontuarioPaciente != null) {
				this.paciente = pacienteFacade
						.obterPacientePorProntuario(prontuarioPaciente);
			}
		} else if (this.paciente == null) {
			this.apresentarMsgNegocio(Severity.ERROR, LiberarConsultasPaginatorControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());
		}
		if (this.paciente != null) {
			this.codigoPaciente = this.paciente.getCodigo();
			this.prontuarioPaciente = this.paciente.getProntuario();
			this.nomePaciente = this.paciente.getNome();
		}
	}

	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getSituacaoConsulta() {
		return situacaoConsulta;
	}


	public void setSituacaoConsulta(String situacaoConsulta) {
		this.situacaoConsulta = situacaoConsulta;
	}

	public Integer getNroConsulta() {
		return nroConsulta;
	}

	public void setNroConsulta(Integer nroConsulta) {
		this.nroConsulta = nroConsulta;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getLabelZonaSala() {
		return labelZonaSala;
	}

	public void setLabelZonaSala(String labelZonaSala) {
		this.labelZonaSala = labelZonaSala;
	}

	 


	public DynamicDataModel<AacConsultas> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacConsultas> dataModel) {
	 this.dataModel = dataModel;
	}
}
