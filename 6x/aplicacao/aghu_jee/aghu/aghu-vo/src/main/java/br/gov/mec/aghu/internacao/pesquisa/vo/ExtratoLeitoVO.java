package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ExtratoLeitoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9009546846850581256L;
	private Date criadoEm;
	private Date dthrLancamento;
	private String tipoMovtoDescricao;
	private String nome;
	private Integer prontuario;
	private String convenioDescricao;
	private String servidorResponsalvel;
	
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getDthrLancamento() {
		return dthrLancamento;
	}

	public void setDthrLancamento(Date dthrLancamento) {
		this.dthrLancamento = dthrLancamento;
	}

	public String getTipoMovtoDescricao() {
		return tipoMovtoDescricao;
	}

	public void setTipoMovtoDescricao(String tipoMovtoDescricao) {
		this.tipoMovtoDescricao = tipoMovtoDescricao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getConvenioDescricao() {
		return convenioDescricao;
	}

	public void setConvenioDescricao(String convenioDescricao) {
		this.convenioDescricao = convenioDescricao;
	}

	public String getServidorResponsalvel() {
		return servidorResponsalvel;
	}

	public void setServidorResponsalvel(String servidorResponsalvel) {
		this.servidorResponsalvel = servidorResponsalvel;
	}	

}
