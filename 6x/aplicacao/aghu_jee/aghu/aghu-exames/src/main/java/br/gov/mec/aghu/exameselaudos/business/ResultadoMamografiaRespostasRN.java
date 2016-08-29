package br.gov.mec.aghu.exameselaudos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoCadDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResJnDAO;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaMamoCad;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.model.AelSismamaMamoResJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ResultadoMamografiaRespostasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ResultadoMamografiaRespostasRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelSismamaMamoCadDAO aelSismamaMamoCadDAO;
	
	@Inject
	private AelSismamaMamoResDAO aelSismamaMamoResDAO;
	
	@Inject
	private AelSismamaMamoResJnDAO aelSismamaMamoResJnDAO;
	
	private static final long serialVersionUID = 6116320456468807589L;
	
	private AelSismamaMamoResDAO getAelSismamaMamoResDAO() {
		return aelSismamaMamoResDAO;
	}

	private AelSismamaMamoCadDAO getAelSismamaMamoCadDAO() {
		return aelSismamaMamoCadDAO;
	}
	
	private boolean isInserirRespostaMamografia(String s03Codigo, Integer cIseSoeSeq, Short cIseSeqp) {
		
		boolean inserir = true;
		
		List<AelSismamaMamoRes> aelSismamaMamoRes = getAelSismamaMamoResDAO().
													pesquisarRespostaMamografiaPorItemSolicitacao(s03Codigo, cIseSoeSeq, cIseSeqp);
		
		if(aelSismamaMamoRes.size() > 0) {
			inserir = false;
		}
		
		return inserir;
		
	}
	
	
	/**
	 * @ORADB p_insere_banco
	 * @param aelSismamaMamoRes
	 * @throws ApplicationBusinessException 
	 */
	private void persistirSismamaMamoRes(AelSismamaMamoRes aelSismamaMamoRes) throws ApplicationBusinessException{
		
		if (isInserirRespostaMamografia(aelSismamaMamoRes.getAelSismamaMamoCad().getCodigo(), 
									    aelSismamaMamoRes.getItemSolicitacaoExames().getId().getSoeSeq(),
									    aelSismamaMamoRes.getItemSolicitacaoExames().getId().getSeqp())) {

			executarAntesInserirSismamaMamoRes(aelSismamaMamoRes);
			getAelSismamaMamoResDAO().persistir(aelSismamaMamoRes);

		} 
		else {
			getAelSismamaMamoResDAO().atualizar(aelSismamaMamoRes);
			executarAposAtualizarSismamaMamoRes(aelSismamaMamoRes);
		}
			
	}
	
	/**
	 * @ORADB AELT_S04_ARU
	 * 
	 * @param aelSismamaMamoRes
	 * @throws ApplicationBusinessException 
	 */
	private void executarAposAtualizarSismamaMamoRes(
			AelSismamaMamoRes aelSismamaMamoRes) throws ApplicationBusinessException {
		
		AelSismamaMamoRes aelSismamaMamoResOld = getAelSismamaMamoResDAO().obterOriginal(aelSismamaMamoRes);
	
		if (CoreUtil.modificados(aelSismamaMamoResOld.getSeq(), aelSismamaMamoRes.getSeq())
			|| CoreUtil.modificados(aelSismamaMamoResOld.getAelSismamaMamoCad().getCodigo(), aelSismamaMamoRes.getAelSismamaMamoCad().getCodigo())
			|| CoreUtil.modificados(aelSismamaMamoResOld.getResposta(), aelSismamaMamoRes.getResposta())
			|| CoreUtil.modificados(aelSismamaMamoResOld.getItemSolicitacaoExames(), aelSismamaMamoRes.getItemSolicitacaoExames())
		) {
			executarInsert(aelSismamaMamoResOld, DominioOperacoesJournal.UPD);
		}
	}
	
	private void executarInsert(AelSismamaMamoRes aelSismamaMamoResOld,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelSismamaMamoResJnDAO sismamaMamoResJnDAO = getAelSismamaMamoResJnDAO();

		AelSismamaMamoResJn sismamaMamoResJn = new AelSismamaMamoResJn();

		sismamaMamoResJn.setNomeUsuario(servidorLogado.getUsuario());
		sismamaMamoResJn.setOperacao(operacao);
		sismamaMamoResJn.setSeq(aelSismamaMamoResOld.getSeq());
		sismamaMamoResJn.setS03Codigo(aelSismamaMamoResOld.getAelSismamaMamoCad().getCodigo());
		sismamaMamoResJn.setResposta(aelSismamaMamoResOld.getResposta());
		sismamaMamoResJn.setIseSoeSeq(aelSismamaMamoResOld.getItemSolicitacaoExames().getId().getSoeSeq());
		sismamaMamoResJn.setIseSeqp(aelSismamaMamoResOld.getItemSolicitacaoExames().getId().getSeqp());

		sismamaMamoResJnDAO.persistir(sismamaMamoResJn);
	}

	/**
	 * @ORADB AELT_S04_BRI
	 * 
	 * @param aelSismamaMamoRes
	 * @throws ApplicationBusinessException 
	 */
	private void executarAntesInserirSismamaMamoRes(
			AelSismamaMamoRes aelSismamaMamoRes) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		aelSismamaMamoRes.setCriadoEm(new Date());

		aelSismamaMamoRes.setServidor(servidorLogado);
		
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB p_armazena_inf_clinicas
	 */
	private void armazenarInfClinicas(Short iseSeqp,Integer iseSoeSeqp,String infoClinicas) throws ApplicationBusinessException {
		
		AelSismamaMamoRes aelSismamaMamoRes = getAelSismamaMamoResDAO().obterRespostaMamografia(iseSoeSeqp, iseSeqp, DominioSismamaMamoCadCodigo.C_INF_CLINICA);
		if(aelSismamaMamoRes == null){
			aelSismamaMamoRes = new AelSismamaMamoRes();
		}
		
		AelSismamaMamoCad aelSismamaMamoCad = getAelSismamaMamoCadDAO().obterAelSismamaMamoCad(DominioSismamaMamoCadCodigo.C_INF_CLINICA.toString());
		
		aelSismamaMamoRes.setAelSismamaMamoCad(aelSismamaMamoCad);
		aelSismamaMamoRes.setResposta(infoClinicas);
		
		AelItemSolicitacaoExames itemSolicitacaoExame = getAelItemSolicitacaoExameDAO().obterPorId(iseSoeSeqp, iseSeqp);
		
		aelSismamaMamoRes.setItemSolicitacaoExames(itemSolicitacaoExame);
		
		persistirSismamaMamoRes(aelSismamaMamoRes);
	}
	
	/**
	 * @throws ApplicationBusinessException  
	 * @ORADB p_armazena_respostas
	 * @ORADB p_armazena_inf_clinicas
	 * @ORADB p_armazena_achados_mama_dir
	 * @ORADB p_armazena_achados_mama_esq
	 * @ORADB p_armazena_conc_diag
	 */
	
	public void armazenarRespostas(Map<String, AelSismamaMamoResVO> mapD, 
								  Map<String, AelSismamaMamoResVO> mapE,
								  Map<String, AelSismamaMamoResVO> mapConclusao,
								  Short iseSeqp, 
								  Integer iseSoeSeqp, 
								  boolean vHabilitaMamaEsquerda, 
								  boolean vHabilitaMamaDireita,String infoClinicas) throws ApplicationBusinessException {
		
		armazenarInfClinicas(iseSeqp,iseSoeSeqp, infoClinicas); 
		
		if(vHabilitaMamaDireita){
			inserirAchadosMamaConclusao(mapD, iseSeqp,iseSoeSeqp, "D");
		}
		if(vHabilitaMamaEsquerda){
			inserirAchadosMamaConclusao(mapE, iseSeqp,iseSoeSeqp, "E");
		}

		inserirAchadosMamaConclusao(mapConclusao, iseSeqp,iseSoeSeqp, null);
		
	}

	/**
	 * @throws ApplicationBusinessException  
	 * @ORADB  p_armazena_achados_mama_dir
	 */
	private void inserirAchadosMamaConclusao(Map<String, AelSismamaMamoResVO> map, Short iseSeqp, Integer iseSoeSeqp, String ladoMama) throws ApplicationBusinessException {
		
		AelSismamaMamoRes aelSismamaMamoRes = null;
		AelSismamaMamoCad aelSismamaMamoCad = new AelSismamaMamoCad();
			
		for(Map.Entry<String, AelSismamaMamoResVO> e : map.entrySet()) {
            
			if(e.getValue() != null) {
				
				aelSismamaMamoRes = getAelSismamaMamoResDAO().obterRespostaMamografia(iseSoeSeqp, iseSeqp, DominioSismamaMamoCadCodigo.valueOf(e.getKey()));
				if(aelSismamaMamoRes == null){
					aelSismamaMamoRes = new AelSismamaMamoRes();
				}
		
				if(ladoMama != null) {
					aelSismamaMamoRes.setResposta(verificarValorCheckBox(e,ladoMama));
				}

				if (aelSismamaMamoRes.getResposta() == null || ladoMama == null) {
					aelSismamaMamoRes.setResposta(conferirQualCampoInserir(e));
				}

				aelSismamaMamoCad = getAelSismamaMamoCadDAO().obterAelSismamaMamoCad(e.getKey());
				
				aelSismamaMamoRes.setAelSismamaMamoCad(aelSismamaMamoCad);
				
				if (DominioSismamaMamoCadCodigo.C_OBS_GERAIS.toString().equals(aelSismamaMamoRes.getAelSismamaMamoCad().getCodigo()) && e.getValue().getObservacoes() != null) {
					aelSismamaMamoRes.setResposta(e.getValue().getObservacoes().replaceAll("\\n", "<br>"));
				}
				
				if (DominioSismamaMamoCadCodigo.C_SER_MATR_RESID.toString().equals(aelSismamaMamoRes.getAelSismamaMamoCad().getCodigo()) && e.getValue().getResidente() != null) {
					aelSismamaMamoRes.setResposta(e.getValue().getResidente().getId().getMatricula().toString());
				}

				if (DominioSismamaMamoCadCodigo.C_SER_VIN_COD_RESID.toString().equals(aelSismamaMamoRes.getAelSismamaMamoCad().getCodigo()) && e.getValue().getResidente() != null) {
					aelSismamaMamoRes.setResposta(e.getValue().getResidente().getId().getVinCodigo().toString());
				}
				
				AelItemSolicitacaoExames itemSolicitacaoExame = getAelItemSolicitacaoExameDAO().obterPorId(iseSoeSeqp, iseSeqp);
				
				aelSismamaMamoRes.setItemSolicitacaoExames(itemSolicitacaoExame);
				
				persistirSismamaMamoRes(aelSismamaMamoRes);
			}
			
		}
		
	}
	
	private String conferirQualCampoInserir(Map.Entry<String, AelSismamaMamoResVO> e) {
		
		String resposta = "";
		
		if(e.getValue().getNumeroFilmes() != null) {
			resposta = e.getValue().getNumeroFilmes().toString();
		}
		else if(e.getValue().getObservacoes() != null) {
			resposta = e.getValue().getObservacoes();
		}
		else if(e.getValue().getComposicao() != null) {
			resposta = String.valueOf(e.getValue().getComposicao().getCodigo());
		}
		else if(e.getValue().getPele() != null) {
			resposta = String.valueOf(e.getValue().getPele().getCodigo());
		}
		else if(e.getValue().getDistribuicao() != null) {
			resposta = String.valueOf(e.getValue().getDistribuicao().getCodigo());
		}
		else if(e.getValue().getLocalizacao() != null) {
			resposta = String.valueOf(e.getValue().getLocalizacao().getCodigo());
		}
		else if(e.getValue().getForma() != null) {
			resposta = String.valueOf(e.getValue().getForma().getCodigo());
		}
		else if(e.getValue().getContorno() != null) {
			resposta = String.valueOf(e.getValue().getContorno().getCodigo());
		}
		else if(e.getValue().getLimite() != null) {
			resposta = String.valueOf(e.getValue().getLimite().getCodigo());
		}
		else if(e.getValue().getLinfonodoAxilar() != null) {
			resposta = String.valueOf(e.getValue().getLinfonodoAxilar().getCodigo());
		}
		else if(e.getValue().getTamanho() != null) {
			resposta = String.valueOf(e.getValue().getTamanho().getCodigo());
		}
		else if(e.getValue().getCategoria() != null) {
			resposta = String.valueOf(e.getValue().getCategoria().getCodigo());
		}
		else if(e.getValue().getRecomendacao() != null) {
			resposta = String.valueOf(e.getValue().getRecomendacao().getCodigo());
		}
		else if(e.getValue().getResponsavel() != null) {
			resposta = String.valueOf(e.getValue().getResponsavel().getId().getNome());
		}
		else if(e.getValue().getResidente() != null) {
			resposta = String.valueOf(e.getValue().getResidente().getPessoaFisica().getNome());
		}
		
		
		return resposta;
	}
//	
//	private boolean validarSeVoPreenchido(Map.Entry<String, AelSismamaMamoResVO> entry) {
//		
//		boolean voPreenchido = false;
//		
//		if(	entry.getValue().isChecked() 
//			|| entry.getValue().getComposicao() != null
//			|| entry.getValue().getPele() != null
//			|| entry.getValue().getDistribuicao() != null
//			|| entry.getValue().getLocalizacao() != null
//			|| entry.getValue().getForma() != null
//			|| entry.getValue().getContorno() != null
//			|| entry.getValue().getLimite() != null
//			|| entry.getValue().getLinfonodoAxilar() != null
//			|| entry.getValue().getTamanho() != null
//			|| entry.getValue().getCategoria() != null
//			|| entry.getValue().getRecomendacao() != null
//			|| entry.getValue().getNumeroFilmes() != null
//		) {
//			voPreenchido = true;
//		}
//		
//		return voPreenchido;	
//		
//	}
	
	private String verificarValorCheckBox(Map.Entry<String, AelSismamaMamoResVO> entry, String ladoMama) {
		
		List<String> listaMama =  new ArrayList<String>();
		String resposta = null;
		
		if(ladoMama.equals("D")) {
			listaMama = getListMamaDireita();
		}
		if(ladoMama.equals("E")) {
			listaMama = getListMamaEsquerda();
		}
		
		if(listaMama.contains(entry.getKey())) {
			if(entry.getValue().isChecked()) {
				resposta = "3";
			} 
			else {
				resposta = "0";
			}
		}
		else{
			//Verifica se é o numero de filmes
			if(entry.getValue().getNumeroFilmes() != null){
				resposta = String.valueOf(entry.getValue().getNumeroFilmes());
			}
		}
		
		return resposta;
	}
	
	
	private List<String> getListMamaDireita() {
		List<String> listMamaDireita = new ArrayList<String>();
		
		listMamaDireita.add("C_NOD_SIM_01D");
		listMamaDireita.add("C_NOD_SIM_02D");
		listMamaDireita.add("C_NOD_SIM_03D");
		listMamaDireita.add("C_MICRO_SIM_01D");
		listMamaDireita.add("C_MICRO_SIM_02D");
		listMamaDireita.add("C_MICRO_SIM_03D");
		listMamaDireita.add("C_ASSI_FOC_SIM01D");
		listMamaDireita.add("C_ASSI_FOC_SIM02D");
		listMamaDireita.add("C_DIS_FOC_SIM01D");
		listMamaDireita.add("C_DIS_FOC_SIM02D");
		listMamaDireita.add("C_ASSI_DIF_SIM01D");
		listMamaDireita.add("C_ASSI_DIF_SIM02D");
		listMamaDireita.add("C_AR_DENS_SIM01D");
		listMamaDireita.add("C_AR_DENS_SIM02D");
		listMamaDireita.add("C_LINF_AUX_AUM_D");
		listMamaDireita.add("C_LINF_AUX_DENSO_D");
		listMamaDireita.add("C_LINF_AUX_CONF_D");
		listMamaDireita.add("C_NOD_DEN_D");
		listMamaDireita.add("C_CALC_VASC_D");
		listMamaDireita.add("C_DIS_ARQ_CIR_D");
		listMamaDireita.add("C_NOD_CAL_D");
		listMamaDireita.add("C_CALC_ASP_BEN_D");
		listMamaDireita.add("C_IMP_INTEG_D");
		listMamaDireita.add("C_NOD_DEN_HET_D");
		listMamaDireita.add("C_LINF_INTRAM_D");
		listMamaDireita.add("C_IMP_SIN_RUP_D");
		listMamaDireita.add("C_DILAT_DUC_D");
			 
		return listMamaDireita;
	}
	
	
	private List<String> getListMamaEsquerda() {
		List<String> listMamaEsquerda = new ArrayList<String>();
		
		listMamaEsquerda.add("C_NOD_SIM_01E");
		listMamaEsquerda.add("C_NOD_SIM_02E");
		listMamaEsquerda.add("C_NOD_SIM_03E");
		listMamaEsquerda.add("C_MICRO_SIM_01E");
		listMamaEsquerda.add("C_MICRO_SIM_02E");
		listMamaEsquerda.add("C_MICRO_SIM_03E");
		listMamaEsquerda.add("C_ASSI_FOC_SIM01E");
		listMamaEsquerda.add("C_ASSI_FOC_SIM02E");
		listMamaEsquerda.add("C_DIS_FOC_SIM01E");
		listMamaEsquerda.add("C_DIS_FOC_SIM02E");
		listMamaEsquerda.add("C_ASSI_DIF_SIM01E");
		listMamaEsquerda.add("C_ASSI_DIF_SIM02E");
		listMamaEsquerda.add("C_AR_DENS_SIM01E");
		listMamaEsquerda.add("C_AR_DENS_SIM02E");
		listMamaEsquerda.add("C_LINF_AUX_AUM_E");
		listMamaEsquerda.add("C_LINF_AUX_DENSO_E");
		listMamaEsquerda.add("C_LINF_AUX_CONF_E");
		listMamaEsquerda.add("C_NOD_DEN_E");
		listMamaEsquerda.add("C_CALC_VASC_E");
		listMamaEsquerda.add("C_DIS_ARQ_CIR_E");
		listMamaEsquerda.add("C_NOD_CAL_E");
		listMamaEsquerda.add("C_CALC_ASP_BEN_E");
		listMamaEsquerda.add("C_IMP_INTEG_E");
		listMamaEsquerda.add("C_NOD_DEN_HET_E");
		listMamaEsquerda.add("C_LINF_INTRAM_E");
		listMamaEsquerda.add("C_IMP_SIN_RUP_E");
		listMamaEsquerda.add("C_DILAT_DUC_E");
			 
		return listMamaEsquerda;
	}

	private AelSismamaMamoResJnDAO getAelSismamaMamoResJnDAO() {
		return aelSismamaMamoResJnDAO;
	}

	private AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
