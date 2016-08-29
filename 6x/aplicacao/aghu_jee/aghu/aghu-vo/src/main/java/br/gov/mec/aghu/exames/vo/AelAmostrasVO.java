package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;


public class AelAmostrasVO implements Serializable  {
	
	private static final long serialVersionUID = -5425223697797219744L;
	
	private Short seqVO;
	private Integer soeSeq;
	private Short seqp;
	private Integer nroUnico;
	private Date dtNumeroUnico;
	private String nroFrascoFabricante;
	private DominioUnidadeMedidaTempo unidTempoIntervaloColeta;
	private BigDecimal tempoIntervaloColeta;
	private DominioSituacaoAmostra situacao;
	private String recipienteColeta;
	private String anticoagulante;
	private Boolean gravar;
	private Boolean emEdicao;
	private Boolean indNroFrascoFornec;
	private Date dthrPrevistaColeta;
	private String paciente;
	private String guiche;
	private String materialAnalise;
	private Integer codigoPacAtd;
	private Integer codigoPacAdd;
	private String nomePacAtd;
	private String nomePacAdd;
	private Short qrtNumero;
	private Short unfSeq;
	private String leito;
	private Integer atdSeq;
	private Integer atdProntuario;
	private Integer atvProntuario;
	private String siglaExame;
	private String descricaoExame;
	private Boolean unidadePatologica;
	

	
	
	public String getPacienteProntuario() {
		StringBuilder sb = new StringBuilder();
		
		if(this.codigoPacAtd != null) {
			sb.append(this.codigoPacAtd);
		}else {
			sb.append(this.codigoPacAdd);
		}
		
		sb.append(" - ");
		
		if(StringUtils.isNotBlank(this.nomePacAtd)) {
			sb.append(this.nomePacAtd);
		} else {
			sb.append(this.nomePacAdd);
		}
		
		return sb.toString();
	}
	
	public Integer getProntuario() {
		/*
		 * 
		 * CASE
            WHEN soe.atd_seq IS NULL THEN atv.prontuario
            ELSE atd.prontuario
        	END AS atd_prontuario, 
		 * 
		 */
		if(this.atdSeq == null) {
			return this.atvProntuario;
		}else {
			return this.atdProntuario;
		}
	}
	
	
	public String getTempoColeta() {
		if(this.unidTempoIntervaloColeta == null && this.tempoIntervaloColeta.compareTo(BigDecimal.ZERO) == 0) {
			return null;
		} else {
			return this.tempoIntervaloColeta.toString().concat(this.unidTempoIntervaloColeta.getDescricao());
		}
		
	}

	public Short getSeqVO() {
		return seqVO;
	}

	public void setSeqVO(Short seqVO) {
		this.seqVO = seqVO;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(final String paciente) {
		this.paciente = paciente;
	}

	public Date getDthrPrevistaColeta() {
		return dthrPrevistaColeta;
	}

	public void setDthrPrevistaColeta(final Date dthrPrevistaColeta) {
		this.dthrPrevistaColeta = dthrPrevistaColeta;
	}

	public Boolean getIndNroFrascoFornec() {
		return indNroFrascoFornec;
	}

	public void setIndNroFrascoFornec(final Boolean indNroFrascoFornec) {
		this.indNroFrascoFornec = indNroFrascoFornec;
	}

	public Integer getNroUnico() {
		return nroUnico;
	}

	public void setNroUnico(final Integer nroUnico) {
		this.nroUnico = nroUnico;
	}

	public Date getDtNumeroUnico() {
		return dtNumeroUnico;
	}

	public void setDtNumeroUnico(final Date dtNumeroUnico) {
		this.dtNumeroUnico = dtNumeroUnico;
	}

	public String getNroFrascoFabricante() {
		return nroFrascoFabricante;
	}

	public void setNroFrascoFabricante(final String nroFrascoFabricante) {
		this.nroFrascoFabricante = nroFrascoFabricante;
	}

	public DominioUnidadeMedidaTempo getUnidTempoIntervaloColeta() {
		return unidTempoIntervaloColeta;
	}

	public void setUnidTempoIntervaloColeta(
			final DominioUnidadeMedidaTempo unidTempoIntervaloColeta) {
		this.unidTempoIntervaloColeta = unidTempoIntervaloColeta;
	}

	public BigDecimal getTempoIntervaloColeta() {
		return tempoIntervaloColeta;
	}

	public void setTempoIntervaloColeta(final BigDecimal tempoIntervaloColeta) {
		this.tempoIntervaloColeta = tempoIntervaloColeta;
	}

	public DominioSituacaoAmostra getSituacao() {
		return situacao;
	}

	public void setSituacao(final DominioSituacaoAmostra situacao) {
		this.situacao = situacao;
	}

	public String getRecipienteColeta() {
		return recipienteColeta;
	}

	public void setRecipienteColeta(final String recipienteColeta) {
		this.recipienteColeta = recipienteColeta;
	}

	public String getAnticoagulante() {
		return anticoagulante;
	}

	public void setAnticoagulante(final String anticoagulante) {
		this.anticoagulante = anticoagulante;
	}

	public Boolean getGravar() {
		return gravar;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(final Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(final Short seqp) {
		this.seqp = seqp;
	}

	public void setGravar(final Boolean gravar) {
		this.gravar = gravar;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(final Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public String getGuiche() {
		return guiche;
	}

	public void setGuiche(final String guiche) {
		this.guiche = guiche;
	}

	public void setMaterialAnalise(final String materialAnalise) {
		this.materialAnalise = materialAnalise;
	}

	public String getMaterialAnalise() {
		return materialAnalise;
	}

	public String getNomePacAtd() {
		return nomePacAtd;
	}

	public void setNomePacAtd(String nomePacAtd) {
		this.nomePacAtd = nomePacAtd;
	}

	public String getNomePacAdd() {
		return nomePacAdd;
	}

	public void setNomePacAdd(String nomePacAdd) {
		this.nomePacAdd = nomePacAdd;
	}

	public Integer getCodigoPacAtd() {
		return codigoPacAtd;
	}

	public void setCodigoPacAtd(Integer codigoPacAtd) {
		this.codigoPacAtd = codigoPacAtd;
	}

	public Integer getCodigoPacAdd() {
		return codigoPacAdd;
	}

	public void setCodigoPacAdd(Integer codigoPacAdd) {
		this.codigoPacAdd = codigoPacAdd;
	}

	public Short getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtdProntuario() {
		return atdProntuario;
	}

	public void setAtdProntuario(Integer atdProntuario) {
		this.atdProntuario = atdProntuario;
	}

	public Integer getAtvProntuario() {
		return atvProntuario;
	}

	public void setAtvProntuario(Integer atvProntuario) {
		this.atvProntuario = atvProntuario;
	}

	public String getDescricaoExame() {
		return descricaoExame;
	}

	public void setDescricaoExame(String descricaoExame) {
		this.descricaoExame = descricaoExame;
	}

	public String getSiglaExame() {
		return siglaExame;
	}

	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}

	public void setUnidadePatologica(Boolean unidadePatologica) {
		this.unidadePatologica = unidadePatologica;
	}

	public Boolean getUnidadePatologica() {
		return unidadePatologica;
	}
	
	public enum Fields {
		SOE_SEQ("soeSeq"),//
		SEQP("seqp"),//
		NUMERO_UNICO("nroUnico"),//	
		DT_NUMERO_UNICO("dtNumeroUnico"),//
		SITUACAO("situacao"),//
		CODIGO_PAC_ATD("codigoPacAtd"),//
		CODIGO_PAC_ADD("codigoPacAdd"),//
		NOME_PAC_ATD("nomePacAtd"),//
		NOME_PAC_ADD("nomePacAdd"),//
		QRT_NUMERO("qrtNumero"),//
		UNIDADE("unfSeq"),//
		LEITO("leito"),//
		ATD_SEQ("atdSeq"),//
		ATD_PRONTUARIO("atvProntuario"),//
		ATV_PRONTUARIO("atvProntuario"),//
		RECIPIENTE_COLETA("recipienteColeta"),//
		ANTICOAGULANTE("anticoagulante"),//
		UNID_TEMPO_INTERVALO_COLETA("unidTempoIntervaloColeta"),//
		TEMPO_INTERVALO_COLETA("tempoIntervaloColeta"),//
		DESCRICAO_EXAME("descricaoExame"),//
		SIGLA_EXAME("siglaExame"),//
		;
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	/*
	 * equals and hashCode gerados com base no id da amostra. Antes de mudar mapear e ver onde é usado e qual impacto.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
		result = prime * result + ((soeSeq == null) ? 0 : soeSeq.hashCode());
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
		if (!(obj instanceof AelAmostrasVO)) {
			return false;
		}
		AelAmostrasVO other = (AelAmostrasVO) obj;
		if (seqp == null) {
			if (other.seqp != null) {
				return false;
			}
		} else if (!seqp.equals(other.seqp)) {
			return false;
		}
		if (soeSeq == null) {
			if (other.soeSeq != null) {
				return false;
			}
		} else if (!soeSeq.equals(other.soeSeq)) {
			return false;
		}
		return true;
	}
	
}
