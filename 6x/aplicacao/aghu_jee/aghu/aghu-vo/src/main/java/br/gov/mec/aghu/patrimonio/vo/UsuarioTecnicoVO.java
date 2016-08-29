package br.gov.mec.aghu.patrimonio.vo;


public class UsuarioTecnicoVO {
	
	private Integer seqAreaTecAvaliacao;
	private Integer matRapTecnico;
	private Short serVinCodigoTecnico;
	private Boolean tecnicoPadrao;
	private String nome;
	private Integer matRapCriacao;
	private Short serVinCodigoCriacao;
	
	
	public enum Fields {
		SEQ_AREA_TEC_AVALIACAO("seqAreaTecAvaliacao"),
		MAT_RAP_TECNICO("matRapTecnico"),
		SER_VIN_CODIGO_TECNICO("serVinCodigoTecnico"),
		TECNICO_PADRAO("tecnicoPadrao"),
		MAT_RAP_CRIACAO("matRapCriacao"),
		SER_VIN_CODIGO_CRIACAO("serVinCodigoCriacao"),
		NOME("nome");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}

	}
	
	public Boolean getTecnicoPadrao() {
		return tecnicoPadrao;
	}
	public void setTecnicoPadrao(Boolean tecnicoPadrao) {
		this.tecnicoPadrao = tecnicoPadrao;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Integer getSeqAreaTecAvaliacao() {
		return seqAreaTecAvaliacao;
	}
	public void setSeqAreaTecAvaliacao(Integer seqAreaTecAvaliacao) {
		this.seqAreaTecAvaliacao = seqAreaTecAvaliacao;
	}
	public Integer getMatRapTecnico() {
		return matRapTecnico;
	}
	public void setMatRapTecnico(Integer matRapTecnico) {
		this.matRapTecnico = matRapTecnico;
	}
	public Short getSerVinCodigoTecnico() {
		return serVinCodigoTecnico;
	}
	public void setSerVinCodigoTecnico(Short serVinCodigoTecnico) {
		this.serVinCodigoTecnico = serVinCodigoTecnico;
	}
	public Integer getMatRapCriacao() {
		return matRapCriacao;
	}
	public void setMatRapCriacao(Integer matRapCriacao) {
		this.matRapCriacao = matRapCriacao;
	}
	public Short getSerVinCodigoCriacao() {
		return serVinCodigoCriacao;
	}
	public void setSerVinCodigoCriacao(Short serVinCodigoCriacao) {
		this.serVinCodigoCriacao = serVinCodigoCriacao;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matRapTecnico == null) ? 0 : matRapTecnico.hashCode());
		result = prime
				* result
				+ ((seqAreaTecAvaliacao == null) ? 0 : seqAreaTecAvaliacao
						.hashCode());
		result = prime
				* result
				+ ((serVinCodigoTecnico == null) ? 0 : serVinCodigoTecnico
						.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		UsuarioTecnicoVO other = (UsuarioTecnicoVO) obj;
		if (matRapTecnico == null) {
			if (other.matRapTecnico != null){
				return false;
			}
		} else if (!matRapTecnico.equals(other.matRapTecnico)){
			return false;
		}
		if (seqAreaTecAvaliacao == null) {
			if (other.seqAreaTecAvaliacao != null){
				return false;
			}
		} else if (!seqAreaTecAvaliacao.equals(other.seqAreaTecAvaliacao)){
			return false;
		}
		if (serVinCodigoTecnico == null) {
			if (other.serVinCodigoTecnico != null){
				return false;
			}
		} else if (!serVinCodigoTecnico.equals(other.serVinCodigoTecnico)){
			return false;
		}
		return true;
	}
	public String getDescricao() {
		return this.getSerVinCodigoTecnico()+"-"+this.getNome();
	}
	
	
}
