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
import br.gov.mec.aghu.model.ScoMaterial;
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
import br.gov.mec.aghu.sig.custos.vo.ItemPrescricaoNptVO;
import br.gov.mec.aghu.sig.custos.vo.NutricaoParenteralVO;
import br.gov.mec.aghu.sig.custos.vo.ObjetoCustoAtividadeVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #28396 - Classe responsável pela contagem de nutrição parenteral por unidade de internação, especialidade e equipe” 
 * do módulo de Custos,
 * @author rhrosa
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPNutricaoParenteral extends ProcessamentoDiarioContagemBusiness {

	private static final long serialVersionUID = 453664388521312615L;

	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {

		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();
		
		// Passo 1
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.NP);
		
		SigCategoriaRecurso categoriaRecurso = categoriasRecursos.get(2);
		
		// Passo 2
		List<NutricaoParenteralVO> nutricoesParenterais = this.getProcessamentoCustoUtils().getPrescricaoMedicaFacade()
				.buscarNutricoesParenteraisPrescritas(atendimento.getSeq(), sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());
		
		if(ProcessamentoCustoUtils.verificarListaNaoVazia(nutricoesParenterais)){
			
			// Passo 3		
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "MENSAGEM_SUCESSO_CONSULTA_NUTRICAO_PARENTERAL", atendimento.getSeq());
			
			// Passo 4
			ObjetoCustoAtividadeVO objetoCustoAtividadeVO = this.getProcessamentoCustoUtils().getSigObjetoCustoComposicoesDAO().buscarObjetoDeCustoNutricaoParenteral(nutricoesParenterais.get(0).getPedSeq());
			
			if (objetoCustoAtividadeVO != null) {
			
				SigAtividades atividade = this.getProcessamentoCustoUtils().getSigAtividadesDAO().obterPorChavePrimaria(objetoCustoAtividadeVO.getTvdSeq());
				SigObjetoCustoVersoes objetoCustoVersao = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterPorChavePrimaria(objetoCustoAtividadeVO.getOcvSeq());
				
				AinMovimentosInternacao movimentoArmazenado = null, ultimoMovimentoInternacao = null;
				SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
				SigCalculoAtdConsumo consumoUnidadeInternacao = null, consumoEspecialidade = null, consumoEquipe = null;
				FccCentroCustos centroCusto = null;
				ScoMaterial material = null;
				FatProcedHospInternos phi = null;
				BigDecimal quantidadeNpt = null, quantidadeItem = null;
				
				Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
				Map<Integer, ScoMaterial> cacheMateriais = new HashMap<Integer, ScoMaterial>();
				Map<Integer, SigCalculoAtdConsumo> cacheConsumo = new HashMap<Integer, SigCalculoAtdConsumo>();
				Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumo = new HashMap<String, SigCalculoDetalheConsumo>();
				
				for (NutricaoParenteralVO vo : nutricoesParenterais) {
	
					//Passo 5
					ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(vo.getPmdDataInicio(), movimentosInternacoes);

					if(ultimoMovimentoInternacao != null){
	
						quantidadeNpt = new BigDecimal(vo.getQtdeNpt());
						
						//Somente buscar de novo quando mudar o movimento, caso contrário será a mesma permanência
						if(movimentoArmazenado == null || !movimentoArmazenado.getSeq().equals(ultimoMovimentoInternacao.getSeq())){
							movimentoArmazenado = ultimoMovimentoInternacao;
							centroCusto = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
							
							// Passo 6
							permanenciaUnidadeInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto(), sigProcessamentoCusto);
	
							// Passo 7
							permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternacao.getEspecialidade(), sigProcessamentoCusto);
	
							// Passo 8
							permanenciaEquipe = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternacao.getServidor(), sigProcessamentoCusto);

							//Só mudará o consumo quando mudar a permanência, já que o objeto de custo será sempre o mesmo
							
							//Passo 9, 10, 11, 12
							consumoUnidadeInternacao = this.manterCalculoConsumo(permanenciaUnidadeInternacao, quantidadeNpt, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, centroCusto, categoriaConsumo, objetoCustoVersao, atendimento, cacheConsumo);
							consumoEspecialidade = this.manterCalculoConsumo(permanenciaEspecialidade, quantidadeNpt, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, centroCusto, categoriaConsumo, objetoCustoVersao, atendimento, cacheConsumo);
							consumoEquipe = this.manterCalculoConsumo(permanenciaEquipe, quantidadeNpt, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, centroCusto, categoriaConsumo, objetoCustoVersao, atendimento, cacheConsumo);
						}
						else{
							//Passo 10 (enquanto não mudar o consumo, só deve atualizar o valor da quantidade)
							atualizarCalculoConsumo(consumoUnidadeInternacao, quantidadeNpt);
							atualizarCalculoConsumo(consumoEspecialidade, quantidadeNpt);
							atualizarCalculoConsumo(consumoEquipe, quantidadeNpt);
						}
						
						// Passo 14
						List<ItemPrescricaoNptVO> listaItensPrescricaoNptVO = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().buscarItensNutricaoParenteral(vo.getNptSeq());
						for (ItemPrescricaoNptVO itemVO : listaItensPrescricaoNptVO) {

							material = this.buscarMaterialPorChavePrimaria(itemVO.getCnpMedMatCodigo(), cacheMateriais);
							phi = this.buscarPhiPorChavePrimaria(itemVO.getPhiSeq(), cachePhis);
							quantidadeItem = ProcessamentoCustoUtils.dividir(itemVO.getQtdePrescrita(), itemVO.getFatorConversaoUp());
							
							//Passo 15, 16, 17, 18
							this.manterCalculoDetalheConsumo(consumoUnidadeInternacao, material, phi, quantidadeItem, atividade, sigProcessamentoCusto,rapServidores, sigProcessamentoPassos, atendimento, categoriaRecurso, cacheDetalheConsumo);
							this.manterCalculoDetalheConsumo(consumoEspecialidade, material, phi, quantidadeItem, atividade, sigProcessamentoCusto,rapServidores, sigProcessamentoPassos, atendimento, categoriaRecurso, cacheDetalheConsumo);
							this.manterCalculoDetalheConsumo(consumoEquipe, material, phi, quantidadeItem, atividade, sigProcessamentoCusto,rapServidores, sigProcessamentoPassos, atendimento, categoriaRecurso, cacheDetalheConsumo);
						}
						
						this.commitProcessamentoCusto();
					}
				}
			}else{
				// [FE03] - Retorna para o fluxo principal do passo1.	
				this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.C, "MENSAGEM_INSUCESSO_CONSULTA_OBJETO_CUSTO_ATIVIDADE");
			}	
		}
		else{
			// [FE02] - Retorna para o fluxo principal do passo1.	
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.C, "MENSAGEM_NAO_EXISTE_NUTRICAO_PARENTERAL_PRESCRITA", atendimento.getSeq());
		}
	}

	private SigCalculoAtdConsumo manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, BigDecimal quantidade,
			SigProcessamentoCusto sigProcessamentoCusto, RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, FccCentroCustos centroCusto, SigCategoriaConsumos categoriaConsumo, SigObjetoCustoVersoes objetoCustoVersao, AghAtendimentos atendimento, Map<Integer, SigCalculoAtdConsumo> cacheConsumo) {

		// Passo 9
		if(!cacheConsumo.containsKey(permanencia.getSeq())){
			cacheConsumo.put(permanencia.getSeq(), this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumo(permanencia, objetoCustoVersao, null));
		}
		SigCalculoAtdConsumo consumo = cacheConsumo.get(permanencia.getSeq());
		
		// Passo 11
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setCentroCustos(centroCusto);
			consumo.setQtde(quantidade);
			consumo.setRapServidores(servidor);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
			cacheConsumo.put(permanencia.getSeq(), consumo);
		} 
		// Passo 10
		else {
			this.atualizarCalculoConsumo(consumo, quantidade);
		}
		
		// Passo 12
		if (permanencia.getCentroCustos() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_CALCULO_NUTRICAO_PARENTERAL_CENTRO_CUSTO", atendimento.getSeq());
		} else if (permanencia.getEspecialidade() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_CALCULO_NUTRICAO_PARENTERAL_ESPECIALIDADE", atendimento.getSeq());
		} else if (permanencia.getResponsavel() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_CALCULO_NUTRICAO_PARENTERAL_EQUIPE", atendimento.getSeq());
		}
		
		return consumo;
	}
	
	private void atualizarCalculoConsumo(SigCalculoAtdConsumo calculoAtdConsumo, BigDecimal quantidade){
		calculoAtdConsumo.setQtde(calculoAtdConsumo.getQtde().add(quantidade));
		this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(calculoAtdConsumo);
	}
	
	private void manterCalculoDetalheConsumo(SigCalculoAtdConsumo consumo,  ScoMaterial material,  FatProcedHospInternos phi, BigDecimal quantidade,   SigAtividades atividade,
			SigProcessamentoCusto sigProcessamentoCusto, RapServidores servidor, SigProcessamentoPassos sigProcessamentoPassos, AghAtendimentos atendimento, SigCategoriaRecurso categoriaRecurso, Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumo) {

		// Passo 15
		String chave = consumo.getSeq()+"-"+phi.getSeq();
		if(!cacheDetalheConsumo.containsKey(chave)){
			cacheDetalheConsumo.put(chave, this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().buscarItemConsumo(consumo, phi));
		}
		SigCalculoDetalheConsumo detalheConsumo =  cacheDetalheConsumo.get(chave);
		
		// Passo 16
		if(detalheConsumo == null){
			detalheConsumo = new SigCalculoDetalheConsumo();
			detalheConsumo.setCalculoAtividadeConsumo(consumo);
			detalheConsumo.setAtividade(atividade);
			detalheConsumo.setCategoriaRecurso(categoriaRecurso);
			detalheConsumo.setQtdePrevisto(quantidade);
			detalheConsumo.setQtdeDebitado(quantidade);
			detalheConsumo.setQtdeConsumido(quantidade);
			detalheConsumo.setProcedHospInterno(phi);
			detalheConsumo.setRapServidores(servidor);
			detalheConsumo.setScoMaterial(material);
			detalheConsumo.setCriadoEm(new Date());
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().persistir(detalheConsumo);		
			cacheDetalheConsumo.put(chave, detalheConsumo);
		}
		// Passo 17
		else{
			detalheConsumo.setQtdePrevisto(detalheConsumo.getQtdePrevisto().add(quantidade));
			detalheConsumo.setQtdeDebitado(detalheConsumo.getQtdeDebitado().add(quantidade));
			detalheConsumo.setQtdeConsumido(detalheConsumo.getQtdeConsumido().add(quantidade));
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		}

		// Passo 18	
		if (consumo.getCalculoAtividadePermanencia().getCentroCustos() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTDE_MEDICAMENTOS_NUTRICAO_PARENTERAL_CENTRO_CUSTO", atendimento.getSeq());
		} else if (consumo.getCalculoAtividadePermanencia().getEspecialidade() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTDE_MEDICAMENTOS_NUTRICAO_PARENTERAL_ESPECIALIDADE", atendimento.getSeq());
		} else if (consumo.getCalculoAtividadePermanencia().getResponsavel() != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_SUCESSO_ATUALIZACAO_QTDE_MEDICAMENTOS_NUTRICAO_PARENTERAL_EQUIPE", atendimento.getSeq());
		}
	}
}