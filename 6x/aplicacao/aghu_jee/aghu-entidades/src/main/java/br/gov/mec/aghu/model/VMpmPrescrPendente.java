package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "V_MPM_PRESCR_PENDENTE", schema = "AGH")
@Immutable
public class VMpmPrescrPendente extends BaseEntityId<VMpmPrescrPendenteId> implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3395120010758014283L;

	private VMpmPrescrPendenteId id;
	
	private AfaViaAdministracao afaViaAdministracao;
	private AfaGrupoUsoMedicamento afaGrupoUsoMedicamento;
	
	public VMpmPrescrPendente() {
	}

	public VMpmPrescrPendente(VMpmPrescrPendenteId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pmdAtdSeq", column = @Column(name = "PMD_ATD_SEQ", nullable = false, precision = 9, scale = 0)),
			@AttributeOverride(name = "pmdSeq", column = @Column(name = "PMD_SEQ", nullable = false, precision = 14, scale = 0)),
			@AttributeOverride(name = "medMatCodigo", column = @Column(name = "MED_MAT_CODIGO", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "indAntimicrobiano", column = @Column(name = "IND_ANTIMICROBIANO", nullable = false, length = 1)),
			@AttributeOverride(name = "codigo", column = @Column(name = "CODIGO")),
			@AttributeOverride(name = "dose", column = @Column(name = "DOSE", nullable = false, precision = 14, scale = 4)),
			@AttributeOverride(name = "frequencia", column = @Column(name = "FREQUENCIA", precision = 3, scale = 0)),
			@AttributeOverride(name = "vadSigla", column = @Column(name = "VAD_SIGLA", nullable = false, length = 2)),
			@AttributeOverride(name = "duracaoTratSolicitado", column = @Column(name = "DURACAO_TRAT_SOLICITADO", precision = 3, scale = 0)),
			@AttributeOverride(name = "indUsoMdtoAntimicrob", column = @Column(name = "IND_USO_MDTO_ANTIMICROB", length = 1)),
			@AttributeOverride(name = "indFaturavel", column = @Column(name = "IND_FATURAVEL")),
			@AttributeOverride(name = "gupSeq", column = @Column(name = "GUP_SEQ", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "descricao", column = @Column(name = "DESCRICAO", nullable = false, length = 60)),
			@AttributeOverride(name = "jumSeq", column = @Column(name = "JUM_SEQ", precision = 8, scale = 0)),
			@AttributeOverride(name = "cspCnvCodigo", column = @Column(name = "CSP_CNV_CODIGO")),
			@AttributeOverride(name = "cspSeq", column = @Column(name = "CSP_SEQ")),
			@AttributeOverride(name = "descricaoEdit", column = @Column(name = "DESCRICAO_EDIT", length = 117)),
			@AttributeOverride(name = "doseEdit", column = @Column(name = "DOSE_EDIT", length = 56)),
			@AttributeOverride(name = "freqEdit", column = @Column(name = "FREQ_EDIT", length = 1000)),
			@AttributeOverride(name = "indQuimioterapico", column = @Column(name = "IND_QUIMIOTERAPICO", nullable = false, length = 1)) })
	public VMpmPrescrPendenteId getId() {
		return this.id;
	}

	public void setId(VMpmPrescrPendenteId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAD_SIGLA", referencedColumnName = "SIGLA", nullable = false, insertable = false, updatable = false)
	public AfaViaAdministracao getAfaViaAdministracao() {
		return afaViaAdministracao;
	}

	public void setAfaViaAdministracao(AfaViaAdministracao afaViaAdministracao) {
		this.afaViaAdministracao = afaViaAdministracao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GUP_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false)
	public AfaGrupoUsoMedicamento getAfaGrupoUsoMedicamento() {
		return afaGrupoUsoMedicamento;
	}

	public void setAfaGrupoUsoMedicamento(
			AfaGrupoUsoMedicamento afaGrupoUsoMedicamento) {
		this.afaGrupoUsoMedicamento = afaGrupoUsoMedicamento;
	}
	
	public enum Fields {
		
		PMD_ATD_SEQ("id.pmdAtdSeq"),
		PMD_SEQ("id.pmdSeq"),
		MED_MAT_CODIGO("id.medMatCodigo"),
		SEQP("id.seqp"),
		IND_ANTIMICROBIANO("id.indAntimicrobiano"),
		CODIGO("id.codigo"),
		DOSE("id.dose"),
		FREQUENCIA("id.frequencia"),
		VAD_SIGLA("id.vadSigla"),
		DURACAO_TRAT_SOLICITADO("id.duracaoTratSolicitado"),
		IND_USO_MDTO_ANTIMICROB("id.indUsoMdtoAntimicrob"),
		IND_FATURAVEL("id.indFaturavel"),
		GUP_SEQ("id.gupSeq"),
		DESCRICAO("id.descricao"),
		JUM_SEQ("id.jumSeq"),
		CSP_CNV_CODIGO("id.cspCnvCodigo"),
		CSP_SEQ("id.cspSeq"),
		DESCRICAO_EDIT("id.descricaoEdit"),
		DOSE_EDIT("id.doseEdit"),
		FREQ_EDIT("id.freqEdit"),
		IND_QUIMIOTERAPICO("id.indQuimioterapico"),
		VIA_ADMINISTRACAO("afaViaAdministracao"),
		GRUPO_USO_MDTO("afaGrupoUsoMedicamento");
		
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		VMpmPrescrPendente other = (VMpmPrescrPendente) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
