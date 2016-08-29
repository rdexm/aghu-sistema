package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;


public class DetalheProducaoObjetoCustoVO implements BaseBean {

	private static final long serialVersionUID = -4590549866598549L;

	private Integer seqDetalheProducao;
	private String  nomeObjetoCusto;
	private Integer numeroVersao;
	private String nomeDirecionador;
	private Date competencia;
	
	public Integer getSeqDetalheProducao() {
		return seqDetalheProducao;
	}
	public void setSeqDetalheProducao(Integer seqDetalheProducao) {
		this.seqDetalheProducao = seqDetalheProducao;
	}
	public String getNomeObjetoCusto() {
		return nomeObjetoCusto;
	}
	public void setNomeObjetoCusto(String nomeObjetoCusto) {
		this.nomeObjetoCusto = nomeObjetoCusto;
	}
	public Integer getNumeroVersao() {
		return numeroVersao;
	}
	public void setNumeroVersao(Integer numeroVersao) {
		this.numeroVersao = numeroVersao;
	}
	public String getNomeDirecionador() {
		return nomeDirecionador;
	}
	public void setNomeDirecionador(String nomeDirecionador) {
		this.nomeDirecionador = nomeDirecionador;
	}
	public Date getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seqDetalheProducao).toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		DetalheProducaoObjetoCustoVO detalheProducaoObjetoCustoVO = (DetalheProducaoObjetoCustoVO) obj;
		return this.getNomeObjetoCusto().equals(detalheProducaoObjetoCustoVO.getNomeObjetoCusto())
				&& this.getNomeDirecionador().equals(detalheProducaoObjetoCustoVO.getNomeDirecionador())
				&& this.getCompetencia().equals(detalheProducaoObjetoCustoVO.getCompetencia())
				&& this.getNumeroVersao().equals(detalheProducaoObjetoCustoVO.getNumeroVersao());
	}	
	
	public enum Fields {

		SEQ_DETALHE_PRODUCAO ("seqDetalheProducao"),
		NOME_OBJETO_CUSTO("nomeObjetoCusto"),
		NUMERO_VERSAO ("numeroVersao"),
		NOME_DIRECIONADOR ("nomeDirecionador"),
		COMPETENCIA("competencia");

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