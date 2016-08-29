package br.gov.mec.aghu.procedimentoterapeutico.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class VincularIntercorrenciaTipoSessaoVO implements BaseBean {

	/**
	 * #47027
	 */
	private static final long serialVersionUID = -4730362201685227461L;
	
	private Short seqTipoIntercor;
	private String descTipoSessao;
	private String descTipoIntercor;
	
	public enum Fields {
		
		DESC_TIPO_SESSAO("descTipoSessao"),
		DESC_TIPO_INTERCOR("descTipoIntercor"),
		SEQ_TIPO_INTERCOR("seqTipoIntercor");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	//Getter e Setters
	public String getDescTipoSessao() {
		return descTipoSessao;
	}
	public void setDescTipoSessao(String descTipoSessao) {
		this.descTipoSessao = descTipoSessao;
	}
	public String getDescTipoIntercor() {
		return descTipoIntercor;
	}
	public void setDescTipoIntercor(String descTipoIntercor) {
		this.descTipoIntercor = descTipoIntercor;
	}
	public Short getSeqTipoIntercor() {
		return seqTipoIntercor;
	}
	public void setSeqTipoIntercor(Short seqTipoIntercor) {
		this.seqTipoIntercor = seqTipoIntercor;
	}


}
