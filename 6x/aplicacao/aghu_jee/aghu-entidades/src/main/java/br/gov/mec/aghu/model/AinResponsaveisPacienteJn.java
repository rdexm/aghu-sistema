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
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.core.validation.CPF;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidade;



@Entity
@Table(name = "AIN_RESPONSAVEIS_PACIENTE_JN", schema = "AGH")
@SequenceGenerator(name="ainRepSq1Jn", sequenceName="AGH.AIN_REP_SQ1_JN", allocationSize = 1)
@Immutable
public class AinResponsaveisPacienteJn extends BaseJournal  implements java.io.Serializable{

	private static final long serialVersionUID = 9097678439555973818L;

	private Integer seqInternacao;
	private DominioTipoResponsabilidade tipoResponsabilidade;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;
	private Long fone;	
	private Integer seq;
	private String nome;
	private Long cpf;
	private String logradouro;
	private String cidade;
	private String uf;
	private Integer cep;
	private Short dddFone;
	private Long regNascimento;
	private Long nroCartaoSaude;
	private Long rg;
	private String orgaoEmisRg;
	private String nomeMae;
	private Integer atendimento;
	private Integer  cntaConv;
	private String email;
	private String emailPaciente;
	private Date dtNascimento;
	private Integer responsavelConta;
	
	public AinResponsaveisPacienteJn() {
		this.criadoEm = new Date();
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ainRepSq1Jn")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "INT_SEQ", nullable = false)
	public Integer getSeqInternacao() {
		return seqInternacao;
	}
	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}
	
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "TIPO_RESPONSABILIDADE", nullable = false, length = 2)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioTipoResponsabilidade getTipoResponsabilidade() {
		return tipoResponsabilidade;
	}
	public void setTipoResponsabilidade(DominioTipoResponsabilidade tipoResponsabilidade) {
		this.tipoResponsabilidade = tipoResponsabilidade;
	}
	
	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	@Column(name = "FONE", length = 10)
	@Max(value = 99999999999L, message = "Valor máximo permitido: 99 99999 9999")
	public Long getFone() {
		return fone;
	}
	public void setFone(Long fone) {
		this.fone = fone;
	}
	
	@Column(name = "NOME", nullable = false, length = 60)
	@NotNull
	@Length(max = 60, message="Nome deve ter no máximo 60 caracteres.")
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "CPF", precision = 11, scale = 0)
	@CPF
	public Long getCpf() {
		return this.cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}
	
	@Column(name = "LOGRADOURO", length = 60)
	@Length(max = 60, message="Logradouro deve ter no máximo 60 caracteres.")
	public String getLogradouro() {
		return this.logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	@Column(name = "CIDADE", length = 35)
	@Length(max = 35, message="Cidade deve ter no máximo 35 caracteres.")
	public String getCidade() {
		return this.cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	@Column(name = "UF_SIGLA")
	public String getUf() {
		return this.uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	@Column(name = "CEP", precision = 8, scale = 0)
	public Integer getCep() {
		return this.cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	@Column(name = "DDD_FONE", precision = 4, scale = 0)
	public Short getDddFone() {
		return this.dddFone;
	}

	public void setDddFone(Short dddFone) {
		this.dddFone = dddFone;
	}

	@Column(name = "REG_NASCIMENTO", length = 10)
	@Max(value = 9999999999L, message = "Valor máximo permitido: 9999999999")
	public Long getRegNascimento() {
		return this.regNascimento;
	}

	public void setRegNascimento(Long regNascimento) {
		this.regNascimento = regNascimento;
	}

	@Column(name = "NRO_CARTAO_SAUDE", length = 11)
	@Max(value = 99999999999L, message = "Valor máximo permitido: 99999999999")
	public Long getNroCartaoSaude() {
		return this.nroCartaoSaude;
	}

	public void setNroCartaoSaude(Long nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}

	@Column(name = "RG", length = 10)
	@Max(value = 9999999999L, message = "Valor máximo permitido: 9999999999")
	public Long getRg() {
		return this.rg;
	}

	public void setRg(Long rg) {
		this.rg = rg;
	}

	@Column(name = "ORGAO_EMIS_RG", length = 10)
	@Length(max = 10, message="Órgão emissor deve ter no máximo 10 caracteres.")
	public String getOrgaoEmisRg() {
		return this.orgaoEmisRg;
	}

	public void setOrgaoEmisRg(String orgaoEmisRg) {
		this.orgaoEmisRg = orgaoEmisRg;
	}

	@Column(name = "NOME_MAE", length = 50)
	@Length(max = 50, message="Nome da mãe deve ter no máximo 50 caracteres.")
	public String getNomeMae() {
		return this.nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	@Column(name = "ATD_SEQ")
	public Integer getAtendimento() {
		return this.atendimento;
	}

	public void setAtendimento(Integer atendimento) {
		this.atendimento = atendimento;
	}

	@Column(name = "CTA_NRO")
	public Integer getCntaConv() {
		return this.cntaConv;
	}

	public void setCntaConv(Integer cntaConv) {
		this.cntaConv = cntaConv;
	}

	@Column(name = "EMAIL", length = 50)
	@Length(max = 50, message="Email deve ter no máximo 50 caracteres.")
	@Email
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "EMAIL_PACIENTE", length = 50)
	@Length(max = 50, message="Email do paciente deve ter no máximo 50 caracteres.")
	@Email
	public String getEmailPaciente() {
		return this.emailPaciente;
	}

	public void setEmailPaciente(String emailPaciente) {
		this.emailPaciente = emailPaciente;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_NASCIMENTO")
	public Date getDtNascimento() {
		return this.dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	@Column(name = "RES_SEQ")
	public Integer getResponsavelConta() {
		return responsavelConta;
	}

	public void setResponsavelConta(Integer responsavelConta) {
		this.responsavelConta = responsavelConta;
	}



	public enum Fields {
		
		SEQ("seq"),
		SEQ_INTERNACAO("seqInternacao"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo");
		
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
