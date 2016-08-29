package br.gov.mec.aghu.sicon.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.dao.ScoAfContratosDAO;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoItensContratoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Efetua a validação de dados a serem enviados para o SICON
 * Valida os dados das operações de envio de contrato, aditivo e rescisão. 
 *
 */
@Stateless
public class EnvioContratoSiconValidacaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EnvioContratoSiconValidacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;
	
	@Inject
	private ScoContratoDAO scoContratoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private ScoItensContratoDAO scoItensContratoDAO;
	
	@Inject
	private ScoAfContratosDAO scoAfContratosDAO;

	private static final long serialVersionUID = 8539168455992930614L;

	public enum EnvioContratoSiconValidacaoONExceptionCode implements
			BusinessExceptionCode {
				CONTRATO_NAO_ENCONTRADO, 
				UASG_INVALIDA, 
				CPF_OBRIGATORIO, 
				DT_ASSINATURA_CONTRATO_OBRIGATORIA,
				DT_ASSINATURA_ADITIVO_OBRIGATORIA, 
				DT_ASSINATURA_RESCISAO_OBRIGATORIA, 
				TP_CONTRATO_NAO_PERMITE_ITENS, 
				TP_CONTRATO_EXIGE_ITENS, 
				MODALIDADE_SEM_COD_SICON, 
				ITEM_SEM_COD_SICON, 
				ITEM_SEM_DESCRICAO, 
				TP_ADITIVO_OBRIGATORIO,
				ADITIVO_NAO_INFORMADO;
	}

	public void validaGeracaoContratoXML(Long nrContrato)
			throws ApplicationBusinessException {
		ScoContrato contrato = getScoContratoDAO().obterContratoPorNumeroContrato(nrContrato);
		
		getScoContratoDAO().refresh(contrato);
		
		contrato = getScoContratoDAO().obterContratoPorNumeroContrato(nrContrato);
		
		if (contrato == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiconValidacaoONExceptionCode.CONTRATO_NAO_ENCONTRADO);
		}

		// Validação do cadastro do cod. UASG utilizado pela Instituição
		validaUasg();

		// Validação cadastro do CPF do usuário logado
		validaCpfServidorLogado();

		// Verifica existência de data de assinatura do contrato
		validaDataAssinaturaContrato(contrato);

		// Valida vinculo de contrato com Itens/Afs
		validaItensContrato(contrato);

		// Valida Modalidade da Licitação
		validaModalidade(contrato);

		// Validação do cadastro de cod. Sicon para os itens do Contrato
		validacaoCodSiconItensEAfs(contrato);

		// Validação do cadastro de descrição para os itens (material e serviço)
		// do contrato
		validaDescricaoItensContrato(contrato);

	}

	public void validaGeracaoAditivoXML(ScoAditContrato aditContrato)
			throws ApplicationBusinessException {
		
		if (aditContrato == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiconValidacaoONExceptionCode.ADITIVO_NAO_INFORMADO);
		}

		// Validação do cadastro do cod. UASG utilizado pela Instituição
		validaUasg();

		// Validação cadastro do CPF do usuário logado
		validaCpfServidorLogado();

		// Validação da existencia de data de assinatura do aditivo
		validaDataAssinaturaAditivo(aditContrato);

		// Validação do tipo de aditivo: acrescimo ou supressão
		validaTipoAditivo(aditContrato);

	}

	public void validaGeracaoRescisaoXML(ScoResContrato rescisao)
			throws ApplicationBusinessException {

		// Validação do cadastro do cod. UASG utilizado pela Instituição
		validaUasg();

		// Validação cadastro do CPF do usuário logado
		validaCpfServidorLogado();

		// Validação da existencia de data de assinatura da rescisão
		validaDataAssinaturaRescisao(rescisao);

	}

	private void validaUasg() throws ApplicationBusinessException {
		if (this.getAghuFacade().getUasg() == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiconValidacaoONExceptionCode.UASG_INVALIDA);
		}
	}

	private void validaCpfServidorLogado() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		if (servidorLogado.getPessoaFisica().getCpf() == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiconValidacaoONExceptionCode.CPF_OBRIGATORIO);
		}
	}

	private void validaDataAssinaturaContrato(ScoContrato contrato)
			throws ApplicationBusinessException {
		if (contrato.getDtAssinatura() == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiconValidacaoONExceptionCode.DT_ASSINATURA_CONTRATO_OBRIGATORIA);
		}
	}

	private void validaDataAssinaturaAditivo(ScoAditContrato aditContrato)
			throws ApplicationBusinessException {
		if (aditContrato.getDataAssinatura() == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiconValidacaoONExceptionCode.DT_ASSINATURA_ADITIVO_OBRIGATORIA);
		}
	}

	private void validaDataAssinaturaRescisao(ScoResContrato rescisao)
			throws ApplicationBusinessException {

		if (rescisao.getDtAssinatura() == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiconValidacaoONExceptionCode.DT_ASSINATURA_RESCISAO_OBRIGATORIA);
		}

	}

	private void validaItensContrato(ScoContrato contrato)
			throws ApplicationBusinessException {
		boolean devePossuirItens = contrato.getTipoContratoSicon()
				.getIndInsereItens();

		// Contrato Automático pode possuir AF's
		if (contrato.getIndOrigem() == DominioOrigemContrato.A) {
			List<ScoAfContrato> afContrato = getScoAfContratosDAO()
					.obterAfByContrato(contrato);

			// Contrato não possui AFs, mas seu Tipo de Contrato exige
			// existencia de AF
			if (afContrato.isEmpty() && devePossuirItens) {
				throw new ApplicationBusinessException(
						EnvioContratoSiconValidacaoONExceptionCode.TP_CONTRATO_EXIGE_ITENS);
			}

			// Contrato possui AFs, mas seu Tipo de Contrato não permite
			// existência de AF
			if (!afContrato.isEmpty() && !devePossuirItens) {
				throw new ApplicationBusinessException(
						EnvioContratoSiconValidacaoONExceptionCode.TP_CONTRATO_NAO_PERMITE_ITENS);
			}

		} else {
			// Contrato não possui itens, mas seu Tipo de Contrato exige
			// existencia de itens
			List<ScoItensContrato> itensContrato = getScoItensContratoDAO()
					.getItensContratoByContrato(contrato);

			// Contrato não possui itens, mas seu Tipo de Contrato exige
			// existencia de itens
			if (itensContrato.isEmpty() && devePossuirItens) {
				throw new ApplicationBusinessException(
						EnvioContratoSiconValidacaoONExceptionCode.TP_CONTRATO_EXIGE_ITENS);
			}

			// Contrato possui itens, mas seu Tipo de Contrato não permite
			// existência de itens
			if (!itensContrato.isEmpty() && !devePossuirItens) {
				throw new ApplicationBusinessException(
						EnvioContratoSiconValidacaoONExceptionCode.TP_CONTRATO_NAO_PERMITE_ITENS);
			}
		}

	}

	// Se modalidade da licitação for informada no Contrato, verificar se esta
	// ainda é ativa
	private void validaModalidade(ScoContrato contrato)
			throws ApplicationBusinessException {
		if (contrato.getModalidadeLicitacao() != null) {
			List<ScoModalidadeLicitacao> modalidadeSicon = getComprasCadastrosBasicosFacade()
					.obterModalidadesLicitacaoAprovadasPorCodigo(
							contrato.getModalidadeLicitacao().getCodigo());

			if (modalidadeSicon.isEmpty()) {
				throw new ApplicationBusinessException(
						EnvioContratoSiconValidacaoONExceptionCode.MODALIDADE_SEM_COD_SICON);
			}
		}
	}

	private void validacaoCodSiconItensEAfs(ScoContrato contrato)
			throws ApplicationBusinessException {

		// Verifica se contrato deve possuir itens
		if (contrato.getTipoContratoSicon().getIndInsereItens()) {

			// Verificação de itens de contrato manual
			if (contrato.getIndOrigem() == DominioOrigemContrato.M) {
				List<ScoItensContrato> itensContrato = getScoItensContratoDAO()
						.getItensContratoByContrato(contrato);

				for (ScoItensContrato item : itensContrato) {
					// Verificação de Cód. Sicon para item tipo Material
					if (item.getMaterial() != null) {
						List<ScoMaterialSicon> scoMaterialSicon = 
								this.getCadastrosBasicosSiconFacade().obterPorMaterial(item.getMaterial());

						if (scoMaterialSicon.isEmpty()) {
							throw new ApplicationBusinessException(
									EnvioContratoSiconValidacaoONExceptionCode.ITEM_SEM_COD_SICON);
						}
					}

					// Verificação de Cód. Sicon para item tipo Serviço
					if (item.getServico() != null) {
						List<ScoServicoSicon> scoServicoSicon =
								this.getCadastrosBasicosSiconFacade().obterPorCodigoServico(item.getServico());

						if (scoServicoSicon.isEmpty()) {
							throw new ApplicationBusinessException(
									EnvioContratoSiconValidacaoONExceptionCode.ITEM_SEM_COD_SICON);
						}

					}

				}
			}

			// Verificação de itens para contrato Automático
			if (contrato.getIndOrigem() == DominioOrigemContrato.A) {
				// Iteração sobre ScoAfContratos
				for (ScoAfContrato afContratos : contrato.getScoAfContratos()) {

					for (ScoItemAutorizacaoForn iprop : afContratos
							.getScoAutorizacoesForn()
							.getItensAutorizacaoForn()) {
						for (ScoFaseSolicitacao fas : iprop
								.getScoFaseSolicitacao()) {
							// Material
							if (fas.getSolicitacaoDeCompra() != null &&
								fas.getSolicitacaoDeCompra().getMaterial() != null) {
								List<ScoMaterialSicon> scoMaterialSicon = 
										this.getCadastrosBasicosSiconFacade().obterPorMaterial(fas.getSolicitacaoDeCompra().getMaterial());
										
								if (scoMaterialSicon.isEmpty()) {
									throw new ApplicationBusinessException(
											EnvioContratoSiconValidacaoONExceptionCode.ITEM_SEM_COD_SICON);
								}
							}

							// Serviço
							if (fas.getSolicitacaoServico() != null && 
								fas.getSolicitacaoServico().getServico() != null) {
								List<ScoServicoSicon> scoServicoSicon =
										this.getCadastrosBasicosSiconFacade().obterPorCodigoServico(fas.getSolicitacaoServico().getServico());

								if (scoServicoSicon.isEmpty()) {
									throw new ApplicationBusinessException(
											EnvioContratoSiconValidacaoONExceptionCode.ITEM_SEM_COD_SICON);
								}

							}
						}
					}
				}
			}

		}
	}

	private void validaDescricaoItensContrato(ScoContrato contrato)
			throws ApplicationBusinessException {

		// Verifica se contrato deve possuir itens
		if (contrato.getTipoContratoSicon().getIndInsereItens()) {
			
			if (contrato.getIndOrigem() == DominioOrigemContrato.M) {
				// Verifica descrição dos itens de contrato manual
				List<ScoItensContrato> itensContrato = getScoItensContratoDAO()
						.getItensContratoByContrato(contrato);

				if (!itensContrato.isEmpty()) {
					for (ScoItensContrato item : itensContrato) {
						if (StringUtils.isBlank(item.getDescricao())) {
							throw new ApplicationBusinessException(
									EnvioContratoSiconValidacaoONExceptionCode.ITEM_SEM_DESCRICAO);
						}
					}
				}
			}

			// Verificação de itens para contrato Automático
			if (contrato.getIndOrigem() == DominioOrigemContrato.A) {
				// Iteração sobre ScoAfContratos
				for (ScoAfContrato afContratos : contrato.getScoAfContratos()) {

					for (ScoItemAutorizacaoForn iprop : afContratos
							.getScoAutorizacoesForn()
							.getItensAutorizacaoForn()) {
						for (ScoFaseSolicitacao fas : iprop
								.getScoFaseSolicitacao()) {
							// Material
							if (fas.getSolicitacaoDeCompra() != null) {
								if (StringUtils.isBlank(fas.getSolicitacaoDeCompra()
										.getMaterial().getNome())) {
									throw new ApplicationBusinessException(
											EnvioContratoSiconValidacaoONExceptionCode.ITEM_SEM_DESCRICAO);
								}
							}

							// Serviço
							if (fas.getSolicitacaoServico() != null) {
								if (StringUtils.isBlank(fas.getSolicitacaoServico()
										.getServico().getNome())) {
									throw new ApplicationBusinessException(
											EnvioContratoSiconValidacaoONExceptionCode.ITEM_SEM_DESCRICAO);
								}
							}
						}
					}
				}
			}

		}
	}

	private void validaTipoAditivo(ScoAditContrato aditContrato)
			throws ApplicationBusinessException {
		if (aditContrato.getVlAditivado() != null
				&& aditContrato.getIndTipoAditivo() == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiconValidacaoONExceptionCode.TP_ADITIVO_OBRIGATORIO);
		}
	}

	/**
	 * @In(value = "aghuFacade", create = true)
	 * protected IAghuFacade aghuFacade;
	 * @return
	 */
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected ICadastrosBasicosSiconFacade getCadastrosBasicosSiconFacade() {
		return cadastrosBasicosSiconFacade;
	}
	
	protected IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	protected ScoContratoDAO getScoContratoDAO() {
		return scoContratoDAO;
	}

	protected ScoAfContratosDAO getScoAfContratosDAO() {
		return scoAfContratosDAO;
	}

	protected ScoItensContratoDAO getScoItensContratoDAO() {
		return scoItensContratoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
