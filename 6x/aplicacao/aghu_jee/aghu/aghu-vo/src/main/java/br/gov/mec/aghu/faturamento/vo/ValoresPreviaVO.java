package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.math.BigDecimal;


public class ValoresPreviaVO implements Serializable, Comparable<ValoresPreviaVO> {

	private static final long serialVersionUID = 3247714924282685557L;
	
	private Short sgrGrpSeq;
	private Byte sgrSubGrupo;
	private Byte codigo;
	private String descricao;
	private BigDecimal valorServHosp;
	private BigDecimal valorServProf;
	private BigDecimal valorTotal;
	private String procedimentoFormatado;
	
	public enum Fields {

		SGR_GRP_SEQ("sgrGrpSeq"),
		SGR_SUB_GRUPO("sgrSubGrupo"),
		CODIGO("codigo"),
		DESCRICAO("descricao"),
		VALOR_SERV_HOSP("valorServHosp"),
		VALOR_SERV_PROF("valorServProf"),
		VALOR_SH_REALIZ("valorShRealiz"),
		VALOR_SP_REALIZ("valorSpRealiz");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	public String getProcedimentoFormatado() {
		String grupo = "";
		String subGrupo = "";
		String forma = "";
		String descricao = "";
		
		if (this.sgrGrpSeq != null) {
			grupo = String.format("%02d", this.sgrGrpSeq); 
		}
		
		if (this.sgrSubGrupo != null) {
			subGrupo = String.format("%02d", this.sgrSubGrupo); 
		}
		
		if (this.codigo != null) {
			forma = String.format("%02d", this.codigo); 
		}
		
		if (this.descricao != null) {
			descricao = this.descricao;
		}
		
		int limite = 35;
		String last = "";

		if (descricao.length() > limite) {
			limite--;
			last = descricao.substring(limite, limite + 1);
			if (last.equals(" ")) {
				descricao = descricao.substring(0, limite);
			} else {
				while (!last.equals(" ") && limite > 0) {
					limite--;
					last = descricao.substring(limite, limite + 1);
				}
				descricao = descricao.substring(0, limite);
			}
		}
		
		procedimentoFormatado = grupo + "." + subGrupo + "." + forma + "-" + descricao;
		
		return procedimentoFormatado;
	}

	public Short getSgrGrpSeq() {
		return sgrGrpSeq;
	}

	public void setSgrGrpSeq(Short sgrGrpSeq) {
		this.sgrGrpSeq = sgrGrpSeq;
	}

	public Byte getSgrSubGrupo() {
		return sgrSubGrupo;
	}

	public void setSgrSubGrupo(Byte sgrSubGrupo) {
		this.sgrSubGrupo = sgrSubGrupo;
	}

	public Byte getCodigo() {
		return codigo;
	}

	public void setCodigo(Byte codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getValorServHosp() {
		return valorServHosp;
	}

	public void setValorServHosp(BigDecimal valorServHosp) {
		this.valorServHosp = valorServHosp;
	}

	public BigDecimal getValorServProf() {
		return valorServProf;
	}

	public void setValorServProf(BigDecimal valorServProf) {
		this.valorServProf = valorServProf;
	}

	public BigDecimal getValorTotal() {
		if (valorServProf == null) {
			valorServProf = BigDecimal.ZERO;
		}
		if (valorServHosp == null) {
			valorServHosp = BigDecimal.ZERO;
		}
		valorTotal = valorServProf.add(valorServHosp);
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	@Override
	public int compareTo(ValoresPreviaVO valor) {
		int i = this.sgrGrpSeq - valor.getSgrGrpSeq();
		
		if (i != 0) {
			return i;
		}
		
		i = this.sgrSubGrupo - valor.getSgrSubGrupo();
				
		if (i != 0) {
			return i;
		}
				
		return this.codigo - valor.getCodigo();
	}	
}
