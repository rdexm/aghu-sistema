package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_listas_siafi database table.
 * 
 */
@Entity
@Table(name="SCO_LISTAS_SIAFI")
public class ScoListasSiafi extends BaseEntityId<ScoListasSiafiId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -8919603647032966763L;
private ScoListasSiafiId id;
	private Timestamp dataUltGeracao;
	private Integer especie;
	private Boolean indAfAssinada;
	private String indAtiva;
	private String indSituacao;
	private String indSituacaoAnt;
	private String numeroListaSiafi;
	private Integer qtde;
	private Integer qtdeEnviada;
	private Integer seqEnvio;
	private Integer seqSiafi;
	private Integer sequenciaAssinada;
	private Double valor;
	private Double valorEnviado;
	private Integer version;
	
	public enum Fields{
		ITEM_AUTORIZACAO("id.scoItensAutorizacaoForn"),
		NUMERO_LISTA("numeroListaSiafi"),
		IND_AF_ASSINADA("indAfAssinada"),
		DATA_ULT_GERACAO("dataUltGeracao"),
		ESPECIE("especie"),
		IND_ATIVA("indAtiva"),
		IND_SITUACAO("indSituacao"),
		IND_SITUACAO_ANT("indSituacaoAnt"),
		NUMERO_LISTA_SIAFI("numeroListaSiafi"),
		QTDE("qtde"),
		QTDE_ENVIADA("qtdeEnviada"),
		SEQ_ENVIO("seqEnvio"),
		SEQ_SIAFI("seqSiafi"),
		VALOR("valor"),
		VALOR_ENVIADO("valorEnviado");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

    public ScoListasSiafi() {
    }


	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "scoItensAutorizacaoForn.id.scoAutorizacaoForn", column = @Column(name = "IAF_AFN_NUMERO", nullable = false)),
		@AttributeOverride(name = "scoItensAutorizacaoForn.id.numero", column = @Column(name = "IAF_NUMERO", nullable = false)),
		@AttributeOverride(name = "anoEmpenho", column = @Column(name = "ANO_EMPENHO", nullable = false)),
		@AttributeOverride(name = "numeroLista", column = @Column(name = "NUMERO_LISTA", nullable = false))
	})
	public ScoListasSiafiId getId() {
		return this.id;
	}

	public void setId(ScoListasSiafiId id) {
		this.id = id;
	}
	

	@Column(name="DATA_ULT_GERACAO")
	public Timestamp getDataUltGeracao() {
		return this.dataUltGeracao;
	}

	public void setDataUltGeracao(Timestamp dataUltGeracao) {
		this.dataUltGeracao = dataUltGeracao;
	}


	public Integer getEspecie() {
		return this.especie;
	}

	public void setEspecie(Integer especie) {
		this.especie = especie;
	}


    @Column(name = "IND_AF_ASSINADA")
    @org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAfAssinada() {
		return this.indAfAssinada;
	}

	public void setIndAfAssinada(Boolean indAfAssinada) {
		this.indAfAssinada = indAfAssinada;
	}


	@Column(name="IND_ATIVA")
	public String getIndAtiva() {
		return this.indAtiva;
	}

	public void setIndAtiva(String indAtiva) {
		this.indAtiva = indAtiva;
	}


	@Column(name="IND_SITUACAO")
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}


	@Column(name="IND_SITUACAO_ANT")
	public String getIndSituacaoAnt() {
		return this.indSituacaoAnt;
	}

	public void setIndSituacaoAnt(String indSituacaoAnt) {
		this.indSituacaoAnt = indSituacaoAnt;
	}


	@Column(name="NUMERO_LISTA_SIAFI")
	public String getNumeroListaSiafi() {
		return this.numeroListaSiafi;
	}

	public void setNumeroListaSiafi(String numeroListaSiafi) {
		this.numeroListaSiafi = numeroListaSiafi;
	}


	public Integer getQtde() {
		return this.qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}


	@Column(name="QTDE_ENVIADA")
	public Integer getQtdeEnviada() {
		return this.qtdeEnviada;
	}

	public void setQtdeEnviada(Integer qtdeEnviada) {
		this.qtdeEnviada = qtdeEnviada;
	}


	@Column(name="SEQ_ENVIO")
	public Integer getSeqEnvio() {
		return this.seqEnvio;
	}

	public void setSeqEnvio(Integer seqEnvio) {
		this.seqEnvio = seqEnvio;
	}


	@Column(name="SEQ_SIAFI")
	public Integer getSeqSiafi() {
		return this.seqSiafi;
	}

	public void setSeqSiafi(Integer seqSiafi) {
		this.seqSiafi = seqSiafi;
	}


	@Column(name="SEQUENCIA_ASSINADA")
	public Integer getSequenciaAssinada() {
		return this.sequenciaAssinada;
	}

	public void setSequenciaAssinada(Integer sequenciaAssinada) {
		this.sequenciaAssinada = sequenciaAssinada;
	}


	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}


	@Column(name="VALOR_ENVIADO")
	public Double getValorEnviado() {
		return this.valorEnviado;
	}

	public void setValorEnviado(Double valorEnviado) {
		this.valorEnviado = valorEnviado;
	}


	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof ScoListasSiafi)) {
			return false;
		}
		ScoListasSiafi other = (ScoListasSiafi) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}