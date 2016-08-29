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
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.RelacaoDeOPMNaoFaturadaVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioRelacaoDeOPMNaoFaturadaPdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3648181537634894817L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioRelacaoDeOPMNaoFaturadaPdfController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private String iniciaisPaciente;

	private String origem;

	private Boolean reapresentada;

	private Boolean isDirectPrint;

	private Long procedimento;

	private Integer ano;

	private Integer mes;

	private Date dtHrInicio;

	private Long sSM;
	
	private Collection<RelacaoDeOPMNaoFaturadaVO> lista;
	
	@PostConstruct
	protected void init(){
		begin(conversation);
	}

	public String inicio() {
		
		lista = null;

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
	public Collection<RelacaoDeOPMNaoFaturadaVO> recuperarColecao() {
		try {
			if(lista == null || lista.isEmpty()) {
				lista = faturamentoFacade.obterRelacaoDeOPMNaoFaturada(sSM, ano, mes, dtHrInicio, procedimento, iniciaisPaciente, reapresentada);
			}
			return lista;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String voltar() {
		lista = null;
		return origem;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioRelacaoDeOPMNaoFaturada.jasper";
	}

	public Map<String, Object> recuperarParametros() {

		final Map<String, Object> params = new HashMap<String, Object>();

		params.put("nomeRelatorio", DominioNomeRelatorio.FATR_RESUMO_AIH);
		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);

		return params;
	}

	public String getIniciaisPaciente() {
		return iniciaisPaciente;
	}

	public void setIniciaisPaciente(String iniciaisPaciente) {
		this.iniciaisPaciente = iniciaisPaciente;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
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

	public Long getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(Long procedimento) {
		this.procedimento = procedimento;
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

	public Long getSSM() {
		return sSM;
	}

	public void setSSM(Long sSM) {
		this.sSM = sSM;
	}

}
