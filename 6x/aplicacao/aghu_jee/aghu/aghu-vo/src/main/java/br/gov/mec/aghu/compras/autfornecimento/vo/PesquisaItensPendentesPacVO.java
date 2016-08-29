package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PesquisaItensPendentesPacVO implements Serializable, Comparable<PesquisaItensPendentesPacVO> {

	private static final long serialVersionUID = -101869066447890000L;

	private Short numero; // ITL.NUMERO
	private SituacaoItem situacao;
	private Date dtJulgada;
	private Long cgc;
	private Long cpf;
	private String razaoSocial;
	private String descricaoMotivoSituacao;
	private DominioTipoFaseSolicitacao dominioTipoFaseSolicitacao; // FSL.TIPO
	private BigDecimal valorUnitario;
	private String descricaoFormaPagamento;
	private ScoSolicitacaoDeCompra solicitacaoDeCompra;
	private ScoSolicitacaoServico solicitacaoServico;
	private String motivoExclusao;
	private Date dtExclusao;


	public PesquisaItensPendentesPacVO() {

	}

	public SituacaoItem getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoItem situacao) {
		this.situacao = situacao;
	}

	public String getDtJulgadaFormatada() {
		return DateUtil.dataToString(this.dtJulgada, null);
	}

	public Date getDtJulgada() {
		return dtJulgada;
	}

	public void setDtJulgada(Date dtJulgada) {
		this.dtJulgada = dtJulgada;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getDescricaoMotivoSituacao() {
		return descricaoMotivoSituacao;
	}

	public void setDescricaoMotivoSituacao(String descricaoMotivoSituacao) {
		this.descricaoMotivoSituacao = descricaoMotivoSituacao;
	}

	public DominioTipoFaseSolicitacao getDominioTipoFaseSolicitacao() {
		return dominioTipoFaseSolicitacao;
	}

	public void setDominioTipoFaseSolicitacao(DominioTipoFaseSolicitacao dominioTipoFaseSolicitacao) {
		this.dominioTipoFaseSolicitacao = dominioTipoFaseSolicitacao;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public String getDescricaoFormaPagamento() {
		return descricaoFormaPagamento;
	}

	public void setDescricaoFormaPagamento(String descricaoFormaPagamento) {
		this.descricaoFormaPagamento = descricaoFormaPagamento;
	}

	public void setNumero(Short numero) {
		this.numero = numero;
	}

	public Short getNumero() {
		return numero;
	}

	public String getDescricaoSituacao() {
		if (this.getSituacao() == null) {
			return null;
		}
		return this.getSituacao().getDescricao();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
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
		PesquisaItensPendentesPacVO other = (PesquisaItensPendentesPacVO) obj;
		if (numero == null) {
			if (other.numero != null) {
				return false;
			}
		} else {
			if (!numero.equals(other.numero)) {
			return false;
			}
		}
		return true;
	}

	public Long getCpfCnpj() {
		return getCpf() == null ? getCgc() : getCpf();
	}

	public String getCpfCnpjFormatado() {
		if (this.getCpf() == null) {
			return CoreUtil.formatarCNPJ(this.getCgc());
		}
		return CoreUtil.formataCPF(this.getCpf());
	}

	public void setSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		this.solicitacaoDeCompra = solicitacaoDeCompra;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoDeCompra() {
		return solicitacaoDeCompra;
	}


	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}


	public enum Fields {
		ITL_NUMERO("numero"), // ITL.NUMERO
		DT_JULGADA("dtJulgada"), 
		CGC("cgc"), 
		CPF("cpf"), 
		RAZAO_SOCIAL("razaoSocial"), 
		TIPO("dominioTipoFaseSolicitacao"), 
		VALOR_UNITARIO("valorUnitario"), 
		FPG_DESCRICAO("descricaoFormaPagamento"), 
		SOLICITACAO_DE_COMPRA("solicitacaoDeCompra"),
		SOLICITACAO_SERVICO("solicitacaoServico"),
		MOTIVO_EXCLUSAO("motivoExclusao"),
		DT_EXCLUSAO("dtExclusao"),
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public enum SituacaoItem {
		EXCLUIDO(1), SEM_PROPOSTA(2), NAO_JULGADO(3), SEM_AF(4);
		private Integer codigo;

		SituacaoItem(final Integer codigo) {
			this.setCodigo(codigo);
		}

		public String getDescricao() {
			switch (this) {
			case EXCLUIDO:
				return "Excluído";
			case SEM_PROPOSTA:
				return "Sem Proposta";
			case NAO_JULGADO:
				return "Não Julgado";
			case SEM_AF:
				return "Sem AF";
			default:
				return "";
			}
		}

		public void setCodigo(Integer codigo) {
			this.codigo = codigo;
		}

		public Integer getCodigo() {
			return codigo;
		}
	}

	@Override
	public int compareTo(PesquisaItensPendentesPacVO obj) {
		if (this == obj) {
			return 0;
		}
		if (obj == null) {
			return -1;
		}
		return this.getNumero().compareTo(obj.getNumero());
	}

	public void setMotivoExclusao(String motivoExclusao) {
		this.motivoExclusao = motivoExclusao;
	}

	public String getMotivoExclusao() {
		return motivoExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}

	public Date getDtExclusao() {
		return dtExclusao;
	}
}
