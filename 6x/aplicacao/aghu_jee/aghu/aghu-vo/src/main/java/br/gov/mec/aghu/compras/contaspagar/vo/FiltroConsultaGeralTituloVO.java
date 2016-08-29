package br.gov.mec.aghu.compras.contaspagar.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoTitulo;
import br.gov.mec.aghu.model.FcpClassificacaoTitulo;
import br.gov.mec.aghu.model.FcpTipoTitulo;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * @author lucas.lima
 *
 */
public class FiltroConsultaGeralTituloVO implements BaseBean {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -9198073262236195263L;

	private ScoFornecedor fornecedor;
	
	private Integer ttlSeq;
	
	private DominioSituacaoTitulo situacaoTitulo;
	
	private FcpClassificacaoTitulo classificacaoTitulo;
	
	private FcpTipoTitulo tipoTitulo;

	private Date dataVencimentoInicial;
	
	private Date dataVencimentoFinal;

	private DominioTipoSolicitacaoTitulo tipoSolicitacao;
	
	private Integer numeroSolicitacao;
	
	private ScoGrupoServico grupoServico;
	
	private ScoGrupoMaterial grupoMaterial;
	
	private ScoServico servico;
	
	private ScoMaterial material;
	
	private FsoGrupoNaturezaDespesa  grupoNaturezaDespesa;
	
	private FsoNaturezaDespesa  naturezaDespesa;
	
	private boolean grupoDespesa;
	
	private Integer[] arrayTtlSeq;
	
		
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public DominioSituacaoTitulo getSituacaoTitulo() {
		return situacaoTitulo;
	}

	public void setSituacaoTitulo(DominioSituacaoTitulo situacaoTitulo) {
		this.situacaoTitulo = situacaoTitulo;
	}

	public FcpClassificacaoTitulo getClassificacaoTitulo() {
		return classificacaoTitulo;
	}

	public void setClassificacaoTitulo(FcpClassificacaoTitulo classificacaoTitulo) {
		this.classificacaoTitulo = classificacaoTitulo;
	}

	public FcpTipoTitulo getTipoTitulo() {
		return tipoTitulo;
	}

	public void setTipoTitulo(FcpTipoTitulo tipoTitulo) {
		this.tipoTitulo = tipoTitulo;
	}

	public DominioTipoSolicitacaoTitulo getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoSolicitacaoTitulo tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}

	public void setGrupoNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}
	
	public FiltroConsultaGeralTituloVO(){
		
	}

	public Date getDataVencimentoInicial() {
		return dataVencimentoInicial;
	}

	public void setDataVencimentoInicial(Date dataVencimentoInicial) {
		this.dataVencimentoInicial = dataVencimentoInicial;
	}



	public Date getDataVencimentoFinal() {
		return dataVencimentoFinal;
	}

	public void setDataVencimentoFinal(Date dataVencimentoFinal) {
		this.dataVencimentoFinal = dataVencimentoFinal;
	}


	public Integer getTtlSeq() {
		return ttlSeq;
	}

	public void setTtlSeq(Integer ttlSeq) {
		this.ttlSeq = ttlSeq;
	}


	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}


	public boolean isGrupoDespesa() {
		return grupoDespesa;
	}

	public void setGrupoDespesa(boolean grupoDespesa) {
		this.grupoDespesa = grupoDespesa;
	}


	public Integer[] getArrayTtlSeq() {
		return arrayTtlSeq;
	}

	public void setArrayTtlSeq(Integer[] arrayTtlSeq) {
		this.arrayTtlSeq = arrayTtlSeq;
	}


	public enum Fields {
		NTD_GND_CODIGO("ntdGndCodigo"),
		DT_INICIAL("dataVencimentoInicial"),
		DT_FINAL("dataVencimentoFinal"),
		DATA_GERACAO("dataGeracao"),
		TTL_SEQ("ttlSeq"),
		GND_DESCRICAO("gndDescricao"),
		NTD_CODIGO("ntdCodigo"),
		CLT_DESCRICAO("cltClassifcacao"),
		FRN_NUMERO("frnNumero"),
		FRN_CPF("cpf"),
		FRN_CNPJ("CNPJ"),
		DESCRICAO_TIPO_TITULO("descricaoTipo"),
		RAZAO_SOCIAL("razaoSocial"),
		DT_VENCIMENTO("dataVencimento"),
		SITUACAO("situacao"),
		VALOR_TITULO("valor");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}