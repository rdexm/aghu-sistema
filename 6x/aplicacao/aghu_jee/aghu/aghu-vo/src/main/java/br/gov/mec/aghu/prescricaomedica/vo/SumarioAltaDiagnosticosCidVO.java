package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObtCausaAntecedente;
import br.gov.mec.aghu.model.MpmObtCausaDireta;
import br.gov.mec.aghu.model.MpmObtOutraCausa;


public class SumarioAltaDiagnosticosCidVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9057661029704372517L;
	// Atributos para identificar o registro no banco
	private MpmAltaSumarioId id;
	private MpmObtCausaDireta obitoCausaDireta;
	private MpmObtCausaAntecedente obitoCausaAntecendente;
	private MpmObtOutraCausa obtOutraCausa;
	private Short seqp;	
	
	// Atributos para apresentação na tela
	private Integer ciaSeq; // MpmCidAtendimento.seq
	private Long diaSeq;    // MamDiagnostico.seq
	private AghCid cid;
	private String complemento; // Vindo das pesquisas auxiliares da combo (procedure MPMP_MONTA_POPLIST_CID)
	private String complementoEditado; // Valor que pode ser informado/editado em tela pelo usuário
	private Boolean emEdicao;
	private Boolean origemListaCombo;
	private Short prioridade;
	

	/**
	 * Construtor padrão necessário para permitir o uso desta classe em criterias.
	 * Ex.: Transformers.aliasToBean(SumarioAltaDiagnosticosCidVO.class)
	 */
	public SumarioAltaDiagnosticosCidVO() {
		this.setEmEdicao(Boolean.FALSE);
		this.setOrigemListaCombo(Boolean.FALSE);		
	}
	
	public SumarioAltaDiagnosticosCidVO(MpmAltaSumarioId id, Short seqp) {
		this();
		this.setId(id);
		this.setSeqp(seqp);
	}
		
	private String getDescricaoFormatada() {		
		
		
		StringBuffer sbDescricao = new StringBuffer();
		
		sbDescricao.append(this.cid.getDescricaoEditada() == null ? this.cid.getDescricao() : this.cid.getDescricaoEditada())
		.append(" (")
		.append(this.cid.getCodigo())
		.append(") ");
		if (this.complemento != null) {
			sbDescricao.append(" - ");
			sbDescricao.append(this.complemento);
		}
		
		return sbDescricao.toString();
	}
	
	public String getDescricao() {
		StringBuffer sbDescricao = new StringBuffer();
		
		sbDescricao.append(getDescricaoFormatada());
		if (this.complementoEditado != null) {
			sbDescricao.append("; ");
			sbDescricao.append(this.complementoEditado);
		}
		
		return sbDescricao.toString();
	}
	
	public Integer getCiaSeq() {
		return this.ciaSeq;
	}

	public void setCiaSeq(Integer ciaSeq) {
		this.ciaSeq = ciaSeq;
	}

	public Long getDiaSeq() {
		return this.diaSeq;
	}

	public void setDiaSeq(Long diaSeq) {
		this.diaSeq = diaSeq;
	}

	public AghCid getCid() {
		return this.cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getComplementoEditado() {
		return this.complementoEditado;
	}

	public void setComplementoEditado(String complementoEditado) {
		this.complementoEditado = complementoEditado;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Boolean getEmEdicao() {
		return this.emEdicao;
	}
	
	public void setOrigemListaCombo(Boolean origemListaCombo) {
		this.origemListaCombo = origemListaCombo;
	}

	public Boolean getOrigemListaCombo() {
		return this.origemListaCombo;
	}

	@Override
	public int hashCode() {
		return this.getCid().getSeq() == null ? 0 : this.getCid().getSeq().hashCode();
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
		
		SumarioAltaDiagnosticosCidVO other = (SumarioAltaDiagnosticosCidVO) obj;
		return (this.getCid().getSeq() != null && this.getCid().getSeq().equals(other.getCid().getSeq()));
	}

	public void setId(MpmAltaSumarioId id) {
		this.id = id;
	}

	public MpmAltaSumarioId getId() {
		return this.id;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Short getSeqp() {
		return this.seqp;
	}

	public MpmObtCausaAntecedente getObitoCausaAntecendente() {
		return obitoCausaAntecendente;
	}

	public void setObitoCausaAntecendente(
			MpmObtCausaAntecedente obitoCausaAntecendente) {
		this.obitoCausaAntecendente = obitoCausaAntecendente;
	}
	
	
	public MpmObtOutraCausa getObtOutraCausa() {
		return obtOutraCausa;
	}
	
	public void setObtOutraCausa(MpmObtOutraCausa obtOutraCausa) {
		this.obtOutraCausa = obtOutraCausa;
	}

	public void setObitoCausaDireta(MpmObtCausaDireta obitoCausaDireta) {
		this.obitoCausaDireta = obitoCausaDireta;
	}
	
	public MpmObtCausaDireta getObitoCausaDireta() {
		return obitoCausaDireta;
	}
	
	public Short getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(Short prioridade) {
		this.prioridade = prioridade;
	}
}