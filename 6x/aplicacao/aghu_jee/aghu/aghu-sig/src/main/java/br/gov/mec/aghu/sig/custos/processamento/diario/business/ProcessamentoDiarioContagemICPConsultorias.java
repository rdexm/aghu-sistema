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
import br.gov.mec.aghu.model.FccCentroCustos;
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
import br.gov.mec.aghu.sig.custos.vo.ConsultoriaMedicaEspecialidadeVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #28384 - Contagem de CONSULTORIAS por unidade de internação, especialidade e equipe
 * 
 * Processo chamado pela classe {@link ProcessamentoDiarioContagemICP}
 * 
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPConsultorias  extends ProcessamentoDiarioContagemBusiness {

	private static final long serialVersionUID = 2447836045970248246L;

	/**
	 * Método responsável por disparar a execução da contagem de consultorias
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
		SigCategoriaConsumos categoriaConsumo =categoriasConsumos.get(DominioIndContagem.CS);
		
		//Passo 2
		SigObjetoCustoVersoes objetoCustoConsultoriaMedica = (SigObjetoCustoVersoes) parametros.get(AghuParametrosEnum.P_AGHU_SIG_SEQ_CONSULTORIAS_MEDICAS);
		
		//Passo 3
		List<ConsultoriaMedicaEspecialidadeVO> listaConsultorias = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarConsultoriasMedicasPorAtendimento(atendimento.getSeq(), sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());
		
		//Passo 4
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_DADOS_CONSULTORIA_OBTIDOS_SUCESSO", atendimento.getSeq());
		
		if(ProcessamentoCustoUtils.verificarListaNaoVazia( listaConsultorias)){
			
			AinMovimentosInternacao movimentoArmazenado = null, ultimoMovimentoInternacao = null;
			SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
			FccCentroCustos centroCusto;
			BigDecimal quantidadeConsultorias;
			
			Map<String, SigCalculoAtdConsumo> cacheConsumos = new HashMap<String, SigCalculoAtdConsumo>();
			
			//Passo 5
			for(ConsultoriaMedicaEspecialidadeVO vo : listaConsultorias){
				
				//Passo 6
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(vo.getDataSolicitacao(), movimentosInternacoes);

				if(ultimoMovimentoInternacao != null){
					
					//Somente buscar de novo quando mudar o movimento, caso contrário será a mesma permanência
					if(movimentoArmazenado == null || !movimentoArmazenado.getSeq().equals(ultimoMovimentoInternacao.getSeq())){
						movimentoArmazenado = ultimoMovimentoInternacao;

						//Passo 7
						permanenciaUnidadeInternacao =  this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto(), sigProcessamentoCusto);
				
						//Passo 8
						permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternacao.getEspecialidade(),  sigProcessamentoCusto );
						
						//Passo 9
						permanenciaEquipe =  this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternacao.getServidor(), sigProcessamentoCusto);
					}
					
					//Centro de custo associado à especialidade que respondeu a consultoria para o paciente
					centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCodigoCentroCusto());
	
					quantidadeConsultorias = new BigDecimal(vo.getQtdeConsultorias());
					
					//Passo 10, 11 e 12 
					this.manterCalculoConsumo(permanenciaUnidadeInternacao, rapServidores, objetoCustoConsultoriaMedica, quantidadeConsultorias, centroCusto, categoriaConsumo, cacheConsumos);
					
					//Passo 13 (repete 10, 11 e 12 para a especialidade e para a equipe
					this.manterCalculoConsumo(permanenciaEspecialidade, rapServidores, objetoCustoConsultoriaMedica, quantidadeConsultorias,  centroCusto, categoriaConsumo, cacheConsumos);
					this.manterCalculoConsumo(permanenciaEquipe, rapServidores, objetoCustoConsultoriaMedica, quantidadeConsultorias,  centroCusto, categoriaConsumo, cacheConsumos);
					
					//Passo 14
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_DADOS_CONSULTORIA_ATUALIZADO", vo.getNomeEspecialidadaConsultoria(), atendimento.getSeq(), centroCusto.getDescricao() );
				
					this.commitProcessamentoCusto();
				}
			}//Passo 15
		}
		else{
			//FE03
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SEM_DADOS_CONSULTORIA", atendimento.getSeq() );
		}
	}

	/**
	 * Execução dos passos 10, 11 e 12. 
	 * Primeiro verifica se existe o consumo. Depois cria se não existir, ou atualiza se já existir
	 * 
	 * @author rogeriovieira
	 * @param servidor servidor utilizado pelo processamento
	 * @param objetoCustoConsultoriaMedica objeto de custo retornado a partir do parâmetro
	 * @param vo dados da consultoria médica
	 * @param permanencia permanencia que pode representar a unidade de internação, a especialidade e a equipe
	 * @param centroCusto  centro de custo do atendimento
	 * @throws ApplicationBusinessException exceção que pode ser lançada ao fazer o commit dos dados
	 */
	private void manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, RapServidores servidor, SigObjetoCustoVersoes objetoCustoConsultoriaMedica,
			BigDecimal quantidadeConsultorias, FccCentroCustos centroCusto, SigCategoriaConsumos categoriaConsumo, Map<String, SigCalculoAtdConsumo> cacheConsumos) throws ApplicationBusinessException{
		
		//Passo 10
		String chave = permanencia.getSeq()+"-"+centroCusto.getCodigo();
		if(!cacheConsumos.containsKey(chave)){
			cacheConsumos.put(chave, this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarCalculoAtendimentoConsumo(permanencia, objetoCustoConsultoriaMedica, centroCusto));
		}
		SigCalculoAtdConsumo consumo = cacheConsumos.get(chave);
		
		//Passo 11
		if(consumo != null){
			consumo.setQtde(consumo.getQtde().add(quantidadeConsultorias));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
		//Passo 12
		else{
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoConsultoriaMedica);
			consumo.setCentroCustos(centroCusto);
			consumo.setQtde(quantidadeConsultorias);
			consumo.setRapServidores(servidor);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
			cacheConsumos.put(chave, consumo);
		}
	}
}
