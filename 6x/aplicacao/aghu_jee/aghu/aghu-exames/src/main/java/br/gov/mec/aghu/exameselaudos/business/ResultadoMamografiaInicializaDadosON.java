package br.gov.mec.aghu.exameselaudos.business;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioCategoriaBiRadsMamografia;
import br.gov.mec.aghu.dominio.DominioComposicaoMama;
import br.gov.mec.aghu.dominio.DominioContornoNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioLimiteNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioLocalizacaoMamografia;
import br.gov.mec.aghu.dominio.DominioPeleMamografia;
import br.gov.mec.aghu.dominio.DominioRecomendacaoMamografia;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioTamanhoNoduloMamografia;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResDAO;
import br.gov.mec.aghu.exames.dao.VAelSerSismamaDAO;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelSerSismama;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.model.VRapPessoaServidorId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Utilizada pela estoria #5978 - Preencher achados radiologicos do resultado da mamografia (SISMAMA)
 *
 */
@Stateless
public class ResultadoMamografiaInicializaDadosON extends BaseBusiness {
	
	@EJB
	private LaudoMamografiaRN laudoMamografiaRN;
	
	@EJB
	private ResultadoMamografiaCarregaDadosON resultadoMamografiaCarregaDadosON;
	
	private static final Log LOG = LogFactory.getLog(ResultadoMamografiaInicializaDadosON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private VAelSerSismamaDAO vAelSerSismamaDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelSismamaMamoResDAO aelSismamaMamoResDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116332235578506589L;

	protected LaudoMamografiaRN getLaudoMamografiaRN() {
		return laudoMamografiaRN;
	}	
	
	protected AelSismamaMamoResDAO getAelSismamaMamoResDAO() {
		return aelSismamaMamoResDAO;
	}		
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}	
	
	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}
	
	protected VAelSerSismamaDAO getVAelSerSismamaDAO() {
		return vAelSerSismamaDAO;
	}
	
	protected ResultadoMamografiaCarregaDadosON getResultadoMamografiaCarregaDadosON() {
		return resultadoMamografiaCarregaDadosON;
	}
	
	private AelItemSolicitacaoExames obterAelItemSolicitacaoExamesPorChavePrimaria(Integer solicitacao, Short item) {
		AelItemSolicitacaoExamesId chavePrimaria = new AelItemSolicitacaoExamesId(solicitacao, item);
		return getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(chavePrimaria);		
	}	

	/***
	 * 02-p_carrega_dados.sql - As chamadas dos metodos desse arquivo estão em metodos separados nas classes: 
	 * ResultadoMamografiaCarregaDadosON e ResultadoMamografiaInicializaDadosON
	 * @param solicitacao
	 * @param item
	 * @param ise
	 * @return
	 */
	public boolean carregarDados(Integer solicitacao, Short item, AelItemSolicitacaoExames ise) {
		List<AelSismamaMamoRes> res = getAelSismamaMamoResDAO().pesquisarRespostasMamografias(ise.getId().getSoeSeq(), ise.getId().getSeqp());
		
		if (res.isEmpty()) {
			return false;
		}
		
		return true;
	}		
	
	public String obterDadosInformacaoClinica(Integer solicitacao, Short item) {
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
		
		if (carregarDados(solicitacao, item, ise)) {
			return getResultadoMamografiaCarregaDadosON().carregarInformacoesClinicas(ise);
		} else {
			return inicializarInformacoesClinicas(ise);
		}
	}	
	
	public void obterInformacoesMama(Integer solicitacao, Short item, String habilitaMamaDireita, Map<String, AelSismamaMamoResVO> mapMamaD,
			String habilitaMamaEsquerda, Map<String, AelSismamaMamoResVO> mapMamaE, Map<String, AelSismamaMamoResVO> mapConclusao) {
		
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
		
		if (carregarDados(solicitacao, item, ise)) {
			List<AelSismamaMamoRes> sisMamoRes = getLaudoMamografiaRN().obterMapaRespostasMamo(solicitacao, item);
			
			getResultadoMamografiaCarregaDadosON().carregarDadosMama(sisMamoRes, habilitaMamaDireita, mapMamaD);// p_carrega_dados_mama_dir
			getResultadoMamografiaCarregaDadosON().carregarDadosMama(sisMamoRes, habilitaMamaEsquerda, mapMamaE);// p_carrega_dados_mama_esq
			getResultadoMamografiaCarregaDadosON().carregarDadosConcDiag(sisMamoRes, mapConclusao);// p_carrega_dados_conc_diag
		} else {
			inicializarInformacoesMamaDireita(habilitaMamaDireita, mapMamaD);// p_inicializa_inf_mama_dir
			inicializarInformacoesMamaEsquerda(habilitaMamaDireita, mapMamaE);// p_inicializa_inf_mama_esq
			inicializarInformacoesConcDiag(mapConclusao); // p_inicializa_inf_conc_diag
		}
	}	
	
	/**
	 * Regra de tela - p_inicializa_inf_clinica
	 */
	private String inicializarInformacoesClinicas(AelItemSolicitacaoExames ise) {
		return ise.getSolicitacaoExame().getInformacoesClinicas();
	}	
	
	/**
	 * p_inicializa_inf_conc_diag
	 */
	public void inicializarInformacoesConcDiag(Map<String, AelSismamaMamoResVO> mapMamaD) { 
		preencherCamposValor(mapMamaD, null,
				DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D,
				DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_E,
				DominioSismamaMamoCadCodigo.C_CON_RECOM_D,
				DominioSismamaMamoCadCodigo.C_CON_RECOM_E,
				DominioSismamaMamoCadCodigo.C_OBS_GERAIS);
	}
	
	public Boolean verificarResidenteConectado() throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Short vincFunc = getParametroFacade().buscarValorShort(AghuParametrosEnum.P_AGHU_VINC_FUNC);
		Integer matriculaResidente = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_MATRICULA_RESIDENTE);

		if (servidorLogado.getId().getVinCodigo().equals(vincFunc) && servidorLogado.getId().getMatricula().intValue() > matriculaResidente.intValue()){
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * p_popula_resp_residente
	 * @throws ApplicationBusinessException 
	 */
	public VAelSerSismama obterResponsavel(Integer solicitacao, Short item, Map<String, AelSismamaMamoResVO> mapMamaD) throws ApplicationBusinessException{
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
		
		Boolean achou;
		Short vSerVinCodigo = null;
		Integer vSerMatricula = null;
		
		VAelSerSismama responsavel = null;
		
	
		// -- conclusão diagnostica
		String resposta = getLaudoMamografiaRN().obterRespostaMamo(ise.getId().getSoeSeq(), 
				ise.getId().getSeqp(), DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D);

		AelSismamaMamoResVO vo = new AelSismamaMamoResVO();
		
		if (!StringUtils.isEmpty(resposta)) {
			vo.setCategoria(DominioCategoriaBiRadsMamografia.getDominioPorCodigo(Integer.parseInt(resposta)));
		}
		
		mapMamaD.put(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D.toString(), vo);
		
		
		//procura se já houve um responsável pela liberação do resultado do exame
		List<AelExtratoItemSolicitacao> listaExtratoItemSolicitacao = getAelExtratoItemSolicitacaoDAO()
		.pesquisarExtratoItemSolicitacaoResponsabilideNotNull(
				ise.getId().getSoeSeq(), ise.getId().getSeqp());

		if (listaExtratoItemSolicitacao.isEmpty()){
			achou = Boolean.FALSE;			
		}
		else {
			achou = Boolean.TRUE;
			
			if (listaExtratoItemSolicitacao.get(0).getServidorEhResponsabilide() != null) {
				vSerMatricula = listaExtratoItemSolicitacao.get(0).getServidorEhResponsabilide().getId().getMatricula();
				vSerVinCodigo = listaExtratoItemSolicitacao.get(0).getServidorEhResponsabilide().getId().getVinCodigo();
			}
		}
		
		Boolean residenteConectado = verificarResidenteConectado();
		
		// Responsavel
	    if ( !(Boolean.FALSE.equals(achou) && Boolean.TRUE.equals(residenteConectado)) ) {

	    	if (Boolean.FALSE.equals(achou)) {
	    		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
	    		
	    		vSerMatricula = servidorLogado.getId().getMatricula();
	    		vSerVinCodigo = servidorLogado.getId().getVinCodigo();
	    	}
	    	
	    	achou = Boolean.FALSE;
	    	
		 	VAelSerSismama vAelSerSismama =	getVAelSerSismamaDAO().pesquisarResponsavelCodigoMatricula(vSerVinCodigo, vSerMatricula);
		    
		 	if (vAelSerSismama == null){
		 		achou = Boolean.FALSE;
		 	}else{
		 		achou = Boolean.TRUE;
		 	}
		 	
		 	if (Boolean.TRUE.equals(achou)){
		 		responsavel = vAelSerSismama; 
		 	}
		 }	
		return responsavel;
	}
	
	/**
	 * p_popula_resp_residente
	 * @throws ApplicationBusinessException 
	 */
	public VRapPessoaServidor obterResidente(Integer solicitacao, Short item) throws ApplicationBusinessException{
		AelItemSolicitacaoExames ise = obterAelItemSolicitacaoExamesPorChavePrimaria(solicitacao, item);
		
		Boolean residenteConectado = verificarResidenteConectado();
		
		// Residente	
		String nomeResidente = getLaudoMamografiaRN().obterRespostaMamo(ise.getId().getSoeSeq(),
				ise.getId().getSeqp(), DominioSismamaMamoCadCodigo.C_RESIDENTE);
		
		VRapPessoaServidor residente = new VRapPessoaServidor();
		VRapPessoaServidorId vRapPessoaServidorId = new VRapPessoaServidorId();
		residente.setId(vRapPessoaServidorId);
		residente.getId().setNome(nomeResidente);
		
		if (!StringUtils.isEmpty(nomeResidente)) {
			String matriculaResidente = getLaudoMamografiaRN().obterRespostaMamo(ise.getId().getSoeSeq(),
					ise.getId().getSeqp(), DominioSismamaMamoCadCodigo.C_SER_MATR_RESID);
			residente.getId().setSerMatricula(Integer.parseInt(matriculaResidente));
			
			String vinCodigoResidente = getLaudoMamografiaRN().obterRespostaMamo(ise.getId().getSoeSeq(),
					ise.getId().getSeqp(), DominioSismamaMamoCadCodigo.C_SER_VIN_COD_RESID);
			if (!StringUtils.isEmpty(vinCodigoResidente)) {
				residente.getId().setSerVinCodigo(Short.parseShort(vinCodigoResidente));
			}
		}
		
		
		if (Boolean.TRUE.equals(residenteConectado)) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if (servidorLogado.getId().getVinCodigo() == null && servidorLogado.getId().getMatricula() == null){
		      residente.getId().setSerMatricula(servidorLogado.getId().getMatricula());
		      residente.getId().setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		      VAelSerSismama vAelSerSismama = getVAelSerSismamaDAO().pesquisarResponsavelCodigoMatricula(servidorLogado.getId().getVinCodigo()
			 			, servidorLogado.getId().getMatricula());
		       
		       
		       String residenteEncontrado = null;
		      
		       if (vAelSerSismama == null){
		    	   residenteEncontrado = "N";
		       }else{
			 		residenteEncontrado = "S";
			 	}
		       
		       if (residenteEncontrado.equals("S")){
		    	   residente.getId().setNome(vAelSerSismama.getId().getNome());
		       }		       
			}
		}		
		
		if (nomeResidente == null) {
			return null;
		}
		residente.getId().setNome(nomeResidente);	
		
		return residente;
	}
	
	/**
	 * p_inicializa_inf_mama_dir 
	 */
	public void inicializarInformacoesMamaDireita(String habilitaMamaDireita, Map<String, AelSismamaMamoResVO> mapMamaD) {
		if (habilitaMamaDireita.equals("S")) {
			
			AelSismamaMamoResVO vo = new AelSismamaMamoResVO();
			
			mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString(), new AelSismamaMamoResVO());
			
			vo.setPele(DominioPeleMamografia.NORMAL);
			mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_PELE_D.toString(), new AelSismamaMamoResVO());
			
			vo = new AelSismamaMamoResVO();
			vo.setComposicao(DominioComposicaoMama.MAMA_DENSA);
			mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_COMPOSIC_D.toString(), new AelSismamaMamoResVO());
			
			preencherCamposValor(mapMamaD, "0",
					DominioSismamaMamoCadCodigo.C_NOD_LOC_01D,
					DominioSismamaMamoCadCodigo.C_NOD_LOC_02D,
					DominioSismamaMamoCadCodigo.C_NOD_LOC_03D,
					DominioSismamaMamoCadCodigo.C_MICRO_SIM_01D,
					DominioSismamaMamoCadCodigo.C_MICRO_SIM_02D,
					DominioSismamaMamoCadCodigo.C_MICRO_SIM_03D,
					DominioSismamaMamoCadCodigo.C_MICRO_LOC_01D,
					DominioSismamaMamoCadCodigo.C_MICRO_LOC_02D,
					DominioSismamaMamoCadCodigo.C_MICRO_LOC_03D,
					DominioSismamaMamoCadCodigo.C_NOD_SIM_01D,
					DominioSismamaMamoCadCodigo.C_NOD_SIM_02D,
					DominioSismamaMamoCadCodigo.C_NOD_SIM_03D
					);			

			preencherCamposValor(mapMamaD, null,
					DominioSismamaMamoCadCodigo.C_NOD_TAM_01D,
					DominioSismamaMamoCadCodigo.C_NOD_TAM_02D,
					DominioSismamaMamoCadCodigo.C_NOD_TAM_03D,
					DominioSismamaMamoCadCodigo.C_NOD_CONT_01D,
					DominioSismamaMamoCadCodigo.C_NOD_CONT_02D,
					DominioSismamaMamoCadCodigo.C_NOD_CONT_03D,
					DominioSismamaMamoCadCodigo.C_NOD_LIM_01D,
					DominioSismamaMamoCadCodigo.C_NOD_LIM_02D,
					DominioSismamaMamoCadCodigo.C_NOD_LIM_03D,
					DominioSismamaMamoCadCodigo.C_MICRO_FORM_01D,
					DominioSismamaMamoCadCodigo.C_MICRO_FORM_02D,
					DominioSismamaMamoCadCodigo.C_MICRO_FORM_03D,
					DominioSismamaMamoCadCodigo.C_MICRO_DISTR_01D,
					DominioSismamaMamoCadCodigo.C_MICRO_DISTR_02D,
					DominioSismamaMamoCadCodigo.C_MICRO_DISTR_03D);
		
			preencherCamposValor(mapMamaD, "0",
					DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM01D,
					DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM02D,
					DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM01D,
					DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM02D,
					DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM01D,
					DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM02D,
					DominioSismamaMamoCadCodigo.C_AR_DENS_SIM01D,
					DominioSismamaMamoCadCodigo.C_AR_DENS_SIM02D);

			preencherCamposValor(mapMamaD, "0",
					DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC01D,
					DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC02D,
					DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC01D,
					DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC02D,
					DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC01D,
					DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC02D,
					DominioSismamaMamoCadCodigo.C_AR_DENS_LOC01D,
					DominioSismamaMamoCadCodigo.C_AR_DENS_LOC02D);			
						
			preencherCamposValor(mapMamaD, "0",
					DominioSismamaMamoCadCodigo.C_LINF_AUX_D,
					DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_D,
					DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_D,
					DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_D,
					DominioSismamaMamoCadCodigo.C_DILAT_DUC_D,
					DominioSismamaMamoCadCodigo.C_NOD_DEN_D,
					DominioSismamaMamoCadCodigo.C_NOD_CAL_D,
					DominioSismamaMamoCadCodigo.C_NOD_DEN_HET_D,
					DominioSismamaMamoCadCodigo.C_CALC_VASC_D,
					DominioSismamaMamoCadCodigo.C_CALC_ASP_BEN_D,
					DominioSismamaMamoCadCodigo.C_NOD_DEN_D,
					DominioSismamaMamoCadCodigo.C_LINF_INTRAM_D,
					DominioSismamaMamoCadCodigo.C_DIS_ARQ_CIR_D,
					DominioSismamaMamoCadCodigo.C_IMP_INTEG_D,
					DominioSismamaMamoCadCodigo.C_IMP_SIN_RUP_D);
		}		
	}
	
	/**
	 * p_inicializa_inf_mama_esq 
	 */
	public void inicializarInformacoesMamaEsquerda(String habilitaMamaEsquerda, Map<String, AelSismamaMamoResVO> mapMamaD) {
		if (habilitaMamaEsquerda.equals("S")) {
			
			AelSismamaMamoResVO vo = new AelSismamaMamoResVO();
			
			mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString(), new AelSismamaMamoResVO());
			
			vo.setPele(DominioPeleMamografia.NORMAL);
			mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_PELE_E.toString(), new AelSismamaMamoResVO());
			
			vo = new AelSismamaMamoResVO();
			vo.setComposicao(DominioComposicaoMama.MAMA_DENSA);
			mapMamaD.put(DominioSismamaMamoCadCodigo.C_RAD_COMPOSIC_E.toString(), new AelSismamaMamoResVO());
			
			preencherCamposValor(mapMamaD, "0",
					DominioSismamaMamoCadCodigo.C_NOD_LOC_01E,
					DominioSismamaMamoCadCodigo.C_NOD_LOC_02E,
					DominioSismamaMamoCadCodigo.C_NOD_LOC_03E,
					DominioSismamaMamoCadCodigo.C_MICRO_SIM_01E,
					DominioSismamaMamoCadCodigo.C_MICRO_SIM_02E,
					DominioSismamaMamoCadCodigo.C_MICRO_SIM_03E,
					DominioSismamaMamoCadCodigo.C_MICRO_LOC_01E,
					DominioSismamaMamoCadCodigo.C_MICRO_LOC_02E,
					DominioSismamaMamoCadCodigo.C_MICRO_LOC_03E,
					DominioSismamaMamoCadCodigo.C_NOD_SIM_01E,
					DominioSismamaMamoCadCodigo.C_NOD_SIM_02E,
					DominioSismamaMamoCadCodigo.C_NOD_SIM_03E
					);			

			preencherCamposValor(mapMamaD, null,
					DominioSismamaMamoCadCodigo.C_NOD_TAM_01E,
					DominioSismamaMamoCadCodigo.C_NOD_TAM_02E,
					DominioSismamaMamoCadCodigo.C_NOD_TAM_03E,
					DominioSismamaMamoCadCodigo.C_NOD_CONT_01E,
					DominioSismamaMamoCadCodigo.C_NOD_CONT_02E,
					DominioSismamaMamoCadCodigo.C_NOD_CONT_03E,
					DominioSismamaMamoCadCodigo.C_NOD_LIM_01E,
					DominioSismamaMamoCadCodigo.C_NOD_LIM_02E,
					DominioSismamaMamoCadCodigo.C_NOD_LIM_03E,
					DominioSismamaMamoCadCodigo.C_MICRO_FORM_01E,
					DominioSismamaMamoCadCodigo.C_MICRO_FORM_02E,
					DominioSismamaMamoCadCodigo.C_MICRO_FORM_03E,
					DominioSismamaMamoCadCodigo.C_MICRO_DISTR_01E,
					DominioSismamaMamoCadCodigo.C_MICRO_DISTR_02E,
					DominioSismamaMamoCadCodigo.C_MICRO_DISTR_03E);
		
			preencherCamposValor(mapMamaD, "0",
					DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM01E,
					DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM02E,
					DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM01E,
					DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM02E,
					DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM01E,
					DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM02E,
					DominioSismamaMamoCadCodigo.C_AR_DENS_SIM01E,
					DominioSismamaMamoCadCodigo.C_AR_DENS_SIM02E);

			preencherCamposValor(mapMamaD, "0",
					DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC01E,
					DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC02E,
					DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC01E,
					DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC02E,
					DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC01E,
					DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC02E,
					DominioSismamaMamoCadCodigo.C_AR_DENS_LOC01E,
					DominioSismamaMamoCadCodigo.C_AR_DENS_LOC02E);			
						
			preencherCamposValor(mapMamaD, "0",
					DominioSismamaMamoCadCodigo.C_LINF_AUX_E,
					DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_E,
					DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_E,
					DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_E,
					DominioSismamaMamoCadCodigo.C_DILAT_DUC_E,
					DominioSismamaMamoCadCodigo.C_NOD_DEN_E,
					DominioSismamaMamoCadCodigo.C_NOD_CAL_E,
					DominioSismamaMamoCadCodigo.C_NOD_DEN_HET_E,
					DominioSismamaMamoCadCodigo.C_CALC_VASC_E,
					DominioSismamaMamoCadCodigo.C_CALC_ASP_BEN_E,
					DominioSismamaMamoCadCodigo.C_NOD_DEN_E,
					DominioSismamaMamoCadCodigo.C_LINF_INTRAM_E,
					DominioSismamaMamoCadCodigo.C_DIS_ARQ_CIR_E,
					DominioSismamaMamoCadCodigo.C_IMP_INTEG_E,
					DominioSismamaMamoCadCodigo.C_IMP_SIN_RUP_E);
		}		
	}
	
	private void preencherCamposValor(Map<String, AelSismamaMamoResVO> mapMamaD, String valor, DominioSismamaMamoCadCodigo... dominios ){
		for (DominioSismamaMamoCadCodigo dominioSismamaMamoCadCodigo : dominios) {
			if	(valor == null){
				mapMamaD.put(dominioSismamaMamoCadCodigo.toString(), new AelSismamaMamoResVO());
			}else{			
				AelSismamaMamoResVO vo = new AelSismamaMamoResVO();				
				
				if (dominioSismamaMamoCadCodigo.toString().contains("LOC")){
					vo.setLocalizacao(DominioLocalizacaoMamografia.getDominioPorCodigo(valor));
				}
				else if (dominioSismamaMamoCadCodigo.toString().contains("TAM")){
					vo.setTamanho(DominioTamanhoNoduloMamografia.getDominioPorCodigo(Integer.parseInt(valor)));
				}
				else if (dominioSismamaMamoCadCodigo.toString().contains("CONT")){
					vo.setContorno(DominioContornoNoduloMamografia.getDominioPorCodigo(Integer.parseInt(valor)));
				}
				else if (dominioSismamaMamoCadCodigo.toString().contains("LIM")){
					vo.setLimite(DominioLimiteNoduloMamografia.getDominioPorCodigo(Integer.parseInt(valor)));
				}else if (dominioSismamaMamoCadCodigo.toString().contains("CAT")){
					vo.setCategoria(DominioCategoriaBiRadsMamografia.getDominioPorCodigo(Integer.parseInt(valor)));
				}else if (dominioSismamaMamoCadCodigo.toString().contains("RECOM")){
					vo.setRecomendacao(DominioRecomendacaoMamografia.getDominioPorCodigo(Integer.parseInt(valor)));
				}else if (dominioSismamaMamoCadCodigo.toString().contains("GERAIS")){
					vo.setObservacoes(valor);
				}
				else if (Integer.parseInt(valor) == 0){
					vo.setChecked(false);
				}else if (Integer.parseInt(valor) == 3){
					vo.setChecked(true);
				}				
				mapMamaD.put(dominioSismamaMamoCadCodigo.toString(), vo);
			}
		}		
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
