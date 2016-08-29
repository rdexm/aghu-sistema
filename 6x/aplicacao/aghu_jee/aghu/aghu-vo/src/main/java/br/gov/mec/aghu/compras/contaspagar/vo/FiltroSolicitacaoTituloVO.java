package br.gov.mec.aghu.compras.contaspagar.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoTitulo;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;

/**
 * @author rafael.silvestre
 */
public class FiltroSolicitacaoTituloVO {

	private DominioTipoSolicitacaoTitulo tipo;
	private Integer solicitacao;
	private Date dataGeracaoInicial;
	private Date dataGeracaoFinal;
	private ScoGrupoMaterial grupoMaterial;
	private ScoMaterial material;
	private ScoGrupoServico grupoServico;
	private ScoServico servico;
	private FsoVerbaGestao verbaGestao;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private FsoNaturezaDespesa naturezaDespesa;
	
	public FiltroSolicitacaoTituloVO() {
	}

	public FiltroSolicitacaoTituloVO(DominioTipoSolicitacaoTitulo tipo) {
		this.tipo = tipo;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}
	
	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}
	
	public Date getDataGeracaoInicial() {
		return dataGeracaoInicial;
	}
	
	public void setDataGeracaoInicial(Date dataGeracaoInicial) {
		this.dataGeracaoInicial = dataGeracaoInicial;
	}
	
	public Date getDataGeracaoFinal() {
		return dataGeracaoFinal;
	}
	
	public void setDataGeracaoFinal(Date dataGeracaoFinal) {
		this.dataGeracaoFinal = dataGeracaoFinal;
	}
	
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}
	
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	
	public ScoMaterial getMaterial() {
		return material;
	}
	
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}
	
	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}
	
	public ScoServico getServico() {
		return servico;
	}
	
	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
	
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}
	
	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}
	
	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}
	
	public void setGrupoNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}
	
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}
	
	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public DominioTipoSolicitacaoTitulo getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoSolicitacaoTitulo tipo) {
		this.tipo = tipo;
	}
}
