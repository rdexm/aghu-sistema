package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;

public class MamPacientesAtendidosVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 656981156282455254L;
	/**
	 * 
	 */
	private Date dtConsulta;
	private String pacNome;
	private Integer pacProntuario;
	private Integer pacIdade;
	private Integer pacCodigo;
	private Short espSeq;
	private String espSigla;
	private Long trgSeq;
	private Short segSeq;
	private DominioPacAtendimento indPacAtendimento;
	private Short unfSeq;
	private DominioTipoMovimento ultTipoMvt;
	private Date dthrUltMvto;
	private Integer conNumero;
	private Integer atdSeq;
	private String localizacao;
	private Boolean pendenciaAssinaturaDigital;
	private String tooltipUltimoAtendimento;
	private String pacProntuarioFormatado;
	private Short seqp;

	public static Comparator<MamPacientesAtendidosVO> getComparator(SortParameter... sortParameters) {
		return new MamPacientesAtendidosVOComparator(sortParameters);
	}

	public enum SortParameter {
		ID_DESCENDING, NAME_ASCENDING
	}

	private static class MamPacientesAtendidosVOComparator implements Comparator<MamPacientesAtendidosVO> {
		private SortParameter[] parameters;

		private MamPacientesAtendidosVOComparator(SortParameter[] parameters) {
			this.parameters = parameters;
		}

		public int compare(MamPacientesAtendidosVO o1, MamPacientesAtendidosVO o2) {
			int comparison;
			for (SortParameter parameter : parameters) {
				switch (parameter) {
				case ID_DESCENDING:
					comparison = o2.seqp - o1.seqp;
					if (comparison != 0){
						return comparison;
					}
					break;
				case NAME_ASCENDING:
					comparison = o1.pacNome.compareTo(o2.pacNome);
					if (comparison != 0){
						return comparison;
					}
					break;
				}
			}
			return 0;
		}
	}

	public Date getDtConsulta() {
		return dtConsulta;
	}

	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacIdade() {
		return pacIdade;
	}

	public void setPacIdade(Integer pacIdade) {
		this.pacIdade = pacIdade;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public String getEspSigla() {
		return espSigla;
	}

	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public Short getSegSeq() {
		return segSeq;
	}

	public void setSegSeq(Short segSeq) {
		this.segSeq = segSeq;
	}

	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}

	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public DominioTipoMovimento getUltTipoMvt() {
		return ultTipoMvt;
	}

	public void setUltTipoMvt(DominioTipoMovimento ultTipoMvt) {
		this.ultTipoMvt = ultTipoMvt;
	}

	public Date getDthrUltMvto() {
		return dthrUltMvto;
	}

	public void setDthrUltMvto(Date dthrUltMvto) {
		this.dthrUltMvto = dthrUltMvto;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Boolean getPendenciaAssinaturaDigital() {
		return pendenciaAssinaturaDigital;
	}

	public void setPendenciaAssinaturaDigital(Boolean pendenciaAssinaturaDigital) {
		this.pendenciaAssinaturaDigital = pendenciaAssinaturaDigital;
	}

	public String getTooltipUltimoAtendimento() {
		return tooltipUltimoAtendimento;
	}

	public void setTooltipUltimoAtendimento(String tooltipUltimoAtendimento) {
		this.tooltipUltimoAtendimento = tooltipUltimoAtendimento;
	}

	public String getPacProntuarioFormatado() {
		return pacProntuarioFormatado;
	}

	public void setPacProntuarioFormatado(String pacProntuarioFormatado) {
		this.pacProntuarioFormatado = pacProntuarioFormatado;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

}
