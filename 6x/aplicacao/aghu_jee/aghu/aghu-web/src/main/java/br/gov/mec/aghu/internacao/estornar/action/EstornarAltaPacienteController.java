package br.gov.mec.aghu.internacao.estornar.action;

import java.net.UnknownHostException;
import java.util.Date;

import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;




public class EstornarAltaPacienteController extends ActionController {
	
	private static final long serialVersionUID = -2147239664417241358L;
	
	
	@EJB
	private IInternacaoFacade internacaoFacade;

		
	//Passado pela url
	private Integer intSeq;
	private String cameFrom = "";
	
	private AinInternacao internacao;

	private Integer prontuario;
	private String nomePaciente;
	private Date dthrInternacao;
	private Date dthrAltaMedica;
	private Date dtSaidaPaciente;
	private Integer docObito;
	private AinTiposAltaMedica tipoAltaMedica;
	private AghInstituicoesHospitalares instituicaoHospitalar;
	
	private static final Log LOG = LogFactory.getLog(EstornarAltaPacienteController.class);
	
	private final String PAGE_LIST_ESTOR_ALTA_PAC = "estornarAltaPacienteList";


	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	
	public void inicio(){
		try{
			//this.internacao = this.pesquisaInternacaoFacade.obterInternacao(this.intSeq);
			this.intSeq = this.internacao.getSeq();
			this.nomePaciente = this.internacao.getPaciente().getNome();
			this.prontuario = this.internacao.getPaciente().getProntuario();
			this.dtSaidaPaciente = this.internacao.getDtSaidaPaciente();
			this.dthrInternacao = this.internacao.getDthrInternacao();
			this.dthrAltaMedica = this.internacao.getDthrAltaMedica();
			this.tipoAltaMedica = this.internacao.getTipoAltaMedica();
			this.instituicaoHospitalar = this.internacao.getInstituicaoHospitalarTransferencia();
			this.docObito = this.internacao.getDocObito();
				
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_ESTORNO_ALTA_PACIENTE");
		}
	}
	
	public String gravar(){
		try {
			
			String nomeMicrocomputador = null;
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			try {
				nomeMicrocomputador = this.getEnderecoIPv4HostRemoto().getHostName();						
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			this.internacaoFacade.estornarAltaPaciente(internacao.getSeq(), nomeMicrocomputador, new Date(), false, servidorLogado);
		
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ESTORNO_ALTA_PACIENTE");
			
			return PAGE_LIST_ESTOR_ALTA_PAC;
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void verificaPacienteInternado(){
		try{
			this.internacaoFacade.verificaPacienteInternado(this.internacao.getSeq());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String cancelar(){
		
		this.internacao.setTipoAltaMedica(this.getTipoAltaMedica());
		this.internacao.setDthrAltaMedica(this.getDthrAltaMedica());
		this.internacao.setDtSaidaPaciente(this.getDtSaidaPaciente());
		this.internacao.setInstituicaoHospitalarTransferencia(this.getInstituicaoHospitalar());
		this.internacao.setDocObito(this.getDocObito());
		
		this.prontuario = null;
		this.nomePaciente = null;
		this.dtSaidaPaciente = null;
		this.dthrAltaMedica = null;
		this.tipoAltaMedica = null;
		this.instituicaoHospitalar = null;
		this.docObito = null;
				
		return PAGE_LIST_ESTOR_ALTA_PAC;
	}
	
	
	//### GETS e SETs ###
	public Integer getIntSeq() {
		return this.intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public AinTiposAltaMedica getTipoAltaMedica() {
		return this.tipoAltaMedica;
	}

	public void setTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}
	
	public AghInstituicoesHospitalares getInstituicaoHospitalar() {
		return this.instituicaoHospitalar;
	}

	public void setInstituicaoHospitalar(
			AghInstituicoesHospitalares instituicaoHospitalar) {
		this.instituicaoHospitalar = instituicaoHospitalar;
	}
	
	public AinInternacao getInternacao() {
		return this.internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public Date getDthrAltaMedica() {
		return this.dthrAltaMedica;
	}

	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

	public Date getDtSaidaPaciente() {
		return this.dtSaidaPaciente;
	}

	public void setDtSaidaPaciente(Date dtSaidaPaciente) {
		this.dtSaidaPaciente = dtSaidaPaciente;
	}

	public Integer getDocObito() {
		return this.docObito;
	}

	public void setDocObito(Integer docObito) {
		this.docObito = docObito;
	}

	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return this.nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getCameFrom() {
		return this.cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public void setDthrInternacao(Date dthrInternacao) {
		this.dthrInternacao = dthrInternacao;
	}

	public Date getDthrInternacao() {
		return this.dthrInternacao;
	}
	
}