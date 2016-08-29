package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaGeralPIAFVO implements BaseBean {
	
	private static final long serialVersionUID = -7468004122874334159L;
	
	private Integer lctNumero;
	private Integer frnNumero;
	private Short nroComplemento;
	private Integer iafNumero;
	private Integer parcela;
	private ScoMaterial material;
	private ScoServico servico;
	private String unidadeCodigo;
	private Integer qtde;
	private Integer qtdeEntregue;
	private Double valorEfetivado;
	private Integer matricula;
	private Short vinCodigo;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private RapServidores servidorGestor;
	private ScoFornecedor fornecedor;
	private Integer saldo;
	
	
	public PesquisaGeralPIAFVO() {
		super();
	}
	
	public enum Fields {
		PFR_LCT_NUMERO("lctNumero"),
		PFR_FRN_NUMERO("frnNumero"),
		NRO_COMPLEMENTO("nroComplemento"),
		IAF_NUMERO("iafNumero"),
		PARCELA("parcela"),
		MATERIAL("material"),
		SERVICO("servico"),
		UNIDADE_CODIGO("unidadeCodigo"),
		QTDE("qtde"),
		QTDE_ENTREGUE("qtdeEntregue"),
		VALOR_EFETIVADO("valorEfetivado"),
		SER_MATRICULA_GESTOR("matricula"),
		SER_VIN_CODIGO_GESTOR("vinCodigo"),
		SITUACAO("situacao"),
		MODALIDADE_LICITACAO("modalidadeLicitacao"),
		SERVIDOR_GESTOR("servidorGestor"),
		FORNECEDOR("fornecedor"),
		SALDO("saldo");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Integer getFrnNumero() {
		return frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Integer getParcela() {
		return parcela;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public String getUnidadeCodigo() {
		return unidadeCodigo;
	}

	public void setUnidadeCodigo(String unidadeCodigo) {
		this.unidadeCodigo = unidadeCodigo;
	}

	public Integer getQtde() {
		return qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	public Integer getQtdeEntregue() {
		return qtdeEntregue;
	}

	public void setQtdeEntregue(Integer qtdeEntregue) {
		this.qtdeEntregue = qtdeEntregue;
	}

	public Double getValorEfetivado() {
		return valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}

	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getSaldo() {
		return saldo;
	}

	public void setSaldo(Integer saldo) {
		this.saldo = saldo;
	}

}
