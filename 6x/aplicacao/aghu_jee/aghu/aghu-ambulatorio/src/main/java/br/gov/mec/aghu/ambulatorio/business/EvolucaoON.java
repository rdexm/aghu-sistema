package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamFiguraEvolucaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamImagemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamItemEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespQuestEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpAlturasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaDiastolicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPaSistolicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPerimCefalicosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTmpPesosDAO;
import br.gov.mec.aghu.ambulatorio.vo.EvolucaoAutorelacaoVO;
import br.gov.mec.aghu.ambulatorio.vo.EvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.GeraEvolucaoVO;
import br.gov.mec.aghu.ambulatorio.vo.PreGeraItemQuestVO;
import br.gov.mec.aghu.ambulatorio.vo.RespostaEvolucaoVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamFiguraEvolucao;
import br.gov.mec.aghu.model.MamImagemEvolucao;
import br.gov.mec.aghu.model.MamItemEvolucoes;
import br.gov.mec.aghu.model.MamItemEvolucoesId;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.model.MamRespQuestEvolucoes;
import br.gov.mec.aghu.model.MamRespQuestEvolucoesId;
import br.gov.mec.aghu.model.MamRespostaEvolucoes;
import br.gov.mec.aghu.model.MamRespostaEvolucoesId;
import br.gov.mec.aghu.model.MamTmpAlturas;
import br.gov.mec.aghu.model.MamTmpPaDiastolicas;
import br.gov.mec.aghu.model.MamTmpPaSistolicas;
import br.gov.mec.aghu.model.MamTmpPerimCefalicos;
import br.gov.mec.aghu.model.MamTmpPesos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por manter as regras de negócio da EVOLUCAO.
 *
 */
@Stateless
public class EvolucaoON extends BaseBusiness {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2263022406106984630L;
	private static final Log LOG = LogFactory.getLog(EvolucaoON.class);

	@Inject
	private MamEvolucoesDAO  mamEvolucoesDAO;
	
	@Inject
	private MamItemEvolucoesDAO  mamItemEvolucoesDAO;
	
	@Inject
	private MamRespostaEvolucoesDAO  mamRespostaEvolucoesDAO;
	
	@Inject
	private MamQuestaoDAO  mamQuestaoDAO;
	
	@Inject
	private MamRespQuestEvolucoesDAO mamRespQuestEvolucoesDAO;	
	
	@Inject
	private MamFiguraEvolucaoDAO  mamFiguraEvolucaoDAO;
	
	@Inject
	private MamNotaAdicionalEvolucoesDAO  mamNotaAdicionalEvolucoesDAO;
	
	@Inject
	private MamTmpPesosDAO mamTmpPesosDAO;
	
	@Inject
	private MamTmpAlturasDAO mamTmpAlturasDAO;
	
	@Inject
	private MamTmpPerimCefalicosDAO mamTmpPerimCefalicosDAO;
	
	@Inject
	private MamTmpPaSistolicasDAO mamTmpPaSistolicasDAO;
	
	@Inject
	private MamTmpPaDiastolicasDAO mamTmpPaDiastolicasDAO;
	
	@Inject
	private MamImagemEvolucoesDAO mamImagemEvolucoesDAO;
	
	@Inject
	private MamNotaAdicionalEvolucoesDAO mamNotaAdicionalDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@EJB
	private IParametroFacade  parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;	
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	@EJB
	private CancelamentoAtendimentoRN cancelamentoAtendimentoRN;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private EvolucaoAuxiliarON evolucaoAuxiliarON;
	
	@EJB
	private MamRespostaEvolucoesRN mamRespostaEvolucoesRN;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
    public enum EvolucaoONExceptionCode implements BusinessExceptionCode {
    	INFORMAR_VALOR_ABA_LIVRE
    }
	
	// #49998  GRAVAR
	public void gravarEvolucao(Integer conNumero,Long pEvoSeq,List<EvolucaoVO> listaBotoes) throws ApplicationBusinessException{
		
		GeraEvolucaoVO geraEvolucaoVO = geraEvolucao(conNumero, pEvoSeq);
		
		for (EvolucaoVO item : listaBotoes) {
			MamItemEvolucoesId id = new MamItemEvolucoesId();
			id.setEvoSeq(geraEvolucaoVO.getEvoSeq());
			id.setTieSeq(item.getTipoItemEvolucao().getSeq());
			MamItemEvolucoes itemEvolucao = mamItemEvolucoesDAO.obterPorChavePrimaria(id);
			// caso não exista o item de evolução
			Boolean insere = false;
			if(itemEvolucao == null){
				itemEvolucao = new MamItemEvolucoes();
				itemEvolucao.setId(id);
				insere = true;
			}
			// Atualizar o conteúdo da tabela de itens da evolução
			// com os valores informados nas abas livres de cada botão
			String textoAjustado = null;
			if (item.getTexto() != null) {
				textoAjustado = item.getTexto().replaceAll("\\r\\n", "\n");
			}
			itemEvolucao.setDescricao(textoAjustado);
			if(insere){
				mamItemEvolucoesDAO.persistir(itemEvolucao);
				insere = false;
				gravarRespostaEvolucao(insere, itemEvolucao, item);
			}else{
				mamItemEvolucoesDAO.merge(itemEvolucao);
				gravarRespostaEvolucao(insere, itemEvolucao, item);
			}
		}
		this.mamItemEvolucoesDAO.flush();
		// se p1 for 'S' executa U1 e U2
		if("S".equals(verUsuarioFezEvolucao(geraEvolucaoVO.getEvoSeq()))){
			//a) Atualizar a tabela da evolução (U1)
			MamEvolucoes evolucao = mamEvolucoesDAO.pesquisarMamEvolucoesPendente(geraEvolucaoVO.getEvoSeq(),DominioIndPendenteAmbulatorio.R);
			if(evolucao != null){
				evolucao.setPendente(DominioIndPendenteAmbulatorio.P);
				mamEvolucoesDAO.atualizar(evolucao);
			}
			//b) Atualizar a tabela da evolução (U2)
			if(geraEvolucaoVO.getEvoEvoSeq() != null){
				MamEvolucoes evolucao2 = mamEvolucoesDAO.pesquisarMamEvolucoesPendente(geraEvolucaoVO.getEvoEvoSeq(),DominioIndPendenteAmbulatorio.V);
				if(evolucao2 != null){
					evolucao2.setPendente(DominioIndPendenteAmbulatorio.A);
					evolucao2.setServidorMvto(servidorLogadoFacade.obterServidorLogado());
					mamEvolucoesDAO.atualizar(evolucao2);
				}
			}
		}
	}
	
	public void gravarRespostaEvolucao(boolean insere, MamItemEvolucoes itemEvolucao, EvolucaoVO item){
		
		if (item.getListaPreGeraItemQuestVO() != null && !item.getListaPreGeraItemQuestVO().isEmpty()) {
			for (PreGeraItemQuestVO registro : item.getListaPreGeraItemQuestVO()) {
				MamRespostaEvolucoes respostaEvolucao = new MamRespostaEvolucoes();
				MamRespostaEvolucoesId id = new MamRespostaEvolucoesId(registro.getpEvoSeq(), registro.getQusQutSeq(), registro.getQusSeqP(), registro.getSeqP());
				
				if(insere){
					respostaEvolucao.setId(id);
					respostaEvolucao.setEspSeq(registro.getvEspSeq());
				}else{
					respostaEvolucao = this.mamRespostaEvolucoesDAO.obterPorChavePrimaria(id);
				}
				
				if (registro.isCheckValor()) {
					respostaEvolucao.setVvqQusQutSeq(registro.getQusQutSeq());
					respostaEvolucao.setVvqQusSeqp(registro.getQusSeqP());
					respostaEvolucao.setVvqSeqp(registro.getSeqP());
				}else{
					respostaEvolucao.setVvqQusQutSeq(null);
					respostaEvolucao.setVvqQusSeqp(null);
					respostaEvolucao.setVvqSeqp(null);
				}

				respostaEvolucao.setResposta(registro.getResposta());
				
				if(insere){
					this.mamRespostaEvolucoesDAO.persistir(respostaEvolucao);
				}else{
					this.mamRespostaEvolucoesDAO.atualizar(respostaEvolucao);
				}
			}
		}
	}
	
	// #49998 - Acao do botao Excluir
	public void excluirEvolucao(Integer conNumero, Long pEvoSeq, EvolucaoVO botaoSelecionado) throws ApplicationBusinessException{
		//Ajuste de documentacao 52434
		GeraEvolucaoVO evolucao = geraEvolucao(conNumero, pEvoSeq);
		pEvoSeq = evolucao.getEvoSeq();
		
		if(Boolean.FALSE.equals(podeExcluirEvolucao(pEvoSeq))){
			cancelaEvolucao(conNumero, pEvoSeq);
		} else {
			excluiEvolucao(pEvoSeq, conNumero, botaoSelecionado);
		}
	}
	
	// #49998 - Acao do botao OK
	// Por solicitação do #52848, a validação foi removida
	public void gravarOkEvolucao(Integer conNumero, Long pEvoSeq, List<EvolucaoVO> listaBotoes) throws ApplicationBusinessException {
		/*
		// Validar os campos (botoes e abas) obrigatorios
		for (EvolucaoVO evolucaoVO : listaBotoes) {
			if (
					evolucaoVO.getTipoItemEvolucao() != null 
					&& 
					Boolean.TRUE.equals(
							evolucaoVO.getTipoItemEvolucao().getObrigatorio()) && evolucaoVO.getTexto() == null
							) {
				throw new ApplicationBusinessException(EvolucaoONExceptionCode.INFORMAR_VALOR_ABA_LIVRE, evolucaoVO.getTipoItemEvolucao().getDescricao());
			} else {
				gravarEvolucao(conNumero, pEvoSeq, listaBotoes);
			}
		}
		*/
		gravarEvolucao(conNumero, pEvoSeq, listaBotoes);
	}
	
	// #49998 P3  @ORADB MAMP_EXCLUI_EVOLUCAO
	public void excluiEvolucao(Long pEvoSeq, Integer conNumero, EvolucaoVO botaoSelecionado) throws ApplicationBusinessException{
		
		for (PreGeraItemQuestVO item : botaoSelecionado.getListaPreGeraItemQuestVO()) {
			MamRespostaEvolucoes repostaEvolucao = this.mamRespostaEvolucoesDAO.obterPorChavePrimaria
					(new MamRespostaEvolucoesId(pEvoSeq, item.getQusQutSeq(), item.getQusSeqP(), item.getSeqP()));
			if(repostaEvolucao != null){
				this.mamRespostaEvolucoesDAO.remover(repostaEvolucao);
			}
		}
		
		RapServidores usuarioLogado = getServidorLogadoFacade().obterServidorLogado();
		
		 List<MamEvolucoes> curEvo = mamEvolucoesDAO.pesquisarMamEvolucoesPorConNumero(pEvoSeq,conNumero);
		 for (MamEvolucoes mamEvolucoes : curEvo) {
			 //  -- EXCLUI AS TEMPORÁRIAS DA EVOLUÇÃO USADAS NOS GRÁFICOS
			 cancelarDelTemp(mamEvolucoes.getSeq());
			 if(DominioIndPendenteAmbulatorio.R.equals(mamEvolucoes.getPendente()) || DominioIndPendenteAmbulatorio.P.equals(mamEvolucoes.getPendente())){
				 pDeleteFisico(mamEvolucoes.getSeq());
			 }
			 if(mamEvolucoes.getEvolucao() != null){
				 MamEvolucoes entity = mamEvolucoesDAO.obterPorChavePrimaria(mamEvolucoes.getEvolucao().getSeq());
				 entity.setPendente(DominioIndPendenteAmbulatorio.E);
				 entity.setDthrMvto(new Date());
				 entity.setServidorMvto(usuarioLogado);
				 marcacaoConsultaRN.atualizarEvolucao(entity);
			 }
		 }
		 
		 List<MamFiguraEvolucao> cFie = mamFiguraEvolucaoDAO.obterFiguraEvolucaoPorConNumero(conNumero);
		 for (MamFiguraEvolucao mamFiguraEvolucao : cFie) {
			 if("R".equals(mamFiguraEvolucao.getIndPendente()) || "P".equals(mamFiguraEvolucao.getIndPendente())){
				 pDelFisicoFigura(mamFiguraEvolucao.getSeq());
			 }
			 if(mamFiguraEvolucao.getMamFiguraEvolucao() != null){
				 MamFiguraEvolucao figura = mamFiguraEvolucaoDAO.obterPorChavePrimaria(mamFiguraEvolucao.getMamFiguraEvolucao().getSeq());
				 figura.setIndPendente("E");
				 figura.setDthrMvto(new Date());
				 figura.setRapServidoresByMamFieSerFk3(usuarioLogado);
				 mamFiguraEvolucaoDAO.atualizar(figura);
			 }
		 }

		 excluirEvolucaoNotasAdicionaisEvolucao(conNumero, usuarioLogado);
	}

	private void excluirEvolucaoNotasAdicionaisEvolucao(Integer conNumero, RapServidores usuarioLogado)
			throws ApplicationBusinessException {
		List<MamNotaAdicionalEvolucoes> cNev = mamNotaAdicionalEvolucoesDAO.pesquisarNotaAdicionalPorNumeroConsultaEPendente(conNumero);
		 // -- Notas adicionais evolucoes
		 for (MamNotaAdicionalEvolucoes mamNotaAdicionalEvolucoes : cNev) {
			if(DominioIndPendenteAmbulatorio.R.equals(mamNotaAdicionalEvolucoes.getPendente()) || DominioIndPendenteAmbulatorio.P.equals(mamNotaAdicionalEvolucoes.getPendente())){
				pDelFisicoNota(mamNotaAdicionalEvolucoes.getSeq());
			}
			if(mamNotaAdicionalEvolucoes.getNotaAdicionalEvolucao() != null){
				MamNotaAdicionalEvolucoes notaAdicional = mamNotaAdicionalEvolucoesDAO.obterPorChavePrimaria(mamNotaAdicionalEvolucoes.getNotaAdicionalEvolucao().getSeq());
				notaAdicional.setPendente(DominioIndPendenteAmbulatorio.E);
				notaAdicional.setDthrMvto(new Date());
				notaAdicional.setServidorMvto(usuarioLogado);
				mamNotaAdicionalEvolucoesDAO.atualizar(notaAdicional);
			}
		}
	}
	
	public void pDeleteFisico(Long tSeq) throws ApplicationBusinessException{
		 List<MamItemEvolucoes> mamItemEvolucoes = mamItemEvolucoesDAO.pesquisarItemEvolucoesPorEvolucao(tSeq);
		 for (MamItemEvolucoes item : mamItemEvolucoes) {
			 marcacaoConsultaRN.removerItemEvolucao(item);
		 }
		 
		 List<MamRespQuestEvolucoes> mamRespQuest = mamRespQuestEvolucoesDAO.pesquisarPorEvolucao(tSeq);
		 for (MamRespQuestEvolucoes item : mamRespQuest) {
			 mamRespQuestEvolucoesDAO.remover(item);
		 }
		 
		 List<MamRespostaEvolucoes> repostaEvolucoes = mamRespostaEvolucoesDAO.pesquisarRespostaEvolucoesPorEvolucao(tSeq);
		 for (MamRespostaEvolucoes item : repostaEvolucoes) {
			 this.mamRespostaEvolucoesRN.remover(item);
		 }
		 MamEvolucoes mamEvolucoes = mamEvolucoesDAO.obterPorChavePrimaria(tSeq);
		 marcacaoConsultaRN.removerEvolucao(mamEvolucoes);
	}
	
	public void pDelFisicoFigura(Long tFieSeq){
		 List<MamImagemEvolucao> imagemEvolucao = mamImagemEvolucoesDAO.pesquisarItemEvolucoesPorEvolucao(tFieSeq);
		 for (MamImagemEvolucao mamImagemEvolucao : imagemEvolucao) {
			 mamImagemEvolucoesDAO.remover(mamImagemEvolucao);
		 }
		 MamFiguraEvolucao figuraEvolucao = mamFiguraEvolucaoDAO.obterPorChavePrimaria(tFieSeq);
		 mamFiguraEvolucaoDAO.remover(figuraEvolucao);
	}	
	
	public void pDelFisicoNota(Integer tNevSeq) throws ApplicationBusinessException{
		 MamNotaAdicionalEvolucoes notaAdicional = mamNotaAdicionalDAO.obterPorChavePrimaria(tNevSeq);
		 marcacaoConsultaRN.removerNotaAdicionalEvolucoes(notaAdicional);
	}	
	
	// #49998 P4  @ORADB MAMP_CANC_DEL_TMP_E
	public void cancelarDelTemp(Long pEvoSeq){
		Date dthrMvto = null;
		
		// -- limpa a tabela temporário dos pesos
		List<MamTmpPesos> lista = mamTmpPesosDAO.obterPesosPorEvolucao(pEvoSeq, dthrMvto);
		for (MamTmpPesos mamTmpPesos : lista) {
			mamTmpPesosDAO.remover(mamTmpPesos);
		}
		
		// -- limpa a tabela temporário das alturas
		List<MamTmpAlturas> listaAltura = mamTmpAlturasDAO.obterAlturaPorEvolucao(pEvoSeq, dthrMvto);
		for (MamTmpAlturas item : listaAltura) {
			mamTmpAlturasDAO.remover(item);
		}
		
		// -- limpa a tabela temporário dos perimetros cefalicos
		List<MamTmpPerimCefalicos> listaPerim = mamTmpPerimCefalicosDAO.obterPorEvolucao(pEvoSeq, dthrMvto);
		for (MamTmpPerimCefalicos item : listaPerim) {
			mamTmpPerimCefalicosDAO.remover(item);
		}
		
		// -- limpa a tabela temporário das pa sistolicas
		List<MamTmpPaSistolicas> listaPa = mamTmpPaSistolicasDAO.obterPorEvolucao(pEvoSeq);
		for (MamTmpPaSistolicas mamTmpPaSistolicas : listaPa) {
			mamTmpPaSistolicasDAO.remover(mamTmpPaSistolicas);
		}
		
		// -- limpa a tabela temporário das pa diastolicas
		List<MamTmpPaDiastolicas> listaDia = mamTmpPaDiastolicasDAO.obterPorEvolucao(pEvoSeq);
		for (MamTmpPaDiastolicas item : listaDia) {
			mamTmpPaDiastolicasDAO.remover(item);
		}
	}

	public Long verificaEvo(MamEvolucoes curEvo,Long vEvoEvoSeq){
		if(curEvo.getEvolucao() != null){
			vEvoEvoSeq = curEvo.getEvolucao().getSeq();
		}
		return vEvoEvoSeq;
	}
	
	// #49998 P1   @ORADB MAMC_VER_USR_FEZ_EVO
	public String verUsuarioFezEvolucao(Long pEvoSeq){
		String vUsrFezEvo = "N";
		String vTemEvo = "N";
		String vTemItemEvo = "N";
		String vTemRespostaEvo = "N";
		String vTemFiguraEvo = "N";
		String vTemNotaEvo = "N";
		
		DominioIndPendenteAmbulatorio vIndPendente = null;
		Long vEvoEvoSeq = null;
		Integer vPacCodigo = null;
		Integer vConNumero = null;
		String vRespostaCadastral = null;
		//  -- cursor da evolucção
		MamEvolucoes  curEvo = mamEvolucoesDAO.obterPorChavePrimaria(pEvoSeq);
		if(curEvo != null){
			vIndPendente = curEvo.getPendente();
			vEvoEvoSeq = verificaEvo(curEvo,vEvoEvoSeq);
			vPacCodigo = curEvo.getPaciente().getCodigo();
			if(curEvo.getConsulta() != null){
				vConNumero = curEvo.getConsulta().getNumero();
			}
		}
		
		vTemEvo = verificaTemEvo(vIndPendente,vEvoEvoSeq,vTemEvo);
		
		if("N".equals(vTemEvo)){
			// -- verifica o item de evoluções
			vTemItemEvo = cursorItemEvolucao(pEvoSeq,vTemItemEvo);
			
			if("N".equals(vTemItemEvo)){
				// -- verifica as respostas de evoluções
				vTemRespostaEvo = "N";
				//  -- cursor das respostas de evolução
				List<RespostaEvolucaoVO> curRev = mamQuestaoDAO.obterQuestaoPorEvolucaoNativo(pEvoSeq);	
				for (RespostaEvolucaoVO respostaEvolucaoVO : curRev) {
					if(respostaEvolucaoVO.getVvqQusQutSeq() != null || respostaEvolucaoVO.getVvqQusSeqp() != null || respostaEvolucaoVO.getVvqSeqp() != null){
						vTemRespostaEvo = "S";
					}
					if(respostaEvolucaoVO.getFueSeq() == null){
						vTemRespostaEvo = verificaTemRespostaEvo(vTemRespostaEvo,respostaEvolucaoVO);
					}else{
						vRespostaCadastral = mamcExecFncEdicao(respostaEvolucaoVO.getFueSeq(), vPacCodigo);
						vTemRespostaEvo = verificaTemRespostaEvoSemSeq(vTemRespostaEvo,respostaEvolucaoVO,vRespostaCadastral);
					}
				 }
				 if("N".equals(vTemRespostaEvo)){
					 //  -- verifica o item de evolução
					 vTemFiguraEvo = cursorFigurasEvolucao(vConNumero,vTemFiguraEvo);
					 
					 if("N".equals(vTemFiguraEvo)){
						// -- verifica a nota adicional de evolução
						vTemNotaEvo = cursorNotaAdicionalEvolucao(vConNumero,vTemNotaEvo);
					 }
				 }
			}
		}
		
		if("S".equals(vTemEvo) || "S".equals(vTemItemEvo) || "S".equals(vTemRespostaEvo) || "S".equals(vTemFiguraEvo) || "S".equals(vTemNotaEvo)){
			vUsrFezEvo = "S";
		}else{
			vUsrFezEvo = "N";
		}
		
		return vUsrFezEvo;
	}
	
	public String verificaTemRespostaEvo (String vTemRespostaEvo,RespostaEvolucaoVO respostaEvolucaoVO){
		if(respostaEvolucaoVO.getTextoFormatado() == null && respostaEvolucaoVO.getResposta() != null){
			vTemRespostaEvo = "S";
		}
		else if(respostaEvolucaoVO.getTextoFormatado() != null && respostaEvolucaoVO.getResposta() == null){
			vTemRespostaEvo = "S";
		}
		else if(respostaEvolucaoVO.getTextoFormatado() != null && respostaEvolucaoVO.getResposta() != null
				&& !respostaEvolucaoVO.getTextoFormatado().equals(respostaEvolucaoVO.getResposta())){
			vTemRespostaEvo = "S";
		}
		return vTemRespostaEvo;
	}
	
	public String cursorItemEvolucao(Long pEvoSeq,String vTemItemEvo){
		Long curIev	= mamItemEvolucoesDAO.pesquisarItemEvolucoesPorEvolucaoCount(pEvoSeq);
		
		if(curIev > 0){
			vTemItemEvo = "S";
		}else{
			vTemItemEvo = "N";
		}
		return vTemItemEvo;
	}
	
	public String cursorFigurasEvolucao(Integer vConNumero,String vTemFiguraEvo){
		Long curFie = mamFiguraEvolucaoDAO.obterQuestaoPorEvolucaoCount(vConNumero);
		if(curFie > 0){
			vTemFiguraEvo = "S";
		}else{
			vTemFiguraEvo = "N";
		}
		return vTemFiguraEvo;
	}
	
	public String cursorNotaAdicionalEvolucao(Integer vConNumero,String vTemNotaEvo){
		List<MamNotaAdicionalEvolucoes> curNevRet = mamNotaAdicionalEvolucoesDAO.pesquisarNotaAdicionalEvolucoesPorNumeroConsulta(vConNumero);
		
		if(!curNevRet.isEmpty()){
			vTemNotaEvo = "S";
		}else{
			vTemNotaEvo = "N";
		}
		return vTemNotaEvo;
	}
	
	public String verificaTemRespostaEvoSemSeq (String vTemRespostaEvo,RespostaEvolucaoVO respostaEvolucaoVO, String vRespostaCadastral){
		if(vRespostaCadastral == null && respostaEvolucaoVO.getResposta() != null){
			vTemRespostaEvo = "S";
		}
		if(vRespostaCadastral != null && respostaEvolucaoVO.getResposta() == null){
			vTemRespostaEvo = "S";
		}
		if(vRespostaCadastral != null && respostaEvolucaoVO.getResposta() != null
				&& !vRespostaCadastral.equals(respostaEvolucaoVO.getResposta())){
			vTemRespostaEvo = "S";
		}
		return vTemRespostaEvo;
	}
	
	public String verificaTemEvo(DominioIndPendenteAmbulatorio vIndPendente,Long vEvoEvoSeq,String vTemEvo){
		if(DominioIndPendenteAmbulatorio.R.equals(vIndPendente)){
			vTemEvo = "N";
		}else if(DominioIndPendenteAmbulatorio.P.equals(vIndPendente) && vEvoEvoSeq == null){
			vTemEvo = "N";
		}else{
			vTemEvo = "S";
		}
		return vTemEvo;
	}
	
	// #49998 P2 - @ORADB P_GERA_EVOLUCAO
	public GeraEvolucaoVO geraEvolucao(Integer pConNumero, Long pParameterEvoSeq) throws ApplicationBusinessException{
        Long vEvoSeqNew = null;      
        Long vEvoEvoSeqNew = null;
		
        // k_variaveis
		GeraEvolucaoVO geraEvolucaoVO = new GeraEvolucaoVO();
		geraEvolucaoVO.setSysdate(new Date());
		geraEvolucaoVO.setServidor(this.getServidorLogadoFacade().obterServidorLogado());

		MamEvolucoes curEvo = null;
		MamEvolucoes curEvoOut = null;
		
		curEvo = mamEvolucoesDAO.obterEvolucoesPorConNumeroEEvoSeq(pConNumero, pParameterEvoSeq);
		
		if(curEvo == null){
			vEvoEvoSeqNew = null;
			geraEvolucaoVO.setReplicaEvolucao(DominioSimNao.S);
		
			// Inserindo nova evolução
			MamEvolucoes novaEvolucao = new MamEvolucoes();
			AacConsultas consulta = aacConsultasDAO.obterConsulta(pConNumero);
			novaEvolucao.setConsulta(consulta);
			novaEvolucao.setPendente(DominioIndPendenteAmbulatorio.R);
			novaEvolucao.setDthrCriacao(geraEvolucaoVO.getSysdate());
			novaEvolucao.setImpresso(Boolean.FALSE);
			novaEvolucao = marcacaoConsultaRN.inserirEvolucao(novaEvolucao);
			vEvoSeqNew = novaEvolucao.getSeq();
		
		} else {
		
			geraEvolucaoVO = verificaReplica(geraEvolucaoVO,curEvo);
			
			if(DominioIndPendenteAmbulatorio.V.equals(curEvo.getPendente())){
				if(curEvo.getServidorValida().getId().getMatricula().equals(geraEvolucaoVO.getServidor().getId().getMatricula()) && 
						curEvo.getServidorValida().getId().getVinCodigo().equals(geraEvolucaoVO.getServidor().getId().getVinCodigo())){
					geraEvolucaoVO.setExecutaPreGera(DominioSimNao.S);
				} else {
					geraEvolucaoVO.setExecutaPreGera(DominioSimNao.N);
				}
			} else if(DominioIndPendenteAmbulatorio.P.equals(curEvo.getPendente()) && curEvo.getEvolucao() != null){
				
				curEvoOut = mamEvolucoesDAO.obterPorChavePrimaria(curEvo.getEvolucao().getSeq());
				
				if(curEvoOut != null){
					if(curEvoOut.getServidorValida() != null){
						if(curEvoOut.getServidorValida().getId().getMatricula().equals(geraEvolucaoVO.getServidor().getId().getMatricula()) && 
								curEvoOut.getServidorValida().getId().getVinCodigo().equals(geraEvolucaoVO.getServidor().getId().getVinCodigo())){
							geraEvolucaoVO.setExecutaPreGera(DominioSimNao.S);
						} else {
							geraEvolucaoVO.setExecutaPreGera(DominioSimNao.N);
						}
					}else{
						geraEvolucaoVO.setExecutaPreGera(DominioSimNao.N);
					}
				}
			} else if(curEvo.getServidorValida() == null){
				geraEvolucaoVO.setExecutaPreGera(DominioSimNao.S);
			}else{
				geraEvolucaoVO.setExecutaPreGera(DominioSimNao.N);
			}
			
			if (DominioIndPendenteAmbulatorio.V.equals(curEvo.getPendente()) && DominioSimNao.N.equals(geraEvolucaoVO.getReplicaEvolucao())){
				vEvoSeqNew = curEvo.getSeq();
				vEvoEvoSeqNew = null;
			} else if (DominioIndPendenteAmbulatorio.R.equals(curEvo.getPendente()) || DominioIndPendenteAmbulatorio.P.equals(curEvo.getPendente())){
		        vEvoSeqNew = curEvo.getSeq();
		        if(curEvo.getEvolucao() != null){
		        	vEvoEvoSeqNew = curEvo.getEvolucao().getSeq();
		        }
			} else {		 
				
		        // Atualizando Evolução
				MamEvolucoes atualizaEvolucao = mamEvolucoesDAO.obterPorChavePrimaria(curEvo.getSeq());
				atualizaEvolucao.setDthrMvto(geraEvolucaoVO.getSysdate());
				atualizaEvolucao.setServidorMvto(geraEvolucaoVO.getServidor());
				marcacaoConsultaRN.atualizarEvolucao(atualizaEvolucao);
				
				// Copia a evolucao
				MamEvolucoes copiaEvolucao = new MamEvolucoes();
				copiaEvolucao.setDthrCriacao(geraEvolucaoVO.getSysdate());
				copiaEvolucao.setConsulta(atualizaEvolucao.getConsulta());
				copiaEvolucao.setPendente(DominioIndPendenteAmbulatorio.R);
				copiaEvolucao.setServidor(geraEvolucaoVO.getServidor());
				copiaEvolucao.setEvolucao(atualizaEvolucao);
				copiaEvolucao.setImpresso(Boolean.FALSE);
				marcacaoConsultaRN.inserirEvolucao(copiaEvolucao);
				vEvoEvoSeqNew = copiaEvolucao.getSeq();
				mamEvolucoesDAO.flush();
				
		        vEvoSeqNew = copiaEvolucao.getSeq();
		        vEvoEvoSeqNew = curEvo.getSeq();				
				
				// Copia as resposta dadas na evolucao
				Set<MamRespostaEvolucoes> mamRespostaEvolucoes = atualizaEvolucao.getRespostasEvolucoes();
				for (MamRespostaEvolucoes rea : mamRespostaEvolucoes) {
					persistirRespostaEvolucao(rea,vEvoSeqNew);
				}
				
				// copia as respostas estruturadas das questões de anamnese
				List<MamRespQuestEvolucoes> mamRespQuestEvolucoesLista = mamRespQuestEvolucoesDAO.pesquisarPorEvolucao(vEvoEvoSeqNew);
				for (MamRespQuestEvolucoes rqe : mamRespQuestEvolucoesLista) {
					MamRespQuestEvolucoesId mamRespQuestEvolucoesId = new MamRespQuestEvolucoesId();
					mamRespQuestEvolucoesId.setRevEvoSeq(vEvoSeqNew);
					mamRespQuestEvolucoesId.setRevQusQutSeq(rqe.getId().getRevQusQutSeq());
					mamRespQuestEvolucoesId.setRevQusSeqp(rqe.getId().getRevQusSeqp());
					mamRespQuestEvolucoesId.setRevSeqp(rqe.getId().getRevSeqp());
					
					MamRespQuestEvolucoes mamRespQuestEvolucoesNew = new MamRespQuestEvolucoes();
					mamRespQuestEvolucoesNew.setId(mamRespQuestEvolucoesId);
					
					MamRespostaEvolucoesId respEvoId = new MamRespostaEvolucoesId(
							rqe.getId().getRevEvoSeq(), 
							rqe.getId().getRevQusQutSeq(), 
							rqe.getId().getRevQusSeqp(), 
							rqe.getId().getRevSeqp());
					
					MamRespostaEvolucoes respEvo = new MamRespostaEvolucoes(respEvoId);
					mamRespQuestEvolucoesNew.setRespostaEvolucao(respEvo);
					mamRespQuestEvolucoesDAO.persistir(mamRespQuestEvolucoesNew);
				}

				// copia os itens de evolucao
				Set<MamItemEvolucoes> mamItemEvolucoes = atualizaEvolucao.getItensEvolucoes();
				for (MamItemEvolucoes iev : mamItemEvolucoes) {
					MamItemEvolucoesId mamItemEvolucoesId = new MamItemEvolucoesId();
					mamItemEvolucoesId.setEvoSeq(vEvoSeqNew);
					mamItemEvolucoesId.setTieSeq(iev.getId().getTieSeq());
					
					MamItemEvolucoes itemEvolucaoNew = new MamItemEvolucoes();
					itemEvolucaoNew.setId(mamItemEvolucoesId);
					itemEvolucaoNew.setDescricao(iev.getDescricao());
					getMarcacaoConsultaRN().inserirItemEvolucao(itemEvolucaoNew);
				}
			}
		}
		
		geraEvolucaoVO.setEvoSeq(vEvoSeqNew);
		geraEvolucaoVO.setEvoEvoSeq(vEvoEvoSeqNew);

		return geraEvolucaoVO;
	}
	
	public void persistirRespostaEvolucao(MamRespostaEvolucoes rea,Long vEvoSeqNew) throws ApplicationBusinessException{
		MamRespostaEvolucoesId mamRespostaEvolucoesId = new MamRespostaEvolucoesId();
		mamRespostaEvolucoesId.setEvoSeq(vEvoSeqNew);
		mamRespostaEvolucoesId.setQusQutSeq(rea.getId().getQusQutSeq());
		mamRespostaEvolucoesId.setQusSeqp(rea.getId().getQusSeqp());
		mamRespostaEvolucoesId.setSeqp(rea.getId().getSeqp());
		
		MamRespostaEvolucoes respostaEvolucaoNew = new MamRespostaEvolucoes();
		respostaEvolucaoNew.setId(mamRespostaEvolucoesId);
		respostaEvolucaoNew.setVvqQusQutSeq(rea.getVvqQusQutSeq());
		respostaEvolucaoNew.setVvqQusSeqp(rea.getVvqSeqp());
		respostaEvolucaoNew.setVvqSeqp(rea.getVvqSeqp());
		respostaEvolucaoNew.setResposta(rea.getResposta());
		respostaEvolucaoNew.setEspSeq(rea.getEspSeq());
		
		this.mamRespostaEvolucoesRN.persistir(respostaEvolucaoNew);
	}

	public GeraEvolucaoVO verificaReplica(GeraEvolucaoVO geraEvolucaoVO, MamEvolucoes curEvo){
		if(DominioIndPendenteAmbulatorio.V.equals(curEvo.getPendente())){
			if(curEvo.getServidorValida().getId().getMatricula().equals(geraEvolucaoVO.getServidor().getId().getMatricula()) && 
					curEvo.getServidorValida().getId().getVinCodigo().equals(geraEvolucaoVO.getServidor().getId().getVinCodigo())){
				geraEvolucaoVO.setReplicaEvolucao(DominioSimNao.S);
			} else {
				geraEvolucaoVO.setReplicaEvolucao(DominioSimNao.N);
			}
		} else {
			geraEvolucaoVO.setReplicaEvolucao(DominioSimNao.N);
		}
		return geraEvolucaoVO;
	}
	
	// #49998 P6 - @ORADB MAMP_PEND
	public Boolean podeExcluirEvolucao(Long parameterEvoSeq){
		Boolean podeExcluir = Boolean.FALSE;
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		EvolucaoAutorelacaoVO evolucao = mamEvolucoesDAO.obeterEvolucaoComAutorelacaoPorSeq(parameterEvoSeq);
		
		if(evolucao != null){
			if(DominioIndPendenteAmbulatorio.V.equals(evolucao.getPendente())) {
				if(evolucao.getSerMatriculaValida().equals(servidorLogado.getId().getMatricula()) &&
						evolucao.getSerVinCodigoValida().equals(servidorLogado.getId().getVinCodigo())){
					podeExcluir = Boolean.TRUE;
				} else {
					podeExcluir = Boolean.FALSE;
				}
			} else if ((DominioIndPendenteAmbulatorio.P.equals(evolucao.getPendente()) || 
					DominioIndPendenteAmbulatorio.R.equals(evolucao.getPendente())) && 
					evolucao.getSerMatriculaValidaRelacao() != null){
				if(evolucao.getSerMatriculaValidaRelacao().equals(servidorLogado.getId().getMatricula()) &&
						evolucao.getSerVinCodigoValidaRelacao().equals(servidorLogado.getId().getVinCodigo())){
					podeExcluir = Boolean.TRUE;
				} else {
					podeExcluir = Boolean.FALSE;
				}
			} else if(evolucao.getSerMatriculaValida() == null &&
					evolucao.getSerVinCodigoValida() == null){
				// e um registro novo
				podeExcluir = Boolean.TRUE;
			}
		}
		
		return podeExcluir;
	}	
	
	// #49998 P7 - @ORADB MAMP_CANC_EVOLUCAO (parte 1/4)
	public void cancelaEvolucao(Integer conNumero, Long evoSeq) throws ApplicationBusinessException{
		Date dthrMvto = null;
		List<MamEvolucoes> mamEvolucoes = mamEvolucoesDAO.obterEvolucoesPorConNumeroEDthrMvtoEEvoSeq(conNumero, dthrMvto, evoSeq);
		for (MamEvolucoes mamEvolucao : mamEvolucoes) {
			cancelarDelTemp(mamEvolucao.getSeq());
			if(DominioIndPendenteAmbulatorio.R.equals(mamEvolucao.getPendente())){
				pTrataRascunho(mamEvolucao.getSeq(), mamEvolucao.getEvolucao().getSeq());
			} else if(DominioIndPendenteAmbulatorio.P.equals(mamEvolucao.getPendente())){
				pTrataPendente(mamEvolucao.getSeq(), mamEvolucao.getEvolucao().getSeq());
			} else if(DominioIndPendenteAmbulatorio.E.equals(mamEvolucao.getPendente())){
				pTrataExclusao(mamEvolucao.getSeq());
			}
		}
	}	
	
	// #49998 P7 - @ORADB MAMP_CANC_EVOLUCAO (parte 2/4)
	public void pTrataRascunho(Long tSeq, Long tEvoSeq) throws ApplicationBusinessException{	
		List<MamItemEvolucoes> mamItemEvolucoes = mamItemEvolucoesDAO.pesquisarItemEvolucoesPorEvolucao(tSeq);
		for (MamItemEvolucoes mamItemEvolucao : mamItemEvolucoes) {
			marcacaoConsultaRN.removerItemEvolucao(mamItemEvolucao);
		}
		
		List<MamRespQuestEvolucoes> mamRespQuestEvolucoes = mamRespQuestEvolucoesDAO.pesquisarPorEvolucao(tSeq);
		for (MamRespQuestEvolucoes mamRespQuestEvolucao : mamRespQuestEvolucoes) {
			mamRespQuestEvolucoesDAO.remover(mamRespQuestEvolucao);
		}

		List<MamRespostaEvolucoes> mamRespostaEvolucoes = mamRespostaEvolucoesDAO.listarRespostasEvolucoesPorEvolucao(tSeq);
		for (MamRespostaEvolucoes mamRespostaEvolucao : mamRespostaEvolucoes) {
			this.mamRespostaEvolucoesRN.remover(mamRespostaEvolucao);
		}
		
		MamEvolucoes mamEvolucoes = mamEvolucoesDAO.obterPorChavePrimaria(tSeq);
		marcacaoConsultaRN.removerEvolucao(mamEvolucoes);
		
		if(tEvoSeq != null){
			MamEvolucoes evolucao = mamEvolucoesDAO.obterPorChavePrimaria(tEvoSeq);
			evolucao.setDthrMvto(null);
			evolucao.setServidorMvto(null);
			marcacaoConsultaRN.atualizarEvolucao(evolucao);
		}
	}
	
	// #49998 P7 - @ORADB MAMP_CANC_EVOLUCAO (parte 3/4)
	public void pTrataPendente(Long tSeq, Long tEvoSeq) throws ApplicationBusinessException{
		List<MamItemEvolucoes> mamItemEvolucoes = mamItemEvolucoesDAO.pesquisarItemEvolucoesPorEvolucao(tSeq);
		for (MamItemEvolucoes mamItemEvolucao : mamItemEvolucoes) {
			marcacaoConsultaRN.removerItemEvolucao(mamItemEvolucao);
		}
		
		List<MamRespQuestEvolucoes> mamRespQuestEvolucoes = mamRespQuestEvolucoesDAO.pesquisarPorEvolucao(tSeq);
		for (MamRespQuestEvolucoes mamRespQuestEvolucao : mamRespQuestEvolucoes) {
			mamRespQuestEvolucoesDAO.remover(mamRespQuestEvolucao);
		}

		List<MamRespostaEvolucoes> mamRespostaEvolucoes = mamRespostaEvolucoesDAO.listarRespostasEvolucoesPorEvolucao(tSeq);
		for (MamRespostaEvolucoes mamRespostaEvolucao : mamRespostaEvolucoes) {
			this.mamRespostaEvolucoesRN.remover(mamRespostaEvolucao);
		}
		
		MamEvolucoes mamEvolucoes = mamEvolucoesDAO.obterPorChavePrimaria(tSeq);
		marcacaoConsultaRN.removerEvolucao(mamEvolucoes);
		
		if(tEvoSeq != null){
			MamEvolucoes evolucao = mamEvolucoesDAO.obterPorChavePrimaria(tEvoSeq);
			evolucao.setDthrMvto(null);
			evolucao.setServidorMvto(null);
			evolucao.setPendente(DominioIndPendenteAmbulatorio.V);
			marcacaoConsultaRN.atualizarEvolucao(evolucao);
		}
	}
	
	// #49998 P7 - @ORADB MAMP_CANC_EVOLUCAO (parte 4/4)
	public void pTrataExclusao(Long tSeq) throws ApplicationBusinessException{
		MamEvolucoes atualizaEvolucao = mamEvolucoesDAO.obterPorChavePrimaria(tSeq);
		if(atualizaEvolucao != null){
			atualizaEvolucao.setDthrMvto(null);
			atualizaEvolucao.setServidorMvto(null);
			atualizaEvolucao.setPendente(DominioIndPendenteAmbulatorio.V);
			marcacaoConsultaRN.atualizarEvolucao(atualizaEvolucao);
		}
	}		
	
	// #49956 ON1
	public Boolean verificaColar() throws ApplicationBusinessException{
		Boolean permiteColar = false;
		AghParametros param = null;

		param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PERMIT_COLAR_EVO);

		if(param == null){
			permiteColar = false;
		}else{
			if("S".equals(param.getVlrTexto())){
				permiteColar = true;
			}
		}
		return permiteColar;
	}
	
	public void gravarMotivoPendente(Integer conNumero,String motivoPendencia, String nomeMicrocomputador, Long evoSeq) throws ApplicationBusinessException{
		
		AghParametros param = null;
		
		//Ajuste de documentacao 52434
		GeraEvolucaoVO evolucao = geraEvolucao(conNumero, evoSeq);
		evoSeq = evolucao.getEvoSeq();
		
		if("EXA".equals(motivoPendencia)){
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_EXAMES);
		}else if("PRE".equals(motivoPendencia)){
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_PROFE);
		}else if("POS".equals(motivoPendencia)){
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_POST);
		}else if("OUT".equals(motivoPendencia)){
			param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SEQ_SIT_PEND_OUT);
		}
		Short satSeq = null;
		if(param.getVlrNumerico() != null){
			satSeq = Short.valueOf(param.getVlrNumerico().toString());
		}
		cancelamentoAtendimentoRN.mampPend(conNumero, new Date(), satSeq, nomeMicrocomputador);
	}
	
	/**
	 * @ORADB MAMC_EXEC_FNC_EDICAO #52025 - FUNCTION DO PACKAGE
	 *        MAMC_EDITA_GRAU_PAC
	 */
	public String mamcExecFncEdicao(Short fueSeq, Integer codigo) {

		String comando = this.ambulatorioFacade.obterCurFuePorSeq(fueSeq);

		if (comando != null && !comando.trim().isEmpty()) {
			switch (comando) {
			case "MAMC_EDITA_DTNAS_PAC":
				return evolucaoAuxiliarON.mamcEditaDtnasPac(codigo);

			case "MAMC_EDITA_CDD_PAC":
				return evolucaoAuxiliarON.mamcEditaCddPac(codigo);

			case "MAMC_EDITA_COR_PAC":
				return evolucaoAuxiliarON.mamcEditaCorPac(codigo);

			case "MAMC_EDITA_NAC_PAC":
				return evolucaoAuxiliarON.mamcEditaNacPac(codigo);

			case "MAMC_EDITA_NATUR_PAC":
				return evolucaoAuxiliarON.mamcEditaNaturPac(codigo);

			case "MAMC_EDITA_PROF_PAC":
				return evolucaoAuxiliarON.mamcEditaProfPac(codigo);

			case "MAMC_EDITA_SEXO_PAC":
				return evolucaoAuxiliarON.mamcEditaSexoPac(codigo);

			case "MAMC_EDITA_NOME_PAC":
				return evolucaoAuxiliarON.mamcEditaNomePac(codigo);

			case "MAMC_EDITA_IDD_PAC":
				return evolucaoAuxiliarON.mamcEditaIddPac(codigo);

			case "MAMC_EDITA_CIVIL_PAC":
				return evolucaoAuxiliarON.mamcEditaCivilPac(codigo);

			case "MAMC_EDITA_IDD_2_PAC":
				return evolucaoAuxiliarON.mamcEditaIdd2Pac(codigo);

			case "MAMC_EDITA_NOME_PAI":
				return evolucaoAuxiliarON.mamcEditaNomePai(codigo);

			case "MAMC_EDITA_NOME_MAE":
				return evolucaoAuxiliarON.mamcEditaNomeMae(codigo);

			case "MAMC_EDITA_IDD_3_PAC":
				return evolucaoAuxiliarON.mamcEditaIdd3Pac(codigo);

			case "MAMC_EDITA_GRAU_PAC":
				return evolucaoAuxiliarON.mamcEditaGrauPac(codigo);

			case "":
				return "";

			}
		}
		return "";
	}
	
	/**
	 * $49992 D1 E D2
	 */
	public void excluirRespostaEItemEvolucao(Long evoSeq){
		List<MamRespostaEvolucoes> listaRespostaEvolucao = this.mamRespostaEvolucoesDAO.pesquisarRespostaEvolucoesPorEvolucao(evoSeq);
		
		for (MamRespostaEvolucoes item : listaRespostaEvolucao) {
			if(item.getResposta() == null || item.getResposta().trim().isEmpty()){
				this.mamRespostaEvolucoesDAO.remover(item);
			}
		}
		
		List<MamItemEvolucoes> listaItemEvolucao = this.mamItemEvolucoesDAO.pesquisarItemEvolucoesPorEvolucao(evoSeq);
		
		for (MamItemEvolucoes itemEvolucao : listaItemEvolucao) {
			if(itemEvolucao.getDescricao() == null || itemEvolucao.getDescricao().trim().isEmpty()){
				this.mamItemEvolucoesDAO.remover(itemEvolucao);
			}
		}
	}
	
	//UTILIZAR ON AUXILIAR PARA EVITAR ERRO DE PMD EvolucaoAuxiliarON
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public MarcacaoConsultaRN getMarcacaoConsultaRN() {
		return marcacaoConsultaRN;
	}

	public void setMarcacaoConsultaRN(MarcacaoConsultaRN marcacaoConsultaRN) {
		this.marcacaoConsultaRN = marcacaoConsultaRN;
	}

	public MamRespostaEvolucoesDAO getMamRespostaEvolucoesDAO() {
		return mamRespostaEvolucoesDAO;
	}

	public void setMamRespostaEvolucoesDAO(MamRespostaEvolucoesDAO mamRespostaEvolucoesDAO) {
		this.mamRespostaEvolucoesDAO = mamRespostaEvolucoesDAO;
	}

	public MamRespQuestEvolucoesDAO getMamRespQuestEvolucoesDAO() {
		return mamRespQuestEvolucoesDAO;
	}

	public void setMamRespQuestEvolucoesDAO(MamRespQuestEvolucoesDAO mamRespQuestEvolucoesDAO) {
		this.mamRespQuestEvolucoesDAO = mamRespQuestEvolucoesDAO;
	}

	public AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	public void setAacConsultasDAO(AacConsultasDAO aacConsultasDAO) {
		this.aacConsultasDAO = aacConsultasDAO;
	}
}