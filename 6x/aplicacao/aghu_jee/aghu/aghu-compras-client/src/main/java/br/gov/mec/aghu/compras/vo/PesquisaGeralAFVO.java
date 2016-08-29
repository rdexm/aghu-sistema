package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaGeralAFVO implements BaseBean {
	
	private static final long serialVersionUID = -7468004122874334159L;
	
	private Integer lctNumero;
	private Integer frnNumero;
	private Short nroComplemento;
	private Integer afpNumero;
	private Short sequenciaAlteracao;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Date dtGeracao;
	private Integer matricula;
	private Short vinCodigo;
	private RapServidores servidorGestor;
	private ScoFornecedor fornecedor;
	
	
	public PesquisaGeralAFVO() {
		super();
	}
	
	public enum Fields {
		PFR_LCT_NUMERO("lctNumero"),
		PFR_FRN_NUMERO("frnNumero"),
		NRO_COMPLEMENTO("nroComplemento"),
		SEQUENCIA_ALTERACAO("sequenciaAlteracao"),
		MODALIDADE_LICITACAO("modalidadeLicitacao"),
		SITUACAO("situacao"),
		DT_GERACAO("dtGeracao"),
		SER_MATRICULA_GESTOR("matricula"),
		SER_VIN_CODIGO_GESTOR("vinCodigo"),
		SERVIDOR_GESTOR("servidorGestor"),
		FORNECEDOR("fornecedor");
		
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

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
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
	
	public Integer getAfpNumero() {
		return afpNumero;
	}

	public void setAfpNumero(Integer afpNumero) {
		this.afpNumero = afpNumero;
	}
	
	
}
