package br.gov.mec.aghu.sicon.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoAditivo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAditContratoId;
import br.gov.mec.aghu.model.ScoAditContratoJn;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoLogEnvioSicon;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.dao.ScoAditContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoAditContratoJnDAO;
import br.gov.mec.aghu.sicon.dao.ScoAfContratosDAO;
import br.gov.mec.aghu.sicon.dao.ScoLogEnvioSiconDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterAditivoContratualON extends BaseBusiness {

	@EJB
	private ManterContratosON manterContratosON;
	
	private static final Log LOG = LogFactory.getLog(ManterAditivoContratualON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private ScoAditContratoDAO scoAditContratoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoAditContratoJnDAO scoAditContratoJnDAO;
	
	@Inject
	private ScoLogEnvioSiconDAO scoLogEnvioSiconDAO;
	
	@Inject
	private ScoAfContratosDAO scoAfContratosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2505905478798299810L;

	private enum ManterAditivoContratualONExceptionCode implements
			BusinessExceptionCode {
		ADITIVO_ENVIADO, VALOR_ADITIVO_ABAIXO_PERMITIDO, INFORMAR_DTVIG_INI_DTVIG_FIM, DT_FIM_OBRIG, DT_INI_OBRIG, TIPO_CONT_OBRIGATORIO, DATA_INICIO_MAIOR_DATA_FIM, VALOR_ADITIVO_ACIMA_PERMITIDO, VALOR_ADITIVADO_OBRIGATORIO, TIPO_ADITIVO_OBRIGATORIO, VALOR_OBJETO_OBRIGATORIO, DT_ASSINATURA_MAIOR, DT_PUBLICACAO_MAIOR;
	}

	private ScoAditContratoDAO aditContratoDAO;
	private ScoAfContratosDAO afContratosDAO;

	public ScoAditContrato gravarAtualizar(ScoAditContrato input) throws ApplicationBusinessException,
			ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoAditContrato aditivo = input;

		propriedadesObrigatoriasOK(input);

		if (isAtualizacao(input)) {
			verificaDataRecisao(input);
			input.setAlteradoEm(new Date());
			input.setServidor(servidorLogado);

			registrarAditContratoJn(aditivo, DominioOperacoesJournal.UPD);

			if (hasToCheckParameters(input)) {
				aditivo = getScoAditContratoDAO().atualizar(input);
				getScoAditContratoDAO().flush();
			} else {
				if (checkIfParametersOK(input)) {
					aditivo = getScoAditContratoDAO().atualizar(input);
					getScoAditContratoDAO().flush();
				}
			}
		} else {
			Date d = new Date();
			// RN03
			input.setCriadoEm(d);
			input.setServidor(servidorLogado);
			if (hasToCheckParameters(input)) {
				aditivo = inserir(input);
			} else {
				if (checkIfParametersOK(input)) {
					aditivo = inserir(input);
				}
			}
		}

		getManterContratosON().atualizaAfsDtVencimentoContrato(input.getCont());

		return aditivo;
	}

	private void verificaDataRecisao(ScoAditContrato input) {
		ScoAditContrato aditContratoOld = getScoAditContratoDAO().obterOld(
				input);

		if (input.getDataRescicao() != null
				&& (!input.getDataRescicao().equals(
						aditContratoOld.getDataRescicao()))) {
			input.setSituacao(DominioSituacaoEnvioContrato.AR);
		}
	}

	public void excluir(ScoAditContratoId id)
			throws ApplicationBusinessException {
		
		ScoAditContrato input = scoAditContratoDAO.obterPorChavePrimaria(id);

		if (!foiEnviado(input)) {
			ScoContrato contrato = input.getCont();

			List<ScoLogEnvioSicon> logsAditivo = this.getScoLogEnvioSiconDAO()
					.pesquisarRetornoIntegracaoPorAditivo(input);

			for (ScoLogEnvioSicon scoLogEnvioSicon : logsAditivo) {
				this.getScoLogEnvioSiconDAO().remover(scoLogEnvioSicon);
			}

			registrarAditContratoJn(input, DominioOperacoesJournal.DEL);

			scoAditContratoDAO.removerPorId(id);

			getManterContratosON().atualizaAfsDtVencimentoContrato(contrato);
		} else {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.ADITIVO_ENVIADO);
		}
	}

	private ScoAditContrato inserir(ScoAditContrato input) throws ApplicationBusinessException {
		ScoAditContratoDAO scoAditContratoDAO = this.getScoAditContratoDAO();

		// recupera aditivos do contrato
		List<ScoAditContrato> res = getScoAditContratoDAO()
				.obterAditivosByContrato(input.getCont());
		if (res.size() == 0) {
			input.getId().setSeq(1);

			registrarAditContratoJn(input, DominioOperacoesJournal.INS);

			scoAditContratoDAO.persistir(input);
			scoAditContratoDAO.flush();
			return input;
		} else {
			int numAditivo = 0;
			for (ScoAditContrato a : res) {
				if (a.getId().getSeq() > numAditivo) {
					numAditivo = a.getId().getSeq();
				}
			}
			input.getId().setSeq(++numAditivo);

			registrarAditContratoJn(input, DominioOperacoesJournal.INS);

			scoAditContratoDAO.persistir(input);
			scoAditContratoDAO.flush();
			return input;
		}
	}

	private void registrarAditContratoJn(ScoAditContrato aditivo,
			DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoAditContrato aditivoOriginal;

		if (operacao.equals(DominioOperacoesJournal.UPD)) {
			aditivoOriginal = getScoAditContratoDAO().obterOriginal(aditivo);
		} else {
			aditivoOriginal = aditivo;
		}

		ScoAditContratoJn aditivoJn = BaseJournalFactory.getBaseJournal(
				operacao, ScoAditContratoJn.class, servidorLogado.getUsuario());

		aditivoJn.setId(aditivoOriginal.getId());
		aditivoJn.setContrato(aditivoOriginal.getCont());
		aditivoJn.setTipoContratoSicon(aditivoOriginal.getTipoContratoSicon());
		aditivoJn.setDtInicioVigencia(aditivoOriginal.getDtInicioVigencia());
		aditivoJn.setObjetoContrato(aditivoOriginal.getObjetoContrato());
		aditivoJn.setDtFimVigencia(aditivoOriginal.getDtFimVigencia());
		aditivoJn.setVlAditivado(aditivoOriginal.getVlAditivado());
		aditivoJn.setDtAssinatura(aditivoOriginal.getDataAssinatura());
		aditivoJn.setDtPublicacao(aditivoOriginal.getDataPublicacao());
		aditivoJn.setDtRescisao(aditivoOriginal.getDataRescicao());
		aditivoJn.setIndSituacao(aditivoOriginal.getSituacao());
		aditivoJn.setJustificativa(aditivoOriginal.getJustificativa());
		aditivoJn.setIndTipoAditivo(aditivoOriginal.getIndTipoAditivo());

		getScoAditContratoJnDAO().persistir(aditivoJn);
		getScoAditContratoJnDAO().flush();
	}

	// RN04
	private boolean checkIfParametersOK(ScoAditContrato input)
			throws ApplicationBusinessException {
		// REDUZINDO VALOR...
		BigDecimal valorInterm = null;
		BigDecimal percentual = null;
		if (input.getIndTipoAditivo() == DominioTipoAditivo.S) {
			AghParametros paramRed = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_PERC_RED_ADITIVO);
			percentual = new BigDecimal(paramRed.getVlrTexto());
			valorInterm = input
					.getCont()
					.getValorTotal()
					.subtract(
							input.getCont().getValorTotal()
									.multiply(percentual));
			if (input.getVlAditivado().compareTo(valorInterm) == -1) {
				throw new ApplicationBusinessException(
						ManterAditivoContratualONExceptionCode.VALOR_ADITIVO_ABAIXO_PERMITIDO);
			}
		}
		// AUMENTANDO VALOR...
		else if (input.getIndTipoAditivo() == DominioTipoAditivo.A) {
			AghParametros paramAd = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_PERC_PAD_ADITIVO);
			// check grupo de natureza
			AghParametros paramObras = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_GND_DESP_OBRAS);
			for (ScoAfContrato afCont : getScoAfContratosDAO()
					.obterAfByContrato(input.getCont())) {
				if (afCont.getScoAutorizacoesForn().getGrupoNaturezaDespesa()
						.getCodigo().toString()
						.startsWith(paramObras.getVlrNumerico().toString())) {
					paramAd = getParametroFacade().buscarAghParametro(
							AghuParametrosEnum.P_PERC_MAX_ADITIVO);
					break;
				}
			}
			percentual = new BigDecimal(paramAd.getVlrTexto());
			valorInterm = input.getCont().getValorTotal()
					.add(input.getCont().getValorTotal().multiply(percentual));
			if (input.getVlAditivado().compareTo(valorInterm) == 1) {
				throw new ApplicationBusinessException(
						ManterAditivoContratualONExceptionCode.VALOR_ADITIVO_ACIMA_PERMITIDO);
			}
		}
		return true;
	}

	// Se a origem for igual a M (Manual) ou se o usuário possuir a permissão
	// "informarVlrContratoSemRest" vinculada ao seu perfil de acesso,
	// qualquer valor indicado pelo usuário deverá ser aceito pelo sistema.
	private boolean hasToCheckParameters(ScoAditContrato input) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		return (input.getCont().getIndOrigem() == DominioOrigemContrato.M || getICascaFacade()
				.usuarioTemPermissao(servidorLogado.getUsuario(),
						"valorContratoAditivado", "gravar"));
	}

	// RN02
	@SuppressWarnings("PMD.NPathComplexity")
	private void propriedadesObrigatoriasOK(ScoAditContrato input)
			throws ApplicationBusinessException {
		if (input.getDtFimVigencia() == null
				&& input.getDtInicioVigencia() != null) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.DT_FIM_OBRIG);
		} else if (input.getDtFimVigencia() != null
				&& input.getDtInicioVigencia() == null) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.DT_INI_OBRIG);
		} else if ((input.getDtFimVigencia() != null && input
				.getDtInicioVigencia() != null)
				&& input.getDtFimVigencia().compareTo(
						input.getDtInicioVigencia()) < 0) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.DATA_INICIO_MAIOR_DATA_FIM);
		}
		if (input.getTipoContratoSicon() == null) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.TIPO_CONT_OBRIGATORIO);
		}
		if (input.getVlAditivado() != null && input.getIndTipoAditivo() == null) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.TIPO_ADITIVO_OBRIGATORIO);
		}
		if (input.getVlAditivado() == null && input.getIndTipoAditivo() != null) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.VALOR_ADITIVADO_OBRIGATORIO);
		}
		if (input.getObjetoContrato() == null) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.VALOR_OBJETO_OBRIGATORIO);
		}
		if (input.getDataAssinatura() != null
				&& input.getDtInicioVigencia() != null
				&& input.getDtInicioVigencia().compareTo(
						input.getDataAssinatura()) < 0) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.DT_ASSINATURA_MAIOR);
		}
		Date atual = new Date();
		if (input.getDataPublicacao() != null
				&& input.getDataPublicacao().compareTo(atual) > 0) {
			throw new ApplicationBusinessException(
					ManterAditivoContratualONExceptionCode.DT_PUBLICACAO_MAIOR);
		}
	}

	private boolean isAtualizacao(ScoAditContrato input) {
		return (input.getId() == null || input.getId().getSeq() == null) ? false
				: true;
	}

	private boolean foiEnviado(ScoAditContrato input) {
		return input.getSituacao() == DominioSituacaoEnvioContrato.E ? true
				: false;
	}

	/**
	 * Indica se deve ou nao habilitar icone para exclusao de aditivo na tela,
	 * baseado no retorno de registros de envio. Se o aditivo nunca foi enviado
	 * com sucesso, habilita a exclusao. Caso contrario, nao habilita.
	 * 
	 * @param contSeq
	 *            Seq do contrato a qual pertence o aditivo.
	 * @param aditivo
	 *            Aditivo a ser analisado.
	 * @return True, para habilitar a exclusao e False para nao habilitar.
	 */
	public Boolean indicarExclusaoAditivo(ScoAditContrato aditivo) {

 		if ((aditivo.getSituacao().equals(DominioSituacaoEnvioContrato.EE))
				&& (getScoLogEnvioSiconDAO().verificarSucessoEmEnvioAditivos(aditivo).size() > 0)) {

				return false;
		}

		return true;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

	protected ScoAfContratosDAO getScoAfContratosDAO() {
		if (afContratosDAO != null) {
			return afContratosDAO;
		} else {
			afContratosDAO = scoAfContratosDAO;
		}
		return afContratosDAO;
	}

	protected ScoAditContratoDAO getScoAditContratoDAO() {
		if (aditContratoDAO != null) {
			return aditContratoDAO;
		} else {
			aditContratoDAO = scoAditContratoDAO;
		}
		return aditContratoDAO;
	}

	protected ScoLogEnvioSiconDAO getScoLogEnvioSiconDAO() {
		return scoLogEnvioSiconDAO;
	}

	protected ScoAditContratoJnDAO getScoAditContratoJnDAO() {
		return scoAditContratoJnDAO;
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected ManterContratosON getManterContratosON() {
		return manterContratosON;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
