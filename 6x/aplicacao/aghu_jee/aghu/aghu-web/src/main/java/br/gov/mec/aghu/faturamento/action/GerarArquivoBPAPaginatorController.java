package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatArqEspelhoProcedAmbVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class GerarArquivoBPAPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4907816937689665020L;
	
	private static final Log LOG = LogFactory.getLog(GerarArquivoBPAPaginatorController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private FatArqEspelhoProcedAmbVO selected;

	public FatArqEspelhoProcedAmbVO getSelected() {
		return selected;
	}

	public void setSelected(FatArqEspelhoProcedAmbVO selected) {
		this.selected = selected;
	}

	@Inject @Paginator
	private DynamicDataModel<FatArqEspelhoProcedAmbVO> dataModel;

	// Suggestions
	private FatCompetencia competencia;

	private Boolean gerouArquivo;

	private String fileName;

	private Integer tciSeq;

	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	public enum GerarArquivoBPAPaginatorControllerExceptionCode implements BusinessExceptionCode {
		LABEL_FAT_TIPO_CARACT_ITENS_INVALIDA
	}

	public void pesquisar() {
		if (competencia == null) {
			apresentarMsgNegocio(Severity.ERROR, "TITLE_COMPETENCIA_PESQUISA_INVALIDA");
		} else {
			this.dataModel.reiniciarPaginator();
		}
	}

	public void limpar() {
		competencia = null;
		gerouArquivo = false;
		fileName = null;
		this.dataModel.limparPesquisa();
	}

	public DynamicDataModel<FatArqEspelhoProcedAmbVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatArqEspelhoProcedAmbVO> dataModel) {
		this.dataModel = dataModel;
	}

	private void criarParametroTciSeq() throws ApplicationBusinessException {
		final AghParametros tctSeq = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TCT_SEQ_DESCONSIDERAR_ITEM_NO_ARQUIVO);
		final List<FatTipoCaractItens> tcis = faturamentoFacade.listarTipoCaractItensPorCaracteristica(tctSeq.getVlrTexto());

		if (tcis != null && !tcis.isEmpty() && tcis.size() == 1) {
			FatTipoCaractItens tci = tcis.get(0);
			tciSeq = tci.getSeq();
		} else {
			throw new ApplicationBusinessException(GerarArquivoBPAPaginatorControllerExceptionCode.LABEL_FAT_TIPO_CARACT_ITENS_INVALIDA);
		}
	}

	public void gerarArquivoBPADataSus() {
		try {
			if (tciSeq == null) {
				criarParametroTciSeq();
			}
			fileName = faturamentoFacade.gerarArquivoBPADataSus(competencia, 1L, tciSeq);
			gerouArquivo = true;
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_BPA_DATA_SUS, e,
					e.getLocalizedMessage()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload() {
		if (fileName != null) {
			try {
				String name = "BPA"
						+ (competencia.getId().getMes() < 10 ? "0" + competencia.getId().getMes() : competencia.getId().getMes())
						+ (competencia.getId().getAno().toString().substring(2)) + ".TXT";
				download(fileName, name);
				gerouArquivo = false;
				fileName = null;

			} catch (IOException e) {
				getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_BPA_DATA_SUS, e,
						e.getLocalizedMessage()));
			}
		}
	}

	@Override
	public Long recuperarCount() {
		Integer fatArqEspelhoProcedAmbVOCount = faturamentoFacade.listarFatArqEspelhoProcedAmbVOCount(competencia);
		if (fatArqEspelhoProcedAmbVOCount != null) {
			return fatArqEspelhoProcedAmbVOCount.longValue();
		} else {
			return 0L;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FatArqEspelhoProcedAmbVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return faturamentoFacade.listarFatArqEspelhoProcedAmbVO(competencia, firstResult, maxResult, orderProperty, asc);
	}

	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB)),pesquisarCompetenciasCount(objPesquisa));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<FatCompetencia>(0);
		}
	}

	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return 0L;
		}
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getTciSeq() {
		return tciSeq;
	}

	public void setTciSeq(Integer tciSeq) {
		this.tciSeq = tciSeq;
	}
}