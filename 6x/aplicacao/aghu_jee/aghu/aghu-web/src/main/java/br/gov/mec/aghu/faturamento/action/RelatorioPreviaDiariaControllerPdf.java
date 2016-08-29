package br.gov.mec.aghu.faturamento.action;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.PreviaDiariaFaturamentoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioPreviaDiariaControllerPdf extends ActionReport {

	private static final long serialVersionUID = 621377442632559721L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioPreviaDiariaControllerPdf.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatCompetencia competencia;

	private Boolean isDirectPrint;

	@PostConstruct
	protected void init() {
		begin(conversation);
	}

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
	public Collection<PreviaDiariaFaturamentoVO> recuperarColecao() {
		try {
			return faturamentoFacade.obterPreviaDiariaFaturamento(competencia, true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioPreviaDiariaFaturamento.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		final String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("mes", competencia.getId().getMes());
		params.put("ano", competencia.getId().getAno());

		List<Date> dtPrevia = faturamentoFacade.obterDataPreviaModuloSIS(competencia);
		params.put("dataPrevia", (dtPrevia != null && !dtPrevia.isEmpty()) ? dtPrevia.get(0) : null);

		return params;
	}

	public String voltar() {
		return "relatorioPreviaDiaria";
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