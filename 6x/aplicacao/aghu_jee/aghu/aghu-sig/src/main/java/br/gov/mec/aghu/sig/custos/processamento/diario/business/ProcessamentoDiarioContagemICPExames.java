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
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoDiarioContagemBusiness;
import br.gov.mec.aghu.sig.custos.vo.ExamesInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Estória do Usuário #27488 - Processamento Diário - Contagem de exames por unidade de internação/especialidade/equipe
 * @author rogeriovieira
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPExames extends ProcessamentoDiarioContagemBusiness {

	private static final long serialVersionUID = -1128765584914347380L;

	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros,  Boolean alta) throws ApplicationBusinessException{

		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();
		
		//Passo 1
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.EX);
		
		//Passo 2		
		List<ExamesInternacaoVO> examesInternacao = this.getProcessamentoCustoUtils().getSigObjetoCustoPhisDAO().buscarExamesInternacao(atendimento, sigProcessamentoCusto);
		
		if(examesInternacao != null){
			
			Integer solicitacaoCorrente = null;
			AinMovimentosInternacao ultimoMovimentoInternacao = null;
			SigCalculoAtdPermanencia permanenciaUnidadeFuncional = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
			FatProcedHospInternos phi = null;
			SigObjetoCustoVersoes objetoCustoVersoes = null;
			Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
			Map<Integer, SigObjetoCustoVersoes> cacheOcvs= new HashMap<Integer, SigObjetoCustoVersoes>();
			Map<String, SigCalculoAtdConsumo> cacheConsumo = new HashMap<String, SigCalculoAtdConsumo>();
			FccCentroCustos centroCusto = null;
			
			for (ExamesInternacaoVO vo : examesInternacao) {
	
				//Só precisa buscar a permanência e gravar o log, quando mudar a solicitação, já que também mudará o movimento
				if (solicitacaoCorrente == null || (solicitacaoCorrente != null && !vo.getSoeSeq().equals(solicitacaoCorrente))) {
					
					solicitacaoCorrente = vo.getSoeSeq();
					//Passo 3
					ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(vo.getSoeCriadoEm(), movimentosInternacoes);
					
					if (ultimoMovimentoInternacao != null) {
						
						//Passo 4
						permanenciaUnidadeFuncional = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto(), sigProcessamentoCusto);					
						
						//Passo 9
						permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternacao.getEspecialidade(), sigProcessamentoCusto);
	
						//Passo 14
						permanenciaEquipe = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternacao.getServidor(), sigProcessamentoCusto);
						
						//Passo 8
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTD_EXAMES_ATD_CENTRO_CUSTO", atendimento.getSeq(), ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto().getDescricao());
						
						//Passo 13
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTD_EXAMES_ATD_ESPECIALIDADE", atendimento.getSeq(), ultimoMovimentoInternacao.getEspecialidade().getNomeEspecialidade());
						
						//Passo 18
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTD_EXAMES_ATD_EQUIPE", atendimento.getSeq(), ultimoMovimentoInternacao.getServidor().getPessoaFisica().getNome());					
					}
				}
				
				if(ultimoMovimentoInternacao != null){
				
					if (vo.getOcvSeq() != null) {
						objetoCustoVersoes = this.buscarOcvPorChavePrimaria(vo.getOcvSeq(),cacheOcvs);
						phi = null;
					}
					else{
						phi = this.buscarPhiPorChavePrimaria(vo.getPhiSeq(), cachePhis);
						objetoCustoVersoes = null;
					}
					
					centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCctCodigo());
					
					//Passo  5, 6,7
					this.manterCalculoConsumo(permanenciaUnidadeFuncional, centroCusto, vo.getQtdeExames(), objetoCustoVersoes, phi, categoriaConsumo, rapServidores, cacheConsumo);
	
					//Passo 10, 11, 12
					this.manterCalculoConsumo(permanenciaEspecialidade, centroCusto, vo.getQtdeExames(), objetoCustoVersoes, phi, categoriaConsumo, rapServidores, cacheConsumo);		
	
					//Passo 15, 16, 17
					this.manterCalculoConsumo(permanenciaEquipe, centroCusto, vo.getQtdeExames(), objetoCustoVersoes, phi, categoriaConsumo, rapServidores, cacheConsumo);
					
					commitProcessamentoCusto();
				}
			}
		}
	}

	private void manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, FccCentroCustos centroCusto, BigDecimal quantidade, SigObjetoCustoVersoes objetoCustoVersao, FatProcedHospInternos phi, 
			 SigCategoriaConsumos categoriaConsumo, RapServidores servidor, Map<String, SigCalculoAtdConsumo> cacheConsumo) throws ApplicationBusinessException {

		//Passo 5, 10, 15
		String chave = permanencia.getSeq()+"-"+(objetoCustoVersao != null ? objetoCustoVersao.getSeq() : 0 )+"-"+(phi != null ? phi.getSeq() : 0);
		if(!cacheConsumo.containsKey(chave)){
			cacheConsumo.put(chave, this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumo(permanencia, objetoCustoVersao, phi));
		}
		SigCalculoAtdConsumo consumo = cacheConsumo.get(chave);
		
		//Passo 6, 11, 16
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setProcedHospInterno(phi);
			consumo.setCentroCustos(centroCusto);
			consumo.setQtde(quantidade);
			consumo.setRapServidores(servidor);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
			cacheConsumo.put(chave, consumo);
		} 
		//Passo 6, 11, 16	
		else {
			consumo.setQtde(consumo.getQtde().add(quantidade));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
	}
}
