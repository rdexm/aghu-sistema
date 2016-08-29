package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class AelMotivoCancelaExamesVO {

	private Short codigo;
	private String descricao;
	private DominioSimNao indRetornaAExecutar;

	
	
	
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
	

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getIndRetornaAExecutar() {
		return indRetornaAExecutar;
	}

	public void setIndRetornaAExecutar(DominioSimNao indRetornaAExecutar) {
		this.indRetornaAExecutar = indRetornaAExecutar;
	}
	
	
	public String getDescricaoRetornaExecutar() {
		if(DominioSimNao.S == this.indRetornaAExecutar) {
			return DominioSimNao.S.getDescricao();
		}else {
			return DominioSimNao.N.getDescricao();
		}
	}
	
	
	public enum Fields {
		SEQ("codigo"),//
		DESCRICAO("descricao"),//
		IND_RETORNA_EXECUTAR("indRetornaAExecutar"),//
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

}
