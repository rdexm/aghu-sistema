package br.gov.mec.aghu.model;

import java.io.Serializable;
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


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioGrauInstrucaoRap;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "RAP_PESSOAS_FISICAS_JN", schema = "AGH")
@SequenceGenerator(name = "rapPesJnSeq", sequenceName = "AGH.RAP_PES_JN_SEQ", allocationSize = 1)

@Immutable
public class RapPessoasFisicasJn extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5015656324719178108L;
	private Integer codigo;
	private Long cpf;
	private Date criadoEm;
	private String nome;
	private String nomeMae;
	private Integer pacProntuario;
	private String nomePai;
	private Date dtNascimento;
	private DominioSexo sexo;
	private String nomeUsual;
	private DominioGrauInstrucaoRap grauInstrucao;
	private DominioEstadoCivil estadoCivil;
	private Integer bclCloLgrCodigo;
	private Integer bclCloCep;
	private Integer bclBaiCodigo;
	private Integer cddCodigo;
	private Integer nacCodigo;
	private String logradouro;
	private String complLogradouro;
	private Integer nroLogradouro;
	private Integer cep;
	private String bairro;
	private String cidadeNascimento;
	private String ufSigla;
	private String nroIdentidade;
	private Integer nroCartProfissional;
	private String serieCartProfissional;
	private Long pisPasep;
	private Long nroTitEleitor;
	private Short zonaTitEleitor;
	private Short secaoTitEleitor;
	private Short dddFoneResidencial;
	private Long foneResidencial;
	private Integer ramalFoneResidencial;
	private Short dddFoneCelular;
	private Long foneCelular;
	private Short dddFonePagerBip;
	private Long fonePagerBip;
	private String nroPagerBip;
	private String emailParticular;

	public RapPessoasFisicasJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rapPesJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "CODIGO", length = 9, nullable = false)
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "CPF", length = 12)
	public Long getCpf() {
		return this.cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	@Column(name = "CRIADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "NOME", length = 50)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "NOME_MAE", length = 45)
	public String getNomeMae() {
		return this.nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	@Column(name = "PAC_PRONTUARIO", length = 8)
	public Integer getPacProntuario() {
		return this.pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	@Column(name = "NOME_PAI", length = 45)
	public String getNomePai() {
		return this.nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = nomePai;
	}

	@Column(name = "DT_NASCIMENTO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtNascimento() {
		return this.dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	@Column(name = "SEXO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSexo getSexo() {
		return this.sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	@Column(name = "NOME_USUAL", length = 15)
	public String getNomeUsual() {
		return this.nomeUsual;
	}

	public void setNomeUsual(String nomeUsual) {
		this.nomeUsual = nomeUsual;
	}

	@Column(name = "GRAU_INSTRUCAO", length = 2)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioGrauInstrucaoRap") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioGrauInstrucaoRap getGrauInstrucao() {
		return this.grauInstrucao;
	}

	public void setGrauInstrucao(DominioGrauInstrucaoRap grauInstrucao) {
		this.grauInstrucao = grauInstrucao;
	}

	@Column(name = "ESTADO_CIVIL", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioEstadoCivil getEstadoCivil() {
		return this.estadoCivil;
	}

	public void setEstadoCivil(DominioEstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	@Column(name = "BCL_CLO_LGR_CODIGO", length = 6)
	public Integer getBclCloLgrCodigo() {
		return this.bclCloLgrCodigo;
	}

	public void setBclCloLgrCodigo(Integer bclCloLgrCodigo) {
		this.bclCloLgrCodigo = bclCloLgrCodigo;
	}

	@Column(name = "BCL_CLO_CEP", length = 8)
	public Integer getBclCloCep() {
		return this.bclCloCep;
	}

	public void setBclCloCep(Integer bclCloCep) {
		this.bclCloCep = bclCloCep;
	}

	@Column(name = "BCL_BAI_CODIGO", length = 5)
	public Integer getBclBaiCodigo() {
		return this.bclBaiCodigo;
	}

	public void setBclBaiCodigo(Integer bclBaiCodigo) {
		this.bclBaiCodigo = bclBaiCodigo;
	}

	@Column(name = "CDD_CODIGO", length = 5)
	public Integer getCddCodigo() {
		return this.cddCodigo;
	}

	public void setCddCodigo(Integer cddCodigo) {
		this.cddCodigo = cddCodigo;
	}

	@Column(name = "NAC_CODIGO", length = 22)
	public Integer getNacCodigo() {
		return this.nacCodigo;
	}

	public void setNacCodigo(Integer nacCodigo) {
		this.nacCodigo = nacCodigo;
	}

	@Column(name = "LOGRADOURO", length = 60)
	public String getLogradouro() {
		return this.logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	@Column(name = "COMPL_LOGRADOURO", length = 15)
	public String getComplLogradouro() {
		return this.complLogradouro;
	}

	public void setComplLogradouro(String complLogradouro) {
		this.complLogradouro = complLogradouro;
	}

	@Column(name = "NRO_LOGRADOURO", length = 5)
	public Integer getNroLogradouro() {
		return this.nroLogradouro;
	}

	public void setNroLogradouro(Integer nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	@Column(name = "CEP", length = 8)
	public Integer getCep() {
		return this.cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	@Column(name = "BAIRRO", length = 60)
	public String getBairro() {
		return this.bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	@Column(name = "CIDADE_NASCIMENTO", length = 40)
	public String getCidadeNascimento() {
		return this.cidadeNascimento;
	}

	public void setCidadeNascimento(String cidadeNascimento) {
		this.cidadeNascimento = cidadeNascimento;
	}

	@Column(name = "UF_SIGLA", length = 2)
	public String getUfSigla() {
		return this.ufSigla;
	}

	public void setUfSigla(String ufSigla) {
		this.ufSigla = ufSigla;
	}

	@Column(name = "NRO_IDENTIDADE", length = 11)
	public String getNroIdentidade() {
		return this.nroIdentidade;
	}

	public void setNroIdentidade(String nroIdentidade) {
		this.nroIdentidade = nroIdentidade;
	}

	@Column(name = "NRO_CART_PROFISSIONAL", length = 9)
	public Integer getNroCartProfissional() {
		return this.nroCartProfissional;
	}

	public void setNroCartProfissional(Integer nroCartProfissional) {
		this.nroCartProfissional = nroCartProfissional;
	}

	@Column(name = "SERIE_CART_PROFISSIONAL", length = 5)
	public String getSerieCartProfissional() {
		return this.serieCartProfissional;
	}

	public void setSerieCartProfissional(String serieCartProfissional) {
		this.serieCartProfissional = serieCartProfissional;
	}

	@Column(name = "PIS_PASEP", length = 12)
	public Long getPisPasep() {
		return this.pisPasep;
	}

	public void setPisPasep(Long pisPasep) {
		this.pisPasep = pisPasep;
	}

	@Column(name = "NRO_TIT_ELEITOR", length = 12)
	public Long getNroTitEleitor() {
		return this.nroTitEleitor;
	}

	public void setNroTitEleitor(Long nroTitEleitor) {
		this.nroTitEleitor = nroTitEleitor;
	}

	@Column(name = "ZONA_TIT_ELEITOR", length = 3)
	public Short getZonaTitEleitor() {
		return this.zonaTitEleitor;
	}

	public void setZonaTitEleitor(Short zonaTitEleitor) {
		this.zonaTitEleitor = zonaTitEleitor;
	}

	@Column(name = "SECAO_TIT_ELEITOR", length = 3)
	public Short getSecaoTitEleitor() {
		return this.secaoTitEleitor;
	}

	public void setSecaoTitEleitor(Short secaoTitEleitor) {
		this.secaoTitEleitor = secaoTitEleitor;
	}

	@Column(name = "DDD_FONE_RESIDENCIAL", length = 4)
	public Short getDddFoneResidencial() {
		return this.dddFoneResidencial;
	}

	public void setDddFoneResidencial(Short dddFoneResidencial) {
		this.dddFoneResidencial = dddFoneResidencial;
	}

	@Column(name = "FONE_RESIDENCIAL", length = 10)
	public Long getFoneResidencial() {
		return this.foneResidencial;
	}

	public void setFoneResidencial(Long foneResidencial) {
		this.foneResidencial = foneResidencial;
	}

	@Column(name = "RAMAL_FONE_RESIDENCIAL", length = 6)
	public Integer getRamalFoneResidencial() {
		return this.ramalFoneResidencial;
	}

	public void setRamalFoneResidencial(Integer ramalFoneResidencial) {
		this.ramalFoneResidencial = ramalFoneResidencial;
	}

	@Column(name = "DDD_FONE_CELULAR", length = 4)
	public Short getDddFoneCelular() {
		return this.dddFoneCelular;
	}

	public void setDddFoneCelular(Short dddFoneCelular) {
		this.dddFoneCelular = dddFoneCelular;
	}

	@Column(name = "FONE_CELULAR", length = 10)
	public Long getFoneCelular() {
		return this.foneCelular;
	}

	public void setFoneCelular(Long foneCelular) {
		this.foneCelular = foneCelular;
	}

	@Column(name = "DDD_FONE_PAGER_BIP", length = 4)
	public Short getDddFonePagerBip() {
		return this.dddFonePagerBip;
	}

	public void setDddFonePagerBip(Short dddFonePagerBip) {
		this.dddFonePagerBip = dddFonePagerBip;
	}

	@Column(name = "FONE_PAGER_BIP", length = 10)
	public Long getFonePagerBip() {
		return this.fonePagerBip;
	}

	public void setFonePagerBip(Long fonePagerBip) {
		this.fonePagerBip = fonePagerBip;
	}

	@Column(name = "NRO_PAGER_BIP", length = 15)
	public String getNroPagerBip() {
		return this.nroPagerBip;
	}

	public void setNroPagerBip(String nroPagerBip) {
		this.nroPagerBip = nroPagerBip;
	}

	@Column(name = "EMAIL_PARTICULAR", length = 45)
	public String getEmailParticular() {
		return this.emailParticular;
	}

	public void setEmailParticular(String emailParticular) {
		this.emailParticular = emailParticular;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getSeqJn() == null) ? 0 : getSeqJn().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RapPessoasFisicasJn other = (RapPessoasFisicasJn) obj;
		if (getSeqJn() == null) {
			if (other.getSeqJn() != null) {
				return false;
			}
		} else if (!getSeqJn().equals(other.getSeqJn())) {
			return false;
		}
		return true;
	}

	public enum Fields {
		JN_USER("nomeUsuario"), JN_DATE_TIME("dataAlteracao"), JN_OPERATION(
				"operacao"), CODIGO("codigo"), CPF("cpf"), CRIADO_EM("criadoEm"), NOME(
				"nome"), NOME_MAE("nomeMae"), PAC_PRONTUARIO("pacProntuario"), NOME_PAI(
				"nomePai"), DT_NASCIMENTO("dtNascimento"), SEXO("sexo"), NOME_USUAL(
				"nomeUsual"), GRAU_INSTRUCAO("grauInstrucao"), ESTADO_CIVIL(
				"estadoCivil"), BCL_CLO_LGR_CODIGO("bclCloLgrCodigo"), BCL_CLO_CEP(
				"bclCloCep"), BCL_BAI_CODIGO("bclBaiCodigo"), CDD_CODIGO(
				"cddCodigo"), NAC_CODIGO("nacCodigo"), LOGRADOURO("logradouro"), COMPL_LOGRADOURO(
				"complLogradouro"), NRO_LOGRADOURO("nroLogradouro"), CEP("cep"), BAIRRO(
				"bairro"), CIDADE_NASCIMENTO("cidadeNascimento"), UF_SIGLA(
				"ufSigla"), NRO_IDENTIDADE("nroIdentidade"), NRO_CART_PROFISSIONAL(
				"nroCartProfissional"), SERIE_CART_PROFISSIONAL(
				"serieCartProfissional"), PIS_PASEP("pisPasep"), NRO_TIT_ELEITOR(
				"nroTitEleitor"), ZONA_TIT_ELEITOR("zonaTitEleitor"), SECAO_TIT_ELEITOR(
				"secaoTitEleitor"), DDD_FONE_RESIDENCIAL("dddFoneResidencial"), FONE_RESIDENCIAL(
				"foneResidencial"), RAMAL_FONE_RESIDENCIAL(
				"ramalFoneResidencial"), DDD_FONE_CELULAR("dddFoneCelular"), FONE_CELULAR(
				"foneCelular"), DDD_FONE_PAGER_BIP("dddFonePagerBip"), FONE_PAGER_BIP(
				"fonePagerBip"), NRO_PAGER_BIP("nroPagerBip"), EMAIL_PARTICULAR(
				"emailParticular")

		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}