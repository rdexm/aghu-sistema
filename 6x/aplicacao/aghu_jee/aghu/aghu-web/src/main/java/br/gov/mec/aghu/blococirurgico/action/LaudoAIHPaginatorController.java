package br.gov.mec.aghu.blococirurgico.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MamLaudoAihVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class LaudoAIHPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.loadParameters();
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MamLaudoAihVO> dataModel;

	
	/**
	 * 
	 */	
	private static final long serialVersionUID = 4038614901761601452L;
	
	/*
	 * Injeções
	 */
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private PesquisaPacienteController pesquisaPacienteController;
	
	@Inject
	private LaudoAIHController laudoAIHController;
	
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente"; 
	private static final String PAGE_BLOCO_LAUDO_AIH = "blococirurgico-laudoAIH"; 
	private final String PAGE_CADASTRO_LAUDO_AIH = "cadastroLaudoAih";
	private final String PAGE_VISUALIZAR_LAUDO_AIH = "blococirurgico-relatorio-relatorioLaudoAIH";

	/*
	 * Campos do filtro
	 */
	private AipPacientes paciente;
	private Integer prontuario;
	private Integer pacCodigoFonetica;
	private Integer pacCodigo;
	private String nomePaciente;
	private String voltarPara;
	private Boolean voltarParaEmergencia = Boolean.FALSE;
	private boolean iniciaPesquisando = true;
	private Long seq;
	private RapServidores servidor;
	private MamLaudoAihVO mamLaudoAihVO;

	protected enum LaudoAIHPaginatorControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_SALVAR_LAUDO_AIH;
	}
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {

		if(this.pacCodigoFonetica != null) {
			this.setPaciente(this.pacienteFacade.obterPaciente(this.pacCodigoFonetica));
			this.prontuario = this.paciente.getProntuario();
			if(iniciaPesquisando){
				this.pesquisar();
			}
		} else {
			if (this.pacCodigo != null) {
				this.setPaciente(this.pacienteFacade.obterPaciente(this.pacCodigo));
				this.prontuario = this.paciente.getProntuario();
				if (iniciaPesquisando) {
					this.pesquisar();
				}
			}
		}
		
		
	
	}
	
	private void loadParameters() {
		String strPacCodigo = this.getRequestParameter("pacCodigo");
		if (strPacCodigo != null) {
			this.pacCodigo = Integer.valueOf(strPacCodigo);
		}
		
		String iniciaPesquisando = this.getRequestParameter("iniciaPesquisando");
		if (iniciaPesquisando != null) {
			this.iniciaPesquisando = Boolean.valueOf(iniciaPesquisando);
		}
		
		String strVoltarPara = this.getRequestParameter("voltarParaEmergencia");
		if (strVoltarPara != null) {
			this.setVoltarParaEmergencia(Boolean.valueOf(strVoltarPara));
		}
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		// Limpa filtro
		this.paciente = null;
		this.prontuario = null;
		this.pacCodigoFonetica = null;
		this.pacCodigo = null;

		this.dataModel.limparPesquisa();
	}

	
	public String voltarPara() {
		return this.voltarPara;
	}

	@Override
	public List<MamLaudoAihVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoFacade.listarLaudosAIH(firstResult, maxResult, orderProperty, asc, paciente);
	}

	@Override
	public Long recuperarCount() {
		return this.blocoCirurgicoFacade.listarLaudosAIHCount(paciente);
	}
	

	public String obterProntuarioFormatado(Object valor) {
		return CoreUtil.formataProntuario(valor);
	}
	
	/**
	 * Testa se o paciente é null <br>
	 * Caso venha null, primeiro pesquisa pelo prontuário<br>
	 * Depois pesquisa pelo paciente.
	 * @return
	 */
	public String pesquisarPacienteProntuario(){
			
		if(paciente != null){
			pacCodigo = paciente.getCodigo();
			pacCodigoFonetica = paciente.getCodigo();
			pesquisar();
		} else if (prontuario != null){
			paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
			pacCodigo = paciente.getCodigo();
			pacCodigoFonetica = paciente.getCodigo();
			pesquisar();
		} else {
			apresentarMsgNegocio("pesqPaciente", Severity.ERROR,
					"CAMPO_OBRIGATORIO","Código Paciente");
		}

		return null;
	}
	
	/**
	 * Carrega a tela do Novo Laudo de Aih
	 * 
	 * @return
	 */
	public String redirecionarNovoLaudoAih( ) {

		if (pacCodigo != null) {
			this.laudoAIHController.setPacCodigo(pacCodigo);
		} else if (pacCodigoFonetica != null) {
			this.laudoAIHController.setPacCodigo(pacCodigoFonetica);
		}

		return PAGE_CADASTRO_LAUDO_AIH;
	}
	
	/**
	 * Carrega a tela de ajuste
	 * 
	 * @return
	 */
	public String ajustarLaudoIncompleto() {
		return PAGE_CADASTRO_LAUDO_AIH;
	}
	
	/**
	 * Carrega a tela de visualização
	 * 
	 * @return	 *  
	 */
	public String visualizarLaudoAIH(Long seq) {
		this.seq = seq;
		
		try {

			MamLaudoAih laudoParaVisualizacao = this.blocoCirurgicoFacade.imprimirLaudoAih(seq);
			
			this.seq = laudoParaVisualizacao.getSeq();
			if (laudoParaVisualizacao.getServidor() != null) {
				this.servidor = laudoParaVisualizacao.getServidor();
			} else {
				this.servidor = this.obterServidorLogado();
			}

			return PAGE_VISUALIZAR_LAUDO_AIH;
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(LaudoAIHPaginatorControllerExceptionCode.MENSAGEM_ERRO_SALVAR_LAUDO_AIH));
			return null;
		}


	}
	

	public String redirecionarPesquisaFonetica() {
		this.pesquisaPacienteController.setCameFrom(PAGE_BLOCO_LAUDO_AIH);
		return PESQUISA_FONETICA;
	}
	
	/*
	 * Getters e Setters
	 */

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
		if (paciente != null && StringUtils.isNotBlank(paciente.getNome())) {
			this.nomePaciente = paciente.getNome();
			this.pacCodigoFonetica = paciente.getCodigo();
			this.prontuario = paciente.getProntuario();
		} else {
			this.nomePaciente = null;
			this.pacCodigoFonetica = null;
			this.prontuario = null;
		}
	}
	
	private RapServidores obterServidorLogado()   {
		RapServidores servidor = null;

		try {
			servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
		} catch (ApplicationBusinessException esr) {
			apresentarExcecaoNegocio(esr);
		}

		return servidor;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}
	
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getIniciaPesquisando() {
		return iniciaPesquisando;
	}

	public void setIniciaPesquisando(Boolean iniciaPesquisando) {
		this.iniciaPesquisando = iniciaPesquisando;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public Long getSeq() {
		return seq;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public void setIniciaPesquisando(boolean iniciaPesquisando) {
		this.iniciaPesquisando = iniciaPesquisando;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public void setMamLaudoAihVO(MamLaudoAihVO mamLaudoAihVO) {
		this.mamLaudoAihVO = mamLaudoAihVO;
	}

	public MamLaudoAihVO getMamLaudoAihVO() {
		return mamLaudoAihVO;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setRegistroColaboradorFacade(IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	 


	public DynamicDataModel<MamLaudoAihVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamLaudoAihVO> dataModel) {
	 this.dataModel = dataModel;
	}

	public LaudoAIHController getLaudoAIHController() {
		return laudoAIHController;
	}

	public void setLaudoAIHController(LaudoAIHController laudoAIHController) {
		this.laudoAIHController = laudoAIHController;
	}
	
	public Boolean getVoltarParaEmergencia() {
		return voltarParaEmergencia;
	}

	public void setVoltarParaEmergencia(Boolean voltarParaEmergencia) {
		this.voltarParaEmergencia = voltarParaEmergencia;
	}
}