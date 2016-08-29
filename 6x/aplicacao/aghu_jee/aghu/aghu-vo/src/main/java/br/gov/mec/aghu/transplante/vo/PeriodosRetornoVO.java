package br.gov.mec.aghu.transplante.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioRepeticaoRetorno;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;

public class PeriodosRetornoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3272505263513538142L;
	
	private Integer seqTipoRetorno;
	private Integer seqPeriodoRetorno;
    private DominioTipoRetorno indTipo;
    private String descricao;
    private DominioSituacao indSituacao;
    private Integer ordem;
    private DominioRepeticaoRetorno indRepeticao;
    private Integer quantidade;
    
    public enum Fields {

		SEQ_TIPO_RETORNO("seqTipoRetorno"), 
		SEQ_PERIODO_RETORNO("seqPeriodoRetorno"),
		INDTIPO("indTipo"),
		DESCRICAO("descricao"),
		SITUACAO("indSituacao"),
		ORDEM("ordem"), 
		IND_REPETICAO("indRepeticao"), 
		QUANTIDADE("quantidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
    
      
	public Integer getSeqTipoRetorno() {
		return seqTipoRetorno;
	}
	public void setSeqTipoRetorno(Integer seqTipoRetorno) {
		this.seqTipoRetorno = seqTipoRetorno;
	}
	public Integer getSeqPeriodoRetorno() {
		return seqPeriodoRetorno;
	}
	public void setSeqPeriodoRetorno(Integer seqPeriodoRetorno) {
		this.seqPeriodoRetorno = seqPeriodoRetorno;
	}
	public DominioTipoRetorno getIndTipo() {
		return indTipo;
	}
	public void setIndTipo(DominioTipoRetorno indTipo) {
		this.indTipo = indTipo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	public Integer getOrdem() {
		return ordem;
	}
	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	public DominioRepeticaoRetorno getIndRepeticao() {
		return indRepeticao;
	}
	public void setIndRepeticao(DominioRepeticaoRetorno indRepeticao) {
		this.indRepeticao = indRepeticao;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


}
