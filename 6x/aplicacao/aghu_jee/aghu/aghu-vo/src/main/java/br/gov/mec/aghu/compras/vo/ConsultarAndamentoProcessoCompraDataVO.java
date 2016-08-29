package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoLicitacao;
import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ConsultarAndamentoProcessoCompraDataVO implements Serializable, BaseBean {

	private static final long serialVersionUID = 1094758748075863219L;
	
	private Integer pacSeq;
	private Short lcpCodigo;
	private Date dtEntrada;
	private Date dtSaida;
	
	/**
	 * Campos do dataModel da tela
	 */
	private Integer numeroPac;
	private ScoModalidadeLicitacao modalidade;
	private DominioTipoLicitacao tipo;
	private Date geracao;
	private Date abertura;
	private String situacao;
	private String objeto;
	private Integer af;
	private Short cp;
	private Date vencimentoContrato;
	private Integer maisAFs;
	
	/**
	 * Valores usados para montar a tooltip Objeto na tela
	 * @author salaberri
	 *
	 */
	private String gestorValue;
	private Integer diasPermanencia;
	private String responsavelLocal;
	private RapRamalTelefonico ramal;
	private String localizaoDescricao;
	
	/**
	 * Valor final da Tooltip Objeto
	 * @author salaberri
	 *
	 */
	private String tooltipObjetoValue;
	
	/**
	 * Valores retornados da C4
	 */
	private DominioSituacaoAutorizacaoFornecimento situacaoAF;
	private Date dataGeracaoAF;
	
	public enum Fields {
		SEQ_PAC("seqPac"),
		NUMERO_PAC("numeroPac"),
		DATA_ENTRADA("dtEntrada"),
		DATA_SAIDA("dtSaida");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getPacSeq() {
		return pacSeq;
	}

	public void setPacSeq(Integer pacSeq) {
		this.pacSeq = pacSeq;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public Date getDtEntrada() {
		return dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	public Date getDtSaida() {
		return dtSaida;
	}

	public void setDtSaida(Date dtSaida) {
		this.dtSaida = dtSaida;
	}

	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}

	public DominioTipoLicitacao getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoLicitacao tipo) {
		this.tipo = tipo;
	}

	public Date getGeracao() {
		return geracao;
	}

	public void setGeracao(Date geracao) {
		this.geracao = geracao;
	}

	public Date getAbertura() {
		return abertura;
	}

	public void setAbertura(Date abertura) {
		this.abertura = abertura;
	}

	public String getObjeto() {
		return objeto;
	}

	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setMaisAFs(Integer maisAFs) {
		this.maisAFs = maisAFs;
	}

	public Integer getMaisAFs() {
		return maisAFs;
	}

	public Integer getAf() {
		return af;
	}

	public void setAf(Integer af) {
		this.af = af;
	}

	public Short getCp() {
		return cp;
	}

	public void setCp(Short cp) {
		this.cp = cp;
	}

	public Date getVencimentoContrato() {
		return vencimentoContrato;
	}

	public void setVencimentoContrato(Date vencimentoContrato) {
		this.vencimentoContrato = vencimentoContrato;
	}

	public void setLcpCodigo(Short lcpCodigo) {
		this.lcpCodigo = lcpCodigo;
	}

	public Short getLcpCodigo() {
		return lcpCodigo;
	}

	public void setGestorValue(String gestorValue) {
		this.gestorValue = gestorValue;
	}

	public String getGestorValue() {
		return gestorValue;
	}

	public void setDiasPermanencia(Integer diasPermanencia) {
		this.diasPermanencia = diasPermanencia;
	}

	public Integer getDiasPermanencia() {
		return diasPermanencia;
	}

	public void setResponsavelLocal(String responsavelLocal) {
		this.responsavelLocal = responsavelLocal;
	}

	public String getResponsavelLocal() {
		return responsavelLocal;
	}

	public void setRamal(RapRamalTelefonico ramal) {
		this.ramal = ramal;
	}

	public RapRamalTelefonico getRamal() {
		return ramal;
	}

	public void setLocalizaoDescricao(String localizaoDescricao) {
		this.localizaoDescricao = localizaoDescricao;
	}

	public String getLocalizaoDescricao() {
		return localizaoDescricao;
	}

	public void setTooltipObjetoValue(String tooltipObjetoValue) {
		this.tooltipObjetoValue = tooltipObjetoValue;
	}

	public String getTooltipObjetoValue() {
		return tooltipObjetoValue;
	}

	public void setSituacaoAF(DominioSituacaoAutorizacaoFornecimento situacaoAF) {
		this.situacaoAF = situacaoAF;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacaoAF() {
		return situacaoAF;
	}

	public void setDataGeracaoAF(Date dataGeracaoAF) {
		this.dataGeracaoAF = dataGeracaoAF;
	}

	public Date getDataGeracaoAF() {
		return dataGeracaoAF;
	}

}
