package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class FatTabRegistroServicoVO {

	private Short seq;

	private String codigo;
	
	private String servico;

	private String dtCompetencia;

	private DominioSituacao situacao;

	private TipoProcessado processado;
	
	public FatTabRegistroServicoVO() {}
	
	public FatTabRegistroServicoVO(Short seq, String servico,
			String dtCompetencia, DominioSituacao situacao, TipoProcessado processado) {
		super();
		this.seq = seq;
		this.servico = servico;
		this.dtCompetencia = dtCompetencia;
		this.situacao = situacao;
		this.processado = processado;
	}

	public enum TipoProcessado {

		INCLUI("I"),
		ALTERA("A"),
		PROCESSADO("S"),
		NAO_PROCESSADO("N"),
		DESPREZADO("D");
		
		private String fields;

		private TipoProcessado(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getServico() {
		return servico;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public String getDtCompetencia() {
		return dtCompetencia;
	}

	public void setDtCompetencia(String dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public TipoProcessado getProcessado() {
		return processado;
	}

	public void setProcessado(TipoProcessado processado) {
		this.processado = processado;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}