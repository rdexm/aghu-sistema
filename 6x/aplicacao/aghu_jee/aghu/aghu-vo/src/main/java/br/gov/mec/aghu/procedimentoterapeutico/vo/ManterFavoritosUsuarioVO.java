package br.gov.mec.aghu.procedimentoterapeutico.vo;

import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ManterFavoritosUsuarioVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1429489471673332194L;
	
	private String seq = "1";
	private Integer favoritosSeq;
	private String tpsDescricao;
	private String salasDescricao;
	
	private MptSalas salas;
	private MptTipoSessao sessao;
	
	public enum Fields {

		FAV_SEQ("favoritosSeq"),
		TPS_DESCRICAO("tpsDescricao"),
		SALAS_DESCRICAO("salasDescricao");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public Integer getFavoritosSeq() {
		return favoritosSeq;
	}
	public void setFavoritosSeq(Integer favoritosSeq) {
		this.favoritosSeq = favoritosSeq;
	}
	public String getTpsDescricao() {
		return tpsDescricao;
	}
	public void setTpsDescricao(String tpsDescricao) {
		this.tpsDescricao = tpsDescricao;
	}

	public String getSalasDescricao() {
		return salasDescricao;
	}
	public void setSalasDescricao(String salasDescricao) {
		this.salasDescricao = salasDescricao;
	}
	public MptSalas getSalas() {
		return salas;
	}
	public void setSalas(MptSalas salas) {
		this.salas = salas;
	}
	public MptTipoSessao getSessao() {
		return sessao;
	}
	public void setSessao(MptTipoSessao sessao) {
		this.sessao = sessao;
	}
	
}