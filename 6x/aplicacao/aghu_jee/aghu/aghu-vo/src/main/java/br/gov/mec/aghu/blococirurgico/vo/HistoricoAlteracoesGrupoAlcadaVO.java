package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class HistoricoAlteracoesGrupoAlcadaVO {

	private String seq;
	private String descricaoGrupo;
	private String descricaoNivel;
	private String descricaoServidor;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private String criacaoNome;
	private Date modificadoEm;
	private String modificadoNome;
	
	public enum Fields {
		SEQ("seq"), DESCRICAO_GRUPO("descricaoGrupo"), 
		DESCRICAO_NIVEL("descricaoNivel"), DESCRICAO_SERVIDOR("descricaoServidor"),
		IND_SITUACAO("indSituacao"), CRIADO_EM("criadoEm"), CRIACAO_NOME("criacaoNome"), 
		MODIFICADO_EM("modificadoEm"), MODIFICADO_NOME("modificadoNome");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getDescricaoGrupo() {
		return descricaoGrupo;
	}

	public void setDescricaoGrupo(String descricaoGrupo) {
		this.descricaoGrupo = descricaoGrupo;
	}

	public String getDescricaoServidor() {
		return descricaoServidor;
	}

	public void setDescricaoServidor(String descricaoServidor) {
		this.descricaoServidor = descricaoServidor;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getCriacaoNome() {
		return criacaoNome;
	}

	public void setCriacaoNome(String criacaoNome) {
		this.criacaoNome = criacaoNome;
	}

	public Date getModificadoEm() {
		return modificadoEm;
	}

	public void setModificadoEm(Date modificadoEm) {
		this.modificadoEm = modificadoEm;
	}

	public String getModificadoNome() {
		return modificadoNome;
	}

	public void setModificadoNome(String modificadoNome) {
		this.modificadoNome = modificadoNome;
	}

	public String getDescricaoNivel() {
		return descricaoNivel;
	}

	public void setDescricaoNivel(String descricaoNivel) {
		this.descricaoNivel = descricaoNivel;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HistoricoAlteracoesGrupoAlcadaVO)) {
			return false;
		}
		HistoricoAlteracoesGrupoAlcadaVO other = (HistoricoAlteracoesGrupoAlcadaVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	
	
	
	
}
