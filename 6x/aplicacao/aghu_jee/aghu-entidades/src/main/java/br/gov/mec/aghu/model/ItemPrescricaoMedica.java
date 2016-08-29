package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.interfaces.IItemPrescricaoMedica;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * 
 */
@MappedSuperclass
public abstract class ItemPrescricaoMedica<ID> extends BaseEntityId<ID>  implements java.io.Serializable, IItemPrescricaoMedica {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3288142531229645243L;

	protected DominioIndPendenteItemPrescricao indPendente;

	protected RapServidores servidorValidacao;

	protected RapServidores servidorValidaMovimentacao;

	protected Date dthrValida;

	protected Date dthrValidaMovimentacao;

	protected Date criadoEm;

	protected Date alteradoEm;
	
	protected Date dthrFim;
	
	protected RapServidores servidorMovimentado;
	
	protected MpmPrescricaoMedica prescricaoMedica;
	
	@Column(name = "IND_PENDENTE", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndPendenteItemPrescricao getIndPendente() {
		return indPendente;
	}

	public void setIndPendente(DominioIndPendenteItemPrescricao indPendente) {
		this.indPendente = indPendente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_VALIDA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_VALIDA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorValidacao() {
		return servidorValidacao;
	}

	public void setServidorValidacao(RapServidores servidorValidacao) {
		this.servidorValidacao = servidorValidacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_MVTO_VALIDA", referencedColumnName = "MATRICULA", insertable=true, updatable=true),
			@JoinColumn(name = "SER_VIN_CODIGO_MVTO_VALIDA", referencedColumnName = "VIN_CODIGO", insertable=true, updatable=true) })
	public RapServidores getServidorValidaMovimentacao() {
		return servidorValidaMovimentacao;
	}

	public void setServidorValidaMovimentacao(
			RapServidores servidorValidaMovimentacao) {
		this.servidorValidaMovimentacao = servidorValidaMovimentacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_VALIDA", length = 7)
	public Date getDthrValida() {
		return dthrValida;
	}

	public void setDthrValida(Date dthrValida) {
		this.dthrValida = dthrValida;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_VALIDA_MVTO", length = 7)
	public Date getDthrValidaMovimentacao() {
		return dthrValidaMovimentacao;
	}

	public void setDthrValidaMovimentacao(Date dthrValidaMovimentacao) {
		this.dthrValidaMovimentacao = dthrValidaMovimentacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 7)
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorMovimentado() {
		return servidorMovimentado;
	}

	public void setServidorMovimentado(RapServidores servidorMovimentado) {
		this.servidorMovimentado = servidorMovimentado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PME_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "PME_SEQ", referencedColumnName = "SEQ") })
	public MpmPrescricaoMedica getPrescricaoMedica() {
		return this.prescricaoMedica; 
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica mpmPrescricaoMedica) {
		this.prescricaoMedica = mpmPrescricaoMedica;
	}	

	/**
	 * Regras para montar a descrição dos itens de Prescricao Medica
	 * 
	 * rcorvalao 28/09/2010
	 * 
	 * @return
	 */
	@Override
	@Transient
	public abstract String getDescricaoFormatada();

	/**
	 * Retorna o itemPrescricao do auto-relacionamento.
	 * 
	 * @return
	 */
	@Transient
	public abstract ItemPrescricaoMedica getAnterior();
	
	@Transient
	public abstract boolean possuiFilhos();

	/**
	 * Evitar de usar a referência para a entidade de Atendimentos.
	 * Esta referência ainda foi mantida por questões do legado do 
	 * HC de Porto Alegre (HCPA). Ao invés de referenciar Atendimentos
	 * preterir referenciar a entidade de Prescrição Médica. 
	 * @return null
	 */
	@Deprecated
	@Transient
	public AghAtendimentos getAtendimento() {
		return null;
	}

	@Deprecated
	@Transient
	public void setAtendimento(AghAtendimentos atendimento) {
		atendimento = null;
	}
	public enum Fields {

		IND_PENDENTE("indPendente"),
		SERVIDOR_VALIDACAO("servidorValidacao"),
		SERVIDOR_VALIDA_MOVIMENTACAO("servidorValidaMovimentacao"),
		DTHR_VALIDA("dthrValida"),
		DTHR_VALIDA_MOVIMENTACAO("dthrValidaMovimentacao"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		DTHR_FIM("dthrFim"),
		SERVIDOR_MOVIMENTADO("servidorMovimentado"),
		PRESCRICAO_MEDICA("prescricaoMedica");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
