package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;

public class PrescricaoMedicamentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 430392287262135383L;
	/**
	 * 
	 */
	
	
	private Integer atdSeq;
	private Long seq;
	private Integer pmdAtdSeq;
	private Long pmdSeq;
	private String vad;
	private Boolean indSeNecessario;
	private Date horaInicioAdministracao;
	private String observacao;
	private BigDecimal gotejo;
	private Short qtdeHorasCorrer;
	private Date dthrInicio;
	private Short tipoVelcAdmSeq;
	private DominioIndPendenteItemPrescricao indPendente;
	private Date dthrFim;
	private Short duracaoTratSolicitado;
	private Short frequencia;
	private Short tipoFreqAprzSeq;
	
	public PrescricaoMedicamentoVO() {
	}

	public PrescricaoMedicamentoVO(Integer atdSeq, Long seq, Integer pmdAtdSeq,
			Long pmdSeq, String vad, Boolean indSeNecessario,
			Date horaInicioAdministracao, String observacao, BigDecimal gotejo,
			Short qtdeHorasCorrer, Date dthrInicio, Short tipoVelcAdmSeq,
			DominioIndPendenteItemPrescricao indPendente, Date dthrFim,
			Short duracaoTratSolicitado, Short frequencia, Short tipoFreqAprzSeq) {
		super();
		this.atdSeq = atdSeq;
		this.seq = seq;
		this.pmdAtdSeq = pmdAtdSeq;
		this.pmdSeq = pmdSeq;
		this.vad = vad;
		this.indSeNecessario = indSeNecessario;
		this.horaInicioAdministracao = horaInicioAdministracao;
		this.observacao = observacao;
		this.gotejo = gotejo;
		this.qtdeHorasCorrer = qtdeHorasCorrer;
		this.dthrInicio = dthrInicio;
		this.tipoVelcAdmSeq = tipoVelcAdmSeq;
		this.indPendente = indPendente;
		this.dthrFim = dthrFim;
		this.duracaoTratSolicitado = duracaoTratSolicitado;
		this.frequencia = frequencia;
		this.tipoFreqAprzSeq = tipoFreqAprzSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}
	
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	
	public Long getSeq() {
		return seq;
	}
	
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	public Date getDthrInicio() {
		return dthrInicio;
	}
	
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
	
	public DominioIndPendenteItemPrescricao getIndPendente() {
		return indPendente;
	}
	
	public void setIndPendente(DominioIndPendenteItemPrescricao indPendente) {
		this.indPendente = indPendente;
	}
	
	public Date getDthrFim() {
		return dthrFim;
	}
	
	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	
	public Short getDuracaoTratSolicitado() {
		return duracaoTratSolicitado;
	}
	
	public void setDuracaoTratSolicitado(Short duracaoTratSolicitado) {
		this.duracaoTratSolicitado = duracaoTratSolicitado;
	}

	public Short getTipoVelcAdmSeq() {
		return tipoVelcAdmSeq;
	}

	public void setTipoVelcAdmSeq(Short tipoVelcAdmSeq) {
		this.tipoVelcAdmSeq = tipoVelcAdmSeq;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public Short getTipoFreqAprzSeq() {
		return tipoFreqAprzSeq;
	}

	public void setTipoFreqAprzSeq(Short tipoFreqAprzSeq) {
		this.tipoFreqAprzSeq = tipoFreqAprzSeq;
	}

	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}

	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}

	public Long getPmdSeq() {
		return pmdSeq;
	}

	public void setPmdSeq(Long pmdSeq) {
		this.pmdSeq = pmdSeq;
	}

	public String getVad() {
		return vad;
	}

	public void setVad(String vad) {
		this.vad = vad;
	}

	public Boolean getIndSeNecessario() {
		return indSeNecessario;
	}

	public void setIndSeNecessario(Boolean indSeNecessario) {
		this.indSeNecessario = indSeNecessario;
	}

	public Date getHoraInicioAdministracao() {
		return horaInicioAdministracao;
	}

	public void setHoraInicioAdministracao(Date horaInicioAdministracao) {
		this.horaInicioAdministracao = horaInicioAdministracao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public BigDecimal getGotejo() {
		return gotejo;
	}

	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}

	public Short getQtdeHorasCorrer() {
		return qtdeHorasCorrer;
	}

	public void setQtdeHorasCorrer(Short qtdeHorasCorrer) {
		this.qtdeHorasCorrer = qtdeHorasCorrer;
	}



	public enum Fields {
		ATD_SEQ("atdSeq"),
		SEQ("seq"),
		PRESCRICAOMDTOORIGEM_ATDSEQ("pmdAtdSeq"),
		VAD("vad"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		IND_PENDENTE("indPendente"),
		DURACAO_TRAT_SOL("duracaoTratSolicitado"),
		FREQUENCIA("frequencia"),
		TIPO_VELC_ADM_SEQ("tipoVelcAdmSeq"),
		TIPO_FREQ_APRZ_SEQ("tipoFreqAprzSeq"),
		INDSENECESSARIO("indSeNecessario"),
		HORAINICIOADMINISTRACAO("horaInicioAdministracao"),
		OBSERVACAO("observacao"),
		GOTEJO("gotejo"),
		QTDEHORASCORRER("qtdeHorasCorrer");

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
