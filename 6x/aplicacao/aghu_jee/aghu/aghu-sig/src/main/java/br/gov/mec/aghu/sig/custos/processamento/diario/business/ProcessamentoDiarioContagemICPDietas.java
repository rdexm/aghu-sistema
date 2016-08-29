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
import br.gov.mec.aghu.model.AnuItemGrupoQuadroDieta;
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
import br.gov.mec.aghu.sig.custos.vo.DietaPrescritaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Estória do Usuário #28394 - Contagem de dietas por unidade de internação/especialidade/equipe
 * @author rogeriovieira
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPDietas extends ProcessamentoDiarioContagemBusiness  {

	private static final long serialVersionUID = -2361193984557680229L;

	/**
	 * método que efetua a contagem das dietas
	 * @param sigProcessamentoCusto processamento atual
	 * @param servidor servidor utilizado pelo processamento  
	 * @param sigProcessamentoPassos passo atual
	 * @param calculoAtendimento cálculo do atendimento do paciente
	 * @throws AGHUNegocioExceptionSemRollback exceção que pode ser lançada ao fazer o commit dos dados
	 * @author jgugel
	 */
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros,  Boolean alta) throws ApplicationBusinessException {

		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();

		//Passo 1	
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.DI);
		
		//Passo 2		
		List<DietaPrescritaVO> listDietaPrescrita = this.getProcessamentoCustoUtils().getSigObjetoCustoPhisDAO()
				.buscaDietasDuranteValidadePrescricao(atendimento, sigProcessamentoCusto);

		//Passo 3
		this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, 
				DominioEtapaProcessamento.D, "MENSAGEM_DADOS_DIETA_PACIENTE_OBTIDOS_SUCESSO", atendimento.getSeq());
		
		if(listDietaPrescrita != null){
			
			AinMovimentosInternacao movimentoArmazenado = null, ultimoMovimentoInternacao = null;
			SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
			SigObjetoCustoVersoes objetoCustoVersao = null;
			AnuItemGrupoQuadroDieta quadroDieta = null;
			Date dataComposta = null;
			
			Map<String, SigCalculoAtdConsumo> cacheConsumos = new HashMap<String, SigCalculoAtdConsumo>();
			
			//Passo 4
			for(DietaPrescritaVO dieta : listDietaPrescrita) {
				
				//Passo 5 pt 01
				
				if(dieta.getRefSeq() != null){
					quadroDieta = this.getProcessamentoCustoUtils().getNutricaoFacade().buscaGrupoDietaPorRefeicao(dieta.getRefSeq());
				}
				
				//Passo 5 pt 02
				if(quadroDieta == null){
					quadroDieta = this.getProcessamentoCustoUtils().getNutricaoFacade().buscaGrupoDietaHabitoAlimentar(dieta.getHauSeq());
				}
				
				if(quadroDieta == null || quadroDieta.getAnuRefeicao() == null){	
					//Passo 5 -  FE03
					continue;
				}
				
				//Passo 06
				dataComposta = DateUtil.comporDiaHora(dieta.getDataInicioPrescricao(), quadroDieta.getAnuRefeicao().getHoraFim());
				
				//Passo 07		
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(dataComposta, movimentosInternacoes);
	
				if(ultimoMovimentoInternacao != null){
					
					//Somente buscar de novo quando mudar o movimento, caso contrário será a mesma permanência
					if(movimentoArmazenado == null || !movimentoArmazenado.getSeq().equals(ultimoMovimentoInternacao.getSeq())){
						movimentoArmazenado = ultimoMovimentoInternacao;
					
						//Passo 08
						permanenciaUnidadeInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto(), sigProcessamentoCusto);
						
						//Passo 09
						permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternacao.getEspecialidade(), sigProcessamentoCusto);
						
						//Passo 10
						permanenciaEquipe = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternacao.getServidor(), sigProcessamentoCusto);
					}
					
					//Passo 11
					objetoCustoVersao = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().buscarObjetoCustoVersaoPelaRefeicao(quadroDieta.getId().getGqdSeq());
					
					if(objetoCustoVersao == null){
						//FE04
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, 
								DominioEtapaProcessamento.D, "MENSAGEM_OBJETO_CUSTO_GRUPO_DIETA_NAO_ENCONTRADO", atendimento.getSeq());
					}
					else{
						this.manterConsumo(permanenciaUnidadeInternacao, objetoCustoVersao, ultimoMovimentoInternacao, rapServidores, categoriaConsumo, sigProcessamentoCusto, sigProcessamentoPassos, atendimento, cacheConsumos);
						this.manterConsumo(permanenciaEspecialidade, objetoCustoVersao, ultimoMovimentoInternacao, rapServidores, categoriaConsumo, sigProcessamentoCusto, sigProcessamentoPassos, atendimento, cacheConsumos);
						this.manterConsumo(permanenciaEquipe, objetoCustoVersao, ultimoMovimentoInternacao, rapServidores, categoriaConsumo, sigProcessamentoCusto, sigProcessamentoPassos, atendimento, cacheConsumos);	
					}
				
					this.commitProcessamentoCusto();
				}
			}
		}
	}
	
	private void manterConsumo(SigCalculoAtdPermanencia permanencia, SigObjetoCustoVersoes objetoCustoVersao, AinMovimentosInternacao ultimoMovimentoInternacao, RapServidores servidor, SigCategoriaConsumos categoriaConsumo, SigProcessamentoCusto sigProcessamentoCusto, SigProcessamentoPassos sigProcessamentoPassos, AghAtendimentos atendimento, Map<String, SigCalculoAtdConsumo> cacheConsumos){
		//Passo 12
		
		String chave = permanencia.getSeq()+"-"+objetoCustoVersao.getSeq();
		if(!cacheConsumos.containsKey(chave)){
			cacheConsumos.put(chave, this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumoPorPermanenciaEVersao(permanencia.getSeq(), objetoCustoVersao.getSeq()));
		}
		SigCalculoAtdConsumo consumo = cacheConsumos.get(chave);
		
		if(consumo == null){
			//Passo 13
			consumo = new SigCalculoAtdConsumo();
			consumo.setCentroCustos(ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto());
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setQtde(BigDecimal.ONE);
			consumo.setRapServidores(servidor);
			consumo.setCriadoEm(new Date());
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
			cacheConsumos.put(chave, consumo);
		
		}else {
			//Passo 14
			consumo.setQtde(consumo.getQtde().add(BigDecimal.ONE));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
			
		}
		
		//Passo 15
		if(permanencia.getCentroCustos() != null){
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, 
					DominioEtapaProcessamento.D, "MENSAGEM_DADOS_DIETA_PACIENTE_NO_CC_ATUALIZADOS_COM_SUCESSO", atendimento.getSeq(), ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto().getCodigo());
		}
		if(permanencia.getEspecialidade() != null){
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, 
					DominioEtapaProcessamento.D, "MENSAGEM_DADOS_DIETA_PACIENTE_NA_ESPECIALIDADE_ATUALIZADOS_COM_SUCESSO", atendimento.getSeq(), ultimoMovimentoInternacao.getEspecialidade().getNomeEspecialidade());
		}
		if(permanencia.getResponsavel() != null){
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, 
					DominioEtapaProcessamento.D, "MENSAGEM_DADOS_DIETA_PACIENTE_NA_EQUIPE_ATUALIZADOS_COM_SUCESSO", atendimento.getSeq(), ultimoMovimentoInternacao.getServidor().getCodigoVinculoNomeServidor());
		}
	}
}
