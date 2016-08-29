package br.gov.mec.aghu.model;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioFormaRespiracao;

public interface IAelItemSolicitacaoExames {
	
	public IAelItemSolicitacaoExamesId getId();
	
	public abstract AelExames getExame();

	public abstract AelMateriaisAnalises getMaterialAnalise();

	public abstract AelUnfExecutaExames getAelUnfExecutaExames();

	public abstract void setAelUnfExecutaExames(
			AelUnfExecutaExames aelUnfExecutaExames);

	public abstract RapServidores getServidorResponsabilidade();

	public abstract DominioFormaRespiracao getFormaRespiracao();

	public abstract BigDecimal getLitrosOxigenio();

	public abstract Short getPercOxigenio();

	public IAelSolicitacaoExames getSolicitacaoExame();

	public String getDescMaterialAnalise();

	public AelRegiaoAnatomica getRegiaoAnatomica();

	public String getDescRegiaoAnatomica();

}