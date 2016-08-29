package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoCompProd;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.FatCompetenciaProd;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class CompetenciaProducaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6210637914406719299L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@Inject @Paginator
	private DynamicDataModel<FatCompetenciaProd> dataModel;
	private List<FatCompetenciaProd> competenciasProducao = new ArrayList<FatCompetenciaProd>();
	private FatCompetenciaProd competenciaProducao;
	private Byte mes;
	private Short ano;
	private Date dthrInicioProd;
	private Date dthrFimProd;
	private DominioSituacaoCompProd indSituacao;
	private boolean editaDataHora;

	private enum CompetenciaProducaoPaginatorControllerExceptionCode implements BusinessExceptionCode {
		MES_INVALIDO, ANO_INVALIDO;
	}

	@PostConstruct
	public void iniciar() {
	 

		begin(conversation);
	
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		try {
			validaMes(this.mes);
			validaAno(this.ano);
			
			this.competenciasProducao = null;
			
			this.setCompetenciaProducao(new FatCompetenciaProd());
			this.setEditaDataHora(false);
			this.competenciaProducao.setMes(this.mes);
			this.competenciaProducao.setAno(this.ano);
			this.competenciaProducao.setDthrInicioProd(this.dthrInicioProd);
			this.competenciaProducao.setDthrFimProd(this.dthrFimProd);
			this.competenciaProducao.setIndSituacao(this.indSituacao);

			this.dataModel.reiniciarPaginator();						
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade.pesquisarFatCompetenciaProdCount(this.getCompetenciaProducao());
	}
	
	@Override
	public List<FatCompetenciaProd> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
	
		this.competenciasProducao = this.faturamentoFacade
				.pesquisarFatCompetenciaProd(this.competenciaProducao,
						firstResult, maxResult, orderProperty, asc);
		
		if (this.competenciasProducao == null) {
			this.competenciasProducao = new ArrayList<FatCompetenciaProd>();
		}
		
		return competenciasProducao;
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limpar() {
		this.setCompetenciaProducao(new FatCompetenciaProd());
		this.setCompetenciasProducao(new ArrayList<FatCompetenciaProd>());
		this.setMes(null);
		this.setAno(null);
		this.setDthrInicioProd(null);
		this.setDthrFimProd(null);
		this.setIndSituacao(null);
		this.dataModel.limparPesquisa();
	}

	public void habilitarEdicaoDataHora(FatCompetenciaProd competenciaProd) {
		if (!this.editaDataHora) {
			this.setEditaDataHora(true);
			this.setCompetenciaProducao(competenciaProd);
		}
	}

	public void gravar(FatCompetenciaProd competenciaProd) {
		try {
			this.faturamentoApoioFacade.gravarFatCompetenciaProd(competenciaProd, new Date());
			this.setEditaDataHora(false);
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZACAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelar() {
		this.setEditaDataHora(false);
	}

	public void gravarProducao() {
		try {		
			final Date dataFimVinculoServidor = new Date();
			this.faturamentoApoioFacade.gravarProducao(dataFimVinculoServidor);
			this.faturamentoApoioFacade.iniciarNovaCompetencia(dataFimVinculoServidor);
			this.faturamentoApoioFacade.atualizarCompetenciaProducao(dataFimVinculoServidor);
			pesquisar();
			 
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO");
		} catch (BaseException e) {
			apresentarMsgNegocio(Severity.ERROR, "GRAVAR_PRODUCAO_ERRO", e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "GRAVAR_PRODUCAO_ERRO", e);
		}
	}

	protected void validaMes(Byte mes) throws ApplicationBusinessException {
		if (this.mes != null) {
			if (this.mes < 1 || this.mes > 12) {
				throw new ApplicationBusinessException(CompetenciaProducaoPaginatorControllerExceptionCode.MES_INVALIDO);
			}
		}
	}

	protected void validaAno(Short ano) throws ApplicationBusinessException {
		if (this.ano != null) {
			if (this.ano < 1940 || this.ano > 2030) {
				throw new ApplicationBusinessException(CompetenciaProducaoPaginatorControllerExceptionCode.ANO_INVALIDO);
			}
		}
	}
	
	// getters & setters
	public FatCompetenciaProd getCompetenciaProducao() {
		return competenciaProducao;
	}

	public void setCompetenciaProducao(FatCompetenciaProd competenciaProducao) {
		this.competenciaProducao = competenciaProducao;
	}

	public Byte getMes() {
		return mes;
	}

	public void setMes(Byte mes) {
		this.mes = mes;
	}

	public Short getAno() {
		return ano;
	}

	public void setAno(Short ano) {
		this.ano = ano;
	}

	public Date getDthrInicioProd() {
		return dthrInicioProd;
	}

	public void setDthrInicioProd(Date dthrInicioProd) {
		this.dthrInicioProd = dthrInicioProd;
	}

	public Date getDthrFimProd() {
		return dthrFimProd;
	}

	public void setDthrFimProd(Date dthrFimProd) {
		this.dthrFimProd = dthrFimProd;
	}

	public DominioSituacaoCompProd getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoCompProd indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isEditaDataHora() {
		return editaDataHora;
	}

	public void setEditaDataHora(boolean editaDataHora) {
		this.editaDataHora = editaDataHora;
	}

	public List<FatCompetenciaProd> getCompetenciasProducao() {
		return competenciasProducao;
	}

	public void setCompetenciasProducao(
			List<FatCompetenciaProd> competenciasProducao) {
		this.competenciasProducao = competenciasProducao;
	}

	public DynamicDataModel<FatCompetenciaProd> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatCompetenciaProd> dataModel) {
		this.dataModel = dataModel;
	}

}