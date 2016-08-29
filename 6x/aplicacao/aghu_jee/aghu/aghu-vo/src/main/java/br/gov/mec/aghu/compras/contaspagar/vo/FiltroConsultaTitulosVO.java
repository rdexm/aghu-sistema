package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.dominio.DominioTipoPagamento;
import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoFornecedor;

/**
 * Filtro da estória #37746
 * @author aghu
 *
 */
public class FiltroConsultaTitulosVO implements Serializable {

	private static final long serialVersionUID = -8837903111653606311L;

	/**
	 * Força instanciar com os parâmetros padrão
	 * @param situacaoTitulo
	 * @param programado
	 * @param estornado
	 */
	public FiltroConsultaTitulosVO(DominioSituacaoTitulo situacaoTitulo, DominioSimNao programado, DominioSimNao estornado, DominioSimNao pgtoEstornado) {
		this.situacaoTitulo = situacaoTitulo;
		this.programado = programado;
		this.estornado = estornado;
		this.pgtoEstornado = pgtoEstornado;
	}

	private Long notaRecebimento;
	private Integer titulo;
	private DominioSituacaoTitulo situacaoTitulo;
	private Date dataInicial;
	private Date dataFinal;
	private Long notaFiscal;
	private DominioSimNao programado;
	private Date dataGeracaoNR;
	private ScoFornecedor scoFornecedor;
	private FsoNaturezaDespesa fsoNaturezaDespesa;
	private FsoVerbaGestao fsoVerbaGestao;
	private Long numeroAF;
	private Long complementoAF;
	private DominioTipoPagamento tipoPagamento;
	private FcpTipoDocumentoPagamento fcpTipoDocPagamento;
	private Long numeroDocumento;
	private DominioSimNao estornado;
	private DominioSimNao inss;
	private Date dataInicialPag;
	private Date dataFinalPag;
	private DominioSimNao pgtoEstornado;

	public Long getNotaRecebimento() {
		return notaRecebimento;
	}

	public void setNotaRecebimento(Long notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	public Integer getTitulo() {
		return titulo;
	}

	public void setTitulo(Integer titulo) {
		this.titulo = titulo;
	}

	public DominioSituacaoTitulo getSituacaoTitulo() {
		return situacaoTitulo;
	}

	public void setSituacaoTitulo(DominioSituacaoTitulo situacaoTitulo) {
		this.situacaoTitulo = situacaoTitulo;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Long getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public DominioSimNao getProgramado() {
		return programado;
	}

	public void setProgramado(DominioSimNao programado) {
		this.programado = programado;
	}

	public Date getDataGeracaoNR() {
		return dataGeracaoNR;
	}

	public void setDataGeracaoNR(Date dataGeracaoNR) {
		this.dataGeracaoNR = dataGeracaoNR;
	}

	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	public FsoNaturezaDespesa getFsoNaturezaDespesa() {
		return fsoNaturezaDespesa;
	}

	public void setFsoNaturezaDespesa(FsoNaturezaDespesa fsoNaturezaDespesa) {
		this.fsoNaturezaDespesa = fsoNaturezaDespesa;
	}

	public FsoVerbaGestao getFsoVerbaGestao() {
		return fsoVerbaGestao;
	}

	public void setFsoVerbaGestao(FsoVerbaGestao fsoVerbaGestao) {
		this.fsoVerbaGestao = fsoVerbaGestao;
	}

	public Long getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Long numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Long getComplementoAF() {
		return complementoAF;
	}

	public void setComplementoAF(Long complementoAF) {
		this.complementoAF = complementoAF;
	}

	public DominioTipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(DominioTipoPagamento tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public FcpTipoDocumentoPagamento getFcpTipoDocPagamento() {
		return fcpTipoDocPagamento;
	}

	public void setFcpTipoDocPagamento(FcpTipoDocumentoPagamento fcpTipoDocPagamento) {
		this.fcpTipoDocPagamento = fcpTipoDocPagamento;
	}

	public Long getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(Long numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public DominioSimNao getEstornado() {
		return estornado;
	}

	public void setEstornado(DominioSimNao estornado) {
		this.estornado = estornado;
	}

	public DominioSimNao getInss() {
		return inss;
	}

	public void setInss(DominioSimNao inss) {
		this.inss = inss;
	}

	public Date getDataInicialPag() {
		return dataInicialPag;
	}

	public void setDataInicialPag(Date dataInicialPag) {
		this.dataInicialPag = dataInicialPag;
	}

	public Date getDataFinalPag() {
		return dataFinalPag;
	}

	public void setDataFinalPag(Date dataFinalPag) {
		this.dataFinalPag = dataFinalPag;
	}

	public DominioSimNao getPgtoEstornado() {
		return pgtoEstornado;
	}

	public void setPgtoEstornado(DominioSimNao pgtoEstornado) {
		this.pgtoEstornado = pgtoEstornado;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.titulo);
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FiltroConsultaTitulosVO other = (FiltroConsultaTitulosVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getTitulo(), other.getTitulo());
		return umEqualsBuilder.isEquals();
	}

}
