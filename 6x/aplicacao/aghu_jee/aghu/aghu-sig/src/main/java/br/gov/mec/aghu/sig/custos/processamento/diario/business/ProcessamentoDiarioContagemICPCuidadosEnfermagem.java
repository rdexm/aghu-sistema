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
import br.gov.mec.aghu.sig.custos.vo.CuidadosEnfermagemVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #27848 - Contagem de cuidados de enfermagem por unidade de internação, especialidade e equipe
 * Processo chamado pela classe {@link ProcessamentoDiarioContagemICP}
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPCuidadosEnfermagem extends ProcessamentoDiarioContagemBusiness {

	private static final long serialVersionUID = -3417876737590651165L;

	/**
	 * Método responsável por disparar a execução da contagem de cuidados
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto processamento atual
	 * @param servidor servidor utilizado pelo processamento  
	 * @param sigProcessamentoPassos passo atual
	 * @param atendimento atendimento do paciente
	 * @throws ApplicationBusinessException exceção que pode ser lançada ao fazer o commit dos dados
	 */
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {

		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();

		//Passo 1
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.CE);
		
		//Passo 2 (ordenado agora pela data de início para otimizar o processamento)
		List<CuidadosEnfermagemVO> cuidados = this.getProcessamentoCustoUtils().getSigObjetoCustoPhisDAO().buscarCuidadosEnfermagem(atendimento.getSeq(), sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());

		if (cuidados != null) {

			AinMovimentosInternacao movimentoArmazenado = null, ultimoMovimentoInternacao = null;
			SigObjetoCustoVersoes objetoCustoVersoes = null;
			FatProcedHospInternos phi = null;
			SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null , permanenciaEspecialidade = null , permanenciaEquipe = null;
			BigDecimal quantidadeCuidados = null;
			FccCentroCustos centroCusto= null;
			Map<String, Long> cacheAprazamentos = new HashMap<String, Long>();
			Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
			Map<Integer, SigObjetoCustoVersoes> cacheOcvs = new HashMap<Integer, SigObjetoCustoVersoes>();
			
			//Passo 3
			for (CuidadosEnfermagemVO vo : cuidados) {

				//Passo 4
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(vo.getDataHoraInicio(), movimentosInternacoes);

				if(ultimoMovimentoInternacao != null){
					
					//Somente buscar de novo quando mudar o movimento, caso contrário será a mesma permanência e o mesmo consumo ainda
					if(movimentoArmazenado == null || !movimentoArmazenado.getSeq().equals(ultimoMovimentoInternacao.getSeq())){
						
						movimentoArmazenado = ultimoMovimentoInternacao;
						
						centroCusto = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
						
						//Passo 5
						permanenciaUnidadeInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, centroCusto, sigProcessamentoCusto);
		
						//Passo 6 
						permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternacao.getEspecialidade(), sigProcessamentoCusto);
		
						//Passo 7 
						permanenciaEquipe = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternacao.getServidor(), sigProcessamentoCusto);
						
						//Passo 12 (Só quando mudar o movimento é que deveria gravar log)
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTD_CUIDADOS_ENFERMAGEM_NO_CENTRO_CUSTO", atendimento.getSeq(), centroCusto.getDescricao());
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTD_CUIDADOS_ENFERMAGEM_NA_ESPECIALIDADE", atendimento.getSeq(), ultimoMovimentoInternacao.getEspecialidade().getNomeEspecialidade());
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTD_CUIDADOS_ENFERMAGEM_NA_EQUIPE", atendimento.getSeq(),ultimoMovimentoInternacao.getServidor().getPessoaFisica().getNome());
					}
					
					//Passo 8 
					quantidadeCuidados = this.calcularQuantidadeVezesCuidadoExecutado(vo, cacheAprazamentos);
					
					if(vo.getOcvSeq() != null){
						objetoCustoVersoes = this.buscarOcvPorChavePrimaria(vo.getOcvSeq(),cacheOcvs);
						phi = null;
					}
					else {
						phi =  this.buscarPhiPorChavePrimaria(vo.getPhiSeq(), cachePhis);
						objetoCustoVersoes = null;
					}
					
					//Quando mudar o phi, tem que buscar novamente os cálculo de consumo ligados ou ao ocv, ou ao phi
					
					//Passo 9 ao 11
					this.manterCalculoConsumo(permanenciaUnidadeInternacao, objetoCustoVersoes, phi, quantidadeCuidados, centroCusto, rapServidores,  categoriaConsumo);
					
					//Passo 13
					this.manterCalculoConsumo(permanenciaEspecialidade, objetoCustoVersoes, phi, quantidadeCuidados, centroCusto, rapServidores,  categoriaConsumo);
					this.manterCalculoConsumo(permanenciaEquipe, objetoCustoVersoes, phi, quantidadeCuidados, centroCusto, rapServidores,  categoriaConsumo);
					
					this.commitProcessamentoCusto();
				}
			}//Passo 20
		}
	}
	/**
	 * Este método reperesenta a execução dos passos 8 ao 11 do algoritmo descrito no documento de análise
	 * 
	 * @author rogeriovieira
	 * @param objetoCustoVersoes
	 * @param phi
	 * @param numeroCuidados
	 * @param vo
	 * @param ultimoMovimentoInternacao
	 * @param permanencia
	 * @param sigProcessamentoCusto
	 * @param servidor
	 * @param sigProcessamentoPassos
	 * @param atendimento
	 * @throws ApplicationBusinessException
	 */
	private SigCalculoAtdConsumo manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, SigObjetoCustoVersoes objetoCustoVersoes, FatProcedHospInternos phi, BigDecimal quantidadeCuidados, FccCentroCustos centroCusto, RapServidores servidor, SigCategoriaConsumos categoriaConsumo) throws ApplicationBusinessException {

		//Passo 9
		SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumo(permanencia, objetoCustoVersoes, phi);
		
		//Passo 10
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersoes);
			consumo.setProcedHospInterno(phi);
			consumo.setCentroCustos(centroCusto); 
			consumo.setQtde(quantidadeCuidados);
			consumo.setRapServidores(servidor);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
		}
		//Passo 11
		else {
			consumo.setQtde(consumo.getQtde().add(quantidadeCuidados));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
		return consumo;
	}
	/**
	 * Este metódo representa a regra de negócio RN01, chamada no Passo 7
	 * 
	 * @author rogeriovieira
	 * @param vo
	 * @return
	 */
	private BigDecimal calcularQuantidadeVezesCuidadoExecutado(CuidadosEnfermagemVO vo, Map<String, Long> aprazamentos) {
		Long calculoNumeroVezesAprazamento = this.calcularNumeroVezesAprazamento(vo.getTfqSeq(), vo.getFrequencia(), aprazamentos);
		Double diferencaEntreDatas = ProcessamentoCustoUtils.calcularEmDiasDiferencaEntreDatas(vo.getDataHoraInicio(), vo.getDataHoraFim());
		return new BigDecimal(Math.round(calculoNumeroVezesAprazamento * diferencaEntreDatas) * vo.getQuantidadeCuidados());
	}
}
