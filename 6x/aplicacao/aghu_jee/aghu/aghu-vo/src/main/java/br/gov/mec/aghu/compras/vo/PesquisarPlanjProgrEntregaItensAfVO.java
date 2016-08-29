package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisarPlanjProgrEntregaItensAfVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2802331515830019587L;

	private Integer numeroAF;
	private Integer numeroLicitacao;
	private Short complemento;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private String modalidadeCompra;
	private String descricaoModalidadeCompra;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private Integer numeroFornecedor;
	private String nomeFornecedor;
	private Date vencimentoContrato;
	private String program;
	private String autom;
	private String gera;
	private String sitEntrega;
	
	private String hintSitEntrega;
	private String hintProgram;
	
	private String coloracaoNumeroAF;
	private String coloracaoProgram;
	private String coloracaoAutom;
	private String coloracaoGera;
	private String coloracaoSitEntrega;
	
	private Boolean selecionado=false;
	
	private Integer pfrLctNumero;
	
	public PesquisarPlanjProgrEntregaItensAfVO() {
		super();
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}
	
	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}

	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}

	public String getModalidadeCompra() {
		return modalidadeCompra;
	}

	public void setModalidadeCompra(String modalidadeCompra) {
		this.modalidadeCompra = modalidadeCompra;
	}

	public String getDescricaoModalidadeCompra() {
		return descricaoModalidadeCompra;
	}

	public void setDescricaoModalidadeCompra(String descricaoModalidadeCompra) {
		this.descricaoModalidadeCompra = descricaoModalidadeCompra;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}

	public String getFornecedor() {
		return numeroFornecedor + " - " + nomeFornecedor;
	}

	public Date getVencimentoContrato() {
		return vencimentoContrato;
	}

	public void setVencimentoContrato(Date vencimentoContrato) {
		this.vencimentoContrato = vencimentoContrato;
	}
	
	public String getProgram() {
		return program;
	}

	public void setProgram(String program) {
		this.program = program;
	}

	public String getAutom() {
		return autom;
	}

	public void setAutom(String autom) {
		this.autom = autom;
	}

	public String getGera() {
		return gera;
	}

	public void setGera(String gera) {
		this.gera = gera;
	}

	public String getSitEntrega() {
		return sitEntrega;
	}

	public void setSitEntrega(String sitEntrega) {
		this.sitEntrega = sitEntrega;
	}

	public String getHintSitEntrega() {
		return hintSitEntrega;
	}

	public void setHintSitEntrega(String hintSitEntrega) {
		this.hintSitEntrega = hintSitEntrega;
	}

	public String getColoracaoProgram() {

		return coloracaoProgram;
	}

	public void setColoracaoProgram(String coloracaoProgram) { this.coloracaoProgram = coloracaoProgram; }

	public String getColoracaoNumeroAF() {
		return coloracaoNumeroAF;
	}

	public void setColoracaoNumeroAF(String coloracaoNumeroAF) {
		this.coloracaoNumeroAF = coloracaoNumeroAF;
	}

	public String getColoracaoAutom() {

		return coloracaoAutom;
	}

	public void setColoracaoAutom(String coloracaoAutom) {
		this.coloracaoAutom = coloracaoAutom;
	}

	public String getColoracaoGera() {

		return coloracaoGera;
	}

	public void setColoracaoGera(String coloracaoGera) {
		this.coloracaoGera = coloracaoGera;
	}

	public String getColoracaoSitEntrega() {

		return coloracaoSitEntrega;
	}

	public void setColoracaoSitEntrega(String coloracaoSitEntrega) {
		this.coloracaoSitEntrega = coloracaoSitEntrega;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}

	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}

	public String getHintProgram() {
		return hintProgram;
	}

	public void setHintProgram(String hintProgram) {
		this.hintProgram = hintProgram;
	}




	public enum Fields {
		NUMERO_AF("numeroAF"),
		NUMERO_LICITACAO("numeroLicitacao"),
		COMPLEMENTO("complemento"), 
		SITUACAO("situacao"),
		MODALIDADE_COMPRA("modalidadeCompra"),
		DESCRICAO_MODALIDADE_COMPRA("descricaoModalidadeCompra"),
		MODALIDADE_EMPENHO("modalidadeEmpenho"), 
		NRO_FORNECEDOR("numeroFornecedor"), 
		NOME_FORNECEDOR("nomeFornecedor"),
		VENCIMENTO_CONTRATO("vencimentoContrato"),
		PFR_LCT_NUMERO("pfrLctNumero"); 

		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()){
			return false;
		}

		PesquisarPlanjProgrEntregaItensAfVO that = (PesquisarPlanjProgrEntregaItensAfVO) o;

		if (numeroAF != null ? !numeroAF.equals(that.numeroAF) : that.numeroAF != null) {
			return false;
		}
		if (numeroLicitacao != null ? !numeroLicitacao.equals(that.numeroLicitacao) : that.numeroLicitacao != null) {
			return false;
		}

		return !(complemento != null ? !complemento.equals(that.complemento) : that.complemento != null);

	}

	@Override
	public int hashCode() {
		int result = numeroAF != null ? numeroAF.hashCode() : 0;
		result = 31 * result + (numeroLicitacao != null ? numeroLicitacao.hashCode() : 0);
		result = 31 * result + (complemento != null ? complemento.hashCode() : 0);
		return result;
	}
}
