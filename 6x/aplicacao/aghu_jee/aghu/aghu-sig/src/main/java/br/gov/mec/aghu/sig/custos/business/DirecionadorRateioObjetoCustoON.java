package br.gov.mec.aghu.sig.custos.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoDirRateiosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DirecionadorRateioObjetoCustoON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(DirecionadorRateioObjetoCustoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigObjetoCustoDirRateiosDAO sigObjetoCustoDirRateiosDAO;

	private static final long serialVersionUID = -982390234092343453L;

	public enum DirecionadorRateioObjetoCustoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DIRECIONADOR_JA_ASSOCIADO_OBJETO_CUSTO,
		MENSAGEM_SOMA_PERCENTUAL_ERRADA, ERRO_DIRECIONADOR_POSSUI_CLIENTES_ATIVOS
	}
	
	public void validarSomaPercentuaisDirecionadoresRateio(DominioSituacaoVersoesCustos situacaoObjetoCusto, List<SigObjetoCustoDirRateios> lista) throws ApplicationBusinessException{
		
		boolean dispararMensagemErro = false;
		Double soma = 0.0;
		if(!lista.isEmpty()){
			
			boolean encontrouElementoAtivo = false;
			
			for(SigObjetoCustoDirRateios objetoCustoDirRateio : lista){
				//Somente entra no calculo se estiver ativo
				if(objetoCustoDirRateio.getSituacao().equals(DominioSituacao.A)){
					soma += objetoCustoDirRateio.getPercentual().doubleValue();
					encontrouElementoAtivo = true;
				}
			}	
			
			if(soma != 100.00){
				//Se existir somente direcionadores inativos e a versão não estiver ativa
				if(!encontrouElementoAtivo && !situacaoObjetoCusto.equals(DominioSituacaoVersoesCustos.A)){
					dispararMensagemErro = false;//Não dispara a mensagem de erro
				}
				else{
					dispararMensagemErro = true;
				}
			}
		}
		
		if(dispararMensagemErro){
			throw new ApplicationBusinessException(DirecionadorRateioObjetoCustoONExceptionCode.MENSAGEM_SOMA_PERCENTUAL_ERRADA, soma); 
		}
	}
	
	public void persistirListaDirecionadorRateioObjetoCusto(List<SigObjetoCustoDirRateios> lista, List<SigObjetoCustoDirRateios> excluidos, SigObjetoCustoVersoes objetoCustoVersao){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		for(SigObjetoCustoDirRateios objetoCustoDirRateio : excluidos){
			if(objetoCustoDirRateio.getSeq() != null){
				objetoCustoDirRateio = this.getSigObjetoCustoDirRateiosDAO().merge(objetoCustoDirRateio);
				this.getSigObjetoCustoDirRateiosDAO().remover(objetoCustoDirRateio);
				this.getSigObjetoCustoDirRateiosDAO().flush();
			}
		}
		for(SigObjetoCustoDirRateios objetoCustoDirRateio : lista){
			
			objetoCustoDirRateio.setServidor(servidorLogado);
			
			if(objetoCustoDirRateio.getSeq() == null){
				
				objetoCustoDirRateio.setCriadoEm(new Date());
				objetoCustoDirRateio.setObjetoCustoVersoes(objetoCustoVersao);
				
				this.getSigObjetoCustoDirRateiosDAO().persistir(objetoCustoDirRateio);
			}
			else{
				this.getSigObjetoCustoDirRateiosDAO().atualizar(objetoCustoDirRateio);
			}
		}
		
	}
	
	//RN01 - Chave única do direcionador 
	public void validarAlteracaoListaDirecionadorRateioObjetoCusto(List<SigObjetoCustoDirRateios> lista,
			SigObjetoCustoDirRateios objetoCustoDirRateio, Integer posicaoobjetoCustoDirRateio,
			List<SigObjetoCustoClientes> listaClientes) throws ApplicationBusinessException {
		
		boolean inserirNovo = (posicaoobjetoCustoDirRateio == null);
		
		//Percorre a lista
		for(int indice = 0; indice < lista.size(); indice++ ){
			
			//Se encontra o mesmo direcionador
			if(lista.get(indice).getDirecionadores().getSeq().intValue() == objetoCustoDirRateio.getDirecionadores().getSeq().intValue()){	
				
				//Verifica se está inserindo um novo, ou se a posição é diferente para mostrar a mensagem de erro
				if(inserirNovo || indice != posicaoobjetoCustoDirRateio){
					throw new ApplicationBusinessException(DirecionadorRateioObjetoCustoONExceptionCode.MENSAGEM_DIRECIONADOR_JA_ASSOCIADO_OBJETO_CUSTO, objetoCustoDirRateio.getDirecionadores().getNome()); 
				}
			}
		}
		
		if(!inserirNovo && DominioSituacao.I.equals(objetoCustoDirRateio.getSituacao())
				&& listaClientes != null && !listaClientes.isEmpty()) {
			for(SigObjetoCustoClientes cliente : listaClientes) {
				if(cliente.getDirecionadores().getSeq().equals(objetoCustoDirRateio.getDirecionadores().getSeq())
						&& DominioSituacao.A.equals(cliente.getSituacao())) {
					throw new ApplicationBusinessException(DirecionadorRateioObjetoCustoONExceptionCode.ERRO_DIRECIONADOR_POSSUI_CLIENTES_ATIVOS);
				}
			}
		}
		
		//Determina qual objeto deve ser alterado 
		SigObjetoCustoDirRateios objetoAlterado = null;
		if(inserirNovo){
			objetoAlterado = new SigObjetoCustoDirRateios();//Novo
		}
		else{
			objetoAlterado = lista.get(posicaoobjetoCustoDirRateio);//Referência
			objetoCustoDirRateio.setEmEdicao(false);//Informa que ele está em edicao
		}
		
		//Carrega os dados do objeto candidato
		objetoAlterado.setEmEdicao(false);
		objetoAlterado.setDirecionadores(objetoCustoDirRateio.getDirecionadores());
		objetoAlterado.setPercentual(objetoCustoDirRateio.getPercentual());
		objetoAlterado.setSituacao(objetoCustoDirRateio.getSituacao());
		
		//Insere na lista se for necessário
		if(inserirNovo){
			lista.add(objetoCustoDirRateio);
		}
	}
	
	//RN02 - Exclusão de direcionadores de rateio de uma versão de objeto de custo ativo
	public boolean validarExclusaoDirecionadorRateio(DominioSituacaoVersoesCustos situacao, Date dataInicio){
		
		//Verificar se a versão do objeto de custo está Ativa
		if(situacao.equals(DominioSituacaoVersoesCustos.A)){
			
			//e sua data de início de vigência é anterior ao mês corrente
			Calendar calendarDataInicial = Calendar.getInstance();
			calendarDataInicial.setTime(dataInicio);
			
			Calendar  calendarDataAtual =  Calendar.getInstance();
			calendarDataAtual.setTime(new Date());
			
			if(calendarDataInicial.get(Calendar.MONTH) < calendarDataAtual.get(Calendar.MONTH) && calendarDataInicial.get(Calendar.YEAR) == calendarDataAtual.get(Calendar.YEAR)){
				return false;
			}
			else if(calendarDataInicial.get(Calendar.MONTH) > calendarDataAtual.get(Calendar.MONTH) && calendarDataInicial.get(Calendar.YEAR) < calendarDataAtual.get(Calendar.YEAR)){
				return false;
			}
		}
		
		return true;
		
	}	
	
	/**
	 * Método que valida se um direcionador já está vinculado a um cliente da versão do objeto de custo
	 * @author rogeriovieira
	 * @param direcionador  direcionador ao qual os clientes podem estar associados
	 * @return true se estiver tudo ok e false se existir algum cliente vinculado ao direcionador
	 */
	public boolean validarExclusaoDirecionadorRateioAssociadoCliente(List<SigObjetoCustoClientes> lista, SigDirecionadores direcionador) {
		
		if(lista != null){
			for (SigObjetoCustoClientes sigObjetoCustoClientes : lista) {
				if(sigObjetoCustoClientes.getDirecionadores().equals(direcionador)){
					return false;
				}
			}
		}
		
		return true;
	}

	public SigObjetoCustoDirRateiosDAO getSigObjetoCustoDirRateiosDAO(){
		return sigObjetoCustoDirRateiosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
