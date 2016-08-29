package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="aelAqiSq1", sequenceName="AEL_AQI_SQ1", allocationSize = 1)
@Table(name = "AEL_ARQUIVO_INTEGRACOES", schema = "AGH")
public class AelArquivoIntegracao extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3035637695387193259L;

	private Integer seq;

	private String diretorioEntrada;
	private String nomeArquivoEntrada;
	private String nomeArquivoSaida;
	private Date dataGeracao;
	private Date dataProcessamento;
	private Integer convenio;
	private Integer plano;
	private Integer quantidadeSolicitacoes;
	private Integer totalRecebida;
	private Integer totalGerada;
	private Integer totalRecusada;
	private Integer totalSemAgenda;
	private Date criadoEm;
	private Integer version;

	private RapServidores servidor;

	// getters & setters
	// AEL_AQI_PK
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelAqiSq1")
	@Column(name = "SEQ", length = 9, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DIRETORIO_ENTRADA", length = 200, nullable = false)
	public String getDiretorioEntrada() {
		return this.diretorioEntrada;
	}

	public void setDiretorioEntrada(String diretorioEntrada) {
		this.diretorioEntrada = diretorioEntrada;
	}

	@Column(name = "NOME_ARQUIVO_ENTRADA", length = 60, nullable = false)
	public String getNomeArquivoEntrada() {
		return this.nomeArquivoEntrada;
	}

	public void setNomeArquivoEntrada(String nomeArquivoEntrada) {
		this.nomeArquivoEntrada = nomeArquivoEntrada;
	}

	@Column(name = "NOME_ARQUIVO_SAIDA", length = 60)
	public String getNomeArquivoSaida() {
		return this.nomeArquivoSaida;
	}

	public void setNomeArquivoSaida(String nomeArquivoSaida) {
		this.nomeArquivoSaida = nomeArquivoSaida;
	}

	@Column(name = "DATA_GERACAO")
	public Date getDataGeracao() {
		return this.dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	@Column(name = "DATA_PROCESSAMENTO", nullable = false)
	public Date getDataProcessamento() {
		return this.dataProcessamento;
	}

	public void setDataProcessamento(Date dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}

	@Column(name = "CONVENIO", length = 3)
	public Integer getConvenio() {
		return this.convenio;
	}

	public void setConvenio(Integer convenio) {
		this.convenio = convenio;
	}

	@Column(name = "PLANO", length = 2)
	public Integer getPlano() {
		return this.plano;
	}

	public void setPlano(Integer plano) {
		this.plano = plano;
	}

	@Column(name = "QUANTIDADE_SOLICITACOES", length = 5)
	public Integer getQuantidadeSolicitacoes() {
		return this.quantidadeSolicitacoes;
	}

	public void setQuantidadeSolicitacoes(Integer quantidadeSolicitacoes) {
		this.quantidadeSolicitacoes = quantidadeSolicitacoes;
	}

	@Column(name = "TOTAL_RECEBIDA", length = 5)
	public Integer getTotalRecebida() {
		return this.totalRecebida;
	}

	public void setTotalRecebida(Integer totalRecebida) {
		this.totalRecebida = totalRecebida;
	}

	@Column(name = "TOTAL_GERADA", length = 5)
	public Integer getTotalGerada() {
		return this.totalGerada;
	}

	public void setTotalGerada(Integer totalGerada) {
		this.totalGerada = totalGerada;
	}

	@Column(name = "TOTAL_RECUSADA", length = 5)
	public Integer getTotalRecusada() {
		return this.totalRecusada;
	}

	public void setTotalRecusada(Integer totalRecusada) {
		this.totalRecusada = totalRecusada;
	}

	@Column(name = "TOTAL_SEM_AGENDA", length = 5)
	public Integer getTotalSemAgenda() {
		return this.totalSemAgenda;
	}

	public void setTotalSemAgenda(Integer totalSemAgenda) {
		this.totalSemAgenda = totalSemAgenda;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	@Column(name = "VERSION", length = 9, nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	// outros

	@Transient
	public String nomeArquivoEntradaSemExtensao(){
		if(this.nomeArquivoEntrada != null){
			return StringUtils.substring(this.nomeArquivoEntrada, 0,this.nomeArquivoEntrada.length() - 4);
		}		
		return null;
	}

	@Transient
	public String nomeArquivoSaidaSemExtensao(){
		if(this.nomeArquivoSaida != null){
			return StringUtils.substring(this.nomeArquivoSaida, 0,this.nomeArquivoSaida.length() - 4);
		}		
		return null;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean equals(Object other) {
		if (!(other instanceof AelArquivoIntegracao)){
			return false;
		}
		AelArquivoIntegracao castOther = (AelArquivoIntegracao) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), DIRETORIO_ENTRADA("diretorioEntrada"), NOME_ARQUIVO_ENTRADA(
				"nomeArquivoEntrada"), NOME_ARQUIVO_SAIDA("nomeArquivoSaida"), DATA_GERACAO(
				"dataGeracao"), DATA_PROCESSAMENTO("dataProcessamento"), CONVENIO(
				"convenio"), PLANO("plano"), QUANTIDADE_SOLICITACOES(
				"quantidadeSolicitacoes"), TOTAL_RECEBIDA("totalRecebida"), TOTAL_GERADA(
				"totalGerada"), TOTAL_RECUSADA("totalRecusada"), TOTAL_SEM_AGENDA(
				"totalSemAgenda"), CRIADO_EM("criadoEm"), VERSION("version"),

		RAP_SERVIDORES("servidor"), ;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		public String toString() {
			return this.field;
		}
	}

}