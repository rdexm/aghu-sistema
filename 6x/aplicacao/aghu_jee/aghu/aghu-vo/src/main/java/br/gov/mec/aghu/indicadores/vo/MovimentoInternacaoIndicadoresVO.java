package br.gov.mec.aghu.indicadores.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;


public class MovimentoInternacaoIndicadoresVO {

	private Integer prontuario;
	private String sigla;
	private Integer intSeq;
	private Integer atuSeq;
	private String tamCodigo;
	private Short oevSeq;
	private Date dthrAltaMedica;
	private Integer tmiSeq;
	private Short unfSeq;
	private String ltoLtoId;
	private Short espSeq;
	Short serVinCodigo;
	Integer serMatricula;
	private RapServidores servidor;
	private Date dthrLancamento;

	// Formatação em chamada de método (Data Final)
	private Date dthrFinalM;
	// Formatação em chamada de método (Tipo Movimento Internacao)
	private Integer tmiSeqT;
	// Formatação em chamada de método (Especialidade)
	private Short espSeqUtil;
	// Formatação em chamada de método (Unidade)
	private Short unidadeUtil;

	private Integer cctCodigo;
	private Integer clcCodigo;

	private Integer procedimento = 0;
	private Integer gphSeq = 0;
	private Integer cddCodigo = 0;
	private Integer bclBaiCodigo = 0;
	private Integer dstCodigo = 0;

	// Formatação em chamada de método (Clinica)
	private Integer codigoClinicaNvl;
	private Boolean indConsClinLto;
	private DominioSimNao indConsClinQrt;
	private Boolean indConsClinUnf;

	private DominioSimNao indExclusivInfeccao;

	private String tipo;
	private String leitoPrivativo;

	private FatConvenioSaudePlano convenioSaudePlano;

	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	private Integer movimentoInternacaoSeq;

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Integer getIntSeq() {
		return intSeq;
	}

	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}

	public Integer getAtuSeq() {
		return atuSeq;
	}

	public void setAtuSeq(Integer atuSeq) {
		this.atuSeq = atuSeq;
	}

	public String getTamCodigo() {
		return tamCodigo;
	}

	public void setTamCodigo(String tamCodigo) {
		this.tamCodigo = tamCodigo;
	}

	public Short getOevSeq() {
		return oevSeq;
	}

	public void setOevSeq(Short oevSeq) {
		this.oevSeq = oevSeq;
	}

	public Date getDthrAltaMedica() {
		return dthrAltaMedica;
	}

	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

	public Integer getTmiSeq() {
		return tmiSeq;
	}

	public void setTmiSeq(Integer tmiSeq) {
		this.tmiSeq = tmiSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public Date getDthrLancamento() {
		return dthrLancamento;
	}

	public void setDthrLancamento(Date dthrLancamento) {
		this.dthrLancamento = dthrLancamento;
	}

	public Date getDthrFinalM() {
		return dthrFinalM;
	}

	public void setDthrFinalM(Date dthrFinalM) {
		this.dthrFinalM = dthrFinalM;
	}

	public Integer getTmiSeqT() {
		return tmiSeqT;
	}

	public void setTmiSeqT(Integer tmiSeqT) {
		this.tmiSeqT = tmiSeqT;
	}

	public Short getEspSeqUtil() {
		return espSeqUtil;
	}

	public void setEspSeqUtil(Short espSeqUtil) {
		this.espSeqUtil = espSeqUtil;
	}

	public Short getUnidadeUtil() {
		return unidadeUtil;
	}

	public void setUnidadeUtil(Short unidadeUtil) {
		this.unidadeUtil = unidadeUtil;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getClcCodigo() {
		return clcCodigo;
	}

	public void setClcCodigo(Integer clcCodigo) {
		this.clcCodigo = clcCodigo;
	}

	public Integer getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(Integer procedimento) {
		this.procedimento = procedimento;
	}

	public Integer getGphSeq() {
		return gphSeq;
	}

	public void setGphSeq(Integer gphSeq) {
		this.gphSeq = gphSeq;
	}

	public Integer getCddCodigo() {
		return cddCodigo;
	}

	public void setCddCodigo(Integer cddCodigo) {
		this.cddCodigo = cddCodigo;
	}

	public Integer getBclBaiCodigo() {
		return bclBaiCodigo;
	}

	public void setBclBaiCodigo(Integer bclBaiCodigo) {
		this.bclBaiCodigo = bclBaiCodigo;
	}

	public Integer getDstCodigo() {
		return dstCodigo;
	}

	public void setDstCodigo(Integer dstCodigo) {
		this.dstCodigo = dstCodigo;
	}

	public Integer getCodigoClinicaNvl() {
		return codigoClinicaNvl;
	}

	public void setCodigoClinicaNvl(Integer codigoClinicaNvl) {
		this.codigoClinicaNvl = codigoClinicaNvl;
	}

	public Boolean getIndConsClinLto() {
		return indConsClinLto;
	}

	public void setIndConsClinLto(Boolean indConsClinLto) {
		this.indConsClinLto = indConsClinLto;
	}

	public DominioSimNao getIndConsClinQrt() {
		return indConsClinQrt;
	}

	public void setIndConsClinQrt(DominioSimNao indConsClinQrt) {
		this.indConsClinQrt = indConsClinQrt;
	}

	public Boolean getIndConsClinUnf() {
		return indConsClinUnf;
	}

	public void setIndConsClinUnf(Boolean indConsClinUnf) {
		this.indConsClinUnf = indConsClinUnf;
	}

	public DominioSimNao getIndExclusivInfeccao() {
		return indExclusivInfeccao;
	}

	public void setIndExclusivInfeccao(DominioSimNao indExclusivInfeccao) {
		this.indExclusivInfeccao = indExclusivInfeccao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getLeitoPrivativo() {
		return leitoPrivativo;
	}

	public void setLeitoPrivativo(String leitoPrivativo) {
		this.leitoPrivativo = leitoPrivativo;
	}

	public Integer getMovimentoInternacaoSeq() {
		return movimentoInternacaoSeq;
	}

	public void setMovimentoInternacaoSeq(Integer movimentoInternacaoSeq) {
		this.movimentoInternacaoSeq = movimentoInternacaoSeq;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	
	public String getValores() {
		StringBuilder sb = new StringBuilder(50);
		
		sb.append(this.prontuario).append(',')
		.append(this.sigla).append(',')
		.append(this.intSeq).append(',')
		.append(this.atuSeq).append(',')
		.append(this.tamCodigo).append(',')
		.append(this.oevSeq).append(',')
		.append(this.dthrAltaMedica).append(',')
		.append(this.tmiSeq).append(',')
		.append(this.unfSeq).append(',')
		.append(this.ltoLtoId).append(',')
		.append(this.espSeq).append(',')
		.append(this.serVinCodigo).append(',')
		.append(this.serMatricula).append(',')
		.append(this.dthrLancamento).append(',')
		.append(this.dthrFinalM).append(',')
		.append(this.tmiSeqT).append(',')
		.append(this.espSeqUtil).append(',')
		.append(this.unidadeUtil).append(',')
		.append(this.cctCodigo).append(',')
		.append(this.clcCodigo).append(',')
		.append(this.procedimento).append(',')
		.append(this.gphSeq).append(',')
		.append(this.cddCodigo).append(',')
		.append(this.bclBaiCodigo).append(',')
		.append(this.dstCodigo).append(',')
		.append(this.codigoClinicaNvl).append(',')
		.append(this.indConsClinLto).append(',')
		.append(this.indConsClinQrt).append(',')
		.append(this.indConsClinUnf).append(',')
		.append(this.indExclusivInfeccao).append(',')
		.append(this.tipo).append(',')
		.append(this.leitoPrivativo).append(',')
		.append(this.convenioSaudePlano.getId().getCnvCodigo()).append(',');
		
		return sb.toString();
		
	}

}
