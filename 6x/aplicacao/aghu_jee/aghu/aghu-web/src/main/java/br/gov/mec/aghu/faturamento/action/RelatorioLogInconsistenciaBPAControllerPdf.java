package br.gov.mec.aghu.faturamento.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciaBPAVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatMensagemLogId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioLogInconsistenciaBPAControllerPdf extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 402547189748325108L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioLogInconsistenciaBPAControllerPdf.class);

	private Log getLog() {
		return LOG;
	}
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private DominioModuloMensagem modulo;

	private FatMensagemLogId erro;

	private DominioSituacaoMensagemLog situacao;

	private Boolean isDirectPrint;
	
	@PostConstruct
	protected void init(){
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
	
	public String voltar(){
		return "relatorioLogInconsistenciaBPA";
	}

	@Override
	public Collection<LogInconsistenciaBPAVO> recuperarColecao() {
		AghParametros param = null;

		Short pTipoGrupoContaSUS = null, pCpgCphCspSeq = null, pCpgCphCspCnvCodigo = null, pIphPhoSeq = null;

		try {
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);

			pTipoGrupoContaSUS = param.getVlrNumerico().shortValue();

			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
			pCpgCphCspSeq = param.getVlrNumerico().shortValue();

			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_PADRAO);
			pCpgCphCspCnvCodigo = (param != null && param.getVlrNumerico() != null) ? param.getVlrNumerico().shortValue() : Short
					.valueOf("1");

			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			pIphPhoSeq = param.getVlrNumerico().shortValue();

			return faturamentoFacade.obterLogInconsistenciaBPAVO(modulo, (erro != null ? erro.getErro() : null), situacao, pIphPhoSeq,
					pTipoGrupoContaSUS, pCpgCphCspSeq, pCpgCphCspCnvCodigo);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/relatorioLogInconsistenciaBPA.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		final String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("GRUPO_MENSAGEM", situacao.toString());

		return params;
	}

	public DominioModuloMensagem getModulo() {
		return modulo;
	}

	public void setModulo(DominioModuloMensagem modulo) {
		this.modulo = modulo;
	}

	public FatMensagemLogId getErro() {
		return erro;
	}

	public void setErro(FatMensagemLogId erro) {
		this.erro = erro;
	}

	public DominioSituacaoMensagemLog getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoMensagemLog situacao) {
		this.situacao = situacao;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}
}