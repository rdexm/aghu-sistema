package br.gov.mec.aghu.exames.solicitacao.vo;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;

public class MaterialVO {
	private AelItemSolicitacaoExames solicitacaoExame;
	private Short iseSeq;
	private Integer solicitacaoExameSeq;
	private Integer numeroAmostra;
	private String regiaoAnatomica;
	private String descricaoMaterial;
	private AelMateriaisAnalises materialAnalise;
	public MaterialVO() {
		
	}

	public MaterialVO(Integer numeroAmostra, AelMateriaisAnalises materialAnalise) {
		this.numeroAmostra = numeroAmostra;
		this.materialAnalise = materialAnalise;
	}
	public AelMateriaisAnalises getMaterialAnalise() {
		return materialAnalise;
	}
	public void setMaterialAnalise(AelMateriaisAnalises materialAnalise) {
		this.materialAnalise = materialAnalise;
	}

	public Integer getNumeroAmostra() {
		return numeroAmostra;
	}

	public void setNumeroAmostra(Integer numeroAmostra) {
		this.numeroAmostra = numeroAmostra;
	}
	public String getRegiaoAnatomica() {
		return regiaoAnatomica;
	}
	public void setRegiaoAnatomica(String regiaoAnatomica) {
		this.regiaoAnatomica = regiaoAnatomica;
	}
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public AelItemSolicitacaoExames getSolicitacaoExame() {
		return solicitacaoExame;
	}
	public void setSolicitacaoExame(AelItemSolicitacaoExames solicitacaoExame) {
		this.solicitacaoExame = solicitacaoExame;
	}
	public Integer getSolicitacaoExameSeq() {
		return solicitacaoExameSeq;
	}
	public void setSolicitacaoExameSeq(Integer solicitacaoExameSeq) {
		this.solicitacaoExameSeq = solicitacaoExameSeq;
	}

	public void setIseSeq(Short iseSeq) {
		this.iseSeq = iseSeq;
	}

	public Short getIseSeq() {
		return iseSeq;
	}
}