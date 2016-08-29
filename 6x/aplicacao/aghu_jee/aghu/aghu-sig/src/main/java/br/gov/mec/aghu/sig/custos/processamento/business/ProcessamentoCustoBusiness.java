package br.gov.mec.aghu.sig.custos.processamento.business;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCustoLog;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@TransactionManagement(TransactionManagementType.BEAN)
public abstract class ProcessamentoCustoBusiness extends ProcessamentoCustoBMTBusiness {

	private static final long serialVersionUID = 3741920517718553668L;
	
	@EJB
	private ProcessamentoCustoUtils processamentoMensalUtils;	
	
	
	private void criarProcessamentoCustoLog(SigProcessamentoCusto processamentoAtual, RapServidores servidor, String descricao,
			DominioEtapaProcessamento etapaProcessamento, SigProcessamentoPassos sigProcessamentoPassos) {
		SigProcessamentoCustoLog processamentoCustoLog = new SigProcessamentoCustoLog();
		processamentoCustoLog.setDescricao(descricao);
		processamentoCustoLog.setEtapa(etapaProcessamento);
		processamentoCustoLog.setCriadoEm(new Date());
		processamentoCustoLog.setRapServidores(servidor);
		processamentoCustoLog.setSigProcessamentoCustos(processamentoAtual);
		processamentoCustoLog.setSigProcessamentoPassos(sigProcessamentoPassos);
		this.getProcessamentoCustoUtils().getSigProcessamentoCustoLogDAO().persistir(processamentoCustoLog);
	}
	
	/**
	 * Loga a descrição para a etapa, informações passadas por parametro, sem realizar o commit do processamento.
	 * 
	 * @author rmalvezzi
	 * @param processamentoAtual			Processamento Atual.
	 * @param servidor						Servidor logado.
	 * @param descricao						Descrição a ser logada.
	 * @param etapaProcessamento			Enum que representa o dominio da etapa.
	 * @param sigProcessamentoPassos		Processamento Passo atual.
	 */
	private void gravarLogProcessamentoSemCommit(SigProcessamentoCusto processamentoAtual, RapServidores servidor, String descricao,
			DominioEtapaProcessamento etapaProcessamento, SigProcessamentoPassos sigProcessamentoPassos) {
		//TODO Não gravar log normais durante os testes, na tentativa de diminuir o tempo de execução
		//this.criarProcessamentoCustoLog(processamentoAtual, servidor, descricao, etapaProcessamento, sigProcessamentoPassos);
	}

	/**
	 * Loga a descrição para a etapa, informações passadas por parametro, e ainda realiza o commit do processamento.
	 * 
	 * @author rmalvezzi
	 * @param processamentoAtual					Processamento Atual.
	 * @param servidor								Servidor logado.
	 * @param descricao								Descrição a ser logada.
	 * @param etapaProcessamento					Enum que representa o dominio da etapa.
	 * @param sigProcessamentoPassos				Processamento Passo atual.
	 * @throws ApplicationBusinessException		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	private void gravarLogProcessamento(SigProcessamentoCusto processamentoAtual, RapServidores servidor, String descricao,
			DominioEtapaProcessamento etapaProcessamento, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		//TODO Não gravar log normais durante os testes, na tentativa de diminuir o tempo de execução
		//this.criarProcessamentoCustoLog(processamentoAtual, servidor, descricao, etapaProcessamento, sigProcessamentoPassos);
		//this.commitProcessamentoCusto();
	}
	
	public void gravarLogDebugProcessamento(String descricao, SigProcessamentoCusto processamentoAtual, RapServidores servidor, 
			DominioEtapaProcessamento etapaProcessamento, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException {
		this.criarProcessamentoCustoLog(processamentoAtual, servidor, descricao, etapaProcessamento, sigProcessamentoPassos);
		this.commitProcessamentoCusto();
	}

	public String buscarMensagem(String chave, Object... parametros){
		return this.getResourceBundleValue(chave, parametros);
	}
	
	public void gravarErrosNoLog(Integer contador, Throwable t, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException{
		contador++;
		if(t != null ){
			if(contador < 5){
				this.gravarErrosNoLog(contador, t.getCause(), sigProcessamentoPassos);
			}
			this.gravarLogDebugProcessamento("Causa: "+t.getMessage(), sigProcessamentoPassos.getSigProcessamentoCusto(), sigProcessamentoPassos.getRapServidores(), DominioEtapaProcessamento.G, sigProcessamentoPassos);
			int pos = 1;
			String mensagem = null;
			if(t.getStackTrace() != null && t.getStackTrace().length > 0) {
				for (StackTraceElement stackTraceElement : t.getStackTrace()) {
					if(stackTraceElement.getClassName().contains("br.gov.mec")){
						mensagem = "Erro " + pos++ + ") Classe: " + stackTraceElement.getClassName() + " - na Linha: " + stackTraceElement.getLineNumber() + " - no Método: " + stackTraceElement.getMethodName();
						this.gravarLogDebugProcessamento(mensagem, sigProcessamentoPassos.getSigProcessamentoCusto(), sigProcessamentoPassos.getRapServidores(), DominioEtapaProcessamento.G, sigProcessamentoPassos);
					}
				}	
			}
		}
	}
	
	/**
	 * Carrega a mensagem presente no arquivo .properties do módulo correspondente a chave passada por parametro e já com a lista de
	 * parametros passado e Loga a descrição para a etapa, informações passadas por parametro, sem realizar o commit do processamento.
	 * 
	 * @author rmalvezzi
	 * @param processamentoAtual			Processamento Atual.
	 * @param servidor						Servidor logado.
	 * @param sigProcessamentoPassos		Processamento Passo atual.
	 * @param etapaProcessamento			Enum que representa o dominio da etapa.
	 * @param chave							Key da mensagem no arquivo.
	 * @param parametros					Lista dos parametros da mensagem.
	 */
	public void buscarMensagemEGravarLogProcessamentoSemCommit(SigProcessamentoCusto processamentoAtual, RapServidores servidor,
			SigProcessamentoPassos sigProcessamentoPassos, DominioEtapaProcessamento etapaProcessamento, String chave, Object... parametros) {
		this.gravarLogProcessamentoSemCommit(processamentoAtual, servidor, this.buscarMensagem(chave, parametros), etapaProcessamento, sigProcessamentoPassos);
	}

	/**
	 * Carrega a mensagem presente no arquivo .properties do módulo correspondente a chave passada por parametro e já com a lista de
	 * parametros passado e Loga a descrição para a etapa, informações passadas por parametro, e ainda realiza o commit do processamento.
	 * 
	 * @author rmalvezzi
	 * @param processamentoAtual					Processamento Atual.
	 * @param servidor								Servidor logado.
	 * @param sigProcessamentoPassos				Processamento Passo atual.
	 * @param etapaProcessamento					Enum que representa o dominio da etapa.
	 * @param chave									Key da mensagem no arquivo.
	 * @param parametros							Lista dos parametros da mensagem.
	 * @throws ApplicationBusinessException		Exceção lançada se algum erro acontecer na hora do commit do processamento.
	 */
	public void buscarMensagemEGravarLogProcessamento(SigProcessamentoCusto processamentoAtual, RapServidores servidor,
			SigProcessamentoPassos sigProcessamentoPassos, DominioEtapaProcessamento etapaProcessamento, String chave, Object... parametros)
			throws ApplicationBusinessException {
		this.gravarLogProcessamento(processamentoAtual, servidor, this.buscarMensagem(chave, parametros), etapaProcessamento, sigProcessamentoPassos);
	}
	
	/**
	 * Este método representa a chamada da função MPMC_NUM_VEZES_APRAZ chamada
	 * na RN01
	 * 
	 * @author rogeriovieira
	 * @param tfqSeq
	 *            Seq do tipo de frenquencia do Aprazamento.
	 * @param frequencia
	 *            A frenquencia.
	 * @return Número de vezes do aprazamento em 24 horas.
	 */
	public Long calcularNumeroVezesAprazamento(Short tfqSeq, Short frequencia) {
		if(tfqSeq != null && frequencia != null){ 
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = this.getProcessamentoCustoUtils().getPrescricaoMedicaFacade().obterTipoFrequeciaAprazPorChavePrimaria(tfqSeq);
			Long calculoNumeroVezesAprazamento24Horas = this.getProcessamentoCustoUtils().getPrescricaoMedicaFacade().calculoNumeroVezesAprazamento24Horas(tipoFrequenciaAprazamento, frequencia);
			if (calculoNumeroVezesAprazamento24Horas == 999 || calculoNumeroVezesAprazamento24Horas == 0L) {
				calculoNumeroVezesAprazamento24Horas = 1L;
			}
			return calculoNumeroVezesAprazamento24Horas;
		}
		return 1L;
	}
	
	public Long calcularNumeroVezesAprazamento (Short tfqSeq, Short frequencia, Map<String, Long> cacheAprazamentos){
		String chave = tfqSeq + "-" + frequencia;
		if(!cacheAprazamentos.containsKey(chave)){
			cacheAprazamentos.put(chave, this.calcularNumeroVezesAprazamento(tfqSeq, frequencia));
		}
		return cacheAprazamentos.get(chave);
	}
	
	public FatProcedHospInternos buscarPhiPorChavePrimaria(Integer seqPhi, Map<Integer, FatProcedHospInternos> cachePhis){
		if(!cachePhis.containsKey(seqPhi)){
			cachePhis.put(seqPhi, this.getProcessamentoCustoUtils().getFaturamentoFacade().obterFatProcedHospInternosPorChavePrimaria(seqPhi));
		}
		return 	cachePhis.get(seqPhi);
	}
	
	public SigObjetoCustoVersoes buscarOcvPorChavePrimaria(Integer seqOcv, Map<Integer, SigObjetoCustoVersoes> cacheOcvs){
		if(!cacheOcvs.containsKey(seqOcv)){
			cacheOcvs.put(seqOcv, this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterPorChavePrimaria(seqOcv));
		}
		return 	cacheOcvs.get(seqOcv);
	}
	
	public SigAtividades buscarAtividadePorChavePrimaria(Integer seqAtividade, Map<Integer, SigAtividades> cacheAtividades){
		if(!cacheAtividades.containsKey(seqAtividade)){
			cacheAtividades.put(seqAtividade, this.getProcessamentoCustoUtils().getSigAtividadesDAO().obterPorChavePrimaria(seqAtividade));
		}
		return 	cacheAtividades.get(seqAtividade);
	}
	
	public ScoMaterial buscarMaterialPorChavePrimaria(Integer codigoMaterial, Map<Integer, ScoMaterial> cacheMateriais){
		if(!cacheMateriais.containsKey(codigoMaterial)){
			cacheMateriais.put(codigoMaterial, this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(codigoMaterial));
		}
		return 	cacheMateriais.get(codigoMaterial);
	}
	
	/**
	 * Busca a categoria de consumo para a contagem (diário/mensal)
	 * @param dominio
	 * @param sigProcessamentoCusto
	 * @param rapServidores
	 * @param sigProcessamentoPassos
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public SigCategoriaConsumos obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem dominio, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos) throws ApplicationBusinessException{
		SigCategoriaConsumos categoriaConsumo = this.getProcessamentoCustoUtils().getSigCategoriaConsumosDAO().obterCategoriaConsumoPorIndicadorContagem(dominio);
		
		if(categoriaConsumo == null){
			//FE01
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.G, "MENSAGEM_CATEGORIA_NAO_CADASTRADA", dominio);
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_BUSCA_CATEGORIA_CONSUMO);
		}
		
		return categoriaConsumo;
	}
	
	/**
	 * Busca os movimentos de internação anteriores a data de lançamento e retorna o primeiro da lista, sendo que esse representa o último movimento do paciente
	 * @param seqInternacao
	 * @param dtHoraLancamento
	 * @return
	 */
	public AinMovimentosInternacao obterUltimoMovimentoInternacao(Integer seqInternacao, Date dtHoraLancamento) {
		List<AinMovimentosInternacao> listaMovimentoMensais = this.getProcessamentoCustoUtils().getInternacaoFacade().buscarMovimentosInternacao(seqInternacao, dtHoraLancamento);
		if(!listaMovimentoMensais.isEmpty()){
			return listaMovimentoMensais.get(0);
		}
		return null;
	}
	
	/**
	 * Busca o movimento a partir da data de lançamento dentre os movimentos da internação no mês da competência
	 * @param seqInternacao
	 * @param dtHoraLancamento
	 * @param movimentosInternacoes
	 * @return
	 */
	public AinMovimentosInternacao obterUltimoMovimentoInternacao(Date dtHoraLancamento, List<AinMovimentosInternacao> movimentosInternacoes) {
		for(AinMovimentosInternacao movimentoInternacao : movimentosInternacoes){
			if(DateUtil.validaDataMenorIgual(movimentoInternacao.getDthrLancamento(), dtHoraLancamento) ){
				return  movimentoInternacao;
			}
		}
		return null;
	}
	
	protected ProcessamentoCustoUtils getProcessamentoCustoUtils(){
		return processamentoMensalUtils;
	}
}