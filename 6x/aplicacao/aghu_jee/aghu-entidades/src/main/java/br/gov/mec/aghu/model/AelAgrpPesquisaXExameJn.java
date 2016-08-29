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


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AEL_AGRP_PESQUISA_X_EXAMES_JN", schema = "AGH")
@SequenceGenerator(name="aelDdvJnSeq", sequenceName="AGH.AEL_DDV_JN_SEQ", allocationSize = 1)
@Immutable
public class AelAgrpPesquisaXExameJn extends BaseJournal {

	private static final long serialVersionUID = -6791489654698983283L;
	private Integer seq;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private AelAgrpPesquisas agrpPesquisa;
	private AelUnfExecutaExames unfExecutaExame;
	private RapServidores servidor;

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelDdvJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	

	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "EMA_EXA_SIGLA", nullable = false),
			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "EMA_MAN_SEQ", nullable = false),
			@JoinColumn(name = "UFE_UNF_SEQ", referencedColumnName = "UNF_SEQ", nullable = false)})
	public AelUnfExecutaExames getUnfExecutaExame() {
		return unfExecutaExame;
	}
	
	public void setUnfExecutaExame(AelUnfExecutaExames unfExecutaExame) {
		this.unfExecutaExame = unfExecutaExame;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APS_SEQ")
	public AelAgrpPesquisas getAgrpPesquisa() {
		return agrpPesquisa;
	}
	
	public void setAgrpPesquisa(AelAgrpPesquisas agrpPesquisa) {
		this.agrpPesquisa = agrpPesquisa;
	}
}
