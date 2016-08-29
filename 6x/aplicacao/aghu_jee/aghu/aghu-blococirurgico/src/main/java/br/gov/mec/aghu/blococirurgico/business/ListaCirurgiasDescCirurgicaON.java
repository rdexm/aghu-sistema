package br.gov.mec.aghu.blococirurgico.business;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAvalPreSedacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoTecnicasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaEquipeAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaTipoAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFiguraDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNotaAdicionaisDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.TelaListaCirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.dominio.DominioCaraterCirurgia;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoes;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoesId;
import br.gov.mec.aghu.model.MbcAvalPreSedacao;
import br.gov.mec.aghu.model.MbcAvalPreSedacaoId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoItensId;
import br.gov.mec.aghu.model.MbcDescricaoTecnicas;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricaoId;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaPaciente;
import br.gov.mec.aghu.model.MbcFichaTipoAnestesia;
import br.gov.mec.aghu.model.MbcFiguraDescricoes;
import br.gov.mec.aghu.model.MbcNotaAdicionais;
import br.gov.mec.aghu.model.MbcProcDescricoes;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcProfDescricoesId;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.CidAtendimentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


/**
 * Classe responsável pela regras especificas de Descrição Cirúrgica chamadas
 * a partir da tela Lista de Cirurgias.
 * 
 * @author dpacheco
 *
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength","PMD.CouplingBetweenObjects" })
@Stateless
public class ListaCirurgiasDescCirurgicaON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(ListaCirurgiasDescCirurgicaON.class);

	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDescricaoTecnicasDAO mbcDescricaoTecnicasDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private MbcProcDescricoesDAO mbcProcDescricoesDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcFichaAnestesiasDAO mbcFichaAnestesiasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcFichaTipoAnestesiaDAO mbcFichaTipoAnestesiaDAO;

	@Inject
	private MbcFichaPacienteDAO mbcFichaPacienteDAO;

	@Inject
	private MbcNotaAdicionaisDAO mbcNotaAdicionaisDAO;

	@Inject
	private MbcDiagnosticoDescricaoDAO mbcDiagnosticoDescricaoDAO;

	@Inject
	private MbcProfDescricoesDAO mbcProfDescricoesDAO;
	
	@Inject
	MbcAvalPreSedacaoDAO mbcAvalPreSedacaoDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcAnestesiaDescricoesDAO mbcAnestesiaDescricoesDAO;

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;

	@Inject
	private MbcFichaEquipeAnestesiaDAO mbcFichaEquipeAnestesiaDAO;

	@Inject
	private MbcFiguraDescricoesDAO mbcFiguraDescricoesDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private IPrescricaoMedicaFacade iPrescricaoMedicaFacade;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade iBlocoCirurgicoProcDiagTerapFacade;

	@EJB
	private ProfDescricoesRN profDescricoesRN;

	@EJB
	private MbcDescricaoTecnicasRN mbcDescricaoTecnicasRN;

	@EJB
	private DescricaoCirurgicaRN descricaoCirurgicaRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private DescricaoItensRN descricaoItensRN;

	@EJB
	private MbcProcDescricoesRN mbcProcDescricoesRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private MbcFiguraDescricoesRN mbcFiguraDescricoesRN;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;

	@EJB
	private ListaCirurgiasDescCirurgicaComplON listaCirurgiasDescCirurgicaComplON;

	@EJB
	private MbcNotaAdicionaisRN mbcNotaAdicionaisRN;

	@EJB
	private AnestesiaDescricoesRN anestesiaDescricoesRN;
	
	@EJB
	private AvalPreSedacaoRN avalPreSedacaoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1251875449743187628L;
	
	public static final String PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA = "realizarDescricaoCirurgica";
	public static final String PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA_PDT = "realizarDescricaoCirurgicaPDT";
	public static final String PERMISSAO_ACAO_EXECUTAR = "executar";
	public static final String PERMISSAO_ACAO_CONSULTAR = "consultar";

	public enum ListarCirurgiasDescCirurgicaONExceptionCode implements BusinessExceptionCode {
		ERRO_USUARIO_SEM_PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA, ERRO_PROFISSIONAL_NAO_INTEGRANTE_DESCRICAO_CIRURGICA, 
		ERRO_PRAZO_EDICAO_DESCRICAO_CIRURGICA_VENCIDO, ERRO_USUARIO_ADM_NAO_PERMITIDO_DESCRICAO_CIRURGICA,
		ERRO_USUARIO_NAO_PERMITIDO_DESCRICAO_PDT, MBC_00694, MBC_00696, MBC_00695, MBC_01802, MBC_00698, 
		MBC_00699, MBC_00674, MBC_00880, MBC_01088, MBC_01147, MBC_00734, PDT_00133, MENSAGEM_ERRO_INFORME_INTERCORRENCIA, MENSAGEM_ERRO_INFORME_PERDA_SANG_SIGNIF, MENSAGEM_ERRO_INFORME_VOLUME_PERDA_SANG, MBC_01835, TITLE_VOLUME_PERDA_SANG_SUGNIF, MENSAGEM_ERRO_INFORME_ACHADOS_OPERATORIOS,MENSAGEM_ERRO_INFORME_DESCRICAO_TECNICA_DESCRICAO;
	}	
	
	/**
	 * Procedure
	 * 
	 * ORADB: EVT_WHEN_BUTTON_PRESSED (forms -> lógica para DESCRICAO: botão Descrever ou Outra)
	 * 
	 * @param telaVO
	 * @param cirurgiaVO
	 * @return Object Objeto retornado será uma instância de MbcDescricaoCirurgica ou PdtDescricao.
	 * @throws ApplicationBusinessException
	 */
	public Object executarDescreverCirurgiaOuOutra(final TelaListaCirurgiaVO telaVO, final CirurgiaVO cirurgiaVO)
			throws ApplicationBusinessException {
		String loginUsuarioLogado = telaVO.getLoginUsuarioLogado();
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorPorUsuario(loginUsuarioLogado);
		Integer crgSeq = cirurgiaVO.getCrgSeq();
		
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterCirurgiaPorSeq(crgSeq);

		verificarPrazoRealizarDescricao(telaVO, cirurgia);
		
		// Verifica tipo de cirurgia
		List<MbcProcEspPorCirurgias> listaProcEspPorCirurgias = getMbcProcEspPorCirurgiasDAO().pesquisarProcEspPorCirurgiasAtivaPrincipalPorCrgSeq(crgSeq);
		if (!listaProcEspPorCirurgias.isEmpty()) {
			MbcProcEspPorCirurgias procEspPorCirurgia = listaProcEspPorCirurgias.get(0);
			
			MbcProcedimentoCirurgicos procedimentoCirurgico = 
					getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(procEspPorCirurgia.getId().getEprPciSeq());

			if (!DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO.equals(procedimentoCirurgico.getTipo())) {
				// Administrativo não pode fazer descrição cirurgica, somente de PDT
				if (getCascaFacade().usuarioTemPermissao(
						loginUsuarioLogado, PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA, PERMISSAO_ACAO_EXECUTAR)) {
					Long countDescricaoCirurgica = getMbcDescricaoCirurgicaDAO()
					.obterCountDistinctDescricaoCirurgicaPorCrgSeqEServidor(
									crgSeq,
									servidorLogado.getId().getMatricula(),
									servidorLogado.getId().getVinCodigo());
					if (countDescricaoCirurgica == 0) {
						return carregarDescricaoCirurgica(crgSeq);
					} else {
						return obterUltimaDescricaoCirurgica(crgSeq);
					}
				} else {
					throw new ApplicationBusinessException(
							ListarCirurgiasDescCirurgicaONExceptionCode.ERRO_USUARIO_ADM_NAO_PERMITIDO_DESCRICAO_CIRURGICA);
				}
			} else { // (fet_proc.tipo = 2) --proc diag teraps (PDT)
				if (getCascaFacade().usuarioTemPermissao(
						loginUsuarioLogado, PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA_PDT, PERMISSAO_ACAO_EXECUTAR)) {
					IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade = getBlocoCirurgicoProcDiagTerapFacade();
		//			List<PdtDescricao> listaDescricao =	blocoCirurgicoProcDiagTerapFacade
		//					.pesquisarDescricaoPorCirurgiaEServidor(crgSeq, servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo());
		//			if (!listaDescricao.isEmpty()) {
		//				return listaDescricao.get(0);
		//			} else {
						return blocoCirurgicoProcDiagTerapFacade.carregarDescricaoProcDiagTerap(crgSeq);	
		//			}					
				} else {
					throw new ApplicationBusinessException(
							ListarCirurgiasDescCirurgicaONExceptionCode.ERRO_USUARIO_NAO_PERMITIDO_DESCRICAO_PDT);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: EVT_WHEN_BUTTON_PRESSED (forms -> lógica para BOTAO EDITAR descricao cirurgica ou PDT)
	 * 
	 * @param telaVO
	 * @param cirurgiaVO
	 * @return Object Objeto retornado será uma instância de MbcDescricaoCirurgica ou PdtDescricao
	 * @throws ApplicationBusinessException
	 */
	public Object executarEdicaoDescricaoCirurgica(final TelaListaCirurgiaVO telaVO, final CirurgiaVO cirurgiaVO)
			throws ApplicationBusinessException {
		String loginUsuarioLogado = telaVO.getLoginUsuarioLogado();
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorPorUsuario(loginUsuarioLogado);
		Integer servidorMatricula = servidorLogado.getId().getMatricula();
		Short servidorVinCodigo = servidorLogado.getId().getVinCodigo();
		Integer crgSeq = cirurgiaVO.getCrgSeq();
		
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterCirurgiaPorSeq(crgSeq);
		
		verificarPermissaoUsuarioRealizarDescCirurgica(loginUsuarioLogado);
		
		// Verifica se tem descr. cirurgica associada à cirurgia ou descrição de PDT
		Long countDescricaoCirurgica = getMbcDescricaoCirurgicaDAO().obterCountDescricaoCirurgicaPorCrgSeq(crgSeq);
		Long countPdt = Long.valueOf(0); 
		if (countDescricaoCirurgica == 0) {
			countPdt = getPdtDescricaoDAO().obterQuantidadePdtDescricaoPorCirurgiaSimples(crgSeq); 
		}
		
		if (countDescricaoCirurgica > 0) {
			List<MbcProfDescricoes> listaProfDescricao = getMbcProfDescricoesDAO()
					.pesquisarProfDescricoesPorCrgSeqEServidor(crgSeq, servidorMatricula, servidorVinCodigo);
			
			if (listaProfDescricao.isEmpty()) {
				List<MbcDescricaoCirurgica> descricoes = getMbcDescricaoCirurgicaDAO()
						.pesquisarDescricaoCirurgicaPorCrgSeqEServidor(
								crgSeq, servidorMatricula, servidorVinCodigo);
				
				if (descricoes.isEmpty()) {
					throw new ApplicationBusinessException(
							ListarCirurgiasDescCirurgicaONExceptionCode.ERRO_PROFISSIONAL_NAO_INTEGRANTE_DESCRICAO_CIRURGICA);
					
				} else {
					
					verificarPrazoRealizarDescricao(telaVO, cirurgia);
					return descricoes.get(0);
				}
				
				
			} else {
				verificarPrazoRealizarDescricao(telaVO, cirurgia);
				
				return listaProfDescricao.get(0).getMbcDescricaoCirurgica();
			}
			
//			return obterUltimaDescricaoCirurgica(crgSeq);
		} else if (countPdt > 0)  { // é PDT
			// Procurar por descricao pendente
			Integer seq = getPdtDescricaoDAO().obterPdtSeqPorCirurgiaEServidorPendente(crgSeq,
							servidorLogado.getId().getMatricula(),
							servidorLogado.getId().getVinCodigo());
			// Procurar por desc concluida
			if (seq == null || seq.intValue() == 0) {
				seq = getPdtDescricaoDAO().obterPdtSeqPorCirurgiaEServidor(crgSeq,
						servidorLogado.getId().getMatricula(),
						servidorLogado.getId().getVinCodigo());
			}	
			if (seq == null || seq.intValue() == 0){
				seq =  getPdtDescricaoDAO().obterPdtDescricaoPorCirurgiaEServidorPendente( crgSeq, 
																	   		 	   servidorLogado.getId().getMatricula(),
																	   		 	   servidorLogado.getId().getVinCodigo());
			}
			if (seq == null || seq.intValue() == 0){
				seq =  getPdtDescricaoDAO().obterPdtDescricaoPorCirurgiaEServidor( crgSeq, 
			   		 	   servidorLogado.getId().getMatricula(),
			   		 	   servidorLogado.getId().getVinCodigo());
			}	

			if (seq == null || seq.intValue() == 0) {											
					throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.PDT_00133);
			}
			
			
			return getPdtDescricaoDAO().obterPorChavePrimaria(seq);
			
		}
		
		return null;
	}
	
	private MbcDescricaoCirurgica obterUltimaDescricaoCirurgica(Integer crgSeq) {
		MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO = getMbcDescricaoCirurgicaDAO();
		Integer servidorMatricula = servidorLogadoFacade.obterServidorLogado().getId().getMatricula();
		Short servidorVinCodigo = servidorLogadoFacade.obterServidorLogado().getId().getVinCodigo();
		
		
		Short ultimoSeqpDescricaoCrg = mbcDescricaoCirurgicaDAO.obterMaiorSeqpDescricaoCirurgicaPorCrgSeqEServidor(
				crgSeq, servidorMatricula, servidorVinCodigo);
		
		List<MbcDescricaoCirurgica> listaDescricaoCirurgica = mbcDescricaoCirurgicaDAO.pesquisarDescricaoCirurgicaPorCrgSeqEServidor(
				crgSeq, servidorMatricula, servidorVinCodigo);
		
		for (MbcDescricaoCirurgica descricaoCirurgica : listaDescricaoCirurgica) {
			if (descricaoCirurgica.getId().getSeqp().equals(ultimoSeqpDescricaoCrg)) {
				return descricaoCirurgica;
			}
		}
		return null;
	}
	
	private void verificarPrazoRealizarDescricao(final TelaListaCirurgiaVO telaVO, final MbcCirurgias cirurgia) 
			throws ApplicationBusinessException {
		
		Integer nroDiasPrazoEdicao = telaVO.getpDiasNotaAdicional();
		Date dataLimite = DateUtil.adicionaDias(cirurgia.getData(), nroDiasPrazoEdicao);
		if (dataLimite.before(DateUtil.truncaData(new Date()))) {
			throw new ApplicationBusinessException(
					ListarCirurgiasDescCirurgicaONExceptionCode.ERRO_PRAZO_EDICAO_DESCRICAO_CIRURGICA_VENCIDO,
					nroDiasPrazoEdicao);
		}
	}
	
	/**
	 * Verifica se usuário tem permissão para realizar Descrição Cirúrgica ou
	 * Descrição de PDT.
	 * 
	 * @param loginUsuarioLogado
	 * @throws ApplicationBusinessException
	 */
	private void verificarPermissaoUsuarioRealizarDescCirurgica(
			String loginUsuarioLogado) throws ApplicationBusinessException {
		
		Boolean possuiPerfilDescricaoCirurgica = getCascaFacade()
				.usuarioTemPermissao(loginUsuarioLogado,
						PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA,
						PERMISSAO_ACAO_EXECUTAR)
				|| getCascaFacade().usuarioTemPermissao(loginUsuarioLogado,
						PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA_PDT,
						PERMISSAO_ACAO_EXECUTAR);
		if (!possuiPerfilDescricaoCirurgica) {
			throw new ApplicationBusinessException(
					ListarCirurgiasDescCirurgicaONExceptionCode.ERRO_USUARIO_SEM_PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA);
		}
	}
	
	/**
	 * Procedure (form: MBCF_DIAGNOSTICO)
	 * ORADB: MBCP_DEL_TABELAS
	 */
	public void desfazCarregamentoDescricaoCirurgica(final Integer crgSeq, final Short crgSeqp) throws ApplicationBusinessException, ApplicationBusinessException{
		final MbcProcDescricoesRN mpdRN = getMbcProcDescricoesRN();
		final List<MbcProcDescricoes> mpds = getMbcProcDescricoesDAO().obterProcDescricoes(crgSeq, crgSeqp, MbcProcDescricoes.Fields.SEQP.toString());
		for (MbcProcDescricoes mpd : mpds) {
			mpdRN.excluirMbcProcDescricoes(mpd);
		}	

		
		final ProfDescricoesRN pfdRN = getProfDescricoesRN();
		final List<MbcProfDescricoes> mpfds = getMbcProfDescricoesDAO().buscarProfDescricoes(crgSeq, crgSeqp);
		for (MbcProfDescricoes mbcProfDescricoes : mpfds) {
			pfdRN.excluirProfDescricoes(mbcProfDescricoes);
		}

		
		final MbcNotaAdicionaisRN notaAdcRN = getMbcNotaAdicionaisRN();
		final List<MbcNotaAdicionais> notasAdicionais = getMbcNotaAdicionaisDAO().obterNotasAdicionais( crgSeq, crgSeqp);
		for (MbcNotaAdicionais mbcNotaAdicional : notasAdicionais) {
			notaAdcRN.excluirMbcNotaAdicionais(mbcNotaAdicional);
		}
		
		MbcAvalPreSedacaoId id = new MbcAvalPreSedacaoId(crgSeq, crgSeqp);
		final MbcAvalPreSedacao avalPreSedacao = mbcAvalPreSedacaoDAO.obterPorChavePrimaria(id);
		if(avalPreSedacao!=null){
			avalPreSedacaoRN.removerMbcAvalPreSedacao(avalPreSedacao);
		}
		
		final AnestesiaDescricoesRN anestesiaDescricoesRN = getAnestesiaDescricoesRN();
		final List<MbcAnestesiaDescricoes> mbcAnestesiaDescricoes = getMbcAnestesiaDescricoesDAO().buscarAnestesiasDescricoes( crgSeq, crgSeqp);
		for (MbcAnestesiaDescricoes mbcAnestesiaDescricao : mbcAnestesiaDescricoes) {
			anestesiaDescricoesRN.excluirMbcAnestesiaDescricoes(mbcAnestesiaDescricao);
		}
		
		
		final DiagnosticoDescricaoRN diagnosticoDescricaoRN = getDiagnosticoDescricaoRN();
		final List<MbcDiagnosticoDescricao> diagnosticos = getMbcDiagnosticoDescricaoDAO().buscarMbcDiagnosticosDescricoes( crgSeq, crgSeqp);
		for (MbcDiagnosticoDescricao diagnosticoDescricao : diagnosticos) {
			diagnosticoDescricaoRN.excluirDiagnosticoDescricoes(diagnosticoDescricao);
		}
		
		
		final MbcDescricaoTecnicasRN mbcDescricaoTecnicasRN = getMbcDescricaoTecnicasRN();
		final List<MbcDescricaoTecnicas> descricoesTec = getMbcDescricaoTecnicasDAO().buscarMbcDescricaoTecnicas( crgSeq, crgSeqp);
		for (MbcDescricaoTecnicas mbcDescricaoTecnica : descricoesTec) {
			mbcDescricaoTecnicasRN.excluirMbcDescricaoTecnicas(mbcDescricaoTecnica);
		}
		
		final DescricaoItensRN diRN = getDescricaoItensRN();
		final List<MbcDescricaoItens> descricaoItens = getMbcDescricaoItensDAO().buscarMbcDescricaoItens( crgSeq, crgSeqp);
		for (MbcDescricaoItens mbcDescricaoItens : descricaoItens) {
			diRN.excluirMbcDescricaoItens(mbcDescricaoItens);
		}
		
		/*	// TODO eSchweigert 30/04/2013 fora de escopo, sera implantado futuramente
		   OPEN  c_imc (v_dcg_crg_seq ,v_dcg_seqp);
		   FETCH c_imc into v_existe;
		   IF c_imc%notfound then
		   	  close c_imc;
		   ELSE
		   	  BEGIN
		   	     DELETE MBC_IMAGEM_DESCRICOES
		   	      WHERE fdc_dcg_crg_seq = v_dcg_crg_seq
		            AND fdc_dcg_seqp    = v_dcg_seqp;
		          exception when others	then
				      qms$handle_ofg45_messages('E',TRUE,'Erro na deleção mbc_imagem_descricoes '||sqlerrm);	
		      END;        
		      close c_imc;
		   END IF;
		 */
		
		final MbcFiguraDescricoesRN fdRN = getMbcFiguraDescricoesRN();
		final List<MbcFiguraDescricoes> figuraDescricoes = getMbcFiguraDescricoesDAO().buscarMbcFiguraDescricoes(crgSeq, crgSeqp);
		for (MbcFiguraDescricoes mbcFiguraDescricao : figuraDescricoes) {
			fdRN.excluirMbcFiguraDescricoes(mbcFiguraDescricao);
		}
		
		final MbcDescricaoCirurgica mdc = getMbcDescricaoCirurgicaDAO().obterPorChavePrimaria(new MbcDescricaoCirurgicaId(crgSeq, crgSeqp));
		getDescricaoCirurgicaRN().excluirDescricaoCirurgica(mdc);
		
		// Certificação Digital
		getListaCirurgiasDescCirurgicaComplON().desbloqDocCertificacao(crgSeq, DominioTipoDocumento.DC);
	}
	
	public MbcProcDescricoesDAO getMbcProcDescricoesDAO(){
		return mbcProcDescricoesDAO;
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCP_CARRG_DESCRICAO
	 * 
	 * @param crgSeq
	 * @throws ApplicationBusinessException 
	 */
	public MbcDescricaoCirurgica carregarDescricaoCirurgica(Integer crgSeq) throws ApplicationBusinessException {
		MbcDescricaoCirurgica descricaoCirurgica = null;
		Boolean temCirurgia = Boolean.FALSE;
		AghEspecialidades especialidade = null;
		AghUnidadesFuncionais unidadeFuncional = null;
		DominioCaraterCirurgia carater = null;
		Date dthrInicioCirurgia = null;
		Integer pacCodigo = null;
		Short dcgSeqp = null;
		//String achados = null;
		Long ficSeq = null;
		DominioAsa asa = null;
		MbcTipoAnestesias tipoAnestesia = null;
		
		AghParametros paramUnidadeCo = null;
		paramUnidadeCo = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_UNIDADE_CO);
		
		//c_cirurgia
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq);
		
		if (cirurgia != null) {
			temCirurgia = Boolean.TRUE;
			especialidade = cirurgia.getEspecialidade();	
			unidadeFuncional = cirurgia.getUnidadeFuncional();
			
			if (DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {
				carater = DominioCaraterCirurgia.ELT;
			} else {
				carater = DominioCaraterCirurgia.URG;
			}
			
			if (cirurgia.getUnidadeFuncional().getSeq().equals(paramUnidadeCo.getVlrNumerico().shortValue())) {
				dthrInicioCirurgia = cirurgia.getDataInicioCirurgia();
			}
			
			pacCodigo = cirurgia.getPaciente().getCodigo();
		}
		
		if (temCirurgia) {
			/* Acha a proxima seqp de mbc_descricao_cirurgicas */
			Short dcgSeqpAtual = getMbcDescricaoCirurgicaDAO().obterMaiorSeqpDescricaoCirurgicaPorCrgSeq(crgSeq);
			if (dcgSeqpAtual != null) {
				dcgSeqp = (short) (dcgSeqpAtual + 1);	
			} else {
				dcgSeqp = 1;
			}		

			MbcDescricaoCirurgicaId descricaoCirurgicaId = new MbcDescricaoCirurgicaId(crgSeq, dcgSeqp);
			descricaoCirurgica = new MbcDescricaoCirurgica();
			descricaoCirurgica.setId(descricaoCirurgicaId);
			descricaoCirurgica.setDthrConclusao(null);
			descricaoCirurgica.setSituacao(DominioSituacaoDescricaoCirurgia.PEN);
			descricaoCirurgica.setCriadoEm(null);
			descricaoCirurgica.setServidor(null);
			descricaoCirurgica.setEspecialidade(especialidade);
			descricaoCirurgica.setMbcCirurgias(cirurgia);
	
			/* Inclui em mbc_descricao_cirurgicas */
			try {
				getDescricaoCirurgicaRN().inserirDescricaoCirurgica(descricaoCirurgica);
			} catch (PersistenceException e) {
				logError(e);
				throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00694, e);
			}
			
			/*
			 * Conforme validado com o analista os achados não serão mais carregados na Descrição cirurgica.
			 */
//			achados = obterAchadosProcedimentoPrincipal(crgSeq, especialidade);
//			
//			if (achados == null) {
//				achados = obterAchadosProcedimentoNaoPrincipal(crgSeq, especialidade);	
//			}
			
			// Verifica se já existe ficha de anestesia para esta cirurgia
			List<MbcFichaAnestesias> listaFichaAnestesia = getMbcFichaAnestesiasDAO().listarFichasAnestesiasPorCirurgias(crgSeq);
			if (!listaFichaAnestesia.isEmpty()) {
				// Verifica se na ficha já tem a informação do ASA do paciente
				ficSeq = listaFichaAnestesia.get(0).getSeq();
				List<MbcFichaPaciente> listaFichaPaciente = getMbcFichaPacienteDAO().pesquisarMbcFichasPacientes(ficSeq);
				if (!listaFichaPaciente.isEmpty()) {
					asa = listaFichaPaciente.get(0).getAsa();
				}
				
				// Verifica se na ficha já tem a informação do tipo de anestesia
				List<MbcFichaTipoAnestesia> listaFichaTipoAnestesia = getMbcFichaTipoAnestesiaDAO().pesquisarMbcFichasTipoAnestesias(ficSeq);
				if (!listaFichaTipoAnestesia.isEmpty()) {
					tipoAnestesia = listaFichaTipoAnestesia.get(0).getTipoAnestesia();
				}
			}
			
			/* Inclui em mbc_descricao_itens */
			MbcDescricaoItensId descricaoItensId = new MbcDescricaoItensId(crgSeq, dcgSeqp);
			MbcDescricaoItens descricaoItens = new MbcDescricaoItens();
			descricaoItens.setId(descricaoItensId);
			descricaoItens.setAsa(asa);
			descricaoItens.setAchadosOperatorios(null);
			descricaoItens.setIntercorrenciaClinica(null);
			descricaoItens.setObservacao(null);
			descricaoItens.setCarater(carater);
			descricaoItens.setDthrInicioCirg(dthrInicioCirurgia);
			descricaoItens.setDthrFimCirg(null);
			descricaoItens.setCriadoEm(null);
			descricaoItens.setServidor(null);
			descricaoItens.setMbcDescricaoCirurgica(descricaoCirurgica);
			
			try {
				getDescricaoItensRN().inserirDescricaoItens(descricaoItens);	
			} catch (PersistenceException e) {
				logError(e);
				throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00695, e);
			}
			
			inserirProfissionaisEDiagnosticoDescricao(crgSeq, unidadeFuncional, pacCodigo,
					dcgSeqp, ficSeq, descricaoCirurgica);
			
			inserirAnestesiaDescricoes(crgSeq, dcgSeqp, tipoAnestesia, descricaoCirurgica);
		
		} else {
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00674);
		}
		
		return descricaoCirurgica;
	}

	private void inserirAnestesiaDescricoes(Integer crgSeq,
			Short dcgSeqp, MbcTipoAnestesias tipoAnestesia, MbcDescricaoCirurgica descricaoCirurgica)
			throws ApplicationBusinessException {
		try {
			if (tipoAnestesia != null) {
				// Grava tipo de anestesia da ficha
				MbcAnestesiaDescricoesId anestesiaDescricaoId = new MbcAnestesiaDescricoesId(crgSeq, dcgSeqp, tipoAnestesia.getSeq());
				MbcAnestesiaDescricoes anestesiaDescricao = new MbcAnestesiaDescricoes();
				anestesiaDescricao.setId(anestesiaDescricaoId);
				anestesiaDescricao.setTipoAnestesia(tipoAnestesia);
				anestesiaDescricao.setCriadoEm(null);
				anestesiaDescricao.setServidor(null);
				anestesiaDescricao.setMbcDescricaoCirurgica(descricaoCirurgica);
				getAnestesiaDescricoesRN().inserirAnestesiaDescricoes(anestesiaDescricao);
			} else {	
				// Grava tipo de anestesia do agendamento
				List<MbcAnestesiaCirurgias> listaAnestesiaCirurgia = getMbcAnestesiaCirurgiasDAO().pesquisarAnestesiaCirurgiaTipoAnestesiaAtivoPorCrgSeq(crgSeq);
			
				if(!listaAnestesiaCirurgia.isEmpty()){
					
					MbcTipoAnestesias tipoAnestesiaAgendamento = listaAnestesiaCirurgia.get(listaAnestesiaCirurgia.size() - 1).getMbcTipoAnestesias();
					
					MbcAnestesiaDescricoesId anestesiaDescricaoId = new MbcAnestesiaDescricoesId(crgSeq, dcgSeqp, tipoAnestesiaAgendamento.getSeq());
					
					MbcAnestesiaDescricoes anestesiaDescricao = new MbcAnestesiaDescricoes();
					anestesiaDescricao.setId(anestesiaDescricaoId);
					anestesiaDescricao.setTipoAnestesia(tipoAnestesiaAgendamento);
					anestesiaDescricao.setCriadoEm(null);
					anestesiaDescricao.setServidor(null);
					anestesiaDescricao.setMbcDescricaoCirurgica(descricaoCirurgica);
					
					getAnestesiaDescricoesRN().inserirAnestesiaDescricoes(anestesiaDescricao);
					
				}
				
			}
		} catch (PersistenceException e) {
			logError(e);
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00699, e);
		}
	}

	private void inserirProfissionaisEDiagnosticoDescricao(Integer crgSeq,
			AghUnidadesFuncionais unidadeFuncional, Integer pacCodigo,
			Short dcgSeqp, Long ficSeq, MbcDescricaoCirurgica descricaoCirurgica)
			throws ApplicationBusinessException {
		
		// Acha a proxima seqp de mbc_prof_descricoes
		Integer pfdSeqp = getMbcProfDescricoesDAO().obterMaiorSeqpProfDescricoesPorDcgCrgSeqEDcgSeqp(crgSeq, dcgSeqp);
		
		if (pfdSeqp != null) {
			pfdSeqp++;
		} else {
			pfdSeqp = 1;
		}
		
		ProfDescricoesRN profDescricoesRN = getProfDescricoesRN();
		
		List<MbcProfCirurgias> listaProfCirurgias = getMbcProfCirurgiasDAO().pesquisarProfCirurgiasPorCrgSeq(crgSeq);
		
		if(!listaProfCirurgias.isEmpty()){
			
			final DominioFuncaoProfissional[] arrayFuncaoProfissional = {
					DominioFuncaoProfissional.MPF,
					DominioFuncaoProfissional.MCO,
					DominioFuncaoProfissional.MAX };
			
			MbcProfDescricoes profDescricao = new MbcProfDescricoes();
			profDescricao.setMbcDescricaoCirurgica(descricaoCirurgica);
			
			/* Inclui em  mbc_prof_descricoes */
			for (MbcProfCirurgias profCirurgia : listaProfCirurgias) {
				
				RapServidores servidorProfAtuaUnidCirgsVinc = null;
				MbcProfAtuaUnidCirgs profAtuaUnidCirgVinc = profCirurgia.getMbcProfAtuaUnidCirgsVinc();
				
				if (profAtuaUnidCirgVinc != null) {
					servidorProfAtuaUnidCirgsVinc = profAtuaUnidCirgVinc.getRapServidores();
				}
				
				try {
					
					if (profCirurgia.getIndResponsavel()) {
						
						profDescricoesRN.inserirProfDescricoes(
								popularMbcProfDescricoes(profDescricao,
										new MbcProfDescricoesId(crgSeq, dcgSeqp, pfdSeqp),
										DominioTipoAtuacao.RESP, 
										profCirurgia.getServidorPuc()));
						
						pfdSeqp++;
						
						if (servidorProfAtuaUnidCirgsVinc != null && servidorProfAtuaUnidCirgsVinc.getId().getMatricula() > 0) {
							
							profDescricoesRN.inserirProfDescricoes(
									popularMbcProfDescricoes(profDescricao,
											new MbcProfDescricoesId(crgSeq,dcgSeqp, pfdSeqp),
											DominioTipoAtuacao.SUP,
											servidorProfAtuaUnidCirgsVinc));
							
							pfdSeqp++;
							
							profDescricoesRN.inserirProfDescricoes(
									popularMbcProfDescricoes(
											profDescricao,
											new MbcProfDescricoesId(crgSeq, dcgSeqp, pfdSeqp),
											DominioTipoAtuacao.CIRG,
											servidorProfAtuaUnidCirgsVinc));
							
							pfdSeqp++;						
						}
						
					} else {
						
						if ((!profCirurgia.getIndRealizou()) && 
								isDominioFuncaoProfissionalValidado(profCirurgia.getFuncaoProfissional(), arrayFuncaoProfissional))  {
								
							profDescricoesRN.inserirProfDescricoes(
									popularMbcProfDescricoes(
											profDescricao,
											new MbcProfDescricoesId(crgSeq, dcgSeqp, pfdSeqp),
											DominioTipoAtuacao.AUX, 
											profCirurgia.getServidorPuc()));
							
							pfdSeqp++;						
						
						}
					}
					
					if (profCirurgia.getIndRealizou()) {
						
						profDescricoesRN.inserirProfDescricoes(
								popularMbcProfDescricoes(
										profDescricao, 
										new MbcProfDescricoesId(crgSeq, dcgSeqp, pfdSeqp), 
										DominioTipoAtuacao.CIRG, 
										profCirurgia.getServidorPuc()));
						
						pfdSeqp++;
					}
					
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00696, e);
					
				} catch (ApplicationBusinessException e) {
					logError(e);
					throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00696, e);
					
				}
			}	
		}
		
		inserirProfissionalFichaEquipeAnestesia(crgSeq, unidadeFuncional, pacCodigo, dcgSeqp,
				ficSeq, descricaoCirurgica, pfdSeqp, profDescricoesRN);
	}
	
	private MbcProfDescricoes popularMbcProfDescricoes(
			MbcProfDescricoes profDescricoes,
			MbcProfDescricoesId mbcProfDescricoesId,
			DominioTipoAtuacao dominioTipoAtuacao, RapServidores rapServidores) throws ApplicationBusinessException{
		
		MbcProfDescricoes mbcProfDescricoes = null;
		
		try {
			
			mbcProfDescricoes = (MbcProfDescricoes) BeanUtils.cloneBean(profDescricoes);
			mbcProfDescricoes.setId(mbcProfDescricoesId);
			mbcProfDescricoes.setTipoAtuacao(dominioTipoAtuacao);
			mbcProfDescricoes.setServidorProf(rapServidores);
			
		} catch (IllegalAccessException e) {
			logError(e);
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00696, e);
			
		} catch (InstantiationException e) {
			logError(e);
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00696, e);
			
		} catch (InvocationTargetException e) {
			logError(e);
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00696, e);
			
		} catch (NoSuchMethodException e) {
			logError(e);
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00696, e);
		}
		
		return mbcProfDescricoes;
	}
	
	private boolean isDominioFuncaoProfissionalValidado(final DominioFuncaoProfissional funcaoDoProfissional, final DominioFuncaoProfissional... dominioFuncaoProfissional) {

		if (Arrays.binarySearch(dominioFuncaoProfissional, funcaoDoProfissional) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	private void inserirProfissionalFichaEquipeAnestesia(Integer crgSeq,
			AghUnidadesFuncionais unidadeFuncional, Integer pacCodigo,
			Short dcgSeqp, Long ficSeq,
			MbcDescricaoCirurgica descricaoCirurgica, Integer pfdSeqp,
			ProfDescricoesRN profDescricoesRN)
			throws ApplicationBusinessException {
		
		IRegistroColaboradorFacade registroColaboradorFacade = getRegistroColaboradorFacade();
		
		// Se tem ficha, carrega os profissionais de anestesia da ficha
		if (ficSeq != null) {
			// Inclui os anestesistas em  mbc_prof_descricoes
			List<RapServidoresVO> listaFichaEquipeAnestesia = getMbcFichaEquipeAnestesiaDAO()
					.pesquisarFichaAnestesiasAssociadaProfAtuaUnidCirg(ficSeq,
							unidadeFuncional.getSeq());
			
			for (RapServidoresVO servidorVO : listaFichaEquipeAnestesia) {
				MbcProfDescricoes profDescricao = new MbcProfDescricoes();
				profDescricao.setId(new MbcProfDescricoesId(crgSeq, dcgSeqp, pfdSeqp));
				profDescricao.setTipoAtuacao(DominioTipoAtuacao.ANES);
				profDescricao.setNome(null);
				profDescricao.setCategoria(null);
				profDescricao.setCriadoEm(null);
				profDescricao.setServidor(null);
				
				RapServidores servidorProf = registroColaboradorFacade.obterServidor(
						new RapServidoresId(servidorVO.getMatricula(), servidorVO.getVinculo()));
				profDescricao.setServidorProf(servidorProf);
				
				profDescricao.setMbcDescricaoCirurgica(descricaoCirurgica);
				
				try {
					profDescricoesRN.inserirProfDescricoes(profDescricao);	
					pfdSeqp++;
				} catch (ApplicationBusinessException e) {
					logError(e);
					throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_01802, e.getLocalizedMessage());
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_01802, e.getLocalizedMessage());					
				}
			}
			
			List<CidAtendimentoVO> listaCidAtendimentoVO = getPrescricaoMedicaFacade()
					.pesquisarCidAtendimentoEmAndamentoOrigemInternacaoPorPacCodigo(
							pacCodigo);
			
			/* Inclui em mbc_diagnostico_descricoes */
			for (CidAtendimentoVO cidAtendimentoVO : listaCidAtendimentoVO) {
				MbcDiagnosticoDescricaoId diagnosticoDescricaoId = new MbcDiagnosticoDescricaoId(
						crgSeq, dcgSeqp, cidAtendimentoVO.getCidSeq(),
						DominioClassificacaoDiagnostico.PRE);
				MbcDiagnosticoDescricao diagnosticoDescricao = new MbcDiagnosticoDescricao();
				diagnosticoDescricao.setId(diagnosticoDescricaoId);				
				diagnosticoDescricao.setMbcDescricaoCirurgica(descricaoCirurgica);
				diagnosticoDescricao.setComplemento(null);
				diagnosticoDescricao.setDestacar(Boolean.FALSE);
				diagnosticoDescricao.setCriadoEm(null);
				diagnosticoDescricao.setServidor(null);
				
				AghCid cid = getAghuFacade().obterAghCidPorChavePrimaria(cidAtendimentoVO.getCidSeq());
				diagnosticoDescricao.setCid(cid);
				
				try {
					getDiagnosticoDescricaoRN().inserirDiagnosticoDescricoes(diagnosticoDescricao);	
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00698, e);
				}
			}
		}
	}
	
	public Boolean pFinalizaDescricao(Integer crgSeq, Short dcgSeqp, String nomeMicrocomputador, DominioSimNao indIntercorrencia, DominioSimNao indPerdaSangue, String intercorrenciaClinica, Integer volumePerdaSangue, String achadosOperatorios, String descricaoTecnicaDesc) throws BaseException {
		return getListaCirurgiasDescCirurgicaComplON().pFinalizaDescricao(crgSeq, dcgSeqp, nomeMicrocomputador,indIntercorrencia, indPerdaSangue, intercorrenciaClinica, volumePerdaSangue, achadosOperatorios, descricaoTecnicaDesc);
	}
	
	public boolean mbcpImprimeDescrCirurgica(final Boolean assinar) throws BaseException {
		return getListaCirurgiasDescCirurgicaComplON().mbcpImprimeDescrCirurgica(assinar);
	}
	
	public MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	public MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}
	
	public MbcProfDescricoesDAO getMbcProfDescricoesDAO() {
		return mbcProfDescricoesDAO;
	}
	
	public MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	public MbcFichaAnestesiasDAO getMbcFichaAnestesiasDAO() {
		return mbcFichaAnestesiasDAO;
	}
	
	public MbcFichaTipoAnestesiaDAO getMbcFichaTipoAnestesiaDAO() {
		return mbcFichaTipoAnestesiaDAO;
	}
	
	public MbcFichaPacienteDAO getMbcFichaPacienteDAO() {
		return mbcFichaPacienteDAO;
	}
	
	public MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	public MbcFichaEquipeAnestesiaDAO getMbcFichaEquipeAnestesiaDAO() {
		return mbcFichaEquipeAnestesiaDAO;
	}
	
	public MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}
	
	public DescricaoCirurgicaRN getDescricaoCirurgicaRN() {
		return descricaoCirurgicaRN;
	}
	
	public ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}
	
	public DescricaoItensRN getDescricaoItensRN() {
		return descricaoItensRN;
	}
	
	public DiagnosticoDescricaoRN getDiagnosticoDescricaoRN() {
		return diagnosticoDescricaoRN;
	}
	
	public AnestesiaDescricoesRN getAnestesiaDescricoesRN() {
		return anestesiaDescricoesRN;
	}
	
	public ListaCirurgiasDescCirurgicaComplON getListaCirurgiasDescCirurgicaComplON() {
		return listaCirurgiasDescCirurgicaComplON;
	}
	
	public IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}	
	
	public ICascaFacade getCascaFacade() {
		return iCascaFacade;
	}
	
	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	public IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.iPrescricaoMedicaFacade;
	}	
	
	public IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return iBlocoCirurgicoProcDiagTerapFacade;
	}

	public MbcProcDescricoesRN getMbcProcDescricoesRN() {
		return mbcProcDescricoesRN;
	}
	
	public MbcNotaAdicionaisRN getMbcNotaAdicionaisRN() {
		return mbcNotaAdicionaisRN;
	}

	public MbcNotaAdicionaisDAO getMbcNotaAdicionaisDAO() {
		return mbcNotaAdicionaisDAO;
	}

	public MbcAnestesiaDescricoesDAO getMbcAnestesiaDescricoesDAO() {
		return mbcAnestesiaDescricoesDAO;
	}
	
	public MbcDiagnosticoDescricaoDAO getMbcDiagnosticoDescricaoDAO() {
		return mbcDiagnosticoDescricaoDAO;
	}
	
	public MbcDescricaoTecnicasDAO getMbcDescricaoTecnicasDAO() {
		return mbcDescricaoTecnicasDAO;
	}

	public MbcDescricaoTecnicasRN getMbcDescricaoTecnicasRN() {
		return mbcDescricaoTecnicasRN;
	}
	
	public MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}
	
	public MbcFiguraDescricoesDAO getMbcFiguraDescricoesDAO() {
		return mbcFiguraDescricoesDAO;
	}
	
	public MbcFiguraDescricoesRN getMbcFiguraDescricoesRN() {
		return mbcFiguraDescricoesRN;
	}

	/**
	 * 
	 * Valor padrão de retorno: FALSE, desativando a Aba Notas Adicionais e ativando todo o resto 
	 *  
	 * Se uma Descrição está Pendente ela pode ser editada e concluída dentro de 90 dias (P_DIAS_NOTA_ADICIONAL) 
	 * contados a partir da data da cirurgia.
	 * 
	 * Se uma Descrição está Concluída ela pode ser editada e até excluída dentro de 48 hrs (P_MIN_ALTERA_DESCR)
	 *  a contar da data de conclusão da descrição somente pelo usuário que incluiu.
	 *  
	 */
	public boolean habilitarNotasAdicionais(final Integer crgSeq, final Short dcgSeqp) throws ApplicationBusinessException, ApplicationBusinessException{

		final MbcDescricaoCirurgicaDAO dao = getMbcDescricaoCirurgicaDAO();
		final MbcDescricaoCirurgica descricaoCirurgica = dao.obterPorChavePrimaria(new MbcDescricaoCirurgicaId(crgSeq, dcgSeqp));
		dao.refresh(descricaoCirurgica);		

		if(descricaoCirurgica != null){
			if(CoreUtil.igual(servidorLogadoFacade.obterServidorLogado(), descricaoCirurgica.getServidor())){
				
				final Integer vLimite = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_NOTA_ADICIONAL);
				
				final Integer qtDias  = DateUtil.obterQtdDiasEntreDuasDatasTruncadas( descricaoCirurgica.getMbcCirurgias().getData(),
																							new Date()
																					);
				boolean limiteEdicao = (qtDias > vLimite);
				
				if(limiteEdicao){
					// Caso tenha ultrapassado o limite de edição, não se pode efetuar nenhuma alteração
					throw new ApplicationBusinessException(
							ListarCirurgiasDescCirurgicaONExceptionCode.ERRO_PRAZO_EDICAO_DESCRICAO_CIRURGICA_VENCIDO, vLimite);
				} else {
					
					if(DominioSituacaoDescricaoCirurgia.PEN.equals(descricaoCirurgica.getSituacao()) ){
						return false;
						
					} else if(DominioSituacaoDescricaoCirurgia.CON.equals(descricaoCirurgica.getSituacao())){
						
						final Integer vLimiteEdicaoConclusao = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_MIN_ALTERA_DESCR);
						final int minutosEntreDatas = DateUtil.obterQtdMinutosEntreDuasDatas(descricaoCirurgica.getDthrConclusao(),new Date());
						
						boolean result = (minutosEntreDatas > vLimiteEdicaoConclusao);
						
						// Caso tenha estourado o tempo, e a situação esteja concluida, 
						// deve desabilitar todas as abas exceto Notas Adicionais
						if(result){
							return true;
							
						} else {
							return false;
						}
					}
				}
				
			} else {
				// Caso seja outro usuário, deve habilitar as Notas Adicionais apenas quando a Descrição já estiver concluida
				if(DominioSituacaoDescricaoCirurgia.CON.equals(descricaoCirurgica.getSituacao()) ){
					return true;
					
				} else {
					return false;
				}
				
			}
		}
		
		return false;
	} 
	
	public PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}

	public MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}
}