package br.gov.mec.aghu.orcamento.cadastrosbasicos.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioIndicadorParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioLimiteValorPatrimonio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;

/**
 * Classe VO responsável pelos critérios de busca por parâmetros de regras orçamentárias.
 * 
 * @author mlcruz
 */
public class FsoParametrosOrcamentoCriteriaVO implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = 3425982542242940247L;
	
	private Integer seq;
	private DominioTipoSolicitacao aplicacao;
	private DominioIndicadorParametrosOrcamento indicador;
	private ScoGrupoMaterial grupoMaterial;
	private ScoMaterial material;
	private ScoGrupoServico grupoServico;
	private ScoServico servico;
	private FccCentroCustos centroCusto;
	private DominioSituacao situacao;
	private DominioLimiteValorPatrimonio limite;
	private BigDecimal valor;
	private Nivel nivel;
	private Parametro parametro;
	private Set<DominioAcaoParametrosOrcamento> acoes;
	private Object filtro;
	private Integer maxResults;
	private String order;
	private FccCentroCustos centroCustoAplicacao;
	private Date dataReferencia;
	private FsoVerbaGestao verbaGestao;
	private FsoGrupoNaturezaDespesa grupoNatureza;
	private FsoNaturezaDespesa natureza;
	private Boolean centroCustoAplicacaoShouldBeNull;
	private Boolean verbaGestaoShouldBeNull;
	private Boolean grupoNaturezaShouldBeNull;
	private Boolean naturezaShouldBeNull;
	private String naturezaAlias;	
	private String grupoNaturezaAlias;	
	private String regra;
	private FccCentroCustos centroCustoSolicitacao;
	private Boolean centroCustoSolicitacaoShouldBeNull;
	

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public DominioTipoSolicitacao getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(DominioTipoSolicitacao aplicacao) {
		this.aplicacao = aplicacao;
	}

	public DominioIndicadorParametrosOrcamento getIndicador() {
		return indicador;
	}

	public void setIndicador(DominioIndicadorParametrosOrcamento indicador) {
		this.indicador = indicador;
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

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioLimiteValorPatrimonio getLimite() {
		return limite;
	}

	public void setLimite(DominioLimiteValorPatrimonio limite) {
		this.limite = limite;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	public Nivel getNivel() {
		return nivel;
	}

	public void setNivel(Nivel nivel) {
		this.nivel = nivel;
	}

	public Parametro getParametro() {
		return parametro;
	}

	public void setParametro(Parametro parametro) {
		this.parametro = parametro;
	}

	public Set<DominioAcaoParametrosOrcamento> getAcoes() {
		return acoes;
	}

	public void setAcoes(Set<DominioAcaoParametrosOrcamento> acoes) {
		this.acoes = acoes;
	}
	
	public DominioAcaoParametrosOrcamento getAcao() {
		if (acoes != null && acoes.size() == 1) {
			return acoes.iterator().next();
		} else {
			return null;
		}
	}

	public Object getFiltro() {
		return filtro;
	}

	public void setFiltro(Object filtro) {
		this.filtro = filtro;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public Date getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(Date dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	public FsoGrupoNaturezaDespesa getGrupoNatureza() {
		return grupoNatureza;
	}

	public void setGrupoNatureza(FsoGrupoNaturezaDespesa grupoNatureza) {
		this.grupoNatureza = grupoNatureza;
	}

	public FsoNaturezaDespesa getNatureza() {
		return natureza;
	}

	public void setNatureza(FsoNaturezaDespesa natureza) {
		this.natureza = natureza;
	}

	public Boolean getCentroCustoAplicacaoShouldBeNull() {
		return centroCustoAplicacaoShouldBeNull;
	}

	public void setCentroCustoAplicacaoShouldBeNull(
			Boolean centroCustoAplicacaoShouldBeNull) {
		this.centroCustoAplicacaoShouldBeNull = centroCustoAplicacaoShouldBeNull;
	}

	public Boolean getVerbaGestaoShouldBeNull() {
		return verbaGestaoShouldBeNull;
	}

	public void setVerbaGestaoShouldBeNull(Boolean verbaGestaoShouldBeNull) {
		this.verbaGestaoShouldBeNull = verbaGestaoShouldBeNull;
	}

	public Boolean getGrupoNaturezaShouldBeNull() {
		return grupoNaturezaShouldBeNull;
	}

	public void setGrupoNaturezaShouldBeNull(Boolean grupoNaturezaShouldBeNull) {
		this.grupoNaturezaShouldBeNull = grupoNaturezaShouldBeNull;
	}

	public Boolean getNaturezaShouldBeNull() {
		return naturezaShouldBeNull;
	}

	public void setNaturezaShouldBeNull(Boolean naturezaShouldBeNull) {
		this.naturezaShouldBeNull = naturezaShouldBeNull;
	}

	public String getNaturezaAlias() {
		return naturezaAlias;
	}

	public void setNaturezaAlias(String naturezaAlias) {
		this.naturezaAlias = naturezaAlias;
	}

	public String getGrupoNaturezaAlias() {
		return grupoNaturezaAlias;
	}

	public void setGrupoNaturezaAlias(String grupoNaturezaAlias) {
		this.grupoNaturezaAlias = grupoNaturezaAlias;
	}

	public String getRegra() {
		return regra;
	}

	public void setRegra(String regra) {
		this.regra = regra;
	}

	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitacao;
	}

	public void setCentroCustoSolicitacao(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitacao = centroCustoSolicitante;
	}

	public Boolean getCentroCustoSolicitacaoShouldBeNull() {
		return centroCustoSolicitacaoShouldBeNull;
	}

	public void setCentroCustoSolicitacaoShouldBeNull(
			Boolean centroCustoSolicitanteShouldBeNull) {
		this.centroCustoSolicitacaoShouldBeNull = centroCustoSolicitanteShouldBeNull;
	}
	

	@Override
	public FsoParametrosOrcamentoCriteriaVO clone()
			throws CloneNotSupportedException {
		return (FsoParametrosOrcamentoCriteriaVO) super.clone();
	}
	
	/**
	 * Clona criteria apenas com os filtros básicos.
	 * 
	 * @return Clone.
	 * @throws CloneNotSupportedException 
	 */
	public FsoParametrosOrcamentoCriteriaVO cloneBasico() throws CloneNotSupportedException {
		FsoParametrosOrcamentoCriteriaVO clone = this.clone();
		
		clone.setFiltro(null);
		
		clone.setCentroCustoAplicacao(null);
		clone.setCentroCustoAplicacaoShouldBeNull(null);
		
		clone.setCentroCustoSolicitacao(null);
		clone.setCentroCustoSolicitacaoShouldBeNull(null);
		
		clone.setVerbaGestao(null);
		clone.setVerbaGestaoShouldBeNull(null);
		clone.setDataReferencia(null);
		
		clone.setNatureza(null);
		clone.setNaturezaShouldBeNull(null);
		
		clone.setGrupoNatureza(null);
		clone.setGrupoNaturezaShouldBeNull(null);
		
		return clone;
	}

	public enum Nivel {
		MATERIAL, SERVICO,
		GRUPO_MATERIAL, GRUPO_SERVICO,
		INDICADOR,
		GERAL;
	}
	
	public enum Parametro {
		GRUPO_NATUREZA,
		NATUREZA,
		VERBA_GESTAO,
		CENTRO_CUSTO
	}
}