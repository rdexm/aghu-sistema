package br.gov.mec.aghu.blococirurgico.workflow.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.AghWFEtapaDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFExecutorDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFFluxoDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFHistoricoExecucaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFRotaDAO;
import br.gov.mec.aghu.blococirurgico.dao.AghWFTemplateEtapaDAO;
import br.gov.mec.aghu.casca.business.ICentralPendenciaFacade;
import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.dominio.DominioAcaoHistoricoWF;
import br.gov.mec.aghu.dominio.DominioWorkflowOPMEsCodigoTemplateEtapa;
import br.gov.mec.aghu.model.AghWFEtapa;
import br.gov.mec.aghu.model.AghWFExecutor;
import br.gov.mec.aghu.model.AghWFFluxo;
import br.gov.mec.aghu.model.AghWFHistoricoExecucao;
import br.gov.mec.aghu.model.AghWFRota;
import br.gov.mec.aghu.model.AghWFTemplateEtapa;
import br.gov.mec.aghu.model.AghWFTemplateExecutor;
import br.gov.mec.aghu.model.AghWFTemplateFluxo;
import br.gov.mec.aghu.model.MbcRequisicaoOpmes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class WorkflowRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(WorkflowRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private AghWFTemplateEtapaDAO aghWFTemplateEtapaDAO;

	@Inject
	private AghWFFluxoDAO aghWFFluxoDAO;

	@Inject
	private AghWFEtapaDAO aghWFEtapaDAO;

	@Inject
	private AghWFRotaDAO aghWFRotaDAO;

	@Inject
	private AghWFHistoricoExecucaoDAO aghWFHistoricoExecucaoDAO;

	@Inject
	private AghWFExecutorDAO aghWFExecutorDAO;


	@EJB
	private ICentralPendenciaFacade iCentralPendenciaFacade;
	
	private static final long serialVersionUID = -5235807051314430721L;
	
	public enum WorkflowRNExceptionCode implements BusinessExceptionCode {
		WF_ETAPA_JUST_RECUSA, WF_FLUXO_JUST_CANCELAMENTO;
	}
	
	public AghWFTemplateFluxo getTemplateFluxo(String modulo) {
		List<AghWFTemplateEtapa> templateEtapas = getAghWFTemplateEtapaDAO().obterTemplateEtapasPorModulo(modulo);
		if (templateEtapas == null || templateEtapas.isEmpty()) {
			return null;
		}
		return templateEtapas.get(0).getTemplateFluxo();
	}
	
	public AghWFFluxo criarFluxo(AghWFFluxo fluxo) {
		getAghWFFluxoDAO().persistir(fluxo);
		getAghWFFluxoDAO().flush();
		return fluxo;
	}
	
	public AghWFFluxo criarFluxo(AghWFTemplateFluxo templateFluxo,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		AghWFFluxo fluxo = new AghWFFluxo();
		fluxo.setTemplateFluxo(templateFluxo);
		fluxo.setDataInicio(new Date());
		fluxo = criarFluxo(fluxo);
		
		List<AghWFTemplateEtapa> templatesEtapa = templateFluxo.getEtapas();
		if(templatesEtapa != null && !templatesEtapa.isEmpty()) {
			AghWFTemplateEtapa templateEtapa = templatesEtapa.get(0);
			AghWFEtapa etapa = this.adicionarEtapa(null, fluxo, templateEtapa, null);
			associarEtapaFluxo(fluxo, etapa);
			List<AghWFTemplateExecutor> templateExecutores = templateEtapa.getExecutores();
			if(templateExecutores != null && !templateExecutores.isEmpty()) {
				adicionarExecutores(etapa, templateExecutores,requisicao);
			}
		}
		
		return fluxo;
	}

	private void associarEtapaFluxo(AghWFFluxo fluxo, AghWFEtapa etapa) {
		List<AghWFEtapa> etapas = new ArrayList<AghWFEtapa>();
		etapas.add(etapa);
		fluxo.setEtapas(etapas);
	}
	
	
	public List<AghWFRota> getRotas(AghWFEtapa etapaOrigem) {
		return getAghWFRotaDAO().obterRotasPorEtapa(etapaOrigem);
	}

	public AghWFExecutor adicionarExecutor(AghWFExecutor executor,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		getAghWFExecutorDAO().persistir(executor);
		getAghWFExecutorDAO().flush();
		criarPendencia(executor,requisicao);
		return executor;
	}
	
	public void adicionarPendenciaAutorizacao(AghWFEtapa etapa, MbcRequisicaoOpmes requisicao, AghWFTemplateEtapa templateEtapaDestino, AghWFExecutor executor, AghWFEtapa etapaDestino) throws ApplicationBusinessException{
		List<AghWFExecutor> executores = getExecutoresEnvolvidosNoProcessoDeAutorizacao(requisicao.getFluxo().getSeq());
		List<RapServidores> servidores = new ArrayList<RapServidores>();
		for (AghWFExecutor aghWFExecutor : executores) {
			if(!servidores.contains(aghWFExecutor.getRapServidor()) && !aghWFExecutor.getRapServidor().equals(requisicao.getRapServidores())){
				servidores.add(aghWFExecutor.getRapServidor());
			}
		}
		for (RapServidores rapServidor : servidores) {
			criarPendenciaAutorizacao(rapServidor, Boolean.TRUE, etapaDestino.getTemplateEtapa().getDescricao(), etapaDestino.getFluxo().getObservacoes(), etapaDestino.getSeq(), etapaDestino.getTemplateEtapa().getUrl(), executor.getSeq(),requisicao);
		}
	}
	
	public List<AghWFExecutor> getExecutoresEnvolvidosNoProcessoDeAutorizacao(Integer seqFluxo) {
		return this.getAghWFExecutorDAO().obterExecutoresEnvolvidosNoProcessoDeAutorizacao(seqFluxo);
	}
	
	public void setExecutarEtapaCancelamentoFluxo(AghWFExecutor executor, AghWFEtapa etapa, String observacao, Boolean salvaHistorico, MbcRequisicaoOpmes requisicao) throws BaseException {
		if (executor != null) {
			executor.setDataExecucao(new Date());
			atualizarExecutor(executor);
		}

		if (salvaHistorico) {
			salvarHistorico(etapa.getFluxo(), etapa, executor, new Date(), "", observacao, DominioAcaoHistoricoWF.CANCELAMENTO);
		}
		// TODO ALTERACAO
		excluirPendenciaExecutor(executor,requisicao);
	}

	public void adicionarExecutor(AghWFEtapa etapa, AghWFTemplateExecutor templateExecutor,MbcRequisicaoOpmes requisicoes) throws ApplicationBusinessException {		
		AghWFExecutor executor = new AghWFExecutor();
		executor.setEtapa(etapa);
		executor.setFluxo(etapa.getFluxo());
		executor.setTemplateFluxo(etapa.getFluxo().getTemplateFluxo());
		executor.setTemplateEtapa(etapa.getTemplateEtapa());
		executor.setIndRecebeNotificacao(templateExecutor.getIndRecebeNotificacao());		
		executor.setRapServidor(templateExecutor.getRapServidor());
		adicionarExecutor(executor,requisicoes);
		
	}
	
	private void adicionarExecutores(AghWFEtapa etapa, List<AghWFTemplateExecutor> executores, MbcRequisicaoOpmes requisicoes) throws ApplicationBusinessException {
		for (AghWFTemplateExecutor aghWFTemplateExecutor : executores) {
			adicionarExecutor(etapa, aghWFTemplateExecutor,requisicoes);
		}
	}
	
	public void adicionarEtapa(AghWFEtapa etapa, AghWFExecutor executor) {
		etapa.setSequencia(getProximoSequenciaParaEtapa(etapa.getFluxo()));
		getAghWFEtapaDAO().persistir(etapa);
		getAghWFEtapaDAO().flush();
	}

	public AghWFEtapa adicionarEtapa(AghWFEtapa etapaAnterior, AghWFFluxo fluxo, AghWFTemplateEtapa templateEtapa, AghWFExecutor executor) {
		AghWFEtapa etapa = new AghWFEtapa();
		etapa.setFluxo(fluxo);
		etapa.setTemplateFluxo(fluxo.getTemplateFluxo());
		etapa.setTemplateEtapa(templateEtapa);
		etapa.setEtapaAnterior(etapaAnterior);
		etapa.setDataInicio(new Date());
		etapa.setPrazoDias(templateEtapa.getPrazoDias());
		etapa.setIndUnanime(templateEtapa.getIndUnanime());
		adicionarEtapa(etapa, executor);
		return etapa;
	}
	
	public AghWFEtapa adicionarEtapaExecutores(AghWFEtapa etapaAnterior, AghWFFluxo fluxo, AghWFTemplateEtapa templateEtapa, AghWFExecutor executor, MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		AghWFEtapa etapa = new AghWFEtapa();
		etapa.setFluxo(fluxo);
		etapa.setTemplateFluxo(fluxo.getTemplateFluxo());
		etapa.setTemplateEtapa(templateEtapa);
		etapa.setEtapaAnterior(etapaAnterior);
		etapa.setDataInicio(new Date());
		etapa.setPrazoDias(templateEtapa.getPrazoDias());
		etapa.setIndUnanime(templateEtapa.getIndUnanime());
		atualizarExecutor(executor);
		adicionarEtapa(etapa, executor);
		adicionarExecutores(etapa, templateEtapa.getExecutores(),requisicao);
		return etapa;
	}

	private Short getProximoSequenciaParaEtapa(AghWFFluxo fluxo) {
		return getAghWFEtapaDAO().obterProxSequenciaParaEtapaFluxo(fluxo.getSeq());
	}
	

	public void executarEtapa(AghWFExecutor executor, AghWFEtapa etapa, String observacao,  Boolean salvaHistorico,MbcRequisicaoOpmes requisicao) throws BaseException {
		boolean existeExecutorSemDtExe = false;
		if(etapa.getIndUnanime()) {
			for(AghWFExecutor executorEtapa: etapa.getExecutores()) {
				if (!executorEtapa.equals(executor) &&  executorEtapa.getDataExecucao() == null) {
					existeExecutorSemDtExe = true;
					break;
				}
			}
			if(existeExecutorSemDtExe) {
				confirmarExecucaoExecutor(executor, etapa, observacao, salvaHistorico,requisicao);
				
			} else {
				confirmarConclusaoEtapa(executor, etapa, observacao, salvaHistorico,requisicao);
			}
		} else {
			confirmarConclusaoEtapa(executor, etapa, observacao, salvaHistorico,requisicao);
		}		
	}

	private void confirmarExecucaoExecutor(AghWFExecutor executor, AghWFEtapa etapa, String observacao, Boolean salvaHistorico,MbcRequisicaoOpmes requisicao) throws BaseException {
		if(executor != null) {
			executor.setDataExecucao(new Date());
			atualizarExecutor(executor);
		}
		
		if(salvaHistorico){
			if(DominioWorkflowOPMEsCodigoTemplateEtapa.EM_ORCAMENTO_01.toString().equals(etapa.getTemplateEtapa().getCodigo())){
				salvarHistorico(etapa.getFluxo(), etapa, executor, new Date(), "", observacao, DominioAcaoHistoricoWF.EXECUCAO);
			}else{
				salvarHistorico(etapa.getFluxo(), etapa, executor, new Date(), "", observacao, DominioAcaoHistoricoWF.APROVACAO);
			}
		}
		//TODO ALTERACAO
		excluirPendenciaExecutor(executor,requisicao);
	}

	private void confirmarConclusaoEtapa(AghWFExecutor executor, AghWFEtapa etapa, String observacao, Boolean salvaHistorico ,MbcRequisicaoOpmes requisicao) throws BaseException {
		etapa.setDataFim(new Date());
		if(executor != null) {
			executor.setDataExecucao(new Date());
			atualizarExecutor(executor);
		}
		atualizarEtapa(etapa);
		
		if(salvaHistorico){
			if(DominioWorkflowOPMEsCodigoTemplateEtapa.EM_ORCAMENTO_01.toString().equals(etapa.getTemplateEtapa().getCodigo())){
				salvarHistorico(etapa.getFluxo(), etapa, executor, new Date(), "", observacao, DominioAcaoHistoricoWF.EXECUCAO);
			}else{
				salvarHistorico(etapa.getFluxo(), etapa, executor, new Date(), "", observacao, DominioAcaoHistoricoWF.APROVACAO);
			}
			
		}
		//TODO ALTERACAO
		excluirPendenciaExecutor(executor,requisicao);
	}
	
	private void atualizarExecutor(AghWFExecutor executor) {
		getAghWFExecutorDAO().atualizar(executor);
		getAghWFExecutorDAO().flush();
	}

	public void atualizarEtapa(AghWFEtapa etapa) {
		getAghWFEtapaDAO().merge(etapa);
		getAghWFEtapaDAO().flush();
	}

	public void rejeitarEtapa(AghWFEtapa etapa, AghWFExecutor executor,
			String justificativa, MbcRequisicaoOpmes requisicao) throws BaseException {
		if (StringUtils.isBlank(justificativa)) {
			throw new ApplicationBusinessException(
					WorkflowRNExceptionCode.WF_ETAPA_JUST_RECUSA);
		}
		etapa.setDataFim(new Date());
		if (etapa.getEtapaAnterior() != null) {
			//TODO ALTERACAO
			etapa.getEtapaAnterior().setDataFim(null);
			atualizarEtapa(etapa.getEtapaAnterior());
			limparExecucaoEtapaExecutores(etapa.getEtapaAnterior(),requisicao,etapa);
		}
		atualizarEtapa(etapa);
		salvarHistorico(etapa.getFluxo(), etapa, executor, new Date(), justificativa, "", DominioAcaoHistoricoWF.REJEICAO);
		//TODO ALTERACAO
		excluirPendenciaExecutor(executor,requisicao);
	}
	
	private void limparExecucaoEtapaExecutores(AghWFEtapa etapa,MbcRequisicaoOpmes requisicao,AghWFEtapa etapaAtual) throws ApplicationBusinessException {
		for(AghWFExecutor executor: etapa.getExecutores()) {
			executor.setDataExecucao(null);
			atualizarExecutor(executor);
			//criarPendencia(executor,requisicao);
		}
		for (AghWFExecutor exec : etapa.getExecutores()) {
			exec.getEtapa().getTemplateEtapa().setDescricao("Requisição Não Autorizada");
			if(!exec.getRapServidor().equals(requisicao.getRapServidores())){
				criarPendencia(exec,requisicao);
			}
		}
	}
	
	public void cancelarFluxo(AghWFExecutor executor, AghWFFluxo fluxo, String justificativa) throws ApplicationBusinessException {
//		if (StringUtils.isBlank(justificativa)) {
//			throw new ApplicationBusinessException(WorkflowRNExceptionCode.WF_FLUXO_JUST_CANCELAMENTO);
//		}			
		finalizarFluxo(executor, fluxo, justificativa, DominioAcaoHistoricoWF.CANCELAMENTO);
	}
	
	public void concluirFluxo(AghWFExecutor executor, AghWFFluxo fluxo,	String observacao) {
		finalizarFluxo(executor, fluxo, observacao, DominioAcaoHistoricoWF.CONCLUSAO);
	}

	private void finalizarFluxo(AghWFExecutor executor, AghWFFluxo fluxo,
			String justificativa, DominioAcaoHistoricoWF acaoHistoricoWF) {
		fluxo.setDataFim(new Date());
		getAghWFFluxoDAO().atualizar(fluxo);
		getAghWFFluxoDAO().flush();
		AghWFEtapa etapa = getUltimaEtapa(fluxo);
		salvarHistorico(fluxo, etapa, executor, new Date(), justificativa, "", acaoHistoricoWF);
	}
	
	public void salvarHistorico(AghWFFluxo fluxo,
			AghWFEtapa etapa, AghWFExecutor executor,
			Date dataRegistro, String justificativa, String observacao,
			DominioAcaoHistoricoWF acao) {
		AghWFHistoricoExecucao historico = new AghWFHistoricoExecucao(fluxo,
				etapa, executor,
				dataRegistro, justificativa, observacao, acao);
		getAghWFHistoricoExecucaoDAO().persistir(historico);
		getAghWFHistoricoExecucaoDAO().flush();
	}
	
	private AghWFEtapa getUltimaEtapa(AghWFFluxo fluxo) {
		if(fluxo.getEtapas() == null || fluxo.getEtapas().isEmpty()) {
			return null;
		}
		int index = fluxo.getEtapas().size() - 1;
		return fluxo.getEtapas().get(index);
	}
	
	
	public AghWFExecutor getExecutorPorRapServidor(RapServidores servidor, Integer seqEtapa) {
		return getAghWFExecutorDAO().obterExecutorPorRapServidor(servidor, seqEtapa);
	}
	
	public void adicionarExecutores(List<AghWFExecutor> executores,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		for (AghWFExecutor executor : executores) {
			adicionarExecutor(executor,requisicao);
		}
	}
	
	public List<AghWFExecutor> getExecutoresPorCodigoFluxo(Integer fluxoSeq) {
		return this.getAghWFExecutorDAO().obterExecutorPorCodigoFluxo(fluxoSeq);
	}
	

	public List<AghWFExecutor> getExecutoresPorFluxo(RapServidores servidor, Integer seqFluxo) {
		return this.getAghWFExecutorDAO().obterExecutoresPorFluxo(servidor, seqFluxo);
	}
	
	private void criarPendencia(AghWFExecutor executor,MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		String descricao = getDescricaoPendencia(executor,requisicao);
		
		String url = montaURL(executor,requisicao);
		
		this.getCentralPendenciaFacade().adicionarPendenciaAcao(descricao, url, 
				"Acompanhar Processos de Autorização de OPMEs", executor.getRapServidor(), executor.getIndRecebeNotificacao());

	}
	
	private String montaURL(AghWFExecutor executor,MbcRequisicaoOpmes requisicao) {
		String url = executor.getEtapa().getTemplateEtapa().getUrl()+"?seq="+requisicao.getSeq()+"&executarIniciar="+true;
		url = url.replaceAll("xhtml", "seam");
		return url;
	}

	private String getDescricaoPendencia(AghWFExecutor executor,MbcRequisicaoOpmes requisicao) {
		StringBuilder descricao = new StringBuilder();
		String pendencia = "Pendência: ";
		String paciente = "Paciente: ";
		String vazio = " ";
		String parent = ")";
		descricao.append(pendencia)
		.append(executor.getEtapa().getTemplateEtapa().getDescricao());
		if(executor.getFluxo().getObservacoes() != null){
			descricao.append(vazio);
			String desc =  executor.getFluxo().getObservacoes();
			desc =  StringUtils.remove(desc, parent);
			descricao.append(desc);
		}
		
		descricao.append(vazio)
		.append(paciente)
		.append(requisicao.getAgendas().getPaciente().getNome())
		.append(parent);
		return descricao.toString();
	}
	
	private void criarPendenciaAutorizacao(RapServidores servidor, Boolean notificacao, String descricaoPendencia, String Observacoes, Integer etapaSeq, String urlReq, Integer execSeq, MbcRequisicaoOpmes requisicao) throws ApplicationBusinessException {
		String descricao = getDescricaoPendenciaAutorizacao(descricaoPendencia, Observacoes,requisicao);
		
		String url = montaURLAutorizacao(etapaSeq, urlReq, execSeq,requisicao);
		
		this.getCentralPendenciaFacade().adicionarPendenciaAcao(descricao, url, 
				"Acompanhar Processos de Autorização de OPMEs", servidor, notificacao);

	}
	
	private String getDescricaoPendenciaAutorizacao(String descricaoPendencia, String Observacoes,  MbcRequisicaoOpmes requisicao) {
		StringBuilder descricao = new StringBuilder();
		String pendencia = "Pendência: ";
		String paciente = "Paciente: ";
		String vazio = " ";
		String parent = ")";
		descricao.append(pendencia)
		.append(descricaoPendencia);
		if(Observacoes != null){
			descricao.append(vazio);
			String desc =  Observacoes;
			desc =  StringUtils.remove(desc, parent);
			descricao.append(desc);
		}
		descricao.append(vazio)
		.append(paciente)
		.append(requisicao.getAgendas().getPaciente().getNome())
		.append(parent);
		return descricao.toString();
	}
	
	private String montaURLAutorizacao(Integer etapaSeq, String urlReq, Integer execSeq, MbcRequisicaoOpmes requisicao) {
		String url = urlReq+"?seq="+requisicao.getSeq()+"&executarIniciar="+true;
		url = url.replaceAll("xhtml", "seam");
		return url;
	}

	public AghWFTemplateEtapa obterUltimaEtapaFluxo(AghWFFluxo fluxo) {
		if (fluxo != null) {
			if (fluxo.getEtapas() != null && !fluxo.getEtapas().isEmpty()) {
				return fluxo.getEtapas().get(fluxo.getEtapas().size() - 1).getTemplateEtapa();
			}
		}
		return null;
	}
	
	private void excluirPendenciaExecutor(AghWFExecutor executor,MbcRequisicaoOpmes requisicao) throws BaseException {
		String descricao = getDescricaoPendencia(executor,requisicao);
		PendenciaVO pendencia = getPendenciaVO(executor, descricao);
		if(pendencia != null) {
			getCentralPendenciaFacade().excluirPendencia(pendencia.getSeqCaixaPostal());
		}
	}

	private PendenciaVO getPendenciaVO(AghWFExecutor executor,String descricao) throws BaseException {
		List<PendenciaVO> list = getCentralPendenciaFacade().getListaPendencias();
		for (PendenciaVO pendenciaVO : list) {
			if(descricao.equalsIgnoreCase(pendenciaVO.getMensagem())) {
				return pendenciaVO;
			}
		}
		return null;
	}
	
	protected AghWFTemplateEtapaDAO getAghWFTemplateEtapaDAO() {
		return aghWFTemplateEtapaDAO;
	}
	
	protected AghWFFluxoDAO getAghWFFluxoDAO() {
		return aghWFFluxoDAO;
	}
	
	protected AghWFExecutorDAO getAghWFExecutorDAO() {
		return aghWFExecutorDAO;
	}
	
	protected AghWFHistoricoExecucaoDAO getAghWFHistoricoExecucaoDAO() {
		return aghWFHistoricoExecucaoDAO;
	}
	
	protected AghWFEtapaDAO getAghWFEtapaDAO() {
		return aghWFEtapaDAO;
	}
	
	protected AghWFRotaDAO getAghWFRotaDAO () {
		return aghWFRotaDAO;
	}
	
	protected ICentralPendenciaFacade getCentralPendenciaFacade() {
		return this.iCentralPendenciaFacade;
	}

}
