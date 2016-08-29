package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioLocalColeta;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;


public class RelatorioMateriaisColetarInternacaoFiltroVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2223880549322559841L;
	//novas 
	private Date dtHrExecucao = new Date();
	private AelSolicitacaoExames aelSolicitacaoExames;
	private AelSitItemSolicitacoes aelSitItemSolicitacoes;
	private AghUnidadesFuncionais unidadeColeta;
	private DominioSimNao indImpressaoEtiquetas = DominioSimNao.S;
	private AghUnidadesFuncionais unidadeFuncionalSolicitante;
	private DominioLocalColeta localColeta = DominioLocalColeta.NENHUM;
	private DominioTipoColeta tipoColeta;
	private ConstanteAghCaractUnidFuncionais caracteristica;
	private ImpImpressora impressora;

	private String indExecutaPlantao;
	private String cpParColeta;
	private String pSituacaoAmostra;
	private String cpParConvenio;
	
	private Date dataInicialPesquisa;
	private Date dataFinalPesquisa;
	private Date dataAgenda;
	private AelGradeAgendaExame gradeAgenda;
	
	public Date getDtHrExecucao() {
		return dtHrExecucao;
	}
	public void setDtHrExecucao(Date dtHrExecucao) {
		this.dtHrExecucao = dtHrExecucao;
	}
	public AelSolicitacaoExames getAelSolicitacaoExames() {
		return aelSolicitacaoExames;
	}
	public void setAelSolicitacaoExames(AelSolicitacaoExames aelSolicitacaoExames) {
		this.aelSolicitacaoExames = aelSolicitacaoExames;
	}
	public AelSitItemSolicitacoes getAelSitItemSolicitacoes() {
		return aelSitItemSolicitacoes;
	}
	public void setAelSitItemSolicitacoes(
			AelSitItemSolicitacoes aelSitItemSolicitacoes) {
		this.aelSitItemSolicitacoes = aelSitItemSolicitacoes;
	}
	
	public DominioSimNao getIndImpressaoEtiquetas() {
		return indImpressaoEtiquetas;
	}
	public void setIndImpressaoEtiquetas(DominioSimNao indImpressaoEtiquetas) {
		this.indImpressaoEtiquetas = indImpressaoEtiquetas;
	}
	public AghUnidadesFuncionais getUnidadeFuncionalSolicitante() {
		return unidadeFuncionalSolicitante;
	}
	public void setUnidadeFuncionalSolicitante(
			AghUnidadesFuncionais unidadeFuncionalSolicitante) {
		this.unidadeFuncionalSolicitante = unidadeFuncionalSolicitante;
	}
	public DominioLocalColeta getLocalColeta() {
		return localColeta;
	}
	public void setLocalColeta(DominioLocalColeta localColeta) {
		this.localColeta = localColeta;
	}
	public DominioTipoColeta getTipoColeta() {
		return tipoColeta;
	}
	public void setTipoColeta(DominioTipoColeta tipoColeta) {
		this.tipoColeta = tipoColeta;
	}
	public ConstanteAghCaractUnidFuncionais getCaracteristica() {
		return caracteristica;
	}
	public void setCaracteristica(ConstanteAghCaractUnidFuncionais caracteristica) {
		this.caracteristica = caracteristica;
	}
	public AghUnidadesFuncionais getUnidadeColeta() {
		return unidadeColeta;
	}
	public void setUnidadeColeta(AghUnidadesFuncionais unidadeColeta) {
		this.unidadeColeta = unidadeColeta;
	}
	public String getIndExecutaPlantao() {
		return indExecutaPlantao;
	}
	public void setIndExecutaPlantao(String indExecutaPlantao) {
		this.indExecutaPlantao = indExecutaPlantao;
	}
	public Date getDataInicialPesquisa() {
		return dataInicialPesquisa;
	}
	public void setDataInicialPesquisa(Date dataInicialPesquisa) {
		this.dataInicialPesquisa = dataInicialPesquisa;
	}
	public Date getDataFinalPesquisa() {
		return dataFinalPesquisa;
	}
	public void setDataFinalPesquisa(Date dataFinalPesquisa) {
		this.dataFinalPesquisa = dataFinalPesquisa;
	}
	public ImpImpressora getImpressora() {
		return impressora;
	}
	public void setImpressora(ImpImpressora impressora) {
		this.impressora = impressora;
	}
	public String getCpParColeta() {
		return cpParColeta;
	}
	public void setCpParColeta(String cpParColeta) {
		this.cpParColeta = cpParColeta;
	}
	public String getpSituacaoAmostra() {
		return pSituacaoAmostra;
	}
	public void setpSituacaoAmostra(String pSituacaoAmostra) {
		this.pSituacaoAmostra = pSituacaoAmostra;
	}
	public String getCpParConvenio() {
		return cpParConvenio;
	}
	public void setCpParConvenio(String cpParConvenio) {
		this.cpParConvenio = cpParConvenio;
	}
	public Date getDataAgenda() {
		return dataAgenda;
	}
	public void setDataAgenda(Date dataAgenda) {
		this.dataAgenda = dataAgenda;
	}
	public AelGradeAgendaExame getGradeAgenda() {
		return gradeAgenda;
	}
	public void setGradeAgenda(AelGradeAgendaExame gradeAgenda) {
		this.gradeAgenda = gradeAgenda;
	}
}