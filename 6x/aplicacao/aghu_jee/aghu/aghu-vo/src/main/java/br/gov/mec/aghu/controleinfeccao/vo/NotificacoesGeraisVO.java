package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.commons.BaseBean;

public class NotificacoesGeraisVO implements BaseBean {

	private static final long serialVersionUID = -863975743802638385L;
	
	private Integer patSeq;
	private String patDescricao;
	private Short topSeq;
	private String topDescricao;
	
	private Integer seq;
	private String descricao;
	private Date dtInicio;
	private Date dtFim;
	private Integer crgSeq;
	
	private String dtInicioFormatado;
	private String dtFimFormatado;
	
	private String nomeServidor;
	private Date dtFimCirurgia;
	private String dtFimCirurgiaFormatado;
	private String tanDescricao;
	
	private String etgDescricao;

	public NotificacoesGeraisVO() {

	}

	public Integer getPatSeq() {
		return patSeq;
	}

	public void setPatSeq(Integer patSeq) {
		this.patSeq = patSeq;
	}

	public String getPatDescricao() {
		return patDescricao;
	}

	public void setPatDescricao(String patDescricao) {
		this.patDescricao = patDescricao;
	}

	public Short getTopSeq() {
		return topSeq;
	}

	public void setTopSeq(Short topSeq) {
		this.topSeq = topSeq;
	}

	public String getTopDescricao() {
		return topDescricao;
	}

	public void setTopDescricao(String topDescricao) {
		this.topDescricao = topDescricao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public Date getDtFimCirurgia() {
		return dtFimCirurgia;
	}

	public void setDtFimCirurgia(Date dtFimCirurgia) {
		this.dtFimCirurgia = dtFimCirurgia;
	}

	public String getEtgDescricao() {
		return etgDescricao;
	}

	public void setEtgDescricao(String etgDescricao) {
		this.etgDescricao = etgDescricao;
	}
	
	public String getDtInicioFormatado() {
		return dtInicioFormatado;
	}

	public void setDtInicioFormatado(String dtInicioFormatado) {
		this.dtInicioFormatado = dtInicioFormatado;
	}

	public String getDtFimFormatado() {
		return dtFimFormatado;
	}

	public void setDtFimFormatado(String dtFimFormatado) {
		this.dtFimFormatado = dtFimFormatado;
	}

	public String getDtFimCirurgiaFormatado() {
		return dtFimCirurgiaFormatado;
	}

	public void setDtFimCirurgiaFormatado(String dtFimCirurgiaFormatado) {
		this.dtFimCirurgiaFormatado = dtFimCirurgiaFormatado;
	}



	public enum Fields {
		PAT_SEQ("patSeq"),
		PAT_DESCRICAO("patDescricao"),
		TOP_SEQ("topSeq"),
		TOP_DESCRICAO("topDescricao"),
		SEQ("seq"),
		DESCRICAO("descricao"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim"),
		NOME_SERVIDOR("nomeServidor"),
		DT_FIM_CIRURGIA("dtFimCirurgia"),
		ETG_DESCRICAO("etgDescricao"),
		TAN_DESCRICAO("tanDescricao"),
		CRG_SEQ("crgSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	@Override
    public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getPatSeq());
        umHashCodeBuilder.append(this.getPatDescricao());
        umHashCodeBuilder.append(this.getTopSeq());
        umHashCodeBuilder.append(this.getTopDescricao());
        umHashCodeBuilder.append(this.getSeq());
        umHashCodeBuilder.append(this.getDescricao());
        umHashCodeBuilder.append(this.getDtInicio());
        umHashCodeBuilder.append(this.getDtFim());
        umHashCodeBuilder.append(this.getNomeServidor());
        umHashCodeBuilder.append(this.getDtFimCirurgia());
        return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof NotificacoesGeraisVO)) {
			return false;
		}
		NotificacoesGeraisVO other = (NotificacoesGeraisVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getPatSeq(), other.getPatSeq());
		umEqualsBuilder.append(this.getPatDescricao(), other.getPatDescricao());
		umEqualsBuilder.append(this.getTopSeq(), other.getTopSeq());
		umEqualsBuilder.append(this.getTopDescricao(), other.getTopDescricao());
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getDtInicio(), other.getDtInicio());
		umEqualsBuilder.append(this.getDtFim(), other.getDtFim());
		umEqualsBuilder.append(this.getNomeServidor(), other.getNomeServidor());
		umEqualsBuilder.append(this.getDtFimCirurgia(), other.getDtFimCirurgia());
		return umEqualsBuilder.isEquals();
	}

	public String getTanDescricao() {
		return tanDescricao;
	}

	public void setTanDescricao(String tanDescricao) {
		this.tanDescricao = tanDescricao;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
}
