package br.gov.mec.aghu.sig.custos.processamento.diario.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoDiarioContagemBusiness;
import br.gov.mec.aghu.sig.custos.vo.ProcedimentoEspecialContagemVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #28397 - Contagem de PROCEDIMENTOS ESPECIAIS por unidade de internação, especialidade e equipe
 * 
 * Processo chamado pela classe {@link ProcessamentoDiarioContagemICP}
 * 
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPProcedimentosEspeciais   extends ProcessamentoDiarioContagemBusiness{

	private static final long serialVersionUID = 2447836045970248246L;

	/**
	 * Método responsável por disparar a execução da contagem de procedimentos especiais
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto processamento atual
	 * @param servidor servidor utilizado pelo processamento  
	 * @param sigProcessamentoPassos passo atual
	 * @param calculoAtendimento cálculo do atendimento do paciente
	 * @throws ApplicationBusinessException exceção que pode ser lançada ao fazer o commit dos dados
	 */
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {

		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();
		
		//Passo 1
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.PE);
		
		//Passo 2 (Parte 1)
		List<ProcedimentoEspecialContagemVO> listaProcedimentosEspeciais = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarProcedimentosEspeciaisDiversos(atendimento.getSeq(),  sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());
		this.processarProcedimentosEspeciais(true, listaProcedimentosEspeciais, "MENSAGEM_DADOS_PROCEDIMENTOS_ESPECIAIS_DIVERSOS_OBTIDOS_SUCESSO", categoriaConsumo, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, atendimento, movimentosInternacoes);
		
		//Passo 2 (Parte 2)
		List<ProcedimentoEspecialContagemVO> listaProcedimentosEspeciaisCirurgicos = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarProcedimentosEspeciaisCirurgicos(atendimento.getSeq(),  sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());
		this.processarProcedimentosEspeciais(false, listaProcedimentosEspeciaisCirurgicos, "MENSAGEM_DADOS_PROCEDIMENTOS_ESPECIAIS_CIRURGICOS_OBTIDOS_SUCESSO", categoriaConsumo, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, atendimento, movimentosInternacoes);
	}
	
	/**
	 * Execução do passos 3 até o passo 12
	 * 
	 * @author rogeriovieira
	 * @param listaProcedimentosEspeciais lista retornada no passo 1
	 * @param processandoParte1 informa se está executando a primeira ou a segunda parte
	 * @param sigProcessamentoCusto processamento atual
	 * @param servidor servidor utilizado pelo processamento  
	 * @param sigProcessamentoPassos passo atual
	 * @param atendimento atendimento da internação
	 * @throws ApplicationBusinessException  exceção que pode ser lançada ao fazer o commit dos dados
	 */
	private void processarProcedimentosEspeciais(Boolean processandoParte1, List<ProcedimentoEspecialContagemVO> listaProcedimentosEspeciais, String chaveMensagemPasso3, SigCategoriaConsumos categoriaConsumo, SigProcessamentoCusto sigProcessamentoCusto, RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, AghAtendimentos atendimento, List<AinMovimentosInternacao> movimentosInternacoes) throws ApplicationBusinessException{
		
		//Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, chaveMensagemPasso3, atendimento.getSeq());
		
		if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaProcedimentosEspeciais)){

			AinMovimentosInternacao movimentoArmazenado = null, ultimoMovimentoInternacao = null;
			SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
			SigObjetoCustoVersoes  objetoCustoVersao= null;
			FatProcedHospInternos phi = null;
			FccCentroCustos centroCusto = null;
			String valorIdentificador = null;
			
			Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
			Map<Integer, SigObjetoCustoVersoes> cacheOcvs= new HashMap<Integer, SigObjetoCustoVersoes>();
			Map<String, SigCalculoAtdConsumo> cacheConsumo = new HashMap<String, SigCalculoAtdConsumo>();
			
			//Passo 4
			for(ProcedimentoEspecialContagemVO vo : listaProcedimentosEspeciais){
				
				//Na parte 1 tem que executar uma validação antes
				if(processandoParte1){
					
					//Verifica se o procedimento possui material associado através da consulta
					List<MpmProcedEspecialRm> listaProcedimentosEspeciaisRm = this.getProcessamentoCustoUtils().getPrescricaoMedicaFacade().listarProcedimentosRmAtivosPeloPedSeq(vo.getPedSeq());
					
					//Se possuir esse procedimento não deverá ser contabilizado
					if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaProcedimentosEspeciaisRm)){
						continue;
					}
				}
				
				//Passo 5
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(vo.getPmeDthrInicio(), movimentosInternacoes);
					
				if(ultimoMovimentoInternacao != null){
					
					//Somente buscar de novo quando mudar o movimento, caso contrário será a mesma permanência
					if(movimentoArmazenado == null || !movimentoArmazenado.getSeq().equals(ultimoMovimentoInternacao.getSeq())){
						movimentoArmazenado = ultimoMovimentoInternacao;
					
						centroCusto = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
						//Passo 6
						permanenciaUnidadeInternacao =  this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto(), sigProcessamentoCusto);
				
						//Passo 7
						permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternacao.getEspecialidade(), sigProcessamentoCusto);
						
						//Passo 8
						permanenciaEquipe =  this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternacao.getServidor(), sigProcessamentoCusto);
					}
					
					if(vo.getOcvSeq() != null){
						objetoCustoVersao = this.buscarOcvPorChavePrimaria(vo.getOcvSeq(), cacheOcvs);
						phi = null;
					}else{
						phi = this.buscarPhiPorChavePrimaria(vo.getPhiSeq(), cachePhis); 
						objetoCustoVersao = null;
					}
					
					valorIdentificador = processandoParte1 ? vo.getPedSeq().toString() : vo.getMpcSeq().toString();
	
					//Passo 9, 10, 11, 12 
					this.manterConsumo(permanenciaUnidadeInternacao, valorIdentificador, sigProcessamentoCusto, servidor, sigProcessamentoPassos, objetoCustoVersao, phi, centroCusto, atendimento, categoriaConsumo, cacheConsumo);
					
					//Passo 13 (repete 9, 10, 11, 12 para a especialidade e para a equipe)
					this.manterConsumo(permanenciaEspecialidade, valorIdentificador, sigProcessamentoCusto, servidor, sigProcessamentoPassos, objetoCustoVersao, phi, centroCusto, atendimento, categoriaConsumo, cacheConsumo);
					this.manterConsumo(permanenciaEquipe, valorIdentificador, sigProcessamentoCusto, servidor, sigProcessamentoPassos,objetoCustoVersao, phi, centroCusto, atendimento, categoriaConsumo, cacheConsumo);
				}
			}
		}
	}
	
	/**
	 * Execução dos passos 9, 10, 11 e 12. 
	 * Primeiro verifica se existe o consumo. Depois cria se não existir, ou atualiza se já existir. E no final grava o log
	 * 
	 * @author rogeriovieira
	 * @param permanencia permanencia que pode representar a unidade de internação, a especialidade e a equipe
	 * @param sigProcessamentoCusto processamento atual
	 * @param servidor servidor utilizado pelo processamento  
	 * @param sigProcessamentoPassos passo atual
	 * @param objetoCustoVersao objeto de custo retornado a partir do parâmetro
	 * @param phi procedimento hospital interno
	 * @param vo dados da consultoria médica
	 
	 * @param centroCusto  centro de custo do atendimento
	 * @param atendimento atendimento da internação
	 * @throws ApplicationBusinessException exceção que pode ser lançada ao fazer o commit dos dados
	 */
	private void manterConsumo(SigCalculoAtdPermanencia permanencia,  String valorIdentificador, SigProcessamentoCusto sigProcessamentoCusto,
			RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, SigObjetoCustoVersoes objetoCustoVersao, FatProcedHospInternos phi,
			FccCentroCustos centroCusto, AghAtendimentos atendimento, SigCategoriaConsumos categoriaConsumo, Map<String, SigCalculoAtdConsumo> cacheConsumo) throws ApplicationBusinessException {

		//Passo 9		
		String chave = permanencia.getSeq()+"-"+(objetoCustoVersao != null ? objetoCustoVersao.getSeq() : 0 )+"-"+(phi != null ? phi.getSeq() : 0);
		if(!cacheConsumo.containsKey(chave)){
			cacheConsumo.put(chave,  this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumo(permanencia, objetoCustoVersao, phi));
		}
		SigCalculoAtdConsumo consumo = cacheConsumo.get(chave);
		
		//Passo 10
		if(consumo == null){
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setProcedHospInterno(phi);
			consumo.setCentroCustos(centroCusto);
			consumo.setQtde(BigDecimal.ONE);
			consumo.setRapServidores(servidor);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
			cacheConsumo.put(chave, consumo);
		}
		//Passo 11
		else{
			consumo.setQtde(consumo.getQtde().add(BigDecimal.ONE));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}		
				
		//Passo 12
		if (consumo.getCalculoAtividadePermanencia().getCentroCustos() != null) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_DADOS_UNIDADE_INTERNACAO_PROCEDIMENTO_ESPECIAL_ATUALIZADO", valorIdentificador, atendimento.getSeq(), permanencia.getCentroCustos().getDescricao());
		} else if (consumo.getCalculoAtividadePermanencia().getEspecialidade() != null) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_DADOS_ESPECIALIDADE_PROCEDIMENTO_ESPECIAL_ATUALIZADO", valorIdentificador, atendimento.getSeq(), permanencia.getEspecialidade().getNomeEspecialidade());
		} else if (consumo.getCalculoAtividadePermanencia().getResponsavel() != null) {
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_DADOS_EQUIPE_PROCEDIMENTO_ESPECIAL_ATUALIZADO", valorIdentificador, atendimento.getSeq(), permanencia.getResponsavel().getPessoaFisica().getNome());
		}
	}
}
