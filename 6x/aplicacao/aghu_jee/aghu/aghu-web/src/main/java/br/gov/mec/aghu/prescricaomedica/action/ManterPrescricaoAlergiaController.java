package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipAlergiaPacientes;
import br.gov.mec.aghu.model.MpmAlergiaUsual;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterPrescricaoAlergiaController extends ActionController {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6208893504755661727L;
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoAlergiaController.class);
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "manterPrescricaoMedica";	
	private static final String PAGINA_HISTORICO_ALERGIAS_PACIENTE = "prescricaomedica-historicoAlergias";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;	

	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ManterPrescricaoMedicaController manterPrescricaoMedicaController;
	
	private List<AipAlergiaPacientes> listaAipAlergiaPacientes;
	
	private List<AipAlergiaPacientes> listaAipAlergiaPacientesOriginal = new ArrayList<AipAlergiaPacientes>();
	
	private PrescricaoMedicaVO prescricaoMedicaVO;
	
	protected MpmPrescricaoMedica prescricaoMedica;
	
	private MpmAlergiaUsual mpmAlergiaUsualSelecionado;
	private MpmAlergiaUsual mpmAlergiaUsual;
	private String motivoCancelamento;
	private int idConversacaoAnterior;
	private Integer pacCodigo;
	private Integer seq;
	private String descricaoNaoCadastrado;
	private int contador = -1;
	private boolean motivoCancelamentoHabilitado;
	private boolean editando;
	private AipAlergiaPacientes aipPacientesSelecionado;
	private AghAtendimentos atendimento;
	private RapServidores servidorLogado;
	private boolean exibirConfirmacaoVoltar;
	private boolean alterado;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		this.motivoCancelamentoHabilitado = true;
		this.listaAipAlergiaPacientes = new ArrayList<AipAlergiaPacientes>();
		String login = this.obterLoginUsuarioLogado();
		try {
			servidorLogado = this.registroColaboradorFacade.obterServidorPorUsuario(login);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
		
	}
	
	/**
	 * Realiza todas as inicializações necessárias para a apresentação da tela
	 * no modo de inclusão ou edição.
	 */
	public void iniciar() { 		
		
			atendimento = this.aghuFacade.obterAtendimentoPeloSeq(this.prescricaoMedicaVO.getId().getAtdSeq());
			this.listaAipAlergiaPacientes =  this.prescricaoMedicaFacade.obterAipAlergiasPacientes(atendimento.getPaciente().getCodigo());
			
			if(listaAipAlergiaPacientes != null && !listaAipAlergiaPacientes.isEmpty()){
				listaAipAlergiaPacientesOriginal = new ArrayList<AipAlergiaPacientes>();
				for (AipAlergiaPacientes aipAlergiaPacientes : listaAipAlergiaPacientes) {
					this.listaAipAlergiaPacientesOriginal.add(aipAlergiaPacientes);
				}
			}
	}
	
	public void adicionar() {
		
		if(this.mpmAlergiaUsualSelecionado == null 
				&& (this.descricaoNaoCadastrado == null || this.descricaoNaoCadastrado.trim().isEmpty())){
			apresentarMsgNegocio(Severity.ERROR, "AIP_00184");
		}else{
			if((this.motivoCancelamentoHabilitado && (this.motivoCancelamento == null
					|| this.motivoCancelamento.trim().isEmpty())) 
					|| ((this.motivoCancelamento != null && !this.motivoCancelamento.trim().isEmpty())
							&& !this.motivoCancelamentoHabilitado)){
				try {
					this.contador = this.contador-1;
					this.prescricaoMedicaFacade.adicionarAipAlergiaPacientes(this.listaAipAlergiaPacientes, this.aipPacientesSelecionado, this.mpmAlergiaUsualSelecionado, 
							this.motivoCancelamentoHabilitado, this.descricaoNaoCadastrado, this.motivoCancelamento, this.contador, this.atendimento.getPaciente());
					limparCamposFormulario();
					this.alterado = true;
				} catch (ApplicationBusinessException e) {
					LOG.error(e.getMessage(), e);
					apresentarExcecaoNegocio(e);
				}
			}else{
				apresentarMsgNegocio(Severity.ERROR, "MPM_ALERGIAS_MOTIVO_CANCELAMENTO");
			}
		} 
	}

	public void editar(AipAlergiaPacientes item){
		this.aipPacientesSelecionado = item;
		if(item.getAusSeq() != null){
			this.mpmAlergiaUsualSelecionado = this.prescricaoMedicaFacade.obterMpmAlergiaUsualPorSeq(item.getAusSeq());
			this.descricaoNaoCadastrado = null;
		}else if(item.getDescricao() != null && !item.getDescricao().trim().isEmpty()){
			this.descricaoNaoCadastrado = item.getDescricao();
			this.mpmAlergiaUsualSelecionado = null;
		}
		
		if(item.getIndSituacao().equals("V")){
			this.motivoCancelamentoHabilitado = true;
		}else if(item.getIndSituacao().equals("I")){
			this.motivoCancelamentoHabilitado = false;
		}
		this.motivoCancelamento = item.getMotivo();
		this.editando = true;
	}
	
	public void gravar(){
		try {
			if(listaAipAlergiaPacientes != null && !listaAipAlergiaPacientes.isEmpty()){
				if((this.motivoCancelamento == null  && this.motivoCancelamentoHabilitado) || 
						(this.motivoCancelamento != null && !this.motivoCancelamentoHabilitado)){
					this.prescricaoMedicaFacade.gravarAipAlergiaPacientes(this.listaAipAlergiaPacientes, servidorLogado);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ALERGIA_GRAVADO_COM_SUCESSO");
					if(listaAipAlergiaPacientes != null && !listaAipAlergiaPacientes.isEmpty()){
						listaAipAlergiaPacientesOriginal = new ArrayList<AipAlergiaPacientes>();
						for (AipAlergiaPacientes aipAlergiaPacientes : listaAipAlergiaPacientes) {
							this.listaAipAlergiaPacientesOriginal.add(aipAlergiaPacientes);
						}
					}
					this.editando = false;
					this.alterado = false;
				}else{
					this.apresentarMsgNegocio(Severity.ERROR, "MPM_ALERGIAS_MOTIVO_CANCELAMENTO");
				}
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void alterar() throws CloneNotSupportedException{
		
		try {
			if((this.motivoCancelamentoHabilitado && (this.motivoCancelamento == null
					|| this.motivoCancelamento.trim().isEmpty())) 
					|| ((this.motivoCancelamento != null && !this.motivoCancelamento.trim().isEmpty())
							&& !this.motivoCancelamentoHabilitado)){
				listaAipAlergiaPacientes = this.prescricaoMedicaFacade.atualizarListaAipAlergiaPacientes(this.listaAipAlergiaPacientes, this.aipPacientesSelecionado, this.mpmAlergiaUsualSelecionado,
						this.motivoCancelamentoHabilitado, this.descricaoNaoCadastrado, this.motivoCancelamento, this.servidorLogado);
				limparCamposFormulario();
				this.editando = false;
				this.aipPacientesSelecionado = null;
				this.alterado = true;
			}else{
				apresentarMsgNegocio(Severity.ERROR, "MPM_ALERGIAS_MOTIVO_CANCELAMENTO");
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String obterDescricaoPorAusSeq(Integer ausSeq){
		return this.prescricaoMedicaFacade.obterMpmAlergiaUsualPorSeq(ausSeq).getDescricao();
	}
	
	public void cancelarAlteracao(){
		limparCamposFormulario();
		this.editando = false;
		this.aipPacientesSelecionado = null;
	}
	
	public String obterValorIndSituacao(AipAlergiaPacientes item){
		if(item.getIndSituacao().equals("V")){
			return "Válida";
		}else{
			return "Inválida";
		}
	}
	
	public void limparCamposFormulario(){
		this.mpmAlergiaUsualSelecionado = null;
		this.motivoCancelamentoHabilitado = true;
		this.descricaoNaoCadastrado = null;
		this.motivoCancelamento = null;
	}

	public void validarCampoMotivoCancelamento(){
		if(this.motivoCancelamentoHabilitado){
			this.motivoCancelamento = null;
		}
	}
	
	public String voltar(Boolean force) {
		this.limparCamposFormulario();
		this.listaAipAlergiaPacientes = this.prescricaoMedicaFacade.obterAipAlergiasPacientes(atendimento.getPaciente().getCodigo());
		return PAGINA_MANTER_PRESCRICAO_MEDICA;	
	}

	public List<MpmAlergiaUsual> obterMpmAlergiaUsual(String descricao) {
		return this.returnSGWithCount(this.prescricaoMedicaFacade.obterMpmAlergiasUsual(descricao), 
				this.prescricaoMedicaFacade.obterMpmAlergiasUsualCount(descricao));
	}

	public String redirecionarHistorico(){
		return PAGINA_HISTORICO_ALERGIAS_PACIENTE;
	}
	
	/**
	 * Solicita modal de confirmação para o cancelamento
	 */
	public String solicitarConfirmacao() {
		if (!listaAipAlergiaPacientesOriginal.equals(listaAipAlergiaPacientes) || alterado){
			openDialog("modalConfirmacaoVoltarWG");
		}else{
			manterPrescricaoMedicaController.setPmeSeq(prescricaoMedicaVO.getPrescricaoMedica().getId().getSeq());
			manterPrescricaoMedicaController.setPmeSeqAtendimento(prescricaoMedicaVO.getPrescricaoMedica().getId().getAtdSeq());
			this.limparCamposFormulario();
			this.listaAipAlergiaPacientes = this.prescricaoMedicaFacade.obterAipAlergiasPacientes(atendimento.getPaciente().getCodigo());
			return PAGINA_MANTER_PRESCRICAO_MEDICA;	
		}
		return null;
		
	}

	/**
	 * Cancela a modal de confirmação
	 */
	public void cancelarModal() {
		setExibirConfirmacaoVoltar(false);
	}
	
	public void limparCampoDescricaoSelecionado(){
		this.mpmAlergiaUsualSelecionado = null;
	}
	
	public void limparCampoDescricaonaoCadastrado(){
		this.descricaoNaoCadastrado = null;
	}
	
	public String getDescricaoNaoCadastrado() {
		return descricaoNaoCadastrado;
	}

	public void setDescricaoNaoCadastrado(String descricaoNaoCadastrado) {
		this.descricaoNaoCadastrado = descricaoNaoCadastrado;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}	

	public boolean isMotivoCancelamentoHabilitado() {
		return motivoCancelamentoHabilitado;
	}

	public void setMotivoCancelamentoHabilitado(boolean motivoCancelamentoHabilitado) {
		this.motivoCancelamentoHabilitado = motivoCancelamentoHabilitado;
	}

	public List<AipAlergiaPacientes> getListaAipAlergiaPacientes() {
		return listaAipAlergiaPacientes;
	}

	public void setListaAipAlergiaPacientes(
			List<AipAlergiaPacientes> listaAipAlergiaPacientes) {
		this.listaAipAlergiaPacientes = listaAipAlergiaPacientes;
	}
	
	public MpmAlergiaUsual getMpmAlergiaUsualSelecionado() {
		return mpmAlergiaUsualSelecionado;
	}

	public void setMpmAlergiaUsualSelecionado(
			MpmAlergiaUsual mpmAlergiaUsualSelecionado) {
		this.mpmAlergiaUsualSelecionado = mpmAlergiaUsualSelecionado;
	}

	public MpmAlergiaUsual getMpmAlergiaUsual() {
		return mpmAlergiaUsual;
	}

	public void setMpmAlergiaUsual(MpmAlergiaUsual mpmAlergiaUsual) {
		this.mpmAlergiaUsual = mpmAlergiaUsual;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public AipAlergiaPacientes getAipPacientesSelecionado() {
		return aipPacientesSelecionado;
	}

	public void setAipPacientesSelecionado(
			AipAlergiaPacientes aipPacientesSelecionado) {
		this.aipPacientesSelecionado = aipPacientesSelecionado;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public List<AipAlergiaPacientes> getListaAipAlergiaPacientesOriginal() {
		return listaAipAlergiaPacientesOriginal;
	}

	public void setListaAipAlergiaPacientesOriginal(
			List<AipAlergiaPacientes> listaAipAlergiaPacientesOriginal) {
		this.listaAipAlergiaPacientesOriginal = listaAipAlergiaPacientesOriginal;
	}

	public boolean isExibirConfirmacaoVoltar() {
		return exibirConfirmacaoVoltar;
	}

	public void setExibirConfirmacaoVoltar(boolean exibirConfirmacaoVoltar) {
		this.exibirConfirmacaoVoltar = exibirConfirmacaoVoltar;
	}

	public ManterPrescricaoMedicaController getManterPrescricaoMedicaController() {
		return manterPrescricaoMedicaController;
	}

	public void setManterPrescricaoMedicaController(
			ManterPrescricaoMedicaController manterPrescricaoMedicaController) {
		this.manterPrescricaoMedicaController = manterPrescricaoMedicaController;
	}

	public boolean isAlterado() {
		return alterado;
	}

	public void setAlterado(boolean alterado) {
		this.alterado = alterado;
	}

}
