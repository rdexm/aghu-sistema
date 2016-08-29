package br.gov.mec.aghu.faturamento.action;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.PendenciaEncerramentoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioPendenciaEncerramentoController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1186662052475391712L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioPendenciaEncerramentoController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Date dtInicial;

	private Date dtFinal;

	private DominioSituacaoMensagemLog grupo;

	private FatMensagemLog inconsistencia;

	private String inicial;

	private Boolean reapresentada;

	public enum RelatorioPendenciaEncerramentoExceptionCode implements BusinessExceptionCode {
		INICIAS_RELATORIO_PEND_ENC_INVALIDAS;
	}

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	
	//TODO 
	/**
	 * Thiago Côrtes #2152
	 * ALterado devido a correção de mapeamento da Entidade FatMensagemLog, segue uma cópia comentada abaixo
	 */
	@Override
	public Collection<PendenciaEncerramentoVO> recuperarColecao() {
		return  faturamentoFacade.pesquisarMensagensErro(dtInicial, dtFinal, grupo, (inconsistencia != null) ? inconsistencia.getErro() : null, inicial, reapresentada);
	}

//	@Override
//	public Collection<PendenciaEncerramentoVO> recuperarColecao() {
//		return  faturamentoFacade.pesquisarMensagensErro(dtInicial, dtFinal, grupo, (inconsistencia != null) ? inconsistencia.getId()
//				.getErro() : null, inicial, reapresentada);
//
//	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioPendenciaEncerramento.jasper";
	}


	public String visualizarRelatorio() {

		try {
			if (!CoreUtil.validaIniciaisPaciente(inicial)) {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioPendenciaEncerramentoExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
				return null;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return "relatorioPendenciaEncerramentoPdf";
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws JRException
	 */
	public void directPrint() {
		try {
			if (!CoreUtil.validaIniciaisPaciente(inicial)) {
				apresentarMsgNegocio(Severity.ERROR,
						RelatorioPendenciaEncerramentoExceptionCode.INICIAS_RELATORIO_PEND_ENC_INVALIDAS.toString());
				return;
			}
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
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "FATR_INT_LOG_MSG");
		params.put("dataInicial", this.getDtInicial());
		params.put("dataFinal", this.getDtFinal());
		params.put("situacao", (this.getReapresentada()) ? "REAPRESENTADAS" : "ENCERRADAS");
		params.put("grupoMsg", (DominioSituacaoMensagemLog.NAOENC.equals(this.getGrupo())) ? "Pendência"
				: ((DominioSituacaoMensagemLog.NAOCOBR.equals(this.getGrupo())) ? "Perda" : ""));

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);

		return params;
	}
	
	public String voltar(){
		return "relatorioPendenciaEncerramento";
	}

	public List<FatMensagemLog> pesquisarIncosistencias(String objPesquisa) {
		return this.returnSGWithCount(faturamentoFacade.listarMensagensErro(objPesquisa),pesquisarIncosistenciasCount(objPesquisa));
	}

	public Long pesquisarIncosistenciasCount(String objPesquisa) {
		return faturamentoFacade.listarMensagensErroCount(objPesquisa);
	}

	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public DominioSituacaoMensagemLog getGrupo() {
		return grupo;
	}

	public void setGrupo(DominioSituacaoMensagemLog grupo) {
		this.grupo = grupo;
	}

	public FatMensagemLog getInconsistencia() {
		return inconsistencia;
	}

	public void setInconsistencia(FatMensagemLog inconsistencia) {
		this.inconsistencia = inconsistencia;
	}

	public String getInicial() {
		return inicial;
	}

	public void setInicial(String inicial) {
		this.inicial = inicial;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}
}
