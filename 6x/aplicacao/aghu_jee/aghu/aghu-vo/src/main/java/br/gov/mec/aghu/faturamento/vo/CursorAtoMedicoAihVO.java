package br.gov.mec.aghu.faturamento.vo;

import java.util.Calendar;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioCobrancaDiaria;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * VO utilizado no cursor c_atos_medicos_aih
 */
public class CursorAtoMedicoAihVO implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8363693585657592809L;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private Date dtAltaAdministrativa;
	private Date dtIntAdministrativa;
	private Short motivo;
	private DominioCobrancaDiaria indCobrancaDiarias;
	private Boolean indQtdMaiorInternacao;
	private Boolean caract;
	private FatAtoMedicoAih atoMedicoAih;
	private Integer comp;
	
	public enum Fields {
		IPH_PHO_SEQ("iphPhoSeq"),
		IPH_SEQ("iphSeq"),
		DT_ALTA_ADMINISTRATIVA("dtAltaAdministrativa"),
		DT_INT_ADMINISTRATIVA("dtIntAdministrativa"),
		MOTIVO("motivo"),
		IND_COBRANCA_DIARIAS("indCobrancaDiarias"),
		IND_QTD_MAIOR_INTERNACAO("indQtdMaiorInternacao"),
		CARACT("caract"),
		ATO_MEDICO_AIH("atoMedicoAih"),
		COMP("comp");
		
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
	public String toString() {
		return "CursorAtoMedicoAihVO [iphPhoSeq=" + iphPhoSeq + ", iphSeq="
				+ iphSeq + ", dtAltaAdministrativa=" + dtAltaAdministrativa
				+ ", dtIntAdministrativa=" + dtIntAdministrativa + ", motivo="
				+ motivo + ", indCobrancaDiarias=" + indCobrancaDiarias
				+ ", indQtdMaiorInternacao=" + indQtdMaiorInternacao
				+ ", caract=" + caract + ", atoMedicoAih=" + atoMedicoAih
				+ ", comp=" + comp + "]";
	}
	
	
	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}
	
	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}
	
	public Integer getIphSeq() {
		return iphSeq;
	}
	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}
	
	public Date getDtAltaAdministrativa() {
		return dtAltaAdministrativa;
	}
	
	public void setDtAltaAdministrativa(Date dtAltaAdministrativa) {
		this.dtAltaAdministrativa = dtAltaAdministrativa;
	}
	
	public Date getDtIntAdministrativa() {
		return dtIntAdministrativa;
	}
	
	public void setDtIntAdministrativa(Date dtIntAdministrativa) {
		this.dtIntAdministrativa = dtIntAdministrativa;
	}
	
	public Integer getDiaDaAlta() {
		Calendar calendar = DateUtil.getCalendarBy(dtAltaAdministrativa);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public Integer getQtdeDiasMes() {
		Date data = DateUtil.obterDataFimCompetencia(dtAltaAdministrativa);
		Calendar calendar = DateUtil.getCalendarBy(data);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	public Integer getAnoMesAlta() {
		return Integer.valueOf(DateUtil.obterDataFormatada(dtAltaAdministrativa, "yyyyMM"));
	}
	
	public Integer getAnoMesInt() {
		return Integer.valueOf(DateUtil.obterDataFormatada(dtIntAdministrativa, "yyyyMM"));
	}
	
	public Integer getDiaMesInt() {
		return Integer.valueOf(DateUtil.obterDataFormatada(dtIntAdministrativa, "dd"));
	}
	
	public Short getMotivo() {
		return motivo;
	}

	public void setMotivo(Short motivo) {
		this.motivo = motivo;
	}

	public DominioCobrancaDiaria getIndCobrancaDiarias() {
		return indCobrancaDiarias;
	}
	
	public void setIndCobrancaDiarias(DominioCobrancaDiaria indCobrancaDiarias) {
		this.indCobrancaDiarias = indCobrancaDiarias;
	}
	
	public Boolean getIndQtdMaiorInternacao() {
		return indQtdMaiorInternacao;
	}
	
	public void setIndQtdMaiorInternacao(Boolean indQtdMaiorInternacao) {
		this.indQtdMaiorInternacao = indQtdMaiorInternacao;
	}
	
	public Boolean getCaract() {
		return caract;
	}
	
	public void setCaract(Boolean caract) {
		this.caract = caract;
	}
	
	public FatAtoMedicoAih getAtoMedicoAih() {
		return atoMedicoAih;
	}
	
	public void setAtoMedicoAih(FatAtoMedicoAih atoMedicoAih) {
		this.atoMedicoAih = atoMedicoAih;
	}
	
	public Integer getComp() {
		return comp;
	}
	
	public void setComp(Integer comp) {
		this.comp = comp;
	}
}