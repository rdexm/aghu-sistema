package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;

/**
 * #3855 Prescrição: Emitir relatório de estatísica da produtividade do
 * consultor
 * 
 * @author aghu
 *
 */
public class ItemRelatorioEstatisticaProdutividadeConsultorVO {
	/*
	 * Dados do ID de MPM_SOLICITACAO_CONSULTORIAS
	 */
	private Integer idAtdSeq;
	private Integer idScnSeq;

	/*
	 * Dados do relatório
	 */
	private String espSol; // C2.SIGLA

	/*
	 * Campos para TIPO CONSULTORIA
	 */
	// SCN.TIPO: Atributo auxiliar para geração do campo tipoConsultoria
	private DominioTipoSolicitacaoConsultoria tipo;
	// SCN.IND_URGENCIA: Atributo auxiliar para geração do campo tipoConsultoria
	private DominioSimNao indUrgencia;
	private String tipoConsultoria; // C2.TIPO2

	private Date dataSolicitacao; // C2.DTHR_SOLICITADA
	private Date dataConhecimento; // C2.DTHR_CONHECIMENTO_RESPOSTA

	/*
	 * Campos para TIPO DTHR_RESPOSTA
	 */
	// SCN.DTHR_RESPOSTA: Atributo auxiliar para geração do campo dataResposta
	private Date dthrResposta;
	// ATD.IND_PAC_ATENDIMENTO: Atributo auxiliar para geração do campo
	// dataResposta
	private DominioPacAtendimento indPacAtendimento;
	private String dataResposta; // C2.DTHR_RESPOSTA

	private String tempoResposta; // RN04 em MPMC_NHORAS_UTEIS

	public Integer getIdAtdSeq() {
		return idAtdSeq;
	}

	public void setIdAtdSeq(Integer idAtdSeq) {
		this.idAtdSeq = idAtdSeq;
	}

	public Integer getIdScnSeq() {
		return idScnSeq;
	}

	public void setIdScnSeq(Integer idScnSeq) {
		this.idScnSeq = idScnSeq;
	}

	public String getEspSol() {
		return espSol;
	}

	public void setEspSol(String espSol) {
		this.espSol = espSol;
	}

	public DominioTipoSolicitacaoConsultoria getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoSolicitacaoConsultoria tipo) {
		this.tipo = tipo;
	}

	public DominioSimNao getIndUrgencia() {
		return indUrgencia;
	}

	public void setIndUrgencia(DominioSimNao indUrgencia) {
		this.indUrgencia = indUrgencia;
	}

	public String getTipoConsultoria() {
		return tipoConsultoria;
	}

	public void setTipoConsultoria(String tipoConsultoria) {
		this.tipoConsultoria = tipoConsultoria;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Date getDataConhecimento() {
		return dataConhecimento;
	}

	public void setDataConhecimento(Date dataConhecimento) {
		this.dataConhecimento = dataConhecimento;
	}

	public Date getDthrResposta() {
		return dthrResposta;
	}

	public void setDthrResposta(Date dthrResposta) {
		this.dthrResposta = dthrResposta;
	}

	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}

	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}

	public String getDataResposta() {
		return dataResposta;
	}

	public void setDataResposta(String dataResposta) {
		this.dataResposta = dataResposta;
	}

	public String getTempoResposta() {
		return tempoResposta;
	}

	public void setTempoResposta(String tempoResposta) {
		this.tempoResposta = tempoResposta;
	}

	/**
	 * Dados auxiliares utilizados nas funcitons e consultas externas (ON)
	 */

	public enum Fields {
		ID_ATD_SEQ("idAtdSeq"), ID_SCN_SEQ("idScnSeq"), ESP_SOL("espSol"), TIPO("tipo"), IND_URGENCIA("indUrgencia"),
		// TIPO_CONSULTORIA("tipoConsultoria"),
		DATA_SOLICITACAO("dataSolicitacao"), DATA_CONHECIMENTO("dataConhecimento"), DTHR_RESPOSTA("dthrResposta"), IND_PAC_ATENDIMENTO("indPacAtendimento"),
		// DATA_RESPOSTA("dataResposta"),
		TEMPO_RESPOSTA("tempoResposta");

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
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getIdAtdSeq());
		umHashCodeBuilder.append(this.getIdScnSeq());
		return umHashCodeBuilder.toHashCode();
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
		ItemRelatorioEstatisticaProdutividadeConsultorVO other = (ItemRelatorioEstatisticaProdutividadeConsultorVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getIdAtdSeq(), other.getIdAtdSeq());
		umEqualsBuilder.append(this.getIdScnSeq(), other.getIdScnSeq());
		return umEqualsBuilder.isEquals();
	}

}
