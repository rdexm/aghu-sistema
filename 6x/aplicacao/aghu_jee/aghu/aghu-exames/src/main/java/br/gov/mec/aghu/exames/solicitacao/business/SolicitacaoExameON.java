package br.gov.mec.aghu.exames.solicitacao.business;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.configuracao.dao.AghEquipesDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTelaOriginouSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.sismama.business.ISismamaFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.AtendimentoSolicExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ConfirmacaoImpressaoEtiquetaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVariaveisVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoColetarVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.transferir.business.ITransferirPacienteFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelSismamaHistoRes;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghImpressoraPadraoUnids;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.ByteInstantiation"})
@Stateless
public class SolicitacaoExameON extends BaseBusiness {

	private static final String ELABORAR_SOLICITACAO_EXAME = "elaborarSolicitacaoExame";
	private static final String ELABORAR_SOLICITACAO_EXAME_CONSULTA_ANTIGA = "elaborarSolicitacaoExameConsultaAntiga";
	private static final String SOLICITAR_EXAME_ESTUDANTE_MEDICINA = "solicitarExamePorEstudanteMedicina";
	private static final String SOLICITAR_EXAME_VIA_SUMARIO_ALTA = "solicitarExamesViaSumarioAlta";
	
	private static final String CLOSE_TAG_NL = "> \n";

	private static final String SEM_IMPRESSAO = "SEM_IMPRESSAO";

	@EJB
	private SolicitacaoExameRN solicitacaoExameRN;
	
	@EJB
	private ItemSolicitacaoExameON itemSolicitacaoExameON;
	
	@EJB
	private AelSismamaMamoResON aelSismamaMamoResON;
	
	@EJB
	private ItemSolicitacaoExameRN itemSolicitacaoExameRN;	
	
	@EJB
	private ItemSolicitacaoExameEnforceRN itemSolicitacaoExameEnforceRN;
	
	@EJB
	private EtiquetasON etiquetasON;
	
	private static final Log LOG = LogFactory.getLog(SolicitacaoExameON.class);
	
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
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject 
	private AacConsultasDAO aacConsultasDAO;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;
	
	@Inject
	private AghEquipesDAO aghEquipesDAO;
	
	@Inject 
	private RapServidoresDAO rapServidoresDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@EJB
	private ITransferirPacienteFacade transferirPacienteFacade;
	
	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;
	
	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private VAelSolicAtendsDAO vAelSolicAtendsDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ISismamaFacade sismamaFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;

	@EJB
	private EtiquetasRedomeON etiquetasRedomeON;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3330637364094685053L;

	public enum SolicitacaoExameONExceptionCode implements BusinessExceptionCode {
		AEL_01325// :Não é permitido solicitar exames de cirurgia ambulatorial antes do dia da cirurgia.
        , AEL_APENAS_DIA_CIRURGIA_OU_SUBSEQUENTE // Solicitar exames de cirurgia ambulatorial somente é permitida na data cirurgia ou no dia subsequente.
		, AEL_PAC_EXTERNO_SOH_UNID_EXECUTORA //: Solicitar exames para atendimento de paciente externo só é permitido para unidade executora.
		, AEL_PAC_NAO_INTERNADO_OU_MAIS_XX_DIAS //: Só é permitido solicitar exames para pacientes internados ou que tiveram alta nos ultimos P_DIAS_SOL_EX_ALTA dias.
		, AEL_APENAS_DIA_CONS_OU_XX_HRS_DA_CONS //: Só é permitido solicitar exames para esta consulta no dia da consulta OU em até P_HORAS_SOL_EX_POSTERIOR_AMB_OBSTETRICA horas após o horário de início da consulta.
		, AEL_DIA_CONSULTA_FORA_RANGE //: Só é permitido solicitar exames para consulta ambulatorial que ocorreu até P_DIAS_SOL_EX_ANTERIOR dias atrás E P_DIAS_SOL_EX_POSTERIOR dias após.
		, AEL_APENAS_DIA_CONS_OU_DIA_ANTERIOR //: Só é permitido solicitar exames para esta consulta no dia da consulta OU no dia anterior da consulta.
		, AEL_01598 // Não é permitido solicitar exames para paciente com alta médica.
		, AEL_IMPRIME_SOLICITACOES_COLETAR // Não há solicitações à imprimir
		, AEL_PAC_INTERNO_SOH_PACIENTE_INTERNO
		, AEL_01366 // Para atendimentos de paciente externo deve ser informado se transplante sim ou não.
		, AEL_01317 // Só é permitido solicitar exames para atendimento cirúrgico no dia da cirurgia e até P_DIAS_SOL_CIRURGIA dia útil após
		, PERMISAO_SOLICEX_ATD_NAO_ENCONTRADO
	}

	public void gravar(AelSolicitacaoExames solicitacaoExame, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		if(solicitacaoExame.getSeq()== null) {
			getSolicitacaoExameRN().inserirSolicitacaoExame(solicitacaoExame, nomeMicrocomputador);
		}
		else {
			getSolicitacaoExameRN().atualizarSolicitacaoExame(solicitacaoExame, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}
	
	/**
	 * Metodo responsavel por inserir AelSolicitacaoExames e seus itens AelItemSolicitacaoExames.<br>
	 * 
	 * @throws BaseException
	 */
	public SolicitacaoExameVO gravar(SolicitacaoExameVO solicitacaoExameVO, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		// Extrai o modelo com as alteracoes.
		AelSolicitacaoExames solicExSalvado = solicitacaoExameVO.getModel();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		// Validar se deve solicitar se paciente transplatado
		this.validarSolicitacaoSeTransplantado(solicitacaoExameVO);
		
		//Açoẽs pré-insert
		this.preInsert(solicitacaoExameVO, solicExSalvado);
		
		if(solicitacaoExameVO.getUnidadeFuncionalAreaExecutora() != null && solicitacaoExameVO.getUnidadeFuncionalAreaExecutora().getSeq() != null){
			solicExSalvado.setUnidadeFuncionalAreaExecutora(solicitacaoExameVO.getUnidadeFuncionalAreaExecutora());
		}
		//Realiza inserção 
		solicExSalvado = getSolicitacaoExameRN().inserir(solicExSalvado, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
		
		//Verifica se deve exibir ticket do paciente
		chamarRelatorioTicketPaciente(solicitacaoExameVO);

		//Verifica se deve exibir imprimir respostas do questionário
		this.chamarRelatorioQuestionario(solicitacaoExameVO);
		this.atualizaIdsItens(solicitacaoExameVO.getItemSolicitacaoExameVos(), solicExSalvado.getItensSolicitacaoExame());
		
		//Atualiza o id da solicitação salva na VO e desatacha o objeto antigo
		solicitacaoExameVO.setSeqSolicitacaoSalva(solicExSalvado.getSeq());
		//getAelSolicitacaoExameDAO().desatachar(solicExSalvado);
		
		this.gravarRespostaSismama(solicitacaoExameVO.getItemSolicitacaoExameVos(), solicExSalvado.getSeq());
		
		return solicitacaoExameVO;
	}
	
	
	// A geração de exames com situação Pendente exige que seus itens sejam inclusos com situação pré-definida
	// nas tabelas AelItemSolicitacaoExame e AelExtratoItemSolicitacao e então a situação atualizada na tabela AelItemSolicitacaoExame
	// inserindo novos registros em AelExtratoItemSolicitacao com situação 'PE'
	public void finalizarGeracaoSolicExamePendente(Integer seqSolicExame) throws BaseException{		
		AelSolicitacaoExames solicitacaoExame = aelSolicitacaoExameDAO.obterPorChavePrimaria(seqSolicExame);
		AelSitItemSolicitacoes situacao = aelSitItemSolicitacoesDAO.obterPeloId("PE");
		for (AelItemSolicitacaoExames item : solicitacaoExame.getItensSolicitacaoExame()){
			if (item.getItemSolicitacaoExame() != null){
				item.setSituacaoItemSolicitacao(situacao);
				aelItemSolicitacaoExameDAO.atualizar(item);
				itemSolicitacaoExameEnforceRN.atualizarExtrato(item, true);
				List<AelItemSolicitacaoExames> listaDependentes = aelItemSolicitacaoExameDAO.pesquisarItensSolicitacaoExameDependentes(item.getId().getSoeSeq(), item.getId().getSeqp());
				for (AelItemSolicitacaoExames dependente : listaDependentes) {
					dependente.setSituacaoItemSolicitacao(situacao);
					aelItemSolicitacaoExameDAO.atualizar(dependente);
					itemSolicitacaoExameEnforceRN.atualizarExtrato(dependente, true);
				}
			}
		}
	}

	private void gravarRespostaSismama(final List<ItemSolicitacaoExameVO> itemSolicitacaoExameVos, final Integer soeSeq)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		for (final ItemSolicitacaoExameVO itemSolicitacaoExameVO : itemSolicitacaoExameVos) {
			if (itemSolicitacaoExameVO.getMostrarAbaQuestionarioSismama() && itemSolicitacaoExameVO.getQuestionarioSismama() != null && !itemSolicitacaoExameVO.getQuestionarioSismama().isEmpty()) {
				
				final Map<DominioSismamaMamoCadCodigo, Object> values = new HashMap<DominioSismamaMamoCadCodigo, Object>();
				final Map<String,Object> mapSismama = this.getAelSismamaMamoResRN().inicializarMapSismama();
				for (final String key : mapSismama.keySet()) {
					final Object valor = itemSolicitacaoExameVO.getQuestionarioSismama().get(key);
					if (valor == null) {
						values.put(DominioSismamaMamoCadCodigo.valueOf(key), mapSismama.get(key));
					} else {
						values.put(DominioSismamaMamoCadCodigo.valueOf(key), valor);
					}
				}

				this.getAelSismamaMamoResRN().gravarAelSismamaMamoRes(values, soeSeq, itemSolicitacaoExameVO.getItemSolicitacaoExame().getId().getSeqp());
			}
			
			if (itemSolicitacaoExameVO.getQuestionarioSismamaBiopsia() != null) {
				salvarQuestionarioSismamaBiopsia(itemSolicitacaoExameVO.getQuestionarioSismamaBiopsia(), servidorLogado, itemSolicitacaoExameVO.getItemSolicitacaoExame());
			}	
		}
	}
	
	
	public void salvarQuestionarioSismamaBiopsia(Map<String, Object> respostas, RapServidores rap, AelItemSolicitacaoExames itemSolEx) throws ApplicationBusinessException{
		Set<String> codigos = respostas.keySet();
		for (String cod : codigos) {	
			Object resposta =  respostas.get(cod);
			if (resposta!=null){
				AelSismamaHistoRes resPersist = getSismamaFacade().criaResposta(itemSolEx, cod, resposta, rap);
				getSismamaFacade().salvarAelSismamaHistoRes(resPersist);
			}	
		}
	}	

	private void atualizaIdsItens(final List<ItemSolicitacaoExameVO> itemSolicitacaoExameVos, final List<AelItemSolicitacaoExames> itensSolicitacaoExame) {
		for (final AelItemSolicitacaoExames aelItemSolicitacaoExames : itensSolicitacaoExame) {
			interno:
			for (final ItemSolicitacaoExameVO vos : itemSolicitacaoExameVos) {
				if(vos.getSequencial() != null 
						&& aelItemSolicitacaoExames.getIndexOrigem() == vos.getSequencial().intValue()){
					vos.setItemSolicitacaoExame(aelItemSolicitacaoExames);
					break interno;
				}
			}
		}
	}

	protected void chamarRelatorioQuestionario(final SolicitacaoExameVO solicitacaoExameVO) {
		if (solicitacaoExameVO.getUnidadeTrabalho() == null
				|| (this.getAghuFacade().validarCaracteristicaDaUnidadeFuncional(solicitacaoExameVO.getUnidadeTrabalho().getSeq(),
						ConstanteAghCaractUnidFuncionais.TICKET_EXAME_PAC_EXTERNO) && (solicitacaoExameVO.getAtendimento() != null && DominioOrigemAtendimento.X.equals(solicitacaoExameVO.getAtendimento().getOrigem())))) {
			if (solicitacaoExameVO.getTelaOriginouSolicitacao() != DominioTelaOriginouSolicitacaoExame.TELA_AMBULATORIO 
					&& solicitacaoExameVO.getAtendimento() != null && !DominioOrigemAtendimento.I.equals(solicitacaoExameVO.getAtendimento().getOrigem()) 
					) {
				solicitacaoExameVO.setImprimirQuestionario(true);
			}
		}

	}

	protected void preInsert(SolicitacaoExameVO solicitacaoExameVO, AelSolicitacaoExames solicEx) throws BaseException {
		//Executa a chamada para as validações implementadas ao adicionar cada item individualmente
		for(ItemSolicitacaoExameVO itemSolicitacaoExameVO : solicitacaoExameVO.getItemSolicitacaoExameVos()) {

			//RN5 e RN6
			if (this.getItemSolicitacaoExameON().verificarNecessidadeExamesPosAlta(itemSolicitacaoExameVO)) {
				this.getItemSolicitacaoExameON().verificarExamesPosAlta(itemSolicitacaoExameVO);
			}

			//RN9
			if (this.getItemSolicitacaoExameON().verificarNecessidadeInformacoesClinicas(itemSolicitacaoExameVO)) {
				this.getItemSolicitacaoExameON().verificarInformacoesClinicas(itemSolicitacaoExameVO);
			}

			//RN15
			this.getItemSolicitacaoExameON().verificarProgRotina(itemSolicitacaoExameVO);

		}

		//Chama validações do forms
		gerarItensAmostra(solicitacaoExameVO, solicEx);
		if(solicitacaoExameVO != null && solicitacaoExameVO.getIsSus() != null){
			verificarColetaRestrita(solicitacaoExameVO, solicEx);
		}
		atualizarNroAmostrasPrimeiroItem(solicEx);
	}
	
	private void atualizarNroAmostrasPrimeiroItem(AelSolicitacaoExames solicEx){
		if (!solicEx.getItensSolicitacaoExame().isEmpty()){
			if (solicEx.getItensSolicitacaoExame().get(0).getNroAmostras() != null && solicEx.getItensSolicitacaoExame().get(0).getNroAmostras() > 1){
				solicEx.getItensSolicitacaoExame().get(0).setNroAmostras(new Byte("1"));
			}
		}
	}

	/**
	 * Executa açõs após a persistência da solicitação.
	 *  
	 */
	public List<String> executarValidacoesPosGravacaoSolicitacaoExame(SolicitacaoExameVO solicitacaoExameVO,
			String nomeMicrocomputador, final Date dataFimVinculoServidor,
			List<ConfirmacaoImpressaoEtiquetaVO> listaConfirmaImpressaoEtiquetas, AghUnidadesFuncionais unidadeExecutora)
			throws BaseException {
		if (listaConfirmaImpressaoEtiquetas == null || listaConfirmaImpressaoEtiquetas.isEmpty()) {
			return null;
		}
		//Atualiza os objetos desatachados
		
		AelSolicitacaoExames solicitacaoExame = getAelSolicitacaoExameDAO().obterPeloId(
				solicitacaoExameVO.getSeqSolicitacaoSalva());
		this.getAelSolicitacaoExameDAO().refresh(solicitacaoExame);
		
		atualizarItensPendentesAmbulatorio(solicitacaoExameVO, solicitacaoExame, nomeMicrocomputador,
				dataFimVinculoServidor);
		
		AghUnidadesFuncionais unidadeTrabalho = null;
		if (solicitacaoExameVO.getUnidadeTrabalho() != null) {
			unidadeTrabalho = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(
					solicitacaoExameVO.getUnidadeTrabalho().getSeq());
			this.refresh(unidadeTrabalho);	
		}
		
		List<String> respostaImpressoras = new LinkedList<>();
		for (ConfirmacaoImpressaoEtiquetaVO confirmaImpressaoEtiqueta : listaConfirmaImpressaoEtiquetas) {
			String respostaImpressora = null;
			try {
				respostaImpressora = imprimirEtiquetas(confirmaImpressaoEtiqueta, nomeMicrocomputador,
						solicitacaoExame, unidadeTrabalho, unidadeExecutora);
			} catch (UnknownHostException e) {
				LOG.error(e.getMessage());
			}
			respostaImpressoras.add(respostaImpressora);
		}
		return respostaImpressoras;
	}

	private String imprimirEtiquetas(ConfirmacaoImpressaoEtiquetaVO confirmaImpressaoEtiqueta,
			String nomeMicrocomputador, AelSolicitacaoExames solicitacaoExame, AghUnidadesFuncionais unidadeTrabalho,
			AghUnidadesFuncionais unidadeExecutora) throws BaseException, UnknownHostException {
		String respostaImpressora;
		if (confirmaImpressaoEtiqueta != null && confirmaImpressaoEtiqueta.isRedome() != null
				&& confirmaImpressaoEtiqueta.isRedome()) {
			respostaImpressora = imprimirEtiquetasRedome(confirmaImpressaoEtiqueta, nomeMicrocomputador,
					solicitacaoExame, unidadeExecutora);
		} else {
			respostaImpressora = imprimirEtiquetasNormais(confirmaImpressaoEtiqueta, nomeMicrocomputador,
					solicitacaoExame, unidadeTrabalho);
		}
		return respostaImpressora;
	}

	private String imprimirEtiquetasNormais(ConfirmacaoImpressaoEtiquetaVO confirmaImpressaoEtiqueta,
			String nomeMicrocomputador, AelSolicitacaoExames solicitacaoExame, AghUnidadesFuncionais unidadeTrabalho)
			throws BaseException {

		if (confirmaImpressaoEtiqueta.getConfirmaImpressao() || confirmaImpressaoEtiqueta.getConfirmaImpressaoSemModal()) {
			String nomeImpressoraEtiqueta = getNomeImpressoraEtiquetas(nomeMicrocomputador);
			if (nomeImpressoraEtiqueta != null) {			
				chamarEtiquetaBarras(solicitacaoExame, unidadeTrabalho, nomeImpressoraEtiqueta,
						confirmaImpressaoEtiqueta.getIndSituacaoExameImpressao());
				return nomeImpressoraEtiqueta;
			} else {
				return null;
			}
		}
		return "SEM_IMPRESSAO";
	}

	private String imprimirEtiquetasRedome(ConfirmacaoImpressaoEtiquetaVO confirmaImpressaoEtiqueta,
			String nomeMicrocomputador, AelSolicitacaoExames solicitacaoExame, AghUnidadesFuncionais unidadeExecutora)
			throws BaseException, UnknownHostException {
		if (confirmaImpressaoEtiqueta.getConfirmaImpressao() || confirmaImpressaoEtiqueta.getConfirmaImpressaoSemModal()) {
			String nomeImpressoraEtiqueta = etiquetasRedomeON.obterNomeImpressoraEtiquetasRedome(
					nomeMicrocomputador);
			if (nomeImpressoraEtiqueta != null) {
				chamarEtiquetaBarrasRedome(solicitacaoExame, unidadeExecutora, nomeImpressoraEtiqueta,
						confirmaImpressaoEtiqueta.getIndSituacaoExameImpressao());
				return nomeImpressoraEtiqueta;
			} else {
				return null;
			}
		}
		return SEM_IMPRESSAO;
	}
	
	 private String getNomeImpressoraEtiquetas(String nomeMicro) throws ApplicationBusinessException {
	        
	        AghMicrocomputador microcomputador = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(nomeMicro,null);
	        
	        if (microcomputador != null && microcomputador.getImpressoraEtiquetas() != null) {
	        	return microcomputador.getImpressoraEtiquetas();
	        }
	        
	        if (microcomputador != null && microcomputador.getAghUnidadesFuncionais() != null) {
	        	List<AghImpressoraPadraoUnids> lista = aghuFacade.listarAghImpressoraPadraoUnids(microcomputador.getAghUnidadesFuncionais().getSeq(), TipoDocumentoImpressao.ETIQUETAS_BARRAS);
	        	if (!lista.isEmpty()) {
		        	if (lista.get(0).getImpImpressora() != null) {
		        		return lista.get(0).getImpImpressora().getFilaImpressora();
		        	}
		        	if (lista.get(0).getNomeImpressora() != null) {
		        		return lista.get(0).getNomeImpressora();
		        	}
	        	}
	        }
	        return null;
	    }

	public SolicitacaoExameVO buscaSolicitacaoExameVO(Integer atendimentoSeq, Integer atendimentoDiversoSeq) throws ApplicationBusinessException {
		if (atendimentoSeq == null && atendimentoDiversoSeq == null) {
			throw new IllegalArgumentException("Ao menos um dos parametros de atendimento devem ser informados. (atendimentoSeq ou atendimentoDiversoSeq)");
		}
		SolicitacaoExameVO returnValeu = null;

		if (atendimentoSeq != null) {
			returnValeu = this.getSolicitacaoExameAtendimento(atendimentoSeq);
		}

		if (atendimentoDiversoSeq != null) {
			returnValeu = this.getSolicitacaoExameAtendimentoDiverso(atendimentoDiversoSeq);
		}

		return returnValeu;
	}

	public  List<AelItemSolicitacaoExames> buscarItensExames(Integer seq) throws ApplicationBusinessException {
		return this.getAelSolicitacaoExameDAO().buscarItensExames(seq);

	}

	public  List<AelItemSolicitacaoExames> buscarItensExamesAExecutar(Integer seq, Short unfSeq) throws ApplicationBusinessException{
		return this.getAelSolicitacaoExameDAO().buscarItensExamesAExecutar(seq,unfSeq, this.obterSituacaoExameAExecutar());

	}



	public  List<AelAmostraItemExames> buscarAmostrasItemExame(Integer soeSeq, Short itemSeq) throws ApplicationBusinessException {
		return this.getAelSolicitacaoExameDAO().buscarAmostrasItemExame(soeSeq,itemSeq);

	}


	public VAelSolicAtendsVO buscaExameCancelamentoSolicRespons(Integer soeSeq) throws BaseException {
		if (soeSeq == null) {
			throw new IllegalArgumentException("Código de solicitação do exame não informado para consulta.");
		}
		VAelSolicAtendsVO vo = null;
		AelSolicitacaoExames solicitacaoExames = this.getAelSolicitacaoExameDAO().obterPorChavePrimaria(soeSeq);
		if(solicitacaoExames != null && solicitacaoExames.getAtendimento()!=null){
			AghAtendimentos atendimentos = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(solicitacaoExames.getAtendimento().getSeq());
			vo = new VAelSolicAtendsVO();
			vo.setNumero(soeSeq);
			vo.setInformacoesClinicas(solicitacaoExames.getInformacoesClinicas());
			if (atendimentos.getConsulta() != null && atendimentos.getConsulta().getNumero()!=null) {
				vo.setNumConsulta(atendimentos.getConsulta().getNumero());
			}
			if (atendimentos.getPaciente() != null && atendimentos.getPaciente().getNome()!=null) {
				vo.setPacienteDiversos(atendimentos.getPaciente().getNome());
			}
			vo.setProntuario(atendimentos.getProntuario());
			vo.setOrigem(atendimentos.getOrigem());
			if (solicitacaoExames.getServidorResponsabilidade() != null) {
				vo.setResponsavel(buscarNomeServResponsavel(Integer.valueOf(solicitacaoExames.getServidorResponsabilidade().getId().getMatricula()),solicitacaoExames.getServidorResponsabilidade().getId().getVinCodigo()) );
			}
			if (solicitacaoExames.getServidor() != null ) {
				vo.setSolicitante(solicitacaoExames.getServidor().getPessoaFisica().getNome());
			}
		}

		return vo;
	}

	/**
	 * oradb aelc_busca_nome_serv
	 * 
	 * @return
	 */
	public String buscarNomeServResponsavel(Integer matricula, Short vinCodigo){
		if(matricula != null){
			RapServidores serv = getRegistroColaboradorFacade().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(matricula,vinCodigo);
			RapPessoasFisicas pessoa = serv.getPessoaFisica();
			if(pessoa != null){
				return pessoa.getNome();
			}else{
				return "";
			}
		}
		return "";
	}


	public SolicitacaoExameVO buscaDetalhesSolicitacaoExameVO(Integer seq) throws ApplicationBusinessException {
		if (seq == null) {
			throw new IllegalArgumentException("A consulta pelo código do exame falhou.");
		}
		SolicitacaoExameVO vo = null;
		AelSolicitacaoExames solicitacaoExames = this.getAelSolicitacaoExameDAO().obterPorChavePrimaria(seq);
		if(solicitacaoExames != null && solicitacaoExames.getAtendimento()!=null){
			AghAtendimentos atendimentos = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(solicitacaoExames.getAtendimento().getSeq());
			vo = new SolicitacaoExameVO(atendimentos);
			vo.setUnidadeSolicitante(solicitacaoExames.getUnidadeFuncional().getAndarAlaDescricao());
			vo.setInformacoesClinicas(solicitacaoExames.getInformacoesClinicas());
			vo.setDthrProgramada(solicitacaoExames.getCriadoEm());
			if(solicitacaoExames.getRecemNascido()){
				vo.setRecemNascido("Sim");
			}else{
				vo.setRecemNascido("Não");
			}
		}

		return vo;
	}

	/**
	 * Metodo para listar Solicitações de Exames Por Unidade Executora.
	 * Tela de Avisos de Soliciação de Internação a executar para Unidades fechadas.
	 * 
	 * @param unidadeExecutora
	 * @return List<VAelSolicAtendsVO>
	 * @throws BaseException 
	 */
	public List<VAelSolicAtendsVO> listarSolicitacaoExamesUnExecutora(AghUnidadesFuncionais unidadeExecutora, Date dtSolicitacao) throws BaseException {
		List<Object[]> resultadoPesquisaView = this.getVAelSolicAtendsDAO().pesquisarSolicAtendsView(unidadeExecutora, this.obterSituacaoExameAExecutar(), dtSolicitacao);
		List<VAelSolicAtendsVO> listaVAelSolicAtendsVO = new LinkedList<VAelSolicAtendsVO>();

		if(resultadoPesquisaView != null && resultadoPesquisaView.size() >0 ){
			Iterator<Object[]> it = resultadoPesquisaView.iterator();

			while (it.hasNext()) {

				Object[] obj = it.next();
				VAelSolicAtendsVO vo = new VAelSolicAtendsVO();

				// Numero foi fornecido por VAelSolicAtends
				if (obj[0] != null) {
					//solicitação de Exame
					vo.setNumero((Integer)obj[0]);
					//Urgência
					//vo.setUr(this.obterBuscaUrgentes(vo.getNumero(),sitCodigo,unidadeExecutora));
					vo.setUr((String)obj[12]);

					//Prioridade
					//vo.setPrioridade(verificaExistenciaExameUrgente(vo.getNumero(), sitCodigo, unidadeExecutora.getSeq(),(Short)obj[4], (Date)obj[2]));
					vo.setPrioridade((String)obj[13]);

					// imprimiu
					//vo.setImprimiu(verificaImpressaoSOE(vo.getNumero(), sitCodigo, unidadeExecutora.getSeq(), (Short)obj[4]));
					vo.setImprimiu((String)obj[14]);
					
					vo.setPacienteGMR(((BigInteger)obj[15]).signum() == 1);
				}
				
				if (obj[1] != null) {
					vo.setProntuario((Integer)obj[16]);
					vo.setPacienteDiversos((String)obj[17]);
					vo.setLocal(recuperarLocalPaciente(obj));
				}

				// Data de solicitacao foi fornecido por VAelSolicAtends
				if (obj[3] != null) {
					vo.setDataSolicitacao((Date)obj[3]);
				}

				// Data de solicitacao foi fornecido por VAelSolicAtends
				if (obj[4] != null) {
					vo.setConvenio((String)obj[4]);
				}


				// Acrescenta item na lista de VO
				listaVAelSolicAtendsVO.add(vo);

			}

		} else{
			listaVAelSolicAtendsVO = null;
		}

		return listaVAelSolicAtendsVO;
	}
	
	public String recuperarLocalPaciente(Object[] obj) {

		String local = "   ";

        if (DominioOrigemAtendimento.A.toString().equals((String)obj[5])) {

            if (obj[11] != null) {

                local = "U:EMG";

            } else {

                local = "U:AMB";

            }

        } else if (DominioOrigemAtendimento.X.toString().equals((String)obj[5])) {

            local = "U:EXT";

        } else if (DominioOrigemAtendimento.D.toString().equals((String)obj[5])) {

            local = "U:DOA";

        } else if (DominioOrigemAtendimento.C.toString().equals((String)obj[5])) {

            local = "U:CIR";

        } else if (DominioOrigemAtendimento.U.toString().equals((String)obj[5])) {

            if ((String)obj[6] != null) {

                local = "U:" + (String)obj[6];

            } else {

                local = "   ";

            }

        } else if (obj[7] != null) {

            local = (String)obj[7]; // leitoid

        } else if (obj[8] != null) {

            local = (String)obj[8]; //quarto descricao

        } else if (obj[9] != null) {

            local = "U:"
                    + (String)obj[9]
                    + " "
                    + (String)obj[10];
        }

        return local;
    }

	public boolean isPacienteGMR(Integer numSolicitacao){
		Integer pacCodigo = obterPacCodigoPorNumSolicitacao(numSolicitacao);

		if (pacCodigo != null){		
			return mciNotificacaoGmrDAO.verificarNotificacaoGmrPorCodigo(pacCodigo);
		} else {
			return false;
		}
	}
	
	private Integer obterPacCodigoPorNumSolicitacao(Integer numSolicitacao){
		AelSolicitacaoExames solicitacaoExame = aelSolicitacaoExameDAO.obterAelSolicitacaoExameAtendimentos(numSolicitacao);
		
		Integer pacCodigo = null;
		if (solicitacaoExame.getAtendimento() != null && solicitacaoExame.getAtendimento().getSeq() != null){
			pacCodigo = solicitacaoExame.getAtendimento().getPacCodigo();
		} else {
			if (solicitacaoExame.getAtendimentoDiverso() != null && solicitacaoExame.getAtendimentoDiverso().getAipPaciente() != null){			
				pacCodigo = solicitacaoExame.getAtendimentoDiverso().getAipPaciente().getCodigo();
			}
		}

		return pacCodigo;
	}
	
	protected String obterSituacaoExameAExecutar() throws ApplicationBusinessException {
		AghParametros aghParametro = null;
		String situacao = null;
		try{
			aghParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_EXECUTAR);
			if (aghParametro != null && aghParametro.getVlrTexto() != null) {
				situacao = aghParametro.getVlrTexto();
			} else {
				throw new Exception("Erro ao recuperar parâmetro de sistema P_SITUACAO_A_EXECUTAR");
			}
		} catch (Exception e) {
			logError(e);
		}
		return situacao;
	}



 	/**
 	 * Metodo para obter Atendimento Diverso.
 	 * 
 	 * @param atendimentoDiversoSeq
 	 * @return
	 * @throws ApplicationBusinessException  
 	 */
	private SolicitacaoExameVO getSolicitacaoExameAtendimentoDiverso(Integer atendimentoDiversoSeq) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		AelAtendimentoDiversos atdDiverso = getAelAtendimentoDiversosDAO().obterPorChavePrimaria(atendimentoDiversoSeq);
		if (atendimentoDiversoSeq == null) {
			throw new IllegalArgumentException("Busca pelo campo chave falhou!!!");
		}	

		String descricaoAtendimentoDiverso = getExamesFacade().obterNomeAtendDiv(atendimentoDiversoSeq);

		SolicitacaoExameVO vo = new SolicitacaoExameVO(atdDiverso);
		vo.setDescricaoAtendimentoDiverso(descricaoAtendimentoDiverso);

		vo.setResponsavel(servidorLogado);

		return vo;
	}

        private SolicitacaoExameVO getSolicitacaoExameAtendimento(Integer atendimentoSeq) throws ApplicationBusinessException {
        	SolicitacaoExameVO vo = null;
        	if (isHCPA()) {
        	    AtendimentoSolicExameVO atendimentoSolicExameVO = getAghuFacade().obterAtendimentoSolicExameVO(atendimentoSeq);
        	    vo = new SolicitacaoExameVO(atendimentoSolicExameVO);
        	} else {
        	    AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atendimentoSeq);
        	    if (atendimento == null) {
        		throw new IllegalArgumentException("Busca pelo campo chave falhou!!!");
        	    }
        	    vo = new SolicitacaoExameVO(atendimento);
        	}
        
        	if (!((temTargetEstudanteMedicina() || temTargetExecutor()) && (vo.getNumeroConsulta() != null))
        		&& !vo.getOrigem().equals(DominioOrigemAtendimento.X)) {
        	    RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
        	    vo.setResponsavel(servidorLogado);
        	}

        	return vo;
        }

	/**
	 * Regras de Negocio para o Processo de Solicitação de Exames para um Paciente.
	 * 
	 * ORADB Form AELF_SOLICITAR_EXAME
	 * ORADB PLL AELF_SOLICITAR_EXAME_VERIFICAR
	 *   
	 * ORADB Form Procedure AELP_CONSISTE_ATD_CONSULTA
	 * ORADB Form Procedure AELP_CONSISTE_ATD
	 * 
	 * @param atendimentoSeq
	 */
	public AghAtendimentos verificarPermissoesParaSolicitarExame(Integer atendimentoSeq, boolean origemSumarioAlta) throws BaseException {
		if (atendimentoSeq == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");			
		}
		
		// Com Regra de centro de custo associado a Unidade Funcional
		AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atendimentoSeq);
		//AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentosPorSeq(atendimentoSeq);
		if (atendimento == null) {
			throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.PERMISAO_SOLICEX_ATD_NAO_ENCONTRADO, atendimentoSeq);
		}

		this.verificarPermissoesSolicitarExame(atendimento, origemSumarioAlta);		
		return atendimento;
	} // verificarPermissoesParaSolicitarExame

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	protected void verificarPermissoesSolicitarExame(AghAtendimentos atendimento, boolean origemSumarioAlta) throws BaseException {
		if (DominioOrigemAtendimento.I == atendimento.getOrigem()
				|| DominioOrigemAtendimento.N == atendimento.getOrigem()) {
			if (atendimento.getInternacao() != null) {
				Date dthrAlaMedica = getTransferirPacienteFacade().getDthrAltaMedica(atendimento.getInternacao().getSeq());
				DominioPacAtendimento indPacAtendimento = atendimento.getIndPacAtendimento();
				if (DominioPacAtendimento.N.equals(indPacAtendimento)) {
					Date dthrFim = atendimento.getDthrFim();
					if (dthrFim != null) {
						AghParametros paramSys = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_SOL_EX_ALTA);
						if (paramSys != null) {
							int dias = paramSys.getVlrNumerico().intValue();
							Date dthrComparacao = DateUtil.adicionaDias(dthrFim, dias);
							if (DateUtil.validaDataMaior(new Date(), dthrComparacao)) {
								// Não é permitido solicitar exames para paciente com alta médica
								throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_01598);
							}
						}	
					}	
				} else if (dthrAlaMedica != null) {
					// Não é permitido solicitar exames para paciente com alta médica
					throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_01598);
				}
			}
		}
		// Origem do Atendimento: Paciente, Internação, Pós-Alta, Urgência ou Nascimento.
		if (DominioOrigemAtendimento.I == atendimento.getOrigem()
				|| DominioOrigemAtendimento.U == atendimento.getOrigem()
				|| DominioOrigemAtendimento.N == atendimento.getOrigem()
		) {

			if (this.temTargetPacientesInternos()) {
				// Paciente ainda internado.
				if (atendimento.getDthrFim() != null) {
					// Se o paciente teve alta nos ultimos 30 dias.
					AghParametros paramSys = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_SOL_EX_ALTA);
					int dias = paramSys.getVlrNumerico().intValue();
					Date dataCalculada = DateUtil.adicionaDias(atendimento.getDthrFim(), dias);

					if (!DateUtil.validaDataMaiorIgual(dataCalculada, new Date())) {
						// AEL_PAC_NAO_INTERNADO_OU_MAIS_XX_DIAS: Só é permitido solicitar exames para pacientes internados ou que tiveram alta nos ultimos P_DIAS_SOL_EX_ALTA dias.
						throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_PAC_NAO_INTERNADO_OU_MAIS_XX_DIAS, dias);
					}
				}
			} else {
				throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_PAC_INTERNO_SOH_PACIENTE_INTERNO);
			}

			// Origem do Atendimento: Ambulatorio.
		} else if (DominioOrigemAtendimento.A == atendimento.getOrigem()) {
			Boolean isCO = false;
			Boolean isEmergenciaObstetrica = false;
			if (atendimento.getUnidadeFuncional() != null) {
				Short seq = atendimento.getUnidadeFuncional().getSeq(); 
				isCO = this.getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(seq, ConstanteAghCaractUnidFuncionais.CO);
				isEmergenciaObstetrica = this.getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(seq, ConstanteAghCaractUnidFuncionais.EMERGENCIA_OBSTETRICA);
			}

			// Se Paciente em consulta no Centro Obstétrico.
			// UnidadeFuncional tiver caracteristicas de CO ou Emergencia Obstétrica.
			if (isCO || isEmergenciaObstetrica) {
				this.validacaoAmbulatorioCentroObstetricoProntoAtd(atendimento, AghuParametrosEnum.P_HORAS_SOL_EX_POSTERIOR_AMB_OBSTETRICA);

				// Paciente em atendimento no Pronto Atendimento.
			} else if (atendimento.getEspecialidade() != null
					&& atendimento.getEspecialidade().getIndProntoAtendimento() != null
					&& atendimento.getEspecialidade().getIndProntoAtendimento().isSim()) {
				this.validacaoAmbulatorioCentroObstetricoProntoAtd(atendimento, AghuParametrosEnum.P_HORAS_SOL_EX_POSTERIOR_AMB_PRONTO_ATD);

				// Paciente em consulta demais zonas ambulatóriais.
			} else {
				this.validacaoAmbulatorioEOutros(atendimento, AghuParametrosEnum.P_DIAS_SOL_EX_POSTERIOR_ILIMITADO, origemSumarioAlta);
			}

			// Origem Atendimento: Externo.
		} else if (DominioOrigemAtendimento.X == atendimento.getOrigem()) {

			// ajustado conforme issue #13029
			FatConvenioSaudePlano fat = atendimento.getConvenioSaudePlano();
			
			if (!DominioGrupoConvenio.S.equals(fat.getConvenioSaude().getGrupoConvenio())) {
				if (!this.temTargetExecutor()) {
					throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_PAC_EXTERNO_SOH_UNID_EXECUTORA);
				}
			} else {
				if (!this.temTargetPacientesExternos()) {
					throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_PAC_EXTERNO_SOH_UNID_EXECUTORA);
				}
			}

			// Paciente em atendimento Cirúrgico Ambulatorial.
		} else if (DominioOrigemAtendimento.C == atendimento.getOrigem()) {
			if ((!(this.temTargetExecutor() && this.temTargetPacientesCirurgicos())) && 
					!this.ehDiaDaCirurgiaOuDiaSubsequente(atendimento)) {
				// AEL_01325: Não é permitido solicitar exames de cirurgia ambulatorial antes do dia da cirurgia.
				throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_APENAS_DIA_CIRURGIA_OU_SUBSEQUENTE);
			}

			validarAtendimentoCirurgia(atendimento.getDthrInicio());
		}
		
	} // verificarPermissoesSolicitarExame

	/**
	 * ORADB AELP_VALIDA_ATD_CIRG 
	 * 
	 * Valida a seguinte regra:
	 * 	só permitido solicitar exames para atendimento cirúrgico no dia da cirurgia e até P_DIAS_SOL_CIRURGIA dia útil após
	 * 
	 * @return {ApplicationBusinessException}
	 * @throws ApplicationBusinessException 
	 */
	private void validarAtendimentoCirurgia(Date dataCirurgia) throws ApplicationBusinessException {

		AghParametros paramDiasSolCirurgia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_SOL_CIRURGIA);
		Integer qtdMaxDiasUteisAposCirurgia = paramDiasSolCirurgia.getVlrNumerico().intValue();

		Date dataUtil = new Date();
		Date dataCorrente = new Date();
		Date dataValida = DateUtil.adicionaDias(dataCirurgia, qtdMaxDiasUteisAposCirurgia);

		// Verificar feriados
		while (DateUtil.validaDataMaior(dataUtil, dataValida)) {

			AghFeriados feriado = getAghuFacade().obterFeriado(dataValida);

			if (feriado != null) {
				dataValida = DateUtil.adicionaDias(dataValida, 1);
			} else {
				break;
			}
		}

		// Verificar dias de final de semana
		while (DateUtil.validaDataMaior(dataUtil, dataValida)) {
			
			if (DateUtil.isFinalSemana(dataValida)) {
				dataValida = DateUtil.adicionaDias(dataValida, 1);
			} else {
				dataUtil = DateUtil.adicionaDias(dataUtil, -1);
			}
		}

		if (DateUtil.validaDataMaior(dataCorrente, dataValida)) {
			throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_01317, qtdMaxDiasUteisAposCirurgia.toString());
		}
	}
	
	/**
	 * Validacoes pra os casos de Origem Atendimento no Ambulatorio
	 * E ( Centro Obstetrico ou  Pronto Atendimento ) 
	 */
	protected void validacaoAmbulatorioCentroObstetricoProntoAtd(AghAtendimentos atendimento, AghuParametrosEnum paramSysHora) throws BaseException {
		Date dtConsulta = atendimento.getDthrInicio();
		//dtConsulta = atendimento.getConsulta().getDtConsulta();

		if (this.temTargetExecutor()) {
			this.validacaoAmbulatorioParaExecutorInterno(dtConsulta, AghuParametrosEnum.P_DIAS_SOL_EX_POSTERIOR);

		} else {
			// Se a consulta foi feita hoje OU 
			// até XX horas (paramSysHora) apartir do horario de inicio da consulta. 
			AghParametros paramSys = this.getParametroFacade().buscarAghParametro(paramSysHora);
			int qtHoras = paramSys.getVlrNumerico().intValue();
			Date hoje = new Date();
			Date consultaMaisXXHoras = DateUtil.adicionaHoras(dtConsulta, qtHoras);

			if (!DateValidator.validarMesmoDia(dtConsulta, hoje) && !DateValidator.validaDataMenorIgual(hoje, consultaMaisXXHoras) ) {
				// AEL_APENAS_DIA_CONS_OU_XX_HRS_DA_CONS: Só é permitido solicitar exames para esta consulta no dia da consulta OU em até P_HORAS_SOL_EX_POSTERIOR_AMB_OBSTETRICA horas após o horário de início da consulta.
				throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_APENAS_DIA_CONS_OU_XX_HRS_DA_CONS, qtHoras);						
			}
		}
	}

	/**
	 * Validacoes pra os casos de Origem Atendimento no Ambulatorio
	 * E NAO for um dos ( Centro Obstetrico, Pronto Atendimento ) 
	 */
	protected void validacaoAmbulatorioEOutros(AghAtendimentos atendimento, AghuParametrosEnum paramSysRangePosterior, boolean origemSumarioAlta) throws BaseException {
		Date dtConsulta = atendimento.getDthrInicio();
		//dtConsulta = atendimento.getConsulta().getDtConsulta();

		if (this.temTargetExecutor()) {
			this.validacaoAmbulatorioParaExecutorInterno(dtConsulta, paramSysRangePosterior);

		} else if (!temTargetSolicitarExamesViaSumarioAlta() || !origemSumarioAlta) {
			// Se consulta foi feita no dia  OU no dia anterior 
			// E se a unidade tem controle de atendimento ambulatorial durante o inicio e o fim do atendimento.
			Date diaAnterior = DateUtil.adicionaDias(new Date(), -1);

			if ( !(DateValidator.validarMesmoDia(dtConsulta, new Date()) || DateValidator.validarMesmoDia(dtConsulta, diaAnterior))
					|| !this.temControleAtendimentoAmbulatorial(atendimento)) {
				// AEL_APENAS_DIA_CONS_OU_DIA_ANTERIOR: Só é permitido solicitar exames para esta consulta no dia da consulta OU no dia anterior da consulta.
				throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_APENAS_DIA_CONS_OU_DIA_ANTERIOR);						
			}
		}
	}

	protected void validacaoAmbulatorioParaExecutorInterno(Date dtConsulta, AghuParametrosEnum paramSysRangePosterior)  throws BaseException {
		AghParametros paramSysAnt = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_SOL_EX_ANTERIOR);
		int qtDiasAnterior = paramSysAnt.getVlrNumerico().intValue();
		AghParametros paramSysPos = this.getParametroFacade().buscarAghParametro(paramSysRangePosterior);
		int qtDiasPosterior = paramSysPos.getVlrNumerico().intValue();

		Date hjMenosXXDias = DateUtil.adicionaDias(new Date(), (qtDiasAnterior * -1));
		Date hjMenosXXDiasInicial = DateUtil.obterDataComHoraInical(hjMenosXXDias);

		Date hjMaisXXDias = DateUtil.adicionaDias(new Date(), qtDiasPosterior);
		Date hjMaisXXDiasFinal = DateUtil.obterDataComHoraFinal(hjMaisXXDias);

		if (!DateUtil.validaDataMaiorIgual(dtConsulta, hjMenosXXDiasInicial) || !DateValidator.validaDataMenorIgual(dtConsulta, hjMaisXXDiasFinal)) {
			// AEL_DIA_CONSULTA_FORA_RANGE: Só é permitido solicitar exames para consulta ambulatorial que ocorreu até P_DIAS_SOL_EX_ANTERIOR dias atrás E P_DIAS_SOL_EX_POSTERIOR dias após.
			throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_DIA_CONSULTA_FORA_RANGE, qtDiasAnterior, qtDiasPosterior);						
		}
	}

	protected VAelSolicAtendsDAO getVAelSolicAtendsDAO() {
		return vAelSolicAtendsDAO;
	}

	protected boolean temControleAtendimentoAmbulatorial(AghAtendimentos atendimento) {
		// TODO corvalao: esperando definicao 
		return true;
	}

	protected boolean ehDiaDaCirurgiaOuDiaSubsequente(AghAtendimentos atendimento) {
		Date dtConsulta = atendimento.getDthrInicio();
		Date diaSub = DateUtil.adicionaDias(dtConsulta, 1);
		Date hoje = new Date();

		return ( (DateValidator.validarMesmoDia(dtConsulta, hoje) || DateValidator.validarMesmoDia(diaSub, hoje)) );
	}

	protected boolean temTargetPacientesExternos() throws ApplicationBusinessException {
		return getICascaFacade().temPermissao(obterLoginUsuarioLogado(), ELABORAR_SOLICITACAO_EXAME, "pacientesExternos");
	}

	protected boolean temTargetPacientesInternos() throws ApplicationBusinessException {
		return getICascaFacade().temPermissao(obterLoginUsuarioLogado(), ELABORAR_SOLICITACAO_EXAME, "pacientesInternos");
	}

	protected boolean temTargetExecutor() throws ApplicationBusinessException {
		return getICascaFacade().temPermissao(obterLoginUsuarioLogado(), ELABORAR_SOLICITACAO_EXAME_CONSULTA_ANTIGA, "executor");
	}
	
	protected boolean temTargetEstudanteMedicina() throws ApplicationBusinessException {
		return  cascaFacade.temPermissao(obterLoginUsuarioLogado(), SOLICITAR_EXAME_ESTUDANTE_MEDICINA, "executar"); 
	}

	protected boolean temTargetPacientesCirurgicos() throws ApplicationBusinessException {
		return getICascaFacade().temPermissao(obterLoginUsuarioLogado(), ELABORAR_SOLICITACAO_EXAME, "pacientesCirurgicos");
	}

	protected boolean temTargetSolicitarExamesViaSumarioAlta() throws ApplicationBusinessException {
		return getICascaFacade().temPermissao(obterLoginUsuarioLogado(), SOLICITAR_EXAME_VIA_SUMARIO_ALTA, "solicitar");
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<SolicitacaoColetarVO> imprimirSolicitacoesColetar(List<Integer> solicitacoes, AghUnidadesFuncionais unidadeExecutora, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		if (solicitacoes == null || solicitacoes.isEmpty()) {

			throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_IMPRIME_SOLICITACOES_COLETAR);

		}
		List<AelAmostraItemExames> listaItens = getAelItemSolicitacaoExameDAO().imprimirSolicitacoesColetar(solicitacoes,unidadeExecutora);
		List<SolicitacaoColetarVO> listVo = new ArrayList<SolicitacaoColetarVO>();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AghParametros paramSituacaoColetar = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		AelSitItemSolicitacoes vIseSituacao = getAelSitItemSolicitacoesDAO().obterPeloId(paramSituacaoColetar.getVlrTexto());

		AghParametros paramAvisoColetaProgramada = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AVISO_COLETA_PROGRAMADA);

		for (AelAmostraItemExames item : listaItens) {

			if (!validaPorParametros(item.getAelItemSolicitacaoExames(),vIseSituacao,paramAvisoColetaProgramada)) {

				continue;

			}

			if (!validaPorTipoAmostraExames(item)) {

				continue;

			}

			SolicitacaoColetarVO vo = new SolicitacaoColetarVO();
			vo.setDescricao(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getUnidadeFuncional().getSeq()+" - "+item.getAelItemSolicitacaoExames().getSolicitacaoExame().getUnidadeFuncional().getDescricao());
			vo.setAndar(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getUnidadeFuncional().getAndar().toString());
			vo.setIndAla2(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getUnidadeFuncional().getIndAla().getDescricao());
			vo.setRecemNascido(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getRecemNascido() ? "Recem Nascido" : null);
			String informacoesClinicas = "";

			if (item.getAelItemSolicitacaoExames().getSolicitacaoExame().getUsaAntimicrobianos() != null && item.getAelItemSolicitacaoExames().getSolicitacaoExame().getUsaAntimicrobianos()) {

				informacoesClinicas = "** Paciente em uso de antimicrobianos ** \n"+item.getAelItemSolicitacaoExames().getSolicitacaoExame().getInformacoesClinicas();

			} else {

				informacoesClinicas = item.getAelItemSolicitacaoExames().getSolicitacaoExame().getInformacoesClinicas();

			}

			vo.setInformacoesClinicas(informacoesClinicas);
			String localPaciente = null;

			if (item.getAelItemSolicitacaoExames().getSolicitacaoExame().getAtendimento() != null) {

				localPaciente = getSolicitacaoExameRN().recuperarLocalPaciente(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getAtendimento());

			}
			vo.setLtoLtoId(localPaciente);

			String prontuario = getExamesFacade().buscarLaudoProntuarioPaciente(item.getAelItemSolicitacaoExames().getSolicitacaoExame());
			vo.setProntuario(prontuario);

			String nome = getExamesFacade().buscarLaudoNomePaciente(item.getAelItemSolicitacaoExames().getSolicitacaoExame());
			vo.setNome(nome);

			SimpleDateFormat dtFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			Date dtNascimento = getExamesFacade().buscarLaudoDataNascimento(item.getAelItemSolicitacaoExames().getSolicitacaoExame());
			vo.setDtNascimento(dtFormatada.format(dtNascimento).toString());
			
			String nomePessoaFisica = getExamesFacade().buscarNomeServ(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getServidor().getId().getMatricula(), 
					item.getAelItemSolicitacaoExames().getSolicitacaoExame().getServidor().getId().getVinCodigo());
			vo.setNomePessoaFisica(nomePessoaFisica);
			
			String nroRegConselho = getExamesFacade().buscarNroRegistroConselho(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getServidor().getId().getVinCodigo(), 
					item.getAelItemSolicitacaoExames().getSolicitacaoExame().getServidor().getId().getMatricula(), Boolean.FALSE);
			vo.setNroRegConselho(nroRegConselho);
			
			String convenio = getExamesFacade().buscarConvenioPlano(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getConvenioSaudePlano());
			vo.setConvenio(convenio);

			vo.setSoeAtdSeq(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getAtendimento().getSeq().toString());

			SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			vo.setCriadoEm(sdf1.format(item.getAelItemSolicitacaoExames().getSolicitacaoExame().getCriadoEm()));

			vo.setIseSeqp(item.getAelItemSolicitacaoExames().getId().getSeqp().toString());

			vo.setDthrProgramada(item.getAelItemSolicitacaoExames().getDthrProgramada().toString());

			sdf1 = new SimpleDateFormat("dd/MM HH:mm");
			vo.setDthrProgramadaOrd(sdf1.format(item.getAelItemSolicitacaoExames().getDthrProgramada()));

			vo.setSoeSeq(item.getAelAmostras().getId().getSoeSeq().toString());

			vo.setSeqp(item.getAelAmostras().getId().getSeqp().toString());

			if (item.getAelAmostras().getUnidTempoIntervaloColeta() != null) {

				vo.setTempo(item.getAelAmostras().getTempoIntervaloColeta().toString()+" "+item.getAelAmostras().getUnidTempoIntervaloColeta());

			}

			if (item.getAelItemSolicitacaoExames().getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndJejum()) {

				vo.setInd("J");
			
			} else {

				if (item.getAelItemSolicitacaoExames().getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndNpo()) {
					
					vo.setInd("N");
			
				} else {
					
					if (item.getAelItemSolicitacaoExames().getAelUnfExecutaExames().getAelExamesMaterialAnalise().getIndDietaDiferenciada()) {
						
						vo.setInd("D");
					
					}
				
				}
			
			}

			vo.setDescricao1(item.getAelAmostras().getRecipienteColeta().getDescricao());

			if (item.getAelAmostras().getAnticoagulante() != null) {
			
				vo.setDescricao2(item.getAelAmostras().getAnticoagulante().getDescricao());
			
			}

			vo.setSigla(item.getAelItemSolicitacaoExames().getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelExames().getSigla());

			AelExames exames = item.getAelItemSolicitacaoExames().getAelUnfExecutaExames().getAelExamesMaterialAnalise().getAelExames();
			StringBuffer descricaoUsual = new StringBuffer(exames.getDescricaoUsual());

			try {
			
				if (item.getAelAmostras() != null && item.getAelAmostras().getUnidTempoIntervaloColeta() != null && item.getAelItemSolicitacaoExames() != null && item.getAelItemSolicitacaoExames().getIntervaloColeta() != null && item.getAelItemSolicitacaoExames().getIntervaloColeta().getTipoSubstancia() != null && item.getAelItemSolicitacaoExames().getIntervaloColeta().getVolumeIngerido() != null && item.getAelItemSolicitacaoExames().getIntervaloColeta().getUnidMedidaVolume() != null) {
				
					if (item.getAelAmostras().getTempoIntervaloColeta().equals(BigDecimal.ZERO)) {
					
						descricaoUsual.append(' ')
							.append(item.getAelItemSolicitacaoExames().getIntervaloColeta().getTipoSubstancia()).append(' ')
							.append(item.getAelItemSolicitacaoExames().getIntervaloColeta().getVolumeIngerido()).append(' ')
							.append(item.getAelItemSolicitacaoExames().getIntervaloColeta().getUnidMedidaVolume());
				
					}
				
				}
			
			} catch (Exception e) {
			
				super.logError(e.getMessage(), e);
			
			}
		
			vo.setDescricaoUsual(descricaoUsual.toString());
			vo.setUfeUnfSeq(item.getAelItemSolicitacaoExames().getUnidadeFuncional().getSeq().toString());
			vo.setTipoColeta(item.getAelItemSolicitacaoExames().getTipoColeta().toString());

			if (item.getAelEquipamentos() != null) {
				
				vo.setDriverId(item.getAelEquipamentos().getDriverId());
				
			}

			if (item.getAelAmostras().getNroUnico() != null){
		
				vo.setNroUnico(item.getAelAmostras().getNroUnico().toString());
			
			}

			AelItemSolicitacaoExames itemSol = item.getAelItemSolicitacaoExames();

			AghParametros paramSituacaoEmColeta = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EM_COLETA);
			AelSitItemSolicitacoes situacao = getAelSitItemSolicitacoesDAO().obterPeloId(paramSituacaoEmColeta.getVlrTexto());

			itemSol.setSituacaoItemSolicitacao(situacao);

			getItemSolicitacaoExameRN().atualizar(itemSol, null, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
			getAelItemSolicitacaoExameDAO().flush();

			listVo.add(vo);
		}
		
		Collections.sort(listVo);
		listVo = agruparMontarLista(listVo);
		converteParaXml(listVo);
		return listVo;
		
	}

	private List<SolicitacaoColetarVO> agruparMontarLista(List<SolicitacaoColetarVO> listVo) {
		SolicitacaoColetarVO voPai = null;
		List<SolicitacaoColetarVO> listVoAux = new ArrayList<SolicitacaoColetarVO>();
		for(SolicitacaoColetarVO vo : listVo){
			//			if(vo.getSoeSeq().equals("5686074")){
			//			System.out.println();
			//			}
			if(voPai == null || (!vo.getSoeSeq().equals(voPai.getSoeSeq()) || !vo.getDescricao1().equals(voPai.getDescricao1()) || !vo.getSeqp().equals(voPai.getSeqp()))){
				vo.setList(new ArrayList<SolicitacaoColetarVO>());
				voPai = vo;
				listVoAux.add(voPai);
			}else{
				//listVo.remove(vo);
				voPai.getList().add(vo);
			}

		}
		return listVoAux;
	}



	public void converteParaXml(List<SolicitacaoColetarVO> source) {

		try {				
			StringBuffer xml = new StringBuffer("\n <?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <aghu> \n");
			xml.append(montarXml(source))
			.append("</aghu>");
			logInfo(xml);
		} catch (IllegalArgumentException e) {
			logError(e);
		} catch (IllegalAccessException e) {
			logError(e);
		}

	}


	@SuppressWarnings("unchecked")
	private String montarXml(List<SolicitacaoColetarVO> source)
	throws IllegalAccessException {
		StringBuffer xml = new StringBuffer();
		if(source != null && !source.isEmpty()){	
			Field[] fields = source.get(0).getClass().getDeclaredFields();
			for(SolicitacaoColetarVO vo : source){
				xml.append("    <").append(vo.getClass().getSimpleName()).append(CLOSE_TAG_NL);
				for(Field field : fields){
					field.setAccessible(true);
					String nomeField = field.getName();


					Class<?> typeField = field.getType();
					if(typeField.getName().contains("List")){
						List<SolicitacaoColetarVO> list = (List<SolicitacaoColetarVO>)field.get(vo);

						xml.append("        <").append(nomeField).append(CLOSE_TAG_NL);
						xml.append(montarXml(list));
						xml.append("\n        </").append(nomeField).append(CLOSE_TAG_NL);

					}else{
						String valor = (String)field.get(vo);
						xml.append("        <").append(nomeField).append('>');
						xml.append(valor);
						xml.append("</").append(nomeField).append(CLOSE_TAG_NL);
					}


				}
				xml.append("    </").append(vo.getClass().getSimpleName()).append(CLOSE_TAG_NL);
			}
		}
		return xml.toString();
	}


	private boolean validaPorTipoAmostraExames(AelAmostraItemExames item) {
		List<AelTipoAmostraExame> lista = getAelTipoAmostraExameDAO().buscarAelTipoAmostraExamePorAelExamesMaterialAnaliseAelAmostrasRecipienteColetaResponsavelCS(item.getAelItemSolicitacaoExames().getAelUnfExecutaExames().getAelExamesMaterialAnalise(), item.getAelAmostras(), item.getAelAmostras().getRecipienteColeta());
		if(lista != null){
			for(AelTipoAmostraExame tipo : lista){
				if(tipo.getOrigemAtendimento().equals(DominioOrigemAtendimento.T) 
						|| 
						tipo.getOrigemAtendimento().equals(getPesquisaExamesFacade().validaLaudoOrigemPaciente(
								item.getAelItemSolicitacaoExames().getSolicitacaoExame()
						))
				){
					return true;
				}
			}
		}
		return false;
	}



	private boolean validaPorParametros(
			AelItemSolicitacaoExames aelItemSolicitacaoExames,
			AelSitItemSolicitacoes vIseSituacao,
			AghParametros paramAvisoColetaProgramada) {
		if(vIseSituacao.getCodigo() != null && vIseSituacao.getCodigo().trim().equalsIgnoreCase(aelItemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo())){
			Date dtHoje = new Date();
			if(aelItemSolicitacaoExames.getDthrProgramada().before(DateUtil.adicionaDias(dtHoje, 1440 / paramAvisoColetaProgramada.getVlrNumerico().intValue()))) {
				return true;
			}
		}
		return false;
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return aelTipoAmostraExameDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected ITransferirPacienteFacade getTransferirPacienteFacade() {
		return this.transferirPacienteFacade;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO; 
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO; 
	}

	protected ItemSolicitacaoExameRN getItemSolicitacaoExameRN() {
		return itemSolicitacaoExameRN; 
	}

	

	private SolicitacaoExameRN getSolicitacaoExameRN() {
		return solicitacaoExameRN;
	}

	protected IPesquisaExamesFacade getPesquisaExamesFacade() {
		return this.pesquisaExamesFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	//------------------------------
	//Regras do forms - Estória #5087

	/**
	 * RN21
	 * 
	 * ORADB AELP_GERA_ITENS_AMOSTRA
	 * 
	 * @throws BaseException 
	 */
	@SuppressWarnings({"deprecation","PMD.NPathComplexity"})
	public void gerarItensAmostra(SolicitacaoExameVO solicitacaoExameVO, AelSolicitacaoExames solicEx) throws BaseException {
		List<ItemSolicitacaoExameVO> itensNovosPorAmostra = new ArrayList<ItemSolicitacaoExameVO>();

		//RN21.1
		int indiceItemSolExame = 0;
		for (ItemSolicitacaoExameVO itemSolicitacaoExameVO : solicitacaoExameVO.getItemSolicitacaoExameVos()) {
			//RN21.2
			ItemSolicitacaoExameVariaveisVO variaveis = new ItemSolicitacaoExameVariaveisVO();
			variaveis.setItemSolicitacaoExame(itemSolicitacaoExameVO.getModel());
			variaveis.setDthrProgramada(itemSolicitacaoExameVO.getDataProgramada());

			if (variaveis.getIntervaloDias() != null) {
				variaveis.setTempoColetas(variaveis.getIntervaloDias().floatValue());
			} else if (variaveis.getIntervaloHoras() != null){
				//(to_number(to_char(intervalo_horas,'hh24')) / 24) +  (to_number(to_char(intervalo_horas,'mi')) / 60 / 24);
				Float tempoColetas = new Float((variaveis.getIntervaloHoras().getHours() / 24.0) + (variaveis.getIntervaloHoras().getMinutes() / 60.0 / 24.0));
				variaveis.setTempoColetas(tempoColetas);
			}

			//RN21.3
			if (itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise() != null) {
				variaveis.setIndSolicInformaColetas(itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getIndSolicInformaColetas());
				variaveis.setIndGeraItemPorColetas(itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getIndGeraItemPorColetas());
				variaveis.setIndExigeDescMatAnls(itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getAelMateriaisAnalises().getIndExigeDescMatAnls());

				//RN21.4
				Integer numeroTotalAmostras = itemSolicitacaoExameVO.getNumeroAmostra();
				if (variaveis.getIndSolicInformaColetas() && variaveis.getIndGeraItemPorColetas()) {
					//RN21.5
					if (itemSolicitacaoExameVO.getNumeroAmostra() != null && itemSolicitacaoExameVO.getNumeroAmostra() > 1 && !itemSolicitacaoExameVO.getIndGeradoAutomatico()) {
						
						// Seta numero de amostras anterior para, em caso de erro de gravacao, retornar ao estado anterior
						// Este mudança representa a ação que é feita quando o usuário edita o item de amostra trocando 
						// apenas o valor de amostra
						itemSolicitacaoExameVO.setNumeroAmostraAnterior(itemSolicitacaoExameVO.getNumeroAmostra());
						
						//RN21.6
						itemSolicitacaoExameVO.setNumeroAmostra(1);

						if (itemSolicitacaoExameVO.getIntervaloDias() != null) {
							//itemSolicitacaoExameVO.setIntervaloDias(0); 
							solicEx.getItensSolicitacaoExame().get(indiceItemSolExame).setIntervaloDias(Byte.valueOf("0"));
						} else {
							//RN21.7
							if (itemSolicitacaoExameVO.getIntervaloHoras() != null) {
								variaveis.setIntervaloHorasAux(itemSolicitacaoExameVO.getIntervaloHoras());
								Calendar cal = Calendar.getInstance();
								cal.setTime(itemSolicitacaoExameVO.getIntervaloHoras());
								cal.set(Calendar.MINUTE, 0);
								cal.set(Calendar.HOUR, 0);
								//itemSolicitacaoExameVO.setIntervaloHoras(cal.getTime());
								solicEx.getItensSolicitacaoExame().get(indiceItemSolExame).setIntervaloHoras(cal.getTime());
							}
						}

						//RN21.8
						List<ItemSolicitacaoExameVO> itensNovos = new ArrayList<ItemSolicitacaoExameVO>();
						for (Byte numAmostra = 2; numAmostra < (numeroTotalAmostras+1); numAmostra++) {
							//RN21.9 - RN21.16
							ItemSolicitacaoExameVO itemSolicitacaoExameVONovo = gerarNovoItemSolicitacaoExame(solicitacaoExameVO, itemSolicitacaoExameVO, variaveis, numAmostra);
							
							/**
							 * Inicio correção issue #12072
							 */
							List<ItemSolicitacaoExameVO> itensDependentes = new ArrayList<ItemSolicitacaoExameVO>();							
							for(ItemSolicitacaoExameVO itemDep : itemSolicitacaoExameVONovo.getDependentesObrigratorios()){
								itemDep.setDataProgramada(itemSolicitacaoExameVONovo.getDataProgramada());
							}
							List<ItemSolicitacaoExameVO> itensDependentesOpcionais = getItemSolicitacaoExameON().obterDependentesOpcionaisSelecionados(itemSolicitacaoExameVONovo);
							for(ItemSolicitacaoExameVO itemOpcional : itensDependentesOpcionais){
								itemOpcional.setDataProgramada(itemSolicitacaoExameVONovo.getDataProgramada());
							}
							itensDependentes.addAll(itemSolicitacaoExameVONovo.getDependentesObrigratorios());
							itensDependentes.addAll(itensDependentesOpcionais);
							
							/**
							 * Fim correção issue #12072
							 */
							
							//Insert
							itemSolicitacaoExameVONovo.setDependentesObrigratorios(itemSolicitacaoExameVONovo.getDependentesObrigratorios());
							itemSolicitacaoExameVONovo.setDependentesOpcionais(itensDependentesOpcionais);
							itensNovos.add(itemSolicitacaoExameVONovo);
							
						}
						itensNovosPorAmostra.addAll(itensNovos);
					}
					if (numeroTotalAmostras != null && numeroTotalAmostras > 1) {
						solicEx.getItensSolicitacaoExame().get(indiceItemSolExame).setNroAmostras(new Byte("1"));
					}
				}
			}
			
			if (!itemSolicitacaoExameVO.getIndGeradoAutomatico()){
				indiceItemSolExame++;
			}
		}//FOR

		for (ItemSolicitacaoExameVO itemNovo : itensNovosPorAmostra) {
			List<ItemSolicitacaoExameVO> examesDependentesObrig = itemNovo.getDependentesObrigratorios();
			List<ItemSolicitacaoExameVO> examesDependentesOps = itemNovo.getDependentesOpcionais();
			AelItemSolicitacaoExames item = itemNovo.getModel();
			List<AelItemSolicitacaoExames> dependentes = new ArrayList<AelItemSolicitacaoExames>();
			
			for (ItemSolicitacaoExameVO subItemDep : examesDependentesObrig) {
				subItemDep.getItemSolicitacaoExame().setDthrProgramada(subItemDep.getDataProgramada());
				dependentes.add(subItemDep.getItemSolicitacaoExame());
			}
			for (ItemSolicitacaoExameVO subItemDep : examesDependentesOps) {
				subItemDep.getItemSolicitacaoExame().setDthrProgramada(subItemDep.getDataProgramada());
				dependentes.add(subItemDep.getItemSolicitacaoExame());
			}
			
			item.setItemSolicitacaoExames(dependentes);
			solicEx.addItemSolicitacaoExame(item);
		}
	}

	/**
	 * Gera um novo AelItemSolicitacaoExames para ser inserido, de acordo com os dados pesquisados.
	 * RN21.9 a 21.16
	 * 
	 */
	protected ItemSolicitacaoExameVO gerarNovoItemSolicitacaoExame(SolicitacaoExameVO solicitacaoExameVO, ItemSolicitacaoExameVO itemSolicitacaoExameVO, ItemSolicitacaoExameVariaveisVO variaveis, Byte numAmostra) {
		//RN21.9
		ItemSolicitacaoExameVO itemSolicitacaoExameVONovo = new ItemSolicitacaoExameVO(itemSolicitacaoExameVO); //Copia atributos do original
		itemSolicitacaoExameVONovo.setSolicitacaoExameVO(solicitacaoExameVO);
		//Atributos copiados na construção do atributo
		//		itemSolicitacaoExameVONovo.setExame(variaveis.getExame());
		//		itemSolicitacaoExameVONovo.setMaterialAnalise(variaveis.getMaterialAnalise());
		//		itemSolicitacaoExameVONovo.setUnidadeFuncional(variaveis.getUnidadeFuncional());
		itemSolicitacaoExameVONovo.setTipoColeta(DominioTipoColeta.N);
		variaveis.setDthrProgramada(DateUtil.adicionaDiasFracao(variaveis.getDthrProgramada(), variaveis.getTempoColetas()));
		itemSolicitacaoExameVONovo.setDataProgramada(variaveis.getDthrProgramada());
		itemSolicitacaoExameVONovo.setSituacaoCodigo(variaveis.getSituacaoItemSolicitacao());
		itemSolicitacaoExameVONovo.setNumeroAmostra(numAmostra.intValue());
		
		try {
			itemSolicitacaoExameVONovo.setDependentesObrigratorios(ItemSolicitacaoExameVO.clonarLista(itemSolicitacaoExameVONovo.getDependentesObrigratorios()));
		} catch (CloneNotSupportedException e) {
			LOG.error(e.getMessage());
		}
		//RN21.10
		if(variaveis.getIntervaloDias() != null) {
			Float soma = variaveis.getTempoAmostraDias() + variaveis.getTempoColetas();
			variaveis.setTempoAmostraDias(soma.byteValue());
			itemSolicitacaoExameVONovo.setIntervaloDias(variaveis.getTempoAmostraDias().intValue());
		} else if(variaveis.getIntervaloHoras() != null) {
			variaveis.setTempoAmostraHoras(variaveis.getTempoColetas().byteValue());
			Date soma = DateUtil.adicionaHoras(variaveis.getIntervaloHorasAux(), variaveis.getTempoAmostraHoras());
			variaveis.setIntervaloHorasAux(soma);
			itemSolicitacaoExameVONovo.setIntervaloHoras(variaveis.getIntervaloHorasAux());
		}

		//RN21.11
		if(variaveis.getTipoTransporte() != null) {
			itemSolicitacaoExameVONovo.setTipoTransporte(variaveis.getTipoTransporte());
		}

		//RN21.12
		if(variaveis.getIndUsoO2() != null) {
			//No VO o indUsoO2 é setado em função dessa propriedade
			itemSolicitacaoExameVONovo.setOxigenioTransporte(DominioSimNao.getInstance(variaveis.getIndUsoO2()));
		}

		//RN21.13
		if(variaveis.getRanSeq() != null) {
			itemSolicitacaoExameVONovo.setRegiaoAnatomica(variaveis.getRegiaoAnatomica());
		}

		//RN21.14
		if(variaveis.getDescRegiaoAnatomica() != null) {
			itemSolicitacaoExameVONovo.setDescRegiaoAnatomica(variaveis.getDescRegiaoAnatomica());
		}

		//RN21.15
		if(variaveis.getDescMaterialAnalise() != null) {
			itemSolicitacaoExameVONovo.setDescMaterialAnalise(variaveis.getDescMaterialAnalise());
		}

		//RN21.16
		itemSolicitacaoExameVONovo.setIndGeradoAutomatico(true);

		return itemSolicitacaoExameVONovo;
	}

	/**
	 * RN23
	 * 
	 * ORADB AELP_VER_COLETA_RESTRITA
	 * 
	 */
	protected void verificarColetaRestrita(SolicitacaoExameVO solicitacaoExameVO, AelSolicitacaoExames solicEx) throws BaseException {
		//FatConvenioSaude convenioSaude = getItemSolicitacaoExameRN().obterFatConvenioSaude(solicEx);
		
		//RN23.1
		//Se convênio for SUS
		if(solicitacaoExameVO.getIsSus()) {
			//RN23.2
			if(solicitacaoExameVO.getUnidadeTrabalho() == null) {
				if(getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(solicitacaoExameVO.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)
						|| getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(solicitacaoExameVO.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.SMO)) {
					return;
				}
			}

			//RN23.3
			for(ItemSolicitacaoExameVO itemSolicitacaoExameVO : solicitacaoExameVO.getItemSolicitacaoExameVos()) {
				//RN23.4
				if(itemSolicitacaoExameVO.getSituacaoCodigo().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AC.toString())) {
					//RN23.5
					AelUnfExecutaExames unfExecutaExames = itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame();

					//Verificação somente para os médicos solicitantes
					if(solicitacaoExameVO.getUnidadeTrabalho() == null) {
						//RN23.6
						verificarAgendamentoRestrito(solicitacaoExameVO, unfExecutaExames);
					}
				}
			}
		}
	}

	/**
	 * RN23.6
	 * 
	 */
	protected void verificarAgendamentoRestrito(SolicitacaoExameVO solicitacaoExameVO, AelUnfExecutaExames unfExecutaExames) throws BaseException {
		//Verifica se o agendamento é restrito a coleta
		if( (unfExecutaExames.getIndAgendamPrevioInt() == DominioSimNaoRestritoAreaExecutora.R
				&& (solicitacaoExameVO.getAtendimento().getOrigem() == DominioOrigemAtendimento.I
						|| solicitacaoExameVO.getAtendimento().getOrigem() == DominioOrigemAtendimento.N
						|| solicitacaoExameVO.getAtendimento().getOrigem() == DominioOrigemAtendimento.H
						|| solicitacaoExameVO.getAtendimento().getOrigem() == DominioOrigemAtendimento.C))
						||
						(unfExecutaExames.getIndAgendamPrevioNaoInt() == DominioSimNaoRestritoAreaExecutora.R
								&& (solicitacaoExameVO.getAtendimento().getOrigem() == DominioOrigemAtendimento.A
										|| solicitacaoExameVO.getAtendimento().getOrigem() == DominioOrigemAtendimento.X
										|| solicitacaoExameVO.getAtendimento().getOrigem() == DominioOrigemAtendimento.D
										|| solicitacaoExameVO.getAtendimento().getOrigem() == DominioOrigemAtendimento.U)) ) {
			//alterado conforme issue #11820
			solicitacaoExameVO.setAlertaExamesColetaEspecial(true);
			//throw new ApplicationBusinessException(SolicitacaoExameONExceptionCode.AEL_01509);
		}
	}

	/**
	 * RN27
	 * 
	 * ORADB AELP_CHAMA_REPORT_TICKET_PAC
	 * 
	 * Verifica se deve imprimir o relatório de ticket de exames do paciente.
	 * 
	 */
	protected void chamarRelatorioTicketPaciente(SolicitacaoExameVO solicitacaoExameVO) {
		
		//Solicitação de Exame gerada com situação = Pendente não deve gerar Ticket
		if (solicitacaoExameVO.isGeraSolicExameSitPendente()){
			return;
		}
		
		//RN27.1
		if(solicitacaoExameVO.getAtendimento() == null && solicitacaoExameVO.getAtendimentoDiverso() != null) {
			//Busca projeto de pesquisa
			AelProjetoPesquisas pjqSeq = solicitacaoExameVO.getAtendimentoDiverso().getAelProjetoPesquisas();

			if(pjqSeq == null || solicitacaoExameVO.getUnidadeTrabalho() != null) {
				return;
			}
		}

		//RN27.2
		//Para situação PENDENTE orinda do sistema ambulatório não imprime ticket do paciente
		// Implementado incorretamente. Este teste não verifica a situação da Solicição, não geraria Ticket apenas por ter sido gerado na TELA_AMBULATORIO
//		if(solicitacaoExameVO.getTelaOriginouSolicitacao() == DominioTelaOriginouSolicitacaoExame.TELA_AMBULATORIO || ) {
//			return;
//		}
			
		//RN27.3
		if(solicitacaoExameVO.getUnidadeTrabalho() == null || solicitacaoExameVO.getUnidadeTrabalho().getUnfSeq() == null
				|| getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(solicitacaoExameVO.getUnidadeTrabalho().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_PNEUMOLOGIA)
				|| getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(solicitacaoExameVO.getUnidadeTrabalho().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_RADIOLOGIA)
				|| getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(solicitacaoExameVO.getUnidadeTrabalho().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_COLETA)) {
		
			//RN27.4
			//Se a origem do atendimento for diferente de Ambulatório e Paciente Externo
			if(solicitacaoExameVO.getAtendimento().getOrigem() != DominioOrigemAtendimento.A
					&& solicitacaoExameVO.getAtendimento().getOrigem() != DominioOrigemAtendimento.X) {
				return;
			}
		} else {
			//RN27.5
			if(solicitacaoExameVO.getAtendimento().getOrigem() != DominioOrigemAtendimento.X) {
				return;
			}
		}

		//RN27.6
		//Quando solicitado pelo solicitante e de unidade da emergência não imprime ticket para o paciente
		if(solicitacaoExameVO.getUnidadeTrabalho() == null && getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(solicitacaoExameVO.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)) {
			return;	
		}

		//RN27.7
		//Verifica se o há exames indicados no cadastro que devem ser impressos
		boolean imprimir = false;
		for(ItemSolicitacaoExameVO itemSolicitacaoExameVO : solicitacaoExameVO.getItemSolicitacaoExameVos()) {
			//RN27.8
			if(itemSolicitacaoExameVO.getUnfExecutaExame().getUnfExecutaExame().getAelExamesMaterialAnalise().getIndImpTicketPaciente()) {
				imprimir = true;
				break;
			}
		}

		//Determina se vai ser exibido o ticekt
		solicitacaoExameVO.setImprimiuTicketPaciente(imprimir);
	}

	/**
	 * RN29
	 * 
	 * ORADB AELP_CHAMA_ETIQ_BARRAS
	 * 
	 * @throws BaseException 
	 * 
	 */
	protected void chamarEtiquetaBarras(AelSolicitacaoExames solicitacaoExame, AghUnidadesFuncionais unidadeTrabalho, 
			String nomeImpressora, String situacaoItemExame) throws BaseException {

		getEtiquetasON().gerarEtiquetas(solicitacaoExame, unidadeTrabalho, nomeImpressora, situacaoItemExame,true);
	}

	/**
		 * RN30
		 *
		 * ORADB AELP_CHAMA_ETIQ_REDOME
		 *
		 * @throws BaseException
		 *
		 */
		protected void chamarEtiquetaBarrasRedome(	AelSolicitacaoExames solicitacaoExame, AghUnidadesFuncionais unidadeExecutora, String nomeImpressora, String situacaoItemExame) throws BaseException {
			etiquetasRedomeON.gerarEtiquetasRedome(solicitacaoExame, unidadeExecutora, nomeImpressora, false, situacaoItemExame,true);
		}

	/**
	 * RN31
	 * 
	 * ORADB AELP_VER_ATU_SIT_PENDENTE
	 * 
	 * Verifica e atualiza a situação dos itens solicitados para pendente.
	 * @throws BaseException 
	 * 
	 */
	public void atualizarItensPendentesAmbulatorio(SolicitacaoExameVO solicitacaoExameVO, AelSolicitacaoExames solicEx, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		//RN31.1
		//Se a solicitação se originou no ambulatório
		if (solicitacaoExameVO.getTelaOriginouSolicitacao() == DominioTelaOriginouSolicitacaoExame.TELA_AMBULATORIO) {
			//RN31.2
			//Procura por itens da solicitação com as flags
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			boolean achouItem = false;
			for(ItemSolicitacaoExameVO itemSolicitacaoExameVO : solicitacaoExameVO.getItemSolicitacaoExameVos()) {
				if ((itemSolicitacaoExameVO.getIndTicketPacImp() != null && itemSolicitacaoExameVO.getIndTicketPacImp()) || (itemSolicitacaoExameVO.getIndInfComplImp() != null && itemSolicitacaoExameVO.getIndInfComplImp())) {
					achouItem = true;
					break;
				}
			}
			if (!achouItem) {
				if (solicEx == null){
					solicEx = getAelSolicitacaoExameDAO().obterPeloId(solicitacaoExameVO.getSeqSolicitacaoSalva());
				}
				
				//RN31.3
				AelSitItemSolicitacoes situacaoItemSolicitacao = getAelSitItemSolicitacoesDAO().obterPeloId(DominioSituacaoItemSolicitacaoExame.PE.toString()); 
				for(AelItemSolicitacaoExames itemSolicitacaoExame : solicEx.getItensSolicitacaoExame()) {
					itemSolicitacaoExame.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
					itemSolicitacaoExame.setOrigemTelaSolicitacao(true);
					getItemSolicitacaoExameRN().atualizar(itemSolicitacaoExame, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
					
					//Para cada dependente
					for (AelItemSolicitacaoExames itemDependente : itemSolicitacaoExame.getItemSolicitacaoExames()) {
						itemDependente.setSituacaoItemSolicitacao(situacaoItemSolicitacao);
						itemDependente.setOrigemTelaSolicitacao(true);
						getItemSolicitacaoExameRN().atualizar(itemDependente, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
					}
				}
			}
		}
	}

	private void validarSolicitacaoSeTransplantado(
			SolicitacaoExameVO solicitacaoExameVO)
			throws ApplicationBusinessException {

		if (solicitacaoExameVO == null) {
			return;
		}

		if (solicitacaoExameVO.getMostrarIndicadorTransplantado()) {

			if (solicitacaoExameVO.getIndTransplante() == null) {
				throw new ApplicationBusinessException(
						SolicitacaoExameONExceptionCode.AEL_01366);
			}
		}
	}
	
	protected ItemSolicitacaoExameON getItemSolicitacaoExameON() {
		return itemSolicitacaoExameON; 
	}

	protected EtiquetasON getEtiquetasON() {
		return etiquetasON;
	}

	protected AelSismamaMamoResON getAelSismamaMamoResRN() {
		return aelSismamaMamoResON;
	}

	
	
	public List<RapQualificacao> pesquisarQualificacoesSolicitacaoExameSemPermissao(
			Short vinculo, Integer matricula,
			Integer diasServidorFimVinculoPermitidoSolicitarExame) {
		return getRegistroColaboradorFacade().pesquisarQualificacoesSolicitacaoExameSemPermissao(vinculo, matricula, diasServidorFimVinculoPermitidoSolicitarExame);
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}	

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected ISismamaFacade getSismamaFacade() {
		return this.sismamaFacade;
	}	
	
	
	protected AelAtendimentoDiversosDAO getAelAtendimentoDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}
	
	/**
	 * Método que busca a unidade executora de trabalho. Lógica unida de duas
	 * controllers que estavam implementando no local incorreto.
	 * 
	 * @param solicitacaoExame
	 * @return
	 * @throws BaseException
	 */
	public AghUnidadesFuncionais obterUnidadeTrabalhoSolicitacaoExame(
			final SolicitacaoExameVO solicitacaoExame) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghUnidadesFuncionais unidadeTrabalho = null;
		if (mostrarUnidadeTrabalhoSolicitacaoExame(solicitacaoExame)) {
			AelUnidExecUsuario aelUnidExecUsuario = getExamesFacade().obterUnidExecUsuarioPeloId(getRegistroColaboradorFacade()
				.obterServidorAtivoPorUsuario(servidorLogado.getUsuario()).getId());
			if (aelUnidExecUsuario != null
					&& aelUnidExecUsuario.getUnfSeq() != null) {
				unidadeTrabalho = aelUnidExecUsuario.getUnfSeq();
			}
		}
		return unidadeTrabalho;
	}

	private IPermissionService getPermissionService() {
		return this.permissionService;
	}

	public Boolean mostrarUnidadeTrabalhoSolicitacaoExame(SolicitacaoExameVO solicitacaoExame) {
		
		Boolean mostrar = Boolean.FALSE;
		
		if (getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), ELABORAR_SOLICITACAO_EXAME_CONSULTA_ANTIGA, "executor")
			&& (solicitacaoExame.getTelaOriginouSolicitacao() == null 
			    || DominioTelaOriginouSolicitacaoExame.TELA_PESQUISA_SOLICITACAO_EXAME.equals(solicitacaoExame.getTelaOriginouSolicitacao())  
			    || DominioTelaOriginouSolicitacaoExame.TELA_PESQUISAR_EXAMES.equals(solicitacaoExame.getTelaOriginouSolicitacao())
				|| DominioTelaOriginouSolicitacaoExame.TELA_ATENDIMENTO_EXTERNO.equals(solicitacaoExame.getTelaOriginouSolicitacao())) )  {
			
			mostrar = Boolean.TRUE;
		}
		
		return mostrar;
		
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	/**
	 * Busca servidores que possam fazer Solicitacao de Exame.<br>
	 * O parametro de busca (objPesquisa) pode ser o nome da pessoa associada ao servidor<br>
	 * OU o vinculo e a matricula do servidor separados por um espaco.<br>
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<RapServidores> buscarServidoresSolicitacaoExame(String objPesquisa) {
		return this.buscarServidores(objPesquisa);
	}

	public Long buscarServidoresSolicitacaoExameCount(String objPesquisa) {
		return Long.valueOf(buscarServidores(objPesquisa).size()); 
	}
		
	private List<RapServidores> buscarServidores(String objPesquisa) {
		Integer diasPermitidos;
		try {
			AghParametros param = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_SOL_EX_FIM_VINCULO_PERMITIDO);
			diasPermitidos = param.getVlrNumerico().intValue();
		} catch (ApplicationBusinessException e) {
			// Se o parametro nao foi criado / configurado. Entao usa o valor default do metodo de pesquisa.
			diasPermitidos = null;
		}

		Short vinculo = null;
		Integer matricula = null;
		if (StringUtils.isNotBlank(objPesquisa)) {
			String[] vinculoMatricula = objPesquisa.split(" ");
			if (vinculoMatricula.length > 1 
					&& StringUtils.isNumeric(vinculoMatricula[0])
					&& StringUtils.isNumeric(vinculoMatricula[1])) {
				vinculo = Short.valueOf(vinculoMatricula[0]);
				matricula = Integer.valueOf(vinculoMatricula[1]);
			}
		}

		List<RapServidores> lista = null;
		if (vinculo != null && matricula != null) {
			lista = this.getRegistroColaboradorFacade().pesquisarServidoresSolicitacaoExame(vinculo, matricula, diasPermitidos);
		}

		if (lista == null || lista.isEmpty()) {
			lista = this.getRegistroColaboradorFacade().pesquisarServidoresSolicitacaoExame(objPesquisa, diasPermitidos);			
		}

		return lista;
	}
	
	public RapServidores buscarResponsavelConsultaOuEquipe(Integer numeroConsulta){
		
		RapServidores servidorResponsavel;
		
		AacConsultas consulta = aacConsultasDAO.obterConsulta(numeroConsulta);
		
		AacGradeAgendamenConsultas gradeAgendamenConsultas = aacGradeAgendamenConsultasDAO.obterPorChavePrimaria(consulta.getGradeAgendamenConsulta().getSeq());   //obterGradeAgendamento(consulta.getGradeAgendamenConsulta().getSeq());
		
		if (gradeAgendamenConsultas.getProfServidor() != null && gradeAgendamenConsultas.getProfServidor().getId() != null){
			servidorResponsavel = rapServidoresDAO.obter(gradeAgendamenConsultas.getProfServidor().getId());
		} else {
			AghEquipes aghEquipes =aghEquipesDAO.obterPorChavePrimaria(gradeAgendamenConsultas.getEquipe().getSeq());
			servidorResponsavel = rapServidoresDAO.obter(aghEquipes.getProfissionalResponsavel().getId());
		}
		
		return servidorResponsavel;
	}

    public void preparaDadosinsercaoExtratoDoadores(Integer soeSeq) throws BaseException {
        getSolicitacaoExameRN().preparaDadosinsercaoExtratoDoadores(soeSeq);
    }

}
