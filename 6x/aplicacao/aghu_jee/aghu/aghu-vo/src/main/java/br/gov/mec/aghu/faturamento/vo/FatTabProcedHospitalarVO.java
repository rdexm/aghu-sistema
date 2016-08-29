package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.model.FatFormaOrganizacao;

public class FatTabProcedHospitalarVO {

	private String subGrupoId;
	private String grupoId;
	private String formaOrgId;
	private String descricao;
	private String sexoStr;
	private String idadeMinStr;
	private String idadeMaxStr;
	private String valorShStr;
	private String valorProcedStr;
	private String valorSpStr;
	private DominioSexoDeterminante sexo;
	private BigDecimal valorSh;
	private BigDecimal valorProced;
	private BigDecimal valorSp;
	private Integer idadeMin;
	private Integer idadeMax;
	private Short vQtMaximaExecucao;
	private Long codTabela;
	private FatFormaOrganizacao fatFormaOrganizacao;
	private Integer seqSusFinanciamento;
	private Integer seqSusComplexidade;
	private TipoProcessado tipoProcessado;
	
	public FatTabProcedHospitalarVO() {}


	public FatTabProcedHospitalarVO(String subGrupoId, String grupoId, String formaOrgId, String descricao,
			String sexoStr, String idadeMinStr, String idadeMaxStr, String valorShStr, String valorProcedStr,
			String valorSpStr, DominioSexoDeterminante sexo, BigDecimal valorSh, BigDecimal valorProced,
			BigDecimal valorSp, Integer idadeMin, Integer idadeMax, Short vQtMaximaExecucao, Long codTabela,
			FatFormaOrganizacao fatFormaOrganizacao, Integer seqSusFinanciamento, Integer seqSusComplexidade,
			TipoProcessado tipoProcessado) {
		super();
		this.subGrupoId = subGrupoId;
		this.grupoId = grupoId;
		this.formaOrgId = formaOrgId;
		this.descricao = descricao;
		this.sexoStr = sexoStr;
		this.idadeMinStr = idadeMinStr;
		this.idadeMaxStr = idadeMaxStr;
		this.valorShStr = valorShStr;
		this.valorProcedStr = valorProcedStr;
		this.valorSpStr = valorSpStr;
		this.sexo = sexo;
		this.valorSh = valorSh;
		this.valorProced = valorProced;
		this.valorSp = valorSp;
		this.idadeMin = idadeMin;
		this.idadeMax = idadeMax;
		this.vQtMaximaExecucao = vQtMaximaExecucao;
		this.codTabela = codTabela;
		this.fatFormaOrganizacao = fatFormaOrganizacao;
		this.seqSusFinanciamento = seqSusFinanciamento;
		this.seqSusComplexidade = seqSusComplexidade;
		this.tipoProcessado = tipoProcessado;
	}


	public enum TipoProcessado {

		INCLUI("I"),
		ALTERA("A"),
		PROCESSADO("S"),
		NAO_PROCESSADO("N"),
		DESPREZADO("D");
		
		private String fields;

		private TipoProcessado(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	public String getSubGrupoId() {
		return subGrupoId;
	}


	public void setSubGrupoId(String subGrupoId) {
		this.subGrupoId = subGrupoId;
	}


	public String getGrupoId() {
		return grupoId;
	}


	public void setGrupoId(String grupoId) {
		this.grupoId = grupoId;
	}


	public String getFormaOrgId() {
		return formaOrgId;
	}


	public void setFormaOrgId(String formaOrgId) {
		this.formaOrgId = formaOrgId;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public String getSexoStr() {
		return sexoStr;
	}


	public void setSexoStr(String sexoStr) {
		this.sexoStr = sexoStr;
	}


	public String getIdadeMinStr() {
		return idadeMinStr;
	}


	public void setIdadeMinStr(String idadeMinStr) {
		this.idadeMinStr = idadeMinStr;
	}


	public String getIdadeMaxStr() {
		return idadeMaxStr;
	}


	public void setIdadeMaxStr(String idadeMaxStr) {
		this.idadeMaxStr = idadeMaxStr;
	}


	public String getValorShStr() {
		return valorShStr;
	}


	public void setValorShStr(String valorShStr) {
		this.valorShStr = valorShStr;
	}


	public String getValorProcedStr() {
		return valorProcedStr;
	}


	public void setValorProcedStr(String valorProcedStr) {
		this.valorProcedStr = valorProcedStr;
	}


	public String getValorSpStr() {
		return valorSpStr;
	}


	public void setValorSpStr(String valorSpStr) {
		this.valorSpStr = valorSpStr;
	}


	public DominioSexoDeterminante getSexo() {
		return sexo;
	}


	public void setSexo(DominioSexoDeterminante sexo) {
		this.sexo = sexo;
	}


	public BigDecimal getValorSh() {
		return valorSh;
	}


	public void setValorSh(BigDecimal valorSh) {
		this.valorSh = valorSh;
	}


	public BigDecimal getValorProced() {
		return valorProced;
	}


	public void setValorProced(BigDecimal valorProced) {
		this.valorProced = valorProced;
	}


	public BigDecimal getValorSp() {
		return valorSp;
	}


	public void setValorSp(BigDecimal valorSp) {
		this.valorSp = valorSp;
	}


	public Integer getIdadeMin() {
		return idadeMin;
	}


	public void setIdadeMin(Integer idadeMin) {
		this.idadeMin = idadeMin;
	}


	public Integer getIdadeMax() {
		return idadeMax;
	}


	public void setIdadeMax(Integer idadeMax) {
		this.idadeMax = idadeMax;
	}


	public Short getvQtMaximaExecucao() {
		return vQtMaximaExecucao;
	}


	public void setvQtMaximaExecucao(Short vQtMaximaExecucao) {
		this.vQtMaximaExecucao = vQtMaximaExecucao;
	}


	public Long getCodTabela() {
		return codTabela;
	}


	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}


	public FatFormaOrganizacao getFatFormaOrganizacao() {
		return fatFormaOrganizacao;
	}


	public void setFatFormaOrganizacao(FatFormaOrganizacao fatFormaOrganizacao) {
		this.fatFormaOrganizacao = fatFormaOrganizacao;
	}


	public Integer getSeqSusFinanciamento() {
		return seqSusFinanciamento;
	}


	public void setSeqSusFinanciamento(Integer seqSusFinanciamento) {
		this.seqSusFinanciamento = seqSusFinanciamento;
	}


	public Integer getSeqSusComplexidade() {
		return seqSusComplexidade;
	}


	public void setSeqSusComplexidade(Integer seqSusComplexidade) {
		this.seqSusComplexidade = seqSusComplexidade;
	}


	public TipoProcessado getTipoProcessado() {
		return tipoProcessado;
	}


	public void setTipoProcessado(TipoProcessado tipoProcessado) {
		this.tipoProcessado = tipoProcessado;
	}
	

 

}