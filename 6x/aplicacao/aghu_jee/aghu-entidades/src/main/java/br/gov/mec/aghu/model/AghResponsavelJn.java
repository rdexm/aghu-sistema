package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.dominio.DominioCpfCgcResponsavel;



@Entity
@Table(name = "AGH_RESPONSAVEIS_JN", schema = "AGH")
@SequenceGenerator(name="aghRespJn", sequenceName="AGH.AGH_RES_JN_SQ1", allocationSize = 1)
@Immutable
public class AghResponsavelJn extends BaseJournal  implements java.io.Serializable {

	private static final long serialVersionUID = 7462387508575725889L;


	private DominioCpfCgcResponsavel dominioCpfCgc;
	private Long cpfCgc;
	private String nroDocExterior;
	private String nome;
	private String nomeMae;
	private Date dtNascimento;
	private Integer aghPaisBcb;
	private Integer cloLgrCodigo;
	private Integer cloCep;
	private Integer baiCodigo;
	private Integer aipCidade;
	private String cidade;
	private String logradouro;
	private String complLogradouro;
	private Integer nroLogradouro;
	private Integer cep;
	private String bairro;
	private String aipUf;
	private String ufSiglaExterior;
	private String rg;
	private Short aipOrgaosEmissor;
	private Long pisPasep;
	private Short dddFone;
	private Long fone;
	private String email;
	private Date criadoEm;
	private Integer matricula;
	private Short vinCodigo;	
	private Integer aipPaciente;
	private Long CodigoClienteNfe;
	private String regNascimento;
	
	public AghResponsavelJn() {
		this.criadoEm = new Date();
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghRespJn")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "INDICADOR_CPF_CGC", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioCpfCgcResponsavel getDominioCpfCgc() {
		return dominioCpfCgc;
	}

	public void setDominioCpfCgc(DominioCpfCgcResponsavel dominioCpfCgc) {
		this.dominioCpfCgc = dominioCpfCgc;
	}

	@Column(name = "CPF_CGC",  precision = 14, scale = 0)
	public Long getCpfCgc() {
		return cpfCgc;
	}

	public void setCpfCgc(Long cpfCgc) {
		this.cpfCgc = cpfCgc;
	}

	@Column(name = "NRO_DOC_EXTERIOR", length = 20)
	public String getNroDocExterior() {
		return nroDocExterior;
	}

	public void setNroDocExterior(String nroDocExterior) {
		this.nroDocExterior = nroDocExterior;
	}

	@Column(name = "NOME", length = 100)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "NOME_MAE", length = 50)
	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_NASCIMENTO")
	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	@Column(name = "PAI_SEQ")
	public Integer getAghPaisBcb() {
		return aghPaisBcb;
	}

	public void setAghPaisBcb(Integer aghPaisBcb) {
		this.aghPaisBcb = aghPaisBcb;
	}

	@Column(name = "BCL_CLO_LGR_CODIGO")
	public Integer getCloLgrCodigo() {
		return cloLgrCodigo;
	}

	public void setCloLgrCodigo(Integer cloLgrCodigo) {
		this.cloLgrCodigo = cloLgrCodigo;
	}

	@Column(name = "BCL_CLO_CEP")
	public Integer getCloCep() {
		return cloCep;
	}

	public void setCloCep(Integer cloCep) {
		this.cloCep = cloCep;
	}

	@Column(name = "BCL_BAI_CODIGO")
	public Integer getBaiCodigo() {
		return baiCodigo;
	}

	public void setBaiCodigo(Integer baiCodigo) {
		this.baiCodigo = baiCodigo;
	}

	@Column(name = "CDD_CODIGO")
	public Integer getAipCidade() {
		return aipCidade;
	}

	public void setAipCidade(Integer aipCidade) {
		this.aipCidade = aipCidade;
	}

	@Column(name = "CIDADE", length = 60)
	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	
	@Column(name = "LOGRADOURO", length = 100)
	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	
	@Column(name = "COMPL_LOGRADOURO", length = 20)
	public String getComplLogradouro() {
		return complLogradouro;
	}

	public void setComplLogradouro(String complLogradouro) {
		this.complLogradouro = complLogradouro;
	}

	@Column(name = "NRO_LOGRADOURO", precision = 5, scale = 0)
	public Integer getNroLogradouro() {
		return nroLogradouro;
	}

	public void setNroLogradouro(Integer nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	@Column(name = "CEP", precision = 8, scale = 0)
	public Integer getCep() {
		return cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	@Column(name = "BAIRRO", length = 60)
	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	@Column(name = "UF_SIGLA", length = 60)
	public String getAipUf() {
		return aipUf;
	}

	public void setAipUf(String aipUf) {
		this.aipUf = aipUf;
	}

	@Column(name = "UF_SIGLA_EXTERIOR", length = 2)
	public String getUfSiglaExterior() {
		return ufSiglaExterior;
	}

	public void setUfSiglaExterior(String ufSiglaExterior) {
		this.ufSiglaExterior = ufSiglaExterior;
	}

	@Column(name = "RG", length = 20)
	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	@Column(name = "OED_CODIGO")
	public Short getAipOrgaosEmissor() {
		return aipOrgaosEmissor;
	}

	public void setAipOrgaosEmissor(Short aipOrgaosEmissor) {
		this.aipOrgaosEmissor = aipOrgaosEmissor;
	}

	@Column(name = "PIS_PASEP", precision = 12, scale = 0)
	public Long getPisPasep() {
		return pisPasep;
	}

	public void setPisPasep(Long pisPasep) {
		this.pisPasep = pisPasep;
	}

	@Column(name = "DDD_FONE", precision = 4, scale = 0)
	public Short getDddFone() {
		return dddFone;
	}

	public void setDddFone(Short dddFone) {
		this.dddFone = dddFone;
	}

	@Column(name = "FONE", precision = 10, scale = 0)
	public Long getFone() {
		return fone;
	}

	public void setFone(Long fone) {
		this.fone = fone;
	}

	@Column(name = "EMAIL", length=100)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	@Column(name = "PAC_CODIGO", precision = 5, scale = 0)
	public Integer getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(Integer aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	@Column(name = "COD_CLIENTE_NFE", precision = 10, scale = 0)
	public Long getCodigoClienteNfe() {
		return CodigoClienteNfe;
	}

	public void setCodigoClienteNfe(Long codigoClienteNfe) {
		CodigoClienteNfe = codigoClienteNfe;
	}

	@Column(name = "REG_NASCIMENTO")
	public String getRegNascimento() {
		return regNascimento;
	}

	public void setRegNascimento(String regNascimento) {
		this.regNascimento = regNascimento;
	}


	public enum Fields {
		
		DOMINIO_CPF_CGC("dominioCpfCgc"),
		SEQ_INTERNACAO("seqInternacao");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return this.fields;
		}
		
	}
	
	
}