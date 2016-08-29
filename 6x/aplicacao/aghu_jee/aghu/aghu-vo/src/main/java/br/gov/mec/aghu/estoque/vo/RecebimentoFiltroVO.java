package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoDevolucao;
import br.gov.mec.aghu.dominio.DominioTipoDevolucao;

public class RecebimentoFiltroVO implements Serializable {
	private static final long serialVersionUID = 8919020356375253000L;

	private Integer nrpSeq;
	private Boolean indConfirmado;
	private Boolean indEstorno;
	private Long numeroNota;
	private Date dtGeracaoIni;
	private Date dtGeracaoFim;
	private Integer numeroFornecedor;
	private Integer nrSeq;
	private Integer eslSeq;
	private Integer lctNumero;
	private Short nroComplemento;
	private Integer codigoMaterial;
	private Integer codigoServico;
	private DominioTipoDevolucao tipoDevolucao;
	private DominioSituacaoDevolucao situacaoDevolucao;

	public Integer getNrpSeq() {
		return nrpSeq;
	}

	public void setNrpSeq(Integer nrpSeq) {
		this.nrpSeq = nrpSeq;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public void setIndConfirmado(Boolean indConfirmado) {
		this.indConfirmado = indConfirmado;
	}

	public Boolean getIndConfirmado() {
		return indConfirmado;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}

	public Boolean getIndEstorno() {
		return indEstorno;
	}

	public void setDtGeracaoIni(Date dtGeracaoIni) {
		this.dtGeracaoIni = dtGeracaoIni;
	}

	public Date getDtGeracaoIni() {
		return dtGeracaoIni;
	}

	public void setDtGeracaoFim(Date dtGeracaoFim) {
		this.dtGeracaoFim = dtGeracaoFim;
	}

	public Date getDtGeracaoFim() {
		return dtGeracaoFim;
	}

	public void setNrSeq(Integer nrSeq) {
		this.nrSeq = nrSeq;
	}

	public Integer getNrSeq() {
		return nrSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}

	public Integer getEslSeq() {
		return eslSeq;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}

	public Integer getCodigoServico() {
		return codigoServico;
	}

	public void setNumeroNota(Long numeroNota) {
		this.numeroNota = numeroNota;
	}

	public Long getNumeroNota() {
		return numeroNota;
	}
	
	public void setTipoDevolucao(DominioTipoDevolucao tipoDevolucao) {
		this.tipoDevolucao = tipoDevolucao;
	}

	public DominioTipoDevolucao getTipoDevolucao() {
		return tipoDevolucao;
	}
	
	public void setSituacaoDevolucao(DominioSituacaoDevolucao situacaoDevolucao) {
		this.situacaoDevolucao = situacaoDevolucao;
	}

	public DominioSituacaoDevolucao getSituacaoDevolucao() {
		return situacaoDevolucao;
	}
}
