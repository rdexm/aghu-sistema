package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioGravidez;

public class DadosGestacaoVO implements Serializable {
	
	private static final long serialVersionUID = -4101227518458831214L;

	private String informacoesPaciente;
	private Byte gestacao;
	private Byte parto;
	private Byte cesariana;
	private Byte aborto;
	private Byte ectopica;
	private DominioGravidez gravidez;
	private String gemelar;
	private String informacoesIg;
	private Date dtUltimaMenstruacao;
	private Date dtPrimeiraEco;
	private Byte ecoSemanas;
	private Byte ecoDias;
	private Byte igAtualSemanas;
	private Byte igAtualDias;
	private Date dtProvavelParto;
	private Byte nroConsultas;
	private Date dtPrimeiraConsulta;
	private Boolean vatCompleta;
	private String tipoSanguineoMae;
	private String fatorRHMae;
	private String coombs;
	private Boolean mesmoPai;
	private String tipoSanguineoPai;
	private String fatorRHPai;
	private Date dtInformadaIg;
	private String justificativa;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date criadoEm;
	private Integer pacCodigo;
	private Short seqp;
	private String usoMedicamentos;
	private Date dthrSumarioAltaMae;
	private String mensagemModalJustificativa;
	private String observacaoExame;
	
	public String getInformacoesPaciente() {
		return informacoesPaciente;
	}
	public void setInformacoesPaciente(String informacoesPaciente) {
		this.informacoesPaciente = informacoesPaciente;
	}
	public Byte getGestacao() {
		return gestacao;
	}
	public void setGestacao(Byte gestacao) {
		this.gestacao = gestacao;
	}
	public Byte getParto() {
		return parto;
	}
	public void setParto(Byte parto) {
		this.parto = parto;
	}
	public Byte getCesariana() {
		return cesariana;
	}
	public void setCesariana(Byte cesariana) {
		this.cesariana = cesariana;
	}
	public Byte getAborto() {
		return aborto;
	}
	public void setAborto(Byte aborto) {
		this.aborto = aborto;
	}
	public Byte getEctopica() {
		return ectopica;
	}
	public void setEctopica(Byte ectopica) {
		this.ectopica = ectopica;
	}
	public DominioGravidez getGravidez() {
		return gravidez;
	}
	public void setGravidez(DominioGravidez gravidez) {
		this.gravidez = gravidez;
	}
	public String getGemelar() {
		return gemelar;
	}
	public void setGemelar(String gemelar) {
		this.gemelar = gemelar;
	}
	public String getInformacoesIg() {
		return informacoesIg;
	}
	public void setInformacoesIg(String informacoesIg) {
		this.informacoesIg = informacoesIg;
	}
	public Date getDtUltimaMenstruacao() {
		return dtUltimaMenstruacao;
	}
	public void setDtUltimaMenstruacao(Date dtUltimaMenstruacao) {
		this.dtUltimaMenstruacao = dtUltimaMenstruacao;
	}
	public Date getDtPrimeiraEco() {
		return dtPrimeiraEco;
	}
	public void setDtPrimeiraEco(Date dtPrimeiraEco) {
		this.dtPrimeiraEco = dtPrimeiraEco;
	}
	public Byte getEcoSemanas() {
		return ecoSemanas;
	}
	public void setEcoSemanas(Byte ecoSemanas) {
		this.ecoSemanas = ecoSemanas;
	}
	public Byte getEcoDias() {
		return ecoDias;
	}
	public void setEcoDias(Byte ecoDias) {
		this.ecoDias = ecoDias;
	}
	public Byte getIgAtualSemanas() {
		return igAtualSemanas;
	}
	public void setIgAtualSemanas(Byte igAtualSemanas) {
		this.igAtualSemanas = igAtualSemanas;
	}
	public Byte getIgAtualDias() {
		return igAtualDias;
	}
	public void setIgAtualDias(Byte igAtualDias) {
		this.igAtualDias = igAtualDias;
	}
	public Date getDtProvavelParto() {
		return dtProvavelParto;
	}
	public void setDtProvavelParto(Date dtProvavelParto) {
		this.dtProvavelParto = dtProvavelParto;
	}
	public Byte getNroConsultas() {
		return nroConsultas;
	}
	public void setNroConsultas(Byte nroConsultas) {
		this.nroConsultas = nroConsultas;
	}
	public Date getDtPrimeiraConsulta() {
		return dtPrimeiraConsulta;
	}
	public void setDtPrimeiraConsulta(Date dtPrimeiraConsulta) {
		this.dtPrimeiraConsulta = dtPrimeiraConsulta;
	}
	public Boolean getVatCompleta() {
		return vatCompleta;
	}
	public void setVatCompleta(Boolean vatCompleta) {
		this.vatCompleta = vatCompleta;
	}
	public String getTipoSanguineoMae() {
		return tipoSanguineoMae;
	}
	public void setTipoSanguineoMae(String tipoSanguineoMae) {
		this.tipoSanguineoMae = tipoSanguineoMae;
	}
	public String getFatorRHMae() {
		return fatorRHMae;
	}
	public void setFatorRHMae(String fatorRHMae) {
		this.fatorRHMae = fatorRHMae;
	}
	public String getCoombs() {
		return coombs;
	}
	public void setCoombs(String coombs) {
		this.coombs = coombs;
	}
	public Boolean getMesmoPai() {
		return mesmoPai;
	}
	public void setMesmoPai(Boolean mesmoPai) {
		this.mesmoPai = mesmoPai;
	}
	public String getTipoSanguineoPai() {
		return tipoSanguineoPai;
	}
	public void setTipoSanguineoPai(String tipoSanguineoPai) {
		this.tipoSanguineoPai = tipoSanguineoPai;
	}
	public String getFatorRHPai() {
		return fatorRHPai;
	}
	public void setFatorRHPai(String fatorRHPai) {
		this.fatorRHPai = fatorRHPai;
	}
	public Date getDtInformadaIg() {
		return dtInformadaIg;
	}
	public void setDtInformadaIg(Date dtInformadaIg) {
		this.dtInformadaIg = dtInformadaIg;
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public String getUsoMedicamentos() {
		return usoMedicamentos;
	}
	public void setUsoMedicamentos(String usoMedicamentos) {
		this.usoMedicamentos = usoMedicamentos;
	}
	public Date getDthrSumarioAltaMae() {
		return dthrSumarioAltaMae;
	}
	public void setDthrSumarioAltaMae(Date dthrSumarioAltaMae) {
		this.dthrSumarioAltaMae = dthrSumarioAltaMae;
	}
	public String getMensagemModalJustificativa() {
		return mensagemModalJustificativa;
	}
	public void setMensagemModalJustificativa(String mensagemModalJustificativa) {
		this.mensagemModalJustificativa = mensagemModalJustificativa;
	}
	public String getObservacaoExame() {
		return observacaoExame;
	}
	public void setObservacaoExame(String observacaoExame) {
		this.observacaoExame = observacaoExame;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((nroConsultas == null) ? 0 : nroConsultas.hashCode());
		result = prime * result + ((seqp == null) ? 0 : seqp.hashCode());
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
		if (!(obj instanceof DadosGestacaoVO)) {
			return false;
		}
		DadosGestacaoVO other = (DadosGestacaoVO) obj;
		if (nroConsultas == null) {
			if (other.nroConsultas != null) {
				return false;
			}
		} else if (!nroConsultas.equals(other.nroConsultas)) {
			return false;
		}
		if (seqp == null) {
			if (other.seqp != null) {
				return false;
			}
		} else if (!seqp.equals(other.seqp)) {
			return false;
		}
		return true;
	}
	
	
}
