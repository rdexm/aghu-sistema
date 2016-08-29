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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioDiasMes;
import br.gov.mec.aghu.dominio.DominioFatoGerador;
import br.gov.mec.aghu.dominio.DominioMesVencimento;
import br.gov.mec.aghu.dominio.DominioTipoTributoVencimento;
import br.gov.mec.aghu.dominio.DominioVencimentoDiaNaoUtil;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aghFcpClvJnSeq", sequenceName="AGH.FCP_CLV_JN_SEQ", allocationSize = 1)
@Table(name = "FCP_CALENDARIO_VENCIMENTO_JN", schema = "AGH")
@Immutable
public class FcpCalendarioVencimentoJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -3827369595096216968L;
	
	private Integer seq;
	private DominioTipoTributoVencimento tipoTributo;
	private Date inicioVigencia;
	private DominioFatoGerador fatoGerador;
	private DominioVencimentoDiaNaoUtil vencimentoDiaNaoUtil;
	private DominioDiasMes inicioPeriodo;
	private DominioDiasMes fimPeriodo;
	private DominioDiasMes diaVencimento;
	private DominioMesVencimento mesVencimento;
	private String observacao;

	public FcpCalendarioVencimentoJn() {
	}

	public FcpCalendarioVencimentoJn(Integer seq, DominioTipoTributoVencimento tipoTributo, Date inicioVigencia, DominioFatoGerador fatoGerador,
			DominioVencimentoDiaNaoUtil vencimentoDiaNaoUtil, DominioDiasMes inicioPeriodo, DominioDiasMes fimPeriodo, DominioDiasMes diaVencimento,
			DominioMesVencimento mesVencimento, String observacao) {
		this.seq = seq;
		this.tipoTributo = tipoTributo;
		this.inicioVigencia = inicioVigencia;
		this.fatoGerador = fatoGerador;
		this.vencimentoDiaNaoUtil = vencimentoDiaNaoUtil;
		this.inicioPeriodo = inicioPeriodo;
		this.fimPeriodo = fimPeriodo;
		this.diaVencimento = diaVencimento;
		this.mesVencimento = mesVencimento;
		this.observacao = observacao;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aghFcpClvJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "TIPO_TRIBUTO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoTributoVencimento getTipoTributo() {
		return tipoTributo;
	}

	public void setTipoTributo(DominioTipoTributoVencimento tipoTributo) {
		this.tipoTributo = tipoTributo;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "INICIO_VIGENCIA", nullable = false, length = 29)
	public Date getInicioVigencia() {
		return inicioVigencia;
	}

	public void setInicioVigencia(Date inicioVigencia) {
		this.inicioVigencia = inicioVigencia;
	}

	@Column(name = "FATO_GERADOR", length = 3)
	@Enumerated(EnumType.STRING)
	public DominioFatoGerador getFatoGerador() {
		return fatoGerador;
	}

	public void setFatoGerador(DominioFatoGerador fatoGerador) {
		this.fatoGerador = fatoGerador;
	}

	@Column(name = "VENCIMENTO_DIA_NAO_UTIL", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioVencimentoDiaNaoUtil getVencimentoDiaNaoUtil() {
		return vencimentoDiaNaoUtil;
	}

	public void setVencimentoDiaNaoUtil(
			DominioVencimentoDiaNaoUtil vencimentoDiaNaoUtil) {
		this.vencimentoDiaNaoUtil = vencimentoDiaNaoUtil;
	}

	@Column(name = "INICIO_PERIODO")
	@Enumerated(EnumType.STRING)
	public DominioDiasMes getInicioPeriodo() {
		return inicioPeriodo;
	}

	public void setInicioPeriodo(DominioDiasMes inicioPeriodo) {
		this.inicioPeriodo = inicioPeriodo;
	}

	@Column(name = "FIM_PERIODO")
	@Enumerated(EnumType.STRING)
	public DominioDiasMes getFimPeriodo() {
		return fimPeriodo;
	}

	public void setFimPeriodo(DominioDiasMes fimPeriodo) {
		this.fimPeriodo = fimPeriodo;
	}

	@Column(name = "DIA_VENCIMENTO")
	@Enumerated(EnumType.STRING)
	public DominioDiasMes getDiaVencimento() {
		return diaVencimento;
	}

	public void setDiaVencimento(DominioDiasMes diaVencimento) {
		this.diaVencimento = diaVencimento;
	}

	@Column(name = "N_MES_VENCIMENTO")
	@Enumerated(EnumType.STRING)
	public DominioMesVencimento getMesVencimento() {
		return mesVencimento;
	}

	public void setMesVencimento(DominioMesVencimento mesVencimento) {
		this.mesVencimento = mesVencimento;
	}

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public enum Fields {

		SEQ("seq"), 
		TIPO_TRIBUTO("tipoTributo"), 
		INICIO_VIGENCIA("inicioVigencia"), 
		FATO_GERADOR("fatoGerador"), 
		VCTO_DIA_NAO_UTIL("vencimentoDiaNaoUtil"),
		INICIO_PERIODO("inicioPeriodo"),
		FIM_PERIODO("fimPeriodo"),
		DIA_VCTO("diaVencimento"),
		MES_VCTO("mesVencimento"),
		OBSERVACAO("observacao");

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
