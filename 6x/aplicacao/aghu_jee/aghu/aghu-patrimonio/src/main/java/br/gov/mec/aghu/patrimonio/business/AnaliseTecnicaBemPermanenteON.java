package br.gov.mec.aghu.patrimonio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.dominio.DominioStatusItemPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoMensagemExame;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmServAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmStatusTicket;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceItemRecebProvisorioId;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmServAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.AnaliseTecnicaBemPermanenteVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras da funcionalidade Solicitar Análise Técnica de Bem Permanente. 
 */
@Stateless
public class AnaliseTecnicaBemPermanenteON extends BaseBusiness {
	
	private static final long serialVersionUID = -439220982716507745L;

	private static final Log LOG = LogFactory.getLog(AnaliseTecnicaBemPermanenteON.class);

	private static final String TITULO_ABA_ACAO = "Consultar Avaliações";

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private PtmTicketRN ptmTicketRN;

	@Inject
	private PtmItemRecebProvisoriosDAO ptmItemRecebProvisoriosDAO;
	
	@Inject
	private PtmServAreaTecAvaliacaoDAO ptmServAreaTecAvaliacaoDAO;

	@Inject
	private EmailUtil emailUtil;
	
	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.core.business.BaseBusiness#getLogger()
	 */
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Enum contendo os códigos de erro possíveis para esta ON.
	 */
	public enum AnaliseTecnicaBemPermanenteONExceptionCode implements BusinessExceptionCode {
		ITEM_RECEBIMENTO_NAO_ENCONTRADO, ASSOCIAR_AREA_TEC_ANALISE, AREA_TEC_ANALISE_NAO_INFORMADA
	}
	
	/**
	 * Método responsável por verificar o tipo e realizar a consulta por itens de recebimento.
	 * 
	 * @param numeroRecebimento - Número de Recebimento
	 * @return Lista de itens de recebimento
	 * @throws ApplicationBusinessException 
	 */
	public List<AnaliseTecnicaBemPermanenteVO> consultarItensRecebimento(Integer numeroRecebimento) throws ApplicationBusinessException {

		List<AnaliseTecnicaBemPermanenteVO> retorno;
		if (getEstoqueFacade().verificarExistenciaAFRecebimento(numeroRecebimento)) {
			retorno = getPtmItemRecebProvisoriosDAO().consultarItensRecebimentoComAF(numeroRecebimento);
		} else {
			retorno = getPtmItemRecebProvisoriosDAO().consultarItensRecebimentoSemAF(numeroRecebimento);
		}

		if (retorno.isEmpty()) {
			throw new ApplicationBusinessException(AnaliseTecnicaBemPermanenteONExceptionCode.ITEM_RECEBIMENTO_NAO_ENCONTRADO);
		}
		
		return retorno;
	}

	/**
	 * Atualiza os itens informados com a informação de Área Técnica selecionada.
	 * 
	 * @param itens - Itens a serem atualizados
	 * @param area - Área selecionada
	 * @param numeroRecebimento - Número de Recebimento informado
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarItensRecebimentoAnaliseTecnica(List<AnaliseTecnicaBemPermanenteVO> itens, PtmAreaTecAvaliacao area, Integer numeroRecebimento)
			throws ApplicationBusinessException {

		// Validações
		if (area == null || area.getSeq() == null) {
			throw new ApplicationBusinessException(AnaliseTecnicaBemPermanenteONExceptionCode.AREA_TEC_ANALISE_NAO_INFORMADA);
		}

		if (itens == null || itens.isEmpty()) {
			throw new ApplicationBusinessException(AnaliseTecnicaBemPermanenteONExceptionCode.ASSOCIAR_AREA_TEC_ANALISE);
		}

		// Persistência
		PtmServAreaTecAvaliacao tecnicoPadrao = getPtmServAreaTecAvaliacaoDAO().obterTecnicoPadraoAreaPorSeqArea(area.getSeq());
		Date dataRecebimento = new Date();

		List<PtmItemRecebProvisorios> itensPatrimonio = new ArrayList<PtmItemRecebProvisorios>();

		for (AnaliseTecnicaBemPermanenteVO itemRecebimento : itens) {
			PtmItemRecebProvisorios itemPatrimonio = getPtmItemRecebProvisoriosDAO().obterPorIdItensEstoque(itemRecebimento.getRecebimento(),
					itemRecebimento.getItemRecebimento());

			if (itemPatrimonio == null) {
				itemPatrimonio = new PtmItemRecebProvisorios();

				SceItemRecebProvisorioId idItemEstoque = new SceItemRecebProvisorioId();
				idItemEstoque.setNrpSeq(itemRecebimento.getRecebimento());
				idItemEstoque.setNroItem(itemRecebimento.getItemRecebimento());
				
				itemPatrimonio.setSceItemRecebProvisorio(getEstoqueFacade().obterItemRecebProvisorioPorChavePrimaria(idItemEstoque));
				itemPatrimonio.setDataRecebimento(dataRecebimento);
				itemPatrimonio.setServidor(getServidorLogadoFacade().obterServidorLogado());
				
				if (tecnicoPadrao == null) {
					itemPatrimonio.setStatus(DominioStatusItemPatrimonio.UM.getCodigo());
				} else {
					itemPatrimonio.setStatus(DominioStatusItemPatrimonio.DOIS.getCodigo());
				}
				
				itemPatrimonio.setAtaSeq(area.getSeq());
				
				if (tecnicoPadrao != null) {
					itemPatrimonio.setServidorTecPadrao(tecnicoPadrao.getServidor());
				}
				
				getPtmItemRecebProvisoriosDAO().persistir(itemPatrimonio);
			} else {
				itemPatrimonio.setAtaSeq(area.getSeq());
				
				if (tecnicoPadrao != null) {
					itemPatrimonio.setServidorTecPadrao(tecnicoPadrao.getServidor());
				}
				
				getPtmItemRecebProvisoriosDAO().merge(itemPatrimonio);
			}

			itensPatrimonio.add(itemPatrimonio);
		}

		enviarEmailsAnaliseTecnica(tecnicoPadrao, area, numeroRecebimento, itens);
		
		enviarPendenciasAnaliseTecnica(tecnicoPadrao, itensPatrimonio, dataRecebimento, area);
	}

	/**
	 * Método responsável por enviar os emails de notificação da solicitação de análise técnica.
	 * 
	 * @param tecnicoPadrao - Técnico padrão da Área Técnica
	 * @param area - Área Técnica de Avaliação
	 * @param numeroRecebimento - Numero do Recebimento
	 * @param itens - Itens de Recebimento
	 * @throws ApplicationBusinessException
	 */
	public void enviarEmailsAnaliseTecnica(PtmServAreaTecAvaliacao tecnicoPadrao, PtmAreaTecAvaliacao area, Integer numeroRecebimento,
			List<AnaliseTecnicaBemPermanenteVO> itens) throws ApplicationBusinessException {

		String remetente = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_EMAIL_PATRIMONIO).toLowerCase();
		
		if (tecnicoPadrao == null) {
			// Email para o chefe 
			String destinatario = area.getServidorCC().getUsuario() + getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL);
			
			String assunto = getResourceBundleValue("ANALISE_TECNICA_ASSUNTO_EMAIL_3", numeroRecebimento);
			
			StringBuilder conteudo = new StringBuilder();
			conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_3_PARTE_1", area.getServidorCC().getPessoaFisica().getNome(),
					numeroRecebimento));
			if (itens.get(0).getAf() != null) {
				conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_3_PARTE_2", itens.get(0).getAfnNumero(), itens.get(0).getComplemento(),
						itens.get(0).getAf()));
			}
			conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_3_PARTE_3"));
			
			getEmailUtil().enviaEmail(remetente, destinatario, null, assunto, conteudo.toString());
		} else {
			// Email para o técnico padrão 
			String destinatario = tecnicoPadrao.getServidor().getUsuario() + getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL);
			
			String assunto = getResourceBundleValue("ANALISE_TECNICA_ASSUNTO_EMAIL_1", numeroRecebimento);
			
			StringBuilder conteudo = new StringBuilder();
					conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_1_PARTE_1", tecnicoPadrao.getServidor().getPessoaFisica().getNome(),
					numeroRecebimento));
			if (itens.get(0).getAf() != null) {
				conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_1_PARTE_2", itens.get(0).getAfnNumero(), itens.get(0).getComplemento(),
						itens.get(0).getAf()));
			}
			conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_1_PARTE_3"));
			
			getEmailUtil().enviaEmail(remetente, destinatario, null, assunto, conteudo.toString());
			
			// Email para o chefe 
			String destinatario2 = area.getServidorCC().getUsuario() + getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL);
			
			String assunto2 = getResourceBundleValue("ANALISE_TECNICA_ASSUNTO_EMAIL_2", numeroRecebimento, tecnicoPadrao.getServidor().getPessoaFisica().getNome());
			
			StringBuilder conteudo2 = new StringBuilder();
			conteudo2.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_2_PARTE_1", area.getServidorCC().getPessoaFisica().getNome(),
					numeroRecebimento, tecnicoPadrao.getServidor().getPessoaFisica().getNome()));
			if (itens.get(0).getAf() != null) {
				conteudo2.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_2_PARTE_2", itens.get(0).getAfnNumero(), itens.get(0).getComplemento(),
						itens.get(0).getAf()));
			}
			conteudo2.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_2_PARTE_3"));
			
			getEmailUtil().enviaEmail(remetente, destinatario2, null, assunto2, conteudo2.toString());
		}
	}
	
	/**
	 * #43446 - Método responsável por enviar os emails de notificação da designação do tecnico responsavel.
	 * 
	 * @param tecnico
	 * @param area
	 * @param aceite
	 * @param afnNumero
	 * @throws ApplicationBusinessException
	 */
	public void enviarEmailsAceiteTecnico(RapServidores tecnico, PtmAreaTecAvaliacao area, AceiteTecnicoParaSerRealizadoVO aceite, Integer afnNumero) 
			throws ApplicationBusinessException {
		
		String remetente = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_EMAIL_PATRIMONIO).toLowerCase();
		
		// Email para o técnico padrão 
		String destinatario = tecnico.getUsuario() + getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL);
		
		String assunto = getResourceBundleValue("ANALISE_TECNICA_ASSUNTO_EMAIL_1", aceite.getRecebimento());
		
		StringBuilder conteudo = new StringBuilder();
		conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_1_PARTE_1", tecnico.getPessoaFisica().getNome(), aceite.getRecebimento()));
		if (aceite.getAf() != null) {
			conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_1_PARTE_2", afnNumero, aceite.getComplemento(), aceite.getAf()));
		}
		conteudo.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_1_PARTE_3"));
		
		getEmailUtil().enviaEmail(remetente, destinatario, null, assunto, conteudo.toString());
		
		// Email para o chefe 
		String destinatario2 = area.getServidorCC().getUsuario() + getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_DOMINIO_EMAIL);
		
		String assunto2 = getResourceBundleValue("ANALISE_TECNICA_ASSUNTO_EMAIL_2", aceite.getRecebimento(), tecnico.getPessoaFisica().getNome());
		
		StringBuilder conteudo2 = new StringBuilder();
		conteudo2.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_2_PARTE_1", area.getServidorCC().getPessoaFisica().getNome(),
				aceite.getRecebimento(), tecnico.getPessoaFisica().getNome()));
		if (aceite.getAf() != null) {
			conteudo2.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_2_PARTE_2", afnNumero, aceite.getComplemento(), aceite.getAf()));
		}
		conteudo2.append(getResourceBundleValue("ANALISE_TECNICA_CONTEUDO_EMAIL_2_PARTE_3"));
		
		getEmailUtil().enviaEmail(remetente, destinatario2, null, assunto2, conteudo2.toString());
	}

	/**
	 * Método responsável por enviar as pendências de notificação da solicitação de análise técnica.
	 * 
	 * @param tecnicoPadrao - Técnico padrão da Área Técnica
	 * @param itens - Itens de Recebimento
	 * @param dataRecebimento - Data de Recebimento
	 * @param area - Área Técnica de Avaliação
	 * @throws ApplicationBusinessException
	 */
	private void enviarPendenciasAnaliseTecnica(PtmServAreaTecAvaliacao tecnicoPadrao, List<PtmItemRecebProvisorios> itens, Date dataRecebimento,
			PtmAreaTecAvaliacao area) throws ApplicationBusinessException {

		if (tecnicoPadrao == null) {
			AghCaixaPostal caixaPostal = adicionarPendenciaChefe(area.getServidorCC(), dataRecebimento);
			/**
			 * Removido devido melhoria de desenvolvimento #50613.
			 */
//			getPtmTicketRN().criarTicketAvaliacaoSemTecnicoPadrao(caixaPostal, itens, area.getServidorCC());
			/**
			 * Criar ticket para o chefe da área.
			 * RN11 da estória #43788 melhoria #50613.
			 */
			getPtmTicketRN().criarTicketAguardandoAtendimento(null, caixaPostal, itens, area.getServidorCC());
		} else {
			criarPendenciaComTecnicoPadrao(tecnicoPadrao.getServidor(), itens, dataRecebimento, area);
		}
	}

	/**
	 * #43788 - RN10 - Situação 2 - Se Área Técnica possui Técnico Padrão
	 * @param servTecPadrao
	 * @param itens
	 * @param dataRecebimento
	 * @param area
	 * @throws ApplicationBusinessException
	 */
	public void criarPendenciaComTecnicoPadrao(RapServidores servTecPadrao, List<PtmItemRecebProvisorios> itens, Date dataRecebimento, PtmAreaTecAvaliacao area) 
			throws ApplicationBusinessException {
		AghCaixaPostal caixaPostal = criarPendenciaTecnicoPadrao(servTecPadrao,
				dataRecebimento, area);

		/**
		 * Removido devido melhoria de desenvolvimento #50613.
		 */
//		getPtmTicketRN().criarTicketAvaliacaoComTecnicoPadrao(caixaPostal, itens, servTecPadrao);
		
		/**
		 * Criar ticket para o técnico padrão.
		 * RN12 da estória #43788 melhoria #50613.
		 */
		getPtmTicketRN().criarTicketAguardandoAtendimento(null, caixaPostal, itens, servTecPadrao);
	}

	/**
	 * Refatoração do método de criar pendência para utilização na melhoria #50614.
	 * @param servTecPadrao
	 * @param dataRecebimento
	 * @param area
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AghCaixaPostal criarPendenciaTecnicoPadrao(
			RapServidores servTecPadrao, Date dataRecebimento,
			PtmAreaTecAvaliacao area) throws ApplicationBusinessException {
		// Pendência para o técnico padrão
		AghCaixaPostal caixaPostal = new AghCaixaPostal();
		
		caixaPostal.setDthrInicio(dataRecebimento);
		caixaPostal.setDthrFim(DateUtil.adicionaDias(dataRecebimento, getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
		caixaPostal.setTipoMensagem(DominioTipoMensagemExame.A);
		caixaPostal.setMensagem(getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_PENDENCIA_ATA_TECNICO));
		caixaPostal.setCriadoEm(new Date());
		caixaPostal.setUrlAcao(getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_SOLIC_ANALISE_TEC));
		caixaPostal.setTituloAbaAcao(TITULO_ABA_ACAO);
		
		List<RapServidores> servidor = new ArrayList<RapServidores>();
		servidor.add(servTecPadrao);
		
		getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostal, servidor, false);
		
		// Pendência para o chefe
		AghCaixaPostal caixaPostal2 = new AghCaixaPostal();
		
		caixaPostal2.setDthrInicio(dataRecebimento);
		caixaPostal2.setDthrFim(DateUtil.adicionaDias(dataRecebimento, getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
		caixaPostal2.setTipoMensagem(DominioTipoMensagemExame.I);
		caixaPostal2.setMensagem(getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_PENDENCIA_ATA));
		caixaPostal2.setCriadoEm(new Date());
		
		List<RapServidores> servidor2 = new ArrayList<RapServidores>();
		servidor2.add(area.getServidorCC());
		
		getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostal2, servidor2, false);
		return caixaPostal;
	}
	
	/**
	 * #43446 - RN14 - Criação de Pendencia 
	 * @param tecnico
	 * @param listaItensRecebProv
	 * @param dataRecebimento
	 * @throws ApplicationBusinessException
	 */
	public void criarPendenciaSemTecnicoPadrao(RapServidores tecnico, Date dataRecebimento, 
			PtmStatusTicket statusTicket) 
			throws ApplicationBusinessException {
		AghCaixaPostal caixaPostal = new AghCaixaPostal();
		
		caixaPostal.setDthrInicio(dataRecebimento);
		caixaPostal.setDthrFim(DateUtil.adicionaDias(dataRecebimento, getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
		caixaPostal.setTipoMensagem(DominioTipoMensagemExame.A);
		caixaPostal.setMensagem(getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_PENDENCIA_ATA_TECNICO));
		caixaPostal.setCriadoEm(new Date());
		caixaPostal.setUrlAcao(getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_SOLIC_ANALISE_TEC));
		caixaPostal.setTituloAbaAcao(TITULO_ABA_ACAO);
		
		List<RapServidores> servidores = new ArrayList<RapServidores>();
		servidores.add(tecnico);
		
		getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostal, servidores, false);
		
		/**
		 * Criando usuário e associando ao status
		 * RN16 da melhoria #50614
		 */
		getPtmTicketRN().criarUsuarioRelacionadoStatus(statusTicket, caixaPostal, tecnico);
	}
	
	public AghCaixaPostal adicionarPendenciaChefe(RapServidores servidor, Date dataInicio) throws ApplicationBusinessException {

		// Pendência para o chefe
		AghCaixaPostal caixaPostal = new AghCaixaPostal();
		
		caixaPostal.setDthrInicio(dataInicio);
		caixaPostal.setDthrFim(DateUtil.adicionaDias(dataInicio,
				getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO)));
		caixaPostal.setTipoMensagem(DominioTipoMensagemExame.A);
		caixaPostal.setMensagem(getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_PENDENCIA_ATA));
		caixaPostal.setCriadoEm(new Date());
		caixaPostal.setUrlAcao(getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_SOLIC_ANALISE_TEC));
		caixaPostal.setTituloAbaAcao(TITULO_ABA_ACAO);
		
		List<RapServidores> servidorList = new ArrayList<RapServidores>();
		servidorList.add(servidor);
		
		getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostal, servidorList, false);
		
		return caixaPostal;
	}

	public IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	public PtmItemRecebProvisoriosDAO getPtmItemRecebProvisoriosDAO() {
		return ptmItemRecebProvisoriosDAO;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public ICentralPendenciaFacade getCentralPendenciaFacade() {
		return centralPendenciaFacade;
	}

	public EmailUtil getEmailUtil() {
		return emailUtil;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public PtmServAreaTecAvaliacaoDAO getPtmServAreaTecAvaliacaoDAO() {
		return ptmServAreaTecAvaliacaoDAO;
	}

	public PtmTicketRN getPtmTicketRN() {
		return ptmTicketRN;
	}
	
}