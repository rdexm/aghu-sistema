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
import br.gov.mec.aghu.model.EceKitComponente;
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
import br.gov.mec.aghu.sig.custos.vo.MedicamentosInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #27654 - CONTAGEM DE MEDICAMENTOS POR UNIDADE DE INTERNAÇÃO, ESPECIALIDADE E EQUIPE.
 * Processo chamado pela classe {@link ProcessamentoDiarioContagemICP}
 * @author rmalvezzi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessamentoDiarioContagemICPMedicamentos extends ProcessamentoDiarioContagemBusiness {

	private static final long serialVersionUID = -1128765584914347380L;

	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCalculoAtdPaciente calculoAtdPaciente, List<AinMovimentosInternacao> movimentosInternacoes, Map<DominioIndContagem, SigCategoriaConsumos> categoriasConsumos, Map<Integer, SigCategoriaRecurso> categoriasRecursos, Map<AghuParametrosEnum, Object> parametros, Boolean alta) throws ApplicationBusinessException {

		AghAtendimentos atendimento = calculoAtdPaciente.getAtendimento();
		
		//Passo 1
		SigCategoriaConsumos categoriaConsumo = categoriasConsumos.get(DominioIndContagem.MD);
		
		SigCategoriaRecurso categoriaRecurso = categoriasRecursos.get(2);
		
		//Passo 2
		SigObjetoCustoVersoes objetoCustoVersao = (SigObjetoCustoVersoes) parametros.get(AghuParametrosEnum.P_AGHU_SIG_SEQ_MEDICACOES_PACIENTE);
		SigAtividades atividade = (SigAtividades) parametros.get(AghuParametrosEnum.P_AGHU_SIG_SEQ_MEDICAR_PACIENTE_PROCESSAMENTO_DIARIO);

		List<MedicamentosInternacaoVO> lista = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarMedicamentosInternacao(atendimento.getSeq(), sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());

		this.processarMedicamentos(lista, rapServidores, atendimento, sigProcessamentoCusto, sigProcessamentoPassos, objetoCustoVersao, atividade, categoriaConsumo, categoriaRecurso, movimentosInternacoes);
	}
	
	private void processarMedicamentos(List<MedicamentosInternacaoVO> lista, RapServidores servidor, AghAtendimentos atendimento,
			SigProcessamentoCusto sigProcessamentoCusto, SigProcessamentoPassos sigProcessamentoPassos, SigObjetoCustoVersoes objetoCustoVersao, 
			SigAtividades atividades, SigCategoriaConsumos categoriaConsumo, SigCategoriaRecurso categoriaRecurso, List<AinMovimentosInternacao> movimentosInternacoes)throws ApplicationBusinessException {
		
		if (lista != null) {
			
			AinMovimentosInternacao movimentoArmazenado = null, ultimoMovimentoInternacao = null;
			Map<String, List<EceKitComponente>> cacheKitMedicamentos = new HashMap<String, List<EceKitComponente>>();
			Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
			Map<Integer, SigCalculoAtdConsumo> cacheConsumo = new HashMap<Integer, SigCalculoAtdConsumo>();
			Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumo = new HashMap<String, SigCalculoDetalheConsumo>();
			Map<String, Long> cacheAprazamentos = new HashMap<String, Long>();
			List<EceKitComponente> kitsMedicamentos = null;
			FatProcedHospInternos phi = null;
			FccCentroCustos centroCusto = null;
			BigDecimal qtdKit = null;
			SigCalculoAtdPermanencia permanenciaUnidadeInternacao = null, permanenciaEspecialidade = null, permanenciaEquipe = null;
			SigCalculoAtdConsumo consumoUnidadeInternacao = null, consumoEspecialidade= null, consumoEquipe =null;
			
			//Passo 19
			for( MedicamentosInternacaoVO vo: lista) {
				
				//Passo 3	
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(vo.getCriadoEm(), movimentosInternacoes);

				if (ultimoMovimentoInternacao != null) {
					
					//Somente buscar de novo quando mudar o movimento, caso contrário será a mesma permanência e o mesmo consumo ainda
					if(movimentoArmazenado == null || !movimentoArmazenado.getSeq().equals(ultimoMovimentoInternacao.getSeq())){
						movimentoArmazenado = ultimoMovimentoInternacao;
						
						centroCusto = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
						//Passo 4		
						permanenciaUnidadeInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, movimentoArmazenado.getUnidadeFuncional().getCentroCusto(), sigProcessamentoCusto);
				
						//Passo 5
						permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, movimentoArmazenado.getEspecialidade(), sigProcessamentoCusto);
				
						//Passo 6
						permanenciaEquipe = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, movimentoArmazenado.getServidor(), sigProcessamentoCusto);
				
						//Passos 7
						consumoUnidadeInternacao = this.manterCalculoConsumo(permanenciaUnidadeInternacao, servidor, objetoCustoVersao, centroCusto, vo.getQtdeDispensada(), categoriaConsumo, cacheConsumo);
						
						//Passo 8
						consumoEspecialidade = this.manterCalculoConsumo(permanenciaEspecialidade, servidor, objetoCustoVersao, centroCusto, vo.getQtdeDispensada(), categoriaConsumo, cacheConsumo);
						consumoEquipe = this.manterCalculoConsumo(permanenciaEquipe, servidor, objetoCustoVersao, centroCusto, vo.getQtdeDispensada(), categoriaConsumo, cacheConsumo);
					
						//Passo 17
						this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, servidor, sigProcessamentoPassos, DominioEtapaProcessamento.D, "MENSAGEM_LOG_QT_MEDICAMENTOS_ATENDIMENTO", atendimento.getSeq(),consumoUnidadeInternacao.getCentroCustos().getCodigoDescricao());
					}
					//Só atualiza a quantidade no consumo enquanto não muda o movimento de internação
					else{
						this.atualizarCalculoConsumo(consumoUnidadeInternacao, vo.getQtdeDispensada());
						this.atualizarCalculoConsumo(consumoEspecialidade, vo.getQtdeDispensada());
						this.atualizarCalculoConsumo(consumoEquipe, vo.getQtdeDispensada());
					}
					
					phi = this.buscarPhiPorChavePrimaria(vo.getPhiSeq(),cachePhis);

					//Passo  9, 10,11
					this.manterCalculoDetalheConsumo(consumoUnidadeInternacao, phi, atividades, categoriaRecurso, servidor, vo.getQtdeSolicitada(), vo.getQtdeDispensada(), cacheDetalheConsumo);
			
					//Passo 12
					this.manterCalculoDetalheConsumo(consumoEspecialidade, phi, atividades, categoriaRecurso, servidor, vo.getQtdeSolicitada(), vo.getQtdeDispensada(),cacheDetalheConsumo);
					this.manterCalculoDetalheConsumo(consumoEquipe, phi, atividades, categoriaRecurso, servidor, vo.getQtdeSolicitada(), vo.getQtdeDispensada(),cacheDetalheConsumo);		
			
					//Passo 13
					kitsMedicamentos = this.buscarKitsMedicamentos(vo.getPhiSeq(), atendimento.getIndPacPediatrico(), vo.getVadSigla(), cacheKitMedicamentos);
					for (EceKitComponente eceKitComponente : kitsMedicamentos) {
						//Passos 14,15,16
						qtdKit = this.calculoQtdKit(vo, eceKitComponente, cacheAprazamentos);
						this.manterCalculoDetalheConsumo(consumoUnidadeInternacao, eceKitComponente.getFatProcedHospInternos(), atividades, categoriaRecurso, servidor, qtdKit, qtdKit, cacheDetalheConsumo);
						this.manterCalculoDetalheConsumo(consumoEspecialidade, eceKitComponente.getFatProcedHospInternos(), atividades, categoriaRecurso, servidor, qtdKit, qtdKit, cacheDetalheConsumo);
						this.manterCalculoDetalheConsumo(consumoEquipe, eceKitComponente.getFatProcedHospInternos(), atividades, categoriaRecurso, servidor, qtdKit,  qtdKit, cacheDetalheConsumo);
					}

					this.commitProcessamentoCusto();
				}
			}
		}
		else{
			//FE04
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, servidor, sigProcessamentoPassos,
					DominioEtapaProcessamento.C, "MENSAGEM_SEM_DADOS_MEDICAMENTOS", atendimento.getSeq());
		}
	}
	
	private List<EceKitComponente> buscarKitsMedicamentos(Integer phiSeq, Boolean pediatrico, String siglaViaAdministracao,  Map<String, List<EceKitComponente>> cacheKitMedicamentos){
		String chave = phiSeq+"-"+siglaViaAdministracao;
		if(!cacheKitMedicamentos.containsKey(chave)){
			cacheKitMedicamentos.put(chave, this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarKitsMedicamentos(phiSeq, pediatrico, siglaViaAdministracao));
		}
		return cacheKitMedicamentos.get(chave);
	}
	
	private SigCalculoAtdConsumo manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, RapServidores rapServidores,
			SigObjetoCustoVersoes objetoCustoVersao, FccCentroCustos centroCusto, BigDecimal quantidadeDispensada, SigCategoriaConsumos categoriaConsumo, Map<Integer, SigCalculoAtdConsumo> cacheConsumo) {
		
		//Passo 7
		if(!cacheConsumo.containsKey(permanencia.getSeq())){
			cacheConsumo.put(permanencia.getSeq(), this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumo(permanencia, objetoCustoVersao, null));
		}
		SigCalculoAtdConsumo consumo = cacheConsumo.get(permanencia.getSeq());
		
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setRapServidores(rapServidores);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setCentroCustos(centroCusto);
			consumo.setCriadoEm(new Date());
			consumo.setQtde(quantidadeDispensada);
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
			cacheConsumo.put(permanencia.getSeq(), consumo);
		}
		else{
			this.atualizarCalculoConsumo(consumo, quantidadeDispensada);
		}
		return consumo;
	}
	
	private void atualizarCalculoConsumo(SigCalculoAtdConsumo sigCalculoAtdConsumo, BigDecimal quantidadeDispensada) {
		sigCalculoAtdConsumo.setQtde(sigCalculoAtdConsumo.getQtde().add(quantidadeDispensada));
		this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(sigCalculoAtdConsumo);
	}

	private void manterCalculoDetalheConsumo(SigCalculoAtdConsumo consumo, FatProcedHospInternos phi,
			SigAtividades atividade, SigCategoriaRecurso categoriaRecurso, RapServidores rapServidores, BigDecimal qtdPrevista, BigDecimal qtdDebitado, Map<String, SigCalculoDetalheConsumo> cacheDetalheConsumo) {
		
		String chave = consumo.getSeq()+"-"+phi.getSeq();
		
		//Passo 09 e 14
		if(!cacheDetalheConsumo.containsKey(chave)){
			cacheDetalheConsumo.put(chave, this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().buscarItemConsumo(consumo, phi));
		}
		SigCalculoDetalheConsumo detalheConsumo =  cacheDetalheConsumo.get(chave);
		//Passo 10 e 15
		if (detalheConsumo == null) {
			detalheConsumo = new SigCalculoDetalheConsumo();
			detalheConsumo.setCalculoAtividadeConsumo(consumo);
			detalheConsumo.setAtividade(atividade);
			detalheConsumo.setProcedHospInterno(phi);
			detalheConsumo.setQtdePrevisto(qtdPrevista);
			detalheConsumo.setQtdeDebitado(qtdDebitado);
			detalheConsumo.setQtdeConsumido(qtdDebitado);
			detalheConsumo.setRapServidores(rapServidores);
			detalheConsumo.setCriadoEm(new Date());
			detalheConsumo.setCategoriaRecurso(categoriaRecurso);
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().persistir(detalheConsumo);
			cacheDetalheConsumo.put(consumo.getSeq()+"-"+phi.getSeq(), detalheConsumo);//Atualizar no cache
		} 
		//Passo 11 e 16
		else {
			detalheConsumo.setQtdePrevisto(detalheConsumo.getQtdePrevisto().add(qtdPrevista));
			detalheConsumo.setQtdeDebitado(detalheConsumo.getQtdeDebitado().add(qtdDebitado));
			detalheConsumo.setQtdeConsumido(detalheConsumo.getQtdeConsumido().add(qtdDebitado));
			
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		}
	}

	/**
	 * Se o componente for usual (U) ou diluente usual(D), qtd do kit (obtida no passo 12) multiplicado pela qtd de kits necessários ao medicamento (obtido no SQL do passo 1)
	 * Para calcular o segunda valor, é necessário chamar a função MPMC_NUM_VEZES_APRAZ e calcula o valor propocional de hora da diferença das datas retornada no passo 1.
	 * Se o componente for alternativo (A) ou diluente alternativo (T), preencher com o valor 0.
	 * 
	 * @author rmalvezzi
	 * @param vo
	 * @param eceKitComponente
	 * @return
	 */
	private BigDecimal calculoQtdKit(MedicamentosInternacaoVO vo, EceKitComponente eceKitComponente, Map<String, Long> cacheAprazamentos) {
		if (eceKitComponente.getTipo().equals("U") || eceKitComponente.getTipo().equals("D")) {
			Long calculoNumeroVezesAprazamento24Horas = this.calcularNumeroVezesAprazamento(vo.getTfdSeq(), vo.getFrequecia(), cacheAprazamentos);
			Double diferencaEntreDatas = ProcessamentoCustoUtils.calcularEmDiasDiferencaEntreDatas(vo.getDataHoraInicio(), vo.getDataHoraFim());
			return new BigDecimal(Math.round(calculoNumeroVezesAprazamento24Horas * diferencaEntreDatas) * eceKitComponente.getQuantidade());
		}
		return BigDecimal.ZERO;
	}
	
}
