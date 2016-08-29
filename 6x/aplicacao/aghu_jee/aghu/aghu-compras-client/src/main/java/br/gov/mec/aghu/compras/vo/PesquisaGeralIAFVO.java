package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaGeralIAFVO implements BaseBean {
	
	private static final long serialVersionUID = -7468004122874334159L;
	
	private Integer lctNumero;
	private Integer frnNumero;
	private Short nroComplemento;
	private Short sequenciaAlteracao;
	private Integer iafNumero;
	private ScoMaterial material;
	private ScoServico servico;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Integer matricula;
	private Short vinCodigo;
	private RapServidores servidorGestor;
	private ScoFornecedor fornecedor;
	private String unidadeCodigo;
	private Integer qtdeSolicitada;
	private Integer qtdeRecebida;
	private Integer saldo;
	private Double valorTotal;
	private Double valorUnitario;
	
	
	public PesquisaGeralIAFVO() {
		super();
	}
	
	public enum Fields {
		PFR_LCT_NUMERO("lctNumero"),
		PFR_FRN_NUMERO("frnNumero"),
		NRO_COMPLEMENTO("nroComplemento"),
		SEQUENCIA_ALTERACAO("sequenciaAlteracao"),
		IAF_NUMERO("iafNumero"),
		MATERIAL("material"),
		SERVICO("servico"),
		MODALIDADE_LICITACAO("modalidadeLicitacao"),
		SITUACAO("situacao"),
		SER_MATRICULA_GESTOR("matricula"),
		SER_VIN_CODIGO_GESTOR("vinCodigo"),
		SERVIDOR_GESTOR("servidorGestor"),
		FORNECEDOR("fornecedor"),
		UNIDADE_CODIGO("unidadeCodigo"),
		QUANTIDADE_SOLICITADA("qtdeSolicitada"),
		QUANTIDADE_RECEBIDA("qtdeRecebida"),
		SALDO("saldo"),
		VALOR_TOTAL("valorTotal"),
		VALOR_UNITARIO("valorUnitario");
		
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

	public Short getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Short sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
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

	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
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

	public String getUnidadeCodigo() {
		return unidadeCodigo;
	}

	public void setUnidadeCodigo(String unidadeCodigo) {
		this.unidadeCodigo = unidadeCodigo;
	}

	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	public Integer getQtdeRecebida() {
		return qtdeRecebida;
	}

	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}

	public Integer getSaldo() {
		return saldo;
	}

	public void setSaldo(Integer saldo) {
		this.saldo = saldo;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
}
