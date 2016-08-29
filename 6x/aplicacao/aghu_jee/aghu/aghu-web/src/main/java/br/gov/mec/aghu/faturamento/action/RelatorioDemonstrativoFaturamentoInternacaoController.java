package br.gov.mec.aghu.faturamento.action;

import java.text.SimpleDateFormat;
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
import br.gov.mec.aghu.faturamento.vo.DemonstrativoFaturamentoInternacaoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioDemonstrativoFaturamentoInternacaoController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2348981516049142398L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioDemonstrativoFaturamentoInternacaoController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Integer prontuario;

	private Date dtInternacaoAdm;
	
	@PostConstruct
	protected void init(){
		begin(conversation, true);
	}

	@Override
	public Collection<DemonstrativoFaturamentoInternacaoVO> recuperarColecao() {
		try {
			return faturamentoFacade.pesquisarEspelhoAih(prontuario, dtInternacaoAdm);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioDemInter.jasper";
	}

	public String visualizarRelatorio() {
		return "relatorioDemonstrativoInternacaoPdf";
	}

	public String voltar() {
		return "relatorioDemonstrativoInternacao";
	}

	public void directPrint() {
		try {
			super.directPrint();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String subReport = "br/gov/mec/aghu/faturamento/report/relatorioDemInterOrtProt.jasper";
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "FATR_AIH_PAC");
		params.put("subRelatorio", Thread.currentThread().getContextClassLoader().getResourceAsStream(subReport));

		AghInstituicoesHospitalares hospital = aghuFacade.recuperarInstituicaoHospitalarLocal();

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeHospital", local);
		params.put("nomeCidade", (hospital.getCddCodigo() != null) ? hospital.getCddCodigo().getNome() : "");
		params.put("siglaEstado", (hospital.getCddCodigo() != null) ? ((hospital.getCddCodigo().getAipUf() != null) ? hospital
				.getCddCodigo().getAipUf().getSigla() : "") : "");

		return params;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDtInternacaoAdm() {
		return dtInternacaoAdm;
	}

	public void setDtInternacaoAdm(Date dtInternacaoAdm) {
		this.dtInternacaoAdm = dtInternacaoAdm;
	}
}
