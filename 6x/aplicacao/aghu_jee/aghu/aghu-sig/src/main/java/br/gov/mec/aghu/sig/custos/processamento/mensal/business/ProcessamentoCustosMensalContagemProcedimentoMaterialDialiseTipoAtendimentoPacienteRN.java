package br.gov.mec.aghu.sig.custos.processamento.mensal.business;

import java.math.BigDecimal;
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
import br.gov.mec.aghu.model.AfaItemPreparoMdto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMaterial;
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
import br.gov.mec.aghu.sig.custos.vo.SigProcedimentoMaterialPrescricaoDialiseVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #32222 - Contagem de PROCEDIMENTOS E mATERIAIS de diálise por tipo de atendimento do paciente
 * @author rogeriovieira
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@Local(ProcessamentoCustosMensalContagemProcedimentoMaterialDialiseTipoAtendimentoPacienteRN.class)
public class ProcessamentoCustosMensalContagemProcedimentoMaterialDialiseTipoAtendimentoPacienteRN extends ProcessamentoMensalBusiness {

	private static final long serialVersionUID = 6527388752195308336L;

	@Override
	public String getTitulo() {
		return "Contagem de procedimentos e materiais de diálise por tipo de atendimento do paciente";
	}

	@Override
	public String getNome() {
		return "ProcessamentoMensalContagemProcedimentoMaterialDialiseTipoAtendimentoPaciente";
	}

	@Override
	public Integer getPassoProcessamento(DominioTipoObjetoCusto tipoObjetoCusto) {
		return 32; 
	}

	@Override
	protected void executarPassosInternos(SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, DominioTipoObjetoCusto tipoObjetoCusto) throws ApplicationBusinessException {

		//Passo 1
		SigCategoriaConsumos categoriaConsumo = this.obterCategoriaConsumoPorIndicadorContagem(DominioIndContagem.DP, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos);
		
		SigCategoriaRecurso categoriaRecurso = this.getProcessamentoCustoUtils().getSigCategoriaRecursoDAO().obterPorChavePrimaria(2);
		
		//Passo 2
		List<SigProcedimentoMaterialPrescricaoDialiseVO> lista = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarProcedimentosEMateriaisDaPrescricaoDeDialise(sigProcessamentoCusto.getDataInicio(), sigProcessamentoCusto.getDataFim());
		
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(lista)) {

			//Passo 3 - tipo = "P"
			AghParametros parametroDialise = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_SIG_SEQ_PROCEDIMENTOS_DIALISE);
			SigObjetoCustoVersoes objetoCustoProcedimentoDialise = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterSigObjetoCustoVersaoAtivaPorParametro(parametroDialise.getVlrNumerico());
			if(objetoCustoProcedimentoDialise == null){
				throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_OBJETO_CUSTO_PARAMETRIZADO_NAO_ENCONTRADO, "P_AGHU_SIG_SEQ_PROCEDIMENTOS_DIALISE");
			}
			
			//Passo 3 - tipo = "M"
			parametroDialise = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_SIG_SEQ_MATERIAIS_DIALISE);
			SigObjetoCustoVersoes objetoCustoMateriaisDialise = this.getProcessamentoCustoUtils().getSigObjetoCustoVersoesDAO().obterSigObjetoCustoVersaoAtivaPorParametro(parametroDialise.getVlrNumerico());
			if(objetoCustoMateriaisDialise == null){
				throw new ApplicationBusinessException(ProcessamentoCustoExceptionCode.MENSAGEM_ERRO_OBJETO_CUSTO_PARAMETRIZADO_NAO_ENCONTRADO, "P_AGHU_SIG_SEQ_MATERIAIS_DIALISE");
			}
			
			//Passo 4
			this.buscarMensagemEGravarLogProcessamento( sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C,"MENSAGEM_SUCESSO_CONSULTA_PROCEDIMENTO_E_MATERIAIS_DIALISE", sigProcessamentoCusto.getCompetenciaMesAno());

			this.processarListaDialise(lista, objetoCustoProcedimentoDialise, objetoCustoMateriaisDialise, sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, categoriaConsumo, categoriaRecurso);
		
		} else {
			// FE02
			this.buscarMensagemEGravarLogProcessamento(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C,"NAO_RETORNA_RESULTADO_PROCEDIMENTO_MATARIAIS_DIALISE", sigProcessamentoCusto.getCompetenciaMesAno());
			return;
		}
	}

	private void processarListaDialise(List<SigProcedimentoMaterialPrescricaoDialiseVO> lista, SigObjetoCustoVersoes objetoCustoProcedimentoDialise, SigObjetoCustoVersoes objetoCustoMateriaisDialise, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, SigProcessamentoPassos sigProcessamentoPassos, SigCategoriaConsumos categoriaConsumo, SigCategoriaRecurso categoriaRecurso) throws ApplicationBusinessException {
		
		Short pConvenioSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS).getVlrNumerico().shortValue();
		
        Short pTipoGrupoContaSus = this.getProcessamentoCustoUtils().getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		
		List<AghAtendimentos> listaAtendimentos;
		AghAtendimentos atendimento;
		AinMovimentosInternacao ultimoMovimentoInternacao;
		FccCentroCustos centroCusto, centroCustoTratamentoTerapeutico ;
		DominioCalculoPermanencia tipoUnidadeFuncional;
		FatProcedHospInternos phi;
		ScoMaterial material;
		BigDecimal quantidade;
		AfaItemPreparoMdto itemPreparoMdto;
		SigCalculoAtdPermanencia permanenciaUnidadeFuncional, permanenciaEspecialidade, permanenciaEquipe;
		SigCalculoAtdConsumo consumoUnidadeFuncional, consumoEspecialidade, consumoEquipe;
		SigObjetoCustoVersoes objetoCustoVersao;
		MptTratamentoTerapeutico tratamentoTerapeutico;
		
		Map<Integer, FatProcedHospInternos> cachePhis = new HashMap<Integer, FatProcedHospInternos>();
		Map<Integer, ScoMaterial> cacheMateriais = new HashMap<Integer, ScoMaterial>();
		
		//Passo 5
		for (SigProcedimentoMaterialPrescricaoDialiseVO vo : lista) {

			tratamentoTerapeutico = this.getProcessamentoCustoUtils().getProcedimentoTerapeuticoFacade().obterTratamentoTerapeutico(vo.getTrpSeq());
			quantidade = BigDecimal.valueOf(vo.getQuantidade());
			itemPreparoMdto = this.getProcessamentoCustoUtils().getFarmaciaFacade().obterItemPreparoMdtosPorChavePrimaria(vo.getItoSeqp(), vo.getItoPtoSeq());
			centroCusto =  this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorChavePrimaria(vo.getCentroCusto());
			phi = this.buscarPhiPorChavePrimaria(vo.getPhiSeq(), cachePhis);
			material = this.buscarMaterialPorChavePrimaria(vo.getMatCodigo(), cacheMateriais);
			
			//Passo 6
			listaAtendimentos = this.getProcessamentoCustoUtils().getAghuFacade().buscarAtendimentos(vo.getPacCodigo(), vo.getDtPrevExecucao());

			if(ProcessamentoCustoUtils.verificarListaNaoVazia(listaAtendimentos)){ 
				atendimento = listaAtendimentos.get(0);
				
				//Passo 7
				ultimoMovimentoInternacao = this.obterUltimoMovimentoInternacao(atendimento.getInternacao().getSeq(), vo.getDtPrevExecucao());
				if(ultimoMovimentoInternacao == null){
					continue;
				}
				
				centroCustoTratamentoTerapeutico = ultimoMovimentoInternacao.getUnidadeFuncional().getCentroCusto();
				tipoUnidadeFuncional = DominioCalculoPermanencia.UI;
			}
			else{
				atendimento = null;
				ultimoMovimentoInternacao = null;
				centroCustoTratamentoTerapeutico = this.getProcessamentoCustoUtils().getCentroCustoFacade().obterCentroCustoPorUnidadeFuncional(tratamentoTerapeutico.getUnfSeq());
				tipoUnidadeFuncional = DominioCalculoPermanencia.UA;
			}
				
			// Passo 8,9,10
			SigCalculoAtdPaciente calculoPaciente = this.manterCalculoPaciente(vo, tratamentoTerapeutico, atendimento, sigProcessamentoCusto, rapServidores, pConvenioSus, pTipoGrupoContaSus);
			
			// Passo 11
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ATENDIMENTO_PACIENTE_INCLUIDO_BASE", vo.getPacCodigo().toString(), vo.getAtdPaciente().toString());

			// Passo 13
			permanenciaUnidadeFuncional = this.manterPermanencia(calculoPaciente, tipoUnidadeFuncional, rapServidores,centroCustoTratamentoTerapeutico, null, null, sigProcessamentoCusto);

			// Passo 14
			permanenciaEspecialidade = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.SM, rapServidores, null, tratamentoTerapeutico.getEspecialidade(), null, sigProcessamentoCusto);

			// Passo 15
			permanenciaEquipe = this.manterPermanencia(calculoPaciente, DominioCalculoPermanencia.EQ, rapServidores, null, null, tratamentoTerapeutico.getServidorResponsavel(), sigProcessamentoCusto);

			// Passo 16
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "TEMPO_PERMANENCIA_ATUALIZADO_BANCO", vo.getCentroCusto().toString(), "", rapServidores.getId().getVinCodigo().toString(), rapServidores.getId().getMatricula().toString());

			// Passo 15
			objetoCustoVersao = vo.getTipo().equals("P") ? objetoCustoProcedimentoDialise : objetoCustoMateriaisDialise;
			
			// Passo 17, 18
			consumoUnidadeFuncional = this.manterConsumo( permanenciaUnidadeFuncional, quantidade, centroCusto, itemPreparoMdto, objetoCustoVersao, categoriaConsumo, rapServidores);
			
			// Passo 20, 21
			consumoEspecialidade = this.manterConsumo(permanenciaEspecialidade, quantidade, centroCusto, itemPreparoMdto, objetoCustoVersao, categoriaConsumo, rapServidores);
			
			// Passo 23, 24
			consumoEquipe = this.manterConsumo(permanenciaEquipe, quantidade, centroCusto, itemPreparoMdto, objetoCustoVersao, categoriaConsumo, rapServidores);				
			
			//Passo 19, 22, 25
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "ATENDIMENTO_PACIENTE_INCLUIDO_BASE", vo.getAtdPaciente(), vo.getCentroCusto(), objetoCustoVersao.getSeq());
				
			//Passo 26, 27, 28
			this.manterDetalheConsumo(consumoUnidadeFuncional, quantidade, material, phi, rapServidores, categoriaRecurso);
			
			//Passo 29, 30, 31
			this.manterDetalheConsumo(consumoEspecialidade, quantidade, material, phi, rapServidores, categoriaRecurso);
			
			//Passo 32, 33, 34
			this.manterDetalheConsumo(consumoEquipe, quantidade, material, phi, rapServidores, categoriaRecurso);

			//Passo 35
			this.buscarMensagemEGravarLogProcessamentoSemCommit(sigProcessamentoCusto, rapServidores, sigProcessamentoPassos, DominioEtapaProcessamento.C, "MENSAGEM_DETALHE_CONSUMO_PROCEDIMENTO_MATERIAL_DIALISE_ATUALIZADO", vo.getAtdPaciente(), centroCusto.getDescricao(), tratamentoTerapeutico.getEspecialidade().getNomeEspecialidade(), tratamentoTerapeutico.getServidorResponsavel().getPessoaFisica().getNome() );
			
			this.commitProcessamentoCusto();
		}
	}

	
	private SigCalculoAtdPaciente manterCalculoPaciente(SigProcedimentoMaterialPrescricaoDialiseVO vo, MptTratamentoTerapeutico tratamentoTerapeutico,  AghAtendimentos atendimento, SigProcessamentoCusto sigProcessamentoCusto, RapServidores rapServidores, Short pConvenioSus, Short pTipoGrupoContaSus) {

		//Passo 8
		List<SigCalculoAtdPaciente> listaCalculoAtendimentos = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().buscarCalculoAtendimentoPaciente(vo.getAtdPaciente(), sigProcessamentoCusto, (atendimento != null ? atendimento.getInternacao() : null) );
		
		//Passo 9
		SigCalculoAtdPaciente calculoPaciente;
		if (ProcessamentoCustoUtils.verificarListaNaoVazia(listaCalculoAtendimentos)) {
			calculoPaciente = listaCalculoAtendimentos.get(0);
			calculoPaciente.setTratamentoTerapeutico(tratamentoTerapeutico);
			calculoPaciente = this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().atualizar(calculoPaciente);
		} 
		//Passo 10
		else {
			calculoPaciente = new SigCalculoAtdPaciente();
			calculoPaciente.setCriadoEm(new Date());
			calculoPaciente.setProcessamentoCusto(sigProcessamentoCusto);
			calculoPaciente.setAtendimento(this.getProcessamentoCustoUtils().getAghuFacade().obterAghAtendimentoPorChavePrimaria(vo.getAtdPaciente()));
			
			calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.IA);
			if (atendimento != null) {
				calculoPaciente.setInternacao(atendimento.getInternacao());
				if (atendimento.getDthrFim() != null){
					calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.A);
				} else  {
					calculoPaciente.setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente.I);
				}
			}
			
			calculoPaciente.setRapServidores(rapServidores);
			calculoPaciente.setIndPacientePediatrico(calculoPaciente.getAtendimento().getIndPacPediatrico());
			calculoPaciente.setTratamentoTerapeutico(tratamentoTerapeutico);
			this.getProcessamentoCustoUtils().getSigCalculoAtdPacienteDAO().persistir(calculoPaciente);
			this.inserirCidsProcedimentos(calculoPaciente, rapServidores, pConvenioSus, pTipoGrupoContaSus);
		}
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
	
	private SigCalculoAtdConsumo manterConsumo(SigCalculoAtdPermanencia permanencia, BigDecimal quantidade, FccCentroCustos centroCusto,  AfaItemPreparoMdto itemPreparoMdto, SigObjetoCustoVersoes objetoCustoVersao, SigCategoriaConsumos categoriaConsumo, RapServidores rapServidores) throws ApplicationBusinessException {

		SigCalculoAtdConsumo consumo = this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().buscarItemConsumoPorPermanenciaEVersao(permanencia.getSeq(), objetoCustoVersao.getSeq());
		
		if (consumo == null) {
			consumo = new SigCalculoAtdConsumo();
			consumo.setCalculoAtividadePermanencia(permanencia);
			consumo.setSigObjetoCustoVersoes(objetoCustoVersao);
			consumo.setCentroCustos(centroCusto);
			consumo.setQtde(quantidade);
			consumo.setRapServidores(rapServidores);
			consumo.setCriadoEm(new Date());
			consumo.setCategoriaConsumo(categoriaConsumo);
			consumo.setItemPreparoMdto(itemPreparoMdto);
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().persistir(consumo);
		}
		else{
			consumo.setQtde(consumo.getQtde().add(quantidade));
			this.getProcessamentoCustoUtils().getSigCalculoAtdConsumoDAO().atualizar(consumo);
		}
		return consumo;
	}

	private void manterDetalheConsumo(SigCalculoAtdConsumo consumo, BigDecimal quantidade, ScoMaterial material, FatProcedHospInternos phi, RapServidores rapServidores, SigCategoriaRecurso categoriaRecurso) throws ApplicationBusinessException {

		SigCalculoDetalheConsumo detalheConsumo = this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().buscarItemConsumo(consumo, phi);
		if (detalheConsumo == null) {
			detalheConsumo = new SigCalculoDetalheConsumo();
			detalheConsumo.setCalculoAtividadeConsumo(consumo);
			detalheConsumo.setCategoriaRecurso(categoriaRecurso);
			detalheConsumo.setProcedHospInterno(phi);
			detalheConsumo.setScoMaterial(material);
			detalheConsumo.setQtdePrevisto(quantidade);
			detalheConsumo.setQtdeDebitado(quantidade);
			detalheConsumo.setQtdeConsumido(quantidade);
			detalheConsumo.setRapServidores(rapServidores);
			detalheConsumo.setCriadoEm(new Date());
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().persistir(detalheConsumo);
		} else {
			detalheConsumo.setQtdePrevisto(detalheConsumo.getQtdePrevisto().add(quantidade));
			detalheConsumo.setQtdeDebitado(detalheConsumo.getQtdeDebitado().add(quantidade));
			detalheConsumo.setQtdeConsumido(detalheConsumo.getQtdeConsumido().add(quantidade));
			this.getProcessamentoCustoUtils().getSigCalculoDetalheConsumoDAO().atualizar(detalheConsumo);
		}
	}
}
