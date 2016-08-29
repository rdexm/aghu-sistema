package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

public class ListaPacientePrescricaoVO implements Serializable{
	
	private static final long serialVersionUID = 5190360800364327025L;

	private Integer seqPte;
	
	private Integer seqAtd;
	
	private Boolean indPrcrImpressao;
	
	
	private String origemAtendimento;
	
	
	
	private String estadoSaude;
	private Short unfSeq;
	private Integer qtDiariasAutorizadas;
	private String ltoId;
	
	private Integer codigoPaciente;
	private String nomePaciente;
	private String nomeSocialPaciente;
	private Integer idadePaciente;
	private Date dtNascimentoPaciente;

	private Integer conNumero;
	private Integer prontuario;
	
	private String siglaEspecialidade;
	private Integer intSeq;

	private String senhaAutorizada;
	
	
	
	private Short qrtNumero;
	private String leitoID;
	private String pacNome;
	private String pacNomeSocial;
	private Integer pacCodigo;
	private Short sufUnfSeq;
	private Short lseEspSeq;
	private Short atdEspSeq;
	private Integer papAtdSeq;
	private Integer atdSeq;
	private Short vinCodigoServidor;
	private Integer matriculaServidor;
	private Date dthrInicio;
	private Integer dthrInicio1;
	private Integer pacIdade;
	private String nomeUsual;
	private String local;
	private String dataFormatada;
	
	private String razaoSocial;
	
	public enum Fields {

		SEQ_PTE("seqPte"), 
		SEQ_ATD("seqAtd"),
		IND_PRCR_IMPRESSAO("indPrcrImpressao"),
		SEQ("seq"),
		ESTADO_SAUDE("estadoSaude"),
		UNF_SEQ("unfSeq"),
		QT_DIARIAS_AUTORIZADAS("qtDiariasAutorizadas"),
		LTO_ID("ltoId"),
		
		CODIGO_PACIENTE("codigoPaciente"),
		NOME_PACIENTE("nomePaciente"),
		NOME_SOCIAL_PACIENTE("nomeSocialPaciente"),
		DT_NASCIMENTO_PACIENTE("dtNascimentoPaciente"),
		
		CON_NUMERO("conNumero"),
		PRONTUARIO("prontuario"),
		DATA_HORA_INICIO("dthrInicio"),
		
		SIGLA_ESPECIALIDADE("siglaEspecialidade"),
		
		SENHA_AUTORIZADA("senhaAutorizada"),
		
		INT_SEQ("intSeq"),
		ATD_PRONTUARIO("prontuario"),
		QRT_NUMERO("qrtNumero"),
		LEITO_ID("leitoID"),
		PAC_NOME("pacNome"),
		PAC_CODIGO("pacCodigo"),
		SUF_UNF_SEQ("sufUnfSeq"),
		DTHR_INICIO("dthrInicio"),
		LSE_ESP_SEQ("lseEspSeq"),
		ATD_ESP_SEQ("atdEspSeq"),
		PAP_ATD_SEQ("papAtdSeq"),
		ATD_SEQ("atdSeq"),
		VIN_CODIGO_SERVIDOR("vinCodigoServidor"),
		MATRICULA_SERVIDOR("matriculaServidor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getSeqPte() {
		return seqPte;
	}

	public void setSeqPte(Integer seqPte) {
		this.seqPte = seqPte;
	}

	public Integer getSeqAtd() {
		return seqAtd;
	}

	public void setSeqAtd(Integer seqAtd) {
		this.seqAtd = seqAtd;
	}

	public Boolean getIndPrcrImpressao() {
		return indPrcrImpressao;
	}

	public void setIndPrcrImpressao(Boolean indPrcrImpressao) {
		this.indPrcrImpressao = indPrcrImpressao;
	}

	public String getOrigemAtendimento() {
		return origemAtendimento;
	}

	public void setOrigemAtendimento(String origemAtendimento) {
		this.origemAtendimento = origemAtendimento;
	}

	public String getEstadoSaude() {
		return estadoSaude;
	}

	public void setEstadoSaude(String estadoSaude) {
		this.estadoSaude = estadoSaude;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Integer getQtDiariasAutorizadas() {
		return qtDiariasAutorizadas;
	}

	public void setQtDiariasAutorizadas(Integer qtDiariasAutorizadas) {
		this.qtDiariasAutorizadas = qtDiariasAutorizadas;
	}

	public String getLtoId() {
		return ltoId;
	}

	public void setLtoId(String ltoId) {
		this.ltoId = ltoId;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getIdadePaciente() {
		return idadePaciente;
	}

	public void setIdadePaciente(Integer idadePaciente) {
		this.idadePaciente = idadePaciente;
	}

	public Date getDtNascimentoPaciente() {
		return dtNascimentoPaciente;
	}

	public void setDtNascimentoPaciente(Date dtNascimentoPaciente) {
		this.dtNascimentoPaciente = dtNascimentoPaciente;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public String getSenhaAutorizada() {
		return senhaAutorizada;
	}

	public void setSenhaAutorizada(String senhaAutorizada) {
		this.senhaAutorizada = senhaAutorizada;
	}

	public Short getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	
	public String getPacNomeSocial() {
		return pacNomeSocial;
	}

	public void setPacNomeSocial(String pacNomeSocial) {
		this.pacNomeSocial = pacNomeSocial;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getSufUnfSeq() {
		return sufUnfSeq;
	}

	public void setSufUnfSeq(Short sufUnfSeq) {
		this.sufUnfSeq = sufUnfSeq;
	}

	public Short getLseEspSeq() {
		return lseEspSeq;
	}

	public void setLseEspSeq(Short lseEspSeq) {
		this.lseEspSeq = lseEspSeq;
	}

	public Short getAtdEspSeq() {
		return atdEspSeq;
	}

	public void setAtdEspSeq(Short atdEspSeq) {
		this.atdEspSeq = atdEspSeq;
	}

	public Integer getPapAtdSeq() {
		return papAtdSeq;
	}

	public void setPapAtdSeq(Integer papAtdSeq) {
		this.papAtdSeq = papAtdSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getVinCodigoServidor() {
		return vinCodigoServidor;
	}

	public void setVinCodigoServidor(Short vinCodigoServidor) {
		this.vinCodigoServidor = vinCodigoServidor;
	}

	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}

	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Integer getPacIdade() {
		return pacIdade;
	}

	public void setPacIdade(Integer pacIdade) {
		this.pacIdade = pacIdade;
	}

	public String getNomeUsual() {
		return nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Integer getDthrInicio1() {
		return dthrInicio1;
	}

	public void setDthrInicio1(Integer dthrInicio1) {
		this.dthrInicio1 = dthrInicio1;
	}

	public String getDataFormatada() {
		return dataFormatada;
	}

	public void setDataFormatada(String dataFormatada) {
		this.dataFormatada = dataFormatada;
	}

	public String getNomeSocialPaciente() {
		return nomeSocialPaciente;
	}

	public void setNomeSocialPaciente(String nomeSocialPaciente) {
		this.nomeSocialPaciente = nomeSocialPaciente;
	}	
	
}
