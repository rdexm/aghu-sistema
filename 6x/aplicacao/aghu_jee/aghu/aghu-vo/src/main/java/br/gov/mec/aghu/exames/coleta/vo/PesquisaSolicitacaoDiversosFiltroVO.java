package br.gov.mec.aghu.exames.coleta.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AbsCandidatosDoadores;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;

/**
 * 
 * @author fpalma
 *
 */
public class PesquisaSolicitacaoDiversosFiltroVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1059280241562502253L;

	private AelProjetoPesquisas projetoPesquisa;
	private AelLaboratorioExternos laboratorioExterno;
	private AelCadCtrlQualidades controleQualidade;
	private AelDadosCadaveres cadaver;
	private AbsCandidatosDoadores doador;
	private Integer codPaciente;
	private Integer solicitacao;
	private Long numeroAp;
	private AelConfigExLaudoUnico configExLaudoUnico;
	private AelSitItemSolicitacoes situacao;
	private DominioSimNao mostraExamesCancelados;
	private DominioSimNao somenteLaboratorial;
	private DominioSimNao impressoLaudo;
	private Date dtInicio;
	private Date dtFinal;
	
	public AelProjetoPesquisas getProjetoPesquisa() {
		return projetoPesquisa;
	}
	public void setProjetoPesquisa(AelProjetoPesquisas projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}
	public AelLaboratorioExternos getLaboratorioExterno() {
		return laboratorioExterno;
	}
	public void setLaboratorioExterno(AelLaboratorioExternos laboratorioExterno) {
		this.laboratorioExterno = laboratorioExterno;
	}
	public AelCadCtrlQualidades getControleQualidade() {
		return controleQualidade;
	}
	public void setControleQualidade(AelCadCtrlQualidades controleQualidade) {
		this.controleQualidade = controleQualidade;
	}
	public AelDadosCadaveres getCadaver() {
		return cadaver;
	}
	public void setCadaver(AelDadosCadaveres cadaver) {
		this.cadaver = cadaver;
	}
	public AbsCandidatosDoadores getDoador() {
		return doador;
	}
	public void setDoador(AbsCandidatosDoadores doador) {
		this.doador = doador;
	}
	public Integer getCodPaciente() {
		return codPaciente;
	}
	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}
	public Integer getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}
	public Long getNumeroAp() {
		return numeroAp;
	}
	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}
	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}
	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}
	public DominioSimNao getMostraExamesCancelados() {
		return mostraExamesCancelados;
	}
	public void setMostraExamesCancelados(DominioSimNao mostraExamesCancelados) {
		this.mostraExamesCancelados = mostraExamesCancelados;
	}
	public DominioSimNao getSomenteLaboratorial() {
		return somenteLaboratorial;
	}
	public void setSomenteLaboratorial(DominioSimNao somenteLaboratorial) {
		this.somenteLaboratorial = somenteLaboratorial;
	}
	public DominioSimNao getImpressoLaudo() {
		return impressoLaudo;
	}
	public void setImpressoLaudo(DominioSimNao impressoLaudo) {
		this.impressoLaudo = impressoLaudo;
	}
	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	public Date getDtInicio() {
		return dtInicio;
	}
	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}
	public Date getDtFinal() {
		return dtFinal;
	}
	public AelConfigExLaudoUnico getConfigExLaudoUnico() {
		return configExLaudoUnico;
	}
	public void setConfigExLaudoUnico(AelConfigExLaudoUnico configExLaudoUnico) {
		this.configExLaudoUnico = configExLaudoUnico;
	}
	
}
	
