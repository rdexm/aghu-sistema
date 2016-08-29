package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioOrigemFichaTecnicaEspecial;
import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@SequenceGenerator(name="mbcFexSq1", sequenceName="AGH.MBC_FEX_SQ1", allocationSize = 1)
@Table(name = "MBC_FICHA_EXAMES", schema = "AGH")
public class MbcFichaExame extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2093905069430761029L;
	private Integer seq;
	private Integer version;
	private AelItemSolicitacaoExames aelItemSolicitacaoExames;
	private MbcFichaAnestesias mbcFichaAnestesias;
	private RapServidores rapServidores;
	private DominioSituacaoExame situacaoExame;
	private DominioOrigemFichaTecnicaEspecial origem;
	private Date criadoEm;

	public MbcFichaExame() {
	}

	public MbcFichaExame(Integer seq, AelItemSolicitacaoExames aelItemSolicitacaoExames, MbcFichaAnestesias mbcFichaAnestesias,
			RapServidores rapServidores, DominioSituacaoExame situacaoExame, DominioOrigemFichaTecnicaEspecial origem, Date criadoEm) {
		this.seq = seq;
		this.aelItemSolicitacaoExames = aelItemSolicitacaoExames;
		this.mbcFichaAnestesias = mbcFichaAnestesias;
		this.rapServidores = rapServidores;
		this.situacaoExame = situacaoExame;
		this.origem = origem;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcFexSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ", nullable = false),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP", nullable = false) })
	public AelItemSolicitacaoExames getAelItemSolicitacaoExames() {
		return this.aelItemSolicitacaoExames;
	}

	public void setAelItemSolicitacaoExames(AelItemSolicitacaoExames aelItemSolicitacaoExames) {
		this.aelItemSolicitacaoExames = aelItemSolicitacaoExames;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FIC_SEQ", nullable = false)
	public MbcFichaAnestesias getMbcFichaAnestesias() {
		return this.mbcFichaAnestesias;
	}

	public void setMbcFichaAnestesias(MbcFichaAnestesias mbcFichaAnestesias) {
		this.mbcFichaAnestesias = mbcFichaAnestesias;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "SITUACAO_EXAME", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoExame getSituacaoExame() {
		return this.situacaoExame;
	}

	public void setSituacaoExame(DominioSituacaoExame situacaoExame) {
		this.situacaoExame = situacaoExame;
	}

	@Column(name = "ORIGEM", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioOrigemFichaTecnicaEspecial getOrigem() {
		return this.origem;
	}

	public void setOrigem(DominioOrigemFichaTecnicaEspecial origem) {
		this.origem = origem;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		AEL_ITEM_SOLICITACAO_EXAMES("aelItemSolicitacaoExames"),
		MBC_FICHA_ANESTESIAS("mbcFichaAnestesias"),
		RAP_SERVIDORES("rapServidores"),
		SITUACAO_EXAME("situacaoExame"),
		ORIGEM("origem"),
		CRIADO_EM("criadoEm");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

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
		if (!(obj instanceof MbcFichaExame)) {
			return false;
		}
		MbcFichaExame other = (MbcFichaExame) obj;
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
