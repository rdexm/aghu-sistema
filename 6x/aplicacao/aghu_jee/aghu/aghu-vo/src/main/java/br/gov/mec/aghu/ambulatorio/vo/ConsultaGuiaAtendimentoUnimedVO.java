package br.gov.mec.aghu.ambulatorio.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntity;


public class ConsultaGuiaAtendimentoUnimedVO implements BaseEntity {
	
	private static final long serialVersionUID = 6110943905466414553L;
	
	/*
	 * Atributos da consulta C1
	 */
	
	// conv conv
	private Integer codAns;						// conv.cod_ans
	
	// proc_efet a
	private Long numeroGuia;					// a.numero_guia
	private Date data;							// a.data
	private BigDecimal qtde;					// a.qtde
	private BigDecimal qtdeCsh;					// a.qtde_csh
	private String indDiscriminaDesconto;		// a.ind_discrimina_desconto
	private Float valorDesconto;				// a.valor_desconto
	private Short viasAccs;						// a.vias_accs
	private Integer prhcCodHcpa;				// a.prhc_cod_hcpa
	private Short tpconvTpitCod;				// a.tpconv_tpit_cod
	
	// aac_consultas con
	private Integer matriculaConsultado;		// con.ser_matricula_consultado
	private Short vinCodigoConsultado;			// con.ser_vin_codigo_consultado

	// aip_pacientes pac
	private Integer prontuario;					// pac.prontuario
	private String nomePaciente;				// pac.nome nome_pac
	
	// pac_intd_conv intd
	private Integer codPrnt;					// intd.cod_prnt
	private String siglaEsp;					// intd.sgla_esp
		
	// cnta_conv cta
	private Integer atdSeq;						// cta.atd_seq
	private Integer mexSeq;						// cta.mex_seq
	private Short cspCnvCodigo;					// cta.csp_cnv_codigo
	private Byte cspSeq;						// cta.csp_seq
	private Integer nro;						// cta.nro
 
	// tipo_tab_pgto tptab
	private String tipoTab;						// tptab.tipo_tab
	private String tabela;						// tptab.dscr
	
	// tab_pgto b
	private Long codTabPgto;					// b.cod
	private String dscrTabPgto;					// b.dscr
	private BigDecimal qtdeCshProf;				// b.qtde_csh_prof
	private BigDecimal qtdeM2;					// b.qtde_m2

	// valr_tab_pgto_x_comp y
	private BigDecimal qtdeChMatTabPgtoComp;	// y.qtde_ch_mat
	private BigDecimal qtdeCshTabPgtoComp;		// y.qtde_csh
	
	// valor_ch_conv_plano flm
	private BigDecimal flmValorConvPlano;		// flm.valor

	// valor_ch_conv_plano h
	private BigDecimal hValorConvPlano;			// h.valor

	// fat_conv_saude_planos plano
	private String descricaoPlano; 				// plano.descricao
	
	// conv cnoper
	private Integer codCnesExec;				// cnoper.cod_ans

	
	/*
	 * Getters e setters
	 */
	
	public Integer getCodAns() {
		return codAns;
	}

	public void setCodAns(Integer codAns) {
		this.codAns = codAns;
	}

	public Long getNumeroGuia() {
		return numeroGuia;
	}

	public void setNumeroGuia(Long numeroGuia) {
		this.numeroGuia = numeroGuia;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public BigDecimal getQtde() {
		return qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}

	public BigDecimal getQtdeCsh() {
		return qtdeCsh;
	}

	public void setQtdeCsh(BigDecimal qtdeCsh) {
		this.qtdeCsh = qtdeCsh;
	}

	public String getIndDiscriminaDesconto() {
		return indDiscriminaDesconto;
	}

	public void setIndDiscriminaDesconto(String indDiscriminaDesconto) {
		this.indDiscriminaDesconto = indDiscriminaDesconto;
	}

	public Float getValorDesconto() {
		return valorDesconto;
	}

	public void setValorDesconto(Float valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public Short getViasAccs() {
		return viasAccs;
	}

	public void setViasAccs(Short viasAccs) {
		this.viasAccs = viasAccs;
	}

	public Integer getMatriculaConsultado() {
		return matriculaConsultado;
	}

	public void setMatriculaConsultado(Integer matriculaConsultado) {
		this.matriculaConsultado = matriculaConsultado;
	}

	public Short getVinCodigoConsultado() {
		return vinCodigoConsultado;
	}

	public void setVinCodigoConsultado(Short vinCodigoConsultado) {
		this.vinCodigoConsultado = vinCodigoConsultado;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getCodPrnt() {
		return codPrnt;
	}

	public void setCodPrnt(Integer codPrnt) {
		this.codPrnt = codPrnt;
	}

	public String getSiglaEsp() {
		return siglaEsp;
	}

	public void setSiglaEsp(String siglaEsp) {
		this.siglaEsp = siglaEsp;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getMexSeq() {
		return mexSeq;
	}

	public void setMexSeq(Integer mexSeq) {
		this.mexSeq = mexSeq;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Byte getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	public Integer getNro() {
		return nro;
	}

	public void setNro(Integer nro) {
		this.nro = nro;
	}

	public String getTipoTab() {
		return tipoTab;
	}

	public void setTipoTab(String tipoTab) {
		this.tipoTab = tipoTab;
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public Long getCodTabPgto() {
		return codTabPgto;
	}

	public void setCodTabPgto(Long codTabPgto) {
		this.codTabPgto = codTabPgto;
	}

	public String getDscrTabPgto() {
		return dscrTabPgto;
	}

	public void setDscrTabPgto(String dscrTabPgto) {
		this.dscrTabPgto = dscrTabPgto;
	}

	public BigDecimal getQtdeCshProf() {
		return qtdeCshProf;
	}

	public void setQtdeCshProf(BigDecimal qtdeCshProf) {
		this.qtdeCshProf = qtdeCshProf;
	}

	public BigDecimal getQtdeM2() {
		return qtdeM2;
	}

	public void setQtdeM2(BigDecimal qtdeM2) {
		this.qtdeM2 = qtdeM2;
	}

	public BigDecimal getQtdeChMatTabPgtoComp() {
		return qtdeChMatTabPgtoComp;
	}

	public void setQtdeChMatTabPgtoComp(BigDecimal qtdeChMatTabPgtoComp) {
		this.qtdeChMatTabPgtoComp = qtdeChMatTabPgtoComp;
	}

	public BigDecimal getQtdeCshTabPgtoComp() {
		return qtdeCshTabPgtoComp;
	}

	public void setQtdeCshTabPgtoComp(BigDecimal qtdeCshTabPgtoComp) {
		this.qtdeCshTabPgtoComp = qtdeCshTabPgtoComp;
	}

	public BigDecimal getFlmValorConvPlano() {
		return flmValorConvPlano;
	}

	public void setFlmValorConvPlano(BigDecimal flmValorConvPlano) {
		this.flmValorConvPlano = flmValorConvPlano;
	}

	public BigDecimal gethValorConvPlano() {
		return hValorConvPlano;
	}

	public void sethValorConvPlano(BigDecimal hValorConvPlano) {
		this.hValorConvPlano = hValorConvPlano;
	}

	public String getDescricaoPlano() {
		return descricaoPlano;
	}

	public void setDescricaoPlano(String descricaoPlano) {
		this.descricaoPlano = descricaoPlano;
	}

	public Integer getCodCnesExec() {
		return codCnesExec;
	}

	public void setCodCnesExec(Integer codCnesExec) {
		this.codCnesExec = codCnesExec;
	}
	
	public Integer getPrhcCodHcpa() {
		return prhcCodHcpa;
	}

	public void setPrhcCodHcpa(Integer prhcCodHcpa) {
		this.prhcCodHcpa = prhcCodHcpa;
	}

	public Short getTpconvTpitCod() {
		return tpconvTpitCod;
	}

	public void setTpconvTpitCod(Short tpconvTpitCod) {
		this.tpconvTpitCod = tpconvTpitCod;
	}
	
	/*
	 * Métodos utilitários
	 */

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCodAns());
		umHashCodeBuilder.append(this.getNumeroGuia());
		umHashCodeBuilder.append(this.getData());
		umHashCodeBuilder.append(this.getQtde());
		umHashCodeBuilder.append(this.getQtdeCsh());
		umHashCodeBuilder.append(this.getIndDiscriminaDesconto());
		umHashCodeBuilder.append(this.getValorDesconto());
		umHashCodeBuilder.append(this.getViasAccs());
		umHashCodeBuilder.append(this.getMatriculaConsultado());
		umHashCodeBuilder.append(this.getVinCodigoConsultado());
		umHashCodeBuilder.append(this.getProntuario());
		umHashCodeBuilder.append(this.getNomePaciente());
		umHashCodeBuilder.append(this.getCodPrnt());
		umHashCodeBuilder.append(this.getSiglaEsp());
		umHashCodeBuilder.append(this.getAtdSeq());
		umHashCodeBuilder.append(this.getMexSeq());
		umHashCodeBuilder.append(this.getCspCnvCodigo());
		umHashCodeBuilder.append(this.getCspSeq());
		umHashCodeBuilder.append(this.getNro());
		umHashCodeBuilder.append(this.getTipoTab());
		umHashCodeBuilder.append(this.getTabela());
		umHashCodeBuilder.append(this.getCodTabPgto());
		umHashCodeBuilder.append(this.getDscrTabPgto());
		umHashCodeBuilder.append(this.getQtdeCshProf());
		umHashCodeBuilder.append(this.getQtdeM2());
		umHashCodeBuilder.append(this.getQtdeChMatTabPgtoComp());
		umHashCodeBuilder.append(this.getQtdeCshTabPgtoComp());
		umHashCodeBuilder.append(this.getFlmValorConvPlano());
		umHashCodeBuilder.append(this.gethValorConvPlano());
		umHashCodeBuilder.append(this.getDescricaoPlano());
		umHashCodeBuilder.append(this.getPrhcCodHcpa());
		umHashCodeBuilder.append(this.getTpconvTpitCod());
		
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
		if (!(obj instanceof ConsultaGuiaAtendimentoUnimedVO)) {
			return false;
		}
		ConsultaGuiaAtendimentoUnimedVO other = (ConsultaGuiaAtendimentoUnimedVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCodAns(), other.getCodAns());
		umEqualsBuilder.append(this.getNumeroGuia(), other.getNumeroGuia());
		umEqualsBuilder.append(this.getData(), other.getData());
		umEqualsBuilder.append(this.getQtde(), other.getQtde());
		umEqualsBuilder.append(this.getQtdeCsh(), other.getQtdeCsh());
		umEqualsBuilder.append(this.getIndDiscriminaDesconto(), other.getIndDiscriminaDesconto());
		umEqualsBuilder.append(this.getValorDesconto(), other.getValorDesconto());
		umEqualsBuilder.append(this.getViasAccs(), other.getViasAccs());
		umEqualsBuilder.append(this.getMatriculaConsultado(), other.getMatriculaConsultado());
		umEqualsBuilder.append(this.getVinCodigoConsultado(), other.getVinCodigoConsultado());		
		umEqualsBuilder.append(this.getProntuario(), other.getProntuario());
		umEqualsBuilder.append(this.getNomePaciente(), other.getNomePaciente());
		umEqualsBuilder.append(this.getCodPrnt(), other.getCodPrnt());
		umEqualsBuilder.append(this.getSiglaEsp(), other.getSiglaEsp());
		umEqualsBuilder.append(this.getAtdSeq(), other.getAtdSeq());
		umEqualsBuilder.append(this.getMexSeq(), other.getMexSeq());
		umEqualsBuilder.append(this.getCspCnvCodigo(), other.getCspCnvCodigo());
		umEqualsBuilder.append(this.getCspSeq(), other.getCspSeq());
		umEqualsBuilder.append(this.getNro(), other.getNro());
		umEqualsBuilder.append(this.getTipoTab(), other.getTipoTab());		
		umEqualsBuilder.append(this.getTabela(), other.getTabela());
		umEqualsBuilder.append(this.getCodTabPgto(), other.getCodTabPgto());
		umEqualsBuilder.append(this.getDscrTabPgto(), other.getDscrTabPgto());
		umEqualsBuilder.append(this.getQtdeCshProf(), other.getQtdeCshProf());
		umEqualsBuilder.append(this.getQtdeM2(), other.getQtdeM2());
		umEqualsBuilder.append(this.getQtdeChMatTabPgtoComp(), other.getQtdeChMatTabPgtoComp());
		umEqualsBuilder.append(this.getQtdeCshTabPgtoComp(), other.getQtdeCshTabPgtoComp());
		umEqualsBuilder.append(this.getFlmValorConvPlano(), other.getFlmValorConvPlano());
		umEqualsBuilder.append(this.gethValorConvPlano(), other.gethValorConvPlano());
		umEqualsBuilder.append(this.getDescricaoPlano(), other.getDescricaoPlano());
		umEqualsBuilder.append(this.getPrhcCodHcpa(), other.getPrhcCodHcpa());
		umEqualsBuilder.append(this.getTpconvTpitCod(), other.getTpconvTpitCod());
				
		return umEqualsBuilder.isEquals();
	}
}