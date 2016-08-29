package br.gov.mec.aghu.patrimonio.business;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
//import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.casca.dao.UsuarioDAO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaixaPostalServidorDAO;
import br.gov.mec.aghu.dominio.DominioFormaIdentificacaoCaixaPostal;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioStatusAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioTipoMensagemExame;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacaoJn;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmItemRecebProvisoriosJn;
import br.gov.mec.aghu.model.PtmServAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmTicket;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.PtmAreaTecAvaliacaoRN.PtmAreaTecAvaliacaoRNExceptionCode;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmAreaTecnicaAvaliacaoJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmItemRecebProvisoriosJnDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmServAreaTecAvaliacaoDAO;
import br.gov.mec.aghu.patrimonio.dao.PtmTicketDAO;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVOComparator;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PtmAreaTecAvaliacaoRN extends BaseBusiness implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8978388519668133757L;
	
	private static final Log LOG = LogFactory.getLog(PtmAreaTecAvaliacaoRN.class);
	private static final String MENSAGEM_PENDENCIA_TECNICO = "MENSAGEM_PENDENCIA_TECNICO";
	private static final String MENSAGEM_PENDENCIA_CHEFE = "MENSAGEM_PENDENCIA_CHEFE";
	private static final String MENSAGEM_PENDENCIA_RESPONSAVEL_TECNICO = "MENSAGEM_PENDENCIA_RESPONSAVEL_TECNICO";
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	@Inject 
	private UsuarioDAO usuarioDAO;
	@Inject 
	private PerfilDAO perfilDAO;
	@Inject 
	private PerfisUsuariosDAO perfisUsuariosDAO;
	@Inject
	private PtmAreaTecnicaAvaliacaoJnDAO ptmAreaTecnicaAvaliacaoJnDAO;
	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;
	@Inject
	private PtmItemRecebProvisoriosDAO ptmItemRecebProvisoriosDAO; 
	@Inject
	private PtmServAreaTecAvaliacaoDAO ptmServAreaTecAvaliacaoDAO;
	@Inject
	private AghCaixaPostalDAO aghCaixaPostalDAO;
	@Inject
	private AghCaixaPostalServidorDAO aghCaixaPostalServidorDAO;
	@Inject
	private EmailUtil emailUtil;
	@Inject
	private PtmItemRecebProvisoriosJnDAO ptmItemRecebProvisoriosJnDAO;
	@Inject
	private PtmTicketDAO ptmTicketDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	// FIXME: Avoid unused private fields such as 'aghuFacade'
//	@EJB
//	private IAghuFacade aghuFacade;
	
	@EJB 
	private ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private PtmAreaTecAvaliacaoDAO ptmAreaTecAvaliacaoDAO;
	
	@EJB
	private ICentralPendenciaFacade centralPendenciaFacade;

	@EJB
	private IPermissionService permissionService;
	
	@EJB
	private PtmTicketRN tmTicketRN;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	public enum PtmAreaTecAvaliacaoRNExceptionCode implements BusinessExceptionCode {
		RESPONSAVEL_TECNICO_NAO_ENCONTRADO, MENSAGEM_RESTRICAO_SEM_ITEM_PROVISORIO, MENSAGEM_ENVIAR_PARAMETRO,
		AGH_PARAMETRO_NAO_EXISTENTE_PTM_AREA_TEC_AVALIACAO, USUARIO_SEM_PERFIL_EDICAO;
	}
	
		
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Persistir PtmAreaTecAvaliacao
	 * 
	 * @param areaTecAvaliacao
	 * @throws BaseException
	 * @throws ApplicationBusinessException
	 */
	public void persistir(PtmAreaTecAvaliacao areaTecAvaliacao, AghuParametrosEnum perfil)throws BaseException, ApplicationBusinessException{
			
		if(areaTecAvaliacao.getSeq() != null){
			
			RapServidores servidorLogado =  servidorLogadoFacade.obterServidorLogado();
			boolean pm03 = cascaFacade.usuarioTemPermissao(servidorLogado.getUsuario(), "vincularUsuarioTecnico");
			
			if(pm03){
				boolean respAreaTecnica = patrimonioFacade.isResponsavelAreaTecnico(servidorLogadoFacade.obterServidorLogado(), areaTecAvaliacao.getSeq());
				if(respAreaTecnica){
					this.atualizar(areaTecAvaliacao, perfil);
				}else{
					throw new ApplicationBusinessException(PtmAreaTecAvaliacaoRNExceptionCode.USUARIO_SEM_PERFIL_EDICAO);
				}
			}else{
				this.atualizar(areaTecAvaliacao, perfil);
			}
		}else{
			this.inserir(areaTecAvaliacao, perfil);
		}
	}
	
	/**
	 * Inserir PtmAreaTecAvaliacao
	 * 
	 * @param areaTecAvaliacao
	 * @throws BaseException
	 */
	protected void inserir(PtmAreaTecAvaliacao areaTecAvaliacao, AghuParametrosEnum perfil)throws BaseException{
		this.preInserir(areaTecAvaliacao);
		this.getPtmAreaTecAvaliacaoDAO().persistir(areaTecAvaliacao);
		this.perfisUsuariosDAO.flush();
		this.posGravarAtualizar(areaTecAvaliacao, perfil);
		this.inserirJournalPtmAreaTecnicaAvaliacao(areaTecAvaliacao, DominioOperacoesJournal.INS);
	}
	
	/**
	 * Seta o servidor logado em PtmAreaTecAvaliacao
	 * @param areaTecAvaliacao
	 * @throws BaseException
	 */
	protected void preInserir(PtmAreaTecAvaliacao areaTecAvaliacao)throws BaseException{
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		areaTecAvaliacao.setServidor(servidorLogado);		
	}
	
	/**
	 * Atualizar PtmAreaTecAvaliacao
	 */
	public void atualizar(PtmAreaTecAvaliacao areaTecAvaliacao, AghuParametrosEnum perfil)throws BaseException{
		
		final PtmAreaTecAvaliacao original = this.getPtmAreaTecAvaliacaoDAO().obterOriginal(areaTecAvaliacao);
		
		this.getPtmAreaTecAvaliacaoDAO().merge(areaTecAvaliacao);
		this.getPtmAreaTecAvaliacaoDAO().flush();

		RapServidores servidororiginal = original.getServidorCC();
		List<PtmAreaTecAvaliacao> listAreasServidor = pesquisarAreaTecAtivaPorServidor(servidororiginal);
		
		//verifica se existe há alguma outra area tecnica ativa para o usuário em questão 
		if( listAreasServidor == null || (listAreasServidor != null && listAreasServidor.isEmpty() ) ){
			this.verificarMudancaResponsavel(original, areaTecAvaliacao,perfil);
		}
		
		//preInserirJournal
		preInserir(areaTecAvaliacao);
		this.inserirJournalPtmAreaTecnicaAvaliacao(areaTecAvaliacao, DominioOperacoesJournal.UPD);
		
		// FIXME: Avoid unused variable
//		List<AghCaixaPostal> aghCaixaPostalList = aghuFacade.pesquisarMensagemPendenciasCaixaPostal(servidororiginal);
		
		//posAtualizar
		if(areaTecAvaliacao.getSituacao().isAtivo()){
			this.posGravarAtualizar(areaTecAvaliacao, perfil);
		}
		
		//verifica se existe há alguma outra area tecnica ativa para o usuário em questão - casos de ativação e inativação da área
		if( listAreasServidor == null || (listAreasServidor != null && listAreasServidor.isEmpty() ) ){
			removerPerfilUsuarioAdm68(perfil, original);
		}else{
			boolean tudoInativo = true;
			for (PtmAreaTecAvaliacao areaTecAval : listAreasServidor) {
				if(areaTecAval.getSituacao().isAtivo()){
					tudoInativo = false;
					break;
				}
			}
			if(tudoInativo){
				removerPerfilUsuarioAdm68(perfil, original);
			}
		}
		
		List<PtmTicket> ptmTickets = ptmTicketDAO.obterTicketPorServidor(servidororiginal);
		
		for (PtmTicket ticket : ptmTickets) {
			getTmTicketRN().atribuirTicketNovoResponsavel(ticket.getSeq(), null, areaTecAvaliacao.getServidorCC());
		}
		
	}

	/**
	 * 
	 */
	private void removerPerfilUsuarioAdm68(AghuParametrosEnum perfil, PtmAreaTecAvaliacao areaTec) throws ApplicationBusinessException {
		String perfilTecnico = getParametroFacade().buscarValorTexto(perfil);
		Usuario usuario = this.usuarioDAO.recuperarUsuario(areaTec.getServidorCC().getUsuario());
		PerfisUsuarios perfisUsuariosExcluir = this.perfisUsuariosDAO.pesquisarPerfisUsuariosPorUsuarioPerfil(usuario, perfilDAO.pesquisarPerfil(perfilTecnico));
		
		//remove perfil pois a área foi inativa e o mesmo não é chefe de nenhuma outra.
		if(perfisUsuariosExcluir != null){
			this.perfisUsuariosDAO.remover(perfisUsuariosExcluir);
		}
	}
	
	/**
	 *  Acrescenta o perfil P_PERFIL_CHEFE_AREA_AVAL ao usuário responsavel
	 */
	
	private void posGravarAtualizar(PtmAreaTecAvaliacao areaTecAvaliacao, AghuParametrosEnum perfilEnum) throws BaseException{
		
		if(areaTecAvaliacao!= null){
			
			// buscar usuario
			RapServidores servidor = rapServidoresDAO.obter(areaTecAvaliacao.getServidorCC().getId()); 
				Usuario usuario = usuarioDAO.recuperarUsuario(servidor.getUsuario());
			
			// buscar perfil - ADM68
			String perfilTecnico = getParametroFacade().buscarValorTexto(perfilEnum);
			Perfil perfil = perfilDAO.pesquisarPerfil(perfilTecnico);

			PerfisUsuarios perfisUsuariosOriginal = this.perfisUsuariosDAO.pesquisarPerfisUsuariosPorUsuarioPerfil(usuario, perfil);
			
			// insere somente se nao houver ainda 
			if(perfisUsuariosOriginal == null){
				//insere perfis_usuarios
					PerfisUsuarios perfisUsuarios = new PerfisUsuarios();
					perfisUsuarios.setUsuario(usuario);
					perfisUsuarios.setPerfil(perfil);
					perfisUsuarios.setDataCriacao(new Date());
					this.perfisUsuariosDAO.persistir(perfisUsuarios);
					this.perfisUsuariosDAO.flush();
			}
		}
	}
	
	/**
	 * RN04 para a permissão PM01 da estória #43449. Melhoria #49193 
	 * @param codigoCentroCusto
	 * @return List<Integer
	 */
	public List<Integer> pesquisarCentroCustoAbaixo(Integer codigoCentroCusto) throws ApplicationBusinessException {

		List<Integer> listaCodCentroCusto = new ArrayList<Integer>();
		List<Integer> listaTemp = new ArrayList<Integer>();
		
		listaTemp.add(codigoCentroCusto);
		
		do {
			listaTemp = ptmAreaTecAvaliacaoDAO.listarCodCentroCustoAbaixo(listaTemp);
			for (Integer ataSeq : listaTemp) {
				if(!listaCodCentroCusto.contains(ataSeq)){
					listaCodCentroCusto.add(ataSeq);
				}
			}
			
		} while (listaTemp != null && !listaTemp.isEmpty() );
		 
		return listaCodCentroCusto;
	}
	
	/**
	 * RN04 para a permissão PM01 da estória #43449. Melhoria #49193 
	 * @param aceiteTecnicoParaSerRealizadoVO
	 * @return List<PtmAreaTecAvaliacao>
	 */
	public List<PtmAreaTecAvaliacao> listarAreaTecAbaixoCentroCusto(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO) throws ApplicationBusinessException {
		List<PtmAreaTecAvaliacao> listaAreaTec = null;
		List<Integer> listaCodCentroCusto = new ArrayList<Integer>();

		listaCodCentroCusto = pesquisarCentroCustoAbaixo(aceiteTecnicoParaSerRealizadoVO.getCentroCusto());
		if(listaCodCentroCusto != null && !listaCodCentroCusto.isEmpty()){
			listaAreaTec = ptmAreaTecAvaliacaoDAO.listarPorCodigoCentroCusto(listaCodCentroCusto);
		}
		
		return listaAreaTec;
	}
	
	/**
	 * Verifica se houve mudança no usuario responsavel : se sim, atualiza perfil
	 * @throws BaseException 
	 */
	protected void verificarMudancaResponsavel(PtmAreaTecAvaliacao original, PtmAreaTecAvaliacao areaTecAvaliacao, AghuParametrosEnum perfilEnum) throws BaseException{	
		if(original != null && original.getServidorCC() != null && original.getServidorCC().getUsuario() != null){
			// buscar usuario
			Usuario usuario = this.usuarioDAO.recuperarUsuario(original.getServidorCC().getUsuario());
		
			// buscar perfil - MODIFICAR
			String perfilTecnico = getParametroFacade().buscarValorTexto(perfilEnum);
			Perfil perfil = perfilDAO.pesquisarPerfil(perfilTecnico);
			PerfisUsuarios perfisUsuariosExcluir = this.perfisUsuariosDAO.pesquisarPerfisUsuariosPorUsuarioPerfil(usuario, perfil);

			if(areaTecAvaliacao != null){
				if(!(original.getServidorCC().equals(areaTecAvaliacao.getServidorCC()))){
					//removerAntigo
					if(perfisUsuariosExcluir != null){
						this.perfisUsuariosDAO.remover(perfisUsuariosExcluir);
					}
					this.perfisUsuariosDAO.flush();
					//adicionarOriginal
					this.posGravarAtualizar(areaTecAvaliacao, perfilEnum);
				}
			}
		}
	}
	
	/**
	 * Registro das operacoes em PtmAreaTecAvaliacaoJN
	 * @param areaTecAvaliacao
	 * @param operacao (insert, update)
	 */
	protected void inserirJournalPtmAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecAvaliacao, DominioOperacoesJournal operacao)throws BaseException{
		
		PtmAreaTecAvaliacaoJn areaTecAvaliacaoJn = BaseJournalFactory.getBaseJournal(operacao, PtmAreaTecAvaliacaoJn.class, areaTecAvaliacao.getServidor() != null ? areaTecAvaliacao.getServidor().getUsuario() : null);
		areaTecAvaliacaoJn.setFccCentroCustos(areaTecAvaliacao.getFccCentroCustos());
		areaTecAvaliacaoJn.setNomeAreaTecAvaliacao(areaTecAvaliacao.getNomeAreaTecAvaliacao());
		areaTecAvaliacaoJn.setServidorCC(areaTecAvaliacao.getServidorCC());
		areaTecAvaliacaoJn.setSituacao(areaTecAvaliacao.getSituacao());
		areaTecAvaliacaoJn.setMensagem(areaTecAvaliacao.getMensagem());
		areaTecAvaliacaoJn.setServidor(areaTecAvaliacao.getServidor());
		areaTecAvaliacaoJn.setOperacao(operacao);
		areaTecAvaliacaoJn.setSeq(areaTecAvaliacao.getSeq());
		areaTecAvaliacaoJn.setIndEmailSumarizado(areaTecAvaliacao.getIndEmailSumarizado());
		getPtmAreaTecnicaAvaliacaoJnDAO().persistir(areaTecAvaliacaoJn);
	}
	
	/**
	 * busca o centro de custo do centro de custo superior ate anterior ao presidente 
	 * (caso extremamente raro quando há centro de custo sem reponsavel)
	 */
	public FccCentroCustos buscarCentroCustoResponsavelSuperior(FccCentroCustos fccCentroCustos, AghuParametrosEnum centroCustoPresidencia) throws ApplicationBusinessException {
		Integer valorPresidente = 0;
		
		try {
			
			if(Integer.valueOf(this.parametroFacade.buscarValorNumerico(centroCustoPresidencia).toString()) != 0){
				valorPresidente = Integer.valueOf(this.parametroFacade.buscarValorNumerico(centroCustoPresidencia).toString());
			}
			
			if(fccCentroCustos != null){
				while( fccCentroCustos.getRapServidor() == null && !fccCentroCustos.getCentroCusto().getCodigo().equals(valorPresidente)){		
					fccCentroCustos = this.centroCustoFacade.pesquisarCentroCustoAtivoPorCodigo(fccCentroCustos.getCentroCusto().getCodigo());
				}	
			}		
			
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(PtmAreaTecAvaliacaoRNExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE_PTM_AREA_TEC_AVALIACAO);
		}
		
		return fccCentroCustos;
		
	}

	/**
	 * #43464 - Obtem entidade por chave primaria (RN03).
	 * @param pk {@link Integer}
	 * @return objeto {@link PtmAreaTecAvaliacao}
	 */
	public PtmAreaTecAvaliacao obterAreaTecAvaliacaoPorChavePrimaria(Integer seq) {
		
		return ptmAreaTecAvaliacaoDAO.obterPorChavePrimaria(seq);
	}

	public PtmAreaTecAvaliacao obterAreaTecnicaPorServidor(RapServidores servidor) {

		return ptmAreaTecAvaliacaoDAO.obterPorServidor(servidor);
	}
	
	public List<PtmAreaTecAvaliacao> obterListaAreaTecnicaPorServidor(RapServidores servidor){
		
		return ptmAreaTecAvaliacaoDAO.pesquisarCriteriaPorServidor(servidor);
	}
	
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecAtivaPorServidor(RapServidores servidor){
		
		return ptmAreaTecAvaliacaoDAO.pesquisarAreaTecAtivaPorServidor(servidor);
	}
	
	
	/**
	 * RN03 para a permissão PM01 da estória #43449. 
	 * @param objPesquisa
	 * @return
	 */
	public List<RapServidores> pesquisarResponsavelTecnicoAreaTecnicaAvaliacao(
			AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO) throws ApplicationBusinessException {

		List<RapServidores> listaCentroCustoComResponsavel = new ArrayList<RapServidores>();
			Integer presidencia = this.getParametroFacade().buscarValorInteiro(
					AghuParametrosEnum.P_AGHU_CENTRO_CUSTO_PRESIDENCIA);
			PtmAreaTecAvaliacao areaTecnicaAvaliacao;
			FccCentroCustos fccCentroCusto = null;
			FccCentroCustos fccCentroCustoSuperior = null;
			RapServidores servidor = null;
			
			//Pegar o centro de custo(CC1) que a solicitação de avaliação pertence atualmente
			areaTecnicaAvaliacao = this.getPtmAreaTecAvaliacaoDAO().obterOriginal(aceiteTecnicoParaSerRealizadoVO.getAreaTecAvaliacao());
			fccCentroCusto = this.getFccCentroCustosDAO().obterOriginal(areaTecnicaAvaliacao.getFccCentroCustos());
			
			do {
				
				if(fccCentroCustoSuperior != null){//subir um nível(põe CCS no lugar de CC1 e recomeça o loop)
					fccCentroCusto = fccCentroCustoSuperior;
				}
				
				//obter o centro de custo superior
				fccCentroCustoSuperior = this.getFccCentroCustosDAO().obterOriginal(fccCentroCusto.getCentroCusto());
				//obtem servidor responsável
				if(fccCentroCustoSuperior.getRapServidor() != null && fccCentroCustoSuperior.getRapServidor().getId() != null){
					servidor = this.getRapServidoresDAO().obter(fccCentroCustoSuperior.getRapServidor().getId());
				}
				
			} while (!presidencia.equals(fccCentroCustoSuperior.getCodigo()) && (servidor == null || (servidor != null && servidor.getId() == null) ) );
			//verificar se CCS tem responsável, se tiver retorna esse responsável e fim 
			if(!presidencia.equals(fccCentroCustoSuperior.getCodigo())){
				listaCentroCustoComResponsavel.add(servidor);
			}
 
		return listaCentroCustoComResponsavel;

	}
	
	
	/**
	 * RN01
	 * @param areaTecnicaAvalicao
	 * @param aceiteTecnicoParaSerRealizadoVO
	 * @throws ApplicationBusinessException
	 * @throws ParseException
	 */
	public void enviarEmailSolicitacaoTecnicaAnalise(PtmAreaTecAvaliacao areaTecnicaAvaliacao, RapServidores responsavel,
			AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO, boolean permissaoChefiaAreaTecnicaAvaliacao, boolean permissaoChefiaPatrimonio)
					throws ApplicationBusinessException, ParseException {
		//Incializando variáveis.
		String assunto = null;
		List<RapServidores> destinatarios = new ArrayList<RapServidores>();
		List<PtmItemRecebProvisorios> listaItemRecebProvisorios = new ArrayList<PtmItemRecebProvisorios>();
		String destinatarioTecnico = null;
		String destinatarioChefe = null;
		String nomeChefe = null;
		String emailChefeSemTecnico = null;
		String emailResponsavelTecnico = null;
		AghCaixaPostal caixaPostal = null;
		PtmServAreaTecAvaliacao tecnicoPadrao = null;
		RapServidores chefe = null;
		RapServidores servidor = null;
		AghParametros paramDominioEmail = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DOMINIO_EMAIL);
		AghParametros paramEmailPatrimonio = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_PATRIMONIO);
		String remetente = paramEmailPatrimonio.getVlrTexto();
		
		listaItemRecebProvisorios = this.getPtmItemRecebProvisoriosDAO().pesquisarItemRecebProvisorios(aceiteTecnicoParaSerRealizadoVO.getRecebimento(), 
				aceiteTecnicoParaSerRealizadoVO.getItemRecebimento(), servidor);
		
		if(listaItemRecebProvisorios != null && !listaItemRecebProvisorios.isEmpty()){
			
			if(areaTecnicaAvaliacao != null && responsavel == null){			
				tecnicoPadrao = this.getPtmServAreaTecAvaliacaoDAO().consultarTecnicoPadrao(areaTecnicaAvaliacao);
				//Obter chefe da área técnica
				chefe = this.getRapServidoresDAO().pesquisarServidorPorMatricula(areaTecnicaAvaliacao);
				//E-mail e nome do Chefe
				destinatarioChefe = chefe.getUsuario() + paramDominioEmail.getVlrTexto();
				nomeChefe = chefe.getPessoaFisica().getNome();
				destinatarios.add(chefe);
				if (tecnicoPadrao != null) {
					destinatarioTecnico = tecnicoPadrao.getServidor().getUsuario() + paramDominioEmail.getVlrTexto();
					String nomeTecnico = tecnicoPadrao.getServidor().getPessoaFisica().getNome();
					assunto = super.getResourceBundleValue("ASSUNTO_EMAIL_TECNICO_PADRAO_SOLICITACAO_ANALISE_TECNICA_2", aceiteTecnicoParaSerRealizadoVO.getRecebimento(), nomeTecnico);

					enviarEmailsPendencia(aceiteTecnicoParaSerRealizadoVO, nomeTecnico, nomeChefe, areaTecnicaAvaliacao, tecnicoPadrao, listaItemRecebProvisorios,
							destinatarios, remetente, destinatarioTecnico, destinatarioChefe, assunto, chefe);
				}
			}
			
			// Quando for selecionado ID02 e que não possui técnico padrão.
			// RN01 da estória 43449
			if(areaTecnicaAvaliacao != null && tecnicoPadrao == null){
					if(tecnicoPadrao == null){
						assunto = super.getResourceBundleValue("ASSUNTO_EMAIL_TECNICO_PADRAO_SOLICITACAO_ANALISE_TECNICA_3_4", aceiteTecnicoParaSerRealizadoVO.getRecebimento());
						//Mensagem 3 da estória #43449
						emailChefeSemTecnico = montarEnvioEmailParaChefeSemTecnico(aceiteTecnicoParaSerRealizadoVO, nomeChefe);
						
						//Atualiza os PtmItemRecebProvisorios
						montarItemRecebProvisorios(areaTecnicaAvaliacao, tecnicoPadrao, listaItemRecebProvisorios);
						
						//Cria a Caixa Postal
						caixaPostal = montarPendenciaChefeArea(listaItemRecebProvisorios);
						//Cria Pendência
						this.getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostal, destinatarios, false);
						
						// RN07 da estória 44286 - Melhoria - 47225 - Atualização do ticket
						List<PtmTicket> listaTicket = pesquisarTicketVinculadoItemRecebimento(aceiteTecnicoParaSerRealizadoVO.getSeqItemPatrimonio().longValue());
						this.getTmTicketRN().atualizarTicketAvaliacaoSemTecnicoPadrao2(caixaPostal, listaTicket, chefe);
						
						emailUtil.enviaEmail(remetente, destinatarioChefe, null, assunto, emailChefeSemTecnico);
					}
				}else if(responsavel != null){ // Quando for selecionado ID03.
					if(permissaoChefiaAreaTecnicaAvaliacao || permissaoChefiaPatrimonio){//ID03 com permissão PM01 ou PM02
					
						List<PtmAreaTecAvaliacao> listaTemp = ptmAreaTecAvaliacaoDAO.pesquisarAreaTecAtivaPorServidor(responsavel);
						if(listaTemp.size() == 0){
							assunto = super.getResourceBundleValue("ASSUNTO_EMAIL_TECNICO_PADRAO_SOLICITACAO_ANALISE_TECNICA_3_4", aceiteTecnicoParaSerRealizadoVO.getRecebimento());
							
							//Consulta o responsável selecionado no ID03
							chefe = this.getRapServidoresDAO().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(responsavel.getId().getMatricula(), responsavel.getId().getVinCodigo());
							nomeChefe = chefe.getPessoaFisica().getNome();
							//Mensagem 4 da estória #43449
							emailChefeSemTecnico = montarEnvioEmailParaChefeSemTecnicoSemArea(aceiteTecnicoParaSerRealizadoVO, nomeChefe);
							//Atualiza os PtmItemRecebProvisorios
							montarItemRecebProvisorios(null, null, listaItemRecebProvisorios);
							//Retorna a caixa postal depois de criar pendência para o chefe
							
							//rn10 - 47225
							caixaPostal = montarPendenciaChefeArea2(listaItemRecebProvisorios);//rn10 - 43449
							destinatarios.add(chefe);
							this.getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostal, destinatarios, false);
							
							List<PtmTicket> listaTicket = pesquisarTicketVinculadoItemRecebimento(aceiteTecnicoParaSerRealizadoVO.getSeqItemPatrimonio().longValue());
							//rn13 - 47225
							this.getTmTicketRN().atualizarTicketAvaliacaoSemTecnicoPadrao(caixaPostal, listaTicket, responsavel);
							destinatarioChefe = responsavel.getUsuario() + paramDominioEmail.getVlrTexto();
							emailUtil.enviaEmail(remetente, destinatarioChefe, null, assunto, emailChefeSemTecnico);
							
						}else{
							assunto = super.getResourceBundleValue("ASSUNTO_EMAIL_TECNICO_PADRAO_SOLICITACAO_ANALISE_TECNICA_5", aceiteTecnicoParaSerRealizadoVO.getRecebimento());
							destinatarioChefe = responsavel.getUsuario() + paramDominioEmail.getVlrTexto();
							emailResponsavelTecnico = montarEnvioEmailParaResponsavelTecnico(aceiteTecnicoParaSerRealizadoVO, responsavel.getPessoaFisica().getNome());
							emailUtil.enviaEmail(remetente, destinatarioChefe, null, assunto, emailResponsavelTecnico);
							
							//rn15 - 47225
							destinatarios.add(responsavel);
							caixaPostal = montarPendenciaResponsavelTecnico(listaItemRecebProvisorios);
							this.getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostal, destinatarios, false);
							
							List<PtmTicket> listaTicket = pesquisarTicketVinculadoItemRecebimento(aceiteTecnicoParaSerRealizadoVO.getSeqItemPatrimonio().longValue());
							//rn13 - 47225       												  
							this.getTmTicketRN().atualizarTicketAvaliacaoComResponsavelTecnico(caixaPostal, listaTicket, responsavel);
						}
					}
				}
			}else{
				throw new ApplicationBusinessException(PtmAreaTecAvaliacaoRNExceptionCode.MENSAGEM_RESTRICAO_SEM_ITEM_PROVISORIO);
			}
		
		
	}

	private void enviarEmailsPendencia(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO, String nomeTecnico, String nomeChefe,
			PtmAreaTecAvaliacao areaTecnicaAvaliacao, PtmServAreaTecAvaliacao tecnicoPadrao, List<PtmItemRecebProvisorios> listaItemRecebProvisorios,
			List<RapServidores> destinatarios, String remetente, String destinatarioTecnico, String destinatarioChefe, String assunto, RapServidores chefe)
					throws ApplicationBusinessException, ParseException {

		//Mensagem 1 da estória #43449
		String emailTecnico = montarEnvioEmailParaTecnico(aceiteTecnicoParaSerRealizadoVO, nomeTecnico);

		//Mensagem 2 da estória #43449
		String emailChefe = montarEnvioEmailParaChefe(aceiteTecnicoParaSerRealizadoVO, nomeChefe, nomeTecnico);
		
		if (areaTecnicaAvaliacao != null) {
			//Atualiza os PtmItemRecebProvisorios
			montarItemRecebProvisorios(areaTecnicaAvaliacao, tecnicoPadrao, listaItemRecebProvisorios);
			//Criando Pendência para o chefe
			this.getCentralPendenciaFacade().adicionarPendenciaParaServidores(montarPendenciaChefeArea2(listaItemRecebProvisorios), destinatarios, false);
			destinatarios = new ArrayList<RapServidores>();
			if(!tecnicoPadrao.getServidor().getId().equals(chefe.getId())){					
				destinatarios.add(tecnicoPadrao.getServidor());
			}
			//Retorna a caixa postal após de criar pendência para técnico
			AghCaixaPostal caixaPostalTecnico = montarPendenciaTecnicoArea(listaItemRecebProvisorios);
			this.getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostalTecnico, destinatarios, false);
			// RN11 - estória #43449 - melhoria - #47225 - Atualização do Ticket // C3 - estória #43449 - melhoria - #47225
			List<PtmTicket> listaTicket = pesquisarTicketVinculadoItemRecebimento(aceiteTecnicoParaSerRealizadoVO.getSeqItemPatrimonio().longValue());
			this.getTmTicketRN().atualizarTicketAvaliacaoComTecnicoPadrao(caixaPostalTecnico, listaTicket, tecnicoPadrao.getServidor());
		}
		
		emailUtil.enviaEmail(remetente, destinatarioTecnico, null, assunto, emailTecnico);
		emailUtil.enviaEmail(remetente, destinatarioChefe, null, assunto, emailChefe);
	}
	
//	private void enviarEmailsPendenciaChefeAreaTecnica(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO, String nomeTecnico, String nomeChefe,
//			PtmAreaTecAvaliacao areaTecnicaAvaliacao, PtmServAreaTecAvaliacao tecnicoPadrao, List<PtmItemRecebProvisorios> listaItemRecebProvisorios,
//			List<RapServidores> destinatarios, String remetente, String destinatarioTecnico, String destinatarioChefe, String assunto, RapServidores chefe)
//					throws ApplicationBusinessException, ParseException {
//
////		//Mensagem 1 da estória #43449
//		String emailTecnico = montarEnvioEmailParaTecnico(aceiteTecnicoParaSerRealizadoVO, nomeTecnico);
//
//		//Mensagem 2 da estória #43449
//		String emailChefe = montarEnvioEmailParaChefe(aceiteTecnicoParaSerRealizadoVO, nomeChefe, nomeTecnico);
//		
//		if (areaTecnicaAvaliacao != null) {
//			//Atualiza os PtmItemRecebProvisorios
//			montarItemRecebProvisorios(areaTecnicaAvaliacao, tecnicoPadrao, listaItemRecebProvisorios);
//			//Criando Pendência para o chefe
//			this.getCentralPendenciaFacade().adicionarPendenciaParaServidores(montarPendenciaChefeArea(listaItemRecebProvisorios), destinatarios, false);
//			destinatarios = new ArrayList<RapServidores>();
//			if(!tecnicoPadrao.getServidor().getId().equals(chefe.getId())){					
//				destinatarios.add(tecnicoPadrao.getServidor());
//			}
//			//Retorna a caixa postal após de criar pendência para técnico
//			AghCaixaPostal caixaPostalTecnico = montarPendenciaTecnicoArea(listaItemRecebProvisorios);
//			this.getCentralPendenciaFacade().adicionarPendenciaParaServidores(caixaPostalTecnico, destinatarios, false);
//			// Situação 2 da RN01 da estória 44286 - Criação do Ticket
//			this.getTmTicketRN().criarTicketAvaliacaoComTecnicoPadrao(caixaPostalTecnico, listaItemRecebProvisorios, tecnicoPadrao.getServidor());
//		}
//		
//		emailUtil.enviaEmail(remetente, destinatarioTecnico, null, assunto, emailTecnico);
//		emailUtil.enviaEmail(remetente, destinatarioChefe, null, assunto, emailChefe);
//	}

	private AghCaixaPostal montarPendenciaChefeArea(
			List<PtmItemRecebProvisorios> listaItemRecebProvisorios) throws ApplicationBusinessException {
		
		AghCaixaPostal pendencia = new AghCaixaPostal();
		for (PtmItemRecebProvisorios itemRecebProvisorios : listaItemRecebProvisorios) {
			
			pendencia.setDthrInicio(itemRecebProvisorios.getDataUltimaAlteracao());
			Integer diasUteis = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO);
			pendencia.setDthrFim(DateUtil.adicionaDias(pendencia.getDthrInicio(), diasUteis));
			pendencia.setTipoMensagem(DominioTipoMensagemExame.A);		
			pendencia.setMensagem(getResourceBundleValue(MENSAGEM_PENDENCIA_CHEFE));
			pendencia.setCriadoEm(new Date());
			pendencia.setUrlAcao(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SOLIC_ANALISE_TEC));
			pendencia.setAcaoObrigatoria(DominioSimNao.N.isSim());
			pendencia.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
		}
		
		return pendencia;
	}
	
	//rn15 - 47225
	private AghCaixaPostal montarPendenciaResponsavelTecnico(
			List<PtmItemRecebProvisorios> listaItemRecebProvisorios) throws ApplicationBusinessException {
		
		AghCaixaPostal pendencia = new AghCaixaPostal();
			
			pendencia.setDthrInicio(new Date());
			Integer diasUteis = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO);
			pendencia.setDthrFim(DateUtil.adicionaDias(pendencia.getDthrInicio(), diasUteis));
			pendencia.setTipoMensagem(DominioTipoMensagemExame.A);		
			pendencia.setMensagem(getResourceBundleValue(MENSAGEM_PENDENCIA_RESPONSAVEL_TECNICO));
			pendencia.setCriadoEm(new Date());
			pendencia.setUrlAcao(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SOLIC_ANALISE_TEC));
			pendencia.setAcaoObrigatoria(DominioSimNao.S.isSim());
			pendencia.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
		
		return pendencia;
	}
	
	//rn10 - 47225
	private AghCaixaPostal montarPendenciaChefeArea2(
			List<PtmItemRecebProvisorios> listaItemRecebProvisorios) throws ApplicationBusinessException {
		
		AghCaixaPostal pendencia = new AghCaixaPostal();
			
			pendencia.setDthrInicio(new Date());
			Integer diasUteis = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO);
			pendencia.setDthrFim(DateUtil.adicionaDias(pendencia.getDthrInicio(), diasUteis));
			pendencia.setTipoMensagem(DominioTipoMensagemExame.A);		
			pendencia.setMensagem(getResourceBundleValue(MENSAGEM_PENDENCIA_CHEFE));
			pendencia.setCriadoEm(new Date());
			pendencia.setUrlAcao(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SOLIC_ANALISE_TEC));
			pendencia.setAcaoObrigatoria(DominioSimNao.S.isSim());
			pendencia.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
			
		return pendencia;
	}
	
	//rn12 - 47225
	private AghCaixaPostal montarPendenciaTecnicoArea(
			List<PtmItemRecebProvisorios> listaItemRecebProvisorios) throws ApplicationBusinessException {
		
		AghCaixaPostal pendencia = new AghCaixaPostal();
		for (PtmItemRecebProvisorios itemRecebProvisorios : listaItemRecebProvisorios) {
			
			pendencia.setDthrInicio(itemRecebProvisorios.getDataUltimaAlteracao());
			Integer diasUteis = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_DIAS_UTEIS_NOTIFICACAO_RECEBIMENTO);
			pendencia.setDthrFim(DateUtil.adicionaDias(pendencia.getDthrInicio(), diasUteis));
			pendencia.setTipoMensagem(DominioTipoMensagemExame.A);			
			pendencia.setMensagem(getResourceBundleValue(MENSAGEM_PENDENCIA_TECNICO));
			pendencia.setCriadoEm(new Date());
			pendencia.setUrlAcao(this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SOLIC_ANALISE_TEC));
			pendencia.setAcaoObrigatoria(DominioSimNao.N.isSim());
			pendencia.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
		}
		
		return pendencia;
	}
	
	private String montarEnvioEmailParaTecnico(
			AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO,
			String nomeTecnico) {

		String conteudoComAf;
		String conteudoSemAf;
		String email;

		if (aceiteTecnicoParaSerRealizadoVO.getAf() != null
				&& aceiteTecnicoParaSerRealizadoVO.getAf() != 0) {
			conteudoComAf = super
					.getResourceBundleValue("CONTEUDO_UM_EMAIL_COM_AF_SOLICITACAO_ANALISE_TECNICA");
			email = MessageFormat.format(conteudoComAf, nomeTecnico,
					aceiteTecnicoParaSerRealizadoVO.getRecebimento(),
					aceiteTecnicoParaSerRealizadoVO.getAf(),
					aceiteTecnicoParaSerRealizadoVO.getComplemento(),
					aceiteTecnicoParaSerRealizadoVO.getNroSolicCompras());
		} else {
			conteudoSemAf = super
					.getResourceBundleValue("CONTEUDO_UM_EMAIL_SEM_AF_SOLICITACAO_ANALISE_TECNICA");
			email = MessageFormat.format(conteudoSemAf, nomeTecnico,
					aceiteTecnicoParaSerRealizadoVO.getRecebimento());
		}
		return email;
	}
	
	private String montarEnvioEmailParaChefe(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO,
			String nomeChefe, String nomeTecnico){
		
		String conteudoComAf;
		String conteudoSemAf;
		String email;
		
		if (aceiteTecnicoParaSerRealizadoVO.getAf() != null
				&& aceiteTecnicoParaSerRealizadoVO.getAf() != 0) {
			conteudoComAf = super.getResourceBundleValue("CONTEUDO_DOIS_EMAIL_COM_AF_SOLICITACAO_ANALISE_TECNICA");
	        email = MessageFormat.format(conteudoComAf,
	        nomeChefe, aceiteTecnicoParaSerRealizadoVO.getRecebimento(),
	        nomeTecnico, aceiteTecnicoParaSerRealizadoVO.getAf(),
	        aceiteTecnicoParaSerRealizadoVO.getComplemento(),
			aceiteTecnicoParaSerRealizadoVO.getNroSolicCompras());
		} else {
			conteudoSemAf = super
					.getResourceBundleValue("CONTEUDO_DOIS_EMAIL_SEM_AF_SOLICITACAO_ANALISE_TECNICA");
			email = MessageFormat.format(conteudoSemAf, nomeChefe,
					aceiteTecnicoParaSerRealizadoVO.getRecebimento(),
					nomeTecnico);
		}
		
        return email;
	}
	
	/**
	 * Método que monta e-mail para cehfe sem técnico.
	 * RN01
	 * @param aceiteTecnicoParaSerRealizadoVO
	 * @param nomeChefe
	 * @return
	 */
	private String montarEnvioEmailParaChefeSemTecnico(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO,
			String nomeChefe){
		
		String conteudoComAf;
		String conteudoSemAf;
		String email;
		
		if (aceiteTecnicoParaSerRealizadoVO.getAf() != null
				&& aceiteTecnicoParaSerRealizadoVO.getAf() != 0) {
			conteudoComAf = super.getResourceBundleValue("CONTEUDO_TRES_EMAIL_COM_AF_SOLICITACAO_ANALISE_TECNICA");
	        email = MessageFormat.format(conteudoComAf,
	        nomeChefe, aceiteTecnicoParaSerRealizadoVO.getRecebimento(),
	        aceiteTecnicoParaSerRealizadoVO.getAf(),
	        aceiteTecnicoParaSerRealizadoVO.getComplemento(),
			aceiteTecnicoParaSerRealizadoVO.getNroSolicCompras());
		} else {
			conteudoSemAf = super.getResourceBundleValue("CONTEUDO_TRES_EMAIL_SEM_AF_SOLICITACAO_ANALISE_TECNICA");
			email = MessageFormat.format(conteudoSemAf, nomeChefe,
					aceiteTecnicoParaSerRealizadoVO.getRecebimento());
		}
		
        return email;
	}
	
	private String montarEnvioEmailParaChefeSemTecnicoSemArea(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO,
			String nomeChefe){
		
		String conteudoComAf;
		String conteudoSemAf;
		String email;
		
		if (aceiteTecnicoParaSerRealizadoVO.getAf() != null
				&& aceiteTecnicoParaSerRealizadoVO.getAf() != 0) {
			conteudoComAf = super.getResourceBundleValue("CONTEUDO_QUATRO_EMAIL_COM_AF_SOLICITACAO_ANALISE_TECNICA");
	        email = MessageFormat.format(conteudoComAf,
	        nomeChefe, aceiteTecnicoParaSerRealizadoVO.getRecebimento(),
	        aceiteTecnicoParaSerRealizadoVO.getAf(),
	        aceiteTecnicoParaSerRealizadoVO.getComplemento(),
			aceiteTecnicoParaSerRealizadoVO.getNroSolicCompras());
		} else {
			conteudoSemAf = super.getResourceBundleValue("CONTEUDO_QUATRO_EMAIL_SEM_AF_SOLICITACAO_ANALISE_TECNICA");
			email = MessageFormat.format(conteudoSemAf, nomeChefe,
					aceiteTecnicoParaSerRealizadoVO.getRecebimento());
		}
		
        return email;
	}
	
	private String montarEnvioEmailParaResponsavelTecnico(AceiteTecnicoParaSerRealizadoVO aceiteTecnicoParaSerRealizadoVO,
			String responsavel){
		
		String conteudoComAf;
		String conteudoSemAf;
		String email;
		
		if (aceiteTecnicoParaSerRealizadoVO.getAf() != null
				&& aceiteTecnicoParaSerRealizadoVO.getAf() != 0) {
			conteudoComAf = super.getResourceBundleValue("CONTEUDO_QUATRO_EMAIL_COM_AF_SOLICITACAO_ANALISE_TECNICA_RESPONSAVEL");
	        email = MessageFormat.format(conteudoComAf,
	        		responsavel, aceiteTecnicoParaSerRealizadoVO.getRecebimento(),
	        aceiteTecnicoParaSerRealizadoVO.getAf(),
	        aceiteTecnicoParaSerRealizadoVO.getComplemento(),
			aceiteTecnicoParaSerRealizadoVO.getNroSolicCompras());
		} else {
			conteudoSemAf = super.getResourceBundleValue("CONTEUDO_QUATRO_EMAIL_SEM_AF_SOLICITACAO_ANALISE_TECNICA_RESPONSAVEL");
			email = MessageFormat.format(conteudoSemAf, responsavel,
					aceiteTecnicoParaSerRealizadoVO.getRecebimento());
		}
		
        return email;
	}

	private List<PtmItemRecebProvisorios> montarItemRecebProvisorios(
			PtmAreaTecAvaliacao areaTecnicaAvaliacao,
			PtmServAreaTecAvaliacao tecnicoPadrao,
			List<PtmItemRecebProvisorios> listaItemRecebProvisorios) throws ParseException {
		
		for (PtmItemRecebProvisorios itemRecebProvisorios : listaItemRecebProvisorios) {
			
			this.inserirJournalPtmItemRecebProvisorios(itemRecebProvisorios, DominioOperacoesJournal.UPD);
			
			if(areaTecnicaAvaliacao != null && areaTecnicaAvaliacao.getSeq() != null){
				itemRecebProvisorios.setAtaSeq(areaTecnicaAvaliacao.getSeq());
			}else{
				itemRecebProvisorios.setAtaSeq(null);
			}
			if(tecnicoPadrao != null && tecnicoPadrao.getTecnicoPadrao() != null){			
				itemRecebProvisorios.setServidorTecPadrao(tecnicoPadrao.getServidor());
			}else{
				itemRecebProvisorios.setServidorTecPadrao(null);
			}
			itemRecebProvisorios.setStatus(DominioStatusAceiteTecnico.TROCA_AREA_AVALIACAO_TECNICA.getCodigo());
			itemRecebProvisorios.setDataUltimaAlteracao(DateUtil.obterDataComHoraInical(new Date()));
			this.getPtmItemRecebProvisoriosDAO().atualizar(itemRecebProvisorios);
		}
		
		return listaItemRecebProvisorios;
	}
	
	/**
	 * Registro das operacoes em PtmItemRecebProvisoriosJN
	 * @param ptmItemRecebProvisorios
	 * @param operacao (insert, update)
	 */
	public void inserirJournalPtmItemRecebProvisorios(PtmItemRecebProvisorios ptmItemRecebProvisorios, DominioOperacoesJournal operacao) {
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final PtmItemRecebProvisoriosJn ptmItemRecebProvisoriosJn = new PtmItemRecebProvisoriosJn();
		
		ptmItemRecebProvisoriosJn.setNomeUsuario(servidorLogado.getUsuario());
		ptmItemRecebProvisoriosJn.setOperacao(operacao);
		if(ptmItemRecebProvisorios.getAtaSeq() != null){			
			ptmItemRecebProvisoriosJn.setAtaSeq(ptmItemRecebProvisorios.getAtaSeq());
		}
		ptmItemRecebProvisoriosJn.setDataRecebimento(ptmItemRecebProvisorios.getDataRecebimento());
		ptmItemRecebProvisoriosJn.setDataUltimaAlteracao(ptmItemRecebProvisorios.getDataUltimaAlteracao());
		ptmItemRecebProvisoriosJn.setPagamentoParcial(ptmItemRecebProvisorios.getPagamentoParcial());
		ptmItemRecebProvisoriosJn.setSceItemRecebProvisorio(ptmItemRecebProvisorios.getSceItemRecebProvisorio());
		ptmItemRecebProvisoriosJn.setSeq(ptmItemRecebProvisorios.getSeq());
		ptmItemRecebProvisoriosJn.setServidor(ptmItemRecebProvisorios.getServidor());
		if(ptmItemRecebProvisorios.getServidorTecPadrao() != null){			
			ptmItemRecebProvisoriosJn.setServidorTecPadrao(ptmItemRecebProvisorios.getServidorTecPadrao());
		}
		ptmItemRecebProvisoriosJn.setStatus(ptmItemRecebProvisorios.getStatus());
		ptmItemRecebProvisoriosJnDAO.persistir(ptmItemRecebProvisoriosJn);
		ptmItemRecebProvisoriosJnDAO.flush();
	}
	
	/**
	 * #47543
	 * Consulta do suggestionBox quando possui as permissões PM02 e PM03.
	 * @param parametro
	 * @param servidor
	 * @return lista
	 */
	public List<PtmAreaTecAvaliacao> pesquisarAreaTecnicaPorPermissoes(String parametro, RapServidores servidor){
		List<PtmAreaTecAvaliacao> listaAreasTecnicas = ptmAreaTecAvaliacaoDAO.pesquisarListaAreaTecnicaSuggestionBox(parametro, servidor);		
		listaAreasTecnicas.addAll(ptmAreaTecAvaliacaoDAO.pesquisarListaAreaTecnicaPorResponsavel(parametro, servidor));
		Set<PtmAreaTecAvaliacao> listaAreasTecnicasSet = new HashSet<PtmAreaTecAvaliacao>(listaAreasTecnicas);
		Comparator<PtmAreaTecAvaliacao> comparador = new AceiteTecnicoParaSerRealizadoVOComparator.OrderByNomeAreaTecnica();
		List<PtmAreaTecAvaliacao> listaRetorno = new ArrayList<PtmAreaTecAvaliacao>(listaAreasTecnicasSet);
		Collections.sort(listaRetorno, comparador);
		return listaRetorno;
	}
	
	public Long pesquisarAreaTecnicaPorPermissoesCount(String parametro, RapServidores servidor){
		List<PtmAreaTecAvaliacao> lista = pesquisarAreaTecnicaPorPermissoes(parametro, servidor);
		Long tamanho = Long.valueOf(lista.size());
		return tamanho;
	}
	
	
	
	/**
	 * #43449
	 * Selecionar ticket que pertence ao item de recebimento
	 * @param seqItemRecebimento
	 * @return lista
	 */
	public List<PtmTicket> pesquisarTicketVinculadoItemRecebimento(Long seqItemRecebimento){
		List<PtmTicket> listaTicket = ptmTicketDAO.obterTicketPorItemRecebimento(seqItemRecebimento);
		return listaTicket;
	}
	
	public UsuarioDAO getUsuarioDAO() {
		return usuarioDAO;
	}

	public PtmAreaTecAvaliacaoDAO getPtmAreaTecAvaliacaoDAO() {
		return ptmAreaTecAvaliacaoDAO;
	}
	
	public RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}

	public PerfilDAO getPerfilDAO() {
		return perfilDAO;
	}

	public PerfisUsuariosDAO getPerfisUsuariosDAO() {
		return perfisUsuariosDAO;
	}
	
	public PtmAreaTecnicaAvaliacaoJnDAO getPtmAreaTecnicaAvaliacaoJnDAO(){
		return this.ptmAreaTecnicaAvaliacaoJnDAO;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public FccCentroCustosDAO getFccCentroCustosDAO() {
		return fccCentroCustosDAO;
	}
	
	public PtmItemRecebProvisoriosDAO getPtmItemRecebProvisoriosDAO() {
		return ptmItemRecebProvisoriosDAO;
	}
	
	public PtmServAreaTecAvaliacaoDAO getPtmServAreaTecAvaliacaoDAO() {
		return ptmServAreaTecAvaliacaoDAO;
	}
	
	public AghCaixaPostalDAO getAghCaixaPostalDAO() {
		return aghCaixaPostalDAO;
	}

	public AghCaixaPostalServidorDAO getAghCaixaPostalServidorDAO() {
		return aghCaixaPostalServidorDAO;
	}
	
	public ICentralPendenciaFacade getCentralPendenciaFacade() {
		return centralPendenciaFacade;
	}

	public IPermissionService getPermissionService() {
		return permissionService;
	}

	public PtmTicketRN getTmTicketRN() {
		return tmTicketRN;
	}
	
}
