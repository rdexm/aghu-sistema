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
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.AihPorProcedimentoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioAihsPorProcedimentoPdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6873767392772011686L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioAihsPorProcedimentoPdfController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private String iniciaisPaciente;

	private Integer ano;

	private Integer mes;

	private Date dtHrInicio;

	private Long procedimentoInicial;

	private Long procedimentoFinal;

	private boolean reapresentada;

	private Boolean isDirectPrint;

	private Boolean todosProcedimentos;

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
	public Collection<AihPorProcedimentoVO> recuperarColecao() {
		final Long codTabIni = (Boolean.TRUE.equals(todosProcedimentos)) ? 1 : procedimentoInicial;
		final Long codTabFim = (Boolean.TRUE.equals(todosProcedimentos)) ? 999999999L : procedimentoFinal;

		return faturamentoFacade.obterAihsPorProcedimentoVO(codTabIni, codTabFim, dtHrInicio, mes, ano, iniciaisPaciente, reapresentada);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioAIHPorProcedimentos.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		final String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		final Long codTabIni = (Boolean.TRUE.equals(todosProcedimentos)) ? 1 : procedimentoInicial;
		final Long codTabFim = (Boolean.TRUE.equals(todosProcedimentos)) ? 999999999L : procedimentoFinal;

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("P_SIT_CONTA", reapresentada ? "CONTAS REAPRESENTADAS" : "CONTAS ENCERRADAS");
		params.put("NM_MES", CoreUtil.obterMesPorExtenso(mes));
		params.put("ANO", ano);
		params.put("PROCEDIMENTO_INICIAL", codTabIni.toString());
		params.put("PROCEDIMENTO_FINAL", codTabFim.toString());

		return params;
	}

	public String voltar() {
		return "relatorioAihsPorProcedimento";
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

	public Long getProcedimentoInicial() {
		return procedimentoInicial;
	}

	public void setProcedimentoInicial(Long procedimentoInicial) {
		this.procedimentoInicial = procedimentoInicial;
	}

	public Long getProcedimentoFinal() {
		return procedimentoFinal;
	}

	public void setProcedimentoFinal(Long procedimentoFinal) {
		this.procedimentoFinal = procedimentoFinal;
	}

	public boolean isReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public Boolean getTodosProcedimentos() {
		return todosProcedimentos;
	}

	public void setTodosProcedimentos(Boolean todosProcedimentos) {
		this.todosProcedimentos = todosProcedimentos;
	}
}