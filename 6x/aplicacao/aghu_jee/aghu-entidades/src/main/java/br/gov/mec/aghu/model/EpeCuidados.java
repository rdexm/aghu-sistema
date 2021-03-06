package br.gov.mec.aghu.model;

// Generated 03/02/2011 17:19:32 by Hibernate Tools 3.2.5.Beta

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;





import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * EpeCuidados generated by hbm2java
 */
@Entity
@SequenceGenerator(name="epeCuiSq1", sequenceName="AGH.EPE_CUI_SQ1", allocationSize = 1)
@Table(name = "EPE_CUIDADOS", schema = "AGH")
public class EpeCuidados extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1305825793719464277L;
	private Short seq;
	private String descricao;
	private Boolean indSemDiagnostico;
	private DominioSituacao indSituacao;
	private Boolean indDigitaComplemento;
	private Date criadoEm;
	private RapServidores servidor;
	private String rotina;
	private String informacoesAdicionais;
	private Short frequencia;
	private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;
	private Boolean indCci;
	private Boolean indRotina;
	private Date tempo;
	private Date alteradoEm;
	private RapServidores rapServidorMovimentado;
	
	private Set<EpePrescricoesCuidados> prescricoesCuidados = new HashSet<EpePrescricoesCuidados>(0);
	private Set<EpeCuidadoDiagnostico> cuidadosDiagnosticos = new HashSet<EpeCuidadoDiagnostico>(0);
	private Set<EpeCuidadoMedicamento> cuidadosMedicamentos = new HashSet<EpeCuidadoMedicamento>(0);

//	private Short seqEsperanto;
	
	public EpeCuidados() {
	}

	public EpeCuidados(Short seq, String descricao, Boolean indSemDiagnostico,
			DominioSituacao indSituacao, Boolean indDigitaComplemento,
			Date criadoEm, RapServidores servidor, String rotina,
			String informacoesAdicionais, Short frequencia,
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento,
			Boolean indCci, Boolean indRotina, Date tempo,
			Date alteradoEm, RapServidores rapServidorMovimentado,
			Set<EpePrescricoesCuidados> prescricoesCuidados) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSemDiagnostico = indSemDiagnostico;
		this.indSituacao = indSituacao;
		this.indDigitaComplemento = indDigitaComplemento;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.rotina = rotina;
		this.informacoesAdicionais = informacoesAdicionais;
		this.frequencia = frequencia;
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
		this.indCci = indCci;
		this.indRotina = indRotina;
		this.tempo = tempo;
		this.alteradoEm = alteradoEm;
		this.rapServidorMovimentado = rapServidorMovimentado;
		this.prescricoesCuidados = prescricoesCuidados;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "epeCuiSq1")
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 200)
	@Length(max = 200)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SEM_DIAGNOSTICO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSemDiagnostico() {
		return this.indSemDiagnostico;
	}

	public void setIndSemDiagnostico(Boolean indSemDiagnostico) {
		this.indSemDiagnostico = indSemDiagnostico;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_DIGITA_COMPLEMENTO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDigitaComplemento() {
		return this.indDigitaComplemento;
	}

	public void setIndDigitaComplemento(Boolean indDigitaComplemento) {
		this.indDigitaComplemento = indDigitaComplemento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "ROTINA")
	public String getRotina() {
		return this.rotina;
	}

	public void setRotina(String rotina) {
		this.rotina = rotina;
	}

	@Column(name = "INFORMACOES_ADICIONAIS", length = 2000)
	@Length(max = 2000)
	public String getInformacoesAdicionais() {
		return this.informacoesAdicionais;
	}

	public void setInformacoesAdicionais(String informacoesAdicionais) {
		this.informacoesAdicionais = informacoesAdicionais;
	}

	@Column(name = "FREQUENCIA", precision = 3, scale = 0)
	public Short getFrequencia() {
		return this.frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TFQ_SEQ")
	public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
		return tipoFrequenciaAprazamento;
	}

	public void setTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
	}

	@Column(name = "IND_CCI", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCci() {
		return this.indCci;
	}

	public void setIndCci(Boolean indCci) {
		this.indCci = indCci;
	}

	@Column(name = "IND_ROTINA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRotina() {
		return this.indRotina;
	}

	public void setIndRotina(Boolean indRotina) {
		this.indRotina = indRotina;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO")
	public Date getTempo() {
		return this.tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( { @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidorMovimentado() {
		return rapServidorMovimentado;
	}

	public void setRapServidorMovimentado(RapServidores rapServidorMovimentado) {
		this.rapServidorMovimentado = rapServidorMovimentado;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cuidado")
	public Set<EpePrescricoesCuidados> getPrescricoesCuidados() {
		return this.prescricoesCuidados;
	}

	public void setPrescricoesCuidados(
			Set<EpePrescricoesCuidados> prescricoesCuidados) {
		this.prescricoesCuidados = prescricoesCuidados;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cuidado")
	public Set<EpeCuidadoDiagnostico> getCuidadosDiagnosticos() {
		return this.cuidadosDiagnosticos;
	}	
	
	public void setCuidadosDiagnosticos(
			Set<EpeCuidadoDiagnostico> cuidadosDiagnosticos) {
		this.cuidadosDiagnosticos = cuidadosDiagnosticos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cuidado")
	public Set<EpeCuidadoMedicamento> getCuidadosMedicamentos() {
		return this.cuidadosMedicamentos;
	}

	public void setCuidadosMedicamentos(Set<EpeCuidadoMedicamento> cuidadosMedicamentos) {
		this.cuidadosMedicamentos = cuidadosMedicamentos;
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_CCI("indCci"),
		IND_SEM_DIAGNOSTICO("indSemDiagnostico"),
		IND_SITUACAO("indSituacao"),
		CUIDADOS_DIAGNOSTICOS("cuidadosDiagnosticos"),
		CUIDADOS_MEDICAMENTOS("cuidadosMedicamentos"),
		PRESCRICAO_CUIDADOS("prescricoesCuidados"),
		TIPO_FREQUENCIA_APRAZAMENTO ("tipoFrequenciaAprazamento"),
		IND_ROTINA("indRotina");
		

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		EpeCuidados other = (EpeCuidados) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

//	@Column(name = "SEQ_ESPERANTO", nullable = true, precision = 4, scale = 0)
//	public Short getSeqEsperanto() {
//		return seqEsperanto;
//	}
//
//	public void setSeqEsperanto(Short seqEsperanto) {
//		this.seqEsperanto = seqEsperanto;
//	}	
	
}
