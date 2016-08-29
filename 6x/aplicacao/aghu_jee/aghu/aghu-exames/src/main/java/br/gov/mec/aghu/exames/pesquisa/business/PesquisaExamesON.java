package br.gov.mec.aghu.exames.pesquisa.business;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoDAO;
import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoHistDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.questionario.vo.QuestionarioVO;
import br.gov.mec.aghu.exames.questionario.vo.RespostaQuestaoVO;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PesquisaExamesON extends BaseBusiness {


@Inject
private AelRespostaQuestaoHistDAO aelRespostaQuestaoHistDAO;

private static final Log LOG = LogFactory.getLog(PesquisaExamesON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private AelRespostaQuestaoDAO aelRespostaQuestaoDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6353137653577690002L;

	public enum PesquisaExamesONExceptionCode implements BusinessExceptionCode {
		
		AEL_01100, ERRO_MENU_EXAMES, ERRO_SOLICITACAO_EXAME_RESPOSTA_QUESTAO, MSG_ERRO_PERMISSAO_VISUALIZAR_LAUDO,
		ERRO_SELECIONE_APENAS_UMA_SOLICITACAO_EXAME, NAO_PERMITE_VER_EXECUTANDO;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}

		public static void catchException(Exception e) throws ApplicationBusinessException {
			catchException(e, null);
		}

		public static void catchException(Exception e, AelExamesExceptionCode defaultExeptionCode)
				throws ApplicationBusinessException {
			if (e instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) e;
			} else if (defaultExeptionCode != null) {
				defaultExeptionCode.throwException();
			}
		}
	}

	
	
	public boolean permiteVisualizarLaudoMedico(){
		return getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarLaudoMedico", "visualizar");
	}
	
	public boolean permitevisualizarLaudoAtdExt(){
		return getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarLaudoAtdExt", "visualizar");
	}
	
	public boolean permitevisualizarLaudoSamis(){
		return getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarLaudoSamis", "visualizar");
	}
	
	
	/**

	 * Verifica se os exames selecionados estao na situacao liberado ou executando
	 * @throws ApplicationBusinessException
	 */
	public void validaSituacaoExamesSelecionados(Map<Integer, Vector<Short>> solicitacoes, Boolean isHist, Boolean validarSitExecutando) throws ApplicationBusinessException {

		Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
		AghParametros paramLiberado = null;
		AghParametros paramExecutando = null;
		
		paramLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		paramExecutando = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);

		while (it.hasNext()) {

			Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
			Integer solicitacao = paramCLValores.getKey();
			Vector<Short> seqps = paramCLValores.getValue();
			for (Iterator iterator = seqps.iterator(); iterator.hasNext();) {

				Short seqp = (Short) iterator.next();
				String codigoItemSolic = null;
				AelItemSolicitacaoExames itemSolicitacaoExames = null;
				if(isHist){
					AelItemSolicExameHist itemSolicExames = getExamesFacade().buscaItemSolicitacaoExamePorIdHist(solicitacao, seqp);
					codigoItemSolic = itemSolicExames.getSituacaoItemSolicitacao().getCodigo();
				}else{
					itemSolicitacaoExames = getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(new AelItemSolicitacaoExamesId(solicitacao, seqp));
					codigoItemSolic = itemSolicitacaoExames.getSituacaoItemSolicitacao().getCodigo();
					
					// #36088
					if(validarSitExecutando && !itemSolicitacaoExames.getAelUnfExecutaExames().getIndPermVerLaudoExecutando() 
							&& codigoItemSolic.equals(paramExecutando.getVlrTexto())){
						throw new ApplicationBusinessException(PesquisaExamesONExceptionCode.NAO_PERMITE_VER_EXECUTANDO);
					}
				}
				
				if (!(codigoItemSolic.equals(paramLiberado.getVlrTexto())
						|| codigoItemSolic.equals(paramExecutando.getVlrTexto()))) {
					throw new ApplicationBusinessException(PesquisaExamesONExceptionCode.AEL_01100);
				}
			}
		}
	}

	/**
	 * Verifica se a situacao está dentro de: Coletado pelo Solicitante, Agendado, A Executar e A Coletar
	 * @param situacaoCodigo
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public boolean validarSituacaoExameSelecionado(final String situacaoCodigo) {
		// Verifica se a situacao está dentro de: 
		// Coletado pelo Solicitante, Agendado, A Executar e A Coletar
		return	situacaoCodigo.equals(DominioSituacaoItemSolicitacaoExame.CO.toString()) ||
				situacaoCodigo.equals(DominioSituacaoItemSolicitacaoExame.AG.toString()) ||
				situacaoCodigo.equals(DominioSituacaoItemSolicitacaoExame.AX.toString()) ||
				situacaoCodigo.equals(DominioSituacaoItemSolicitacaoExame.AC.toString());
	}
	
	/**
	 * Verifica se foi selecionado apenas um exame
	 * @param solicitacoes
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	
	public Boolean validaQuantidadeExamesSelecionados(final Map<Integer, Vector<Short>> solicitacoes) {
		if (solicitacoes.size() > 1) {
			return false;
		} else if (solicitacoes.size() == 1) {
			Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
			if (it.hasNext()) {
				Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
				Vector<Short> seqps = paramCLValores.getValue();
				Iterator<Short> iterator = seqps.iterator();
				iterator.next();
				if (iterator.hasNext()) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void validarExamesComResposta(final Integer soeSeq, final Short seqp) throws ApplicationBusinessException {
		List<QuestionarioVO> lista = this.getAelRespostaQuestaoDAO().pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(soeSeq, seqp);
		if(lista ==null || lista.size()==0){
			throw new ApplicationBusinessException(PesquisaExamesONExceptionCode.ERRO_SOLICITACAO_EXAME_RESPOSTA_QUESTAO);
		}
	}
	
	
	public Map<Integer, Vector<Short>> obterListaSolicitacoesImpressaoLaudo(final Map<Integer, Vector<Short>> solicitacoes, final List<PesquisaExamesPacientesResultsVO> listaPacientes, final Integer prontuario, final DominioTipoImpressaoLaudo tipoImpressaoLaudo) throws ApplicationBusinessException{
		
		Map<Integer, Vector<Short>> solicitacoesTemp = null;
		
		if(solicitacoes!=null && !solicitacoes.isEmpty()){
		
			solicitacoesTemp = new HashMap<Integer, Vector<Short>>();
	
			Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
			while (it.hasNext()) {

				Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
				Integer solicitacao = paramCLValores.getKey();
				Vector<Short> seqps = paramCLValores.getValue();
				
				for (Iterator iterator = seqps.iterator(); iterator.hasNext();) {
					Short seqp = (Short) iterator.next();
					
					for (PesquisaExamesPacientesResultsVO listaResult : listaPacientes) {
						/*Manda pra impressora caso abaixo e somente as solicitações selecionadas 
						 * Se o usuário não possuir permissão visualizarLaudoMedico e possui permissão visualizarLaudoAtdExt 
						 * e ( é atendimento externo ou origem = ‘X’ ou ‘D’ ou prontuário do paciente é nulo), então envia para impressora*/
						if(listaResult.getCodigoSoe().equals(solicitacao) 
								&& listaResult.getIseSeq().equals(seqp)
								&& (
										(	(DominioOrigemAtendimento.X.equals(listaResult.getOrigemAtendimento())
											|| DominioOrigemAtendimento.D.equals(listaResult.getOrigemAtendimento())
											|| prontuario == null) && DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO.equals(tipoImpressaoLaudo)
										)
										||
										(	!DominioOrigemAtendimento.X.equals(listaResult.getOrigemAtendimento())
											&& !DominioOrigemAtendimento.D.equals(listaResult.getOrigemAtendimento())
											&& DominioTipoImpressaoLaudo.LAUDO_SAMIS.equals(tipoImpressaoLaudo))
										)){
							/*Adiciona a lista*/
							selecionaItemExameTemp(solicitacoesTemp, solicitacao, seqp);
						}
					}
				}
			}
		}
		
		if(solicitacoesTemp == null || solicitacoesTemp.isEmpty()){
			throw new ApplicationBusinessException(PesquisaExamesONExceptionCode.MSG_ERRO_PERMISSAO_VISUALIZAR_LAUDO);  
		}
		
		
		return solicitacoesTemp;
	}
	
	
	private void selecionaItemExameTemp(Map<Integer, Vector<Short>> solicitacoesTemp, Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		if(solicitacoesTemp.containsKey(codigoSoeSelecionado)){
			if(solicitacoesTemp.get(codigoSoeSelecionado).contains(iseSeqSelecionado)){

				solicitacoesTemp.get(codigoSoeSelecionado).remove(iseSeqSelecionado);

				if(solicitacoesTemp.get(codigoSoeSelecionado).size()==0){
					solicitacoesTemp.remove(codigoSoeSelecionado);
				}
			}else{
				solicitacoesTemp.get(codigoSoeSelecionado).add(iseSeqSelecionado);
			}
		}else{
			solicitacoesTemp.put(codigoSoeSelecionado, new Vector<Short>());
			solicitacoesTemp.get(codigoSoeSelecionado).add(iseSeqSelecionado);
		}
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	private IPermissionService getPermissionService() {
		return this.permissionService;
	}
	
	private AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return this.aelItemSolicitacaoExameDAO;
	}
	
	protected AelRespostaQuestaoDAO getAelRespostaQuestaoDAO() {
		return aelRespostaQuestaoDAO;
	}

	public String getUrlImpax(Map<Integer, Vector<Short>> solicitacoes) throws BaseException {
//		Integer codPaciente = null;
		String accessiom= null; //pac_oru_acc_numbe
		String url = null;

			AghParametros paramverImgImpax = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_VER_IMAGENS_IMPAX);
			AghParametros paramEndWebImagens = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ENDERECO_WEB_IMAGENS);

			if(solicitacoes!=null && paramverImgImpax.getVlrNumerico().equals(BigDecimal.valueOf(1))){
				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();
				
				AelItemSolicitacaoExames itemSolicitacaoExames = null;				
				if (it.hasNext()) {
					Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
					Integer solicitacao = paramCLValores.getKey();
					Vector<Short> seqps = paramCLValores.getValue();
					AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
					id.setSoeSeq(solicitacao);
					id.setSeqp(seqps.get(0));
					itemSolicitacaoExames = this.getExamesFacade().obteritemSolicitacaoExamesPorChavePrimaria(id);
					accessiom = getPacOruAccNumber(itemSolicitacaoExames);
				}	
//				if(paciente != null && paciente.getCodigo() != null){
//					codPaciente = paciente.getCodigo();
//				}
				
				Integer atvSeq = itemSolicitacaoExames!=null ? itemSolicitacaoExames.getSolicitacaoExame().getAtendimentoDiverso().getSeq() : null;
				
				if(atvSeq != null && accessiom!=null){
					url = paramEndWebImagens.getVlrTexto()+atvSeq+"ATV"+"%26accession%3D"+accessiom;
				}
				//url exemplo = "window.open('agfahc://impax-client-epr/?user=pacshcpa&password=hcpapacs&domain=Agfa%20Healthcare&patientid=2856450&accession=281277', 'IMPAX');";
			}
		return url;
	}
	
	private String getPacOruAccNumber(AelItemSolicitacaoExames itemSolicitacaoExames) {
		String accessiom;
//		Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();
//		Integer solicitacao = paramCLValores.getKey();
//		Vector<Short> seqps = paramCLValores.getValue();
//
//		AelItemSolicitacaoExamesId id = new AelItemSolicitacaoExamesId();
//		id.setSoeSeq(solicitacao);
//		id.setSeqp(seqps.get(0));
//		AelItemSolicitacaoExames itemSolicitacaoExames = this.getExamesFacade().obteritemSolicitacaoExamesPorChavePrimaria(id);
		accessiom = itemSolicitacaoExames.getPacOruAccNumber();
		
		return accessiom;
	}

	public void validarExamesComRespostaHistorico(final Integer soeSeq, final Short seqp) throws ApplicationBusinessException {
		List<QuestionarioVO> lista = this.getAelRespostaQuestaoHistDAO().pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(soeSeq, seqp);
		if(lista ==null || lista.size()==0){
			throw new ApplicationBusinessException(PesquisaExamesONExceptionCode.ERRO_SOLICITACAO_EXAME_RESPOSTA_QUESTAO);
		}
	}
	
	protected AelRespostaQuestaoHistDAO getAelRespostaQuestaoHistDAO() {
		return aelRespostaQuestaoHistDAO;
	}

	public List<QuestionarioVO> pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(
			Integer soeSeq, Short seqp, Boolean isHist) {
		if(isHist){
			return this.getAelRespostaQuestaoHistDAO().pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(soeSeq, seqp);
		}else{
			return this.getAelRespostaQuestaoDAO().pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(soeSeq, seqp);
		}
	}

	public List<RespostaQuestaoVO> pesquisarRespostasPorQuestionarioEItemSolicitacaoExame(
			Integer qtnSeq, Integer soeSeq, Short seqp, Boolean isHist) {
		if(isHist){
			return this.getAelRespostaQuestaoHistDAO().pesquisarRespostasPorQuestionarioEItemSolicitacaoExame(qtnSeq, soeSeq, seqp);
		}else{
			return this.getAelRespostaQuestaoDAO().pesquisarRespostasPorQuestionarioEItemSolicitacaoExame(qtnSeq, soeSeq, seqp);
		}
	}
	
	public AelItemSolicitacaoExames obterDadoItensSolicitacaoPorSoeSeq(Integer soeSeq, Short seqp) {
		return getAelItemSolicitacaoExameDAO().obterDadoItensSolicitacaoPorSoeSeq(soeSeq, seqp);
	}
	
	public List<AelItemSolicitacaoExames> obterDadosItensSolicitacaoPorSoeSeq(Integer soeSeq, Short seqp) {
		return getAelItemSolicitacaoExameDAO().obterDadosItensSolicitacaoPorSoeSeq(soeSeq, seqp);
	}

}