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
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoDetalheConsumo;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigCategoriaRecurso;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoDiarioContagemBusiness;
import br.gov.mec.aghu.sig.custos.vo.OrtesesProtesesesInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #28385 - Contagem de ÓRTESES E PRÓTESES por unidade de internação/especialidade/equipe
 * 
 * Processo chamado pela classe {@link ProcessamentoDiarioContagemICP}
 * 
 * @author rogeriovieira
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPOrtesesProteses extends ProcessamentoDiarioContagemBusiness {

	private static final long serialVersionUID = 2447836045970248246L;

	/**
	 * Método responsável por disparar a execução da contagem de órteses e próteses
	 * 
	 * @author rogeriovieira
	 * @param sigProcessamentoCusto processamento atual
	 * @param rapServidores rapServidores utilizado pelo processamento  
	 * @param sigProcessamentoPassos passo atual
	 * @param calculoAtendimento cálculo do atendimento do paciente
	 * @throws ApplicationBusinessException exceção que pode ser lançada ao fazer o commit dos dados
	 */
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {

		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();

		//Passo 1
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.OP);
		
		SigCategoriaRecurso categoriaRecurso =  categoriasRecursos.get(2);
		
		//Passo 2
		SigObjetoCustoVersoes objetoCustoOrteseProtese = (SigObjetoCustoVersoes) parametros.get(AghuParametrosEnum.P_AGHU_SIG_SEQ_ORTESE_PROTESE);
		SigAtividades atividadeUtilizarOrteseProtesePaciente = (SigAtividades) parametros.get(AghuParametrosEnum.P_AGHU_SIG_SEQ_UTILIZAR_ORTESE_PROTESE_NO_PACIENTE);
		
		//Variáveis que são utilizadas repetidamente dentro do processamento
		Date dataInicioProcessamento = sigProcessamentoCusto.getDataInicio();
		Date dataFimProcessamento = sigProcessamentoCusto.getDataFim();

		//Passo 3 
		List<OrtesesProtesesesInternacaoVO> listaConsultorias = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarOrtesesProtesesPorInternacao(atendimento.getInternacao().getSeq(), dataInicioProcessamento, dataFimProcessamento);
		
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaConsultorias)) {

			//Passo 4 
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "MENSAGEM_DADOS_ORTESE_PROTESE_OBTIDOS_SUCESSO", atendimento.getInternacao().getSeq());
			
			AinMovimentosInternacao movimentoArmazenado = null, ultimoMovimentoInternacao = null;
			SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
			SigCalculoAtdConsumo consumoUnidadeInternacao = null, consumoEspecialidade = null, consumoEquipe = null;
			FccCentroCustos centroCusto = null;
			FatProcedHospInternos phi = null;
			
			Map<String, SigCalculoAtdConsumo> cacheConsumos = new HashMap<String, SigCalculoAtdConsumo>();
			Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumo = new HashMap<String, SigCalculoDetalheConsumo>();

			//Passo 5 
			for (OrtesesProtesesesInternacaoVO vo : listaConsultorias) {

				//Busca o último movimento de internação, que deve estar na primeira posição pois a lista está ordenada desc	
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(vo.getDataUtilizacao(), movimentosInternacoes);

				if(ultimoMovimentoInternacao != null){
					
					//Somente buscar de novo quando mudar o movimento, caso contrário será a mesma permanência
					if(movimentoArmazenado == null || !movimentoArmazenado.getSeq().equals(ultimoMovimentoInternacao.getSeq())){
						movimentoArmazenado = ultimoMovimentoInternacao;
						
						//Passo 6
						permanenciaUnidadeInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto(), sigProcessamentoCusto);
		
						//Passo 7
						permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternacao.getEspecialidade(), sigProcessamentoCusto);
		
						//Passo 8
						permanenciaEquipe = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternacao.getServidor(), sigProcessamentoCusto);
					}
					
					//Centro de custo de onde foi debitado a utilização da órtese ou prótese
					
					if(vo.getCodigoCentroCusto() != null){
						centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCodigoCentroCusto());
					}
					else{
						centroCusto = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
					}
	
					//PHI associado à órtese ou prótese utilizada no paciente internado
					phi = this.getProcessamentoCustoUtils().getFaturamentoFacade().obterProcedimentoHospitalarInterno(vo.getSeqPhi());
	
					//Passo 9
					consumoUnidadeInternacao = this.manterConsumo(permanenciaUnidadeInternacao, rapServidores, objetoCustoOrteseProtese,  centroCusto, categoriaConsumo, cacheConsumos );
					
					//Passo 10 (repete 9 para a especialidade e para a equipe)
					consumoEspecialidade = this.manterConsumo(permanenciaEspecialidade, rapServidores, objetoCustoOrteseProtese, centroCusto, categoriaConsumo, cacheConsumos);
					consumoEquipe = this.manterConsumo(permanenciaEquipe, rapServidores, objetoCustoOrteseProtese, centroCusto, categoriaConsumo, cacheConsumos);
					
					//Passo 11, 12, 13 e 14
					this.manterDetalheConsumo(consumoUnidadeInternacao, permanenciaUnidadeInternacao, phi, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, atividadeUtilizarOrteseProtesePaciente, categoriaRecurso, vo, atendimento.getInternacao(), cacheDetalheConsumo);
	
					//Passo 14 (Repete o passo 11, 12, 13 e 14 para especialidade e equipe)
					this.manterDetalheConsumo(consumoEspecialidade, permanenciaEspecialidade, phi, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, atividadeUtilizarOrteseProtesePaciente, categoriaRecurso, vo, atendimento.getInternacao(), cacheDetalheConsumo);
					this.manterDetalheConsumo(consumoEquipe, permanenciaEquipe, phi, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,atividadeUtilizarOrteseProtesePaciente, categoriaRecurso, vo, atendimento.getInternacao(), cacheDetalheConsumo);
				
					this.commitProcessamentoCusto();
				}
			}
		}
		else{
			//FE04
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "MENSAGEM_SEM_DADOS_ORTESE_PROTESE", atendimento.getSeq());
		}
	}

	/**
	 * Execução dos passos 9. 
	 * Primeiro verifica se existe o consumo. Depois cria se não existir, ou atualiza se já existir
	 * 
	 * @author rogeriovieira
	 * @param rapServidores rapServidores utilizado pelo processamento
	 * @param objetoCustoTratamentoPaciente objeto de custo retornado a partir do parâmetro
	 * @param calculoAtdPermanencia permanencia que pode representar a unidade de internação, a especialidade e a equipe
	 * @param centroCusto  centro de custo do atendimento
	 * @throws ApplicationBusinessException exceção que pode ser lançada ao fazer o commit dos dados
	 * 
	 * @return cálculo de consumos do atendimento, obtido ou gerado
	 */
	private SigCalculoAtdConsumo manterConsumo(SigCalculoAtdPermanencia calculoAtdPermanencia, RapServidores rapServidores,
			SigObjetoCustoVersoes objetoCustoTratamentoPaciente, FccCentroCustos centroCusto, SigCategoriaConsumos categoriaConsumo, Map<String, SigCalculoAtdConsumo> cacheConsumos)
			throws ApplicationBusinessException {

		String chave = calculoAtdPermanencia.getSeq()+"-"+centroCusto.getCodigo();
		if(!cacheConsumos.containsKey(chave)){
			cacheConsumos.put(chave,  this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarCalculoAtendimentoConsumo(calculoAtdPermanencia, objetoCustoTratamentoPaciente, centroCusto));
		}		
		SigCalculoAtdConsumo consumo = cacheConsumos.get(chave);

		//Passo 9
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(calculoAtdPermanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoTratamentoPaciente);
			consumo.setCentroCustos(centroCusto);
			consumo.setQtde(BigDecimal.ONE);
			consumo.setRapServidores(rapServidores);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
			cacheConsumos.put(chave, consumo);
		}
		else{
			consumo.setQtde(consumo.getQtde().add(BigDecimal.ONE));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
		return consumo;
	}

	/**
	 * Execução do passo 11, 12, 13, 14
	 * Insere/Atualiza o detalhe de consumo para consumo do passo 8 e grava log
	 * 
	 * @author rogeriovieira
	 * @param consumo cálculo do consumo de atendimento
	 * @param phi procedimento hospitalar interno
	 * @param sigProcessamentoCusto processamento atual
	 * @param rapServidores rapServidores utilizado pelo processamento
	 * @param sigProcessamentoPassos passo atual do processamento
	 * @param atividadeUtilizarOrteseProtesePaciente atividade a qual o detalhe deve ser vinculado
	 * @param categoriaRecurso categoria de recursos que indica insumos
	 * @param phi  phi associado à órtese ou prótese 
	 * @param vo VO que representa o registro retornado na busca de orteses e proteses
	 * @param internacao internação referente ao atendimento
	 * @throws ApplicationBusinessException
	 */
	private void manterDetalheConsumo(SigCalculoAtdConsumo consumo, SigCalculoAtdPermanencia permanencia, FatProcedHospInternos phi, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, SigAtividades atividadeUtilizarOrteseProtesePaciente, SigCategoriaRecurso categoriaRecurso,
			OrtesesProtesesesInternacaoVO vo, AinInternacao internacao, Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumo) throws ApplicationBusinessException {

		//Passo 11 
		String chave = consumo.getSeq()+"-"+phi.getSeq();
		if(!cacheDetalheConsumo.containsKey(chave)){
			cacheDetalheConsumo.put(chave, this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().buscarItemConsumo(consumo, phi)); 
		}
		
		SigCalculoDetalheConsumo detalheConsumo = cacheDetalheConsumo.get(chave);
		//Passo 12
		if (detalheConsumo == null) {
			detalheConsumo = new SigCalculoDetalheConsumo();
			detalheConsumo.setCalculoAtividadeConsumo(consumo);
			detalheConsumo.setAtividade(atividadeUtilizarOrteseProtesePaciente);
			detalheConsumo.setProcedHospInterno(phi);
			detalheConsumo.setQtdePrevisto(BigDecimal.ONE);
			detalheConsumo.setQtdeDebitado(BigDecimal.ONE);
			detalheConsumo.setQtdeConsumido(BigDecimal.ONE);
			detalheConsumo.setIdentificador(vo.getRmpSeq().toString());
			detalheConsumo.setScoMaterial(this.getProcessamentoCustoUtils().getComprasFacade().obterScoMaterialPorChavePrimaria(vo.getMatCodigo()));
			detalheConsumo.setCategoriaRecurso(categoriaRecurso);
			detalheConsumo.setRapServidores(rapServidores);
			detalheConsumo.setCriadoEm(new Date());
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().persistir(detalheConsumo);
			cacheDetalheConsumo.put(chave, detalheConsumo);
		}
		//Passo 13
		else {
			detalheConsumo.setQtdePrevisto(detalheConsumo.getQtdePrevisto().add(BigDecimal.ONE));
			detalheConsumo.setQtdeDebitado(detalheConsumo.getQtdeDebitado().add(BigDecimal.ONE));
			detalheConsumo.setQtdeConsumido(detalheConsumo.getQtdeConsumido().add(BigDecimal.ONE));
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		}
		//Passo 14
		if (consumo.getCalculoAtividadePermanencia().getCentroCustos() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C,  "MENSAGEM_DADOS_UNIDADE_INTERNACAO_ORTESE_PROTESE_ATUALIZADO", vo.getRmpSeq(), internacao.getSeq(), permanencia.getCentroCustos().getDescricao());
		} else if (consumo.getCalculoAtividadePermanencia().getEspecialidade() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "MENSAGEM_DADOS_ESPECIALIDADE_ORTESE_PROTESE_ATUALIZADO", vo.getRmpSeq(), internacao.getSeq(), permanencia.getEspecialidade().getNomeEspecialidade());
		} else if (consumo.getCalculoAtividadePermanencia().getResponsavel() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "MENSAGEM_DADOS_EQUIPE_ORTESE_PROTESE_ATUALIZADO", vo.getRmpSeq(), internacao.getSeq(), permanencia.getResponsavel().getPessoaFisica().getNome());
		}	
	}
}
