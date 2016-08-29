package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioIndTempo;
import br.gov.mec.aghu.dominio.DominioOperacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;

@Entity
@SequenceGenerator(name = "sigDirSq1", sequenceName = "SIG_DIR_SQ1", allocationSize = 1)
@Table(name = "SIG_DIRECIONADORES", schema = "AGH")
public class SigDirecionadores extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4656706832724528494L;
	
	private Integer seq;
	private String nome;
	private String descricao;
	private DominioOperacao operacao;
	private DominioTipoDirecionadorCustos indTipo;
	private Date criadoEm;
	private RapServidores servidorResp;
	private DominioSituacao indSituacao;
	private DominioIndTempo indTempo;
	private Boolean indNroExecucoes;
	private Boolean indColetaSistema;
	private DominioTipoCalculoObjeto indTipoCalculo;
	private BigDecimal fatConvHoras;
	private String nomeView;
	private String filtro;
	private Integer version;
	
	
	public SigDirecionadores(){
		
	}
	
	public SigDirecionadores(Integer seq){
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigDirSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "NOME", nullable = false, length = 50)
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "DESCRICAO", nullable = true, length = 2000)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "OPERACAO", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioOperacao getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacao operacao) {
		this.operacao = operacao;
	}

	@Column(name = "IND_TIPO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoDirecionadorCustos getIndTipo() {
		return indTipo;
	}

	public void setIndTipo(DominioTipoDirecionadorCustos indTipo) {
		this.indTipo = indTipo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorResp() {
		return this.servidorResp;
	}

	public void setServidorResp(RapServidores servidorResp) {
		this.servidorResp = servidorResp;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_TEMPO", nullable = true, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioIndTempo getIndTempo() {
		return indTempo;
	}

	public void setIndTempo(DominioIndTempo indTempo) {
		this.indTempo = indTempo;
	}

	@Column(name = "IND_NRO_EXECUCOES", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndNroExecucoes() {
		return indNroExecucoes;
	}

	public void setIndNroExecucoes(Boolean indNroExecucoes) {
		this.indNroExecucoes = indNroExecucoes;
	}
	
	
	@Column(name = "IND_COLETA_SISTEMA", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndColetaSistema() {
		return indColetaSistema;
	}

	public void setIndColetaSistema(Boolean indColetaSistema) {
		this.indColetaSistema = indColetaSistema;
	}

	@Column(name = "IND_TIPO_CALCULO", nullable = true, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoCalculoObjeto getIndTipoCalculo() {
		return indTipoCalculo;
	}

	public void setIndTipoCalculo(DominioTipoCalculoObjeto indTipoCalculo) {
		this.indTipoCalculo = indTipoCalculo;
	}

	@Column(name = "FAT_CONV_HORAS", nullable = true, precision = 20, scale = 6)
	public BigDecimal getFatConvHoras() {
		return fatConvHoras;
	}

	public void setFatConvHoras(BigDecimal fatConvHoras) {
		this.fatConvHoras = fatConvHoras;
	}
	
	@Column(name = "NOME_VIEW", nullable = true, length = 30)
	public String getNomeView() {
		return nomeView;
	}

	public void setNomeView(String nomeView) {
		this.nomeView = nomeView;
	}

	@Column(name = "FILTRO", nullable = true, length = 20)
	public String getFiltro() {
		return filtro;
	}

	public void setFiltro(String filtro) {
		this.filtro = filtro;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {

		SEQ("seq"),
		NOME("nome"),
		DESCRICAO("descricao"),
		OPERACAO("operacao"),
		INDICADOR_TIPO("indTipo"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("servidorResp"),
		INDICADOR_SITUACAO("indSituacao"),
		INDICADOR_TEMPO("indTempo"),
		INDICADOR_NRO_EXECUCOES("indNroExecucoes"),
		INDICADOR_TIPO_CALCULO_OBJETO("indTipoCalculo"),
		INDICADOR_COLETA_SISTEMA("indColetaSistema"),
		FAT_CONV_HORAS("fatConvHoras"),
		NOME_VIEW("nomeView"),
		FILTRO("filtro");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigDirecionadores)) {
			return false;
		}
		SigDirecionadores castOther = (SigDirecionadores) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
