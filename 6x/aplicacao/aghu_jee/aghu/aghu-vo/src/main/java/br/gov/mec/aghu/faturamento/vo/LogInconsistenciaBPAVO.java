package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.sql.Timestamp;



public class LogInconsistenciaBPAVO implements Serializable {

//	br.gov.mec.aghu.faturamento.vo.LogInconsistenciaBPAVO
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1347431230571700710L;

	private String erro;

	private Integer pacCodigo;
	
	private Integer prontuario;
	
	private String pacNome;

	private Integer prmSeq;

	private Integer phi;
	
	private Integer iph;
	
	private Long codSUS;

	private String descricaoIPH;
	
	private String descricaoPHI;

	private Timestamp dthrRealizado;
	
	private String indOrigem;
	
	private Integer soeSeq;
	
	private Short seqp;

	private Integer conNumero;
	
	private Integer grade;

	private Integer eprPciSeq;

	private Short eprEspSeq;
	
	private String indRespProc;
	
	private Integer seqCirurgia;

	private Short vinculoProf;
	
	private Integer matriculaProf;
	
	private String nomeProf;
	
	private String siglaEsp;
	
	private String codAtvProf;

	private String siglaProcAmb;
	
	private Short espSeqProcAmb;
	
	private Short iphPhoSeq;
	
	public enum Fields {
		
		PAC_CODIGO("pacCodigo"),
		PAC_NOME("pacNome"),
		PRONTUARIO("prontuario"),
		ERRO("erro"),
		PRM_SEQ("prmSeq"),
		PHI("phi"),
		IPH("iph"),
		COD_SUS("codSUS"),
		DESCRICAO_IPH("descricaoIPH"),
		DESCRICAO_PHI("descricaoPHI"),
		DT_REALIZADO("dthrRealizado"),
		IND_ORIGEM("indOrigem"),
		SEQ_P("seqp"),
		SOE_SEQ("soeSeq"),
		CON_NUMERO("conNumero"),
		GRADE("grade"),
		EPR_PCI_SEQ("eprPciSeq"),
		EPR_ESP_SEQ("eprEspSeq"),
		IND_RESP_PROC("indRespProc"),
		SEQ_CIRURGIA("seqCirurgia"),
		VINCULO_PROF("vinculoProf"),
		MATRICULA_PROF("matriculaProf"),
		NOME_PROF("nomeProf"),
		SIGLA_ESP("siglaEsp"),
		COD_ATV_PROF("codAtvProf"),
		SIGLA_PROC_AMB("siglaProcAmb"),
		ESP_SEQ_PROC_AMB("espSeqProcAmb"),
		IPH_PHO_SEQ("iphPhoSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}		
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getPrmSeq() {
		return prmSeq;
	}

	public void setPrmSeq(Integer prmSeq) {
		this.prmSeq = prmSeq;
	}

	public Integer getPhi() {
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}

	public Integer getIph() {
		return iph;
	}

	public void setIph(Integer iph) {
		this.iph = iph;
	}

	public Long getCodSUS() {
		return codSUS;
	}

	public void setCodSUS(Long codSUS) {
		this.codSUS = codSUS;
	}

	public String getDescricaoIPH() {
		return descricaoIPH;
	}

	public void setDescricaoIPH(String descricaoIPH) {
		this.descricaoIPH = descricaoIPH;
	}

	public String getDescricaoPHI() {
		return descricaoPHI;
	}

	public void setDescricaoPHI(String descricaoPHI) {
		this.descricaoPHI = descricaoPHI;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Timestamp getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(Timestamp dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public String getIndOrigem() {
		return indOrigem;
	}

	public void setIndOrigem(String indOrigem) {
		this.indOrigem = indOrigem;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getEprPciSeq() {
		return eprPciSeq;
	}

	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}

	public Short getEprEspSeq() {
		return eprEspSeq;
	}

	public void setEprEspSeq(Short eprEspSeq) {
		this.eprEspSeq = eprEspSeq;
	}

	public String getIndRespProc() {
		return indRespProc;
	}

	public void setIndRespProc(String indRespProc) {
		this.indRespProc = indRespProc;
	}

	public Integer getSeqCirurgia() {
		return seqCirurgia;
	}

	public void setSeqCirurgia(Integer seqCirurgia) {
		this.seqCirurgia = seqCirurgia;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public Short getVinculoProf() {
		return vinculoProf;
	}

	public void setVinculoProf(Short vinculoProf) {
		this.vinculoProf = vinculoProf;
	}

	public Integer getMatriculaProf() {
		return matriculaProf;
	}

	public void setMatriculaProf(Integer matriculaProf) {
		this.matriculaProf = matriculaProf;
	}

	public String getNomeProf() {
		return nomeProf;
	}

	public void setNomeProf(String nomeProf) {
		this.nomeProf = nomeProf;
	}

	public String getSiglaEsp() {
		return siglaEsp;
	}

	public void setSiglaEsp(String siglaEsp) {
		this.siglaEsp = siglaEsp;
	}

	public String getCodAtvProf() {
		return codAtvProf;
	}

	public void setCodAtvProf(String codAtvProf) {
		this.codAtvProf = codAtvProf;
	}

	public String getSiglaProcAmb() {
		return siglaProcAmb;
	}

	public void setSiglaProcAmb(String siglaProcAmb) {
		this.siglaProcAmb = siglaProcAmb;
	}

	public Short getEspSeqProcAmb() {
		return espSeqProcAmb;
	}

	public void setEspSeqProcAmb(Short espSeqProcAmb) {
		this.espSeqProcAmb = espSeqProcAmb;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}
	
}