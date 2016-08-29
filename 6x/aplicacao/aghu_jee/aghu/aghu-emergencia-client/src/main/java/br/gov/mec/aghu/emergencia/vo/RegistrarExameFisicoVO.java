package br.gov.mec.aghu.emergencia.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;

import br.gov.mec.aghu.dominio.DominioAdequacaoPesoRN;
import br.gov.mec.aghu.dominio.DominioAspectoGeral;
import br.gov.mec.aghu.dominio.DominioMoro;
import br.gov.mec.aghu.dominio.DominioReflexoVermelho;
import br.gov.mec.aghu.dominio.DominioSaturacaoDiferencial;

	
public class RegistrarExameFisicoVO{
	private Integer seq;
	private Byte seqp;
	private Date dtHrExameFisico;
	private String nome;
	private String comp;
	private BigDecimal pc;
	private BigDecimal pt;
	private BigDecimal ca;
	private Short fc;
	private Short fr;
	private BigDecimal temp;
	private Integer prontuario;
	private Integer pacCodigo;
	private DominioAspectoGeral aspGeral;
	private DominioMoro  moro;
	private DominioMoro fugaAsfixia;
	private DominioMoro reptacao;
	private DominioMoro marcha;
	private DominioMoro succao;
	private Boolean crede;
	private DominioReflexoVermelho reflexoVermelho;
	private DominioSaturacaoDiferencial saturacaoDiferencial;
	private Boolean semParticularidade;
	private Boolean particularidade;
	private String observacao;
	private Integer sdm;
	private Short tempoRetornoMae;
	private Short tempoRetornoMin;
	private Byte idadeGestacionalFinal;
	private DominioAdequacaoPesoRN adqPeso;
	private boolean alterouItem;
	private boolean desabilitarAdqPeso;
	
	public Short getTempoRetornoMae() {
		return tempoRetornoMae;
	}
	public void setTempoRetornoMae(Short tempoRetornoMae) {
		this.tempoRetornoMae = tempoRetornoMae;
	}
	public Short getTempoRetornoMin() {
		return tempoRetornoMin;
	}
	public void setTempoRetornoMin(Short tempoRetornoMin) {
		this.tempoRetornoMin = tempoRetornoMin;
	}
	public Date getDtHrExameFisico() {
		return dtHrExameFisico;
	}
	public void setDtHrExameFisico(Date dtHrExameFisico) {
		this.dtHrExameFisico = dtHrExameFisico;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getComp() {
		return comp;
	}
	public void setComp(String comp) {
		this.comp = comp;
	}
	public BigDecimal getPc() {
		return pc;
	}
	public void setPc(BigDecimal pc) {
		this.pc = pc;
	}
	public BigDecimal getPt() {
		return pt;
	}
	public void setPt(BigDecimal pt) {
		this.pt = pt;
	}
	public BigDecimal getCa() {
		return ca;
	}
	public void setCa(BigDecimal ca) {
		this.ca = ca;
	}
	public Short getFc() {
		return fc;
	}
	public void setFc(Short fc) {
		this.fc = fc;
	}
	public Short getFr() {
		return fr;
	}
	public void setFr(Short fr) {
		this.fr = fr;
	}
	public BigDecimal getTemp() {
		return temp;
	}
	public void setTemp(BigDecimal temp) {
		this.temp = temp;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public DominioAspectoGeral getAspGeral() {
		return aspGeral;
	}
	public void setAspGeral(DominioAspectoGeral aspGeral) {
		this.aspGeral = aspGeral;
	}
	public DominioMoro getMoro() {
		return moro;
	}
	public void setMoro(DominioMoro moro) {
		this.moro = moro;
	}
	public DominioMoro getFugaAsfixia() {
		return fugaAsfixia;
	}
	public void setFugaAsfixia(DominioMoro fugaAsfixia) {
		this.fugaAsfixia = fugaAsfixia;
	}
	public DominioMoro getReptacao() {
		return reptacao;
	}
	public void setReptacao(DominioMoro reptacao) {
		this.reptacao = reptacao;
	}
	public DominioMoro getMarcha() {
		return marcha;
	}
	public void setMarcha(DominioMoro marcha) {
		this.marcha = marcha;
	}
	public DominioMoro getSuccao() {
		return succao;
	}
	public void setSuccao(DominioMoro succao) {
		this.succao = succao;
	}
	public Boolean getCrede() {
		return crede;
	}
	public void setCrede(Boolean crede) {
		this.crede = crede;
	}
	public DominioReflexoVermelho getReflexoVermelho() {
		return reflexoVermelho;
	}
	public void setReflexoVermelho(DominioReflexoVermelho reflexoVermelho) {
		this.reflexoVermelho = reflexoVermelho;
	}
	public DominioSaturacaoDiferencial getSaturacaoDiferencial() {
		return saturacaoDiferencial;
	}
	public void setSaturacaoDiferencial(
			DominioSaturacaoDiferencial saturacaoDiferencial) {
		this.saturacaoDiferencial = saturacaoDiferencial;
	}
	public Boolean getSemParticularidade() {
		return semParticularidade;
	}
	public void setSemParticularidade(Boolean semParticularidade) {
		this.semParticularidade = semParticularidade;
	}
	public Boolean getParticularidade() {
		return particularidade;
	}
	public void setParticularidade(Boolean particularidade) {
		this.particularidade = particularidade;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public Integer getSdm() {
		return sdm;
	}
	public void setSdm(Integer sdm) {
		this.sdm = sdm;
	}

	public Byte getIdadeGestacionalFinal() {
		return idadeGestacionalFinal;
	}
	public void setIdadeGestacionalFinal(Byte idadeGestacionalFinal) {
		this.idadeGestacionalFinal = idadeGestacionalFinal;
	}
	public DominioAdequacaoPesoRN getAdqPeso() {
		return adqPeso;
	}
	public void setAdqPeso(DominioAdequacaoPesoRN adqPeso) {
		this.adqPeso = adqPeso;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
	        if (!(obj instanceof RegistrarExameFisicoVO)) {
	            return false;
	        }
	        RegistrarExameFisicoVO other = (RegistrarExameFisicoVO) obj;
	        EqualsBuilder umEqualsBuilder = new EqualsBuilder();
	        umEqualsBuilder.append(this.getSeq(), other.getSeq());
	        return umEqualsBuilder.isEquals();
	    }
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Byte getSeqp() {
		return seqp;
	}
	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}
	public boolean isAlterouItem() {
		return alterouItem;
	}
	public void setAlterouItem(boolean alterouItem) {
		this.alterouItem = alterouItem;
	}
	public boolean isDesabilitarAdqPeso() {
		return desabilitarAdqPeso;
	}
	public void setDesabilitarAdqPeso(boolean desabilitarAdqPeso) {
		this.desabilitarAdqPeso = desabilitarAdqPeso;
	}

}
