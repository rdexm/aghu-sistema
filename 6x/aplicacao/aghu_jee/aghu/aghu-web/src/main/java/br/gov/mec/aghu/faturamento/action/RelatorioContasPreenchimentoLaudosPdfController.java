package br.gov.mec.aghu.faturamento.action;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ContasPreenchimentoLaudosVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioContasPreenchimentoLaudosPdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1993322964782295671L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioContasPreenchimentoLaudosPdfController.class);

	private Log getLog() {
		return LOG;
	}
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Date dtPrevia;

	private Short unfSeq;

	private String origem;

	private String iniciaisPaciente;

	private Boolean isDirectPrint;

	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	public String inicio() {

		if (iniciaisPaciente != null && iniciaisPaciente.isEmpty()) {
			iniciaisPaciente = null;
		}

		if (isDirectPrint) {
			try {
				directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				getLog().error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
		return null;
	}

	@Override
	public Collection<ContasPreenchimentoLaudosVO> recuperarColecao() {
		return faturamentoFacade.obterContasPreenchimentoLaudos(dtPrevia, unfSeq, iniciaisPaciente);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioContasPreenchimentoLaudos.jasper";
	}

	public Map<String, Object> recuperarParametros() {

		final Map<String, Object> params = new HashMap<String, Object>();

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("dtUltimaPrevia", dtPrevia);

		String descUnf = null;
		if (unfSeq != null) {
			AghUnidadesFuncionais unf = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
			descUnf = unf.getDescricao();
		}

		params.put("descUnf", descUnf);

		return params;
	}

	public String voltar(){
		return "relatorioContasPreenchimentoLaudos";
	}
	
	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getIniciaisPaciente() {
		return iniciaisPaciente;
	}

	public void setIniciaisPaciente(String iniciaisPaciente) {
		this.iniciaisPaciente = iniciaisPaciente;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public Date getDtPrevia() {
		return dtPrevia;
	}

	public void setDtPrevia(Date dtPrevia) {
		this.dtPrevia = dtPrevia;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
}