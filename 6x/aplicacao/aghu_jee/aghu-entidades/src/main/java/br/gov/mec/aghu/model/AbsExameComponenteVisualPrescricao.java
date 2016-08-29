package br.gov.mec.aghu.model;

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

import br.gov.mec.aghu.dominio.DominioUnidadeTempo;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name = "absEcvSq1", sequenceName = "AGH.ABS_ECV_SQ1", allocationSize = 1)
@Table(name = "ABS_EXAMES_COMP_VISUAL_PRCR", schema = "AGH")
public class AbsExameComponenteVisualPrescricao extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5328826352810589490L;

//	private enum AbsExameCompVisualPrescricaoExceptionCode implements
//			BusinessExceptionCode {
//		ABS_ECV_CK1, ABS_ECV_CK2, ABS_ECV_CK3, ABS_ECV_CK4, ABS_ECV_CK5, ABS_ECV_CK6;
//	}

	private Integer seq;

	private AelCampoLaudo campoLaudo;
	
	private AbsComponenteSanguineo componenteSanguineo;

	private AbsProcedHemoterapico procedimentoHemoterapico;

	private Long valorMinimo;

	private Long valorMaximo;

	private Byte quantidadeCasasDecimais;

	private Short tempo;

	private DominioUnidadeTempo unidadeTempo;

	private Short idadeMinima;

	private DominioUnidadeTempo unidadeTempoIdadeMinima;

	private Short idadeMaxima;

	private DominioUnidadeTempo unidadeTempoIdadeMaxima;

	private Date criadoEm;

	private Date alteradoEm;

	private RapServidores servidor;

	private RapServidores servidorAlterado;

	public AbsExameComponenteVisualPrescricao() {
	}

	public AbsExameComponenteVisualPrescricao(Integer seq, AelCampoLaudo campoLaudo,
			Long valorMinimo, Long valorMaximo, Byte quantidadeCasasDecimais,
			Short tempo, DominioUnidadeTempo unidadeTempo, Short idadeMinima,
			DominioUnidadeTempo unidadeTempoIdadeMinima, Short idadeMaxima,
			DominioUnidadeTempo unidadeTempoIdadeMaxima, Date criadoEm, Date alteradoEm,
			AbsComponenteSanguineo componenteSanguineo,
			AbsProcedHemoterapico procedimentoHemoterapico,
			RapServidores servidor, RapServidores servidorAlterado) {
		this.seq = seq;
		this.campoLaudo = campoLaudo;
		this.componenteSanguineo = componenteSanguineo;
		this.procedimentoHemoterapico = procedimentoHemoterapico;
		this.valorMinimo = valorMinimo;
		this.valorMaximo = valorMaximo;
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
		this.tempo = tempo;
		this.unidadeTempo = unidadeTempo;
		this.idadeMinima = idadeMinima;
		this.unidadeTempoIdadeMinima = unidadeTempoIdadeMinima;
		this.idadeMaxima = idadeMaxima;
		this.unidadeTempoIdadeMaxima = unidadeTempoIdadeMaxima;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.servidor = servidor;
		this.servidorAlterado = servidorAlterado;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "absEcvSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAL_SEQ", nullable = false)	
	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}


	@Column(name = "VALOR_MINIMO", precision = 13, scale = 0)
	public Long getValorMinimo() {
		return this.valorMinimo;
	}

	public void setValorMinimo(Long valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	@Column(name = "VALOR_MAXIMO", precision = 13, scale = 0)
	public Long getValorMaximo() {
		return this.valorMaximo;
	}

	public void setValorMaximo(Long valorMaximo) {
		this.valorMaximo = valorMaximo;
	}

	@Column(name = "QTDE_CASAS_DECIMAIS", precision = 2, scale = 0)
	public Byte getQuantidadeCasasDecimais() {
		return this.quantidadeCasasDecimais;
	}

	public void setQuantidadeCasasDecimais(Byte quantidadeCasasDecimais) {
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
	}

	@Column(name = "TEMPO", precision = 3, scale = 0)
	public Short getTempo() {
		return this.tempo;
	}

	public void setTempo(Short tempo) {
		this.tempo = tempo;
	}

	@Column(name = "UNIDADE_TEMPO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioUnidadeTempo getUnidadeTempo() {
		return this.unidadeTempo;
	}

	public void setUnidadeTempo(DominioUnidadeTempo unidadeTempo) {
		this.unidadeTempo = unidadeTempo;
	}

	@Column(name = "IDADE_MINIMA", precision = 3, scale = 0)
	public Short getIdadeMinima() {
		return this.idadeMinima;
	}

	public void setIdadeMinima(Short idadeMinima) {
		this.idadeMinima = idadeMinima;
	}

	@Column(name = "UNID_TEMPO_ID_MIN", length = 1)	
	@Enumerated(EnumType.STRING)
	public DominioUnidadeTempo getUnidadeTempoIdadeMinima() {
		return this.unidadeTempoIdadeMinima;
	}

	public void setUnidadeTempoIdadeMinima(DominioUnidadeTempo unidadeTempoIdadeMinima) {
		this.unidadeTempoIdadeMinima = unidadeTempoIdadeMinima;
	}

	@Column(name = "IDADE_MAXIMA", precision = 3, scale = 0)
	public Short getIdadeMaxima() {
		return this.idadeMaxima;
	}

	public void setIdadeMaxima(Short idadeMaxima) {
		this.idadeMaxima = idadeMaxima;
	}

	@Column(name = "UNID_TEMPO_ID_MAX", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioUnidadeTempo getUnidadeTempoIdadeMaxima() {
		return this.unidadeTempoIdadeMaxima;
	}

	public void setUnidadeTempoIdadeMaxima(DominioUnidadeTempo unidadeTempoIdadeMaxima) {
		this.unidadeTempoIdadeMaxima = unidadeTempoIdadeMaxima;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}

	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CSA_CODIGO", referencedColumnName = "CODIGO")
	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(
			AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHE_CODIGO", referencedColumnName = "CODIGO")
	public AbsProcedHemoterapico getProcedimentoHemoterapico() {
		return procedimentoHemoterapico;
	}

	public void setProcedimentoHemoterapico(
			AbsProcedHemoterapico procedimentoHemoterapico) {
		this.procedimentoHemoterapico = procedimentoHemoterapico;
	}

	

	public enum Fields {
		
		SEQ("seq"),
		CAMPO_LAUDO("campoLaudo"),
		COMPONENTE_SANGUINEO("componenteSanguineo"),
		COMPONENTE_SANGUINEO_CODIGO("componenteSanguineo.codigo"),
		PROCEDIMENTO_HEMOTERAPICO("procedimentoHemoterapico"),
		PROCEDIMENTO_HEMOTERAPICO_CODIGO("procedimentoHemoterapico.codigo"),
		VALOR_MINIMO("valorMinimo"),
		VALOR_MAXIMO("valorMaximo"),
		QUANTIDADE_CASAS_DECIMAIS("quantidadeCasasDecimais"),
		TEMPO("tempo"),
		UNIDADE_TEMPO("unidadeTempo"),
		IDADE_MINIMA("idadeMinima"),
		UNIDADE_TEMPO_IDADE_MINIMA("unidadeTempoIdadeMinima"),
		IDADE_MAXIMA("idadeMaxima"),
		UNIDADE_TEMPO_IDADE_MAXIMA("unidadeTempoIdadeMaxima"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SERVIDOR("servidor"),
		SERVIDOR_ALTERADO("servidorAlterado");
	 	
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		AbsExameComponenteVisualPrescricao other = (AbsExameComponenteVisualPrescricao) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

}
