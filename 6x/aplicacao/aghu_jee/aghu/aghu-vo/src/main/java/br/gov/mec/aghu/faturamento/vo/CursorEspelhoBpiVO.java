package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;



public class CursorEspelhoBpiVO {

	private String cnscbo;
	private Long procedimentohosp;
	private String codatvprof;
	private Long cnsmedico;
	private Date dataatendimento;
	private Long cnspaciente;
	private Integer pacCodigo;
	private String nomepaciente;
	private Date dtnascimento;
	private Byte raca;
    private Integer nacionalidade;
	private Byte carateratendimento;
	private Long nroautorizacao;
	private String sexo;
	private Integer codibge;
	private String cid10;
	private Short idade;
	private Integer competencia;
	private Short iphphoseq;
	private Integer iphseq;
	private Integer fccseq;
	private Integer fcfseq;
	private String origeminf;
	private Integer quantidadesumarizada;
    private Double vlranestes;
    private Double vlrproc;
    private Double vlrsadt;
    private Double vlrservhosp;
    private Double vlrservprof;
	private String servico;
	private String classificacao;
	private Short unidadefuncional;
	private String servClass;
	
	public String getCnscbo() {
		return cnscbo;
	}
	public void setCnscbo(String cnscbo) {
		this.cnscbo = cnscbo;
	}
	public Long getProcedimentohosp() {
		return procedimentohosp;
	}
	public void setProcedimentohosp(Long procedimentohosp) {
		this.procedimentohosp = procedimentohosp;
	}
	public String getCodatvprof() {
		return codatvprof;
	}
	public void setCodatvprof(String codatvprof) {
		this.codatvprof = codatvprof;
	}
	public Long getCnsmedico() {
		return cnsmedico;
	}
	public void setCnsmedico(Long cnsmedico) {
		this.cnsmedico = cnsmedico;
	}
	public Date getDataatendimento() {
		return dataatendimento;
	}
	public void setDataatendimento(Date dataatendimento) {
		this.dataatendimento = dataatendimento;
	}
	public Long getCnspaciente() {
		return cnspaciente;
	}
	public void setCnspaciente(Long cnspaciente) {
		this.cnspaciente = cnspaciente;
	}
	public String getNomepaciente() {
		return nomepaciente;
	}
	public void setNomepaciente(String nomepaciente) {
		this.nomepaciente = nomepaciente;
	}
	public Date getDtnascimento() {
		return dtnascimento;
	}
	public void setDtnascimento(Date dtnascimento) {
		this.dtnascimento = dtnascimento;
	}
	public Byte getRaca() {
		return raca;
	}
	public void setRaca(Byte raca) {
		this.raca = raca;
	}
	public Byte getCarateratendimento() {
		return carateratendimento;
	}
	public void setCarateratendimento(Byte carateratendimento) {
		this.carateratendimento = carateratendimento;
	}
	public Long getNroautorizacao() {
		return nroautorizacao;
	}
	public void setNroautorizacao(Long nroautorizacao) {
		this.nroautorizacao = nroautorizacao;
	}
	public String getSexo() {
		return sexo;
	}
	
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	
	public Integer getCodibge() {
		return codibge;
	}
	public void setCodibge(Integer codibge) {
		this.codibge = codibge;
	}
	public String getCid10() {
		return cid10;
	}
	public void setCid10(String cid10) {
		this.cid10 = cid10;
	}
	public Short getIdade() {
		return idade;
	}
	public void setIdade(Short idade) {
		this.idade = idade;
	}
	public Integer getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Integer competencia) {
		this.competencia = competencia;
	}
	public Short getIphphoseq() {
		return iphphoseq;
	}
	public void setIphphoseq(Short iphphoseq) {
		this.iphphoseq = iphphoseq;
	}
	public Integer getIphseq() {
		return iphseq;
	}
	public void setIphseq(Integer iphseq) {
		this.iphseq = iphseq;
	}
	public Integer getFccseq() {
		return fccseq;
	}
	public void setFccseq(Integer fccseq) {
		this.fccseq = fccseq;
	}
	public Integer getFcfseq() {
		return fcfseq;
	}
	public void setFcfseq(Integer fcfseq) {
		this.fcfseq = fcfseq;
	}
	public String getOrigeminf() {
		return origeminf;
	}
	public void setOrigeminf(String origeminf) {
		this.origeminf = origeminf;
	}
	public Integer getQuantidadesumarizada() {
		return quantidadesumarizada;
	}
	public void setQuantidadesumarizada(Integer quantidadesumarizada) {
		this.quantidadesumarizada = quantidadesumarizada;
	}
	public Double getVlranestes() {
		return vlranestes;
	}
	public void setVlranestes(Double vlranestes) {
		this.vlranestes = vlranestes;
	}
	public Double getVlrproc() {
		return vlrproc;
	}
	public void setVlrproc(Double vlrproc) {
		this.vlrproc = vlrproc;
	}
	public Double getVlrsadt() {
		return vlrsadt;
	}
	public void setVlrsadt(Double vlrsadt) {
		this.vlrsadt = vlrsadt;
	}
	public Double getVlrservhosp() {
		return vlrservhosp;
	}
	public void setVlrservhosp(Double vlrservhosp) {
		this.vlrservhosp = vlrservhosp;
	}
	public Double getVlrservprof() {
		return vlrservprof;
	}
	public void setVlrservprof(Double vlrservprof) {
		this.vlrservprof = vlrservprof;
	}
	public Integer getNacionalidade() {
		return nacionalidade;
	}
	public void setNacionalidade(Integer nacionalidade) {
		this.nacionalidade = nacionalidade;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public String getServico() {
		return servico;
	}
	public void setServico(String servico) {
		this.servico = servico;
	}
	public String getClassificacao() {
		return classificacao;
	}
	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}
	public Short getUnidadefuncional() {
		return unidadefuncional;
	}
	public void setUnidadefuncional(Short unidadefuncional) {
		this.unidadefuncional = unidadefuncional;
	}
	public String getServClass() {
		return servClass;
	}
	public void setServClass(String servClass) {
		this.servClass = servClass;
	}
	
	/**
	 * eSchweigert 12/08/2013
	 * Não alterar, esta aplicando campos para agrupamento
	 */
	@Override
	public int hashCode() {

		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(carateratendimento);
		umHashCodeBuilder.append(cid10);
		umHashCodeBuilder.append(classificacao);
		umHashCodeBuilder.append(cnscbo);
		umHashCodeBuilder.append(cnsmedico);
		umHashCodeBuilder.append(cnspaciente);
		umHashCodeBuilder.append(codatvprof);
		umHashCodeBuilder.append(codibge);
		umHashCodeBuilder.append(competencia);
		umHashCodeBuilder.append(dataatendimento);
		umHashCodeBuilder.append(dtnascimento);
		umHashCodeBuilder.append(fccseq);
		umHashCodeBuilder.append(fcfseq);
		umHashCodeBuilder.append(idade);
		umHashCodeBuilder.append(iphphoseq);
		umHashCodeBuilder.append(iphseq);
		umHashCodeBuilder.append(nacionalidade);
		umHashCodeBuilder.append(nomepaciente);
		umHashCodeBuilder.append(nroautorizacao);
		umHashCodeBuilder.append(origeminf);
		umHashCodeBuilder.append(pacCodigo);
		umHashCodeBuilder.append(procedimentohosp);
		umHashCodeBuilder.append(raca);
		umHashCodeBuilder.append(servico);
		umHashCodeBuilder.append(sexo);
		umHashCodeBuilder.append(servClass);
		
		return umHashCodeBuilder.toHashCode();
	}

	/**
	 * eSchweigert 12/08/2013
	 * Não alterar, esta aplicando campos para agrupamento
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CursorEspelhoBpiVO)) {
			return false;
		}
		

		CursorEspelhoBpiVO other = (CursorEspelhoBpiVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(carateratendimento, other.carateratendimento);
		umEqualsBuilder.append(cid10, other.cid10);
		umEqualsBuilder.append(classificacao, other.classificacao);
		umEqualsBuilder.append(cnscbo, other.cnscbo);
		umEqualsBuilder.append(cnsmedico, other.cnsmedico);
		umEqualsBuilder.append(cnspaciente, other.cnspaciente);
		umEqualsBuilder.append(codatvprof, other.codatvprof);
		umEqualsBuilder.append(codibge, other.codibge);
		umEqualsBuilder.append(competencia, other.competencia);
		umEqualsBuilder.append(dataatendimento, other.dataatendimento);
		umEqualsBuilder.append(dtnascimento, other.dtnascimento);
		umEqualsBuilder.append(fccseq, other.fccseq);
		umEqualsBuilder.append(fcfseq, other.fcfseq);
		umEqualsBuilder.append(idade, other.idade);
		umEqualsBuilder.append(iphphoseq, other.iphphoseq);
		umEqualsBuilder.append(iphseq, other.iphseq);
		umEqualsBuilder.append(nacionalidade, other.nacionalidade);
		umEqualsBuilder.append(nomepaciente, other.nomepaciente);
		umEqualsBuilder.append(nroautorizacao, other.nroautorizacao);
		umEqualsBuilder.append(origeminf, other.origeminf);
		umEqualsBuilder.append(pacCodigo, other.pacCodigo);
		umEqualsBuilder.append(procedimentohosp, other.procedimentohosp);
		umEqualsBuilder.append(raca, other.raca);
		umEqualsBuilder.append(servico, other.servico);
		umEqualsBuilder.append(sexo, other.sexo);
		umEqualsBuilder.append(servClass, other.servClass);
		
		return umEqualsBuilder.isEquals();
	}
	
	@Override
	public String toString() {
		return "CursorEspelhoBpiVO [cnscbo=" + cnscbo + ", procedimentohosp="
				+ procedimentohosp + ", codatvprof=" + codatvprof
				+ ", cnsmedico=" + cnsmedico + ", dataatendimento="
				+ dataatendimento + ", cnspaciente=" + cnspaciente
				+ ", pacCodigo=" + pacCodigo + ", nomepaciente=" + nomepaciente
				+ ", dtnascimento=" + dtnascimento + ", raca=" + raca
				+ ", nacionalidade=" + nacionalidade + ", carateratendimento="
				+ carateratendimento + ", nroautorizacao=" + nroautorizacao
				+ ", sexo=" + sexo + ", codibge=" + codibge + ", cid10="
				+ cid10 + ", idade=" + idade + ", competencia=" + competencia
				+ ", iphphoseq=" + iphphoseq + ", iphseq=" + iphseq
				+ ", fccseq=" + fccseq + ", fcfseq=" + fcfseq + ", origeminf="
				+ origeminf + ", servico=" + servico + ", classificacao="
				+ classificacao + ", servClass=" + servClass + "]";
	}
	
	
	
}