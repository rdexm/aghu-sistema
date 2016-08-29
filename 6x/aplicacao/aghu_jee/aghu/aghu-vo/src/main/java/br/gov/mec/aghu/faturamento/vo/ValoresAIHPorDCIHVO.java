package br.gov.mec.aghu.faturamento.vo;

import java.sql.Timestamp;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoCobrancaAih;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ValoresAIHPorDCIHVO {

	// dci.codigo_dcih dcih
	private String dcih;

	// decode(dci.tipo, 'n', 'normal', 'f', 'faec', 'c', 'aihs tipo 5') tipo
	private String tipo;

	// tcs.descricao tipo_dcih
	private String descricao;

	// cth.nro_aih aih
	private Long nroaih;

	// eai.pac_prontuario pront
	private Integer prontuario;

	// eai.iph_cod_sus_realiz proced
	private Integer procedimento;

	// cth.dt_alta_administrativa alta
	private Timestamp alta;

	// (trunc(cth.dt_alta_administrativa)-trunc(cth.dt_int_administrativa)) per
	private Date dtaltaadm;
	private Date dtintadm;

	// (cth.dias_uti_mes_inicial+cth.dias_uti_mes_anterior+cth.dias_uti_mes_alta) uti
	private Integer uti;

	// cth.diarias_acompanhante ac
	private Integer acomp;

	// decode(cth.valor_hemat,0,null,'h') h
	private Double hem;

	// cth.valor_sh + valor_sh_uti + valor_sh_utie + valor_sh_acomp ...
	private Double servhosp;

	// cth.valor_sp + valor_sp_uti + valor_sp_utie + valor_sp_acomp + ...
	private Double servprof;

	// cth.valor_sadt + valor_sadt_uti + valor_sadt_utie + valor_sadt_acomp + ...
	private Double sadt;

	// cth.valor_opm protese
	private Double protese;

	// eai.cth_seq
	private Integer cthseq;

	// eai.seqp
	private Integer eaiseqp;

	// decode(cth.cth_seq_reapresentada,null,' ','r') reapresentada
	private Integer reapresentada;
	
	private Integer iphcodsus;
	

	public Integer getPer() {
		return DateUtil.calcularDiasEntreDatas(getDtintadm(),getDtaltaadm());
	}

	public String getDcih() {
		return dcih;
	}

	public void setDcih(String dcih) {
		this.dcih = dcih;
	}

	public String getTipo() {
		if(tipo != null){
			return DominioTipoDocumentoCobrancaAih.valueOf(tipo).getDescricao().toUpperCase();
			
		} else {
			return tipo;
		}
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getNroaih() {
		return nroaih;
	}

	public void setNroaih(Long nroaih) {
		this.nroaih = nroaih;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(Integer procedimento) {
		this.procedimento = procedimento;
	}

	public Timestamp getAlta() {
		return alta;
	}

	public void setAlta(Timestamp alta) {
		this.alta = alta;
	}

	public Date getDtaltaadm() {
		return dtaltaadm;
	}

	public void setDtaltaadm(Date dtaltaadm) {
		this.dtaltaadm = dtaltaadm;
	}

	public Date getDtintadm() {
		return dtintadm;
	}

	public void setDtintadm(Date dtintadm) {
		this.dtintadm = dtintadm;
	}

	public Integer getUti() {
		return uti;
	}

	public void setUti(Integer uti) {
		this.uti = uti;
	}

	public Integer getAcomp() {
		return acomp;
	}

	public void setAcomp(Integer acomp) {
		this.acomp = acomp;
	}

	public Double getHem() {
		return hem;
	}

	public void setHem(Double hem) {
		this.hem = hem;
	}

	public Double getServhosp() {
		return servhosp;
	}

	public void setServhosp(Double servhosp) {
		this.servhosp = servhosp;
	}

	public Double getServprof() {
		return servprof;
	}

	public void setServprof(Double servprof) {
		this.servprof = servprof;
	}

	public Double getSadt() {
		return sadt;
	}

	public void setSadt(Double sadt) {
		this.sadt = sadt;
	}

	public Double getProtese() {
		return protese;
	}

	public void setProtese(Double protese) {
		this.protese = protese;
	}

	public Integer getCthseq() {
		return cthseq;
	}

	public void setCthseq(Integer cthseq) {
		this.cthseq = cthseq;
	}

	public Integer getEaiseqp() {
		return eaiseqp;
	}

	public void setEaiseqp(Integer eaiseqp) {
		this.eaiseqp = eaiseqp;
	}

	public Integer getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Integer reapresentada) {
		this.reapresentada = reapresentada;
	}

	public Integer getIphcodsus() {
		return iphcodsus;
	}

	public void setIphcodsus(Integer iphcodsus) {
		this.iphcodsus = iphcodsus;
	}
}