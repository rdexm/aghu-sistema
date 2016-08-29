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
import br.gov.mec.aghu.sig.custos.vo.SolicitacaoHemoterapiaNaInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #28395
 * Classe responsável pelo cálculo das hemoterapias aplicadas em um paciente internado. Tem o objetivo de, diariamente, 
 * verificar quais pacientes tiveram alta e contabilizar a quantidade de hemoterapias realizadas durante sua internação 
 * e também associá-los nas unidades de internação, especialidade e equipes pelos quais o paciente passou.
 * 
 * @author rhrosa
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPHemoterapias extends ProcessamentoDiarioContagemBusiness {

	private static final long serialVersionUID = 7834381533139251799L;

	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {

		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();

		// Passo 1
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.HM);
		
		// Passo 2
		List<SolicitacaoHemoterapiaNaInternacaoVO> listaVOsSolicitacoesHemoterapias = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO()
				.buscarSolicitacoesHemoterapicasRealizadasNaInternacao(atendimento.getSeq(), sigProcessamentoCusto.getDataInicio(),sigProcessamentoCusto.getDataFim());

		// Passo 3 
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaVOsSolicitacoesHemoterapias)) {

			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_CONSULTA_SOLICITACOES_HEMOTERAPICAS", atendimento.getSeq());
			
			AinMovimentosInternacao movimentoArmazenado = null, ultimoMovimentoInternacao = null;
			SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
			SigObjetoCustoVersoes objetoCusto = null;
			FatProcedHospInternos phi = null;
			BigDecimal quantidade = null;
			String codigo = null;
			FccCentroCustos centroCusto = null;
			
			Map<String, SigCalculoAtdConsumo> cacheConsumos = new HashMap<String, SigCalculoAtdConsumo>();
			Map<Integer, SigObjetoCustoVersoes> cacheOcvs = new HashMap<Integer, SigObjetoCustoVersoes>();
			Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
			
			// Passo 14
			for (SolicitacaoHemoterapiaNaInternacaoVO vo : listaVOsSolicitacoesHemoterapias) {
				
				// Passo 4
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(vo.getCriadoEm(), movimentosInternacoes);

				if(ultimoMovimentoInternacao != null){

					//Somente buscar de novo quando mudar o movimento, caso contrário será a mesma permanência
					if(movimentoArmazenado == null || !movimentoArmazenado.getSeq().equals(ultimoMovimentoInternacao.getSeq())){
						movimentoArmazenado = ultimoMovimentoInternacao;
						
						centroCusto = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
						
						// Passo 5
						permanenciaUnidadeInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto(), sigProcessamentoCusto);
	
						// Passo 6
						permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternacao.getEspecialidade(), sigProcessamentoCusto);
						
						// Passo 7
						permanenciaEquipe = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternacao.getServidor(), sigProcessamentoCusto);
					}
					
					// Passo 9
					if (vo.getOcvSeq() != null) {
						objetoCusto = this.buscarOcvPorChavePrimaria(vo.getOcvSeq(), cacheOcvs);
						phi = null;
					} else if (vo.getPhiSeq() != null) {
						phi = this.buscarPhiPorChavePrimaria(vo.getPhiSeq(), cachePhis);
						objetoCusto = null;
					}
					
					if(objetoCusto != null || phi != null){
						
						quantidade = this.calculaQtde(vo);
						codigo = vo.getCsaCodigo() != null ?   vo.getCsaCodigo() : vo.getPheCodigo();
						
						this.manterConsumo(permanenciaUnidadeInternacao, objetoCusto, phi, quantidade, codigo, centroCusto, rapServidores, categoriaConsumo, sigProcessamentoCusto, sigProcessamentoPassos, atendimento, cacheConsumos);
						this.manterConsumo(permanenciaEspecialidade, objetoCusto, phi, quantidade, codigo, centroCusto, rapServidores, categoriaConsumo, sigProcessamentoCusto, sigProcessamentoPassos, atendimento, cacheConsumos);
						this.manterConsumo(permanenciaEquipe, objetoCusto, phi, quantidade, codigo, centroCusto, rapServidores, categoriaConsumo, sigProcessamentoCusto, sigProcessamentoPassos, atendimento, cacheConsumos);
					}

					this.commitProcessamentoCusto();
				}
			}
		} else {
			// FE01
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.D, "MENSAGEM_INSUCESSO_CONSULTA_SOLICITACOES_HEMOTERAPICAS", atendimento.getSeq());
		}
	}
	
	
	private void manterConsumo(SigCalculoAtdPermanencia permanencia, SigObjetoCustoVersoes objetoCusto, FatProcedHospInternos phi, BigDecimal quantidade, String codigo, FccCentroCustos centroCusto,  RapServidores servidor, SigCategoriaConsumos categoriaConsumo, SigProcessamentoCusto sigProcessamentoCusto, SigProcessamentoPassos sigProcessamentoPassos, AghAtendimentos atendimento, Map<String, SigCalculoAtdConsumo> cacheConsumos){
	
		//Passo 9
		String chave = permanencia.getSeq()+"-"+(objetoCusto!= null ? objetoCusto.getSeq():null)+"-"+(phi!= null ? phi.getSeq() : null);
		if(!cacheConsumos.containsKey(chave)){
			cacheConsumos.put(chave, this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumo( permanencia, objetoCusto, phi));
		}
		SigCalculoAtdConsumo consumo = cacheConsumos.get(chave);

		//Passo 10
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCusto);
			consumo.setProcedHospInterno(phi);
			consumo.setCentroCustos(centroCusto);
			consumo.setQtde(quantidade);
			consumo.setRapServidores(servidor);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
			cacheConsumos.put(chave, consumo);		
		} 
		// Passo 11
		else {
			consumo.setQtde(consumo.getQtde().add(quantidade));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
		
		// Passo 12
		if (permanencia.getCentroCustos() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos,DominioEtapaProcessamento.D, "MENSAGEM_HEMOTERAPIA_SUCESSO_ATUALIZACAO_CALCULO_CONSUMO_CENTRO_CUSTO", codigo, atendimento.getSeq());	
		} else if (permanencia.getEspecialidade() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos,DominioEtapaProcessamento.D, "MENSAGEM_HEMOTERAPIA_SUCESSO_ATUALIZACAO_CALCULO_CONSUMO_ESPECIALIDADE", codigo, atendimento.getSeq());	
		} else if (permanencia.getResponsavel() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos,DominioEtapaProcessamento.D, "MENSAGEM_HEMOTERAPIA_SUCESSO_ATUALIZACAO_CALCULO_CONSUMO_EQUIPE", codigo, atendimento.getSeq());	
		}
	}

	/**
	 * Calcula a quantidade para o CalculoAtdConsumo do Componente Sanguineo.
	 * 
	 * @param vo  VO com a solicitacao da hemoterapia	
	 * @return quantidade Quantidade de aplicacoes calculada
	 * @author rhrosa
	 */
	private BigDecimal calculaQtde(SolicitacaoHemoterapiaNaInternacaoVO vo) {
		BigDecimal quantidade;
		
		if (vo.getQtdeAplicacoes() == null) {
			vo.setQtdeAplicacoes(Short.parseShort("1"));
		}
		
		//Verifica se o item é um procedimento hemoterapico ou componente sanguineo
		if (vo.getPheCodigo() != null) {
			quantidade = new BigDecimal(vo.getQtdeAplicacoes());
		} else {
			if(vo.getQtdeMl() == null){
				vo.setQtdeMl(Short.valueOf("1"));
			}
			Integer indicador = (vo.getQtdeUnidades() == null ? vo.getQtdeMl().intValue(): vo.getQtdeUnidades());
			quantidade = new BigDecimal(vo.getQtdeAplicacoes() * indicador);
		}
		return quantidade;
	}
}
