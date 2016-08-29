package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaGeralPAFVO implements BaseBean {
	
	private static final long serialVersionUID = -7468004122874334159L;
	
	private Integer lctNumero;
	private Integer frnNumero;
	private Short nroComplemento;
	private Integer afpNumero;
	private Integer matricula;
	private Short vinCodigo;
	private RapServidores servidorGestor;
	private ScoFornecedor fornecedor;
	private Date dtEnvioFornecedor;
	
	
	public PesquisaGeralPAFVO() {
		super();
	}
	
	public enum Fields {
		PFR_LCT_NUMERO("lctNumero"),
		PFR_FRN_NUMERO("frnNumero"),
		NRO_COMPLEMENTO("nroComplemento"),
		AFP_NUMERO("afpNumero"),
		SER_MATRICULA_GESTOR("matricula"),
		SER_VIN_CODIGO_GESTOR("vinCodigo"),
		SERVIDOR_GESTOR("servidorGestor"),
		FORNECEDOR("fornecedor"),
		DT_ENVIO_FORNECEDOR("dtEnvioFornecedor");
		
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

	public Integer getAfpNumero() {
		return afpNumero;
	}

	public void setAfpNumero(Integer afpNumero) {
		this.afpNumero = afpNumero;
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

	public Date getDtEnvioFornecedor() {
		return dtEnvioFornecedor;
	}

	public void setDtEnvioFornecedor(Date dtEnvioFornecedor) {
		this.dtEnvioFornecedor = dtEnvioFornecedor;
	}

}
