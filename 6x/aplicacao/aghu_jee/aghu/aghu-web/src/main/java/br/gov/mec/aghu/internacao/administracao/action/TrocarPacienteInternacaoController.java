package br.gov.mec.aghu.internacao.administracao.action;

import java.net.UnknownHostException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.administracao.business.IAdministracaoInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class TrocarPacienteInternacaoController extends ActionController {

	private static final long serialVersionUID = -4691746076765959083L;
	
	private static final Log LOG = LogFactory.getLog(TrocarPacienteInternacaoController.class);
	
	private final String PAGE_PESQUISAR_PACIENTE_INTERNACAO = "pesquisaTrocarPacienteInternacao";
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	@EJB
	private IAdministracaoInternacaoFacade administracaoInternacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private Integer intSeq;
	private Integer prontuario;
	private Integer prontuarioNovo;
	private Date dataInternacao;
	private Date dataAlta;
	private String tipoAltaMedica;
	private String nomePaciente;
	private String nomePacienteNovo;
	private AipPacientes paciente;
	private String mensagemModal;
	private boolean exibirModal;
	
	private AinInternacao internacao;

	@PostConstruct
	public void inicio(){
		this.internacao = pesquisaInternacaoFacade.obterInternacao(this.intSeq);
		if(this.internacao != null){
			this.nomePaciente = this.internacao.getPaciente().getNome();
			this.prontuario = this.internacao.getPaciente().getProntuario();
			this.dataInternacao = 	this.internacao.getDthrInternacao();
			this.dataAlta = 	this.internacao.getDthrAltaMedica();
			if(this.internacao.getTipoAltaMedica()!= null){
				this.tipoAltaMedica = this.internacao.getTipoAltaMedica().getDescricao(); 
			}
		}
	}
	
	public String gravar(){
		try {
			if(this.prontuarioNovo != null && this.prontuario.intValue() != this.prontuarioNovo.intValue()){
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = getEnderecoIPv4HostRemoto().toString();
				} catch (UnknownHostException e) {
					LOG.error(e.getMessage(), e);
				}
				
				internacao = administracaoInternacaoFacade.atualizarInternacao(intSeq, prontuarioNovo, nomeMicrocomputador);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_TROCAR_PACIENTE_INTERNACAO");
			}

			limparCamposDeControle();
			return PAGE_PESQUISAR_PACIENTE_INTERNACAO;
			
		} catch (BaseException e) {
			this.exibirModal = false;
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			return null;
		} catch (Exception e) {
			this.exibirModal = false;
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, administracaoInternacaoFacade.getStringKeyErroGenericoIntegracaoModulo());
			return null;
		} 
	}
	
	public void pesquisarNomePacientePorProntuario(){
		if(this.prontuarioNovo != null && this.prontuario.intValue() == this.prontuarioNovo.intValue()){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_PRONTUARIOS_IGUAIS");
		}else{
			this.paciente =	pacienteFacade.pesquisarPacientePorProntuario(this.prontuarioNovo);
			if(this.paciente != null){
				this.nomePacienteNovo = this.paciente.getNome(); 
			}
		}
	}
	
	public void verificaExibicaoModal(){
		if(this.prontuarioNovo != null && StringUtils.isNotBlank(this.nomePacienteNovo)){
			this.exibirModal = true;
			openDialog("modalConfirmacaoWG");
		}else{
			this.exibirModal = false;
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PESQUISA_OBRIGATORIA");
		}
	}
	
	public String cancelar(){
		limparCamposDeControle();
		return PAGE_PESQUISAR_PACIENTE_INTERNACAO;
	}
	
	public void limparCamposDeControle(){
		prontuarioNovo = null;
		nomePacienteNovo = null;
		this.paciente = null;
		exibirModal = false;
	}

	//### GETS e SETs ###
	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getProntuarioNovo() {
		return prontuarioNovo;
	}

	public void setProntuarioNovo(Integer prontuarioNovo) {
		this.prontuarioNovo = prontuarioNovo;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomePacienteNovo() {
		return nomePacienteNovo;
	}

	public void setNomePacienteNovo(String nomePacienteNovo) {
		this.nomePacienteNovo = nomePacienteNovo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public Date getDataAlta() {
		return dataAlta;
	}

	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}

	public String getTipoAltaMedica() {
		return tipoAltaMedica;
	}

	public void setTipoAltaMedica(String tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}

	
	public String getMensagemModal() {
		StringBuilder str = new StringBuilder(51);
		str.append("Confirma a alteração da internacao de ")
		.append(this.getProntuario())
		.append(" - ")
		.append(this.getNomePaciente())
		.append(" para ")
		.append(this.getProntuarioNovo())
		.append(" - ")
		.append(this.getNomePacienteNovo())
		.append('?');
		this.mensagemModal = str.toString();
		
		return mensagemModal;
	}

	public boolean isExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

}
