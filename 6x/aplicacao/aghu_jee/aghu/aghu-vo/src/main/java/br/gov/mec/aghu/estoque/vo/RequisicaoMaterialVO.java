package br.gov.mec.aghu.estoque.vo;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioImpresso;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.ScoGrupoMaterial;



public class RequisicaoMaterialVO{

	private Integer seq;
	private Integer reqMaterial;
	private Short almoxSeq;
	private String almoxDesc;
	private String situacao;
	private DominioSituacaoRequisicaoMaterial indSituacao;
	private String dataGeracao;
	private String dataConfirmacao;
	private String dataEfetivacao;
	private Integer centroCustoCodigo;
	private Integer centroCustoAplicacaoCodigo;
	private String centroCustoDescricao;
	private String centroCustoAplicacaoDescricao;
	private Short tipoMovimentoSeq;
	private String tipoMovimentoComplemento;
	private String nomePessoa;
	private Integer numeroRamal;
	private Integer ordemTela;
	private String observacao;
	private Boolean estorno;
	private String paciente;
	private String prontuario;
	private DominioImpresso indImpresso;
	private SceAlmoxarifado almoxarifado;
	private ScoGrupoMaterial grupoMaterial;
	private FccCentroCustos centroCusto;
	private FccCentroCustos centroCustoAplica;
	
	private SceReqMaterial requisicaoMaterial;
	
	private List<RequisicaoMaterialItensVO> itemVO = new ArrayList<RequisicaoMaterialItensVO>();
	
	public Short getAlmoxSeq() {
		return almoxSeq;
	}
	
	public void setAlmoxSeq(Short almoxSeq) {
		this.almoxSeq = almoxSeq;
	}
	
	public String getAlmoxDesc() {
		return almoxDesc;
	}
	
	public void setAlmoxDesc(String almoxDesc) {
		this.almoxDesc = almoxDesc;
	}
	
	public String getSituacao() {
		return situacao;
	}
	
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	public String getDataGeracao() {
		return dataGeracao;
	}
	
	public void setDataGeracao(String dataGeracao) {
		this.dataGeracao = dataGeracao;
	}
	
	public String getDataConfirmacao() {
		return dataConfirmacao;
	}
	
	public void setDataConfirmacao(String dataConfirmacao) {
		this.dataConfirmacao = dataConfirmacao;
	}
	
	public String getDataEfetivacao() {
		return dataEfetivacao;
	}
	
	public void setDataEfetivacao(String dataEfetivacao) {
		this.dataEfetivacao = dataEfetivacao;
	}
	
	public Integer getCentroCustoCodigo() {
		return centroCustoCodigo;
	}
	
	public void setCentroCustoCodigo(Integer centroCustoCodigo) {
		this.centroCustoCodigo = centroCustoCodigo;
	}
	
	public Integer getCentroCustoAplicacaoCodigo() {
		return centroCustoAplicacaoCodigo;
	}
	
	public void setCentroCustoAplicacaoCodigo(Integer centroCustoAplicacaoCodigo) {
		this.centroCustoAplicacaoCodigo = centroCustoAplicacaoCodigo;
	}
	
	public String getCentroCustoDescricao() {
		return centroCustoDescricao;
	}
	
	public void setCentroCustoDescricao(String centroCustoDescricao) {
		this.centroCustoDescricao = centroCustoDescricao;
	}
	
	public String getCentroCustoAplicacaoDescricao() {
		return centroCustoAplicacaoDescricao;
	}
	
	public void setCentroCustoAplicacaoDescricao(
			String centroCustoAplicacaoDescricao) {
		this.centroCustoAplicacaoDescricao = centroCustoAplicacaoDescricao;
	}
	
	public Short getTipoMovimentoSeq() {
		return tipoMovimentoSeq;
	}
	
	public void setTipoMovimentoSeq(Short tipoMovimentoSeq) {
		this.tipoMovimentoSeq = tipoMovimentoSeq;
	}
	
	public String getTipoMovimentoComplemento() {
		return tipoMovimentoComplemento;
	}
	
	public void setTipoMovimentoComplemento(String tipoMovimentoComplemento) {
		this.tipoMovimentoComplemento = tipoMovimentoComplemento;
	}
	
	public String getNomePessoa() {
		return nomePessoa;
	}
	
	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}
	
	public Integer getNumeroRamal() {
		return numeroRamal;
	}
	
	public void setNumeroRamal(Integer numeroRamal) {
		this.numeroRamal = numeroRamal;
	}

	public Integer getReqMaterial() {
		return reqMaterial;
	}

	public void setReqMaterial(Integer reqMaterial) {
		this.reqMaterial = reqMaterial;
	}

	public List<RequisicaoMaterialItensVO> getItemVO() {
		return itemVO;
	}

	public void setItemVO(List<RequisicaoMaterialItensVO> itemVO) {
		this.itemVO = itemVO;
	}

	public SceReqMaterial getRequisicaoMaterial() {
		return requisicaoMaterial;
	}

	public void setRequisicaoMaterial(SceReqMaterial requisicaoMaterial) {
		this.requisicaoMaterial = requisicaoMaterial;
	}

	public Integer getOrdemTela() {
		return ordemTela;
	}

	public void setOrdemTela(Integer ordemTela) {
		this.ordemTela = ordemTela;
	}
	
	public String getObservacao() {
		return observacao;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Boolean getEstorno() {
		return estorno;
	}

	public void setEstorno(Boolean estorno) {
		this.estorno = estorno;
	}

	public String getProntuario() {
		return prontuario;
	}	
	
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getPaciente() {
		return paciente;
	}
	
	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}
	
	public DominioImpresso getIndImpresso() {
		return indImpresso;
	}

	public void setIndImpresso(DominioImpresso indImpresso) {
		this.indImpresso = indImpresso;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public FccCentroCustos getCentroCustoAplica() {
		return centroCustoAplica;
	}

	public void setCentroCustoAplica(FccCentroCustos centroCustoAplica) {
		this.centroCustoAplica = centroCustoAplica;
	}
	
	
	public DominioSituacaoRequisicaoMaterial getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoRequisicaoMaterial indSituacao) {
		this.indSituacao = indSituacao;
	}

	public RequisicaoMaterialVO copiar(){
		try {
			return (RequisicaoMaterialVO) this.clone();
		} catch (CloneNotSupportedException e) {
			// engolir exceção nunca vai acontecer pois o bojeto é clonnable.
			return null;
		}
	}

}