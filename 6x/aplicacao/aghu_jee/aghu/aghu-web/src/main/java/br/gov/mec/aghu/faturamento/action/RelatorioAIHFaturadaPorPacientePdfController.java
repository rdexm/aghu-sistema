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
import br.gov.mec.aghu.faturamento.vo.AIHFaturadaPorPacienteVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioAIHFaturadaPorPacientePdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5799781358137854002L;
	

	private static final Log LOG = LogFactory.getLog(RelatorioAIHFaturadaPorPacientePdfController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Integer ano;

	private Integer mes;

	private Date dtHrInicio;

	private Integer clinica;

	private String iniciaisPaciente;

	private Boolean reapresentada;

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
	public Collection<AIHFaturadaPorPacienteVO> recuperarColecao() {

		final List<AIHFaturadaPorPacienteVO> colecao = faturamentoFacade.obterAIHsFaturadasPorPaciente(dtHrInicio, ano, mes,
				iniciaisPaciente, reapresentada, clinica);
		final Integer nrAihs = colecao.size();
		for (AIHFaturadaPorPacienteVO obj : colecao) {
			obj.setTotAihs(nrAihs);
		}

		return colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioAIHFaturadaPorPaciente.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("P_SIT_CONTA", reapresentada ? "CONTAS REAPRESENTADAS" : "CONTAS ENCERRADAS");
		params.put("NM_MES", CoreUtil.obterMesPorExtenso(mes));
		params.put("ANO", ano);

		return params;
	}

	public String voltar() {
		return "relatorioAIHFaturadaPorPaciente";
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	public String getIniciaisPaciente() {
		return iniciaisPaciente;
	}

	public void setIniciaisPaciente(String iniciaisPaciente) {
		this.iniciaisPaciente = iniciaisPaciente;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public Integer getClinica() {
		return clinica;
	}

	public void setClinica(Integer clinica) {
		this.clinica = clinica;
	}

}
