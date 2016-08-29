package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class PendenciaEncerramentoVO {
	
	private Integer cthseq;
	private Integer prontuario;
	private String nome;
	private String leito;
	private Date dtIntAdm;
	private Date dtAltAdm;
	private Long nroAih;
	private String mspSia;
	private Integer intseq;
	private String erro;
	private String desdobr;
	private Integer phirealizado;
	private Short cspcnvcodigo;
	private Byte cspseq;
	
	private String uErro;
	private String uLeito;
	private String uNome;
	
	public PendenciaEncerramentoVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PendenciaEncerramentoVO(Integer cthseq, Integer prontuario,
			String nome, String leito, Date dtIntAdm, Date altAdm, Long nroAih,
			String mspSia, Integer intseq, String erro, String desdobr,
			Integer phirealizado, Short cspcnvcodigo, Byte cspseq) {
		super();
		this.cthseq = cthseq;
		this.prontuario = prontuario;
		this.nome = nome;
		this.leito = leito;
		this.dtIntAdm = dtIntAdm;
		this.dtAltAdm = altAdm;
		this.nroAih = nroAih;
		this.mspSia = mspSia;
		this.intseq = intseq;
		this.erro = erro;
		this.desdobr = desdobr;
		this.phirealizado = phirealizado;
		this.cspcnvcodigo = cspcnvcodigo;
		this.cspseq = cspseq;
	}

	public Integer getCthseq() {
		return cthseq;
	}

	public void setCthseq(Integer cthseq) {
		this.cthseq = cthseq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Integer getIntseq() {
		return intseq;
	}

	public void setIntseq(Integer intseq) {
		this.intseq = intseq;
	}

	public String getErro() {
		return erro;
	}

	public void setErro(String erro) {
		this.erro = erro;
	}

	public String getDesdobr() {
		return desdobr;
	}

	public void setDesdobr(String desdobr) {
		this.desdobr = desdobr;
	}

	public Integer getPhirealizado() {
		return phirealizado;
	}

	public void setPhirealizado(Integer phirealizado) {
		this.phirealizado = phirealizado;
	}

	public Short getCspcnvcodigo() {
		return cspcnvcodigo;
	}

	public void setCspcnvcodigo(Short cspcnvcodigo) {
		this.cspcnvcodigo = cspcnvcodigo;
	}

	public Byte getCspseq() {
		return cspseq;
	}

	public void setCspseq(Byte cspseq) {
		this.cspseq = cspseq;
	}

	public String getuErro() {
		return uErro;
	}

	public void setuErro(String uErro) {
		this.uErro = uErro;
	}

	public String getuLeito() {
		return uLeito;
	}

	public void setuLeito(String uLeito) {
		this.uLeito = uLeito;
	}

	public String getuNome() {
		return uNome;
	}

	public void setuNome(String uNome) {
		this.uNome = uNome;
	}

	public Date getDtIntAdm() {
		return dtIntAdm;
	}

	public void setDtIntAdm(Date dtIntAdm) {
		this.dtIntAdm = dtIntAdm;
	}

	public Date getDtAltAdm() {
		return dtAltAdm;
	}

	public void setDtAltAdm(Date dtAltAdm) {
		this.dtAltAdm = dtAltAdm;
	}

	public Long getNroAih() {
		return nroAih;
	}

	public void setNroAih(Long nroAih) {
		this.nroAih = nroAih;
	}

	public String getMspSia() {
		return mspSia;
	}

	public void setMspSia(String mspSia) {
		this.mspSia = mspSia;
	}	
}
