package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aipGrupoFamiliarPacientesJnSequence", sequenceName="AGH.AIP_GFP_JN_SEQ", allocationSize = 1)
@Table(name = "AIP_GRUPO_FAMILIA_PACIENTES_JN", schema = "AGH")
@Immutable
public class AipGrupoFamiliarPacientesJn extends BaseJournal{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8024189140579170396L;
	private Integer pacCodigo;
	private Integer agfSeq;
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipGrupoFamiliarPacientesJnSequence")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "AGF_SEQ")
	public Integer getAgfSeq() {
		return agfSeq;
	}

	public void setAgfSeq(Integer agfSeq) {
		this.agfSeq = agfSeq;
	}

	@Column(name = "PAC_CODIGO")
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
}
