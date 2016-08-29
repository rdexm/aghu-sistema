package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class MbcAlcadaAvalOpmsId implements EntityCompositeId {

	private static final long serialVersionUID = 2282675449965471515L;
	private MbcGrupoAlcadaAvalOpms mbcGrupoAlcadaAvalOpms;
	private Short gaoSeq;
	
	public MbcAlcadaAvalOpmsId() {
	}
	
	public MbcAlcadaAvalOpmsId(MbcGrupoAlcadaAvalOpms mbcGrupoAlcadaAvalOpms,Short gaoSeq) {
		this.mbcGrupoAlcadaAvalOpms = mbcGrupoAlcadaAvalOpms;
		this.gaoSeq = gaoSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GAO_SEQ")
	public MbcGrupoAlcadaAvalOpms getMbcGrupoAlcadaAvalOpms() {
		return mbcGrupoAlcadaAvalOpms;
	}

	public void setMbcGrupoAlcadaAvalOpms(
			MbcGrupoAlcadaAvalOpms mbcGrupoAlcadaAvalOpms) {
		this.mbcGrupoAlcadaAvalOpms = mbcGrupoAlcadaAvalOpms;
	}

	@Column(name = "SEQ", nullable = false, precision = 5, scale = 0)
	public Short getGaoSeq() {
		return gaoSeq;
	}

	public void setGaoSeq(Short gaoSeq) {
		this.gaoSeq = gaoSeq;
	}
	
	
}
