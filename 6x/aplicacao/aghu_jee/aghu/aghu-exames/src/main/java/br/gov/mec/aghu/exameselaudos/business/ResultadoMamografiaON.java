package br.gov.mec.aghu.exameselaudos.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioCategoriaBiRadsMamografia;
import br.gov.mec.aghu.dominio.DominioRecomendacaoMamografia;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Utilizada pela estoria #5978 - Preencher achados radiologicos do resultado da mamografia (SISMAMA)
 *
 */
@Stateless
public class ResultadoMamografiaON extends BaseBusiness {


@EJB
private ResultadoMamografiaRN resultadoMamografiaRN;

@EJB
private LaudoMamografiaRN laudoMamografiaRN;

@EJB
private ResultadoMamografiaRespostasRN resultadoMamografiaRespostasRN;

private static final Log LOG = LogFactory.getLog(ResultadoMamografiaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116321235468506589L;
	

	public enum ResultadoMamografiaONExceptionCode implements BusinessExceptionCode {
		AEL_03243;
	}	
	
	public List<AelItemSolicitacaoExames> pesquisarItemSolicitacaoExames(Integer soeSeq, Short ufeUnfSeq) throws ApplicationBusinessException {
		List<AelItemSolicitacaoExames> retorno = new ArrayList<AelItemSolicitacaoExames>();
		for (AelItemSolicitacaoExames ise : getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoPorSolicitacaoUnidadeExecutora(soeSeq, ufeUnfSeq)) {
			if (getResultadoMamografiaRN().verificarExameIsSismama(ise.getExame().getSigla(), ise.getMaterialAnalise().getSeq(), ise.getUnidadeFuncional().getSeq())) {
				if (getResultadoMamografiaRN().verificarUnidadeFuncionalTemCaracteristica(ise.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.EXAME_SISMAMA_MAMO)) {
					if (getResultadoMamografiaRN().verificarSituacaoExameMamografia(ise.getSituacaoItemSolicitacao().getCodigo())) {
						retorno.add(ise);															
					}
				}
			}
		}
		return retorno;
	}

	public DominioRecomendacaoMamografia onChangeCategoria(Map<String, AelSismamaMamoResVO> mapAbaConclusao, boolean mamaDireita) {
		Integer categoriaMama = 0;//Default(Selecione)
		DominioCategoriaBiRadsMamografia categoriaMamaDireita = mapAbaConclusao.get(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D.toString()).getCategoria();
		DominioCategoriaBiRadsMamografia categoriaMamaEsquerda = mapAbaConclusao.get(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_E.toString()).getCategoria();
		
		if (mamaDireita) {
			if (categoriaMamaDireita != null) {
				categoriaMama = categoriaMamaDireita.getCodigo(); 
			}
		} else {
			if (categoriaMamaEsquerda != null) {
				categoriaMama = categoriaMamaEsquerda.getCodigo();
			}
		}
		
		switch (categoriaMama) {
			case 1:
				return DominioRecomendacaoMamografia.COMPLEMENTACAO_COM_ULTRASSONOGRAFIA;
			case 2:
				return DominioRecomendacaoMamografia.MAMOGRAFIA_1_ANO;
			case 3:
				return DominioRecomendacaoMamografia.MAMOGRAFIA_1_ANO;
			case 4:
				return DominioRecomendacaoMamografia.CONTROLE_RADIOLOGICO_6_MESES;
			case 5:
				return DominioRecomendacaoMamografia.HISTOPATOLOGICO;
			case 6:
				return DominioRecomendacaoMamografia.HISTOPATOLOGICO;
			case 7:
				return DominioRecomendacaoMamografia.TERAPEUTICA_ESPECIFICA;
			default:
				return null;
		}
	}
	
	public List<RapServidores> pesquisarResidente(Object obj) throws ApplicationBusinessException {
		BigDecimal vincFunc = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_VINC_FUNC).getVlrNumerico();
		BigDecimal matriculaResidente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATRICULA_RESIDENTE).getVlrNumerico();
		
		return getRegistroColaboradorFacade().pesquisarResidente(obj, vincFunc.shortValue(), matriculaResidente.intValue());
	}
	
	public Long pesquisarResidenteCount(Object obj) throws ApplicationBusinessException {
		BigDecimal vincFunc = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_VINC_FUNC).getVlrNumerico();
		BigDecimal matriculaResidente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATRICULA_RESIDENTE).getVlrNumerico();
		
		return getRegistroColaboradorFacade().pesquisarResidenteCount(obj, vincFunc.shortValue(), matriculaResidente.intValue());
	}
	
	public void reabrirLaudo(String situacaoAreaExecutora, String situacaoLiberado, Integer solicitacao, Short item) {
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
		
		AelSitItemSolicitacoes sise = getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(situacaoAreaExecutora);
		ise.setSituacaoItemSolicitacao(sise);
		
		getAelItemSolicitacaoExameDAO().atualizar(ise);
	}	
	
	private Boolean habilitarMama(String habilitarMamaParam) {
		Boolean habilitarMama = false;

		if (habilitarMamaParam != null && habilitarMamaParam.equals("S")) {
			habilitarMama = true;
		}
		
		return habilitarMama;
	}
	
	public Integer assinarLaudo(String situacaoLiberado, Integer solicitacao, Short item, Map<String, AelSismamaMamoResVO> mapMamaD, 
			Map<String, AelSismamaMamoResVO> mapMamaE, Map<String, AelSismamaMamoResVO> mapAbaConclusao, String rxMamaBilateral, 
			String sexoPaciente, String habilitaMamaEsquerda, String habilitaMamaDireita, 
			String nomeResponsavel, Integer matricula, Integer vinCodigo,
			String infoClinicas, String nomeMicrocomputador) throws BaseException, BaseListException {
		
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
		
		// p_salva_dados
		salvarDados(ise, mapMamaD, mapMamaE, mapAbaConclusao, rxMamaBilateral, sexoPaciente, 
				habilitarMama(habilitaMamaEsquerda), habilitarMama(habilitaMamaDireita), nomeResponsavel, infoClinicas);

		//precisa dar flush após gravar pq vai usar dados do BD para montar o laudo
		getAelItemSolicitacaoExameDAO().flush();
		
		// grava o resultado do exame solicitado ==> aqui dentro faz flush
		getLaudoMamografiaRN().gerarLaudoExameMamografiaSismama(solicitacao, item, nomeMicrocomputador);
		
		//obtem a situacao liberado
		AelSitItemSolicitacoes situacao = getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(situacaoLiberado);
		
		// atualiza o exame solicitado para liberado
		ise.setSituacaoItemSolicitacao(situacao);
		
		RapServidores servidor = getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(matricula, vinCodigo.shortValue());
		
		ise.setServidorResponsabilidade(servidor);

		getSolicitacaoExameFacade().atualizarSemFlush(ise, nomeMicrocomputador, true);
		
		// p_imprime_laudo
		return getResultadoMamografiaRN().imprimirLaudo(ise);
	}
	
	public Boolean exibirModalReabrirLaudo(String situacaoLiberado, Integer solicitacao, Short item) {
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
		
		if (ise != null && ise.getSituacaoItemSolicitacao() != null && ise.getSituacaoItemSolicitacao().getCodigo().equals(situacaoLiberado)) {
			return true;
		}
		return false;
	}
	
	public Boolean exibirModalAssinarLaudo(String situacaoAreaExecutora, String situacaoExecutando, Integer solicitacao, Short item) {
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);		

		if (ise != null && ise.getSituacaoItemSolicitacao() != null) {
			if (ise.getSituacaoItemSolicitacao().getCodigo().equals(situacaoAreaExecutora) 
					|| ise.getSituacaoItemSolicitacao().getCodigo().equals(situacaoExecutando)) {
				return true;
			}
		}
		return false;
	}
	
	private AelItemSolicitacaoExames obterAelItemSolicitacaoExamesPorChavePrimaria(Integer solicitacao, Short item) {
		AelItemSolicitacaoExamesId chavePrimaria = new AelItemSolicitacaoExamesId(solicitacao, item);
		return getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(chavePrimaria);		
	}
	
	protected ResultadoMamografiaRN getResultadoMamografiaRN() {
		return resultadoMamografiaRN;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}
	
	protected ResultadoMamografiaRespostasRN getResultadoMamografiaRespostasRN(){
		return resultadoMamografiaRespostasRN;
	}
	
	protected LaudoMamografiaRN getLaudoMamografiaRN() {
		return laudoMamografiaRN;
	}
	
	/**
	 * @throws ApplicationBusinessException  
	 * @throws BaseListException 
	 * @ORADB p_salva_dados
	 * Apenas chamadas para o: p_consiste_dados, p_armazena_dados
	 * 
	 */
	public void salvarDados(AelItemSolicitacaoExames aelItemSolicitacaoExames, 
							 Map<String, AelSismamaMamoResVO> mapMamaD, 
							 Map<String, AelSismamaMamoResVO> mapMamaE, 
							 Map<String, AelSismamaMamoResVO> mapConclusao,
							 String rxMamaBilateral,
							 String vSexoPaciente,
							 boolean vHabilitaMamaEsquerda,
							 boolean vHabilitaMamaDireita,
							 String medicoResponsavel, String infoClinicas) throws ApplicationBusinessException, BaseListException {
		
		Short iseSeqp = aelItemSolicitacaoExames.getId().getSeqp();
		Integer iseSoeSeqp = aelItemSolicitacaoExames.getId().getSoeSeq();
		
		//p_consiste_dados
		getResultadoMamografiaRN().consistirDados(aelItemSolicitacaoExames, 
													mapMamaD, 
													mapMamaE, 
													mapConclusao, 
													rxMamaBilateral,  
													vSexoPaciente, 
													vHabilitaMamaEsquerda, 
													vHabilitaMamaDireita,
													medicoResponsavel);
		
		
		//p_armazena_respostas
		getResultadoMamografiaRespostasRN().armazenarRespostas(mapMamaD, 
				mapMamaE, 
				mapConclusao, 
				iseSeqp, 
				iseSoeSeqp, 
				vHabilitaMamaEsquerda, 
				vHabilitaMamaDireita, infoClinicas);
		

	}
	
	public void validarResponsavel(Integer matricula, Integer vinCodigo) throws ApplicationBusinessException {
		if (matricula == null || vinCodigo == null) {
			throw new ApplicationBusinessException(ResultadoMamografiaONExceptionCode.AEL_03243);
		}
	}

	/**
	 * 05-ISE-AEL_ITEM_SOLICITACAO_EXAMES-new-record-instance
	 * @param iseSeqpLida
	 * @param solicitacao
	 * @param item
	 * @param rxMamaEsquerda
	 * @param rxMamaDireita
	 * @return
	 */
	public Map<String, Object> montarControleAbas(Short iseSeqpLida, Integer solicitacao, Short item, String rxMamaEsquerda, String rxMamaDireita) {
		Map<String, Object> mapAbas = new HashMap<String, Object>();
		
		if (iseSeqpLida == null) {
			AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
			
			if (!ise.getExame().getSigla().equals(rxMamaDireita) && !ise.getExame().getSigla().equals(rxMamaEsquerda)) {
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITA_MAMA_D.toString(), "S");
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITA_MAMA_E.toString(), "S");
			} else if (ise.getExame().getSigla().equals(rxMamaDireita)) {
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITA_MAMA_D.toString(), "S");
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITA_MAMA_E.toString(), "N");
			} else if (ise.getExame().getSigla().equals(rxMamaEsquerda)) {
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITA_MAMA_D.toString(), "N");
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITA_MAMA_E.toString(), "S");
			}
			
			mapAbas.put(DominioSismamaMamoCadCodigo.ISE_SEQP_LIDA.toString(), ise.getId().getSeqp());
		}
		
		return mapAbas;
	}

	/**
	 * 01-p_analisa_dados_ise
	 * OBS.: No arquivo p_analisa_dados_ise, existe um ELSE no final da condição, 
	 * mas de acordo com o documento, ele pode ser desconsiderado. Sendo assim ele não foi implementado.
	 * @param solicitacao
	 * @param item
	 * @param situacaoLiberado
	 * @param situacaoAreaExecutora
	 * @param situacaoExecutando
	 * @param habilitaMamaDireita
	 * @param habilitaMamaEsquerda
	 * @return
	 */
	public Map<String, Boolean> montarControleTela(Integer solicitacao, Short item, String situacaoLiberado, String situacaoAreaExecutora, 
			String situacaoExecutando, String habilitaMamaDireita, String habilitaMamaEsquerda) {
		
		Map<String, Boolean> mapAbas = new HashMap<String, Boolean>();
		
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
		
		if (ise.getSituacaoItemSolicitacao().getCodigo().equals(situacaoLiberado)) {
			mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_ABA_1.toString(), Boolean.FALSE);
			mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_ABA_2.toString(), Boolean.FALSE);
			mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_ABA_3.toString(), Boolean.TRUE);
			mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_CAMPOS_ABA_3.toString(), Boolean.FALSE);
			mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_BOTAO_GRAVAR.toString(), Boolean.FALSE);
			mapAbas.put(DominioSismamaMamoCadCodigo.MOSTRAR_BOTAO_ASSINAR_LAUDO.toString(), Boolean.FALSE);
			mapAbas.put(DominioSismamaMamoCadCodigo.MOSTRAR_BOTAO_REABRIR_LAUDO.toString(), Boolean.TRUE);
		} else if (ise.getSituacaoItemSolicitacao().getCodigo().equals(situacaoAreaExecutora) 
				|| ise.getSituacaoItemSolicitacao().getCodigo().equals(situacaoExecutando)) {
			if (habilitaMamaDireita.equals("S")) {
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_ABA_1.toString(), Boolean.TRUE);
			} else {
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_ABA_1.toString(), Boolean.FALSE);
			}

			if (habilitaMamaEsquerda.equals("S")) {
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_ABA_2.toString(), Boolean.TRUE);
			} else {
				mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_ABA_2.toString(), Boolean.FALSE);
			}
			
			mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_ABA_3.toString(), Boolean.TRUE);
			mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_CAMPOS_ABA_3.toString(), Boolean.TRUE);
			mapAbas.put(DominioSismamaMamoCadCodigo.HABILITAR_BOTAO_GRAVAR.toString(), Boolean.TRUE);
			mapAbas.put(DominioSismamaMamoCadCodigo.MOSTRAR_BOTAO_ASSINAR_LAUDO.toString(), Boolean.TRUE);
			mapAbas.put(DominioSismamaMamoCadCodigo.MOSTRAR_BOTAO_REABRIR_LAUDO.toString(), Boolean.FALSE);
		}
		
		return mapAbas;
	}
	
	
	/**
	 * INICIALIZANDO MAPS
	 */
	public Map<String, AelSismamaMamoResVO> inicializarMapDireita() {
		Map<String, AelSismamaMamoResVO> mapMamaD = new HashMap<String, AelSismamaMamoResVO>();
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString(), new AelSismamaMamoResVO());
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_PELE_D.toString(), new AelSismamaMamoResVO());
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_COMPOSIC_D.toString(), new AelSismamaMamoResVO());
		// nodulo 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_SIM_01D.toString(), new AelSismamaMamoResVO());
		// localizacao 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_LOC_01D.toString(), new AelSismamaMamoResVO());
		// tamanho 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_TAM_01D.toString(), new AelSismamaMamoResVO());		
		// contorno 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_CONT_01D.toString(), new AelSismamaMamoResVO());
		// limite 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_LIM_01D.toString(), new AelSismamaMamoResVO());
		// nodulo 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_SIM_02D.toString(), new AelSismamaMamoResVO());
		// localizacao 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_LOC_02D.toString(), new AelSismamaMamoResVO());
		// tamanho 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_TAM_02D.toString(), new AelSismamaMamoResVO());		
		// contorno 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_CONT_02D.toString(), new AelSismamaMamoResVO());
		// limite 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_LIM_02D.toString(), new AelSismamaMamoResVO());
		// nodulo 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_SIM_03D.toString(), new AelSismamaMamoResVO());
		// localizacao 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_LOC_03D.toString(), new AelSismamaMamoResVO());
		// tamanho 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_TAM_03D.toString(), new AelSismamaMamoResVO());		
		// contorno 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_CONT_03D.toString(), new AelSismamaMamoResVO());
		// limite 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_LIM_03D.toString(), new AelSismamaMamoResVO());
		// microcalcificacao 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_SIM_01D.toString(), new AelSismamaMamoResVO());
		// localizacao 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_LOC_01D.toString(), new AelSismamaMamoResVO());
		// forma 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_FORM_01D.toString(), new AelSismamaMamoResVO());		
		// distribuicao 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_DISTR_01D.toString(), new AelSismamaMamoResVO());
		// microcalcificacao 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_SIM_02D.toString(), new AelSismamaMamoResVO());
		// localizacao 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_LOC_02D.toString(), new AelSismamaMamoResVO());
		// forma 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_FORM_02D.toString(), new AelSismamaMamoResVO());		
		// distribuicao 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_DISTR_02D.toString(), new AelSismamaMamoResVO());		
		// microcalcificacao 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_SIM_03D.toString(), new AelSismamaMamoResVO());
		// localizacao 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_LOC_03D.toString(), new AelSismamaMamoResVO());
		// forma 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_FORM_03D.toString(), new AelSismamaMamoResVO());		
		// distribuicao 3
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_MICRO_DISTR_03D.toString(), new AelSismamaMamoResVO());
		// assimetria 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM01D.toString(), new AelSismamaMamoResVO());
		// localizacao assimetria 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC01D.toString(), new AelSismamaMamoResVO());
		// distorcao 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM01D.toString(), new AelSismamaMamoResVO());		
		// localizacao distorcao 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC01D.toString(), new AelSismamaMamoResVO());		
		// assimetria 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM02D.toString(), new AelSismamaMamoResVO());
		// localizacao assimetria 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC02D.toString(), new AelSismamaMamoResVO());
		// distorcao 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM02D.toString(), new AelSismamaMamoResVO());		
		// localizacao distorcao 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC02D.toString(), new AelSismamaMamoResVO());
		// assimetria 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM01D.toString(), new AelSismamaMamoResVO());
		// localizacao assimetria 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC01D.toString(), new AelSismamaMamoResVO());
		// area densa 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM01D.toString(), new AelSismamaMamoResVO());		
		// localizacao area densa 1
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_AR_DENS_LOC01D.toString(), new AelSismamaMamoResVO());		
		// assimetria 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM02D.toString(), new AelSismamaMamoResVO());
		// localizacao assimetria 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC02D.toString(), new AelSismamaMamoResVO());
		// area densa 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM02D.toString(), new AelSismamaMamoResVO());		
		// localizacao area densa 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_AR_DENS_LOC02D.toString(), new AelSismamaMamoResVO());
		// linfonodos axilares 2
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_LINF_AUX_D.toString(), new AelSismamaMamoResVO());
		// aumentados
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_D.toString(), new AelSismamaMamoResVO());
		// densos
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_D.toString(), new AelSismamaMamoResVO());		
		// confluentes
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_D.toString(), new AelSismamaMamoResVO());
		// nodulo com densidade gordura
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_DEN_D.toString(), new AelSismamaMamoResVO());		
		// calcificacoes vasculares
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_CALC_VASC_D.toString(), new AelSismamaMamoResVO());
		// distorcao arquitetural por cirurgia
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_DIS_ARQ_CIR_D.toString(), new AelSismamaMamoResVO());
		// nodulo calcificado
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_CAL_D.toString(), new AelSismamaMamoResVO());		
		// outras calcificacoes aspecto benigno
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_CALC_ASP_BEN_D.toString(), new AelSismamaMamoResVO());
		// implante integro
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_IMP_INTEG_D.toString(), new AelSismamaMamoResVO());		
		// nodulo com densidade heteDominioComposicaoMamarogenea
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_NOD_DEN_HET_D.toString(), new AelSismamaMamoResVO());
		// linfonodos intramamarios
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_LINF_INTRAM_D.toString(), new AelSismamaMamoResVO());
		// implante com sinais de ruptura
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_IMP_SIN_RUP_D.toString(), new AelSismamaMamoResVO());		
		// dilatacao ductal regiao retroareolar
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_DILAT_DUC_D.toString(), new AelSismamaMamoResVO());
		
		return mapMamaD;
	}
	
	public Map<String, AelSismamaMamoResVO> inicializarMapEsquerda() {
		Map<String, AelSismamaMamoResVO> mapMamaE = new HashMap<String, AelSismamaMamoResVO>();
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString(), new AelSismamaMamoResVO());
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_RAD_PELE_E.toString(), new AelSismamaMamoResVO());
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_RAD_COMPOSIC_E.toString(), new AelSismamaMamoResVO());
		// nodulo 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_SIM_01E.toString(), new AelSismamaMamoResVO());
		// localizacao 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_LOC_01E.toString(), new AelSismamaMamoResVO());
		// tamanho 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_TAM_01E.toString(), new AelSismamaMamoResVO());		
		// contorno 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_CONT_01E.toString(), new AelSismamaMamoResVO());
		// limite 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_LIM_01E.toString(), new AelSismamaMamoResVO());
		// nodulo 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_SIM_02E.toString(), new AelSismamaMamoResVO());
		// localizacao 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_LOC_02E.toString(), new AelSismamaMamoResVO());
		// tamanho 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_TAM_02E.toString(), new AelSismamaMamoResVO());		
		// contorno 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_CONT_02E.toString(), new AelSismamaMamoResVO());
		// limite 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_LIM_02E.toString(), new AelSismamaMamoResVO());
		// nodulo 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_SIM_03E.toString(), new AelSismamaMamoResVO());
		// localizacao 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_LOC_03E.toString(), new AelSismamaMamoResVO());
		// tamanho 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_TAM_03E.toString(), new AelSismamaMamoResVO());		
		// contorno 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_CONT_03E.toString(), new AelSismamaMamoResVO());
		// limite 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_LIM_03E.toString(), new AelSismamaMamoResVO());
		// microcalcificacao 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_SIM_01E.toString(), new AelSismamaMamoResVO());
		// localizacao 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_LOC_01E.toString(), new AelSismamaMamoResVO());
		// forma 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_FORM_01E.toString(), new AelSismamaMamoResVO());		
		// distribuicao 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_DISTR_01E.toString(), new AelSismamaMamoResVO());
		// microcalcificacao 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_SIM_02E.toString(), new AelSismamaMamoResVO());
		// localizacao 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_LOC_02E.toString(), new AelSismamaMamoResVO());
		// forma 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_FORM_02E.toString(), new AelSismamaMamoResVO());		
		// distribuicao 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_DISTR_02E.toString(), new AelSismamaMamoResVO());		
		// microcalcificacao 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_SIM_03E.toString(), new AelSismamaMamoResVO());
		// localizacao 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_LOC_03E.toString(), new AelSismamaMamoResVO());
		// forma 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_FORM_03E.toString(), new AelSismamaMamoResVO());		
		// distribuicao 3
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_MICRO_DISTR_03E.toString(), new AelSismamaMamoResVO());
		// assimetria 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM01E.toString(), new AelSismamaMamoResVO());
		// localizacao assimetria 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC01E.toString(), new AelSismamaMamoResVO());
		// distorcao 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM01E.toString(), new AelSismamaMamoResVO());		
		// localizacao distorcao 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC01E.toString(), new AelSismamaMamoResVO());		
		// assimetria 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM02E.toString(), new AelSismamaMamoResVO());
		// localizacao assimetria 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC02E.toString(), new AelSismamaMamoResVO());
		// distorcao 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM02E.toString(), new AelSismamaMamoResVO());		
		// localizacao distorcao 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC02E.toString(), new AelSismamaMamoResVO());
		// assimetria 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM01E.toString(), new AelSismamaMamoResVO());
		// localizacao assimetria 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC01E.toString(), new AelSismamaMamoResVO());
		// area densa 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM01E.toString(), new AelSismamaMamoResVO());		
		// localizacao area densa 1
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_AR_DENS_LOC01E.toString(), new AelSismamaMamoResVO());		
		// assimetria 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM02E.toString(), new AelSismamaMamoResVO());
		// localizacao assimetria 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC02E.toString(), new AelSismamaMamoResVO());
		// area densa 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM02E.toString(), new AelSismamaMamoResVO());		
		// localizacao area densa 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_AR_DENS_LOC02E.toString(), new AelSismamaMamoResVO());
		// linfonodos axilares 2
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_LINF_AUX_E.toString(), new AelSismamaMamoResVO());
		// aumentados
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_E.toString(), new AelSismamaMamoResVO());
		// densos
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_E.toString(), new AelSismamaMamoResVO());		
		// confluentes
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_E.toString(), new AelSismamaMamoResVO());
		// nodulo com densidade gordura
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_DEN_E.toString(), new AelSismamaMamoResVO());		
		// calcificacoes vasculares
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_CALC_VASC_E.toString(), new AelSismamaMamoResVO());
		// distorcao arquitetural por cirurgia
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_DIS_ARQ_CIR_E.toString(), new AelSismamaMamoResVO());
		// nodulo calcificado
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_CAL_E.toString(), new AelSismamaMamoResVO());		
		// outras calcificacoes aspecto benigno
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_CALC_ASP_BEN_E.toString(), new AelSismamaMamoResVO());
		// implante integro
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_IMP_INTEG_E.toString(), new AelSismamaMamoResVO());		
		// nodulo com densidade heterogenea
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_NOD_DEN_HET_E.toString(), new AelSismamaMamoResVO());
		// linfonodos intramamarios
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_LINF_INTRAM_E.toString(), new AelSismamaMamoResVO());
		// implante com sinais de ruptura
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_IMP_SIN_RUP_E.toString(), new AelSismamaMamoResVO());		
		// dilatacao ductal regiao retroareolar
		mapMamaE.put(DominioSismamaMamoCadCodigo.C_DILAT_DUC_E.toString(), new AelSismamaMamoResVO());
		
		return mapMamaE;
	}
	
	public Map<String, AelSismamaMamoResVO> inicializarMapConclusao() {
		Map<String, AelSismamaMamoResVO> mapAbaConclusao = new HashMap<String, AelSismamaMamoResVO>();
		// categoria mama direita
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D.toString(), new AelSismamaMamoResVO());
		// categoria mama esquerda
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_E.toString(), new AelSismamaMamoResVO());
		// recomendacao mama direita
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_CON_RECOM_D.toString(), new AelSismamaMamoResVO());
		// recomendacao mama direita
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_CON_RECOM_E.toString(), new AelSismamaMamoResVO());
		// observacoes
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_OBS_GERAIS.toString(), new AelSismamaMamoResVO());
		
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_RESPONSAVEL.toString(), new AelSismamaMamoResVO());
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_SER_MATR_RESID.toString(), new AelSismamaMamoResVO());
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_SER_VIN_COD_RESID.toString(), new AelSismamaMamoResVO());
		mapAbaConclusao.put(DominioSismamaMamoCadCodigo.C_RESIDENTE.toString(), new AelSismamaMamoResVO());
		
		return mapAbaConclusao;
	}

	private IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return solicitacaoExameFacade;
	}	
	
}
