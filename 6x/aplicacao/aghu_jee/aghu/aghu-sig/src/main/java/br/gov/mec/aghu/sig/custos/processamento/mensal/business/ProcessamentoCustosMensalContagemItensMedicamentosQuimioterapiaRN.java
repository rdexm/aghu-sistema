package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.dominio.DominioIndContagem;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.farmacia.vo.ComposicaoItemPreparoVO;
import br.gov.mec.aghu.model.AfaItemPreparoMdto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
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
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoExceptionCode;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoCustoUtils;
import br.gov.mec.aghu.sig.custos.processamento.business.ProcessamentoMensalBusiness;
import br.gov.mec.aghu.sig.custos.vo.SigAtendimentoVO;
import br.gov.mec.aghu.sig.custos.vo.SigObjetoCustoPhisVO;
import br.gov.mec.aghu.sig.custos.vo.SigPreparoMdtoPrescricaoMedicaVO;
import br.gov.mec.aghu.sig.custos.vo.SigPreparoMdtoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #32017
 * Esta contagem tem o objetivo de, na execução do processamento mensal, verificar quais pacientes tiveram itens/medicamentos quimioterápicos 
 * (bolsas, seringas e dispensações) prescritos durante o seu tratamento. Conforme o atendimento do paciente será associado às unidades de atendimento 
 * (ambulatorial ou internação), especialidades e equipes pelos quais o paciente passou.
 * 
 * @author rmalvezzi
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN.class)
public class ProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 7844499461636265383L;

	@Override
	public String getTitulo() {
		return "Contagem de bolsas, seringas e dispensações de quimioterapias por tipo de atendimento do paciente";
	}

	@Override
	public String getNome() {
		return "ProcessamentoCustosMensalContagemItensMedicamentosQuimioterapiaRN";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 28;
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos,
			DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {
		
		Short pConvenioSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
		
        Short pTipoGrupoContaSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		
		SigCategoriaRecurso categoriaRecurso = this.getProcessamentoCustoUtils().getSigCategoriaRecursoDAO().obterPorChavePrimaria(2);
		
		Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
		Map<Integer, ScoMaterial> cacheMateriais = new HashMap<Integer, ScoMaterial>();
		Map<String, SigObjetoCustoPhisVO> cacheObjetosAtividades = new HashMap<String, SigObjetoCustoPhisVO>();
		Map<Integer, SigObjetoCustoVersoes> cacheOcv = new HashMap<Integer, SigObjetoCustoVersoes>();
		Map<Integer, SigAtividades> cacheAtividades = new HashMap<Integer, SigAtividades>();
		
		//Etapa 1
		this.executarEtapa1PreparadosEDispensados(categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, cachePhis, cacheMateriais, cacheObjetosAtividades, cacheOcv, cacheAtividades, pConvenioSus, pTipoGrupoContaSus);
		
		//Etapa 2
		this.executarEtapa2SemPreparo(categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, cachePhis, cacheMateriais, pConvenioSus, pTipoGrupoContaSus);
		
		//Etapa 3
		this.executarEtapa3PrescricaoMedica(categoriaRecurso, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, cachePhis, cacheMateriais, cacheObjetosAtividades, cacheOcv, cacheAtividades, pConvenioSus, pTipoGrupoContaSus);
	}

	//Etapa 1
	protected void executarEtapa1PreparadosEDispensados(SigCategoriaRecurso categoriaRecurso, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, Map<Integer, FatProcedHospInternos> cachePhis, Map<Integer, ScoMaterial> cacheMateriais, Map<String, SigObjetoCustoPhisVO> cacheObjetosAtividades, Map<Integer, SigObjetoCustoVersoes> cacheOcv, Map<Integer, SigAtividades> cacheAtividades, Short pConvenioSus, Short pTipoGrupoContaSus) throws ApplicationBusinessException {
		// Passo 01
		SigCategoriaConsumos categoriaConsumo = this.obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.QM, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		// Passo 02
		List<SigPreparoMdtoVO> lista = this.getProcessamentoCustoUtils().getFarmaciaFacade().buscarBolsasSeringasDinpensacoes(sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim(), 6);
		
		
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista)) {
			
			// PASSO 03
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.C, "RETORNOU_BOLSA_SERINGA_DISPENSACAO",  sigProcessamentoCusto.getCompetenciaMesAno());
	
			SigObjetoCustoPhisVO sigObjetoCustoPhisVO;
			SigObjetoCustoVersoes objetoCustoVersao;
			SigAtividades atividade;
			AghUnidadesFuncionais unidadeFuncional;
			AghAtendimentos atendimentoInternacao;
			List<AghAtendimentos> atendimentos;
			MptTratamentoTerapeutico tratamentoTerapeutico;
			FccCentroCustos centroCusto;
			DominioCalculoPermanencia tipo;
			SigCalculoAtdPaciente calculoPaciente;
			SigCalculoAtdPermanencia permanenciaUnidadeInternacao, permanenciaEspecialidade, permanenciaEquipe;
			SigCalculoAtdConsumo consumoUnidadeInternacao, consumoEspecialidade, consumoEquipe;
			AfaItemPreparoMdto itemPreparoMdto;
			
			// Passo 04
			for (SigPreparoMdtoVO vo : lista) {
	
				// PASSO 5
				sigObjetoCustoPhisVO =  this.obterObjetoCustoAtividadePorEtiqueta(vo.getTipoEtiqueta(), sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, cacheObjetosAtividades);
				
				if(sigObjetoCustoPhisVO != null){

					objetoCustoVersao = this.buscarOcvPorChavePrimaria(sigObjetoCustoPhisVO.getOcvSeq(), cacheOcv);
					atividade = this.buscarAtividadePorChavePrimaria(sigObjetoCustoPhisVO.getTvdSeq(), cacheAtividades);
					tratamentoTerapeutico = this.getProcessamentoCustoUtils().getProcedimentoTerapeuticoFacade().obterTratamentoTerapeutico(vo.getTrpSeq());
					centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCentroCusto());
					itemPreparoMdto = this.getProcessamentoCustoUtils().getFarmaciaFacade().obterItemPreparoMdtosPorChavePrimaria(vo.getItoSeqp(), vo.getItoPtoSeq());
					
					// Passo 6
					unidadeFuncional = this.getProcessamentoCustoUtils().getAghuFacade().buscarUnidadeInternacaoAtiva(vo.getUnfSeq());

					atendimentoInternacao = null;
					if(unidadeFuncional != null){
						//Passo 7
						atendimentos = this.getProcessamentoCustoUtils().getAghuFacade().buscarAtendimentos(vo.getPacCodigo(), vo.getDtPrevExecucao());
						atendimentoInternacao = ProcessamentoCustoUtils.verificarListaNaoVazia(atendimentos) ? atendimentos.get(0) : null;
					}

					//Passo 8,9,10
					calculoPaciente = this.manterCalculoPaciente(tratamentoTerapeutico, vo.getAtdPaciente(), atendimentoInternacao, sigProcessamentoCusto, rapServidores, pConvenioSus, pTipoGrupoContaSus);
		
					// PASSO 11
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ATENDIMENTO_PACIENTE_BASE_CALCULO", vo.getAtdPaciente(), vo.getPacCodigo());
		
					// PASSO 12
					tipo = ((calculoPaciente.getSituacaoCalculoPaciente() == DominioSituacaoCalculoPaciente.I) ? DominioCalculoPermanencia.UI : DominioCalculoPermanencia.UA);
					permanenciaUnidadeInternacao = this.manterPermanencia(calculoPaciente, tipo, rapServidores, centroCusto, null, null, sigProcessamentoCusto);
		
					// PASSO 14
					permanenciaEspecialidade = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.SM, rapServidores, null, tratamentoTerapeutico.getEspecialidade(), null, sigProcessamentoCusto);
		
					// PASSO 15
					permanenciaEquipe = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.EQ, rapServidores, null, null, tratamentoTerapeutico.getServidorResponsavel(), sigProcessamentoCusto);
		
					// PASSO 16
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "TEMPO_PERMANENCIA_ATUALIZADO", vo.getCentroCusto().toString());
					
					// PASSO 16 E 17
					consumoUnidadeInternacao = this.manterCalculoConsumo(permanenciaUnidadeInternacao, itemPreparoMdto, centroCusto, objetoCustoVersao,  rapServidores, categoriaConsumo);
					
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.C, "ITEM_ATUALIZADO_NA_BASE_CALCULO");
					
					// PASSO 18
					consumoEspecialidade = this.manterCalculoConsumo(permanenciaEspecialidade, itemPreparoMdto, centroCusto, objetoCustoVersao,  rapServidores, categoriaConsumo);
					
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ITEM_ATUALIZADO_NA_BASE_CALCULO");
		
					consumoEquipe = this.manterCalculoConsumo(permanenciaEquipe, itemPreparoMdto, centroCusto, objetoCustoVersao,  rapServidores, categoriaConsumo);
					
					this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ITEM_ATUALIZADO_NA_BASE_CALCULO");
					
					// PASSO 19, 20, 21 e 22
					this.executarProcessamentoItensDePreparoQuimioterapia(vo.getItoPtoSeq(), vo.getItoSeqp(), atividade, consumoUnidadeInternacao, consumoEspecialidade, consumoEquipe, categoriaRecurso, sigProcessamentoCusto, sigProcessamentoPassos, rapServidores, cachePhis, cacheMateriais);
		
					this.commitProcessamentoCusto();
				}
			}
		}
		else{
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "NAO_RETORNOU_BOLSA_SERINGA_DISPENSACAO",  sigProcessamentoCusto.getCompetenciaMesAno());
		}
	}

	public void executarEtapa2SemPreparo(SigCategoriaRecurso categoriaRecurso, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, Map<Integer, FatProcedHospInternos> cachePhis, Map<Integer, ScoMaterial> cacheMateriais, Short pConvenioSus, Short pTipoGrupoContaSus) throws ApplicationBusinessException {

		//Passo 1
		SigCategoriaConsumos categoriaConsumo = this.obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.MD, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);

		//Passo 2: P_AGHU_SIG_SEQ_MEDICACOES_PACIENTE
		BigDecimal seqObjetoCusto = this.getProcessamentoCustoUtils().getParametroFacade().obterValorNumericoAghParametros(AghuParametrosEnum.P_AGHU_SIG_SEQ_MEDICACOES_PACIENTE.toString());
		SigObjetoCustoVersoes  objetoCustoVersao = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterSigObjetoCustoVersaoAtivaPorParametro(seqObjetoCusto);
		if(objetoCustoVersao == null){
			//FE05
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_OBJETO_CUSTO_PARAMETRIZADO_NAO_ENCONTRADO, "P_AGHU_SIG_SEQ_MEDICACOES_PACIENTE");
		}

		//Passo 3: P_AGHU_SIG_SEQ_MEDICAR_PACIENTE_PROCESSAMENTO_DIARIO		
		BigDecimal seqAtividade = this.getProcessamentoCustoUtils().getParametroFacade().obterValorNumericoAghParametros(AghuParametrosEnum.P_AGHU_SIG_SEQ_MEDICAR_PACIENTE_PROCESSAMENTO_DIARIO.toString());
		SigAtividades atividade = this.getProcessamentoCustoUtils().getSigAtividadesDAO().obterPorChavePrimaria(seqAtividade.intValue());
		if(atividade == null){
			//FE06
			throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_ATIVIDADE_PARAMETRIZADA_NAO_ENCONTRADA, "P_AGHU_SIG_SEQ_MEDICAR_PACIENTE_PROCESSAMENTO_DIARIO");
		}		
		
		// PASSO 04
		List<SigAtendimentoVO> lista = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarMedicamentosDispensadosSemPreparo(6, sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());

		if(!ProcessamentoCustoUtils.verificarListaNaoVazia(lista)){
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "MEDICAMENTO_SEM_PREPARO_DISPENSADOS_NO_PERIODO_NAO_ENCONTRADO");
			return;
		}
		
		// PASSO 05
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.C, "MEDICAMENTOS_SEM_PREPARO_DISPENSADOS_PERIODO", sdf.format(sigProcessamentoCusto.getDataInicio()),sdf.format(sigProcessamentoCusto.getDataFim()));
		
		AghUnidadesFuncionais unidadeFuncional;
		MptTratamentoTerapeutico tratamentoTerapeutico;
		List<AghAtendimentos> atendimentos;
		AghAtendimentos atendimentoInternacao;
		FccCentroCustos centroCusto;
		DominioCalculoPermanencia tipo;
		FatProcedHospInternos phi;
		ScoMaterial material;
		SigCalculoAtdPaciente calculoPaciente;
		SigCalculoAtdPermanencia permanenciaUnidadeInternacao, permanenciaEspecialidade, permanenciaEquipe;
		SigCalculoAtdConsumo consumoUnidadeInternacao, consumoEspecialidade, consumoEquipe;
		
		// PASSO 06
		for (SigAtendimentoVO vo : lista) {

			// Passo 7
			unidadeFuncional = this.getProcessamentoCustoUtils().getAghuFacade().buscarUnidadeInternacaoAtiva(vo.getUnfSeqSolicitante());
			centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCentroCusto());
			tratamentoTerapeutico = this.getProcessamentoCustoUtils().getProcedimentoTerapeuticoFacade().obterTratamentoTerapeutico(vo.getTrpSeq());
			phi = this.buscarPhiPorChavePrimaria(vo.getPhiSeq(),cachePhis);
			material = this.buscarMaterialPorChavePrimaria(vo.getMedicamento(), cacheMateriais);
			
			atendimentoInternacao = null;
			if (unidadeFuncional != null) {
				atendimentos = this.getProcessamentoCustoUtils().getAghuFacade().buscarAtendimentos(vo.getPacCodigo(), vo.getDtPrevExecucao());
				atendimentoInternacao = ProcessamentoCustoUtils.verificarListaNaoVazia(atendimentos) ? atendimentos.get(0) : null;
			}
			
			// Passo 8, 9, 10
			calculoPaciente = this.manterCalculoPaciente(tratamentoTerapeutico, vo.getAtdPaciente(), atendimentoInternacao, sigProcessamentoCusto, rapServidores, pConvenioSus, pTipoGrupoContaSus);

			// PASSO 11
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.C, "ATENDIMENTO_VINCULO_PACIENTE_BASE", vo.getAtdPaciente().toString(), vo.getPacCodigo().toString());

			// PASSO 10			
			tipo = ((calculoPaciente.getSituacaoCalculoPaciente() == DominioSituacaoCalculoPaciente.I) ? DominioCalculoPermanencia.UI : DominioCalculoPermanencia.UA);
			permanenciaUnidadeInternacao = this.manterPermanencia(calculoPaciente, tipo, rapServidores, centroCusto, null, null, sigProcessamentoCusto);
							
			// PASSO 12
			permanenciaEspecialidade = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.SM, rapServidores, null, tratamentoTerapeutico.getEspecialidade(), null, sigProcessamentoCusto);

			// PASSO 13
			permanenciaEquipe = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.EQ, rapServidores, null, null, tratamentoTerapeutico.getServidorResponsavel(), sigProcessamentoCusto);

			// PASSO 14
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "PERMANENCIA_BASE_CALCULO", rapServidores.getId().getVinCodigo().toString(), rapServidores.getId().getMatricula());

			// PASSO 17 E 18 -> Centro de custo
			consumoUnidadeInternacao = this.manterCalculoConsumo(permanenciaUnidadeInternacao, null, centroCusto, objetoCustoVersao,  rapServidores, categoriaConsumo);
			
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ITENS_DISPENSADOS_SEM_PREPARO_PRESCRICOES_QUIMIOTERAPIA", vo.getAtdPaciente(), vo.getCentroCusto());
			
			// PASSO 17 E 18 -> Especialidade
			consumoEspecialidade = this.manterCalculoConsumo(permanenciaEspecialidade, null, centroCusto, objetoCustoVersao, rapServidores, categoriaConsumo);
			
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ITENS_DISPENSADOS_SEM_PREPARO_PRESCRICOES_QUIMIOTERAPIA", vo.getAtdPaciente(), vo.getCentroCusto());
			
			// PASSO 17 E 18 -> Equipe
			consumoEquipe = this.manterCalculoConsumo(permanenciaEquipe, null, centroCusto, objetoCustoVersao,  rapServidores, categoriaConsumo);
			
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ITENS_DISPENSADOS_SEM_PREPARO_PRESCRICOES_QUIMIOTERAPIA", vo.getAtdPaciente(), vo.getCentroCusto() );
			
			//PASSO 20 e 21
			this.manterDetalheConsumo(consumoUnidadeInternacao, null, vo.getTotalSolic(), vo.getTotalDisp(), atividade, phi, material, categoriaRecurso, rapServidores);

			// PASSO 22
			this.manterDetalheConsumo(consumoEspecialidade, null, vo.getTotalSolic(), vo.getTotalDisp(), atividade, phi, material, categoriaRecurso, rapServidores);
			this.manterDetalheConsumo(consumoEquipe, null, vo.getTotalSolic(), vo.getTotalDisp(), atividade, phi, material, categoriaRecurso, rapServidores);
			
			//Passo 21
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "QUANTIDADE_MEDICAMENTO_PREPARO_DISPENSADO", vo.getAtdPaciente().toString(), vo.getCentroCusto().toString());
			
			this.commitProcessamentoCusto();
		}
		
	}
	
	public void executarEtapa3PrescricaoMedica(SigCategoriaRecurso categoriaRecurso, SigProcessamentoCusto processamento, RapServidores rapServidores,
			SigProcessamentoPassos sigProcessamentoPassos, Map<Integer, FatProcedHospInternos> cachePhis, Map<Integer, ScoMaterial> cacheMateriais, 
			Map<String, SigObjetoCustoPhisVO> cacheObjetosAtividades, Map<Integer, SigObjetoCustoVersoes> cacheOcv, Map<Integer, SigAtividades> cacheAtividades, 
			Short pConvenioSus, Short pTipoGrupoContaSus) throws ApplicationBusinessException {
		
		//Passo 1
		SigCategoriaConsumos categoriaConsumo = this.obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.QM, processamento, rapServidores, sigProcessamentoPassos);
		
		//Passo 2
		List<SigPreparoMdtoPrescricaoMedicaVO> lista = this.getProcessamentoCustoUtils().getFarmaciaFacade().buscarBolsasSeringasDinpensacoesPrescricaoMedica(processamento.getDataInicio(), processamento.getDataFim());

		// PASSO 3
		SimpleDateFormat sdfDiaMesAno = new SimpleDateFormat("dd/MM/yyyy");
		this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, rapServidores, sigProcessamentoPassos,
				DominioEtapaProcessamento.C, "BOLSAS_SERINGAS_SUCESSO", sdfDiaMesAno.format(processamento.getDataInicio()), sdfDiaMesAno.format(processamento.getDataFim()));

		
		SigObjetoCustoPhisVO sigObjetoCustoPhisVO;
		AinInternacao internacao = null;
		List<SigCalculoAtdPaciente> listaCalculoPacientes;
		AinMovimentosInternacao ultimoMovimentoInternaco;
		AghAtendimentos atendimento;
		SigCalculoAtdPermanencia permanenciaUnidadeInternacao, permanenciaEspecialidade, permanenciaEquipe;
		SigCalculoAtdConsumo consumoUnidadeInternacao, consumoEspecialidade, consumoEquipe;
		SigAtividades atividade;
		SigObjetoCustoVersoes objetoCustoVersao;
		FccCentroCustos centroCusto;
		AfaItemPreparoMdto itemPreparoMdto;
		
		// PASSO 4
		for (SigPreparoMdtoPrescricaoMedicaVO vo : lista) {

			// PASSO 5
			sigObjetoCustoPhisVO = this.obterObjetoCustoAtividadePorEtiqueta(vo.getTipoEtiqueta(), processamento, rapServidores, sigProcessamentoPassos, cacheObjetosAtividades);
			
			if(sigObjetoCustoPhisVO != null){
				
				objetoCustoVersao = this.buscarOcvPorChavePrimaria(sigObjetoCustoPhisVO.getOcvSeq(), cacheOcv);
				atividade = this.buscarAtividadePorChavePrimaria(sigObjetoCustoPhisVO.getTvdSeq(), cacheAtividades);
				centroCusto = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCentroCusto());
				itemPreparoMdto = this.getProcessamentoCustoUtils().getFarmaciaFacade().obterItemPreparoMdtosPorChavePrimaria(vo.getItoSeqp(), vo.getItoPtoSeq());
				
				// PASSO 6		
				if( vo.getAtdInternacao() != null) {
					internacao = this.getProcessamentoCustoUtils().getInternacaoFacade().obterAinInternacaoPorChavePrimariaProcessamentoCusto(vo.getAtdInternacao());
				}
				else{
					internacao = null;
				}
	
				listaCalculoPacientes = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarCalculoAtendimentoPaciente(vo.getAtdPaciente(), processamento, internacao);
	
				if (!ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoPacientes)) {
	
					// PASSO 05
					this.inserirCalculoPaciente(processamento, rapServidores, vo.getAtdPaciente(), null, this.getProcessamentoCustoUtils().getAghuFacade().obterAghAtendimentoPorChavePrimaria(vo.getAtdPaciente()), pConvenioSus, pTipoGrupoContaSus);
	
					//Passo 06
					this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "CALCULO_PACIENTE_BASE_DADOS", vo.getAtdPaciente().toString());
				}
	
				//Passo 07
				ultimoMovimentoInternaco = this.obterUltimoMovimentoInternacao(vo.getAtdInternacao(), vo.getDtPreparo());
	
				if(ultimoMovimentoInternaco != null){
					
					atendimento = this.getProcessamentoCustoUtils().getAghuFacade().obterAghAtendimentoPorChavePrimaria(vo.getAtdPaciente());
		
					// PASSO 08
					permanenciaUnidadeInternacao = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(atendimento, ultimoMovimentoInternaco.getUnidadeFuncional().getCentroCusto(), processamento);
		
					// PASSO 09
					permanenciaEquipe = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(atendimento, ultimoMovimentoInternaco.getServidor(), processamento);
		
					// PASSO 10
					permanenciaEspecialidade = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(atendimento, ultimoMovimentoInternaco.getEspecialidade(), processamento);
					
					// PASSO 12 E 13
					consumoUnidadeInternacao = this.manterCalculoConsumo(permanenciaUnidadeInternacao, itemPreparoMdto, centroCusto, objetoCustoVersao, rapServidores, categoriaConsumo);
					
					this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ITENS_PRESCRICAO_MEDICA_ATUALIZADA", vo.getAtdPaciente());
	
					// PASSO 14
					consumoEquipe = this.manterCalculoConsumo(permanenciaEquipe, itemPreparoMdto, centroCusto, objetoCustoVersao,  rapServidores, categoriaConsumo);
					
					this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ITENS_PRESCRICAO_MEDICA_ATUALIZADA", vo.getAtdPaciente());
		
					consumoEspecialidade = this.manterCalculoConsumo(permanenciaEspecialidade, itemPreparoMdto, centroCusto, objetoCustoVersao, rapServidores, categoriaConsumo);
					
					this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ITENS_PRESCRICAO_MEDICA_ATUALIZADA", vo.getAtdPaciente());
					
					// PASSO 15, 16, 17, 18
					this.executarProcessamentoItensDePreparoQuimioterapia(vo.getItoPtoSeq(), vo.getItoSeqp(), atividade, consumoUnidadeInternacao, consumoEspecialidade, consumoEquipe, categoriaRecurso, processamento, sigProcessamentoPassos, rapServidores, cachePhis, cacheMateriais);	
				}
			}
			this.commitProcessamentoCusto();
		}
	}
	
	
	private void executarProcessamentoItensDePreparoQuimioterapia(Integer itoPtoSeq, Short itoSeqp, SigAtividades atividade, SigCalculoAtdConsumo consumoUnidadeInternacao, SigCalculoAtdConsumo consumoEspecialidade, SigCalculoAtdConsumo consumoEquipe, SigCategoriaRecurso categoriaRecurso, SigProcessamentoCusto processamento,  SigProcessamentoPassos passos, RapServidores rapServidor, Map<Integer, FatProcedHospInternos> cachePhis, Map<Integer, ScoMaterial> cacheMateriais){

		//Passo 20
		List<ComposicaoItemPreparoVO> composicoesItemPreparo = this.getProcessamentoCustoUtils().getFarmaciaFacade().pesquisarComposicaoItemPreparo(itoPtoSeq, itoSeqp);

		if (ProcessamentoCustoUtils.verificarListaNaoVazia(composicoesItemPreparo)) {
			
			FatProcedHospInternos phi; ScoMaterial material;
			for (ComposicaoItemPreparoVO vo : composicoesItemPreparo) {
				phi = this.buscarPhiPorChavePrimaria(vo.getPhiSeq(),cachePhis);
				material = this.buscarMaterialPorChavePrimaria(vo.getMedicamento(), cacheMateriais);
				//Passo 21
				
				this.manterDetalheConsumo(consumoUnidadeInternacao, vo.getIndExterno(), vo.getQtdDispensada(), vo.getQtdDispensada(), atividade, phi, material, categoriaRecurso, rapServidor);
				this.manterDetalheConsumo(consumoEspecialidade,vo.getIndExterno(), vo.getQtdDispensada(), vo.getQtdDispensada(),  atividade, phi, material, categoriaRecurso, rapServidor);
				this.manterDetalheConsumo(consumoEquipe, vo.getIndExterno(), vo.getQtdDispensada(), vo.getQtdDispensada(), atividade, phi, material, categoriaRecurso, rapServidor);
				
				//Passo 22
				this.buscarMensagemEGravarLogProcessamentoSemCommit(processamento, rapServidor, passos,DominioEtapaProcessamento.C, "QUANTIDADE_MED_QUIMIO_ATUALIZADO");
			}
		}
	}
	
	public SigCalculoAtdPaciente manterCalculoPaciente(MptTratamentoTerapeutico tratamentoTerapeutico, Integer atdPaciente, AghAtendimentos atendimentoInternacao, SigProcessamentoCusto processamentoCusto, RapServidores rapServidores, Short pConvenioSus, Short pTipoGrupoContaSus) {
		
		//Passo 8
		List<SigCalculoAtdPaciente> listaCalculoPacientes = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarCalculoAtendimentoPaciente(atdPaciente, processamentoCusto, (atendimentoInternacao != null ? atendimentoInternacao.getInternacao() : null));
		
		//Passo 9
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoPacientes)) {
			SigCalculoAtdPaciente calculoPacientes = listaCalculoPacientes.get(0);
			calculoPacientes.setTratamentoTerapeutico(tratamentoTerapeutico);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().atualizar(calculoPacientes);
			return calculoPacientes;
		} 
		//Passo 10
		else {
			return this.inserirCalculoPaciente(processamentoCusto, rapServidores, atdPaciente, tratamentoTerapeutico, atendimentoInternacao, pConvenioSus, pTipoGrupoContaSus);
		}
	}

	private SigCalculoAtdPaciente inserirCalculoPaciente(SigProcessamentoCusto processamentoCusto, RapServidores rapServidores, Integer atdPaciente, MptTratamentoTerapeutico tratamentoTerapeutico, AghAtendimentos atendimento, Short pConvenioSus, Short pTipoGrupoContaSus) {

		SigCalculoAtdPaciente calculoPaciente = new SigCalculoAtdPaciente();
		calculoPaciente.setCriadoEm(new Date());
		calculoPaciente.setProcessamentoCusto(processamentoCusto);

		if (atendimento != null) {
			calculoPaciente.setInternacao(atendimento.getInternacao());
			if (atendimento.getDthrFim() == null) {
				calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.I);
			} else {
				calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.IA);
			}
		}else{
			calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.A);
		}
		
		calculoPaciente.setAtendimento(this.getProcessamentoCustoUtils().getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdPaciente));
		calculoPaciente.setRapServidores(rapServidores);
		calculoPaciente.setTratamentoTerapeutico(tratamentoTerapeutico);
		calculoPaciente.setIndPacientePediatrico(calculoPaciente.getAtendimento().getIndPacPediatrico());
		this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().persistir(calculoPaciente);
		this.inserirCidsProcedimentos(calculoPaciente, rapServidores, pConvenioSus, pTipoGrupoContaSus);
		return calculoPaciente;
	}


	private SigCalculoAtdPermanencia manterPermanencia(SigCalculoAtdPaciente calculoPaciente, DominioCalculoPermanencia tipo, RapServidores servidor,  FccCentroCustos centroCusto, AghEspecialidades especialidade, RapServidores equipe, SigProcessamentoCusto sigProcessamentoCusto ){
		
		SigCalculoAtdPermanencia permanencia = null;
		
		if(centroCusto != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorCentroCusto(calculoPaciente.getAtendimento(), centroCusto, sigProcessamentoCusto);
		}
		else if(especialidade != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEspecialidade(calculoPaciente.getAtendimento(), especialidade, sigProcessamentoCusto);
		}
		else if(equipe != null){
			permanencia = this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().buscarPermanenciaPorEquipe(calculoPaciente.getAtendimento(), equipe, sigProcessamentoCusto);
		}

		
		if (permanencia == null) {
			permanencia = new SigCalculoAtdPermanencia();
			permanencia.setCriadoEm(new Date());
			permanencia.setCalculoAtdPaciente(calculoPaciente);
			permanencia.setRapServidores(servidor);
			permanencia.setTipo(tipo);
			permanencia.setTempo(new BigDecimal(1440));
			permanencia.setCentroCustos(centroCusto);
			permanencia.setEspecialidade(especialidade);
			permanencia.setResponsavel(equipe);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPermanenciaDAO().persistir(permanencia);
		}
		
		return permanencia;
	}

	private SigCalculoAtdConsumo manterCalculoConsumo(SigCalculoAtdPermanencia permanencia, AfaItemPreparoMdto itemPreparoMdto, FccCentroCustos centroCusto, SigObjetoCustoVersoes objetoCustoVersao,  RapServidores rapServidores, SigCategoriaConsumos categoriaConsumo) {
		SigCalculoAtdConsumo  consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumoPorPermanenciaEVersao(permanencia.getSeq(), objetoCustoVersao.getSeq());
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setQtde( BigDecimal.ONE);
			consumo.setRapServidores(rapServidores);
			consumo.setCriadoEm(new Date());
			consumo.setCentroCustos(centroCusto);
			consumo.setItemPreparoMdto(itemPreparoMdto);
			consumo.setCategoriaConsumo(categoriaConsumo);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
		} else {
			consumo.setQtde(consumo.getQtde().add(BigDecimal.ONE));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
		return consumo;
	}

	private void manterDetalheConsumo(SigCalculoAtdConsumo consumo, String identificador, BigDecimal qtdePrevisto, BigDecimal qtdeDebitado,  SigAtividades atividade, FatProcedHospInternos phi, ScoMaterial material, SigCategoriaRecurso categoriaRecurso, RapServidores servidor){
		
		qtdePrevisto = qtdePrevisto != null ? qtdePrevisto : BigDecimal.ZERO;
		qtdeDebitado = qtdeDebitado != null ? qtdeDebitado : BigDecimal.ZERO;
		
		SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().buscarItemConsumo(consumo, phi);		
		if(detalheConsumo == null){
			detalheConsumo = new SigCalculoDetalheConsumo();
			detalheConsumo.setCalculoAtividadeConsumo(consumo);
			detalheConsumo.setAtividade(atividade);
			detalheConsumo.setCategoriaRecurso(categoriaRecurso);
			detalheConsumo.setProcedHospInterno(phi);
			detalheConsumo.setScoMaterial(material);
			detalheConsumo.setIdentificador(identificador);
			detalheConsumo.setQtdePrevisto(qtdePrevisto);
			detalheConsumo.setQtdeDebitado(qtdeDebitado);
			detalheConsumo.setQtdeConsumido(qtdeDebitado);
			detalheConsumo.setRapServidores(servidor);
			detalheConsumo.setCriadoEm(new Date());
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().persistir(detalheConsumo);
		}
		else{
			detalheConsumo.setQtdePrevisto(detalheConsumo.getQtdePrevisto().add(qtdePrevisto));
			detalheConsumo.setQtdeDebitado(detalheConsumo.getQtdeDebitado().add(qtdeDebitado));
			detalheConsumo.setQtdeConsumido(detalheConsumo.getQtdeConsumido().add(qtdeDebitado));
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		}
	}

	protected SigObjetoCustoPhisVO obterObjetoCustoAtividadePorEtiqueta(String tipoEtiqueta, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, Map<String, SigObjetoCustoPhisVO> cacheObjetosAtividades) throws ApplicationBusinessException{
		
		//Passo 15
		SigObjetoCustoPhisVO vo = null;
		if(!cacheObjetosAtividades.containsKey(tipoEtiqueta)){
			List<SigObjetoCustoPhisVO> lista = this.getProcessamentoCustoUtils().getSigObjetoCustoPhisDAO().pesquisarObjectoCustoPorTipoEtiqueta(tipoEtiqueta);
			vo = ProcessamentoCustoUtils.verificarListaNaoVazia(lista) ? lista.get(0) : null;
			cacheObjetosAtividades.put(tipoEtiqueta, vo);
		}
		
		//[FE03]
		if (vo != null) {
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos,DominioEtapaProcessamento.C, "OBJETO_CUSTO_QUIMIO_NAO_ENCONTRADO");
			return null;
		}
		
		return cacheObjetosAtividades.get(tipoEtiqueta);
	}
}
