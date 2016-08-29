package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MAM_RESPOSTA_EVOLUCOES", schema = "AGH")
public class MamRespostaEvolucoes extends BaseEntityId<MamRespostaEvolucoesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2324531288109533507L;
	private MamRespostaEvolucoesId id;
	private Integer vvqQusQutSeq;
	private Short vvqQusSeqp;
	private Short vvqSeqp;
	private Short espSeq;
	private String resposta;
	private AipPesoPacientes aipPesoPaciente;
	private AipAlturaPacientes aipAlturaPaciente;	
	private Integer plpPacCodigo;
	private Short plpSeqp;
	private Integer pipPacCodigo;
	private Short pipSeqp;
	private Integer pdpPacCodigo;
	private Short pdpSeqp;
	private Short unfSeq;
	private MamEvolucoes evolucao;
	private MamQuestao mamQuestao;
	private MamValorValidoQuestao mamValorValidoQuestao;

	public MamRespostaEvolucoes() {
	}

	public MamRespostaEvolucoes(MamRespostaEvolucoesId id) {
		this.id = id;
	}

	public MamRespostaEvolucoes(MamRespostaEvolucoesId id,
			Integer vvqQusQutSeq, Short vvqQusSeqp, Short vvqSeqp,
			Short espSeq, String resposta, AipPesoPacientes aipPesoPaciente,
			AipAlturaPacientes aipAlturaPaciente,
			Integer plpPacCodigo, Short plpSeqp, Integer pipPacCodigo,
			Short pipSeqp, Integer pdpPacCodigo, Short pdpSeqp, Short unfSeq) {
		this.id = id;
		this.vvqQusQutSeq = vvqQusQutSeq;
		this.vvqQusSeqp = vvqQusSeqp;
		this.vvqSeqp = vvqSeqp;
		this.espSeq = espSeq;
		this.resposta = resposta;
		this.aipPesoPaciente = aipPesoPaciente;
		this.aipAlturaPaciente = aipAlturaPaciente;
		this.plpPacCodigo = plpPacCodigo;
		this.plpSeqp = plpSeqp;
		this.pipPacCodigo = pipPacCodigo;
		this.pipSeqp = pipSeqp;
		this.pdpPacCodigo = pdpPacCodigo;
		this.pdpSeqp = pdpSeqp;
		this.unfSeq = unfSeq;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "evoSeq", column = @Column(name = "EVO_SEQ", nullable = false, precision = 14, scale = 0)),
			@AttributeOverride(name = "qusQutSeq", column = @Column(name = "QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "qusSeqp", column = @Column(name = "QUS_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 3, scale = 0)) })
	public MamRespostaEvolucoesId getId() {
		return this.id;
	}

	public void setId(MamRespostaEvolucoesId id) {
		this.id = id;
	}

	@Column(name = "VVQ_QUS_QUT_SEQ", precision = 6, scale = 0)
	public Integer getVvqQusQutSeq() {
		return this.vvqQusQutSeq;
	}

	public void setVvqQusQutSeq(Integer vvqQusQutSeq) {
		this.vvqQusQutSeq = vvqQusQutSeq;
	}

	@Column(name = "VVQ_QUS_SEQP", precision = 3, scale = 0)
	public Short getVvqQusSeqp() {
		return this.vvqQusSeqp;
	}

	public void setVvqQusSeqp(Short vvqQusSeqp) {
		this.vvqQusSeqp = vvqQusSeqp;
	}

	@Column(name = "VVQ_SEQP", precision = 3, scale = 0)
	public Short getVvqSeqp() {
		return this.vvqSeqp;
	}

	public void setVvqSeqp(Short vvqSeqp) {
		this.vvqSeqp = vvqSeqp;
	}

	@Column(name = "ESP_SEQ", precision = 4, scale = 0)
	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	@Column(name = "RESPOSTA", length = 4000)
	@Length(max = 4000)
	public String getResposta() {
		return this.resposta;
	}

	public void setResposta(String resposta) {
		this.resposta = resposta;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
        @JoinColumn(name = "PEP_PAC_CODIGO", referencedColumnName = "PAC_CODIGO"),
        @JoinColumn(name = "PEP_CRIADO_EM", referencedColumnName = "CRIADO_EM")})
	public AipPesoPacientes getAipPesoPaciente() {
		return aipPesoPaciente;
	}

	public void setAipPesoPaciente(AipPesoPacientes aipPesoPaciente) {
		this.aipPesoPaciente = aipPesoPaciente;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
        @JoinColumn(name = "ATP_PAC_CODIGO", referencedColumnName = "PAC_CODIGO"),
        @JoinColumn(name = "ATP_CRIADO_EM", referencedColumnName = "CRIADO_EM")})
	public AipAlturaPacientes getAipAlturaPaciente() {
		return aipAlturaPaciente;
	}

	public void setAipAlturaPaciente(AipAlturaPacientes aipAlturaPaciente) {
		this.aipAlturaPaciente = aipAlturaPaciente;
	}

	@Column(name = "PLP_PAC_CODIGO", precision = 8, scale = 0)
	public Integer getPlpPacCodigo() {
		return this.plpPacCodigo;
	}

	public void setPlpPacCodigo(Integer plpPacCodigo) {
		this.plpPacCodigo = plpPacCodigo;
	}

	@Column(name = "PLP_SEQP", precision = 4, scale = 0)
	public Short getPlpSeqp() {
		return this.plpSeqp;
	}

	public void setPlpSeqp(Short plpSeqp) {
		this.plpSeqp = plpSeqp;
	}

	@Column(name = "PIP_PAC_CODIGO", precision = 8, scale = 0)
	public Integer getPipPacCodigo() {
		return this.pipPacCodigo;
	}

	public void setPipPacCodigo(Integer pipPacCodigo) {
		this.pipPacCodigo = pipPacCodigo;
	}

	@Column(name = "PIP_SEQP", precision = 4, scale = 0)
	public Short getPipSeqp() {
		return this.pipSeqp;
	}

	public void setPipSeqp(Short pipSeqp) {
		this.pipSeqp = pipSeqp;
	}

	@Column(name = "PDP_PAC_CODIGO", precision = 8, scale = 0)
	public Integer getPdpPacCodigo() {
		return this.pdpPacCodigo;
	}

	public void setPdpPacCodigo(Integer pdpPacCodigo) {
		this.pdpPacCodigo = pdpPacCodigo;
	}

	@Column(name = "PDP_SEQP", precision = 4, scale = 0)
	public Short getPdpSeqp() {
		return this.pdpSeqp;
	}

	public void setPdpSeqp(Short pdpSeqp) {
		this.pdpSeqp = pdpSeqp;
	}

	@Column(name = "UNF_SEQ", precision = 4, scale = 0)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EVO_SEQ", nullable = false, insertable = false, updatable = false)
	public MamEvolucoes getEvolucao() {
		return this.evolucao;
	}
	
	public void setEvolucao(MamEvolucoes evolucao) {
		this.evolucao = evolucao;
	}

	public enum Fields {
		PEP_PAC_CODIGO("aipPesoPaciente.id.pacCodigo"), PEP_CRIADO_EM("aipPesoPaciente.id.criadoEm"),
		ATP_PAC_CODIGO("aipAlturaPaciente.id.pacCodigo"), ATP_CRIADO_EM("aipAlturaPaciente.id.criadoEm"),
		PIP_PAC_CODIGO("pipPacCodigo"), PIP_SEQP("pipSeqp"), PDP_PAC_CODIGO("pdpPacCodigo"), PDP_SEQP("pdpSeqp"),
		PLP_PAC_CODIGO("plpPacCodigo"), PLP_SEQP("plpSeqp"), EVO_SEQ("id.evoSeq"), QUS_QUT_SEQ("id.qusQutSeq"),
		QUS_SEQP("id.qusSeqp"), SEQ_P("id.seqp"),
		VALOR_VALIDO("mamValorValidoQuestao"),
		MAM_QUESTAO("mamQuestao"),
		EVOLUCAO("evolucao"),
		VVQ_QUS_QUT_SEQ("vvqQusQutSeq"),
		VVQ_QUS_SEQP("vvqSeqp"),
		VVQ_SEQP("vvqSeqp"),
		RESPOSTA("resposta"),
		ESP_SEQ("espSeq"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
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
		if (!(obj instanceof MamRespostaEvolucoes)) {
			return false;
		}
		MamRespostaEvolucoes other = (MamRespostaEvolucoes) obj;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
        @JoinColumn(name = "VVQ_QUS_QUT_SEQ", referencedColumnName = "QUS_QUT_SEQ", insertable=false, updatable=false),
        @JoinColumn(name = "VVQ_QUS_SEQP", referencedColumnName = "QUS_SEQP", insertable=false, updatable=false),
        @JoinColumn(name = "VVQ_SEQP", referencedColumnName = "SEQP", insertable=false, updatable=false)})
	public MamValorValidoQuestao getMamValorValidoQuestao() {
		return mamValorValidoQuestao;
	}

	public void setMamValorValidoQuestao(MamValorValidoQuestao valorValido) {
		this.mamValorValidoQuestao = valorValido;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
        @JoinColumn(name = "QUS_QUT_SEQ",referencedColumnName = "QUT_SEQ",  nullable = false, insertable = false, updatable = false),
        @JoinColumn(name = "QUS_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false)})
	public MamQuestao getMamQuestao() {
		return mamQuestao;
	}

	public void setMamQuestao(MamQuestao questao) {
		this.mamQuestao = questao;
	}

}