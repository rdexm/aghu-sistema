package br.gov.mec.aghu.faturamento.vo;

public class FatGrupoSubGrupoVO {

	private Short grupo;
	private Byte subGrupo;

	public FatGrupoSubGrupoVO(Short grupo, Byte subGrupo) {
		super();
		this.grupo = grupo;
		this.subGrupo = subGrupo;
	}

	public FatGrupoSubGrupoVO() {
	}

	public Short getGrupo() {
		return grupo;
	}

	public void setGrupo(Short grupo) {
		this.grupo = grupo;
	}

	public Byte getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(Byte subGrupo) {
		this.subGrupo = subGrupo;
	}

	public enum Fields {
		SUB_GRUPO("subGrupo"), 
		GRUPO("grupo"), 
		;

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
