package br.gov.mec.aghu.exameselaudos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.estoque.vo.VoltarProtocoloUnicoVO;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelRefCodeDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoCadDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResDAO;
import br.gov.mec.aghu.exames.dao.AelVersaoLaudoDAO;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelRefCode;
import br.gov.mec.aghu.model.AelRefCodeId;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.model.AelSismamaMamoCad;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @ORADB - AELK_LAUDO_MAMO
 * @since - 11/12/2012
 * @author daniel.silva
 * 
 * RN criada para agrupar todas as functions e procedures contidas no package AELK_LAUDO_MAMO.
 *
 */
@Stateless
public class LaudoMamografiaRN extends BaseBusiness {


private static final String AREA_DENSA_LOCALIZACAO = "Área Densa - Localização: ";

private static final String DISTORCAO_FOCAL_LOCALIZACAO = "Distorção Focal - Localização: ";

private static final String ASSIMETRIA_DIFUSA_LOCALIZACAO = "Assimetria Difusa - Localização: ";

private static final String ASSIMETRIA_FOCAL_LOCALIZACAO = "Assimetria Focal - Localização: ";

@EJB
private LaudoMamografiaObtencaoDadosRN laudoMamografiaObtencaoDadosRN;

private static final Log LOG = LogFactory.getLog(LaudoMamografiaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelVersaoLaudoDAO aelVersaoLaudoDAO;

@Inject
private AelResultadoExameDAO aelResultadoExameDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IExamesFacade examesFacade;

@Inject
private AelRefCodeDAO aelRefCodeDAO;

@Inject
private AelSismamaMamoCadDAO aelSismamaMamoCadDAO;

@Inject
private AelSismamaMamoResDAO aelSismamaMamoResDAO;

@Inject
private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116321235468806675L;
	
	private static final String STRING_BR = "<br/>"; //com \n nao quebra linha

	public enum LaudoMamografiaRNExceptionCode implements BusinessExceptionCode {
		AEL_03232, AEL_03233, AEL_03234, AEL_03235;
	}
	
	/**
	 * @ORADB - AELC_RESPOSTA_MAMO
	 * @author daniel.silva
	 * @since - 11/12/2012
	 * 
	 * Busca a resposta de um campo e exame da mamografia - sismama.
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param campo
	 * @return
	 */
	public String obterRespostaMamo(Integer soeSeq, Short seqp, DominioSismamaMamoCadCodigo campo) {
		AelSismamaMamoRes res = getAelSismamaMamoResDAO().obterRespostaMamografia(soeSeq, seqp, campo);
		if (res != null) {
			return res.getResposta();
		} else {
			return null;
		}
	}

	/**
	 * Obtem todas as respostas de um item solicitação 
	 * @author bruno.mourao
	 * @since 04/01/2013
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public List<AelSismamaMamoRes> obterMapaRespostasMamo(Integer soeSeq, Short seqp) {
		return getAelSismamaMamoResDAO().obterRespostasMamografia(soeSeq, seqp);
	}

	/**
	 * Obtem todas as respostas de um item solicitação que estao com o valor do
	 * campo Resposta null
	 * 
	 * @author phillip.santos
	 * @since 06/01/2013
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public List<AelSismamaMamoRes> obterRespostasMamografiaRespNull(Integer soeSeq, Short seqp) {
		return getAelSismamaMamoResDAO().obterRespostasMamografiaRespNull(soeSeq, seqp);
	}

	/**
	 * @ORADB - AELC_DESC_RESP_MAMO
	 * @author daniel.silva
	 * @since - 13/12/2012
	 * 
	 * Busca a resposta de um campo e exame da mamografia - sismama.
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param campo
	 * @param tipo
	 * @return
	 */
	public String obterDescricaoRespostaMamo(Integer iseSoeSeq, Short iseSeqp, DominioSismamaMamoCadCodigo campo, String tipo) {
		//retorna a resposta de um campo
		String resposta = obterRespostaMamo(iseSoeSeq, iseSeqp, campo);
		//retorna o dominio de um campo
		AelSismamaMamoCad dominio = getAelSismamaMamoCadDAO().obterPorChavePrimaria(campo.toString());
		AelRefCode ref = getAelRefCodeDAO().obterPorChavePrimaria(new AelRefCodeId(resposta, dominio.getSdmNome()));
		
		if (ref == null) {
			return null;
		}

		return "A".equals(tipo) ? ref.getRvAbbreviation() : ref.getRvMeaning();
	}
	
	/**
	 * @ORADB - AELP_GERA_LAUDO_MAMO
	 * @author daniel.silva
	 * @since - 13/12/2012
	 * 
	 * Gera laudo de exame para mamografia de acordo com informações SISMAMA
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @throws BaseException 
	 */
	public void gerarLaudoExameMamografiaSismama(Integer iseSoeSeq, Short iseSeqp, String nomeMicrocomputador) throws BaseException {
		//cursores já implementados:
		//AelResultadoExameDAO.obterMaxSeqpResultadoExame c_seqp
		
		//busca o parametro do campo laudo
		Integer campoLaudo = obterParametroCalSeqLaudoSismamaMamo();
		
		//verifica o número de versões ativas do laudo. Deve existir 1 e apenas 1
		validarQuantidadeVersoesLaudo(iseSoeSeq, iseSeqp);

		//busca a versao do laudo
		AelVersaoLaudo versao = getAelVersaoLaudoDAO().obterVersaoAtivaLaudo(iseSoeSeq, iseSeqp);

		//busca o campo laudo
		List<AelParametroCamposLaudo> campos = pesquisarCamposLaudo(versao, campoLaudo);
		
		//verifica se existe o campo p este laudo
		if (campos.isEmpty()) {
			throw new ApplicationBusinessException(LaudoMamografiaRNExceptionCode.AEL_03235);
		}
		AelParametroCamposLaudo pcl = campos.get(0);

		//verifica se existe um resultado ativo
		List<AelResultadoExame> resultados = getAelResultadoExameDAO().listarResultadoVersaoLaudo(iseSoeSeq, iseSeqp);

		if (!resultados.isEmpty()) {
			AelResultadoExame res = resultados.get(0);
			res.setAnulacaoLaudo(Boolean.TRUE);
			getExamesFacade().atualizarAelResultadoExame(res, nomeMicrocomputador); 
		}
		
		//busca o próximo seqp
		Integer seqp = obterProximoSeqp(iseSoeSeq, iseSeqp, campoLaudo, versao);

		//inclui resultado --> aqui está dando flush, por isso o rollback abaixo
		AelResultadoExame resultado = gravarResultadoExame(iseSoeSeq, iseSeqp, versao, pcl, seqp);
		
		AelParametroCamposLaudo parametroCampoLaudo = getAelParametroCamposLaudoDAO().obterPorChavePrimaria(new AelParametroCampoLaudoId(resultado.getId().getPclVelEmaExaSigla(), resultado.getId().getPclVelEmaManSeq(), resultado.getId().getPclVelSeqp(), resultado.getId().getPclCalSeq(), resultado.getId().getPclSeqp()));
		resultado.setParametroCampoLaudo(parametroCampoLaudo);
		
		//monta o texto do laudo
		String laudo = obterLaudoMontado(iseSoeSeq, iseSeqp);
		resultado.setDescricao(laudo);
		
		getExamesFacade().atualizarAelResultadoExame(resultado,nomeMicrocomputador);
		
	}

//	private void gravarDescricaoResultado(AelResultadoExame resultado, String laudo) throws BaseException {
//		AelDescricoesResultado descricao = new AelDescricoesResultado();
//		
//		AelResultadoExameId id = new AelResultadoExameId();
//		id.setIseSoeSeq(resultado.getId().getIseSoeSeq());
//		id.setIseSeqp(resultado.getId().getIseSeqp());
//		id.setPclCalSeq(resultado.getId().getPclCalSeq());
//		id.setPclSeqp(resultado.getId().getPclSeqp());
//		id.setPclVelEmaExaSigla(resultado.getId().getPclVelEmaExaSigla());
//		id.setPclVelEmaManSeq(resultado.getId().getPclVelEmaManSeq());
//		id.setPclVelSeqp(resultado.getId().getPclVelSeqp());
//		id.setSeqp(resultado.getId().getSeqp());
//
//		descricao.setResultadoExame(resultado);
//		descricao.setDescricao(laudo);
//		descricao.setId(id);
//		
//		getExamesFacade().persistirAelDescricaoResultado(descricao);
//	}

	private Integer obterProximoSeqp(Integer iseSoeSeq, Short iseSeqp, Integer campoLaudo, AelVersaoLaudo versao) {
		VoltarProtocoloUnicoVO vo = new VoltarProtocoloUnicoVO();
		vo.setSiglaExame(versao.getId().getEmaExaSigla());
		vo.setMaterialAnalise(versao.getId().getEmaManSeq());
		
		return getAelResultadoExameDAO().obterMaxSeqpResultadoExame(iseSoeSeq, iseSeqp, vo, null, campoLaudo);
	}

	private AelResultadoExame gravarResultadoExame(Integer iseSoeSeq, Short iseSeqp, AelVersaoLaudo versao, AelParametroCamposLaudo pcl, Integer seqp) throws BaseException {
		AelResultadoExameId id = new AelResultadoExameId(
				iseSoeSeq, 
				iseSeqp, 
				versao.getId().getEmaExaSigla(), 
				versao.getId().getEmaManSeq(), 
				versao.getId().getSeqp(), 
				pcl.getId().getCalSeq(), 
				pcl.getId().getSeqp(), 
				seqp);
		
		AelResultadoExame resultado = new AelResultadoExame(id, null, Boolean.FALSE, null, null, null, null, null, null);
		getExamesFacade().inserirAelResultadoExame(resultado);
		
		AelResultadoExame retorno = getAelResultadoExameDAO().obterPorChavePrimaria(new AelResultadoExameId(iseSoeSeq, iseSeqp, 
																					versao.getId().getEmaExaSigla(), 
																					versao.getId().getEmaManSeq(), 
																					versao.getId().getSeqp(), 
																					pcl.getId().getCalSeq(), 
																					pcl.getId().getSeqp(), 
																					seqp));
		
		return retorno;
	}

	private List<AelParametroCamposLaudo> pesquisarCamposLaudo(AelVersaoLaudo versao, Integer campoLaudo) {
		return getAelParametroCamposLaudoDAO().obterCampoLaudo(
				new AelParametroCampoLaudoId(
						versao.getExameMaterialAnalise().getId().getExaSigla(),
						versao.getExameMaterialAnalise().getId().getManSeq(), 
						versao.getId().getSeqp(),
						campoLaudo,
						null));
	}

	private Integer obterParametroCalSeqLaudoSismamaMamo() throws ApplicationBusinessException {
		AghParametros param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CAL_SEQ_LAUDO_SISMAMA_MAMO);
		if (param.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(LaudoMamografiaRNExceptionCode.AEL_03232);
		}
		return param.getVlrNumerico().intValue();
	}

	private void validarQuantidadeVersoesLaudo(Integer iseSoeSeq, Short iseSeqp) throws ApplicationBusinessException {
		Long tot = getAelVersaoLaudoDAO().pesquisarQuantidadeVersoesAtivasLaudo(iseSoeSeq, iseSeqp);
		//se não encontrou = ERRO
		if (tot == null || tot == 0) {
			throw new ApplicationBusinessException(LaudoMamografiaRNExceptionCode.AEL_03233);//	BEGIN
		} else if (tot > 1) {
			throw new ApplicationBusinessException(LaudoMamografiaRNExceptionCode.AEL_03234);
		}
	}
	
	/**
	 * @ORABD - AELC_MONT_LAUDO_MAMO
	 * 
	 * Monta laudo de exame a partir das informações do sismama.
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public String obterLaudoMontado(Integer iseSoeSeq, Short iseSeqp) {
		StringBuilder laudo = new StringBuilder();
		
		Boolean imprimeTitulo = Boolean.FALSE;
		Boolean linfonodo = Boolean.FALSE;
		Boolean nodulo= Boolean.FALSE;
		
		LaudoMamografiaObtencaoDadosRN obtencadoDadosRN = laudoMamografiaObtencaoDadosRN;

		laudo.append(obtencadoDadosRN.obterDadosInformacoesClinicas(iseSoeSeq, iseSeqp))
		
		//******************MAMA DIREITA******************************
		.append(obtencadoDadosRN.obterDadosMama(iseSoeSeq, iseSeqp, DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D, DominioSismamaMamoCadCodigo.C_RAD_PELE_D, DominioSismamaMamoCadCodigo.C_RAD_COMPOSIC_D, "DIREITA"))

		//NÓDULOS
		//01
		.append(obtencadoDadosRN.obterDadosNodulo(DominioSismamaMamoCadCodigo.C_NOD_SIM_01D, DominioSismamaMamoCadCodigo.C_NOD_LOC_01D, DominioSismamaMamoCadCodigo.C_NOD_TAM_01D, DominioSismamaMamoCadCodigo.C_NOD_CONT_01D, DominioSismamaMamoCadCodigo.C_NOD_LIM_01D, iseSoeSeq, iseSeqp))
		//02
		.append(obtencadoDadosRN.obterDadosNodulo(DominioSismamaMamoCadCodigo.C_NOD_SIM_02D, DominioSismamaMamoCadCodigo.C_NOD_LOC_02D, DominioSismamaMamoCadCodigo.C_NOD_TAM_02D, DominioSismamaMamoCadCodigo.C_NOD_CONT_02D, DominioSismamaMamoCadCodigo.C_NOD_LIM_02D, iseSoeSeq, iseSeqp))
		//03
		.append(obtencadoDadosRN.obterDadosNodulo(DominioSismamaMamoCadCodigo.C_NOD_SIM_03D, DominioSismamaMamoCadCodigo.C_NOD_LOC_03D, DominioSismamaMamoCadCodigo.C_NOD_TAM_03D, DominioSismamaMamoCadCodigo.C_NOD_CONT_03D, DominioSismamaMamoCadCodigo.C_NOD_LIM_03D, iseSoeSeq, iseSeqp))
		
		//MICROCALCIFICACAO
		//01
		.append(obtencadoDadosRN.obterDadosMicroCalficicacao(DominioSismamaMamoCadCodigo.C_MICRO_LOC_01D, DominioSismamaMamoCadCodigo.C_MICRO_FORM_01D, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_01D, iseSoeSeq, iseSeqp))
		//02
		.append(obtencadoDadosRN.obterDadosMicroCalficicacao(DominioSismamaMamoCadCodigo.C_MICRO_LOC_02D, DominioSismamaMamoCadCodigo.C_MICRO_FORM_02D, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_02D, iseSoeSeq, iseSeqp))
		//03
		.append(obtencadoDadosRN.obterDadosMicroCalficicacao(DominioSismamaMamoCadCodigo.C_MICRO_LOC_03D, DominioSismamaMamoCadCodigo.C_MICRO_FORM_03D, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_03D, iseSoeSeq, iseSeqp))
		
		//Assimetria Focal
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(ASSIMETRIA_FOCAL_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC01D, iseSoeSeq, iseSeqp))
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(ASSIMETRIA_FOCAL_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC02D, iseSoeSeq, iseSeqp))
		
		//Assimetria Difusa
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(ASSIMETRIA_DIFUSA_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC01D, iseSoeSeq, iseSeqp))
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(ASSIMETRIA_DIFUSA_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC02D, iseSoeSeq, iseSeqp))
		
		//Distorção Focal
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(DISTORCAO_FOCAL_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC01D, iseSoeSeq, iseSeqp))
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(DISTORCAO_FOCAL_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC02D, iseSoeSeq, iseSeqp))
		
		//AREA DENSA
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(AREA_DENSA_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_AR_DENS_LOC01D, iseSoeSeq, iseSeqp))
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(AREA_DENSA_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_AR_DENS_LOC02D, iseSoeSeq, iseSeqp));
		
		//LINFONODOS
		linfonodo = obtencadoDadosRN.obterDadosLinfonodos(laudo, null, linfonodo, DominioSismamaMamoCadCodigo.C_LINF_AUX_D, iseSoeSeq, iseSeqp);
		linfonodo = obtencadoDadosRN.obterDadosLinfonodos(laudo, " - Aumentados", linfonodo, DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_D, iseSoeSeq, iseSeqp);
		linfonodo = obtencadoDadosRN.obterDadosLinfonodos(laudo, " - Densos", linfonodo, DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_D, iseSoeSeq, iseSeqp);
		linfonodo = obtencadoDadosRN.obterDadosLinfonodos(laudo, " - Confluentes", linfonodo, DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_D, iseSoeSeq, iseSeqp);

		if (linfonodo) {
			laudo.append(STRING_BR);
		}
		
		//ACHADOS mama DIREITA 
		StringBuilder achadosMamaDireita = new StringBuilder();
		nodulo = obtencadoDadosRN.obterDadosAchadosMama(achadosMamaDireita, "Com Densidade de Gordura - ", DominioSismamaMamoCadCodigo.C_NOD_DEN_D, nodulo, iseSoeSeq, iseSeqp, Boolean.FALSE);
		nodulo = obtencadoDadosRN.obterDadosAchadosMama(achadosMamaDireita, "Calcificado - ", DominioSismamaMamoCadCodigo.C_NOD_CAL_D, nodulo, iseSoeSeq, iseSeqp, Boolean.FALSE);
		nodulo = obtencadoDadosRN.obterDadosAchadosMama(achadosMamaDireita, "Com Densidade Heterogênea - ", DominioSismamaMamoCadCodigo.C_NOD_DEN_HET_D, nodulo, iseSoeSeq, iseSeqp, Boolean.FALSE);
		//se tem uma informação de nódulo troca a linha
		if (nodulo) { 
			laudo.append(StringUtils.substring(achadosMamaDireita.toString(), 0, achadosMamaDireita.toString().length() - 3)).append(STRING_BR);
		}
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com calcificações vasculares", DominioSismamaMamoCadCodigo.C_CALC_VASC_D, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com outras calcificações aspecto benigno", DominioSismamaMamoCadCodigo.C_CALC_ASP_BEN_D, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Linfonodos intramamários", DominioSismamaMamoCadCodigo.C_LINF_INTRAM_D, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Distorção arquitetural por cirurgia", DominioSismamaMamoCadCodigo.C_DIS_ARQ_CIR_D, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Implante íntegro", DominioSismamaMamoCadCodigo.C_IMP_INTEG_D, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Implante com sinais de ruptura", DominioSismamaMamoCadCodigo.C_IMP_SIN_RUP_D, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Dilatação ductal região retroareolar", DominioSismamaMamoCadCodigo.C_DILAT_DUC_D, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		
		laudo.append(obterTitulo(iseSoeSeq, iseSeqp, imprimeTitulo, "D"))
		//***********************FIM MAMA DIREITA *************************
		
		//******************MAMA ESQUERDA******************************
		.append(obtencadoDadosRN.obterDadosMama(iseSoeSeq, iseSeqp, DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E, DominioSismamaMamoCadCodigo.C_RAD_PELE_E, DominioSismamaMamoCadCodigo.C_RAD_COMPOSIC_E, "ESQUERDA"))

		//NÓDULOS
		//01
		.append(obtencadoDadosRN.obterDadosNodulo(DominioSismamaMamoCadCodigo.C_NOD_SIM_01E, DominioSismamaMamoCadCodigo.C_NOD_LOC_01E, DominioSismamaMamoCadCodigo.C_NOD_TAM_01E, DominioSismamaMamoCadCodigo.C_NOD_CONT_01E, DominioSismamaMamoCadCodigo.C_NOD_LIM_01E, iseSoeSeq, iseSeqp))
		//02
		.append(obtencadoDadosRN.obterDadosNodulo(DominioSismamaMamoCadCodigo.C_NOD_SIM_02E, DominioSismamaMamoCadCodigo.C_NOD_LOC_02E, DominioSismamaMamoCadCodigo.C_NOD_TAM_02E, DominioSismamaMamoCadCodigo.C_NOD_CONT_02E, DominioSismamaMamoCadCodigo.C_NOD_LIM_02E, iseSoeSeq, iseSeqp))
		//03
		.append(obtencadoDadosRN.obterDadosNodulo(DominioSismamaMamoCadCodigo.C_NOD_SIM_03E, DominioSismamaMamoCadCodigo.C_NOD_LOC_03E, DominioSismamaMamoCadCodigo.C_NOD_TAM_03E, DominioSismamaMamoCadCodigo.C_NOD_CONT_03E, DominioSismamaMamoCadCodigo.C_NOD_LIM_03E, iseSoeSeq, iseSeqp))
		
		//MICROCALCIFICA??O
		//01
		.append(obtencadoDadosRN.obterDadosMicroCalficicacao(DominioSismamaMamoCadCodigo.C_MICRO_LOC_01E, DominioSismamaMamoCadCodigo.C_MICRO_FORM_01E, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_01E, iseSoeSeq, iseSeqp))
		//02
		.append(obtencadoDadosRN.obterDadosMicroCalficicacao(DominioSismamaMamoCadCodigo.C_MICRO_LOC_02E, DominioSismamaMamoCadCodigo.C_MICRO_FORM_02E, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_02E, iseSoeSeq, iseSeqp))
		//03
		.append(obtencadoDadosRN.obterDadosMicroCalficicacao(DominioSismamaMamoCadCodigo.C_MICRO_LOC_03E, DominioSismamaMamoCadCodigo.C_MICRO_FORM_03E, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_03E, iseSoeSeq, iseSeqp))
		
		//Assimetria Focal
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(ASSIMETRIA_FOCAL_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC01E, iseSoeSeq, iseSeqp))
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(ASSIMETRIA_FOCAL_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC02E, iseSoeSeq, iseSeqp))
		
		//Assimetria Difusa
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(ASSIMETRIA_DIFUSA_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC01E, iseSoeSeq, iseSeqp))
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(ASSIMETRIA_DIFUSA_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC02E, iseSoeSeq, iseSeqp))
		
		//Distorção Focal
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(DISTORCAO_FOCAL_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC01E, iseSoeSeq, iseSeqp))
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(DISTORCAO_FOCAL_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC02E, iseSoeSeq, iseSeqp))
		
		//AREA DENSA
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(AREA_DENSA_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_AR_DENS_LOC01E, iseSoeSeq, iseSeqp))
		.append(obtencadoDadosRN.obterDadosAssimetriaDistorcaoAreaDensa(AREA_DENSA_LOCALIZACAO, DominioSismamaMamoCadCodigo.C_AR_DENS_LOC02E, iseSoeSeq, iseSeqp));
		
		//LINFONODOS
		linfonodo = Boolean.FALSE;
		linfonodo = obtencadoDadosRN.obterDadosLinfonodos(laudo, null, linfonodo, DominioSismamaMamoCadCodigo.C_LINF_AUX_E, iseSoeSeq, iseSeqp);
		linfonodo = obtencadoDadosRN.obterDadosLinfonodos(laudo, " - Aumentados", linfonodo, DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_E, iseSoeSeq, iseSeqp);
		linfonodo = obtencadoDadosRN.obterDadosLinfonodos(laudo, " - Densos", linfonodo, DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_E, iseSoeSeq, iseSeqp);
		linfonodo = obtencadoDadosRN.obterDadosLinfonodos(laudo, " - Confluentes", linfonodo, DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_E, iseSoeSeq, iseSeqp);
		
		if (linfonodo) {
			laudo.append(STRING_BR);
		}
		
		//ACHADOS mama ESQUERDA
		StringBuilder achadosMamaEsquerda = new StringBuilder();
		nodulo = Boolean.FALSE;
		nodulo = obtencadoDadosRN.obterDadosAchadosMama(achadosMamaEsquerda, "Com Densidade de Gordura - ", DominioSismamaMamoCadCodigo.C_NOD_DEN_E, nodulo, iseSoeSeq, iseSeqp, Boolean.FALSE);
		nodulo = obtencadoDadosRN.obterDadosAchadosMama(achadosMamaEsquerda, "Calcificado - ", DominioSismamaMamoCadCodigo.C_NOD_CAL_E, nodulo, iseSoeSeq, iseSeqp, Boolean.FALSE);
		nodulo = obtencadoDadosRN.obterDadosAchadosMama(achadosMamaEsquerda, "Com Densidade Heterogênea - ", DominioSismamaMamoCadCodigo.C_NOD_DEN_HET_E, nodulo, iseSoeSeq, iseSeqp, Boolean.FALSE);
		//se tem uma informação de nódulo troca a linha
		if (nodulo) { 
			laudo.append(StringUtils.substring(achadosMamaEsquerda.toString(), 0, achadosMamaEsquerda.toString().length() - 3)).append(STRING_BR);
		}
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com calcificações vasculares", DominioSismamaMamoCadCodigo.C_CALC_VASC_E, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com outras calcificações aspecto benigno", DominioSismamaMamoCadCodigo.C_CALC_ASP_BEN_E, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Linfonodos intramamários", DominioSismamaMamoCadCodigo.C_LINF_INTRAM_E, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Distorção arquitetural por cirurgia", DominioSismamaMamoCadCodigo.C_DIS_ARQ_CIR_E, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Implante Íntegro", DominioSismamaMamoCadCodigo.C_IMP_INTEG_E, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Implante com sinais de ruptura", DominioSismamaMamoCadCodigo.C_IMP_SIN_RUP_E, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		obtencadoDadosRN.obterDadosAchadosMama(laudo, "Com Dilatação ductal região retroareolar", DominioSismamaMamoCadCodigo.C_DILAT_DUC_E, Boolean.TRUE, iseSoeSeq, iseSeqp, Boolean.TRUE);
		
		laudo.append(obterTitulo(iseSoeSeq, iseSeqp, imprimeTitulo, "E"));
		//***************FIM MAMA ESQUERDA *************************
		
		if (imprimeTitulo) {
			laudo.append(STRING_BR); //linha em branco
		}
		
		//OBSERVACAO
		laudo.append(obtencadoDadosRN.obterDadosObservacao(iseSoeSeq, iseSeqp))
		
		//RESIDENTE
		.append(obtencadoDadosRN.obterDadosResidente(iseSoeSeq, iseSeqp));
		
		return laudo.toString();

	}

	
	
	
	
	private String obterTitulo(Integer iseSoeSeq, Short iseSeqp, Boolean imprimiTitulo, String mama) {
		String retorno = "";
		String respostaDireita = obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D, "C");
		String respostaEsquerda = obterDescricaoRespostaMamo(iseSoeSeq, iseSeqp, DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_E, "C");
		if (StringUtils.isNotBlank(respostaDireita) && StringUtils.isNotBlank(respostaEsquerda)) {
			imprimiTitulo = Boolean.TRUE;
		}
		if (StringUtils.equals(mama, "D")) {
			if (StringUtils.isNotBlank(respostaDireita)) {
				retorno = "BI-RADS - ".concat(respostaDireita).concat(STRING_BR);
			}			
		} else if (StringUtils.equals(mama, "E")) {
			if (StringUtils.isNotBlank(respostaEsquerda)) {
				retorno = "BI-RADS - ".concat(respostaEsquerda).concat(STRING_BR);
			}
		}
		return retorno;
	}
	
	
	protected AelSismamaMamoResDAO getAelSismamaMamoResDAO() {
		return aelSismamaMamoResDAO;
	}
	
	protected AelSismamaMamoCadDAO getAelSismamaMamoCadDAO() {
		return aelSismamaMamoCadDAO;
	}
	
	protected AelRefCodeDAO getAelRefCodeDAO() {
		return aelRefCodeDAO;
	}
	
	protected AelVersaoLaudoDAO getAelVersaoLaudoDAO() {
		return aelVersaoLaudoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}
	
	protected AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}

}