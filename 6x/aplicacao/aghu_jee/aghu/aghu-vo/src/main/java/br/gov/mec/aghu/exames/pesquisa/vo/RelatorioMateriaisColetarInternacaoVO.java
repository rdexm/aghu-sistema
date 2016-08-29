package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;
import java.util.Date;



public class RelatorioMateriaisColetarInternacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7940029308535122986L;
	private String descricao;
	private Integer andar;
	private String indAla2;
	private boolean recemNascido;
	private String informacoesClinicas;
	private String ltoLtoId;
	private String prontuario;
	private String nome;
	private String convenio;
	private Date criadoEm;
	private Integer soeAtdSeq;
	private String sitCodigo;
	private Short iseSeqp;
	private Date dthrProgramada;
	private Date dthrProgramadaOrd;
	private Integer soeSeq;
	private Short seqp;
	private String tempo;
	private String ind;
	private String descricao1;
	private String descricao2;
	private String sigla;
	private String descricaoUsual;
	private Integer ufeUnfSeq;
	private String tipoColeta;
	private String driverId;
	private String tipoRegistro;
	private Integer nroUnico;

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getAndar() {
		return andar;
	}
	public void setAndar(Integer andar) {
		this.andar = andar;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getConvenio() {
		return convenio;
	}
	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public String getTempo() {
		return tempo;
	}
	public void setTempo(String tempo) {
		this.tempo = tempo;
	}
	public String getInd() {
		return ind;
	}
	public void setInd(String ind) {
		this.ind = ind;
	}
	public String getDescricao1() {
		return descricao1;
	}
	public void setDescricao1(String descricao1) {
		this.descricao1 = descricao1;
	}
	public String getDescricao2() {
		return descricao2;
	}
	public void setDescricao2(String descricao2) {
		this.descricao2 = descricao2;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getIndAla2() {
		return indAla2;
	}
	public void setIndAla2(String indAla2) {
		this.indAla2 = indAla2;
	}
	public boolean isRecemNascido() {
		return recemNascido;
	}
	public void setRecemNascido(boolean recemNascido) {
		this.recemNascido = recemNascido;
	}
	public String getInformacoesClinicas() {
		return informacoesClinicas;
	}
	public void setInformacoesClinicas(String informacoesClinicas) {
		this.informacoesClinicas = informacoesClinicas;
	}
	public String getLtoLtoId() {
		return ltoLtoId;
	}
	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public Integer getSoeAtdSeq() {
		return soeAtdSeq;
	}
	public void setSoeAtdSeq(Integer soeAtdSeq) {
		this.soeAtdSeq = soeAtdSeq;
	}
	public String getSitCodigo() {
		return sitCodigo;
	}
	public void setSitCodigo(String sitCodigo) {
		this.sitCodigo = sitCodigo;
	}
	public Short getIseSeqp() {
		return iseSeqp;
	}
	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}
	public Date getDthrProgramada() {
		return dthrProgramada;
	}
	public void setDthrProgramada(Date dthrProgramada) {
		this.dthrProgramada = dthrProgramada;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public String getDescricaoUsual() {
		return descricaoUsual;
	}
	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}
	public Integer getUfeUnfSeq() {
		return ufeUnfSeq;
	}
	public void setUfeUnfSeq(Integer ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}
	public String getTipoColeta() {
		return tipoColeta;
	}
	public void setTipoColeta(String tipoColeta) {
		this.tipoColeta = tipoColeta;
	}
	public String getDriverId() {
		return driverId;
	}
	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}
	public String getTipoRegistro() {
		return tipoRegistro;
	}
	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}
	public Integer getNroUnico() {
		return nroUnico;
	}
	public void setNroUnico(Integer nroUnico) {
		this.nroUnico = nroUnico;
	}
	public Date getDthrProgramadaOrd() {
		return dthrProgramadaOrd;
	}
	public void setDthrProgramadaOrd(Date dthrProgramadaOrd) {
		this.dthrProgramadaOrd = dthrProgramadaOrd;
	}

}