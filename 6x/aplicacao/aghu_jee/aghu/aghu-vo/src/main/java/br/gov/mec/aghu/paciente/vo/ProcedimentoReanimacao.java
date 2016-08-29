package br.gov.mec.aghu.paciente.vo;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * Classe utilizado para recuperar dados da Base.<br>
 * Utilizada para verificacoes apenas.<br>
 *  
 * @author marcelo.corati
 *
 */

public class ProcedimentoReanimacao implements BaseBean{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1623328877002365767L;
	
	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private String componente;
	private String medicamento;
	private Integer matCodigo;
	private String compCodigo;
	
	public ProcedimentoReanimacao(){}
	
	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		COMPONENTE("componente"),
		MEDICAMENTO("medicamento"),
		SITUACAO("indSituacao"),
		COMP_CODIGO("compCodigo"),
		MAT_CODIGO("matCodigo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

	public String getComponente() {
		return componente;
	}

	public void setComponente(String componente) {
		this.componente = componente;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getCompCodigo() {
		return compCodigo;
	}

	public void setCompCodigo(String compCodigo) {
		this.compCodigo = compCodigo;
	}

}
