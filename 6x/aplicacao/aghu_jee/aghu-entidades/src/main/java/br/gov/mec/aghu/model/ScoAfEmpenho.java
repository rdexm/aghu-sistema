package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sco_af_empenhos database table.
 * 
 */
@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
@Table(name="SCO_AF_EMPENHOS")
public class ScoAfEmpenho extends BaseEntitySeq<Integer> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -2400424314297513886L;
private Integer seq;
	private Timestamp alteradoEm;
	private Integer anoEmpenho;
	private Integer anoListaItensAfSiafi;
	private Long codProgTrabRed;
	private Timestamp criadoEm;
	private Timestamp dtEnvioSiafi;
	private DominioSimNao envioManual;
	private Integer esferaOrcamentaria;
	private Integer especie;
	private Long frfCodigo;
	private DominioSimNao indConfirmadoSiafi;
	private DominioSimNao indEnviado;
	private Integer nroListaItensAfSiafi;
	private String numero;
	private String numeroDocumento;
	private String numeroDocumentoSiafi;
	private Integer serMatricula;
	private Integer serVinCodigo;
	private Double valor;
	private Integer version;
	private ScoAutorizacaoForn scoAutorizacoesForn;
	
	public enum Especie{
		TRES(3);
		
		private Integer valor;
		
		private Especie(Integer cod){
			valor = cod;
		}
		
		public Integer getCodigo(){
			return this.valor;
		}
	}
	
	public enum Fields{
		AUTORIZACAO_FORN("scoAutorizacoesForn"),
		AUTORIZACAO_FORN_NUMERO("scoAutorizacoesForn.numero"),
		ESPECIE("especie"),
		VALOR("valor"),
		NUM_DOCUMENTO_SIAFI("numeroDocumentoSiafi"),
		IND_ENVIO_MANUAL("envioManual"),
		IND_CONFIRMADO_SIAFI("indConfirmadoSiafi"),
		IND_ENVIADO("indEnviado"),
		ANO_EMPENHO("anoEmpenho"),
		FRF_CODIGO("frfCodigo"),
		SEQ("seq"),
		NRO_LISTA_ITENS_AF_SIAFI("nroListaItensAfSiafi"),
		ANO_LISTA_ITENS_AF_SIAFI("anoListaItensAfSiafi"),
		DT_ENVIO_SIAFI("dtEnvioSiafi"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		NUMERO("numero");
		
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

    public ScoAfEmpenho() {
    }


	@Id
	@Column(name = "SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCO_AF_EMPENHOS_SEQ_GENERATOR")
	@SequenceGenerator(name="SCO_AF_EMPENHOS_SEQ_GENERATOR", sequenceName="AGH.SCO_AFP_SQ1", allocationSize = 1)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@Column(name="ALTERADO_EM")
	public Timestamp getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Timestamp alteradoEm) {
		this.alteradoEm = alteradoEm;
	}


	@Column(name="ANO_EMPENHO")
	public Integer getAnoEmpenho() {
		return this.anoEmpenho;
	}

	public void setAnoEmpenho(Integer anoEmpenho) {
		this.anoEmpenho = anoEmpenho;
	}


	@Column(name="ANO_LISTA_ITENS_AF_SIAFI")
	public Integer getAnoListaItensAfSiafi() {
		return this.anoListaItensAfSiafi;
	}

	public void setAnoListaItensAfSiafi(Integer anoListaItensAfSiafi) {
		this.anoListaItensAfSiafi = anoListaItensAfSiafi;
	}


	@Column(name="COD_PROG_TRAB_RED")
	public Long getCodProgTrabRed() {
		return this.codProgTrabRed;
	}

	public void setCodProgTrabRed(Long codProgTrabRed) {
		this.codProgTrabRed = codProgTrabRed;
	}


	@Column(name="CRIADO_EM")
	public Timestamp getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Timestamp criadoEm) {
		this.criadoEm = criadoEm;
	}


	@Column(name="DT_ENVIO_SIAFI")
	public Timestamp getDtEnvioSiafi() {
		return this.dtEnvioSiafi;
	}

	public void setDtEnvioSiafi(Timestamp dtEnvioSiafi) {
		this.dtEnvioSiafi = dtEnvioSiafi;
	}


	@Column(name="ENVIO_MANUAL")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getEnvioManual() {
		return this.envioManual;
	}

	public void setEnvioManual(DominioSimNao envioManual) {
		this.envioManual = envioManual;
	}


	@Column(name="ESFERA_ORCAMENTARIA")
	public Integer getEsferaOrcamentaria() {
		return this.esferaOrcamentaria;
	}

	public void setEsferaOrcamentaria(Integer esferaOrcamentaria) {
		this.esferaOrcamentaria = esferaOrcamentaria;
	}


	public Integer getEspecie() {
		return this.especie;
	}

	public void setEspecie(Integer especie) {
		this.especie = especie;
	}


	@Column(name="FRF_CODIGO")
	public Long getFrfCodigo() {
		return this.frfCodigo;
	}

	public void setFrfCodigo(Long frfCodigo) {
		this.frfCodigo = frfCodigo;
	}


	@Column(name="IND_CONFIRMADO_SIAFI")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndConfirmadoSiafi() {
		return this.indConfirmadoSiafi;
	}

	public void setIndConfirmadoSiafi(DominioSimNao indConfirmadoSiafi) {
		this.indConfirmadoSiafi = indConfirmadoSiafi;
	}


	@Column(name="IND_ENVIADO")
	@Enumerated(EnumType.STRING)
	public DominioSimNao getIndEnviado() {
		return this.indEnviado;
	}

	public void setIndEnviado(DominioSimNao indEnviado) {
		this.indEnviado = indEnviado;
	}


	@Column(name="NRO_LISTA_ITENS_AF_SIAFI")
	public Integer getNroListaItensAfSiafi() {
		return this.nroListaItensAfSiafi;
	}

	public void setNroListaItensAfSiafi(Integer nroListaItensAfSiafi) {
		this.nroListaItensAfSiafi = nroListaItensAfSiafi;
	}


	public String getNumero() {
		return this.numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}


	@Column(name="NUMERO_DOCUMENTO")
	public String getNumeroDocumento() {
		return this.numeroDocumento;
	}

	public void setNumeroDocumento(String numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}


	@Column(name="NUMERO_DOCUMENTO_SIAFI")
	public String getNumeroDocumentoSiafi() {
		return this.numeroDocumentoSiafi;
	}

	public void setNumeroDocumentoSiafi(String numeroDocumentoSiafi) {
		this.numeroDocumentoSiafi = numeroDocumentoSiafi;
	}


	@Column(name="SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}


	@Column(name="SER_VIN_CODIGO")
	public Integer getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name="VALOR")
	public Double getValor() {
		return this.valor;
	}
 
	public void setValor(Double valor) {
		this.valor = valor;
	}


	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to ScoAutorizacoesForn
    @ManyToOne
	@JoinColumn(name="AFN_NUMERO")
	public ScoAutorizacaoForn getScoAutorizacoesForn() {
		return this.scoAutorizacoesForn;
	}

	public void setScoAutorizacoesForn(ScoAutorizacaoForn scoAutorizacoesForn) {
		this.scoAutorizacoesForn = scoAutorizacoesForn;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof ScoAfEmpenho)) {
			return false;
		}
		ScoAfEmpenho other = (ScoAfEmpenho) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}