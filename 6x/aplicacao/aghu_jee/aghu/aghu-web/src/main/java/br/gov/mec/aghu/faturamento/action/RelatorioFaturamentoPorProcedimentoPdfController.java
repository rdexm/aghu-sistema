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
import br.gov.mec.aghu.faturamento.vo.FaturamentoPorProcedimentoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioFaturamentoPorProcedimentoPdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7986227212677013553L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioFaturamentoPorProcedimentoPdfController.class);

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

	private Boolean isDirectPrint;
	
	@PostConstruct
	protected void init(){
		begin(conversation);
	}

	public String inicio() {
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
	public Collection<FaturamentoPorProcedimentoVO> recuperarColecao() {
		return faturamentoFacade.obterFaturamentoPorProcedimento(dtHrInicio, ano, mes);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioFaturamentoPorProcedimento.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("NM_MES", CoreUtil.obterMesPorExtenso(mes).toUpperCase());
		params.put("ANO", ano);

		final FaturamentoPorProcedimentoVO valores = faturamentoFacade.obterFaturamentoPorProcedimentoUTIEspelho(dtHrInicio, ano, mes);

		params.put("diariaAcompServHosp", valores.getDiariaAcompServHosp());
		params.put("diariaAcompServProf", valores.getDiariaAcompServProf());
		params.put("diariaUtiHosp", valores.getDiariaUtiHosp());
		params.put("diariaUtiProf", valores.getDiariaUtiProf());
		params.put("diasAcomp", valores.getDiasAcomp() == null ? 0 : valores.getDiasAcomp());
		params.put("diasUTI", valores.getDiasUTI());
		params.put("valorUTI", valores.getValorUTI());
		params.put("valorAcomp", valores.getValorAcomp());

		return params;
	}

	public String voltar() {
		return "relatorioFaturamentoPorProcedimento";
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

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

}
