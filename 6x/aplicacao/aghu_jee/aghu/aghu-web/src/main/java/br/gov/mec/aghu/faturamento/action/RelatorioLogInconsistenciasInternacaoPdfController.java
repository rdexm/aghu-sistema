package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioGrupoProcedimento;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciasInternacaoVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioLogInconsistenciasInternacaoPdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8389699016585699701L;

	private static final Log LOG = LogFactory.getLog(RelatorioLogInconsistenciasInternacaoPdfController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	private Date dtCriacaoIni;
	private Date dtCriacaoFim;
	private Date dtPrevia;
	private Integer pacProntuario;
	private Integer cthSeq;
	private String inconsistencia;
	private String iniciaisPaciente;
	private String origem;
	private Boolean reapresentada;
	private Boolean isDirectPrint;
	private DominioGrupoProcedimento grupoProcedimento;

	/*
	 * Datas recebidas como parametros String pelo page.xml
	 */
	private String dtCriacaoIniStr;
	private String dtCriacaoFimStr;
	private String dtPreviaStr;
		
	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	public String inicio() {
	 
		this.carregarParametros();
/*
		try {
			dtCriacaoIni = converterStringParaData(dtCriacaoIniStr);
			dtCriacaoFim = converterStringParaData(dtCriacaoFimStr);
			dtPrevia = converterStringParaData(dtPreviaStr);
		} catch (ParseException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}*/
		
		if (iniciaisPaciente != null && iniciaisPaciente.isEmpty()) {
			iniciaisPaciente = null;
		}
		
		if (isDirectPrint) {
			try {
				directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (Exception e) {
				getLog().error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
		return null;
	
	}
	
	private void carregarParametros() {
			String cthSeq = this.getRequestParameter("cthSeq");
			if(StringUtils.isNotBlank(cthSeq)) {
				this.setCthSeq(Integer.valueOf(cthSeq));
			}
			String prontuario = this.getRequestParameter("pacProntuario");
			if(StringUtils.isNotBlank(prontuario)) {
				this.setPacProntuario(Integer.valueOf(prontuario));
			}
			String reapresentada = this.getRequestParameter("reapresentada");
			if(StringUtils.isNotBlank(reapresentada)) {
				this.setReapresentada(Boolean.valueOf(reapresentada));
			}
			String isDirectPrint = this.getRequestParameter("isDirectPrint");
			if(StringUtils.isNotBlank(isDirectPrint)) {
				this.setIsDirectPrint(Boolean.valueOf(isDirectPrint));
			}
			
			this.recuperarColecao();
			
	}

	@Override
	public Collection<LogInconsistenciasInternacaoVO> recuperarColecao() {
		List<LogInconsistenciasInternacaoVO> colecao = new ArrayList<>();
		try {
			colecao = faturamentoFacade.getLogsInconsistenciasInternacaoVO(dtCriacaoIni, dtCriacaoFim, dtPrevia, pacProntuario, cthSeq,
					inconsistencia, iniciaisPaciente, reapresentada, grupoProcedimento);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		Integer tempCthSeq = null;
		for (final LogInconsistenciasInternacaoVO log : colecao) {
			if (!log.getCthseq().equals(tempCthSeq)) {
				tempCthSeq = log.getCthseq();
				log.setLeito(internacaoFacade.obterLeitoContaHospitalar(log.getCthseq()));
			}
		}

		return colecao;
	}

	public String voltar() {
		return origem;
	}	
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioLogInconsistenciasInternacao.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		params.put("nomeRelatorio", DominioNomeRelatorio.FATR_INT_LOG_ERRO);

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("CRIADO_EM_INICIO", dtCriacaoIni);
		params.put("CRIADO_EM_FIM", dtCriacaoFim);

		if (grupoProcedimento != null) {
			params.put("P_DESCR_GRUPO", grupoProcedimento.getDescricao().toUpperCase());
		} else {
			params.put("P_DESCR_GRUPO", null);
		}

		params.put("P_SIT_CONTA", (reapresentada != null && reapresentada) ? "CONTAS REAPRESENTADAS" : "CONTAS ENCERRADAS");

		return params;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Date getDtCriacaoIni() {
		return dtCriacaoIni;
	}

	public void setDtCriacaoIni(Date dtCriacaoIni) {
		this.dtCriacaoIni = dtCriacaoIni;
	}

	public Date getDtCriacaoFim() {
		return dtCriacaoFim;
	}

	public void setDtCriacaoFim(Date dtCriacaoFim) {
		this.dtCriacaoFim = dtCriacaoFim;
	}

	public Date getDtPrevia() {
		return dtPrevia;
	}

	public void setDtPrevia(Date dtPrevia) {
		this.dtPrevia = dtPrevia;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public String getInconsistencia() {
		return inconsistencia;
	}

	public void setInconsistencia(String inconsistencia) {
		this.inconsistencia = inconsistencia;
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

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public DominioGrupoProcedimento getGrupoProcedimento() {
		return grupoProcedimento;
	}

	public void setGrupoProcedimento(DominioGrupoProcedimento grupoProcedimento) {
		this.grupoProcedimento = grupoProcedimento;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}
	
	public String getDtCriacaoIniStr() {
		return dtCriacaoIniStr;
	}

	public void setDtCriacaoIniStr(String dtCriacaoIniStr) {
		this.dtCriacaoIniStr = dtCriacaoIniStr;
	}

	public String getDtCriacaoFimStr() {
		return dtCriacaoFimStr;
	}

	public void setDtCriacaoFimStr(String dtCriacaoFimStr) {
		this.dtCriacaoFimStr = dtCriacaoFimStr;
	}

	public String getDtPreviaStr() {
		return dtPreviaStr;
	}

	public void setDtPreviaStr(String dtPreviaStr) {
		this.dtPreviaStr = dtPreviaStr;
	}
}
