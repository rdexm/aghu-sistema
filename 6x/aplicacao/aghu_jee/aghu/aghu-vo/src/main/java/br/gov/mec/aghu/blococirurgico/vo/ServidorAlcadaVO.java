package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidadeOPMS;

public class ServidorAlcadaVO {
	private Integer id;
	private String nome;
	private Integer sequencia;
	
	public Integer getSequencia() {
		return sequencia;
	}

	public void setSequencia(Integer sequencia) {
		this.sequencia = sequencia;
	}

	private DominioTipoResponsabilidadeOPMS responsabilidade;
	private DominioSituacao situacao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioTipoResponsabilidadeOPMS getResponsabilidade() {
		return responsabilidade;
	}

	public void setResponsabilidade(
			DominioTipoResponsabilidadeOPMS responsabilidade) {
		this.responsabilidade = responsabilidade;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

}
