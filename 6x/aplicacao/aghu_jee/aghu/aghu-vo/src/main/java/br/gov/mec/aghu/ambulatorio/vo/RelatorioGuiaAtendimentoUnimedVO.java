package br.gov.mec.aghu.ambulatorio.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntity;


public class RelatorioGuiaAtendimentoUnimedVO implements BaseEntity {

	private static final long serialVersionUID = 9128835777665985682L;
	
	private String convenio;
	private String guia;
	private String codAns;
	private String dataAut;
	private String senha;
	private String dataEmis;
	private String matr;
	private String plano;
	private String nomePac;
	private String cartaoSus;
	private String codigoOperadora;
	private String nomeProf;
	private String conselho;
	private String nroConselho;
	private String uf;	
	private String operadora;
	private String tlExec;
	private String logrExec;
	private String cidadeExec;
	private String ufExec;
	private String codIbgeExec;
	private String cepExec;
	private String codCnesExec;
	private String tipoAtend;
	private String saida;
    private String somaVlrTot;
    private String qtdeCsh;
    private String contratado;
    
    private List<RelatorioGuiaAtendimentoUnimedSubProced1VO> subRelatorioProcedimentos1 = new ArrayList<RelatorioGuiaAtendimentoUnimedSubProced1VO>();
    private List<RelatorioGuiaAtendimentoUnimedSubProced2VO> subRelatorioProcedimentos2 = new ArrayList<RelatorioGuiaAtendimentoUnimedSubProced2VO>();
	
	/*
	 * Getters e Setters
	 */
    public String getConvenio() {
		return convenio;
	}

	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}

	public String getGuia() {
		return guia;
	}

	public void setGuia(String guia) {
		this.guia = guia;
	}

	public String getCodAns() {
		return codAns;
	}

	public void setCodAns(String codAns) {
		this.codAns = codAns;
	}

	public String getDataAut() {
		return dataAut;
	}

	public void setDataAut(String dataAut) {
		this.dataAut = dataAut;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getDataEmis() {
		return dataEmis;
	}

	public void setDataEmis(String dataEmis) {
		this.dataEmis = dataEmis;
	}

	public String getMatr() {
		return matr;
	}

	public void setMatr(String matr) {
		this.matr = matr;
	}

	public String getPlano() {
		return plano;
	}

	public void setPlano(String plano) {
		this.plano = plano;
	}

	public String getNomePac() {
		return nomePac;
	}

	public void setNomePac(String nomePac) {
		this.nomePac = nomePac;
	}

	public String getCartaoSus() {
		return cartaoSus;
	}

	public void setCartaoSus(String cartaoSus) {
		this.cartaoSus = cartaoSus;
	}

	public String getCodigoOperadora() {
		return codigoOperadora;
	}

	public void setCodigoOperadora(String codigoOperadora) {
		this.codigoOperadora = codigoOperadora;
	}

	public String getNomeProf() {
		return nomeProf;
	}

	public void setNomeProf(String nomeProf) {
		this.nomeProf = nomeProf;
	}

	public String getConselho() {
		return conselho;
	}

	public void setConselho(String conselho) {
		this.conselho = conselho;
	}

	public String getNroConselho() {
		return nroConselho;
	}

	public void setNroConselho(String nroConselho) {
		this.nroConselho = nroConselho;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getOperadora() {
		return operadora;
	}

	public void setOperadora(String operadora) {
		this.operadora = operadora;
	}

	public String getTlExec() {
		return tlExec;
	}

	public void setTlExec(String tlExec) {
		this.tlExec = tlExec;
	}

	public String getLogrExec() {
		return logrExec;
	}

	public void setLogrExec(String logrExec) {
		this.logrExec = logrExec;
	}

	public String getCidadeExec() {
		return cidadeExec;
	}

	public void setCidadeExec(String cidadeExec) {
		this.cidadeExec = cidadeExec;
	}

	public String getUfExec() {
		return ufExec;
	}

	public void setUfExec(String ufExec) {
		this.ufExec = ufExec;
	}

	public String getCodIbgeExec() {
		return codIbgeExec;
	}

	public void setCodIbgeExec(String codIbgeExec) {
		this.codIbgeExec = codIbgeExec;
	}

	public String getCepExec() {
		return cepExec;
	}

	public void setCepExec(String cepExec) {
		this.cepExec = cepExec;
	}

	public String getCodCnesExec() {
		return codCnesExec;
	}

	public void setCodCnesExec(String codCnesExec) {
		this.codCnesExec = codCnesExec;
	}

	public String getTipoAtend() {
		return tipoAtend;
	}

	public void setTipoAtend(String tipoAtend) {
		this.tipoAtend = tipoAtend;
	}

	public String getSaida() {
		return saida;
	}

	public void setSaida(String saida) {
		this.saida = saida;
	}

	public String getSomaVlrTot() {
		return somaVlrTot;
	}

	public void setSomaVlrTot(String somaVlrTot) {
		this.somaVlrTot = somaVlrTot;
	}

	public String getQtdeCsh() {
		return qtdeCsh;
	}

	public void setQtdeCsh(String qtdeCsh) {
		this.qtdeCsh = qtdeCsh;
	}

	public String getContratado() {
		return contratado;
	}

	public void setContratado(String contratado) {
		this.contratado = contratado;
	}

	public List<RelatorioGuiaAtendimentoUnimedSubProced1VO> getSubRelatorioProcedimentos1() {
		return subRelatorioProcedimentos1;
	}

	public void setSubRelatorioProcedimentos1(
			List<RelatorioGuiaAtendimentoUnimedSubProced1VO> subRelatorioProcedimentos1) {
		this.subRelatorioProcedimentos1 = subRelatorioProcedimentos1;
	}

	public List<RelatorioGuiaAtendimentoUnimedSubProced2VO> getSubRelatorioProcedimentos2() {
		return subRelatorioProcedimentos2;
	}

	public void setSubRelatorioProcedimentos2(
			List<RelatorioGuiaAtendimentoUnimedSubProced2VO> subRelatorioProcedimentos2) {
		this.subRelatorioProcedimentos2 = subRelatorioProcedimentos2;
	}		
	
	/*
	 * Métodos utilitários
	 */

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getConvenio());
		umHashCodeBuilder.append(this.getGuia());
		umHashCodeBuilder.append(this.getCodAns());
		umHashCodeBuilder.append(this.getDataAut());
		umHashCodeBuilder.append(this.getSenha());
		umHashCodeBuilder.append(this.getDataEmis());
		umHashCodeBuilder.append(this.getMatr());
		umHashCodeBuilder.append(this.getPlano());
		umHashCodeBuilder.append(this.getNomePac());
		umHashCodeBuilder.append(this.getCartaoSus());
		umHashCodeBuilder.append(this.getCodigoOperadora());
		umHashCodeBuilder.append(this.getNomeProf());
		umHashCodeBuilder.append(this.getConselho());
		umHashCodeBuilder.append(this.getNroConselho());
		umHashCodeBuilder.append(this.getUf());
		umHashCodeBuilder.append(this.getOperadora());
		umHashCodeBuilder.append(this.getTlExec());
		umHashCodeBuilder.append(this.getLogrExec());
		umHashCodeBuilder.append(this.getCidadeExec());
		umHashCodeBuilder.append(this.getUfExec());
		umHashCodeBuilder.append(this.getCodIbgeExec());
		umHashCodeBuilder.append(this.getCepExec());
		umHashCodeBuilder.append(this.getCodCnesExec());
		umHashCodeBuilder.append(this.getTipoAtend());
		umHashCodeBuilder.append(this.getSaida());
		umHashCodeBuilder.append(this.getSomaVlrTot());
		umHashCodeBuilder.append(this.getQtdeCsh());
		umHashCodeBuilder.append(this.getContratado());
		umHashCodeBuilder.append(this.getSubRelatorioProcedimentos1());
		umHashCodeBuilder.append(this.getSubRelatorioProcedimentos2());
		
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
		if (!(obj instanceof RelatorioGuiaAtendimentoUnimedVO)) {
			return false;
		}
		RelatorioGuiaAtendimentoUnimedVO other = (RelatorioGuiaAtendimentoUnimedVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getConvenio(), other.getConvenio());
		umEqualsBuilder.append(this.getGuia(), other.getGuia());
		umEqualsBuilder.append(this.getCodAns(), other.getCodAns());
		umEqualsBuilder.append(this.getDataAut(), other.getDataAut());
		umEqualsBuilder.append(this.getSenha(), other.getSenha());
		umEqualsBuilder.append(this.getDataEmis(), other.getDataEmis());
		umEqualsBuilder.append(this.getMatr(), other.getMatr());
		umEqualsBuilder.append(this.getPlano(), other.getPlano());
		umEqualsBuilder.append(this.getNomePac(), other.getNomePac());
		umEqualsBuilder.append(this.getCartaoSus(), other.getCartaoSus());
		umEqualsBuilder.append(this.getCodigoOperadora(), other.getCodigoOperadora());
		umEqualsBuilder.append(this.getNomeProf(), other.getNomeProf());
		umEqualsBuilder.append(this.getConselho(), other.getConselho());
		umEqualsBuilder.append(this.getNroConselho(), other.getNroConselho());
		umEqualsBuilder.append(this.getUf(), other.getUf());
		umEqualsBuilder.append(this.getOperadora(), other.getOperadora());
		umEqualsBuilder.append(this.getTlExec(), other.getTlExec());
		umEqualsBuilder.append(this.getLogrExec(), other.getLogrExec());
		umEqualsBuilder.append(this.getCidadeExec(), other.getCidadeExec());
		umEqualsBuilder.append(this.getUfExec(), other.getUfExec());
		umEqualsBuilder.append(this.getCodIbgeExec(), other.getCodIbgeExec());
		umEqualsBuilder.append(this.getCepExec(), other.getCepExec());
		umEqualsBuilder.append(this.getCodCnesExec(), other.getCodCnesExec());
		umEqualsBuilder.append(this.getTipoAtend(), other.getTipoAtend());
		umEqualsBuilder.append(this.getSaida(), other.getSaida());
		umEqualsBuilder.append(this.getSomaVlrTot(), other.getSomaVlrTot());
		umEqualsBuilder.append(this.getQtdeCsh(), other.getQtdeCsh());
		umEqualsBuilder.append(this.getContratado(), other.getContratado());
		umEqualsBuilder.append(this.getSubRelatorioProcedimentos1(), other.getSubRelatorioProcedimentos1());
		umEqualsBuilder.append(this.getSubRelatorioProcedimentos2(), other.getSubRelatorioProcedimentos2());
				
		return umEqualsBuilder.isEquals();
	}
	
}