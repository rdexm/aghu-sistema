package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ConsultaRateioProfissionalVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioConsultaRateioProfissionalControllerPdf extends ActionReport {

	private static final long serialVersionUID = 621377442632559721L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioConsultaRateioProfissionalControllerPdf.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private FatCompetencia competencia;

	private Boolean isDirectPrint;
	
	@PostConstruct
	protected void init(){
		begin(conversation);
	}

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<ConsultaRateioProfissionalVO> colecao = new LinkedList<ConsultaRateioProfissionalVO>();

	public String iniciar() {
		if (isDirectPrint) {
			try {
				directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				getLog().error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			}
		}
		return null;
	}

	@Override
	public Collection<ConsultaRateioProfissionalVO> recuperarColecao() {
		try {
			colecao = faturamentoFacade.listarConsultaRateioServicosProfissionais(competencia);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		}
		return colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioConsultaRateioProfissional.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		final String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("NM_MES", CoreUtil.obterMesPorExtenso(competencia.getId().getMes()).toUpperCase());
		params.put("ANO", competencia.getId().getAno());

		return params;
	}
	

	public String voltar(){
		return "relatorioConsultaRateioProfissional";
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}
}