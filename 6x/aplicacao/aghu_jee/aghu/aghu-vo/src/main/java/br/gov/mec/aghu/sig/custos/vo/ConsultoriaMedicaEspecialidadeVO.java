package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;

public class ConsultoriaMedicaEspecialidadeVO implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9026696565908899411L;
	
	private String nomeEspecialidadaConsultoria;
	private Integer codigoCentroCusto;
	private Integer seqConsultoria;
	private Date dataSolicitacao;
	private Date dataResposta;
	private Long qtdeConsultorias;

	
	
	public ConsultoriaMedicaEspecialidadeVO(){	
	}
			
	public ConsultoriaMedicaEspecialidadeVO(String nomeEspecialidadaConsultoria, Integer codigoCentroCusto, Integer seqConsultoria, Date dataSolicitacao,
			Date dataResposta, Long qtdeConsultorias) {
		super();
		this.nomeEspecialidadaConsultoria = nomeEspecialidadaConsultoria;
		this.codigoCentroCusto = codigoCentroCusto;
		this.seqConsultoria = seqConsultoria;
		this.dataSolicitacao = dataSolicitacao;
		this.dataResposta = dataResposta;
		this.qtdeConsultorias = qtdeConsultorias;
	}

	public String getNomeEspecialidadaConsultoria() {
		return nomeEspecialidadaConsultoria;
	}

	public void setNomeEspecialidadaConsultoria(String nomeEspecialidadaConsultoria) {
		this.nomeEspecialidadaConsultoria = nomeEspecialidadaConsultoria;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public Integer getSeqConsultoria() {
		return seqConsultoria;
	}

	public void setSeqConsultoria(Integer seqConsultoria) {
		this.seqConsultoria = seqConsultoria;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public Date getDataResposta() {
		return dataResposta;
	}

	public void setDataResposta(Date dataResposta) {
		this.dataResposta = dataResposta;
	}

	public Long getQtdeConsultorias() {
		return qtdeConsultorias;
	}

	public void setQtdeConsultorias(Long qtdeConsultorias) {
		this.qtdeConsultorias = qtdeConsultorias;
	}

	public enum Fields {
		
		NOME_ESPECIALIDADE_CONSULTORIA ("nomeEspecialidadaConsultoria"),
		CODIGO_CENTRO_CUSTO("codigoCentroCusto"),
		SEQ_CONSULTORIA("seqConsultoria"),
		DATA_SOLICITACAO("dataSolicitacao"),
		DATA_RESPOSTA("dataResposta"),
		QTDE_CONSULTORIAS("qtdeConsultorias");	

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
