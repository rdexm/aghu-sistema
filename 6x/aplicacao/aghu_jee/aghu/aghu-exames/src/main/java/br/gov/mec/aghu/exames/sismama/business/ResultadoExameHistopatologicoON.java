package br.gov.mec.aghu.exames.sismama.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSismamaAdequabilidadeMaterial;
import br.gov.mec.aghu.dominio.DominioSismamaDimensMaxTumor;
import br.gov.mec.aghu.dominio.DominioSismamaExtensaoTumorPele;
import br.gov.mec.aghu.dominio.DominioSismamaGrau;
import br.gov.mec.aghu.dominio.DominioSismamaHistoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaLesaoCaraterNeoplasicoMaligno;
import br.gov.mec.aghu.dominio.DominioSismamaLinfonodos;
import br.gov.mec.aghu.dominio.DominioSismamaMargensCirurgicas;
import br.gov.mec.aghu.dominio.DominioSismamaNumeroLinfonodosComprometidos;
import br.gov.mec.aghu.dominio.DominioSismamaProcedimentoCirurgico;
import br.gov.mec.aghu.dominio.DominioSismamaReceptor;
import br.gov.mec.aghu.dominio.DominioSismamaSimNao;
import br.gov.mec.aghu.dominio.DominioSismamaSimNaoNaoSabe;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoCadDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaHistoResDAO;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameSismamaVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.model.AelSismamaHistoCad;
import br.gov.mec.aghu.model.AelSismamaHistoRes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responável pelas regras de negócio para 
 * Resultado de Exames Histopatológico - SISMAMA.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class ResultadoExameHistopatologicoON extends BaseBusiness {


private static final String EXCECAO_LANCADA = "Exceção lançada: ";

@EJB
private ResultadoExameHistopatologicoRN resultadoExameHistopatologicoRN;

private static final Log LOG = LogFactory.getLog(ResultadoExameHistopatologicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelSismamaHistoResDAO aelSismamaHistoResDAO;

@EJB
private IFaturamentoFacade faturamentoFacade;

@Inject
private AelSismamaHistoCadDAO aelSismamaHistoCadDAO;

@Inject
private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@EJB
private IExamesFacade examesFacade;

@EJB
private IExamesPatologiaFacade examesPatologiaFacade;
	
	private static final long serialVersionUID = 3945848025556021163L;
	private static final String DESCRICAO_SISMAMA = "SISMAMA";
	private static final String RESPOSTA_STRING_TRUE = "3";
	private static final String RESPOSTA_STRING_FALSE = "0";
	private static final Integer TAMANHO_MAXIMO_DESCRICAO_EXAME = 40;
	
	public enum ResultadoExameSismamaONCode implements BusinessExceptionCode {
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_REGIAO_ANATOMICA_NAO_ENCONTRADA, 
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA_CAMPOS_LESAO_BENIGNA,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_SELECAO_LINFONODOS_OBRIGATORIA,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_INSERIR_RESPOSTA,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_ATUALIZAR_RESPOSTA,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_ADEQ_SATIS_LESAO_NOK,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_CORE_BIOPSY_BENIGNO_OBRIGATORIO,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_CORE_BIOPSY,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_CORE_BIOPSY_MOTIVO_OBRIG,
		ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_CORE_BIOPSY_MOTIVO_NOK,
		AEL_03214, AEL_03314, AEL_03273, AEL_03315, AEL_03275, AEL_03276, 
		AEL_03316, AEL_03317, AEL_03318, AEL_03278, AEL_03277, AEL_03279;
	}
	
	/**
	 * Valida todos os itens para consistir exames cuja região anatômica é SISMAMA.
	 * 
	 * ORADB: AELC_VALIDA_EXAME_SISMAMA
	 * 
	 * @param numeroAp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarExamesSismamaPorNumeroAp(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException {	
		List<AelItemSolicitacaoExames> listaItemSolicitacaoExame = aelItemSolicitacaoExameDAO.obterListaItemSolicitacaoExamesPorNumeroAPGrupoPesquisaSISMAMA(numeroAp, lu2Seq);
		
		if (listaItemSolicitacaoExame == null || listaItemSolicitacaoExame.isEmpty()){
			return Boolean.FALSE;
		}
		
		AelItemSolicitacaoExames itemSolicitacaoExame = listaItemSolicitacaoExame.get(0);
		AelSolicitacaoExames solicitacaoExame = itemSolicitacaoExame.getSolicitacaoExame();
		AelRegiaoAnatomica regiaoAnatomica = itemSolicitacaoExame.getRegiaoAnatomica();
		AghUnidadesFuncionais unidadeFuncional = itemSolicitacaoExame.getUnidadeFuncional();
		
		if (solicitacaoExame == null) {
			return Boolean.FALSE;
		}
		
		AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
		if (atendimento != null) {
			FatConvenioSaudePlano convenioSaudePlano = atendimento
					.getConvenioSaudePlano();
			Long countConvenioSaude = 0l;
			
			if (convenioSaudePlano != null) {
				countConvenioSaude = getFaturamentoFacade()
						.obterCountConvenioSaudeAtivoPorPgdSeq(
								convenioSaudePlano.getConvenioSaude().getCodigo());
			}

			if (countConvenioSaude > 0) {
				if (!getExamesFacade().obterOrigemIg(solicitacaoExame).equals(
						"AMB")) {
					return Boolean.FALSE;
				}
			} else {
				return Boolean.FALSE;
			}
		} else {
			return Boolean.FALSE;
		}
		
		IParametroFacade parametroFacade = getParametroFacade();
		AghParametros paramRegiaoMamaDir = parametroFacade
				.obterAghParametro(AghuParametrosEnum.P_REGIAO_MAMA_DIR);
		AghParametros paramRegiaoMamaEsq = parametroFacade
				.obterAghParametro(AghuParametrosEnum.P_REGIAO_MAMA_ESQ);
		
		if (regiaoAnatomica != null
				&& (regiaoAnatomica.getSeq().equals(
						paramRegiaoMamaDir.getVlrNumerico().intValue()) || regiaoAnatomica
						.getSeq().equals(
								paramRegiaoMamaEsq.getVlrNumerico().intValue()))
				&& getAghuFacade().validarCaracteristicaDaUnidadeFuncional(
						unidadeFuncional.getSeq(),
						ConstanteAghCaractUnidFuncionais.EXAME_SISMAMA_HISTO)) {
			return Boolean.TRUE;
		}
				
		return Boolean.FALSE;		
	}
	
	
	/**
	 * Método responsável por verificar se houve preenchimento 
	 * de algum item do questionario do SISMAMA.
	 * 
	 * ORADB:	AELF_LAUDO_UNICO \ AELP_SISMAMA_RESULT_HISTO
	 * 
	 * @param numeroAp
	 * @param lu2Seq => seq da configuração do exame
	 * 			
	 */
	public void verificarPreenchimentoExamesSismama(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException {
		Long countSismamaHistoRes = getAelSismamaHistoResDAO().obterCountSismamaHistoRes(numeroAp, lu2Seq);
		
		if (countSismamaHistoRes == 0) {
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03214);
		}
	}
	
	/**
	 * Método responsável por pesquisar os exames para a seleção 
	 * do exame de SISMAMA no qual será preenchido o questionário.
	 * 
	 * ORADB:	AELF_SISMAMA_RES_HIS \ WHEN-NEW-FORM-INSTANCE
	 * 			AELF_SISMAMA_RES_HIS \ CGFK$QRY_ISE_AEL_ISE_EXA_FK1
	 * 			AELF_SISMAMA_RES_HIS \ POST-QUERY
	 * 
	 * @param numeroAp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemSolicitacaoExameSismamaVO> pesquisarExameSismama(Long numeroAp, Integer lu2Seq) throws ApplicationBusinessException {
		//numeroAp
		List<AelItemSolicitacaoExames> listaItemSolicitacaoExame = getAelItemSolicitacaoExameDAO()
				.obterAelItemSolicitacaoExamesPorNumeroAP(numeroAp, lu2Seq);

		List<ItemSolicitacaoExameSismamaVO> listaItemSolicitacaoExameSismamaVO = new ArrayList<ItemSolicitacaoExameSismamaVO>();

		for (AelItemSolicitacaoExames itemSolicitacaoExame : listaItemSolicitacaoExame) {
			// Monta lista com os exames relacionados à SISMAMA
			
			AelAnatomoPatologico aelAnatomoPatologico = getExamesPatologiaFacade().obterAelAnatomoPatologicoPorItemSolic(itemSolicitacaoExame.getId().getSoeSeq(), itemSolicitacaoExame.getId().getSeqp());
			
			if (aelAnatomoPatologico != null) {
				
				if (getResultadoExameHistopatologicoRN().verificarExameSismama(
						aelAnatomoPatologico.getNumeroAp(),
						itemSolicitacaoExame.getId().getSeqp(),
						DESCRICAO_SISMAMA,
						aelAnatomoPatologico.getConfigExame().getSeq())) {
					
					String descricaoUsualExame = itemSolicitacaoExame.getExame()
							.getDescricaoUsual();
					
					AelRegiaoAnatomica regiaoAnatomica = itemSolicitacaoExame.getRegiaoAnatomica();
					
					if (regiaoAnatomica == null) {
						throw new ApplicationBusinessException(
								ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_REGIAO_ANATOMICA_NAO_ENCONTRADA);
					}
					
					String descricaoMaterialAnalise = itemSolicitacaoExame.getDescMaterialAnalise();
					
					StringBuilder descricaoCompletaMaterialAnalise = new StringBuilder(itemSolicitacaoExame
							.getRegiaoAnatomica().getDescricao());
					
					if (descricaoMaterialAnalise != null) {
						descricaoCompletaMaterialAnalise.append(' ').append(descricaoMaterialAnalise);
					}
					
					Integer tamanhoDescricao = descricaoCompletaMaterialAnalise.length();
					if (tamanhoDescricao > TAMANHO_MAXIMO_DESCRICAO_EXAME) {
						tamanhoDescricao = TAMANHO_MAXIMO_DESCRICAO_EXAME;
						descricaoCompletaMaterialAnalise.substring(0, tamanhoDescricao);
					}
					
					ItemSolicitacaoExameSismamaVO vo = new ItemSolicitacaoExameSismamaVO();
					vo.setItemSolicitacaoExame(itemSolicitacaoExame);
					vo.setDescricaoUsualExame(descricaoUsualExame);
					vo.setDescricaoMaterialAnalise(descricaoCompletaMaterialAnalise.toString());
					listaItemSolicitacaoExameSismamaVO.add(vo);
				}
				
			}
			
		}

		return listaItemSolicitacaoExameSismamaVO;
	}
	
	/**
	 * Busca a resposta salva de um campo para um determinado exame (soeSeq e seqp).
	 * As respostas são atualizadas no Map respostas passado como parâmetro.
	 * 
	 * ORADB: AELF_SISM_RESP_HIST / P_RECUPERA_RESPOSTAS
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param respostas
	 * @return Boolean indicando se recuperou respostas salvas.
	 */
	public void recuperarRespostas(Integer soeSeq, Short seqp,
			Map<String, Object> respostas)
			throws ApplicationBusinessException {
		
		Set<String> setCodigoCampo = respostas.keySet();
		AelSismamaHistoResDAO sismamaHistoResDAO = getAelSismamaHistoResDAO();
		Long countRespostas = sismamaHistoResDAO.obterRespostasSismamaHistoCountPorSoeSeqSeqp(soeSeq, seqp);
		
		if (countRespostas > 0) {
			for (String codigoCampo : setCodigoCampo) {
				String resposta = null;
				
				List<AelSismamaHistoRes> listaRespostaSismama = sismamaHistoResDAO
						.pesquisarSismamaHistoResPorSoeSeqSeqpCodigoCampo(soeSeq, seqp, codigoCampo);			
				
				if (!listaRespostaSismama.isEmpty()) {
					// Deve haver somente uma resposta para cada campo
					AelSismamaHistoRes respostaSismama = listaRespostaSismama.get(0);
					resposta = respostaSismama.getResposta();
				}

				if (resposta != null) {
					try {
						recuperarRespostasDominioEInteger(respostas, codigoCampo, resposta);
						recuperarRespostasBoolean(respostas, codigoCampo, resposta);
						recuperarRespostasString(respostas, codigoCampo, resposta);
					} catch (NumberFormatException e) {
						LOG.error(EXCECAO_LANCADA, e);
						throw new ApplicationBusinessException(
								ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA,
								codigoCampo);
					}
				}
			}
		}
	}
		
	private void recuperarRespostasDominioEInteger(Map<String, Object> respostas, String codigoCampo, String resposta) {
		if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_RES_PROCIR.name())) {
			respostas.put(codigoCampo, DominioSismamaProcedimentoCirurgico.getInstance(Integer.parseInt(resposta)));
		} 

		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_RES_ADEQ.name())) {
			respostas.put(codigoCampo, DominioSismamaAdequabilidadeMaterial.getInstance(Integer.parseInt(resposta)));
		} 
		
		// Campos de tipo DominioSismamaDimensMaxTumor
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_TAM_TUMDOM.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_DIM_TUMSEC.name())) {
			respostas.put(codigoCampo, DominioSismamaDimensMaxTumor.getInstance(Integer.parseInt(resposta)));
		}
		
		// Campos de tipo DominioSismamaSimNao
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_MIC_MICRCALC.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_ASSSEC.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_OUTHIS.name())) {
			respostas.put(codigoCampo, DominioSismamaSimNao.getInstance(Integer.parseInt(resposta)));
		} 
		
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_NEO_MALIG.name())) {
			respostas.put(codigoCampo, DominioSismamaLesaoCaraterNeoplasicoMaligno.getInstance(Integer.parseInt(resposta)));							
		} 
		
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_GRAU.name())) {
			respostas.put(codigoCampo, DominioSismamaGrau.getInstance(Integer.parseInt(resposta)));
		}
		
		// Campos de tipo DominioSismamaSimNaoNaoSabe
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_CENT.name()) 
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_PERIN.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_VASC.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_EMBO.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_FOCA.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_MAM.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_GRAD.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_MUSC.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_FASC.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_COAL.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_EXTR.name())) {
			respostas.put(codigoCampo, DominioSismamaSimNaoNaoSabe.getInstance(Integer.parseInt(resposta)));					
		}

		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_PELE.name())) {
			respostas.put(codigoCampo, DominioSismamaExtensaoTumorPele.getInstance(Integer.parseInt(resposta)));					
		}		
		
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_MARG.name())) {
			respostas.put(codigoCampo, DominioSismamaMargensCirurgicas.getInstance(Integer.parseInt(resposta)));	
		} 
		
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_LINFO.name())) {
			respostas.put(codigoCampo, DominioSismamaLinfonodos.getInstance(Integer.parseInt(resposta)));
		} 
		
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_LINFAV.name())) {
			respostas.put(codigoCampo, Integer.parseInt(resposta));						
		} 
		
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_LINFCO.name())) {
			respostas.put(codigoCampo, DominioSismamaNumeroLinfonodosComprometidos.getInstance(Integer.parseInt(resposta)));
		}
		
		// Campos de tipo DominioSismamaReceptor
		else if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_RECES.name()) 
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_RECPR.name())) {
			respostas.put(codigoCampo, DominioSismamaReceptor.getInstance(Integer.parseInt(resposta)));							
		}
	}
	
	private void recuperarRespostasBoolean(Map<String, Object> respostas, String codigoCampo, String resposta) {
		if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_HIPSATI.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_HIPCATI.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_LOBCATI.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_ADENOS.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_ESDERO.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_FIBROC.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_FIBROA.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_SOLITA.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_MASTIT.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_MULTI.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_FLORID.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_OUTROS.name()) 
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_IND.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_SUSP.name())) {

			respostas.put(codigoCampo, converterRespostaStringParaBoolean(resposta));
		}
	}
	
	private void recuperarRespostasString(Map<String, Object> respostas, String codigoCampo, String resposta) {
		if (codigoCampo.equals(DominioSismamaHistoCadCodigo.C_RES_INS_POR.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_BEN_OUTDES.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_NEO_OUTDES.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_ASSSESP.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_OUTDES.name())
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_HIS_OBS.name()) 
				|| codigoCampo.equals(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_POR.name())) {
			
			respostas.put(codigoCampo, resposta);		
		}
	}
	
	private Boolean converterRespostaStringParaBoolean(String resposta) {
		if (RESPOSTA_STRING_TRUE.equals(resposta)) {
			return Boolean.TRUE;
		} else if (RESPOSTA_STRING_FALSE.equals(resposta)) {
			return Boolean.FALSE;
		}
		
		return Boolean.FALSE;
	}
	
	private String converterRespostaBooleanParaString(Boolean resposta) {
		if (Boolean.TRUE.equals(resposta)) {
			return RESPOSTA_STRING_TRUE;
		} else {
			return RESPOSTA_STRING_FALSE;
		}
	}	
	
	/**
	 * Realiza a validação das respostas informadas.
	 * 
	 * ORADB: AELF_SISM_RESP_HIST / P_CRITICA_RESPOSTAS
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param respostas
	 * @return
	 */
	public void validarRespostas(Map<String, Object> respostas) throws ApplicationBusinessException {
		validarRespostaProcedimentoCirurgico(respostas);
		
		try {
			validarRespostasAdequabilidadeMaterial(respostas);
		} catch (ClassCastException e) {
			LOG.error(EXCECAO_LANCADA, e);
			throw new ApplicationBusinessException(
					ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA,
					DominioSismamaHistoCadCodigo.C_RES_ADEQ.name());
		}
		
		try {
			validarRespostasLesaoCaraterMaligno(respostas);
		} catch (ClassCastException e) {
			LOG.error(EXCECAO_LANCADA, e);
			throw new ApplicationBusinessException(
					ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA,
					DominioSismamaHistoCadCodigo.C_HIS_ASSSEC.name());
		}
				
		if (verificarPreenchimentoLinfonodos(respostas)) {			
			try {
				if (respostas.get(DominioSismamaHistoCadCodigo.C_HIS_LINFAV.name()) != null){
					Integer nroLinfonodosAvaliados = Integer.valueOf((Short) respostas.get(DominioSismamaHistoCadCodigo.C_HIS_LINFAV.name()));
					if (nroLinfonodosAvaliados != null && (nroLinfonodosAvaliados < 0 || nroLinfonodosAvaliados > 99)) {
						// Numero de linfonodos avaliados deverá ser entre 1 e 99.
						throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03277);
					}
				}
			} catch (ClassCastException e) {
				LOG.error(EXCECAO_LANCADA, e);
				throw new ApplicationBusinessException(
						ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA,
						DominioSismamaHistoCadCodigo.C_HIS_LINFAV.name());
			}
			
			
			validarRespostasLinfonodos(respostas);	
		}
		  
		try {
			validarRespostasOutrosEstudosImunoHistoquimicos(respostas);
		} catch (ClassCastException e) {
			LOG.error(EXCECAO_LANCADA, e);
			throw new ApplicationBusinessException(
					ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA,
					DominioSismamaHistoCadCodigo.C_HIS_OUTHIS.name());
		}

		try {
			validarRespostasLesaoBenigna(respostas);
		} catch (ClassCastException e) {
			LOG.error(EXCECAO_LANCADA, e);
			throw new ApplicationBusinessException(
					ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA_CAMPOS_LESAO_BENIGNA);
		}
		
	}
	
	private void validarRespostaProcedimentoCirurgico(Map<String, Object> respostas) 
			throws ApplicationBusinessException {
		if (respostas.get(DominioSismamaHistoCadCodigo.C_RES_PROCIR.name()) == null) {
			// Informe procedimento cirúrgico obrigatoriamente
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03314);
		}
	}
	
	private void validarRespostasAdequabilidadeMaterial(Map<String, Object> respostas) 
			throws ApplicationBusinessException, ClassCastException {
		Object objRespAdeq = respostas.get(DominioSismamaHistoCadCodigo.C_RES_ADEQ.name());
		if (objRespAdeq != null && ((DominioSismamaAdequabilidadeMaterial) objRespAdeq).equals(DominioSismamaAdequabilidadeMaterial.INSATISFATORIO)
				&& StringUtils.isEmpty((String) respostas.get(DominioSismamaHistoCadCodigo.C_RES_INS_POR.name()))) {
			// Preencha o motivo obrigatoriamente.
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03273);
		} else if (objRespAdeq == null) {
			// Preencha o adequabilidade do material obrigatoriamente.
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03315);
		}		
	}
	
	private void validarRespostasLesaoCaraterMaligno(Map<String, Object> respostas) 
			throws ApplicationBusinessException {
		Object objRespHisAsssec = respostas.get(DominioSismamaHistoCadCodigo.C_HIS_ASSSEC.name());
		if (objRespHisAsssec != null && ((DominioSismamaSimNao) objRespHisAsssec).equals(DominioSismamaSimNao.SIM) 
				&& StringUtils.isEmpty((String) respostas.get(DominioSismamaHistoCadCodigo.C_HIS_ASSSESP.name()))) {
			// Especifique o tipo histológico secundário obrigatoriamente.
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03276);
		}
	}
	
	private void validarRespostasOutrosEstudosImunoHistoquimicos(Map<String, Object> respostas) 
			throws ApplicationBusinessException, ClassCastException {
		Object objRespostaHisOutHis = respostas.get(DominioSismamaHistoCadCodigo.C_HIS_OUTHIS.name());
		if (objRespostaHisOutHis != null && ((DominioSismamaSimNao) objRespostaHisOutHis).equals(DominioSismamaSimNao.SIM)
				&& StringUtils.isEmpty((String) respostas.get(DominioSismamaHistoCadCodigo.C_HIS_OUTDES.name()))) {
			// Especifique os outros estudos imuno-histoquimicos obrigatóriamente.
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03278);
		}
	}
	
	private Boolean verificarPreenchimentoLinfonodos(Map<String, Object> respostas) {
		return respostas.get(DominioSismamaHistoCadCodigo.C_HIS_LINFO.name()) != null
				|| respostas.get(DominioSismamaHistoCadCodigo.C_HIS_LINFAV.name()) != null
				|| respostas.get(DominioSismamaHistoCadCodigo.C_HIS_LINFCO.name()) != null
				|| respostas.get(DominioSismamaHistoCadCodigo.C_HIS_COAL.name()) != null
				|| respostas.get(DominioSismamaHistoCadCodigo.C_HIS_EXTR.name()) != null;
	}
	
	private void validarRespostasLinfonodos(Map<String, Object> respostas) 
			throws ApplicationBusinessException {
		if (respostas.get(DominioSismamaHistoCadCodigo.C_HIS_LINFO.name()) == null) {
			// Informar se linfonodos axilares ou supraclaviculares obrigatoriamente.
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_SELECAO_LINFONODOS_OBRIGATORIA);
		} else if (respostas.get(DominioSismamaHistoCadCodigo.C_HIS_LINFCO.name()) == null) {
			// Informar número de linfonodos comprometidos obrigatoriamente.
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03316);
		} else if (respostas.get(DominioSismamaHistoCadCodigo.C_HIS_COAL.name()) == null) {
			// Informe a presença de coalescência linfonodal obrigatoriamente.
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03317);
		} else if (respostas.get(DominioSismamaHistoCadCodigo.C_HIS_EXTR.name()) == null) {
			// Informe se houve extravazamento da capsula linfonodal obrigatoriamente.
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03318);
		}
	}
	
	private void validarRespostasLesaoBenigna(Map<String, Object> respostas) 
			throws ApplicationBusinessException, ClassCastException {
		
		/*
		 * Atenção: os casts abaixo são feitos diretamente para Boolean considerando que as respostas dos
		 * campos abaixo foram setadas no map respostas como Boolean anteriormente na execução do método recuperarRespostas(...).
		 */
		
		DominioSismamaProcedimentoCirurgico procCirg = (DominioSismamaProcedimentoCirurgico) respostas.get(DominioSismamaHistoCadCodigo.C_RES_PROCIR.name());
		Boolean booleanRespCoreBiopsyIndeterminada = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_IND.name());
		String strRespCoreBiopsyIndPor = (String) respostas.get(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_POR.name());
		Boolean booleanRespCoreBiopsySuspeita = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_TEMP_CBIO_SUSP.name());
		
		Boolean booleanRespBenHipsati = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_HIPSATI.name());
		Boolean booleanRespBenHipcati = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_HIPCATI.name());
		Boolean booleanRespBenLobcati = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_LOBCATI.name());
		Boolean booleanRespBenAdenos = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_ADENOS.name());
		Boolean booleanRespBenEsdero = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_ESDERO.name());
		Boolean booleanRespBenFibroc = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_FIBROC.name());
		Boolean booleanRespBenFibroa = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_FIBROA.name());
		Boolean booleanRespBenSolita = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_SOLITA.name());
		Boolean booleanRespBenMulti = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_MULTI.name());
		Boolean booleanRespBenFlorid = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_FLORID.name());
		Boolean booleanRespBenMastit = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_MASTIT.name());
		Boolean booleanRespBenOutros = (Boolean) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_OUTROS.name());
		String strRespBenOutdes = (String) respostas.get(DominioSismamaHistoCadCodigo.C_BEN_OUTDES.name());

		DominioSismamaAdequabilidadeMaterial adeqMat = (DominioSismamaAdequabilidadeMaterial) respostas.get(DominioSismamaHistoCadCodigo.C_RES_ADEQ.name());

		validaRegrasCoreBiopsy(procCirg, booleanRespCoreBiopsyIndeterminada, strRespCoreBiopsyIndPor, booleanRespCoreBiopsySuspeita,
				booleanRespBenHipsati, booleanRespBenHipcati, booleanRespBenLobcati, booleanRespBenAdenos, booleanRespBenEsdero,
				booleanRespBenFibroc, booleanRespBenFibroa, booleanRespBenSolita, booleanRespBenMulti, booleanRespBenFlorid,
				booleanRespBenMastit, booleanRespBenOutros, strRespBenOutdes);
				
		
		validaBenignoOuMaligno(respostas, booleanRespBenHipsati, booleanRespBenHipcati, booleanRespBenLobcati, booleanRespBenAdenos,
				booleanRespBenEsdero, booleanRespBenFibroc, booleanRespBenFibroa, booleanRespBenSolita, booleanRespBenMulti,
				booleanRespBenFlorid, booleanRespBenMastit, booleanRespBenOutros, strRespBenOutdes);
		
		validaTipoLesaoPreenchido(respostas, booleanRespBenHipsati, booleanRespBenHipcati, booleanRespBenLobcati, booleanRespBenAdenos,
				booleanRespBenEsdero, booleanRespBenFibroc, booleanRespBenFibroa, booleanRespBenSolita, booleanRespBenMulti,
				booleanRespBenFlorid, booleanRespBenMastit, booleanRespBenOutros, strRespBenOutdes, adeqMat);
	}


	private void validaTipoLesaoPreenchido(Map<String, Object> respostas, Boolean booleanRespBenHipsati, Boolean booleanRespBenHipcati,
			Boolean booleanRespBenLobcati, Boolean booleanRespBenAdenos, Boolean booleanRespBenEsdero, Boolean booleanRespBenFibroc,
			Boolean booleanRespBenFibroa, Boolean booleanRespBenSolita, Boolean booleanRespBenMulti, Boolean booleanRespBenFlorid,
			Boolean booleanRespBenMastit, Boolean booleanRespBenOutros, String strRespBenOutdes,
			DominioSismamaAdequabilidadeMaterial adeqMat) throws ApplicationBusinessException {
		if ((adeqMat.getCodigo() == 3)
				&& verificarNaoPreenchimentoLesaoMaligna(respostas)
				&& ((booleanRespBenHipsati == null || !booleanRespBenHipsati) 
					&& (booleanRespBenHipcati == null || !booleanRespBenHipcati)
					&& (booleanRespBenLobcati == null || !booleanRespBenLobcati)
					&& (booleanRespBenAdenos == null || !booleanRespBenAdenos)
					&& (booleanRespBenEsdero == null || !booleanRespBenEsdero)
					&& (booleanRespBenFibroc == null || !booleanRespBenFibroc)
					&& (booleanRespBenFibroa == null || !booleanRespBenFibroa)
					&& (booleanRespBenSolita == null || !booleanRespBenSolita) 
					&& (booleanRespBenMulti  == null || !booleanRespBenMulti)
					&& (booleanRespBenFlorid == null || !booleanRespBenFlorid)
					&& (booleanRespBenMastit == null || !booleanRespBenMastit)
					&& (booleanRespBenOutros == null || !booleanRespBenOutros) 
					&& (StringUtils.isEmpty(strRespBenOutdes)))) {
			throw new ApplicationBusinessException(
					ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_ADEQ_SATIS_LESAO_NOK);
		}
	}


	private void validaBenignoOuMaligno(Map<String, Object> respostas, Boolean booleanRespBenHipsati, Boolean booleanRespBenHipcati,
			Boolean booleanRespBenLobcati, Boolean booleanRespBenAdenos, Boolean booleanRespBenEsdero, Boolean booleanRespBenFibroc,
			Boolean booleanRespBenFibroa, Boolean booleanRespBenSolita, Boolean booleanRespBenMulti, Boolean booleanRespBenFlorid,
			Boolean booleanRespBenMastit, Boolean booleanRespBenOutros, String strRespBenOutdes) throws ApplicationBusinessException {		
		if (verificarPreenchimentoLesaoMaligna(respostas)
			&& ((booleanRespBenHipsati != null && booleanRespBenHipsati)
				|| (booleanRespBenHipcati != null && booleanRespBenHipcati)
				|| (booleanRespBenLobcati != null && booleanRespBenLobcati)
				|| (booleanRespBenAdenos != null && booleanRespBenAdenos)
				|| (booleanRespBenEsdero != null && booleanRespBenEsdero)
				|| (booleanRespBenFibroc != null && booleanRespBenFibroc)
				|| (booleanRespBenFibroa != null && booleanRespBenFibroa)
				|| (booleanRespBenSolita != null && booleanRespBenSolita)
				|| (booleanRespBenMulti != null && booleanRespBenMulti)
				|| (booleanRespBenFlorid != null && booleanRespBenFlorid)
				|| (booleanRespBenMastit != null && booleanRespBenMastit)
				|| (booleanRespBenOutros != null && booleanRespBenOutros)
				|| (!StringUtils.isEmpty(strRespBenOutdes)))) {

			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.AEL_03279);
		}
	}
	
	private void validaRegrasCoreBiopsy(DominioSismamaProcedimentoCirurgico procCirg, Boolean booleanRespCoreBiopsyIndeterminada,
			String strRespCoreBiopsyIndPor, Boolean booleanRespCoreBiopsySuspeita, Boolean booleanRespBenHipsati,
			Boolean booleanRespBenHipcati, Boolean booleanRespBenLobcati, Boolean booleanRespBenAdenos, Boolean booleanRespBenEsdero,
			Boolean booleanRespBenFibroc, Boolean booleanRespBenFibroa, Boolean booleanRespBenSolita, Boolean booleanRespBenMulti,
			Boolean booleanRespBenFlorid, Boolean booleanRespBenMastit, Boolean booleanRespBenOutros, String strRespBenOutdes)
			throws ApplicationBusinessException {
		if (DominioSismamaProcedimentoCirurgico.BIOPSIA_POR_AGULHA_GROSSA.equals(procCirg)) {
			validaCoreBiopsyPreenchida(booleanRespCoreBiopsyIndeterminada, strRespCoreBiopsyIndPor, booleanRespBenHipsati,
					booleanRespBenHipcati, booleanRespBenLobcati, booleanRespBenAdenos, booleanRespBenEsdero, booleanRespBenFibroc,
					booleanRespBenFibroa, booleanRespBenSolita, booleanRespBenMulti, booleanRespBenFlorid, booleanRespBenMastit,
					booleanRespBenOutros, strRespBenOutdes);
		} else if (!DominioSismamaProcedimentoCirurgico.BIOPSIA_POR_AGULHA_GROSSA.equals(procCirg)
				&& ((booleanRespCoreBiopsyIndeterminada != null && booleanRespCoreBiopsyIndeterminada)
						|| (booleanRespCoreBiopsySuspeita != null && booleanRespCoreBiopsySuspeita) || (!StringUtils
							.isEmpty(strRespCoreBiopsyIndPor)))) {
			throw new ApplicationBusinessException(ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_CORE_BIOPSY);
		}
	}


	private void validaCoreBiopsyPreenchida(Boolean booleanRespCoreBiopsyIndeterminada, String strRespCoreBiopsyIndPor,
			Boolean booleanRespBenHipsati, Boolean booleanRespBenHipcati, Boolean booleanRespBenLobcati, Boolean booleanRespBenAdenos,
			Boolean booleanRespBenEsdero, Boolean booleanRespBenFibroc, Boolean booleanRespBenFibroa, Boolean booleanRespBenSolita,
			Boolean booleanRespBenMulti, Boolean booleanRespBenFlorid, Boolean booleanRespBenMastit, Boolean booleanRespBenOutros,
			String strRespBenOutdes) throws ApplicationBusinessException {
		Boolean benigno = (booleanRespBenHipsati == null || !booleanRespBenHipsati) && (booleanRespBenHipcati == null || !booleanRespBenHipcati)
				&& (booleanRespBenLobcati == null || !booleanRespBenLobcati) && (booleanRespBenAdenos == null || !booleanRespBenAdenos)
				&& (booleanRespBenEsdero == null || !booleanRespBenEsdero) && (booleanRespBenFibroc == null || !booleanRespBenFibroc)
				&& (booleanRespBenFibroa == null || !booleanRespBenFibroa) && (booleanRespBenSolita == null || !booleanRespBenSolita)
				&& (booleanRespBenMulti == null || !booleanRespBenMulti) && (booleanRespBenFlorid == null || !booleanRespBenFlorid)
				&& (booleanRespBenMastit == null || !booleanRespBenMastit) && (booleanRespBenOutros == null || !booleanRespBenOutros)
				&& (StringUtils.isEmpty(strRespBenOutdes));
		if (benigno	&& (booleanRespCoreBiopsyIndeterminada != null && booleanRespCoreBiopsyIndeterminada)) {
			throw new ApplicationBusinessException(
					ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_CORE_BIOPSY_BENIGNO_OBRIGATORIO);
		} else if ((booleanRespCoreBiopsyIndeterminada != null && booleanRespCoreBiopsyIndeterminada)
				&& StringUtils.isEmpty(strRespCoreBiopsyIndPor)) {
			throw new ApplicationBusinessException(
					ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_CORE_BIOPSY_MOTIVO_OBRIG);
		} else if ((booleanRespCoreBiopsyIndeterminada == null || !booleanRespCoreBiopsyIndeterminada) && !StringUtils.isEmpty(strRespCoreBiopsyIndPor)) {
			throw new ApplicationBusinessException(
					ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_CORE_BIOPSY_MOTIVO_NOK);
		}
	}
	
	private Boolean verificarPreenchimentoLesaoMaligna(Map<String, Object> respostas) {
		return respostas.get(DominioSismamaHistoCadCodigo.C_NEO_MALIG.name()) != null
				|| !StringUtils.isEmpty((String) respostas.get(DominioSismamaHistoCadCodigo.C_NEO_OUTDES.name()))
				|| respostas.get(DominioSismamaHistoCadCodigo.C_HIS_ASSSEC.name()) != null 
				|| !StringUtils.isEmpty((String) respostas.get(DominioSismamaHistoCadCodigo.C_HIS_ASSSESP.name()));
	}
	
	private Boolean verificarNaoPreenchimentoLesaoMaligna(Map<String, Object> respostas) {
		return respostas.get(DominioSismamaHistoCadCodigo.C_NEO_MALIG.name()) == null
				&& StringUtils.isEmpty((String) respostas.get(DominioSismamaHistoCadCodigo.C_NEO_OUTDES.name()))
				&& respostas.get(DominioSismamaHistoCadCodigo.C_HIS_ASSSEC.name()) == null 
				&& StringUtils.isEmpty((String) respostas.get(DominioSismamaHistoCadCodigo.C_HIS_ASSSESP.name()));
	}
	
	/**
	 * Salva as respostas informadas para os 
	 * campos de um determinado exame (soeSeq e seqp).
	 * 
	 * ORADB: AELF_SISM_RESP_HIST / P_ARMAZENA_RESPOSTAS
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param respostas
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public void gravarRespostas(Integer soeSeq, Short seqp,
			Map<String, Object> respostas)
			throws ApplicationBusinessException {
		
		Set<String> setCodigoCampo = respostas.keySet();
		AelItemSolicitacaoExamesId itemSolicitacaoExameId = 
				new AelItemSolicitacaoExamesId(soeSeq, seqp);
		AelItemSolicitacaoExames itemSolicitacaoExame = 
				getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(itemSolicitacaoExameId);				

		for (String codigoCampo : setCodigoCampo) {
			Object objResposta = respostas.get(codigoCampo);
			String resposta = null;
					
			try {
				if (objResposta instanceof Dominio) {
					resposta = String.valueOf(((Dominio) objResposta).getCodigo()); 
				} else if (objResposta instanceof Integer) {
					resposta = String.valueOf((Integer) objResposta); 
				} else if (objResposta instanceof Boolean) {
					resposta = converterRespostaBooleanParaString((Boolean) objResposta);
				} else if (objResposta instanceof String) {
					resposta = (String) objResposta;
				} else if (objResposta instanceof Short){
					resposta = String.valueOf((Short) objResposta);
				}
			} catch (ClassCastException e) {
				LOG.error(EXCECAO_LANCADA, e);
				throw new ApplicationBusinessException(
						ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_PROBLEMA_AO_OBTER_RESPOSTA,
						codigoCampo);
			}
			
			// Pesquisa para ver se resposta já foi salva para o campo
			List<AelSismamaHistoRes> listaSismamaHistoRes = getAelSismamaHistoResDAO()
					.pesquisarSismamaHistoResPorSoeSeqSeqpCodigoCampo(soeSeq, seqp,
							codigoCampo);

			if (listaSismamaHistoRes.isEmpty()) {
				AelSismamaHistoCad sismamaHistoCad =
						getAelSismamaHistoCadDAO().obterPorChavePrimaria(codigoCampo);
				AelSismamaHistoRes novoSismamaHistoRes = new AelSismamaHistoRes();
				novoSismamaHistoRes.setItemSolicitacaoExame(itemSolicitacaoExame);
				novoSismamaHistoRes.setSismamaHistoCad(sismamaHistoCad);
				novoSismamaHistoRes.setResposta(resposta);
				
				try {
					getResultadoExameHistopatologicoRN().inserirResultadoExameHistopatologico(novoSismamaHistoRes);
				} catch (ApplicationBusinessException e) {
					LOG.error("Exceção ApplicationBusinessException capturada, lançada para cima.");
					throw e;
				} catch (Exception e) {
					LOG.error(EXCECAO_LANCADA, e);
					throw new ApplicationBusinessException(ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_INSERIR_RESPOSTA, codigoCampo);
				}
			} else {
				AelSismamaHistoRes sismamaHistoRes = listaSismamaHistoRes.get(0);
				sismamaHistoRes.setResposta(resposta);

				try {
					getResultadoExameHistopatologicoRN().atualizarResultadoExameHistopatologico(sismamaHistoRes);
				} catch (Exception e) {
					LOG.error(EXCECAO_LANCADA, e);
					throw new ApplicationBusinessException(ResultadoExameSismamaONCode.ERRO_RESULTADO_EXAME_HISTOPAT_SISMAMA_ATUALIZAR_RESPOSTA, codigoCampo);
				}
			}
		}
	}
	
	protected AelAgrpPesquisaXExameDAO getAelAgrpPesquisaXExameDAO() {
		return aelAgrpPesquisaXExameDAO;
	}
	
	protected AelSismamaHistoResDAO getAelSismamaHistoResDAO() {
		return aelSismamaHistoResDAO;
	}
	
	protected AelSismamaHistoCadDAO getAelSismamaHistoCadDAO() {
		return aelSismamaHistoCadDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected ResultadoExameHistopatologicoRN getResultadoExameHistopatologicoRN() {
		return resultadoExameHistopatologicoRN;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return examesPatologiaFacade;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

}
