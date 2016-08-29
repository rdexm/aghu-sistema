package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class AnamneseItemTipoItemVO implements Serializable{

	private static final long serialVersionUID = 99818454728018368L;
	
	private String descricao;
	private Boolean permiteLivre;
	private Boolean permiteQuest;
	private Boolean permiteFigura;
	private Boolean identificacao;

	public enum Fields {
		DESCRICAO("descricao"),
		PERMITE_LIVRE("permiteLivre"),
		PERMITE_QUEST("permiteQuest"),
		PERMITE_FIGURA("permiteFigura"),
		IDENTIFICACAO("identificacao");
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getPermiteLivre() {
		return permiteLivre;
	}

	public void setPermiteLivre(Boolean permiteLivre) {
		this.permiteLivre = permiteLivre;
	}

	public Boolean getPermiteQuest() {
		return permiteQuest;
	}

	public void setPermiteQuest(Boolean permiteQuest) {
		this.permiteQuest = permiteQuest;
	}

	public Boolean getPermiteFigura() {
		return permiteFigura;
	}

	public void setPermiteFigura(Boolean permiteFigura) {
		this.permiteFigura = permiteFigura;
	}

	public Boolean getIdentificacao() {
		return identificacao;
	}

	public void setIdentificacao(Boolean identificacao) {
		this.identificacao = identificacao;
	}
}
