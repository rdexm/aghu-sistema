package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;

public class TicketMdtoDispensadoVO implements Serializable {

	private static final long serialVersionUID = -5867431499043389311L;

	private Long dispensacaoMdtoSeq;
	private Boolean mdtoControlado;
	private String mdtoDescricao;
	private String mdtoSigla;
	private Integer mdtoCodigo;
	private BigDecimal qtdeDispensada;
	private DominioSituacaoDispensacaoMdto indSituacao;
	private BigDecimal qtdeEstornada;
	private Date dthrTicket;
	private Date dthrDispensacao;
	private BigDecimal qtdUtilizada;
	private Long qtdUtilizadaLong;
	private Boolean checkboxReadonly;
	private Boolean checkboxSelecionado;
	private String quantidade;

	public TicketMdtoDispensadoVO() {

	}

	public enum Fields {
		DISPENSACAO_MDTO_SEQ("dispensacaoMdtoSeq"),
		MDTO_CONTROLADO("mdtoControlado"),
		MDTO_DESCRICAO("mdtoDescricao"),
		MDTO_SIGLA("mdtoSigla"),
		MDTO_CODIGO("mdtoCodigo"),
		QTD_DISPENSADA("qtdeDispensada"),
		IND_SITUACAO("indSituacao"),
		QTD_ESTORNADA("qtdeEstornada"),
		DTHR_TICKET("dthrTicket"),
		DTHR_DISPENSACAO("dthrDispensacao"),
		//QTD_UTILIZADA("qtdUtilizada"),
		QTD_UTILIZADA_LONG("qtdUtilizadaLong");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getDispensacaoMdtoSeq() {
		return dispensacaoMdtoSeq;
	}

	public void setDispensacaoMdtoSeq(Long dispensacaoMdtoSeq) {
		this.dispensacaoMdtoSeq = dispensacaoMdtoSeq;
	}

	public Boolean getMdtoControlado() {
		return mdtoControlado;
	}

	public void setMdtoControlado(Boolean mdtoControlado) {
		this.mdtoControlado = mdtoControlado;
	}

	public String getMdtoDescricao() {
		return mdtoDescricao;
	}

	public void setMdtoDescricao(String mdtoDescricao) {
		this.mdtoDescricao = mdtoDescricao;
	}

	public Integer getMdtoCodigo() {
		return mdtoCodigo;
	}

	public void setMdtoCodigo(Integer mdtoCodigo) {
		this.mdtoCodigo = mdtoCodigo;
	}

	public BigDecimal getQtdeDispensada() {
		return qtdeDispensada;
	}

	public void setQtdeDispensada(BigDecimal qtdeDispensada) {
		this.qtdeDispensada = qtdeDispensada;
	}

	public DominioSituacaoDispensacaoMdto getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoDispensacaoMdto indSituacao) {
		this.indSituacao = indSituacao;
	}

	public BigDecimal getQtdeEstornada() {
		return qtdeEstornada;
	}

	public void setQtdeEstornada(BigDecimal qtdeEstornada) {
		this.qtdeEstornada = qtdeEstornada;
	}

	public Date getDthrTicket() {
		return dthrTicket;
	}

	public void setDthrTicket(Date dthrTicket) {
		this.dthrTicket = dthrTicket;
	}

	public Date getDthrDispensacao() {
		return dthrDispensacao;
	}

	public void setDthrDispensacao(Date dthrDispensacao) {
		this.dthrDispensacao = dthrDispensacao;
	}

	public BigDecimal getQtdUtilizada() {
		return qtdUtilizada;
	}

	public void setQtdUtilizada(BigDecimal qtdUtilizada) {
		this.qtdUtilizada = qtdUtilizada;
	}

	public void setMdtoSigla(String mdtoSigla) {
		this.mdtoSigla = mdtoSigla;
	}

	public String getMdtoSigla() {
		return mdtoSigla;
	}

	public void setCheckboxReadonly(Boolean checkboxReadonly) {
		this.checkboxReadonly = checkboxReadonly;
	}

	public Boolean getCheckboxReadonly() {
		return checkboxReadonly;
	}

	public void setCheckboxSelecionado(Boolean checkboxSelecionado) {
		this.checkboxSelecionado = checkboxSelecionado;
	}

	public Boolean getCheckboxSelecionado() {
		return checkboxSelecionado;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

	public String getQuantidade() {
		return quantidade;
	}

	public Long getQtdUtilizadaLong() {
		return qtdUtilizadaLong;
	}

	public void setQtdUtilizadaLong(Long qtdUtilizadaLong) {
		this.qtdUtilizadaLong = qtdUtilizadaLong;
	}
}