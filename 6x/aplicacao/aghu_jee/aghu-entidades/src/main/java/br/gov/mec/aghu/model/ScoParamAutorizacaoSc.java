package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="scoPasSq1", sequenceName="SCO_PAS_SQ1", allocationSize = 1)
@Table(name = "SCO_PARAM_AUTORIZACAO_SC", schema = "AGH")
public class ScoParamAutorizacaoSc extends BaseEntitySeq<Integer> implements java.io.Serializable{

	private static final long serialVersionUID = -3327482467403378880L;
	
	private Integer seq;
	private FccCentroCustos centroCustoSolicitante;
	private FccCentroCustos centroCustoAplicacao;
	private DominioSituacao indSituacao;
	private Boolean indHierarquiaCCusto;
	private ScoPontoParadaSolicitacao pontoParada;
	private ScoPontoParadaSolicitacao pontoParadaProxima;
	private RapServidores servidor;
	private RapServidores servidorAutoriza;
	private RapServidores servidorCompra;	
	private RapServidores servidorCriacao;	
	private RapServidores servidorAlteracao;
	private Date criadoEm;
	private Integer version;	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoPasSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitante;
	}

	public void setCentroCustoSolicitante(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitante = centroCustoSolicitante;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_APLICACAO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Column(name = "IND_HIERARQUIA_CCUSTO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndHierarquiaCCusto() {
		return indHierarquiaCCusto;
	}

	public void setIndHierarquiaCCusto(Boolean indHierarquiaCCusto) {
		this.indHierarquiaCCusto = indHierarquiaCCusto;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PPS_CODIGO", referencedColumnName = "CODIGO")
	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	@ManyToOne(fetch = FetchType.LAZY)	
	@JoinColumn(name = "PPS_CODIGO_LOC_PROXIMA", referencedColumnName = "CODIGO")
	public ScoPontoParadaSolicitacao getPontoParadaProxima() {
		return pontoParadaProxima;
	}

	public void setPontoParadaProxima(ScoPontoParadaSolicitacao pontoParadaProxima) {
		this.pontoParadaProxima = pontoParadaProxima;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_AUTORIZA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_AUTORIZA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAutoriza() {
		return servidorAutoriza;
	}

	public void setServidorAutoriza(RapServidores servidorAutoriza) {
		this.servidorAutoriza = servidorAutoriza;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_COMPRA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_COMPRA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCompra() {
		return servidorCompra;
	}

	public void setServidorCompra(RapServidores servidorCompra) {
		this.servidorCompra = servidorCompra;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_CRIACAO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CRIACAO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCriacao() {
		return servidorCriacao;
	}

	public void setServidorCriacao(RapServidores servidorCriacao) {
		this.servidorCriacao = servidorCriacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERACAO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERACAO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}

	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao = servidorAlteracao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		SEQ("seq"),
		CENTRO_CUSTO_SOLICITANTE("centroCustoSolicitante"),
		CCT_CODIGO("centroCustoSolicitante.codigo"),
		CENTRO_CUSTO_APLICACAO("centroCustoAplicacao"),
		CCT_CODIGO_APLICACAO("centroCustoAplicacao.codigo"),
		IND_SITUACAO("indSituacao"),
		IND_HIERARQUIA_CCUSTO("indHierarquiaCCusto"),
		PONTO_PARADA("pontoParada"),
	    PPS_CODIGO("pontoParada.codigo"),
		PONTO_PARADA_PROXIMA("pontoParadaProxima"),
	    PPS_CODIGO_LOC_PROXIMA("pontoParadaProxima.codigo"),
		SERVIDOR("servidor"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		SERVIDOR_AUTORIZA("servidorAutoriza"),
		SER_MATRICULA_AUTORIZA("servidorAutoriza.id.matricula"),
		SER_VIN_CODIGO_AUTORIZA("servidorAutoriza.id.vinCodigo"),
		SERVIDOR_COMPRA("servidorCompra"),
		SER_MATRICULA_COMPRA("servidorCompra.id.matricula"),
		SER_VIN_CODIGO_COMPRA("servidorCompra.id.vinCodigo"),
		SERVIDOR_CRIACAO("servidorCriacao"),
		SER_MATRICULA_CRIACAO("servidorCriacao.id.matricula"),
		SER_VIN_CODIGO_CRIACAO("servidorCriacao.id.vinCodigo"),
		SERVIDOR_ALTERACAO("servidorAlteracao"),
		SER_MATRICULA_ALTERACAO("servidorAlteracao.id.matricula"),
		SER_VIN_CODIGO_ALTERACAO("servidorAlteracao.id.vinCodigo"),
		CRIADO_EM("criadoEm"),
		VERSION("version");
		private String fields;

        private Fields(String fields) {
            this.fields = fields;
        }

        @Override
        public String toString() {
            return this.fields;
        }
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}if (obj == null){
			return false;
		}if (getClass() != obj.getClass()){
			return false;
		}
		ScoParamAutorizacaoSc other = (ScoParamAutorizacaoSc) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
}