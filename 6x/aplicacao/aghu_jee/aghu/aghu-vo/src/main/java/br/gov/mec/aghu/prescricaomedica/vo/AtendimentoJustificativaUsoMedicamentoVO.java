package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigInteger;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AtendimentoJustificativaUsoMedicamentoVO implements BaseBean {

	private static final long serialVersionUID = 2208781762971845083L;

	// AghAtendimentos
	private Integer atdSeq;
	
	// AipPacientes
	private String nome;
	private String nomeMae;
	private Date dtNascimento;
	private Integer cddCodigo;
	private Integer ocpCodigo;
	private String ufSigla;
	private DominioCor cor;
	private DominioSexo sexo;
	private DominioGrauInstrucao grauInstrucao;
	private Short dddFoneResidencial;
	private Long foneResidencial;
	private DominioEstadoCivil estadoCivil;
	private Integer prontuario;
	private BigInteger nroCartaoSaude;
	private Integer pacCodigo;
	
	// AipEnderecosPacientes
	private String logradouro;
	private Integer nroLogradouro;
	private String bairro;
	private String complLogradouro;
	private String cddNome;
	private String enpCidade;
	
	private Integer cep;
	private Integer bclBaiCodigo;
	private Integer bclCloCep;
	private Integer bclCloLgrCodigo;
	

	
	// Nvl(cdd.nome, enp.cidade) cidade,
	public String getCidade() {
		if(cddNome != null) {
			return cddNome;
		} else {
			return enpCidade;
		}
	}	
	
	// GETTERS AND SETTERS
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeMae() {
		return nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Integer getCddCodigo() {
		return cddCodigo;
	}

	public void setCddCodigo(Integer cddCodigo) {
		this.cddCodigo = cddCodigo;
	}

	public Integer getOcpCodigo() {
		return ocpCodigo;
	}

	public void setOcpCodigo(Integer ocpCodigo) {
		this.ocpCodigo = ocpCodigo;
	}

	public String getUfSigla() {
		return ufSigla;
	}

	public void setUfSigla(String ufSigla) {
		this.ufSigla = ufSigla;
	}

	public DominioCor getCor() {
		return cor;
	}

	public void setCor(DominioCor cor) {
		this.cor = cor;
	}

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public DominioGrauInstrucao getGrauInstrucao() {
		return grauInstrucao;
	}

	public void setGrauInstrucao(DominioGrauInstrucao grauInstrucao) {
		this.grauInstrucao = grauInstrucao;
	}

	public Short getDddFoneResidencial() {
		return dddFoneResidencial;
	}

	public void setDddFoneResidencial(Short dddFoneResidencial) {
		this.dddFoneResidencial = dddFoneResidencial;
	}

	public Long getFoneResidencial() {
		return foneResidencial;
	}

	public void setFoneResidencial(Long foneResidencial) {
		this.foneResidencial = foneResidencial;
	}

	public DominioEstadoCivil getEstadoCivil() {
		return estadoCivil;
	}

	public void setEstadoCivil(DominioEstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public BigInteger getNroCartaoSaude() {
		return nroCartaoSaude;
	}

	public void setNroCartaoSaude(BigInteger nroCartaoSaude) {
		this.nroCartaoSaude = nroCartaoSaude;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public Integer getNroLogradouro() {
		return nroLogradouro;
	}

	public void setNroLogradouro(Integer nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getComplLogradouro() {
		return complLogradouro;
	}

	public void setComplLogradouro(String complLogradouro) {
		this.complLogradouro = complLogradouro;
	}

	public String getCddNome() {
		return cddNome;
	}

	public void setCddNome(String cddNome) {
		this.cddNome = cddNome;
	}

	public String getEnpCidade() {
		return enpCidade;
	}

	public void setEnpCidade(String enpCidade) {
		this.enpCidade = enpCidade;
	}

	public Integer getCep() {
		return cep;
	}

	public void setCep(Integer cep) {
		this.cep = cep;
	}

	public Integer getBclBaiCodigo() {
		return bclBaiCodigo;
	}

	public void setBclBaiCodigo(Integer bclBaiCodigo) {
		this.bclBaiCodigo = bclBaiCodigo;
	}

	public Integer getBclCloCep() {
		return bclCloCep;
	}

	public void setBclCloCep(Integer bclCloCep) {
		this.bclCloCep = bclCloCep;
	}

	public Integer getBclCloLgrCodigo() {
		return bclCloLgrCodigo;
	}

	public void setBclCloLgrCodigo(Integer bclCloLgrCodigo) {
		this.bclCloLgrCodigo = bclCloLgrCodigo;
	}

	public enum Fields {
	
		ATD_SEQ("atdSeq"),
		PAC_NOME("nome"),
		PAC_NOME_MAE("nomeMae"),
		PAC_DT_NASCIMENTO("dtNascimento"),
		PAC_CDD_CODIGO("cddCodigo"),
		PAC_OCP_CODIGO("ocpCodigo"),
		PAC_UF_SIGLA("ufSigla"),
		PAC_COR("cor"),
		PAC_SEXO("sexo"),
		PAC_GRAU_INSTRUCAO("grauInstrucao"),
		PAC_DDD_FONE_RESIDENCIAL("dddFoneResidencial"),
		PAC_FONE_RESIDENCIAL("foneResidencial"),
		PAC_ESTADO_CIVIL("estadoCivil"),
		PAC_PRONTUARIO("prontuario"),
		PAC_NRO_CARTAO_SAUDE("nroCartaoSaude"),
		PAC_CODIGO("pacCodigo"),
		ENP_LOGRADOURO("logradouro"),
		ENP_NRO_LOGRADOURO("nroLogradouro"),
		ENP_BAIRRO("bairro"),
		ENP_COMPL_LOGRADOURO("complLogradouro"),
		CDD_NOME("cddNome"),
		ENP_CIDADE("enpCidade"),
		ENP_CEP("cep"),
		ENP_BCL_BAI_CODIGO("bclBaiCodigo"),
		ENP_BCL_CLO_CEP("bclCloCep"),
		ENP_BCL_CLO_LGR_CODIGO("bclCloLgrCodigo")
		;
		
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