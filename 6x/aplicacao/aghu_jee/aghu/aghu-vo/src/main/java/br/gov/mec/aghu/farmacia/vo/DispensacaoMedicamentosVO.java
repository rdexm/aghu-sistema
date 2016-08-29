package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.StringUtil;


public class DispensacaoMedicamentosVO implements Serializable, BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5260834110192693444L;

	private String descricaoEdit;
	
	private String tprSigla;
	
	private Integer matCodigo;
	
	private String descricaoSigla;
	
	private String descricao;
	
	private BigDecimal qtdeSolicitadaDispensacao;
	
	private BigDecimal qtdeDispensadaDispensacao;
	
	//Obter da tabela AfaTipoOcorDispensacoes pela chave da AfaDispensacaoMdtos
	private Short seqTipoOcorrencia;
	
	private String descricaoTipoOcorrencia;
	
	private Short seqUnidadeFuncional;

	private String descricaoUnidadeFuncional;
	
	private String situacaoDispensacao;
	
	
	//Valores do Hint
	private String nomeTriadoPor;
	private String dataTriadoPor;
	
	//Dados do servidor
	private String nomeDispensadoPor;
	private String dataDispensadoPor;
	
	private String nomeConferidoPor;
	private String dataConferidoPor;
	
	private String nomeEntreguePor;
	private String dataEntreguePor;
	
	public String getDescricaoTrunc(Long size) {
		return StringUtil.trunc(descricao, true, size);
	}
	
	public String getDescricaoEditTrunc(Long size) {
		return StringUtil.trunc(descricaoEdit, true, size);
	}
	
	public String getTprSiglaTrunc(Long size) {
		return StringUtil.trunc(tprSigla, true, size);
	}
	
	public String getCodigoDescricaoTipoOcorrencia() {
		String codigoDescricao;
		if(seqTipoOcorrencia == null || descricaoTipoOcorrencia == null){
			return "";
		}
		codigoDescricao = seqTipoOcorrencia + " - " + descricaoTipoOcorrencia;
		return codigoDescricao;
	}
	
	public String getCodigoDescricaoTipoOcorrenciaTrunc(Long size) {
		return StringUtil.trunc(getCodigoDescricaoTipoOcorrencia(), true, size);
	}
	
	public String getCodigoDescricaoUnidadeFuncional() {
		String codigoDescricao;
		codigoDescricao = seqUnidadeFuncional + " - " + descricaoUnidadeFuncional;
		return codigoDescricao;
	}
	
	public String getCodigoDescricaoUnidadeFuncionalTrunc(Long size) {
		return StringUtil.trunc(getCodigoDescricaoUnidadeFuncional(), true, size);
	}

	public String getDescricaoEdit() {
		return descricaoEdit;
	}

	public void setDescricaoEdit(String descricaoEdit) {
		this.descricaoEdit = descricaoEdit;
	}

	public String getTprSigla() {
		return tprSigla;
	}

	public void setTprSigla(String tprSigla) {
		this.tprSigla = tprSigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getQtdeSolicitadaDispensacao() {
		return qtdeSolicitadaDispensacao;
	}

	public void setQtdeSolicitadaDispensacao(BigDecimal qtdeSolicitadaDispensacao) {
		this.qtdeSolicitadaDispensacao = qtdeSolicitadaDispensacao;
	}

	public BigDecimal getQtdeDispensadaDispensacao() {
		return qtdeDispensadaDispensacao;
	}

	public void setQtdeDispensadaDispensacao(BigDecimal qtdeDispensadaDispensacao) {
		this.qtdeDispensadaDispensacao = qtdeDispensadaDispensacao;
	}

	public Short getSeqTipoOcorrencia() {
		return seqTipoOcorrencia;
	}

	public void setSeqTipoOcorrencia(Short seqTipoOcorrencia) {
		this.seqTipoOcorrencia = seqTipoOcorrencia;
	}

	public String getDescricaoTipoOcorrencia() {
		return descricaoTipoOcorrencia;
	}

	public void setDescricaoTipoOcorrencia(String descricaoTipoOcorrencia) {
		this.descricaoTipoOcorrencia = descricaoTipoOcorrencia;
	}

	public Short getSeqUnidadeFuncional() {
		return seqUnidadeFuncional;
	}

	public void setSeqUnidadeFuncional(Short seqUnidadeFuncional) {
		this.seqUnidadeFuncional = seqUnidadeFuncional;
	}

	public String getDescricaoUnidadeFuncional() {
		return descricaoUnidadeFuncional;
	}

	public void setDescricaoUnidadeFuncional(String descricaoUnidadeFuncional) {
		this.descricaoUnidadeFuncional = descricaoUnidadeFuncional;
	}

	public String getSituacaoDispensacao() {
		return situacaoDispensacao;
	}

	public void setSituacaoDispensacao(String situacaoDispensacao) {
		this.situacaoDispensacao = situacaoDispensacao;
	}

	public String getNomeTriadoPor() {
		return nomeTriadoPor;
	}

	public void setNomeTriadoPor(String nomeTriadoPor) {
		this.nomeTriadoPor = nomeTriadoPor;
	}

	public String getDataTriadoPor() {
		return dataTriadoPor;
	}

	public void setDataTriadoPor(String dataTriadoPor) {
		this.dataTriadoPor = dataTriadoPor;
	}

	public String getNomeDispensadoPor() {
		return nomeDispensadoPor;
	}

	public void setNomeDispensadoPor(String nomeDispensadoPor) {
		this.nomeDispensadoPor = nomeDispensadoPor;
	}

	public String getDataDispensadoPor() {
		return dataDispensadoPor;
	}

	public void setDataDispensadoPor(String dataDispensadoPor) {
		this.dataDispensadoPor = dataDispensadoPor;
	}

	public String getNomeConferidoPor() {
		return nomeConferidoPor;
	}

	public void setNomeConferidoPor(String nomeConferidoPor) {
		this.nomeConferidoPor = nomeConferidoPor;
	}

	public String getDataConferidoPor() {
		return dataConferidoPor;
	}

	public void setDataConferidoPor(String dataConferidoPor) {
		this.dataConferidoPor = dataConferidoPor;
	}

	public String getNomeEntreguePor() {
		return nomeEntreguePor;
	}

	public void setNomeEntreguePor(String nomeEntreguePor) {
		this.nomeEntreguePor = nomeEntreguePor;
	}

	public String getDataEntreguePor() {
		return dataEntreguePor;
	}

	public void setDataEntreguePor(String dataEntreguePor) {
		this.dataEntreguePor = dataEntreguePor;
	}

	public void setDescricaoSigla(String descricaoSigla) {
		this.descricaoSigla = descricaoSigla;
	}

	public String getDescricaoSigla() {
		return descricaoSigla;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
}