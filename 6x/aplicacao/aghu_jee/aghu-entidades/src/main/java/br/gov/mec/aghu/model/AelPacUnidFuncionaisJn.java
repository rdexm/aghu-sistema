package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioCondicaoPaciente;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AEL_PAC_UNID_FUNCIONAIS_JN", schema = "AGH")
@SequenceGenerator(name = "aelPufJnSeq", sequenceName = "AGH.AEL_PUF_JN_SEQ", allocationSize = 1)

@Immutable
public class AelPacUnidFuncionaisJn extends BaseJournal implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4405740564053613406L;
	private Integer piuPacCodigo;
	private Short piuUnfSeq;
	private Integer seqp;
	private AelUnfExecutaExames unfExecutaExames;
	private Date criadoEm;
	private RapServidores servidor;
	private Date dtExecucao;
	private String identificadorComplementar;
	private DominioCondicaoPaciente condicaoPac;
	private Integer nroFilme;
	private String observacao;
	private RapServidores servidorAlterado;
	private Date alteradoEm;
	private AelEquipamentos equipamento;
	private AelItemSolicitacaoExames itemSolicitacaoExames;
	
	
	

	public AelPacUnidFuncionaisJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelPufJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}


	@Column(name = "PIU_PAC_CODIGO", nullable = false, precision = 8, scale = 0)
	public Integer getPiuPacCodigo() {
		return this.piuPacCodigo;
	}

	public void setPiuPacCodigo(Integer piuPacCodigo) {
		this.piuPacCodigo = piuPacCodigo;
	}

	@Column(name = "PIU_UNF_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getPiuUnfSeq() {
		return this.piuUnfSeq;
	}

	public void setPiuUnfSeq(Short piuUnfSeq) {
		this.piuUnfSeq = piuUnfSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 8, scale = 0)
	public Integer getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "UFE_EMA_EXA_SIGLA", referencedColumnName = "EMA_EXA_SIGLA", nullable = false),
			@JoinColumn(name = "UFE_EMA_MAN_SEQ", referencedColumnName = "EMA_MAN_SEQ", nullable = false),
			@JoinColumn(name = "UFE_UNF_SEQ", referencedColumnName = "UNF_SEQ", nullable = false)})
	public AelUnfExecutaExames getUnfExecutaExames() {
		return unfExecutaExames;
	}

	public void setUnfExecutaExames(AelUnfExecutaExames unfExecutaExames) {
		this.unfExecutaExames = unfExecutaExames;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_EXECUCAO", length = 7)
	public Date getDtExecucao() {
		return this.dtExecucao;
	}

	public void setDtExecucao(Date dtExecucao) {
		this.dtExecucao = dtExecucao;
	}

	@Column(name = "IDENTIFICADOR_COMPLEMENTAR", length = 3)
	@Length(max = 3)
	public String getIdentificadorComplementar() {
		return this.identificadorComplementar;
	}

	public void setIdentificadorComplementar(String identificadorComplementar) {
		this.identificadorComplementar = identificadorComplementar;
	}

	@Column(name = "CONDICAO_PAC", length = 2)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioCondicaoPaciente") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioCondicaoPaciente getCondicaoPac() {
		return this.condicaoPac;
	}

	public void setCondicaoPac(DominioCondicaoPaciente condicaoPac) {
		this.condicaoPac = condicaoPac;
	}

	@Column(name = "NRO_FILME", precision = 7, scale = 0)
	public Integer getNroFilme() {
		return this.nroFilme;
	}

	public void setNroFilme(Integer nroFilme) {
		this.nroFilme = nroFilme;
	}

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlterado() {
		return this.servidorAlterado;
	}

	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EQU_SEQ")
	public AelEquipamentos getEquipamento() {
		return equipamento;
	}

	public void setEquipamento(AelEquipamentos equipamento) {
		this.equipamento = equipamento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ", nullable = true),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP", nullable = true)})
	public AelItemSolicitacaoExames getItemSolicitacaoExames() {
		return itemSolicitacaoExames;
	}

	public void setItemSolicitacaoExames(
			AelItemSolicitacaoExames itemSolicitacaoExames) {
		this.itemSolicitacaoExames = itemSolicitacaoExames;
	}

	public enum Fields {

		PIU_PAC_CODIGO("piuPacCodigo"),
		PIU_UNF_SEQ("piuUnfSeq"),
		SEQP("seqp"),
		UFE_EMA_EXA_SIGLA("unfExecutaExames.id.emaExaSigla"),
		UFE_EMA_MAN_SEQ("unfExecutaExames.id.emaManSeq"),
		UFE_UNF_SEQ("unfExecutaExames.id.unfSeq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		ISE_SOE_SEQ("itemSolicitacaoExames.id.soeSeq"),
		ISE_SEQP("itemSolicitacaoExames.id.seqp"),
		DT_EXECUCAO("dtExecucao"),
		IDENTIFICADOR_COMPLEMENTAR("identificadorComplementar"),
		CONDICAO_PAC("condicaoPac"),
		NRO_FILME("nroFilme"),
		OBSERVACAO("observacao"),
		SERVIDOR_ALTERADO("servidorAlterado"),
		ALTERADO_EM("alteradoEm"),
		EQU_SEQ("equipamento.seq");

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
