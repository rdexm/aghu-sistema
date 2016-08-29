package br.gov.mec.aghu.estoque.vo;

import java.util.List;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifadoComposicao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;

public class ComposicaoGruposVO {

	private Integer seq;
	private SceAlmoxarifadoComposicao composicao;
	private String descricaoComposicao;
	private RapServidores servidorInclusao;
	private List<ScoGrupoMaterial> listaGrupos;
	private Short idRow;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idRow == null) ? 0 : idRow.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		ComposicaoGruposVO other = (ComposicaoGruposVO) obj;

		if (idRow == null) {
			if (other.idRow != null) {
				return false;
			}
		} else if (!idRow.equals(other.idRow)) {
			return false;
		}
		return true;
	}

	public String getDescricaoComposicao() {
		return descricaoComposicao;
	}

	public void setDescricaoComposicao(String descricaoComposicao) {
		this.descricaoComposicao = descricaoComposicao;
	}

	public RapServidores getServidorInclusao() {
		return servidorInclusao;
	}

	public void setServidorInclusao(RapServidores servidorInclusao) {
		this.servidorInclusao = servidorInclusao;
	}

	public List<ScoGrupoMaterial> getListaGrupos() {
		return listaGrupos;
	}

	public void setListaGrupos(List<ScoGrupoMaterial> listaGrupos) {
		this.listaGrupos = listaGrupos;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public SceAlmoxarifadoComposicao getComposicao() {
		return composicao;
	}

	public void setComposicao(SceAlmoxarifadoComposicao composicao) {
		this.composicao = composicao;
	}

	public Short getIdRow() {
		return idRow;
	}

	public void setIdRow(Short idRow) {
		this.idRow = idRow;
	}

}
