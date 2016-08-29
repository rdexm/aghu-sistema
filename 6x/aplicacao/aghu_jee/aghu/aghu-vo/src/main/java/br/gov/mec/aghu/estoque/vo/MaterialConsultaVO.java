package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;

/**
 * @author pedro.santiago
 *
 */
public class MaterialConsultaVO {

	private Short seqTipoMovimento;
	private Integer numeroDocumento;
	private Date dtInicio;
	private Date dtFim;	
	private Integer numeroFornecedor;
	private Long numeroDocumentoFiscal;
	private Integer codigoMaterial;
	private Integer numeroAF;
	private Short numeroComplemento;
	private DominioSimNao encerrado;
	private DominioSimNao efetivado;
	private DominioSimNao estornado;
	private DominioSimNao adiantamento;
	
	public enum Fields {
		SEQ_TIPO_MOVIMENTO("seqTipoMovimento"), 
		NRO_DOCUMENTO("numeroDocumento"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim"),
		NUM_FORNECEDOR("numeroFornecedor"),
		NUM_DOC_FISCAL("numeroDocumentoFiscal"),
		COD_MATERIAL("codigoMaterial"),
		NUMERO_AF("numeroAF"),
		NUM_COMPLEMENTO("numeroComplemento"),
		ENCERRADO("encerrado"),
		EFETIVADO("efetivado"),
		ESTORNADO("estornado"),
		ADIANTAMENTO("adiantamento");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getSeqTipoMovimento() {
		return seqTipoMovimento;
	}

	public void setSeqTipoMovimento(Short seqTipoMovimento) {
		this.seqTipoMovimento = seqTipoMovimento;
	}

	public Integer getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(Integer numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Long getNumeroDocumentoFiscal() {
		return numeroDocumentoFiscal;
	}

	public void setNumeroDocumentoFiscal(Long numeroDocumentoFiscal) {
		this.numeroDocumentoFiscal = numeroDocumentoFiscal;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getNumeroAF() {
		return numeroAF;
	}

	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public DominioSimNao getEncerrado() {
		return encerrado;
	}

	public void setEncerrado(DominioSimNao encerrado) {
		this.encerrado = encerrado;
	}

	public DominioSimNao getEfetivado() {
		return efetivado;
	}

	public void setEfetivado(DominioSimNao efetivado) {
		this.efetivado = efetivado;
	}

	public DominioSimNao getEstornado() {
		return estornado;
	}

	public void setEstornado(DominioSimNao estornado) {
		this.estornado = estornado;
	}

	public DominioSimNao getAdiantamento() {
		return adiantamento;
	}

	public void setAdiantamento(DominioSimNao adiantamento) {
		this.adiantamento = adiantamento;
	}
}
